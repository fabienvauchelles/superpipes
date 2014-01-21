![SuperPipes](https://raw2.github.com/fabienvauchelles/superpipes/master/docs/images/logo_slogan238.png)


# Transform: T_Date

Full class path : [`com.vaushell.superpipes.transforms.date.T_Date`](../../superpipes/src/main/java/com/vaushell/superpipes/transforms/date/T_Date.java)


## Goal

This transform keeps message only if the date is later and/or prior a specific date.


## Standard parameters

Key | Description | Type | Required | Default value | Example value
 --- | --- | --- | --- | --- | --- 
date-min | Minimum date (format: `dd/MM/yyyy HH:mm:ss`). This is inclusive | date | no | N/A | 20/01/2014 15:00:00
date-max | Maximum date (format: `dd/MM/yyyy HH:mm:ss`). This is exclusive | date | no | N/A | 21/01/2014 14:30:00


## Use example

```xml
<transform type="com.vaushell.superpipes.transforms.date.T_Date">
    <params>
      <param name="date-min" value="20/01/2014 15:00:00" />
      <param name="date-max" value="21/01/2014 14:30:00" />
    </params>
</transform>
```
