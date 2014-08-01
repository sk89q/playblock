package com.skcraft.playblock.installer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.sk89q.task.ProgressListener;
import com.sk89q.task.SwingProgressListener;
import com.sk89q.task.Task;
import com.sk89q.task.TaskException;
import com.skcraft.playblock.installer.tasks.Install;
import com.skcraft.playblock.installer.tasks.Uninstall;
import com.skcraft.playblock.util.EnvUtils;
import com.skcraft.playblock.util.EnvUtils.Arch;

public class PlayBlockSetup extends JFrame implements ProgressListener {

    private JPanel selectionPanel;
    private JPanel taskPanel;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    private Task currentTask;
    private boolean noQuit = false;

    public PlayBlockSetup() {
        setTitle("PlayBlock Installer");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponents();
        pack();
        setLocationRelativeTo(null);

        try {
            InputStream in = PlayBlockSetup.class.getResourceAsStream("/installer/installer_icon.png");
            if (in != null) {
                setIconImage(ImageIO.read(in));
            }
        } catch (IOException e) {
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (currentTask != null) {
                    tryCancelling();
                } else {
                    quit();
                }
            }
        });
    }

    private void quit() {
        if (noQuit) {
            dispose();
        } else {
            System.exit(0);
        }
    }

    private void addComponents() {
        final PlayBlockSetup parent = this;

        try {
            InputStream is = PlayBlockSetup.class.getResourceAsStream("/installer/installer_bg.png");

            if (is != null) {
                BufferedImage banner = ImageIO.read(is);
                JLabel picLabel = new JLabel(new ImageIcon(banner));
                add(picLabel, BorderLayout.NORTH);
            }
        } catch (IOException e) {
        }

        JButton button;

        selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(4, 1, 0, 5));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        add(selectionPanel, BorderLayout.SOUTH);

        selectionPanel.add(new JLabel("Welcome to the PlayBlock installer! Please select a choice:", SwingConstants.LEFT));

        // Buttons
        JPanel selectionButtons = new JPanel();
        selectionButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        selectionPanel.add(selectionButtons);

        selectionButtons.add(button = new JButton("Install 32-bit"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                execute(new Install(Arch.X86));
            }
        });

        selectionButtons.add(Box.createHorizontalStrut(6));
        selectionButtons.add(button = new JButton("Install 64-bit"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                execute(new Install(Arch.X86_64));
            }
        });

        selectionButtons.add(Box.createHorizontalStrut(6));
        selectionButtons.add(button = new JButton("Uninstall..."));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (SetupUtils.yesNo(parent, "Are you sure that you wish to delete all the support files?", "Confirmation")) {
                    execute(new Uninstall());
                }
            }
        });

        selectionPanel.add(Box.createVerticalStrut(5));

        // Links
        JPanel links = new JPanel();
        links.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        selectionPanel.add(links);

        links.add(button = new LinkButton("Help!"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                SetupUtils.openUrl("http://skq.me/playblock-help");
            }
        });

        links.add(Box.createHorizontalStrut(10));
        links.add(button = new LinkButton("Source Code"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                SetupUtils.openUrl("http://skq.me/playblock-source");
            }
        });

        links.add(Box.createHorizontalStrut(10));
        links.add(button = new LinkButton("About"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                SetupUtils.openUrl("http://skq.me/playblock-about");
            }
        });

        // -------

        taskPanel = new JPanel();
        taskPanel.setLayout(new GridLayout(4, 1, 0, 5));
        taskPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        taskPanel.add(progressBar = new JProgressBar(0, 1000));
        taskPanel.add(statusLabel = new JLabel("Initializing..."));

        taskPanel.add(Box.createVerticalStrut(5));

        JPanel taskButtons = new JPanel();
        taskButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        taskPanel.add(taskButtons);

        taskButtons.add(button = new JButton("Cancel"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                tryCancelling();
            }
        });
    }

    private void cancel() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    private void tryCancelling() {
        if (currentTask != null) {
            if (SetupUtils.yesNo(this, "Are you sure that you want to cancel?", "Confirmation")) {
                cancel();
                dispose();
            }
        } else {
            quit();
        }
    }

    private void execute(Task task) {
        remove(selectionPanel);
        add(taskPanel, BorderLayout.SOUTH);
        pack();

        cancel();

        currentTask = task;
        task.addProgressListener(new SwingProgressListener(this));
        task.start();
    }

    @Override
    public void progressChange(double progress) {
        if (progress < 0) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setIndeterminate(false);
            progressBar.setValue((int) (progress * 1000));
        }
    }

    @Override
    public void statusChange(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void complete() {
        if (currentTask instanceof Install) {
            SetupUtils.showMessageDialog(this, "Installation completed successfully. " + "If you still does not work, try installing in-game.", "Complete", JOptionPane.INFORMATION_MESSAGE);
        } else {
            SetupUtils.showMessageDialog(this, "Completed successfully! This setup tool will now close.", "Complete", JOptionPane.INFORMATION_MESSAGE);
        }

        quit();
    }

    @Override
    public void aborted() {
    }

    @Override
    public void error(Throwable exception) {
        if (exception instanceof TaskException) {
            SetupUtils.showMessageDialog(this, exception.getMessage(), exception.getCause(), "An error has occurred", JOptionPane.ERROR_MESSAGE);
        } else {
            SetupUtils.showMessageDialog(this, exception.getMessage(), exception, "An error has occurred", JOptionPane.ERROR_MESSAGE);
        }

        quit();
    }

    public static PlayBlockSetup startEmbedded(ProgressListener listener) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        PlayBlockSetup frame = new PlayBlockSetup();
        frame.noQuit = true;
        frame._setAutoRequestFocus(false);
        frame.setVisible(true);
        Task task = new Install(EnvUtils.getJvmArch());
        task.addProgressListener(listener);
        frame.execute(task);
        return frame;
    }

    /**
     * Set the auto-request focus property, if it's available.
     * 
     * @param autoRequestFocus
     *            true to auto request focus
     */
    private void _setAutoRequestFocus(Boolean autoRequestFocus) {
        try {
            // This is Java 7+ only!
            Method method = Window.class.getMethod("setAutoRequestFocus", boolean.class);
            method.invoke(this, autoRequestFocus);
        } catch (Throwable t) {
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        PlayBlockSetup frame = new PlayBlockSetup();
        frame.setVisible(true);
    }

}
