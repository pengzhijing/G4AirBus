package com.shima.smartbushome.assist;

import com.shima.smartbushome.database.Savemarco;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/10/19.
 */
public class marcoCompare implements Comparator<Savemarco> {
    /*
             * int compare(Student o1, Student o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
    public int compare(Savemarco o1, Savemarco o2) {

        //按照学生的年龄进行升序排列
        if (o1.sentorder > o2.sentorder) {
            return 1;
        }
        if (o1.sentorder == o2.sentorder) {
            return 0;
        }
        return -1;
    }
}
