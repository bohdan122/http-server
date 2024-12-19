import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HomePageHandler implements HttpHandler {
    private final ProductService productService;

    public HomePageHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equals(method)) {
            displayProducts(exchange);
        } else if ("POST".equals(method)) {
            handleFormSubmission(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private void displayProducts(HttpExchange exchange) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("<html><head><title>ATB Server - Products</title></head><body>");
        response.append("<h1>Product List</h1>");
        response.append("<ul>");

        for (Product product : productService.getAllProductsList()) {
            response.append("<li>").append(product.getName())
                    .append(" - Price: $").append(product.getPrice())
                    .append("<form method='POST' action='/products' style='display:inline;'>")
                    .append("<input type='hidden' name='delete' value='")
                    .append(product.getName()).append("'>")
                    .append("<button type='submit'>Delete</button>")
                    .append("</form>")
                    .append("</li>");
        }

        response.append("</ul>");
        response.append("<h2>Add New Product</h2>");
        response.append("<form method='POST' action='/products'>");
        response.append("Name: <input type='text' name='name'><br>");
        response.append("Price: <input type='text' name='price'><br>");
        response.append("<button type='submit'>Add Product</button>");
        response.append("</form>");

        response.append("</body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.toString().getBytes());
        }
    }

    private void handleFormSubmission(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        StringBuilder body = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            body.append((char) i);
        }

        String decodedBody = URLDecoder.decode(body.toString(), StandardCharsets.UTF_8);
        String[] params = decodedBody.split("&");

        if (params.length == 1 && params[0].startsWith("delete=")) {
            String productName = params[0].split("=")[1];
            deleteProduct(exchange, productName);
        } else if (params.length == 2) {
            String name = params[0].split("=")[1];
            double price = Double.parseDouble(params[1].split("=")[1]);
            addProduct(exchange, name, price);
        }
    }

    private void deleteProduct(HttpExchange exchange, String productName) throws IOException {
        boolean deleted = productService.deleteProduct(productName);
        String response = deleted ? "Product deleted successfully!" : "Product not found!";
        exchange.getResponseHeaders().set("Location", "/products");
        exchange.sendResponseHeaders(302, -1); // Redirect to /products
        exchange.close();
    }

    private void addProduct(HttpExchange exchange, String name, double price) throws IOException {
        productService.addProduct(new Product(name, price));
        exchange.getResponseHeaders().set("Location", "/products");
        exchange.sendResponseHeaders(302, -1); // Redirect to /products
        exchange.close();
    }
}
