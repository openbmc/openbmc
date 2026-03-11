.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 3.3 (hardknott)
=======================

This section provides migration information for moving to the Yocto
Project 3.3 Release (codename "hardknott") from the prior release.


.. _migration-3.3-minimum-system-requirements:

Minimum system requirements
---------------------------

You will now need at least Python 3.6 installed on your build host. Most recent
distributions provide this, but should you be building on a distribution that
does not have it, you can use the :term:`buildtools` tarball (easily installable
using ``scripts/install-buildtools``) --- see
:ref:`ref-manual/system-requirements:required git, tar, python, make and gcc versions`
for details.

.. _migration-3.3-removed-recipes:

Removed recipes
---------------

The following recipes have been removed:

- ``go-dep``: obsolete with the advent of go modules
- ``gst-validate``: replaced by ``gst-devtools``
- ``linux-yocto``: removed 5.8 version recipes (5.4 / 5.10 still provided)
- ``vulkan-demos``: replaced by ``vulkan-samples``


.. _migration-3.3-common-license-only-versions:

Single version common license file naming
-----------------------------------------

Some license files in ``meta/files/common-licenses`` have been renamed to match
current SPDX naming conventions:

- AGPL-3.0 -> AGPL-3.0-only
- GPL-1.0 -> GPL-1.0-only
- GPL-2.0 -> GPL-2.0-only
- GPL-3.0 -> GPL-3.0-only
- LGPL-2.0 -> LGPL-2.0-only
- LGPL-2.1 -> LGPL-2.1-only
- LGPL-3.0 -> LGPL-3.0-only

Additionally, corresponding "-or-later" suffixed files have been added e.g.
``GPL-2.0-or-later``.

It is not required that you change :term:`LICENSE` values as there are mappings
from the original names in place; however, in rare cases where you have a recipe
which sets :term:`LIC_FILES_CHKSUM` to point to file(s) in
``meta/files/common-licenses`` (which in any case is not recommended) you will
need to update those.


.. _migration-3.3-python3targetconfig:

New ``python3targetconfig`` class
---------------------------------

A new :ref:`ref-classes-python3targetconfig` class has
been created for situations where you would previously have inherited the
:ref:`ref-classes-python3native` class but need access to
target configuration data (such as correct installation directories). Recipes
where this situation applies should be changed to inherit
:ref:`ref-classes-python3targetconfig` instead of
:ref:`ref-classes-python3native`. This also adds a dependency
on target ``python3``, so it should only be used where appropriate in order to
avoid unnecessarily lengthening builds.

Some example recipes where this change has been made: ``gpgme``, ``libcap-ng``,
``python3-pycairo``.


.. _migration-3.3-distutils-path:

``setup.py`` path for Python modules
------------------------------------

In a Python module, sometimes ``setup.py`` can be buried deep in the
source tree. Previously this was handled in recipes by setting :term:`S` to
point to the subdirectory within the source where ``setup.py`` is located.
However with the recent :ref:`pseudo <overview-manual/concepts:fakeroot and pseudo>`
changes, some Python modules make changes to files beneath ``${S}``, for
example::

   S = "${WORKDIR}/git/python/pythonmodule"

then in ``setup.py`` it works with source code in a relative fashion, such
as ``../../src``. This causes pseudo to fail as it isn't able to track
the paths properly. This release introduces a new ``DISTUTILS_SETUP_PATH``
variable so that recipes can specify it explicitly, for example::

   S = "${WORKDIR}/git"
   DISTUTILS_SETUP_PATH = "${S}/python/pythonmodule"

Recipes that inherit from ``distutils3`` (or :ref:`ref-classes-setuptools3`
which itself inherits ``distutils3``) that also set :term:`S` to point to a
Python module within a subdirectory in the aforementioned manner should be
changed to set ``DISTUTILS_SETUP_PATH`` instead.


.. _migration-3.3-bitbake:

BitBake changes
---------------

- BitBake is now configured to use a default ``umask`` of ``022`` for all tasks
  (specified via a new :term:`BB_DEFAULT_UMASK` variable). If needed, ``umask`` can
  still be set on a per-task basis via the ``umask`` varflag on the task
  function, but that is unlikely to be necessary in most cases.

- If a version specified in :term:`PREFERRED_VERSION` is not available this
  will now trigger a warning instead of just a note, making such issues more
  visible.


.. _migration-3.3-packaging:

Packaging changes
-----------------

The following packaging changes have been made; in all cases the main package
still depends upon the split out packages so you should not need to do anything
unless you want to take advantage of the improved granularity:

- ``dbus``: ``-common`` and ``-tools`` split out
- ``iproute2``: split ``ip`` binary to its own package
- ``net-tools``: split ``mii-tool`` into its own package
- ``procps``: split ``ps`` and ``sysctl`` into their own packages
- ``rpm``: split build and extra functionality into separate packages
- ``sudo``: split ``sudo`` binary into ``sudo-sudo`` and libs into ``sudo-lib``
- ``systemtap``: examples, Python scripts and runtime material split out
- ``util-linux``: ``libuuid`` has been split out to its own
  ``util-linux-libuuid`` recipe (and corresponding packages) to avoid circular
  dependencies if ``libgcrypt`` support is enabled in ``util-linux``.
  (``util-linux`` depends upon ``util-linux-libuuid``.)


.. _migration-3.3-misc:

Miscellaneous changes
---------------------

- The default poky :term:`DISTRO_VERSION` value now uses the core metadata's
  git hash (i.e. :term:`METADATA_REVISION`) rather than the date (i.e.
  :term:`DATE`) to reduce one small source of non-reproducibility. You can
  of course specify your own :term:`DISTRO_VERSION` value as desired
  (particularly if you create your own custom distro configuration).
- ``adwaita-icon-theme`` version 3.34.3 has been added back, and is selected
  as the default via :term:`PREFERRED_VERSION` in
  ``meta/conf/distro/include/default-versions.inc`` due to newer versions
  not working well with ``librsvg`` 2.40. ``librsvg`` is not practically
  upgradeable at the moment as it has been ported to Rust, and Rust is not
  (yet) in OE-Core, but this will change in a future release.
- ``ffmpeg`` is now configured to disable GPL-licensed portions by default
  to make it harder to accidentally violate the GPL. To explicitly enable GPL
  licensed portions, add ``gpl`` to :term:`PACKAGECONFIG` for ``ffmpeg``
  using a bbappend (or use ``PACKAGECONFIG_append_pn-ffmpeg = " gpl"`` in
  your configuration.)
- ``connman`` is now set to conflict with ``systemd-networkd`` as they
  overlap functionally and may interfere with each other at runtime.
- Canonical SPDX license names are now used in image license manifests in
  order to avoid aliases of the same license from showing up together (e.g.
  ``GPLv2`` and ``GPL-2.0``)
