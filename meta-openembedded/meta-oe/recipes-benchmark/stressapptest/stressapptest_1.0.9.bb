SUMMARY = "Stressful Application Test"
DESCRIPTION = "Stressful Application Test (or stressapptest, its unix name) \
 is a memory interface test. It tries to maximize randomized traffic to memory \
 from processor and I/O, with the intent of creating a realistic high load \
 situation in order to test the existing hardware devices in a computer. \
"
HOMEPAGE = "https://github.com/stressapptest/stressapptest"
SECTION = "benchmark"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ea9d559f985fb4834317d8ed6b9e58"

SRCREV = "9146a8bfe3e3daefa95f7a61b75183e5fc64af2c"

PV .= "+1.0.10git${SRCPV}"

EXTRA_AUTOCONF:append:armv7a = " --with-cpu=armv7a"
EXTRA_AUTOCONF:append:armv7ve = " --with-cpu=armv7a"

GI_DATA_ENABLED:libc-musl:armv7a = "False"
GI_DATA_ENABLED:libc-musl:armv7ve = "False"
SRC_URI = "git://github.com/stressapptest/stressapptest;branch=master;protocol=https \
           file://libcplusplus-compat.patch \
           file://read_sysfs_for_cachesize.patch \
           file://0001-configure-Add-with-cpu.patch \
           file://0002-Replace-lfs64-functions-and-defines.patch \
           file://0003-configure-Check-for-pthread_rwlockattr_setkind_np-be.patch \
           "

S = "${WORKDIR}/git"

inherit autotools
