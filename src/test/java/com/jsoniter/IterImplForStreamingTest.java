package com.jsoniter;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class IterImplForStreamingTest extends TestCase {

	public void testReadMaxDouble() throws Exception {
		String maxDouble = "1.7976931348623157e+308";
		JsonIterator iter = JsonIterator.parse("1.7976931348623157e+308");
		IterImplForStreaming.numberChars numberChars = IterImplForStreaming.readNumber(iter);
		String number = new String(numberChars.chars, 0, numberChars.charsLength);
		assertEquals(maxDouble, number);
	}

	@Category(StreamingCategory.class)
	public void testLoadMore() throws IOException {
		final String originalContent = "1234567890";
		final byte[] src = ("{\"a\":\"" + originalContent + "\"}").getBytes();

		int initialBufferSize;
		Any parsedString;
		// Case #1: Data fits into initial buffer, autoresizing on
		// Input must definitely fit into such large buffer
		initialBufferSize = src.length * 2;
		JsonIterator jsonIterator = JsonIterator.parse(getSluggishInputStream(src), initialBufferSize, 512);
		jsonIterator.readObject();
		parsedString = jsonIterator.readAny();
		assertEquals(originalContent, parsedString.toString());
		// Check buffer was not expanded
		assertEquals(initialBufferSize, jsonIterator.buf.length);

		// Case #2: Data does fit into initial buffer, autoresizing off
		initialBufferSize = originalContent.length() / 2;
		jsonIterator = JsonIterator.parse(getSluggishInputStream(src), initialBufferSize, 0);
		jsonIterator.readObject();
		try {
			jsonIterator.readAny();
			fail("Expect to fail because buffer is too small.");
		} catch (JsonException e) {
			if (!e.getMessage().startsWith("loadMore")) {
				throw e;
			}
		}
		// Check buffer was not expanded
		assertEquals(initialBufferSize, jsonIterator.buf.length);

		// Case #3: Data does fit into initial buffer, autoresizing on
		initialBufferSize = originalContent.length() / 2;
		int autoExpandBufferStep = initialBufferSize * 3;
		jsonIterator = JsonIterator.parse(getSluggishInputStream(src), initialBufferSize, autoExpandBufferStep);
		jsonIterator.readObject();
		parsedString = jsonIterator.readAny();
		assertEquals(originalContent, parsedString.toString());
		// Check buffer was expanded exactly once
		assertEquals(initialBufferSize + autoExpandBufferStep, jsonIterator.buf.length);
	}

	private static InputStream getSluggishInputStream(final byte[] src) {
		return new InputStream() {
			int position = 0;

			@Override
			public int read() throws IOException {
				throw new NotImplementedException();
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				if (position < src.length) {
					b[off] = src[position++];
					return 1;
				}
				return -1;
			}
		};
	}
}
