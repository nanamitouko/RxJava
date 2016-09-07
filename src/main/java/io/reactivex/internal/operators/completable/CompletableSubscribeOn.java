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

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.*;

public final class CompletableSubscribeOn extends Completable {
    final CompletableSource source;

    final Scheduler scheduler;

    public CompletableSubscribeOn(CompletableSource source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(final CompletableObserver s) {

        final SubscribeOnObserver parent = new SubscribeOnObserver(s);
        s.onSubscribe(parent);

        Disposable f = scheduler.scheduleDirect(new Runnable() {
            @Override
            public void run() {
                source.subscribe(parent);
            }
        });

        parent.task.replace(f);

    }

    static final class SubscribeOnObserver
    extends AtomicReference<Disposable>
    implements CompletableObserver, Disposable {
        /** */
        private static final long serialVersionUID = 7000911171163930287L;

        final CompletableObserver actual;

        final SequentialDisposable task;

        public SubscribeOnObserver(CompletableObserver actual) {
            this.actual = actual;
            this.task = new SequentialDisposable();
        }

        @Override
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        @Override
        public void onError(Throwable e) {
            actual.onError(e);
        }

        @Override
        public void onComplete() {
            actual.onComplete();
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
            task.dispose();
        }

        @Override
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(this);
        }
    }

}
