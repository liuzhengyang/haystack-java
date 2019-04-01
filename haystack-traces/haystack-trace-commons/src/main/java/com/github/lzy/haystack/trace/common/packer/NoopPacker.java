package com.github.lzy.haystack.trace.common.packer;

import java.io.OutputStream;

import com.google.protobuf.GeneratedMessageV3;

/**
 * @author liuzhengyang
 */
public class NoopPacker<T extends GeneratedMessageV3> extends Packer<T> {
    @Override
    protected OutputStream compressStream(OutputStream outputStream) {
        return null;
    }
}
