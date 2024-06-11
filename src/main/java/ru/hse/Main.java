package ru.hse;

import com.google.gson.Gson;
import ru.hse.parser.*;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class Main {
    private static List<String> list;
    public static void main(String[] args) {
        list = new ArrayList<>();

        get("/weatherald/:location", (req,res) ->
            new GlobalParser(req.params(":location")).parseJson());

        get("/78/:args", (req,res) -> {
            list.add(req.params(":args"));
            return new Gson().toJson(list);
        });
    }
}