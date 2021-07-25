PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/yrutschle/sslh"
SRCREV = "de0ec959d944d719cc75736864290dc35c3ff685"
S = "${WORKDIR}/git"

DEPENDS += "libbsd"
DEPENDS += "libcap"
DEPENDS += "libconfig"
DEPENDS += "systemd"
DEPENDS += "pcre"

EXTRA_OEMAKE += "DESTDIR=${D}"
EXTRA_OEMAKE += "PREFIX=${prefix}"
EXTRA_OEMAKE += "USELIBCAP=1"
EXTRA_OEMAKE += "USELIBBSD=1"
EXTRA_OEMAKE += "USESYSTEMD=1"

do_patch() {
  # Workaround timestamps of the source being later than the generated,
  # vendored output files. This non-determinism sometimes causes failures.
  sed -i '/conf2struct/d' ${S}/Makefile
}

do_compile() {
  oe_runmake
}

do_install() {
  oe_runmake install
}
