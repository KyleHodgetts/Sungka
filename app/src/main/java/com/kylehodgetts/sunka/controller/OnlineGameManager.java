package com.kylehodgetts.sunka.controller;

import android.app.Activity;
import android.util.Log;

import com.kylehodgetts.sunka.controller.wifi.SingletonSocket;
import com.kylehodgetts.sunka.controller.bus.Event;
import com.kylehodgetts.sunka.controller.bus.EventBus;
import com.kylehodgetts.sunka.controller.bus.EventHandler;
import com.kylehodgetts.sunka.event.EndGame;
import com.kylehodgetts.sunka.event.PlayerChoseTray;
import com.kylehodgetts.sunka.model.GameState;
import com.kylehodgetts.sunka.util.Tuple2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Kyle Hodgetts
 * @version 1.0
 * Handles the communication between the two devices for the duration of the game
 */
public class OnlineGameManager extends EventHandler<GameState>{

    private EventBus<GameState> bus;
    private Socket socket;
    private boolean gameIsRunning;

    /**
     *
     * @param bus the games event bus
     */
    public OnlineGameManager(final EventBus<GameState> bus) {
        super("OnlineManager");
        socket = SingletonSocket.getSocket();
        this.bus = bus;
        gameIsRunning = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gameIsRunning = true;
                    Log.d("OnlineGameManager: ", "Game is running");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(gameIsRunning){
                        if(socket.getInputStream().available() > 0 && socket.getLocalAddress() != socket.getInetAddress()) {
                            String received = bufferedReader.readLine();
                            if(received != null) {
                                bus.feedEvent(new PlayerChoseTray(1, Integer.parseInt(received)));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public Tuple2<GameState, Boolean> handleEvent(Event event, GameState state) {
        if (event instanceof PlayerChoseTray && ((PlayerChoseTray) event).getPlayerIndex() == 0){
            try {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                writer.println(((PlayerChoseTray) event).getTrayIndex());
                writer.flush();
                return new Tuple2<>(state, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (event instanceof EndGame) {
            gameIsRunning = false;
        }
        return new Tuple2<>(state, false);
    }

    @Override
    public void updateView(GameState state, Activity activity) {}
}
