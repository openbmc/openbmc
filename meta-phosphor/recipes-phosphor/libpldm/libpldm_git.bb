SUMMARY = "libpldm shared library"
DESCRIPTION = "PLDM library implementing various PLDM specifications"
HOMEPAGE = "https://github.com/openbmc/libpldm"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "6fd81a518d8a021009101038b0d80092f14a9028"
PACKAGECONFIG[oem-ibm] = "-Doem-ibm=enabled,-Doem-ibm=disabled,,"

PV = "0.1.0+git${SRCPV}"
PR = "r1"
SRC_URI = "git://github.com/openbmc/libpldm;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit meson

EXTRA_OEMESON:append = " -Dtests=disabled"
