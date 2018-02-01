require tasks.inc

SRC_URI = "git://git.gnome.org/${BPN}"

SRCREV = "ea52d46d691c5fce4473ea4e24a35411381f3a65"
PV = "0.13+git${SRCPV}"
PR = "r3"

S = "${WORKDIR}/git"

# ../../git/libkoto/koto-utils.c:81:3: error: format not a string literal, argument types not checked [-Werror=format-nonliteral]
PNBLACKLIST[tasks] ?= "Fails to build with gcc-6 - the recipe will be removed on 2017-09-01 unless the issue is fixed"
