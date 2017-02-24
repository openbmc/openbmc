require xen.inc

SRCREV ?= "9a6cc4f5c14b3d7542b7523f88a1b65464733d3a"

XEN_REL ?= "4.7"
XEN_BRANCH ?= "staging-${XEN_REL}"

PV = "${XEN_REL}+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = " \
    git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH} \
    "

DEFAULT_PREFERENCE = "-1"
