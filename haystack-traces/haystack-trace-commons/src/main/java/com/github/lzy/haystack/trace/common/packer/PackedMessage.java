package com.github.lzy.haystack.trace.common.packer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import com.github.lzy.haystack.trace.common.utils.SerializationUtils;
import com.google.protobuf.GeneratedMessageV3;

/**
 * @author liuzhengyang
 */
public class PackedMessage<T extends GeneratedMessageV3> {

    private static byte[] MAGIC_BYTES = "hytc".getBytes(StandardCharsets.UTF_8);

    private final T protoObj;
    private final Function<T, byte[]> pack;
    private final PackedMetadata metadata;
    private byte[] metadataBytes;

    public PackedMessage(T protoObj, Function<T, byte[]> pack, PackedMetadata metadata) {
        this.protoObj = protoObj;
        this.pack = pack;
        this.metadata = metadata;
        metadataBytes = SerializationUtils.serialize(metadata).getBytes(StandardCharsets.UTF_8);
    }

    public byte[] packedDataBytes() {
        byte[] packedDataBytes = pack.apply(protoObj);
        if (PackerType.NONE == metadata.getPackerType()) {
            return packedDataBytes;
        } else {
            return ByteBuffer
                    .allocate(MAGIC_BYTES.length + 4 + metadataBytes.length + packedDataBytes.length)
                    .put(MAGIC_BYTES)
                    .putInt(metadataBytes.length)
                    .put(metadataBytes)
                    .put(packedDataBytes).array();
        }
    }

    public T getProtoObj() {
        return protoObj;
    }
}
