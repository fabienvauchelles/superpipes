![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Link our Facebook application with SuperPipes


## Step 1

Create a Facebook application ([see this tutorial](Create_Facebook_Application.md)).

---

## Step 2

Add the credentials of the application in the configuration:

```xml
<node id="facebook-fabienvauchelles" type="com.vaushell.superpipes.nodes.fb.N_FB_Post">
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

Remember the node on which we perform the authentication (here: `facebook-fabienvauchelles`).

Then copy the URL authentication:

![Step4](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/facebook_link1.png)

---

## Step 5

Paste the URL in the browser:

![Step5](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/facebook_link2.png)

---

## Step 6

Authenticate with the correct account:

![Step6](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/facebook_link3.png)

Then allow the application to access our account.

---

## Step 7

__Be quick !__

Copy the returned URL:

![Step7](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/facebook_link4.png)

Because Facebook redirects to a blank page:

![Step7b](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/facebook_link5.png)

---

## Step 8

Paste the URL to retrieve the verification code and copy it. It is indicated after "code=":

![Step8](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/facebook_link6.png)

---

## Step 9

Paste the verification code into SuperPipes:

![Step9](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/facebook_link7.png)

__It's done !__
