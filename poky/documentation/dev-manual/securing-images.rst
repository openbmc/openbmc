.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Making Images More Secure
*************************

Security is of increasing concern for embedded devices. Consider the
issues and problems discussed in just this sampling of work found across
the Internet:

-  *"*\ `Security Risks of Embedded
   Systems <https://www.schneier.com/blog/archives/2014/01/security_risks_9.html>`__\ *"*
   by Bruce Schneier

-  *"*\ `Internet Census
   2012 <http://census2012.sourceforge.net/paper.html>`__\ *"* by Carna
   Botnet

-  *"*\ `Security Issues for Embedded
   Devices <https://elinux.org/images/6/6f/Security-issues.pdf>`__\ *"*
   by Jake Edge

When securing your image is of concern, there are steps, tools, and
variables that you can consider to help you reach the security goals you
need for your particular device. Not all situations are identical when
it comes to making an image secure. Consequently, this section provides
some guidance and suggestions for consideration when you want to make
your image more secure.

.. note::

   Because the security requirements and risks are different for every
   type of device, this section cannot provide a complete reference on
   securing your custom OS. It is strongly recommended that you also
   consult other sources of information on embedded Linux system
   hardening and on security.

General Considerations
======================

There are general considerations that help you create more secure images.
You should consider the following suggestions to make your device
more secure:

-  Scan additional code you are adding to the system (e.g. application
   code) by using static analysis tools. Look for buffer overflows and
   other potential security problems.

-  Pay particular attention to the security for any web-based
   administration interface.

   Web interfaces typically need to perform administrative functions and
   tend to need to run with elevated privileges. Thus, the consequences
   resulting from the interface's security becoming compromised can be
   serious. Look for common web vulnerabilities such as
   cross-site-scripting (XSS), unvalidated inputs, and so forth.

   As with system passwords, the default credentials for accessing a
   web-based interface should not be the same across all devices. This
   is particularly true if the interface is enabled by default as it can
   be assumed that many end-users will not change the credentials.

-  Ensure you can update the software on the device to mitigate
   vulnerabilities discovered in the future. This consideration
   especially applies when your device is network-enabled.

-  Regularly scan and apply fixes for CVE security issues affecting
   all software components in the product, see ":ref:`dev-manual/vulnerabilities:checking for vulnerabilities`".

-  Regularly update your version of Poky and OE-Core from their upstream
   developers, e.g. to apply updates and security fixes from stable
   and :term:`LTS` branches.

-  Ensure you remove or disable debugging functionality before producing
   the final image. For information on how to do this, see the
   ":ref:`dev-manual/securing-images:considerations specific to the openembedded build system`"
   section.

-  Ensure you have no network services listening that are not needed.

-  Remove any software from the image that is not needed.

-  Enable hardware support for secure boot functionality when your
   device supports this functionality.

Security Flags
==============

The Yocto Project has security flags that you can enable that help make
your build output more secure. The security flags are in the
``meta/conf/distro/include/security_flags.inc`` file in your
:term:`Source Directory` (e.g. ``poky``).

.. note::

   Depending on the recipe, certain security flags are enabled and
   disabled by default.

Use the following line in your ``local.conf`` file or in your custom
distribution configuration file to enable the security compiler and
linker flags for your build::

   require conf/distro/include/security_flags.inc

Considerations Specific to the OpenEmbedded Build System
========================================================

You can take some steps that are specific to the OpenEmbedded build
system to make your images more secure:

-  Ensure "debug-tweaks" is not one of your selected
   :term:`IMAGE_FEATURES`.
   When creating a new project, the default is to provide you with an
   initial ``local.conf`` file that enables this feature using the
   :term:`EXTRA_IMAGE_FEATURES`
   variable with the line::

      EXTRA_IMAGE_FEATURES = "debug-tweaks"

   To disable that feature, simply comment out that line in your
   ``local.conf`` file, or make sure :term:`IMAGE_FEATURES` does not contain
   "debug-tweaks" before producing your final image. Among other things,
   leaving this in place sets the root password as blank, which makes
   logging in for debugging or inspection easy during development but
   also means anyone can easily log in during production.

-  It is possible to set a root password for the image and also to set
   passwords for any extra users you might add (e.g. administrative or
   service type users). When you set up passwords for multiple images or
   users, you should not duplicate passwords.

   To set up passwords, use the :ref:`ref-classes-extrausers` class, which
   is the preferred method. For an example on how to set up both root and
   user passwords, see the ":ref:`ref-classes-extrausers`" section.

   .. note::

      When adding extra user accounts or setting a root password, be
      cautious about setting the same password on every device. If you
      do this, and the password you have set is exposed, then every
      device is now potentially compromised. If you need this access but
      want to ensure security, consider setting a different, random
      password for each device. Typically, you do this as a separate
      step after you deploy the image onto the device.

-  Consider enabling a Mandatory Access Control (MAC) framework such as
   SMACK or SELinux and tuning it appropriately for your device's usage.
   You can find more information in the
   :yocto_git:`meta-selinux </meta-selinux/>` layer.

Tools for Hardening Your Image
==============================

The Yocto Project provides tools for making your image more secure. You
can find these tools in the ``meta-security`` layer of the
:yocto_git:`Yocto Project Source Repositories <>`.

