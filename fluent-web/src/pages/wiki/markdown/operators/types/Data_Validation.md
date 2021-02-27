## Data Validation Operator

`Data validation operator` is mainly used to check if your response is as expected.

for example:

You have sent a http request to created a workspace and you want to make sure the workspace is created as you expect.

You can send another http request to list the workspace and check if your workspace name is in the list.

Check Conditions
---

*In the `Data Validation Operator`, you can add endless check conditions in it. And all the condition will be checked during execution. If one condition fails, the whole operator will fail.*

**Variable to check**

You can use variable referrence to refer to the string you want to check.

`or`

Just enter the simple text.

**Check Functions**

There are several check function are supported in the `Data Validation Operator`

* `equals(var1)` (if you don't use a function, we will use `equals` function as default)
* `notEquals(var1)`
* `parseJson(var1, '/json/node/0/path')` - *The json node path rule is the same as the rule of jackson [json pointer path](https://www.baeldung.com/json-pointer) [(RFC 6901)](https://tools.ietf.org/html/rfc6901)*