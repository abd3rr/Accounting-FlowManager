package fr.uha.AccountingFlowManager.controller;

import fr.uha.AccountingFlowManager.dto.ProductDTO;
import fr.uha.AccountingFlowManager.enums.Currency;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String getAllProducts(Model model) {
        List<ProductCatalog> products = productService.getAllProducts();
        for(ProductCatalog product : products) {
            //System.out.println(product);
        }
        model.addAttribute("products",products );
        model.addAttribute("size",products.size());

        // The deleteResult attribute was added before the redirect
        if (model.containsAttribute("deleteResult")) {
            Boolean deleteResult = (Boolean) model.getAttribute("deleteResult");
            String deleteMessage = (String) model.getAttribute("deleteMessage");
            // Log or print the results
            System.out.println("Delete Result: " + deleteResult);
            System.out.println("Delete Message: " + deleteMessage);
            // Add attributes
            model.addAttribute("deleteResult", deleteResult);
            model.addAttribute("deleteMessage", deleteMessage);
        }
        return "product/index";  // The template name
    }
    @GetMapping("/search")
    public String searchProducts(@RequestParam(name="name", required = false) String name, Model model) {
        if (name != null) {
            List<ProductCatalog> products = productService.searchProducts(name);
            model.addAttribute("products", products);
            model.addAttribute("size", products.size());
        }
        return "product/index";  // The template name
    }
    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        ProductCatalog productCatalog = productService.getProductById(id);
        model.addAttribute("product", productCatalog);
        return "product/show";
    }


    @GetMapping("/{id}/edit")
    public String showUpdateProductForm(@PathVariable Long id, Model model) {
        ProductCatalog productCatalog = productService.getProductById(id);
        model.addAttribute("product", productCatalog);
        model.addAttribute("currencies", Currency.values());

        ProductDTO productDTO=new ProductDTO();
        productDTO.setName(productCatalog.getName());
        productDTO.setPrice(productCatalog.getPrice());
        productDTO.setCurrency(productCatalog.getCurrency());
        productDTO.setDuration(productCatalog.getDuration());
        productDTO.setDescription(productCatalog.getDescription());
        productDTO.setService(productCatalog.isService());

        model.addAttribute("productDTO",productDTO);
        return "product/edit";
    }

    @PostMapping("{id}/edit")
    public String updateProduct(Model model,
                                @PathVariable Long id,
                                @Valid @ModelAttribute ProductDTO productDTO,
                                BindingResult bindingResult) {

        System.out.println("###############################################################");
        System.out.println(productDTO.toString());

        if(bindingResult.hasErrors()) {
            return "products/edit";
        }

        ProductCatalog productCatalog = productService.getProductById(id);

        if(productCatalog == null) {
            return "redirect:/products";
        }
        if(productDTO.getName() != null)
            productCatalog.setName(productDTO.getName());
        if(productDTO.getDescription() != null)
            productCatalog.setDescription(productDTO.getDescription());
        if(productDTO.getDuration() != null)
            productCatalog.setDuration(productDTO.getDuration());
        if(productDTO.getDuration()==null){
            productCatalog.setDuration(Duration.ZERO);
        }else{
            productCatalog.setDuration(productDTO.getDuration());
        }

        //No need for test, they are always present
        productCatalog.setPrice(productDTO.getPrice());
        if(productDTO.getCurrency() != null)
            productCatalog.setCurrency(productDTO.getCurrency());

/*

        if(productDTO.getCurrency() != null)
            productCatalog.setCurrency(productDTO.getCurrency());

        productCatalog.setService(productDTO.isService());
        productCatalog.setLastUpdated(LocalDateTime.now());


            productCatalog.setService(productDTO.isService());
            productCatalog.setLastUpdated(LocalDateTime.now());*/


        productService.updateProduct(productCatalog, id);
        return "redirect:/products";
    }
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id, RedirectAttributes redirectAttributes){
        Boolean result = productService.deleteProduct(id);
        String message = result ? "Product deleted successfully." :
                "The product is still in use. It cannot be deleted.";
        redirectAttributes.addFlashAttribute("deleteResult", result);
        redirectAttributes.addFlashAttribute("deleteMessage", message);
        return "redirect:/products";
    }

    @GetMapping("/add")
    public String showCreateProductForm2(Model model) {
        ProductDTO productDTO = new ProductDTO();
        model.addAttribute("product", productDTO);
        model.addAttribute("currencies", Currency.values());
        System.out.println("ProductDTO object created and passed to the form" );
        return "product/create";
    }
    @PostMapping
    public String createProduct(@Valid @ModelAttribute ProductDTO productDTO, BindingResult bindingResult) {
        System.out.println("ProductDTO object created and passed to the form was received" );
        ProductCatalog productCatalog = new ProductCatalog();
        if(productDTO.getName() == null || productDTO.getDescription() == null) {
            System.out.println("Name and description are empty");

        }else{
            System.out.println("Name and description are valid");

            if(productDTO.getDuration() == null) {
                productCatalog.setDuration(Duration.ZERO);
            }else{
                productCatalog.setDuration(productDTO.getDuration());
            }

            productCatalog.setName(productDTO.getName());
            productCatalog.setDescription(productDTO.getDescription());
            productCatalog.setPrice(productDTO.getPrice());
            if(productDTO.getCurrency()==null){
                productCatalog.setCurrency(Currency.CAD);
            }else{
                productCatalog.setCurrency(productDTO.getCurrency());
            }
            if(productDTO.getCurrency()==null){
                productCatalog.setCurrency(Currency.CAD);
            }

            productCatalog.setService(productDTO.isService());
            productCatalog.setDateAdded(LocalDateTime.now());
            productCatalog.setLastUpdated(LocalDateTime.now());
            productCatalog.setDuration(Duration.ZERO);

            System.out.println("The output product is ");
            System.out.println(productCatalog);

            productService.createProduct(productCatalog);


            return "redirect:/products";

        }


        if(bindingResult.hasErrors()) {
            return "product/create";
        }
        System.out.println("Product created successfully. " + productDTO.getName());
        return "redirect:/products";
    }
}