# BasketSplitter

This is a simple tool to split a basket of items into delivery options that minimizes the number of deliveries while also maximizing the number of items in the biggest delivery.

# Algorithm
The algorithm consists of two main steps:
1. Find the smallest number of deliveries that can be made.
2. Find the best way to split the items into the deliveries.

## Step 1
This project contains two ways to find the smallest number of deliveries:
### 1. **Dynamic Programming**: (implemented in `DynamicDeliveryCountMinimizer`)

1. **Definition of the objective function:** Let \( F(S) \) be the minimum number of delivery methods needed to cover the set of products \( S \subseteq P \).
2. **Base case:** \( F(\emptyset) = 0 \), because no products require delivery.
3. **Recursive step:** For each product \( p \) not included in \( S \), find the delivery method that, by adding the fewest new products, maximizes the coverage of the set \( S \). In other words, for each product \( p \) outside \( S \), calculate \( F(S \cup \{p\}) \) as the minimum of \( F(S) + 1 \) (if adding product \( p \) requires a new delivery method) and \( F(S) \) (if product \( p \) can be covered by already selected delivery methods).
4. **Application of memoization:** Due to a large number of repeated calculations, apply memoization to store the results of calculations for individual subsets of products to avoid redundant calculations.

After determining the function F for all subsets of products, we find the solution by analyzing the results for F(P), which gives the minimum number of delivery methods needed to deliver all products.

### 2. **Linear Programming**: (implemented in `LinearDeliveryCountMinimizer`)

#### Problem Formulation

Assume that we have n products and m available delivery methods. Each product i can be delivered by one or more delivery methods from a specific subset m. The goal is to minimize the number of used delivery methods while ensuring that each product is delivered.

#### Decision Variables

- x_j =
    - 1, if delivery method j is used
    - 0, otherwise

- y_{ij} =
    - 1, if product i is delivered by delivery method j
    - 0, otherwise

#### Objective Function

Minimize the number of used delivery methods:

Minimize Z = sum of x_j for j = 1, 2, ..., m

#### Constraints

1. Each product must be delivered by an available delivery method:

sum of y_{ij} for j = 1..m >= 1, for each i = 1..n
2. Product i can be delivered by delivery method j only if this delivery method is used:

-1 <= y_{ij} - x_j, <= 0 for each i = 1..n and for each j = 1..m

3. Decision variables are binary:

x_j, y_{ij} are in {0, 1}

#### Solution

I used Google-OR tools solver for the solution.

## Step 2
(implemented in `ItemsCountMaximizer`)

After determining the smallest number of deliveries, the next step is to find the best way to split the items into the deliveries. This is done by using a greedy algorithm that sorts the products by weight and then assigns them to the delivery with the smallest weight.
For this I used a greedy algorithm that prioritizes the delivery that appears in the most items.
Then we assign the items to that delivery and repeat the process until all items are assigned.

## Installation
Project is built using Gradle. To build the project, run the following command in the project root directory:
```shell
./gradlew build
```
Project uses java 17.

### Used libraries
- JUnit 5 for testing
- AssertJ also for testing
- Google-OR tools for linear programming
- lombok for boilerplate code reduction
- json for parsing json files

## Notes
- At first, I tried using only a greedy approach to solve the problem, but it was not optimal. The greedy approach does not guarantee the smallest number of deliveries. But it worked really fast.
The test that broke the greedy is included in `BasketSplitterTest.split_returnsCorrectDeliveryOptions_forGivenItems` test.
- When trying to fix the greedy algorithm, I tried to use a dynamic programming. It worked well but was an order of magnitude slower than the greedy approach.
- I wanted to try something else and thought about using linear programming. I decided to use Google-OR tools solver because it's free and I had some experience with it. 
It worked but unfortunately, it was even slower than the dynamic programming approach. I decided to keep the code, but it's not used anywhere in the project and does not have tests.
If you want to try it, swap `DynamicDeliveryCountMinimizer` with `LinearDeliveryCountMinimizer` in `BasketSplitter` constructor.
If not for given api specification, I would use dependency injection to switch between the algorithms.
- Dependency injection could also be used to switch ConfigLoader implementation if it were to source data from something different than a file.