package com.joshschriever.livenotes.musicxml;

public class SimpleNote {
    private String step;
    private int octave;
    private String type;

    public SimpleNote(String step, int octave, String type) {
        this.step = step;
        this.octave = octave;
        this.type = type;
    }
}
