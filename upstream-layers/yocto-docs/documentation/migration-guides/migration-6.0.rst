.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: wrynose
.. |yocto-ver| replace:: 6.0
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Migration notes for |yocto-ver| (|yocto-codename|)
**************************************************

This section provides migration information for moving to the Yocto
Project |yocto-ver| Release (codename "|yocto-codename|") from the prior release.
For a list of new features and enhancements, see the
:doc:`/migration-guides/release-notes-6.0` section.

The |yocto-ver| (|yocto-codename|) release is the new LTS release after 5.0
(scarthgap). If you are migrating from the 5.0 version, be sure to read the
previous migration guides:

-  :doc:`/migration-guides/migration-5.1`
-  :doc:`/migration-guides/migration-5.2`
-  :doc:`/migration-guides/migration-5.3`

See also the list of new features and enhancements of the previous releases:

-  :doc:`/migration-guides/release-notes-5.1`
-  :doc:`/migration-guides/release-notes-5.2`
-  :doc:`/migration-guides/release-notes-5.3`

Supported kernel versions
-------------------------

The :term:`OLDEST_KERNEL` setting is XXX in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

Supported distributions
-----------------------

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

-  XXX

On the other hand, some earlier distributions are no longer supported:

-  XXX

See :ref:`all supported distributions <system-requirements-supported-distros>`.

BitBake fetcher removals
------------------------

The following :term:`BitBake` :ref:`fetchers <bitbake:bb-fetchers>` have been
disabled or removed:

-  The ``npm`` and ``npmsw`` (:wikipedia:`NPM <Npm>`) fetchers were disabled due
   to security concerns (:bitbake_rev:`355cd226e0720a9ed7683bb01c8c0a58eee03664`) and lack of maintenance.

   All associated tests have been disabled.

-  Support for the :wikipedia:`Bazaar <GNU_Bazaar>` fetcher was dropped
   (:bitbake_rev:`8e057d54f09ca0a1fb64c716da6b66f0ce4555b0`).

-  Support for the `OSC <https://github.com/openSUSE/osc>`__ fetcher was dropped
   (:bitbake_rev:`99497c1317788894cb00ecbb395df8693653f224`).

-  Support for the `CVS` fetcher was dropped
   (:bitbake_rev:`5427500e4f23580962126d2b27ed627ca237fe4a`).

Default :term:`INIT_MANAGER` definition changed to ``systemd``
--------------------------------------------------------------

The default definition of :term:`INIT_MANAGER` in the :oe_git:`defaultsetup.conf
<openembedded-core/tree/meta/conf/distro/defaultsetup.conf>` file was changed
from ``none`` to ``systemd``.

This means that users of the default distro setup (appearing as the ``nodistro``
:term:`DISTRO`) will be using :wikipedia:`systemd <Systemd>` as the default init
manager.

This does not change the default init manager of the :term:`Poky` distribution,
which remains :wikipedia:`SysVinit <UNIX_System_V>`.

Reverting to :wikipedia:`SysVinit <UNIX_System_V>` can be done by specifying the
:term:`INIT_MANAGER` in your own :term:`DISTRO` configuration file::

   INIT_MANAGER = "sysvinit"

See commit :oecore_rev:`0b4061c5d50261f826d0edb4b478d2d305274b7c` for more information.

Changes to the list of :term:`DISTRO_FEATURES` enabled by default
-----------------------------------------------------------------

The following :term:`DISTRO_FEATURES` are now enabled by default in
:oe_git:`meta/conf/bitbake.conf
</openembedded-core/tree/meta/conf/bitbake.conf>`:

-  ``multiarch``: Enable building applications with multiple architecture
   support.

-  ``opengl``: Include the Open Graphics Library, which is a
   cross-language multi-platform application programming interface used
   for rendering two and three-dimensional graphics.

-  ``ptest``: Enable building the package tests where supported by
   individual recipes. To add the built :ref:`ref-classes-ptest` packages to the
   image, add ``ptest-pkgs`` to :term:`IMAGE_FEATURES` in your image recipe.

-  ``vulkan``: Include support for the :wikipedia:`Vulkan API <Vulkan>`.

-  ``wayland``: Include the Wayland display server protocol and the
   library that supports it.

See commit :oecore_rev:`2e1e7c86064ce36580953650b27cca9ae7c269c4` for more information.

:ref:`ref-classes-native` and :ref:`ref-classes-cross` classes :term:`DEBUG_BUILD` change
-----------------------------------------------------------------------------------------

Previously, setting :term:`DEBUG_BUILD` to "1" globally would prevent the
:term:`OpenEmbedded Build System` from stripping target,
:ref:`ref-classes-native` and :ref:`ref-classes-cross` binaries. This was
changed so that only **target** binaries are stripped when this variable is set.

This change can be reverted by setting the two following lines in a
:term:`configuration file`::

   INHIBIT_SYSROOT_STRIP:class-cross = "${@oe.utils.vartrue('DEBUG_BUILD', '1', '', d)}"
   INHIBIT_SYSROOT_STRIP:class-native = "${@oe.utils.vartrue('DEBUG_BUILD', '1', '', d)}"

