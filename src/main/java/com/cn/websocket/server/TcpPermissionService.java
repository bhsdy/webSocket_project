package com.cn.websocket.server;

import com.cn.websocket.entity.AnonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TcpPermissionService {

   /* @Autowired
    protected PermissionAPI permissionAPI;

    @Autowired
    private RedisTemplate<Serializable, Object> redisTemplate;*/

    @Autowired
    private AnonConfig anonConfig;

//    @Value("${openPermission}")
//    private Boolean openPermission;

    public Boolean checkPermission(String url,Long userId){
        //是否为配置的白名单
        List<String> anonList = anonConfig.getAnon();
        for (String anon:anonList) {
            if(url.equals(anon)){
                return true;
            }
            if(anon.length() > 3 && anon.substring(anon.length()-3).equals("/**")){
                String str = anon.substring(0,anon.length()-3);
                if(str != null
                        &&str.length() <= url.length()
                        && str.equals(url.substring(0,str.length()))){
                    return true;
                }
            }
        }
        //判断是否需要过滤
        /*if(isWhite(url,permissionAPI,redisTemplate)){
            return true;
        }
        String key =new StringBuffer(RedisKey.FUTURES_USER_PERMISSION_USER_KEY).append(userId).toString();
        List<InterfaceUrlDTO> interfaceUrlDTOList;
        try {
            if(redisTemplate.hasKey(key)){
                interfaceUrlDTOList = (List<InterfaceUrlDTO>) redisTemplate.opsForHash().get(key, userId);
            }else {
                interfaceUrlDTOList = permissionAPI.queryInterfaceUrlByUserId(userId);
            }
        } catch (Exception e) {
            try {
                interfaceUrlDTOList = permissionAPI.queryInterfaceUrlByUserId(userId);
            } catch (Exception e1) {
                throw new ServerException(RestStatus.ERROR_PERMISSON);
            }
        }
        boolean tag = false;
        if(!CollectionUtils.isEmpty(interfaceUrlDTOList)){
            for (InterfaceUrlDTO interfaceUrlDTO:interfaceUrlDTOList) {
                if(url.equals(interfaceUrlDTO.getUrl())){
                    tag = true;
                }
            }
        }
        if(!tag){
            throw new ServerException(RestStatus.ERROR_PERMISSON_CHECK);
        }*/
        return true;
    }

    /*private boolean isWhite(String url,PermissionAPI permissionAPI,RedisTemplate redisTemplate){
        String key = RedisKey.FUTURES_USER_PERMISSION_INTERFACE_KEY;
        List<InterfaceUrlDTO> interfaceUrlDTOList;
        try {
            if(redisTemplate.hasKey(key)){
                interfaceUrlDTOList = (List<InterfaceUrlDTO>) redisTemplate.opsForHash().get(key, key);
            }else {
                interfaceUrlDTOList = permissionAPI.queryInterfaceAll();
                redisTemplate.opsForHash().put(key,key,interfaceUrlDTOList);
            }
        } catch (Exception e) {
            interfaceUrlDTOList = permissionAPI.queryInterfaceAll();
        }
        if(! CollectionUtils.isEmpty(interfaceUrlDTOList)){

            for (InterfaceUrlDTO interfaceUrlDTO:interfaceUrlDTOList) {
                if(url.equals(interfaceUrlDTO.getUrl()) && interfaceUrlDTO.isStatus()){
                    return false;
                }
            }
        }
        return true;
    }*/

}
