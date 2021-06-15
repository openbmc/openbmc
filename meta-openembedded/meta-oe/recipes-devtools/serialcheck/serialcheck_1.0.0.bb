SUMMARY = "Application to verify operation of serial ports"
HOMEPAGE = "https://github.com/nsekhar/serialcheck"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    git://github.com/nsekhar/serialcheck.git \
"

SRCREV = "45eb2ffa5378396e85432872833890b0a1cba872"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS_append_libc-musl = " argp-standalone"
EXTRA_OEMAKE_append_libc-musl = " LIBS='-largp'"

PACKAGE_BEFORE_PN += "${PN}-stats"

do_install() {
    install -d ${D}${bindir}
    install ${B}/serialcheck ${D}${bindir}
    install ${B}/serialstats ${D}${bindir}
    install -d ${D}${docdir}/${BP}
    install ${S}/README ${D}${docdir}/${BP}
}

FILES_${PN}-stats = "${bindir}/serialstats"

BBCLASSEXTEND = "nativesdk"
