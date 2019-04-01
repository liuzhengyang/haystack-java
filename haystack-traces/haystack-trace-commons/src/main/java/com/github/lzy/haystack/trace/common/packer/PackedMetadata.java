package com.github.lzy.haystack.trace.common.packer;

/**
 * @author liuzhengyang
 */
public class PackedMetadata {
    private final PackerType packerType;

    public PackedMetadata(PackerType packerType) {
        this.packerType = packerType;
    }

    public PackerType getPackerType() {
        return packerType;
    }

}
