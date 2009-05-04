package org_pef_dtbook2pef.filters;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *  
 * 
 * @author  Joel Hakansson, TPB
 * @version 30 apr 2009
 * @since 1.0
 */
public class RegexFilter implements StringFilter {
	private final Pattern pattern;
	private final String replacement;
	
	/**
	 * Create a new RegexFilter.
	 * @param regex The expression
	 * @param replacement The replacement
	 * @throws  PatternSyntaxException
     *          If the expression's syntax is invalid
	 */
	public RegexFilter(String regex, String replacement) {
		this.pattern = Pattern.compile(regex);
		this.replacement = replacement;
	}
	
	/**
	 * Get this objects <a href="../util/regex/Pattern.html#sum">regular expression</a>
	 * @return this objects regular expression
	 */
	public Pattern getPattern() {
		return pattern;
	}
	
	/**
	 * Get this objects replacement String
	 * @return this objects replacement String
	 */
	public String getReplacement() {
		return replacement;
	}
	
	/**
     * Replaces each substring of the given str that matches this objects <a
     * href="../util/regex/Pattern.html#sum">regular expression</a> with this objects
     * replacement.
     * 
	 * @param str The string to replace in
	 * @return The resulting <tt>String</tt>
	 */
	public String replace(String str) {
		return pattern.matcher(str).replaceAll(replacement);
		//return str.replaceAll("", replacement);
	}
}
