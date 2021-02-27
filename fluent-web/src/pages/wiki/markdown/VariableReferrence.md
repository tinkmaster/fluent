## Varibales Referrence

All the varibales must be included in pattern `#{ }` to tell Fluent that you need to resolve this variables.

And in variable's values, patterns `#{ }` are allowed to be included by in other patterns `#{ }`, such as `#{parseJson(#{parseJson(#{operators[3].result},/body)},/data/0)}`

Content about:

* The variables provided by fluent

The variables provided by fluent
---

**Pipeline**

| Name | Comment | Attentions |
| - | - | - |
| #{pipeline.name} | Get current pipeline name | - |
| #{currentStage.operators[$id].xxxx} | Get operator values in current stage | - |
| #{stages.$stage.operators[$id].xxxx} | If you want to get operator info in different `$stages`, `$stage` can be replace by `before/execute/clean` | - |


**Operators**

Get operator values in **current stage**

| Name | Comment | Attentions |
| - | - | - |
| #{operators[$id].name} | `$id` should be replace with the id displaying in the pipeline graph node. | - |
| #{operators[$id].type} | The same CharSequence you select in the operator | - |
| #{operators[$id].result} | The execution result stored in each operator | Before you are going to fetch the operator execution result, make sure that operator has already finished. |

**Environments**

Get environment values in **different environments**

| Name | Comment | Attentions |
| - | - | - |
| #{currentEnv.$varName} | `$varName` should be replaced by the name you defined in the environment variable pairs. | Please make sure you have selectd the env value in pipeline. |
| #{env.$envName} | `$envName` should be replaced by the name you defined in the environments. You can refer to other environment's value using this pattern. | - |


**Globals**

Get variable value in **`Global` scope**.

| Name | Comment | Attentions |
| - | - | - |
| #{global}.$varName | `$varName` should be replaced by the name you defined in the global variable pairs. | - |

**Secrets**

Get variable value in **`Secrets` scope**.

| Name | Comment | Attentions |
| - | - | - |
| #{secret.$varName} | `$varName` should be replaced by the name you defined in the secret variable pairs. | - |

