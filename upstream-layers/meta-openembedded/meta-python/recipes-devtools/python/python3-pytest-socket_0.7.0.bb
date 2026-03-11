SUMMARY = "Pytest Plugin to disable socket calls during tests"
HOMEPAGE = "https://github.com/miketheman/pytest-socket"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1752f63a3a8a02fed42e580e9b94a081"

SRC_URI[sha256sum] = "71ab048cbbcb085c15a4423b73b619a8b35d6a307f46f78ea46be51b1b7e11b3"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "python3-pytest"

BBCLASSEXTEND = "native nativesdk"

PYPI_PACKAGE = "pytest_socket"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
