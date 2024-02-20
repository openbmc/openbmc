SUMMARY = "FlatCC FlatBuffers in C for C"
DESCRIPTION = "FlatCC is a compiler that generates FlatBuffers code for C \
given a FlatBuffer schema file."
HOMEPAGE = "https://github.com/dvidelabs/flatcc"
SECTION = "devel/lib"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3d8fb7158bf7e2600ba3191428dc4ef"

PV = "0.6.2+git"

SRC_URI = " \
           git://github.com/dvidelabs/flatcc.git;protocol=https;branch=master \
           file://0001-Check-for-C-standard-version-23-for-__fallthrough__.patch \
"
SRCREV = "1653ec964730ec7d9892a08a1695ada6d20f5196"

S = "${WORKDIR}/git"

inherit cmake

# Enable installation for target
# Disable tests as is not possible to execute with cross-compilation
EXTRA_OECMAKE += " \
    -DFLATCC_INSTALL=On \
    -DFLATCC_TEST=Off \
    -DFLATCC_ALLOW_WERROR=Off \
    -DFLATCC_INSTALL_LIB=${baselib} \
"

BBCLASSEXTEND = "native nativesdk"
