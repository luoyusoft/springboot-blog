package com.jinhx.blog.entity.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * HostReadVO
 *
 * @author jinhx
 * @since 2019-02-17
 */
@Data
public class HostReadVO implements Serializable {

    private static final long serialVersionUID = -7051057878383055830L;

    private Integer id;

    private Integer type;

    private Integer title;

    private Integer likeNum;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
