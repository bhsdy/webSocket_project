package com.cn.websocket.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonInclude(value = Include.NON_NULL)
public class ValidationError implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
 
    private String message;
    
    private List<String> arguments;

}
