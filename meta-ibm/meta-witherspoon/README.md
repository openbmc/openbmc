Witherspoon
================

This is the Witherspoon machine layer.

Witherspoon, or AC922, is an IBM POWER9 two-socket, 2U Accelerated Compute
Server with up to 6 NVIDIA Tesla GPUs. More information can be found
[here](https://www.ibm.com/us-en/marketplace/power-systems-ac922).

In addition to witherspoon, this layer contains additional machine
configurations such as swift. The default machine target is witherspoon,
so in order to build a different configuration, or to build witherspoon
after building a different one, set the MACHINE environment to the desired
configuration name (see the conf/machine/ subdirectory for available options):

    export MACHINE=machine_configuration_name

Then build:

    bitbake obmc-phosphor-image

Example:

    export MACHINE=swift
    bitbake obmc-phosphor-image
    bitbake phosphor-logging

    export MACHINE=witherspoon
    bitbake obmc-phosphor-image
