Fluent
===

This is a test framework which is mainly designed for web applications 
that use json-formation-based http protocol to communicate.

There are three main concepts:
* Operator
* Pipeline
* Execution

Pipeline is consisted by Opeartors. Execution is generated by running pipeline in the website.

Right now there are two types of operators:
* Http Operator
    * Send http request and receive response from web application backend.
* Data Validation Operator
    * Used for validating the data received by http operator, you can use certain char sequence pattern to refer to the response or other parameters.
* Execution
    * It's generated by running the pipeline and you can check the all the node execution information and check it in the bottom of the website. 

How to build and run
---

```shell script
make docker && docker run -p 8080:8080 tinkericlee/fluent:1.0.0
```

if you want to store data in local disk, add option `-v /var/lib/tinkmaster/fluent:/var/lib/tinkmaster/fluent` after `docker run`


Modules
---

**fluent-common**

This module is consisted of these types of classes:
* Project-leveled exceptions
* Project-leveled entity
* Utils
    * http tools
    
**fluent-api-persistence**

Responsible for storing and fetching database data.

**fluent-core**
 
This module contains the self-designed scheduler .  
Main class types:
* PipelineTaskDetector
    * Detect the unscheduled executions and submit it to the scheduler service.
* Scheduler service
    * Initialized with several tasks and wait until having gotten the task. After completing the task, continue to wait.
    
**fluent-api-service**

Mainly interact with `fluent-persistence` and providing data fetching and data storing operation.

**fluent-api-server**

Supported by `SpringBoot` framework and has server configuration and controllers.

**fluent-web**

Use `react+redux+antd` framework to construct the frontend.

**fluent-disk**

Make distribution of project fluent.

