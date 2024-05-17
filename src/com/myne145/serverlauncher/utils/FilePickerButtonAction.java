package com.myne145.serverlauncher.utils;

import java.io.File;

@FunctionalInterface
public interface FilePickerButtonAction {
    void run(File fileToAdd);
}
