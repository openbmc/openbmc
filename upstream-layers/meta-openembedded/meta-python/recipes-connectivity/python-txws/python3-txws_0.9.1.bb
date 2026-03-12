SUMMARY = "Twisted Web Sockets"
HOMEPAGE = "https://github.com/MostAwesomeDude/txWS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76699830db7fa9e897f6a1ad05f98ec8"

DEPENDS = "python3-twisted python3-six python3-vcversioner python3-six-native python3-vcversioner-native"

RDEPENDS:${PN} += " \
    python3-six \
    python3-twisted \
"

inherit setuptools3 pypi

PYPI_PACKAGE = "txWS"

SRC_URI[sha256sum] = "cb93086095d04a5d70f53a75053f7df478ff37e972c3637fb55ca4a9e6b94679"
