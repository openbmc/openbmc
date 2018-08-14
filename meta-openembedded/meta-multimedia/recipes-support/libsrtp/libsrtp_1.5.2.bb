DESCRIPTION = "library implementing Secure RTP (RFC 3711)"
HOMEPAGE = "https://github.com/cisco/libsrtp"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15bc16b9d2e305974dde47e733883714"

S = "${WORKDIR}/git"
SRCREV = "e806a66bad35f4b215b7a825c24ef0ec4cc19569"
SRC_URI = "git://github.com/cisco/libsrtp.git;protocol=https; \
           file://0001-Rename-conflicting-variable-mips.patch \
           "

inherit autotools

do_install[postfuncs] += " rm_unused_bindir "

rm_unused_bindir() {
    rmdir ${D}/${bindir}
}

ALLOW_EMPTY_${PN} = "1"
