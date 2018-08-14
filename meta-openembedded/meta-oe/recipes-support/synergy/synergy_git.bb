SUMMARY = "Synergy - control multiple computers with one keyboard and mouse"
HOMEPAGE = "http://synergy-project.org"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2ba51ca68e055566aade24662f9eb41"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
SECTION = "x11/utils"

DEPENDS = "virtual/libx11 libxtst libxinerama curl openssl"
do_unpack_extra[depends] = "unzip-native:do_populate_sysroot"

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://github.com/symless/synergy.git;protocol=http"

# Version 1.8.8-stable
SRCREV ?= "c30301e23424db1125664da17deb8c3aa6aec52d"
PV = "1.8.8+${SRCPV}"

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
