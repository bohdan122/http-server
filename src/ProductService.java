import java.util.*;

public class ProductService {
    private final Map<String, Product> productStore = new HashMap<>();

    public String getAllProducts() {
        if (productStore.isEmpty()) return "No products available.";

        StringBuilder response = new StringBuilder("Products:\n");
        for (Product product : productStore.values()) {
            response.append(product).append("\n");
        }
        return response.toString();
    }

    public List<Product> getAllProductsList() {
        return new ArrayList<>(productStore.values());
    }

    public void addProduct(Product product) {
        productStore.put(product.getName(), product);
    }

    public boolean updateProduct(String name, double price) {
        Product product = productStore.get(name);
        if (product != null) {
            product.setPrice(price);
            return true;
        }
        return false;
    }

    public boolean deleteProduct(String name) {
        return productStore.remove(name) != null;
    }
}
