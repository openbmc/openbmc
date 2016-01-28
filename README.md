# OpenBMC #

[![Build Status](https://openpower.xyz/buildStatus/icon?job=openbmc-build)](https://openpower.xyz/job/openbmc-build/)

## Building ##

OpenBMC uses Yocto/Open-Embedded for a build system, which supports an
out-of-tree build.  It is recommended that you create an empty directory
somewhere to hold the build.  This directory will get big.

On Ubuntu 14.04 the following packages are required to build the default target

    sudo apt-get install -y git build-essential libsdl1.2-dev texinfo gawk chrpath diffstat

On Fedora 23 the following packages are required to build the default target:

    sudo dnf install -y git patch diffstat texinfo chrpath SDL-devel bitbake
    sudo dnf groupinstall "C Development Tools and Libraries"

To start a build:

    cd <builddir>
    . <repodir>/openbmc-env
    bitbake obmc-phosphor-image
