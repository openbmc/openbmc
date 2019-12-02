require links.inc

DEPENDS += "gpm"
RCONFLICTS_${PN} = "links-x11"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --with-fb \
                --without-directfb --without-pmshell --without-atheos \
                --without-x"

SRC_URI[md5sum] = "ee39e612249440d0497535d0dafc3c0e"
SRC_URI[sha256sum] = "4b4f07d0e6261118d1365a5a5bfa31e1eafdbd280cfae6f0e9eedfea51a2f424"
SRC_URI[icon.md5sum] = "477e8787927c634614bac01b44355a33"
SRC_URI[icon.sha256sum] = "eddcd8b8c8698aa621d1a453943892d77b72ed492e0d14e0dbac5c6a57e52f47"
