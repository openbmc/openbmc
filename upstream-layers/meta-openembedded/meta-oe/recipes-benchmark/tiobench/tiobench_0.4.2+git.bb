SUMMARY = "Threaded I/O tester"
HOMEPAGE = "https://github.com/mkuoppal/tiobench"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

SRC_URI = "git://github.com/mkuoppal/tiobench.git;protocol=https;branch=master \
           file://0001-Allow-large-file-support-with-musl.patch \
           "

SRCREV = "11c965c8c0f37927423fb4ea590df6c093cf2f1e"

EXTRA_OEMAKE = "PREFIX=${prefix} CFLAGS='${CFLAGS}'"

do_install() {
    install -Dm 0755 ${S}/tiotest ${D}${bindir}/tiotest
    install -Dm 0755 ${S}/tiobench.pl ${D}${bindir}/tiobench.pl

    install -d ${D}${docdir}/${BPN}
    install -m 0644 ${S}/README.md ${S}/BUGS ${S}/ChangeLog ${S}/TODO ${D}${docdir}/${BPN}
}

RDEPENDS:${PN} += "\
    perl \
    perl-module-getopt-long \
"
