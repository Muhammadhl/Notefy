package com.joshschriever.livenotes.musicxml;

public class SimpleNote {
    public String step;
    public int octave;
    public String type;
    public int alter;

    public SimpleNote(String step, int octave, String type, int alter) {
        this.step = step;
        this.octave = octave;
        this.type = type;
        this.alter = alter;
    }
}
