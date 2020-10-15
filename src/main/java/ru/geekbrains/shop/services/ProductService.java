package ru.geekbrains.shop.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import ru.geekbrains.shop.dto.ProductDto;
import ru.geekbrains.shop.persistence.entities.Image;
import ru.geekbrains.shop.persistence.entities.Product;
import ru.geekbrains.shop.persistence.entities.enums.ProductCategory;
import ru.geekbrains.shop.persistence.repositories.ProductRepository;
import ru.geekbrains.shop.services.notification.MailService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MailService mailService;

    public Optional getOneById(UUID id) {
        return productRepository.findById(id);
    }

    public List<Product> getAll(Integer category) {
        return category == null ? productRepository.findAll() : productRepository.findAllByCategory(ProductCategory.values()[category]);
    }

    public String save(ProductDto productDto, Image image) {

        Product product = Product.builder()
            .added(new Date())
            .title(productDto.getTitle())
            .available(productDto.isAvailable())
            .category(productDto.getCategory())
            .price(productDto.getPrice())
            .description(productDto.getDescription())
            .image(image)
        .build();

        productRepository.save(product);
        Context context = new Context();
        context.setVariable("product_number",product.getId());

        mailService.send("shopgeekbrains@gmail.com", "Добавление продукта" ,context);

        log.info("New Product has been succesfully added! {}", product);
        return "redirect:/";
    }

}