package securedqr.smit.edu.in.app.util;

import securedqr.smit.edu.in.app.com.google.zxing.integration.android.IntentIntegrator;
import securedqr.smit.edu.in.app.com.google.zxing.integration.android.IntentResult;
import securedqr.smit.edu.in.app.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * This is the main activity
 * @since 1.0
 */
public class QRCode extends Activity implements OnClickListener {
	/**
	 * @param tv TextView for displaying information
	 * @param filePath External storage directory path
	 */
	private Button scanBtn,gen,ver,dqr;
	public static TextView tv;
	public static String scanContent="No result";
	public static final String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/QR";
    private static final int REQUEST_RW_STORAGE = 2909;

    private void makeQRDirectory() {
        File dir=new File(QRCode.filePath);
        if(!dir.exists()) {
            dir.mkdir();
        }
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load the main activity layout
        setContentView(R.layout.activity_qr);
        //Create directory
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getRWPermission();
        }
        else {
            makeQRDirectory();
        }
        //Check which button is pressed
        scanBtn = (Button)findViewById(R.id.scan_button);
        tv=(TextView)findViewById(R.id.file_write);
        ver=(Button)findViewById(R.id.ver_sig);
        gen=(Button)findViewById(R.id.gen_qr);
        dqr=(Button)findViewById(R.id.decode);
        ver.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
        gen.setOnClickListener(this);
        dqr.setOnClickListener(this);
    }

	@Override
	public void onClick(View v)	{
		tv.setText("");
		if(v.getId()==R.id.scan_button) {
			IntentIntegrator scanner = new IntentIntegrator(this); //Zxing android interface library
			scanner.initiateScan(); //Requires BarcodeScanner app by Zxing to be installed in the phone
		}
		if(v.getId()==R.id.ver_sig) {
			Intent verify= new Intent(QRCode.this,Verify.class);
        	startActivity(verify);
		}
		if(v.getId()==R.id.gen_qr) {
			Intent qr=new Intent(QRCode.this,GenQR.class);
			startActivity(qr);
		}
		if(v.getId()==R.id.decode) {
			Intent qr=new Intent(QRCode.this,DecodeQR.class);
			startActivity(qr);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(intent!=null)
		{
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if (scanningResult != null) {
				scanContent = scanningResult.getContents();
				writeToFile();
				String zipin=filePath+"/result.zip";
				try {
					String files[]=Unzip.unzip(zipin, filePath);
					if(files[2].equals(""))
						tv.setText("Image was not scanned properly.Try again!");
					else
						tv.append("\nExtracted files are: \n"+files[0]+"\n"+files[1]+"\n"+files[2]);
				}catch(Exception e) {
					Log.createLog(e,getApplicationContext());
				}
			}
			else {
				tv.setText("Device doesn't support read/write!");
			}
		}
		else {
		    Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
		    toast.setGravity(Gravity.CENTER,0,0);
		    toast.show();
		}
	}

	private void writeToFile() {
		    File dir = new File (filePath);
		    File file = new File(dir, "/result.zip");
		    try {
		        FileOutputStream fos = new FileOutputStream(file);
				fos.write(Base64.decode(scanContent.getBytes(), Base64.DEFAULT));
		        fos.close();
		    }
		    catch(IOException e) {
		    	Log.createLog(e, getApplicationContext()); //Write logs to log.txt
		    }
		    tv.append("File written to: "+file);
	}
    private void getRWPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_RW_STORAGE);
        }
        else {
            makeQRDirectory();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_RW_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeQRDirectory();
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}