package com.course.util;

public class CourseFullException extends Exception{
	
	private String message;
	public CourseFullException(String message) {
		this.message=message;
	}
public String toString() {
	return "CourseFullException"+message;
}
}
