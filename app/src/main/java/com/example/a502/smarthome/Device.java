package com.example.a502.smarthome;

import java.io.Serializable;

/**
 * Created by user562 on 2019-04-03.
 */

public class Device implements Serializable {
    String name;
    int type;                   //0:led 1:blind 2:fan 3:temp
    int[] room = new int[3];    //0:기본 1~2추가
    String state;
}
