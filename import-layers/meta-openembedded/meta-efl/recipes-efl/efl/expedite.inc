DESCRIPTION = "Expedite is a comprehensive benchmarking suite for Evas"
DEPENDS = "eet evas libsdl"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d70bc65d46237e4ef386beebe7d0fe5b"

INC_PR = "r0"

inherit e

EXTRA_OECONF = "\
    --x-includes=${STAGING_INCDIR}/X11 \
    --x-libraries=${STAGING_LIBDIR} \
    --enable-simple-x11 \
\
    --enable-software-x11 \
    --enable-xrender-x11 \
    --enable-software-16-x11 \
    --enable-opengl-x11 \
    --enable-fb \
    --disable-software-ddraw \
    --disable-direct3d \
"

FILES_${PN} += "${datadir}"
RDEPENDS_${PN} = "expedite-themes evas-engine-fb evas-engine-software-generic evas-engine-software-x11 evas-engine-gl-x11 evas-loader-png"
