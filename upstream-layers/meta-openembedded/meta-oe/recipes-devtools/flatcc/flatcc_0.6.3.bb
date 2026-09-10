SUMMARY = "FlatCC FlatBuffers in C for C"
DESCRIPTION = "FlatCC is a compiler that generates FlatBuffers code for C \
given a FlatBuffer schema file."
HOMEPAGE = "https://github.com/dvidelabs/flatcc"
SECTION = "devel/lib"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3d8fb7158bf7e2600ba3191428dc4ef"

SRC_URI = "git://github.com/dvidelabs/flatcc.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "503799885b5517ea9d316c17e35471178c09e35a"

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
