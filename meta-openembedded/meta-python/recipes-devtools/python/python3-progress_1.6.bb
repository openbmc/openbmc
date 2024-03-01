SUMMARY = "Easy progress reporting for Python"
HOMEPAGE = "http://github.com/verigak/progress/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00ab78a4113b09aacf63d762a7bb9644"

SRC_URI[sha256sum] = "c9c86e98b5c03fa1fe11e3b67c1feda4788b8d0fe7336c2ff7d5644ccfba34cd"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target += " \
    python3-datetime \
    python3-math \
"

BBCLASSEXTEND = "native nativesdk"
