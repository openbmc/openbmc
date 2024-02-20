DESCRIPTION="Protocol Buffers with small code size"
LICENSE="Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

SRC_URI = "git://github.com/nanopb/nanopb.git;branch=master;protocol=https"
SRCREV = "6cfe48d6f1593f8fa5c0f90437f5e6522587745e"

S = "${WORKDIR}/git"

inherit cmake

BBCLASSEXTEND = "native nativesdk"

