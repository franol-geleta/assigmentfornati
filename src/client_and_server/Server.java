// Name  Franol geleta
// Id ATE/5157/09

package client_and_server;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class Server extends JFrame {

 // keywords
    private static final String SET_NAME = "__setName";
    
 // used for socket
    private ServerSocket server;
    private Socket conn;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean connected;
    private static String serverName = "Server";
    private static String clientName = "Client";
    private static final int PORT_NUMBER = 8080;
    
 // used for gui
    private final JPanel main, statusPanel, p1;
    private final JTextArea msgArea;
    private JTextField msgField;
    private final JButton sendBtn;
    private final JLabel statusLabel;
    
 // Menu
    private JMenuBar bar;
    private JMenu file, help;
    private JMenuItem changeName, exit, about;
    
    Server() {
        super("Server");
        setDefaultCloseOperation(3);
        setLayout(new BorderLayout(0, 10));
        setLocation(150, 150);
        setResizable(false);
        setSize(400, 300);
        
     // basic gui
        main = new JPanel(new BorderLayout(15, 15));
            msgArea = new JTextArea();
            msgArea.setEditable(false);
            p1 = new JPanel(new BorderLayout());
                msgField = new JTextField();
                msgField.addActionListener((e)->{
                    if(connected==true) {
                        sendMsg();
                        msgField.setText(null);
                    }
                });
                sendBtn = new JButton("Send");
                sendBtn.addActionListener((e)->{
                    if(connected==true) {
                        sendMsg();
                        msgField.setText(null);
                    }
                });
            p1.add(msgField, BorderLayout.CENTER);
            p1.add(sendBtn, BorderLayout.EAST);
        main.add(new JScrollPane(msgArea), BorderLayout.CENTER);
        main.add(p1, BorderLayout.SOUTH);
        
     // status panel
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusLabel = new JLabel("Connection Status");
        statusPanel.add(statusLabel);
        
     // menu bar
        bar = new JMenuBar();
            file = new JMenu("File");
                changeName = new JMenuItem("Change Name");
                changeName.addActionListener((e)->{
                    serverName = JOptionPane.showInputDialog(this, "Enter your new name");
                    msgArea.append("You changed your name to " + serverName + "\n");
                    try {
                        if(connected)
                            out.writeUTF(Server.SET_NAME + " " + serverName);
                    } catch(IOException ex) {}
                });
                file.add(changeName);
                file.add(new JSeparator(SwingConstants.HORIZONTAL));
                exit = new JMenuItem("Exit");
                exit.addActionListener((e)->{
                    System.exit(0);
                });
                file.add(exit);
            bar.add(file);
            help = new JMenu("Help");
                about = new JMenuItem("About");
                about.addActionListener((e)->{
                    JDialog ad = new JDialog(this, "About");
                    ad.setSize(200, 200);
                    ad.setLayout(null);
                    ad.setLocationRelativeTo(this);
                    ad.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    
                    JTextArea abt = new JTextArea("This is just for demo.");
                    abt.setEditable(false);
                    abt.setEnabled(false);
                    abt.setLineWrap(true);
                    abt.setLocation(10, 10);
                    abt.setSize(165, 115);
                    ad.add(abt);
                    
                    JButton close = new JButton("Close");
                    close.setSize(65, 25);
                    close.setLocation(110, 130);
                    close.addActionListener((event)->{
                        ad.setVisible(false);
                    });
                    ad.add(close);
                    
                    ad.setVisible(true);
                });
                help.add(about);
            bar.add(help);
        add(bar, BorderLayout.NORTH);
        
        add(main);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
 // initiates the connection
    private void connectAndStart() throws IOException {
     // searching label animation
    	new Thread(()->{
            int i=0;
            while (connected==false) {
                switch (i%5) {
                    case 0:
                        statusLabel.setText("Waiting for client.");
                        break;
                    case 1:
                        statusLabel.setText("Waiting for client..");
                        break;
                    case 2:
                        statusLabel.setText("Waiting for client...");
                        break;
                    case 3:
                        statusLabel.setText("Waiting for client....");
                        break;
                    default:
                        statusLabel.setText("Waiting for client.....");
                        break;
                }
                i++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {}
            }
        }).start();
        
     // host and wait for connection from client
        server = new ServerSocket(PORT_NUMBER);
        conn = server.accept();
        in = new DataInputStream(conn.getInputStream());
    	out = new DataOutputStream(conn.getOutputStream());
        
     // send out name
        out.writeUTF(serverName);
     // get client name
        clientName = in.readUTF();
        
        connected = true;
        msgArea.append(clientName + " connected.\n");
        statusLabel.setText("Connected");
        
        acceptMsg();
    }
    
 // used to broadcast message to client connected
    private void sendMsg() {
        String temp = msgField.getText();

     // if entered message is a keyword
        if(temp.startsWith("__")) {
         // changing name
            if(temp.startsWith(Server.SET_NAME)) {
             // sets the server name
                serverName = temp.substring(Server.SET_NAME.length()+1);
             // show the change on the display
                msgArea.append("You changed your name to " + serverName + "\n");
                try {
                 // broadcasts the name change to client
                    out.writeUTF(temp);
                } catch(IOException ex) {}
            }
            
         // unknown keyword
            else {
                msgArea.append("Invalid keyword. Contact the developer for keywords\n");
            }
        }

     // if it's ordianry message to client
        else {
            try {
             // sends message
                out.writeUTF(temp);

             // displays sent message
                msgArea.append(serverName + ": " + temp + "\n");
            } catch(IOException ex) {}
        }
    }
    
 // used to accept message from client connected
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
                    if(temp.startsWith(SET_NAME)) {
                     // temp name to store previous name
                        String name = clientName;
                     // sets the client name
                        clientName = temp.substring(SET_NAME.length()+1);
                     // show the change on the display
                        msgArea.append(name + " changed name to " + clientName + "\n");
                    }
                    
                }
                
             // checks if it's an ordinary message from client
                else {
                 // displays received message
                    msgArea.append(clientName + ": " + temp + "\n");
                }
             
            }
        }).start();
    }
    
 // checks the connection status
    private void checkConnection() {
        new Thread(()->{
            while(true) {
                while(connected)
                    statusLabel.setText("Connected");
            }
        }).start();
    }
    
    public static void main(String[] args) throws IOException {
        try {
            for(UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
                if("Nimbus".equals(i.getName())) {
                    UIManager.setLookAndFeel(i.getClassName());
                }
            }
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
        }
        
        Server server = new Server();
        server.setVisible(true);
        
        server.connectAndStart();
        
        server.addWindowStateListener((e)->{
            if(!e.getWindow().isShowing()) {
                System.out.println("Window can be closed or minimized.");
            } else {
                System.out.println("There's nothing");
            }
        });
    }
}
