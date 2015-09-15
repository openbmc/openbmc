require gtk-sato-engine.inc

LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/sato-utils.h;endline=24;md5=708f28cfe7fe028d497aaf4389b80b62 \
                    file://src/sato-main.c;endline=24;md5=b5e5dddebca570275becb51b526e4c5a"

SRCREV = "4740ad8d53aba4368ce3e03b06cfdc69eb86dcdc"
PV = "0.3.3+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/${BPN}"

S = "${WORKDIR}/git"

EXTRA_OECONF += "${@bb.utils.contains('MACHINE_FEATURES', 'qvga', '--with-mode=qvga', '',d)}"
