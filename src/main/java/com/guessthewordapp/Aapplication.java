/*package com.guessthewordapp;

import com.guessthewordapp.application.contract.*;
import com.guessthewordapp.application.impl.*;
import com.guessthewordapp.config.InfrastructureConfig;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.*;
import java.util.List;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.Scanner;
import com.guessthewordapp.application.contract.dto.GameSessionDTO;

class Main {
    private final UserService userService;
    private final GameSessionService gameSessionService;
    private final WordService wordService;
    private final AuthenticationService authService;
    private final AuthorizationService authorizationService;
    private final Scanner scanner;

    public Main(UserService userService,
        GameSessionService gameSessionService,
        WordService wordService,
        AuthenticationService authService,
        AuthorizationService authorizationService) {
        this.userService = userService;
        this.gameSessionService = gameSessionService;
        this.wordService = wordService;
        this.authService = authService;
        this.authorizationService = authorizationService;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(InfrastructureConfig.class)) {

            // Отримуємо біни через інтерфейси
            UserService userService = context.getBean(UserService.class);
            GameSessionService gameSessionService = context.getBean(GameSessionService.class); // Тепер Spring знайде лише одну реалізацію
            WordService wordService = context.getBean(WordService.class);
            AuthenticationService authService = context.getBean(AuthenticationService.class);
            AuthorizationService authorizationService = context.getBean(AuthorizationService.class);

            Main app = new Main(
                userService,
                gameSessionService,
                wordService,
                authService,
                authorizationService
            );
            app.run();

        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        System.out.println("=== Welcome to Guess the Word Game ===");
        User currentUser = null;

        while (true) {
            if (currentUser == null) {
                currentUser = showAuthMenu();
                if (currentUser == null) continue;
            }

            showMainMenu(currentUser);
        }
    }

    private User showAuthMenu() {
        System.out.println("\n=== Authentication Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                return authService.login(username, password);
            }
            case 2 -> {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                return authService.register(username, password, email, UserRole.PLAYER);
            }
            case 0 -> System.exit(0);
            default -> System.out.println("Invalid choice!");
        }
        return null;
    }

    private void showMainMenu(User user) {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Start Game");
        System.out.println("2. View Stats");

        if (authorizationService.hasRole(user, UserRole.ADMIN)) {
            System.out.println("3. Admin Panel");
        }

        System.out.println("0. Logout");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> startGame(user);
            case 2 -> showStats(user);
            case 3 -> {
                if (authorizationService.hasRole(user, UserRole.ADMIN)) {
                    showAdminPanel();
                } else {
                    System.out.println("Access denied!");
                }
            }
            case 0 -> {
                System.out.println("Goodbye, " + user.getUsername() + "!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    private void startGame(User user) {
        System.out.println("\n=== Game Started ===");
        GameSessionDTO session = gameSessionService.startSession(user.getId());
        System.out.println("Session ID: " + session.id());
        // Game logic here
    }

    private void showStats(User user) {
        System.out.println("\n=== Your Statistics ===");
        List<GameSessionDTO> sessions = gameSessionService.getSessionsByUser(user.getId());
        sessions.forEach(s -> System.out.println(
            "Session " + s.id() + ": " +
                s.startedAt() + " - " +
                (s.endedAt() != null ? s.endedAt() : "In progress")
        ));
    }

    private void showAdminPanel() {
        System.out.println("\n=== Admin Panel ===");
        System.out.println("1. Add Word");
        System.out.println("2. List Words");
        System.out.println("0. Back");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> addWord();
            case 2 -> listWords();
            case 0 -> { return; }
            default -> System.out.println("Invalid choice!");
        }
    }

    private void addWord() {
        System.out.print("Enter word: ");
        String word = scanner.nextLine();
        // wordService.save(...);
        System.out.println("Word added!");
    }

    private void listWords() {
        System.out.println("\n=== Word List ===");
        // wordService.getAllWords().forEach(...);
    }
}*/