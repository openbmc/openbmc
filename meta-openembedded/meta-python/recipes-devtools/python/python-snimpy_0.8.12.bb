SUMMARY = "Interactive SNMP tool"
DESCRIPTION = "Snimpy is a Python-based tool providing a simple interface to build SNMP query"
HOMEPAGE = "https://pypi.python.org/pypi/snimpy"
SECTION = "devel/python"

DEPENDS += "libsmi python-cffi-native python-vcversioner-native"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=7c53ab2d1240828625c3e093d049d4f4"

SRC_URI[md5sum] = "6d016b6356db432e6a87ad708eb8fcb6"
SRC_URI[sha256sum] = "04efcfba867ffb0e10cc8d7f93a553ceb6bdf2ee34e49762749028a2c76096c1"

inherit pypi setuptools

RDEPENDS_${PN} = "python-cffi \
                  python-pycparser \
                  python-pysnmp \
                  python-setuptools \
"
