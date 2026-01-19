package pl.tkaczyk.productsmicroservice.service;

import pl.tkaczyk.productsmicroservice.rest.CreateProductRestModel;

public interface ProductService {
    String createProduct(CreateProductRestModel product) throws Exception;
}
