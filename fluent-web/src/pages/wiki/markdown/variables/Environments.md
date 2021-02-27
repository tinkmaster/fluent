## Environments

`Environment` variable is used to store env related variables such as:
* backend host name
* backend port

Variables pair
---

You can add endless variables in one env, each variable needs two elements: `variable name` and `variable value`

You are not allowed to refer to other variable in name scope. However, **you can also refer to other variables except in `other environment`**

In pipeline, you can only refer to one `Environment`. So make sure manage your referrence chain well.