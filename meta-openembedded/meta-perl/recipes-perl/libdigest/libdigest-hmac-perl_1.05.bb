SUMMARY = "Keyed-Hashing for Message Authentication"
DESCRIPTION = "Keyed-Hashing for Message Authentication"
HOMEPAGE = "https://metacpan.org/pod/Digest::HMAC"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=6;endline=9;md5=6321739e9ccbf74ee5dd2eb82c42d609"

RDEPENDS:${PN} = "libdigest-sha1-perl perl-module-extutils-makemaker perl-module-digest-md5"

SRC_URI = "${CPAN_MIRROR}/authors/id/A/AR/ARODLAND/Digest-HMAC-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "215cb59cba610745cfb2d4b3f8ef756d590e57e3ad7986a992e87c4969fcdc7a"

S = "${WORKDIR}/Digest-HMAC-${PV}"

inherit cpan ptest

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
