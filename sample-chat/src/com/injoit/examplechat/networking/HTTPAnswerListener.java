package com.injoit.examplechat.networking;

public abstract interface HTTPAnswerListener
{
    public abstract void ProcessAnswer(int handler, String result);
    public abstract void ProcessError(int handler, String message);
}
