SUMMARY = "Brotli compression format"
HOMEPAGE = "https://pypi.org/project/Brotli/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=941ee9cd1609382f946352712a319b4b"

PYPI_PACKAGE = "Brotli"
PYPI_PACKAGE_EXT = "zip"

SRC_URI[sha256sum] = "4d1b810aa0ed773f81dceda2cc7b403d01057458730e309856356d4ef4188438"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
  ${PYTHON_PN}-cffi \
"

BBCLASSEXTEND = "native nativesdk"
