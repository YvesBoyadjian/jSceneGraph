/**
 * 
 */
package jscenegraph.database.inventor;

/**
 * @author Yves Boyadjian
 *
 */
public class SoMachine {

	// For LITTLE_ENDIAN
	public static void DGL_HTON_INT32(byte[] t, byte[] f)     
    {                       
            t[0] = f[3];        
            t[1] = f[2];        
            t[2] = f[1];        
            t[3] = f[0];        
    }
	
	public static void DGL_HTON_INT32(int[] t, int f) {
		t[0] = Integer.reverseBytes(f);
	}
	
	public static void DGL_HTON_INT32(int[] t, int[] f) {
		t[0] = Integer.reverseBytes(f[0]);
	}
	
	// TODO : verify that it is the good order
	public static void DGL_HTON_INT32(byte[] t, int f) {
		int i = f;
        t[0] = (byte)(i >>> 24);
        t[1] = (byte)((i >> 16) & 0xFF); 
        t[2] = (byte)((i >> 8) & 0xFF);
        t[3] = (byte)((i & 0xFF));		
	}

	// TODO : verify that it is the good order
	public static void DGL_HTON_INT32(byte[] t, int[] f) {
		int i = f[0];
        t[0] = (byte)(i >>> 24);
        t[1] = (byte)((i >> 16) & 0xFF); 
        t[2] = (byte)((i >> 8) & 0xFF);
        t[3] = (byte)((i & 0xFF));		
	}
}
