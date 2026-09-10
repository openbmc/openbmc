DESCRIPTION = "Protocol Buffers with small code size"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

SRC_URI = "git://github.com/nanopb/nanopb.git;branch=maintenance_0.4;protocol=https;tag=${PV}"
SRCREV = "160d4f09e5fabb2b66aa2dea32d4f38ace2c4b3f"


inherit cmake

BBCLASSEXTEND = "native nativesdk"

