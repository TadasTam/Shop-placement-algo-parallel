import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static java.lang.Math.abs;
import static java.lang.Math.exp;

public class Pagrindinis
{
    public static void main(String args[]) throws ExecutionException, InterruptedException, IOException  //static method
    {
        int count = 20;
        double[] x = new double[count];
        double[] y = new double[count];
        read(x, y, count);

        int Tsk = x.length / 2;
        int Nsk = x.length - Tsk;
        double step = 0.01;
        int iterations = 5000;

        double[] xr = new double[Tsk+Nsk];
        double[] yr = new double[Tsk+Nsk];

        for (int i = 0; i < Tsk; i++)
        {
            xr[i] = x[i];
            yr[i] = y[i];
        }

        printPairs(x, y);

        if (true)
        {
            NotParallel(Tsk, Nsk, x.clone(), y.clone(), xr, yr, step, iterations);
        }

        List<Integer> listOfNumbers = new ArrayList<Integer>();
        for (int i = Tsk; i < Tsk+Nsk; i++)
        {
            listOfNumbers.add(i);
        }

        if (true)
        {
                Parallel(Tsk, Nsk, x.clone(), y.clone(), xr, yr, step, iterations, listOfNumbers);
        }

        if (true)
        {
            for (int Threads = 1; Threads <= 16; Threads++)
            {
                variableParallel(Threads, Tsk, Nsk, x.clone(), y.clone(), xr, yr, step, iterations, listOfNumbers);
//                printPairs(xr, yr);
            }


        }

    }

    static void NotParallel(int Tsk, int Nsk, double[] xp, double[] yp, double[] xr, double[] yr, double step, int iterations)
    {
        System.out.println();
        System.out.println("NOT PARALLEL");

        System.out.println("Pradine " + TF(xp, yp));

        double start = System.nanoTime();
        for (int i = Tsk; i < Tsk+Nsk; i++)
        {
            List<Double> db = dTF(i, xp, yp, step, Tsk, step, iterations);
            xr[i] = db.get(0);
            yr[i] = db.get(1);
        }

        double duration = System.nanoTime() - start;
        System.out.println("Galutine " + TF(xr, yr));;
        System.out.println("Laikas " + duration);
        System.out.println("Sekundes " + duration / 1_000_000_000);
        System.out.println("Milisekundes " + duration / 1_000_000);
    }

    static void Parallel(int Tsk, int Nsk, double[] xp, double[] yp, double[] xr, double[] yr, double step, int iterations, List<Integer> listOfNumbers)
    {
        System.out.println();
        System.out.println("FIXED SIZE POOL");
        System.out.println("Pradine " + TF(xp, yp));

        double start = System.nanoTime();

        listOfNumbers.parallelStream().forEach(number ->
                sumres(dTF(number, xp, yp, step, Tsk, step, iterations),number, xr, yr)
        );

        double duration = System.nanoTime() - start;
        System.out.println("Galutine " + TF(xr, yr));;
        System.out.println("Laikas " + duration);
        System.out.println("Sekundes " + duration / 1_000_000_000);
        System.out.println("Milisekundes " + duration / 1_000_000);
    }

    static void variableParallel(int Threads, int Tsk, int Nsk, double[] xp, double[] yp, double[] xr, double[] yr, double step, int iterations, List<Integer> listOfNumbers) throws ExecutionException, InterruptedException {

        System.out.println();
        System.out.println("VARIABLE SIZE POOL, Threads = " + Threads);
        System.out.println("Pradine " + TF(xp, yp));

        double start = System.nanoTime();

        ForkJoinPool fjpool = new ForkJoinPool(Threads);
        fjpool.submit(() -> listOfNumbers.parallelStream().forEach(number -> sumres(dTF(number, xp, yp, step, Tsk, step, iterations),number, xr, yr))).get();

        double duration = System.nanoTime() - start;
        System.out.println("Galutine " + TF(xr, yr));
        System.out.println("Laikas " + duration);
        System.out.println("Sekundes " + duration / 1_000_000_000);
        System.out.println("Milisekundes " + duration / 1_000_000);
    }

    static void read(double[] x, double[] y, int count) throws IOException {
        String filename = "data" + count + ".txt";

        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int i = 0;
        while ((st = br.readLine()) != null) {
            x[i] = Double.parseDouble(st.split(";")[0]);
            y[i] = Double.parseDouble(st.split(";")[1]);
            i++;
        }
    }

    static void printPairsCo(double[] x, double[] y)
    {
        for (int i = 0; i < x.length; i++)
        {
            System.out.println("(" + x[i] + " ; " + y[i] + ")");
        }

    }
    static void printPairs(double[] x, double[] y)
    {
        for (int i = 0; i < x.length; i++)
        {
            System.out.print(x[i] + ", ");
        }
        System.out.println();
        for (int i = 0; i < y.length; i++)
        {
            System.out.print(y[i] + ", ");
        }

    }

    static void sumres(List<Double> res, int i, double[] x, double[] y)
    {
        x[i] = res.get(0);
        y[i] = res.get(1);
    }

    static double TF(double[] x, double[] y)
    {
        int n= x.length;
        double suma=0;
        for (int i = 0; i < n; i++)
        {
            for (int j = i+1; j < n; j++) {
                double lyginimas = exp(-0.1*((x[i] - x[j])*(x[i] - x[j]) + (y[i] - y[j])*(y[i] - y[j]) ));
                suma = suma + lyginimas;
            }
            double ribos = 0;
            if((x[i] > 10 || x[i] < -10) && (y[i] > 10 || y[i] < -10))
            {
                ribos = 0.5 * ( (abs(x[i])-10)*(abs(x[i])-10) + (abs(y[i])-10)*(abs(y[i])-10));
            }
            else if (x[i] > 10 || x[i] < -10)
            {
                ribos = 0.5 * ((abs(x[i]) - 10) *(abs(x[i]) - 10));
            }
            else if (y[i] > 10 || y[i] < -10)
            {
                ribos = 0.5 * ((abs(y[i]) - 10)*(abs(y[i]) - 10));
            }
            suma = suma + ribos;
        }

        return suma;
    }

    static List<Double> dTF(int i, double[] x, double[] y, double h, int lock, double step, int loops)
    {
        int n = x.length;
//        System.out.println(Thread.currentThread().getName());
        for (int j = 0; j < loops; j++)
        {
            List<Double> cords = calc(x.clone(),y.clone(),h,lock,i);

            x[i] -= step * cords.get(0);
            y[i] -= step * cords.get(1);
        }

        return (List<Double>) Arrays.asList(x[i], y[i]);
    }

    static List<Double> calc(double[] x, double[] y, double h,int lock, int i)
    {
        int n = x.length;
        double[] xn = x.clone();
        double[] yn = y.clone();

        xn[i] += h;
        yn[i] += h;

        double gx = (TF(xn, y) - TF(x, y)) / h;
        double gy = (TF(x, yn) - TF(x, y)) / h;

        return (List<Double>) Arrays.asList(gx, gy);
    }
}