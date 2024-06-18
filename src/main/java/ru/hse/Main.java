package ru.hse;

import org.jsoup.Jsoup;
import ru.hse.parser.GlobalParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;

public class Main {
    public static void main(String[] args) throws IOException {
        get("/weatherald/:location", (req,res) ->{
            System.out.println("==============");
            System.out.println(req.ip());
            System.out.println(req.params(":location"));
            return new GlobalParser(req.params(":location")).parseJson();
        });
    }
}