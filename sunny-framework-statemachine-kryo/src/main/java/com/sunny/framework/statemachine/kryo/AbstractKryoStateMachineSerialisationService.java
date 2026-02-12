/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunny.framework.statemachine.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.service.StateMachineSerialisationService;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract base implementation for {@link StateMachineSerialisationService} using kryo.
 *
 * @param <S> the type of state
 * @param <E> the type of event
 * @author Janne Valkealahti
 */
public abstract class AbstractKryoStateMachineSerialisationService<S, E> implements StateMachineSerialisationService<S, E> {

    protected final Pool<Kryo> pool;

    protected AbstractKryoStateMachineSerialisationService() {
        pool = new Pool<>(true, false, 1024) {
            protected Kryo create() {
                Kryo kryo = new Kryo();
                configureKryoInstance(kryo);
                return kryo;
            }
        };
    }

    @Override
    public byte[] serialiseStateMachineContext(StateMachineContext<S, E> context) throws Exception {
        return encode(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StateMachineContext<S, E> deserialiseStateMachineContext(byte[] data) {
        return decode(data, StateMachineContext.class);
    }

    /**
     * Subclasses implement this method to encode with Kryo.
     *
     * @param kryo   the Kryo instance
     * @param object the object to encode
     * @param output the Kryo Output instance
     */
    protected abstract void doEncode(Kryo kryo, Object object, Output output);

    /**
     * Subclasses implement this method to decode with Kryo.
     *
     * @param kryo  the Kryo instance
     * @param input the Kryo Input instance
     * @param type  the class of the decoded object
     * @param <T>   the type for decoded object
     * @return the decoded object
     */
    protected abstract <T> T doDecode(Kryo kryo, Input input, Class<T> type);

    /**
     * Subclasses implement this to configure the kryo instance.
     * This is invoked on each new Kryo instance when it is created.
     *
     * @param kryo the kryo instance
     */
    protected abstract void configureKryoInstance(Kryo kryo);

    private byte[] encode(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        encode(object, bos);
        byte[] bytes = bos.toByteArray();
        bos.close();
        return bytes;
    }

    private void encode(final Object object, OutputStream outputStream) throws IOException {
        Assert.notNull(object, "cannot encode a null object");
        Assert.notNull(outputStream, "'outputSteam' cannot be null");
        Kryo kryo = pool.obtain();
        try (Output output = (outputStream instanceof Output ? (Output) outputStream : new Output(outputStream))) {
            doEncode(pool.obtain(), object, output);
        } finally {
            pool.free(kryo);
        }
    }

    private <T> T decode(byte[] bytes, Class<T> type) {
        Assert.notNull(bytes, "'bytes' cannot be null");
        return decode(new Input(bytes), type);
    }

    private <T> T decode(InputStream inputStream, final Class<T> type) {
        Assert.notNull(inputStream, "'inputStream' cannot be null");
        Assert.notNull(type, "'type' cannot be null");
        Kryo kryo = pool.obtain();
        try (Input input = (inputStream instanceof Input ? (Input) inputStream : new Input(inputStream))) {
            return doDecode(kryo, input, type);
        } finally {
            pool.free(kryo);
        }
    }
}
