package org_pef_text;

import java.nio.charset.Charset;

/**
 * AbstractTable is an interface for transcoding between Unicode braille and
 * some other braille table.
 * 
 * @author  Joel Hakansson, TPB
 * @version 7 nov 2008
 * @since 1.0
 */
public interface AbstractTable {
	
	/**
	 * Transcode the given text string as braille. This may be a one-to-one mapping or
	 * a many-to-one depending on the table implementation.
	 * @param text
	 * @return
	 */
	public String toBraille(String text);
	
	/**
	 * Transcode the given braille into text.
	 * 
	 * In most cases this will reverse the effect of
	 * toBraille(String text), i.e. text.equals(toText(toBraille(text))), however
	 * an implementation cannot rely on it.
	 * 
	 * Values must be between 0x2800 and 0x28FF.
	 * @param braille
	 * @return
	 */
	public String toText(String braille);
	
	/**
	 * Get the preferred charset for this braille format
 	 * @return
	 */
	public Charset getPreferredCharset();

	/**
	 * 
	 * @return returns true if 8-dot braille is supported, false otherwise
	 */
	public boolean supportsEightDot();
}