![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_RSS

Full class path : [`com.vaushell.superpipes.nodes.rss.N_RSS`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/rss/N_RSS.java)


## Goal

This node reads a RSS feed.


## Default parameters

* anti-burst: 60000 (10 minutes)
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
url | URI of the RSS feed | string | yes | N/A | http://feeds.feedburner.com/fabienvauchelles
max | Number of messages to read. | integer | yes | N/A | 50

## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_blog_to_fb.png)

I read all messages of my RSS feed 'http://feeds.feedburner.com/fabienvauchelles' every 15 minutes:

```xml
<node id="rss-read" type="com.vaushell.superpipes.nodes.rss.N_RSS">
    <params>
        <param name="url" value="http://feeds.feedburner.com/fabienvauchelles" />
        <param name="delay" value="900000" />
    </params>
</node>
```
