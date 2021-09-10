require ttf.inc

SUMMARY = "Ipa fonts - TTF Version"
HOMEPAGE = "https://moji.or.jp/ipafont"
LICENSE = "IPA"
LICENSE_URL = "https://moji.or.jp/ipafont/license/"
LIC_FILES_CHKSUM = "file://IPA_Font_License_Agreement_v1.0.txt;md5=6cd3351ba979cf9db1fad644e8221276 \
"
SRC_URI = "https://moji.or.jp/wp-content/ipafont/IPAfont/IPAfont00303.zip "

SRC_URI[sha256sum] = "f755ed79a4b8e715bed2f05a189172138aedf93db0f465b4e20c344a02766fe5"

S = "${WORKDIR}/IPAfont00303"

PACKAGES = "ttf-ipag ttf-ipagp ttf-ipam ttf-ipamp"
FONT_PACKAGES = "ttf-ipag ttf-ipagp ttf-ipam ttf-ipamp"

FILES:ttf-ipag = "${datadir}/fonts/truetype/ipag.ttf"
FILES:ttf-ipagp = "${datadir}/fonts/truetype/ipagp.ttf"
FILES:ttf-ipam = "${datadir}/fonts/truetype/ipam.ttf"
FILES:ttf-ipamp = "${datadir}/fonts/truetype/ipamp.ttf"
