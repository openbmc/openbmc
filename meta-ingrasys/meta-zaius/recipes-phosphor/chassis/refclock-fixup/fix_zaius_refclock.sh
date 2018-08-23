#!/bin/sh
# fix up refclock by reconfiguring output of Si5338C clock generator

i2cset="i2cset"
clkgen_i2c_bus="0"
clkgen_i2c_addr="0x70"

# Usage: clkgen_write <address> <data> [write length]
clkgen_write()
{
    $i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr $1 $2 $3
}

# disable outputs
clkgen_write 230 0x10 b

# set output configurations of each of the four channels to 3.3V HCSL
clkgen_write 36 0x07 b
clkgen_write 37 0x07 b
clkgen_write 38 0x07 b
clkgen_write 39 0x07 b
clkgen_write 40 0xe7 b
clkgen_write 41 0x9c b
clkgen_write 42 0x27 b

# enable outputs
clkgen_write 230 0x00 b
