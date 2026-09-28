SUMMARY = "RAR archive reader for Python"
HOMEPAGE = "https://github.com/markokr/rarfile"
LICENSE = "ISC"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1916695551f7eec48dfd97db9467b831"

inherit setuptools3

SRC_URI[sha256sum] = "b7d29d671223cdc80e8b992df212d152ad88e6c961d4426c3fb2fefe8dd86fdf"

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
