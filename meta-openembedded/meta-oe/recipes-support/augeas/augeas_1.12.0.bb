SUMMARY = "Augeas configuration API"
HOMEPAGE = "http://augeas.net/"
BUGTRACKER = "https://fedorahosted.org/augeas/report/1"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbb461211a33b134d42ed5ee802b37ff"

SRC_URI = "http://download.augeas.net/${BP}.tar.gz \
           file://sepbuildfix.patch \
           file://0001-src-internal-Use-__GLIBC__-to-check-for-GNU-extentio.patch \
          "
SRC_URI[md5sum] = "74f1c7b8550f4e728486091f6b907175"
SRC_URI[sha256sum] = "321942c9cc32185e2e9cb72d0a70eea106635b50269075aca6714e3ec282cb87"

DEPENDS = "readline libxml2"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-lenses lib${BPN}"

FILES:${PN}-lenses = "${datadir}/augeas/lenses"
FILES:lib${BPN} = "${libdir}/lib*${SOLIBS}"

RDEPENDS:lib${BPN} += "${PN}-lenses"
RRECOMMENDS:lib${BPN} += "${PN}"

LEAD_SONAME = "libaugeas.so"

do_install:append() {
    rm -fr ${D}${datadir}/vim
}

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"

EXTRA_AUTORECONF += "-I ${S}/gnulib/m4"
