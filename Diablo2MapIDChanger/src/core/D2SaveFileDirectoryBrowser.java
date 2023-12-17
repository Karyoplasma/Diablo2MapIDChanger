package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class D2SaveFileDirectoryBrowser {

	private D2SaveFileDirectoryBrowser() {

	}
	
	public static List<File> returnCharFileList(File directory) throws IOException{
		if (!directory.isDirectory()) {
			return null;
		}
		
		List<File> ret = new ArrayList<File>();
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().toLowerCase().endsWith(".d2s")) {
					ret.add(file);
				}
            }
        }
		return ret;
		
	}
}
