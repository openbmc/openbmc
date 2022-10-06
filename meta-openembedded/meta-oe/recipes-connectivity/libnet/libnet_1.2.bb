SUMMARY = "A packet dissection and creation library"
HOMEPAGE = "https://github.com/libnet/libnet"

SECTION = "libs"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=07f291bf6e78efa05cec668cf6a09acc"

DEPENDS = "libpcap"

SRC_URI = "git://github.com/libnet/libnet.git;protocol=https;branch=master \
	file://0001-Use-standard-int64_t-instead-of-__int64_t-for-mingw-.patch"

SRC_URI[sha256sum] = "1e9e9054d688b059bcbaf878d8c4fbf69bfc0c9386cd4e7779fbb53339050d2e"
SRCREV = "deeeeaeb84f8bc5d2299913d4ccf53d0d4c26966"

S = "${WORKDIR}/git"

inherit autotools binconfig
