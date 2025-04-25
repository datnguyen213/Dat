package com.example.client;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClientTask extends AsyncTask<String, Void, String> {
    private String serverIp;
    private Context context;

    public TCPClientTask(Context context, String serverIp) {
        this.context = context;
        this.serverIp = serverIp;
    }

    @Override
    protected String doInBackground(String... params) {
        String message = params[0];
        try (Socket socket = new Socket(serverIp, 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);
            return in.readLine(); // Nhận phản hồi

        } catch (IOException e) {
            Log.e("TCPClient", "Lỗi khi gửi tin", e);
            return "Lỗi: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("TCPClient", "Phản hồi từ server: " + result);
        Toast.makeText(context, "Phản hồi từ server: " + result, Toast.LENGTH_LONG).show();
        ((MainActivity) context).updateMessages(result);
    }
}