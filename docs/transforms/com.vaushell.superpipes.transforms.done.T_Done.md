![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_Done

Full class path : [`com.vaushell.superpipes.transforms.done.T_Done`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/done/T_Done.java)


## Goal

With this transform, message can't pass more than once.


__How does it work ?__

It adds all string and creates a MD5 hash.

If the hash passes more than once, it is discarded.


## Standard parameters

No parameter.


## Use example

```xml
<transform type="com.vaushell.superpipes.transforms.done.T_Done" />
```
