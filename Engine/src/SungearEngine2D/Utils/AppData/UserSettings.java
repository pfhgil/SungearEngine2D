package SungearEngine2D.Utils.AppData;

import Core2D.Log.Log;
import Core2D.Utils.FileUtils;

import java.io.*;
import java.util.List;

public class UserSettings implements Serializable {

    public List<String> lastProjects;

    public List<String> getLastProjects() { return lastProjects; }
    public void setLastProjects(List<String> lastProjects) { this.lastProjects = lastProjects; }


    private static final long serialVersionUID = 1L;
    public static String fileName = "UserSettings.dat";
    public transient static UserSettings instance;

    public static UserSettings getUserSettings(){ // Получает текущие настройки
        var usFile = new File(AppDataManager.getRoamingDirectory().getAbsolutePath() + File.separator + fileName);
        if (usFile.exists()){
            instance = (UserSettings) FileUtils.deSerializeObject(usFile);
        } else{
            instance = new UserSettings();
        }
        return instance;
    }

    public void save(){ //Сохраняет текущие настройки
        var usFile = new File(AppDataManager.createRoamingDirectory() + File.separator + fileName);
        usFile.delete();
        try{
            usFile.createNewFile();
        } catch (IOException e){}

        FileUtils.serializeObject(usFile, this);
    }

    UserSettings(){
        if (instance!=null)
            throw new RuntimeException("multipleInstanceError: UserSettings already exist");
        instance = this;
    }

}
