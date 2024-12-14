DESCRIPTION="Protocol Buffers with small code size"
LICENSE="Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

SRC_URI = "git://github.com/nanopb/nanopb.git;branch=master;protocol=https"
SRCREV = "98bf4db69897b53434f3d0ba72e0a3ab1a902824"

S = "${WORKDIR}/git"

inherit cmake

BBCLASSEXTEND = "native nativesdk"

