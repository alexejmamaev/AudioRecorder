package com.mamaevaleksej.audiorecorder.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mamaevaleksej.audiorecorder.R;
import com.mamaevaleksej.audiorecorder.data.AppRepository;
import com.mamaevaleksej.audiorecorder.data.Record;
import com.mamaevaleksej.audiorecorder.ui.views.ItemTouchHelperAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecorderAdapter extends RecyclerView.Adapter<RecorderAdapter.RecorderAdapterViewHolder>
        implements ItemTouchHelperAdapter {

    public static final String TAG = RecorderAdapter.class.getSimpleName();

    private static final String DATE_FORMAT = "E, dd.MM.yyyy  h:m:s";
    final private ItemClickListener mListener;
    private Context mContext;
    private List<Record> mRecords;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private AppRepository mRepository;

    public RecorderAdapter(Context context, ItemClickListener listener, AppRepository repository) {
        mContext = context.getApplicationContext();
        mListener = listener;
        mRepository = repository;
    }

    @NonNull
    @Override
    public RecorderAdapter.RecorderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.record_item, parent, false);
        return new RecorderAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecorderAdapter.RecorderAdapterViewHolder viewHolder, int position) {
        Record record = mRecords.get(position);

        // Выводит путь файла в TextView
        String filePath = record.getFilePath();
        viewHolder.recordNameTV.setText(new File(filePath).getName());

        // Длительность записи
        long recordLength = record.getRecordLength();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(recordLength);
        String formattedLength = String.format(Locale.getDefault(), "%2d sec : %2d mil",
                seconds,
                recordLength - TimeUnit.SECONDS.toMillis(seconds));
        viewHolder.recordLengthTV.setText(formattedLength);

        if (!record.isListened()) {
            viewHolder.recordNameTV.setTextColor(Color.RED);
        } else {
            viewHolder.recordNameTV.setTextColor(Color.BLACK);
        }


        // Дата записи
        java.util.Date recordTime = record.getRecordTime();
        viewHolder.recordTimeTV.setText(dateFormat.format(recordTime));

    }

    @Override
    public int getItemCount() {
        if (mRecords == null){
            return 0;
        }
        return mRecords.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Record record = mRecords.get(position);
        mRepository.deleteRecord(record.getId());
        notifyDataSetChanged();
        File recordFile = new File(record.getFilePath());
        recordFile.delete();
    }

    public interface ItemClickListener{
        void onItemClickListener(Record record);
    }

    public List<Record> getmRecords(){
        return mRecords;
    }

    public void setmRecords(List<Record> records){
        mRecords = records;
        notifyDataSetChanged();
    }

    /* VIEW HOLDER */
    class RecorderAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView recordNameTV;
        TextView recordTimeTV;
        TextView recordLengthTV;

        private RecorderAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            recordNameTV = itemView.findViewById(R.id.tv_file_name);
            recordTimeTV = itemView.findViewById(R.id.tv_record_time);
            recordLengthTV = itemView.findViewById(R.id.tv_record_length);

            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mListener.onItemClickListener(mRecords.get(getAdapterPosition()));
        }
    }
}
