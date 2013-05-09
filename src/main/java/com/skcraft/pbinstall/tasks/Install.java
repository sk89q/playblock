package com.skcraft.pbinstall.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingWorker;

import com.sk89q.task.Task;
import com.sk89q.task.TaskException;
import com.skcraft.playblock.util.EnvUtils;
import com.skcraft.playblock.util.PlayBlockPaths;

/**
 * Install support files.
 */
public class Install extends Task {

    private static final String URL_WIN64 = "http://update.sk89q.com/playblock/supportlibs-win64.zip";
    private static final String URL_WIN32 = "http://update.sk89q.com/playblock/supportlibs-win32.zip";
    private static final String URL_MAC64 = "http://update.sk89q.com/playblock/supportlibs-macosx64.zip";
    private static final String URL_MAC32 = "http://update.sk89q.com/playblock/supportlibs-macosx32.zip";
    
    private final boolean for64Bit;
    
    public Install(boolean for64Bit) {
        this.for64Bit = for64Bit;
    }

    @Override
    protected void execute() throws Exception {
        String url;
        
        if (EnvUtils.isWindows()) {
            if (for64Bit) {
                url = URL_WIN64;
            } else {
                url = URL_WIN32;
            }
        } else if (EnvUtils.isMac()) {
            if (for64Bit) {
                url = URL_MAC64;
            } else {
                url = URL_MAC32;
            }
        } else if (EnvUtils.isUnix()) {
            throw new TaskException("<html>Sorry, please install the appropriate " +
            		"version (32-bit or 64-bit) of VLC for your system using " +
            		"your package manager (apt-get, yum, etc.)");
        } else {
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
        return PlayBlockPaths.getNativeLibrariesDir(for64Bit);
    }

}
