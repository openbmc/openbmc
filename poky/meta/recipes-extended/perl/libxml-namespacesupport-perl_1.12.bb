SUMMARY = "Perl module for supporting simple generic namespaces"
HOMEPAGE = "http://veillard.com/XML/"
DESCRIPTION = "XML::NamespaceSupport offers a simple way to process namespace-based XML names. \
                It also helps maintain a prefix-to-namespace URI map, and provides a number of \
                basic checks. "

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

SRCNAME = "XML-NamespaceSupport"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c9d8a117e7620b5adf8d69c29613ceab"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PE/PERIGRIN/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "a8916c6d095bcf073e1108af02e78c97"
SRC_URI[sha256sum] = "47e995859f8dd0413aa3f22d350c4a62da652e854267aa0586ae544ae2bae5ef"

UPSTREAM_CHECK_REGEX = "XML\-NamespaceSupport\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN}-ptest += "perl-module-test-more"

BBCLASSEXTEND="native nativesdk"

