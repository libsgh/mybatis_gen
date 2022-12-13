package com.libsgh.mybatis.gen.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfo {
    private String icon;
    private String name;
    private String modif_time;
    private String path;
    private long size;
}
