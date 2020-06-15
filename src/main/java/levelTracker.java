import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class levelTracker {

    int currentLevel;

    public levelTracker(){
        try{
            loadCurr();
        }catch(IOException | ParseException e){
            System.out.println(e);
        }
    }

    /* on incremente currentLevel */
    public void incrementeCurrent(){
        try {
            currentLevel++;
            updateCurr();
        }catch(IOException | ParseException e){
            System.out.println(e);
        }
    }

    /* on retourne le niveau actuel */
    public int getCurrent(){
        return currentLevel;
    }

    /* on charge le niveau courant dans les fichiers */
    private void loadCurr() throws IOException, ParseException {
        /* On charge le fichier qui track les niveaux */
        FileReader f = new FileReader("levels/curr.json");

        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(f);

        currentLevel = Math.toIntExact((long) obj.get("current"));
    }

    /* on met a jour le niveau dans les fichiers */
    private void updateCurr() throws IOException, ParseException {
        FileReader f = new FileReader("levels/curr.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(f);

        obj.put("current",currentLevel);

        FileWriter r = new FileWriter("levels/curr.json");
        r.write(obj.toJSONString());
        r.close();

    }
}
