package com.kylehodgetts.sunka;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kylehodgetts.sunka.controller.AIManager;
import com.kylehodgetts.sunka.controller.AnimationManager;
import com.kylehodgetts.sunka.controller.GameManager;
import com.kylehodgetts.sunka.controller.OnlineGameManager;
import com.kylehodgetts.sunka.controller.ViewManager;
import com.kylehodgetts.sunka.controller.bus.EventBus;
import com.kylehodgetts.sunka.event.NewGame;
import com.kylehodgetts.sunka.event.TrayOnClickListener;
import com.kylehodgetts.sunka.model.Board;
import com.kylehodgetts.sunka.model.GameState;
import com.kylehodgetts.sunka.model.Player;
import com.kylehodgetts.sunka.uiutil.ShellDrawable;
import com.kylehodgetts.sunka.util.FileUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Board Activity Class used to create the game board. Inflate and style the board to the user's
 * device screen. Also to instantiate the logic objects to control and handle the game events.
 *
 * @author Phileas Hocquard
 * @author Charlie Baker
 * @author Jonathan Burton
 * @author Kyle Hodgetts
 * @version 1.7
 */
public class BoardActivity extends AppCompatActivity {

    private static int gameType;

    public static final int ONEPLAYER = 1;
    public static final int TWOPLAYER = 2;
    public static final int ONLINE = 3;

    public static final String EXTRA_INT = "com.kylehodgetts.sunka.boardactivity.gametype";
    private static final String FILE_NAME = "sungkasave";

    private View decorView;
    private boolean areShellsCreated;
    private GameState state;

    private HashMap<Integer, ArrayList<ShellDrawable>> shellAllocations;


    /**
     * Creates the board activity, instantiates all necessary objects and sets the content view for
     * the user's device screen to the activity board xml file.
     *
     * @param savedInstanceState Bundle used to restore any previously stored information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decorView = getWindow().getDecorView();
        areShellsCreated = false;

        this.setContentView(R.layout.activity_board);

        gameType = getIntent().getIntExtra(EXTRA_INT, 0);
        state = (GameState) FileUtility.readFromSaveFile(this, FILE_NAME);
        if(state == null) {
            state = new GameState(new Board(), new Player(), new Player());
        }

        EventBus<GameState> bus = new EventBus<>(state, this);
        bus.registerHandler(new GameManager(bus));
        bus.registerHandler(new ViewManager(bus, this));
        bus.registerHandler(new AnimationManager(bus, this));

        if (gameType == ONEPLAYER) {
            bus.registerHandler(new AIManager(bus));
            makeXMLButtons(bus, false);
        } else if (gameType == TWOPLAYER) {
            makeXMLButtons(bus, true);
        } else if (gameType == ONLINE) {
            bus.registerHandler(new OnlineGameManager(bus));
            makeXMLButtons(bus, false);
        }
        bus.registerHandler(new GameManager(bus));
        bus.registerHandler(new ViewManager(bus, this));
        bus.registerHandler(new AnimationManager(bus, this));
        bus.feedEvent(new NewGame());
    }

    /**
     * Triggers on the device's system back key being pressed
     * @param keyCode   Key Code
     * @param event     Key Event
     * @return          onKeyDown event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setMessage("Will you want to return to this game?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            FileUtility.saveGame(BoardActivity.this, FILE_NAME, state);
                            returnToMainMenu();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            returnToMainMenu();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Method used to inflate the relevant trays in the correct place on the game activity board.
     * This method also attaches the necessary onClickListener to each tray.
     *
     * @param bus The game Event Bus so the tray can be attached with the onClickListener
     */
    private void makeXMLButtons(EventBus bus, boolean bothSetsButtonsClickable) {
        GridLayout gridlayout = (GridLayout) findViewById(R.id.gridLayout);

        for(int i=1; i >= 0; --i) {
            for(int j=6; j >= 0; --j) {
                final LinearLayout linearLayout;
                if (i == 0) {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.buttonlayoutb, gridlayout, false);
                } else {
                    linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.buttonlayouta, gridlayout, false);
                }
                linearLayout.setId(Integer.parseInt(i + "" + j));

                RelativeLayout button = (RelativeLayout) linearLayout.findViewById(R.id.button);

                // Due to grid layout weights only being available from API 21 onwards to scale the layout
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    param.columnSpec = GridLayout.spec(i == 1?6-j:j,1f);
                    param.rowSpec = GridLayout.spec((i+1)%2, 1f);

                }
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.setGravity(Gravity.FILL_HORIZONTAL);
                linearLayout.setLayoutParams(param);
                gridlayout.addView(linearLayout);

