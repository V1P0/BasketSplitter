package com.ocado.basket.logic.minimzer;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.ocado.basket.exceptions.InvalidItemException;

import java.util.*;
import java.util.stream.Collectors;

public class LinearDeliveryCountMinimizer implements AbstractDeliveryCountMinimizer{
    static {
        Loader.loadNativeLibraries();
    }

    private final Map<String, List<String>> deliveryOptions;

    public LinearDeliveryCountMinimizer(Map<String, List<String>> deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    @Override
    public Set<String> optimizeBasket(List<String> products) {
        List<String> allMethods = deliveryOptions.values().stream().flatMap(List::stream).distinct().collect(Collectors.toList());
        for (String product : products) {
            if (!deliveryOptions.containsKey(product)) {
                throw new InvalidItemException("Invalid item: " + product + " not found in delivery options");
            }
        }
        return solveBasketDelivery(deliveryOptions, products, allMethods);
    }

    private static Set<String> solveBasketDelivery(Map<String, List<String>> deliveryOptions, List<String> allProducts, List<String> allMethods) {
        MPSolver solver = new MPSolver("BasketDeliveryOptimization",
                MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        // Tworzenie zmiennych decyzyjnych
        Map<String, MPVariable> x = new HashMap<>();
        for (String method : allMethods) {
            x.put(method, solver.makeIntVar(0, 1, "x[" + method + "]"));
        }

        Map<String, Map<String, MPVariable>> y = new HashMap<>();
        for (String product : allProducts) {
            y.put(product, new HashMap<>());
            for (String method : deliveryOptions.getOrDefault(product, List.of())) {
                y.get(product).put(method, solver.makeIntVar(0, 1, "y[" + product + "][" + method + "]"));
            }
        }

        // Dodawanie ograniczeń
        for (String product : allProducts) {
            MPConstraint constraint = solver.makeConstraint(1, Double.POSITIVE_INFINITY);
            for (String method : deliveryOptions.getOrDefault(product, List.of())) {
                constraint.setCoefficient(y.get(product).get(method), 1);
            }
        }

        for (String product : allProducts) {
            for (String method : deliveryOptions.getOrDefault(product, List.of())) {
                MPConstraint constraint = solver.makeConstraint(-1, 0);
                constraint.setCoefficient(y.get(product).get(method), 1);
                constraint.setCoefficient(x.get(method), -1);
            }
        }

        // Definiowanie funkcji celu
        MPObjective objective = solver.objective();
        for (String method : allMethods) {
            objective.setCoefficient(x.get(method), 1);
        }
        objective.setMinimization();

        // Rozwiązywanie problemu
        MPSolver.ResultStatus resultStatus = solver.solve();
        Set<String> usedMethods = new HashSet<>();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            for (String method : allMethods) {
                if (x.get(method).solutionValue() > 0.5) {
                    usedMethods.add(method);
                }
            }
        } else {
            throw new RuntimeException("No solution found");
        }
        return usedMethods;
    }


}
