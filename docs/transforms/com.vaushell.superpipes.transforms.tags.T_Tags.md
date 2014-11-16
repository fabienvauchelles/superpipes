![SuperPipes](https://raw.githubusercontent.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_Tags

Full class path : [`com.vaushell.superpipes.transforms.tags.T_Tags`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/tags/T_Tags.java)


## Goal

This transform keeps message only if tags contain a set of tags.


## Standard parameters

<table>

<tr>
<th>Key</th>
<th>Description</th>
<th>Type</th>
<th>Required</th>
<th>Default value</th>
<th>Example value</th>
</tr>

<tr>
<td>tags</td>
<td>A set of tags, separated by commas</td>
<td>string</td>
<td>yes</td>
<td>N/A</td>
<td>java,coding</td>
</tr>

<tr>
<td>type</td>
<td>
Type of operation:
<ul>
<li><code>INCLUDE_ONE</code>: the message contains at least one of the tags;</li>
<li><code>INCLUDE_ALL</code>: the message contains all the tags;</li>
<li><code>EXCLUDE_ONE</code>: the message doesn't contain one of the tags;</li>
<li><code>EXCLUDE_ALL</code>: the message doesn't contain all the tags.</li>
</ul>
</td>
<td>string</td>
<td>yes</td>
<td>N/A</td>
<td>java,coding</td>
</tr>

</table>


## Use example

```xml
<transform type="com.vaushell.superpipes.transforms.tags_T_Tags">
    <params>
      <param name="tags" value="java,coding" />
      <param name="type" value="INCLUDE_ONE" />
    </params>
</transform>
```
