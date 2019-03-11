package com.joshschriever.livenotes.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.WindowManager.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;


import com.joshschriever.livenotes.enumeration.LongTapAction;
import com.joshschriever.livenotes.fragment.KeySigDialogFragment;
import com.joshschriever.livenotes.fragment.SaveDialogFragment;
import com.joshschriever.livenotes.fragment.TimeDialogFragment;
import com.joshschriever.livenotes.fragment.ChooseFileFragment;
import com.joshschriever.livenotes.midi.AdaptedMidiMessage;
import com.joshschriever.livenotes.musicxml.DurationHandler;
import com.joshschriever.livenotes.musicxml.KeySigHandler;
import com.joshschriever.livenotes.musicxml.MidiToXMLRenderer;
import com.joshschriever.livenotes.musicxml.MusicXmlRenderer;
import com.joshschriever.livenotes.musicxml.Note;
import com.joshschriever.livenotes.musicxml.SimpleNote;
import com.project.notefy.R;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java8.util.Optional;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import uk.co.dolphin_com.seescoreandroid.CursorView;
import uk.co.dolphin_com.seescoreandroid.SeeScoreView;
import uk.co.dolphin_com.sscore.Component;
import uk.co.dolphin_com.sscore.LoadOptions;
import uk.co.dolphin_com.sscore.SScore;
import uk.co.dolphin_com.sscore.ex.ScoreException;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.joshschriever.livenotes.activity.note.CI;
import static com.joshschriever.livenotes.activity.note.DO;
import static com.joshschriever.livenotes.activity.note.FA;
import static com.joshschriever.livenotes.activity.note.LA;
import static com.joshschriever.livenotes.activity.note.ME;
import static com.joshschriever.livenotes.activity.note.RE;
import static com.joshschriever.livenotes.activity.note.SOL;
import static com.joshschriever.livenotes.musicxml.MusicXmlRenderer.isRest;
import static com.joshschriever.livenotes.musicxml.MusicXmlRenderer.streamElements;
import static java8.util.J8Arrays.stream;
import static java8.util.function.Predicates.negate;
import static java8.util.stream.Collectors.toList;
import static java8.util.stream.StreamSupport.stream;
import static uk.co.dolphin_com.seescoreandroid.LicenceKeyInstance.SeeScoreLibKey;

