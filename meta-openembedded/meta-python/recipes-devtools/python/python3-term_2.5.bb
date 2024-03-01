SUMMARY = "An enhanced version of the tty module"
SECTION = "devel/python"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d90e2d280a4836c607520383d1639be1"

SRC_URI[sha256sum] = "846fda941dbb8a7c9f246e99bf5ee731910fc4a4cc54b7e36457c133c9f6b78b"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    python3-io \
"
