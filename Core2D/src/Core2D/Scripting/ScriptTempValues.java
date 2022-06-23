package Core2D.Scripting;

import java.util.ArrayList;
import java.util.List;

// обертка под все значения в скрпте script
public class ScriptTempValues
{
    private transient Script script;
    private List<ScriptTempValue> scriptTempValues = new ArrayList<>();

    public void applyToScript() {
        for(ScriptTempValue scriptTempValue : scriptTempValues) {
            scriptTempValue.applyToScript();
        }
    }

    public void destroy()
    {
        script = null;
        for(int i = 0; i < scriptTempValues.size(); i++) {
            scriptTempValues.get(i).destroy();
        }
        scriptTempValues.clear();
    }

    public Script getScript() { return script; }
    public void setScript(Script script)
    {
        this.script = script;
        for(ScriptTempValue scriptTempValue : scriptTempValues) {
            scriptTempValue.setScript(script);
        }
        script = null;
    }

    public List<ScriptTempValue> getScriptTempValues() { return scriptTempValues; }
}
