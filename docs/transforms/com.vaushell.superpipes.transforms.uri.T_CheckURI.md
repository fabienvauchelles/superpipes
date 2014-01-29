![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_CheckURI

Full class path : [`com.vaushell.superpipes.transforms.uri.T_CheckURI`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/uri/T_CheckURI.java)


## Goal

This transform discard message with an inaccessible URI.


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
timeout | Socket timeout in milliseconds. How long should I wait before reaching the URI ? | long | no | 20000 | 20000
retry | How many times do I retry if the check fails ? | integer | no | 3 | 10
wait-time | How long should I wait between 2 checks ? (in milliseconds) | long | no | 2000 | 5000
wait-time-multiplier | How multiple I `wait-time` each time ? | double | no | 2.0 | 2.0
jitter-range | Add or substract randomly time to `wait-time` (between 0 and `jitter-range`) | int | no | 500 | 500
max-duration | How long shoud I retry ? (in milliseconds, 0=disabled) | long | no | 10000 | 10000


## Use example

```xml
<transform type="com.vaushell.superpipes.transforms.tags_T_Tags">
    <params>
      <param name="tags" value="java,coding" />
      <param name="type" value="INCLUDE_ONE" />
    </params>
</transform>
```
