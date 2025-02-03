Master-next: 
[![pipeline status](https://gitlab.com/akuster/meta-security/badges/master-next/pipeline.svg)](https://gitlab.com/akuster/meta-security/-/commits/master-next)

Master: [![pipeline status](https://gitlab.com/akuster/meta-security/badges/master/pipeline.svg)](https://gitlab.com/akuster/meta-security/-/commits/master)

Meta-security
=============

The bbappend files for some recipes (e.g. linux-yocto) in this layer need
to have 'security' in DISTRO_FEATURES to have effect.
To enable them, add in configuration file the following line.

  DISTRO_FEATURES:append = " security"

If meta-security is included, but security  is not enabled as a
distro feature a warning is printed at parse time:

    You have included the meta-security layer, but
    'security' has not been enabled in your DISTRO_FEATURES. Some bbappend files
    and preferred version setting may not take effect.

If you know what you are doing, this warning can be disabled by setting the following
variable in your configuration:

  SKIP_META_SECURITY_SANITY_CHECK = 1

This layer provides security tools, hardening tools for Linux kernels
and libraries for implementing security mechanisms.

Dependencies
============

This layer depends on:

  URI: git://git.openembedded.org/openembedded-core
  branch: [same one as checked out for this layer]

  URI: git://git.openembedded.org/meta-openembedded/meta-oe
  branch: [same one as checked out for this layer]

Adding the security layer to your build
========================================

In order to use this layer, you need to make the build system aware of
it.

Assuming the security layer exists at the top-level of your
yocto build tree, you can add it to the build system by adding the
location of the security layer to bblayers.conf, along with any
other layers needed. e.g.:

  BBLAYERS ?= " \
    /path/to/oe-core/meta \
    /path/to/meta-openembedded/meta-oe \
    /path/to/layer/meta-security "

Optional Dynamic layer dependancy
======================================

  URI: git://git.openembedded.org/meta-openembedded/meta-oe

  URI: git://git.openembedded.org/meta-openembedded/meta-perl

  URI: git://git.openembedded.org/meta-openembedded/meta-python

  BBLAYERS += "/path/to/layer/meta-openembedded/meta-oe"
  BBLAYERS += "/path/to/layer/meta-openembedded/meta-perl"
  BBLAYERS += "/path/to/layer/meta-openembedded/meta-python"

This will activate the dynamic-layer mechanism.



Maintenance
======================================

Send pull requests, patches, comments or questions to yocto-patches@lists.yoctoproject.org  

When sending single patches, please using something like:
'git send-email -1 --to yocto-patches@lists.yoctoproject.org --subject-prefix=meta-security][PATCH'

These values can be set as defaults for this repository:

$ git config sendemail.to yocto-patches@lists.yoctoproject.org
$ git config format.subjectPrefix meta-security][PATCH

Now you can just do 'git send-email origin/master' to send all local patches.

For pull requests, please use create-pull-request and send-pull-request. 

Maintainers:    Armin Kuster <akuster808@gmail.com>


License
=======

All metadata is MIT licensed unless otherwise stated. Source code included
in tree for individual recipes is under the LICENSE stated in each recipe
(.bb file) unless otherwise stated.
