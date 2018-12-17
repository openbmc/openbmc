SUMMARY = "Python implementation of subunit test streaming protocol"
HOMEPAGE = "https://pypi.python.org/pypi/python-subunit/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;beginline=1;endline=20;md5=909c08e291647fd985fbe5d9836d51b6"

PYPI_PACKAGE = "python-subunit"

SRC_URI[md5sum] = "16d468a3aeafe6c60a0c3b2b9132d65b"
SRC_URI[sha256sum] = "9607edbee4c1e5a30ff88549ce8d9feb0b9bcbcb5e55033a9d76e86075465cbb"

inherit pypi

BBCLASSEXTEND = "nativesdk"
