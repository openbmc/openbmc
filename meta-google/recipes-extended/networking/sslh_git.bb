PR = "r2"
PV = "0.1+git${SRCPV}"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/yrutschle/sslh;branch=master;protocol=https"
SRCREV = "63f9c4a582f79f4d0e484efe0ccaeed77a79f7df"
S = "${WORKDIR}/git"

inherit perlnative

DEPENDS += "conf2struct-native"
DEPENDS += "libbsd"
DEPENDS += "libcap"
DEPENDS += "libconfig"
DEPENDS += "systemd"
DEPENDS += "pcre2"

EXTRA_OEMAKE += "DESTDIR=${D}"
EXTRA_OEMAKE += "PREFIX=${prefix}"
EXTRA_OEMAKE += "USELIBCAP=1"
EXTRA_OEMAKE += "USELIBBSD=1"
EXTRA_OEMAKE += "USESYSTEMD=1"

do_configure() {
  oe_runmake distclean
}

do_compile() {
  # Workaround for a non-installed and broken echosrv
  sed -i 's,^\(all:.*\) echosrv,\1,' ${S}/Makefile

  # Workaround for the broken dependencies in the Makefile
  oe_runmake sslh-conf.h

  oe_runmake
}

do_install() {
  oe_runmake install
}
