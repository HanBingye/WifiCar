package com.bing.wificar;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class ConnectThread extends Thread {

    private  String ip;
    private int port;
    private PrintWriter pw;
    private Handler handler;

    public ConnectThread(String ip,int port,Handler handler) {
        this.ip=ip;
        this.port=port;
        this.handler = handler;

    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            socket.connect(socketAddress);
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//            pw.println(1);测试是否连接上

            Message msg = handler.obtainMessage();
            msg.what=1;
            handler.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
            Message msg = handler.obtainMessage();
            msg.what=0;
            handler.sendMessage(msg);
        }
    }

    public PrintWriter getPw() {
        return pw;
    }
}
