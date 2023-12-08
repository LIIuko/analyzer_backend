package com.example.analyzer.controllers;

import com.example.analyzer.entities.CaseProducts;
import com.example.analyzer.entities.Product;
import com.example.analyzer.services.CaseProductService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

@RequestMapping("/caseproduct")
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class CaseProductController {
    @Autowired
    private CaseProductService caseProductService;

    @PostMapping("/add")
    public CaseProducts addCProduct(String title) {
        title = title.toLowerCase();

        Product productIstudio = parseIstudio(title);
        Product productMegafon = parseMegafon(title);
        Product productBiggeek = parseBiggeek(title);
        Product productIpoint = parseIpoint(title);
        Product productMobinot = parseMobinot(title);

        Set<Product> products = new HashSet<>(Arrays.asList(productIstudio, productMegafon, productBiggeek, productIpoint, productMobinot));

        caseProductService.addProduct(new CaseProducts(title, products));

        return new CaseProducts(title, products);
    }


    @GetMapping("/get")
    public List<CaseProducts> getAll() {
        return caseProductService.getAll();
    }

    @GetMapping("/get/{id}")
    public CaseProducts getById(@PathVariable Long id) {
        return caseProductService.getById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        caseProductService.deleteProduct(id);
    }

    @PatchMapping("/update/{id}")
    public void update(@PathVariable Long id) {
        String title = caseProductService.getById(id).getTitle().toLowerCase();

        Product productIstudio = parseIstudio(title);
        Product productMegafon = parseMegafon(title);
        Product productBiggeek = parseBiggeek(title);
        Product productIpoint = parseIpoint(title);
        Product productMobinot = parseMobinot(title);

        Set<Product> products = new HashSet<>(Arrays.asList(productIstudio, productMegafon, productBiggeek, productIpoint, productMobinot));

        caseProductService.updateProduct(new CaseProducts(caseProductService.getById(id).getTitle(), products));
    }

    //Поиск товара
    private boolean checkTitle(String title, String siteTitle) {
        double count = 0;
        List<String> titleWords = List.of(title.split(" "));
        titleWords = titleWords.stream().filter(s -> !s.equals("apple") & !s.equals("iphone")).collect(Collectors.toList());
        if(titleWords.isEmpty()){
            return true;
        }
        for (String word : titleWords) {
            if (siteTitle.contains(word)) {
                count++;
            }
        }
        if (count / titleWords.size() * 100 > 60) {
            return true;
        }
        return false;
    }


    private Product parseIstudio(String title) {
        // Парсинг istudio-msk
        Document document = null;
        String url = "";
        int price = -1;
        try {
            for (int page = 0; page < 20; page++) {
                document = Jsoup.connect("https://istudio-msk.ru/catalog/iPhone/?sort=price&desc=1&curPos=" + page * 20).get();
                for (Element el : document.getElementsByClass("product-item")) {
                    byte[] ptext = el.getElementsByClass("blk_name").text().getBytes(ISO_8859_1);
                    String value = new String(ptext, UTF_8);
                    if (checkTitle(title, value.toLowerCase())) {
                        url = "https://istudio-msk.ru" + el.getElementsByClass("blk_img").select("a").attr("href");
                        price = parseInt(el.getElementsByClass("cen").text().replaceAll("[^0-9]", ""));
                        return new Product("istudio" ,url, price);
                    }
//                    System.out.println(value);
//                    System.out.println(el.getElementsByClass("cen").text().replaceAll("[^0-9]", ""));
                }
            }

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    private Product parseMegafon(String title) {
        // Парсинг megafon
        Document document = null;
        String url = "";
        int price = -1;
        try {
            for (int page = 0; page < 20; page++) {
                document = Jsoup.connect("https://moscow.shop.megafon.ru/mobile/apple?s_f=price&s_d=asc" + page).get();
                for (Element el : document.getElementsByClass("b-goods-list__item")) {
                    byte[] ptext = el.getElementsByClass("b-good__title-link").text().getBytes(ISO_8859_1);
                    String value = new String(ptext, UTF_8);
                    if (checkTitle(title, value.toLowerCase())) {
                        url = "https://moscow.shop.megafon.ru" + el.getElementsByClass("b-good__photo").select("a").attr("href");
                        price = parseInt(el.getElementsByClass("b-price-good-list__value").text().replaceAll("[^0-9]", ""));
                        return new Product("megafon", url, price);
                    }
//                    System.out.println(value);
//                    System.out.println(el.getElementsByClass("b-price-good-list__value").text().replaceAll("[^0-9]", ""));
                }
            }

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    private Product parseIpoint(String title) {
        // Парсинг i-point
        Document document = null;
        String url = "";
        int price = Integer.MAX_VALUE;
        try {
            document = Jsoup.connect("https://i-point.ru/iphone/").get();
            for (Element el : document.getElementsByClass("item-card")) {
                byte[] ptext = el.getElementsByClass("item-name").text().getBytes(ISO_8859_1);
                String value = new String(ptext, UTF_8);
                if (checkTitle(title, value.toLowerCase())) {
                    if (el.getElementsByClass("main-price").isEmpty()) {
                        continue;
                    }
                    if (price > parseInt(el.getElementsByClass("main-price").text().replaceAll("[^0-9]", ""))) {
                        url = "https://i-point.ru" + el.getElementsByClass("item-name").attr("href");
                        price = parseInt(el.getElementsByClass("main-price").text().replaceAll("[^0-9]", ""));
                    }
                }
//                System.out.println(value);
//                System.out.println(el.getElementsByClass("main-price").text().replaceAll("[^0-9]", ""));
            }

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

        if (url.isEmpty()) {
            System.out.println("Данного товара нет");
            return null;
        }
        return new Product("i-point", url, price);
    }


    private Product parseMobinot(String title) {
        // Парсинг mobinot
        Document document = null;
        String url = "";
        int price = -1;
        try {
            for (int page = 0; page < 20; page++) {
                document = Jsoup.connect("https://mobinot.ru/catalog/apple/iphone/?sort=catalog_PRICE_3&method=asc&PAGEN_1=" + page).get();
                for (Element el : document.getElementsByClass("subcategory__products-list__main-container__products-grid__card")) {
                    byte[] ptext = el.getElementsByClass("subcategory__products-list__main-container__products-grid__card__product-info__name").text().getBytes(ISO_8859_1);
                    String value = new String(ptext, UTF_8);
                    if (checkTitle(title, value.toLowerCase())) {

                        System.out.println(value);
                        System.out.println( checkTitle(title, value.toLowerCase()));
                        System.out.println(url = "https://mobinot.ru" + el.getElementsByClass("subcategory__products-list__main-container__products-grid__card__product-info__name").attr("href"));

                        url = "https://mobinot.ru" + el.getElementsByClass("subcategory__products-list__main-container__products-grid__card__product-info__name").attr("href");
                        price = parseInt(el.getElementsByClass("subcategory__products-list__main-container__products-grid__card__product-info__price-and-button__price").text().replaceAll("[^0-9]", ""));
                        return new Product("mobinot", url, price);
                    }
//                    System.out.println(value);
//                    System.out.println(el.getElementsByClass("subcategory__products-list__main-container__products-grid__card__product-info__price-and-button__price").text().replaceAll("[^0-9]", ""));
                }
            }

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    private Product parseBiggeek(String title) {
        // Парсинг Biggeek
        Document document = null;
        String url = "";
        int price = Integer.MAX_VALUE;
        try {
            for (int page = 0; page < 20; page++) {
                document = Jsoup.connect("https://biggeek.ru/catalog/apple-iphone?sort=price&page=" + page).get();
                for (Element el : document.getElementsByClass("catalog-card")) {
                    byte[] ptext = el.getElementsByClass("catalog-card__title").text().getBytes(ISO_8859_1);
                    String value = new String(ptext, UTF_8);
                    if (checkTitle(title, value.toLowerCase())) {
                        if (price > parseInt(el.getElementsByClass("cart-modal-count").text().replaceAll("[^0-9]", ""))) {
                            url = "https://biggeek.ru" + el.getElementsByClass("cart-modal-title").attr("href");
                            price = parseInt(el.getElementsByClass("cart-modal-count").text().replaceAll("[^0-9]", ""));
                        }
                    }
//                    System.out.println(value);
//                    System.out.println(el.getElementsByClass("cart-modal-count").text().replaceAll("[^0-9]", ""));
                }
            }

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

        if (url.isEmpty()) {
            System.out.println("Данного товара нет");
            return null;
        }
        return new Product("Biggeek", url, price);
    }
}
