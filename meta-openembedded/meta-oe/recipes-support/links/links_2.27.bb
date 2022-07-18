require links.inc

DEPENDS += "gpm"
RCONFLICTS:${PN} = "links-x11"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --with-fb \
                --without-directfb --without-pmshell --without-atheos \
                --without-x"
SRC_URI[sha256sum] = "d8ddcbfcede7cdde80abeb0a236358f57fa6beb2bcf92e109624e9b896f9ebb4"
