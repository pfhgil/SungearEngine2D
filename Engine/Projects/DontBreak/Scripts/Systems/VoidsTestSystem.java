package sungear.project.test12.Scripts.Systems;

import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Scripting.*;
import Core2D.Log.*;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class VoidsTestSystem extends System
{
    @Override
    public void update()
    {
        //Log.CurrentSession.println("df", Log.MessageType.SUCCESS);
    }
    
    @Override
    public void deltaUpdate(float deltaTime)
    {
        
    }

    @Override
    public void collider2DEnter(Entity otherObj)
    {
        Log.CurrentSession.println("name: " + otherObj.name, Log.MessageType.SUCCESS);
    }

    @Override
    public void collider2DExit(Entity otherObj)
    {
        
    }
}