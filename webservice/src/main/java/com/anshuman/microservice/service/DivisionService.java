package com.anshuman.microservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DivisionService {

    public String divideNumber(int numerator, int denominator)
    {
        try {
            return numerator + "/" + denominator + " = " + divide(numerator, denominator);
        }
        catch (ArithmeticException ex)
        {
            log.error("Exception encountered while dividing numerator={} with denominator={} " +
                    "with errorMessage={} and stackTrace=", numerator, denominator, ex.getMessage(), ex);

            throw new RuntimeException(ex); // rethrowing exception, to be handled by web-exception-handler.
        }
    }

    private int divide(int num1, int num2)
    {
        if (num2==0)
            throw new ArithmeticException("why are you dividing by 0 my friend?");
        else return num1/num2;
    }

}
