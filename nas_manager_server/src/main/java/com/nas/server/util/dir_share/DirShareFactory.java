package com.nas.server.util.dir_share;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Set;

public class DirShareFactory {

    private static final HashMap<Protocol, ShareServer> cache = new HashMap<>();

    public static ShareServer generateShareUtil(Protocol shareProtocol) throws InstantiationException, IllegalAccessException {
        if (cache.containsKey(shareProtocol)) {
            return cache.get(shareProtocol);
        }
        Reflections reflections = new Reflections("com.nas.server.util.dir_share");
        Set<Class<? extends ShareServer>> classSet = reflections.getSubTypesOf(ShareServer.class);
        ShareServer obj = null;
        for (Class<? extends ShareServer> clazz : classSet){
            // 实例化获取到的类
            ShareServer tmpObj = clazz.newInstance();
            cache.put(tmpObj.getProtocolName(), tmpObj);
            if (shareProtocol.equals(tmpObj.getProtocolName())) {
                obj = tmpObj;
            }
        }
        return obj;
    }

}
