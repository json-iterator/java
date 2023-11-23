import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public record SQLTimestampSupport(SimpleDateFormat dateFormat) implements Decoder, Encoder {
    @Override
    public Object decode (JsonIterator iter) throws IOException {
        String timestampStr = iter.readString();
        return Timestamp.valueOf(timestampStr);
    }

    @Override
    public void encode (Object obj, JsonStream stream) throws IOException {
        Timestamp timestamp = (Timestamp) obj;
        stream.writeVal(timestamp.toString());
    }

    private Timestamp parse (JsonIterator jsonIterator) throws IOException {
        try {
            return new Timestamp(dateFormat.parse(jsonIterator.readString()).getTime());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public void registerModule () {
        JsoniterSpi.registerTypeDecoder(Timestamp.class, this::parse);
        JsoniterSpi.registerTypeEncoder(Timestamp.class, (obj, stream) -> stream.writeVal(obj.toString()));
    }
}
