package com.graffitab.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ExampleController {

    private static Logger LOG = LogManager.getLogger();

    @RequestMapping("/")
    public String getHome(ModelMap model) {
        model.addAttribute("test","test");
        return "home";
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public @ResponseBody FileTestDto getFile() {

        FileTestDto fileDto = new FileTestDto();
        fileDto.setName("Something");
        fileDto.setValue("Something else");
        LOG.info("This file is a test");
        return fileDto;
    }

   /*
    "file": {
        "name": "I like it",
                "value": "I love it"
    }
   */
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public @ResponseBody FileTestDto setFile(@JsonProperty("file") FileTestDto fileDto) {

        String name = fileDto.getName();
        String value = fileDto.getValue();

        return fileDto;
    }

    /*
        {
          "file2": {
            "testValue": "Jeje",
            "file": {
              "name": "I like it",
              "value": "I love it"
            }
          }
        }
     */

    @RequestMapping(value = "/file2", method = RequestMethod.POST)
    public @ResponseBody TestingDto setFile(@JsonProperty("file2") TestingDto fileDto) {
        String name = fileDto.getTestValue();
        return fileDto;
    }

}