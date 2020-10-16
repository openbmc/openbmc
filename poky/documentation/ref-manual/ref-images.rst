.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

******
Images
******

The OpenEmbedded build system provides several example images to satisfy
different needs. When you issue the ``bitbake`` command you provide a
"top-level" recipe that essentially begins the build for the type of
image you want.

.. note::

   Building an image without GNU General Public License Version 3
   (GPLv3), GNU Lesser General Public License Version 3 (LGPLv3), and
   the GNU Affero General Public License Version 3 (AGPL-3.0) components
   is only supported for minimal and base images. Furthermore, if you
   are going to build an image using non-GPLv3 and similarly licensed
   components, you must make the following changes in the
   local.conf
   file before using the BitBake command to build the minimal or base
   image:
   ::

           1. Comment out the EXTRA_IMAGE_FEATURES line
           2. Set INCOMPATIBLE_LICENSE = "GPL-3.0 LGPL-3.0 AGPL-3.0"


From within the ``poky`` Git repository, you can use the following
command to display the list of directories within the :term:`Source Directory`
that contain image recipe files: ::

   $ ls meta*/recipes*/images/*.bb

Following is a list of supported recipes:

-  ``build-appliance-image``: An example virtual machine that contains
   all the pieces required to run builds using the build system as well
   as the build system itself. You can boot and run the image using
   either the `VMware
   Player <http://www.vmware.com/products/player/overview.html>`__ or
   `VMware
   Workstation <http://www.vmware.com/products/workstation/overview.html>`__.
   For more information on this image, see the :yocto_home:`Build
   Appliance </software-item/build-appliance>` page
   on the Yocto Project website.

-  ``core-image-base``: A console-only image that fully supports the
   target device hardware.

-  ``core-image-clutter``: An image with support for the Open GL-based
   toolkit Clutter, which enables development of rich and animated
   graphical user interfaces.

-  ``core-image-full-cmdline``: A console-only image with more
   full-featured Linux system functionality installed.

-  ``core-image-lsb``: An image that conforms to the Linux Standard Base
   (LSB) specification. This image requires a distribution configuration
   that enables LSB compliance (e.g. ``poky-lsb``). If you build
   ``core-image-lsb`` without that configuration, the image will not be
   LSB-compliant.

-  ``core-image-lsb-dev``: A ``core-image-lsb`` image that is suitable
   for development work using the host. The image includes headers and
   libraries you can use in a host development environment. This image
   requires a distribution configuration that enables LSB compliance
   (e.g. ``poky-lsb``). If you build ``core-image-lsb-dev`` without that
   configuration, the image will not be LSB-compliant.

-  ``core-image-lsb-sdk``: A ``core-image-lsb`` that includes everything
   in the cross-toolchain but also includes development headers and
   libraries to form a complete standalone SDK. This image requires a
   distribution configuration that enables LSB compliance (e.g.
   ``poky-lsb``). If you build ``core-image-lsb-sdk`` without that
   configuration, the image will not be LSB-compliant. This image is
   suitable for development using the target.

-  ``core-image-minimal``: A small image just capable of allowing a
   device to boot.

-  ``core-image-minimal-dev``: A ``core-image-minimal`` image suitable
   for development work using the host. The image includes headers and
   libraries you can use in a host development environment.

-  ``core-image-minimal-initramfs``: A ``core-image-minimal`` image that
   has the Minimal RAM-based Initial Root Filesystem (initramfs) as part
   of the kernel, which allows the system to find the first "init"
   program more efficiently. See the
   :term:`PACKAGE_INSTALL` variable for
   additional information helpful when working with initramfs images.

-  ``core-image-minimal-mtdutils``: A ``core-image-minimal`` image that
   has support for the Minimal MTD Utilities, which let the user
   interact with the MTD subsystem in the kernel to perform operations
   on flash devices.

-  ``core-image-rt``: A ``core-image-minimal`` image plus a real-time
   test suite and tools appropriate for real-time use.

-  ``core-image-rt-sdk``: A ``core-image-rt`` image that includes
   everything in the cross-toolchain. The image also includes
   development headers and libraries to form a complete stand-alone SDK
   and is suitable for development using the target.

-  ``core-image-sato``: An image with Sato support, a mobile environment
   and visual style that works well with mobile devices. The image
   supports X11 with a Sato theme and applications such as a terminal,
   editor, file manager, media player, and so forth.

-  ``core-image-sato-dev``: A ``core-image-sato`` image suitable for
   development using the host. The image includes libraries needed to
   build applications on the device itself, testing and profiling tools,
   and debug symbols. This image was formerly ``core-image-sdk``.

-  ``core-image-sato-sdk``: A ``core-image-sato`` image that includes
   everything in the cross-toolchain. The image also includes
   development headers and libraries to form a complete standalone SDK
   and is suitable for development using the target.

-  ``core-image-testmaster``: A "master" image designed to be used for
   automated runtime testing. Provides a "known good" image that is
   deployed to a separate partition so that you can boot into it and use
   it to deploy a second image to be tested. You can find more
   information about runtime testing in the
   ":ref:`dev-manual/dev-manual-common-tasks:performing automated runtime testing`"
   section in the Yocto Project Development Tasks Manual.

-  ``core-image-testmaster-initramfs``: A RAM-based Initial Root
   Filesystem (initramfs) image tailored for use with the
   ``core-image-testmaster`` image.

-  ``core-image-weston``: A very basic Wayland image with a terminal.
   This image provides the Wayland protocol libraries and the reference
   Weston compositor. For more information, see the
   ":ref:`dev-manual/dev-manual-common-tasks:using wayland and weston`"
   section in the Yocto Project Development Tasks Manual.

-  ``core-image-x11``: A very basic X11 image with a terminal.
