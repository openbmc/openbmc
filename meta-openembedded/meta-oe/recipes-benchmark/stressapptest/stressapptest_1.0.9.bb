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

SRCREV = "fb72e5e5f0879231f38e0e826a98a6ca2d1ca38e"

SRC_URI = "git://github.com/stressapptest/stressapptest;branch=master;protocol=https \
           file://libcplusplus-compat.patch \
           file://read_sysfs_for_cachesize.patch \
          "

S = "${WORKDIR}/git"

inherit autotools
