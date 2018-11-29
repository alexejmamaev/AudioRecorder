package com.mamaevaleksej.audiorecorder.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class RecordReminderFirebaseJobService extends JobService {

    private static AsyncTask mBackgroundTask;

    /**
     * The entry point to your Job. Implementations should offload work to another thread of execution
     * as soon as possible because this runs on the main thread. If work was offloaded, call {@link
     * JobService#jobFinished(JobParameters, boolean)} to notify the scheduling service that the work
     * is completed.
     *
     * <p>If a job with the same service and tag was rescheduled during execution {@link
     * JobService#onStopJob(JobParameters)} will be called and the wakelock will be released. Please
     * make sure that all reschedule requests happen at the end of the job.
     *
     * @param jobParameters
     * @return {@code true} if there is more work remaining in the worker thread, {@code false} if the
     * job was completed.
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = RecordReminderFirebaseJobService.this;
                NotificationTask.executeTask(context, NotificationTask.ACTION_REMIND_OF_IGNORED_RECORDS);
                return null;
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             *
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param o The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false);
            }
        }.execute();
        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job, most
     * likely because the runtime constraints associated with the job are no longer satisfied. The job
     * must stop execution.
     *
     * @param job
     * @return true if the job should be retried
//     * @see Job.Builder#setRetryStrategy(RetryStrategy)
//     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
