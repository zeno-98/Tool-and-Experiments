## Experiment Result

## AUTOSAR-1 experiment
There are 8 automata in AUTOSAR-1

> runnable1,runnable2,runnable3,runnable4,buffer1,buffer2,schedule1,schedule2

the models of AUTOSAR-1 are available in  [autosar-1](https://github.com/suuuyu/verification/blob/main/src/main/resources/verification/autosar_ex2-source.xml)

### Verification for AUTOSAR-1 where `M_1` is a DOTA.

the property and partitions for `M_1` and `M_2` are shown below:

| ID   | properties                        | descriptions            | `M_1`            | `M_2`                                                        |
| ---- | --------------------------------- | ----------------------- | ---------------- | ------------------------------------------------------------ |
| 1    | A[ ] buffer1.count >= 0           | buffer1 never underflow | buffer1          | runnable1,runnable2,runnable3,runnable4,buffer2,schedule1,schedule2 |
| 2    | A[ ] buffer1.count <= buffer1.len | buffer1 never overflow  | *same as line 1* | *same as line 1*                                             |
| 3    | A[ ] buffer2.count >= 0           | buffer2 never overflow  | buffer2          | runnable1,runnable2,runnable3,runnable4,buffer2,schedule1,schedule2 |
| 4    | A[ ] buffer2.count <= buffer2.len | buffer2 never underflow | *same as line 3* | *same as line 3*                                             |

### Verification for AUTOSAR-1 where `M_1` is a composition of DOTAs.

the property and partitions for `M_1` and `M_2` are shown below:

| ID   | properties                               | descriptions               | `M_1`                                                             | `M_2`                |
| ---- | ---------------------------------------- | ------------------------------------------------------------------------------------------------  |-------------------------------------------------------------------| ------------------------|
| 1    | A[ ] buffer1.count >= 0                  | buffer1 never underflow                                      | runnable1,runnable2,runnable3,runnable4,buffer1,buffer2,schedule2 | schedule1 |
| 2    | A[ ] buffer1.count <= buffer1.len        | buffer1 never overflow                                       | *same as line 1*                                                  | *same as line 1*                                             |
| 3    | A[ ] buffer2.count >= 0                  | buffer2 never overflowsame as line 1                         | *same as line 1*                                                  | same as line 1 |
| 4    | A[ ] buffer2.count <= buffer2.len        | buffer2 never underflow                                      | *same as line 1*                                                  | *same as line 3*                                             |
| 5    | A[ ] not (runnable1.s2 and runnable2.s2) | The write and read actions of the two runnables can not be executed simultaneously | same as line 1                                                    | same as line 1 |
| 6    | A[ ] not (runnable3.s2 and runnable4.s2) | The read and write actions of the two runnables can not be executed simultaneously | runnable1,runnable2,runnable3,runnable4,buffer1,buffer2,schedule1 | schedule2 |

## AUTOSAR-2 experiment

There are 14 automata in AUTOSAR-2

> runnable1,runnable2,runnable3,runnable4,runnable5,runnable6,buffer1,buffer2,buffer3,buffer4,buffer5,rte,task,schedule

the models of AUTOSAR-2 are available in  [autosar-2](https://github.com/suuuyu/verification/blob/main/src/main/resources/verification/autosar_ex3-source.xml)

## Verification for AUTOSAR-2 

the property and partitions for `M_1` and `M_2` are shown below:

| ID   | properties                        | descriptions            | `M_1`                                                        | `M_2`            |
| ---- | --------------------------------- | ----------------------- | ------------------------------------------------------------ | ---------------- |
| 1    | A[ ] buffer1.count >= 0           | buffer1 never underflow | runnable1,runnable2,runnable3,runnable4,runnable5,runnable6,buffer1,buffer2,buffer3,buffer4,buffer5,rte,task | schedule         |
| 2    | A[ ] buffer1.count <= buffer1.len | buffer1 never overflow  | *same as line 1*                                             | *same as line 1* |
| 3    | A[ ] buffer2.count >= 0           | buffer2 never underflow | *same as line 1*                                             | *same as line 1* |
| 4    | A[ ] buffer2.count <= buffer2.len | buffer2 never overflow  | *same as line 1*                                             | *same as line 1* |

## AUTOSAR-3 Experiment

There are 14 automata in AUTOSAR-3

> runnable1,runnable2,runnable3,runnable4,runnable5,runnable6,runnable7,buffer1,buffer2,buffer3,task2,task3,task1,schedule;

the models of AUTOSAR-3 are available in  [autosar-3](https://github.com/suuuyu/verification/blob/main/src/main/resources/verification/autosar_ex4-origin.xml)

## Verification for AUTOSAR-3 

the property and partitions for `M_1` and `M_2` are shown below:

| ID   | properties              | descriptions            | `M_1`                                                        | `M_2`            |
| ---- | ----------------------- | ----------------------- | ------------------------------------------------------------ | ---------------- |
| 1    | A[ ] buffer1.count >= 0 | buffer1 never underflow | runnable1,runnable2,runnable3,runnable4,runnable5,runnable6,runnable7,buffer1,buffer2,buffer3,task1,task3 | schedule,task2   |
| 2    | A[ ] buffer2.count >=0  | buffer2 never underflow | runnable1,runnable2,runnable3,runnable4,runnable5,runnable6,runnable7,buffer1,buffer2,buffer3,task2,task3 | schedule,task1   |
| 3    | A[ ] buffer3.count >= 0 | buffer3 never underflow | *same as line 2*                                             | *same as line 2* |