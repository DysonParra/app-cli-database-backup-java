/*
 * @fileoverview    {TrayIconSetter}
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementation done.
 * @version 2.0     Documentation added.
 */
package com.project.dev.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.MAXIMIZED_BOTH;
import static java.awt.Frame.NORMAL;

/**
 * TODO: Description of {@code TrayIconSetter}.
 *
 * @author Dyson Parra
 * @since Java 17 (LTS), Gradle 7.3
 */
public class TrayIconSetter {

    /**
     * FIXME: Description of method {@code setTrayIconToFrame}. Agrega un TrayIcon a un JFrame.
     *
     * @param frame es el JFrame al que se le agregará un TrayIcon.
     * @return Si fue posible asignar un TrayIcon.
     */
    public static boolean setTrayIconToFrame(final JFrame frame) {
        if (SystemTray.isSupported()) {
            PopupMenu trayPopupMenu = new PopupMenu();
            final SystemTray systemTray = SystemTray.getSystemTray();
            Image icon = frame.getIconImage();
            String title = frame.getTitle();
            final TrayIcon trayIcon = new TrayIcon(icon, title, trayPopupMenu);
            trayIcon.setImageAutoSize(true);

            MenuItem open = new MenuItem("abrir");
            open.addActionListener((ActionEvent e) -> {
                frame.setVisible(true);
                frame.setExtendedState(JFrame.NORMAL);
            });
            trayPopupMenu.add(open);

            MenuItem close = new MenuItem("cerrar");
            close.addActionListener((ActionEvent e) -> {
                systemTray.remove(trayIcon);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            });
            trayPopupMenu.add(close);

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        frame.setVisible(true);
                        frame.setExtendedState(JFrame.NORMAL);
                    }
                }
            });

            frame.addWindowStateListener((WindowEvent e) -> {
                if (e.getNewState() == ICONIFIED || e.getNewState() == 7) {
                    try {
                        systemTray.add(trayIcon);
                        frame.setVisible(false);
                    } catch (AWTException ignored) {
                    }
                } else if (e.getNewState() == MAXIMIZED_BOTH || e.getNewState() == NORMAL) {
                    systemTray.remove(trayIcon);
                    frame.setVisible(true);
                }
            });

            return true;
        } else {
            System.out.println("Unsoported");
            return false;
        }
    }
}
