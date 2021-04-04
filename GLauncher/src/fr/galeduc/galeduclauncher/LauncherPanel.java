//Copyright GLauncher By Gaëtan, MOJANG AB, FORGE, LITARVAN For LIBS

package fr.galeduc.galeduclauncher;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

//Nom des Images pour les afficher
@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener {
	
	private Image background  = Swinger.getResource("Background.png");
	private static RamSelector selector = new RamSelector(new File (Launcher.HB_DIR, "ram.txt"));
	private Saver saver = new Saver(new File(Launcher.HB_DIR, "launcher.properties"));
	private JTextField usernameField = new JTextField(saver.get("username"));
	private JPasswordField passwordField = new JPasswordField();	
	private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png"));
	private STexturedButton hideButton = new STexturedButton(Swinger.getResource("reduce2.png"));
	private STexturedButton quitButton = new STexturedButton(Swinger.getResource("quit2.png"));
	private STexturedButton settingsButton = new STexturedButton(Swinger.getResource("settings.png"));
	private JLabel infoLabel2 = new JLabel("", SwingConstants.CENTER);
	private STexturedButton discordButton = new 
			STexturedButton(Swinger.getResource("discord.png"));
	private STexturedButton twitterButton = new 
			STexturedButton(Swinger.getResource("twitter.png"));
	private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
	private JLabel infoLabel = new JLabel("Cliquez sur jouer !", SwingConstants.CENTER);

	//Emplacement des Boutons ect...
	public LauncherPanel() {
		this.setLayout(null);
		
		usernameField.setForeground(Color.WHITE);
		usernameField.setFont(usernameField.getFont().deriveFont(20F));
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setBorder(null);
		usernameField.setOpaque(false);
		usernameField.setSelectionColor(Color.ORANGE);
		usernameField.setBounds(352, 225, 285, 25);
		this.add(usernameField);
		
		passwordField.setForeground(Color.WHITE);
		passwordField.setFont(usernameField.getFont());
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setSelectionColor(Color.ORANGE);
		passwordField.setBounds(352, 385, 285, 25);
		this.add(passwordField);
		
		playButton.setBounds(400, 470);
		playButton.addEventListener(this);
		this.add(playButton);
		
		progressBar.setBounds(0, 559, 983, 18);
		this.add(progressBar);
		
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setFont(usernameField.getFont());
		infoLabel.setBounds(10, 505, 990, 25);
		//this.add(infoLabel);
		
		infoLabel2.setForeground(Color.WHITE);
		infoLabel2.setFont(usernameField.getFont());
		infoLabel2.setBounds(0, 328, 976, 22);
		//this.add(infoLabel2);
		
		quitButton.setBounds(950, 10);
		quitButton.setSize(20, 20);
		quitButton.addEventListener(this);
		this.add(quitButton);
		
		hideButton.setBounds(920, 10);
		hideButton.setSize(20, 20);
		hideButton.addEventListener(this);
		this.add(hideButton);
		
		settingsButton.setBounds(880, 7);
		settingsButton.setSize(30, 30);
		settingsButton.addEventListener(this);
		this.add(settingsButton);
		
		discordButton.setBounds(880, 430);
		discordButton.setSize(70, 70);
		discordButton.addEventListener(this);
		this.add(discordButton);
		
		twitterButton.setBounds(800, 425);
		twitterButton.setSize(72, 72);
		twitterButton.addEventListener(this);
		this.add(twitterButton);
	}
	
	//Message D'erreur du Launcher
	@SuppressWarnings("deprecation")
	@Override
   public void onEvent(SwingerEvent e) {
	   if(e.getSource() == playButton) {
			System.out.println("Bouton 'Jouer' cliquer");
			
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() ==0) {	
				JOptionPane.showMessageDialog(this, "Erreur, veuiller entrer un pseudo et un mot de passe valides.", "Erreur 01", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			
            Thread t = new Thread() {
                @Override
                public void run() {
                	try {
	                Launcher.auth(usernameField.getText(), passwordField.getText());
                	} catch (AuthenticationException e) {
                		JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de se connecter :" + e.getErrorModel().getErrorMessage(), "Erreur 02", JOptionPane.ERROR_MESSAGE);
        				setFieldsEnabled(true);
        				return;
        				
                	}
                	
                	saver.set("username", usernameField.getText());
                	
                	try {
    	                Launcher.update();
                    	} catch (Exception e) {
            				Launcher.interruptThread();
            				LauncherFrame.getCrashReporter().catchError(e, "Impossible de mettre a jour le launcher !");
                    		
                    	}
                	
                	try {
    	                Launcher.launch();
                    	} catch (LaunchException e) {
                    		LauncherFrame.getCrashReporter().catchError(e, "Impossible de lancer le jeu !");
                    	}
                	
                	System.out.println("Connection réussi");
                }
            };
            t.start();
            
		}
	   //Bouton Discord et Tweeter à régler !
	   else if(e.getSource() == discordButton)
           try {
               Desktop.getDesktop().browse(new URI("https://discord.gg/C5tAqaFu4u"));
           } catch (IOException e1) {
               e1.printStackTrace();
           } catch (URISyntaxException e1) {
               e1.printStackTrace();
           }
	   else if(e.getSource() == quitButton)
		  
		   System.exit(0);
	   
	   else if(e.getSource() == hideButton)
		   LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
	   else if(e.getSource() == twitterButton)
           try {
               Desktop.getDesktop().browse(new URI("https://twitter.com/iaia4_youtubeur"));
           } catch (IOException e1) {
               e1.printStackTrace();
           } catch (URISyntaxException e1) {
               e1.printStackTrace();
           }
	   else if(e.getSource() == settingsButton)
			selector.display();
	}

	// Ne pas toucher
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
	private void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar() {	
		return progressBar;
	}
	
	public void setInfoText(String text) {
		
		infoLabel.setText(text);
	}
}

//Copyright GLauncher By Gaëtan, MOJANG AB, FORGE, LITARVAN For LIBS