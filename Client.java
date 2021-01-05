import java.net.*;
import java.io.*;
import java.util.Date;

public class Client
{
    public static long ping(String ipaddr) throws UnknownHostException, IOException
    {
        InetAddress site = InetAddress.getByName(ipaddr);
        Date start = new Date();
        if (site.isReachable(1000))
        {
            Date end =new Date();
            long ping =end.getTime() - start.getTime();
            return ping;
        }

        return 1000;
    }

    public String closest_server()
    {
        String PATH = "serverslist.txt";
        File file = new File(PATH);
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String text;
            long minping = 10000;
            String minaddr = "";
            while ((text=br.readLine()) != null)
            {
                long p = ping(text);
                if (p <= minping && p != 1000)
                {
                    minping = p;
                    minaddr = text;
                }
            }
            
            br.close();
            return minaddr;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "File Not Present";
    }

    public String read_input() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String content = "";
        String line = "";
        while (!line.equals("End"))
        {
            line = br.readLine();
            if (line.equals("End"))
            {
                break;
            }
            content = content + line + "$%^";
        }
        return content;
    }

    // Function to read the file string setn from server
    public void read_output(String content) throws IOException
    {
        String[] filetext = content.split("\\$\\%\\^");
        File file = new File("temp.txt");
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < filetext.length; i++)
        {
            bw.write(filetext[i]);
            bw.newLine();
        }
        bw.close();
    }

    // Function to read file
    public static String readFile() throws IOException
    {
        String content = "";
        String text;
        File file = new File("temp.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((text=br.readLine()) != null)
        {
            content = content + text + "$%^";
        }
        br.close();
        return content;
    }

    // Open the file in Notepad
    public static void notepad() throws IOException
    {
        Runtime rs = Runtime.getRuntime();
        rs.exec("notepad temp.txt");
    }
    // This function will create a connection to server and send operations
    //The parameters are Server IP which is 127.0.0.1 and port of server is 5000
    public void start_client(String hostname, int port)
    {
        try (Socket socket = new Socket(hostname, port))
        {
            System.out.println("Connected to Server: " + hostname);
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            Console console = System.console();
            String text="";
            String remember = "";
            Boolean writeFlag = false;

            // To close connection type "Over"
            while (!text.equals("Over"))
            {
                if (writeFlag == false)
                {
                    text = console.readLine("Enter \n 1: To Create File (1 <user> <filename>)\n 2: To Read File (2 <user> <filename>) \n 3: To Delete File (3 <user> <filename>)\n 4: To Append File (4 <user> <filename>)\n 5: To Restore File (5 <user> <filename>)\n 6: To Write File (6 <user> <filename>)\n\n");
                
                    if (text.charAt(0)=='1' || text.charAt(0) =='4')
                    {
                        String content = "";
                        System.out.println("Enter File Text. To end type End");
                        content = this.read_input();
                        text = text + " " +content;
                    }
                    if (text.charAt(0)=='6')
                    {
                        remember = text.substring(1);
                        text = "9" + text.substring(1);
                        writeFlag = true;
            
                    }
                    writer.println(text);
                    InputStream input = socket.getInputStream();
                
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                
                    String server_output = reader.readLine();
                    if (server_output.equals("Locked"))
                    {
                        System.out.println("File Locked\nPlease try again after some time.");
                        writeFlag = false;
                        continue;
                    }
                    if (server_output.equals("File Not Present"))
                    {
                        System.out.println("File Not Present");
                        writeFlag = false;
                        continue;
                    }
                    
                    if (text.charAt(0)=='9' || text.charAt(0)=='2')
                    {
                        this.read_output(server_output);
//                        notepad();

                    }
                    else
                    {
                        System.out.println(server_output);
                    }
                }
                else if (writeFlag == true)
                {
                    text = console.readLine("Write 'COMMIT' to commit the update\nSave and close the file before commit\n");
                    if (text.equals("COMMIT"))
                    {
                        writeFlag = false;
                        text = readFile();
                        text = "6" + remember + " " + text;
                        writer.println(text);
                        InputStream input = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        String server_output = reader.readLine();
                        System.out.println(server_output);

                    }
                }
            }
            socket.close();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Server not found: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public static void main(String[] args)
    {
        int port = 5000;
        Client client = new Client();
        String hostname = "3.235.71.109";
        client.start_client(hostname, port);
    }
}
