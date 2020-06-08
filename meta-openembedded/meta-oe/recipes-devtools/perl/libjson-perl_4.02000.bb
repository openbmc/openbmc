SUMMARY = "Perl module to decode/encode json files"
DESCRIPTION = "This package contains the JSON.pm module with friends. \
The module implements JSON encode/decode."

HOMEPAGE = "https://metacpan.org/pod/JSON"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=1171;endline=1176;md5=3be2cb8159d094768e67386c453e8bbe"

DEPENDS += "perl"

SRC_URI = "git://github.com/makamaka/JSON.git;protocol=https"

SRCREV = "42a6324df654e92419512cee80c0b49155d9e56d"

S = "${WORKDIR}/git"

inherit cpan

RDEPENDS_${PN} += "perl"

BBCLASSEXTEND = "native nativesdk"
