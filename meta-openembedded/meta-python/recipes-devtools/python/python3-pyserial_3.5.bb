SUMMARY = "Serial Port Support for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=520e45e59fc2cf94aa53850f46b86436"

SRC_URI[sha256sum] = "3c77e014170dfffbd816e6ffc205e9842efb10be9f58ec16d3e8675b4925cddb"

inherit pypi setuptools3 ptest-python-pytest

PTEST_PYTEST_DIR = "test"

do_install:append() {
    rm -f ${D}${bindir}/pyserial-miniterm
    rm -f ${D}${bindir}/pyserial-ports
    rm -rf ${D}${bindir}/__pycache__
    rmdir ${D}${bindir}
}

PACKAGES =+ "${PN}-java ${PN}-osx ${PN}-win32 ${PN}-tools"

FILES:${PN}-java = " \
    ${PYTHON_SITEPACKAGES_DIR}/serial/*java* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/__pycache__/*java* \
"

FILES:${PN}-osx = " \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/*osx* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/__pycache__/*osx* \
"

FILES:${PN}-win32 = " \
    ${PYTHON_SITEPACKAGES_DIR}/serial/*serialcli* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/__pycache__/*serialcli* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/*win32* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/__pycache__/*win32* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/miniterm* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/__pycache__/miniterm* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/*windows* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/__pycache__/*windows* \
"

RDEPENDS:${PN} = "\
    python3-fcntl \
    python3-io \
    python3-logging \
    python3-netclient \
    python3-numbers \
    python3-shell \
    python3-stringold \
    python3-threading \
"

BBCLASSEXTEND = "native nativesdk"

