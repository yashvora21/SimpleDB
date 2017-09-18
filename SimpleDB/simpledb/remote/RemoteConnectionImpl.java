package simpledb.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import simpledb.buffer.Buffer;
import simpledb.file.Block;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

/**
 * The RMI server-side implementation of RemoteConnection.
 * @author Edward Sciore
 */
@SuppressWarnings("serial") 
class RemoteConnectionImpl extends UnicastRemoteObject implements RemoteConnection {
   private Transaction tx;
   
   /**
    * Creates a remote connection
    * and begins a new transaction for it.
    * @throws RemoteException
    */
   RemoteConnectionImpl() throws RemoteException {
      tx = new Transaction();
   }
   
   /**
    * Creates a new RemoteStatement for this connection.
    * @see simpledb.remote.RemoteConnection#createStatement()
    */
   public RemoteStatement createStatement() throws RemoteException {
      return new RemoteStatementImpl(this);
   }
   
   /**
    * Closes the connection.
    * The current transaction is committed.
    * @see simpledb.remote.RemoteConnection#close()
    */
   public void close() throws RemoteException {
      tx.commit();
   }
   
// The following methods are used by the server-side classes.
   
   /**
    * Returns the transaction currently associated with
    * this connection.
    * @return the transaction associated with this connection
    */
   Transaction getTransaction() {  
      return tx;
   }
   
   /**
    * Commits the current transaction,
    * and begins a new one.
    */
   void commit() {
      tx.commit();
      ArrayList<Integer> stats = SimpleDB.fileMgr().getFileStatistics();
      System.out.println("File statistics=>\nNumber of blocks read: " + stats.get(0) + "\nNumber of block written: " + stats.get(1));
      
      Map<Block, Buffer> bufferPool = SimpleDB.bufferMgr().getBufferStatistics();
//      System.out.println("Buffer statistics=>\nNumber of available buffers: " + stats.get(2) + "\nNumber of blocks read: " + stats.get(0) + "\nNumber of block written: " + stats.get(1));
      Iterator it = bufferPool.entrySet().iterator();
	  while (it.hasNext()) {
		  Map.Entry pair = (Map.Entry)it.next();
		  Buffer buff = (Buffer) pair.getValue();
		  System.out.println("Buffer: " + buff + "\nTotal Reads: " + buff.getBufferReads() + "\nTotal Writes: " + buff.getBufferWrites());
	  }
      tx = new Transaction();
   }
   
   /**
    * Rolls back the current transaction,
    * and begins a new one.
    */
   void rollback() {
      tx.rollback();
      ArrayList<Integer> stats = SimpleDB.fileMgr().getFileStatistics();
      System.out.println("Number of blocks read: " + stats.get(0) + "\nNumber of block written: " + stats.get(1));
      
      Map<Block, Buffer> bufferPool = SimpleDB.bufferMgr().getBufferStatistics();
//    System.out.println("Buffer statistics=>\nNumber of available buffers: " + stats.get(2) + "\nNumber of blocks read: " + stats.get(0) + "\nNumber of block written: " + stats.get(1));
      Iterator it = bufferPool.entrySet().iterator();
	  while (it.hasNext()) {
		  Map.Entry pair = (Map.Entry)it.next();
		  Buffer buff = (Buffer) pair.getValue();
		  System.out.println("Buffer: " + buff + "\nTotal Reads: " + buff.getBufferReads() + "\nTotal Writes: " + buff.getBufferWrites());
	  }
	  tx = new Transaction();
   }
}

