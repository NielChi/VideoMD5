package com.niel.code.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.AsyncTask;
import android.util.Log;

public class ParseMD5 extends AsyncTask<String, Integer, String> {
	private final String TAG = "MD5";
	private final int START = 0;
	private final int PAUSE = 1;
	private final int STOP = 2;
	int isStatus = STOP;
	
	private OnParseListener parseListener;
	
	private int index;
	
	public interface OnParseListener {
		public void onProgressUpdate(int index, int percentage);
		public void onMD5Checksum(int index, String md5);
	}
	
	public void setOnParseListener(OnParseListener parseListener) {
		this.parseListener = parseListener;
	}
	
	public void start() {
		if( (isStatus == PAUSE) || (isStatus == STOP) ) {
			isStatus = START;
		}
	}
	
	public void pause() {
		if(isStatus == START) {
			isStatus = PAUSE;
		}
	}
	
	public void stop() {
		if( (isStatus == PAUSE) || (isStatus == START) ) {
			isStatus = STOP;
			this.cancel(true);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		isStatus = START;
		File file = new File(params[0]);
		index = Integer.valueOf(params[1]);
		String md5 = calculateMD5(file);
		return md5;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		parseListener.onProgressUpdate(index, values[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		parseListener.onMD5Checksum(index, result);
		super.onPostExecute(result);
	}
	
	private String calculateMD5(File updateFile) {
        MessageDigest digest;
        long fileTotalSize = 0;
        long readSize = 0;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
            fileTotalSize = updateFile.length();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
            	while(isStatus == PAUSE) {
            		Thread.sleep(100);
            	}
            	if(isCancelled()) {
            		return null;
            	}
                digest.update(buffer, 0, read);
                readSize = readSize + read;
                float size = (((float)readSize / (float)fileTotalSize) * 100 );
                publishProgress((int)size);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (Exception e) {
            Log.e(TAG, "Unable to process file for MD5");
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }
	
}