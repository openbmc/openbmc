SUMMARY = "A pure-Python git object database"
HOMEPAGE = "http://github.com/gitpython-developers/gitdb"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=59e5ecb13339a936eedf83282eaf4528"

DEPENDS = "python3-smmap"

inherit pypi setuptools3

PYPI_PACKAGE = "gitdb"

SRC_URI[sha256sum] = "6eb990b69df4e15bad899ea868dc46572c3f75339735663b81de79b06f17eb9a"

RDEPENDS:${PN} += "python3-compression \
                   python3-crypt \
                   python3-io \
                   python3-mmap \
                   python3-shell \
                   python3-smmap \
"
BBCLASSEXTEND = "native nativesdk"
