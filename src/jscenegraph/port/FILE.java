/**
 * 
 */
package jscenegraph.port;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yves Boyadjian
 *
 */
public class FILE {
	
	public static final int EOF = -1;
	
	PushbackInputStream in;

	public FILE(InputStream in) {
		this.in = new PushbackInputStream(in);
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

	public static void ungetc(char c, FILE fp) {
		try {
			fp.in.unread(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getc(FILE fp) {
		try {
			return fp.in.read();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static int feof(FILE fp) {
		try {
			int nextByte = fp.in.read();
			if(nextByte == -1) {
				return -1;
			}
			else {
				fp.in.unread(nextByte);
				return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static long fread(char[] c, int i, int j, FILE fp) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int fscanf(FILE fp, String string, double[] d) {
		try {
		switch(string) {
		case "%lf":
			StringBuffer chars = new StringBuffer();
			boolean isSpace = false;
			do {
					int c = fp.in.read();
				if( c == -1 || Character.isSpace((char)c) || c == ',') {
					isSpace = true;
					if( c != -1) {
						fp.in.unread(c);
					}
				}
				else {
					chars.append((char)c);
				}
			} while(!isSpace);
			d[0] = Double.parseDouble(chars.toString());
			return 1;
			default:
				return 0;
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return EOF;
	}

	public static int fread(byte[] c, int i, int length, FILE fp) {
		// TODO Auto-generated method stub
		return 0;
	}

}
