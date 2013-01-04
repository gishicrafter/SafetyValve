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


package safetyvalve.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.TreeSet;
import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHelper {

	@Target({ElementType.FIELD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Item
	{
		String category() default "";
		String name() default "";
		String comment() default "";
	}
	
	@Target({ElementType.FIELD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Block
	{
		String category() default "";
		String name() default "";
		String comment() default "";
	}
	
	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TerrainBlock
	{
		String category() default "";
		String name() default "";
		String comment() default "";
	}
	
	@Target({ElementType.FIELD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Value
	{
		String category() default "";
		String name() default "";
		String comment() default "";
	}
	
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Category
	{
		String parent() default "";
		String name() default "";
	}
	
	Configuration config;
	
	public ConfigHelper(File configFile)
	{
		config = new Configuration(configFile);
	}
	
	private static boolean isInteger(String s)
	{
		try{
			Integer.parseInt(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	private static boolean isDouble(String s)
	{
		try{
			Double.parseDouble(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	private static boolean isEnumConstant(Class c, String name)
	{
		try{
			Enum.valueOf(c, name);
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	private void loadTo(Field field, Item annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException
	{
		String category = annotation.category();
		String key = annotation.name();
		String comment = annotation.comment();
		if(comment.equals("")) comment = null;
		if(key.equals("")) key = field.getName();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = Configuration.CATEGORY_ITEM;
			}else{
				category = defaultCategory;
			}
		}
		int defaultInt = field.getInt(null);
		Property prop = config.getItem(category, key, defaultInt, comment);
		field.setInt(null, prop.getInt());
	}
	
	private void loadTo(Field field, Block annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException
	{
		String category = annotation.category();
		String key = annotation.name();
		String comment = annotation.comment();
		if(comment.equals("")) comment = null;
		if(key.equals("")) key = field.getName();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = Configuration.CATEGORY_BLOCK;
			}else{
				category = defaultCategory;
			}
		}
		int defaultInt = field.getInt(null);
		Property prop = config.getBlock(category, key, defaultInt, comment);
		field.setInt(null, prop.getInt());
	}
	
	private void loadTo(Field field, TerrainBlock annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException
	{
		String category = annotation.category();
		String key = annotation.name();
		String comment = annotation.comment();
		if(comment.equals("")) comment = null;
		if(key.equals("")) key = field.getName();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = Configuration.CATEGORY_BLOCK;
			}else{
				category = defaultCategory;
			}
		}
		int defaultInt = field.getInt(null);
		Property prop = config.getTerrainBlock(category, key, defaultInt, comment);
		field.setInt(null, prop.getInt());
	}
	
	private void loadTo(Field field, Value annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, SecurityException, InvocationTargetException, NoSuchMethodException
	{
		String category = annotation.category();
		String key = annotation.name();
		String comment = annotation.comment();
		if(comment.equals("")) comment = null;
		if(key.equals("")) key = field.getName();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = Configuration.CATEGORY_GENERAL;
			}else{
				category = defaultCategory;
			}
		}
		
		int defaultInt;
		double defaultDouble;
		boolean defaultBool;
		Property prop;
		
		if(field.getType() == int.class){
			loadTo(field, category, key, field.getInt(null), comment);
		}else if(field.getType() == double.class){
			loadTo(field, category, key, field.getDouble(null), comment);
		}else if(field.getType() == float.class){
			loadTo(field, category, key, field.getFloat(null), comment);
		}else if(field.getType() == boolean.class){
			loadTo(field, category, key, field.getBoolean(null), comment);
		}else if(field.getType().isEnum()) {
			if(field.get(null) != null){
				loadTo(field, category, key, (Enum) field.get(null), comment);
			}else{
				loadTo(field, category, key, (Enum) Array.get(field.getType().getMethod("values").invoke(null), 0), comment);
			}
		}else if(field.getType() == String.class){
			loadTo(field, category, key, (String) field.get(null), comment);
		}else if(field.getType().isArray()){
			if(field.getType().getComponentType() == int.class){
				loadTo(field, category, key, (int[])field.get(null), comment);
			}else if(field.getType().getComponentType() == double.class){
				loadTo(field, category, key, (double[])field.get(null), comment);
			}else if(field.getType().getComponentType() == float.class){
				loadTo(field, category, key, (float[])field.get(null), comment);
			}else if(field.getType().getComponentType() == boolean.class){
				loadTo(field, category, key, (boolean[])field.get(null), comment);
			}else if(field.getType().getComponentType() == String.class){
				loadTo(field, category, key, (String[])field.get(null), comment);
			}else if(field.getType().getComponentType().isEnum()){
				loadEnumListTo(field, category, key, comment);
			}
		}else if(field.getType() == EnumSet.class){
			loadEnumSetTo(field, category, key, comment);
		}
	}
	
	private void loadTo(Field field, String category, String key, int defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		Property prop = config.get(category, key, defaultValue, comment);
		field.setInt(null, prop.getInt());
	}
	
	private void loadTo(Field field, String category, String key, double defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		Property prop = config.get(category, key, defaultValue, comment);
		field.setDouble(null, prop.getDouble(defaultValue));
	}
	
	private void loadTo(Field field, String category, String key, float defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		Property prop = config.get(category, key, defaultValue, comment);
		field.setFloat(null, (float) prop.getDouble(defaultValue));
	}
	
	private void loadTo(Field field, String category, String key, boolean defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		Property prop = config.get(category, key, defaultValue, comment);
		field.setBoolean(null, prop.getBoolean(defaultValue));
	}
	
	private void loadTo(Field field, String category, String key, Enum defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		Property prop = config.get(category, key, defaultValue.name(), comment);
		if(isEnumConstant(field.getType(), prop.value)){
			field.set(null, Enum.valueOf((Class)field.getType(), prop.value));
		}
	}
	
	private void loadTo(Field field, String category, String key, String defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		if(defaultValue == null) defaultValue = "";
		Property prop = config.get(category, key, defaultValue, comment);
		field.set(null, prop.value);
	}
	
	private void loadTo(Field field, String category, String key, int[] defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		if(defaultValue == null) defaultValue = new int[0];
		Property prop = config.get(category, key, defaultValue, comment);
		field.set(null, prop.getIntList());
	}
	
	private void loadTo(Field field, String category, String key, double[] defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		if(defaultValue == null) defaultValue = new double[0];
		Property prop = config.get(category, key, defaultValue, comment);
		field.set(null, prop.getDoubleList());
	}
	
	private void loadTo(Field field, String category, String key, float[] defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		if(defaultValue == null) defaultValue = new float[0];
		double[] doubleList;
		doubleList = new double[defaultValue.length];
		for(int i = 0; i < defaultValue.length; ++i){
			doubleList[i] = defaultValue[i];
		}
		Property prop = config.get(category, key, doubleList, comment);
		doubleList = prop.getDoubleList();
		float[] floatList;
		floatList = new float[doubleList.length];
		for(int i = 0; i < doubleList.length; ++i){
			floatList[i] = (float) doubleList[i];
		}
		field.set(null, floatList);
	}
	
	private void loadTo(Field field, String category, String key, boolean[] defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		if(defaultValue == null) defaultValue = new boolean[0];
		Property prop = config.get(category, key, defaultValue, comment);
		field.set(null, prop.getBooleanList());
	}
	
	private void loadTo(Field field, String category, String key, String[] defaultValue, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		if(defaultValue == null) defaultValue = new String[0];
		Property prop = config.get(category, key, defaultValue, comment);
		field.set(null, prop.valueList);
	}
	
	private void loadEnumListTo(Field field, String category, String key, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		Object value = field.get(null);
		Class type = field.getType().getComponentType();
		int length = 0;
		if(value != null) length = Array.getLength(value);
		
		String[] strList = new String[length];
		for(int i = 0; i < length; ++ i){
			strList[i] = ((Enum)Array.get(value, i)).name();
		}
		Property prop = config.get(category, key, strList, comment);
		length = prop.valueList.length;
		value = Array.newInstance(type, length);
		for(int i = 0; i < length; ++i){
			try{
				Array.set(value, i, Enum.valueOf(type, prop.valueList[i]));
			}catch(IllegalArgumentException e){
				
			}
		}
		field.set(null, value);
	}
	
	private void loadEnumSetTo(Field field, String category, String key, String comment) throws IllegalArgumentException, IllegalAccessException
	{
		EnumSet value = (EnumSet) field.get(null);
		Type gentype = field.getGenericType();
		Class type;
		if(!(gentype instanceof ParameterizedType)){
			if(value.size()>0){
				type = value.iterator().next().getClass();
			}else{
				System.out.println("loadEnumSetTo() failed.");
				return;
			}
		}else{
			type = (Class)((ParameterizedType)gentype).getActualTypeArguments()[0];
		}
		
		String[] strList = new String[value.size()];
		int i = 0;
		for(Object obj : value){
			strList[i] = ((Enum)obj).name();
			++i;
		}
		Property prop = config.get(category, key, strList, comment);
		TreeSet newSet = new TreeSet();
		for(String str : prop.valueList){
			try{
				newSet.add(Enum.valueOf(type, str));
			}catch(Exception e){
			}
		}
		field.set(null, EnumSet.copyOf(newSet));
	}
	
	private void loadTo(Class klass, Item annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException
	{
		String category = annotation.category();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = Configuration.CATEGORY_ITEM;
			}else{
				category = defaultCategory;
			}
		}
		
		String child = klass.getSimpleName();
		
		if(child.equals("")) throw new RuntimeException("Category class should not be annonymous.");
		
		category = category + Configuration.CATEGORY_SPLITTER + child;
		
		loadToImpl(klass, category);
	}
	
	private void loadTo(Class klass, Block annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException
	{
		String category = annotation.category();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = Configuration.CATEGORY_BLOCK;
			}else{
				category = defaultCategory;
			}
		}
		
		String child = klass.getSimpleName();
		
		if(child.equals("")) throw new RuntimeException("Category class should not be annonymous.");
		
		category = category + Configuration.CATEGORY_SPLITTER + child;
		
		loadToImpl(klass, category);
	}
	
	private void loadTo(Class klass, Value annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException
	{
		String category = annotation.category();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = Configuration.CATEGORY_GENERAL;
			}else{
				category = defaultCategory;
			}
		}
		
		String child = klass.getSimpleName();
		
		if(child.equals("")) throw new RuntimeException("Category class should not be annonymous.");
		
		category = category + Configuration.CATEGORY_SPLITTER + child;
		
		loadToImpl(klass, category);
	}
	
	private void loadTo(Class klass, Category annotation, String defaultCategory) throws IllegalArgumentException, IllegalAccessException
	{
		String child = klass.getSimpleName();
		if(child.equals("")) throw new RuntimeException("Category class should not be annonymous.");
		
		String category = annotation.parent();
		if(category.equals("")){
			if(defaultCategory == null || defaultCategory.equals("")){
				category = child;
			}else{
				category = defaultCategory + Configuration.CATEGORY_SPLITTER + child;
			}
		}else{
			category = category + Configuration.CATEGORY_SPLITTER + child;
		}
		
		loadToImpl(klass, category);
	}
	
	private void loadToImpl(Class klass, String defaultCategory)
	{
		try{
			Field[] fields = klass.getFields();
			for(Field field : fields){
				Item annotationItem = field.getAnnotation(Item.class);
				Block annotationBlock = field.getAnnotation(Block.class);
				TerrainBlock annotationTerrain = field.getAnnotation(TerrainBlock.class);
				Value annotationValue = field.getAnnotation(Value.class);
				if(annotationItem != null){
					loadTo(field, annotationItem, defaultCategory);
				}else if(annotationBlock != null){
					loadTo(field, annotationBlock, defaultCategory);
				}else if(annotationTerrain != null){
					loadTo(field, annotationTerrain, defaultCategory);
				}else if(annotationValue != null){
					loadTo(field, annotationValue, defaultCategory);
				}
			}
			
			Class[] classes = klass.getClasses();
			for(Class clazz : classes){
				Item annotationItem = (Item) clazz.getAnnotation(Item.class);
				Block annotationBlock = (Block) clazz.getAnnotation(Block.class);
				Value annotationValue = (Value) clazz.getAnnotation(Value.class);
				Category annotationCat = (Category) clazz.getAnnotation(Category.class);
				
				if(annotationItem != null){
					loadTo(clazz, annotationItem, defaultCategory);
				}else if(annotationBlock != null){
					loadTo(clazz, annotationBlock, defaultCategory);
				}else if(annotationValue != null){
					loadTo(clazz, annotationValue, defaultCategory);
				}else if(annotationCat != null){
					loadTo(clazz, annotationCat, defaultCategory);
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public void loadTo(Class klass)
	{
		try{
			config.load();
			Item annotationItem = (Item) klass.getAnnotation(Item.class);
			Block annotationBlock = (Block) klass.getAnnotation(Block.class);
			Value annotationValue = (Value) klass.getAnnotation(Value.class);
			Category annotationCat = (Category) klass.getAnnotation(Category.class);
			
			if(annotationItem != null){
				loadTo(klass, annotationItem, null);
			}else if(annotationBlock != null){
				loadTo(klass, annotationBlock, null);
			}else if(annotationValue != null){
				loadTo(klass, annotationValue, null);
			}else if(annotationCat != null){
				loadTo(klass, annotationCat, null);
			}else{
				loadToImpl(klass, null);
			}
			config.save();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
