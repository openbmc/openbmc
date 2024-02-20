SUMMARY = "A Python SOCKS client module"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d457bcffb9661b45f799d4efee72f16"

SRC_URI[md5sum] = "89b1a6865c61bae67a32417517612ee6"
SRC_URI[sha256sum] = "3f8804571ebe159c380ac6de37643bb4685970655d3bba243530d6558b799aa0"

PYPI_PACKAGE = "PySocks"
inherit pypi setuptools3

RDEPENDS:${PN}:class-target += "\
    python3-email \
    python3-io \
    python3-logging \
    python3-netclient \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
