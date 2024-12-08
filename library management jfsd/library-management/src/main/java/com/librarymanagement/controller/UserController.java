package com.librarymanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.librarymanagement.dao.BookCategoryDao;
import com.librarymanagement.dao.BookDao;
import com.librarymanagement.dao.BookRequestDao;
import com.librarymanagement.dao.UserDao;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BookCategory;
import com.librarymanagement.model.BookRequest;
import com.librarymanagement.model.User;
import com.librarymanagement.service.CaptchaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private BookCategoryDao bookCategoryDao;

    @Autowired
    private BookRequestDao bookRequestDao;

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/userlogin")
    public String goToLoginPage() {
        return "userlogin";
    }

    @GetMapping("/userregister")
    public String goToRegisterPage() {
        return "userregister";
    }

    @PostMapping("/userregister")
    public ModelAndView registerUser(@ModelAttribute User user, @RequestParam("g-recaptcha-response") String captchaResponse) {
        ModelAndView mv = new ModelAndView();

        // Verify CAPTCHA
        boolean isCaptchaValid = captchaService.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            mv.addObject("status", "CAPTCHA verification failed! Please try again.");
            mv.setViewName("userregister");
            return mv;
        }

        if (this.userDao.save(user) != null) {
            mv.addObject("status", user.getFirstname() + " Successfully Registered!");
            mv.setViewName("index");
        } else {
            mv.addObject("status", user.getFirstname() + " Failed to Register User!");
            mv.setViewName("userregister");
        }
        return mv;
    }

    @PostMapping("/userlogin")
    public ModelAndView loginUser(HttpServletRequest request, @RequestParam("userId") String userId,
                                  @RequestParam("password") String password, @RequestParam("g-recaptcha-response") String captchaResponse) {
        ModelAndView mv = new ModelAndView();

        // Verify CAPTCHA
        boolean isCaptchaValid = captchaService.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            mv.addObject("status", "CAPTCHA verification failed! Please try again.");
            mv.setViewName("userlogin");
            return mv;
        }

        User user = userDao.findByUserIdAndPassword(userId, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("activeuser", user);
            session.setAttribute("userlogin", "user");
            mv.addObject("status", user.getFirstname() + " Successfully Logged In!");
            mv.setViewName("index");
        } else {
            mv.addObject("status", "Invalid username or password!");
            mv.setViewName("userlogin");
        }
        return mv;
    }

    @GetMapping("/viewstudents")
    public ModelAndView viewCustomers() {
        ModelAndView mv = new ModelAndView();
        List<User> users = this.userDao.findAll();
        mv.addObject("users", users);
        mv.setViewName("viewstudents");
        return mv;
    }

    @GetMapping("/viewStudentDetails")
    public ModelAndView viewStudentDetails(@RequestParam("id") int id) {
        ModelAndView mv = new ModelAndView();
        User student = userDao.findById(id).orElse(null);
        mv.addObject("student", student);
        mv.setViewName("viewstudentdetails");
        return mv;
    }

    @PostMapping("/deleteStudent")
    public ModelAndView deleteStudent(@RequestParam("id") int id) {
        ModelAndView mv = new ModelAndView();
        userDao.deleteById(id);
        mv.addObject("status", "Student Deleted Successfully!!!");
        mv.setViewName("index");
        return mv;
    }

    // Category Management
    @GetMapping("/addcategory")
    public String addCategoryPage() {
        return "addcategory";
    }

    @PostMapping("/addcategory")
    public ModelAndView addCategory(@RequestParam("name") String name) {
        ModelAndView mv = new ModelAndView();
        BookCategory category = new BookCategory();
        category.setName(name);
        bookCategoryDao.save(category);
        mv.addObject("status", "Category added successfully!");
        mv.setViewName("index");
        return mv;
    }

    // Book Management
    @GetMapping("/addbook")
    public ModelAndView addBookPage() {
        ModelAndView mv = new ModelAndView();
        List<BookCategory> categories = this.bookCategoryDao.findAll();
        mv.addObject("categories", categories);
        mv.setViewName("addbook");
        return mv;
    }

    @PostMapping("/addbook")
    public ModelAndView addBook(@RequestParam("name") String name, @RequestParam("isbn") String isbn,
                                @RequestParam("author") String author, @RequestParam("publisher") String publisher,
                                @RequestParam("edition") String edition, @RequestParam("price") String price,
                                @RequestParam("quantity") int quantity, @RequestParam("categoryId") int categoryId) {
        ModelAndView mv = new ModelAndView();
        Book book = new Book();
        book.setName(name);
        book.setIsbn(isbn);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setEdition(edition);
        book.setPrice(Double.parseDouble(price));
        book.setQuantity(quantity);
        BookCategory category = bookCategoryDao.findById(categoryId).orElse(null);
        book.setCategory(category);
        bookDao.save(book);
        mv.addObject("status", "Book added successfully!");
        mv.setViewName("addbook");
        return mv;
    }

    @GetMapping("/viewallbooks")
    public ModelAndView viewAllBooksPage() {
        ModelAndView mv = new ModelAndView();
        List<Book> books = this.bookDao.findAll();
        mv.addObject("books", books);
        mv.setViewName("viewallbooks");
        return mv;
    }

    // More methods omitted for brevity (you can re-use the ones from your existing code)
}