public class LiveNotesActivity extends Activity
        implements MidiToXMLRenderer.Callbacks,
        SeeScoreView.TapNotification,
        LongTapAction.ActionVisitor,
        SaveDialogFragment.Callbacks,
        TimeDialogFragment.Callbacks,
        KeySigDialogFragment.Callbacks,
        MyCustomDialog.Callbacks,
        ChooseOPDialog.Callbacks,
        ChooseFileFragment.Callbacks {


    private static final String KEY_BEATS = "keyBeats";
    private static final String KEY_BEAT_VALUE = "keyBeatValue";
    private static final String KEY_TEMPO = "keyTempo";
    private static final String KEY_FIFTHS = "keyFifths";
    private static final String KEY_IS_MAJOR = "keyIsMajor";
    private static final String KEY_PRECISION = "keyPrecision";
    private static final String KEY_CAN_START = "keyCanStart";
    private static final String KEY_MUSIC_XML = "keyMusicXML";

    private static final String TAG_SAVE_DIALOG = "tagSaveDialog";
    private static final String TAG_TIME_DIALOG = "tagTimeDialog";
    private static final String TAG_KEY_SIG_DIALOG = "tagKeySigDialog";
    private static final String TAG_CHOOSE_FILE_DIALOG = "tagChooseFileDialog";

    private static final LoadOptions LOAD_OPTIONS = new LoadOptions(SeeScoreLibKey, true);
    private static final int PERMISSION_REQUEST_ALL_REQUIRED = 1;
    private static final List<String> REQUIRED_PERMISSIONS = new ArrayList<>();

    static {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("SeeScoreLib");

        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private ScrollView scrollView;
    private SeeScoreView scoreView;
    private MyCustomDialog dialog;
    private ChooseOPDialog opdialog;

    private MidiToXMLRenderer midiToXMLRenderer;


    private DurationHandler durationHandler;
    private MusicXmlRenderer renderer;
    private MusicXmlRenderer import_renderer;

    private Optional<LongTapAction> longTapAction = Optional.empty();

    private int timeSigBeats;
    private int timeSigBeatValue;
    private int tempoBPM;
    private int keyFifths;
    private boolean keyIsMajor;
    private int precision;
    private String restoredXML;
    private int _upper_timestamp = 600;
    private int _bottom_timestamp = 600;
    private List<Integer> _last_upper_duration = new ArrayList<Integer>();
    private List<Integer> _last_lower_duration = new ArrayList<Integer>();
    private ArrayList<SimpleNote> notes_to_import = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_notes);

        if (savedInstanceState == null) {
            checkPermissions();
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        timeSigBeats = savedInstanceState.getInt(KEY_BEATS);
        timeSigBeatValue = savedInstanceState.getInt(KEY_BEAT_VALUE);
        tempoBPM = savedInstanceState.getInt(KEY_TEMPO);
        keyFifths = savedInstanceState.getInt(KEY_FIFTHS);
        keyIsMajor = savedInstanceState.getBoolean(KEY_IS_MAJOR);
        precision = savedInstanceState.getInt(KEY_PRECISION);

        initializeScoreView();
        if (savedInstanceState.getBoolean(KEY_CAN_START)) {
            continueInitialize();
        } else {
            restoredXML = savedInstanceState.getString(KEY_MUSIC_XML);
            if (restoredXML != null) {
                setScoreXML(restoredXML);
                //setLongTapAction(LongTapAction.SAVE, false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (midiToXMLRenderer != null) {
            midiToXMLRenderer.stopRecording();
            if (longTapAction.map(action -> action.equals(LongTapAction.START)).orElse(false)) {
                outState.putBoolean(KEY_CAN_START, true);
            } else {
                //setLongTapAction(LongTapAction.SAVE, false);
            }
        }

        outState.putInt(KEY_BEATS, timeSigBeats);
        outState.putInt(KEY_BEAT_VALUE, timeSigBeatValue);
        outState.putInt(KEY_TEMPO, tempoBPM);
        outState.putInt(KEY_FIFTHS, keyFifths);
        outState.putBoolean(KEY_IS_MAJOR, keyIsMajor);
        outState.putInt(KEY_PRECISION, precision);
        outState.putString(KEY_MUSIC_XML, getXML());

        super.onSaveInstanceState(outState);
    }

    private void checkPermissions() {
        String[] permissionsToRequest = stream(REQUIRED_PERMISSIONS)
                .filter(s -> checkSelfPermission(s) == PackageManager.PERMISSION_DENIED)
                .toArray(String[]::new);
        if (permissionsToRequest.length > 0) {
            requestPermissions(permissionsToRequest, PERMISSION_REQUEST_ALL_REQUIRED);
        } else {
            initialize();
        }
    }

    @Override
    public void onRequestPermissionsResult(int code,
                                           @NonNull String permissions[],
                                           @NonNull int[] results) {
        if (results.length > 0
                && stream(results).allMatch(r -> r == PackageManager.PERMISSION_GRANTED)) {
            initialize();
        } else {
            checkPermissions();
        }
    }

    private void initialize() {
        opdialog = new ChooseOPDialog();
        dialog = new MyCustomDialog();
        initializeScoreView();
        //new TimeDialogFragment().show(getFragmentManager(), TAG_TIME_DIALOG);
        new ChooseFileFragment().show(getFragmentManager(), TAG_CHOOSE_FILE_DIALOG);
    }
/*
    private void initializeScoreView() {
        //scoreView = new SeeScoreView(this, getAssets(), null, this);
        scoreView = new SeeScoreView(this, cursorView, getAssets(), null, this);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.addView(scoreView);
    }*/

    private void initializeScoreView() {
        //Creating the cursor view
        final CursorView cursorView = new CursorView(this, new CursorView.OffsetCalculator() {
            public float getScrollY() {
                final ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);
                return sv.getScrollY();
            }
        });
        //scoreView = new SeeScoreView(this, getAssets(), null, this);
        scoreView = new SeeScoreView(this, cursorView, getAssets(), null, this);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.addView(scoreView);
    }

    @Override
    public void onFileSet(String filename, boolean new_file) {
        if(new_file) {
            new TimeDialogFragment().show(getFragmentManager(), TAG_TIME_DIALOG);
        }
        else {
            readXMLProperties(filename);

            continueInitialize();

            addImportedNotes();
        }
    }

    public void readXMLProperties(String filename) {
        File path = getExternalStoragePublicDirectory("Notefy/Scores");
        File file = new File(path, filename);
        Builder builder = new Builder();
        Document doc;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            Element part = root.getFirstChildElement("part");
            Element first_measure = part.getFirstChildElement("measure");
            Element attributes = first_measure.getFirstChildElement("attributes");
            Element key = attributes.getFirstChildElement("key");
            Element time = attributes.getFirstChildElement("time");
            Element direction = first_measure.getFirstChildElement("direction");
            Element direction_type = direction.getFirstChildElement("direction-type");
           /* Element metronome = direction_type.getFirstChildElement("metronome");
            Element sound = direction.getFirstChildElement("sound");
            String tempo = "";
            if(sound != null) {
                tempo = sound.getAttribute("tempo").getValue();
            }*/
            if(key != null) {
                this.keyFifths = Integer.parseInt(key.getFirstChildElement("fifths").getValue());
                this.keyIsMajor = key.getFirstChildElement("mode").equals("major");
            }
            else {
                this.keyFifths = 0;
                this.keyIsMajor = true;
            }

            this.precision = 1;
            this.timeSigBeats = Integer.parseInt(time.getFirstChildElement("beats").getValue());
            this.timeSigBeatValue = Integer.parseInt(time.getFirstChildElement("beat-type").getValue());
          /*  if(metronome != null) {
                this.tempoBPM = Integer.parseInt(metronome.getFirstChildElement("per-minute").getValue());
            }
            else {
                this.tempoBPM = Integer.parseInt(tempo);
            }*/
            this.tempoBPM = 100;



            streamElements(part.getChildElements("measure")).
                    forEach(measure ->
                            streamElements(measure.getChildElements("note")).filter(negate(isRest)).
                                    forEach(note -> notes_to_import.add(elementToNote(note))));


        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SimpleNote elementToNote(Element note) {
        Element rest = note.getFirstChildElement("rest");
        if(rest != null) return null;
        Element pitch = note.getFirstChildElement("pitch");
        Element octave = pitch.getFirstChildElement("octave");
        Element step = pitch.getFirstChildElement("step");
        Element alter = pitch.getFirstChildElement("alter");
        Element type = note.getFirstChildElement("type");
        if(alter == null){
            return new SimpleNote(step.getValue(), Integer.parseInt(octave.getValue()), type.getValue(),0);
        }
        return new SimpleNote(step.getValue(), Integer.parseInt(octave.getValue()), type.getValue(),Integer.parseInt(alter.getValue()));
    }

    @Override
    public void onTimeSet(int timeSigBeats, int timeSigBeatValue, int tempoBPM) {
        this.timeSigBeats = timeSigBeats;
        this.timeSigBeatValue = timeSigBeatValue;
        this.tempoBPM = tempoBPM;



        new KeySigDialogFragment().show(getFragmentManager(), TAG_KEY_SIG_DIALOG);
    }

    @Override
    public void onKeySigSet(int fifths, boolean isMajor) {
        this.keyFifths = fifths;
        this.keyIsMajor = isMajor;
        this.precision = 1;

        continueInitialize();

    }

    private static AdaptedMidiMessage noteOn(int value) {
        return new AdaptedMidiMessage(AdaptedMidiMessage.STATUS_NOTE_ON, value);
    }

    private static AdaptedMidiMessage noteOff(int value) {
        return new AdaptedMidiMessage(AdaptedMidiMessage.STATUS_NOTE_OFF, value);
    }

    @Override
    public void chooseDelete() {
        if(midiToXMLRenderer.deleteLastNote() == false) {
            Toast.makeText(this, "There are no notes to remove",
                    Toast.LENGTH_LONG).show();
            return;
        }
        //_bottom_timestamp -= _last_lower_duration.get(0);
        //_last_lower_duration.remove(0);
        _upper_timestamp -= _last_upper_duration.get(0);
        _last_upper_duration.remove(0);


    }

    @Override
    public void chooseAdd() {
        dialog.show(getFragmentManager(), "MyCustomDialog");
    }
    @Override
    public void chooseSave() {
        saveScore();
    }

    private void addImportedNotes(){

        for (SimpleNote note_to_import : notes_to_import){
            int value = 0;
            if(note_to_import.step.equals("C")){ value = 48; }
            if(note_to_import.step.equals("D")){ value = 50; }
            if(note_to_import.step.equals("E")){ value = 52; }
            if(note_to_import.step.equals("F")){ value = 53; }
            if(note_to_import.step.equals("G")){ value = 55; }
            if(note_to_import.step.equals("A")){ value = 57; }
            if(note_to_import.step.equals("B")){ value = 59; }

            String sign="";
            if(note_to_import.alter == 1)  { sign = "diese"; }
            if(note_to_import.alter == -1) { sign = "bemol"; }

            int duration = 0;
            if(note_to_import.type.equals("whole"))    { duration = 2400; }
            if(note_to_import.type.equals("half"))     { duration = 1200; }
            if(note_to_import.type.equals("quarter"))  { duration = 600;  }
            if(note_to_import.type.equals("eighth"))   { duration = 300;  }
            if(note_to_import.type.equals("16th"))     { duration = 150;  }
            if(note_to_import.type.equals("32th"))     { duration = 75;   }

            value += (12*(note_to_import.octave-4));
            onClickAdd(value,sign,duration);
        }
    }



    @Override
    public void onClickAdd(int value, String sign, int duration) {
        value = UpdateValueAccordingToSign(value,sign);

        if (value >= 48) {
            midiToXMLRenderer.messageReady(noteOn(value), _upper_timestamp);
            midiToXMLRenderer.messageReady(noteOff(value), _upper_timestamp + duration);
            _upper_timestamp += duration;
            _last_upper_duration.add(duration);

        } else {
            midiToXMLRenderer.messageReady(noteOn(value), _bottom_timestamp);
            midiToXMLRenderer.messageReady(noteOff(value), _bottom_timestamp + duration);
            _bottom_timestamp += duration;
            _last_lower_duration.add(duration);
        }
    }

    private int UpdateValueAccordingToSign(int value, String sign) {
        if(sign.equals("bemol"))
            value--;
        if(sign.equals("diese"))
            value++;
        return value;
    }


    private void continueInitialize() {
        initializeRenderer();

        onReadyToRecord();
    }

    private void initializeRenderer() {
        midiToXMLRenderer = new MidiToXMLRenderer(this,
                                                  timeSigBeats,
                                                  timeSigBeatValue,
                                                  tempoBPM,
                                                  keyFifths,
                                                  keyIsMajor,
                                                  precision);
        onXMLUpdated();
        durationHandler = new DurationHandler(timeSigBeats, timeSigBeatValue, tempoBPM, precision);
        //renderer = new MusicXmlRenderer(durationHandler, new KeySigHandler(keyFifths, keyIsMajor));
        //import_renderer = new MusicXmlRenderer("Composition_2019-02-24_14.40.39.xml",durationHandler, new KeySigHandler(keyFifths, keyIsMajor));
    }


    @Override
    public void onXMLUpdated() {
        setScoreXML(midiToXMLRenderer.getXML());
        scrollView.postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN), 80);
        scrollView.postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN), 180);
    }
