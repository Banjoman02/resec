package me.kesh.resec;

import api.ModPlayground;
import api.listener.Listener;
import api.common.GameClient;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.mod.StarLoader;
import api.listener.events.input.KeyPressEvent;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.common.TextAreaInput;
import org.schema.game.client.data.GameClientState;

import java.io.IOException;
import java.io.File;  // Import the File class
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

public class resec extends StarMod {

    public static boolean enabled = true;
    public static boolean show_ingame = false;
    public static int ticks = 100;

    public char toggle_key = '`';

    public static void saveCoordinateData(String save_path, String data) {
        File coords = new File(save_path);
        if (!coords.exists()) {
            ModPlayground.broadcastMessage("Coordinate file did not exist.");
            try {
                coords.createNewFile();
                ModPlayground.broadcastMessage("Created coordinate file.");
            } catch (IOException e) {
                System.err.println("[resec] FAILED TO WRITE COORDINATE FILE IDFK.");
            }
        }
        try {
            Files.write(Paths.get(save_path), data.getBytes(), StandardOpenOption.APPEND);
            System.err.println("[resec] Saved output data...");
        }
        catch (IOException e){
            System.err.println("[resec] Could not save coords to file");
            ModPlayground.broadcastMessage("[resec] Coordinate file not available!");
        }
    }

    @Override
    public void onEnable() {
        System.err.println("[resec] enabled!!");

        // Handle toggling with the keyboard.
        StarLoader.registerListener(KeyPressEvent.class, this, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent event) {
                // Ignore everything if the user had a GUI open.
                TextAreaInput activeField = GameClientState.instance.getController().getInputController().getCurrentActiveField();
                boolean hasGuisActive = activeField != null;
                if(hasGuisActive){
                    return;
                }

                if (event.getChar() == toggle_key){
                    show_ingame = !show_ingame;
                    if (show_ingame) {
                        ModPlayground.broadcastMessage("[resec] Coordinate display ingame ENABLED!");
                    }
                    else {
                        ModPlayground.broadcastMessage("[resec] Coordinate display ingame DISABLED!");
                    }
                }
            }
        });

        // Handle retrieving coordinate information.
        new StarRunnable(){
            @Override
            public void run(){
                Collection<PlayerState> players = GameClient.getConnectedPlayers();
                for (PlayerState player : players){

                    String string = player.getName();
                    Vector3i coordinates = player.getCurrentSector();
                    int x = coordinates.x;
                    int y = coordinates.y;
                    int z = coordinates.z;

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    String textfile = System.getProperty("user.home") + File.separator + "coords.txt";
                    String dataline = timeStamp + "  " + string + ": " + x + " " + y+ " " + z;
                    String saveline = "\n" + dataline;

                    if (enabled) {
                        saveCoordinateData(textfile, saveline);
                    }
                    if (show_ingame) {
                        ModPlayground.broadcastMessage(dataline);
                    }
                }
            }
        }.runTimer(this, ticks);
    }
}
