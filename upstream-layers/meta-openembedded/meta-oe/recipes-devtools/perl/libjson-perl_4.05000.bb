SUMMARY = "Perl module to decode/encode json files"
DESCRIPTION = "This package contains the JSON.pm module with friends. \
The module implements JSON encode/decode."

HOMEPAGE = "https://metacpan.org/pod/JSON"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=1171;endline=1176;md5=3be2cb8159d094768e67386c453e8bbe"

DEPENDS += "perl"

SRC_URI = "git://github.com/makamaka/JSON.git;protocol=https;branch=master"

SRCREV = "39bc0e567c202762a575fed2844ebdb941c3ca09"


inherit cpan

RDEPENDS:${PN} += "perl"

BBCLASSEXTEND = "native nativesdk"
