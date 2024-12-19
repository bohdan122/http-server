import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ATBServer {
    public static void main(String[] args) throws IOException {
        // Create the HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Create the ProductController which handles product-related requests
        ProductController productController = new ProductController();
        
        // Map the /products URL to the HomePageHandler for product display
        server.createContext("/products", new HomePageHandler(productController.getProductService()));

        // Set the default executor for the server
        server.setExecutor(null);
        
        // Start the server and log the status to console
        System.out.println("Server is running on http://localhost:8080");
        server.start();
    }
}
