/**
 * 
 */
package com.maoba.activity.news;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.maoba.AsyncImageLoader;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.LetterBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.ImageUtil;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;
import com.maoba.util.StringUtil;
import com.maoba.view.PullToRefreshListView;
import com.maoba.view.PullToRefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 私信界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-31 下午2:32:22
 */
public class PrivateLetterActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLift;
	private TextView tvTitle;
	private Button btnRight;

	private PullToRefreshListView lvPersonalLetter;// 聊天的listView

	private ImageView ivEmoticon; // 选择表情
	private EditText edtLetter; // 编辑私信
	private Button btnSend; // 发送私信

	private LetterAdapter letterAdapter;

	private Handler iLetterHandler;
	private TimerTask letterTimerTask;
	private Timer letterTimer;

	private List<LetterBean> letterBeans;

	private int userId;

	// private NewsBean newsbean;// 私信列表传递的bean

	private boolean isLoaded = false;
	private boolean isSend = false;

	private ProgressDialog pd;

	private final static int HANDLER_DATA = 11;

	private int friendId;
	private String nick_Name;

	// 表情
	private ScrollView scrollViewFace;
	private LinearLayout vFace01;
	private LinearLayout vFace02;
	private Map<String, Integer> faceMap = new HashMap<String, Integer>();
	private int[] faceRes = new int[] { R.drawable.ic_face_001, R.drawable.ic_face_002, R.drawable.ic_face_003,
			R.drawable.ic_face_004, R.drawable.ic_face_005, R.drawable.ic_face_006, R.drawable.ic_face_007,
			R.drawable.ic_face_008, R.drawable.ic_face_009, R.drawable.ic_face_010, R.drawable.ic_face_011,
			R.drawable.ic_face_012, R.drawable.ic_face_013, R.drawable.ic_face_014, R.drawable.ic_face_015,
			R.drawable.ic_face_016 };
	int haveFacePic = -1;// 一段对话中是否含有表情图片；是，0；否，-1。

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// newsbean = (NewsBean)
		// getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);
		userId = SharedPrefUtil.getUid(PrivateLetterActivity.this);
		friendId = (int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		nick_Name = (String) getIntent().getExtras().getString("NICK_NAME");
		setContentView(R.layout.private_news_layoute);
		findView();
		fillData();

		for (int i = 0; i < faceRes.length; i++) {
			String j;
			int k = i + 1;
			if (k < 10) {
				j = "00" + k;
			} else if (k < 100) {
				j = "0" + k;
			} else {
				j = "" + k;
			}
			String key = "[edu" + j + "]";
			faceMap.put(key, faceRes[i]);
		}
	}

	private void findView() {
		ibLift = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		lvPersonalLetter = (PullToRefreshListView) this.findViewById(R.id.lvPersonalLetter);
		ivEmoticon = (ImageView) this.findViewById(R.id.ivEmoticon);
		edtLetter = (EditText) this.findViewById(R.id.edtLetter);
		btnSend = (Button) this.findViewById(R.id.btnSend);

		vFace01 = (LinearLayout) this.findViewById(R.id.view_face01);
		vFace02 = (LinearLayout) this.findViewById(R.id.view_face02);

	}

	private void fillData() {
		fillFacePic();// 加载表情 图片

		ibLift.setImageResource(R.drawable.ic_btn_left);
		ibLift.setOnClickListener(this);
		btnRight.setText("资料");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		btnRight.setVisibility(View.GONE);

		tvTitle.setText(nick_Name);

		scrollViewFace = (ScrollView) this.findViewById(R.id.scroll_view_face);
		ivEmoticon.setOnClickListener(this);
		btnSend.setOnClickListener(this);

		// PullService.isCurrActivity = true;

		letterBeans = new ArrayList<LetterBean>();
		letterAdapter = new LetterAdapter();
		lvPersonalLetter.setAdapter(letterAdapter);
		lvPersonalLetter.setonRefreshListener(onRefreshListener);

		if (NetUtil.checkNet(PrivateLetterActivity.this)) {
			new ListLetterTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
		// initNotifyHandler();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.ivEmoticon:// 添加表情
			if (scrollViewFace.getVisibility() == View.VISIBLE) {
				scrollViewFace.setVisibility(View.GONE);
			} else {
				scrollViewFace.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btnRight:
			break;
		case R.id.btnSend:// 发送
			try {
				String letterStr = edtLetter.getText().toString().trim();
				if (!StringUtil.isBlank(letterStr)) {
					if (!isSend) {
						new SendLetter(letterStr).execute();
					} else {
						showShortToast("消息正在发送中...");
					}
				} else {
					showShortToast("请您输入信息");
				}
			} catch (Exception e) {
			}

			break;
		default:
			break;
		}

	}

	/**
	 * 加载表情图片
	 */
	private void fillFacePic() {
		for (int i = 0; i < faceRes.length; i++) {
			View emotionV = getLayoutInflater().inflate(R.layout.private__letter_face_item, null);
			ImageView ivEmotion = (ImageView) emotionV.findViewById(R.id.ivFacePic);
			ivEmotion.setImageResource(faceRes[i]);
			emotionV.setOnClickListener(facePicOnClickListener);
			emotionV.setTag(i + 1);
			if (i < faceRes.length / 2) {
				vFace01.addView(emotionV);
			} else if (i > (faceRes.length / 2) - 1) {
				vFace02.addView(emotionV);
			}

		}
	}

	/**
	 * 表情图片的点击事件
	 */
	OnClickListener facePicOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String j;
			int insertFacePicNum = (Integer) v.getTag();
			if (insertFacePicNum < 10) {
				j = "00" + insertFacePicNum;
			} else if (insertFacePicNum < 100) {
				j = "0" + insertFacePicNum;
			} else {
				j = "" + insertFacePicNum;
			}
			String FaceName = "[edu" + j + "]";
			String editStr = edtLetter.getText().toString();
			String neeEditStr = editStr + FaceName;
			edtLetter.setText(neeEditStr);
			edtLetter.setSelection(neeEditStr.length());
		}
	};

	/**
	 * 上拉刷新数据
	 */
	OnRefreshListener onRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			if (NetUtil.checkNet(PrivateLetterActivity.this)) {
				new ListLetterTask().execute();
			} else {
				showShortToast(R.string.NoSignalException);
			}
		}
	};

	/**
	 * 获取私信聊天数据
	 * 
	 * */
	private class ListLetterTask extends AsyncTask<Void, Void, ResponseBean<LetterBean>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected ResponseBean<LetterBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getLetterList(friendId,userId);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<LetterBean> result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				if (result.getStatus() != Constants.REQUEST_FAILD) {
					List<LetterBean> letterList = result.getObjList();
					if (letterList.size() > 0) {
						letterBeans.addAll(letterList);

						// 安时间排序
						sortNotifyListByTime(letterBeans);
						letterAdapter.notifyDataSetChanged();
						lvPersonalLetter.onRefreshComplete();
						lvPersonalLetter.setSelection(letterBeans.size() - 1);
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
								PrivateLetterActivity.this.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}

				}
			}
		}

	}

	public void sortNotifyListByTime(List<LetterBean> list) {
		Collections.sort(list, new Comparator<LetterBean>() {

			@Override
			public int compare(LetterBean obj1, LetterBean obj2) {
				String time1 = obj1.getSendTime();
				String time2 = obj2.getSendTime();
				if (time1 != null && !"".equals(time1) && time2 != null && !"".equals(time2)) {
					return compareTime(time1, time2);
				} else {
					return 0;
				}
			}
		});
	}

	public int compareTime(String firTimeS, String secTimeS) {
		try {
			SimpleDateFormat chineseSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
			chineseSdf.setTimeZone(timeZone);
			Date firDate = chineseSdf.parse(firTimeS);
			Date secDate = chineseSdf.parse(secTimeS);
			return firDate.compareTo(secDate);
		} catch (ParseException e) {
		}
		return 0;
	}

	/**
	 * 发送私信
	 * 
	 * @author zhouyong
	 * 
	 */
	private class SendLetter extends AsyncTask<Void, Void, JSONObject> {
		private String letterStr;

		/**
		 * @param letterStr
		 */
		public SendLetter(String letterStr) {
			this.letterStr = letterStr;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(PrivateLetterActivity.this);
			}
			pd.setMessage("消息发送中...");
			pd.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().getSendLetter(userId, friendId, letterStr);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {
							showShortToast("发送成功");
							edtLetter.setText("");
							((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
									PrivateLetterActivity.this.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
							if (result.has("sender_list")) {
								JSONArray jsonArray = result.getJSONArray("sender_list");

								JSONObject obj = jsonArray.getJSONObject(0);
								if (obj != null) {
									LetterBean bean = new LetterBean(obj);
									if (bean != null) {
										String sendUrl = null;
										String sendTime = obj.getString("sender_time");
										if(obj.has("receiver_path")){
											 sendUrl = obj.getString("receiver_path");
										}
										letterAdapter = new LetterAdapter(sendTime, sendUrl);
										lvPersonalLetter.setAdapter(letterAdapter);
										letterBeans.add(bean);
										letterAdapter.notifyDataSetChanged();
										lvPersonalLetter.onRefreshComplete();
										lvPersonalLetter.setSelection(letterBeans.size() - 1);
									}
								}
							}
						} else if (status == Constants.REQUEST_FAILD) {
							// showShortToast("时间超时");
							// startActivity(new
							// Intent(PrivateLetterActivity.this,
							// LoginActivity.class).putExtra("back",
							// "back"));
							// finish();
						} else {
							showShortToast(result.getString("error"));
						}
					} catch (Exception e) {
					}
				}
			}
			isSend = false;

		}

	}

	/**
	 * 私信Adapter
	 * 
	 * @author zhouyong
	 * 
	 */

	private class LetterAdapter extends BaseAdapter {
		private String sendTime;
		private String sendUrl;

		/**
		 * @param sendTime
		 */
		public LetterAdapter(String sendTime, String sendUrl) {
			this.sendTime = sendTime;
			this.sendUrl = sendUrl;
		}

		/**
		 * 
		 */
		public LetterAdapter() {
		}

		@Override
		public int getCount() {
			return letterBeans.size();
		}

		@Override
		public Object getItem(int position) {
			return letterBeans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LetterBean bean = letterBeans.get(position);
			// int sender = bean.getSender();
			ViewHolder viewHolder = null;
			if (userId == bean.getSenderId()) {
				convertView = getLayoutInflater().inflate(R.layout.private_letter_right_item, null);
			} else {
				convertView = getLayoutInflater().inflate(R.layout.private_letter_left_item, null);
			}
			if (convertView.getTag() == null) {
				viewHolder = new ViewHolder();
				viewHolder.ivUserPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhoto);
				viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.tvLetter);
				viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tvSendTime);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String contentStr = bean.getContent();
			SpannableString spannableString = null;
			// 判读单个回话列表是文字还是表情头像
			if (!StringUtil.isBlank(contentStr)) {
				boolean isHaveFacePic = contentStr.contains("[edu");
				if (isHaveFacePic == true) {
					if (faceMap != null) {
						spannableString = ImageUtil.changeTextToEmotions(faceMap, contentStr,
								PrivateLetterActivity.this);
					}
				}
			}

			if (spannableString != null) {
				viewHolder.tvLetter.setText(spannableString);
			} else {
				viewHolder.tvLetter.setText(contentStr);
			}

			// viewHolder.tvLetter.setText(bean.getContent());
			if (userId == bean.getSenderId()) {
				viewHolder.tvSendTime.setText(sendTime);
			} else {
				viewHolder.tvSendTime.setText(bean.getSendTime());
			}

			String photoUrl = null;
			if (userId == bean.getSenderId()) {
				photoUrl = BusinessHelper.PIC_BASE_URL + sendUrl;
			} else {
				photoUrl = bean.getFriendUrl();
			}
			viewHolder.ivUserPhoto.setTag(photoUrl);
			Drawable cacheDrawable = AsyncImageLoader.getInstance().loadDrawable(photoUrl, new ImageCallback() {
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView ivPhoto = (ImageView) lvPersonalLetter.findViewWithTag(imageUrl);
					if (ivPhoto != null) {
						if (imageDrawable != null) {

							ivPhoto.setImageDrawable(imageDrawable);
							LetterAdapter.this.notifyDataSetChanged();
						} else {
							ivPhoto.setImageResource(R.drawable.bg_photo_left);
						}
					}
				}
			});
			if (cacheDrawable != null) {
				viewHolder.ivUserPhoto.setImageDrawable(cacheDrawable);
			} else {
				viewHolder.ivUserPhoto.setImageResource(R.drawable.bg_photo_right);
			}
			return convertView;
		}

		class ViewHolder {
			ImageView ivUserPhoto;
			TextView tvLetter;
			TextView tvSendTime;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initNotifyHandler() {
		iLetterHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case HANDLER_DATA:
					List<LetterBean> beans = (List<LetterBean>) msg.obj;
					if (beans != null) {
						letterBeans.addAll(beans);
						sortNotifyListByTime(letterBeans);
						letterAdapter.notifyDataSetChanged();
						lvPersonalLetter.onRefreshComplete();
						if (beans.size() > 0) {
							lvPersonalLetter.setSelection(letterBeans.size() - 1);
						}
					}
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void startNotifyTask() {
		if (letterTimerTask == null) {
			letterTimerTask = new TimerTask() {
				@Override
				public void run() {
					try {
						if (NetUtil.checkNet(PrivateLetterActivity.this)) {
							if (!isLoaded) {
								isLoaded = true;
								BusinessHelper businessHelper = new BusinessHelper();

								ResponseBean<LetterBean> result = businessHelper.getLetterList(userId, friendId);
								if (result != null) {
									if (result.getStatus() != Constants.REQUEST_FAILD) {
										List<LetterBean> letterList = result.getObjList();
										if (letterList.size() > 0) {
											Message msg = new Message();
											msg.what = HANDLER_DATA;
											msg.obj = letterList;
											iLetterHandler.sendMessage(msg);
										}
									}
								} else {
									showShortToast("链接服务器失败");
								}
								isLoaded = false;
							}
						}
					} catch (Exception e) {
					}
				}
			};
			letterTimer = new Timer();
			letterTimer.schedule(letterTimerTask, 0, 2 * 1000);
		}
	}

	private void stopNotifyTimer() {
		if (letterTimer != null) {
			letterTimer.cancel();
			letterTimer = null;
		}
		if (letterTimerTask != null) {
			letterTimerTask = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (NetUtil.checkNet(this)) {
			startNotifyTask();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopNotifyTimer();
		// PullService.isCurrActivity = false;
	}

}
