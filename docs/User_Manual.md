![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)

# User manual

## Table of Contents

<a href="#which-nodes-can-i-use-"><img alt="NodeList" src="https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/btnodeslist.png" /></a> <a href="#which-transforms-can-i-use-"><img alt="NodeList" src="https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/bttransformslist.png" /></a>

* [How SuperPipes works ?](#how-superpipes-works-)
* [How are the files organized ?](#how-are-the-files-organized-)
* [How is the configuration file built ?](#how-is-the-configuration-file-built-)
	* [Section 1: Mailer](#section-1-mailer)
	* [Section 2: Commons](#section-2-commons)
	* [Section 3: Nodes](#section-3-nodes)
	* [Section 4: Routes](#section-4-routes)
* [Which nodes can I use ?](#which-nodes-can-i-use-)
* [Which transforms can I use ?](#which-transforms-can-i-use-)
* [Examples](#examples)
	* [Post a message of my blog on Facebook and Twitter](#post-a-message-of-my-blog-on-facebook-and-twitter)
	* [Like all messages after the 20/01/2014, from a Facebook page, with my own account](#like-all-messages-after-the-20012014-from-a-facebook-page-with-my-own-account)
	* [Remove all tweets from a Twitter account](#remove-all-tweets-from-a-twitter-account)
	* [Remove all messages from a Facebook page](#remove-all-messages-from-a-facebook-page)
	* [Migrate a Shaarli to another Shaarli](#migrate-a-shaarli-to-another-shaarli)
* [How to go further ?](#how-to-go-further-)


## How SuperPipes works ?

SuperPipes is a __flow graph__. Edges are the routes.

Each nodes work independently.

When a message is sent to a node, it is pre-processed. A pre-processor modify or discard a message.

If the node wants, it will read the message. Or it will create many others. A node can create without receiving a message!

When a message is sent by a node, it is post-processed (same mechanism as the pre-processor).

In addition, a node has a process that is repeated indefinitely.

Here is a diagram example:

![Diagram example](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/flow_example.png)


## How are the files organized ?

SuperPipes is located in `/usr/local/superpipes` (on Debian) :

```
/
|-etc
  |-init.d
    |-superpipes             : Startup script
|-usr
  |-local
    |-superpipes             : Main installation directory
      |-conf
        |-configuration.xml  : Configuration file
      |-datas                : Stored datas for nodes
      |-lib                  : JAVA libraries
      |-log
        |-error              : Error log
        |-info               : Info log
      |-superpipes.jar       : Main program
      |-superpipes.sh        : Command line startup script
```


## How is the configuration file built ?

Superpipes uses an XML configuration file: `/usr/local/superpipes/conf/configuration.xml`

The file contains 4 sections:

![Sections](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/xml-sections.png)

__Warning !__ The order of sections should be: mailer, commons, nodes and routes.


### Section 1: Mailer

When a message triggers an error, I want to be notified by email!

So, I provide my SMTP server:

```xml
<mailer>
    <params>
        <param name="anti-burst" value="1000" />
        <param name="host" value="localhost" />
        <param name="port" value="25" />
        <param name="from" value="superpipes@localhost" />
        <param name="to" value="superpipes@localhost" />
    </params>
</mailer>
```

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
host | SMTP host | string | yes | N/A | smtp.myownserver.com
port | SMTP port | integer | no | 25 | 25
from | Email sender | string | yes | N/A | superpipes@localhost
to | Email receiver | string | yes | N/A | superpipes@localhost
anti-burst | Wait for X milliseconds before sending another message | long | no | 1000 | 1000


### Section 2: Commons

Commons are reusable parameters.

If I have 3 nodes for 1 Facebook account, I write my credentials a single time.

So, I write a common section:

```xml
<commons>
    <common id="facebook">
        <params>
            <param name="key" value="APP_KEY" />
            <param name="secret" value="APP_SECRET" />
        </params>
    </common>
</commons>
```

### Section 3: Nodes

I describe a node in this section with:

* an ID: unique identifier of the node
* a type: the class path (like `com.vaushell.superpipes.nodes.fb.N_FB`)
* a list of parameters
* a list of in-transforms: message pre-processors
* a list of out-transforms: message post-processors
* and specific configuration, if required by the type.


#### Standard node example

```xml
<node id="facebook-read" type="com.vaushell.superpipes.nodes.fb.N_FB" commons="facebook" >
    <params>
        <param name="max" value="50" />
    </params>

    <in>
        <!-- Nothing to pre-process here -->
    </in>

    <out>
        <transform type="com.vaushell.superpipes.transforms.done.T_Done" />
        <transform type="com.vaushell.superpipes.transforms.bitly.T_Shorten" commons="bitly" />
    </out>
</node>
```

__What can I say about this node ?__

I create a node, labelled by `facebook-read`, with a type of `com.vaushell.superpipes.nodes.fb.N_FB`.

It reads a Facebook wall (or a page). Credentials are defined by a `key` parameter and a `secret` parameter, located in the common `facebook`.

It reads up to 50 messages.

For each:

* I discard messages already read (transform `com.vaushell.superpipes.transforms.done.T_Done`).
* I convert the URI with Bitly service (transform `com.vaushell.superpipes.transforms.bitly.T_Shorten`), with credentials located in the common `bitly`.


#### Specific node example

Nodes can define a specific structure:

```xml
<node id="buffer" type="com.vaushell.superpipes.nodes.buffer.N_Buffer">
    <params>
        <param name="flow-limit" value="3600000" />
    </params>

    <slots>
        <slot days="MON,TUE,WED,THU,FRI" startat="12:00:00" endat="14:00:00" />
    </slots>

    <in>
        <transform type="com.vaushell.superpipes.transforms.tags.T_Tags">
            <params>
                <param name="type" value="INCLUDE_ONE" />
                <param name="tags" value="cooking" />
            </params>
        </transform>
    </in>
</node>
```

__What can I say about this node ?__

I create a node labelled by `buffer` with a type of `com.vaushell.superpipes.nodes.buffer.N_Buffer`.

It sends 1 message every 3600000ms (every hour).

It sends messages only week (MON,TUE,WED,THU,FRI), for lunch (12h00-14h00). Otherwise, messages are stacked.

The node accepts a message only if it contains the tag `cooking`.


### Section 4: Routes

The routes are the edges of the graph. This is a simple list of source-destination:

```xml
<routes>
    <route source="facebook-read" destination="buffer" />
    <route source="buffer" destination="logger" />
</routes>
```


## Which nodes can I use ?

Nodes have common parameters and individual parameters.

The common parameters are:

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
anti-burst | Wait for X milliseconds before processing another incoming message | long | no | depends on the node | 60000
delay | Each node has a process that repeats indefinitly. Wait for X milliseconds between repetitions | long | no | depends on the node | 60000

I can add these nodes:

<table>

<tr>
<th>Class path</th>
<th>Description</th>
</tr>

<tr>
<td><img alt="star" src="https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/star24.png" /> <a href="nodes/com.vaushell.superpipes.nodes.buffer.N_Buffer.md">com.vaushell.superpipes.nodes.buffer.N_Buffer</a></td>
<td>
Delay the message:
<ul>
<li>only during certain time slots;</li>
<li>add a minimum time between 2 messages;</li>
<li>add a fixed or random time before processing a message.</li>
</ul>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.dummy.N_Dummy.md">com.vaushell.superpipes.nodes.dummy.N_Dummy</a></td>
<td>Do nothing except to transfer the message. This is useful for combining with transforms.</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.fb.N_FB.md">com.vaushell.superpipes.nodes.fb.N_FB</a></td>
<td>Read a Facebook wall or a Facebook page</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.fb.N_FB_Delete.md">com.vaushell.superpipes.nodes.fb.N_FB_Delete</a></td>
<td>Delete a Facebook message</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.fb.N_FB_Post.md">com.vaushell.superpipes.nodes.fb.N_FB_Post</a></td>
<td>Post a message on a Facebook wall or page</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.fb.N_FB_PostLike.md">com.vaushell.superpipes.nodes.fb.N_FB_PostLike</a></td>
<td>Like a Facebook message</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.linkedin.N_LNK.md">com.vaushell.superpipes.nodes.linkedin.N_LNK</a></td>
<td>Read the status updates from a LinkedIn profile</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.linkedin.N_LNK_Post.md">com.vaushell.superpipes.nodes.linkedin.N_LNK_Post</a></td>
<td>Post a status update to a LinkedIn profile</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.rss.N_RSS.md">com.vaushell.superpipes.nodes.rss.N_RSS</a></td>
<td>Read a RSS feed</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.shaarli.N_Shaarli.md">com.vaushell.superpipes.nodes.shaarli.N_Shaarli</a></td>
<td>Read a Shaarli feed</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.shaarli.N_Shaarli_Post.md">com.vaushell.superpipes.nodes.shaarli.N_Shaarli_Post</a></td>
<td>Post a link to Shaarli</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.stub.N_MessageLogger.md">com.vaushell.superpipes.nodes.stub.N_MessageLogger</a></td>
<td>Show incoming message</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.tumblr.N_TB.md">com.vaushell.superpipes.nodes.tumblr.N_TB</a></td>
<td>Read a Tumblr blog</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.tumblr.N_TB_Delete.md">com.vaushell.superpipes.nodes.tumblr.N_TB_Delete</a></td>
<td>Delete a message from a Tumblr blog</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.tumblr.N_TB_Post.md">com.vaushell.superpipes.nodes.tumblr.N_TB_Post</a></td>
<td>Post a message to a Tumblr blog</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.twitter.N_TW.md">com.vaushell.superpipes.nodes.twitter.N_TW</a></td>
<td>Read a Twitter wall</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.twitter.N_TW_Delete.md">com.vaushell.superpipes.nodes.twitter.N_TW_Delete</a></td>
<td>Delete a tweet</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.twitter.N_TW_Post.md">com.vaushell.superpipes.nodes.twitter.N_TW_Post</a></td>
<td>Post a tweet</td>
</tr>

<tr>
<td><a href="nodes/com.vaushell.superpipes.nodes.twitter.N_TW_Retweet.md">com.vaushell.superpipes.nodes.twitter.N_TW_Retweet</a></td>
<td>Retweet a tweet</td>
</tr>

</table>

![Star](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/star24.png) very useful :)


## Which transforms can I use ?

I can add these transforms:

<table>

<tr>
<th>Class path</th>
<th>Description</th>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.bitly.T_Expand.md">com.vaushell.superpipes.transforms.bitly.T_Expand</a></td>
<td>Expand the URI of a message with Bitly service</td>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.bitly.T_Shorten.md">com.vaushell.superpipes.transforms.bitly.T_Shorten</a></td>
<td>Shorten the URI of a message with Bitly service</td>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.date.T_Date.md">com.vaushell.superpipes.transforms.date.T_Date</a></td>
<td>Keep message only if the date is later and/or prior a specific date</td>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.done.T_Done.md">com.vaushell.superpipes.transforms.done.T_Done</a></td>
<td>Message can't pass more than once</td>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.image.T_FindBiggest.md">com.vaushell.superpipes.transforms.image.T_FindBiggest</a></td>
<td>Find the largest image (pixel size) of the URI of the message and download it</td>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.rnd.T_RandomLinear.md">com.vaushell.superpipes.transforms.rnd.T_RandomLinear</a></td>
<td>Keep only X percent of messages</td>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.tags.T_Tags.md">com.vaushell.superpipes.transforms.tags.T_Tags</a></td>
<td>Keep message only if tags contain a set of tags</td>
</tr>

<tr>
<td><a href="transforms/com.vaushell.superpipes.transforms.uri.T_CheckURI.md">com.vaushell.superpipes.transforms.uri.T_CheckURI</a></td>
<td>Discard message with an inaccessible URI</td>
</tr>

</table>


## Examples

### Post a message of my blog on Facebook and Twitter

![Schema](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_blog_to_twitter_fb.png)

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>

    <mailer>
        <params>
            <param name="anti-burst" value="1000" />
            <param name="host" value="localhost" />
            <param name="port" value="25" />
            <param name="from" value="superpipes@localhost" />
            <param name="to" value="superpipes@localhost" />
        </params>
    </mailer>

    <nodes>
        <node id="rss-read" type="com.vaushell.superpipes.nodes.rss.N_RSS">
            <params>
                <param name="url" value="http://feeds.feedburner.com/fabienvauchelles"/>

                <!-- Read 30 news every 15 minutes -->
                <param name="max" value="30" />
                <param name="delay" value="900000" />
            </params>

            <out>
                <transform type="com.vaushell.superpipes.transforms.date.T_Date">
                  <params>
                      <param name="date-min" value="20/01/2014 00:00:00" />
                  </params>
                </transform>
                <transform type="com.vaushell.superpipes.transforms.done.T_Done" />
            </out>
        </node>

        <node id="buffer" type="com.vaushell.superpipes.nodes.buffer.N_Buffer">
            <params>
                <param name="flow-limit" value="3600000">
                    <!-- 1 news maximum each hour -->
                </param>
            </params>

            <slots>
                <!-- Only week, between 12:00 and 14:00 -->
                <slot days="MON,TUE,WED,THU,FRI" startat="12:00:00" endat="14:00:00" />
            </slots>
        </node>

        <node id="facebook-post" type="com.vaushell.superpipes.nodes.fb.N_FB_Post">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
            </params>
        </node>

        <node id="twitter-post" type="com.vaushell.superpipes.nodes.twitter.N_TW_Post">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
            </params>
        </node>
    </nodes>

    <routes>
        <route source="rss-read" destination="buffer" />
        <route source="buffer" destination="facebook-post" />
        <route source="buffer" destination="twitter-post" />
    </routes>

</configuration>
```


### Like all messages after the 20/01/2014, from a Facebook page, with my own account

![Schema](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_facebook_like.png)

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>

    <mailer>
        <params>
            <param name="anti-burst" value="1000" />
            <param name="host" value="localhost" />
            <param name="port" value="25" />
            <param name="from" value="superpipes@localhost" />
            <param name="to" value="superpipes@localhost" />
        </params>
    </mailer>

    <commons>
        <common id="facebook">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
            </params>
        </common>
    </commons>

    <nodes>
        <node id="facebook-read" type="com.vaushell.superpipes.nodes.fb.N_FB" commons="facebook">
            <params>
                <param name="userid" value="FACEBOOK_USER_ID"/>

                <!-- Read 30 news every 15 minutes -->
                <param name="max" value="30" />
                <param name="delay" value="900000" />
            </params>

            <out>
                <transform type="com.vaushell.superpipes.transforms.date.T_Date">
                  <params>
                      <param name="date-min" value="20/01/2014 00:00:00" />
                  </params>
                </transform>
                <transform type="com.vaushell.superpipes.transforms.done.T_Done" />
            </out>
        </node>

        <node id="buffer" type="com.vaushell.superpipes.nodes.buffer.N_Buffer">
            <params>
                <param name="flow-limit" value="300000">
                    <!-- 1 news maximum each 5 min -->
                </param>

                <!-- Wait between 10 min and 4 hours -->
                <param name="wait-min" value="600000" />
                <param name="wait-ax" value="14400000" />
            </params>

            <slots>
                <!-- every day between 10:00 and 22:00 -->
                <slot days="MON,TUE,WED,THU,FRI,SAT,SUN" startat="10:00:00" endat="22:00:00" />
            </slots>
        </node>

        <node id="facebook-like" type="com.vaushell.superpipes.nodes.fb.N_FB_PostLike" commons="facebook" />
    </nodes>

    <routes>
        <route source="facebook-read" destination="buffer" />
        <route source="buffer" destination="facebook-like" />
    </routes>

</configuration>
```


### Remove all tweets from a Twitter account

![Schema](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/delete_twitter.png)

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>

    <mailer>
        <params>
            <param name="anti-burst" value="1000" />
            <param name="host" value="localhost" />
            <param name="port" value="25" />
            <param name="from" value="superpipes@localhost" />
            <param name="to" value="superpipes@localhost" />
        </params>
    </mailer>

    <commons>
        <common id="twitter">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
            </params>
        </common>
    </commons>

    <nodes>
        <node id="twitter-read" type="com.vaushell.superpipes.nodes.twitter.N_TW" commons="twitter">
            <params>
                <param name="delay" value="900000" />
                <param name="max" value="15" />
            </params>
        </node>
        <node id="twitter-delete" type="com.vaushell.superpipes.nodes.twitter.N_TW_Delete" commons="twitter" />
    </nodes>

    <routes>
        <route source="twitter-read" destination="twitter-delete" />
    </routes>

</configuration>
```


### Remove all messages from a Facebook page

![Schema](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/delete_facebook.png)

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>

    <mailer>
        <params>
            <param name="anti-burst" value="1000" />
            <param name="host" value="localhost" />
            <param name="port" value="25" />
            <param name="from" value="superpipes@localhost" />
            <param name="to" value="superpipes@localhost" />
        </params>
    </mailer>

    <commons>
        <common id="facebook">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
                <param name="pagename" value="MY PAGE NAME" />
            </params>
        </common>
    </commons>

    <nodes>
        <node id="facebook-read" type="com.vaushell.superpipes.nodes.fb.N_FB" commons="facebook">
            <params>
                <param name="delay" value="900000" />
                <param name="max" value="15" />
            </params>
        </node>
        <node id="facebook-delete" type="com.vaushell.superpipes.nodes.fb.N_FB_Delete" commons="facebook" />
    </nodes>

    <routes>
        <route source="facebook-read" destination="facebook-delete" />
    </routes>

</configuration>
```


### Migrate a Shaarli to another Shaarli

![Schema](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/example_migrate_shaarli.png)

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>

    <mailer>
        <params>
            <param name="anti-burst" value="1000" />
            <param name="host" value="localhost" />
            <param name="port" value="25" />
            <param name="from" value="superpipes@localhost" />
            <param name="to" value="superpipes@localhost" />
        </params>
    </mailer>

    <nodes>
        <node id="shaarli-source" type="com.vaushell.superpipes.nodes.shaarli.N_Shaarli">
            <params>
                <param name="url" value="http://lesliensducode.com" />
                <param name="delay" value="100000000">
                    <!-- With a very long time, I consider a single execution -->
                </param>
                <param name="max" value="1000000">
                    <!-- Or more :) -->
                </param>
            </params>
        </node>
 
        <node id="shaarli-destination" type="com.vaushell.superpipes.nodes.shaarli.N_Shaarli_Post">
            <params>
                <param name="url" value="http://mynewshaarli.com" />
                <param name="login" value="LOGIN" />
                <param name="password" value="PASSWORD" />
            </params>
        </node>
    </nodes>

    <routes>
        <route source="shaarli-source" destination="shaarli-destination" />
    </routes>

</configuration>
```


__More examples to come!__


## How to go further ?

See [Developer Manual](Developer_Manual.md).