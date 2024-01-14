import java.io.FileNotFoundException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.io.PrintWriter;

import static java.lang.Math.abs;

public class Generatorius
{
    public static void main(String args[]) throws ExecutionException, InterruptedException, FileNotFoundException  //static method
    {

        for (int count : new int[]{2})
        {
            Generate(count);
        }

    }

    static void Generate(int Tsk) throws FileNotFoundException {
        PrintWriter out = new PrintWriter("data" + Tsk + ".txt");

        for (int i = 0; i < Tsk; i++)
        {
            Random r = new Random();
            double x = -10 + (10 - (-10)) * r.nextDouble();
            double y = -10 + (10 - (-10)) * r.nextDouble();

            out.println(x+";"+y);
        }

        out.close();
    }
}