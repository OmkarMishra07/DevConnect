package com.example.MergeX.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum Education {
    BTECH("B.Tech"),
    BA("B.A."),
    BSC("B.Sc"),
    BCOM("B.Com"),
    MTECH("M.Tech"),
    PHD("PhD");

    private final String education;

}
