![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_FB

Full class path : [`com.vaushell.superpipes.nodes.fb.N_FB`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/fb/N_FB.java)


## Goal

This node reads messages from a Facebook wall or a Facebook page.

To use the node, I create a Facebook application and get my credentials (key and secret).

See [How to create a Facebook application and get credentials](../tutorials/Create_Facebook_Application.md) and [How to link a Facebook application with SuperPipes](../tutorials/Link_Facebook_Application.md).


## Default parameters

* anti-burst: 60000 (10 minutes: I don't want to be blacklisted!)
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
key | Application Key | string | yes | N/A | 435923492349
secret | Application Secret | string | yes | N/A | 012345679abcdef0123456
userid | Force a user ID. I read the wall of a friend. To find the ID, I go to [FindMyFaceboookID](http://findmyfacebookid.com/). Can't be used with `pagename`  parameter. | string | no | N/A | 12323324234
pagename | Pagename name to read. This isn't an ID: this is the page name. Can't be used with `userid` parameter. | string | no | N/A | Les liens du code
max | Number of messages to read. | integer | yes | N/A | 50


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/buffer_example.png)

I read 50 messages of my page 'Les liens du code' every 15 minutes:

```xml
<node id="facebook-read" type="com.vaushell.superpipes.nodes.fb.N_FB">
    <params>
        <param name="key" value="435923492349" />
        <param name="secret" value="012345679abcdef0123456" />
        <param name="pagename" value="Les liens du code" />
        <param name="max" value="50" />
        <param name="delay" value="900000" />
    </params>
</node>
```
