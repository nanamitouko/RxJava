/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package io.reactivex.internal.operators.completable;

import java.util.concurrent.TimeUnit;

import io.reactivex.*;
import io.reactivex.internal.disposables.SequentialDisposable;

public final class CompletableTimer extends Completable {

    final long delay;
    final TimeUnit unit;
    final Scheduler scheduler;

    public CompletableTimer(long delay, TimeUnit unit, Scheduler scheduler) {
        this.delay = delay;
        this.unit = unit;
        this.scheduler = scheduler;
    }



    @Override
    protected void subscribeActual(final CompletableObserver s) {
        SequentialDisposable sd = new SequentialDisposable();
        s.onSubscribe(sd);
        if (!sd.isDisposed()) {
            sd.replace(scheduler.scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    s.onComplete();
                }
            }, delay, unit));
        }
    }

}
