SUMMARY = "Apply values to optional params"
HOMEPAGE = "https://github.com/bcb/apply_defaults"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c89120516900f96f4c60d35fdc4c3f15"

PYPI_PACKAGE = "apply_defaults"

SRC_URI[md5sum] = "719abb133f4b46283ebd940fcdf30a78"
SRC_URI[sha256sum] = "1ce26326a61d8773d38a9726a345c6525a91a6120d7333af79ad792dacb6246c"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"
