SUMMARY = "libpldm shared library"
DESCRIPTION = "PLDM library implementing various PLDM specifications"
HOMEPAGE = "https://github.com/openbmc/libpldm"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "297720aa6a0bdf60ace81b30c196e9c04471e5d4"
PACKAGECONFIG[oem-ibm] = "-Doem-ibm=enabled,-Doem-ibm=disabled,,"

PV = "0.1.0+git${SRCPV}"
PR = "r1"
SRC_URI = "git://github.com/openbmc/pldm;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson

EXTRA_OEMESON:append = " -Dtests=disabled -Dlibpldm-only=enabled"
