package com.kiss.mq.model.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.kiss.mq.model.enums.WorkMode;

/**
 * @author zhangziyao
 * @date 2020/12/27 3:59 下午
 */
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetaData {

    private String topic;

    private String queue;

    private WorkMode workMode;

    private String message;
}
