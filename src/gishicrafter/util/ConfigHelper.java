/*
 * Copyright (c) 2012 gishicrafter
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
*/


package gishicrafter.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHelper {

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Item
	{
		String name() default "";
		int defaultID() default -1;
		String comment() default "";
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Block
	{
		String name() default "";
		int defaultID() default -1;
		String comment() default "";
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Value
	{
		String category() default Configuration.CATEGORY_GENERAL;
		String name() default "";
		String defaultValue() default "";
		String comment() default "";
	}
	
	private File configFile;
	
	public ConfigHelper(File configFile)
	{
		this.configFile = configFile;
	}
	
	public static boolean isInteger(String s)
	{
		try{
			Integer.parseInt(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public static boolean isDouble(String s)
	{
		try{
			Double.parseDouble(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public static boolean isEnumConstant(Class c, String name)
	{
		try{
			Enum.valueOf(c, name);
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	public void loadTo(Class klass)
	{
		try{
			Configuration config = new Configuration(configFile);
			config.load();
			Property prop;
			Field[] fields = klass.getFields();
			int defaultInt;
			String defaultStr;
			boolean defaultBool;
			double defaultDouble;
			for(Field field : fields){
				Item annotationItem = field.getAnnotation(Item.class);
				Block annotationBlock = field.getAnnotation(Block.class);
				Value annotationValue = field.getAnnotation(Value.class);
				String key;
				String comment;
				if(annotationItem != null){
					key = annotationItem.name();
					comment = annotationItem.comment();
					if(comment.equals("")) comment = null;
					if(key.equals("")) key = field.getName() + ".id";
					defaultInt = annotationItem.defaultID();
					if(defaultInt == -1) defaultInt = field.getInt(null);
					prop = config.getItem(key, defaultInt, comment);
					field.setInt(null, prop.getInt());
				}else if(annotationBlock != null){
					key = annotationBlock.name();
					comment = annotationBlock.comment();
					if(comment.equals("")) comment = null;
					if(key.equals("")) key = field.getName() + ".id";
					defaultInt = annotationBlock.defaultID();
					if(defaultInt == -1) defaultInt = field.getInt(null);
					prop = config.getBlock(key, defaultInt, comment);
					field.setInt(null, prop.getInt());
				}else if(annotationValue != null){
					key = annotationValue.name();
					comment = annotationValue.comment();
					if(comment.equals("")) comment = null;
					if(key.equals("")) key = field.getName();
					defaultStr = annotationValue.defaultValue();
					
					if(field.getType() == int.class){
						if(!isInteger(defaultStr)) defaultInt = field.getInt(null);
						else defaultInt = Integer.parseInt(defaultStr);
						prop = config.get(annotationValue.category(), key, defaultInt, comment);
						field.setInt(null, prop.getInt());
					}else if(field.getType() == double.class){
						if(!isDouble(defaultStr)) defaultDouble = field.getDouble(null);
						else defaultDouble = Double.parseDouble(defaultStr);
						prop = config.get(annotationValue.category(), key, defaultDouble, comment);
						field.setDouble(null, prop.getDouble(defaultDouble));
					}else if(field.getType() == float.class){
						if(!isDouble(defaultStr)) defaultDouble = field.getFloat(null);
						else defaultDouble = Double.parseDouble(defaultStr);
						prop = config.get(annotationValue.category(), key, defaultDouble, comment);
						field.setFloat(null, (float)prop.getDouble(defaultDouble));
					}else if(field.getType() == boolean.class){
						if(defaultStr.equals("")) defaultBool = field.getBoolean(null);
						else defaultBool = Boolean.parseBoolean(defaultStr);
						prop = config.get(annotationValue.category(), key, defaultBool, comment);
						field.setBoolean(null, prop.getBoolean(false));
					}else if(field.getType().isEnum()) {
						if(!isEnumConstant(field.getType(), defaultStr)){
							if(field.get(null) != null){
								defaultStr = ((Enum)field.get(null)).name();
							}else{
								defaultStr = (String)Array.get(field.getType().getMethod("values").invoke(null), 0);
							}
						}
						prop = config.get(annotationValue.category(), key, defaultStr, comment);
						if(isEnumConstant(field.getType(), prop.value)){
							field.set(null, Enum.valueOf((Class)field.getType(), prop.value));
						}
					}else if(field.getType() == String.class){
						if(defaultStr.equals("") && field.get(null) != null) defaultStr = (String)field.get(null);
						prop = config.get(annotationValue.category(), key, defaultStr, comment);
						field.set(null, prop.value);
					}
				}
			}
			config.save();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
