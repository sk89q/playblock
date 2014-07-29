package com.skcraft.playblock.installer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class SetupUtils {

    private SetupUtils() {
    }

    public static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Throwable e) {
            showMessageDialog(null, "Failed to open " + url, e, "Error opening URL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean isParent(File testParent, File child) {
        File parent = child.getParentFile();
        while (parent != null) {
            if (parent.equals(testParent))
                return true;
            parent = parent.getParentFile();
        }
        return false;
    }

    public static void showMessageDialog(Component parentComponent, String message, String title, int messageType) {
        showMessageDialog(parentComponent, message, (String) null, title, messageType);
    }

    public static void showMessageDialog(Component parentComponent, String message, String log, String title, int messageType) {

        JLabel label = new JLabel(message);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.add(label, BorderLayout.NORTH);

        if (log != null) {
            JTextArea textArea = new JTextArea(log);
            textArea.setEditable(false);
            textArea.setFont(label.getFont());
            textArea.setTabSize(4);

            JScrollPane scrollPane = new JScrollPane(textArea) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(400, 150);
                }
            };
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        JOptionPane.showMessageDialog(parentComponent, panel, title, messageType);
    }

    public static void showMessageDialog(Component parentComponent, String message, Throwable t, String title, int messageType) {

        StringWriter writer = new StringWriter();
        writer.write("To report this error, please provide:\n\n");
        t.printStackTrace(new PrintWriter(writer));

        showMessageDialog(parentComponent, message, writer.toString(), title, messageType);
    }

    public static boolean yesNo(Component parent, String message, String title) {
        return 0 == JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
    }

}
