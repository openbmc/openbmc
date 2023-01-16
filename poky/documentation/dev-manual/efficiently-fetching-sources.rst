.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Efficiently Fetching Source Files During a Build
************************************************

The OpenEmbedded build system works with source files located through
the :term:`SRC_URI` variable. When
you build something using BitBake, a big part of the operation is
locating and downloading all the source tarballs. For images,
downloading all the source for various packages can take a significant
amount of time.

This section shows you how you can use mirrors to speed up fetching
source files and how you can pre-fetch files all of which leads to more
efficient use of resources and time.

Setting up Effective Mirrors
============================

A good deal that goes into a Yocto Project build is simply downloading
all of the source tarballs. Maybe you have been working with another
build system for which you have built up a
sizable directory of source tarballs. Or, perhaps someone else has such
a directory for which you have read access. If so, you can save time by
adding statements to your configuration file so that the build process
checks local directories first for existing tarballs before checking the
Internet.

Here is an efficient way to set it up in your ``local.conf`` file::

   SOURCE_MIRROR_URL ?= "file:///home/you/your-download-dir/"
   INHERIT += "own-mirrors"
   BB_GENERATE_MIRROR_TARBALLS = "1"
   # BB_NO_NETWORK = "1"

In the previous example, the
:term:`BB_GENERATE_MIRROR_TARBALLS`
variable causes the OpenEmbedded build system to generate tarballs of
the Git repositories and store them in the
:term:`DL_DIR` directory. Due to
performance reasons, generating and storing these tarballs is not the
build system's default behavior.

You can also use the
:term:`PREMIRRORS` variable. For
an example, see the variable's glossary entry in the Yocto Project
Reference Manual.

Getting Source Files and Suppressing the Build
==============================================

Another technique you can use to ready yourself for a successive string
of build operations, is to pre-fetch all the source files without
actually starting a build. This technique lets you work through any
download issues and ultimately gathers all the source files into your
download directory :ref:`structure-build-downloads`,
which is located with :term:`DL_DIR`.

Use the following BitBake command form to fetch all the necessary
sources without starting the build::

   $ bitbake target --runall=fetch

This
variation of the BitBake command guarantees that you have all the
sources for that BitBake target should you disconnect from the Internet
and want to do the build later offline.

