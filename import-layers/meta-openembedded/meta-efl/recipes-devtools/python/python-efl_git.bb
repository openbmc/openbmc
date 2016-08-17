require ${BPN}.inc

SRCREV = "71ed20ebf662a7b72e60913df94ce6933236bf09"
PV = "1.12.0+git${SRCPV}"
DEFAULT_PREFERENCE = "-1"

SRC_URI = "git://git.enlightenment.org/bindings/python/${BPN}.git;branch=python-efl-1.12"

S = "${WORKDIR}/git"
