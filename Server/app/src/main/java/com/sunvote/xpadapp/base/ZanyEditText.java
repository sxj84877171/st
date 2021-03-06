package com.sunvote.xpadapp.base;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class ZanyEditText extends EditText {

    private Random r = new Random();

    public ZanyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ZanyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZanyEditText(Context context) {
        super(context);
    }

    public void setRandomBackgroundColor() {
        setBackgroundColor(Color.rgb(r.nextInt(256), r.nextInt(256), r
                .nextInt(256)));
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    private class ZanyInputConnection extends InputConnectionWrapper {

        public ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
             //   ZanyEditText.this.setRandomBackgroundColor();
            	String text = ZanyEditText.this.getText().toString();
            	if(text.length()>0){
            		ZanyEditText.this.setText(text.substring(0, text.length()-1));
            		
            		 
            		CharSequence seqtext = ZanyEditText.this.getText();
            		 if (seqtext instanceof Spannable) {
            		    Spannable spanText = (Spannable)seqtext;
            		    Selection.setSelection(spanText, seqtext.length());
            	  }

            	}
                // Un-comment if you wish to cancel the backspace:
                // return false;
            }
            return super.sendKeyEvent(event);
        }

    }

}
