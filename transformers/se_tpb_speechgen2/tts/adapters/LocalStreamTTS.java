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
package se_tpb_speechgen2.tts.adapters;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;

import org.daisy.util.file.StreamRedirector;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;

import se_tpb_speechgen2.audio.AudioFiles;
import se_tpb_speechgen2.tts.TTSConstants;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.concurrent.TTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * Uses simple stdin-stdout piping to communicate with an 
 * external (sapi, e.g) process.
 * 
 * @author Martin Blomberg
 *
 */
/**
 * @author martin
 *
 */
public class LocalStreamTTS implements TTSAdapter {

	private TTSUtils mTu;				// utility functions
	private Map mParams;				// configuration parameters defined by user

	private String mLastText;			// the text most recently being processed
	private String mCommand;			// the command to use to start external tts process
	private Process mProcess;			// the abstraction of the external tts process

	private OutputStreamWriter mWriter;	// writes data to the external tts process
	private BufferedReader mReader;		// reads data from the external tts process
	private boolean DEBUG = false;		
		
	/**
	 * Constructs a new instance given the parameters in <code>params</code>
	 * and the util functions <code>tu</code>. 
	 * None of those may be <code>null</code>.
	 * @param tu the utility functions.
	 * @param params the parameters provided by configuration.
	 * @throws IOException
	 */
	public LocalStreamTTS(TTSUtils tu, Map params) throws IOException {
		if (tu != null) {
			mTu = tu;
		} else {
			String msg = "No TTSUtils supplied! Unable to continue";
			throw new IllegalArgumentException(msg);
		}
		
		if (params != null) {
			mParams = params;
			mCommand = (String) mParams.get(TTSConstants.COMMAND);
			Runtime rt = Runtime.getRuntime();
			mProcess = rt.exec(mCommand);			
			
			mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));	
			mWriter = new OutputStreamWriter(
					new BufferedOutputStream(mProcess.getOutputStream()), "utf-8");
			
			new StreamRedirector(mProcess.getErrorStream(), System.err, true).start();
			
		} else {
			String msg = "No parameters supplied! Unable to continue";
			throw new IllegalArgumentException(msg);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSAdapter#close()
	 */
	public void close() throws IOException {
		mWriter.write("\n");
		mWriter.flush();
		mWriter.close();
		mReader.close();
		
		mProcess.destroy();
		
		
		/* 
		mWriter.write(System.getProperty("line.separator"));
		mWriter.flush();	
		mWriter.close();
		mReader.close();
		
		int exitVal = -1;
		long pollInterval = 1000;
		long startTime = System.currentTimeMillis();
		
		do {
			try {
				exitVal = mProcess.exitValue();
			} catch (IllegalThreadStateException ignored) {
				
				try {
					Thread.sleep(pollInterval);
				} catch (InterruptedException ignoredAgain) {
					/ * nothing here, we are just trying to shutdown. * /
				}
			}	
		} while (exitVal < 0 && (mTimeout > (System.currentTimeMillis() - startTime)));
		
		if (exitVal < 0) {
			mProcess.destroy();
		}
		
		if (exitVal != 0) {
			String msg = "Filibuster error occurred: At least " + exitVal + " skipped phrase" + (exitVal > 1? "s" : "") 
			+ ", please refer to the filibuster error logs for more information.";
			throw new TTSException(msg);
		} else if (exitVal < 0) {
			String msg = "Filibuster returned error: " + exitVal + ", " +
					"please refer to the filibuster error logs for more information.";
			throw new TTSException(msg);
		}
		 
		 */
	}

	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSAdapter#read(java.util.List, javax.xml.namespace.QName, java.io.File)
	 */
	public long read(List announcements, QName attrName, File destination) throws TTSException {
		String line = TTSUtils.concatAttributes(announcements, attrName);		
		String tmp = line;
		line = mTu.str2input(line);
		if (line.trim().length() == 0) {
			return 0;
		}
		try {
			send(line, destination);
			receive();
			try {
				return AudioFiles.getAudioFileDuration(destination);
			} catch (UnsupportedAudioFileException e) {
				throw new TTSException(e.getMessage(), e);
			}
		} catch (IOException e) {
			System.err.println("Line generating an error: >>" + tmp + "<<");
			e.printStackTrace();
			throw new TTSException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSAdapter#read(org.w3c.dom.Document, java.io.File)
	 */
	public long read(Document doc, File destination) throws TTSException {
		try {
			String line = "";
			try {
				line = mTu.dom2input(doc);
			} catch (CatalogExceptionNotRecoverable e1) {
				throw new TTSException(e1.getMessage(), e1);
			} catch (XSLTException e1) {
				throw new TTSException(e1.getMessage(), e1);
			}
			
			if (line.trim().length() == 0) {
				return 0;
			}
			
			send(line, destination);
			receive();
			try {
				return AudioFiles.getAudioFileDuration(destination);
			} catch (UnsupportedAudioFileException e) {
				throw new TTSException(e.getMessage(), e);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TTSException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Sends a job to the tts.
	 * @param line the text to synthesize.
	 * @param destination the file in which to store the produced audio.
	 * @throws IOException
	 */
	private void send(String line, File destination) throws IOException {
		mLastText = line;
		mWriter.write(destination.getAbsolutePath());
		mWriter.write("\n");
		mWriter.write(line);
		mWriter.write("\n");
		mWriter.flush();
	}
	
	/**
	 * Waits for the external tts process to write the audio to
	 * file and send an "OK" back. This is just to get the two 
	 * processes synchronized.
	 * @throws IOException
	 * @throws TTSException
	 */
	private void receive() throws IOException, TTSException {	
		String line = mReader.readLine();
		if (line == null || !line.equals("OK")) {
			String msg = "Connection to the TTS was lost unexpectedly, " +
			"read " + line + " instead of OK.\n" +
			"Waited for speech >>" + mLastText + "<<";
			throw new TTSException(msg);
		}
	}
	
	
	private void DEBUG(String msg) {
		if (DEBUG) {
			String base = "DEBUG (" + getClass().getName() + "): ";
			System.err.println(base + msg);
		}
	}
}
