SUMMARY = "Apply values to optional params"
HOMEPAGE = "https://github.com/bcb/apply_defaults"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c89120516900f96f4c60d35fdc4c3f15"

PYPI_PACKAGE = "apply_defaults"

SRC_URI[sha256sum] = "3773de3491b94c0fe44310f1a85888389cdc71e1544b343bce0d2bd6991acea5"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"
