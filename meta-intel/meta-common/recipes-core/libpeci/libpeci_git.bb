SUMMARY = "PECI Library"
DESCRIPTION = "PECI Library"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake

SRC_URI = "git://github.com/openbmc/libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "c965e72c6765e054531c1ab91e7fa13f04651f21"

S = "${WORKDIR}/git"
