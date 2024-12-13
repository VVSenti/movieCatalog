package ru.sentyurin.util.exception;

public class NoDataInRepository extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoDataInRepository(String message) {
		super(message);
	}
}
