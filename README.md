
**COMPILE:**

./compile.bat

**RUN:**

./run.bat

**NOTES:**

no need "previous stop"

FlightInfo need a linkedlist to store how many flights need to go to and which is the current flight

Everytime visit node, find next location going to (linked list attribute needed in FlightInfo class)

When initially load data with 2 pit stops, need to split into 3 flight info objects
so AB, BC, and CD.

For BC and CD, we need to store a boolean that it is part of a pitstop 
flight (eg. isOrigin) so cannot start from here (or can just skip here)


**REPORT**
    - Dataset
    - Algorithm
    - Experimentation
    - Analyses

    1. Restate problem ( jiongyu )
        - We can describe it as " wait for covid to end , anticipate our solutuon to be useful "
    2. Describe gist of algo implemented and key analyses
        - analyzing the correctness and efficiency properties of those algorithms
            - Proving by induction etc..
            - [https://www.cs.rice.edu/~nakhleh/COMP182/Greedy.pdf](https://www.cs.rice.edu/~nakhleh/COMP182/Greedy.pdf)
        - greedy1 -> brute-force/back-tracking -> dijkstra ( progression and improve over time? might link to experimental results )
    3. Outline dataset
        - Such a dataset has real-world relevance and is of significant size to study algorithmic efficiencies as the data size scales.
        - Justify why our dataset is not that huge.
    4. Present experimental results
        - Running experiments and discussing the experimental outcomes
        - It means to study the performance of a data structure or algorithm as scientifically as possible, by taking into account how it performs under different conditions. The evaluation criteria include correctness, time complexity, space complexity.

            For examples:

            - you may test it on dataset of different sizes to study how scalable the algorithm is (in time and in space),
            - you may try to understand when one algo does better than another algo,
            - or how one algo behaves on different datasets.
    5. X-factor ( jiongyu )
        1. Applicability
        2. What makes project special
            - Interestingness of the problem
            - How we implemented data structures not taught in class, how we modify it to suit interesting constraints
    6. Potential extensions to be done

    ( pt 1 - 4 ( 50 % ) , pt 5 ( 15 %) , good video ( 20%)  )