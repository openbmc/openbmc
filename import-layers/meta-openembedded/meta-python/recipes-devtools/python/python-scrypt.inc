DESCRIPTION = "Bindings for the scrypt key derivation function library"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=8bef8e6712b1be5aa76af1ebde9d6378"
HOMEPAGE="https://bitbucket.org/mhallin/py-scrypt/overview"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-scrypt:"

SRC_URI += "file://0001-py-scrypt-remove-the-hard-coded-include-paths.patch"

SRC_URI[md5sum] = "ae8e3263aa31b040c1f9c7f1e1843a56"
SRC_URI[sha256sum] = "f8239b2d47fa1d40bc27efd231dc7083695d10c1c2ac51a99380360741e0362d"

inherit pypi

RDEPENDS_${PN}_class-target += "\
"
