package com.github.lzy.haystack.trace.common.packer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import com.google.protobuf.GeneratedMessageV3;

/**
 * @author liuzhengyang
 */
public class GzipPacker<T extends GeneratedMessageV3> extends Packer<T> {

    @Override
    protected PackerType getPackerType() {
        return PackerType.GZIP;
    }

    @Override
    protected OutputStream compressStream(OutputStream outputStream) {
        try {
            return new GZIPOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
