package com.example.aiyan.basiccoroutines;

import androidx.annotation.NonNull;

import org.junit.Test;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class ExampleCoroutine {

    @Test
    public void cps() {
        Object data1 = Suspend_functionKt.getData1(new Continuation<Unit>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {

            }
        });
        System.out.println(data1);

        Object data2 = Suspend_functionKt.getData2("data2", new Continuation<String>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {

            }
        });
        System.out.println(data2);

        Object data3 = Suspend_functionKt.getData3("data3", new Continuation<String>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {

            }
        });
        System.out.println(data3);
    }
}
