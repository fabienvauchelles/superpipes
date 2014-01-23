![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_TB_Delete

Full class path : [`com.vaushell.superpipes.nodes.tumblr.N_TB_Delete`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/tumblr/N_TB_Delete.java)


## Goal

This node deletes a message from a Tumblr blog.

To use the node, I create a Tumblr application and get my credentials (key and secret).

See [How to create a Tumblr application and get credentials](../tutorials/Create_Tumblr_Application.md) and [How to link a Tumblr application with SuperPipes](../tutorials/Link_Tumblr_Application.md).


## Default parameters

* anti-burst: 500
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | ---
timeout | Socket timeout in milliseconds. How long should I wait before the link is deleted ? | long | no | 20000 | 20000
retry | How many times do I retry if the check fails ? | integer | no | 3 | 3
delayBetweenRetry | How long should I wait between 2 checks ? (in milliseconds) | long | no | 5000 | 5000
key | Application Key | string | yes | N/A | A46892234irzeir23zer2349234zer23490234
secret | Application Secret | string | yes | N/A | 1239zer234ZEReeAZE2azeiiaazeiazeier
blogname | Name of the blog (such as lesliensducode.tumblr.com) | string | no | N/A | lesliensducode.tumblr.com


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_delete_tumblr.png)

I delete all messages of my Tumblr blog 'lesliensducode.tumblr.com':

```xml
<node id="tumblr-delete" type="com.vaushell.superpipes.nodes.tumblr.N_TB_Delete">
    <params>
        <param name="key" value="A46892234irzeir23zer2349234zer23490234" />
        <param name="secret" value="1239zer234ZEReeAZE2azeiiaazeiazeier" />
        <param name="blogname" value="lesliensducode.tumblr.com" />
    </params>
</node>
```
