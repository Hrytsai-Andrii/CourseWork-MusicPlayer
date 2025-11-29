package com.example.kursova.server;

import com.example.kursova.server.repository.*;
import com.example.kursova.server.service.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    private static final int PORT = 8888;

    public static void main(String[] args) {
        System.out.println(">>> Запуск сервера...");
        try {
            DatabaseConnection.initSchema(); 
            System.out.println(">>> База даних ініціалізована.");
        } catch (Exception e) {
            System.err.println("!!! Помилка ініціалізації БД:");
            e.printStackTrace();
            return;
        }

        UserRepository userRepo = new UserRepository(); 
        TrackRepository trackRepo = new TrackRepository(); 
        PlaylistRepository playlistRepo = new PlaylistRepository(); 
        StateRepository stateRepo = new StateRepository(); 

        UserService userService = new UserService(userRepo); 
        TrackService trackService = new TrackService(trackRepo); 
        PlaylistService playlistService = new PlaylistService(playlistRepo); 

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(">>> Сервер слухає порт " + PORT);
            System.out.println(">>> Очікування клієнтів...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(">>> Нове підключення: " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(
                        clientSocket,
                        userService,
                        trackService,
                        playlistService,
                        stateRepo
                );
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}