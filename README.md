## Building ##

OpenBMC uses Yocto/Open-Embedded for a build system, which supports an 
out-of-tree build.  It is recommended that you create an empty directory
somewhere to hold the build.  This directory will get big.

To start a build:

    cd <builddir>
    . <repodir>/openbmc-env
    bitbake obmc-phosphor-image