See commit :oecore_rev:`3c29afed1dc00ee8bccf93b8927d5ef2824065f6` for more information.

.. _ref-migration-6-0-u-boot-config-flow-changes:

U-Boot configuration flow changes (:ref:`ref-classes-uboot-config`)
-------------------------------------------------------------------

Declaring multiple U-Boot configurations used to be entirely defined by
one :term:`UBOOT_CONFIG` variable flag per configuration, e.g.::

   UBOOT_CONFIG ??= "foo bar"
   UBOOT_CONFIG[foo] = "config,images,binary"
   UBOOT_CONFIG[bar] = "config2,images2,binary2"

This has now been split into more variable flags, e.g.::

   UBOOT_CONFIG ??= "foo bar"
   UBOOT_CONFIG[foo] = "config"
   UBOOT_CONFIG[bar] = "config2"

   UBOOT_CONFIG_IMAGE_FSTYPES[bar] = "fstype"

   UBOOT_CONFIG_BINARY[foo] = "binary"

   UBOOT_CONFIG_MAKE_OPTS[foo] = "FOO=1"
   UBOOT_CONFIG_MAKE_OPTS[bar] = "BAR=1"

   UBOOT_CONFIG_FRAGMENTS[foo] = "foo.fragment"

See the documentation of the :ref:`ref-classes-uboot-config` for full details.

While the previous legacy approach is still supported, it will be removed in
for the next release. Users are advised to migrate to the new approach.

See commit :oecore_rev:`cd9e7304481b24b27df61c03ad73496d18e4d47c` for more information.

.. note::

   Single configuration builds have not changed and are still declared using the
   following statements::

      UBOOT_MACHINE = "config"
      UBOOT_BINARY = "u-boot.bin"

:ref:`ref-classes-pkgconfig`-related variables are no longer automatically exported
-----------------------------------------------------------------------------------

All the :ref:`ref-classes-pkgconfig`-related variables, such as
:term:`PKG_CONFIG_PATH`, are no longer exported in
:oe_git:`meta/conf/bitbake.conf <bitbake/tree/meta/conf/bitbake.conf>` with the
:ref:`export <bitbake:bitbake-user-manual/bitbake-user-manual-metadata:Exporting
Variables to the Environment>` directive.

These ``export`` statements have been moved to the :ref:`ref-classes-pkgconfig`
class, meaning recipes using these variables that not yet inheriting the
:ref:`ref-classes-pkgconfig` class should now inherit it with::

   inherit pkgconfig

See commit :oecore_rev:`68d2d38483efada7bc2409e10508b03a7431caff` for more information.

Rust language changes
---------------------

systemd changes
---------------

Support for SysVinit compatibility in systemd was dropped
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Support for the :wikipedia:`SysVinit <UNIX_System_V>` compatibility in
:wikipedia:`systemd <Systemd>` was dropped.

This is due to `recent announcements
<https://github.com/systemd/systemd/releases/tag/v260-rc1>`__ in `systemd` which
is planning to drop support for the `SysVinit` compatibility.

This means that the ``systemd`` and ``sysvinit`` :term:`distro features
<DISTRO_FEATURES>` cannot be used together anymore.

Users are advised to switch to one init manager or the other entirely.

See commit :oecore_rev:`d9ec9e20eebc062d084dd76b59d665994e0cb51b` for more information.

Recipe changes
--------------

Removed variables
-----------------

The following variables have been removed:

Removed recipes
---------------

The following recipes have been removed in this release:

-  ``jquery``: The last users of this recipe were the reproducible tests, but
   have been reworked to use the `jQuery` CDN instead with
   :oecore_rev:`d3ee5497b1ce6eb487419f6d821c3ad38491e5ec` (See
   :oecore_rev:`aae793a17e7d03293a9625c12eaf44c5a078614a`)

-  ``systemd-compat-units``: Dropped as a consequence of removing
   :wikipedia:`SysVinit <UNIX_System_V>` support in :wikipedia:`systemd
   <Systemd>`

Removed :term:`PACKAGECONFIG` options
-------------------------------------

-  ``mesa``: ``freedreno-fdperf`` (:oecore_rev:`293edd0d3d077d0fde7ba6671dc9a26d5b4cf5e4`)
-  ``libcxx``: ``no-atomics`` (:oecore_rev:`ccc585f94c51ebaef863f116bcd2b41b2d958666`)
-  ``systemd``: ``sysvinit`` (:oecore_rev:`e00d5d6eac65e2cd88e34c2790469c7325bfb37d`)

Removed classes
---------------

The following classes have been removed in this release:

-  ``oelint``: remove as most of the checks done by this class are done in other
   areas of code now, making this class obsolete.

Removed features
----------------

The following features have been removed in this release:

Miscellaneous changes
---------------------

-  :ref:`ref-classes-meson`: drop ``meson_do_qa_configure`` as it was
   non-functional (:oecore_rev:`0514b451b5d96135c6d24e75e0afa8b5aea513dd`)
