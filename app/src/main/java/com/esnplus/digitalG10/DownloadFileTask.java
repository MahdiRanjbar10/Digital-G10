package com.esnplus.digitalG10;

import android.os.AsyncTask;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * Task to download a file from Dropbox and put it to an OutputStream
 */
class DownloadFileTask extends AsyncTask<FileMetadata, Void, ByteArrayOutputStream> {

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onDownloadComplete(ByteArrayOutputStream result);
        void onError(Exception e);
    }
    DownloadFileTask(DbxClientV2 dbxClient, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
    }
    @Override
    protected void onPostExecute(ByteArrayOutputStream result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDownloadComplete(result);
        }
    }
    @Override
    protected ByteArrayOutputStream doInBackground(FileMetadata... params) {
        FileMetadata metadata = params[0];
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mDbxClient.files().download(metadata.getPathLower(), metadata.getRev()).download(byteArrayOutputStream);
            return byteArrayOutputStream;
        }catch (DbxException | IOException e) {
            mException = e;
        }
        return null;
    }
}