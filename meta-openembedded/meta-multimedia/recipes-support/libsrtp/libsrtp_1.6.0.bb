DESCRIPTION = "library implementing Secure RTP (RFC 3711)"
HOMEPAGE = "https://github.com/cisco/libsrtp"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15bc16b9d2e305974dde47e733883714"

S = "${WORKDIR}/git"
SRCREV = "2b091a4fb21c9b06fc5d4b5763bdaec97542fcd7"
SRC_URI = "git://github.com/cisco/libsrtp.git;protocol=https;branch=1_6_x_throttle; \
           file://0001-Rename-conflicting-variable-mips.patch \
           "

inherit autotools pkgconfig

EXTRA_OEMAKE += "shared_library"

ALLOW_EMPTY_${PN} = "1"
