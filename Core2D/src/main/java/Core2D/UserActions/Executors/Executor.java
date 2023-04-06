package Core2D.UserActions.Executors;

import Core2D.Common.Interfaces.Unregistered;
import Core2D.ECS.Entity;
import Core2D.Scene2D.SceneManager;
import Core2D.UserActions.Commands.Command;

import java.util.ArrayList;
import java.util.List;

public class Executor
{
    public static int maxCommandsNum = 32;

    private static List<Command> commands = new ArrayList<>();
    // pointer on current command
    private static Command lastExecutedCommand;

    public static Object bufferedObject;

    public static boolean active = true;

    @SafeVarargs
    public static <T> void executeCommand(Command command, T... params)
    {
        if(!active) return;

        if(commands.size() >= maxCommandsNum) {
            Command firstCommand = commands.get(0);
            firstCommand.free();
            commands.remove(firstCommand);
        }

        if(!(command instanceof Unregistered)) {
            lastExecutedCommand = command;
            commands.add(command);
        }

        command.execute((Object[]) params);
    }

    public static void restoreLastCommand()
    {
        if(!active) return;

        if (lastExecutedCommand != null) {
            lastExecutedCommand.restore();
        }

        int idx = commands.indexOf(lastExecutedCommand) + 1;

        if(commands.size() > 0 && idx < commands.size()) {
            lastExecutedCommand = commands.get(idx);
        }
    }

    public static void revertLastCommand()
    {
        if(!active) return;

        if (lastExecutedCommand != null) {
            lastExecutedCommand.revert();
        }

        int idx = commands.indexOf(lastExecutedCommand) - 1;

        if(commands.size() > 0 && idx >= 0 && idx < commands.size()) {
            lastExecutedCommand = commands.get(idx);
        }
    }

    public static void reloadReferences()
    {
        for(Command command : commands) {
            for(int i = 0; i < command.executedObjects.size(); i++) {
                Object obj = command.executedObjects.get(i);

                if(obj instanceof Entity entity &&
                        SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                    command.executedObjects.set(i, SceneManager.currentSceneManager.getCurrentScene2D().findEntityByID(entity.ID));
                }
            }
        }
    }

    public static void clearExecutedCommands()
    {
        commands.removeIf(Command::isExecuted);
    }

    public static List<Command> getCommands() { return commands; }

    public static Command getLastExecutedCommand() { return lastExecutedCommand; }
}
