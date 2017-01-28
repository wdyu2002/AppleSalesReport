package com.devculture.shell;

import javax.swing.SwingUtilities;
import com.devculture.swing.MainFrame;
import com.devculture.tools.AppleSalesReporter.AppleSalesReporter;

public class AppPortal {
	
	public enum AppName {
		AppleSalesReporter,
		TextureAtlasCreator,
		MapLevelEditor,
	};
	
	public static void OpenApplication(final AppName app) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = MainFrame.getInstance();
					switch(app) {
						case AppleSalesReporter:
							frame.setAppName("Sales Reporting Tool");
							frame.setAppPane(new AppleSalesReporter());
							frame.setAppSize(968, 600);
							break;
						case TextureAtlasCreator:
							frame.setAppName("Texture Atlas Creator");
							frame.setAppPane(new AppleSalesReporter());
							frame.setAppSize(1280, 1024);
							break;
						case MapLevelEditor:
							frame.setAppName("Map Level Editor");
							frame.setAppPane(new AppleSalesReporter());
							frame.setAppSize(1280, 1024);
							break;
						}
				} catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
	}
	
}
