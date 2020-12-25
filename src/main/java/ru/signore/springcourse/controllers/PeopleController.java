package ru.signore.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.signore.springcourse.dao.PersonDAO;
import ru.signore.springcourse.models.Person;

import javax.validation.Valid;


//@Controller - (Слой представления) Аннотация для маркировки java класса, как класса контроллера.
//        Данный класс представляет собой компонент, похожий на обычный сервлет (HttpServlet)
//        (работающий с объектами HttpServletRequest и HttpServletResponse),
//        но с расширенными возможностями от Spring Framework.
@Controller
//Все адреса в данном контроллере localhost:8080/people
@RequestMapping("/people")
public class PeopleController {

    private final PersonDAO personDAO;

//    автоматическое внедрение bean в контроллер, лучше использовать внедрение через конструктор или сет (DI)
//    @Autowired - Аннотация позволяет автоматически установить значение поля.
    @Autowired
    public PeopleController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    //Набрав localhost:8080/people попадем в данный запрос
    @GetMapping()
    public String index(Model model){
        //Получим всех людей из DAO, положим их в модель и передадим на отображение в представление,
        //потом с помощью thymeleaf отобразим
        //в people будет лежать список из людей
        model.addAttribute("people",personDAO.index());
        //возвращаем шаблон(страницу), который будет отображать список из людей
        return "people/index";
    }

    //к localhost:8080/people добавляется /id ({id} - означает что можем указать любое число)
    @GetMapping("/{id}")
    //с помощью аннотации @PathVariable извлекаем id из URl и получем к нему доступ внутри метода
    //в аргументе id будет лежать то целое число, которое находится в адресе запроса к этому методу
    //в качестве второго аргумента model, т.к. будет передаваться человек в шаблон
    public String show(@PathVariable("id") int id, Model model){
        //Получим 1 человека по его id из DAO и отдадим на отображение в представление
        //в person будет лежать результат метода show(id)
        model.addAttribute("person",personDAO.show(id));
        //возвращаем шаблон(страницу), который будет отображать человек
        return "people/show";
    }

    //GET запрос на new person, вернется localhost:8080/people/new
    //при использовании таймлифф форм нужно передавать тот объект, для которого данная форма нужна
    @GetMapping("/new")
    public String newPerson(Model model){
        //передаем объект Person в модели, person - ключ , объект Person - значение (id, age =0, name, email = null)
        model.addAttribute("person", new Person());
        return "people/new";
    }

//    после заполения формы и нажатия Create будет такой POST запрос
//    localhost:8080/people?id=1&name=Tom&age=3&email=tom@gmail.com
    @PostMapping
    //для создания нового человека используется аннотация @ModelAttribute, ключ person,
    // в данном ключе будет лежать объект Person с данными из html формы new в виде POST запроса
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "/people/new";
        //метод save принимает на вход объект Person и сохраняет его в БД
        personDAO.save(person);
        //используем механизм redirect, браузер сделает запрос к странице /people
        return "redirect:/people";
    }
    //
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("person", personDAO.show(id));
        return "people/edit";
    }

//    @ModelAttribute - Аннотация, связывающая параметр метода или возвращаемое значение метода с атрибутом модели,
//    которая будет использоваться при выводе jsp-страницы.
//    @PathVariable - Аннотация, которая показывает, что параметр метода должен быть связан с переменной из урл-адреса.
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("id") int id){
        if (bindingResult.hasErrors())
            return "/people/edit";
        personDAO.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        personDAO.delete(id);
        return "redirect:/people";

    }

}
