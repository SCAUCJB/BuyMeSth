package cache;

import java.io.Serializable;

/**
 * Created by John on 2016/8/3.\
 * 缓存接口，初步定义了存数据和取数据以及清理
 */

public interface IDiskCacheManage {

    <T extends Serializable> void put(String fileName, String folder,T data);

    <T> T get(String fileName, String folder);

    void clear();
}
