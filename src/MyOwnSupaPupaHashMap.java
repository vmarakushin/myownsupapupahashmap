
import java.util.*;
///Сразу признаю, не кривя душой, списывал с тырнета, но разобрался в каждой строчке, операторе и символе




/// Норм название чо вы....
public class MyOwnSupaPupaHashMap<K,V> {

    /// 16 это скучно!
    private final static int DEFAULT_INITIAL_CAPACITY = 27;
    private final static float DEFAULT_MAX_LOAD_FACTOR = 0.75f;
    private final static float DEFAULT_MIN_LOAD_FACTOR = 0.3f;
    private int capacity;


    /// Тута будем хранить массив пар(значение - ключь)
    private Pair<K,V>[] pairsList;


    /// Вот этот мув мне понравился! Забыли принести с собой капасити? не беда, у нас есть свой!
    public MyOwnSupaPupaHashMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }
    public MyOwnSupaPupaHashMap(int initialCapacity) {
        /// КСТА мистер ДЖИПИТИ мне рассказывал страшную сказку про то как не получится инициализировать массив без каста
        /// this.pairsList = (Pair<K,V>[]) new Pair[initialCapacity];
        this.pairsList = new Pair[initialCapacity];
        this.capacity = initialCapacity;
    }


    /// Вот она наша пара! И никакого "Дома 2"
    private class Pair <K,V>{
        /// нэйминги странные потому что  при попытке назвать сеттер setKey идейка стала на меня ругаться
        private K pairKey;
        private V pairValue;
        private Pair next;


        /// Конструкторы на все случи жизни

        public Pair(K key, V value) {
            this.pairKey = key;
            this.pairValue = value;
        }

        public Pair(K key, V value, Pair next) {
            this.pairKey = key;
            this.pairValue = value;
            this.next = next;
        }


        /// Чат джипити сказал что тут не нужны геттеры и сеттеры, рандомный чувак с форума сказал что здесь можно обойтись без геттеров и сеттеров
        /// Пролетающая мимо ворона, заглянув ко мне в окно, каркнула что оно быстрее работает без лишних абстракций
        /// Но это моя супа пупа хэш мапа! (хоть чем-то должна отличаться от  реализации с тырнета))

        public void pairKeySet(K pairKey){
            this.pairKey = pairKey;
        }

        public void pairValueSet(V pairValue){
            this.pairValue = pairValue;
        }

/*        public Object getPairKey(){
            return pairKey;
        }*/

        public K getPairKey(){
            return pairKey;
        }



        ///UPD чтобы не было лишних кастов и дженериков мы укажем правильный тип возвращаемого
/*
        public Object getPairValue(){
            return pairValue;
        }
*/

        public V getPairValue(){
            return pairValue;
        }

        public Pair getNext(){
            return next;
        }

        public void setNext(Pair next){
            this.next = next;
        }



    }

    /// Теперь сделаем(скоммуниздим и разберемся) функцию хэширования, далее она нам пригодится
    private int hash(K key){

        /// эта строчка от себя (и от идейки))) --- все с null ключами пихаем в 0 индекс и не мучаемся
        if (key == null) return 0;
        /// кстати, это тут  не очень нужно, ведь нулевые ключи мы обрабатываем в остальных методах! но оставим... на всякий......

        /// return Math.abs(key.hashCode() % capacity)
        /// Для начала стандартный  хэшКоде() из Object натравим на ключь
        int a = key.hashCode();
        /// Проведем деление с остатком на капасити
        int b = a % capacity;
        /// От остатка возьмем только модуль, ибо нам может достаться отрицательный хэш, что для работы с массивами не очень полезно
        int c = Math.abs(b);
        /// Ну все, этого хватит
        return c;


    }


    /// Ну а теперь ближе к Тэзэшечке - put(),get(),delete()


    public void put(K key, V value){
        /// тут идейка предложила откидывать пары с пустым значением, но нет, мы их оставим
        /// if (key == null || value == null) return;
        if (key == null) return;

        /// Я понял что такое дженерики, понял что такое вайлдкартс, но я так и не понял, нахрена совать их везде, где только можно, даже там, где уже ошибиться невозможно(наверное)?
        ///Pair <K,V> pair = new Pair<>(key, value);
        Pair newPair = new Pair(key, value);

        /// Ну чтож пора подумоть куда мы пихнем нашу пару
        int index = hash(key);

        /// И вот туда и пихнем, при условии того, что место свободно
        if (pairsList[index] == null) {pairsList[index] = newPair;}

        /// Ну а если не свободно
        else {
            /// Берем одно в левую руку, другое - во вторую
            Pair current = pairsList[index];
            Pair previous = null;

            /// Переберем весь список
            while (current != null) {
                /// <--- и если ключи у нас сошлись, то обновляем значение
                if (current.getPairKey().equals(key)) {
                    ///                               не ну тут и у меня глаза разбежались, но сдавать назад я не собираюсь!
                    current.pairValueSet(newPair.getPairValue());
                    return;
                }
                /// если ключи не сошлись то обновляем содержимое рук и на новую итерацию <---
                previous = current;
                current = current.getNext();
            }

            /// так вот, если мы перебрали весь односвязный ссылочный список и не нашли пары с идентичным ключом, записываем пару в конец этого списка
        previous.setNext(newPair);
        }

    }


    public V get(K key){
        /// Ну да по нулевому ключу мы не много чего найдем
        if (key == null) return null;
        /// Поищем с какого индекса нам спросить
        int index = hash(key);
        /// Если там пусто то вернем пустоту(в душе)
        if (pairsList[index] == null) {return null;}
        /// Ну а если нет, поедем перебирать список, пока не найдем пару с тем же ключом
        else {
            Pair current = pairsList[index];
            while (current != null) {
                if (current.getPairKey().equals(key)) {
                    /// Тут мне идейка сама каст подсунула
                    /// Я переделал геттер чтобы он возвращал V, а не  Object! Но ей все равно мало.........
                    return (V) current.getPairValue();

                }
                current = current.getNext();
            }
        }
        /// Если мы ничего не нашли то вот он вам чудесный и распрекрасный null
        return null;
    }

    /// Кстати, этот метод я "напишу" сам
    /// Ну как напишу, стырю из get() алгоритм выбора Pair, потом просто ссылки обрубить
    /// P.S. я пол метода просто жал на tab, мне нравится идейка!
    public void delete(K key){
        /// Без ключа - пока
        if (key == null) return;
        /// Ищем индекс
        int index = hash(key);
        /// Если в нем и так пусто, то мы закончили
        if (pairsList[index] == null) {return;}
        /// Если в нем не пусто, то пойдем по нашим парам искать ту, которой меньше всего повезло
        else {
            Pair current = pairsList[index];
            Pair previous = null;
            while (current != null) {
                /// При совпадении ключей
                if (current.getPairKey().equals(key)) {
                    /// Алгоритм для первого элемента списка - просто следующий поставить в индекс в массиве, даже если это будет null
                    if (previous == null){
                        pairsList[index] = current.getNext();
                        return;
                    }
                    /// А если это не первый элемент, то нужно просто "срастить" список
                    else {
                        previous.setNext(current.getNext());
                        return;
                    }
                /// Вот и все... Джава очень добрая, позволит нашей "паре" доживать свои деньки в счастливом безделии, до следующего "Пришествия" Дюка
                }
                previous = current;
                current = current.getNext();
            }
        }
    }


    /// Вот эту штуку почти полностью сам написал, минимум копипасты
    public void checkAndRefactor(){
        int busyIndexes = 0;
        /// Создадим массив для временного хранения всех пар из мапы
        ArrayList<Pair> tempPairsList = new ArrayList<>();
        /// Посчитаем сколько индексов занято
        for (Pair<K, V> kvPair : pairsList) {
            if (kvPair != null) busyIndexes++;
        }
        /// Приведем все интежеры во время вычисления к флоутам
        float currentLoadFactor = (float) busyIndexes / (float) capacity;

        /// Сценарий переполнения
        if (currentLoadFactor > DEFAULT_MAX_LOAD_FACTOR){
            ///Проходимся по всем индексам
            for (Pair<K, V> kvPair : pairsList){
                if (kvPair != null){
                    Pair current = kvPair;
                    /// И по всем вложенным парам
                    while (current != null) {
                        /// И запихиваем во временный массив
                        tempPairsList.add(current);
                        current = current.getNext();
                    }

                }
            }
            ///Удваиваем капасити
            capacity = capacity * 2;
            ///Инициализируем наше хранилище с новым капасити
            this.pairsList = new Pair[capacity];
            ///Распихиваем сохраненные данные
            for (Pair<K, V> kvPair : tempPairsList){
                put(kvPair.getPairKey(), kvPair.getPairValue());
            }

        }

        /// Сценарий недобора
        if (currentLoadFactor < DEFAULT_MIN_LOAD_FACTOR){
            ///Проходимся по всем индексам
            for (Pair<K, V> kvPair : pairsList){
                if (kvPair != null){
                    Pair current = kvPair;
                    /// И по всем вложенным парам
                    while (current != null) {
                        /// И запихиваем во временный массив
                        tempPairsList.add(current);
                        current = current.getNext();
                    }

                }
            }
            ///Ужимаем капасити
            capacity = capacity / 2;
            ///Инициализируем наше хранилище с новым капасити
            this.pairsList = new Pair[capacity];
            ///Распихиваем сохраненные данные
            for (Pair<K, V> kvPair : tempPairsList){
                put(kvPair.getPairKey(), kvPair.getPairValue());
            }
        }
    }
}
