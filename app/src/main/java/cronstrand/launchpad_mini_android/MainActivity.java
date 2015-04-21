package cronstrand.launchpad_mini_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import jp.kshoji.driver.midi.activity.AbstractSingleMidiActivity;
import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;

/**
 * Created by Rasmus on 2015-04-18.
 */

public class MainActivity extends AbstractSingleMidiActivity {
    SparseArray<Point> launchpadGrid;

    boolean midiOn = true;
    TextView text ;
    MidiOutputDevice midiOutputDevice;
    final Handler timerHandler = new Handler();
    final Handler midiInputEventHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int numb = msg.what;
            text.setText(""+ numb);
            // message handled successfully
            return true;
        }
    });
    public Game game;
    public CountDownTimer countDownTimer;
    public int gridTimer;
    Runnable runnable;
public int[] countDownGrid;
    private int[] borderGrid;
    private boolean border;
    private int[] countDownGridTop;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
/*
        ListView midiInputEventListView = (ListView) findViewById(R.id.midiInputEventListView);
        midiInputEventAdapter = new ArrayAdapter<String>(this, R.layout.midi_event, R.id.midiEventDescriptionTextView);
        midiInputEventListView.setAdapter(midiInputEventAdapter);

        ListView midiOutputEventListView = (ListView) findViewById(R.id.midiOutputEventListView);
        midiOutputEventAdapter = new ArrayAdapter<String>(this, R.layout.midi_event, R.id.midiEventDescriptionTextView);
        midiOutputEventListView.setAdapter(midiOutputEventAdapter);
*/

        text = (TextView) findViewById(R.id.textView);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MidiOutputDevice midiOutputDevice = getMidiOutputDevice();
                int note = 0;

                for (int i = 0 ; i <127 ; i++){
                    note = i;
                    if (note % 8 == 0){
                        note+= 8;
                    }
                    if (midiOn) {
                        midiOutputDevice.sendMidiNoteOn(0, 0, note, 127);
                    }
                    else{
                         midiOutputDevice.sendMidiNoteOff(0, 0, note, 127);
                    }
                    // Perform action on click


                }
                midiOn = !midiOn;
            }
        });

        game = new Game(7);

       // midiOutputDevice.sendMidiNoteOff(0, 0, note, 127);
        launchpadGrid = new SparseArray<Point>(){{
            for (int a=0, b = 16, c=32, d=48,e=64,f=80,g=96,h=112  ;a<8 ; a++, b++,c++,d++,e++,f++,g++,h++){
                put(a,new Point(a,0));
                put(b,new Point(a,1));
                put(c,new Point(a,2));
                put(d,new Point(a,3));
                put(e,new Point(a,4));
                put(f,new Point(a,5));
                put(g,new Point(a,6));
                put(h,new Point(a,7));
            }
        }};
        countDownGrid = new int[]{120, 104, 88, 72, 56, 40, 24, 8};
        countDownGridTop = new int[]{104,105,106,107,108,109,110,111};
        borderGrid = new int[]{112, 113, 114, 115, 116, 117, 118, 119, 7,23,39,55,71,87,103};
    }
    public void startTimer(){
        for (int note: countDownGrid){
            midiOutputDevice.sendMidiNoteOn(0, 0, note, 62);
        }
        gridTimer = 7;
        timerHandler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (game.gameOver){

                    resetTimer();
                    return;
                }
                if (gridTimer >= 0) {

                    midiOutputDevice.sendMidiNoteOff(0, 0, countDownGrid[gridTimer], 128);
                    timerHandler.postDelayed(this, 1000);
                    gridTimer--;
                }
                else
                {
                    game.timesUp(midiOutputDevice);

                }

            }
        };
        timerHandler.postDelayed(runnable, 1000);

    }

    private void resetTimer() {
        timerHandler.removeCallbacks(runnable);
        for (int note:countDownGrid){
            midiOutputDevice.sendMidiNoteOff(0, 0, note, 128);
        }

        midiOutputDevice.sendMidiNoteOn(0, 0, 120, 13);

    }

    @Override
    public void onDeviceAttached(UsbDevice usbDevice) {
        midiOutputDevice = getMidiOutputDevice();
        midiOutputDevice.sendMidiNoteOn(0, 0, 120, 13);
        for (int note:borderGrid){
            midiOutputDevice.sendMidiNoteOn(0, 0, note, 13);
        }

       // startTimer();

    }
    private void toggleBorder() {
        if(border== true) {
            for (int note : borderGrid) {
                midiOutputDevice.sendMidiNoteOn(0, 0, note, 13);
            }
            border = false;
        }
        else{
            for (int note : borderGrid) {
                midiOutputDevice.sendMidiNoteOff(0, 0, note, 128);
            }
            border = true;
        }
    }


    @Override
    public void onDeviceDetached(UsbDevice usbDevice) {

    }

    @Override
    public void onMidiMiscellaneousFunctionCodes(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiCableEvents(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiSystemCommonMessage(MidiInputDevice midiInputDevice, int i, byte[] bytes) {

    }

    @Override
    public void onMidiSystemExclusive(MidiInputDevice midiInputDevice, int i, byte[] bytes) {

    }

    @Override
    public void onMidiNoteOff(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiNoteOn(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        if (i2 == 120){
            game.resetGame(midiOutputDevice);

            return;
        }
        if (i2 == 8){
            toggleBorder();
        }
        /*
        int row = i2/16;
        int column = i2 % 16;
        */
        if (game.gameOver) return;
        Point launchButton = launchpadGrid.get(i2);
        if(launchButton == null) return;
        int row = launchpadGrid.get(i2).x;
        int column = launchpadGrid.get(i2).y;
        if (column >= game.boardSize) return;
        if (row >= game.boardSize) return;
        LaunchButton button = game.gameGrid[row][column];
        midiInputEventHandler.sendEmptyMessage(0);

        if (button == null){
            midiInputEventHandler.sendEmptyMessage(0);
            game.gameGrid[row][column] = new LaunchButton(game.currentColor,i2,game.currentPlayer);

            midiOutputDevice.sendMidiNoteOn(i, i1, i2, game.gameGrid[row][column].color);

            game.checkLose(row, column, midiOutputDevice);
            game.nextPlayer();
            startTimer();
        }

    }

   
    @Override
    public void onMidiPolyphonicAftertouch(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiControlChange(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiProgramChange(MidiInputDevice midiInputDevice, int i, int i1, int i2) {

    }

    @Override
    public void onMidiChannelAftertouch(MidiInputDevice midiInputDevice, int i, int i1, int i2) {

    }

    @Override
    public void onMidiPitchWheel(MidiInputDevice midiInputDevice, int i, int i1, int i2) {

    }

    @Override
    public void onMidiSingleByte(MidiInputDevice midiInputDevice, int i, int i1) {

    }
}
