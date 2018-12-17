SUMMARY = "Synergy - control multiple computers with one keyboard and mouse"
HOMEPAGE = "http://synergy-project.org"
LIC_FILES_CHKSUM = "file://LICENSE;md5=54c1fc8d8bb6776ae501acfb1585e9d6"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
SECTION = "x11/utils"

DEPENDS = "virtual/libx11 libxtst libxinerama curl openssl"

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://github.com/symless/synergy-core;protocol=https;nobranch=1"

# Version 1.10.1-stable
SRCREV ?= "1b4c076127687aceac931d269e898beaac1cad9f"
PV = "1.10.1+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake distro_features_check

EXTRA_OECMAKE += "-DSYNERGY_BUILD_LEGACY_GUI=OFF"

FILES_${PN} += "${datadir}/icons/hicolor"
