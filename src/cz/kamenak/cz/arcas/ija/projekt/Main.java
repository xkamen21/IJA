package cz.kamenak.cz.arcas.ija.projekt;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Hlavní třída programu - Zajišťuje vytvoření okna, sceny a načtení dat ze soubru.
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public class Main extends Application {
    
    private JSONObject JSON;
    private final HashMap<String, Street> streets = new HashMap<>();
    private final HashMap<String, Stop> stops = new HashMap<>();
    private final List<Drawable> elements = new ArrayList<>();
    private final List<Link> paths = new ArrayList<>();

    /**
     * Main pro načtení hodnot z JSON souboru.
     */
    public Main(){
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otevřete soubor s JSON daty");
        
        ExtensionFilter filter = new ExtensionFilter("JSON Data", "*.json", "*.txt");
        
        fileChooser.getExtensionFilters().add(filter);
        
        File file = fileChooser.showOpenDialog(null);
        if(file == null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Chyba!");
            alert.setHeaderText("Soubor nenačten!");
            alert.setContentText("Pro správnou funkci aplikace je třeba otevřít soubor s daty ve formátu JSON.");

            alert.showAndWait();
            System.exit(1);
        }
        
        try {
            loadJSON(file);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        loadStops();
        loadStreets();
        loadLinks();
    }

    /**
     * Metoda pro spuštění aplikace.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        //nacteni souboru FXML do promenne MapLoader
        FXMLLoader MapLoader = new FXMLLoader(getClass().getResource("/layout.fxml"));

        //nacteni root elementu "BorderPane"
        BorderPane root = MapLoader.load();

        //nacteni sceny pro visualizaci vseho co se nachazi v BorderPane
        Scene scene = new Scene(root);

        //nastaveni sceny jako primarni stage
        primaryStage.setScene(scene);

        //zobrazeni sceny
        primaryStage.show();


        MainController controller = MapLoader.getController();


        controller.initData(streets, paths);
        controller.setElements(elements);
        controller.startTime(1);

    }

    /**
     * Metoda pro načtení JSON souboru.
     * @param file JSON soubor
     * @throws IOException
     */
    private void loadJSON(File file) throws IOException{
        byte[] encoded = Files.readAllBytes(file.toPath());
        JSON = new JSONObject(new String(encoded, Charset.forName("UTF-8")));
    }

    /**
     * Metoda pro načtení zastávek ze souboru.
     */
    private void loadStops(){
        JSONArray stopsJSON= JSON.getJSONArray("stops");
        for(int i = 0; i < stopsJSON.length(); i++){
            JSONObject stopJSON = stopsJSON.getJSONObject(i);
            Coordinate coord = new Coordinate(stopJSON.getJSONObject("loc"));
            String name = stopJSON.getString("name");
            Stop stop = new Stop(coord, name);
            stops.put(name, stop);
            elements.add(stop);
        }
    }

    /**
     * Metoda pro načtení ulic ze souboru.
     */
    private void loadStreets(){
        JSONArray streetsJSON = JSON.getJSONArray("streets");
        
        for(int i = 0; i < streetsJSON.length(); i++){
            JSONObject streetJSON = streetsJSON.getJSONObject(i);
            String name = streetJSON.getString("name");
            Coordinate start = new Coordinate(streetJSON.getJSONObject("start"));
            Coordinate end = new Coordinate(streetJSON.getJSONObject("end"));

            
                        
            Street street = new Street(name, start, end, new ArrayList<>(stops.values()));
            
            Street put = streets.put(name, street);
            
            elements.add(street);
            
        }
    }

    /**
     * Metoda pro načtení linek ze souboru.
     */
    private void loadLinks(){
        JSONArray linksJSON = JSON.getJSONArray("links");
        for(int i = 0; i < linksJSON.length(); i++){
            JSONObject link = linksJSON.getJSONObject(i);
            Coordinate loc = new Coordinate(link.getJSONObject("loc"));
            int speed = link.getInt("speed");
            int interval = link.getInt("interval");
            boolean roundabout = link.getBoolean("roundabout");
            String name = link.getString("name");
            
            JSONArray streetListJSON = link.getJSONArray("streetList");
            List<Street> streetList = new ArrayList<>();
            
            for(int j = 0; j < streetListJSON.length(); j++){
                Street street = streets.get(streetListJSON.getString(j));
                if(street == null){
                    System.err.println("Ulice s názvem \"" + streetListJSON.getString(j) + "\" neexistuje!");
                    continue;
                }
                streetList.add(street);
            }
            Link path = new Link(loc, streetList, speed, interval, name, roundabout);
            paths.add(path);
            
        }
    }
}
