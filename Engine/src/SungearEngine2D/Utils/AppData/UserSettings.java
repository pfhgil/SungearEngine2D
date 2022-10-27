package SungearEngine2D.Utils.AppData;

import Core2D.Log.Log;

import java.io.*;
import java.util.List;

public class UserSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    public static String fileName = "UserSettings.dat";
    public static UserSettings getUserSettings(){
        var usFile = new File(AppDataManager.getRoamingDirectory().getAbsolutePath() + File.separator + fileName);
        if (usFile.exists()){
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("person.dat")))
            {
                 instance=(UserSettings) ois.readObject();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        } else{
            instance = new UserSettings();
        }
        return instance;
    }
    public void save(){
        var usFile = new File(AppDataManager.createRoamingDirectory() + File.separator + fileName);
        usFile.delete();
        try{
            usFile.createNewFile();
        } catch (IOException e){}

        try {
            FileOutputStream outputStream = new FileOutputStream(usFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
        } catch (IOException e){
            Log.CurrentSession.println("Can't save settings in \"" + usFile.getAbsolutePath() + "\" with error:\n" + e.toString(), Log.MessageType.ERROR);
        }

    }
    UserSettings(){
        if (instance!=null)
            throw new RuntimeException("multipleInstanceError: UserSettings already exist");
        instance = this;
    }
    public transient static UserSettings instance;

    public List<String> getLastProjects() { return lastProjects; }
    public void setLastProjects(List<String> lastProjects) { this.lastProjects = lastProjects; }
    public List<String> lastProjects;
}
