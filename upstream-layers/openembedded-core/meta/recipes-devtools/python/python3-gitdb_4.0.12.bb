SUMMARY = "A pure-Python git object database"
HOMEPAGE = "http://github.com/gitpython-developers/gitdb"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=59e5ecb13339a936eedf83282eaf4528"

DEPENDS = "python3-smmap"

inherit pypi setuptools3

PYPI_PACKAGE = "gitdb"

SRC_URI[sha256sum] = "5ef71f855d191a3326fcfbc0d5da835f26b13fbcba60c32c21091c349ffdb571"

RDEPENDS:${PN} += "python3-compression \
                   python3-crypt \
                   python3-io \
                   python3-mmap \
                   python3-shell \
                   python3-smmap \
"
BBCLASSEXTEND = "native nativesdk"
