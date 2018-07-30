package TCP;

import java.awt.*;
import java.io.*;
import java.net.*;

public class TCPEchoClient
{
    public static final String MSG= "An Echo Message!";
    public static OutputStream outputstream;
    public static InputStream inputstream;
    public static Socket clientSocket;
    public static double portNumber=0;
    public static double startTime;
    public static double delayAmount;
    public static double perMSGTime;
    public static long  transferRate=0;
    public static int BUFSIZE= 15;
    public static int numofSentMessages=0;

    public static void main(String[] args) throws IOException
    {
        /*arg[0]=ip, arg[1]=port, arg[2]=transferRate, arg[3]=BuffSize*/
        if (args.length != 4)
        {
            System.err.printf("Missing Argument(s)");
            System.exit(1);
        }
        /*Check whether given IP is valid or not*/
        if (!chechkValidIP(args[0]))
        {
            System.out.println("Invalid IP");
            System.exit(2);
        }

        /*Get port number from args and check is it valid*/
        portNumber=Integer.parseInt(args[1]);
        if(portNumber>65535 || portNumber<0)
        {
            System.out.println("Invalid Port Number ");
            System.exit(3);
        }

        /*Get the transfer rate from the args and assign 1 if it's 0*/
        transferRate=Integer.parseInt(args[2]);
        if(transferRate==0) transferRate=1;

        /*delayAmount is the time amount which will be spent for each message
         * for example if transfer rate is 5, delayamount is 200 ms which means
         * after one message is sent and received, it'll wait to complete 200 ms
         * to start sending & receiving next message*/
        delayAmount=1000/transferRate;

        /*Get transfer rate from args and assign it to BuffSize*/
        BUFSIZE=Integer.parseInt(args[3]);
        if (BUFSIZE<=0)  BUFSIZE=1;

        try
        {
            /*Create socket*/
            clientSocket = new Socket(args[0], Integer.valueOf(args[1]));
            /*Create InputStream to get message*/
            inputstream= clientSocket.getInputStream();
            /*Create OuputStream to send message*/
            outputstream=clientSocket.getOutputStream();
        }
        catch (Exception e)
        {
            System.out.println("Server is not running");
            System.exit(-1);
        }

        /*This is the part we start sendind & receiving messages.
         * we get the current system time to accomplish the process in 1 second*/
        startTime=System.currentTimeMillis();

        /*While loop continuous until all messages are sent or
         * until our one second is about to finish*/
        while (numofSentMessages<transferRate && (System.currentTimeMillis()-startTime<970))
        {
            /*perMSGTime here represents how much it's been since the process started
             * for each message.*/
            perMSGTime=System.currentTimeMillis();

            /*Send messaage with OutputStream.write(byte[]) method*/
            outputstream.write(MSG.getBytes());

            String receivedMSG="";

            /*do-while process continuous until we read 0 bytes to buffer,
             * in other words where we got all message and stream is empty now*/
            int readBytes;
            do
            {
                byte[] buf=new byte[BUFSIZE];
                readBytes=inputstream.read(buf);
                /*Here we create received message by adding bytes from buffer to String*/
                receivedMSG+=new String(buf,0,readBytes);
            }while (receivedMSG.length()<MSG.length());

            /*Chechk received message to see if it's equal to sent message*/
            if (MSG.equals(receivedMSG))
            {
                System.out.println("Packet Number: "+ numofSentMessages+
                        " Received MSG: "+receivedMSG +
                        " Length of Received MSG: "+receivedMSG.length());

            }
            else
                System.out.println("Sent and received msg not equal");

            numofSentMessages++;
            /*Wait until perMSGTime is equal to delayAmount*/
            while (System.currentTimeMillis()-perMSGTime<delayAmount);
        }

        /*In case there is a small amount of time to complete 1 sec, wait*/
        while (System.currentTimeMillis()-startTime<1000);

        System.out.println("Total Amount For Process :"+(System.currentTimeMillis()-startTime));
        System.out.println("Number of Sent Messages: "+(numofSentMessages));
        System.out.println("Remaining Messages:"+(transferRate-numofSentMessages));
        /*close the socket*/
        clientSocket.close();
    }

    /**
     * @param IP Takes the given IP
     * @return boolean value true for valid IP and false for invalid
     */
    public  static boolean chechkValidIP(String IP)
    {
        /*Check last char of IP and return if it's not a digit (if it is dot or any character but digit*/
        if (!Character.isDigit(IP.charAt(IP.length()-1)))
            return false;
        String[] tokens=IP.split("\\.");
        if(tokens.length!=4)
            return false;
        for(String str:tokens)
        {
            /*Return false if tokens contains anything but digits*/
            if(!str.matches("[0-9]+"))
            {
                return false;
            }
            /*Return false if token numbers are not between 0-255*/
            int i=Integer.parseInt(str);
            if((i<0) || (i>255))
                return false;
        }
        return true;
    }
}