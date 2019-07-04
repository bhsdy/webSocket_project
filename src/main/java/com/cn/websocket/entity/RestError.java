package com.cn.websocket.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(value = Include.NON_NULL)
public class RestError implements Serializable {

	private static final long serialVersionUID = 1472444186817360841L;

	private Integer code;
	
	private String msg;

	private Map<String, List<ValidationError>> fieldErrors;
	
	public RestError(RestStatus status) {
		this.code = status.getCode();
		this.msg = status.getMsg();
	}

	public RestError(Integer ret, String message) {
		this.code = ret;
		this.msg = message;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {

		private Integer code;
		private String msg;
		
		public Builder() {

		}

		public Builder restStatus(RestStatus status) {
			this.code = status.getCode();
			this.msg = status.getMsg();
			return this;
		}

		public Builder restStatus(HttpStatus status) {
			this.code = status.value();
			this.msg = status.getReasonPhrase();
			return this;
		}

		public Builder code(Integer code) {
			this.code = code;
			return this;
		}

		public Builder message(String message) {
			this.msg = message;
			return this;
		}

		public RestError build() {
			return new RestError(this.code, this.msg);
		}
	}
}
