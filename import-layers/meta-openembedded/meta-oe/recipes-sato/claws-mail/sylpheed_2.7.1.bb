SECTION = "x11/network"
SUMMARY = "Mail user agent"
DEPENDS = "gtk+ gpgme gnutls"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1"

PR = "r2"

SRC_URI = "http://sylpheed.sraoss.jp/sylpheed/v2.7/sylpheed-${PV}.tar.bz2 \
    file://glib-2.32.patch \
"
SRC_URI[md5sum] = "1f470525c1fbe53253813a0978c18228"
SRC_URI[sha256sum] = "8bb6457db4e2eea1877b487d9ac8513546372db9a6a2e4271d11229f4af84e23"

FILES_${PN} += "${datadir}/pixmaps ${datadir}/applications"
FILES_${PN}-doc += "${datadir}"

EXTRA_OECONF = "--disable-ssl"

CFLAGS += "-D_GNU_SOURCE"

do_configure_prepend() {
    mkdir -p m4
    for i in $(find ${S} -name "Makefile.am") ; do
        sed -i s:'-I$(includedir)'::g $i
    done
}

inherit autotools pkgconfig

