SUMMARY = "Yet Another V4L2 Test Application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://git.ideasonboard.org/yavta.git \
           file://0001-Add-stdout-mode-to-allow-streaming-over-the-network-.patch"
SRCREV = "7e9f28bedc1ed3205fb5164f686aea96f27a0de2"

PV = "0.0"
PR = "r2"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "-e MAKEFLAGS="

# The yavta sources include copies of the headers required to build in the
# include directory.  The Makefile uses CFLAGS to include these, but since
# we override the CFLAGS then we need to add this include path back in.
CFLAGS += "-I${S}/include"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 yavta ${D}${bindir}
}


