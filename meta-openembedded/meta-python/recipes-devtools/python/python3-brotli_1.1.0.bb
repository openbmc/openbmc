SUMMARY = "Brotli compression format"
HOMEPAGE = "https://pypi.org/project/Brotli/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=941ee9cd1609382f946352712a319b4b"

PYPI_PACKAGE = "Brotli"

SRC_URI[sha256sum] = "81de08ac11bcb85841e440c13611c00b67d3bf82698314928d0b676362546724"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
  python3-cffi \
"

BBCLASSEXTEND = "native nativesdk"
