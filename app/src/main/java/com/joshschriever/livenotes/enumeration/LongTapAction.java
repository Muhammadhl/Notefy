package com.joshschriever.livenotes.enumeration;



import com.project.notefy.R;

import java8.util.function.Consumer;

public enum LongTapAction {
    START(R.string.start_description, ActionVisitor::startRecording),
    STOP(R.string.stop_description, ActionVisitor::stopRecording),
    SAVE(R.string.save_description, ActionVisitor::saveScore),
    RESET(R.string.reset_description, ActionVisitor::resetScore),
    ADD(R.string.add_note, ActionVisitor::addNote),
    CHOOSE(R.string.choose_description, ActionVisitor::Choose);

    private int description;
    private Consumer<ActionVisitor> action;

    LongTapAction(int description, Consumer<ActionVisitor> action) {
        this.description = description;
        this.action = action;
    }

    public void showDescription(ActionVisitor actionVisitor) {
        actionVisitor.showDescription(description);
    }

    public void takeAction(ActionVisitor actionVisitor) {
        action.accept(actionVisitor);
    }

    public interface ActionVisitor {

        void startRecording();

        void stopRecording();

        void saveScore();

        void resetScore();

        void showDescription(int descriptionResId);

        void addNote();

        void Choose();
    }

}
