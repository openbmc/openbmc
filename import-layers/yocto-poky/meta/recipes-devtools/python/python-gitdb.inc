SUMMARY = "A pure-Python git object database"
HOMEPAGE = "http://github.com/gitpython-developers/gitdb"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=59e5ecb13339a936eedf83282eaf4528"

SRC_URI = "https://files.pythonhosted.org/packages/source/g/gitdb/gitdb-${PV}.tar.gz"

SRC_URI[md5sum] = "44e4366b8bdfd306b075c3a52c96ae1a"
SRC_URI[sha256sum] = "a3ebbc27be035a2e874ed904df516e35f4a29a778a764385de09de9e0f139658"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/gitdb/"

S = "${WORKDIR}/gitdb-${PV}"

BBCLASSEXTEND = "nativesdk"
