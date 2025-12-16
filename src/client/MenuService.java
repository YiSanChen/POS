package client;

import common.Product;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MenuService {

    // 讀取 menu.json 並轉成 Product 物件清單
    public List<Product> loadMenu() {
    	
        List<Product> menu = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get("menu.json")));
            content = content.trim().replace("[", "").replace("]", ""); 
            String[] items = content.split("},"); 

            for (String item : items) {
                item = item.replace("{", "").replace("}", "");
                
                String id = extractValue(item, "id");
                String name = extractValue(item, "name");
                int price = Integer.parseInt(extractValue(item, "price"));
                String category = extractValue(item, "category");
                
                menu.add(new Product(id, name, price, category));
            }
            System.out.println("菜單讀取成功，共 " + menu.size() + " 道菜。");

        } catch (IOException e) {
            System.err.println("找不到 menu.json，載入預設測試資料。");
            
        }
        return menu;
    }

    private String extractValue(String jsonItem, String key) {
    	
        String searchKey = "\"" + key + "\":";
        int start = jsonItem.indexOf(searchKey);
        if (start == -1) return "";
        
        start += searchKey.length();
        
        boolean isString = jsonItem.charAt(start) == '"';
        if (isString) start++; 
        
        int end;
        if (isString) {
            end = jsonItem.indexOf("\"", start);
        } else {
            end = jsonItem.indexOf(",", start);
            if (end == -1) end = jsonItem.length();
        }
        
        return jsonItem.substring(start, end).trim();
    }
}