/*
    public void onXMLUpdated2() {
        setScoreXML(renderer.getMusicXMLString());
        scrollView.postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN), 80);
        scrollView.postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN), 180);
    }*/

    private void setScoreXML(String newXML) {
        try {
            setScore(SScore.loadXMLData(newXML.getBytes(), LOAD_OPTIONS));
        } catch (ScoreException e) {
            e.printStackTrace();
        }
    }

    private void setScore(SScore score) {
        new Handler(getMainLooper()).post(
                () -> scoreView.setScore(score,
                                         stream(new Boolean[score.numParts()])
                                                 .map(__ -> Boolean.TRUE).collect(toList()),
                                         1.0f));
    }

    MidiToXMLRenderer getMidiToXMLRenderer() {
        return midiToXMLRenderer;
    }

    private void onReadyToRecord() {
        midiToXMLRenderer.setReady();
        setLongTapAction(LongTapAction.CHOOSE, true);
        //setLongTapAction(LongTapAction.START, false);
        //showToast(R.string.start_playing_or_long_press, Toast.LENGTH_LONG);
    }

    @Override
    public void onStartRecording() {
        //setLongTapAction(LongTapAction.STOP, false);
    }

    @Override
    public void tap(int systemIndex, int partIndex, int barIndex, Component[] components) {
        longTapAction.ifPresent(action -> action.showDescription(this));
    }

    @Override
    public void longTap(int systemIndex, int partIndex, int barIndex, Component[] components) {
        longTapAction.ifPresent(action -> action.takeAction(this));
    }

    @Override
    public void startRecording() {
        //midiToXMLRenderer.startRecording();
        //setLongTapAction(LongTapAction.STOP, true);
        Note note = Note.newNote(0,1,48).build();
        Pair<Note, List<Note>> pair = durationHandler.getNoteSequenceFromNote(note);
        renderer.noteEvent(pair.first, pair.second);
        //this.onXMLUpdated2();

        note = Note.newNote(0,80,52).build();
        pair = durationHandler.getNoteSequenceFromNote(note);
        renderer.noteEvent(pair.first, pair.second);
        //this.onXMLUpdated2();

        note = Note.newNote(0,80,54).build();
        pair = durationHandler.getNoteSequenceFromNote(note);
        renderer.noteEvent(pair.first, pair.second);
        onXMLUpdated();
    }

    @Override
    public void stopRecording() {
        //midiToXMLRenderer.stopRecording();
        //setLongTapAction(LongTapAction.SAVE, true);
    }

    @Override
    public void saveScore() {
        //clearLongTapAction();
        new SaveDialogFragment().show(getFragmentManager(), TAG_SAVE_DIALOG);
    }

    @Override
    public void addNote() {
        dialog.show(getFragmentManager(), "MyCustomDialog");
    }

    @Override
    public void Choose() {
        opdialog.show(getFragmentManager(), "ChooseOPDialog");
    }

    @Override
    public void resetScore() {
        clearLongTapAction();
        resetFields();
        initialize();
    }

    @Override
    public void showDescription(int descriptionResId) {
        showToast(descriptionResId, Toast.LENGTH_SHORT);
    }

    private void setLongTapAction(@NonNull LongTapAction action, boolean showDescription) {
        longTapAction = Optional.of(action);
        if (showDescription) {
            action.showDescription(this);
        }
    }

    private void clearLongTapAction() {
        longTapAction = Optional.empty();
    }

    private void showToast(int messageResId, int length) {
        Toast.makeText(this, messageResId, length).show();
    }

    @Override
    public void onSave(String fileName) {
        if (saveFile(fileName)) {
            //setLongTapAction(LongTapAction.RESET, false);
            //showToast(R.string.saved_reset, Toast.LENGTH_LONG);
        } else {
            //setLongTapAction(LongTapAction.SAVE, false);
            //showToast(R.string.error_saving, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onCancelSaving() {
        //setLongTapAction(LongTapAction.RESET, true);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean saveFile(String fileName) {
        try {
            File path = getExternalStoragePublicDirectory(getString(R.string.storage_dir));
            path.mkdirs();
            File file = new File(path, fileName);

            FileOutputStream stream = new FileOutputStream(file);
            stream.write(getXML().getBytes());
            stream.flush();
            stream.close();

            MediaScannerConnection.scanFile(this, new String[] {file.toString()}, null, null);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String getXML() {
        return midiToXMLRenderer != null ? midiToXMLRenderer.getXML() : restoredXML;
    }

    private void resetFields() {
        scrollView.removeView(scoreView);
        scrollView = null;
        scoreView = null;

        midiToXMLRenderer = null;

        longTapAction = Optional.empty();
    }



/*
    @Override
    public void onBackPressed() {
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private note IntToNote (int value) throws IllegalArgumentException {
        if (value % 12 == 0) { return DO; }
        else if (value % 12 == 2) { return RE; }
        else if (value % 12 == 4) { return ME; }
        else if (value % 12 == 5) { return FA; }
        else if (value % 12 == 7) { return SOL; }
        else if (value % 12 == 9) { return LA; }
        else if (value % 12 == 11){ return CI; }
        else { throw new IllegalArgumentException();}
    }


}
