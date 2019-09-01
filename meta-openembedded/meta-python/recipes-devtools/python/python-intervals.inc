DESCRIPTION = "Interval arithmetic for Python"
HOMEPAGE = "https://github.com/AlexandreDecan/python-intervals"
SECTION = "devel/python"

LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

PYPI_PACKAGE := "python-intervals"

inherit pypi

SRC_URI[md5sum] = "a35dd44269fdb134bf19cfd80cb2c120"
SRC_URI[sha256sum] = "a8a15b7c2fadb8465310f44aa96e384828b35a29de38ac74738cd665924cb8ac"

BBCLASSEXTEND = "native"
