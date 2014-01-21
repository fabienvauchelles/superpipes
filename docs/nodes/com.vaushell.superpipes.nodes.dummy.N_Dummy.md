![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Node: N_Dummy

Full class path : [`com.vaushell.superpipes.nodes.dummy.N_Dummy`](../../superpipes/src/main/java/com/vaushell/superpipes/nodes/dummy/N_Dummy.java)


## Goal

This node does nothing except to forward messages.

It can be combined with transforms.
It can help to design a graph.


## Default parameters

* anti-burst: 0
* delay: 0


## Standard parameters

No parameter.


## Use example

I create a filter node:

```xml
<node id="dummy" type="com.vaushell.superpipes.nodes.dummy.N_Dummy">
    <params>
        <param name="anti-burst" value="1000" />
    </params>

    <out>
      <transform type="com.vaushell.superpipes.transforms.done.T_Done" />
    </out>
</node>
```
