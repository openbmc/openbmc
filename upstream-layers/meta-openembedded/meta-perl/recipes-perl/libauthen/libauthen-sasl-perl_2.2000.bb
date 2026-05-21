SUMMARY = "SASL Authentication framework for Perl"
DESCRIPTION = "SASL is a generic mechanism for authentication used by several network \
protocols. Authen::SASL provides an implementation framework that all \
protocols should be able to share."
HOMEPAGE = "https://metacpan.org/dist/Authen-SASL/"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://lib/Authen/SASL/Perl.pm;beginline=1;endline=3;md5=17123315bbcda19f484c07227594a609"

DEPENDS = "perl"
RDEPENDS:${PN} = "libdigest-hmac-perl"

SRC_URI = "${CPAN_MIRROR}/authors/id/E/EH/EHUELS/Authen-SASL-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "8cdf5a7f185448b614471675dae5b26f8c6e330b62264c3ff5d91172d6889b99"

S = "${UNPACKDIR}/Authen-SASL-${PV}"

inherit cpan ptest

export PERL_USE_UNSAFE_INC = "1"

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

RDEPENDS:${PN}-ptest += "perl-module-test-more \
                         perl-module-findbin \
                         perl-module-test2-api-breakage \
                         perl-module-tie-handle"

BBCLASSEXTEND = "native"
