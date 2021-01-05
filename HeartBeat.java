import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HeartBeat extends Thread
{
    public static ArrayList<String> readServers() throws IOException
    {
        String PATH = "servers.txt";
        File file = new File(PATH);
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<String> ip = new ArrayList<String>();
        String text;
        String currentIp =  findip();
        while ((text=br.readLine()) != null)
        {
            if (!(text.equals(currentIp)))
            {
                ip.add(text);
            }
        }
        br.close();
        return ip;
        
    }

    public static void writeServers(ArrayList<String> ip) throws IOException
    {
        String PATH = "AvailableServers.txt";
        File file = new File(PATH);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < ip.size(); i++)
        {
            bw.write(ip.get(i));
            bw.newLine();
        }
        bw.close();
    }    

    public static boolean sendPingRequest(String ipAddress) throws UnknownHostException, IOException
    {
        InetAddress system = InetAddress.getByName(ipAddress);
        //System.out.println("Sending Ping Request to " + ipAddress);
        
        if (system.isReachable(5000))
        { 
            //System.out.println("IP reachable  "+ipAddress);
            return true;
        }
        else
        {
            //System.out.println("IP not reachable  "+ipAddress);
            return false;
        }
    }

    public static String findip() throws UnknownHostException
    {
        InetAddress inetAddress = InetAddress.getLocalHost();
        //System.out.println("IP Address:- " + inetAddress.getHostAddress());
        //System.out.println("Host Name:- " + inetAddress.getHostName());
        return inetAddress.getHostAddress();
    }

    public void run()
    {
        while (true)
        {
            try
            {
                //System.out.println("Inside Hearbeat  ");
                ArrayList<String> ip = readServers();
                ArrayList<String> available = new ArrayList<String>();
                for (int i = 0; i < ip.size(); i++)
                {
                    if (sendPingRequest(ip.get(i)) == true)
                        available.add(ip.get(i));
                }
                writeServers(available);
                
                TimeUnit.SECONDS.sleep(5);
            }
            catch (Exception ex)
            {
                System.out.println("HeartBeat exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}