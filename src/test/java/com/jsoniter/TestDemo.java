package com.jsoniter;

import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsonWrapper;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.any.Any;
import com.jsoniter.fuzzy.MaybeEmptyArrayDecoder;
import com.jsoniter.fuzzy.MaybeStringLongDecoder;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDemo extends TestCase {
    public void test_bind_api() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        int[] val = iter.read(int[].class);
        System.out.println(val[3]);
    }

    public void test_any_api() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        System.out.println(iter.readAny().toInt(3));
    }

    public void test_iterator_api() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        int total = 0;
        while (iter.readArray()) {
            total += iter.readInt();
        }
        System.out.println(total);
    }

    public static class ABC {
        public Any a;
    }

    public void test_abc() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'a': {'b': {'c': 'd'}}}".replace('\'', '"'));
        ABC abc = iter.read(ABC.class);
        assertEquals("d", abc.a.get("b", "c").object());
    }

    public void test_iterator_api_and_bind() throws IOException {
        JsonIterator iter = JsonIterator.parse("[123, {'name': 'taowen', 'tags': ['crazy', 'hacker']}]".replace('\'', '"'));
        iter.readArray();
        int userId = iter.readInt();
        iter.readArray();
        User user = iter.read(User.class);
        user.userId = userId;
        iter.readArray(); // end of array
        System.out.println(user);
    }

    public void test_empty_array_as_null() throws IOException {
        JsoniterSpi.registerExtension(new EmptyExtension() {
            @Override
            public Decoder createDecoder(final String cacheKey, final Type type) {
                if (cacheKey.endsWith(".original")) {
                    // avoid infinite loop
                    return null;
                }
                if (type != Date.class) {
                    return null;
                }
                return new Decoder() {
                    @Override
                    public Object decode(JsonIterator iter1) throws IOException {
                        if (iter1.whatIsNext() == ValueType.ARRAY) {
                            if (iter1.readArray()) {
                                // none empty array
                                throw iter1.reportError("decode [] as null", "only empty array is expected");
                            } else {
                                return null;
                            }
                        } else {
                            // just use original decoder
                            TypeLiteral typeLiteral = new TypeLiteral(type, cacheKey + ".original",
                                    TypeLiteral.create(type).getDecoderCacheKey());
                            return iter1.read(typeLiteral);
                        }
                    }
                };
            }
        });
        JsonIterator iter = JsonIterator.parse("[]");
        assertNull(iter.read(Date.class));
    }

    public static class Order {
        @JsonProperty(decoder = MaybeStringLongDecoder.class)
        public long order_id;
        @JsonProperty(decoder = MaybeEmptyArrayDecoder.class)
        public OrderDetails order_details;
    }

    public static class OrderDetails {
        public String pay_type;
    }

    public void test_iterator() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'numbers': ['1', '2', ['3', '4']]}".replace('\'', '"'));
        assertEquals("numbers", iter.readObject());
        assertTrue(iter.readArray());
        assertEquals("1", iter.readString());
        assertTrue(iter.readArray());
        assertEquals("2", iter.readString());
        assertTrue(iter.readArray());
        assertEquals(ValueType.ARRAY, iter.whatIsNext());
        assertTrue(iter.readArray()); // start inner array
        assertEquals(ValueType.STRING, iter.whatIsNext());
        assertEquals("3", iter.readString());
        assertTrue(iter.readArray());
        assertEquals("4", iter.readString());
        assertFalse(iter.readArray()); // end inner array
        assertFalse(iter.readArray()); // end outer array
        assertNull(iter.readObject()); // end object
    }

    public void test_any_is_fun() throws IOException {
        Any any = JsonIterator.deserialize("{'numbers': ['1', '2', ['3', '4']]}".replace('\'', '"'));
        any.get("numbers").asList().add(Any.wrap("hello"));
        assertEquals("{'numbers':['1', '2', ['3', '4'],'hello']}".replace('\'', '"'), JsonStream.serialize(any));
        any = JsonIterator.deserialize("{'error': 'failed'}".replace('\'', '"'));
        assertFalse(any.toBoolean("success"));
        any = JsonIterator.deserialize("{'success': true}".replace('\'', '"'));
        assertTrue(any.toBoolean("success"));
        any = JsonIterator.deserialize("{'success': 'false'}".replace('\'', '"'));
        assertFalse(any.toBoolean("success"));
        any = JsonIterator.deserialize("[{'score':100}, {'score':102}]".replace('\'', '"'));
        assertEquals("[100,102]", JsonStream.serialize(any.get('*', "score")));
        any = JsonIterator.deserialize("[{'score':100}, {'score':[102]}]".replace('\'', '"'));
        assertEquals("[{},{'score':102}]".replace('\'', '"'), JsonStream.serialize(any.get('*', '*', 0)));
        any = JsonIterator.deserialize("[{'score':100}, {'score':102}]".replace('\'', '"'));
        assertEquals(Long.class, any.get(0, "score").object().getClass());
        any = JsonIterator.deserialize("[{'score':100}, {'score':102}]".replace('\'', '"'));
        assertEquals(ValueType.INVALID, any.get(0, "score", "number").valueType());
        any = JsonIterator.deserialize("[{'score':100}, {'score':102}]".replace('\'', '"'));
        for (Any record : any) {
            Any.EntryIterator entries = record.entries();
            while (entries.next()) {
                System.out.println(entries.key());
                System.out.println(entries.value());
            }
        }
    }

    public static class TestObject {
        public String body;
        public int commentCount;
    }

    public void test_utf8() {
        String input = "{\"body\":\"یبل تیبلتیبمسش یبمک سشیمب سشیکمب تشسکمیبنمسیتبمسشتیب منشستمتبیملتیبملتیبمتلیمبلت یبلتیبل ینبنن اسی باسیش نباسشینباشسینبشسنتیب شسنیاب نشسیابنسشتیابنتسشیابنسشیابنسیشابنسشیاب نسشیاب سشیب سشیبن ت سینبسیبنسیشاب نسیاب سیاب نسیتبا سینا سیا بسیاب نستیشاب نستیبسی\",\"commentCount\":0,\"doILike\":false,\"doISuggest\":false,\"likeCount\":1,\"rowId\":\"58bf6ed1c8015f0bd4422c70\",\"specialLabel\":0,\"submitDate\":\"2017-03-08T02:39:13.568Z\",\"suggestCount\":0,\"title\":\"تست می باشد.\",\"type\":1,\"url\":[\"images/cell/490661220.jpg\"],\"username\":\"mahdihp\"}";
        TestObject obj = JsonIterator.deserialize(input, TestObject.class);
        assertEquals(0, obj.commentCount);
    }

    public void test_req() throws Exception {
        String content = "{\"auth_keys\": [{\"ak_username\": \"test1\", \"ak_tags\": \"\", \"ak_id\": 26120, \"fid\": 111, \"ak_hostname\": \"host1\"}, {\"ak_username\": \"test1\", \"ak_tags\": \"\", \"ak_id\": 23270, \"fid\": 111, \"ak_hostname\": \"host2\"}],\n" +
                "\"private_keys\": [{\"pk_id\": 2570, \"pk_username\": \"test1\", \"pk_tags\": \"\", \"pk_hostname\": \"host3\", \"fid\": 111}],\"id\": 111}";
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        JsoniterAnnotationSupport.enable();
        JsonIterator iter = JsonIterator.parse(content);
        UKMAuthzResponseBean bean = iter.read(UKMAuthzResponseBean.class);
        System.out.println(bean.id);
        System.out.println(iter.currentBuffer());
    }

    public static class UKMAuthzResponseBean {
        /**
         * @return the auth_keys
         */
        public List<AuthzKeyBean> getAuth_keys() {
            return auth_keys;
        }
        /**
         * @param auth_keys the auth_keys to set
         */
        public void setAuth_keys(List<AuthzKeyBean> auth_keys) {
            this.auth_keys = auth_keys;
        }
        /**
         * @return the id
         */
        public int getId() {
            return id;
        }
        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }
        /**
         * @return the private_keys
         */
        public List<PrivateKeyBean> getPrivate_keys() {
            return private_keys;
        }
        /**
         * @param private_keys the private_keys to set
         */
        public void setPrivate_keys(List<PrivateKeyBean> private_keys) {
            this.private_keys = private_keys;
        }

        /** (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "UKMAuthzResponseBean [auth_keys=" + auth_keys + ", id=" + id + ", private_keys=" + private_keys
                    + ", getAuth_keys()=" + getAuth_keys() + ", getId()=" + getId() + ", getPrivate_keys()="
                    + getPrivate_keys() + "]";
        }

        private List<AuthzKeyBean> auth_keys;
        private int id;
        private List<PrivateKeyBean> private_keys;
    }

    public static class AuthzKeyBean {


        @JsonWrapper
        public void initialize(
                @JsonProperty("ak_username") String ak_username,
                @JsonProperty("ak_tags") String  ak_tags,
                @JsonProperty("ak_id") int  ak_id,
                @JsonProperty("fid") int  fid,
                @JsonProperty("ak_hostname") String  ak_hostname)
        {
            this.ak_hostname = ak_hostname;
            this.ak_username = ak_username;
            this.ak_id = String.valueOf(ak_id);
        }


        /**
         * @return the ak_hostname
         */
        public String getAk_hostname() {
            return ak_hostname;
        }
        /**
         * @param ak_hostname the ak_hostname to set
         */
        public void setAk_hostname(String ak_hostname) {
            this.ak_hostname = ak_hostname;
        }
        /**
         * @return the ak_username
         */
        public String getAk_username() {
            return ak_username;
        }
        /**
         * @param ak_username the ak_username to set
         */
        public void setAk_username(String ak_username) {
            this.ak_username = ak_username;
        }
        /**
         * @return the ak_id
         */
        public String getAk_id() {
            return ak_id;
        }
        /**
         * @param ak_id the ak_id to set
         */
        public void setAk_id(String ak_id) {
            this.ak_id = ak_id;
        }

        /** (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "AuthzKeyBean [ak_hostname=" + ak_hostname + ", ak_username=" + ak_username + ", ak_id=" + ak_id
                    + ", getAk_hostname()=" + getAk_hostname() + ", getAk_username()=" + getAk_username() + ", getAk_id()="
                    + getAk_id() + "]";
        }

        private String ak_hostname;
        private String ak_username;
        private String ak_id;
        private String ak_tags;
        private String fid;
    }

    public static class PrivateKeyBean {
        /**
         * @return the pk_hostname
         */
        public String getPk_hostname() {
            return pk_hostname;
        }
        /**
         * @param pk_hostname the pk_hostname to set
         */
        public void setPk_hostname(String pk_hostname) {
            this.pk_hostname = pk_hostname;
        }
        /**
         * @return the pk_username
         */
        public String getPk_username() {
            return pk_username;
        }
        /**
         * @param pk_username the pk_username to set
         */
        public void setPk_username(String pk_username) {
            this.pk_username = pk_username;
        }
        /**
         * @return the pk_id
         */
        public int getPk_id() {
            return pk_id;
        }
        /**
         * @param pk_id the pk_id to set
         */
        public void setPk_id(int pk_id) {
            this.pk_id = pk_id;
        }
        /** (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "PrivateKeyBean [pk_hostname=" + pk_hostname + ", pk_username=" + pk_username + ", pk_id=" + pk_id
                    + ", getPk_hostname()=" + getPk_hostname() + ", getPk_username()=" + getPk_username() + ", getPk_id()="
                    + getPk_id() + "]";
        }

        private String pk_hostname;
        private String pk_username;
        private int pk_id;
    }
}
