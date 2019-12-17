SUMMARY = "Interactive SNMP tool"
DESCRIPTION = "Snimpy is a Python-based tool providing a simple interface to build SNMP query"
HOMEPAGE = "https://pypi.python.org/pypi/snimpy"
SECTION = "devel/python"

DEPENDS += "libsmi python-cffi-native python-vcversioner-native"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=7c53ab2d1240828625c3e093d049d4f4"

SRC_URI[md5sum] = "7c57acac10226df5bf43e10b929942df"
SRC_URI[sha256sum] = "98b1790977b435332c03ab2603f6621eeeee69a50453ac01ca55dc7696d08535"

inherit pypi setuptools

RDEPENDS_${PN} = "python-cffi \
                  python-pycparser \
                  python-pysnmp \
                  python-setuptools \
"
