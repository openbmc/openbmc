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
           file://0001-automake-Update-for-python.m4-to-respect-libdir.patch \
           file://buildtest.patch \
           file://performance.patch \
           file://new_rt_path_for_test-driver.patch \
           file://0001-automake-Add-default-libtool_tag-to-cppasm.patch \
           file://0001-build-fix-race-in-parallel-builds.patch \
           file://0001-Drop-ar-u-argument.patch \
           "

SRC_URI[sha256sum] = "07bd24ad08a64bc17250ce09ec56e921d6343903943e99ccf63bbf0705e34605"

PERL = "${USRBINPATH}/perl"
PERL:class-native = "${USRBINPATH}/env perl"
PERL:class-nativesdk = "${USRBINPATH}/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_install:append () {
    install -d ${D}${datadir}
}

BBCLASSEXTEND = "native nativesdk"
