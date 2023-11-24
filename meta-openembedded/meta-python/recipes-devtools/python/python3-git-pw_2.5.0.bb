SUMMARY = "A tool for integrating Git with Patchwork"
HOMEPAGE = "https://github.com/getpatchwork/git-pw"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=26e1a171d943c64f00c51f90c256b9d4"

SRC_URI[sha256sum] = "c60169f9566bd6710f9c0985a005a0c326460b739d3f2b5c5c71e85211584590"

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
