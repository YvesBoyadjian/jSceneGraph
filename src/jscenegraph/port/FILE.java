/**
 * 
 */
package jscenegraph.port;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author Yves Boyadjian
 *
 */
public class FILE {
	
	InputStream in;

	public FILE(InputStream in) {
		this.in = in;
	}

	public static void fclose(FILE fp) {
		if(fp.in != null) {
			try {
				fp.in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Java port
	 * @param fileName
	 * @param string
	 * @return
	 */
	public static FILE fopen(String fileName, String options) {
	    FileSystem fileSystem = FileSystems.getDefault();
	    Path fileNamePath = fileSystem.getPath(fileName);
	    OpenOption option = null;
	    switch(options) {
	    case "r":
	    	option = StandardOpenOption.READ;
	    	break;
	    }
	    
	    try {
			InputStream inputStream = Files.newInputStream(fileNamePath, option);
			return new FILE(inputStream);
		} catch (IOException e) {
			return null;
		}
	}

	public InputStream getInputStream() {
		return in;
	}

}
