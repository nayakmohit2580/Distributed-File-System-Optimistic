
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException; 

public interface ServerReplicaServerInterface extends Remote{
	
 public boolean updateFile(String user, String filename, String content) throws RemoteException, IOException;
 
 public boolean createFile(String user, String filename, String content) throws RemoteException,IOException;
 
 public boolean deleteFile(String user, String filename) throws RemoteException,IOException;
 
 public boolean restoreFile(String user, String filename) throws RemoteException,IOException;
 
 public boolean appendFile(String user, String filename, String content) throws RemoteException,IOException;

 public boolean ExecuteOperation(String operation, String user, String filename, String content) throws RemoteException,IOException;

 public boolean replicate(String operation,String user,String filename,String content) throws RemoteException,IOException;


}

