import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ProductController implements HttpHandler {
    private final ProductService productService = new ProductService();

    public ProductService getProductService() {
        return productService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getAllProducts(exchange);
                break;
            case "POST":
                addProduct(exchange);
                break;
            case "PUT":
                updateProduct(exchange);
                break;
            case "DELETE":
                deleteProduct(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private void getAllProducts(HttpExchange exchange) throws IOException {
        String response = productService.getAllProducts();
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void addProduct(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        StringBuilder body = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            body.append((char) i);
        }

        String decodedBody = URLDecoder.decode(body.toString(), StandardCharsets.UTF_8);
        String[] params = decodedBody.split("&");
        String name = params[0].split("=")[1];
        double price = Double.parseDouble(params[1].split("=")[1]);

        productService.addProduct(new Product(name, price));
        String response = "Product added successfully!";
        exchange.sendResponseHeaders(201, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void updateProduct(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        StringBuilder body = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            body.append((char) i);
        }

        String decodedBody = URLDecoder.decode(body.toString(), StandardCharsets.UTF_8);
        String[] params = decodedBody.split("&");
        String name = params[0].split("=")[1];
        double price = Double.parseDouble(params[1].split("=")[1]);

        boolean updated = productService.updateProduct(name, price);
        String response = updated ? "Product updated successfully!" : "Product not found!";
        exchange.sendResponseHeaders(updated ? 200 : 404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void deleteProduct(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        StringBuilder body = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            body.append((char) i);
        }
    
        String decodedBody = URLDecoder.decode(body.toString(), StandardCharsets.UTF_8);
        String name = decodedBody.split("=")[1];
    
        boolean deleted = productService.deleteProduct(name);
        String response = deleted ? "Product deleted successfully!" : "Product not found!";
        exchange.sendResponseHeaders(deleted ? 200 : 404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
}