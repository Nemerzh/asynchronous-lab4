import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class CompletableFutureExample {

    public static void main(String[] args) {
        long startGen = System.currentTimeMillis();

        // Асинхронно генеруємо масив з 10 чисел
        CompletableFuture<int[]> generateArray = CompletableFuture.supplyAsync(() -> {
            log("Генерація масиву");
            return IntStream.generate(() -> ThreadLocalRandom.current().nextInt(1, 100))
                    .limit(10)
                    .toArray();
        });

        // Вимірюємо час завершення генерації масиву
        CompletableFuture<Void> generationTime = generateArray.thenRunAsync(() -> {
            long end = System.currentTimeMillis();
            log("Масив згенеровано. Час генерації: " + (end - startGen) + " мс");
        });

        long startAdd = System.currentTimeMillis();
        // Додаємо +10 до кожного елемента
        CompletableFuture<int[]> addTen = generateArray.thenApplyAsync(array -> {
            log("Додавання +10 до кожного елементу");
            return IntStream.of(array).map(num -> num + 10).toArray();
        });

        // Вимірюємо час завершення додавання
        CompletableFuture<Void> additionTime = addTen.thenRunAsync(() -> {
            long end = System.currentTimeMillis();
            log("Додавання завершено. Час додавання: " + (end - startAdd) + " мс");
        });
        long startDiv = System.currentTimeMillis();
        // Ділимо кожен елемент на 2
        CompletableFuture<double[]> divideByTwo = addTen.thenApplyAsync(array -> {
            log("Ділення кожного елементу на 2");
            return IntStream.of(array).mapToDouble(num -> num / 2.0).toArray();
        });

        // Вимірюємо час завершення ділення
        CompletableFuture<Void> divisionTime = divideByTwo.thenRunAsync(() -> {
            long end = System.currentTimeMillis();
            log("Ділення завершено. Час ділення: " + (end - startDiv) + " мс");
        });

        // Виводимо результати
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                generateArray.thenAcceptAsync(array -> log("Початковий масив: " + arrayToString(array))),
                addTen.thenAcceptAsync(array -> log("Масив після додавання +10: " + arrayToString(array))),
                divideByTwo.thenAcceptAsync(array -> log("Результат ділення: " + arrayToString(array)))
        );

        allTasks.join();

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
            if (i == array.length - 1) {
                sb.append(array[i]);
            } else {
                sb.append(array[i]).append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