                //we don't want the opposite side clickable if there are not two local players
                if (i == 0 || i == 1 && bothSetsButtonsClickable) {
                    button.setOnClickListener(new TrayOnClickListener(i, j, bus));
                }
            }
        }
    }


    /**
     * Makes the game board activity completely full screen, but allowing access to the users status bar
     * by them swiping from the top of the screen.
     * This method also ensures each of the trays are square by setting their height to match the trays
     * inflated width.
     *
     * @param hasFocus boolean if the activity currently has focus by the user.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            }

            GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);

            for(int i = 0; i < gridLayout.getChildCount(); ++i) {
                LinearLayout child = (LinearLayout) gridLayout.getChildAt(i);
                int width = gridLayout.getChildAt(i).getWidth();
                GridLayout.LayoutParams llparams = (GridLayout.LayoutParams) child.getLayoutParams();
                llparams.width = width;
                llparams.height = width + child.findViewById(R.id.tv).getHeight();
                gridLayout.getChildAt(i).setLayoutParams(llparams);

                // Creates shells if they have not already been created on the board
                if(!areShellsCreated) {
                    createShells((RelativeLayout) gridLayout.getChildAt(i).findViewById(R.id.button));
                    initialiseShellAllocations();
                }
            }
            areShellsCreated = true;
        }
    }

    /**
     * Method that allows us to return to the main menu .
     */
    public void returnToMainMenu() {
        Intent intent = new Intent(BoardActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     *
     * @return
     *          1: Player is playing against an AI player,
     *          2: Player is playing head to head with another player
     *          3: Player is playing with another player over WiFi
     */
    public static int getGameType() {
        return gameType;
    }

    /**
     * Creates the shell drawable views for each tray and adds them to the relevant parent being their
     * currently allocated tray for the current game state.
     *
     * @param button indicating the tray button the shells are to be assigned to
     */
    public void createShells(RelativeLayout button) {
        Random random = new Random();
        int numberOfShells = state.getBoard().getTray(button.getId()/10, button.getId()%10);

        for(int shell=0; shell < numberOfShells; ++shell) {
            ShellDrawable shellDrawable = new ShellDrawable(this, random.nextInt(button.getWidth()/2),
                    random.nextInt(button.getHeight()/2), 40, 20);
            RelativeLayout.LayoutParams shellParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            shellParams.leftMargin = 0;
            shellParams.topMargin = 0;
            shellParams.rightMargin = 0;
            shellParams.bottomMargin = 0;

            shellDrawable.setLayoutParams(shellParams);
            shellDrawable.setRotation(new Random().nextInt(360));
            button.addView(shellDrawable, shellParams);

        }
    }

    /**
     * Initialises the HashMap containing mappings to ArrayLists holding the allocatation of each
     * shell to it's currently allocated tray based on the current game state.
     *
     */
    private void initialiseShellAllocations() {
        shellAllocations = new HashMap<>();
        for(int player=0; player < 2; ++player) {
            for(int tray=0; tray < 7; ++tray) {
                ArrayList<ShellDrawable> shellArrayList = new ArrayList<>();
                shellAllocations.put(Integer.parseInt(player+""+tray), shellArrayList);

                LinearLayout trayLinearLayout = (LinearLayout) findViewById(Integer.parseInt(player+""+tray));
                RelativeLayout trayButton = (RelativeLayout) trayLinearLayout.findViewById(R.id.button);

                int numberOfShells = state.getBoard().getTray(player, tray);
                for(int shell=0; shell < numberOfShells; ++shell) {
                    shellArrayList.add((ShellDrawable) trayButton.getChildAt(shell));
                }
            }
        }
    }

    public HashMap<Integer, ArrayList<ShellDrawable>> getShellAllocations() { return shellAllocations; }
}
