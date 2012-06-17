/*
 * IAGApp.java
 * $Id$
 */

package org.ignisf.iag;

import java.awt.Toolkit;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class IAGApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new IAGView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
        root.setIconImage(Toolkit.getDefaultToolkit().getImage(IAGApp.class.getResource("/org/ignisf/iag/resources/icon.png")));
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of IAGApp
     */
    public static IAGApp getApplication() {
        return Application.getInstance(IAGApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(IAGApp.class, args);
    }
}
