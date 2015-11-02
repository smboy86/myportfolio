package teambutterflower.co.kr.myportfolio.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import teambutterflower.co.kr.myportfolio.R;

/**
 * Created by 개개팔육 on 2015-04-22.
 */
public class MyDialog extends Dialog{
    private String exifAttribute;
    Dialog dialog = null;
    private Activity activity;
    private Context context;


    // 케이스 정의
    private static final int END_REVIEW = 0;

    public MyDialog(Context context){
        super(context);
        this.context = context; // 다이얼로그를 띄운 Activity의 context를 받아 셋팅해준다 = 종료를 위해
    }

    protected Dialog createdDialog(Activity mActivity, int id) {
        this.activity = mActivity;
        TextView content;
        dialog = new Dialog(this.context);

        switch (id) {
            case END_REVIEW:
                //requestWindowFeature(Window.FEATURE_NO_TITLE);
                //setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
//                dialog.setTitle("ASDfasdf");
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.dialog_base);
                //dialog.setContentView(R.layout.dialog_base);
                //dialog.setTitle("알림");
                //content = (TextView) dialog.findViewById(R.id.dlgImageName);
                //content.setText(exifAttribute);

                Button okDialogButton = (Button) findViewById(R.id.btnOk);
                okDialogButton.setOnClickListener(okDialogButtonOnClickListener);

                Button reviewDialogButton = (Button) findViewById(R.id.review);
                reviewDialogButton.setOnClickListener(okDialogButtonOnClickListener2);

                /*

                https://play.google.com/store/apps/details?id=teambutterflower.co.kr.MyPortfolio
                WebView webView = (WebView) findViewById(R.id.webPopup);
                webView.setWebViewClient(new myWebViewClient());
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.loadUrl("http://google.com");
                 */

                break;
            default:
                break;
        }
        return dialog;
    }


    private Button.OnClickListener okDialogButtonOnClickListener =
            new Button.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                    activity.finish();
                }
            };

    private Button.OnClickListener okDialogButtonOnClickListener2 =
            new Button.OnClickListener() {
                public void onClick(View v) {
                    //mainActivity.market();
                }
            };
}
