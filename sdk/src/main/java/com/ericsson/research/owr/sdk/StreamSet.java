/*
 * Copyright (c) 2015, Ericsson AB.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package com.ericsson.research.owr.sdk;

import com.ericsson.research.owr.MediaSource;
import com.ericsson.research.owr.MediaType;

import java.util.List;

/**
 * An interface masquerading as an abstract class that represents a set of stream to attach to a RtcSession
 * The reason it's an abstract class is to be able to make the abstract methods package-private
 */
public abstract class StreamSet {
    /**
     * An interface that represents a single audio, video or data stream.
     * The stream interface Stream should not be implemented directly, but through the MediaStream and DataStream interfaces.
     */
    interface Stream {
        /**
         * @return the type of the stream
         */
        abstract StreamDescription.Type getType();

        /**
         * Called once the final mode has been determined for the stream
         * @param mode of the stream
         */
        abstract void setStreamMode(StreamDescription.Mode mode);
    }

    /**
     * An interface that represents a single audio or video stream.
     */
    abstract class MediaStream implements Stream {
        @Override
        public StreamDescription.Type getType() {
            return getMediaType() == MediaType.AUDIO ? StreamDescription.Type.AUDIO : StreamDescription.Type.VIDEO;
        }

        /**
         * Implementations should return a unique identifier for the stream, or null.
         * @return a unique identifier, or null
         */
        abstract String getId();

        /**
         * Implementations should return the media type of the stream.
         * @return the media type of the stream
         */
        abstract MediaType getMediaType();

        /**
         * If the implementation returns false no local stream will be sent, event if one is requested by the peer.
         * @return false if no media should be sent, true otherwise
         */
        abstract boolean wantSend();

        /**
         * If the implementation return false no remote stream will be requested
         * A remote stream might still be received though, in which case it can then be ignored.
         * @return true if remote media should be requested, false otherwise
         */
        abstract boolean wantReceive();

        /**
         * This method is called when the media source for the stream is received
         * @param mediaSource a media source matching the media type of the stream
         */
        abstract void onRemoteMediaSource(MediaSource mediaSource);

        /**
         * Implementations should use the media source delegate to set the media source
         * for the stream. The delegate can be called at any time, but may be ignored if
         * the stream is not active. The most recent stream to be sent to the delegate
         * is always the one that will be used.
         * The media source type type should be the same as the media type of the stream.
         * @param mediaSourceDelegate the delegate to use to set the media source.
         */
        abstract void setMediaSourceDelegate(MediaSourceDelegate mediaSourceDelegate);
    }

    /**
     * An interface that represents a data stream.
     */
    abstract class DataStream implements Stream {
        @Override
        public StreamDescription.Type getType() {
            return StreamDescription.Type.DATA;
        }

        // TODO
    }

    /**
     * Implementations should return a list of streams that are sent and/or received.
     * The streams should be ordered in highest-to-lowest priority. If enough streams of a particular
     * type are not received in the remote description, the excess streams will be invalidated, beginning
     * at the end of the list.
     * @return a list of streams
     */
    abstract List<Stream> getStreams();

    public interface MediaSourceDelegate {
        public void setMediaSource(MediaSource mediaSource);
    }
}
