require links.inc

DEPENDS += "gpm"
RCONFLICTS_${PN} = "links-x11"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --with-fb \
                --without-directfb --without-pmshell --without-atheos \
                --without-x"

SRC_URI[md5sum] = "d06aa6e14b2172d73188871a5357185a"
SRC_URI[sha256sum] = "0c182b1cbcdfd5cdcd2f75a6032d1a4b660d07c1225c1e07757cec81d3302130"
