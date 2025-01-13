package com.luv2code.jobportal.controller;

import com.luv2code.jobportal.entity.RecruiterProfile;
import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.repository.UsersRepository;
import com.luv2code.jobportal.services.RecruiterProfileService;
import com.luv2code.jobportal.util.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {


    private final UsersRepository usersRepository;

    private final RecruiterProfileService recruiterProfileService;
    //private final AdviceModeImportSelector adviceModeImportSelector;


    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
       // this.adviceModeImportSelector = adviceModeImportSelector;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth instanceof AnonymousAuthenticationToken))
        {
            String currentUsername = auth.getName();

            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not find the user"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());

            if(!recruiterProfile.isEmpty())
            {
                model.addAttribute("profile", recruiterProfile.get());
                System.out.println("First call" + model.containsAttribute("profile"));
            }
        }
        return "recruiter-profile";
    }

    @PostMapping("/addNew")
    public String addNew(@Valid RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth instanceof AnonymousAuthenticationToken))
        {
            String currentUsername = auth.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not find the user"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile", recruiterProfile);
        System.out.println("Second call" + model.containsAttribute("profile"));
        String fileName ="";
        if(!multipartFile.getOriginalFilename().equals(""))
        {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        String uploadDir = "photos/recruiter/"+savedUser.getProfilePhoto();

        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";

    }
}
