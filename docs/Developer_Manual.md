![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Developer manual

## Table of Contents

* [What technology is used ?](#what-technology-is-used-)
* [How can I get the source code ?](#how-can-i-get-the-source-code-)
* [How do I run the code ?](#how-do-i-run-the-code-)
* [What coding rules should I follow ?](#what-coding-rules-should-i-follow-)
	* [Robustness](#robustness)
	* [Documentation](#documentation)
	* [Unit tests](#unit-tests)
* [How the Maven file tree is built ?](#how-the-maven-file-tree-is-built-)
* [How the source code is organized ?](#how-the-source-code-is-organized-)
* [What helpers are available ?](#what-helpers-are-available-)
	* [OAuth service](#oauth-service)
	* [HTTP operations](#http-operations)
* [How to create a new node ?](#how-to-create-a-new-node-)
* [How to create a new transform ?](#how-to-create-a-new-transform-)


## What technology is used ?

SuperPipes uses [JAVA 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Maven](http://maven.apache.org/).


## How can I get the source code ?

I download the source code from [Github](https://github.com/fabienvauchelles/superpipes):

```sh
$ git clone https://github.com/fabienvauchelles/superpipes
```


## How do I run the code ?

### Step 1

I import the project in my favourite IDE (Netbeans, Eclipse, etc.)


### Step 2

In the main directory, I create a `conf-local/test/configuration.xml` with:

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>

    <mailer>
        <params>
            <param name="anti-burst" value="1000" />
            <param name="host" value="localhost" />
            <param name="port" value="33325" />
            <param name="from" value="superpipes@localhost" />
            <param name="to" value="superpipes@localhost" />
        </params>
    </mailer>

    <commons>

        <common id="bitly">
            <params>
                <param name="username" value="USERNAME" />
                <param name="apikey" value="API_KEY" />
            </params>
        </common>

        <common id="twitter">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
            </params>
        </common>

        <common id="facebook">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
            </params>
        </common>

        <common id="facebookpage">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
                <param name="pagename" value="MY_PAGE_NAME" />
                <!-- I have to create a Facebook page -->
            </params>
        </common>

        <common id="linkedin">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
            </params>
        </common>

        <common id="tumblr">
            <params>
                <param name="key" value="APP_KEY" />
                <param name="secret" value="APP_SECRET" />
                <param name="blogname" value="MY_BLOG.tumblr.com" />
            </params>
        </common>

    </commons>

</configuration>
```

This configuration file is required to run the unit tests.

### Step 3

I create a conf-local/configuration.xml with my favourite conf! (see examples).

### Step 4

In my IDE, I run the project with these parameters:

```
Main class: com.vaushell.superpipes.App
Arguments: conf-local/configuration.xml conf-local/datas 86400000
```

The arguments are:
```
<configuration file> <nodes's data directory> <time to live in milliseconds>
```

## What coding rules should I follow ?

### Robustness

Superpipes project uses:

* [Checkstyle](http://checkstyle.sourceforge.net/) and [PMD](http://pmd.sourceforge.net/) to control the code;
* [Findbugs](http://findbugs.sourceforge.net/) to find bugs.


Code must comply with the rules before submitted.

Most basic errors are found by plugins.


### Documentation

Why do I document my code ?

1. To understand it later;
2. So that others understand.

Javadoc should be provided on these elements:

* public classes;
* public methods;
* arguments of public methods;
* return of public methods.

Otherwise, checkstyle will scream.


### Unit tests

All tools and complex nodes implement unit testing with [TestNG](http://testng.org/).

External libraries aren't being tested (I assume that this is already done).

Here are some examples:

* for [nodes](../superpipes/src/test/java/com/vaushell/superpipes/nodes/twitter/N_TW_PostTest.java)
* for [transforms](../superpipes/src/test/java/com/vaushell/superpipes/transforms/bitly/T_ShortenExpandTest.java)
* for [scribe service](../superpipes/src/test/java/com/vaushell/superpipes/tools/scribe/fb/FacebookClientTest.java) (external oauth service)
* for [helper](../superpipes/src/test/java/com/vaushell/superpipes/tools/ValuesGeneratorTest.java)


## How the Maven file tree is built ?

Here are the details of the Maven file tree:

```
.
|-docs            : Documentation
|-superpipes      : Java project
  |-conf          : Official configuration
  |-conf-local    : Local configuration (ignored by Git)
  |-pom.xml       : Maven configuration file (build, deploy, etc.)
  |-src           : Source code
    |-assemble    : Prepare the Debian package
    |-deb         : Debian package content
    |-main        : Source code
      |-java      : JAVA source code
      |-resources : Resources for JAVA source code (such as logging configuration)
    |-proguard    : Bytecode optimization & obfuscation
    |-test        : Test code
      |-java      : JAVA test code
      |-resources : Resources for JAVA test code
      |-webapp    : Webapp content for Jersey (unit testing purpose only)
```


## How the source code is organized ?

The source code is organized as follows:

[__`com.vaushell.superpipes`__](../superpipes/src/main/java/com/vaushell/superpipes) is the main package. It contains:

* [`App.java`](../superpipes/src/main/java/com/vaushell/superpipes/App.java): the command line start
* [`DaemonApp.java`](../superpipes/src/main/java/com/vaushell/superpipes/DaemonApp.java): the daemon start

[__`com.vaushell.superpipes.dispatch`__](../superpipes/src/main/java/com/vaushell/superpipes/dispatch) contains the logic and messaging stuff.

[__`com.vaushell.superpipes.nodes`__](../superpipes/src/main/java/com/vaushell/superpipes/nodes) contains all nodes.

[__`com.vaushell.superpipes.transforms`__](../superpipes/src/main/java/com/vaushell/superpipes/transforms) contains all transforms.

[__`com.vaushell.superpipes.tools`__](../superpipes/src/main/java/com/vaushell/superpipes/tools) contains all helpers (tools).


## What helpers are available ?

### OAuth service

OAuth services (Facebook, Twitter, LinkedIn, Tumblr) extend the [OAuthClient](../superpipes/src/main/java/com/vaushell/superpipes/tools/scribe/OAuthClient.java) class.

The use the [Scribe library](https://github.com/fernandezpablo85/scribe-java).

If you want to add a service, I suggest you look at how the Tumblr library is made:

* [TumblrClient](../superpipes/src/main/java/com/vaushell/superpipes/tools/scribe/tumblr/TumblrClient.java) : Tumblr client;
* [TumblrException](../superpipes/src/main/java/com/vaushell/superpipes/tools/scribe/tumblr/TumblrException.java) : exceptions on error;
* [TB_Post](../superpipes/src/main/java/com/vaushell/superpipes/tools/scribe/tumblr/TB_Post.java) et [TB_Blog](../superpipes/src/main/java/com/vaushell/superpipes/tools/scribe/tumblr/TB_Blog.java) : data model;
* [N_TB](../superpipes/src/main/java/com/vaushell/superpipes/nodes/tumblr/N_TB.java) : node that uses TumblrClient to read messages from a blog;
* [TumblrClientTest](../superpipes/src/test/java/com/vaushell/superpipes/tools/scribe/tumblr/TumblrClientTest.java) : unit testing.


### HTTP operations

All HTTP operations use the [HTTPhelper](../superpipes/src/main/java/com/vaushell/superpipes/tools/HTTPhelper.java) class.

It standardizes: 

* the creation of an Apache HttpClient client;
* the images uploading;
* the detection of redirects;
* etc.


## How to create a new node ?

A node is executed in a thread.

I can do what I want:

* run a task,
* wait for a message,
* wait 2000ms,
* etc.

A node extends the [`A_Node`](../superpipes/src/main/java/com/vaushell/superpipes/nodes/A_Node.java) class.

To understand how it's work, I look at:

* [N_Dummy](../superpipes/src/main/java/com/vaushell/superpipes/nodes/dummy/N_Dummy.java) for an empty node;
* [N_Logger](../superpipes/src/main/java/com/vaushell/superpipes/nodes/stub/N_MessageLogger.java) for a very simple consumer node;
* [N_FB](../superpipes/src/main/java/com/vaushell/superpipes/nodes/fb/N_FB.java) for a producer node;
* [N_FB_Post](../superpipes/src/main/java/com/vaushell/superpipes/nodes/fb/N_FB_Post.java) for a consumer node;
* [N_Buffer](../superpipes/src/main/java/com/vaushell/superpipes/nodes/buffer/N_Buffer.java) for a complex node.


## How to create a new transform ?

A transform is executed:

* before receiving a node;
* after sending a message.

__Warning!__ A transform blocks the execution of the thread!

The goal is to provide simple process.

A transform extends the [`A_Transform`]../superpipes/src/main/java/com/vaushell/superpipes/transforms/A_Transform.java) class.

To understand how it's work, I look at:

* [T_Date](../superpipes/src/main/java/com/vaushell/superpipes/transforms/date/T_Date.java) for a simple transform;
* [T_FindBiggest](../superpipes/src/main/java/com/vaushell/superpipes/transforms/image/T_FindBiggest.java) for a complex transform.
