DESCRIPTION = "Jalali implementation of Python's datetime module"
HOMEPAGE = "https://github.com/slashmili/python-jalali"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c80be45b33471b4a23cf53d06a8172be"

SRC_URI[sha256sum] = "25af1f48d1456b9a0acc7b32c68c66e9cccbae541672f9b9724f274d7810a307"

PYPI_PACKAGE = "jdatetime"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        python3-modules \
"

