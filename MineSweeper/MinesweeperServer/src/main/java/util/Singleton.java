package util;

import java.util.ArrayList;

import model.Player;

public class Singleton {

    private final ArrayList<Player> players;

    private static Singleton instance;

    private Singleton(){
        players = new ArrayList<>();
    }

    public static Singleton getInstance(){
        if (instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayers(Player player) {
        players.add(player);
    }
}