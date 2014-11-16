![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_Shorten

Full class path : [`com.vaushell.superpipes.transforms.bitly.T_Shorten`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/bitly/T_Shorten.java)


## Goal

This transform reduces a long URI to a short URI with Bitly service.


For example:

http://sebsauvage.net/links/?IwlDoA becomes http://bit.ly/1lLnAva


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
<transform type="com.vaushell.superpipes.transforms.bitly.T_Shorten">
    <params>
      <param name="username" value="fabienxxxxxxxxx" />
      <param name="username" value="R_xxxxxxxxxxxx" />
    </params>
</transform>
```
