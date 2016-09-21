package util;

/**
 * Created by ！ on 2016/9/20.
 */
public class Byte2String {
    static public String convert(long bite){
        char[] pp = {' ','K','M','G','T','E'};
        int i = 0;
        double b = bite;
        while(b>=1024){
            b/=1024;
            i++;
        }
        return String.format("%.2f", b)+pp[i]+'B';
    }
}
