SUMMARY = "A tool for integrating Git with Patchwork"
HOMEPAGE = "https://github.com/getpatchwork/git-pw"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=26e1a171d943c64f00c51f90c256b9d4"

SRC_URI[sha256sum] = "e118e31bbe259ed9ae540d627cc563a6ece553d02aed11b1a92bbac408a0da0e"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-pbr-native \
"

RDEPENDS:${PN} += " \
    python3-arrow \
    python3-click \
    python3-pyyaml \
    python3-requests \
    python3-tabulate \
"
