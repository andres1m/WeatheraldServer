package ru.hse;

import com.google.gson.Gson;
import ru.hse.parser.*;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        get("/weatherald/:location", (req,res) -> new GlobalParser(req.params(":location")).parseJson());
    }
}