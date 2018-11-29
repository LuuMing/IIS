package com.example.luming.iis;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class mSocket extends Socket {
    private static mSocket socket = null;
    private static OutputStream out = null;
    private static InputStream in = null;
    private mSocket(){};
    public static mSocket getInstance() throws IOException {
        if(socket == null)
        {
            socket = new mSocket();
        }
        return socket;
    }
    public static OutputStream getOut()
    {
        if(out == null)
        {
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }
    public static InputStream getIn()
    {
        if(in == null)
        {
            try {
                in = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return in;
    }
}
