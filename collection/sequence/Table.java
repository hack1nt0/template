package template.collection.sequence;


import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dy on 2017/2/4.
 * Row based table.
 */
public class Table {

    Table table;

    public <T> T get(int i) {
        return table.get(i);
    }

    public Table(){}

    public Table(Table that) {
        this.table = that;
    }

    public <T> Table(AbstractList<T> arr) {
        this(new Table() {
            @Override
            public <T> T get(int i) {
                return (T)arr.get(i);
            }
        });
    }

    public <T> Table(T[] arr) {
        this(new Table() {
            @Override
            public <T> T get(int i) {
                return (T)arr[i];
            }
        });
    }

    public void createIndex(int colNo) {

    }
}
