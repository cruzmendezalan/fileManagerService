package com.actia.copiaE1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupIntentReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
//		Intent serviceIntent = new Intent();
//		serviceIntent.setAction("com.wissen.startatboot.DownMovService");
//		context.startService(serviceIntent);
		/*Intent i = new Intent(context, MyService.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);*/
		
		Intent startServiceIntent = new Intent(context, MyService.class);
		context.startService(startServiceIntent);

	}
}
