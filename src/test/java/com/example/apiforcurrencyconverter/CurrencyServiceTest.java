package com.example.apiforcurrencyconverter;

import com.example.apiforcurrencyconverter.Models.ConversionCurrency;
import com.example.apiforcurrencyconverter.Models.Currency;
import com.example.apiforcurrencyconverter.Repositories.CurrencyRepository;
import com.example.apiforcurrencyconverter.Services.CurrencyService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRepository repository;
    private CurrencyService subject;
    @Before
    public void setup() {
        subject = new CurrencyService(repository);
    }

    @Test
    public void getAllCurrenciesEmpty() {
        Mockito.when(repository.findAll()).thenReturn(Arrays.asList());

        List<Currency> currencies = subject.getAllCurrencies();
        Assert.assertTrue(currencies.isEmpty());
    }

    @Test
    public void getAllCurrenciesTestAreSorted() {
        Currency currencyZMW = new Currency("ZMW", 1);
        Currency currencyEUR = new Currency("EUR", 1);
        Currency currencyAED = new Currency("AED", 4.2);
        Currency currencyUSD = new Currency("USD", 0.8);

        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(currencyZMW, currencyEUR, currencyUSD, currencyAED));

        List<Currency> currencies = subject.getAllCurrencies();
        Assert.assertTrue(!currencies.isEmpty());

        Assert.assertEquals(currencies.get(0),currencyAED);
        Assert.assertEquals(currencies.get(1),currencyEUR);
        Assert.assertEquals(currencies.get(2),currencyUSD);
        Assert.assertEquals(currencies.get(3),currencyZMW);
    }

    @Test(expected = NullPointerException.class)
    public void getAllCurrenciesTestGivesNullPointerException() {

        Mockito.when(repository.findAll()).thenReturn(null);

        subject.getAllCurrencies();
    }

    @Test
    public void convertShouldReturnEmptyWhenegativeValue() {
        Currency currencyEUR = new Currency("EUR", 1);
        Currency currencyUSD = new Currency("USD", 0.8);

        Mockito.when(repository.findById("EUR")).thenReturn(Optional.of(currencyEUR));
        Mockito.when(repository.findById("USD")).thenReturn(Optional.of(currencyUSD));

        ConversionCurrency conversionCurrency = new ConversionCurrency("EUR", "USD", -10);

        Optional<Double> result = this.subject.convert(conversionCurrency);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void convertShouldReturnEmptyCurrencyDoesNotExist() {
        Currency currencyUSD = new Currency("USD", 0.8);

        Mockito.when(repository.findById("EUR")).thenReturn(Optional.empty());
        Mockito.when(repository.findById("USD")).thenReturn(Optional.of(currencyUSD));

        ConversionCurrency conversionCurrency = new ConversionCurrency("EUR", "USD", 0);

        Optional<Double> result = this.subject.convert(conversionCurrency);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void convertShouldReturnValue() {
        Currency currencyEUR = new Currency("EUR", 1);
        Currency currencyUSD = new Currency("USD", 1.15795);

        Mockito.when(repository.findById("EUR")).thenReturn(Optional.of(currencyEUR));
        Mockito.when(repository.findById("USD")).thenReturn(Optional.of(currencyUSD));

        ConversionCurrency conversionCurrency = new ConversionCurrency("EUR", "USD", 10);

        Optional<Double> result = this.subject.convert(conversionCurrency);

        Assert.assertTrue(result.isPresent());
        double excepted = Math.round(8.635951466 * 100.0) /100.0;
        double actual = Math.round(result.get() * 100.0) /100.0;
        Assert.assertTrue(excepted == actual);
    }
}
