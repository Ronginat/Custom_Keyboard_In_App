package materailsearchbar.ronapplication.com.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class MyCustomKeyboard implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView mKeyboardView;
    private Activity mHostActivity;
    

    //region Keyboard Action Listener

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
        if( focusCurrent == null || focusCurrent.getClass()!=EditText.class ) return;
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();
        playClick(primaryCode);
        // Apply the key to the edit_text
        switch(primaryCode){
            case KeyCodes.DELETE:
                if (editable != null && start>0) editable.delete(start - 1, start);
                break;
            case Keyboard.KEYCODE_CANCEL:
                hideCustomKeyboard();
                break;
            case Keyboard.KEYCODE_SHIFT:
                break;
            case KeyCodes.LEFT:
                if( start>0 ) edittext.setSelection(start - 1);
                break;
            case KeyCodes.RIGHT:
                if (start < edittext.length()) edittext.setSelection(start + 1);
                break;
            case KeyCodes.LANG_SWITCH:
                //Log.e("Keyboardddd", "language switch");
                break;
            default:
                // insert character
                editable.insert(start, Character.toString((char) primaryCode));
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    //endregion

    //region C'tors

    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id <var>viewid</var>) of the <var>host</var> activity,
     * and load the keyboard layout from xml file <var>layoutid</var> (see {@link Keyboard} for description).
     * Note that the <var>host</var> activity must have a <var>KeyboardView</var> in its layout (typically aligned with the bottom of the activity).
     * Note that the keyboard layout xml file may include key codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the {@link #registerEditText(int)}.
     *
     * @param host The hosting activity.
     * @param viewId The id of the KeyboardView.
     * @param layoutId The id of the xml file containing the keyboard layout.
     */
    public MyCustomKeyboard(Activity host, int viewId, int layoutId) {
        mHostActivity = host;
        mKeyboardView = mHostActivity.findViewById(viewId);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutId));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(this);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mKeyboardView.setTranslationX(0f);
        mKeyboardView.setTranslationY(1500f);
    }

    //endregion

    //region UI Methods

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Make the CustomKeyboard visible, and hide the system keyboard for view v. */
    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null ) ((InputMethodManager)mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void moveViewToScreenCenter(boolean visible) {
        if(!visible) {
            showCustomKeyboard(mKeyboardView);
            mKeyboardView.animate()
                    .translationXBy(0f)
                    .translationYBy(-1500f)

                    .setDuration(1200).start();
        }
        else {
            mKeyboardView.animate()
                    .translationXBy(-4000f)
                    .translationYBy(0f)
                    .rotationBy(1800)
                    .setDuration(500).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideCustomKeyboard();
                    mKeyboardView.setTranslationX(0f);
                    mKeyboardView.setTranslationY(1500f);
                }
            }, 600);
        }
    }

    //endregion

    //region Register Method

    /**
     * Register <var>EditText<var> with resource id <var>resId</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param resId The resource id of the EditText that registers to the custom keyboard.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void registerEditText(int resId) {
        // Find the EditText 'resId'
        final EditText editText= mHostActivity.findViewById(resId);
        // Make the custom keyboard appear
        //final ViewGroup transitionsContainer = mHostActivity.findViewById(R.id.transitions_container);

        editText.setOnClickListener(new View.OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override public void onClick(View v) {
                boolean visible = isCustomKeyboardVisible();
                moveViewToScreenCenter(visible);
            }
        });

        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'editText.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'editText.setCursorVisible(true)' doesn't work )
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        editText.setInputType(editText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        //moveViewToScreenCenter(true);
    }


    //endregion

    //region Sound

    private void playClick(int keyCode){
        AudioManager audioManager = (AudioManager) mHostActivity.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.playSoundEffect(SoundEffectConstants.CLICK, 1.0f);
        Vibrator vibrator = (Vibrator) mHostActivity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(30);

        switch(keyCode){
            case 32:
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR, 1.0f);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN, 1.0f);
                break;
            case Keyboard.KEYCODE_DELETE:
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE, 1.0f);
                break;
            case KeyCodes.LEFT:
                audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_LEFT, 1.0f);
                break;
            case KeyCodes.RIGHT:
                audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_RIGHT, 1.0f);
                break;
            default: audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f);
        }
        /*
        switch(keyCode){
            case 32:
                Log.e("Keyboarddd", "before play space");
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
        }
        */
    }

    //endregion
}
