![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Link our Twitter application with SuperPipes


## Step 1

Create a Twitter application ([see this tutorial](Create_Twitter_Application.md)).

---

## Step 2

Add the credentials of the application in the configuration:

```xml
<node id="twitter-fabienvauchelles" type="com.vaushell.superpipes.nodes.twitter.N_TW_Post">
    <params>
        <param name="key" value="MY_APP_KEY" />
        <param name="secret" value="MY_APP_SECRET" />
    </params>
</node>
```

---

## Step 3

Start SuperPipes with the command line.

---

## Step 4

Remember the node on which we perform the authentication (here: `twitter-fabienvauchelles`).

Then copy the URL authentication:

![Step4](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/twitter_link1.png)

---

## Step 5

Paste the URL in the browser:

![Step5](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/twitter_link2.png)

---

## Step 6

Authenticate with the correct account:

![Step6](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/twitter_link3.png)

---

## Step 7

Copy the verification code:

![Step7](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/twitter_link4.png)

---

## Step 8

Paste the verification code into SuperPipes:

![Step8](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/twitter_link5.png)

__It's done !__
