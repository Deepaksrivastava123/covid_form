package com.sdbiosensor.covicatch.utils;

import android.widget.EditText;
import android.widget.TextView;

import com.sdbiosensor.covicatch.R;

public class ValidationUtils {

    static String emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.+[a-zA-Z0-9]+";

    public static boolean emailValidation(EditText editText) {
        String emailString = editText.getText().toString().trim();
        if (emailString.matches(emailRegex) && emailString.length() > 0) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(editText.getContext().getString(R.string.error_email_invalid));
            return false;
        }
    }

    public static boolean blankValidation(EditText editText) {
        String emailString = editText.getText().toString().trim();
        if (emailString.length() > 0) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(editText.getContext().getString(R.string.error_text_blank));
            return false;
        }
    }
    public static boolean blankValidation(TextView editText) {
        String emailString = editText.getText().toString().trim();
        if (emailString.length() > 0) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(editText.getContext().getString(R.string.error_text_blank));
            return false;
        }
    }

    public static boolean minLengthValidation(EditText editText, int minLength, String errorMsg) {
        String emailString = editText.getText().toString().trim();
        if (emailString.length() >= minLength) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(errorMsg);
            return false;
        }
    }
}
