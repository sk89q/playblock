package com.skcraft.playblock.installer.tasks;

import java.io.File;

import com.sk89q.task.Task;
import com.skcraft.playblock.installer.SetupUtils;

public class DirectoryDelete extends Task {

    private final File dir;

    public DirectoryDelete(File dir) {
        this.dir = dir;
    }

    @Override
    public void execute() {
        fireProgressChange(-1);
        fireStatusChange("Removing " + dir.getAbsolutePath() + "...");
        delete(dir, dir);
    }

    public boolean delete(File originalParent, File dir) {
        fireStatusChange("Removing " + dir.getAbsolutePath() + "...");

        if (dir.exists()) {
            File[] files = dir.listFiles();

            if (null != files) {
                for (File object : files) {
                    if (object.isDirectory()) {
                        delete(originalParent, object);
                    } else {
                        if (SetupUtils.isParent(originalParent, object)) {
                            fireStatusChange("Removing " + object.getAbsolutePath() + "...");

                            object.delete();
                        }
                    }
                }
            }
        }

        return dir.delete();
    }

}
