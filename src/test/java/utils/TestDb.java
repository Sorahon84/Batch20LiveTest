package utils;

import java.util.List;
import java.util.Map;

public class TestDb {
    public static void main(String[] args) {

    List<Map<String,String>> data= DbReader.fetch("select * from person");
        System.out.println(data.get(3)); //change row with index you can change row and you see entire row
      Map<String,String> rowMap=  data.get(3);
      System.out.println(rowMap.get("City")); // you can choose City, age any row
      System.out.println(rowMap.get("age"));

    }
}
