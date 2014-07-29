package com.skcraft.playblock.installer.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.sk89q.task.Task;
import com.sk89q.task.TaskException;
import com.skcraft.playblock.util.EnvUtils;
import com.skcraft.playblock.util.EnvUtils.Arch;
import com.skcraft.playblock.util.PlayBlockPaths;

/**
 * Install support files.
 */
public class Install extends Task {

    private static final String URL_WIN64 = "http://update.sk89q.com/playblock/supportlibs-win64.zip";
    private static final String URL_WIN32 = "http://update.sk89q.com/playblock/supportlibs-win32.zip";
    private static final String URL_MAC64 = "http://update.sk89q.com/playblock/supportlibs-macosx64.zip";
    private static final String URL_MAC32 = "http://update.sk89q.com/playblock/supportlibs-macosx32.zip";

    private final Arch arch;

    public Install(Arch arch) {
        this.arch = arch;
    }

    @Override
    protected void execute() throws Exception {
        String url;

        switch (EnvUtils.getPlatform()) {
        case WINDOWS:
            if (arch == Arch.X86_64) {
                url = URL_WIN64;
            } else {
                url = URL_WIN32;
            }
            break;

        case MAC_OS_X:
            if (arch == Arch.X86_64) {
                url = URL_MAC64;
            } else {
                url = URL_MAC32;
            }
            break;

        case LINUX:
            throw new TaskException("<html>Sorry, please install the appropriate " + "version (32-bit or 64-bit) of VLC for your system using " + "your package manager (apt-get, yum, etc.)");

        default:
            throw new TaskException("Sorry, your platform is not supported.");
        }

        File tempFile = File.createTempFile("playblock-libvlc-", null);
        tempFile.deleteOnExit();

        OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
        HttpDownload download = new HttpDownload(url, out);
        attach(download, 0, 0.5).execute();

        DirectoryDelete delete = new DirectoryDelete(getTargetDir());
        attach(delete, 0.5, 0.6).execute();

        Thread.sleep(200); // Fix for slower disks

        ZipExtract extract = new ZipExtract(tempFile, getTargetDir());
        attach(extract, 0.6, 1).execute();

        fireStatusChange("Installation successful!");
    }

    private File getTargetDir() {
        return PlayBlockPaths.getPlayBlockArchLibsDir(arch);
    }

}
