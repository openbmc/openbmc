SUMMARY = "Filesystem events monitoring"
DEPENDS = "python3-argh"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "e3e7065cbdabe6183ab82199d7a4f6b3ba0a438c5a512a68559846ccb76a78ec"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-argh \
    python3-pathtools3 \
    python3-pyyaml \
    python3-requests \
"

BBCLASSEXTEND = "native nativesdk"
