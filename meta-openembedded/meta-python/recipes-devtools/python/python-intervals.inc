DESCRIPTION = "Interval arithmetic for Python"
HOMEPAGE = "https://github.com/AlexandreDecan/python-intervals"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

PYPI_PACKAGE := "python-intervals"

inherit pypi

SRC_URI[md5sum] = "a35dd44269fdb134bf19cfd80cb2c120"
SRC_URI[sha256sum] = "a8a15b7c2fadb8465310f44aa96e384828b35a29de38ac74738cd665924cb8ac"

BBCLASSEXTEND = "native"
