package com.oriku;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Thread;
import java.lang.Exception;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.media.MediaPlayer;
import android.widget.VideoView;
import android.widget.MediaController;
import android.util.Log;

import tv.ouya.console.api.*;
import tv.ouya.console.internal.util.Strings;

public class Bridge {
	public static final int PURCHASE_AUTHENTICATION_ACTIVITY_ID = 1;
	public static final int GAMER_UUID_AUTHENTICATION_ACTIVITY_ID = 2;

	public static Activity gContext;
	public static OuyaFacade ouyaFacade = OuyaFacade.getInstance();
	public static String gamerUUID = "";

	public static void initOuya(Activity context) {
		gContext = context;
		ouyaFacade.init(context, "d5c3b28e-7232-46f4-b0ef-e7951ac85aae");
		OuyaController.init(context);
		fetchGamerUUID();
	}

	public static void fetchGamerUUID() {
		ouyaFacade.requestGamerUuid(new CancelIgnoringOuyaResponseListener<String>() {
			@Override
			public void onSuccess(String result) {
				gamerUUID = result;
				Log.w("EMUyaJ", "Got gamerUUID " + gamerUUID);
			}

			@Override
			public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
				Log.w("EMUyaJ", "fetch gamer UUID error (code " + errorCode + ": " + errorMessage + ")");
				boolean wasHandledByAuthHelper =
				OuyaAuthenticationHelper.handleError(gContext, errorCode, errorMessage, optionalData, GAMER_UUID_AUTHENTICATION_ACTIVITY_ID,
					new OuyaResponseListener<Void>() {
						@Override
						public void onSuccess(Void result) {
							fetchGamerUUID();   // Retry the fetch if the error was handled.
						}

						@Override
						public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
							//showError("Unable to fetch gamer UUID (error " + errorCode + ": " + errorMessage + ")");
							Log.w("EMUyaJ", "Unable to fetch gamer UUID error (code " + errorCode + ": " + errorMessage + ")");
						}

						@Override
						public void onCancel() {
							//showError("Unable to fetch gamer UUID");
							Log.w("EMUyaJ", "Unable to fetch gamer UUID");
						}
					});

				if (!wasHandledByAuthHelper) {
					//showError("Unable to fetch gamer UUID (error " + errorCode + ": " + errorMessage + ")");
					Log.w("EMUyaJ", "Unable to fetch gamer UUID error (code " + errorCode + ": " + errorMessage + ")");
				}
			}
		});
	}

	public static void showKeyboard(Activity context) {
		InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		mgr.showSoftInput(context.getWindow().getDecorView(), InputMethodManager.SHOW_IMPLICIT);
	}

	public static void hideKeyboard(Activity context) {
		InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
	}
}

