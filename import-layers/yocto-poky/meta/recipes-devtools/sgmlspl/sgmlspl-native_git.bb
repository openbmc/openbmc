SUMMARY = "A simple post-processor for SGMLS and NSGMLS"
HOMEPAGE = "https://github.com/gitpan/SGMLSpm"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"
SRC_URI = "git://github.com/gitpan/SGMLSpm \
          "

SRCREV = "71595b9b5e36bfc00046995e058926bd27793fef"

PV = "1.1+git${SRCPV}"

S = "${WORKDIR}/git"

inherit native cpan

do_install_append() {
    ln -s sgmlspl.pl ${D}${bindir}/sgmlspl
}
