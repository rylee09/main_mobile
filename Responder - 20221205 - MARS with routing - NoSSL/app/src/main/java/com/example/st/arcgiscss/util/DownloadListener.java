package com.example.st.arcgiscss.util;

public interface DownloadListener {

    void onStart();

    void onProgress(int currentLength);

    void onFinish(String localPath);

    void onFailure();
}
