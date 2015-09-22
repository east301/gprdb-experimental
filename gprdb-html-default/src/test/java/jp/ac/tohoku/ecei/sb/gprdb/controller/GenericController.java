package jp.ac.tohoku.ecei.sb.gprdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Shu Tadaka
 */
@Controller
public class GenericController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getWelcomePage() {
        return new ModelAndView("generic-welcome");
    }

}
