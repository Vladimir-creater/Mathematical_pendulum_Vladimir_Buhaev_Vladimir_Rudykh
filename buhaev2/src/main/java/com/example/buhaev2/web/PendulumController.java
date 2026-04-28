package com.example.buhaev2.web;

import com.example.buhaev2.model.PendulumForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class PendulumController {

    @GetMapping("/")
    public String index(Model model) {
        PendulumForm form = new PendulumForm();
        form.setPlanet("🌍 Земля");
        form.setLength(1.0);
        form.setOscillations(10);

        model.addAttribute("form", form);
        model.addAttribute("planets", PendulumForm.PLANETS_G.keySet());
        return "pendulum";
    }

    @PostMapping("/calculate")
    public String calculate(@ModelAttribute("form") PendulumForm form, Model model) {
        form.calculate();
        model.addAttribute("form", form);
        model.addAttribute("planets", PendulumForm.PLANETS_G.keySet());
        return "pendulum";
    }
}