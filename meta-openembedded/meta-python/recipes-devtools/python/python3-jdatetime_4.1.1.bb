DESCRIPTION = "Jalali implementation of Python's datetime module"
HOMEPAGE = "https://github.com/slashmili/python-jalali"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c80be45b33471b4a23cf53d06a8172be"

SRC_URI[sha256sum] = "1dd0ee210160c7bd30002803c443e6260ac602ea65b065652a1d567d3bfdca7a"

PYPI_PACKAGE = "jdatetime"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        ${PYTHON_PN}-modules \
"

