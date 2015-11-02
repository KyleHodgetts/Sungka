package com.kylehodgetts.sunka;

import android.view.View;

import com.kylehodgetts.sunka.controller.bus.EventBus;
import com.kylehodgetts.sunka.event.PlayerChoseTray;
import com.kylehodgetts.sunka.model.GameState;

/**
 * @author Adam Chlupacek
 * @version 1.1
 *          A listener for tray clicks
 */
public class TrayOnClick implements View.OnClickListener {

    private int trayIndex, playerIndex;
    private EventBus<GameState> bus;

    public TrayOnClick(int trayIndex, int playerIndex, EventBus<GameState> bus) {
        this.trayIndex = trayIndex;
        this.playerIndex = playerIndex;
        this.bus = bus;
    }

    @Override
    public void onClick(View v) {
        bus.feedEvent(new PlayerChoseTray(trayIndex, playerIndex));
    }
}
