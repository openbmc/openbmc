SUMMARY = "Application to verify operation of serial ports"
HOMEPAGE = "https://github.com/nsekhar/serialcheck"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    git://github.com/nsekhar/serialcheck.git;branch=master;protocol=https \
"

SRCREV = "45eb2ffa5378396e85432872833890b0a1cba872"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS:append:libc-musl = " argp-standalone"
EXTRA_OEMAKE:append:libc-musl = " LIBS='-largp'"

PACKAGE_BEFORE_PN += "${PN}-stats"

do_install() {
    install -d ${D}${bindir}
    install ${B}/serialcheck ${D}${bindir}
    install ${B}/serialstats ${D}${bindir}
    install -d ${D}${docdir}/${BP}
    install ${S}/README ${D}${docdir}/${BP}
}

FILES:${PN}-stats = "${bindir}/serialstats"

BBCLASSEXTEND = "nativesdk"
