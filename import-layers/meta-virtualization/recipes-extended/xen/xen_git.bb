require xen.inc

SRCREV = "1fd615aa0108490ffc558d27627f509183cbfdaf"

XEN_REL="4.6"

PV = "${XEN_REL}.0+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = " \
    git://xenbits.xen.org/xen.git;branch=staging-${XEN_REL} \
    "

DEFAULT_PREFERENCE = "-1"
