package com.example.my.pkg;

import com.google.common.collect.ImmutableList;
import jp.ac.tohoku.ecei.sb.gprdb.app;

import java.util.Collection;

public class MyApplicationSourceProviderImpl implements ApplicationSourceProvider {

    @Override
    public Collection<Object> getSources() {
        return ImmutableList.of(MyApplicationSource.class);
    }

}
