require links.inc

DEPENDS += "gpm"
RCONFLICTS_${PN} = "links-x11"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --with-fb \
                --without-directfb --without-pmshell --without-atheos \
                --without-x"

SRC_URI[md5sum] = "645fb0c0294e4c3268f94d307d394ffb"
SRC_URI[sha256sum] = "82f03038d5e050a65681b9888762af41c40fd42dec7e59a8d630bfb0ee134a3f"
SRC_URI[icon.md5sum] = "477e8787927c634614bac01b44355a33"
SRC_URI[icon.sha256sum] = "eddcd8b8c8698aa621d1a453943892d77b72ed492e0d14e0dbac5c6a57e52f47"
