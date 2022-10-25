package Core2D.Systems;

import Core2D.Scripting.ScriptTempValues;

import java.util.ArrayList;
import java.util.List;

public class ScriptSystem
{
    private List<ScriptTempValues> scriptTempValuesList = new ArrayList<>();

    public transient boolean runScripts = true;

    public void applyTempValues()
    {
        for(ScriptTempValues scriptTempValues : scriptTempValuesList) {
            scriptTempValues.applyToScript();
        }
    }

    public List<ScriptTempValues> getScriptTempValuesList() { return scriptTempValuesList; }
}
