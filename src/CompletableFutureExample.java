import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CompletableFutureExample {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        // Асинхронно генеруємо масив з 10 чисел
        CompletableFuture<int[]> generateArray = CompletableFuture.supplyAsync(() -> {
            log("Генерація масиву");
            return IntStream.generate(() -> ThreadLocalRandom.current().nextInt(1, 100))
                    .limit(10)
                    .toArray();
        });

        // Асинхронно додаємо +10 до кожного елементу
        CompletableFuture<int[]> addTen = generateArray.thenApplyAsync(array -> {
            log("Додавання +10 до кожного елементу");
            return IntStream.of(array).map(num -> num + 10).toArray();
        });

        // Асинхронно ділимо кожен елемент на 2
        CompletableFuture<double[]> divideByTwo = addTen.thenApplyAsync(array -> {
            log("Ділення кожного елементу на 2");
            return IntStream.of(array).mapToDouble(num -> num / 2.0).toArray();
        });

        // Асинхронно виводимо початковий і проміжні масиви
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                generateArray.thenAcceptAsync(array -> log("Початковий масив: " + arrayToString(array))),
                addTen.thenAcceptAsync(array -> log("Масив після додавання +10: " + arrayToString(array))),
                divideByTwo.thenAcceptAsync(array -> log("Результат ділення: " + arrayToString(array)))
        );

        allTasks.join();

        long end = System.currentTimeMillis();
        System.out.println("Загальний час виконання: " + (end - start) + " мс");
    }

    private static void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }


    private static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String arrayToString(double[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i == array.length - 1){
                sb.append(array[i]);
            } else {
                sb.append(array[i]).append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
