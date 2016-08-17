SUMMARY = "Perl module for supporting simple generic namespaces"
DESCRIPTION = "XML::NamespaceSupport offers a simple way to process namespace-based XML names. \
                It also helps maintain a prefix-to-namespace URI map, and provides a number of \
                basic checks. "

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
PR = "r3"

LIC_FILES_CHKSUM = "file://META.yml;beginline=22;endline=22;md5=3b2b564dae8b9af9e8896e85c07dcbe5"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/P/PE/PERIGRIN/XML-NamespaceSupport-${PV}.tar.gz"
SRC_URI[md5sum] = "222cca76161cd956d724286d36b607da"
SRC_URI[sha256sum] = "6d8151f0a3f102313d76b64bfd1c2d9ed46bfe63a16f038e7d860fda287b74ea"


S = "${WORKDIR}/XML-NamespaceSupport-${PV}"

inherit cpan

BBCLASSEXTEND="native"

