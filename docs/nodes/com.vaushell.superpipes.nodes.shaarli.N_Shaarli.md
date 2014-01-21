![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_Shaarli

Full class path : [`com.vaushell.superpipes.nodes.shaarli.N_Shaarli`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/shaarli/N_Shaarli.java)


## Goal

This node reads a Shaarli feed.

It uses [Shaari JAVA API](https://github.com/fabienvauchelles/shaarli-java-api).


## Default parameters

* anti-burst: 60000 (10 minutes: I don't want to be blacklisted!)
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | ---
url | URI of the RSS feed | string | yes | N/A | http://feeds.feedburner.com/lesliensducode
max | Number of messages to read | integer | yes | N/A | 50
reverse | Starting with the oldest message (true) or the most recent (false) | boolean | no | false | true


## Template parameters

If the site template changes, I specify to the API how to find essential information.

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | ---
key | See the documentation Shaari JAVA API | string | yes | N/A | links-count
csspath | See the documentation Shaari JAVA API | string | yes | (empty string) | form[name=searchform] input[class=medium]
attribut | See the documentation Shaari JAVA API | string | yes | (empty string) | placeholder
regex | See the documentation Shaari JAVA API | regular expression | yes | (empty string) | \\d+


## Use example

![Example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_migrate_shaarli.png)

I read 50 links of my Shaarli feed 'http://feeds.feedburner.com/lesliensducode' every 15 minutes:

```xml
<node id="shaarli-read" type="com.vaushell.superpipes.nodes.rss.N_RSS">
    <params>
        <param name="url" value="http://feeds.feedburner.com/lesliensducode" />
        <param name="max" value="50" />
        <param name="delay" value="900000" />
    </params>
    <templates>
        <template key="id-dateformat" csspath="yyyyMMdd_HHmmss" attribut="" regex="" />
        <template key="permalink-dateformat" csspath="EEE MMM dd HH:mm:ss yyyy" attribut="" regex="" />
        <template key="cloudtag" csspath="#cloudtag span" attribut="" regex="" />
        <template key="cloudtag-name" csspath="" attribut="" regex="" />
        <template key="cloudtag-count" csspath="" attribut="" regex="\\d+" />
        <template key="links-count" csspath="form[name=searchform] input[class=medium]" attribut="placeholder" regex="\\d+" />
        <template key="token" csspath="input[name=token]" attribut="value" regex="" />
        <template key="links" csspath="table.article" attribut="" regex="" />
        <template key="links-private" csspath="td[class=private]" attribut="" regex="" />
        <template key="links-id" csspath="td[class=id]" attribut="" regex="" />
        <template key="links-permalink-id" csspath="a[class=permalink]" attribut="id" regex="" />
        <template key="links-title" csspath="a[class=title]" attribut="" regex="" />
        <template key="links-description" csspath="span[class=description]" attribut="" regex="" />
        <template key="links-url" csspath="a[class=title]" attribut="href" regex="" />
        <template key="tags" csspath="tr[class=tag] a" attribut="" regex="" />
        <template key="tags-tag" csspath="" attribut="" regex="" />
        <template key="page-max" csspath="#footer-nav td.size20" attribut="" regex="(\\d+)$" />
    </templates>
</node>
```
