package org.gradle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Test {
	static JFrame frame;
	static JPanel panel;
	static JLabel getVideo,loadingSign,taskComplete,fileName,copyright;
	static JButton genTitle,closeApp,openChooser,launchVideo,launchSubtitle;
	static String name,previous=null;
	static ButtonGroup groupButton;
	public static void main(String[] arg){
		frame=new JFrame("Subtitle Generator");
		frame.setVisible(true);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel=new JPanel();
		panel.setLayout(null);
		Border border=BorderFactory.createTitledBorder("Subtitle Generator");
		panel.setBorder(border);
		panel.setBackground(Color.getHSBColor(192, 41, 76));
		frame.add(panel);
		
		groupButton=new ButtonGroup();
		
		openChooser=new JButton("Select File");
		panel.add(openChooser);
		openChooser.setBounds(180,50,120,25);
		openChooser.setBackground(Color.PINK);
		openChooser.setOpaque(true);
		final JFileChooser fileDialog=new JFileChooser("C:\\Users\\ANURAG\\Desktop\\Videos");
		FileNameExtensionFilter filter=new FileNameExtensionFilter("Video Files", "mp4","avi","wav","mkv","3gp");
		fileDialog.setFileFilter(filter);
		openChooser.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				fileDialog.showOpenDialog(fileDialog);
				name=fileDialog.getSelectedFile().getAbsolutePath();
				if(name!=null)
					System.out.println("Entered Block");
					fileName.setText(name.substring(name.lastIndexOf('\\')+1,name.length()));
					genTitle.setEnabled(true);
					launchVideo.setEnabled(false);
					launchSubtitle.setEnabled(false);
					taskComplete.setVisible(false);
				}
		});
		getVideo=new JLabel("Choose File..");
		panel.add(getVideo);
		getVideo.setBounds(300,20,200,25);
		getVideo.setVisible(false);
		
		fileName=new JLabel("No File Selected");
		panel.add(fileName);
		fileName.setBounds(190, 100, 200, 50);
		fileName.setAlignmentX(Component.CENTER_ALIGNMENT);;
		
		ImageIcon circle = new ImageIcon("ajax-loader.gif");
		loadingSign=new JLabel("Processing..",circle,JLabel.CENTER);
		panel.add(loadingSign);
		loadingSign.setBounds(180, 150, 100, 100);
		loadingSign.setVisible(false);
		
		genTitle=new JButton("Generate Subtitles");
		panel.add(genTitle);
		genTitle.setBounds(90,300,140,25);
		genTitle.setEnabled(false);
		genTitle.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				loadingSign.setVisible(true);
				String test=name.substring(0,name.length()-4)+".srt";
				File f=new File(test);
				if(f.exists()){
					System.out.println("file already exists");
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					loadingSign.setVisible(false);
					taskComplete.setVisible(true);
					launchVideo.setEnabled(true);
					launchSubtitle.setEnabled(true);
				}
				else{
					System.out.println("Doesnot exist");
					new Test().new Processing().execute();
				}
			}
		});
		
		closeApp=new JButton("Exit");
		panel.add(closeApp);
		closeApp.setBounds(240,300,140,25);
		closeApp.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		launchVideo=new JButton("Open VLC");
		panel.add(launchVideo);
		launchVideo.setBounds(90,340,140,25);
		launchVideo.setEnabled(false);
		launchVideo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					launchPlayer(name);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		launchSubtitle=new JButton("View Subtitles");
		panel.add(launchSubtitle);
		launchSubtitle.setBounds(240, 340, 140, 25);
		launchSubtitle.setEnabled(false);
		launchSubtitle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					String subtitleName=name.substring(0, name.length()-4) + ".srt";
					launchSubtitle(subtitleName);
				}catch(IOException e){
					e.printStackTrace();
				}
				
			}
		});
		
		taskComplete=new JLabel("Successful!");
		panel.add(taskComplete);
		taskComplete.setForeground(Color.RED);
		taskComplete.setBounds(200,350,100,100);
		taskComplete.setVisible(false);
		
		copyright=new JLabel("Copyright \u00a9, 2016");
		panel.add(copyright);
		copyright.setBounds(180,425,150,25);
		copyright.putClientProperty("html", null);
	}
	
	public static void launchPlayer(String filePath) throws IOException{
		try {
			ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\VideoLAN\\VLC\\vlc.exe", filePath);
			Process start = pb.start();
		    } catch (Exception ex) {}
	}
	
	public static void launchSubtitle(String filePath) throws IOException{
		try{
//		ProcessBuilder pb=new ProcessBuilder("C:\\Program Files\\Sublime Text 2\\sublime_text.exe",filePath);
			ProcessBuilder pb=new ProcessBuilder("C:\\Windows\\notepad.exe",filePath);
			Process start=pb.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	class Processing extends SwingWorker<Integer, Object>{

		@Override
		protected Integer doInBackground() throws Exception {
			try{
				String audioFilePath=VideoToAudio.getAudio(name);
				GenerateSRT.getSrt(audioFilePath);
				File file=new File(audioFilePath);
				if(file.delete()){
					System.out.println("File is deleted");
				}
				else{
					System.out.println("Unable to delete");
				}
				return 1;
			}catch(Exception exception){
				exception.printStackTrace();
				return -1;
			}
		}
		
		@Override
		protected void done(){
			int result=-1;
			try {
				result = get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			if(result==1){
				loadingSign.setVisible(false);
				taskComplete.setVisible(true);
				launchVideo.setEnabled(true);
				launchSubtitle.setEnabled(true);
			}else{
				loadingSign.setVisible(false);
				taskComplete.setText("Error!");
				taskComplete.setVisible(true);
				JOptionPane.showMessageDialog(null, "An error has occured","Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}