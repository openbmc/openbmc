SUMMARY = "Keyed-Hashing for Message Authentication"
DESCRIPTION = "Keyed-Hashing for Message Authentication"
HOMEPAGE = "http://search.cpan.org/~gaas/Digest-HMAC-1.03/"
SECTION = "libs"

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://README;beginline=13;endline=17;md5=da980cdc026faa065e5d5004115334e6"

RDEPENDS_${PN} = "libdigest-sha1-perl perl-module-extutils-makemaker perl-module-digest-md5"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GA/GAAS/Digest-HMAC-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[md5sum] = "e6a5d6f552da16eacb5157ea4369ff9d"
SRC_URI[sha256sum] = "3bc72c6d3ff144d73aefb90e9a78d33612d58cf1cd1631ecfb8985ba96da4a59"

S = "${WORKDIR}/Digest-HMAC-${PV}"

inherit cpan ptest

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
