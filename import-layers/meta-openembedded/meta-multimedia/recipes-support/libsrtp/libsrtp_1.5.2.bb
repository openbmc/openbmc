DESCRIPTION = "library implementing Secure RTP (RFC 3711)"
HOMEPAGE = "https://github.com/cisco/libsrtp"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15bc16b9d2e305974dde47e733883714"

SRC_URI = "https://github.com/cisco/libsrtp/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz \
           file://0001-Rename-conflicting-variable-mips.patch \
           "
SRC_URI[md5sum] = "2309aa6027992810a4285b042c71e644"
SRC_URI[sha256sum] = "86e1efe353397c0751f6bdd709794143bd1b76494412860f16ff2b6d9c304eda"

inherit autotools

do_install[postfuncs] += " rm_unused_bindir "

rm_unused_bindir() {
    rmdir ${D}/${bindir}
}
