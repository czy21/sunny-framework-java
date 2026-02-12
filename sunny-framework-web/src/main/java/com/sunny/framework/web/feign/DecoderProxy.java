package com.sunny.framework.web.feign;


import com.sunny.framework.core.model.CommonCodeEnum;
import com.sunny.framework.core.model.CommonResult;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class DecoderProxy implements Decoder {
    Decoder target;

    public DecoderProxy(Decoder target) {
        this.target = target;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Object ret = target.decode(response, type);
        if (ret instanceof CommonResult<?> commonRes && !CommonCodeEnum.SUCCESS.getCode().equals(commonRes.getCode())) {
            throw new FeignCommonException(commonRes.getCode(), commonRes.getMessage());
        }
        return ret;
    }
}
