SUMMARY = "Utility scripts for internationalizing XML"
SECTION = "devel"
LICENSE = "GPLv2"

URLV="${@'.'.join(d.getVar('PV',1).split('.')[0:2])}"
SRC_URI = "http://launchpad.net/${BPN}/trunk/${PV}/+download/${BP}.tar.gz"

UPSTREAM_CHECK_URI = "https://launchpad.net/intltool/trunk/"

S = "${WORKDIR}/intltool-${PV}"

DEPENDS = "libxml-parser-perl-native"
RDEPENDS_${PN} = "gettext-dev libxml-parser-perl"
DEPENDS_class-native = "libxml-parser-perl-native"
# gettext is assumed to exist on the host
RDEPENDS_${PN}_class-native = "libxml-parser-perl-native"
RRECOMMENDS_${PN} = "perl-modules"
RRECOMMENDS_${PN}_class-native = ""

FILES_${PN}-dev = ""
FILES_${PN} += "${datadir}/aclocal"

INSANE_SKIP_${PN} += "dev-deps"

inherit autotools pkgconfig perlnative

export PERL = "${bindir}/env perl"
PERL_class-native = "/usr/bin/env nativeperl"

BBCLASSEXTEND = "native"
