![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Link our Tumblr application with SuperPipes


## Step 1

Create a Tumblr application ([see this tutorial](Create_Tumblr_Application.md)).

---

## Step 2

Add the credentials of the application in the configuration:

```xml
<node id="tumblr-fabienvauchelles" type="com.vaushell.superpipes.nodes.tumblr.N_TB_Post">
    <params>
        <param name="blogname" value="lesliensducode.tumblr.com" />
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

Remember the node on which we perform the authentication (here: tumblr-fabienvauchelles).

Then copy the URL authentication:

![Step4](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/tumblr_link1.png)

---

## Step 5

Paste the URL in the browser:

![Step5](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/tumblr_link2.png)

---

## Step 6

Authenticate with the correct account:

![Step6](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/tumblr_link3.png)

---

## Step 7

Allow the application to access our account:

![Step7](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/tumblr_link4.png)

---

## Step 8

In the URL returned by Tumblr, copy the verification code. It is indicated after "oauth_verifier=":

![Step8](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/tumblr_link5.png)

---

## Step 9

Paste the verification code into SuperPipes:

![Step9](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/tumblr_link6.png)

__It's done !__
