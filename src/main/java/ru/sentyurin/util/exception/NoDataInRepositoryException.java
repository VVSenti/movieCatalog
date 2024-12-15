package ru.sentyurin.util.exception;

public class NoDataInRepositoryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoDataInRepositoryException(String message) {
		super(message);
	}
}
