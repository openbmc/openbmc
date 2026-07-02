DESCRIPTION = "Once installed, the /dev/crash driver will be used by default for live system crash sessions."
SECTION = "devel"
SUMMARY = "/dev/crash driver"
HOMEPAGE = "https://github.com/crash-utility/crash/tree/master/memory_driver"
RECIPE_MAINTAINER = "Robert Berger <Robert.Berger@ReliableEmbeddedSystems.com>"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://crash.c;beginline=1;endline=25;md5=c278eae78e2bf99783849a90f03d0e43"

inherit module

SRCREV = "61fe107ff96a22e7df0029877529b7ce6da36850"
SRC_URI = "git://github.com/crash-utility/crash;protocol=https;branch=master;subpath=memory_driver;destsuffix=${BP} \
           file://0001-hacked-Makefile-for-module.bbclass.patch;striplevel=2 \
           "
PV = "9.0.2+git"

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.

RPROVIDES:${PN} += "kernel-module-crash-memory-driver"

# to local.conf add:
#    to add the kernel module to the rootfs:
# MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-module-crash-memory-driver"
#    to autoload it:
# KERNEL_MODULE_AUTOLOAD += "crash"
