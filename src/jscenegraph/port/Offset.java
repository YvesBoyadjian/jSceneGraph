/**
 * 
 */
package jscenegraph.port;

import java.lang.reflect.Field;

import jscenegraph.database.inventor.fields.SoFieldContainer;

/**
 * @author Yves Boyadjian
 *
 */
public class Offset {
	
	private String fieldName;

	public Offset(String fieldName) {
		this.fieldName = fieldName;
	}

	public void copyFrom(Offset offset) {
		fieldName = offset.getFieldName();
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object plus(SoFieldContainer container) {
		try {
			Field field = container.getClass().getField(fieldName);
			Object fieldObject = field.get(container);
			return fieldObject;
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
