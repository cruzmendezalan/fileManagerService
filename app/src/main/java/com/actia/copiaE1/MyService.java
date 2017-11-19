package com.actia.copiaE1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StatFs;
import android.util.Log;
import android.util.Xml;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyService extends Service {
	private Timer timer;
	public String UDPserver = "";
	public String miip = "";
	public static String ipserver = "";
	public int portnumber = 0;
	public boolean resConect = false;
	private static WifiManager wifiManager;
	String[] Clientes = null;
	final Handler myHandler = new Handler();
	
	boolean isrunning=false;
	
	int contador = 0;
	int elpidwget = 0;
	
	public int intcont = 0;
	public int ContWget = 0;
	public int desc = 0;
	
	String[] nombres;
    String[] destino;
	
	//static File rootDir = Environment.getExternalStorageDirectory();
    
    static String rootDir = "/mnt/extsd/";
    String USBrootDir = ""; //"/mnt/usbhost0/";
	
	private String filecontrl = "todownload.txt";
	private String borrapelic = "borrapeliculas.txt";
	private String listapelic = "peliculas.txt";

	private final String LOG_TAG = "E1CopiaUSB-V4";
	
	int id = 0;
	
	public int NOTIFICATION_ID = 1010;
	public int XMLNOTIFIC_ID = 1011;
	public int NOTIFICACION = 1012;
	public int MY_NOTIFICATION_ID = 1013;
	
	
	String datosxml = "";
	
	int numass = 0;
	int numpos = 0;
	int numsigtac = 0;
	String macadr ="";
	String numbus ="";
	boolean allfiles=false;
	
	int cont_downs=0;
	
	String VAR_pathToOurFile = "" ;
	String VAR_urlServer = "";
	
	File sigtac = null;
	
	File tacmitad = null;
	boolean esmitad=false;
	boolean esfinal=false;
	File tacfinal = null;
	
	boolean NextMovie=true;
	
	File killdown = null; 
	File killwget = null; 
	
	int cont_global=0;
    int numitems=0;
    private long enqueue;
    private DownloadManager dm;
    
    //final File mixml = new File("/mnt/storage/actia/tactil_info.xml");
    File mixml = null; // new File(rootDir.toString() + "/tactil_info.xml");
    
    String namefilesdown[];
    String namefilesdel[];
    int leetxts=0;
	int cont_gral=0;

	
	static File f;
	static FileWriter fw;
	static String mipath="";
	static boolean continuar=false;
	
	NotificationManager nm;
	Notification notif;
    
    
	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}

	@Override
	public void onCreate() {
		super.onCreate();

		Toast.makeText(this, "Service CopiaE1 Created", Toast.LENGTH_LONG).show();
		id = android.os.Process.myPid();
		
		
		Notification note = new Notification(0, null, System.currentTimeMillis());
		note.flags |= Notification.FLAG_NO_CLEAR;
		
		startForeground(id, note);
		
		
		Inicio();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_STICKY;
    }
	
	void handleStart(Intent intent, int startId) {
        // do work
		Toast.makeText(this, "Copia Service Started", Toast.LENGTH_LONG).show();
		Log.d(LOG_TAG, "Copia Service Started");
		Log.d(LOG_TAG, "Copia Service Started");
		Log.d(LOG_TAG, "Copia Service Started");
	}

	private void initPost(String pathToOurFile, String urlServer) {
    	
    	HttpURLConnection connection = null;
    	DataOutputStream outputStream = null;
    	DataInputStream inputStream = null;

    	//String pathToOurFile = "/data/file_to_send.mp3";
    	//String urlServer = "http://192.168.1.1/handle_upload.php";
    	String lineEnd = "\r\n";
    	String twoHyphens = "--";
    	String boundary =  "*****";

    	int bytesRead, bytesAvailable, bufferSize;
    	byte[] buffer;
    	int maxBufferSize = 1*1024*1024;

    	try {
	    	FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
	
	    	URL url = new URL(urlServer);
	    	connection = (HttpURLConnection) url.openConnection();
	
	    	// Allow Inputs & Outputs
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	connection.setUseCaches(false);
	
	    	// Enable POST method
	    	connection.setRequestMethod("POST");
	
	    	connection.setRequestProperty("Connection", "Keep-Alive");
	    	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
	    	outputStream = new DataOutputStream( connection.getOutputStream() );
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
	    	outputStream.writeBytes(lineEnd);
	
	    	bytesAvailable = fileInputStream.available();
	    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	buffer = new byte[bufferSize];
	
	    	// Read file
	    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
	    	while (bytesRead > 0) {
		    	outputStream.write(buffer, 0, bufferSize);
		    	bytesAvailable = fileInputStream.available();
		    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	}
	
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	    	// Responses from the server (code and message)
	    	int serverResponseCode = connection.getResponseCode();
	    	String serverResponseMessage = connection.getResponseMessage();
	
	    	fileInputStream.close();
	    	outputStream.flush();
	    	outputStream.close();
	    	//Toast.makeText(this , "Se envio el Archivo: " + pathToOurFile , 8).show();
	    	//this.finish();
    	} catch (Exception ex){
    	//Exception handling
    		//Toast.makeText(this , ex.getMessage(), 5).show();
    	}

    }

	private void triggerXMLNotification(String msns){
    	
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.xml16, "Informacion", System.currentTimeMillis());
        
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setImageViewResource(R.id.img_notification, R.drawable.xml48);
        contentView.setTextViewText(R.id.txt_notification, msns + "el archivo /actia/tactil_info.xml ");
        
        notification.contentView = contentView;
        
        Intent notificationIntent = new Intent(this, MyService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;
        
        notificationManager.notify(XMLNOTIFIC_ID, notification);
        
        //NOTIFICATION_ID = NOTIFICATION_ID+1;
    }
	
	private void triggerSpaceNotification(String msns){
    	
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.sdcard16gb_32, "Informacion", System.currentTimeMillis());
        
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setImageViewResource(R.id.img_notification, R.drawable.sdcard16gb_48);
        contentView.setTextViewText(R.id.txt_notification, msns);
        
        notification.contentView = contentView;
        
        Intent notificationIntent = new Intent(this, MyService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;
        
        notificationManager.notify(XMLNOTIFIC_ID, notification);
        
        //NOTIFICATION_ID = NOTIFICATION_ID+1;
    }
	
	private void triggerNotification(String movie){
		try {
			
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(NOTIFICATION_ID);
			
			
			NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	        Notification notification = new Notification(android.R.drawable.stat_notify_sync, "¡Descargando Peliculas!", System.currentTimeMillis());
	        
	        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
	        contentView.setImageViewResource(R.id.img_notification, android.R.drawable.stat_notify_sync);
	        contentView.setTextViewText(R.id.txt_notification, "Pelicula: " + movie);
	        
	        notification.contentView = contentView;
	        
	        Intent notificationIntent = new Intent(this, MyService.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	        notification.contentIntent = contentIntent;
	        
	        notificationManager.notify(NOTIFICATION_ID, notification);
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("triggerNotification", "Severo Error " + e.toString());
		}
    	
        
        
        //NOTIFICATION_ID = NOTIFICATION_ID+1;
    }
	
	private void removeNotification() {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTIFICATION_ID);
	}

	private void remueveXMLNotification() {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(XMLNOTIFIC_ID);
	}
	
	private void Inicio(){

        timer=new Timer();
        timer.schedule(new TimerTask(){
        	@Override
            public void run() {
                // TODO Auto-generated method stub
        		int mivalor=0;
        		
        		boolean existe=false;
        		
        		if(cont_gral==0){
        			Log.d(LOG_TAG, "Para darle al menos 1 minuto a E1 que monte bien la SDcard");
        			cont_gral=1;
        			return;
        		}
        		
        		Log.d(LOG_TAG, "Corriendo...");
        		cont_global=cont_global+1;
        		Log.d(LOG_TAG, "Contador Global = " + cont_global);
        		
        		File contenido0 = new File("/mnt/usbhost0/contenido.txt");
        		File contenido1 = new File("/mnt/usbhost1/contenido.txt");
        		File contenido2 = new File("/mnt/usbhost2/contenido.txt");
        		
        		if (contenido0.exists()){
        			USBrootDir="/mnt/usbhost0/";
        			Log.d(LOG_TAG, "USBrootDir " + USBrootDir);
        			contenido0.delete();
        			existe = true;
        		} else {
        			if (contenido1.exists()){
            			USBrootDir="/mnt/usbhost1/";
            			Log.d(LOG_TAG, "USBrootDir " + USBrootDir);
            			contenido1.delete();
            			existe = true;
            		} else {
            			if (contenido2.exists()){
                			USBrootDir="/mnt/usbhost2/";
                			Log.d(LOG_TAG, "USBrootDir " + USBrootDir);
                			contenido2.delete();
                			existe=true;
                		} 
    					
    				}
					
				}
        		
        		
        		if (existe==false){
        			Log.d(LOG_TAG, "No Existe contenido.txt");
        			Log.d(LOG_TAG, "No Existe contenido.txt");
        			Log.d(LOG_TAG, "No Existe contenido.txt");
        			return;
        		}
        		
        		File inicioupdate = new File(rootDir + "inicioupdate.txt");
        		if(!inicioupdate.exists()){
        			try {
        				inicioupdate.createNewFile();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		
        		File borrapub = new File(USBrootDir + "borrapub.txt");
        		File newbanners = new File(USBrootDir + "newbanners.txt");
        		File newpromov = new File(USBrootDir + "newpromov.txt");
        		File newpub = new File(USBrootDir + "newpub.txt");
        		File pubunica = new File(USBrootDir + "pubunica.txt");
        		
        		File peliculas = new File(USBrootDir + "peliculas.txt");
        		File borrapeliculas = new File(USBrootDir + "borrapeliculas.txt");
        		
        		File usbbanners = new File(USBrootDir + "banners");
        		File usbpromosmovies=new File(USBrootDir + "promosmovies");
        		File usbimg = new File(USBrootDir + "img");
        		File usbpromos = new File(USBrootDir + "promos");
        		File usbvideo = new File(USBrootDir + "peliculas/video");
        		
        		File E1img = new File(rootDir + "img");
        		File E1promos = new File(rootDir + "promos");
        		File E1video = new File(rootDir + "video");
        		
        		if(newbanners.exists()){
        			newbanners.delete();
        			Log.i(LOG_TAG, " File newbanners.txt erased");
                	File banners = new File(rootDir + "banners");
        			deleteNon_EmptyDir(banners);
        			//crear carpeta banners
        			checkAndCreateDirectory("banners");
        			try {
						FileUtils.copyDirectory(usbbanners, banners);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//showNotification();
					//creaok();
					mivalor=1;
        		}
        		
        		if (newpromov.exists()){
        			newpromov.delete();
        			Log.i(LOG_TAG, " File newpromov.txt erased");
        			File promosmoviesE1 = new File(rootDir + "promosmovies");
        			deleteNon_EmptyDir(promosmoviesE1);
        			checkAndCreateDirectory("promosmovies");
        			try {
						FileUtils.copyDirectory(usbpromosmovies, promosmoviesE1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//showNotification();
					//creaok();
					mivalor=1;
        		}
        		
        		if (newpub.exists()){
        			newpub.delete();
        			Log.i(LOG_TAG, "File newpub.txt erased");
        			File imgE1 = new File(rootDir + "img");
        			File promosE1 = new File(rootDir + "promos");
        			deleteNon_EmptyDir(imgE1);
        			deleteNon_EmptyDir(promosE1);
        			checkAndCreateDirectory("img");
        			checkAndCreateDirectory("promos");
        			try {
						FileUtils.copyDirectory(usbimg, imgE1 );
						FileUtils.copyDirectory(usbpromos, promosE1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//showNotification();
					//creaok();
					mivalor=1;
        		}
        		
        		if (pubunica.exists()){
        			pubunica.delete();
        			Log.i(LOG_TAG, "File pubunica.txt erased");
        			List<File> pngs = (List<File>) FileUtils.listFiles(usbimg,TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        			for(File file : pngs){
        				try {
							FileUtils.copyFileToDirectory(file, E1img);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}
        			
        			List<File> promos = (List<File>) FileUtils.listFiles(usbpromos,TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        			for(File file : promos){
        				try {
							FileUtils.copyFileToDirectory(file, E1promos);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}
        			//showNotification();
        			//creaok();
        			mivalor=1;
        		}
        		
        		if (borrapub.exists()){
        			try {
                		Log.e(LOG_TAG, "Borrar Archivos");

                		FileInputStream fileIS = new FileInputStream(borrapub);
                		BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS),8192);
                		String readString = new String();
                		
                		File filepub;
                		boolean success3;

                		//just reading each line and pass it on the debugger
                		while((readString = buf.readLine())!= null) {
                			Log.e(LOG_TAG, readString);
                			filepub = new File(rootDir + readString);
                			Log.e(LOG_TAG, "Contenido a Borrar INI4");
                			success3 = filepub.delete();

                			if (!success3) {
                				Log.e(LOG_TAG, "No Existe Archivo: " + readString);
                			}
                			else {
                				Log.i(LOG_TAG, "Archivo borrado: " + readString);
                			}
                		}
                		borrapub.delete();
                		Log.i(LOG_TAG, "File borrapub.txt erased");
                		Log.i(LOG_TAG, "Listo!!!");
                		//showNotification();
                		//creaok();
                		mivalor=1;
                   
                	} catch (FileNotFoundException e) {
                		e.printStackTrace();
                		Log.e(LOG_TAG, e.getMessage());
                	} catch (IOException e){
                		e.printStackTrace();
                		Log.e(LOG_TAG, e.getMessage());
                	}

        		}
        		
        		if (borrapeliculas.exists() && peliculas.exists()){
        			
        			try {
                		Log.e(LOG_TAG, "Borrar Peliculas");

                		FileInputStream fileIS = new FileInputStream(borrapeliculas);
                		BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS),8192);
                		String readString = new String();
                		
                		File filepel;
                		boolean success3;
                		
                		int i=0;
                		
                		String destino="";
                		
                		List<File> mp4s = (List<File>) FileUtils.listFiles(usbvideo,TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                		
                		for (int j=0;j<mp4s.size();j++){
                			Log.d(LOG_TAG, mp4s.get(j).toString());
                		}
                			

                		//just reading each line and pass it on the debugger
                		while((readString = buf.readLine())!= null) {
                			Log.e(LOG_TAG, readString);
                			if (readString.contains("NOBORRAR")){
                				//deleteNon_EmptyDir(E1video);
                				//FileUtils.copyDirectory(usbvideo, E1video );
                				for (int k=0;k<mp4s.size();k++){
                        			Log.d(LOG_TAG, "Copiando ... " + mp4s.get(k).toString());
                        			Log.d(LOG_TAG, "Valor de Contador=  " + k);
                        			FileUtils.copyFileToDirectory(mp4s.get(k), E1video);
                        		}
                			} else {
                				
                				
                				if(!readString.contains(".")){
                    	    		//checkAndCreateDirectory("/video/"+ str); //crea la carpeta si no existe
                    	    		Log.d(LOG_TAG, "Folder Destino " + readString);
                    	    		destino= readString +"/";
                	    		}
                    	    	if(readString.contains(".")){
                    	    		
                    				filepel = new File(rootDir + destino + readString);
                        			Log.e(LOG_TAG, "Pelicula a Borrar " + rootDir + destino + readString);
                        			success3 = filepel.delete();

                        			if (!success3) {
                        				Log.e(LOG_TAG, "No Existe Archivo: " + readString);
                        			}
                        			else {
                        				Log.i(LOG_TAG, "Archivo borrado: " + readString);
                        			}
                        			
                        			try {
            							FileUtils.copyFileToDirectory(mp4s.get(i), E1video);
            							i=i++;
            						} catch (IOException e) {
            							// TODO Auto-generated catch block
            							e.printStackTrace();
            						}

                    	    		
                    	    	}
                				
							}
                			
                		}
                		
                		borrapeliculas.delete();
                		Log.i(LOG_TAG, "File borrapeliculas.txt erased");
                		peliculas.delete();
                		Log.i(LOG_TAG, "File peliculas.txt erased");
                		Log.i(LOG_TAG, "Listo!!!");
                		//showNotification();
                		//creaok();
                		mivalor=1;
                   
                	} catch (FileNotFoundException e) {
                		e.printStackTrace();
                		Log.e(LOG_TAG, e.getMessage());
                	} catch (IOException e){
                		e.printStackTrace();
                		Log.e(LOG_TAG, e.getMessage());
                	}
        			
        		}
        		
        		if (mivalor==1){
        			showNotification();
    				creaok();
        			mivalor=0;
        		}
             	
            }
        }, 0, 60000); //300000
	}
	
	private void creaok(){
		File updateok = new File(rootDir + "updateok.txt");
		if(!updateok.exists()){
			try {
				updateok.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void showNotification() {
//		Context context = getApplicationContext();
//	    Intent myIntent = new Intent(Intent.ACTION_VIEW);
//	    PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
//	      
//	    notif = new Notification.Builder(context)
//	          .setContentTitle("Notificacion!")
//	          .setContentText("Listo puede retirar su memoria USB")
//	          .setTicker("Notificacion!")
//	          .setWhen(System.currentTimeMillis())
//	          .setContentIntent(pendingIntent)
//	          .setDefaults(Notification.DEFAULT_SOUND)
//	          .setAutoCancel(true)
//	          .setSmallIcon(R.drawable.ok)
//	          .build();
//	    
//	    nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//	    nm.notify(MY_NOTIFICATION_ID, notif);
		
	try {
	    
	    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTIFICATION_ID);
		
		
		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(android.R.drawable.stat_notify_sync, "¡Notificacion!", System.currentTimeMillis());
        
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setImageViewResource(R.id.img_notification, android.R.drawable.stat_notify_sync);
        contentView.setTextViewText(R.id.txt_notification, "Puede desconectar su Memoria USB");
        
        notification.contentView = contentView;
        
        Intent notificationIntent = new Intent(this, MyService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;
        
        notificationManager.notify(NOTIFICATION_ID, notification);
		
	} catch (Exception e) {
		// TODO: handle exception
		Log.d("triggerNotification", "Severo Error " + e.toString());
	}
	    
	    
	}
	
	public static boolean deleteNon_EmptyDir(File dir) {
     	if (dir.isDirectory()) {
     		String[] children = dir.list();
     		for (int i=0; i<children.length; i++) {
     			boolean success = deleteNon_EmptyDir(new File(dir, children[i]));
     			if (!success) {
     				return false;
     			}
     		}
     	}
     	return dir.delete();
     }
	
	public void vercontenido(){
		try {
			File rootvideo = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/video");
			directorios(rootvideo);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("vercontenido", "Hubo un error al intentar leer el contenido");
		}
		
	}
	
	public void borrapeliculas(int idx){
		Log.d("borrapeliculas","Dentro de borrapeliculas...");
		if(namefilesdel.length>idx){
			if(namefilesdel[idx].contains(".mp4")){
				//File delfile=new File(rootDir.toString() +"/video/"+ namefilesdel[idx]);
				File delfile=new File(rootDir.toString() + "/" + namefilesdel[idx]);
				delfile.delete();
				Log.d("borrapeliculas","Se borro: " + namefilesdel[idx]);
				//String moviestxtloc=rootDir.toString() + "/video/" + namefilesdel[idx].substring(0, namefilesdel[idx].lastIndexOf("/"))+"/movies.txt";
				String moviestxtloc=rootDir.toString() + "/" + namefilesdel[idx].substring(0, namefilesdel[idx].lastIndexOf("/"))+"/movies.txt";
				Log.d("borralinea","El archivo que debe borrar una linea esta en " + moviestxtloc);
				String lineatodel=namefilesdel[idx].substring(namefilesdel[idx].lastIndexOf("/")+1,namefilesdel[idx].lastIndexOf(".")) ;
				Log.d("borralinea","Se borro linea " + lineatodel + " de movies.txt " );
				removeLineFromFile(moviestxtloc,lineatodel);
			}
			if(namefilesdel[idx].contains(".png")){
				//File delfile=new File(rootDir.toString() + "/video/"+ namefilesdel[idx]);
				File delfile=new File(rootDir.toString() + "/" + namefilesdel[idx]);
				delfile.delete();
				Log.d("borrapeliculas","Se borro: " + namefilesdel[idx]);
			}
			if(namefilesdel[idx].contains(".txt")){
				//File delfile=new File(rootDir.toString() +"/video/"+ namefilesdel[idx]);
				File delfile=new File(rootDir.toString() + "/" + namefilesdel[idx]);
				delfile.delete();
				Log.d("borrapeliculas","Se borro: " + namefilesdel[idx]);
			}
		}
	}
	
//	public void borrapeliculas(int idx){
//		Log.d("borrapeliculas","Dentro de borrapeliculas...");
//		if(namefilesdel.length>idx){
//			if(namefilesdel[idx].contains(".mp4")){
//				File delfile=new File(rootDir.toString() +"/video/"+ namefilesdel[idx]);
//				delfile.delete();
//				Log.d("borrapeliculas","Se borro: " + namefilesdel[idx]);
//				String moviestxtloc=rootDir.toString() +"/video/"+ namefilesdel[idx].substring(0, namefilesdel[idx].lastIndexOf("/"))+"/movies.txt";
//				Log.d("borralinea","El archivo que debe borrar una linea esta en " + moviestxtloc);
//				String lineatodel=namefilesdel[idx].substring(namefilesdel[idx].lastIndexOf("/")+1,namefilesdel[idx].lastIndexOf(".")) ;
//				Log.d("borralinea","Se borro linea " + lineatodel + " de movies.txt " );
//				removeLineFromFile(moviestxtloc,lineatodel);
//			}
//			if(namefilesdel[idx].contains(".png")){
//				File delfile=new File(rootDir.toString() +"/video/"+ namefilesdel[idx]);
//				delfile.delete();
//				Log.d("borrapeliculas","Se borro: " + namefilesdel[idx]);
//			}
//			if(namefilesdel[idx].contains(".txt")){
//				File delfile=new File(rootDir.toString() +"/video/"+ namefilesdel[idx]);
//				delfile.delete();
//				Log.d("borrapeliculas","Se borro: " + namefilesdel[idx]);
//			}
//		}
//	}
	
	public void leetodownloadtxt(){
		//boolean val=false;
		try {
			Log.d("leetodownloadtxt", "DENTRO DE leetodownloadtxt");
			
			File todownloadtxt = new File(rootDir.toString() + "/" + filecontrl);
    		if (todownloadtxt.exists() && todownloadtxt.length()>0 ){
	    		BufferedReader br = new BufferedReader(new FileReader(todownloadtxt),8192);
	    		String linea;
	    		String peliculatest=""; 
	    		Boolean borrar=false;
	    		
	    		while((linea=br.readLine())!=null) {
	    			String fileandsize[] = linea.split(" ");
	    			Log.d("LINEA", linea);
	    			Log.d("PELICULA", fileandsize[0]);
	    			Log.d("TAMANO", fileandsize[1]);

	    			String[] divide = fileandsize[0].split("/");
	    			Log.d("HTTP", divide[0]);
	    			Log.d("NULO", divide[1]);
	    			Log.d("IP", divide[2]);
	    			Log.d("FOLDER", divide[3]);
	    			Log.d("GENERO", divide[4]); //Genero de la pelicula
	    			Log.d("NOMBRE", divide[5]); //Nombre de la pelicula
	    			
	    			//String fileincompleto=rootDir.toString() + "/video/" + divide[4] +"/"+divide[5];
	    			String fileincompleto=rootDir.toString() + "/video/" + divide[5] + ".tmp";
	    			
	    			if(divide[5].contains(".mp4")){
	    			
		    			File filepart=new File(fileincompleto);
		    			
		    			if (filepart.exists()==true && filepart.length()!=Integer.parseInt(fileandsize[1])){
		    				triggerNotification(divide[5]);
		    				//debe descargar el archivo donde se quedo.
		    				URL url = new URL(fileandsize[0]);
	
		    				URLConnection connection = url.openConnection();
		    				File fileThatExists = new File(fileincompleto); 
		    				OutputStream output = new FileOutputStream(fileincompleto, true);
		    				connection.setRequestProperty("Range", "bytes=" + fileThatExists.length() + "-");
	
		    				connection.connect();
	
		    				int lenghtOfFile = connection.getContentLength();
	
		    				InputStream input = new BufferedInputStream(connection.getInputStream());
		    				byte data[] = new byte[4096];
	
		    				long total = 0;
		    				int count=0;
	
		    				while ((count = input.read(data)) != -1) {
		    				    total += count;
	
		    				    output.write(data, 0 , count);
		    				}
		    				
		    				//renombrar
		    				Log.d("renombrar", "RENOMBRAR ARCHIVOS");
		    			    String original="";
		    			    
		    			    original= divide[5];
		    			    Log.d("ORIGINALFILE", "ARCHIVO ORIGINAL: " + original);
		    			    
		    			    File newfile = new File(rootDir + "/video/" + original);
		    			    //File filetmp = new File(saveto);
		    			    if(filepart.exists()){
		    			    	filepart.renameTo(newfile);
		    			    	Log.d("RENOMBRAR", "SE RENOMBRO CORRECTAMENTE EL ARCHIVO TMP");
		    			    	removeNotification();
		    			    } else {
		    			    	Log.d("RENOMBRAR", "NO SE PUDO RENOMBRAR EL ARCHIVO TMP");
		    				}
		    			}
	    			}
	    		}
    		}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("leetodownloadtxt", "Hubo un severo error: " +e.toString());
		}
	}
	
	
	public boolean leeerarchivos(){
		boolean resultado=false;
		try {
    		File peliculastxt= new File(rootDir.toString() + "/actia/" + listapelic);
    		
    		if(peliculastxt.exists()){
    			Log.d("leeerarchivos","Dentro de leeerarchivos...");
    			BufferedReader in = new BufferedReader(new FileReader(peliculastxt),8192);
	    	    String str;
	    	    String pelicula="";
	    	    String origen="";
	    	    String completa="";
	    	    
	    	    while ((str = in.readLine()) != null) {
    	    		if(!str.contains(".")){
        	    		checkAndCreateDirectory("/video/"+ str); //crea la carpeta si no existe
        	    		//Log.d("CARPETA", "folder Origen " + str);
    	    			origen= str+"/";
    	    		}
        	    	if(str.contains(".")){
        	    		//Log.d("PELICULAS", "pelicula " + str);
        	    		pelicula=str;
        	    		//Log.d("leeerarchivos", origen + pelicula );
        	    		completa=completa+origen+pelicula+",";	    	    		
        	    	}
	    	    }
	    	    in.close();
	    	    Log.d("leeerarchivos", "Se cierra in INSTREAM");
	    	    completa=completa.substring(0, completa.lastIndexOf(","));
	    	    namefilesdown = completa.split(",");
	    	    
	    	    /*for (int i=0;i<namefilesdown.length;i++){
	    	    	Log.d("leeerarchivos", "NOMBRES: "+ namefilesdown[i]);
	    	    }*/
	    	    resultado=true;
    		} else {
    			Log.d("leeerarchivos", "No Existe peliculas.txt, se intentara descargar de nuevo");
    			leetxts=0;
    			resultado=false;
			}
    		
    		File borrapeliculastxt= new File(rootDir.toString() + "/actia/" + borrapelic);
    		
    		if(borrapeliculastxt.exists()){
    			Log.d("leeerarchivos","Dentro de leeerarchivos...");
    			BufferedReader in = new BufferedReader(new FileReader(borrapeliculastxt),8192);
	    	    String str;
	    	    String pelicula="";
	    	    String origen="";
	    	    String completa="";
	    	    
	    	    while ((str = in.readLine()) != null) {
    	    		if(!str.contains(".")){
        	    		//checkAndCreateDirectory("/video/"+ str); //crea la carpeta si no existe
        	    		//Log.d("CARPETA", "folder Origen " + str);
    	    			origen= str+"/";
    	    		}
        	    	if(str.contains(".")){
        	    		//Log.d("PELICULAS", "pelicula " + str);
        	    		pelicula=str;
        	    		//Log.d("leeerarchivos", origen + pelicula );
        	    		completa=completa+origen+pelicula+",";	    	    		
        	    	}
	    	    }
	    	    in.close();
	    	    if(completa.length()==0){
	    	    	completa=completa+origen+pelicula+",";
	    	    }
	    	    Log.d("leeerarchivos", "Se cierra in INSTREAM");
	    	    Log.d("leeerarchivos", "1Cadena Completa: "+completa);
	    	    completa=completa.substring(0, completa.lastIndexOf(","));
	    	    Log.d("leeerarchivos", "2Cadena Completa: "+completa);
	    	    namefilesdel = completa.split(",");
	    	    
	    	    /*for (int i=0;i<namefilesdown.length;i++){
	    	    	Log.d("leeerarchivos", "NOMBRES: "+ namefilesdown[i]);
	    	    }*/
	    	    resultado=true;
    		} else {
    			Log.d("leeerarchivos", "No Existe borrapeliculas.txt, se intentara descargar de nuevo");
    			leetxts=0;
    			resultado=false;
			}
    	} catch (Exception e) {
    		// TODO: handle exception
    		Log.d("runWget", "Error en runWget: " + e.toString());
    		resultado=false;
    	}
		return resultado;
	}
	
	public void descargaManager(String miURL,String saveto){
		String colapeliculas=rootDir.toString()+"/todownload.txt";
	    File todownloadtxt = new File(colapeliculas);
	    int fileSize=0;
	    
	    String tmpurl=miURL;
	    
    	try { // catches IOException below
    		if(todownloadtxt.exists()==false){
    	    	todownloadtxt.createNewFile();
    	    	Log.d("descargaManager", "Se crea: " + colapeliculas);
    	    }
	    	URL url;
    		URLConnection connection;

    		url = new URL(miURL);
    		connection = url.openConnection();
    		fileSize = connection.getContentLength();
    		if(fileSize < 0)
    			Log.d("descargaManager", "Could not determine file size.");
    		else
    			Log.d("descargaManager", miURL + "\nSize: " + fileSize);
    		connection.getInputStream().close();
	    		
			final String infofile = new String(miURL + " " + fileSize);
			BufferedWriter bW = new BufferedWriter(new FileWriter(todownloadtxt,true),8192);
			// Write the string to the file
			bW.write(infofile+"\n");
			bW.flush();
			bW.close();
			Log.d("descargaManager", "Se agrego " + infofile +  " en: " + colapeliculas);
    	    
    	} catch (IOException e) {
    		// TODO: handle exception
    		e.printStackTrace();
    		Log.d("descargaManager", "Error muy severo: " + e.toString());
    	}
		
		try {
			triggerNotification(saveto);
			Log.d("descargaManager", "Link: " + miURL);
			Log.d("descargaManager", "Salvar en: " + saveto);
			URL u = new URL(miURL);
		    HttpURLConnection c = (HttpURLConnection) u.openConnection();
		    c.setRequestMethod("GET");
		    c.setDoOutput(true);
		    c.connect();
		    
		    File miarchivo = new File(saveto);
		    
		    if (!miarchivo.exists())
		    	miarchivo.createNewFile();
		    
		    FileOutputStream f = new FileOutputStream(miarchivo);
		    
		    InputStream in = c.getInputStream();

		    byte[] buffer = new byte[4096];
		    int len1 = 0;
		    while ( (len1 = in.read(buffer)) > 0 ) {
		    	f.write(buffer,0, len1);
		    }
		    f.close();
		    
		    //renombrar
		    String original="";
		    
		    original= tmpurl.substring(28,tmpurl.length());
		    Log.d("ORIGINALFILE", "ARCHIVO ORIGINAL: " + original);
		    
		    File newfile = new File(rootDir + "/video/" + original);
		    //File filetmp = new File(saveto);
		    if(miarchivo.exists()){
		    	miarchivo.renameTo(newfile);
		    	Log.d("RENOMBRAR", "SE RENOMBRO CORRECTAMENTE EL ARCHIVO TMP");
		    	removeNotification();
		    } else {
		    	Log.d("RENOMBRAR", "NO SE PUDO RENOMBRAR EL ARCHIVO TMP");
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("descargaManager", "Error muy severo: " + e.toString());
		}
	}
	
	public void leerXML(){
		FileInputStream fIn;
        XmlPullParser parser = Xml.newPullParser();

		try {
			fIn = new FileInputStream(mixml);
			InputStreamReader isr = new InputStreamReader(fIn);
			
			int valor=0;
			
			// auto-detect the encoding from the stream
		    parser.setInput(isr);
		    int eventType = parser.getEventType();
		    boolean done = false;
		    while (eventType != XmlPullParser.END_DOCUMENT && !done){
		        String name = null;
		        switch (eventType){
		            case XmlPullParser.START_DOCUMENT:
		                break;
		            case XmlPullParser.START_TAG:
		                name = parser.getName();
		                if (name.equalsIgnoreCase("asientos") || name.equalsIgnoreCase("device_id") || name.equalsIgnoreCase("mac")){
		                	Log.d("XMLPARSER", "Encontro Datos");
		                	valor=1;
		                }
		                break;
		            case XmlPullParser.TEXT:
		            	if (valor ==1){
		            		datosxml = datosxml + parser.getText() + ",";
		            		Log.d("XMLPARSER", "Valor: " + parser.getText());
		            		valor=0;
		            	}
	                	break;
		            case XmlPullParser.END_TAG:
		                name = parser.getName();
		                if (name.equalsIgnoreCase("tactil")){
		                    done = true;
		                }
		                break;
		            }
		        eventType = parser.next();
		    }
		    
		    datosxml=datosxml.substring(0, datosxml.lastIndexOf(","));
		    Log.d("XMLPARSER", "Datos XML: " + datosxml);
		    
		    String ArregloXML[] = datosxml.split(",");
		    Log.d("XMLPARSER", "ArregloXML[0]: " + ArregloXML[0]); //numero asientos
		    Log.d("XMLPARSER", "ArregloXML[1]: " + ArregloXML[1]); //device_id
		    Log.d("XMLPARSER", "ArregloXML[2]: " + ArregloXML[2]); //mac
		    
		    numass = Integer.parseInt(ArregloXML[0]);
		    numpos = Integer.parseInt(ArregloXML[1].substring(ArregloXML[1].lastIndexOf("A")+1));
		    numbus = ArregloXML[1].substring(ArregloXML[1].lastIndexOf("U")+1,ArregloXML[1].lastIndexOf("A"));
			macadr = ArregloXML[2];
			
			if(numpos==numass/2){
				Log.d("TACHALF", "Soy el numero de asiento mitad: " + numpos); //numero asientos
				tacmitad = new File(rootDir.toString() + "/endofsegment.hlf");
				if(!tacmitad.exists()){
					try {
						Log.d("TACHALF", "Se crea el archivo mitad"); //mac
						tacmitad.createNewFile();
						esmitad = true;
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
						Log.d("TACHALF", e.toString());
					}
				}
			}
			
			if(numpos==numass){
				Log.d("TACFIN", "Soy el numero de asiento Final: " + numpos); //numero asientos
				tacfinal = new File(rootDir.toString() + "/endofall.all");
				if(!tacfinal.exists()){
					try {
						Log.d("TACFIN", "Se crea el archivo ultimo");
						tacfinal.createNewFile();
						esfinal = true;
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						Log.d("TACFIN", e.toString());
					}
					
				}
			}
			
			Log.d("XMLPARSER", "numass: " + numass); //numero asientos
		    Log.d("XMLPARSER", "numpos: " + numpos); //device_id
		    Log.d("XMLPARSER", "numbus: " + numbus); //bus
		    Log.d("XMLPARSER", "macadr: " + macadr); //mac
		    
		    numsigtac = numpos + 1;
		    
		    Log.d("SIGUIENTE", "numsigtac: " + numsigtac);
		    Log.d("SIGUIENTE", "numsigtac: " + numsigtac);
		    
		    if (numsigtac>numass &&  allfiles == true){
		    	Log.d("ALLFILES", "Se dercargaron todos los archivos");
		    	sigtac = new File(rootDir.toString()+"/ok.tac");
		    } else {
		    	if(numsigtac>numass){
		    		numsigtac=1;
		    	}
		    	Log.d("ALLFILES", "Continuara el siguiente tactil");
		    	sigtac = new File(rootDir.toString()+"/"+ numsigtac + ".tac");
		    	if(!sigtac.exists()){
    		    	try {
    		    		Log.d("TACFILE", "Se crea el siguiente tactil " + sigtac.toString() );
						sigtac.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("CREAFILE", e.toString());
					}
    		    }
		    }
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
		removeNotification();
		
	}
	
	public void ValidaWiFi() {
    	try {
    		//Toast.makeText(getApplicationContext(), "Mensaje Cada 50 segundos", Toast.LENGTH_LONG).show();
    		ConnectivityManager myConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    		Log.i(LOG_TAG, "Paso-1");
        	NetworkInfo myNetworkInfo = myConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	Log.i(LOG_TAG, "Paso-2");
        	WifiManager myWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        	Log.i(LOG_TAG, "Paso-3");
    		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
    		Log.i(LOG_TAG, "Paso-4");
    		Log.e(LOG_TAG, "myWifiInfo.getMacAddress().toString() !!COMMENTED...");
    		String statuscon="";
    		String strip="";
    		String strSSID="";
    		
        	if (myNetworkInfo.isConnected()){
        		Log.i(LOG_TAG, "Ins:10");
        		Log.e(LOG_TAG, "strSSID: " + strSSID);
        		Log.i(LOG_TAG, "Ins:11");
        		//Toast.makeText(getApplicationContext(), statuscon + "\n" + strSSID + "\n" + strip, Toast.LENGTH_LONG).show();
        	}
        	else{
        		//Toast.makeText(getApplicationContext(), "--- DIS-CONNECTED! ---", Toast.LENGTH_LONG).show();
        		Log.e(LOG_TAG, "--- DIS-CONNECTED! ---");
        		wifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
    			//Toast.makeText(getApplicationContext(), "Se apagara WiFi", Toast.LENGTH_LONG).show();
    			//Log.e(LOG_TAG, "Se apagara WiFi");
    			if(wifiManager.isWifiEnabled()==false){
        			//Thread.sleep(5000);
        			//Toast.makeText(getApplicationContext(), "Se habilitara WiFi", Toast.LENGTH_LONG).show();
        			Log.e(LOG_TAG, "Se habilitara WiFi");
        			wifiManager.setWifiEnabled(true);                		
    			}
    			else {
    				Log.e(LOG_TAG, "Se apagara WiFi");
        			Log.e(LOG_TAG, "Se APAGO WiFi");
    				wifiManager.setWifiEnabled(false);
				}
    			//wifiManager.setWifiEnabled(false);
        	}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e(LOG_TAG, e.getMessage());
		}
        
    }
	
	public static boolean ValidaFechaMovieFiles(String cadcompleta,String ipsvr){
		boolean validafecha=false;
		Log.d("ValidaFechaMovieFiles0", "Dentro de ValidaFechaMovieFiles");
		
		String result = "";
    	//InputStream is=null;
    	try{
    	    /*HttpClient httpclient = new DefaultHttpClient();
    	    HttpPost httppost = new HttpPost(ipsvr + "getfilesize.php");      
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity entity = response.getEntity();*/
    		//URL url = new URL("http://192.168.1.2/getfilesize.php");
    		URL url = new URL(ipserver + "getfilesize.php");
    		HttpURLConnection con = (HttpURLConnection) url.openConnection();
    		
    	    //is = entity.getContent();
    	    //BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8192);
    		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()),8192);
    	    String line = "";
    	    while ((line = reader.readLine()) != null) {
    	        result += line;
    	    }
    	    //is.close();
    	    Log.d("DataFromPHP",result);
    	    String[] valores=result.split(",");
    	    
    	    Log.d("ValidaFechaMovieFiles1","Archivos " + cadcompleta);
    	    
    	    String[] separated = cadcompleta.split(",");
    	    
    	    String peliculas="";
    		String borrapel="";
    		borrapel = separated[0].substring(separated[0].lastIndexOf("/")+1);
    	    peliculas = separated[1].substring(separated[1].lastIndexOf("/")+1);
    		
    	    File fdescpel=new File(rootDir.toString() + "/actia/" + peliculas );
			File fpel=new File(rootDir.toString() + "/actia/size" + peliculas );
			
			File fdescborra=new File(rootDir.toString() + "/actia/" + borrapel );
			File fborra=new File(rootDir.toString() + "/actia/size" + borrapel );
        	
			if (fpel.exists() && fborra.exists()) {
	        	try {
	        	    BufferedReader br = new BufferedReader(new FileReader(fpel),8192);
	        	    String lineapel,valpel = null;
	
	        	    while ((lineapel = br.readLine()) != null) {
	        	    	valpel=lineapel;
	        	    	Log.d("ValidaFechaMovieFiles2", "File " + fpel.toString() + " Contents ==> " + valpel);
	        	    	//Toast.makeText(getApplicationContext(),"File Contents ==> " + val,Toast.LENGTH_SHORT).show();
	        	    }
	        	    
	        	    if ( !valpel.contains(valores[0]) ){
	        	    	fdescpel.delete();
	        	    	fpel.delete();
	        	    	validafecha = true;
	        	    } else {
	        	    	Log.d("ValidaFechaMovieFiles3", "Las fechas son iguales No se descargara " + separated[0]);
	        	    	validafecha = false;
					}
	        	    
	        	    BufferedReader br2 = new BufferedReader(new FileReader(fborra),8192);
	        	    String lineaborra,valborra = null;
	
	        	    while ((lineaborra = br2.readLine()) != null) {
	        	    	valborra=lineaborra;
	        	    	Log.d("ValidaFechaMovieFiles4", "File " + fborra.toString() + " Contents ==> " + valborra);
	        	    	//Toast.makeText(getApplicationContext(),"File Contents ==> " + val,Toast.LENGTH_SHORT).show();
	        	    }
	        	    
	        	    if ( !valborra.contains(valores[1]) ){
	        	    	fdescborra.delete();
	        	    	fborra.delete();
	        	    	validafecha = true;
	        	    } else {
	        	    	Log.d("ValidaFechaMovieFiles5", "Las fechas son iguales No se descargara " + separated[1]);
	        	    	validafecha = false;
					}
	        	    
	        	}
	        	catch (IOException e) {
	        	    //You'll need to add proper error handling here
	        		e.printStackTrace();
	        		validafecha = false;
	        		Log.d("ValidaFechaMovieFiles6", e.toString());
	        	}
			} else {
				validafecha = true;
			}
    	}catch(Exception ex){  
    	    Log.e("Error",ex.toString());
    	}
    	
        return validafecha;
	}
	
	public void MovieFiles(String cadcompleta){
		Boolean descargar=false;
		
		try {
         	String[] separated = cadcompleta.split(",");
        	
        	for (int i = 0; i < separated.length; i++) {
        		String nombrearchivo="";

        		nombrearchivo = separated[i].substring(separated[i].lastIndexOf("/")+1);
             	
        		if(existe(separated[i])==false){
        			Log.d("MovieFiles", "Archivo " + separated[i] + " No Existe en el Server!!! " );
        			break;
        		}
        		
        		URL url = new URL(separated[i]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                Integer fileLength = connection.getContentLength();
                Long dt = connection.getLastModified();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),8192);
                OutputStream output = new FileOutputStream(rootDir.toString() + "/actia/" + nombrearchivo);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    //publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                
                Writer writer = null;
    			
                File fdesc=new File(rootDir.toString() + "/actia/" + nombrearchivo );
                File f=new File(rootDir.toString() + "/actia/size" + nombrearchivo );
    	        if (!f.exists()) {
    	        	try {
    	            	//File does not exists
    	                f.createNewFile();
    	                String text = dt.toString();
    	                writer = new BufferedWriter(new FileWriter(f));
    	                writer.write(text);
    	            } catch (FileNotFoundException e) {
    	                e.printStackTrace();
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            } finally {
    	                try {
    	                    if (writer != null) {
    	                        writer.close();
    	                    }
    	                } catch (IOException e) {
    	                    e.printStackTrace();
    	                }
    	            }
    	        }else {
    	        	try {
    	        	    BufferedReader br = new BufferedReader(new FileReader(f),8192);
    	        	    String line,val = null;
    	
    	        	    while ((line = br.readLine()) != null) {
    	        	    	val=line;
    	        	    	Log.d("MovieFiles", "File " + f.toString() + " Contents ==> " + val);
    	        	    }
    	        	    
    	        	    if (dt != Long.parseLong(val) ){
    	        	    	fdesc.delete();
    	        	    	f.delete();
    	        	    	descargar=true;
    	        	    }
    	        	}
    	        	catch (IOException e) {
    	        	    //You'll need to add proper error handling here
    	        		e.printStackTrace();
    	        	}
    			}
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.d("Descarga", e.toString());
        }
        if(descargar==true){
        	File archivocontrol = new File(rootDir.toString() + "/" + filecontrl);
        	archivocontrol.delete();
        	if (!archivocontrol.exists()){
        		String cadarchivos=ipserver+borrapelic+","+ipserver+listapelic;
        		MovieFiles(cadarchivos);
        	}
        }
	}
	
	public static boolean existe(String URLName){
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      // NOTE : you may also need HttpURLConnection.setInstanceFollowRedirects(false)
	      HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
	      con.setRequestMethod("POST");
	      
	      if(con.getResponseCode() == 200){
	    	  Log.d("EXISTE","Valor Codigo Respuesta "+ con.getResponseCode());
	    	  return true;
	      }
	      else{
	    	  Log.d("EXISTE","Valor Codigo Respuesta "+ con.getResponseCode());
	    	  return  false;  
	      }
	    } catch (Exception e) {
	    	Log.d("EXISTE", "Error en Existe: " + e.toString());
	    	return false;
	    }
	}
	
	public static boolean existe2(String ipsvr,String idtac){
		boolean valor=false;
		Log.d("existe2", "Dentro de existe2");
		
		String result = "";
    	InputStream is=null;
    	try{
    	    HttpClient httpclient = new DefaultHttpClient();
    	    HttpPost httppost = new HttpPost(ipsvr + "file_exists.php?idtac=" + idtac);      
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity entity = response.getEntity();
    	    is = entity.getContent();
    	    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8192);
    	    String line = "";
    	    while ((line = reader.readLine()) != null) {
    	        result += line;
    	    }
    	    is.close();
    	    Log.d("DataFromPHPExiste2","Existe " + idtac + " : " +result);
    	    
    	    if(result.contains("SI")){
    	    	valor=true;
    	    	Log.d("Existe21","Existe " + idtac + " : SIIIIIIIII!!!!" );
    	    }
    	    if(result.contains("NO")){
    	    	valor=false;
    	    	Log.d("Existe22","Existe " + idtac + " : NOOOOOOOOO!!!!" );
    	    }
	        
    	}catch(Exception ex){  
    	    Log.e("Error",ex.toString());
    	}
    	
        return valor;
	}
	
	public void checkAndCreateDirectory(String dirName){
        File new_dir = new File( rootDir + dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
        }
    }
	
	public String getLocalIpAddress() {
		String ipv4="";
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface
	                .getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf
	                    .getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                //System.out.println("ip1--:" + inetAddress);
	                //System.out.println("ip2--:" + inetAddress.getHostAddress());

	      // for getting IPV4 format
	      if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {

	                    String ip = inetAddress.getHostAddress().toString();
	                    //System.out.println("ip---::" + ip);
	                    //EditText tv = (EditText) findViewById(R.id.ipadd);
	                    //tv.setText(ip);
	                    // return inetAddress.getHostAddress().toString();
	                    return ipv4;
	                }
	            }
	        }
	    } catch (Exception ex) {
	        Log.e("IP Address", ex.toString());
	    }
	    return null;
	}
	
	public boolean runFind(String path, String filename) {
    	try {
    		Log.d("runFind", "Dentro de runFind");
    		
    		File file1=new File(path+filename);
    		
    		if(file1.exists()){
    			Log.d("runFind", "SIIIII SE ENCONTRO EL ARCHIVO " + path + filename );
    			return true;
    		} else {
    			Log.d("runFind", "NOOOOO SE ENCONTRO EL ARCHIVO " + path + filename );
    			return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("runFind", "HUBO UN SEVERO ERROR en runFind" + e.toString());
			return false;
		}
	}
	
	public void removeLineFromFile(String file, String lineToRemove) {
		try {
	  	      File inFile = new File(file);
	  	      if (!inFile.isFile()) {
	  	    	  //System.out.println("Parameter is not an existing file");
	  	    	  Log.d("removeLineFromFile", "Parameter is not an existing file");
	  	    	  return;
	  	      }
	  	       
	  	      //Construct the new file that will later be renamed to the original filename.
	  	      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
	  	      
	  	      if(!tempFile.exists()){
	  	    	  tempFile.createNewFile();
	  	      }
	  	      
	  	      BufferedReader br = new BufferedReader(new FileReader(file),8192);
	  	      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
	  	      
	  	      String line = null;
	
	  	      //Read from the original file and write to the new
	  	      //unless content matches data to be removed.
	  	      while ((line = br.readLine()) != null) {
	  	    	  if (!line.trim().equals(lineToRemove)) {
	  	    		  pw.println(line);
	  	    		  pw.flush();
	  	    	  }
	  	      }
	  	      pw.close();
	  	      br.close();
	  	      
	  	      //Delete the original file
	  	      if (!inFile.delete()) {
	  	    	  //System.out.println("Could not delete file");
	  	    	  Log.d("removeLineFromFile", "Could not delete file ");
	  	    	  triggerSpaceNotification("1Hubo un problema con movies.txt");
	  	    	  return;
	  	      }
	  	      
	  	      //Rename the new file to the filename the original file had.
	  	      if (!tempFile.renameTo(inFile)){
	  	    	  //System.out.println("Could not rename file");
	  	    	  Log.d("removeLineFromFile", "Could not rename file");
	  	    	  triggerSpaceNotification("2Hubo un problema con movies.txt");
	  	      }
		} catch (FileNotFoundException ex) {
  	    	ex.printStackTrace();
  	    } catch (IOException ex) {
  	    	ex.printStackTrace();
  	    }
	}

	public double getExternalStotageFreeSpace(){
		StatFs stat = new StatFs(rootDir);
		double sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();
		//one binary gigabyte equals 1,073,741,824 bytes.
		//double gigaAvailable = sdAvailSize / 1073741824;
		double gigaAvailable = sdAvailSize;
		return gigaAvailable;
	}
	
	public static boolean directorios(File dir) {
    	String ruta=""; 
    	if (dir.isDirectory()) {
    		
    		ruta=dir.getAbsolutePath();
    		
    		String[] children = dir.list();
    		
    		Arrays.sort(children);

    		try {
    			for (int i=0; i<children.length; i++) {
    				if(children[i].toString().contains("icon") || children[i].toString().contains("movies")){
    					Log.d("ICONOMOVIES", "NO SE AGREGARA A LA LISTA EL ARCHIVO: "+ children[i].toString());
    				}else {
    					
    					String misfiles=children[i].toString();
    					Log.d("misfiles", "misfiles: "+ misfiles);
    					
    					File rutafile = new File(ruta+"/"+misfiles);
    					
    					if (rutafile.isDirectory()){
    						
    						//mipath=ruta+"/"+misfiles +"/" + misfiles+".txt";
    						mipath=ruta+"/"+misfiles +"/movies.txt";
    						continuar=false;
    						
    						f = new File(mipath);
        					if(f.exists() && f.length()==0){
        						f.delete();
        						continuar=true;
        						Log.d("MOVIESTXT", "Se continuara el proceso de escritura en movies.txt");
        					}
        					
        					if(!f.exists()){
        						Log.d("MOVIESTXT", "movies.txt no existe se creara y se continuara el proceso de escritura");
        						f.createNewFile();
        						continuar=true;
        					}
    						
    					} 
    					
    					if (continuar==true){
    						
    						if (rutafile.isFile()){
        						if(misfiles.contains("mp4")){
            						//fw.write(ruta+"/"+children[i].toString()+ "\n");
        							f = new File(mipath);
        							
        							fw = new FileWriter(f,true);
                					fw.write("\n"+children[i].toString().substring(0, children[i].toString().lastIndexOf(".")));
            					}
    						}
    						
    					}
    						
    					boolean success = directorios(new File(dir, children[i]));
            			if (!success) {
            				return false;
            			}
					}
    				
        		}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    	}
    	try {
    		f=null;
    		fw.flush();
    		fw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return true;
    }
	
}