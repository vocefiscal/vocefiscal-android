package org.vocefiscal.twitter;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.vocefiscal.twitter.Twitter_Handler;
import org.vocefiscal.twitter.Twitter_Handler.TwDialogListener;

public class Twitt_Sharing {

	private final Twitter_Handler mTwitter;
	private final Activity activity;
	private String twitt_msg;
	private File image_path;

	public Twitt_Sharing( Activity act, String consumer_key, String consumer_secret ) {
		this.activity = act;
		mTwitter = new Twitter_Handler(activity, consumer_key, consumer_secret);
	}

	public void shareToTwitter(String msg, File Image_url) {
		this.twitt_msg = msg;
		this.image_path = Image_url;
		mTwitter.setListener(mTwLoginDialogListener);

		if (mTwitter.hasAccessToken()) {
			// this will post data in asyn background thread
			showTwittDialog();
		} else {
			mTwitter.authorize();
		}
	}

	private void showTwittDialog() {

		new PostTwittTask().execute(twitt_msg);

	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		@Override
		public void onError(String value) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					showToast("Login Failed");
					mTwitter.resetAccessToken();
				}
			});
		}

		@Override
		public void onComplete(String value) {
			showTwittDialog();
		}
	};

	void showToast(final String msg) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
			}
		});

	}

	class PostTwittTask extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Posting Twitt...");
			pDialog.setCancelable(false);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... twitt) {
			try {
				// mTwitter.updateStatus(twitt[0]);
				// File imgFile = new File("/sdcard/bluetooth/Baby.jpg");

				return Share_Pic_Text_Titter(image_path, twitt_msg, mTwitter.twitterObj);

			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					return "Posting Failed because of Duplicate message...";
				}
				e.printStackTrace();
				return "Posting Failed!!!";
			}

		}

		@Override
		protected void onPostExecute(final String result) {

			super.onPostExecute(result);
			pDialog.dismiss();
			if (null != result && result.equals("success")) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						showToast(result);
					}
				});

			}
		}
	}

	public String Share_Pic_Text_Titter(File image_path, String message, Twitter twitter)
			throws Exception {
		String Status = "";
		try {

			StatusUpdate st = new StatusUpdate(message);
			st.setMedia(image_path);
			twitter.updateStatus(st);
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					Toast.makeText(activity, "Successfully update on Twitter...!",
							Toast.LENGTH_SHORT).show();
				}
			});
			Status = "success";
			/*
			 * Toast.makeText(activity, "Successfully update on Twitter...!",
			 * Toast.LENGTH_SHORT).show();
			 */
		} catch (TwitterException e) {
			Status = "fail";
			Log.v("log_tag", "Pic Upload error" + e);
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(activity, "Ooopss..!!! Failed to update on Twitter.",
							Toast.LENGTH_SHORT).show();
				}
			});

		}
		return Status;
	}

	public void Authorize_UserDetail() {

	}
}
