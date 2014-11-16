![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_FindBiggest

Full class path : [`com.vaushell.superpipes.transforms.image.T_FindBiggest`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/image/T_FindBiggest.java)


## Goal

This transform finds the largest image (pixel size) of the URI of the message.

The image is downloaded inside the message.

I add it before a Twitter post node ([N_TW_Post](../nodes/com.vaushell.superpipes.nodes.twitter.N_TW_Post.md)), to post a tweet with an image.


## Standard parameters

No parameter.


## Use example

```xml
<transform type="com.vaushell.superpipes.transforms.image.T_FindBiggest" />
```
