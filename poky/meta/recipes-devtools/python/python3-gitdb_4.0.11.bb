SUMMARY = "A pure-Python git object database"
HOMEPAGE = "http://github.com/gitpython-developers/gitdb"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=59e5ecb13339a936eedf83282eaf4528"

DEPENDS = "python3-smmap"

inherit pypi setuptools3

PYPI_PACKAGE = "gitdb"

SRC_URI[sha256sum] = "bf5421126136d6d0af55bc1e7c1af1c397a34f5b7bd79e776cd3e89785c2b04b"

RDEPENDS:${PN} += "python3-compression \
                   python3-crypt \
                   python3-io \
                   python3-mmap \
                   python3-shell \
                   python3-smmap \
"
BBCLASSEXTEND = "native nativesdk"
