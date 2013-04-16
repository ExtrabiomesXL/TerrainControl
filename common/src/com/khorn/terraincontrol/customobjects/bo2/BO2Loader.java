package com.khorn.terraincontrol.customobjects.bo2;

import com.khorn.terraincontrol.customobjects.CustomObject;
import com.khorn.terraincontrol.customobjects.CustomObjectLoader;

import java.io.File;

public class BO2Loader implements CustomObjectLoader
{
    public CustomObject loadFromFile(String objectName, File file)
    {
        return new BO2(file, objectName);
    }

    @Override
    public void onShutdown()
    {
        // Stub method
    }
}
