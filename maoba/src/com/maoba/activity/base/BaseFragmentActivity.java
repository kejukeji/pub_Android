package com.maoba.activity.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import com.maoba.R;

public class BaseFragmentActivity extends FragmentActivity {
	protected AlertDialog mAlertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	public void defaultFinish()
	{
		super.finish();
	}
	/**
	 * 跳转activity
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * 跳转activity ，绑定数据
	 * @param pClass
	 * @param pBundle
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}
	/**
	 * 显示toast（时间短）
	 * @param pResId
	 */
	protected void showShortToast(int pResId) {
		showShortToast(getString(pResId));
	}

	/**
	 * 显示toast（时间长）
	 * @param pResId
	 */
	protected void showLongToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 显示toast（时间短）
	 * @param pMsg
	 */
	protected void showShortToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
	}
	private ProgressDialog pd;
	/**
	 * 显示progressDialog
	 */
	protected void showPd(String message){
		if(pd == null){
			pd = new ProgressDialog(this);
		}
		pd.setMessage(message);
		pd.show();
	}
	/**
	 * 显示progressDialog
	 */
	protected void showPd(int msgId){
		if(pd == null){
			pd = new ProgressDialog(this);
		}
		pd.setMessage(getString(msgId));
		pd.show();
	}
	/**
	 * 关闭progressDialog
	 */
	protected void dismissPd(){
		if(pd != null){
			pd.dismiss();
		}
	}
	/**
	 *  显示提醒dialog
	 * @param pTitle
	 * @param pMessage
	 * @param pOkClickListener
	 * @param pCancelClickListener
	 * @param pDismissListener
	 * @return
	 */
	protected AlertDialog showAlertDialog(int pTitle, int pMessage,
			DialogInterface.OnClickListener pOkClickListener, DialogInterface.OnClickListener pCancelClickListener,
			DialogInterface.OnDismissListener pDismissListener) {
		mAlertDialog = new AlertDialog.Builder(this).setTitle(pTitle).setMessage(pMessage)
				.setPositiveButton(android.R.string.ok, pOkClickListener)
				.setNegativeButton(android.R.string.cancel, pCancelClickListener).show();
		if (pDismissListener != null) {
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}
}
