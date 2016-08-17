SUMMARY = "SASL Authentication framework for Perl"
DESCRIPTION = "SASL is a generic mechanism for authentication used by several network \
protocols. Authen::SASL provides an implementation framework that all \
protocols should be able to share."
HOMEPAGE = "http://search.cpan.org/dist/Authen-SASL/"
SECTION = "libs"

LICENSE = "Artistic-1.0|GPL-1.0+"
LIC_FILES_CHKSUM = "file://lib/Authen/SASL/Perl.pm;beginline=1;endline=3;md5=17123315bbcda19f484c07227594a609"

DEPENDS = "perl"
RDEPENDS_${PN} = "libdigest-hmac-perl"

SRC_URI = "http://www.cpan.org/authors/id/G/GB/GBARR/Authen-SASL-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[md5sum] = "7c03a689d4c689e5a9e2f18a1c586b2f"
SRC_URI[sha256sum] = "6614fa7518f094f853741b63c73f3627168c5d3aca89b1d02b1016dc32854e09"

S = "${WORKDIR}/Authen-SASL-${PV}"

inherit cpan ptest

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
