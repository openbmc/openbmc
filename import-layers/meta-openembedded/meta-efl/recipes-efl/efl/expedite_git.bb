require ${BPN}.inc

SRCREV = "a5e6af917af52877b378090811cf836c16d0bfbb"
PV = "1.7.99+gitr${SRCPV}"

DEFAULT_PREFERENCE = "-1"

SRC_URI = "\
    git://git.enlightenment.org/tools/${BPN}.git \
"
S = "${WORKDIR}/${SRCNAME}"
