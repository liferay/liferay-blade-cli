package com.liferay.blade.cli.gradle;

public class ProcessResult {
	private final int returnCode;
	private final String output;
	public ProcessResult(int returnCode, String output) {
		super();
		this.returnCode = returnCode;
		this.output = output;
	}
	public int getResultCode() {
		return returnCode;
	}
	public String getOutput() {
		return output;
	}
}
