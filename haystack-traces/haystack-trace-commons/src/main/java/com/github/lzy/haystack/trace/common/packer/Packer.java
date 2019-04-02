package com.github.lzy.haystack.trace.common.packer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.google.protobuf.GeneratedMessageV3;

import lombok.val;

/**
 * @author liuzhengyang
 */
public abstract class Packer<T extends GeneratedMessageV3> {

    protected abstract PackerType getPackerType();

    protected abstract OutputStream compressStream(OutputStream outputStream);

    private byte[] pack(T protoObj) {
        val outStream = new ByteArrayOutputStream();
        val compressedStream = compressStream(outStream);

        if (compressedStream != null) {
            try {
                IOUtils.copy(new ByteArrayInputStream(protoObj.toByteArray()), compressedStream);
                compressedStream.close();
                return outStream.toByteArray();
            } catch (IOException e) {
                return protoObj.toByteArray();
            }
        } else {
            return protoObj.toByteArray();
        }
    }

    public PackedMessage<T> apply(T protoObj) {
        return new PackedMessage<T>(protoObj, this::pack, new PackedMetadata(getPackerType()));
    }

}
