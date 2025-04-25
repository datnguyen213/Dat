package com.example.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread extends Thread {
    private ServerSocket serverSocket;
    private MainActivity mainActivity;

    public TCPServerThread(MainActivity activity) {
        this.mainActivity = activity;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(12345);
            Log.d("TCPServer", "Đang lắng nghe trên cổng 12345...");

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String received = in.readLine();

                Log.d("TCPServer", "Đã nhận: " + received);
                mainActivity.updateMessages("Client: " + received);


                // Gửi phản hồi lại nếu cần
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Server đã nhận: " + received);

                clientSocket.close();
            }
        } catch (IOException e) {
            Log.e("TCPServer", "Lỗi server: ", e);
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

