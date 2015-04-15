package lanchon.extractar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

// TODO: Load the resource class in a restricted sandbox.

public class Main {

	public static void main(String[] args) {

		try {

			if (args.length != 2) {
				printUsage();
				System.exit(1);
			}
	
			String path = args[0];
			String name = args[1];
	
			File file = new File(path);
			if (!file.exists()) throw new FileNotFoundException(path);

			URLClassLoader loader = new URLClassLoader(new URL[] { file.toURI().toURL() });
			try {
				Class<?> resource = loader.loadClass(name);
				processResourceClass(resource);
			}
			finally { loader.close(); }
		
		}
		catch (Exception e) {
			System.err.println("error: " + e.toString());
			System.exit(1);
		}

	}

	private static void processResourceClass(Class<?> resource) {
		int m = resource.getModifiers();
		if (!Modifier.isPublic(m)) throw new RuntimeException("Resource class not public: " + resource);
		if (!Modifier.isFinal(m)) throw new RuntimeException("Resource class not final: " + resource);
		for (Class<?> nested : resource.getDeclaredClasses()) processNestedClass(nested);
	}

	private static void processNestedClass(Class<?> nested) {
		int m = nested.getModifiers();
		if (!Modifier.isPublic(m)) throw new RuntimeException("Nested class not public: " + nested);
		if (!Modifier.isStatic(m)) throw new RuntimeException("Nested class not static: " + nested);
		if (!Modifier.isFinal(m)) throw new RuntimeException("Nested class not final: " + nested);
		for (Field field : nested.getDeclaredFields()) processField(field);
	}

	private static void processField(Field field) {
		int m = field.getModifiers();
		if (!Modifier.isPublic(m)) throw new RuntimeException("Field not public: " + field);
		if (!Modifier.isStatic(m)) throw new RuntimeException("Field not static: " + field);
		//if (!Modifier.isFinal(m)) throw new RuntimeException("Field not final: " + field);
		Class<?> type = field.getType();
		if (type == int.class) processIntField(field);
		else if (type == int[].class) processIntArrayField(field);
		else throw new RuntimeException("Field not of type 'int' or 'int[]': " + field);
	}

	private static void processIntField(Field field) {
		String category = field.getDeclaringClass().getSimpleName();
		String name = field.getName();
		int value;
		try {
			value = field.getInt(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("int ").append(category).append(' ').append(name).append(' ').append(formatInt(value));
		System.out.println(sb);
	}

	private static void processIntArrayField(Field field) {
		String category = field.getDeclaringClass().getSimpleName();
		String name = field.getName();
		int[] values;
		try {
			values = (int[]) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("int ").append(category).append(' ').append(name).append(" {");
		boolean first = true;
		for (int value : values) {
			if (first) first = false;
			else sb.append(',');
			sb.append(' ').append(formatInt(value));
		}
		sb.append(" }");
		System.out.println(sb);
	}

	private static String formatInt(int i) {
		if (i < 0x10000000) return String.valueOf(i);
		else return String.format("0x%x", i);
	}

	private static void printUsage() {
		System.out.println("ExtractAR Version " + getVersion() + " by Lanchon");
		System.out.println("extractar <classpath-directory-or-jar> <fully-qualified-name-of-R-class>");
	}

	private static String getVersion() {
		String version = "<undefined>";
		final String file = "version";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(file)));
			version = reader.readLine().trim();
			reader.close();
		} catch (Exception e) {}
		return version;
	}

}
