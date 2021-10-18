SUMMARY = "PECI Library"
DESCRIPTION = "PECI Library"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake

SRC_URI = "git://github.com/openbmc/libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "03d7dae24d74f35d5703da74931938fcab7467d3"

S = "${WORKDIR}/git"
