DESCRIPTION = "unittest subTest() support and subtests fixture."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=242b4e17fa287dcf7aef372f6bc3dcb1"

SRC_URI[sha256sum] = "5bd1e4bf0eda4c89a6cd42b0ee28e1d2ca0848de3fd67ad8cdd6d559ed00f120"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
