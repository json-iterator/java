package com.jsoniter;

import com.jsoniter.any.Any;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

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
		final String originalContent = "1234";
		final byte[] src = ("{\"a\":\"" + originalContent + "\"}").getBytes();
		InputStream slowStream = new InputStream() {
			int position = 0;
			boolean pretendEmptyNextRead = false;

			@Override
			public int read() throws IOException {
				if (position < src.length) {
					if (pretendEmptyNextRead) {
						pretendEmptyNextRead = false;
						return -1;
					} else {
						pretendEmptyNextRead = true;
						return src[position++];
					}
				}
				return -1;
			}
		};

		// Input must definitely fit into such large buffer
		final int initialBufferSize = src.length * 2;
		JsonIterator jsonIterator = JsonIterator.parse(slowStream, initialBufferSize);
		jsonIterator.readObject();
		Any parsedString = jsonIterator.readAny();
		assertEquals(originalContent, parsedString.toString());
		// Check buffer was not expanded prematurely
		assertEquals(initialBufferSize, jsonIterator.buf.length);
	}
}
