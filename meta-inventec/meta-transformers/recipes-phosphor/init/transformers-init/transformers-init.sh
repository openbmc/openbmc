#!/bin/sh

# Init GPIO setting

# shellcheck disable=SC2046
gpioset $(gpiofind BMC_READY)=0
echo "BMC ready !!"
# shellcheck disable=SC2046
gpioset $(gpiofind RST_BMC_SGPIO)=1
echo "Release reset SGPIO !!"
