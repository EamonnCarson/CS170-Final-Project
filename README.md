# CS170-Final-Project

## The Problem

This project is to write a solver for the NP-Hard problem — the "Not between" problem — which has the following form:
given a set of clauses (tuples of 3 elements 'x y z') determine an ordering L of elements such that for each clause 'x y z'
it is true that z is not between the elements x and y in the ordering L.

For example if we had 3 clauses:
```
1 3 2 // 2 is not between 1 and 3
1 4 2 // 2 is not between 1 and 4
2 4 3 // 3 is not between 2 and 4
```
then one possible ordering would be "1 3 4 2"

## The Solution
The solver reduces the Not-Between Problem into a SAT problem (specifically 3SAT) and then uses SAT4J (a Java SAT solving library)
to derive a solution. A proper explanation of the solution can be found in CS170_Project_Writup.pdf

## Evaluation
Considering this program took a day to research and write, I'd recommend this approach to solving NP-Hard problems 
(reduction to a well-researched NP-Hard problem) to anyone who just needs a quick solution on a small scale.
In terms of project scores, I do not know exactly how much time other groups used for computation; however it seemed that
simulated annealing was another popular approach which seemed to be rather effective, so consider that as well.
