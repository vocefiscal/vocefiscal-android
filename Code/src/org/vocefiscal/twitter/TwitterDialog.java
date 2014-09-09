package org.vocefiscal.twitter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.vocefiscal.twitter.Twitter_Handler.TwDialogListener;
import org.vocefiscal.R;

public class TwitterDialog extends Dialog {

    static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
    static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
	    ViewGroup.LayoutParams.FILL_PARENT,
	    ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;
    private final String mUrl;
    private final TwDialogListener mListener;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;
    private boolean progressDialogRunning = false;

    public TwitterDialog(Context context, String url, TwDialogListener listener) {
	super(context);

	mUrl = url;
	mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mSpinner = new ProgressDialog(getContext());

	mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
	mSpinner.setMessage("Loading...");

	mContent = new LinearLayout(getContext());

	mContent.setOrientation(LinearLayout.VERTICAL);

	setUpTitle();
	setUpWebView();

	Display display = getWindow().getWindowManager().getDefaultDisplay();
	final float scale = getContext().getResources().getDisplayMetrics().density;
	float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT
		: DIMENSIONS_LANDSCAPE;

	addContentView(mContent, new FrameLayout.LayoutParams(
		(int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1]
			* scale + 0.5f)));
    }

    private void setUpTitle() {
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	Drawable icon = getContext().getResources().getDrawable(
		R.drawable.twitter_icon);

	mTitle = new TextView(getContext());

	mTitle.setText("Twitter");
	mTitle.setTextColor(Color.WHITE);
	mTitle.setTypeface(Typeface.DEFAULT_BOLD);
	mTitle.setBackgroundColor(0xFFbbd7e9);
	mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
	mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
	mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

	mContent.addView(mTitle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView() {
	mWebView = new WebView(getContext());

	mWebView.setVerticalScrollBarEnabled(false);
	mWebView.setHorizontalScrollBarEnabled(false);
	mWebView.setWebViewClient(new TwitterWebViewClient());
	mWebView.getSettings().setJavaScriptEnabled(true);
	mWebView.loadUrl(mUrl);
	mWebView.setLayoutParams(FILL);

	mContent.addView(mWebView);
    }

    private class TwitterWebViewClient extends WebViewClient {

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    if (url.startsWith(Twitter_Handler.CALLBACK_URL)) {
		mListener.onComplete(url);

		TwitterDialog.this.dismiss();

		return true;
	    } else if (url.startsWith("authorize")) {
		return false;
	    }
	    return true;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
		String description, String failingUrl) {
	    super.onReceivedError(view, errorCode, description, failingUrl);
	    mListener.onError(description);
	    TwitterDialog.this.dismiss();
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    super.onPageStarted(view, url, favicon);
	    mSpinner.show();
	    progressDialogRunning = true;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
	    super.onPageFinished(view, url);
	    String title = mWebView.getTitle();
	    if (title != null && title.length() > 0) {
		mTitle.setText(title);
	    }
	    progressDialogRunning = false;
	    mSpinner.dismiss();
	}

    }

    @Override
    protected void onStop() {
	progressDialogRunning = false;
	super.onStop();
    }

    @Override
    public void onBackPressed() {
	if (!progressDialogRunning) {
	    TwitterDialog.this.dismiss();
	}
    }
}
