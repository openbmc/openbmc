SUMMARY = "Perl module for supporting simple generic namespaces"
HOMEPAGE = "http://veillard.com/XML/"
DESCRIPTION = "XML::NamespaceSupport offers a simple way to process namespace-based XML names. \
                It also helps maintain a prefix-to-namespace URI map, and provides a number of \
                basic checks. "

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
PR = "r3"

LIC_FILES_CHKSUM = "file://META.yml;beginline=22;endline=22;md5=9ca1a4a941496e7feedac72c4fb8b137"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/P/PE/PERIGRIN/XML-NamespaceSupport-${PV}.tar.gz"
SRC_URI[md5sum] = "a8916c6d095bcf073e1108af02e78c97"
SRC_URI[sha256sum] = "47e995859f8dd0413aa3f22d350c4a62da652e854267aa0586ae544ae2bae5ef"


S = "${WORKDIR}/XML-NamespaceSupport-${PV}"

inherit cpan

BBCLASSEXTEND="native"

