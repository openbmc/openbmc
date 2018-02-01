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
            file://0001-automake-port-to-Perl-5.22-and-later.patch \
            "

SRC_URI[md5sum] = "716946a105ca228ab545fc37a70df3a3"
SRC_URI[sha256sum] = "7946e945a96e28152ba5a6beb0625ca715c6e32ac55f2e353ef54def0c8ed924"

PERL = "${USRBINPATH}/perl"
PERL_class-native = "${USRBINPATH}/env perl"
PERL_class-nativesdk = "${USRBINPATH}/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_install_append () {
    install -d ${D}${datadir}
}

BBCLASSEXTEND = "native nativesdk"
