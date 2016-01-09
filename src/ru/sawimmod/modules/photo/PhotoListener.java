package ru.sawimmod.modules.photo;


import ru.sawimmod.activities.BaseActivity;

public interface PhotoListener {
    void processPhoto(BaseActivity activity, byte[] photo);
}


