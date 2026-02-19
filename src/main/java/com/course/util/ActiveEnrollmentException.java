package com.course.util;

public class ActiveEnrollmentException extends Exception {
	private String message;
	public  ActiveEnrollmentException(String message) {
		this.message=message;
	}
	public String toString() {
		return " ActiveEnrollmentException"+message;
	}
}
