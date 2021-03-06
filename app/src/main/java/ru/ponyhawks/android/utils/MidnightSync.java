package ru.ponyhawks.android.utils;

import com.cab404.chumroll.ChumrollAdapter;
import com.cab404.chumroll.ViewConverter;
import com.cab404.moonlight.framework.ModularBlockParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Binds Moonlight and Chumroll in a fast way
 * Also a very handy tool for synchronizing Chumroll add calls to main thread
 * <p/>
 * Created at 01:51 on 14/09/15
 *
 * @author cab404
 */
public class MidnightSync extends UniteSynchronization implements ModularBlockParser.ParsedObjectHandler {

    /**
     * Drops th item
     */
    public static final int INDEX_REMOVE = -1;
    /**
     * Adds item to the end
     */
    public static final int INDEX_END = -2;

    private final Map<Integer, Binding> bindings;
    private final ChumrollAdapter target;

    public MidnightSync(ChumrollAdapter target) {
        this.bindings = new HashMap<>();
        this.target = target;
    }

    public <A> MidnightSync bind(Integer id, ViewConverter<A> converter) {
        return bind(id, converter, null);
    }

    public <A> MidnightSync bind(Integer id, ViewConverter<A> converter, InsertionRule<A> rule) {
        bindings.put(id, new Binding<>(converter, rule));
        return this;
    }

    public <A> MidnightSync bind(Integer id, Class<? extends ViewConverter<A>> converter) {
        return bind(id, converter, null);
    }

    @SuppressWarnings("unchecked")
    public <A> MidnightSync bind(Integer id, Class<? extends ViewConverter<A>> converter, InsertionRule<A> rule) {
        bindings.put(id, new Binding<>(target.getConverters().getInstance(converter), rule));
        return this;
    }

    @Override
    public void handle(Object object, int key) {
        if (bindings.containsKey(key)) {
            post(new InsertObject(bindings.get(key), object));
            invalidate = true;
        }
    }

    /**
     * Injects view into adapter. All views are being injected in order
     */
    public <V> void inject(V object, ViewConverter<V> use) {
        inject(object, use, null);
    }


    public <V> void inject(V object, ViewConverter<V> use, InsertionRule<V> rule) {
        invalidate = true;
        post(new InsertObject(new Binding<>(use, rule), object));
    }

    private class Binding<Clazz> {
        public Binding(ViewConverter<Clazz> converter, InsertionRule<Clazz> rule) {
            this.converter = converter;
            this.rule = rule;
        }

        ViewConverter<Clazz> converter;
        InsertionRule<Clazz> rule;

        @SuppressWarnings("unchecked")
        void insert(Object object) {
            if (rule == null)
                target.add(converter, ((Clazz) object));
            else {
                int index = rule.indexFor((Clazz) object, converter, target);
                if (index == target.getCount()) index = INDEX_END;

                if (index == INDEX_REMOVE)
                    return;
                if (index == INDEX_END) {
                    target.add(
                            converter,
                            (Clazz) object
                    );
                    return;
                }
                target.add(
                        index,
                        converter,
                        (Clazz) object
                );
            }
        }
    }

    boolean invalidate = false;

    private class InsertObject implements Runnable {
        Binding binding;
        Object object;

        public InsertObject(Binding binding, Object object) {
            if (binding == null) throw new RuntimeException("Binding cannot be empty");
            this.binding = binding;
            this.object = object;
        }

        @Override
        public void run() {
            binding.insert(object);
        }
    }

    public interface InsertionRule<V> {
        /**
         * @see #INDEX_END
         * @see #INDEX_REMOVE
         */
        int indexFor(V object, ViewConverter<V> converter, ChumrollAdapter adapter);
    }

    @Override
    protected void onCycleFinish() {
        if (invalidate)
            target.notifyDataSetChanged();
    }
}
