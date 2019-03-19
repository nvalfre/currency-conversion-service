package com.nv.microservices.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeServiceProxy currencyExchangeServiceProxy;

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean converCurrency(@PathVariable String from,@PathVariable String to, @PathVariable BigDecimal quantity){

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}"
                , CurrencyConversionBean.class, uriVariables);

        CurrencyConversionBean currencyConversionBean = responseEntity.getBody();

        BigDecimal conversionMultiple = currencyConversionBean.getConversionMultiple();

        CurrencyConversionBean currencyConversionBeanTransformed = new CurrencyConversionBean(
                currencyConversionBean.getId(), from, to,
                conversionMultiple,
                quantity,
                quantity.multiply(conversionMultiple),
                0);

        return currencyConversionBeanTransformed;
    }

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}/proxy")
    public CurrencyConversionBean converCurrencyProxy(@PathVariable String from,@PathVariable String to, @PathVariable BigDecimal quantity){
                CurrencyConversionBean currencyConversionBean = currencyExchangeServiceProxy.retrieveExchangeValue(from,to);

        BigDecimal conversionMultiple = currencyConversionBean.getConversionMultiple();

        CurrencyConversionBean currencyConversionBeanTransformed = new CurrencyConversionBean(
                currencyConversionBean.getId(), from, to,
                conversionMultiple,
                quantity,
                quantity.multiply(conversionMultiple),
                0);

        return currencyConversionBeanTransformed;
    }
}
