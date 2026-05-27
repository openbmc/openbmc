SUMMARY = "libpldm shared library"
DESCRIPTION = "PLDM library implementing various PLDM specifications"
HOMEPAGE = "https://github.com/openbmc/libpldm"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "ff989ea2ed75176f3b07d964e471f80c038a530b"

LIBPLDM_ABI_DEVELOPMENT = "deprecated,stable,testing"
LIBPLDM_ABI_MAINTENANCE = "stable,testing"
LIBPLDM_ABI_PRODUCTION = "deprecated,stable"
PACKAGECONFIG ??= "abi-production"
PACKAGECONFIG[abi-development] = "-Dabi=${LIBPLDM_ABI_DEVELOPMENT},,,"
PACKAGECONFIG[abi-maintenance] = "-Dabi=${LIBPLDM_ABI_MAINTENANCE},,,"
PACKAGECONFIG[abi-production] = "-Dabi=${LIBPLDM_ABI_PRODUCTION},,,"

LIBPLDM_OEM ??= "ibm,meta"
PACKAGECONFIG[oem] = "-Doem=${LIBPLDM_OEM},-Doem=[],,"

PV = "git${SRCPV}"
PR = "r1"
SRC_URI = "git://github.com/openbmc/libpldm;branch=main;protocol=https"

inherit meson

EXTRA_OEMESON:append = " -Dtests=false"
