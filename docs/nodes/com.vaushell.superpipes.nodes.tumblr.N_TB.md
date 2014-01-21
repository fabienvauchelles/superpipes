![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_TB

Full class path : [`com.vaushell.superpipes.nodes.tumblr.N_TB`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/tumblr/N_TB.java)


## Goal

This node reads a Tumblr blog.

To use the node, I create a Tumblr application and get my credentials (key and secret).

See [How to create a Tumblr application and get credentials](../tutorials/Create_Tumblr_Application.md).


## Default parameters

* anti-burst: 2000 (2 seconds)
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | ---
key | Application Key | string | yes | N/A | A46892234irzeir23zer2349234zer23490234
secret | Application Secret | string | yes | N/A | 1239zer234ZEReeAZE2azeiiaazeiazeier
blogname | Name of the blog (such as lesliensducode.tumblr.com) | string | no | N/A | lesliensducode.tumblr.com
max | Number of messages to read | integer | yes | N/A | 50


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_migrate_tumblr.png)

I read 50 messages of my Tumblr blog 'lesliensducode.tumblr.com' every 15 minutes:

```xml
<node id="tumblr-read" type="com.vaushell.superpipes.nodes.tumblr.N_TB">
    <params>
        <param name="key" value="A46892234irzeir23zer2349234zer23490234" />
        <param name="secret" value="1239zer234ZEReeAZE2azeiiaazeiazeier" />
        <param name="blogname" value="lesliensducode.tumblr.com" />
        <param name="max" value="50" />
        <param name="delay" value="900000" />
    </params>
</node>
```
