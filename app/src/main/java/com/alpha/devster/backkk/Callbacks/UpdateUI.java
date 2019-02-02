package com.alpha.devster.backkk.Callbacks;

public class UpdateUI {

    private static boolean exitApp = false;
    private static boolean replay = false;
    private static boolean pause = false;
    private static boolean play = false;
    private static boolean next = false;
    private static boolean prev = false;

    //-------------------------------------------------------------------------- Getters
    public  boolean isReplay() {
        return replay;
    }

    public  boolean isPause() {
        return pause;
    }

    public  boolean isPlay() {
        return play;
    }

    public  boolean isNext() {
        return next;
    }

    public  boolean isPrev() {
        return prev;
    }

    public boolean isExit() {
        return exitApp;
    }

    //-----------------------------------------------------------------------------Setters
    public void ExitApp(boolean exit) {
        UpdateUI.exitApp = exit;
    }

    public void replay(boolean replay) {
        UpdateUI.replay = replay;
    }

    public void pause(boolean pause) {
        UpdateUI.pause = pause;
    }

    public void play(boolean play) {
        UpdateUI.play = play;
    }

    public void next(boolean next) {
        UpdateUI.next = next;
    }

    public void prev(boolean prev) {
        UpdateUI.prev = prev;
    }


    public void setDefault() {
        exitApp = false;
        replay = false;
        pause = false;
        play = false;
        next = false;
        prev = false;
    }

}
