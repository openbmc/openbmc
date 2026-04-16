SUMMARY = "EEMBC CoreMark CPU benchmark"
DESCRIPTION = "CoreMark is a CPU benchmark program"
HOMEPAGE = "https://www.eembc.org/coremark/"
LICENSE = "Apache-2.0 & LicenseRef-EEMBC-AUA"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=0a18b17ae63deaa8a595035f668aebe1"

SRC_URI = "git://github.com/eembc/coremark.git;branch=main;protocol=https"
SRCREV = "1f483d5b8316753a742cbf5590caf5bd0a4e4777"

inherit pkgconfig

do_configure[noexec] = "1"

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile() {
    oe_runmake compile
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/coremark.exe ${D}${bindir}/coremark
}
