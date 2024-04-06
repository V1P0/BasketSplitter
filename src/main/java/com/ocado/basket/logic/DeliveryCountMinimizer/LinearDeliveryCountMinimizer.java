package com.ocado.basket.logic.DeliveryCountMinimizer;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.ocado.basket.exceptions.InvalidItemException;
import com.ocado.basket.exceptions.NoSolutionFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for finding the optimal delivery options for a given basket of products.
 * It uses linear programming to find the optimal delivery options.
 * It uses Google OR-Tools to solve the linear programming problem.
 *
 * @version 1.0
 */
public class LinearDeliveryCountMinimizer implements AbstractDeliveryCountMinimizer {
    static {
        Loader.loadNativeLibraries();
    }

    private final Map<String, List<String>> deliveryOptions;

    public LinearDeliveryCountMinimizer(Map<String, List<String>> deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    @Override
    public Set<String> optimizeBasket(List<String> products) {
        validateProducts(products);
        List<String> allMethods = getAllMethods();
        return solveBasketDelivery(products, allMethods);
    }

    private void validateProducts(List<String> products) {
        for (String product : products) {
            if (!deliveryOptions.containsKey(product)) {
                throw new InvalidItemException("Invalid item: " + product + " not found in delivery options");
            }
        }
    }

    private List<String> getAllMethods() {
        return deliveryOptions.values().stream().flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    private Set<String> solveBasketDelivery(List<String> allProducts, List<String> allMethods) {
        MPSolver solver = createSolver();
        Map<String, MPVariable> decisionVariables = createDecisionVariables(allMethods, solver);
        Map<String, Map<String, MPVariable>> productMethodVariables = createProductMethodVariables(allProducts, solver);
        addConstraints(allProducts, solver, decisionVariables, productMethodVariables);
        setObjective(allMethods, solver, decisionVariables);
        return solveProblem(allMethods, solver, decisionVariables);
    }

    private MPSolver createSolver() {
        return new MPSolver("BasketDeliveryOptimization", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
    }

    private Map<String, MPVariable> createDecisionVariables(List<String> allMethods, MPSolver solver) {
        Map<String, MPVariable> decisionVariables = new HashMap<>();
        for (String method : allMethods) {
            decisionVariables.put(method, solver.makeIntVar(0, 1, "x[" + method + "]"));
        }
        return decisionVariables;
    }

    private Map<String, Map<String, MPVariable>> createProductMethodVariables(List<String> allProducts, MPSolver solver) {
        Map<String, Map<String, MPVariable>> productMethodVariables = new HashMap<>();
        for (String product : allProducts) {
            productMethodVariables.put(product, new HashMap<>());
            for (String method : deliveryOptions.getOrDefault(product, List.of())) {
                productMethodVariables.get(product).put(method, solver.makeIntVar(0, 1, "y[" + product + "][" + method + "]"));
            }
        }
        return productMethodVariables;
    }

    private void addConstraints(List<String> allProducts, MPSolver solver, Map<String, MPVariable> decisionVariables, Map<String, Map<String, MPVariable>> productMethodVariables) {
        for (String product : allProducts) {
            addProductConstraint(solver, productMethodVariables, product);
            addMethodConstraint(solver, productMethodVariables, decisionVariables, product);
        }
    }

    private void addProductConstraint(MPSolver solver, Map<String, Map<String, MPVariable>> productMethodVariables, String product) {
        MPConstraint constraint = solver.makeConstraint(1, Double.POSITIVE_INFINITY);
        for (String method : deliveryOptions.getOrDefault(product, List.of())) {
            constraint.setCoefficient(productMethodVariables.get(product).get(method), 1);
        }
    }

    private void addMethodConstraint(MPSolver solver, Map<String, Map<String, MPVariable>> productMethodVariables, Map<String, MPVariable> decisionVariables, String product) {
        for (String method : deliveryOptions.getOrDefault(product, List.of())) {
            MPConstraint constraint = solver.makeConstraint(-1, 0);
            constraint.setCoefficient(productMethodVariables.get(product).get(method), 1);
            constraint.setCoefficient(decisionVariables.get(method), -1);
        }
    }

    private void setObjective(List<String> allMethods, MPSolver solver, Map<String, MPVariable> decisionVariables) {
        MPObjective objective = solver.objective();
        for (String method : allMethods) {
            objective.setCoefficient(decisionVariables.get(method), 1);
        }
        objective.setMinimization();
    }

    private Set<String> solveProblem(List<String> allMethods, MPSolver solver, Map<String, MPVariable> decisionVariables) {
        MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            return getUsedMethods(allMethods, decisionVariables);
        } else {
            throw new NoSolutionFoundException("No optimal solution found for the basket delivery optimization problem");
        }
    }

    private Set<String> getUsedMethods(List<String> allMethods, Map<String, MPVariable> decisionVariables) {
        Set<String> usedMethods = new HashSet<>();
        for (String method : allMethods) {
            if (decisionVariables.get(method).solutionValue() > 0.5) {
                usedMethods.add(method);
            }
        }
        return usedMethods;
    }
}

