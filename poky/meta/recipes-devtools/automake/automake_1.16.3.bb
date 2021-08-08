require automake.inc
LICENSE = "GPLv2"
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

SRC_URI += "file://python-libdir.patch \
           file://buildtest.patch \
           file://performance.patch \
           file://new_rt_path_for_test-driver.patch \
           file://0001-automake-Add-default-libtool_tag-to-cppasm.patch \
           file://0001-build-fix-race-in-parallel-builds.patch \
           "

SRC_URI[sha256sum] = "ce010788b51f64511a1e9bb2a1ec626037c6d0e7ede32c1c103611b9d3cba65f"

PERL = "${USRBINPATH}/perl"
PERL:class-native = "${USRBINPATH}/env perl"
PERL:class-nativesdk = "${USRBINPATH}/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_install:append () {
    install -d ${D}${datadir}
}

BBCLASSEXTEND = "native nativesdk"
