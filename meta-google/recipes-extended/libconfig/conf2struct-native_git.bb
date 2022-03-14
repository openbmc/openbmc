LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=667d4ad55f5fbf4c3e853e8acd0f74de"

PV = "0.1+git${SRCPV}"
SRC_URI = "git://github.com/yrutschle/conf2struct;branch=master;protocol=https"
SRCREV = "6bc9eed1eb50175e5fda791f27d85e72f5a6ac78"
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
