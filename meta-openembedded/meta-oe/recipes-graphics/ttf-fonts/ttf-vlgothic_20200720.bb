require ttf.inc

SUMMARY = "Japanese TrueType fonts from Vine Linux"
HOMEPAGE = "http://vlgothic.dicey.org/"

LICENSE = "ttf-mplus & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.en;md5=cc06b20e7a20bdf6c989624405378303 \
                    file://LICENSE_E.mplus;md5=1c4767416f20215f1e61b970f2117db9 \
"

SRC_URI = "https://osdn.jp/dl/vlgothic/VLGothic-${PV}.tar.xz"

SRC_URI[sha256sum] = "297a3813675fbea12c5813b55a78091c9a5946515ecbf9fde8b8102e01c579f4"

S = "${WORKDIR}/VLGothic"

do_install:append () {
    install -D -m644 ${S}/LICENSE_E.mplus ${D}${datadir}/licenses/${PN}/COPYING_MPLUS.txt
    install -D -m644 ${S}/README.sazanami ${D}${datadir}/licenses/${PN}/COPYING_SAZANAMI.txt
    install -D -m644 ${S}/LICENSE.en ${D}${datadir}/licenses/${PN}/COPYING_VLGOTHIC.txt
}

PACKAGES = "${PN}"
FONT_PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/fonts/truetype ${datadir}/licenses"
