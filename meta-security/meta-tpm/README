meta-tpm layer
==============

The bbappend files for some recipes (e.g. linux-yocto) in this layer need
to have 'tpm' in DISTRO_FEATURES to have effect.
To enable them, add in configuration file the following line.

  DISTRO_FEATURES:append = " tpm"

If meta-tpm is included, but tpm is not enabled as a
distro feature a warning is printed at parse time:

    You have included the meta-tpm layer, but
    'tpm' has not been enabled in your DISTRO_FEATURES. Some bbappend files
    and preferred version setting may not take effect.

If you know what you are doing, this warning can be disabled by setting the following
variable in your configuration:

  SKIP_META_TPM_SANITY_CHECK = 1


This layer contains base TPM recipes.

Dependencies
============

This layer depends on:

  URI: git://git.openembedded.org/openembedded-core
  branch: master
  revision: HEAD
  prio: default

  URI: git://git.openembedded.org/meta-openembedded/meta-oe
  branch: master
  revision: HEAD
  prio: default

Adding the meta-tpm layer to your build
========================================

In order to use this layer, you need to make the build system aware of
it.

Assuming this layer exists at the top-level of your
yocto build tree, you can add it to the build system by adding the
location of the meta-tpm layer to bblayers.conf, along with any
other layers needed. e.g.:

  BBLAYERS ?= " \
    /path/to/oe-core/meta \
    /path/to/meta-openembedded/meta-oe \
    /path/to/layer/meta-tpm \


Maintenance
-----------

Send pull requests, patches, comments or questions to yocto@lists.yoctoproject.org

When sending single patches, please using something like:
'git send-email -1 --to yocto@lists.yoctoproject.org --subject-prefix=meta-security][PATCH'

These values can be set as defaults for this repository:

$ git config sendemail.to yocto@lists.yoctoproject.org
$ git config format.subjectPrefix meta-security][PATCH

Now you can just do 'git send-email origin/master' to send all local patches.

Maintainers:    Armin Kuster <akuster808@gmail.com>


License
=======

All metadata is MIT licensed unless otherwise stated. Source code included
in tree for individual recipes is under the LICENSE stated in each recipe
(.bb file) unless otherwise stated.
