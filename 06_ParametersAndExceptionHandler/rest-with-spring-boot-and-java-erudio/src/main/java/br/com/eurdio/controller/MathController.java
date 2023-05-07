package br.com.eurdio.controller;

import br.com.eurdio.exceptions.UnsupportedMathOperationException;
import br.com.eurdio.service.MathService;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MathController {
    
    public MathController(MathService mathService){
        this.mathService = mathService;
    }
    
    private final MathService mathService;

    @RequestMapping(value = "/sum/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double sum(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo) throws Exception {
        if(!mathService.isNumeric(numberOne) || !mathService.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value! ");
        }
        return mathService.convertToDouble(numberOne) + mathService.convertToDouble(numberTwo)  ;
    }

    @RequestMapping(value = "/subtraction/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double subtraction(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo) throws Exception {
        if(!mathService.isNumeric(numberOne) || !mathService.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value! ");
        }
        return mathService.convertToDouble(numberOne) - mathService.convertToDouble(numberTwo);
    }

    @RequestMapping(value = "/multiplication/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double multiplication(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo) throws Exception {
        if(!mathService.isNumeric(numberOne) || !mathService.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value! ");
        }
        return mathService.convertToDouble(numberOne) * mathService.convertToDouble(numberTwo);
    }

    @RequestMapping(value = "/division/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double division(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo) throws Exception {
        if(!mathService.isNumeric(numberOne) || !mathService.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value! ");
        }
        return mathService.convertToDouble(numberOne) / mathService.convertToDouble(numberTwo);
    }

    @RequestMapping(value = "/mean/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double mean(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo) throws Exception {
        if(!mathService.isNumeric(numberOne) || !mathService.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value! ");
        }
        return (mathService.convertToDouble(numberOne) + mathService.convertToDouble(numberTwo))/2.0;
    }

    @RequestMapping(value = "/squareRoot/{number}", method = RequestMethod.GET)
    public Double squareRoot(
            @PathVariable(value = "numberOne") String number) throws Exception {
        if(!mathService.isNumeric(number) ){
            throw new UnsupportedMathOperationException("Please set a numeric value! ");
        }
        return Math.sqrt(mathService.convertToDouble(number));
    }
}
