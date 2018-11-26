package com.example.luming.iis;

import java.net.Socket;

public class mSocket extends Socket {
    private static mSocket socket = null;
    private mSocket(){};
    public static mSocket getInstance()
    {
        if(socket == null)
        {
            socket = new mSocket();
        }
        return socket;
    }
}
