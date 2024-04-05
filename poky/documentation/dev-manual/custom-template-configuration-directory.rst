.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Creating a Custom Template Configuration Directory
**************************************************

If you are producing your own customized version of the build system for
use by other users, you might want to provide a custom build configuration
that includes all the necessary settings and layers (i.e. ``local.conf`` and
``bblayers.conf`` that are created in a new :term:`Build Directory`) and a custom
message that is shown when setting up the build. This can be done by
creating one or more template configuration directories in your
custom distribution layer.

This can be done by using ``bitbake-layers save-build-conf``::

   $ bitbake-layers save-build-conf ../../meta-alex/ test-1
   NOTE: Starting bitbake server...
   NOTE: Configuration template placed into /srv/work/alex/meta-alex/conf/templates/test-1
   Please review the files in there, and particularly provide a configuration description in /srv/work/alex/meta-alex/conf/templates/test-1/conf-notes.txt
   You can try out the configuration with
   TEMPLATECONF=/srv/work/alex/meta-alex/conf/templates/test-1 . /srv/work/alex/poky/oe-init-build-env build-try-test-1

The above command takes the config files from the currently active :term:`Build Directory` under ``conf``,
replaces site-specific paths in ``bblayers.conf`` with ``##OECORE##``-relative paths, and copies
the config files into a specified layer under a specified template name.

To use those saved templates as a starting point for a build, users should point
to one of them with :term:`TEMPLATECONF` environment variable::

   TEMPLATECONF=/srv/work/alex/meta-alex/conf/templates/test-1 . /srv/work/alex/poky/oe-init-build-env build-try-test-1

The OpenEmbedded build system uses the environment variable
:term:`TEMPLATECONF` to locate the directory from which it gathers
configuration information that ultimately ends up in the
:term:`Build Directory` ``conf`` directory.

If :term:`TEMPLATECONF` is not set, the default value is obtained
from ``.templateconf`` file that is read from the same directory as
``oe-init-build-env`` script. For the Poky reference distribution this
would be::

   TEMPLATECONF=${TEMPLATECONF:-meta-poky/conf/templates/default}

If you look at a configuration template directory, you will
see the ``bblayers.conf.sample``, ``local.conf.sample``, ``conf-summary.txt`` and
``conf-notes.txt`` files. The build system uses these files to form the
respective ``bblayers.conf`` file, ``local.conf`` file, and show
users usage information about the build they're setting up
when running the ``oe-init-build-env`` setup script. These can be
edited further if needed to improve or change the build configurations
available to the users, and provide useful summaries and detailed usage notes.

