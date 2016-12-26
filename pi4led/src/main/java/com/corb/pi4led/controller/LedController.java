package com.corb.pi4led.controller;

import com.pi4j.io.gpio.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Calvin on 14/12/2016.
 */
@RestController
public class LedController {

    private static GpioPinDigitalOutput pin;

    @RequestMapping("/")
    public String greeting(){
        return "Hello world.";
    }

    @RequestMapping("/light")
    public String light() {
        if(pin ==null){
            GpioController gpio = GpioFactory.getInstance();
            pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLed", PinState.LOW);
        }

        pin.toggle();

        return "Light Toggled.";
    }
}
