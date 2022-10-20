package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Log.Log;
import SungearEngine2D.GUI.Views.View;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.awt.*;
import java.util.Scanner;

public class LogView extends View
{
    private ImString log = new ImString();

    public void draw()
    {
        ImGui.begin("Log", ImGuiWindowFlags.NoMove);
        {
            log.set(Log.CurrentSession.getAllLog(), true);

            Scanner textScanner = new Scanner(log.get());
            Color col = new Color(1.0f, 1.0f, 1.0f, 1.0f);

            ImGui.pushID("LogText");
            while(textScanner.hasNextLine()) {
                String nextLine = textScanner.nextLine();
                Scanner lineScanner = new Scanner(nextLine);
                lineScanner.useDelimiter(" ");
                while(lineScanner.hasNext()) {
                    String nextWord = lineScanner.next();
                    if(nextWord.length() >= 9 && nextWord.charAt(0) == '[' && nextWord.charAt(8) == ']' && nextWord.charAt(1) == '#') {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i = 1; i < nextWord.length() - 1; i++) {
                            stringBuilder.append(nextWord.charAt(i));
                        }
                        col = Color.decode(stringBuilder.toString());
                    } else {
                        ImGui.sameLine();
                        ImGui.textColored(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha(), nextWord);
                    }
                }
                ImGui.newLine();
            }
            ImGui.popID();

            update();
        }
        ImGui.end();
    }
}
