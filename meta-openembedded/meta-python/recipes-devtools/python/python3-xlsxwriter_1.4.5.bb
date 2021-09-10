SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4e95e368cd6cb090815046688e92d11e"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

RDEPENDS:${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-compression \
	python3-numbers \
	python3-io \
"

SRC_URI[sha256sum] = "0956747859567ec01907e561a7d8413de18a7aae36860f979f9da52b9d58bc19"

BBCLASSEXTEND = "native nativesdk"
