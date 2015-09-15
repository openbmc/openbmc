require ofono.inc

S	 = "${WORKDIR}/git"
SRCREV = "14544d5996836f628613c2ce544380ee6fc8f514"
PV	 = "0.12-git${SRCPV}"
PR = "r5"

SRC_URI  = "git://git.kernel.org/pub/scm/network/ofono/ofono.git \
	    file://ofono"

do_configure_prepend () {
  ${S}/bootstrap
}

