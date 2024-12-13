SUMMARY = "Filesystem events monitoring"
DEPENDS = "python3-argh"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "dcebf7e475001d2cdeb020be630dc5b687e9acdd60d16fea6bb4508e7b94cf76"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-argh \
    python3-pathtools3 \
    python3-pyyaml \
    python3-requests \
"

BBCLASSEXTEND = "native nativesdk"
