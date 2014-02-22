package com.example.audioextractproto1;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.View;

public class AudioExtractorActivity extends Activity {
	private static final String LOG_TAG = "AudioExtractorProto1";

	private static final String relDirPath = "test/AudioExtractProto1";
	private static final String fileName = "fullRecording.3gp";
	private final String filePath;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	public AudioExtractorActivity() {
		// create test directory if it does not exist
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + relDirPath);
		dir.mkdirs();
		
		filePath = new File(dir, fileName).getAbsolutePath();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_extractor);
	}

	public void startRecording(View view) {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(filePath);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	public void stopRecording(View view) {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		Log.e(LOG_TAG, "stopped recording");
	}

	public void startPlaying(View view) {
		Log.e(LOG_TAG, "started playing");

		mPlayer = new MediaPlayer();

		try {
			mPlayer.setDataSource(filePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	public void stopPlaying(View view) {
		mPlayer.release();
		mPlayer = null;
	}
}
