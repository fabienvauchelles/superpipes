![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_Buffer

Full class path : [`com.vaushell.superpipes.nodes.buffer.N_Buffer`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/buffer/N_Buffer.java)


## Goal

This node has multiple functions:

* I post messages only during certain time slots;
* I add a minimum time between 2 messages;
* I add a fixed or random time before processing a message.


## Default parameters

* anti-burst: 0
* delay: 0


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
flow-limit | Wait for X millisecond before sending another message. I select the frequency of messages. | long | no | N/A | 90000
wait-min | Wait a random time (between `wait-min` and `wait-max` milliseconds) before processing another message. For example, I like a Facebook message in 5 minutes or in 4 hours! | integer | no | N/A | 300000
wait-max | See wait-min. | integer | no | N/A | 14400000

__Tips:__ to avoid side effects, `flow-limit` must not match to a time slot. It must be smaller or larger.


## Slot parameters

I set time slots to send messages.

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
days | Day activation of the slot. Values are separated by commas. Days are: MON,TUE,WED,THU,FRI,SAT,SUN | string | yes | N/A | SAT,SUN
startat | Start time of the slot. Format is `HH:mm:ss`. This is inclusive. | date | yes | N/A | 10:30:00
endat | End time of the slot. Format is `HH:mm:ss`. This is exclusive. | date | yes | N/A | 19:00:00

## Use example

![Example](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/buffer_example.png)

I like all the messages of my product page:

* on week, between 19:00 and 21:00
* on weekend, between 12:00 and 14:00

I wait randomly between 5 minutes and 4 hours to like a message (i'm an automated process but I want to act like a human).

```xml
<node id="buffer" type="com.vaushell.superpipes.nodes.buffer.N_Buffer">
    <params>
        <param name="wait-min" value="300000" />
        <param name="wait-max" value="14400000" />
    </params>

    <slots>
        <slot days="MON,TUE,WED,THU,FRI" startat="19:00:00" endat="21:00:00" />
        <slot days="SAT,SUN" startat="12:00:00" endat="14:00:00" />
    </slots>
</node>
```
