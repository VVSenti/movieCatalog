package ru.sentyurin.util.exception;

public class FileReadingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FileReadingException(String message) {
		super(message);
	}

}
