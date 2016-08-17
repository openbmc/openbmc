require tasks.inc

SRC_URI = "git://git.gnome.org/${BPN}"

SRCREV = "ea52d46d691c5fce4473ea4e24a35411381f3a65"
PV = "0.13+git${SRCPV}"
PR = "r3"

S = "${WORKDIR}/git"
