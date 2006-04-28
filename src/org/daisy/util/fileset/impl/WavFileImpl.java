/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2006  Daisy Consortium
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
package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.daisy.util.fileset.interfaces.audio.WavFile;
import org.daisy.util.mime.MIMETypeException;

import javazoom.jl.decoder.BitstreamException;

/**
 * @author Linus Ericson
 */
class WavFileImpl extends AudioFileImpl implements WavFile {

    private int sampleFrequency;
    private int channels;
    private int sampleSize;
    private int duration;
    
    WavFileImpl(URI uri) throws FileNotFoundException, IOException, MIMETypeException {
		super(uri, WavFile.mimeStringConstant);
	}
    
    public void parse() throws FileNotFoundException, IOException, BitstreamException {
        try {
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(this.getAbsoluteFile());
            if (!"WAVE".equals(aff.getType())) {
                throw new UnsupportedAudioFileException("WAVE file expected, found " + aff.getType() + ": " + this.getAbsolutePath());
            }
            AudioFormat format = aff.getFormat();
            this.sampleFrequency = (int)format.getSampleRate();
            this.channels = format.getChannels();
            this.sampleSize = format.getSampleSizeInBits();
            this.duration = (int)(1000.0 * aff.getFrameLength() / format.getFrameRate());
        } catch (UnsupportedAudioFileException e) {
            // FIXME throw better exception
            throw new IOException("Unsupported file type: " + e.getMessage());
        } 
    }

    public int getSampleFrequency() {
        return this.sampleFrequency;
    }

    public boolean isMono() {
        return (this.channels == 1);
    }

    public long getDuration() {
        return this.duration;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }

}