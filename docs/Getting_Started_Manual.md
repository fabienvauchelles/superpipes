![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)

# Getting started manual

## Debian

### How to install SuperPipes ?

#### Step 1: Install JAVA 7

A JAVA JRE 7 is a requirement.

I could find more info [here](https://help.ubuntu.com/community/Java).


#### Step 2: Install JSVC

The package is available with apt-get:

```sh
$ sudo apt-get install jsvc
```

JSVC is used to daemonize SuperPipes (more info [here](http://commons.apache.org/proper/commons-daemon/jsvc.html)).


#### Step 3: Download and install the Debian package

The latest package is available [here](http://fabien.vauchelles.com/superpipes/1.0.2/superpipes_1.0.2_all.deb).

```sh
$ wget http://fabien.vauchelles.com/superpipes/1.0.2/superpipes_1.0.2_all.deb
$ sudo dpkg -i superpipes_1.0.2_all.deb
```

### How to use SuperPipes ?

#### Step 1: Create a flow

I edit `/usr/local/superpipes/conf/configuration.xml` and write my favourite flow!

For example, I could write:

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<configuration>
    <mailer>
        <params>
            <param name="anti-burst" value="1000" />
            <param name="host" value="localhost" />
            <param name="port" value="33325" />
            <param name="from" value="superpipes@vauchelles.com" />
            <param name="to" value="superpipes@vauchelles.com" />
        </params>
    </mailer>
    <nodes>
        <node id="rss" type="com.vaushell.superpipes.nodes.rss.N_RSS">
            <!-- RSS doesn't receive messages. It reads a feed at regular intervals -->
            <params>
                <param name="url" value="http://feeds.feedburner.com/fabienvauchelles" />
                <param name="delay" value="60000" />
            </params>
        </node>
        <node id="logger" type="com.vaushell.superpipes.nodes.stub.N_MessageLogger" />
    </nodes>
    <routes>
        <route source="rss" destination="logger" />
    </routes>
</configuration>
```

Superpipes reads the RSS feed and show all messages.

More details could be found [here](User_Manual.md).


#### Step 2: Control the flow

I start the flow with:

```sh
$ sudo su - superpipes
$ java -jar superpipes.jar
```

When SuperPipes is executed on the command line:

* I can authenticate me with OAuth (Facebook, Twitter, LinkedIn, Tumblr, etc.);
* I can solve problems;
* I can control whether all messages are properly delivered.


#### Step 3: Launch the daemon

This command starts the daemon:

```sh
$ /etc/init.d/superpipes start
```

However, SuperPipes is started at boot.


## Other platform

It's coming!

SuperPipes is a JAR file. I can manually install it on each platform:)

Just [download the dist file](http://fabien.vauchelles.com/superpipes/1.0.2/superpipes-1.0.2-dist.tar.gz), unzip it, and run it.


## How to go further ?

See [User Manual](User_Manual.md).
