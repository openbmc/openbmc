require recipes-bsp/u-boot/u-boot.inc

# To build u-boot for your machine, provide the following lines in your machine
# config, replacing the assignments as appropriate for your machine.
# UBOOT_MACHINE = "omap3_beagle_config"
# UBOOT_ENTRYPOINT = "0x80008000"
# UBOOT_LOADADDRESS = "0x80008000"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb \
                    file://README;beginline=1;endline=22;md5=78b195c11cb6ef63e6985140db7d7bab"

# This revision corresponds to the tag "v2013.07"
# We use the revision in order to avoid having to fetch it from the repo during parse
# SRCREV = "62c175fbb8a0f9a926c88294ea9f7e88eb898f6c"
SRCREV="${AUTOREV}"


PV = "v2013.07+git${SRCPV}"

UBRANCH = "v2013.07-aspeed-openbmc"
SRC_URI = "git://git@github.com/openbmc/u-boot.git;branch=${UBRANCH};protocol=https"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
