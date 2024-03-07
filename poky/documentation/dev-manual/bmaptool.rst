.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Flashing Images Using ``bmaptool``
**********************************

A fast and easy way to flash an image to a bootable device is to use
bmaptool, which is integrated into the OpenEmbedded build system.
bmaptool is a generic tool that creates a file's block map (bmap) and
then uses that map to copy the file. As compared to traditional tools
such as dd or cp, bmaptool can copy (or flash) large files like raw
system image files much faster.

.. note::

   -  If you are using Ubuntu or Debian distributions, you can install
      the ``bmap-tools`` package using the following command and then
      use the tool without specifying ``PATH`` even from the root
      account::

         $ sudo apt install bmap-tools

   -  If you are unable to install the ``bmap-tools`` package, you will
      need to build bmaptool before using it. Use the following command::

         $ bitbake bmaptool-native

Following, is an example that shows how to flash a Wic image. Realize
that while this example uses a Wic image, you can use bmaptool to flash
any type of image. Use these steps to flash an image using bmaptool:

#. *Update your local.conf File:* You need to have the following set
   in your ``local.conf`` file before building your image::

      IMAGE_FSTYPES += "wic wic.bmap"

#. *Get Your Image:* Either have your image ready (pre-built with the
   :term:`IMAGE_FSTYPES`
   setting previously mentioned) or take the step to build the image::

      $ bitbake image

#. *Flash the Device:* Flash the device with the image by using bmaptool
   depending on your particular setup. The following commands assume the
   image resides in the :term:`Build Directory`'s ``deploy/images/`` area:

   -  If you have write access to the media, use this command form::

         $ oe-run-native bmaptool-native bmaptool copy build-directory/tmp/deploy/images/machine/image.wic /dev/sdX

   -  If you do not have write access to the media, set your permissions
      first and then use the same command form::

         $ sudo chmod 666 /dev/sdX
         $ oe-run-native bmaptool-native bmaptool copy build-directory/tmp/deploy/images/machine/image.wic /dev/sdX

For help on the ``bmaptool`` command, use the following command::

   $ bmaptool --help

