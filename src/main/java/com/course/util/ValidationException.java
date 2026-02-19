package com.course.util;

public class ValidationException extends Exception {
	public String message;
	public ValidationException(String message) {
		this.message=message;
	}
	public String toString() {
		return "ValidateException"+message;
	}

}
