package com.keju.maomao.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.activity.HomeActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.AndroidUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * 铃声和振动服务
 * 
 * @author Zhoujun
 * 
 */
public class RingService extends Service {
	public static BusinessHelper businessHelper;
	private NotificationManager mNM;
	public static final int PUSH_MESSAGE = 100;

	private Handler iNotifyHandler;
	private TimerTask notifyTimerTask;
	private Timer notifyTimer;

	private Handler iMessageHandler;
	private TimerTask messageTimerTask;
	private Timer messageTimer;

	@Override
	public void onCreate() {
		businessHelper = new BusinessHelper();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		initNotifyHandler();
//		startNotifyTask();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNM.cancelAll();

	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (!(SharedPrefUtil.getPlayRing(RingService.this) && SharedPrefUtil.getVibrate(RingService.this))) {
			stopNotifyTimer();
			stopSelf();
		}
	}

	private void initNotifyHandler() {
		iNotifyHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				super.handleMessage(msg);
			}
		};
	}

	private void startNotifyTask() {
		if (notifyTimerTask == null) {
			notifyTimerTask = new TimerTask() {
				@Override
				public void run() {
					try {
						if (NetUtil.checkNet(RingService.this)) {
							int uid = SharedPrefUtil.getUid(RingService.this);
							BusinessHelper businessHelper = new BusinessHelper();
							JSONObject result = businessHelper.getSysLetter1(uid);
							;
							if (result != null) {
								if (result.getInt("status") != Constants.REQUEST_FAILD) {

									int systemMessageCount = result.getInt("system_count");
									int privateMessageCount = result.getInt("direct_count");
									int finalCount = systemMessageCount + privateMessageCount;
									if (finalCount == 0) {
									} else {
										if (SharedPrefUtil.getNewLetter(RingService.this)
												&& SharedPrefUtil.getPlayRing(RingService.this)
												&& SharedPrefUtil.getVibrate(RingService.this)) {
											PlayRing();
											AndroidUtil.Vibrate(RingService.this, 100);
										} else if (SharedPrefUtil.getNewLetter(RingService.this)
												&& SharedPrefUtil.getPlayRing(RingService.this)) {
											PlayRing();
										} else if (SharedPrefUtil.getNewLetter(RingService.this)
												&& SharedPrefUtil.getVibrate(RingService.this)) {
											AndroidUtil.Vibrate(RingService.this, 100);
										} else {

										}
									}
									// Message msg = new Message();
									// msg.what = HANDLER_DATA;
									// msg.obj = result;
									// iLetterHandler.sendMessage(msg);
								}
							} else {

							}
						}
					} catch (Exception e) {
					}
				}
			};
			notifyTimer = new Timer();
			notifyTimer.schedule(notifyTimerTask, 0, 10 * 1000);
		}
	}

	private void stopNotifyTimer() {
		if (notifyTimer != null) {
			notifyTimer.cancel();
			notifyTimer = null;
		}
		if (notifyTimerTask != null) {
			notifyTimerTask = null;
		}
	}

	/**
	 * 播放铃声
	 * 
	 * 
	 */
	private void PlayRing() {
		String ringUrl = SharedPrefUtil.getRingUrl(RingService.this);
		if (ringUrl == null) {

		} else {
			NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			notification.sound = Uri.parse(ringUrl);
			manager.notify(1, notification);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

	/**
	 * 显示通知
	 * 
	 * @param eventBean
	 */
	private void showNotification(int notifyId, String title, String content) {
		// The details of our fake message
		// CharSequence title = createTitle(eventtype);
		// CharSequence content = createContent(eventtype,count);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("notifyId", notifyId);// 消息id
		intent.putExtra("title", title);// 消息内容
		/**
		 * requestCode 这个属性需要不一样，否则的话多个通知会指向相同的intent
		 */
		PendingIntent contentIntent = PendingIntent.getActivity(this, notifyId, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// The ticker text, this uses a formatted string so our message could be
		// localized

		// construct the Notification object.
		Notification notif = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
		// 点击通知后自动从通知栏消失
		notif.flags = Notification.FLAG_AUTO_CANCEL;
		// Set the info for the views that show in the notification panel.
		notif.setLatestEventInfo(this, title, content, contentIntent);

		// after a 100ms delay, vibrate for 250ms, pause for 100 ms and
		// then vibrate for 500ms.
		// notif.vibrate = new long[] { 100, 250, 100, 500 };
		notif.defaults = Notification.DEFAULT_SOUND;
		mNM.notify(notifyId, notif);
	}
}
