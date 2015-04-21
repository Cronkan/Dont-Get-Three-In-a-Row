package cronstrand.launchpad_mini_android;

import android.os.Handler;

import java.util.List;

import jp.kshoji.driver.midi.device.MidiOutputDevice;

/**
 * Created by Rasmus on 2015-04-18.
 */
public class Game {

    public LaunchButton[][] gameGrid;
    public int currentColor;
    public int currentPlayer;
    public int player1 ;
    public int player2 ;
    public int[] loseGrid;
    public int boardSize;
    public boolean gameOver;
    public Game(int boardSize) {
        this.boardSize = boardSize;
        gameGrid = new LaunchButton[boardSize][boardSize];
        currentPlayer = 1;
        player1 = 29;
        player2 = 28;
        currentColor = player1;
        gameOver = false;
    }


    public void nextPlayer() {
        currentPlayer = currentPlayer == 2 ? 1 : 2;
        currentColor = currentPlayer == 1 ? player1 : player2;
    }

    public void resetGame(MidiOutputDevice device) {
        for (LaunchButton[] buttonArray : gameGrid){
            for (LaunchButton button : buttonArray ){
                if (button != null){
                    device.sendMidiNoteOff(0,0,button.note,button.color);
                }
            }
        }
        gameGrid = new LaunchButton[boardSize][boardSize];

        gameOver = false;
    }

    public void checkLose(int row, int column, MidiOutputDevice device) {
        int[][] check = {{1, 0, 2, 0},
                {1 , 0 , -1, 0},
                {-1, 0 , -2, 0},
                {0 , 1 ,  0, 2},
                {0 , 1 ,  0, -1},
                {0 , -1,  0, -2},
                {-1, -1, -2, -2},
                {-1, -1,  1, 1},
                {1 , 1 ,  2, 2},
                {1 , -1,  2, -2},
                {1 , -1, -1, 1},
                { 1,  1,  2, 2},
                {-1,  1 ,-2, 2}};
        for (int[] checkit : check)
            if (threeInRow(checkit,row,column)) {
                device.sendMidiNoteOn(0,0,gameGrid[loseGrid[0]][loseGrid[1]].note,11);
                device.sendMidiNoteOn(0,0,gameGrid[loseGrid[2]][loseGrid[3]].note,11);
                device.sendMidiNoteOn(0,0,gameGrid[loseGrid[4]][loseGrid[5]].note,11);
                gameOver = true;

                return;
               // resetGame(device);
            }
    }


    private boolean threeInRow(int[] check,int row, int col){
        /*
        int row1 = ((row+8)+check[0])%8;
        int col1 = ((col+8)+check[1])%8;
        int row2 = ((row+8)+check[2])%8;
        int col2 = ((col+8)+check[3])%8;
        */
        int row1 = ((row+boardSize)+check[0])%boardSize;
        int col1 = ((col+boardSize)+check[1])%boardSize;
        int row2 = ((row+boardSize)+check[2])%boardSize;
        int col2 = ((col+boardSize)+check[3])%boardSize;

        if (gameGrid[row1][col1] == null || gameGrid[row2][col2] == null )
        {
            return false;
        }

        if (gameGrid[row1][col1].player == currentPlayer && gameGrid[row2][col2].player == currentPlayer){
            loseGrid = new int[]{row1, col1, row2, col2, row, col};
            return true;
        }

        else{
            return false;
        }
    }

    public void timesUp(MidiOutputDevice device) {
        gameOver = true;
        for (LaunchButton[] buttonRow : gameGrid) {
            for (LaunchButton button : buttonRow){
                if (button != null && button.player == currentPlayer){
                    device.sendMidiNoteOn(0,0,button.note,11);
                }
        }
        }

    }
}
