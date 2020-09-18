require links.inc

DEPENDS += "gpm"
RCONFLICTS_${PN} = "links-x11"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --with-fb \
                --without-directfb --without-pmshell --without-atheos \
                --without-x"

SRC_URI[md5sum] = "b88a46733d6932442ed8a6b751aac6b4"
SRC_URI[sha256sum] = "285eed8591c7781ec26213df82786665aaa1b9286782e8a7a1a7e2a6e1630d63"
SRC_URI[icon.md5sum] = "477e8787927c634614bac01b44355a33"
SRC_URI[icon.sha256sum] = "eddcd8b8c8698aa621d1a453943892d77b72ed492e0d14e0dbac5c6a57e52f47"
