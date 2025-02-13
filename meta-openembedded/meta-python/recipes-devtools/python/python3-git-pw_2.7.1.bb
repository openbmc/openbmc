SUMMARY = "A tool for integrating Git with Patchwork"
HOMEPAGE = "https://github.com/getpatchwork/git-pw"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=26e1a171d943c64f00c51f90c256b9d4"

SRC_URI[sha256sum] = "f69c57aafd13d21d6fa604dff680c4f9113a8f31bf3f65dd663bad0e1839b0e1"
PYPI_PACKAGE = "git_pw"

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
