![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_RandomLinear

Full class path : [`com.vaushell.superpipes.transforms.rnd.T_RandomLinear`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/rnd/T_RandomLinear.java)


## Goal

This transform keeps only X percent of messages.

The function used is `java.util.Random.nextInt()`.


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
percent | Percentage of acceptance (X/100). | integer | yes | N/A | 30


## Use example

```xml
<transform type="com.vaushell.superpipes.transforms.rnd.T_RandomLinear">
    <params>
      <param name="percent" value="30" />
    </params>
</transform>
```
