package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import model.Player;
import util.Notification;
import view.*;

import javax.swing.*;

public class ClientController implements Runnable, Notification {
    private Socket myClient;
    Thread clientThread;
    String clientName;

    LoginView loginView;
    HomeView homeView;

    GameFrame gameView;

    boolean isRunning;
    public String oppName;
    ObjectInputStream ois;
    ObjectOutputStream oos;


    private static ClientController instance;

    public static ClientController getInstance()
    {
        return instance;
    }

    public ClientController() {
        instance = this;
        loginView = new LoginView();

        loginView.setVisible(true);
        loginView.setLocationRelativeTo(null);
        homeView = new HomeView();
        
        isRunning = true;
        addEventsLoginView();
        addEventsHomeView();
    }
    
    @Override
    public void run() {
        SimpleEntry response;
        String cmd;
        Object data;
        while(isRunning) {
            Object o  = receiveData();
            response = (SimpleEntry)o;
            cmd = (String)response.getKey();
            data = response.getValue();
            System.out.println("Nhận được: " + cmd);
            switch(cmd) {
                case "GET_ONLINE_PLAYERS":
                    ArrayList<Player> onlinePlayers = (ArrayList) data ;
                    homeView.listPlayer = onlinePlayers;
                    homeView.setOnlineTable(onlinePlayers);
                    break;
                case "GET_RANK_PLAYERS":
                    ArrayList<Player> rankPlayers = (ArrayList)data;
                    homeView.listRankPlayer = rankPlayers;
                    homeView.setRankTable(rankPlayers);
                    break;
                case "CHALLENGE_PLAYER":
                    String sender = (String)data;
                    if(sender.equals(CANT_CHALLENGE)) {
                        JOptionPane.showMessageDialog(homeView, CANT_CHALLENGE);
                    }
                    else {
                        int dialogResult = JOptionPane.showConfirmDialog (homeView, "Người chơi " + sender + " thách đấu bạn, có chấp nhận không?");
                        if(dialogResult == JOptionPane.YES_OPTION){
                            sendData("REPLY_CHALLENGE", sender);
                        } else {
                            sendData("REPLY_CHALLENGE", "NO" +sender);
                        }
                    }
                    break;
                case "REPLY_CHALLENGE":
                    String msg = (String)data;
                    System.out.println(msg);
                    if(msg.equals("YES")) {
                        JOptionPane.showMessageDialog(homeView, "Người chơi đã đồng ý");
                        sendData("CREATE ROOM", clientName);
                    } else {
                        JOptionPane.showMessageDialog(homeView, "Người chơi đã từ chối");
                    }
                    break;
                case  "CREATE_ROOM":
                    oppName = (String)data;
                    gameView = new GameFrame(10, 10, 10);
                    gameView.setVisible(true);
                    break;
                case "LOSE_GAME":
                    int dialogResult = JOptionPane.showConfirmDialog (homeView, "Bạn đã thua có muốn tiếp tục chơi?");
                    if(dialogResult == JOptionPane.YES_OPTION){
                        sendData("CREATE ROOM", clientName);
                    } else {
                        homeView.setVisible(true);
                        gameView.setVisible(false);
                    }
                    break;
                case "WIN_GAME":
                    int rs = JOptionPane.showConfirmDialog (homeView, "Bạn đã thắng có muốn tiếp tục chơi?");
                    if(rs == JOptionPane.YES_OPTION){
                        sendData("CREATE ROOM", clientName);
                    } else {
                        homeView.setVisible(true);
                        gameView.setVisible(false);
                    }
            }

        }
    }
    
    //add events
    void addEventsLoginView() {
        loginView.getLoginBtn().addActionListener(ae -> {
            Player player = new Player(loginView.getUsernameText().getText(), String.valueOf(loginView.getPasswordText().getPassword()));
            openConnection();
            sendData("CHECK_PLAYER", player);
            String result = (String) receiveData();
            System.out.println("RECEIVED FROM SERVER: " + result);
            if(result.equals(LOGIN_SUCCESS)) {
                loginView.showMessage(result);
                loginView.setVisible(true);
                clientName = player.getUserName();
                clientThread = new Thread(instance);
                clientThread.start();
                sendData("GET_ONLINE_PLAYERS", null);
                sendData("GET_RANK_PLAYERS", null);
                homeView.setVisible(true);
                homeView.setLocationRelativeTo(null);
            } else if(result.equals(ALREADY_LOGIN)){
                loginView.showMessage(result);
                loginView.setVisible(true);
                homeView.setVisible(true);
                homeView.setLocationRelativeTo(null);
                clientName = player.getUserName();
                clientThread = new Thread(instance);
                clientThread.start();
                sendData("GET_ONLINE_PLAYERS", null);
                sendData("GET_RANK_PLAYERS", null);
            }
            else {
                loginView.showMessage(LOGIN_FAIL);
            }
        });

        loginView.getSignupBtn().addActionListener(ae -> {
            Player player = new Player(loginView.getUsernameText().getText(), String.valueOf(loginView.getPasswordText().getPassword()));
            openConnection();
            sendData("SIGNUP_PLAYER", player);
            String result = (String)receiveData();
            System.out.println("RECEIVED FROM SERVER: " + result);
            if(result.equals(SIGNUP_SUCCESS)) {
                loginView.showMessage(result);
                loginView.setVisible(true);

                clientName = player.getUserName();
                clientThread = new Thread(instance);
                clientThread.start();
                sendData("GET_ONLINE_PLAYERS", null);
                sendData("GET_RANK_PLAYERS", null);
                homeView.setVisible(true);
                homeView.setLocationRelativeTo(null);
            } else {
                loginView.showMessage(SIGNUP_FAIL);
            }
        });
                
        
    }

    void addEventsHomeView() {
        homeView.getRankTypeCb().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int type = homeView.getRankTypeCb().getSelectedIndex();
                if(type == 0) {
                    homeView.sortByPoint();
                }
                if(type == 1) {
                    homeView.sortByAvgPointOpp();
                }
                if(type == 2) {
                    homeView.sortByAvgTime();
                }
            }
        });

        homeView.getPlayerTable().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = homeView.getPlayerTable().getSelectedRow();
                int dialogResult = JOptionPane.showConfirmDialog (homeView, "Thách đấu người chơi này?");
                if(dialogResult == JOptionPane.YES_OPTION){
                    sendData("CHALLENGE_PLAYER", homeView.listPlayer.get(index).getUserName());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

        });
    }

    // khi client khởi tạo thì mở kết nối
    public void openConnection(){
        try {
            int serverPort = 5555;
            String serverHost = "localhost";
            myClient = new Socket(serverHost, serverPort);
            oos = new ObjectOutputStream(myClient.getOutputStream());
            ois = new ObjectInputStream(myClient.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            myClient.close();
            isRunning = false;
            ois.close();
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void sendData(String cmd, Object data){
        try {
            if(data instanceof Player) {
                Player player = (Player)data;
                SimpleEntry<String, Player> req = new SimpleEntry<>(cmd, player);
                oos.writeObject(req);
            }
            if(data == null) {
                SimpleEntry<String, Object> req = new SimpleEntry<>(cmd, null);
                oos.writeObject(req);
            }
            if(data instanceof String) {
                String s = (String)data;
                SimpleEntry<String, String> req = new SimpleEntry<>(cmd, s);
                oos.writeObject(req);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Object receiveData(){
        try {
            Object o = ois.readObject();
            if(o instanceof String){
                return o;
            }
            return o;
        } catch (Exception ex) {
            ex.printStackTrace();     
            return null;
        }
    }
}
