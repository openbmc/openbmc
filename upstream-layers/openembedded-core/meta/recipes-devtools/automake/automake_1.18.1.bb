require automake.inc
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS:class-native = "autoconf-native"

NAMEVER = "${@oe.utils.trim_version("${PV}", 2)}"

RDEPENDS:${PN} += "\
    autoconf \
    perl \
    perl-module-bytes \
    perl-module-data-dumper \
    perl-module-strict \
    perl-module-text-parsewords \
    perl-module-thread-queue \
    perl-module-threads \
    perl-module-vars "

RDEPENDS:${PN}:class-native = "autoconf-native hostperl-runtime-native"

SRC_URI += "\
           file://0002-automake-Update-for-python.m4-to-respect-libdir.patch \
           file://0004-Add-a-new-distro-feature-ptest.patch \
           file://0006-automake-Remove-delays-in-configure-scripts-using-au.patch \
           "

SRC_URI[sha256sum] = "63e585246d0fc8772dffdee0724f2f988146d1a3f1c756a3dc5cfbefa3c01915"

PERL = "${USRBINPATH}/perl"
PERL:class-native = "${USRBINPATH}/env perl"
PERL:class-nativesdk = "${USRBINPATH}/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_install:append () {
    install -d ${D}${datadir}
}

BBCLASSEXTEND = "native nativesdk"
