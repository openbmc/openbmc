SUMMARY = "RAR archive reader for Python"
HOMEPAGE = "https://github.com/markokr/rarfile"
LICENSE = "ISC"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1916695551f7eec48dfd97db9467b831"

inherit setuptools3

SRC_URI[sha256sum] = "8e1c8e72d0845ad2b32a47ab11a719bc2e41165ec101fd4d3fe9e92aa3f469ef"

inherit pypi

PYPI_PACKAGE = "rarfile"

RDEPENDS:${PN} += "\
    7zip \
    python3-core \
    python3-datetime \
    python3-crypt \
    python3-io \
"

BBCLASSEXTEND = "native nativesdk"
