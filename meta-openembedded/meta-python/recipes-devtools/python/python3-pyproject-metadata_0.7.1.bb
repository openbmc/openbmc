SUMMARY = "PEP 621 metadata parsing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=310439af287b0fb4780b2ad6907c256c"

PYPI_PACKAGE = "pyproject-metadata"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "0a94f18b108b9b21f3a26a3d541f056c34edcb17dc872a144a15618fed7aef67"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
    python3-logging \
    python3-packaging \
    python3-profile \
"
