SUMMARY = "Test vectors for the cryptography package."
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"

DEPENDS = " \
    ${PYTHON_PN}-cryptography \
"

SRC_URI[md5sum] = "b8555dfadafc4ecab4ee4650430d9cab"
SRC_URI[sha256sum] = "0728815ef0c53d67fd437aa5220450a9752d41ecb28108f5df628a092ff466ea"

PYPI_PACKAGE = "cryptography_vectors"

BBCLASSEXTEND = "native nativesdk"
