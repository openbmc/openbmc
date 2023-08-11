SUMMARY = "Send file to trash natively under Mac OS X, Windows and Linux"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a02659c2d5f4cc626e4dcf6504b865eb"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "c132d59fa44b9ca2b1699af5c86f57ce9f4c5eb56629d5d55fbb7a35f84e2312"

PYPI_PACKAGE = "Send2Trash"

RDEPENDS:${PN} += "\
    python3-io \
    python3-datetime \
"
