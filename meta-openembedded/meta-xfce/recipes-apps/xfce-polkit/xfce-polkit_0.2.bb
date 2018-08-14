SUMMARY = "A simple PolicyKit authentication agent for XFCE"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=758b03f872a88c99fc36d50c5932091c"

DEPENDS = "libxfce4ui polkit"

inherit xfce-app

SRC_URI = " \
    git://github.com/ncopa/${BPN}.git \
    file://0001-fix-Name-Comment-fields.patch \
"
SRCREV = "6ad1ee833c9e22e4dd72a8f7d54562d046965283"
S = "${WORKDIR}/git"
