package com.kylehodgetts.sunka;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.kylehodgetts.sunka.controller.GameManager;
import com.kylehodgetts.sunka.controller.ViewManager;
import com.kylehodgetts.sunka.controller.bus.EventBus;
import com.kylehodgetts.sunka.event.NewGame;
import com.kylehodgetts.sunka.event.TrayOnClickListener;
import com.kylehodgetts.sunka.model.AI;
import com.kylehodgetts.sunka.model.Board;
import com.kylehodgetts.sunka.model.GameState;
import com.kylehodgetts.sunka.model.Player;


/**
 * @author Phileas Hocquard
 * @author Charlie Baker
 * @author Jonathan Burton
 * @version 1.3
 */
public class BoardActivity extends AppCompatActivity {

    //TODO: Implement OnPause, OnResume, OnStop methods. And within all other necessary classes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        //TODO add gametype check here, and pass that to the relevant object below

        GameState state = new GameState(new Board(), new Player(), new Player());
        EventBus<GameState> bus = new EventBus<>(state, this);
        bus.registerHandler(new GameManager(bus));
        bus.registerHandler(new AI(bus)); //This doesn't disable the second player touch yet
        bus.registerHandler(new ViewManager(bus));

        makeXMLButtons(bus);

        bus.feedEvent(new NewGame());
    }

    private void makeXMLButtons(EventBus bus) {
        GridLayout gridlayout = (GridLayout) findViewById(R.id.gridLayout);

        for(int i=0; i < 2; ++i) {
            for(int j=0; j < 7; ++j) {
                final LinearLayout linearLayout;
                if(i==0) { linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.buttonlayoutb, gridlayout, false); }
                else { linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.buttonlayouta, gridlayout, false); }
                linearLayout.setId(Integer.parseInt(i + "" + j));


                ImageButton button = (ImageButton) linearLayout.findViewById(R.id.button);

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.columnSpec = GridLayout.spec(i == 1?6-j:j);
                param.rowSpec = GridLayout.spec((i+1)%2);
                param.width=GridLayout.LayoutParams.WRAP_CONTENT;
                param.height=GridLayout.LayoutParams.WRAP_CONTENT;
                param.setMargins(10,10,10,10);
                param.setGravity(Gravity.FILL);
                linearLayout.setLayoutParams(param);
                gridlayout.addView(linearLayout);

                button.setOnClickListener(new TrayOnClickListener(j, i, bus));
            }
        }
    }
}
