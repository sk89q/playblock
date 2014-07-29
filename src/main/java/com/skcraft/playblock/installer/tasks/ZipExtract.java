package com.skcraft.playblock.installer.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.sk89q.task.Task;
import com.skcraft.playblock.util.IOUtils;

/**
 * Extracts a .zip file's contents to a directory.
 * 
 * <p>
 * Zip files may traverse outside their target directory.
 * </p>
 */
public class ZipExtract extends Task {

    private final File file;
    private final File targetDir;

    /**
     * Extract a given file to a given directory.
     * 
     * @param file
     *            the file
     * @param targetDir
     *            the target directory
     */
    public ZipExtract(File file, File targetDir) {
        this.file = file;
        this.targetDir = targetDir;
    }

    @SuppressWarnings("resource")
    @Override
    protected void execute() throws InterruptedException, ZipException, IOException {
        fireProgressChange(0);
        fireStatusChange("Prepare to extract " + file.getAbsolutePath() + "...");

        targetDir.mkdirs();

        ZipFile zip = null;

        try {
            // Open ZIP file and get a list of entries
            zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();

            int index = 0;
            int numEntries = zip.size();

            while (entries.hasMoreElements()) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                index++;

                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                File destFile = new File(targetDir, entryName);
                File destFolder = destFile.getParentFile();
                destFolder.mkdirs();

                // Fire events
                fireProgressChange(index / (double) numEntries);
                fireStatusChange("Extracting " + entryName + "...");

                if (entry.isDirectory()) {
                    destFile.mkdir();
                } else {
                    BufferedInputStream in = null;
                    OutputStream out = null;

                    try {
                        in = new BufferedInputStream(zip.getInputStream(entry));
                        out = new BufferedOutputStream(new FileOutputStream(destFile));
                        StreamCopy copy = new StreamCopy(in, out);
                        copy.execute();
                    } finally {
                        IOUtils.close(in);
                        IOUtils.close(out);
                    }
                }
            }
        } finally {
            IOUtils.close(zip);
        }
    }

}
