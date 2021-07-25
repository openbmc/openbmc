LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=1b886630cdc9a17c26250934beda407d"

PV = "0.1+git${SRCPV}"
SRC_URI = "git://github.com/yrutschle/conf2struct"
SRCREV = "06ddd9273f14dadd5f7b4cf144177ffa6a29f105"
S = "${WORKDIR}/git"

SRC_URI += " \
  file://0001-makefile-Add-missing-LDFLAGS.patch \
  file://0001-conf2struct-Use-the-right-perl.patch \
  "

inherit native

DEPENDS += " \
  libconfig-native \
  libconfig-perl-native \
  "

EXTRA_OEMAKE += " \
  DESTDIR=${D} \
  prefix=${prefix} \
  "

do_compile() {
    oe_runmake checker
}

do_install() {
    oe_runmake install
}
