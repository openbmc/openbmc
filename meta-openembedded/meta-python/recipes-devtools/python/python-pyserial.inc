SUMMARY = "Serial Port Support for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d476d94926db6e0008a5b3860d1f5c0d"

SRC_URI[md5sum] = "ed6183b15519a0ae96675e9c3330c69b"
SRC_URI[sha256sum] = "6e2d401fdee0eab996cf734e67773a0143b932772ca8b42451440cfed942c627"

inherit pypi

PACKAGES =+ "${PN}-java ${PN}-osx ${PN}-win32 ${PN}-tools"

FILES_${PN}-java = " \
    ${PYTHON_SITEPACKAGES_DIR}/serial/*java* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/__pycache__/*java* \
"

FILES_${PN}-osx = " \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/*osx* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/__pycache__/*osx* \
"

FILES_${PN}-win32 = " \
    ${PYTHON_SITEPACKAGES_DIR}/serial/*serialcli* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/__pycache__/*serialcli* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/*win32* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/__pycache__/*win32* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/miniterm* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/__pycache__/miniterm* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/*windows* \
    ${PYTHON_SITEPACKAGES_DIR}/serial/tools/__pycache__/*windows* \
"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"
