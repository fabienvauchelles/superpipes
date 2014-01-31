![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_TB_Post

Full class path : [`com.vaushell.superpipes.nodes.tumblr.N_TB_Post`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/tumblr/N_TB_Post.java)


## Goal

This node posts a message to a Tumblr blog.

To use the node, I create a Tumblr application and get my credentials (key and secret).

See [How to create a Tumblr application and get credentials](../tutorials/Create_Tumblr_Application.md) and [How to link a Tumblr application with SuperPipes](../tutorials/Link_Tumblr_Application.md).


## Default parameters

* anti-burst: 2000 (2 seconds)
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | ---
retry | How many times do I retry if the check fails ? | integer | no | 10 | 10
wait-time | How long should I wait between 2 checks ? (in milliseconds) | long | no | 5000 | 5000
wait-time-multiplier | How multiple I `wait-time` each time ? | double | no | 2.0 | 2.0
jitter-range | Add or substract randomly time to `wait-time` (between 0 and `jitter-range`) | int | no | 500 | 500
max-duration | How long shoud I retry ? (in milliseconds, 0=disabled) | long | no | 0 | 10000
key | Application Key | string | yes | N/A | A46892234irzeir23zer2349234zer23490234
secret | Application Secret | string | yes | N/A | 1239zer234ZEReeAZE2azeiiaazeiazeier
blogname | Name of the blog (such as lesliensducode.tumblr.com) | string | no | N/A | lesliensducode.tumblr.com


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_migrate_tumblr.png)

I post a message from a Tumblr blog to my new Tumblr blog 'lesliensducode.tumblr.com':

```xml
<node id="tumblr-post" type="com.vaushell.superpipes.nodes.tumblr.N_TB_Post">
    <params>
        <param name="key" value="A46892234irzeir23zer2349234zer23490234" />
        <param name="secret" value="1239zer234ZEReeAZE2azeiiaazeiazeier" />
        <param name="blogname" value="lesliensducode.tumblr.com" />
    </params>
</node>
```
