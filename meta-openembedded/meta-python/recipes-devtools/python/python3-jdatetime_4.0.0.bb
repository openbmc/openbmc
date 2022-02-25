DESCRIPTION = "Jalali implementation of Python's datetime module"
HOMEPAGE = "https://github.com/slashmili/python-jalali"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c80be45b33471b4a23cf53d06a8172be"

SRC_URI[sha256sum] = "d35baea2ed213e4e87bb840c61637001540bd21e8e4454bd12352b06591ec08e"

PYPI_PACKAGE = "jdatetime"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        ${PYTHON_PN}-modules \
"

