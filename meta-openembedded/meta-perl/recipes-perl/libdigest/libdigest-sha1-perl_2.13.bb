SUMMARY = "Perl interface to the SHA-1 algorithm "
DESCRIPTION = "Digest::SHA1 - Perl interface to the SHA-1 algorithm"
HOMEPAGE = "https://metacpan.org/pod/Digest::SHA1"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=10;endline=14;md5=ff5867ebb4bc1103a7a416aef2fce00a"

SRC_URI = "${CPAN_MIRROR}/authors/id/G/GA/GAAS/Digest-SHA1-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "68c1dac2187421f0eb7abf71452a06f190181b8fc4b28ededf5b90296fb943cc"

S = "${WORKDIR}/Digest-SHA1-${PV}"

inherit cpan ptest

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND="native"

FILES:${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/Digest/SHA1/.debug/"
