SUMMARY = "Argcomplete provides easy, extensible command line tab completion of arguments for your Python script."
HOMEPAGE = "https://github.com/kislyuk/argcomplete"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[md5sum] = "2262d0466a40d42267b424bba5010a0b"
SRC_URI[sha256sum] = "849c2444c35bb2175aea74100ca5f644c29bf716429399c0f2203bb5d9a8e4e6"

PYPI_PACKAGE = "argcomplete"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-core \
"

BBCLASSEXTEND = "native nativesdk"

