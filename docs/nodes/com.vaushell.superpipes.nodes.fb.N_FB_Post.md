![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_FB_Post

Full class path : [`com.vaushell.superpipes.nodes.fb.N_FB_Post`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/fb/N_FB_Post.java)


## Goal

This node posts a message on a Facebook wall or page.

To use the node, I create a Facebook application and get my credentials (key and secret).

See [How to create a Facebook application and get credentials](../tutorials/Create_Facebook_Application.md).


## Default parameters

* anti-burst: 60000 (10 minutes: I don't want to be blacklisted!)
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
timeout | Socket timeout in milliseconds. How long should I wait before the message is delivered ? | long | no | 20000 | 20000
retry | How many times do I retry if the check fails ? | integer | no | 3 | 3
delayBetweenRetry | How long should I wait between 2 checks ? (in milliseconds) | long | no | 5000 | 5000
key | Application Key | string | yes | N/A | 435923492349
secret | Application Secret | string | yes | N/A | 012345679abcdef0123456
userid | Force a user ID. I read the wall of a friend. To find the ID, I go to [FindMyFaceboookID](http://findmyfacebookid.com/). Can't be used with `pagename`  parameter. | string | no | N/A | 12323324234
pagename | Pagename name to read. This isn't an ID: this is the page name. Can't be used with `userid` parameter. | string | no | N/A | Les liens du code


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_blog_to_fb.png)

I post a message from my blog to my Facebook wall:

```xml
<node id="facebook-post" type="com.vaushell.superpipes.nodes.fb.N_FB_Post">
    <params>
        <param name="key" value="435923492349" />
        <param name="secret" value="012345679abcdef0123456" />
    </params>
</node>
```
