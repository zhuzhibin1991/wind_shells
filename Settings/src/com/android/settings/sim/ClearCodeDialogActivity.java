/******** chusuxia@wind-mobi.com 20160519 add for feature103783 start ********/

package com.android.settings.sim;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import com.android.settings.R;


public class ClearCodeDialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); 
        //showclearCodeDialog("2222");
 
 		showclearCodeDialog("SIN SUBSCRIPCION AL SERVICIO -33-");

    }
    private void showclearCodeDialog(String mString) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder= new AlertDialog.Builder(this);
		//builder.setTitle("dialog");
		builder.setCancelable(false);
		builder.setMessage(mString);
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
				finshMainActivity();
			}
		});
        /*builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finshMainActivity();
            }
        });*/
		Dialog dialog=builder.create();
		dialog.show();
	}
	private void finshMainActivity(){
		finish();
	}
}
/******** chusuxia@wind-mobi.com 20160519 add for feature103783 end ********/