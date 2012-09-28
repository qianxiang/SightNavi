package org.jint.bmy.sightnavi.model;

import org.jint.util.LogUtil;

import android.os.AsyncTask;


/**
 * @author jintian
 *
 */
public abstract class Service {
	public static final int TASK_RESULT_SUCCESS = 1;
	public static final int TASK_RESULT_FAULT = 2;
	
	private AsyncTask<Integer, Integer, Integer> task;
	private boolean isCanceled = false;
	private Object executeResult;
	private OnSuccessHandler onSuccessHandler;
	private OnFaultHandler onFaultHandler;
	private Exception executeException;
	
	public final Object syncExecute() throws Exception{
		isCanceled = false;
		
		executeResult = onExecute();
		
		return executeResult;
	}
	
	public final void asyncExecute(){
		isCanceled = false;
		
		task = new AsyncTask<Integer, Integer, Integer>(){

			@Override
			protected Integer doInBackground(Integer... arg0) {
				try {
					executeResult = onExecute();
					return TASK_RESULT_SUCCESS;
				} catch (Exception e) {
					executeException = e;
					LogUtil.error("Error when service execute", e);
				}
				
				return TASK_RESULT_FAULT;
			}
			
			protected void onPostExecute(Integer result) {
				if(result == TASK_RESULT_SUCCESS){
					if(onSuccessHandler != null){
						onSuccessHandler.onSuccess(executeResult);
					}
				} else if (result == TASK_RESULT_FAULT){
					if(onFaultHandler != null){
						onFaultHandler.onFault(executeException);
					}
				}
			};
		}.execute(0);
		
	}	
	
	public final void cancel(){
		isCanceled = true;
		task.cancel(true);
	}
	
	public abstract Object onExecute() throws Exception;
	
	public OnSuccessHandler getOnSuccessHandler() {
		return onSuccessHandler;
	}

	public void setOnSuccessHandler(OnSuccessHandler onSuccessHandler) {
		this.onSuccessHandler = onSuccessHandler;
	}

	public OnFaultHandler getOnFaultHandler() {
		return onFaultHandler;
	}

	public void setOnFaultHandler(OnFaultHandler onFaultHandler) {
		this.onFaultHandler = onFaultHandler;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public interface OnSuccessHandler {
		public void onSuccess(Object result);
	}
	
	public interface OnFaultHandler {
		public void onFault(Exception ex);
	}
}
