package com.mamaevaleksej.audiorecorder.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.mamaevaleksej.audiorecorder.R;
import com.mamaevaleksej.audiorecorder.Utils.AppRepository;
import com.mamaevaleksej.audiorecorder.Utils.Constants;
import com.mamaevaleksej.audiorecorder.model.Record;
import com.mamaevaleksej.audiorecorder.sync.PlayService;
import com.mamaevaleksej.audiorecorder.sync.RecordService;

public class RecorderActivity extends AppCompatActivity implements RecorderAdapter.ItemClickListener {

    private final String TAG = RecorderActivity.class.getSimpleName();
    private RecorderActivityViewModel mViewModel;
    private Toast mToast;
    private Button mButtonSave;
    private RecyclerView mRecyclerView;
    private RecorderAdapter mAdapter;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    // Setup callbacks from Services via BroadcastReceiver
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Fragment playingFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.play_fragment_placeholder);

            if (!TextUtils.isEmpty(action)) {

                switch (action) {
                    // Set up actions upon callback from Record service
                    case RecordService.ACTION_RECORD:
                        String mRecordedFilePath = intent.getStringExtra(Constants.RECORDED_FILE_PATH);
                        mViewModel.setRecording(false);
                        mButtonSave.setText(R.string.button_save);
                        if (mToast != null) mToast.cancel();
                        String toastMssg = String.format(getString(R.string.recording_finished), mRecordedFilePath);
                        mToast = Toast.makeText(RecorderActivity.this, toastMssg, Toast.LENGTH_SHORT);
                        mToast.show();
                        break;
                    // Track started and we need to show the fragment
                    case PlayService.ACTION_TRACK_IS_PLAYING:
                        Log.d(TAG, "++++++++++++++++ ACTION_TRACK_IS_PLAYING");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.play_fragment_placeholder, new PlayingIndicatorFragment())
                                .commitAllowingStateLoss();
                        break;
                    // Track stopped so we need do remove existing playing Fragment
                    case PlayService.ACTION_TRACK_STOPPED_PLAYING:
                        Log.d(TAG, "++++++++++++++++ ACTION_TRACK_STOPPED_PLAYING");
                        if (playingFragment != null)
                            getSupportFragmentManager().beginTransaction().remove(playingFragment)
                                    .commitAllowingStateLoss();
                        break;
                    // Track is not found so we need to show toast indicating that
                    case PlayService.ACTION_FILE_NOT_FOUND:
                        Log.d(TAG, "++++++++++++++++ ACTION_FILE_NOT_FOUND");
                        if (mToast != null) mToast.cancel();
                        mToast = Toast.makeText
                                (RecorderActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT);
                        mToast.show();
                        break;
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        setupViewModel();

        initViews();

        // SetUp swipe deletion
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        // Register BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(RecordService.ACTION_RECORD);
        filter.addAction(PlayService.ACTION_TRACK_IS_PLAYING);
        filter.addAction(PlayService.ACTION_TRACK_STOPPED_PLAYING);
        filter.addAction(PlayService.ACTION_FILE_NOT_FOUND);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Log.d(TAG, "RecordService is running ==============>>> "
                + AppRepository.getsInstance(this).myServiceIsRunning(this, RecordService.class));
        Log.d(TAG, "PlayService is running ==============>>> "
                + AppRepository.getsInstance(this).myServiceIsRunning(this, PlayService.class));


        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.play_fragment_placeholder);
        if (!AppRepository.getsInstance(this).myServiceIsRunning(this, PlayService.class)
                && currentFragment != null){
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        }
    }

    //    Initialize this Activity views
    private void initViews() {

        /* Set save button */
        mButtonSave = findViewById(R.id.buttonRecord);

        if (mViewModel.isRecording()) {
            mButtonSave.setText(R.string.button_stop);
        } else {
            mButtonSave.setText(R.string.button_save);
        }

        mButtonSave.setOnClickListener(v -> {
            if (supportMicCheck()) {

                if (!permissionsGranted()) {
                    ActivityCompat.requestPermissions(RecorderActivity.this, permissions,
                            Constants.REQUEST_PERMISSIONS);
                } else {
                    // Kicks off new Record service if one hasn't been started before
                    if (!mViewModel.isRecording()) {
                        mViewModel.setRecording(true);
                        mButtonSave.setText(R.string.button_stop);
                        setRecordService();
                    } else {
                        // Passes negative flag to the running Record service in order to stop it
                        mButtonSave.setText(R.string.button_save);
                        mViewModel.setRecording(false);
                        setRecordService();
                    }
                }

            } else {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(RecorderActivity.this, getString(R.string.no_mic),
                        Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

        /* Set play button */
        Button mButtonPlay = findViewById(R.id.buttonPlay);
        mButtonPlay.setOnClickListener(v -> {
            //Kicks off new Play service
            if (mViewModel.getItemID() != 0)
                setPlayService(mViewModel.getItemID());
        });

        mRecyclerView = findViewById(R.id.recyclerview_records);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecorderAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this)
                .get(RecorderActivityViewModel.class);
        mViewModel.getRecords().observe(this, records -> mAdapter.setmRecords(records));
    }

    @Override
    public void onItemClickListener(Record record) {
        setPlayService(record.getId());
        mViewModel.setRecord(record);
    }

    //    Checks whether the device has microphone or not
    private boolean supportMicCheck() {
        PackageManager manager = this.getPackageManager();
        return manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    // Checks if all the permissions have been granted
    private boolean permissionsGranted() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void setRecordService() {
        Intent intent = new Intent(this, RecordService.class);
        intent.putExtra(Constants.IS_RECORDING, mViewModel.isRecording());
        startService(intent);
        Log.d(TAG, "setRecordService Service Record is running: -------> " +
                AppRepository.getsInstance(this).myServiceIsRunning(this, RecordService.class));
    }

    private void setPlayService(int id) {
        mViewModel.setItemID(id);
        //Kicks off new Play service if Play service isn't running yet
        if (AppRepository.getsInstance(this).myServiceIsRunning(this, PlayService.class)) {
            stopService(new Intent(this, PlayService.class));
        }

        Intent playServiceIntent = new Intent(this, PlayService.class);
        playServiceIntent.putExtra(PlayService.ID, id);

        startService(playServiceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Called if the app is killed
        if (isFinishing()) {
            if (mToast != null) {
                mToast.cancel();
            }
            Log.d(TAG, "App is killed >>>>>>>>>>>");
            // Stops Record Service
            if (AppRepository.getsInstance(this).myServiceIsRunning(this, RecordService.class)) {
                mViewModel.setRecording(false);
                setRecordService();
            }
            // Stops Play Service
            if (AppRepository.getsInstance(this).myServiceIsRunning(this, PlayService.class)) {
                stopService(new Intent(this, PlayService.class));
            }
            // Unregister BroadcastReceiver
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }

        // Called when activity is recreated due to orientation change (app is not killed)
        else {
            Log.d(TAG, "Orientation change >>>>>>>>>>>>");
        }
    }
    //    TODO(1) Add menu (from the beginning / reverse / delete all records)
    //    TODO(2) Add Loader spinner on the DB query
    //    TODO(3) Highlight selected item in the RecyclerView
    //    TODO(4) Add deletion icon when item is swiped
    //    TODO(5) Add JobDispatcher to remind user to listen to the recorded file
}
