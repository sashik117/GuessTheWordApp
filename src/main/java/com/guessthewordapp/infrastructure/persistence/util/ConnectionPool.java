package com.guessthewordapp.infrastructure.persistence.util;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class ConnectionPool implements DataSource {
    private final BlockingQueue<Connection> availableConnections;
    private final List<Connection> realConnections;
    private final String url;
    private final int maxConnections;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    public ConnectionPool(PoolConfig config) {
        this.url = config.url;
        this.maxConnections = config.maxConnections;
        this.availableConnections = new ArrayBlockingQueue<>(maxConnections);
        this.realConnections = new ArrayList<>(maxConnections);
        initializePool();
    }

    private void initializePool() {
        if (isInitialized.compareAndSet(false, true)) {
            try {
                Class.forName("org.sqlite.JDBC");
                for (int i = 0; i < maxConnections; i++) {
                    Connection realConn = DriverManager.getConnection(url);
                    realConnections.add(realConn);
                    availableConnections.add(createProxyConnection(realConn));
                }
            } catch (ClassNotFoundException | SQLException e) {
                throw new ConnectionPoolException("Failed to initialize SQLite connection pool", e);
            }
        }
    }

    private Connection createProxyConnection(Connection realConnection) {
        return (Connection) Proxy.newProxyInstance(
            ConnectionPool.class.getClassLoader(),
            new Class[]{Connection.class},
            (proxy, method, args) -> {
                if ("close".equals(method.getName())) {
                    if (!realConnection.isClosed()) {
                        boolean offered = availableConnections.offer((Connection) proxy);
                        if (!offered) {
                            System.err.println("Failed to return connection to pool");
                        }
                    }
                    return null;
                } else if ("isClosed".equals(method.getName())) {
                    return realConnection.isClosed();
                }
                return method.invoke(realConnection, args);
            });
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = availableConnections.poll(5, TimeUnit.SECONDS);
            if (connection == null) {
                throw new SQLException("Connection pool exhausted");
            }

            if (connection.isClosed()) {
                Connection realConn = DriverManager.getConnection(url);
                synchronized (realConnections) {
                    realConnections.add(realConn);
                }
                connection = createProxyConnection(realConn);
            }
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public PrintWriter getLogWriter() { return null; }
    @Override
    public void setLogWriter(PrintWriter out) {}
    @Override
    public void setLoginTimeout(int seconds) {}
    @Override
    public int getLoginTimeout() { return 0; }
    @Override
    public Logger getParentLogger() { return null; }

    public void shutdown() {
        synchronized (realConnections) {
            try {
                for (Connection realConn : realConnections) {
                    try {
                        if (!realConn.isClosed()) {
                            realConn.close();
                        }
                    } catch (SQLException e) {
                        System.err.println("Failed to close connection: " + e.getMessage());
                    }
                }
                realConnections.clear();
                availableConnections.clear();
                isInitialized.set(false);
            } catch (Exception e) {
                throw new ConnectionPoolException("Failed to shutdown connection pool", e);
            }
        }
    }

    public static class PoolConfig {
        private final String url;
        private final int maxConnections;

        private PoolConfig(Builder builder) {
            this.url = builder.url;
            this.maxConnections = builder.maxConnections;
        }

        public static class Builder {
            private String url = "jdbc:sqlite:./guesstheword.db";
            private int maxConnections = 5;

            public Builder withUrl(String url) {
                this.url = url;
                return this;
            }

            public Builder withMaxConnections(int maxConnections) {
                this.maxConnections = Math.max(1, maxConnections);
                return this;
            }

            public PoolConfig build() {
                return new PoolConfig(this);
            }
        }
    }

    private static class ConnectionPoolException extends RuntimeException {
        public ConnectionPoolException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}