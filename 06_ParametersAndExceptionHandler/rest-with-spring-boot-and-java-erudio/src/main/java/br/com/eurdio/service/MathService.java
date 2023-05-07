package br.com.eurdio.service;

import org.springframework.stereotype.Service;

@Service
public class MathService {

    public Double convertToDouble(String strNumber) {
        if (strNumber == null){
            return 0D;
        }
        String number = strNumber.replaceAll(",", ".");
        if (isNumeric(strNumber)){
            return Double.parseDouble(number);
        }
        return 0D;
    }

    public boolean isNumeric(String strNumber) {
        if (strNumber == null){
            return false;
        }
        String number = strNumber.replaceAll(",", ".");
        return number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }
}
