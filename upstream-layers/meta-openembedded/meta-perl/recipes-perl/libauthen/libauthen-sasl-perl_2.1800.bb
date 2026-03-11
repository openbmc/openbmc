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
SRC_URI[sha256sum] = "0b03686bddbbf7d5c6548e468d079a4051c9b73851df740ae28cfd2db234e922"

S = "${UNPACKDIR}/Authen-SASL-${PV}"

inherit cpan ptest

export PERL_USE_UNSAFE_INC = "1"

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
