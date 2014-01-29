![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_TW_Delete

Full class path : [`com.vaushell.superpipes.nodes.twitter.N_TW_Delete`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/twitter/N_TW_Delete.java)


## Goal

This node deletes a tweet.

To use the node, I create a Twitter application and get my credentials (key and secret).

See [How to create a Twitter application and get credentials](../tutorials/Create_Twitter_Application.md) and [How to link a Twitter application with SuperPipes](../tutorials/Link_Twitter_Application.md).


## Default parameters

* anti-burst: 60000 (10 minutes) => I don't want to be blacklisted!
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | ---
timeout | Socket timeout in milliseconds. How long should I wait before the tweet is deleted ? | long | no | 20000 | 20000
retry | How many times do I retry if the check fails ? | integer | no | 10 | 10
wait-time | How long should I wait between 2 checks ? (in milliseconds) | long | no | 5000 | 5000
wait-time-multiplier | How multiple I `wait-time` each time ? | double | no | 2.0 | 2.0
jitter-range | Add or substract randomly time to `wait-time` (between 0 and `jitter-range`) | int | no | 500 | 500
max-duration | How long shoud I retry ? (in milliseconds, 0=disabled) | long | no | 0 | 10000
key | Application Key | string | yes | N/A | 32zerisdfkzeri456qadk123
secret | Application Secret | string | yes | N/A | lqsdlERTO345zerlsdf3459zsflzer2349Sdflzer9234


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/delete_twitter.png)

I delete all tweets of my wall:

```xml
<node id="twitter-delete" type="com.vaushell.superpipes.nodes.twitter.N_TW_Delete">
    <params>
        <param name="key" value="32zerisdfkzeri456qadk123" />
        <param name="secret" value="lqsdlERTO345zerlsdf3459zsflzer2349Sdflzer9234" />
    </params>
</node>
```
