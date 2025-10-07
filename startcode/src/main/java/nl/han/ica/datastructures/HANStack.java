package nl.han.ica.datastructures;

import java.util.List;

public class HANStack<T> implements IHANStack<T> {

    List<T> list;

    public HANStack(List<T> list) {
        this.list = list;
    }

    public HANStack() {
        this.list = list;
    }

    @Override
    public void push(T value) {
        list.add(value);
    }

    @Override
    public T pop() {

        T o = list.get(list.toArray().length - 1);
        list.remove(o);

        return o;
    }

    @Override
    public T peek() {
        return list.get(list.toArray().length - 1);
    }
}


