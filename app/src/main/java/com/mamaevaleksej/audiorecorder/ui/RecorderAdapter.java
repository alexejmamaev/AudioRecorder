package com.mamaevaleksej.audiorecorder.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mamaevaleksej.audiorecorder.R;
import com.mamaevaleksej.audiorecorder.model.Record;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecorderAdapter extends RecyclerView.Adapter<RecorderAdapter.RecorderAdapterViewHolder> {

    private static final String DATE_FORMAT = "E, dd.MM.yyyy  h:m:s";
    final private ItemClickListener mListener;
    private Context mContext;
    private List<Record> mRecords;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public RecorderAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mListener = listener;
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
        viewHolder.recordNameTV.setText(filePath);

        int recordLength = record.getRecordLength();
        viewHolder.recordLengthTV.setText(recordLength + "");

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

    public interface ItemClickListener{
        void onItemClickListener(int itemId);
    }

    public List<Record> getmRecords(){
        return mRecords;
    }

    public void setmRecords(List<Record> records){
        mRecords = records;
        notifyDataSetChanged();
    }

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
            int id = mRecords.get(getAdapterPosition()).getId();
            mListener.onItemClickListener(id);
        }
    }
}
