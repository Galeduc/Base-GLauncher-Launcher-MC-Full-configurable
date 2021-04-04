//Copyright GLauncher By Gaëtan, MOJANG AB, FORGE, LITARVAN For LIBS

package fr.galeduc.galeduclauncher;

import java.io.File;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.internal.InternalLaunchProfile;
import fr.theshark34.openlauncherlib.internal.InternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

@SuppressWarnings("deprecation")
public class Launcher {
// Régler la version du jeu
	public static final GameVersion HB_VERSION = new GameVersion("1.8", GameType.V1_8_HIGHER);
	public static final GameInfos HB_INFOS = new GameInfos("GLauncher", HB_VERSION, new GameTweak[] {GameTweak.FORGE});
	public static final File HB_DIR = HB_INFOS.getGameDir();
	public static final File HB_CRASHES_DIR = new File(HB_DIR, "crashes");
	private static AuthInfos authInfos;
	private static Thread updateThread;
	
	// Version Premium
	public static void auth(String username, String password) throws AuthenticationException {
		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password,"");
		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
		
	}
	
	// Versions CRACK
	
	  //public static void auth(String username, String password) throws AuthenticationException {
		    //Authenticator authenticator = new Authenticator("https://authserver.mojang.com/", AuthPoints.NORMAL_AUTH_POINTS);
		   //AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, UUID.randomUUID().toString());
	//authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
	//}
	
	public static void update() throws Exception {
		SUpdate su = new SUpdate("", HB_DIR);
		su.addApplication(new FileDeleter());
		
		updateThread = new Thread() {
			private int val;
			private int max;
			
			@Override
			
// Message de Vérification			
			public void run() {
				while(!this.isInterrupted()) {
					if(BarAPI.getNumberOfFileToDownload() == 0) {
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verification des fichiers");
					continue;
					}
					
					val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
					max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);
					
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);
					
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Telechargement des fichiers " +
					BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +
					Swinger.percentage(val, max) + "%");
					
					//try {
					//   ZipFile zipFile = new ZipFile(System.getProperty("user.home")+"/AppData/Roaming/.GLauncher/natives.zip");
					//   zipFile.extractAll(System.getProperty("user.home")+"/AppData/Roaming/.GLauncher/natives");
					//} catch (ZipException e) {  
					//	   e.printStackTrace();  
					//}
					}
			}
		};
		
		updateThread.start();		
		su.start();
		updateThread.interrupt();
		
	}

	// Lancement du jeu + Reach Presence Discord
	public static void launch() throws LaunchException 
	{
		InternalLaunchProfile profile = MinecraftLauncher.createInternalProfile(HB_INFOS, GameFolder.BASIC, authInfos);
		InternalLauncher launcher = new InternalLauncher(profile);
		LauncherFrame.getInstance().setVisible(false);
        //Discord RPC add
		DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence1 = new DiscordRichPresence();
        //Start of RPC details
        presence1.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence1.details = "";
        presence1.largeImageKey = "favicon";
        presence1.smallImageKey = "minecraft";
        presence1.smallImageText = "En jeu...";
        //End of RPC details
        lib.Discord_UpdatePresence(presence1);
        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
		launcher.launch();
			
		System.exit(0);
	}
	
	public static void interruptThread() {
		updateThread.interrupt();
		
		
		}
}

//Copyright GLauncher By Gaëtan, MOJANG AB, FORGE, LITARVAN For LIBS