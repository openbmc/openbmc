SUMMARY = "Keynote tool and library"
DESCRIPTION = "KeyNote is a simple and flexible trust-management \
  system designed to work well for a variety of large- and small- \
  scale Internet-based applications. \
"
HOMEPAGE = "http://www.cs.columbia.edu/~angelos/keynote.html"
SECTION = "security"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3a265095c549c1808686a676f2699c98"

MAIN_ID = "${@d.getVar('PV').split('.')[0]}"
MINOR_ID = "${@d.getVar('PV').split('.')[1]}"
SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}-${MAIN_ID}-${MINOR_ID}/${BPN}_${PV}.tar.gz \
       file://configure-remove-hardcode-path.patch \
       file://makefile-add-ldflags.patch \
       file://run-ptest \
"
S = "${WORKDIR}/${BPN}-${PV}+dfsg.orig"

inherit autotools-brokensep ptest

SRC_URI[md5sum] = "a14553e6ad921b5c85026ce5bec3afe7"
SRC_URI[sha256sum] = "38d2acfa1c3630a07adcb5c8fe92d2aef7f0e6d242b8998b2bbb1c6e4c408d46"

DEPENDS = "flex openssl"

EXTRA_OEMAKE += "test-sample -j1"

do_install() {
    install -D -m 0755 ${S}/keynote ${D}${bindir}/keynote
    install -D -m 0644 ${S}/libkeynote.a ${D}${libdir}/libkeynote.a
    install -D -m 0644 ${S}/keynote.h ${D}${includedir}/keynote.h
}

do_install_ptest() {
    install -D -m 0755 ${S}/sample-app ${D}${PTEST_PATH}
    cp -r ${S}/testsuite ${D}${PTEST_PATH}
    sed -i 's|@PTEST_PATH@|${PTEST_PATH}|' ${D}${PTEST_PATH}/run-ptest
}
