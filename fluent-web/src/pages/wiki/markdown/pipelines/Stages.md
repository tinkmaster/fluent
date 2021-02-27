## Stages

The pipeline has three stages which are shown above the graph.

* Before
* Execute
* Clean

Before stage
------

This stage is designed for making preparation for later `execute stage`. And this stages is usually considered with `clean stage` since you have to **clean all the resources**.

You can usually add some resources you need:

* create a new workspace to avoid affecting exising system
* ......

or check `the real environment` status

* if all the machine works well
* if the backend is alive
* ......

Execute Stage
------

In here, since all the preparations have made, run the test logic here and check its response using the different [Operators](/#/wiki/operators/introduction).

Clean Stage
------

Each pipeline should have the clean stage to make sure all the test resources have been clean.

Also you can send messages `using operators` with `conditional run in operator` and `MESSAGE SENDER type in operator` to your dev team to notify the test result.