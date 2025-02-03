DESCRIPTION = "Protocol Buffers with small code size"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

SRC_URI = "git://github.com/nanopb/nanopb.git;branch=maintenance_0.4;protocol=https"
SRCREV = "cad3c18ef15a663e30e3e43e3a752b66378adec1"

S = "${WORKDIR}/git"

inherit cmake

BBCLASSEXTEND = "native nativesdk"

