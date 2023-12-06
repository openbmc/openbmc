#!/bin/sh

# Set BMC ready
gpioset "$(gpiofind BMC_READY)"=0
echo BMC ready !!
