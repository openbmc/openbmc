DESCRIPTION = "Jalali implementation of Python's datetime module"
HOMEPAGE = "https://github.com/slashmili/python-jalali"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c80be45b33471b4a23cf53d06a8172be"

SRC_URI[sha256sum] = "c81d5898717b82b609a3ce2a73f8b8d3230b0c757e5c0de9d6b1acfdc224f551"

PYPI_PACKAGE = "jdatetime"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        python3-modules \
"

