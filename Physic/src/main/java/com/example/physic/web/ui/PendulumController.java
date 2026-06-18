package com.example.physic.web.ui;

import com.example.physic.modules.pendulum.PendulumForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pendulum")
@SessionAttributes("pendulumForm")
public class PendulumController {

    @ModelAttribute("pendulumForm")
    public PendulumForm pendulumForm() {
        PendulumForm form = new PendulumForm();
        form.setPlanet("Земля");
        form.setLength(1.0);
        form.setOscillations(10);
        form.setAngle(10.0);
        form.setLengthError(0.001);
        form.setTimeError(0.1);
        form.setAngleError(1.0);
        return form;
    }

    @GetMapping
    public String index(@ModelAttribute("pendulumForm") PendulumForm form, ModelMap model) {
        model.addAttribute("planets", PendulumForm.PLANETS_G.keySet());
        return "pendulum";
    }

    @PostMapping("/calculate")
    public String calculate(
            @ModelAttribute("pendulumForm") PendulumForm form,
            ModelMap model) {

        form.calculate();
        model.addAttribute("planets", PendulumForm.PLANETS_G.keySet());
        return "pendulum";
    }

    @PostMapping("/reset")
    public String reset(@ModelAttribute("pendulumForm") PendulumForm form) {
        form.setPlanet("Земля");
        form.setLength(null);
        form.setTimeTotal(null);
        form.setOscillations(null);
        form.setAngle(null);
        form.setLengthError(0.001);
        form.setTimeError(0.1);
        form.setAngleError(1.0);
        form.setError(null);
        return "redirect:/pendulum";
    }
}