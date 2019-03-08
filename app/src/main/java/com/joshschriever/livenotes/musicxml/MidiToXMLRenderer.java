package com.joshschriever.livenotes.musicxml;

import com.joshschriever.livenotes.midi.AdaptedMessageRecipient;
import com.joshschriever.livenotes.midi.AdaptedMidiMessage;

public class MidiToXMLRenderer implements AdaptedMessageRecipient {

    private Callbacks callbacks;
    private MusicXmlRenderer renderer;
    private MidiParser parser;

    private boolean ready = false;
    private boolean recording = false;

    public MidiToXMLRenderer(Callbacks callbacks,
                             int beats,
                             int beatValue,
                             int tempo,
                             int keyFifths,
                             boolean keyIsMajor,
                             int precision) {
        this.callbacks = callbacks;

        DurationHandler durationHandler = new DurationHandler(beats, beatValue, tempo, precision);
        renderer = new MusicXmlRenderer(durationHandler, new KeySigHandler(keyFifths, keyIsMajor));

        parser = new MidiParser(durationHandler);
        parser.addParserListener(renderer);
    }

    public void setReady() {
        ready = true;
    }

    public void startRecording() {
        if (ready && !recording) {
            parser.startWithRests(System.currentTimeMillis());
            callbacks.onXMLUpdated();
            recording = true;
        }
    }

    public void stopRecording() {
        if (recording) {
            ready = false;
            recording = false;
            parser.stop(System.currentTimeMillis());
            renderer.cleanup();
            callbacks.onXMLUpdated();
        }
    }

    @Override
    public void messageReady(AdaptedMidiMessage message, long timeStamp) {
        if (ready) {
            if (!recording) {
                recording = true;
                callbacks.onStartRecording();

                parser.startWithNote(timeStamp, message);
            } else {
                parser.parse(timeStamp, message);
            }
            callbacks.onXMLUpdated();
        }
    }

    public boolean deleteLastNote() {
        boolean tmp = false;
        if (ready) {
            tmp = renderer.removeLastNote();
            if(renderer.removeMeasureNeeded()) {
                parser.removeLastMeasure();
            }
            callbacks.onXMLUpdated();
        }
        return tmp;
    }

    public String getXML() {
        return renderer.getMusicXMLString();
    }

    public interface Callbacks {

        void onXMLUpdated();

        void onStartRecording();
    }

}
