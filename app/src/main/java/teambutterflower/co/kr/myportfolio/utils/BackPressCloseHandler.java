package teambutterflower.co.kr.myportfolio.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by smPark on 2015-03-04.
 */
public class BackPressCloseHandler extends Activity{
    private long backKeyPressedTime = 0;

    private Activity activity;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public BackPressCloseHandler(Activity mActivity){
        this.activity = mActivity;
    }

    public void onBackPressed(Context context){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            showGuide(context, activity);
            //showToast();
        }else {
            activity.finish();
            //toast.cancel();
        }
    }

    private void showGuide(Context context, Activity activity) {
        //new AlertDialog.Builder(activity).setTitle("DialogFragment").create();

        //MyDialog myDialog = new MyDialog(context);
        //myDialog.createdDialog(activity, 0).show(); // 0 END_REVIEW

        mDialog = createDialog();
        mDialog.show();
    }

    private void showToast(){
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }


    private AlertDialog mDialog = null;

    private AlertDialog createDialog()
    {
        AlertDialog.Builder ab = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
        ab.setTitle("안내");
        ab.setMessage("리뷰에 궁금하신 기능과 부분을 남겨주시면\n확인하고 개발방법을 안내해 드리겠습니다. ^^");
        ab.setCancelable(true);
/* // 15.4.22 포기
        ab.setPositiveButton("리뷰쓰기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //해당 앱의 마켓으로 이동.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=MyPortfolio.kr.co.teambutterflower")));
                //MainActivity mainActivity = new MainActivity();
                //mainActivity.market();
                //setDismiss(mDialog);
            }
        });
*/
        ab.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(mDialog);
                activity.finish();
            }
        });

        return ab.create();
    }

    private void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
