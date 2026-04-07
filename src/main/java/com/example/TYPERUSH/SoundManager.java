package com.example.TYPERUSH;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundManager {

    private static SoundManager instance;
    private AudioClip correctSound;
    private AudioClip wrongSound;
    private AudioClip finishSound;

    private SoundManager() {
        loadSounds();
    }

    public static SoundManager getInstance() {
        if(instance==null){
            instance=new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            URL cURL=getClass().getResource("sounds/correct.wav");
            if(cURL!=null) correctSound=new AudioClip(cURL.toExternalForm());

            URL wURL=getClass().getResource("sounds/wrong.wav");
            if(wURL!=null) wrongSound=new AudioClip(wURL.toExternalForm());

            URL finURL=getClass().getResource("sounds/finish.wav");
            if(finURL!=null) finishSound=new AudioClip(finURL.toExternalForm());
        }
        catch(Exception e){
            // no need to do anything
        }
    }

    public void playCorrect(){
        if(correctSound!=null){
            correctSound.setVolume(0.4);
            correctSound.play();



        }
    }

    public void playWrong(){
        if(wrongSound!=null){
            wrongSound.setVolume(0.8);
            wrongSound.play();
        }
    }

    public void playFinish() {
        if (finishSound != null) {
            finishSound.setVolume(1.0);     // max volume
         //   finishSound.play();
        }
    }
}