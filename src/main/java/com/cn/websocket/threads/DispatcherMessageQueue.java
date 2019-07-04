package com.cn.websocket.threads;

import com.cn.websocket.TcpApplication;
import com.cn.websocket.entity.*;
import com.cn.websocket.exception.JwtAuthenticationException;
import com.cn.websocket.exception.ServerException;
import com.cn.websocket.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DispatcherMessageQueue extends MessageQueue<ImmutablePair<Channel, byte[]>> {

    @Autowired
    private ObjectMapper objectMapper;

//    @Autowired
//    private JwtService jwtService;

    @Autowired
    private AnonConfig anonConfig;

    @Autowired
    @Qualifier("coreThreadExecutor")
    private StandardThreadExecutor executor;
    
    @Autowired
    private ChannelCache channelCache;
    
    private MessageQueue<Runnable> singleMessageQueue = new MessageQueue<Runnable>() {
		
		@Override
		protected void execute(Runnable message) {
			message.run();
		}
		
	};

//    @Autowired
//    private TcpPermissionService tcpPermissionService;
    
//    @Autowired
//	private RedisTemplate<Serializable, Object> redisTemplate;

    @PostConstruct
    public void init() {
        executor.start();
        singleMessageQueue.start();
        start();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(ImmutablePair<Channel, byte[]> pair) {
        Channel channel = pair.left;
        Map<String, Object> requestMap = null;
        String requestStr = null;
        try {
        	requestStr = new String(pair.right, "UTF-8");
            requestMap = objectMapper.readValue(requestStr, Map.class);
        } catch (Exception e) {
            channel.writeAndFlush(CommonUtil.buildResponseData("", RestStatus.ERROR_PARAM_FORMAT, Integer.MAX_VALUE));
            log.error("REQUEST_PARAM|{}", requestStr);
            log.error("", e);
            return;
        }
        final Map<String, Object> paramMap = requestMap;
        String cmd = (String) paramMap.get("cmd");
        String token = (String) paramMap.get("token");
        Integer requestId = (Integer) paramMap.get("requestId");
        try {
        	CommandMapping commandMapping = TcpApplication.getCommandMapping(cmd);
        	Runnable runnable = () -> {
            	boolean isSucc = false;
            	Map<String, Object> param = null;
            	if (paramMap.containsKey("param")) {
            		param = (Map<String, Object>) paramMap.get("param");
            	} else {
            		param = new HashMap<>();
            	}
                try {
                    Method method = commandMapping.getMethod();
                    Parameter[] parameters = method.getParameters();
                    Object[] paramValues = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        paramValues[i] = getParameterValue(param, parameters[i]);
                    }
//                    verifyToken(commandMapping.getUri(), token, channel);
                    ChannelContext.set(channel);
                    Object object = method.invoke(commandMapping.getObject(), paramValues);
                    ResponseEntity<?> response = (ResponseEntity<?>) object;
                    if (response.getBody() instanceof ResponseDTO) {
                        channel.writeAndFlush(CommonUtil.buildResponseData(cmd, (ResponseDTO<?>) response.getBody(), requestId));
                    } else {
                        channel.writeAndFlush(CommonUtil.buildResponseData(cmd, (RestError) response.getBody(), requestId));
                    }
                    isSucc = true;
                } catch (ServerException e) {
                    channel.writeAndFlush(CommonUtil.buildResponseData(cmd, e, requestId));
                } catch (Exception e) {
                    if (e instanceof InvocationTargetException) {
                        InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                        if (invocationTargetException.getTargetException() instanceof ServerException) {
                            channel.writeAndFlush(CommonUtil.buildResponseData(cmd,
                                    (ServerException) ((InvocationTargetException) e).getTargetException(), requestId));
                            return;
                        } else if (invocationTargetException.getTargetException() instanceof JwtAuthenticationException) {
                            channel.writeAndFlush(CommonUtil.buildResponseData(cmd,
                                    new ServerException(RestStatus.ERROR_TOKEN), requestId));
                            return;
                        }
                    }
                    channel.writeAndFlush(CommonUtil.buildResponseData(cmd, RestStatus.ERROR_SYSTEM, requestId));
                    log.error("", e);
                } finally {
                	/*try {
                		JWTAuthenticatedUserPrincipal auth = getJWTAuthenticatedUserPrincipal();
                		Long userId = auth == null ? null : auth.getUid();
                		PlatformEnum platform = auth == null ? null : auth.getPlatform();
                		InetSocketAddress address = channelCache.getChannelRealAddress(channel);
                		String ip = address.getAddress().getHostAddress();
						LogUtil.flowLog.info("{}|{}|{}|TCP|{}|{}|{}", cmd, userId, platform, isSucc, ip, 
								objectMapper.writeValueAsString(param));
					} catch (JsonProcessingException e) {
						log.error("", e);
					}*/
                	SecurityContextHolder.clearContext();
                    ChannelContext.remove();
                }
            };
            if(commandMapping.isSingleExecute()) {
        		singleMessageQueue.addMessage(runnable);
        	} else {
        		executor.execute(runnable);
        	}
        } catch (ServerException e) {
        	log.error("ERROR_PARAM: " + requestMap.toString(), e);
            channel.writeAndFlush(CommonUtil.buildResponseData(cmd, e, requestId));
            return;
        }
    }
    
  /*  private JWTAuthenticatedUserPrincipal getJWTAuthenticatedUserPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof JWTAuthenticatedUserPrincipal) {
				return (JWTAuthenticatedUserPrincipal) principal;
			}
		}
		return null;
	}*/
    
    /*private boolean verifyToken(String uri, String jwt, Channel channel) {
        List<String> anonList = anonConfig.getAnon();
        boolean needVerify = true;
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        for (String anon : anonList) {
            if (anon.equals(uri)) {
                needVerify = false;
                break;
            } else {
                if (anon.endsWith("/**")) {
                    if (uri.startsWith(anon.substring(0, anon.length() - 2))) {
                        needVerify = false;
                    }
                }
            }
        }
        if (needVerify || !StringUtils.isBlank(jwt)) {
            Claims claims;
            if(needVerify && !redisTemplate.hasKey(RedisKey.FUTURES_USER_TOKEN_KEY + jwt)) {
            	throw new ServerException(RestStatus.ERROR_UNAUTHORIZED);
            }
            try {
                claims = jwtService.verify(jwt);
            } catch (AuthenticationException e) {
            	if(needVerify) {
            		throw new ServerException(RestStatus.ERROR_TOKEN);
            	} else {
            		return true;
            	}
            }
            JWTAuthenticatedUserPrincipal principal = new JWTAuthenticatedUserPrincipal(claims);
            JwtAuthenticationToken token = new JwtAuthenticationToken(principal, jwt, null);
            SecurityContextHolder.getContext().setAuthentication(token);
            Long userId = token.getPrincipal().getUid();
            //权限校验
            tcpPermissionService.checkPermission(uri, userId);
        }
        return true;
    }*/

    private Object getParameterValue(Map<String, Object> param, Parameter parameter) {
        RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
        try {
            if (requestBody != null) {
                if (param == null && requestBody.required()) {
                    throw new ServerException(RestStatus.ERROR_PARAM);
                }
                return objectMapper.readValue(objectMapper.writeValueAsString(param), parameter.getType());
            } else {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    String name = requestParam.name().trim().equals("") ? parameter.getName() : requestParam.name();
                    Object value = param.get(name);
                    if (value == null) {
                        if (requestParam.required()) {
                            throw new ServerException(RestStatus.ERROR_PARAM);
                        } else {
                            return null;
                        }
                    } else {
                        return getValue(param, parameter, value, parameter.getType());
                    }
                } else {
                    Object value = param.get(parameter.getName());
                    return getValue(param, parameter, value, parameter.getType());
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw new ServerException(RestStatus.ERROR_PARAM_FORMAT);
        }
    }

    private Object getValue(Map<String, Object> param, Parameter parameter, Object value, Class<?> type)
            throws Exception {
        if (value == null) {
            return null;
        }
        if (type == String.class) {
            return (String) value;
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(value.toString());
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value.toString());
        } else if (type == short.class || type == Short.class) {
            return Short.parseShort(value.toString());
        } else if (type == double.class || type == Double.class) {
            return (Double) value;
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value.toString());
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value.toString());
        } else if (type == boolean.class || type == Boolean.class) {
            return (Boolean) value;
        } else if (type == char.class || type == Character.class) {
            return (Character) value;
        } else if (type.isEnum()) {
            return getEnumObject((String) value, type);
        } else {
            return objectMapper.readValue(objectMapper.writeValueAsString(value),
                    parameter.getType());
        }
    }

    private Object getEnumObject(String value, Class<?> clazz) throws Exception {
        Method method = clazz.getMethod("valueOf", String.class);
        return method.invoke(clazz, value);
    }

}
