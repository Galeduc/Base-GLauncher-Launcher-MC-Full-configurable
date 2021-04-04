//Copyright GLauncher By Gaëtan, MOJANG AB, FORGE, LITARVAN For LIBS

package fr.galeduc.galeduclauncher;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;


import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.theshark34.openlauncherlib.LanguageManager;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.util.WindowMover;

@SuppressWarnings("serial")
public class LauncherFrame extends JFrame {
	
	private static LauncherFrame instance;
	private LauncherPanel launcherPanel;
	private static CrashReporter crashReporter;
	public static Thread splashT;
	  
	private static SplashScreen splash;

	
	public LauncherFrame() {
		this.setTitle("GLauncher");
		this.setSize(983, 577);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setIconImage(Swinger.getResource("favicon.png"));
		this.setContentPane(launcherPanel = new LauncherPanel());
		WindowMover mover = new WindowMover(this);
	    addMouseListener((MouseListener)mover);
	    addMouseMotionListener((MouseMotionListener)mover);
	    //Logo début
	    splashT = new Thread(() -> {
	          splash = new SplashScreen("GLauncher", Swinger.getResource("screenn.png"));
	          splash.setIconImage(Swinger.getResource("favicon.png"));
	          splash.setSize(332, 332);
	          splash.setLocationRelativeTo(null);
	          splash.setVisible(true);
	          try {
	            Thread.sleep(2000L);
	          } catch (InterruptedException e) {
	            e.printStackTrace();
	          } 
	          splash.setVisible(false);
	          try {
	            Thread.sleep(1000L);
	          } catch (InterruptedException e) {
	            e.printStackTrace();
	          } 
	          setVisible(true);
	        });
	    splashT.start();
	    Animator.fadeInFrame(this, Animator.FAST);
	  }
		

	public static void main(String[] args) {
		LanguageManager.setLang(LanguageManager.FRENCH);
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/galeduc/galeduclauncher/resources/");
		Launcher.HB_CRASHES_DIR.mkdirs();
		crashReporter = new CrashReporter("GLauncher", Launcher.HB_CRASHES_DIR);
		instance = new LauncherFrame();
		//Discord RPC add
		DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        //Start of RPC details
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Dans le launcher...";
        presence.smallImageKey = "bootstrap";
        presence.largeImageKey = "favicon";
        presence.smallImageText = "Dans le launcher...";
        //End of RPC details
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
	}

	public static  LauncherFrame getInstance() {
		return  instance;
		
	}
	public static CrashReporter getCrashReporter() {
		return  crashReporter;
		
	}
	public LauncherPanel getLauncherPanel() {
		return this.launcherPanel;
	}
	
}
//Copyright GLauncher By Gaëtan, MOJANG AB, FORGE, LITARVAN For LIBS