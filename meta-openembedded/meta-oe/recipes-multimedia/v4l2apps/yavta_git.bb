SUMMARY = "Yet Another V4L2 Test Application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://git.ideasonboard.org/yavta.git;branch=master \
          "
SRCREV = "65f740aa1758531fd810339bc1b7d1d33666e28a"

PV = "0.0"
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


