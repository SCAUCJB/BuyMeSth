package base;

import java.io.Serializable;

/**
 * Created by John on 2016/8/1.
 */
public interface BaseEntity {
    class BaseBean implements Serializable {
        public long id;
        public String objectId;
    }
}
