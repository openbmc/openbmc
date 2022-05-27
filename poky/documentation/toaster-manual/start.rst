.. SPDX-License-Identifier: CC-BY-SA-2.0-UK
.. Set default pygments highlighting to shell for this document
.. highlight:: shell

************************
Preparing to Use Toaster
************************

This chapter describes how you need to prepare your system in order to
use Toaster.

Setting Up the Basic System Requirements
========================================

Before you can use Toaster, you need to first set up your build system
to run the Yocto Project. To do this, follow the instructions in the
":ref:`dev-manual/start:preparing the build host`" section of
the Yocto Project Development Tasks Manual. For Ubuntu/Debian, you might
also need to do an additional install of pip3. ::

   $ sudo apt install python3-pip

Establishing Toaster System Dependencies
========================================

Toaster requires extra Python dependencies in order to run. A Toaster
requirements file named ``toaster-requirements.txt`` defines the Python
dependencies. The requirements file is located in the ``bitbake``
directory, which is located in the root directory of the
:term:`Source Directory` (e.g.
``poky/bitbake/toaster-requirements.txt``). The dependencies appear in a
``pip``, install-compatible format.

Install Toaster Packages
------------------------

You need to install the packages that Toaster requires. Use this
command::

   $ pip3 install --user -r bitbake/toaster-requirements.txt

The previous command installs the necessary Toaster modules into a local
Python 3 cache in your ``$HOME`` directory. The caches is actually
located in ``$HOME/.local``. To see what packages have been installed
into your ``$HOME`` directory, do the following::

   $ pip3 list installed --local

If you need to remove something, the following works::

   $ pip3 uninstall PackageNameToUninstall
