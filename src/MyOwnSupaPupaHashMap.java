import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code MyOwnSupaPupaHashMap} - моя реализация хешмапы
 * Интерфейс: get, put, delete
 * Остальные методы приватные и служат "внутри"
 * Рычаги управления выведены наверх
 * Автоматически сжимается и растет при вызове put, delete
 * @param <K> Ключ для записи
 * @param <V> Значение
 * @author vmarakushin
 * @version 1.1
 */
public class MyOwnSupaPupaHashMap<K,V> {

    /** <Рычаги управления> */

    /** Дефолтная длина массива бакетов pairsList */
    private final static int DEFAULT_INITIAL_CAPACITY = 16;

    /** Фактор заполнения массива для расширения */
    private final static float DEFAULT_MAX_LOAD_FACTOR = 0.75f;

    /** Фактор заполнения массива для сжатия */
    private final static float DEFAULT_MIN_LOAD_FACTOR = 0.3f;

    /** Множитель расширения/сжатия массива */
    private final static float SCALING_FACTOR = 2.0f;

    /** </Рычаги управления> */


    private Pair<K,V>[] pairsList;

    private int capacity;


    public MyOwnSupaPupaHashMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }
    @SuppressWarnings("unchecked")
    public MyOwnSupaPupaHashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.pairsList = (Pair<K, V>[]) new Pair[capacity];
    }


    /**
     * Метод {@code get()} возвращает значение ноды по ключу
     * @param key ключ
     * @return значение
     */
    public V get(K key) {
        if (key == null) return null;
        int index = hash(key);
        if (pairsList[index] == null) {return null;}
        else {
            Pair<K,V> current = pairsList[index];
            while (current != null) {
                if (current.getKey().equals(key)) {
                    return current.getValue();
                }
                current = current.getNext();
            }
        }
        return null;
    }


    /**
     * Метод {@code put()} кладет в мапу указанную пару ключ-значение
     * @param key ключ
     * @param value значение
     * @param autoHandle флаг авто-ресайза(опционально, дефолт - тру)
     */
    public void put(K key, V value, boolean autoHandle) {

        if (key == null) return;
        Pair<K,V> newPair = new Pair<>(key, value);
        int index = hash(key);

        if (pairsList[index] == null) {
            pairsList[index] = newPair;
        }
        else {
            Pair<K,V> current = pairsList[index];
            Pair<K,V> previous = null;

            while (current != null) {
                if (current.getKey().equals(key)) {
                    current.setValue(newPair.getValue());
                    return;
                }
                previous = current;
                current = current.getNext();
            }
            previous.setNext(newPair);
        }
        if (autoHandle) autoHandleCapacity();
    }
    public void put(K key, V value)
    {this.put(key, value,true);}


    /**
     * Метод {@code delete()} удаляет ноду по ключу
     * @param key ключ
     * @param autoHandle флаг авто-ресайза(опционально, дефолт - тру)
     */
    public void delete(K key, boolean autoHandle) {
        if (key == null) return;
        int index = hash(key);
        if (pairsList[index] != null) {
            Pair<K,V> current = pairsList[index];
            Pair<K,V> previous = null;
            while (current != null) {
                if (current.getKey().equals(key)) {
                    if (previous == null) {
                        pairsList[index] = current.getNext();
                    } else {
                        previous.setNext(current.getNext());
                    }
                }
                previous = current;
                current = current.getNext();
            }
        }
        if (autoHandle) autoHandleCapacity();
    }
    public void delete(K key)
    {this.delete(key,true);}


    /**
     * Метод {@code hash()} хэш-функция данной мапы
     * @param key ключ
     * @return индекс в массиве по ключу
     */
    private int hash(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode() * 31 % capacity);
    }


    /**
     * Класс {@code Pair} - внутренний класс {@link MyOwnSupaPupaHashMap}, нода хешмапы.
     * Содержит в себе ключ, значение, ссылку на следующую ноду, а так же необходимые геттеры и сеттеры.
     * @param <K> Ключ
     * @param <V> Значение
     */
    private class Pair<K, V> {

        private K key;
        private V value;
        private Pair<K,V> next;


        private Pair(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }


        private K getKey() {
            return key;
        }


        private V getValue() {
            return value;
        }


        private Pair<K,V> getNext() {
            return next;
        }


        private void setValue(V value) {
            this.value = value;
        }


        private void setNext(Pair<K,V> next) {
            this.next = next;
        }
    }


    /**
     * Метод {@code autoHandleCapacity} - логика авто-ресайза.
     * Использует checkWorkLoad() для определения необходимости расширения/сжатия
     * Использует updateCapacity() для масштабирования мапы
     */
    private void autoHandleCapacity() {
        int move = checkWorkLoad();
        if (move != 0) {
            updateCapacity(move);
        }
    }


    /**
     * Метод {@code checkWorkLoad} - определяет, нужно ли масштабирование и в какую сторону
     * @return необходимое действие: 1 - увеличение, -1 - сжатие, 0 - действий не требуется
     */
    private int checkWorkLoad() {
        int busyIndexes = 0;
        for (Pair<K,V> kvPair : pairsList) {
            if (kvPair != null) busyIndexes++;
        }
        float currentLoadFactor = (float) busyIndexes / capacity;

        if (currentLoadFactor > DEFAULT_MAX_LOAD_FACTOR) return 1;

        if (currentLoadFactor < DEFAULT_MIN_LOAD_FACTOR) return -1;

        return 0;
    }


    /**
     * Метод {@code updateCapacity} - инициализирует массив бакетов с новым капасити
     * @param move действие: 1 - увеличение, -1 - сжатие
     */
    private void updateCapacity(int move) {
        switch (move) {
            case 1 -> capacity = (int) (capacity * SCALING_FACTOR);
            case -1 -> capacity = (int) (capacity / SCALING_FACTOR);
            default -> {return;}
        }

        List<Pair<K,V>> temp = new ArrayList<>();

        for (Pair<K,V> kvPair : pairsList) {
            if (kvPair != null) {
                Pair<K,V> current = kvPair;
                while (current != null) {
                    temp.add(current);
                    current = current.getNext();
                }
            }
        }

        pairsList = (Pair<K, V>[]) new Pair[capacity];
        for (Pair<K,V> pair : temp) {
            pair.setNext(null);
            put(pair.getKey(), pair.getValue(), false);
        }
        temp.clear();
    }
}