package tacos.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Order;
import tacos.Taco;
import tacos.repo.IngredientRepository;
import tacos.repo.TacoRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
	private IngredientRepository ingredientRepo;
	private TacoRepository designRepo;

	@Autowired
	public DesignTacoController(
			IngredientRepository ingredientRepo,
			TacoRepository designRepo) {
		this.ingredientRepo = ingredientRepo;
		this.designRepo = designRepo;
	}

	@GetMapping
	public String showDesignFrom(Model model) {
		List<Ingredient> ingredients = new ArrayList<>();
		this.ingredientRepo.findAll().forEach(ingredients::add);

		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
					filterByType(ingredients, type));
		}

		model.addAttribute("design", new Taco());
		return "design";
	}

	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}

	@ModelAttribute(name = "taco")
	public Taco taco() {
		return new Taco();
	}

	@PostMapping
	public String processDesign(
			@Valid Taco design, Errors errors,
			@ModelAttribute Order order) {
		if (errors.hasErrors()) {
			return "design";
		}
		Taco saved = designRepo.save(design);
//		order.addDesign(saved);
		log.info("Processing design: {}", design);
		return "redirect:/orders/current";
	}

	private List<Ingredient> filterByType(
			List<Ingredient> ingredients, Type type) {
		return ingredients
				.stream()
				.filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
	}
}