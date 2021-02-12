SUMMARY = "Perl module to decode/encode json files"
DESCRIPTION = "This package contains the JSON.pm module with friends. \
The module implements JSON encode/decode."

HOMEPAGE = "https://metacpan.org/pod/JSON"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=1171;endline=1176;md5=3be2cb8159d094768e67386c453e8bbe"

DEPENDS += "perl"

SRC_URI = "git://github.com/makamaka/JSON.git;protocol=https"

SRCREV = "ebbae181c5e311fa80ee4c6379b598c7a6400570"

S = "${WORKDIR}/git"

inherit cpan

RDEPENDS_${PN} += "perl"

BBCLASSEXTEND = "native nativesdk"
