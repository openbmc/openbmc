require ttf.inc

SUMMARY = "Liberation fonts - TTF Version"
HOMEPAGE = "https://www.redhat.com/promo/fonts/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://License.txt;md5=5b171c5100029d884fcea21d9a2b7543 \
"

PR = "r7"

SRC_URI = "http://fedorahosted.org/liberation-fonts/export/807b6dfd069b998cd9b4d3158da98817ef23c79d/F-9/liberation-fonts-ttf-3.tar.gz"
S = "${WORKDIR}/liberation-fonts-${PV}"

PACKAGES = "ttf-liberation-mono ttf-liberation-sans ttf-liberation-serif"
FONT_PACKAGES = "ttf-liberation-mono ttf-liberation-sans ttf-liberation-serif"

FILES_ttf-liberation-mono  = "${datadir}/fonts/truetype/*Mono*"
FILES_ttf-liberation-sans  = "${datadir}/fonts/truetype/*Sans*"
FILES_ttf-liberation-serif = "${datadir}/fonts/truetype/*Serif*"

SRC_URI[md5sum] = "77728078a17e39f7c242b42c3bf6feb8"
SRC_URI[sha256sum] = "174cf27c57612971434ec8cc4a52bfd37bad8408e9b9219539c6d5113df6ff8f"
