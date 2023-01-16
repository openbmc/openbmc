.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 4.2 (mickledore)
----------------------------------

New Features / Enhancements in 4.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- Python 3.8 is the minimum Python version required on the build host.
  For host distributions that do not provide it, this is included as part of the
  :term:`buildtools` tarball.

- This release now supports a new ``addpylib`` directive to enable
  Python libraries within layers.

  This directive should be added to your layer configuration,
  as in the below example from ``meta/conf/layer.conf``::

     addpylib ${LAYERDIR}/lib oe

-  Architecture-specific enhancements:

-  Kernel-related enhancements:

-  QEMU/runqemu enhancements:

-  Image-related enhancements:

