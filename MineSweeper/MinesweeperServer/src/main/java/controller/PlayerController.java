package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import util.*;
import model.Player;

import static java.util.Comparator.comparing;

public class PlayerController { // controller xử lý dữ liệu của player
    private final Singleton connection;

    private static PlayerController instance;

    public static PlayerController getInstance() {
        if (instance == null) {
            instance = new PlayerController();
        }
        return instance;
    }
    
    public PlayerController() {
        this.connection = Singleton.getInstance();
    }

    public Player checkPlayer(Player player) {
        List<Player> listPlayer = connection.getPlayers();
        for(Player kPlayer : listPlayer){
            if(Objects.equals(player.getUserName(), kPlayer.getUserName())){
                return player;
            } else {
                return null;
            }
        }
        return null;
    }

    public ArrayList<Player> getRankPlayersByPoint() {
        ArrayList<Player> tPlayer = connection.getPlayers();
        tPlayer.sort(comparing(Player::getPoint));
        return tPlayer;
    }

    public void UpdateStatus(String player, String status) {
        List<Player> listPlayer = connection.getPlayers();
        for (Player value : listPlayer) {
            if (value.getUserName().equals(player)) {
                value.setStatus(status);
            }
        }
    }

    public void updatePoint(String player, float point) {
        ArrayList<Player> listPlayer = connection.getPlayers();
        for (Player value : listPlayer) {
            if (value.getUserName().equals(player)) {
                value.setPoint(point);
            }
        }
    }

    public String insert(Player player) {
        connection.addPlayers(player);
        return "OK";
    }
}
