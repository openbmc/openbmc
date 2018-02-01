SUMMARY = "Interactive SNMP tool"
DESCRIPTION = "Snimpy is a Python-based tool providing a simple interface to build SNMP query"
HOMEPAGE = "https://pypi.python.org/pypi/snimpy"
SECTION = "devel/python"

DEPENDS = "libsmi python-cffi-native python-vcversioner-native"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=7c53ab2d1240828625c3e093d049d4f4"

SRCNAME = "snimpy"

SRC_URI = "https://files.pythonhosted.org/packages/source/s/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "7f6270ce2e7206df165602e66d5ceb96"
SRC_URI[sha256sum] = "10410614c7bff1053ca65bd760ce919f1a074f4988b857df4c57cf35847922b0"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} = "python-cffi \
                  python-pycparser \
                  python-pysnmp \
                  python-setuptools \
"
