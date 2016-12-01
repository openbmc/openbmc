#!/bin/sh
# fix up Si 5338C clock generator

clkgen_i2c_bus="0"
clkgen_i2c_addr="0x70"

# disable outputs
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 230 0x10 b

# set output configurations of each of the four channels to 3.3V HCSL
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 36 0x07 b
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 37 0x07 b
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 38 0x07 b
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 39 0x07 b
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 40 0xe7 b
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 41 0x9c b
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 42 0x27 b

# enable outputs
i2cset -y $clkgen_i2c_bus $clkgen_i2c_addr 230 0x00 b
