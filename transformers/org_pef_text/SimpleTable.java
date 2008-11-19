package org_pef_text;

import java.nio.charset.Charset;
import java.util.HashMap;

import org_pef_text.TableFactory.EightDotFallbackMethod;

public class SimpleTable implements AbstractTable {
	private HashMap<Character, Character> b2t;
	private HashMap<Character, Character> t2b;
	private String tableDef;
	private Charset charset;
	private EightDotFallbackMethod fallback;
	private char replacement;
	private boolean ignoreCase;
	private boolean supports8dot;
	
	
	public SimpleTable(String table, Charset charset, EightDotFallbackMethod fallback, char replacement, boolean ignoreCase) {
		this.tableDef = table;
		this.charset = charset;
		this.fallback = fallback;
		this.replacement = replacement;
		this.ignoreCase = ignoreCase;
		this.supports8dot = tableDef.length()==256;
		b2t = new HashMap<Character, Character>();
		t2b = new HashMap<Character, Character>();
		//lower case def.
		int i = 0;
		char b;
		for (char t : table.toCharArray()) {
			b = (char)(0x2800+i);
			put(b, t);
			i++;
		}
	}

	private void put(char braille, char glyph) {
		if (ignoreCase) {
			t2b.put(Character.toLowerCase(glyph), braille);
		} else {
			t2b.put(glyph, braille);
		}
		b2t.put(braille, glyph);
	}

	public Charset getPreferredCharset() {
		return charset;
	}
	
	public boolean supportsEightDot() {
		return supports8dot;
	}

	private char toBraille(char c) {
		if (ignoreCase) {
			c = Character.toLowerCase(c);
		}
		if (t2b.get(c)==null) throw new IllegalArgumentException("Character '" + c + "' (0x" + Integer.toHexString((int)(c)) + ") not found.");
		return (t2b.get(c));
	}

	public String toBraille(String text) {
		StringBuffer sb = new StringBuffer();
		for (char c : text.toCharArray()) {
			sb.append(toBraille(c));
		}
		return sb.toString();
	}

	private Character toText(char braillePattern) {
		if (b2t.get(braillePattern)==null) {
			int val = (braillePattern+"").codePointAt(0);
			if (val>=0x2840 && val<=0x28FF) {
				switch (fallback) {
					case MASK:
						return toText((char)(val&0x283F));
					case REPLACE:
						if (b2t.get(replacement)!=null) {
							return toText(replacement);
						} else {
							throw new IllegalArgumentException("Replacement char not found.");
						}
					case REMOVE:
						return null;
				}
			} else {			
				throw new IllegalArgumentException("Braille pattern '" + braillePattern + "' not found.");
			}
		}
		return (b2t.get(braillePattern));
	}

	public String toText(String braille) {
		StringBuffer sb = new StringBuffer();
		Character t;
		for (char c : braille.toCharArray()) {
			t = toText(c);
			if (t!=null) {
				sb.append(t);
			}
		}
		return sb.toString();
	}

}
