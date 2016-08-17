SUMMARY = "Synergy - control multiple computers with one keyboard and mouse"
HOMEPAGE = "http://synergy-project.org"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f366945b209c5523e39889f636af00a"
LICENSE = "GPL-2.0"
SECTION = "x11/utils"

DEPENDS = "virtual/libx11 libxtst libxinerama unzip-native curl openssl"

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://github.com/synergy/synergy.git;protocol=http"

# Version 1.7.4-rc8
SRCREV ?= "588fb4b805dd452556d05dbc03fe29ea5b4e43c0"
PV = "1.7.3+1.7.4-rc8+${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake distro_features_check

do_unpack_extra() {
    cd ${S}/ext
    for file in *.zip; do
        fname="${file##*/}"
        unzip $file -d ${fname%.*}
    done
}
addtask unpack_extra after do_unpack before do_patch

do_install() {
    install -d ${D}/usr/bin
    install -m 0755 ${S}/bin/synergy* ${D}/usr/bin/
}
