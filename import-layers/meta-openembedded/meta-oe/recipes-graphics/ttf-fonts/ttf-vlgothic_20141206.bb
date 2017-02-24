require ttf.inc

SUMMARY = "Japanese TrueType fonts from Vine Linux"
AUTHOR = "Contributor: noonov <noonov@gmail.com>"
HOMEPAGE = "http://vlgothic.dicey.org/"

LICENSE = "mplus & BSD"
LIC_FILES_CHKSUM = "file://LICENSE.en;md5=66ecd0fd7e4da6246fa30317c7b66755 \
                    file://LICENSE_E.mplus;md5=1c4767416f20215f1e61b970f2117db9 \
"

SRC_URI = "https://osdn.jp/dl/vlgothic/VLGothic-${PV}.tar.xz"

SRC_URI[md5sum] = "bb7fadb2dff09a4fb6a11dc9dfdc0c36"
SRC_URI[sha256sum] = "982040db2f9cb73d7c6ab7d9d163f2ed46d1180f330c9ba2fae303649bf8102d"

S = "${WORKDIR}/VLGothic"

do_install_append () {
    install -D -m644 ${S}/LICENSE_E.mplus ${D}${datadir}/licenses/${PN}/COPYING_MPLUS.txt
    install -D -m644 ${S}/README.sazanami ${D}${datadir}/licenses/${PN}/COPYING_SAZANAMI.txt
    install -D -m644 ${S}/LICENSE.en ${D}${datadir}/licenses/${PN}/COPYING_VLGOTHIC.txt
}

PACKAGES = "${PN}"
FONT_PACKAGES = "${PN}"

FILES_${PN} = "${datadir}/fonts/truetype ${datadir}/licenses"
