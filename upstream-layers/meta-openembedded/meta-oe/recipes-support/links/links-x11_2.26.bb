require links.inc

DEPENDS += "virtual/libx11"
RCONFLICTS:${PN} = "links"

inherit features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://links2.desktop \
             file://links2.png"

SRC_URI[sha256sum] = "f05b3577f25dbe63e491c424f0ecb31f7bfadce9b2bc2f111dfed049c004c9cb"

S = "${UNPACKDIR}/links-${PV}"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --without-fb \
                --without-directfb --without-pmshell --without-atheos \
                --with-x --without-gpm"

do_install:append() {
    install -d ${D}/${datadir}/applications
    install -m 0644 ${UNPACKDIR}/links2.desktop ${D}/${datadir}/applications
    install -d ${D}/${datadir}/pixmaps
    install -m 0644 ${UNPACKDIR}/links2.png ${D}/${datadir}/pixmaps
}
