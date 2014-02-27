package com.variance.msora.chat;


import android.app.Activity;

public class Messaging extends Activity {

	// private static final int MESSAGE_CANNOT_BE_SENT = 0;
	// private EditText messageText;
	// private EditText messageHistoryText;
	// private Button sendMessageButton;
	// private IAppManager imService;
	// private FriendInfo friend = new FriendInfo();
	//
	// private ServiceConnection mConnection = new ServiceConnection() {
	//
	// public void onServiceConnected(ComponentName className, IBinder service)
	// {
	// imService = ((IMService.IMBinder)service).getService();
	// }
	// public void onServiceDisconnected(ComponentName className) {
	// imService = null;
	// Toast.makeText(Messaging.this, R.string.local_service_stopped,
	// Toast.LENGTH_SHORT).show();
	// }
	// };
	//
	// @Override
	// protected void onCreate(Bundle savedInstanceState)
	// {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	//
	// setContentView(R.layout.messaging_screen); //messaging_screen);
	//
	// messageHistoryText = (EditText) findViewById(R.id.messageHistory);
	//
	// messageText = (EditText) findViewById(R.id.message);
	//
	// messageText.requestFocus();
	//
	// sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
	//
	// Bundle extras = this.getIntent().getExtras();
	//
	// friend.id = extras.getString(FriendInfo.ID);
	// friend.ip = extras.getString(FriendInfo.IP);
	// friend.port = extras.getString(FriendInfo.PORT);
	// String msg = extras.getString(FriendInfo.MESSAGE);
	//
	// setTitle("Messaging with " + friend.id);
	//
	//
	// // EditText friendUserName = (EditText)
	// findViewById(R.id.friendUserName);
	// // friendUserName.setText(friend.userName);
	//
	// if (msg != null)
	// {
	// this.appendToMessageHistory(friend.id , msg);
	// ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friend.id+msg).hashCode());
	// }
	//
	// sendMessageButton.setOnClickListener(new OnClickListener(){
	// CharSequence message;
	// Handler handler = new Handler();
	// public void onClick(View arg0) {
	// message = messageText.getText();
	// if (message.length()>0)
	// {
	// appendToMessageHistory(imService.getUsername(), message.toString());
	//
	// messageText.setText("");
	// Thread thread = new Thread(){
	// public void run() {
	// if (!imService.sendMessage(friend.id, message.toString()))
	// {
	//
	// handler.post(new Runnable(){
	//
	// public void run() {
	// showDialog(MESSAGE_CANNOT_BE_SENT);
	// }
	//
	// });
	// }
	// }
	// };
	// thread.start();
	//
	// }
	//
	// }});
	//
	// messageText.setOnKeyListener(new OnKeyListener(){
	// public boolean onKey(View v, int keyCode, KeyEvent event)
	// {
	// if (keyCode == 66){
	// sendMessageButton.performClick();
	// return true;
	// }
	// return false;
	// }
	//
	//
	// });
	//
	// }
	//
	// @Override
	// protected Dialog onCreateDialog(int id) {
	// int message = -1;
	// switch (id)
	// {
	// case MESSAGE_CANNOT_BE_SENT:
	// message = R.string.message_cannot_be_sent;
	// break;
	// }
	//
	// if (message == -1)
	// {
	// return null;
	// }
	// else
	// {
	// return new AlertDialog.Builder(Messaging.this)
	// .setMessage(message)
	// .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// /* User clicked OK so do some stuff */
	// }
	// })
	// .create();
	// }
	// }
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	// unregisterReceiver(messageReceiver);
	// unbindService(mConnection);
	//
	// FriendController.setActiveFriend(null);
	//
	// }
	//
	// @Override
	// protected void onResume()
	// {
	// super.onResume();
	// bindService(new Intent(Messaging.this, IMService.class), mConnection ,
	// Context.BIND_AUTO_CREATE);
	//
	// IntentFilter i = new IntentFilter();
	// i.addAction(IMService.TAKE_MESSAGE);
	//
	// registerReceiver(messageReceiver, i);
	//
	// FriendController.setActiveFriend(friend.id);
	//
	// }
	//
	//
	// public class MessageReceiver extends BroadcastReceiver {
	//
	// @Override
	// public void onReceive(Context context, Intent intent)
	// {
	// Bundle extra = intent.getExtras();
	// String username = extra.getString(FriendInfo.ID);
	// String message = extra.getString(FriendInfo.MESSAGE);
	//
	// if (username != null && message != null)
	// {
	// if (friend.id.equals(username)) {
	// appendToMessageHistory(username, message);
	// }
	// else {
	// if (message.length() > 15) {
	// message = message.substring(0, 15);
	// }
	// Toast.makeText(Messaging.this, username + " says '"+
	// message + "'",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// }
	//
	// };
	//
	// private MessageReceiver messageReceiver = new MessageReceiver();
	//
	// private void appendToMessageHistory(String username, String message) {
	// if (username != null && message != null) {
	// messageHistoryText.append(username + ":\n");
	// messageHistoryText.append(message + "\n");
	// }
	// }
	//
	//
	//
	//

}
