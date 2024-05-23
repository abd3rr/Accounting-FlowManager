package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.enums.Currency;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.repository.InvoiceLineRepository;
import fr.uha.AccountingFlowManager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final InvoiceLineRepository invoiceLineRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, InvoiceLineRepository invoiceLineRepository){
        this.productRepository = productRepository;
        this.invoiceLineRepository = invoiceLineRepository;
    }

    public List<ProductCatalog> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }

    public ProductCatalog getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public ProductCatalog createProduct(ProductCatalog product) {
        System.out.println("Service called");
        product.setDateAdded(LocalDateTime.now());
        product.setLastUpdated(LocalDateTime.now());
        System.out.println(product);

        productRepository.save(product);
        return product;
    }

    public ProductCatalog updateProduct(ProductCatalog productCatalog, Long id) {
        return productRepository.findById(id).map(product -> {
            product.setName(productCatalog.getName());
            product.setDescription(productCatalog.getDescription());
            product.setPrice(productCatalog.getPrice());
            product.setCurrency(productCatalog.getCurrency());
            product.setDuration(productCatalog.getDuration());
            product.setService(productCatalog.isService());
            product.setLastUpdated(LocalDateTime.now());
            return productRepository.save(product);
        }).orElse(null);
    }

    public Boolean deleteProduct(Long id) {
        long count = invoiceLineRepository.countByProductId(id);
        if(count > 0){
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    public List<ProductCatalog> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}