

// Name  Franol geleta
// Id ATE/5157/09


package client_and_server;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;

public class Client extends JFrame {

 // keywords
	private final static String setName = "__setName";
	
 // used for socket
	private Socket conn;
	private DataInputStream in;
	private DataOutputStream out;
	private static String host = "localhost";
	private static String serverName = "Server";
	private static String clientName = "Client";
	private boolean connected;
	private static int portNumber = 8080;
	
 // used for gui
	private JPanel main, statusPanel, p1;
    private JTextArea msgArea;
    private JTextField msgField;
    private JButton sendBtn;
    private JLabel statusLabel;
    
    Client() {
        super("Client 2");
        setDefaultCloseOperation(3);
        setLayout(new BorderLayout());
        setLocation(600, 150);
        setResizable(false);
        setSize(400, 300);
        
     // basic gui
        main = new JPanel(new BorderLayout(15, 15));
            msgArea = new JTextArea();
            msgArea.setEditable(false);
            p1 = new JPanel(new BorderLayout());
                msgField = new JTextField();
                msgField.addActionListener((e)->{
                    sendMsg();
                    msgField.setText(null);
                });
                sendBtn = new JButton("Send");
                sendBtn.addActionListener((e)->{
            		sendMsg();
            		msgField.setText(null);
                });
            p1.add(msgField, BorderLayout.CENTER);
            p1.add(sendBtn, BorderLayout.EAST);
        main.add(new JScrollPane(msgArea), BorderLayout.CENTER);
        main.add(p1, BorderLayout.SOUTH);
        
     // status panel
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusLabel = new JLabel("Connection Status");
        statusPanel.add(statusLabel);
        
        add(main);
        add(new JPanel(), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }

 // used to connect to a server
    private void connectAndStart() {
	 // searching label animation
    	new Thread(()->{
			int i=0;
			while (connected==false) {
				if(i%5==0)
					statusLabel.setText("Connecting.");
				else if(i%5==1)
					statusLabel.setText("Connecting..");
				else if(i%5==2)
					statusLabel.setText("Connecting...");
				else if(i%5==3)
					statusLabel.setText("Connecting....");
				else
					statusLabel.setText("Connecting.....");
				i++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {}
			}
		}).start();
    	
   	 // try connecting to a host
		do {
			try {
				conn = new Socket(host, portNumber);
				in = new DataInputStream(conn.getInputStream());
		    	out = new DataOutputStream(conn.getOutputStream());
		    	
		    	connected = true;
		    	statusLabel.setText("Connected");
		    	msgArea.append("Connected to server!\n");
		    	
		    	acceptMsg();
			} catch (UnknownHostException ex) {
			} catch (IOException ex) {}
		} while (!(conn instanceof Socket));
    }
    
 // used to broadcast message to server connected
    private void sendMsg() {
    	String temp = msgField.getText();

        // if entered message is a keyword
           if(temp.startsWith("__")) {
            // changing name
               if(temp.startsWith(Client.setName)) {
                // sets the client name
                   clientName = temp.substring(Client.setName.length()+1);
                // show the change on the display
                   msgArea.append("You changed your name to " + clientName + "\n");
                   try {
                    // broadcasts the name change to server
                       out.writeUTF(temp);
                   } catch(IOException ex) {}
               }
               
            // unknown keyword
               else {
                   msgArea.append("Invalid keyword. Contact the developer for keywords\n");
               }
           }

        // if it's ordianry message to server
           else {
               try {
                // sends message
                   out.writeUTF(temp);

                // displays sent message
                   msgArea.append(clientName + ": " + temp + "\n");
               } catch(IOException ex) {}
           }
    }
    
 // used to accept messages broadcasted from server
    private void acceptMsg() {
    	new Thread(()->{
            // constantly waiting for incoming messages
               while(true) {
                   String temp = null;
                   try{
                       temp = in.readUTF();
                   } catch(IOException ex) {}
                   
                // checks if it starts with a keyword
                   if(temp.startsWith("__")) {
                       
                    // sets client name
                       if(temp.startsWith(setName)) {
                    	// temp name to store previous name
                           String name = serverName;
                        // sets the server name
                           serverName = temp.substring(setName.length()+1);
                        // show the change on the display
                           msgArea.append(name + " changed name to " + serverName + "\n");
                       }
                       
                   }
                   
                // checks if it's an ordinary message from client
                   else {
                    // displays received message
                       msgArea.append(serverName + ": " + temp + "\n");
                   }
                
               }
           }).start();
    }
    
	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
            for(UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
                if("Nimbus".equals(i.getName())) {
                    UIManager.setLookAndFeel(i.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
        } catch (IllegalAccessException ex) {
        } catch (InstantiationException ex) {
        } catch (UnsupportedLookAndFeelException ex) {}

		Client client = new Client();
		
		client.setVisible(true);
		
		client.connectAndStart();
	}

}
