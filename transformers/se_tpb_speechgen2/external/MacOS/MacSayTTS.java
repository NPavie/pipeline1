/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_speechgen2.external.MacOS;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * 
 * TTS Adapter that use the Mac OS X built '/usr/bin/say' command for speech
 * synthesis .
 * 
 * @author Romain Deltour
 * 
 */
public class MacSayTTS extends AbstractTTSAdapter {

	public MacSayTTS(TTSUtils ttsUtils, Map<String, String> params) {
		super(ttsUtils, params);
	}

	@SuppressWarnings("unused")
	@Override
	public void read(String line, File destination) throws IOException,
			TTSException {
		String destName = destination.getAbsolutePath();
		String aiffName = destName + ".aiff";
		String say = "/usr/bin/say";
		String sox = System.getProperty("pipeline.sox.path");
		String[] cmd = null;
		try {
			cmd = new String[] { say, "-o", aiffName, line };
			Runtime.getRuntime().exec(cmd).waitFor();
			cmd = new String[] { sox, aiffName, "-t", "wav", destName };
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (Exception e) {
			throw new TTSException("Could not execute: " + cmd, e);
		} finally {
			(new File(aiffName)).delete();
		}
	}

	@Override
	protected boolean canSpeak(String line) {
		if (line == null) {
			return false;
		}
		boolean canSpeak = false;
		int i = 0;
		while (!canSpeak && i < line.length()) {
			int cp = Character.codePointAt(line, i++);
			canSpeak = !(Character.isSpaceChar(cp) || Character
					.isWhitespace(cp));
		}
		return canSpeak;
	}

}
