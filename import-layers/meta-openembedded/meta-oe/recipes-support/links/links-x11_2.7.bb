require links.inc

DEPENDS += "virtual/libx11"
RCONFLICTS_${PN} = "links"

inherit distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://links2.desktop \
             http://www.xora.org.uk/oe/links2.png;name=icon"

S = "${WORKDIR}/links-${PV}"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --without-fb \
                --without-directfb --without-pmshell --without-atheos \
                --with-x --without-gpm"

do_install_append() {
    install -d ${D}/${datadir}/applications
    install -m 0644 ${WORKDIR}/links2.desktop ${D}/${datadir}/applications
    install -d ${D}/${datadir}/pixmaps
    install -m 0644 ${WORKDIR}/links2.png ${D}/${datadir}/pixmaps
}

SRC_URI[md5sum] = "d06aa6e14b2172d73188871a5357185a"
SRC_URI[sha256sum] = "0c182b1cbcdfd5cdcd2f75a6032d1a4b660d07c1225c1e07757cec81d3302130"
SRC_URI[icon.md5sum] = "477e8787927c634614bac01b44355a33"
SRC_URI[icon.sha256sum] = "eddcd8b8c8698aa621d1a453943892d77b72ed492e0d14e0dbac5c6a57e52f47"
