package com.bing.wificar;


class SendThread extends Thread {
    private char ch;

    public SendThread(char ch) {
        this.ch = ch;
    }

    @Override
    public void run() {


        ActionActivity.pw.print(ch);
        ActionActivity.pw.print('n');
        ActionActivity.pw.flush();

    }
}

