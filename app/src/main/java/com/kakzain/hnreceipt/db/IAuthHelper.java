package com.kakzain.hnreceipt.db;

import android.content.Context;
import android.net.Uri;

public interface IAuthHelper {
    void signInWithEmailAndPassword(String email, String password, OnSignInCompleteCallback onSignInCompleteCallback);
    void signInWithGoogleAuth(String idToken, OnSignInCompleteCallback onSignInCompleteCallback);
    void signInWithFacebookAuth(String idToken, OnSignInCompleteCallback onSignInCompleteCallback);
    void signUpWithEmailAndPassword(String email, String password, OnSignInCompleteCallback onSignInCompleteCallback);
    String getDisplayName();
    Uri getPhotoUrl();
    String getEmail();
    String getPhoneNumber();
    void signOut(Context context);
    void updateProfil(String newName, String photoUri, OnUpdateProfileCompleteCallback onUpdateCompleteCallback);

    interface OnSignInCompleteCallback {
        void onSuccess(String signInMethod);
        void onFailure(Exception e);
    }

    interface OnUpdateProfileCompleteCallback {
        void onSuccess(Void avoid);
        void onFailure(Exception e);
    }
}
