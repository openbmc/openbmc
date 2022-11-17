SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7d2e9d24c2b5bad57ca894da972e22e"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native ${PYTHON_PN}-pip-native"

SRC_URI[sha256sum] = "c219a00245af0f6fa4e95901ed28044544f50152840c5b6a3e7b2568db34d156"

inherit pypi setuptools3
