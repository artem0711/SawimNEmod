


package ru.sawimmod.io;

import ru.sawimmod.SawimException;
import ru.sawimmod.activities.BaseActivity;

import java.io.InputStream;


public interface FileBrowserListener {
    public void onFileSelect(BaseActivity activity, InputStream in, String fileName) throws SawimException;
}


