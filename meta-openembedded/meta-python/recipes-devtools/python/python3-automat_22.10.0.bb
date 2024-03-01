DESCRIPTION = "Self-service finite-state machines for the programmer on the go"
HOMEPAGE = "https://github.com/glyph/Automat"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ad213bcca81688e94593e5f60c87477"

SRC_URI[sha256sum] = "e56beb84edad19dcc11d30e8d9b895f75deeb5ef5e96b84a467066b3b84bb04e"

DEPENDS += "python3-setuptools-scm-native"

PYPI_PACKAGE = "Automat"
inherit pypi setuptools3

RDEPENDS:${PN} += "\
   python3-attrs \
   python3-six \
"
