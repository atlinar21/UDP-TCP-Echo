package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPEchoClient
{
    public static   final   String MSG= "An Echo Message!";
    public static   double  startTime;
    public static   double  delayAmount;
    public static   double  portNumber=0;
    public static   double  perMSGTime;
    public static   long    transferRate=0;
    public static   int     MYPORT= 0;
    public static   int     BUFSIZE= 10;
    public static   int     numofSentMessages=0;


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
        byte[] buf= new byte[BUFSIZE];


        /* Create socket */
        DatagramSocket socket= new DatagramSocket(null);

        /* Create local endpoint using bind() */
        SocketAddress localBindPoint= new InetSocketAddress(MYPORT);
        socket.bind(localBindPoint);

        /* Create remote endpoint */
        SocketAddress remoteBindPoint=
                new InetSocketAddress(args[0],
                        Integer.valueOf(args[1]));

        /* Create datagram packet for sending message */
        DatagramPacket sendPacket=
                new DatagramPacket(MSG.getBytes(),
                        MSG.length(),
                        remoteBindPoint);

        /* Create datagram packet for receiving echoed message */
        DatagramPacket receivePacket= new DatagramPacket(buf, buf.length);

        /*This is the part we start sendind & receiving messages.
         * we get the current system time to accomplish the process in 1 second*/
        startTime=System.currentTimeMillis();

        while (numofSentMessages<transferRate && (System.currentTimeMillis()-startTime<970))
        {
            /*perMSGTime here represents how much it's been since the process started
             * for each message.*/
            perMSGTime=System.currentTimeMillis();
            /* Send and receive message*/
            socket.send(sendPacket);
            socket.receive(receivePacket);

            /* Compare sent and received message */
            String receivedString=
                    new String(receivePacket.getData(),
                            receivePacket.getOffset(),
                            receivePacket.getLength());
            if (receivedString.compareTo(MSG) == 0)
                System.out.println(receivePacket.getLength()+"bytes sent and received");
            else
            {
                System.out.println("Sent and received msg not equal");
                System.out.println("Received Message: "+receivedString);
            }

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
        socket.close();
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