package com.variance.msora.contacts.task;

public interface HttpRequestTaskListener<Params, Result> {
	void onTaskStarted();

	void onTaskCompleted(Result result);

	Result doTask(Params... params);
}
