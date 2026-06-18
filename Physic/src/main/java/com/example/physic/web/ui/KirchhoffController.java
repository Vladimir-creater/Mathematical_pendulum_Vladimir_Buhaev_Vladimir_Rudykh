package com.example.physic.web.ui;

import com.example.physic.modules.kirchhoff.Kirchhoff;
import com.example.physic.modules.kirchhoff.KirchhoffError;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/kirchhoff")
public class KirchhoffController {

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("kirchhoff", new Kirchhoff());
        return "kirchhoff";
    }

    @PostMapping("/calculate-first")
    public String calculateFirst(
            @RequestParam double i1In,
            @RequestParam double i2In,
            @RequestParam double i3In,
            @RequestParam(defaultValue = "0.05") double deltaI,
            Model model) {

        Kirchhoff k = new Kirchhoff();
        k.setI1In(i1In); k.setI2In(i2In); k.setI3In(i3In);
        k.calculateFirst();

        KirchhoffError err = new KirchhoffError(deltaI, 0.1, 0.5);
        err.calculateFirstLawError(k.getIOut());

        model.addAttribute("kirchhoff", k);
        model.addAttribute("error", err);
        model.addAttribute("firstResult", true);
        return "kirchhoff";
    }

    @PostMapping("/calculate-second")
    public String calculateSecond(
            @RequestParam double emf,
            @RequestParam double r1,
            @RequestParam double r2,
            @RequestParam double r3,
            @RequestParam(defaultValue = "0.1")  double deltaE,
            @RequestParam(defaultValue = "0.5")  double deltaR,
            Model model) {

        Kirchhoff k = new Kirchhoff();
        k.setEmf(emf);
        k.setR1(r1); k.setR2(r2); k.setR3(r3);
        k.calculateSecond();

        KirchhoffError err = new KirchhoffError(0.05, deltaE, deltaR);
        err.calculateSecondLawError(
                r1, r2, r3,
                k.getRPar(), k.getRTotal(),
                k.getITotal(), k.getUPar(),
                k.getU3(), k.getI1(), k.getI2());

        model.addAttribute("kirchhoff", k);
        model.addAttribute("error", err);
        model.addAttribute("secondResult", true);
        return "kirchhoff";
    }
}