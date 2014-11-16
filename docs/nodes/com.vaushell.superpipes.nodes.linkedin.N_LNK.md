![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_LNK

Full class path : [`com.vaushell.superpipes.nodes.linkedin.N_LNK`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/linkedin/N_LNK.java)


## Goal

This node reads the status updates from a LinkedIn profile.

To use the node, I create a LinkedIn application and get my credentials (key and secret).

See [How to create a LinkedIn application and get credentials](../tutorials/Create_LinkedIn_Application.md) and [How to link a LinkedIn application with SuperPipes](../tutorials/Link_LinkedIn_Application.md).


## Default parameters

* anti-burst: 60000 (10 minutes: I don't want to be blacklisted!)
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
key | Application Key | string | yes | N/A | 91238oazeidd5
secret | Application Secret | string | yes | N/A | wlzerksqdfk0QQQ
userid | Force a user ID. I read the status of a friend. To find the ID, I use the tips at http://www.anduro.com/blog/2013/02/whats-my-linkedin-user-id-and-start-date.html | string | no | N/A | 69391283
max | Number of messages to read | integer | yes | N/A | 50


## Use example

![Example](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/example_linkedin_read.png)

I read 50 status of my profile every 15 minutes:

```xml
<node id="linkedin-read" type="com.vaushell.superpipes.nodes.linkedin.N_LNK">
    <params>
        <param name="key" value="91238oazeidd5" />
        <param name="secret" value="wlzerksqdfk0QQQ" />
        <param name="max" value="50" />
        <param name="delay" value="900000" />
    </params>
</node>
```
