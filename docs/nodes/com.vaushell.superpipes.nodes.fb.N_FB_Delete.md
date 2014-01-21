![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_FB_Delete

Full class path : [`com.vaushell.superpipes.nodes.fb.N_FB_Delete`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/fb/N_FB_Delete.java)


## Goal

This node deletes a Facebook message.

To use the node, I create a Facebook application and get my credentials (key and secret).

See [How to create a Facebook application and get credentials](../tutorials/Create_Facebook_Application.md).


## Default parameters

* anti-burst: 500
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
timeout | Socket timeout in milliseconds. How long should I wait before the message is delete ? | long | no | 20000 | 20000
retry | How many times do I retry if the check fails ? | integer | no | 3 | 3
delayBetweenRetry | How long should I wait between 2 checks ? (in milliseconds) | long | no | 5000 | 5000
key | Application Key | string | yes | N/A | 435923492349
secret | Application Secret | string | yes | N/A | 012345679abcdef0123456
pagename | Pagename name to read. This isn't an ID: this is the page name. Can't be used with userid parameter. | string | no | N/A | Les liens du code


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/delete_facebook.png)

I delete all messages of my page 'Les liens du code':

```xml
<node id="facebook-delete" type="com.vaushell.superpipes.nodes.fb.N_FB_Delete">
    <params>
        <param name="key" value="435923492349" />
        <param name="secret" value="012345679abcdef0123456" />
        <param name="pagename" value="Les liens du code" />
    </params>
</node>
```
