.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 4.2 (mickledore)
========================

Migration notes for 4.2 (mickledore)
------------------------------------

This section provides migration information for moving to the Yocto
Project 4.2 Release (codename "mickledore") from the prior release.

.. _migration-4.2-supported-distributions:

Supported distributions
~~~~~~~~~~~~~~~~~~~~~~~

This release supports running BitBake on new GNU/Linux distributions:

-  Fedora 36 and 37
-  AlmaLinux 8.7 and 9.1
-  OpenSuse 15.4

On the other hand, some earlier distributions are no longer supported:

-  Debian 10.x
-  Fedora 34 and 35
-  AlmaLinux 8.5

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _migration-4.2-python-3.8:

Python 3.8 is now the minimum required Python version version
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BitBake and OpenEmbedded-Core now require Python 3.8 or newer,
making it a requirement to use a distribution providing at least this
version, or to install a :term:`buildtools` tarball.

.. _migration-4.2-gcc-8.0:

gcc 8.0 is now the minimum required GNU C compiler version
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This version, released in 2018, is a minimum requirement
to build the ``mesa-native`` recipe and as the latter is in the
default dependency chain when building QEMU this has now been
made a requirement for all builds.

In the event that your host distribution does not provide this
or a newer version of gcc, you can install a
:term:`buildtools-extended` tarball.

.. _migration-4.2-new-nvd-api:

Fetching the NVD vulnerability database through the 2.0 API
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This new version adds a new fetcher for the NVD database using the 2.0 API,
as the 1.0 API will be retired in 2023.

The implementation changes as little as possible, keeping the current
database format (but using a different database file for the transition
period), with a notable exception of not using the META table.

Here are minor changes that you may notice:

-  The database starts in 1999 instead of 2002
-  The complete fetch is longer (30 minutes typically)

.. _migration-4.2-rust-crate-checksums:

Rust: mandatory checksums for crates
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This release now supports checksums for Rust crates and makes
them mandatory for each crate in a recipe. See :yocto_git:`python3_bcrypt recipe changes
</poky/commit/?h=mickledore&id=0dcb5ab3462fdaaf1646b05a00c7150eea711a9a>`
for example.

The ``cargo-update-recipe-crates`` utility
:yocto_git:`has been extended </poky/commit/?h=mickledore&id=eef7fbea2c5bf59369390be4d5efa915591b7b22>`
to include such checksums. So, in case you need to add the list of checksums
to a recipe just inheriting the :ref:`ref-classes-cargo` class so far, you can
follow these steps:

#.  Make the recipe inherit :ref:`ref-classes-cargo-update-recipe-crates`
#.  Remove all ``crate://`` lines from the recipe
#.  Create an empty ``${BPN}-crates.inc`` file and make your recipe require it
#.  Execute ``bitbake -c update_crates your_recipe``
#.  Copy and paste the output of BitBake about the missing checksums into the
    ``${BPN}-crates.inc`` file.


.. _migration-4.2-addpylib:

Python library code extensions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BitBake in this release now supports a new ``addpylib`` directive to enable
Python libraries within layers.

This directive should be added to your layer configuration
as in the below example from ``meta/conf/layer.conf``::

   addpylib ${LAYERDIR}/lib oe

Layers currently adding a lib directory to extend Python library code should now
use this directive as :term:`BBPATH` is not going to be added automatically by
OE-Core in future. Note that the directives are immediate operations, so it does
make modules available for use sooner than the current BBPATH-based approach.

For more information, see :ref:`bitbake-user-manual/bitbake-user-manual-metadata:extending python library code`.


.. _migration-4.2-removed-variables:

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

-  ``SERIAL_CONSOLE``, deprecated since version 2.6, replaced by :term:`SERIAL_CONSOLES`.
-  ``PACKAGEBUILDPKGD``, a mostly internal variable in the :ref:`ref-classes-package`
   class was rarely used to customise packaging. If you were using this in your custom
   recipes or bbappends, you will need to switch to using :term:`PACKAGE_PREPROCESS_FUNCS`
   or :term:`PACKAGESPLITFUNCS` instead.

.. _migration-4.2-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

-  ``python3-picobuild``: after switching to ``python3-build``
-  ``python3-strict-rfc3339``: unmaintained and not needed by anything in
   :oe_git:`openembedded-core </openembedded-core>`
   or :oe_git:`meta-openembedded </meta-openembedded>`.
-  ``linux-yocto``: removed version 5.19 recipes (6.1 and 5.15 still provided)


.. _migration-4.2-removed-classes:

Removed classes
~~~~~~~~~~~~~~~

The following classes have been removed in this release:

-  ``rust-bin``: no longer used
-  ``package_tar``: could not be used for actual packaging, and thus not particularly useful.


LAYERSERIES_COMPAT for custom layers and devtool workspace
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Some layer maintainers have been setting :term:`LAYERSERIES_COMPAT` in their
layer's ``conf/layer.conf`` to the value of ``LAYERSERIES_CORENAMES`` to
effectively bypass the compatibility check - this is no longer permitted.
Layer maintainers should set :term:`LAYERSERIES_COMPAT` appropriately to
help users understand the compatibility status of the layer.

