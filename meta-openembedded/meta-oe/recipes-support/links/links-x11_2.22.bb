require links.inc

DEPENDS += "virtual/libx11"
RCONFLICTS:${PN} = "links"

inherit features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://links2.desktop \
             http://www.xora.org.uk/oe/links2.png;name=icon"

SRC_URI[sha256sum] = "0364986b3a7f1e8e3171bea362b53f71e1dd3360a8842d66fdc65580ebc2084d"
SRC_URI[icon.sha256sum] = "eddcd8b8c8698aa621d1a453943892d77b72ed492e0d14e0dbac5c6a57e52f47"

S = "${WORKDIR}/links-${PV}"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --without-fb \
                --without-directfb --without-pmshell --without-atheos \
                --with-x --without-gpm"

do_install:append() {
    install -d ${D}/${datadir}/applications
    install -m 0644 ${WORKDIR}/links2.desktop ${D}/${datadir}/applications
    install -d ${D}/${datadir}/pixmaps
    install -m 0644 ${WORKDIR}/links2.png ${D}/${datadir}/pixmaps
}
