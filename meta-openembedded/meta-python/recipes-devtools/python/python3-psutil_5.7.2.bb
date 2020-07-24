SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e35fd9f271d19d5f742f20a9d1f8bb8b"

PYPI_SRC_URI = "https://files.pythonhosted.org/packages/aa/3e/d18f2c04cf2b528e18515999b0c8e698c136db78f62df34eee89cee205f1/psutil-${PV}.tar.gz"
SRC_URI[md5sum] = "50bf39b4faa268ba1da8a9730bb38bb6"
SRC_URI[sha256sum] = "90990af1c3c67195c44c9a889184f84f5b2320dce3ee3acbd054e3ba0b4a7beb"

inherit pypi setuptools3

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/test* \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/__pycache__/test* \
"


RDEPENDS_${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-xml \
    ${PYTHON_PN}-netclient \
"

BBCLASSEXTEND = "native nativesdk"
