package org.chobit.apt.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JVM相关工具类
 *
 * @author robin
 */
public final class JvmKit {


	/**
	 * 获取jre版本
	 *
	 * @return jre版本
	 */
	private static int getJreVersion() {
		String version = System.getProperty("java.version");

		// Up to Java 8, from a version string like "1.8.whatever", extract "8".
		if (version.startsWith("1.")) {
			return Integer.parseInt(version.substring(2, 3));
		}

		// Since Java 9, from a version string like "11.0.1" or "11-ea" or "11u25", extract "11".
		// The format is described at http://openjdk.org/jeps/223 .
		Pattern newVersionPattern = Pattern.compile("^(\\d+).*$");
		Matcher newVersionMatcher = newVersionPattern.matcher(version);
		if (newVersionMatcher.matches()) {
			String v = newVersionMatcher.group(1);
			assert v != null : "@AssumeAssertion(nullness): inspection";
			return Integer.parseInt(v);
		}

		throw new RuntimeException("Could not determine version from property java.version=" + version);
	}


	private JvmKit() {
		throw new AssertionError("Private constructor, cannot be accessed.");
	}
}
