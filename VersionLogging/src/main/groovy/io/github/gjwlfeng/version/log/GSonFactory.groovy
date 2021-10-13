package io.github.gjwlfeng.version.log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GSonFactory {

    public static GSonFactory sGSonFactory;

    private Gson mGSon;

    private GSonFactory() {
        mGSon= new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public static GSonFactory getInstance() {
        if (sGSonFactory == null) {
            synchronized (GSonFactory.class) {
                if (sGSonFactory == null) {
                    sGSonFactory = new GSonFactory();
                }
            }
        }
        return sGSonFactory;
    }

    public Gson getGSon() {
        return mGSon;
    }
}
