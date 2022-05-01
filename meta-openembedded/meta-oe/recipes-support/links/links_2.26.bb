require links.inc

DEPENDS += "gpm"
RCONFLICTS:${PN} = "links-x11"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --with-fb \
                --without-directfb --without-pmshell --without-atheos \
                --without-x"
SRC_URI[sha256sum] = "f05b3577f25dbe63e491c424f0ecb31f7bfadce9b2bc2f111dfed049c004c9cb"
