package org.chobit.apt;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * json序列化工具类
 *
 * @author robin
 */
public final class JsonStringSerializer {


    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(JsonStringSerializer.class);


    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JacksonException e) {
            logger.warn("json serialize error.", e);
            return String.valueOf(obj);
        }
    }

}
