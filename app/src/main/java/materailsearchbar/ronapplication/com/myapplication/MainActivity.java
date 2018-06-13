package materailsearchbar.ronapplication.com.myapplication;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    MyCustomKeyboard mCustomKeyboard;
    
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mCustomKeyboard = new MyCustomKeyboard(this, R.id.keyboard_view, R.xml.heb_qwerty );

        mCustomKeyboard.registerEditText(R.id.edit_text);

        //final Animation animation = new TranslateAnimation(0.0f,360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    }
    
    @Override public void onBackPressed() { 
    	// NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
        if (mCustomKeyboard.isCustomKeyboardVisible()) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
    }
}

