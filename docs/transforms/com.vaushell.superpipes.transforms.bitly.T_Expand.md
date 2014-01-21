![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_Expand

Full class path : [`com.vaushell.superpipes.transforms.bitly.T_Expand`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/bitly/T_Expand.java)


## Goal

This transform expands a short URI to a long URI with Bitly service.

For example:

http://bit.ly/1lLnAva becomes http://sebsauvage.net/links/?IwlDoA

Before using the transform, I have:

1. To create a [Bitly account](https://bitly.com/);
2. To get credentials `Username` and `API key` at https://bitly.com/a/your_api_key


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
username | API username | string | yes | N/A | fabienxxxxxxxxx
apikey | API key | string | yes | N/A | R_xxxxxxxxxxxx


## Use example

```xml
<transform type="com.vaushell.superpipes.transforms.bitly.T_Expand">
    <params>
      <param name="username" value="fabienxxxxxxxxx" />
      <param name="username" value="R_xxxxxxxxxxxx" />
    </params>
</transform>
```
