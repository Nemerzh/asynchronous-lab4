import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;

public class CompletableFutureSequence {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        // Асинхронно генеруємо послідовність дійсних чисел
        CompletableFuture<double[]> generateSequence = CompletableFuture.supplyAsync(() -> {
            log("Генерація послідовності чисел");
            return DoubleStream.generate(() -> ThreadLocalRandom.current().nextDouble(1, 10))
                    .limit(20)
                    .toArray();
        });

        // Обчислюємо добуток (a2 - a1) * (a3 - a2) * ... * (an - an-1)
        CompletableFuture<Double> calculateProduct = generateSequence.thenApplyAsync(sequence -> {
            log("Обчислення добутку різниць між сусідніми елементами");
            double product = 1.0;
            for (int i = 1; i < sequence.length; i++) {
                double a = (Math.round(sequence[i] * 100.0) / 100.0) - (Math.round(sequence[i - 1] * 100.0) / 100.0);
                a = Math.round(a * 100.0) / 100.0;
                product *= a;

            }
            return Math.round(product * 100.0) / 100.0;
        });

        // Виводимо початкову послідовність
        generateSequence.thenAcceptAsync(sequence -> {
            log("Початкова послідовність: " + arrayToString(sequence));
        });

        // Виводимо результат обчислення
        calculateProduct.thenAcceptAsync(result -> {;
            log("Результат обчислення: " + result);
        });

        // Очікуємо завершення всіх задач
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                generateSequence,
                calculateProduct
        );

        allTasks.join();

        long end = System.currentTimeMillis();
        System.out.println("Загальний час виконання: " + (end - start) + " мс");
    }

    private static void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }


    private static String arrayToString(double[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i == array.length - 1){
                sb.append(Math.round(array[i] * 100.0) / 100.0);
            } else {
                sb.append(Math.round(array[i] * 100.0) / 100.0).append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
