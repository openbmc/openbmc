.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: whinlatter
.. |yocto-ver| replace:: 5.3
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Release |yocto-ver| (|yocto-codename|)
======================================

Migration notes for |yocto-ver| (|yocto-codename|)
--------------------------------------------------

This section provides migration information for moving to the Yocto
Project |yocto-ver| Release (codename "|yocto-codename|") from the prior release.

:term:`WORKDIR` changes
~~~~~~~~~~~~~~~~~~~~~~~

``S = ${WORKDIR}/something`` no longer supported
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If a recipe has :term:`S` set to be ``${``\ :term:`WORKDIR`\ ``}/something``,
this is no longer supported, and an error will be issued. The recipe should be
changed to::

   S = "${UNPACKDIR}/something"

``S = ${WORKDIR}/git`` and ``S = ${UNPACKDIR}/git`` should be removed
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The Git fetcher now unpacks into the :term:`BB_GIT_DEFAULT_DESTSUFFIX` directory
rather than the ``git/`` directory under :term:`UNPACKDIR`.
:term:`BB_GIT_DEFAULT_DESTSUFFIX` is set in :term:`OpenEmbedded-Core
(OE-Core)`'s :oe_git:`bitbake.conf
</openembedded-core/tree/meta/conf/bitbake.conf>` to :term:`BP`.

This location matches the default value of :term:`S` set by bitbake.conf, so :term:`S`
setting in recipes can and should be removed.

Note that when :term:`S` is set to a subdirectory of the git checkout, then it
should be instead adjusted according to the previous point::

   S = "${UNPACKDIR}/${BP}/something"

Note that "git" as the source checkout location can be hardcoded
in other places in recipes; when it's in :term:`SRC_URI`, replace with
:term:`BB_GIT_DEFAULT_DESTSUFFIX`, otherwise replace with :term:`BP`.

How to make those adjustments without tedious manual editing
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The following sed command can be used to remove S = "${WORKDIR}/git
across a whole layer::

   sed -i "/^S = \"\${WORKDIR}\/git\"/d" `find . -name *.bb -o -name *.inc -o -name *.bbclass`

Then, the following command can tweak the remaining :term:`S` assignments to
refer to :term:`UNPACKDIR` instead of :term:`WORKDIR`::

   sed -i "s/^S = \"\${WORKDIR}\//S = \"\${UNPACKDIR}\//g" `find . -name *.bb -o -name *.inc -o -name *.bbclass`

The first change can introduce a lot of consecutive empty lines, so those can be removed with::

   sed -i -z -E 's/([ \t\f\v\r]*\n){3,}/\n\n/g' `find . -name *.bb -o -name *.inc`

Supported kernel versions
~~~~~~~~~~~~~~~~~~~~~~~~~

The :term:`OLDEST_KERNEL` setting is XXX in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

Supported distributions
~~~~~~~~~~~~~~~~~~~~~~~

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

-  XXX

On the other hand, some earlier distributions are no longer supported:

-  XXX

See :ref:`all supported distributions <system-requirements-supported-distros>`.

Rust language changes
~~~~~~~~~~~~~~~~~~~~~

systemd changes
~~~~~~~~~~~~~~~

Recipe changes
~~~~~~~~~~~~~~

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

Removed classes
~~~~~~~~~~~~~~~

The following classes have been removed in this release:

-  ``kernel-fitimage.bbclass``: the class has been replaced by the
   :ref:`ref-classes-kernel-fit-image` class. The new implementation resolves
   the long-standing :yocto_bugs:`bug 12912</show_bug.cgi?id=12912>`.

   If you are using the kernel FIT image support, you will need to:

   #. Make sure to include ``kernel-fit-extra-artifacts`` in your :term:`KERNEL_CLASSES`
      variable to ensure the required files are exposed to the :term:`DEPLOY_DIR_IMAGE`
      directory::

         KERNEL_CLASSES += "kernel-fit-extra-artifacts"

   #. Use the specific FIT image recipe rather than the base kernel recipe.
      For example, instead of::

         bitbake linux-yocto

      the FIT image is now build by::

         bitbake linux-yocto-fitimage

      For custom kernel recipes, creating a corresponding custom FIT image recipe
      is usually a good approach.

   #. If a FIT image is used as a replacement for the kernel image in the root
      filesystem, add the following configuration to your machine configuration
      file::

         # Create and deploy the vmlinux artifact which gets included into the FIT image
         KERNEL_CLASSES += "kernel-fit-extra-artifacts"

         # Do not install the kernel image package
         RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""
         # Install the FIT image package
         MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "linux-yocto-fitimage"

         # Configure the image.bbclass to depend on the FIT image instead of only
         # the kernel to ensure the FIT image is built and deployed with the image
         KERNEL_DEPLOY_DEPEND = "linux-yocto-fitimage:do_deploy"

   See the :ref:`ref-classes-kernel-fit-image` section for more information.

Removed features
~~~~~~~~~~~~~~~~

The following features have been removed in this release:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~
