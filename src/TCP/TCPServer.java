package TCP;

import java.net.*;
import java.io.*;

public class TCPServer implements Runnable
{
    public static final int MYPORT= 4950;
    public static final int BUFSIZE= 10;
    Socket socket = null;

    public TCPServer(Socket socket)
    {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException
    {
        ServerSocket server = null;
        try
        {
            server = new ServerSocket(MYPORT);
        }
        catch (IOException e)
        {
            System.err.println("Can not listen on port: %d. Choose another one. Exiting...\n");
            System.exit(-1);
        }

        while (true)
        {
            Socket sc = server.accept();
            new Thread(new TCPServer(sc)).start();
        }
    }

    public void run()
    {
        try
        {
            while(socket.isConnected())
            {
                /*InputStream to receive message*/
                InputStream inputStream=socket.getInputStream();
                /*OutputStream to send message*/
                OutputStream outputStream=socket.getOutputStream();
                /*This will be the whole string that is received*/
                String receivedMessage="";
                /*This will be the temp string that is received and sent to client*/
                String tmp="";
                do
                {
                    /*Create buffer*/
                    byte[] buf = new byte[BUFSIZE];
                    /*Read message to buf*/
                    inputStream.read(buf);
                    /*Save message */
                    tmp = new String(buf).trim();
                    /*Create the whole message*/
                    receivedMessage+=tmp;
                    /*Send back to client if message isn't empty*/
                    if(!tmp.isEmpty())
                        outputStream.write(tmp.getBytes());
                }while (!tmp.isEmpty());

                /*Close the socket*/
                socket.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}