

import java.io.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends Replica
{
	static Registry registry ;
	//This function will start the server at a port number
    public void start_server(int port)
    {
        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            System.out.println("Server is listening on port " + port);
            new HeartBeat().start();
            while (true)
            {
                //Accept connection from Client
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                System.out.println(socket.getInetAddress());
                
                // Creating a new thread for each Client connected
                new ServerThread(socket).start();
            }
        }
        catch (IOException ex)
        {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server();
//        System.setProperty("java.rmi.server.hostname", "127.0.0.1");

        //The port number is 5000
        
        try {
        	
			StartReplica();
			server.start_server(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static String findip() throws UnknownHostException
    {
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostAddress();
    }
    
    public static void StartReplica() throws IOException {
		int port = 50000;
		try { 
	         // Instantiating the implementation class 
                System.setProperty("java.rmi.server.hostname", findip());
                LocateRegistry.createRegistry(port);
	        	registry = LocateRegistry.getRegistry(port);

				ServerReplicaServerInterface replica = new Replica(); 
		    
		         // Exporting the object of implementation class  
		         // (here we are exporting the remote object to the stub) 
				ServerReplicaServerInterface stub = (ServerReplicaServerInterface) UnicastRemoteObject.exportObject(replica,0);  		         
		         // Binding the remote object (stub) in the registry 	 
		        registry.rebind("Replica", stub);  
		        System.out.println("register Replica:" );
	         System.err.println("Server ready"); 
	      } catch (Exception e) { 
	         System.err.println("Server exception: " + e.toString()); 
	         e.printStackTrace(); 
	      } 
	}
	
	public static ArrayList<String> readServers() throws IOException
    {
        String PATH = "AvailableServers.txt";
        File file = new File(PATH);
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<String> ip = new ArrayList<String>();
        String text;
        while ((text=br.readLine()) != null)
        {
            ip.add(text);
        }
        br.close();
        return ip;
        
    }
}
