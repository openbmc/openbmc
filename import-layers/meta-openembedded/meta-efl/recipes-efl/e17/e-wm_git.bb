require ${BPN}.inc

PV = "0.19.4+git${SRCPV}"
DEFAULT_PREFERENCE = "-2"

SRC_URI = " \
    git://git.enlightenment.org/core/enlightenment.git;branch=enlightenment-0.19 \
    file://0001-configure.ac-add-foreign.patch \
    file://enlightenment_start.oe \
    file://applications.menu \
"
S = "${WORKDIR}/git"

SRCREV = "4545d4a70031e0b2565b8d83d5f756bff1a584d0"
