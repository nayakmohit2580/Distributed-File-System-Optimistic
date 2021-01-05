

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry;  


public class Replica implements ServerReplicaServerInterface{
	private static String PATH = "..//Directory";
	
	@Override
	public synchronized boolean ExecuteOperation(String operation, String user, String filename, String content) throws RemoteException,IOException
	{
		if (operation.equals("write"))
		{
			this.updateFile(user, filename, content);
			this.replicate("write",user, filename, content);
		}
		else if (operation.equals("append"))
		{
			this.appendFile(user, filename, content);
			this.replicate("append",user, filename, content);
		}
		return true;
	}

	@Override
	public synchronized boolean replicate(String operation,String user,String filename,String content) throws RemoteException,IOException
	{
		boolean success = false;
		try {  
            // Getting the registry 
            ArrayList<String> replicaIP = readServers();
            
            int port = 50000;
            // Looking up the registry for the remote object 
             for(int i = 0; i < replicaIP.size(); i++) {
                System.setProperty("java.rmi.server.hostname", replicaIP.get(i));
                Registry registry = LocateRegistry.getRegistry(replicaIP.get(i), port); 
                ServerReplicaServerInterface replica = (ServerReplicaServerInterface) registry.lookup("Replica"); 
           
                // Calling the remote method using the obtained object 
                if (operation == "write")
                {
                    success = replica.updateFile(user, filename, content);
                }
                else if (operation == "append")
                {
                    success = replica.appendFile(user, filename, content);
                }
                if(success = false) {
                   System.out.println("write err happens at" + " replica" + i);
                }
                System.out.println("write success at" + " replica" + i);
                
            }
            
            // System.out.println("Remote method invoked"); 
         } catch (Exception e) {
            System.err.println("Client exception: " + e.toString()); 
            e.printStackTrace(); 
         } 
		return success;
		
	}

	@Override
	public synchronized boolean updateFile(String user, String filename, String content) throws RemoteException, IOException {
		// TODO Auto-generated method stub
				System.out.println("Hello from Update Replica");
				String directoryName = PATH.concat("/"+user);
		        String fileName = filename+".txt";
		        String[] filetext = content.split("\\$\\%\\^");

		        //Creating File
		        File file = new File(directoryName + "/" + fileName);
				
		        try{
		            FileWriter fw = new FileWriter(file.getAbsoluteFile());
		            BufferedWriter bw = new BufferedWriter(fw);
		            for (int i = 0; i < filetext.length; i++)
		            {
		                bw.write(filetext[i]);
		                bw.newLine();
		            }
		            
		            bw.close();
		        }
		        catch (IOException e)
		        {
		            e.printStackTrace();
		        }
		        return true;
		        
			}
	@Override
	public synchronized boolean createFile(String user, String filename, String content) throws IOException{
		String directoryName = PATH.concat("/"+user);
        String fileName = filename;
        File directory = new File(directoryName);
        String[] filetext = content.split("\\$\\%\\^");

        //If directory doesn't exist then it creates
        if (! directory.exists())
        {
            directory.mkdir();
        }
        //Creating File
        File file = new File(directoryName + "/" + fileName);
        if (file.exists())
        {
//            log(user,filename,"Create","File Already Exists.");
            return false;
        }
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < filetext.length; i++)
            {
                bw.write(filetext[i]);
                bw.newLine();
            }
            
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
//        log(user,filename,"Create","File Created Successfully");
        return true;
	}
	@Override
	 public synchronized boolean deleteFile(String user, String filename) throws IOException{
		String directoryName = PATH.concat("/"+user);
        String fileName = filename;
        File file = new File(directoryName + "/" + fileName);

        //If file present then it deletes the file
        if (file.exists())
        {
            String tempName = "..//Deleted/" + user;
            File directory = new File(tempName);
            if (! directory.exists())
                directory.mkdir();
            file.renameTo(new File(tempName + "/"+file.getName()));
//            log(user,filename,"Delete","File Deleted Successfully");
            return true;
        }
        else
        {
//            log(user,filename,"Delete","File Not Present");
            return false;
        }
	}
	 @Override
	 public synchronized boolean restoreFile(String user, String filename) throws IOException{
		 String directoryName = PATH.concat("/"+user);
	        String fileName = filename;
	        File file = new File("..//Deleted/" + user+"/"+filename);
	        if (file.exists())
	        {
	            file.renameTo(new File(directoryName + "/" + fileName));
//	            log(user,filename,"Restore","File Restored");
	            return true;
	        }
	        else
	        {
//	            log(user,filename,"Restore","File Doesn't Exist");
	            return false;
	        }
	 }
	 @Override
	 public synchronized boolean appendFile(String user, String filename, String content) throws IOException{
		 String directoryName = PATH.concat("/"+user);
	        String fileName = filename+".txt";;
	        File directory = new File(directoryName);
	        String[] filetext = content.split("\\$\\%\\^");

	        //If directory doesn't exist then it creates
	        if (! directory.exists())
	        {
	            directory.mkdir();
	        }
	        
	        File file = new File(directoryName + "/" + fileName);
	        file.getParentFile().mkdirs();
	        file.createNewFile();   	
		    try{
		        FileWriter fw = new FileWriter(file, true);
		        BufferedWriter bw = new BufferedWriter(fw);
		        for (int i = 0; i < filetext.length; i++)
		         {
		            bw.write(filetext[i]);
		            bw.newLine();
		         }
		            
		            bw.close();
		        }
		    catch (IOException e)
		        {
		            e.printStackTrace();
		        }

	        return true;
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
