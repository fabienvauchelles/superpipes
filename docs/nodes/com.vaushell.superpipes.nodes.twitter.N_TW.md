![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_TW

Full class path : [`com.vaushell.superpipes.nodes.twitter.N_TW`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/twitter/N_TW.java)


## Goal

This node reads a Twitter wall.

To use the node, I create a Twitter application and get my credentials (key and secret).

See [How to create a Twitter application and get credentials](../tutorials/Create_Twitter_Application.md) and [How to link a Twitter application with SuperPipes](../tutorials/Link_Twitter_Application.md).


## Default parameters

* anti-burst: 60000 (10 minutes) => I don't want to be blacklisted!
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | ---
key | Application Key | string | yes | N/A | 32zerisdfkzeri456qadk123
secret | Application Secret | string | yes | N/A | lqsdlERTO345zerlsdf3459zsflzer2349Sdflzer9234
userid | Force a user ID. I read the wall of a friend. To find the ID, I go to http://gettwitterid.com/ | string | no | N/A | 9123456
max | Number of tweets to read | integer | yes | N/A | 50


## Use example

![Example](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/example_migrate_twitter.png)

I read 50 tweets of my wall every 15 minutes:

```xml
<node id="twitter-read" type="com.vaushell.superpipes.nodes.twitter.N_TW">
    <params>
        <param name="key" value="32zerisdfkzeri456qadk123" />
        <param name="secret" value="lqsdlERTO345zerlsdf3459zsflzer2349Sdflzer9234" />
        <param name="max" value="50" />
        <param name="delay" value="900000" />
    </params>
</node>
```
