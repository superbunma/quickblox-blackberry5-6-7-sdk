package com.injoit.examplenews.qblox.networking;

public abstract interface QBHTTPAnswerListener
{
    public abstract void ProcessAnswer(int handler, String result);
    public abstract void ProcessError(int handler, String message);
}