Additionally, the :term:`LAYERSERIES_COMPAT` value for the devtool workspace
layer is now set at the time of creation, thus if you upgrade with the
workspace layer enabled and you wish to retain it, you will need to manually
update the :term:`LAYERSERIES_COMPAT` value in ``workspace/conf/layer.conf``
(or remove the path from :term:`BBLAYERS` in ``conf/bblayers.conf`` and
delete/move the ``workspace`` directory out of the way if you no longer
need it).


.. _migration-4.2-runqemu-slirp:

runqemu now limits slirp host port forwarding to localhost
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

With default slirp port forwarding configuration in runqemu, qemu
previously listened on TCP ports 2222 and 2323 on all IP addresses
available on the build host. Most use cases with runqemu only need
it for localhost and it is not safe to run qemu images with root
login without password enabled and listening on all available,
possibly Internet reachable network interfaces. Thus, in this
release we limit qemu port forwarding to localhost (127.0.0.1).

However, if you need the qemu machine to be reachable from the
network, then it can be enabled via ``conf/local.conf`` or machine
config variable ``QB_SLIRP_OPT``::

   QB_SLIRP_OPT = "-netdev user,id=net0,hostfwd=tcp::2222-:22"


.. _migration-4.2-patch-qa:

Patch QA checks
~~~~~~~~~~~~~~~

The QA checks for patch fuzz and Upstream-Status have been reworked
slightly in this release. The Upstream-Status checking is now configurable
from :term:`WARN_QA` / :term:`ERROR_QA` (``patch-status-core`` for the
core layer, and ``patch-status-noncore`` for other layers).

The ``patch-fuzz`` and ``patch-status-core`` checks are now in the default
value of :term:`ERROR_QA` so that they will cause the build to fail
if triggered. If you prefer to avoid this you will need to adjust the value
of :term:`ERROR_QA` in your configuration as desired.


.. _migration-4.2-mesa:

Native/nativesdk mesa usage and graphics drivers
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This release includes mesa 23.0, and with that mesa release it is not longer
possible to use drivers from the host system, as mesa upstream has added strict
checks for matching builds between drivers and libraries that load them.

This is particularly relevant when running QEMU built within the build
system. A check has been added to runqemu so that there is a helpful error
when there is no native/nativesdk opengl/virgl support available.

To support this, a number of drivers have been enabled when building ``mesa-native``.
The one major dependency pulled in by this change is ``llvm-native`` which will
add a few minutes to the build on a modern machine. If this is undesirable, you
can set the value of :term:`DISTRO_FEATURES_NATIVE` in your configuration such
that ``opengl`` is excluded.


.. _migration-4.2-misc-changes:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

-  The :term:`IMAGE_NAME` variable is now set based on :term:`IMAGE_LINK_NAME`. This
   means that if you are setting :term:`IMAGE_LINK_NAME` to "" to disable unversioned
   image symlink creation, you also now need to set :term:`IMAGE_NAME` to still have
   a reasonable value e.g.::

      IMAGE_LINK_NAME = ""
      IMAGE_NAME = "${IMAGE_BASENAME}${IMAGE_MACHINE_SUFFIX}${IMAGE_VERSION_SUFFIX}"

-  In ``/etc/os-release``, the ``VERSION_CODENAME`` field is now used instead of
   ``DISTRO_CODENAME`` (though its value is still set from the :term:`DISTRO_CODENAME`
   variable) for better conformance to standard os-release usage. If you have runtime
   code reading this from ``/etc/os-release`` it may need to be updated.

-  The kmod recipe now enables OpenSSL support by default in order to support module
   signing. If you do not need this and wish to reclaim some space/avoid the dependency
   you should set :term:`PACKAGECONFIG` in a kmod bbappend (or ``PACKAGECONFIG:pn-kmod``
   at the configuration level) to exclude ``openssl``.

-  The ``OEBasic`` signature handler (see :term:`BB_SIGNATURE_HANDLER`) has been
   removed. It is unlikely that you would have selected to use this, but if you have
   you will need to remove this setting.

-  The :ref:`ref-classes-package` class now checks if package names conflict via
   ``PKG:${PN}`` override during ``do_package``. If you receive the associated error
   you will need to address the :term:`PKG` usage so that the conflict is resolved.

-  openssh no longer uses :term:`RRECOMMENDS` to pull in ``rng-tools``, since rngd
   is no longer needed as of Linux kernel 5.6. If you still need ``rng-tools``
   installed for other reasons, you should add ``rng-tools`` explicitly to your
   image. If you additionally need rngd to be started as a service you will also
   need to add the ``rng-tools-service`` package as that has been split out.

-  The cups recipe no longer builds with the web interface enabled, saving ~1.8M of
   space in the final image. If you wish to enable it, you should set
   :term:`PACKAGECONFIG` in a cups bbappend (or ``PACKAGECONFIG:pn-cups`` at the
   configuration level) to include ``webif``.

-  The :ref:`ref-classes-scons` class now passes a ``MAXLINELENGTH`` argument to
   scons in order to fix an issue with scons and command line lengths when ccache is
   enabled. However, some recipes may be using older scons versions which don't support
   this argument. If that is the case you can set the following in the recipe in order
   to disable this::

      SCONS_MAXLINELENGTH = ""
