package com.example.audioextractproto1;

import java.io.File;
import java.io.IOException;

import com.ringdroid.soundfile.CheapAAC;
import com.ringdroid.soundfile.CheapSoundFile;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.View;

public class AudioExtractorActivity extends Activity {
	private static final String LOG_TAG = "AudioExtractorProto1";

	private final String relDirPath = "test/AudioExtractProto1";

	private final String fullRecFileName = "FullRecording3.aac";
	private static final String extracRecFileName = "extractedRecording.aac";

	private final String fullRecFilePath;
	private final String extracRecFilePath;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	public AudioExtractorActivity() {
		// create test directory if it does not exist
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + relDirPath);
		dir.mkdirs();

		fullRecFilePath = new File(dir, fullRecFileName).getAbsolutePath();

		extracRecFilePath = new File(dir, extracRecFileName).getAbsolutePath();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_extractor);
	}

	public void startRecording(View view) {
		Log.e(LOG_TAG, "starting recording in MPEG4 + AMR_WB -> m4a");

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setOutputFile(fullRecFilePath);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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

		try {
			extractLastFiveSecs();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(LOG_TAG, "extractLastFiveSeconds failed");
		}
	}

	private void extractLastFiveSecs() throws IOException {
		CheapSoundFile aacFile = new CheapAAC();

		aacFile.ReadFile(new File(fullRecFilePath));

		// 5 secs before the end
		double startTime = framesToSeconds(aacFile, aacFile.getNumFrames()) - 5;

		final int startFrame = secondsToFrames(aacFile, startTime);
		final int endFrame = aacFile.getNumFrames();

		aacFile.WriteFile(new File(extracRecFilePath), startFrame, endFrame
				- startFrame);
	}

	private int secondsToFrames(CheapSoundFile soundFile, double seconds) {
		return (int) (1.0 * seconds * soundFile.getSampleRate()
				/ soundFile.getSamplesPerFrame() + 0.5);
	}

	private double framesToSeconds(CheapSoundFile soundFile, int frames) {
		return 1.0 * frames * soundFile.getSamplesPerFrame()
				/ soundFile.getSampleRate();
	}

	public void startPlaying(View view) {
		startPlaying(fullRecFilePath);
	}

	public void playExtracted(View view) {
		startPlaying(extracRecFilePath);
	}

	private void startPlaying(String filePath) {
		Log.e(LOG_TAG, "started playing");

		mPlayer = new MediaPlayer();

		try {
			mPlayer.setDataSource(filePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed for " + filePath);
		}
	}

	public void stopPlaying(View view) {
		mPlayer.release();
		mPlayer = null;
	}
}
