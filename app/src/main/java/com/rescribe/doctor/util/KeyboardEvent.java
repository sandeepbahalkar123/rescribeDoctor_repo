package com.rescribe.doctor.util;

import android.animation.LayoutTransition;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class KeyboardEvent {

    public KeyboardEvent(RelativeLayout mainRelativeLayout, final KeyboardListener keyboardListener) {
        mainRelativeLayout.setLayoutTransition(new LayoutTransition());
        mainRelativeLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, final int newBottom, int oldLeft, int oldTop, int oldRight, final int oldBottom) {
                if (getDifference(oldBottom, newBottom) > 100) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (oldBottom < newBottom)
                                keyboardListener.onKeyboardClose();
                            else keyboardListener.onKeyboardOpen();
                        }
                    }, 1);
                }
            }
        });
    }

    private int getDifference(int oldBottom, int newBottom) {
        return oldBottom > newBottom ? oldBottom - newBottom : newBottom - oldBottom;
    }

    public interface KeyboardListener {
        void onKeyboardOpen();

        void onKeyboardClose();
    }
}
