package com.redux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static mini.com.google.common.base.Preconditions.checkState;

public abstract class Store<A extends Action, S extends State> {

    /**
     * Store hold the reference to the app's data(state) and combined reducer(action reactor)
     * @param initialState the initial state of the app.
     * @param reducer the combined reducer. could have multiple child reducers
     * @param <A> that extends an Action class
     * @param <S> that extends a
     * @return
     */
    static public <A extends Action, S extends State> CoreStore<A, S> create(S initialState, Reducer<A, S> reducer) {
        return new CoreStore<>(initialState, reducer);
    }

    public abstract Subscription subscribe(Subscriber subscriber);

    public abstract S getState();

    public abstract void dispatch(A action);

    static class CoreStore<A extends Action, S extends State> extends Store<A,S> {

        private static final int LISTENERS_INITIAL_CAPACITY = 100;

        private final List<Subscriber> subscribers;
        private final Reducer<A, S> reducer;
        private final AtomicBoolean isReducing;

        private S currentState;

        CoreStore(S initialState, Reducer<A, S> reducer) {
            this.reducer = reducer;
            this.currentState = initialState;
            this.subscribers = new ArrayList<>(LISTENERS_INITIAL_CAPACITY);
            this.isReducing = new AtomicBoolean(false);
        }

        @Override public Subscription subscribe(Subscriber subscriber) {
            subscribers.add(subscriber);
            return Subscription.create(subscribers, subscriber);
        }

        @Override public S getState() {
            return currentState;
        }

        /**
         * dispatch() method is guaranteed to be called one-by-one by the store object
         * @param action
         */
        @Override public void dispatch(final A action) {
            checkState(!isReducing.get(), "Can not dispatch an action when an other action is being processed");

            isReducing.set(true);
            currentState = reduce(action, currentState);
            isReducing.set(false);

            notifyStateChanged();
        }

        /**
         * call the SAM(single abstract method) of the reducer to react to current state and passed Action
         *
         * @param action
         * @param state
         * @return
         */
        private S reduce(A action, S state) {
            return reducer.call(action, state);
        }

        /**
         * notify the subscribers that the state has changed. All the UI components must implement Subscriber
         * interface to react to state change. In React-Native, it's like the MapStateToProps in connect() method.
         * In anvil , you just need to change the model , and it'll do the notify for you
         */
        private void notifyStateChanged() {
            for (int i = 0, size = subscribers.size(); i < size; i++) {
                subscribers.get(i).onStateChanged();
            }
        }
    }
}
