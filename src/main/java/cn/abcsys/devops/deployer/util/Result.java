package cn.abcsys.devops.deployer.util;

public class Result {

	private boolean success;
	private String message;
	private Object data;// 操作返回的数据
	private int size;//操作数据的大小
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	public Result(boolean success) {
		super();
		this.success = success;
	}
	/**
	 * @param success
	 * @param data
	 * @param message
	 */
	public Result(boolean success, Object data, String message) {
		this.success = success;
		this.message = message;
		this.data = data;
	}
	
	public Result(boolean success, String message, Object data, int size) {
		super();
		this.success = success;
		this.message = message;
		this.data = data;
		this.size = size;
	}
	public Result() {
		super();
	}
	
	@Override
	public String toString() {
		return "Result [success=" + success + ", message=" + message + "]";
	}
	
}
