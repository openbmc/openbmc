SUMMARY = "Utility scripts for internationalizing XML"
HOMEPAGE = "https://launchpad.net/intltool"
DESCRIPTION = "Utility scripts for internationalizing XML. This tool automatically extracts translatable strings from oaf, glade, bonobo ui, nautilus theme and other XML files into the po files."
SECTION = "devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://launchpad.net/${BPN}/trunk/${PV}/+download/${BP}.tar.gz \
           file://intltool-nowarn.patch \
           file://perl-522-deprecations.patch \
           file://remove-perl-check.patch \
           file://noperlcheck.patch \
           "
SRC_URI[md5sum] = "12e517cac2b57a0121cda351570f1e63"
SRC_URI[sha256sum] = "67c74d94196b153b774ab9f89b2fa6c6ba79352407037c8c14d5aeb334e959cd"

UPSTREAM_CHECK_URI = "https://launchpad.net/intltool/trunk/"

DEPENDS = "libxml-parser-perl-native"
RDEPENDS:${PN} = "gettext-dev libxml-parser-perl"
DEPENDS:class-native = "libxml-parser-perl-native gettext-native"

inherit autotools pkgconfig perlnative

export PERL = "${bindir}/env perl"
PERL:class-native = "/usr/bin/env nativeperl"
PERL:class-nativesdk = "/usr/bin/env perl"

# gettext is assumed to exist on the host
RDEPENDS:${PN}:class-native = "libxml-parser-perl-native"
RRECOMMENDS:${PN} = "perl-modules"
RRECOMMENDS:${PN}:class-native = ""

FILES:${PN}-dev = ""
FILES:${PN} += "${datadir}/aclocal"

INSANE_SKIP:${PN} += "dev-deps"

BBCLASSEXTEND = "native nativesdk"
