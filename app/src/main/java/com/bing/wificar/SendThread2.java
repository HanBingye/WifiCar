package com.bing.wificar;


class SendThread2 extends Thread {
    private char ch;

    public SendThread2(char ch) {
        this.ch = ch;
    }

    @Override
    public void run() {


        DuojiActivity.pw.print(ch);
        DuojiActivity.pw.print('n');
        DuojiActivity.pw.flush();

    }
}

