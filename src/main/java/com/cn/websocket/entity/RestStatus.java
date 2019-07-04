package com.cn.websocket.entity;

public interface RestStatus {

	public int getCode();

	public String getMsg();

	public RestStatus SUCCESS = new RestStatus() {

		public int getCode() {
			return 0;
		}

		public String getMsg() {
			return "";
		}

	};

	public RestStatus ERROR_SYSTEM = new RestStatus() {

		public int getCode() {
			return 1000;
		}

		public String getMsg() {
			return "系统异常";
		}

	};

	public RestStatus ERROR_PARAM_FORMAT = new RestStatus() {

		public int getCode() {
			return 1001;
		}

		public String getMsg() {
			return "参数格式错误";
		}

	};

	public RestStatus ERROR_PARAM = new RestStatus() {

		public int getCode() {
			return 1002;
		}

		public String getMsg() {
			return "非法请求参数";
		}

	};

	public RestStatus ERROR_NO_COMMAND = new RestStatus() {

		public int getCode() {
			return 1003;
		}

		public String getMsg() {
			return "指令不存在";
		}

	};

	public RestStatus ERROR_IP_LIMIT = new RestStatus() {

		public int getCode() {
			return 1004;
		}

		public String getMsg() {
			return "ip拒绝访问";
		}

	};

	public RestStatus ERROR_LIMIT_COUNT_IP = new RestStatus() {

		public int getCode() {
			return 1005;
		}

		public String getMsg() {
			return "ip访问次数限制";
		}

	};

	public RestStatus ERROR_LIMIT_COUNT_USER = new RestStatus() {

		public int getCode() {
			return 1006;
		}

		public String getMsg() {
			return "用户访问次数限制";
		}

	};

	public RestStatus ERROR_UNAUTHORIZED = new RestStatus() {

		public int getCode() {
			return 1007;
		}

		public String getMsg() {
			return "用户未登录认证";
		}

	};

	public RestStatus ERROR_TOKEN = new RestStatus() {

		public int getCode() {
			return 1008;
		}

		public String getMsg() {
			return "非法token";
		}

	};

	public RestStatus ERROR_OPTIMISTICLOCK = new RestStatus() {

		public int getCode() {
			return 1009;
		}

		public String getMsg() {
			return "数据版本冲突";
		}

	};

	public RestStatus ERROR_NO_ACCESS_PERMISSION = new RestStatus() {

		public int getCode() {
			return 1010;
		}

		public String getMsg() {
			return "用户没有权限";
		}

	};

	public RestStatus ERROR_FILE_UPLOAD_FAIL = new RestStatus() {

		public int getCode() {
			return 1011;
		}

		public String getMsg() {
			return "文件上传失败";
		}

	};

	public RestStatus ERROR_PERMISSON = new RestStatus() {

		public int getCode() {
			return 1012;
		}

		public String getMsg() {
			return "权限校验失败";
		}

	};
	public RestStatus ERROR_PERMISSON_CHECK = new RestStatus() {

		public int getCode() {
			return 1013;
		}

		public String getMsg() {
			return "您没有访问权限";
		}

	};
	
	public RestStatus ERROR_TOKEN_FAILURE = new RestStatus() {

		public int getCode() {
			return 1014;
		}

		public String getMsg() {
			return "认证失效";
		}

	};

	public RestStatus ERROR_NO_ACCESS_PERMISSION_2 = new RestStatus() {

		public int getCode() {
			return 1016;
		}

		public String getMsg() {
			return "用户没有权限访问这个接口";
		}

	};

}
