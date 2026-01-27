package pl.tkaczyk.transferservice.service;


import pl.tkaczyk.transferservice.model.TransferRestModel;

public interface TransferService {
    public boolean transfer(TransferRestModel productPaymentRestModel);
}
