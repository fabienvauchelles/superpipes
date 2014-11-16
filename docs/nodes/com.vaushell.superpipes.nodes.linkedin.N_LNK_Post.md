![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_LNK_Post

Full class path : [`com.vaushell.superpipes.nodes.linkedin.N_LNK_Post`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/linkedin/N_LNK_Post.java)


## Goal

This node posts a status update to a LinkedIn profile.

__Warning!__ LinkedIn API disallows to delete a status.

To use the node, I create a LinkedIn application and get my credentials (key and secret).

See [How to create a LinkedIn application and get credentials](../tutorials/Create_LinkedIn_Application.md) and [How to link a LinkedIn application with SuperPipes](../tutorials/Link_LinkedIn_Application.md).


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
key | Application Key | string | yes | N/A | 435923492349
secret | Application Secret | string | yes | N/A | 012345679abcdef0123456

## Use example

![Example](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/example_blog_to_linked.png)

I post a message from my blog to my LinkedIn profile:

```xml
<node id="linkedin-post" type="com.vaushell.superpipes.nodes.linkedin.N_LNK_Post">
    <params>
        <param name="key" value="435923492349" />
        <param name="secret" value="012345679abcdef0123456" />
    </params>
</node>
```
