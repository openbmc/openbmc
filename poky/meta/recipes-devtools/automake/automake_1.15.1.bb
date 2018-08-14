require automake.inc
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS_class-native = "autoconf-native"

NAMEVER = "${@oe.utils.trim_version("${PV}", 2)}"

RDEPENDS_${PN} += "\
    autoconf \
    perl \
    perl-module-bytes \
    perl-module-data-dumper \
    perl-module-strict \
    perl-module-text-parsewords \
    perl-module-thread-queue \
    perl-module-threads \
    perl-module-vars "

RDEPENDS_${PN}_class-native = "autoconf-native hostperl-runtime-native"
RDEPENDS_${PN}_class-nativesdk = "nativesdk-autoconf"

SRC_URI += "file://python-libdir.patch \
            file://buildtest.patch \
            file://performance.patch \
            file://new_rt_path_for_test-driver.patch \
            file://automake-replace-w-option-in-shebangs-with-modern-use-warnings.patch \
            file://0001-automake-Add-default-libtool_tag-to-cppasm.patch \
            "

SRC_URI[md5sum] = "95df3f2d6eb8f81e70b8cb63a93c8853"
SRC_URI[sha256sum] = "988e32527abe052307d21c8ca000aa238b914df363a617e38f4fb89f5abf6260"

PERL = "${USRBINPATH}/perl"
PERL_class-native = "${USRBINPATH}/env perl"
PERL_class-nativesdk = "${USRBINPATH}/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_install_append () {
    install -d ${D}${datadir}
}

BBCLASSEXTEND = "native nativesdk"
