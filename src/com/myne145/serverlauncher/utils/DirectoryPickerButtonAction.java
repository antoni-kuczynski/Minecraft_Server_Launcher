package com.myne145.serverlauncher.utils;

import java.io.File;

@FunctionalInterface
public interface DirectoryPickerButtonAction {
    void run(File fileToAdd);
}
