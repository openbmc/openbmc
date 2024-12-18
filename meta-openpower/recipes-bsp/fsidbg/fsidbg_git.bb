SUMMARY     = "FSI debug tool"
DESCRIPTION = "fsidbg is a tool to access remote FSI engines and perform client driver operations"
LICENSE     = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

SRC_URI = "git://github.com/eddiejames/fsidbg.git;branch=master;protocol=https"

SRCREV = "dfe278065c877724242dfae15a4c627fd2e3611c"
PV = "git${SRCREV}"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 fsidbg ${D}${bindir}
}

TARGET_CC_ARCH += "${LDFLAGS}"
