import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        final  int SIZE = 56, SIZE_GROUP = 10;
        //Створюємо масив випадкових чисел
        int [] numbers = new int[SIZE];
        int [] start_End = start_End();
        fillArray(numbers, start_End[0], start_End[1]);


        System.out.println("Масив:"+ Arrays.toString(numbers));

        //Вираховуємо кількість груп
        int numGroup = (int) Math.ceil((double) numbers.length / SIZE_GROUP);

        // Колекція для збереження майбутніх результатів
        ExecutorService executor = Executors.newFixedThreadPool(numGroup);
        List<Future<Set<Integer>>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numbers.length; i += SIZE_GROUP) {
            int[] group = copy(numbers, i, Math.min(SIZE_GROUP, numbers.length - i));

            Callable<Set<Integer>> task = () -> {
                Set<Integer> results = new CopyOnWriteArraySet<>();
                for (int j = 0; j < group.length - 1; j += 2) {
                    int product = group[j] * group[j + 1];
                    results.add(product);
                }
                return results;
            };
            Future<Set<Integer>> future = executor.submit(task);
            futures.add(future);
        }
        // Збираємо результати
        Set<Integer> finalResults = new CopyOnWriteArraySet<>();
        for (Future<Set<Integer>> future : futures) {
            if (!future.isCancelled()) {
                try {
                    finalResults.addAll(future.get());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Виводимо інформацію про статус завдань
        for (Future<Set<Integer>> future : futures) {
            System.out.println("isDone: " + future.isDone() + ", isCancelled: " + future.isCancelled());
        }

        // Виводимо час виконання програми
        long endTime = System.currentTimeMillis();
        System.out.println("Час виконання програми: " + (endTime - startTime) + " мс");

        // Виводимо результати обробки
        System.out.println("Результати обчислення попарних добутків:");
        System.out.println(finalResults);

        print_detail_result(numbers, finalResults);
        executor.shutdown();

    }
    //Функція для заповнення випадковими числами в діапазоні від start до end масив n
    public static void fillArray(int [] n, int start, int end){
        Random random = new Random();
        //Iterator <Integer> it = n.iterator();
        for (int i = 0; i<n.length;i++ ) {
            n[i] = random.nextInt(start, end+1);
        }
    }
    //Функція для вводу початку і кінця діапазону
    public static int[] start_End(){
        Scanner sc = new Scanner(System.in);
        int start =0, end = 1;
        System.out.print("Введіть початок діапазону: ");
        if(sc.hasNextInt()) {
            start = sc.nextInt();
            System.out.print("Введіть кінець діапазону: ");
            if(sc.hasNextInt()) {
                end = sc.nextInt();

            } else {
                System.out.println("Не ціле число");
            }
        } else {
            System.out.println("Не ціле число");
        }
        return new int[]{start, end};
    }
    //Функція для копіювання частини масиву в новий масив
    public static int [] copy(int [] arr, int start, int size){
        int [] temp = new int[size];
        for(int i = 0; i<size; i++){
            temp[i] = arr[i+start];
        }
        return temp;
    }

    public static void print_detail_result(int [] arr, Set<Integer> res){
        Integer[] resultArray = res.toArray(new Integer[0]);
        System.out.print("Візуально полегшенний результат: ");
        for(int i =0; i<res.size(); i++){
            System.out.println("num["+arr[2*i]+"] * num["+arr[2*i+1]+"] = fR["+resultArray[i]+"]");
        }
    }
}