package com.redux

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

public class Operations @Inject constructor(private val service: Service) {

    fun fetch(): Observable<List<Todo>> {
        return Observable.just(service.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

//        return Observable.create(
//                Observable.OnSubscribe<List<Todo>> {
//                    subscriber ->
//                    try {
//                        subscriber.onNext(service.get())
//                    } catch (e: IOException) {
//                        subscriber.onError(e)
//                    } catch (e: InterruptedException) {
//                        subscriber.onError(e)
//                    }
//
//                    subscriber.onCompleted()
//                })
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}
