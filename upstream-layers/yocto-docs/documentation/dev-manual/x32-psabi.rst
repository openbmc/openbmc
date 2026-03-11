.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Using x32 psABI
***************

x32 processor-specific Application Binary Interface (`x32
psABI <https://software.intel.com/en-us/node/628948>`__) is a native
32-bit processor-specific ABI for Intel 64 (x86-64) architectures. An
ABI defines the calling conventions between functions in a processing
environment. The interface determines what registers are used and what
the sizes are for various C data types.

Some processing environments prefer using 32-bit applications even when
running on Intel 64-bit platforms. Consider the i386 psABI, which is a
very old 32-bit ABI for Intel 64-bit platforms. The i386 psABI does not
provide efficient use and access of the Intel 64-bit processor
resources, leaving the system underutilized. Now consider the x86_64
psABI. This ABI is newer and uses 64-bits for data sizes and program
pointers. The extra bits increase the footprint size of the programs,
libraries, and also increases the memory and file system size
requirements. Executing under the x32 psABI enables user programs to
utilize CPU and system resources more efficiently while keeping the
memory footprint of the applications low. Extra bits are used for
registers but not for addressing mechanisms.

The Yocto Project supports the final specifications of x32 psABI as
follows:

-  You can create packages and images in x32 psABI format on x86_64
   architecture targets.

-  You can successfully build recipes with the x32 toolchain.

-  You can create and boot ``core-image-minimal`` and
   ``core-image-sato`` images.

-  There is RPM Package Manager (RPM) support for x32 binaries.

-  There is support for large images.

To use the x32 psABI, you need to edit your ``conf/local.conf``
configuration file as follows::

   MACHINE = "qemux86-64"
   DEFAULTTUNE = "x86-64-x32"
   baselib = "${@d.getVar('BASE_LIB:tune-' + (d.getVar('DEFAULTTUNE') \
       or 'INVALID')) or 'lib'}"

Once you have set
up your configuration file, use BitBake to build an image that supports
the x32 psABI. Here is an example::

   $ bitbake core-image-sato

