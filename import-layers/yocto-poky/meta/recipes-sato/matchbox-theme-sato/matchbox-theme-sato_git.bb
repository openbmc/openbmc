require matchbox-theme-sato.inc

SRCREV = "f72cf4ed7d71ad9e47b0f2d3dbc593bc2f3e76f8"
PV = "0.2+git${SRCPV}"

DEFAULT_PREFERENCE = "-1"

SRC_URI = "git://git.yoctoproject.org/matchbox-sato"

EXTRA_OECONF += "${@bb.utils.contains('MACHINE_FEATURES', 'qvga', '--with-mode=qvga', '',d)}"

S = "${WORKDIR}/git"
