import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;
import java.util.UUID;

ublic class JavaUUIDSupport implements Encoder, Decoder {
    @Override
    public Object decode (JsonIterator jsonIterator) throws IOException {
        return UUID.fromString(jsonIterator.readString());
    }

    @Override
    public void encode (Object obj, JsonStream stream) throws IOException {
        stream.writeVal(obj.toString());
    }

    public static void registerModule () {
        JsoniterSpi.registerTypeDecoder(UUID.class, jsonIterator -> UUID.fromString(jsonIterator.readString()));
        JsoniterSpi.registerTypeEncoder(UUID.class, (obj, stream) -> stream.writeVal(obj.toString()));
    }
}
