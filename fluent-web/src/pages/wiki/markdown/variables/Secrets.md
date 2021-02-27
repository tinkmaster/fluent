## Secrets

`Secrets` is not the same as `Global` and `Environemnt`. You are not allowed to edit its value once it has been created.

You can only delete the secret and recreate one.

**The key used to encrypt the secret value is different in different fluent server**

Variables pairs
---

You can add endless variables in `Secrets`, each variable needs two elements: `variable name` and `variable value`

You are not allowed to refer to other variable both in name scope and value scope.

Fluent will only return the **CharSequence** you entered in value scope during the creation.