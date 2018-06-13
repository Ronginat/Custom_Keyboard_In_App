package materailsearchbar.ronapplication.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    MyCustomKeyboard mCustomKeyboard;
    
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mCustomKeyboard = new MyCustomKeyboard(this, R.id.keyboard_view, R.xml.heb_qwerty );

        EditText editText = findViewById(R.id.edittext1);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
                audioManager.playSoundEffect(SoundEffectConstants.CLICK, 1.0f);
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }
        });
        mCustomKeyboard.registerEditText(R.id.edittext0);
    }
    
    @Override public void onBackPressed() { 
    	// NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
        if (mCustomKeyboard.isCustomKeyboardVisible()) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
    }

}

