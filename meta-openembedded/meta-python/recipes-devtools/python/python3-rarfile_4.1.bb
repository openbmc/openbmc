SUMMARY = "RAR archive reader for Python"
HOMEPAGE = "https://github.com/markokr/rarfile"
LICENSE = "ISC"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2f31e224cbf0c29cb6c55f2bae0e165f"

inherit setuptools3

SRC_URI[sha256sum] = "db60b3b5bc1c4bdeb941427d50b606d51df677353385255583847639473eda48"

inherit pypi

PYPI_PACKAGE="rarfile"

RDEPENDS:${PN} += "\
    p7zip \
    python3-core \
    python3-datetime \
    python3-crypt \
    python3-io \
"

BBCLASSEXTEND = "native nativesdk"
