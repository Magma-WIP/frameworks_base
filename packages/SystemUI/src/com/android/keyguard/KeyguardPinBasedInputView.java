/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.keyguard;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * A Pin based Keyguard input view
 */
public abstract class KeyguardPinBasedInputView extends KeyguardAbsKeyInputView
        implements View.OnKeyListener, View.OnTouchListener {

    protected PasswordTextView mPasswordEntry;
    private View mOkButton;
    private View mDeleteButton;
    private View mButton0;
    private View mButton1;
    private View mButton2;
    private View mButton3;
    private View mButton4;
    private View mButton5;
    private View mButton6;
    private View mButton7;
    private View mButton8;
    private View mButton9;

    public KeyguardPinBasedInputView(Context context) {
        this(context, null);
    }

    public KeyguardPinBasedInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setButtonVisibility(View button, boolean visible) {
        if (button instanceof View && button != null) {
            button.setVisibility(
                    visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public View getOkButton() {
        return mOkButton;
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        // send focus to the password field
        return mPasswordEntry.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    protected void resetState() {
        setPasswordEntryEnabled(true);
    }

    @Override
    protected void setPasswordEntryEnabled(boolean enabled) {
        mPasswordEntry.setEnabled(enabled);
        mOkButton.setEnabled(enabled);
        if (enabled && !mPasswordEntry.hasFocus()) {
            mPasswordEntry.requestFocus();
        }
    }

    @Override
    protected void setPasswordEntryInputEnabled(boolean enabled) {
        mPasswordEntry.setEnabled(enabled);
        mOkButton.setEnabled(enabled);
        if (enabled && !mPasswordEntry.hasFocus()) {
            mPasswordEntry.requestFocus();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            performClick(mOkButton);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            performClick(mDeleteButton);
            return true;
        }
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            int number = keyCode - KeyEvent.KEYCODE_0;
            performNumberClick(number);
            return true;
        }
        if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
            int number = keyCode - KeyEvent.KEYCODE_NUMPAD_0;
            performNumberClick(number);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getPromptReasonStringRes(int reason) {
        switch (reason) {
            case PROMPT_REASON_RESTART:
                return R.string.kg_prompt_reason_restart_pin;
            case PROMPT_REASON_TIMEOUT:
                return R.string.kg_prompt_reason_timeout_pin;
            case PROMPT_REASON_DEVICE_ADMIN:
                return R.string.kg_prompt_reason_device_admin;
            case PROMPT_REASON_USER_REQUEST:
                return R.string.kg_prompt_reason_user_request;
            case PROMPT_REASON_NONE:
                return 0;
            default:
                return R.string.kg_prompt_reason_timeout_pin;
        }
    }

    private void performClick(View view) {
        view.performClick();
    }

    private void performNumberClick(int number) {
        switch (number) {
            case 0:
                performClick(mButton0);
                break;
            case 1:
                performClick(mButton1);
                break;
            case 2:
                performClick(mButton2);
                break;
            case 3:
                performClick(mButton3);
                break;
            case 4:
                performClick(mButton4);
                break;
            case 5:
                performClick(mButton5);
                break;
            case 6:
                performClick(mButton6);
                break;
            case 7:
                performClick(mButton7);
                break;
            case 8:
                performClick(mButton8);
                break;
            case 9:
                performClick(mButton9);
                break;
        }
    }

    @Override
    protected void resetPasswordText(boolean animate, boolean announce) {
        mPasswordEntry.reset(animate, announce);
    }

    @Override
    protected byte[] getPasswordText() {
        return charSequenceToByteArray(mPasswordEntry.getText());
    }

    @Override
    protected void onFinishInflate() {
        mPasswordEntry = findViewById(getPasswordTextViewId());
        mPasswordEntry.setOnKeyListener(this);

        // Set selected property on so the view can send accessibility events.
        mPasswordEntry.setSelected(true);

        mPasswordEntry.setUserActivityListener(new PasswordTextView.UserActivityListener() {
            @Override
            public void onUserActivity() {
                onUserInput();
            }
        });

        mOkButton = findViewById(R.id.key_enter);
        if (mOkButton != null) {
            mOkButton.setOnTouchListener(this);
            mOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPasswordEntry.isEnabled()) {
                        verifyPasswordAndUnlock();
                    }
                }
            });
            mOkButton.setOnHoverListener(new LiftToActivateListener(getContext()));
        }

        mDeleteButton = findViewById(R.id.delete_button);
        mDeleteButton.setVisibility(View.VISIBLE);
        mDeleteButton.setOnTouchListener(this);
        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // check for time-based lockouts
                if (mPasswordEntry.isEnabled()) {
                    mPasswordEntry.deleteLastChar();
                }
            }
        });
        mDeleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // check for time-based lockouts
                if (mPasswordEntry.isEnabled()) {
                    resetPasswordText(true /* animate */, true /* announce */);
                }
                doHapticKeyClick();
                return true;
            }
        });

        mButton0 = findViewById(R.id.key0);
        mButton1 = findViewById(R.id.key1);
        mButton2 = findViewById(R.id.key2);
        mButton3 = findViewById(R.id.key3);
        mButton4 = findViewById(R.id.key4);
        mButton5 = findViewById(R.id.key5);
        mButton6 = findViewById(R.id.key6);
        mButton7 = findViewById(R.id.key7);
        mButton8 = findViewById(R.id.key8);
        mButton9 = findViewById(R.id.key9);

        mPasswordEntry.requestFocus();
        super.onFinishInflate();
    }

    @Override
    public void onResume(int reason) {
        super.onResume(reason);
        mPasswordEntry.requestFocus();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            doHapticKeyClick();
        }
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    public CharSequence getTitle() {
        return getContext().getString(
                com.android.internal.R.string.keyguard_accessibility_pin_unlock);
    }

    /*
     * This method avoids creating a new string when getting a byte array from EditView#getText().
     */
    private static byte[] charSequenceToByteArray(CharSequence chars) {
        if (chars == null) {
            return null;
        }
        byte[] bytes = new byte[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            bytes[i] = (byte) chars.charAt(i);
        }
        return bytes;
    }
}
