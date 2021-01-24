package com.kakzain.hnreceipt.db;

import android.content.Context;
import android.net.Uri;

public interface IAuthHelper {
    public void signInWithEmailAndPassword(String email, String password, OnSignInCompleteCallback onSignInCompleteCallback);
    public void signInWithGoogleAuth(String idToken, OnSignInCompleteCallback onSignInCompleteCallback);
    public void signInWithFacebookAuth(String idToken, OnSignInCompleteCallback onSignInCompleteCallback);
    public void signUpWithEmailAndPassword(String email, String password, OnSignInCompleteCallback onSignInCompleteCallback);
    public String getDisplayName();
    public Uri getPhotoUrl();
    public String getEmail();
    public String getPhoneNumber();
    public void signOut(Context context);
    public void updateProfil(String newName, String photoUri, OnUpdateProfileCompleteCallback onUpdateCompleteCallback);

    public interface OnSignInCompleteCallback {
        public void onSuccess(String signInMethod);
        public void onFailure(Exception e);
    }

    public interface OnUpdateProfileCompleteCallback {
        public void onSuccess(Void avoid);
        public void onFailure(Exception e);
    }
}
