import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class levelTracker {

    int maxLevel;
    int lastPlayed;

    public levelTracker(){
        try{
            loadCurr();
        }catch(IOException | ParseException e){
            System.out.println(e);
        }
    }

    /* on incremente currentLevel */
    public void incrementeMax(){
        try {
            maxLevel++;
            updateCurr();
        }catch(IOException | ParseException e){
            System.out.println(e);
        }
    }

    public void setLastPlayed(int i){
        try{
            lastPlayed = i;
            updateCurr();
        }catch(IOException | ParseException e){
            System.out.println(e);
        }
    }

    /* on retourne le dernier niveau joué */
    public int getLastPlayed(){
        return lastPlayed;
    }

    /* on retourne le niveau max débloqué */
    public int getMaxLevel(){
        return maxLevel;
    }

    /* on charge le niveau courant dans les fichiers */
    private void loadCurr() throws IOException, ParseException {
        /* On charge le fichier qui track les niveaux */
        FileReader f = new FileReader("levels/curr.json");

        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(f);

        maxLevel = Math.toIntExact((long) obj.get("maxLevel"));
        lastPlayed = Math.toIntExact((long) obj.get("lastPlayed"));
    }

    /* on met a jour le niveau dans les fichiers */
    private void updateCurr() throws IOException, ParseException {
        FileReader f = new FileReader("levels/curr.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(f);

        obj.put("maxLevel",maxLevel);
        obj.put("lastPlayed",lastPlayed);

        FileWriter r = new FileWriter("levels/curr.json");
        r.write(obj.toJSONString());
        r.close();

    }
}
