package br.com.eurdio.service;

import br.com.eurdio.exceptions.UnsupportedMathOperationException;
import org.springframework.stereotype.Service;

@Service
public class MathService {

    public Double sum(String numberOne, String numberTwo){
        verifyForValidEntry(numberOne);
        verifyForValidEntry(numberTwo);
        return convertToDouble(numberOne) + convertToDouble(numberTwo);
    }
    public Double subtraction(String numberOne, String numberTwo){
        verifyForValidEntry(numberOne);
        verifyForValidEntry(numberTwo);
        return convertToDouble(numberOne) - convertToDouble(numberTwo);
    }
    public Double multiplication(String numberOne, String numberTwo){
        verifyForValidEntry(numberOne);
        verifyForValidEntry(numberTwo);
        return convertToDouble(numberOne) * convertToDouble(numberTwo);
    }
    public Double division(String numberOne, String numberTwo){
        verifyForValidEntry(numberOne);
        verifyForValidEntry(numberTwo);
        return convertToDouble(numberOne) / convertToDouble(numberTwo);
    }
    public Double mean(String numberOne, String numberTwo){
        verifyForValidEntry(numberOne);
        verifyForValidEntry(numberTwo);
        return (convertToDouble(numberOne) + convertToDouble(numberTwo))/2.0;
    }

    public Double squareRoot(String number){
        verifyForValidEntry(number);
        return Math.sqrt(convertToDouble(number));
    }

    private void verifyForValidEntry(String number){
        if(!isNumeric(number) ){
            throw new UnsupportedMathOperationException("Please set a numeric value! ");
        }
    }

    private Double convertToDouble(String strNumber) {
        if (strNumber == null){
            return 0D;
        }
        String number = strNumber.replaceAll(",", ".");
        if (isNumeric(strNumber)){
            return Double.parseDouble(number);
        }
        return 0D;
    }

    private boolean isNumeric(String strNumber) {
        if (strNumber == null){
            return false;
        }
        String number = strNumber.replaceAll(",", ".");
        return number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }
}
