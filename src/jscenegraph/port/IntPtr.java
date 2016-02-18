/**
 * 
 */
package jscenegraph.port;

import java.nio.ByteBuffer;

import com.jogamp.common.nio.Buffers;

/**
 * @author Yves Boyadjian
 *
 */
public class IntPtr {
	
	private ByteBuffer buffer;
	private int intOffset;
	
	public IntPtr(int size) {
		int capacity = size*Integer.SIZE/Byte.SIZE;
		buffer = Buffers.newDirectByteBuffer(capacity);
	}

	public IntPtr(FloatPtr other) {
		buffer = other.getBuffer();
		intOffset = other.getFloatOffset();
	}

	public IntPtr(IntPtr other) {
		buffer = other.getBuffer();
		intOffset = other.intOffset;
	}

	public IntPtr(int[] indices) {
		int capacity = indices.length*Integer.SIZE/Byte.SIZE;
		buffer = Buffers.newDirectByteBuffer(capacity);
		buffer.asIntBuffer().put(indices);
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void asterisk(int value) {
		buffer.asIntBuffer().put(intOffset, value);
	}

	public int getIntOffset() {
		return intOffset;
	}

	public void plusPlus() {
		intOffset++;
	}

	public int get() {
		return buffer.asIntBuffer().get(intOffset);
	}
}
