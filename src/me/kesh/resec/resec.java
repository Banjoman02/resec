package me.kesh.resec;

import api.ModPlayground;
import api.common.GameClient;
import api.mod.StarMod;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;

import java.io.IOException;
import java.io.File;  // Import the File class
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

public class resec extends StarMod {

    public static int ticks = 1000;
    public static boolean enabled = true;

    @Override
    public void onEnable() {
        System.err.println("[resec] enabled!!");

        new StarRunnable(){
            @Override
            public void run(){
                Collection<PlayerState> players = GameClient.getConnectedPlayers();
                for (PlayerState player : players){

                    String string = player.getName();
                    // Get the coordinates
                    Vector3i coordinates = player.getCurrentSector();
                    int x = coordinates.x;
                    int y = coordinates.y;
                    int z = coordinates.z;

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                    File coords = new File(System.getProperty("user.home") + File.separator + "/coords.txt");
                    String saveline = "\n" + timeStamp + "  " + string + ": " + x + " " + y+ " " + z;

                    if (enabled) {
                        if (!coords.exists()) {
                            try {
                                coords.createNewFile();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        //ModPlayground.broadcastMessage(saveline);
                        String textfile = System.getProperty("user.home") + File.separator + "/coords.txt";
                        try {
                            Files.write(Paths.get(textfile), saveline.getBytes(), StandardOpenOption.APPEND);
                        }
                        catch (IOException e){
                            System.err.println("[resec] Could not save coords to file");
                            ModPlayground.broadcastMessage("[resec] Coordinate file not available!");
                        }
                    }
                }
            }
        }.runTimer(this, ticks);
    }

    public void createOutputFileAndSave(String file_path, String data) {
        // Creates the output file that the coordinates are stored in.
        boolean created = false;
        System.err.println("[resec] Created new coordinate file");
        File save_file = new File(file_path);
        try {
            created = save_file.createNewFile();
            Files.write(Paths.get(file_path), data.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            ModPlayground.broadcastMessage("[resec] Coordinate save file creation failed!");
        }
    }
}
