package ru.sentyurin.util.exception;

public class DataBaseDriverClassNotFound extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DataBaseDriverClassNotFound(String message) {
		super(message);
	}
}
