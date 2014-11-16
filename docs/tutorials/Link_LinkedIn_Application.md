![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Link our LinkedIn application with SuperPipes


## Step 1

Create a LinkedIn application ([see this tutorial](Create_LinkedIn_Application.md)).

---

## Step 2

Add the credentials of the application in the configuration:

```xml
<node id="linkedin-fabienvauchelles" type="com.vaushell.superpipes.nodes.linkedin.N_LNK_Post">
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

Remember the node on which we perform the authentication (here: `linkedin-fabienvauchelles`).

Then copy the URL authentication:

![Step4](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/linkedin_link1.png)

---

## Step 5

Paste the URL in the browser:

![Step5](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/linkedin_link2.png)

---

## Step 6

Authenticate with the correct account.

Then allow the application to access our account :

![Step6](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/linkedin_link3.png)

---

## Step 7

In the URL returned by LinkedIn, copy the verification code. It is indicated after "oauth_verifier=":

![Step7](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/linkedin_link4.png)

---

## Step 8

Paste the verification code into SuperPipes:

![Step8](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/linkedin_link5.png)

__It's done !__
