require ttf.inc

SUMMARY = "Takao Fonts are a community developed derivatives of IPA Fonts."
DESCRIPTION = "Takao Fonts are a community developed derivatives of IPA Fonts."

HOMEPAGE = "https://launchpad.net/takao-fonts"
SECTION = "User Interface/X"

LICENSE = "IPA"
LIC_FILES_CHKSUM = "file://IPA_Font_License_Agreement_v1.0.txt;md5=6cd3351ba979cf9db1fad644e8221276"

SRC_URI = "https://launchpad.net/${BPN}/trunk/15.03/+download/TakaoFonts_00303.01.tar.xz"
SRC_URI[sha256sum] = "e9871f72ac69acb3e277aebbee7ca01fbebf54800733e29fafdc46133fc3552f"

S = "${WORKDIR}/TakaoFonts_00303.01"

PACKAGES = "ttf-takao-pgothic ttf-takao-gothic ttf-takao-pmincho ttf-takao-mincho"
FONT_PACKAGES = "ttf-takao-pgothic ttf-takao-gothic ttf-takao-pmincho ttf-takao-mincho"

FILES:ttf-takao-pgothic = "${datadir}/fonts/truetype/TakaoPGothic.ttf"
FILES:ttf-takao-gothic = "${datadir}/fonts/truetype/TakaoGothic.ttf"
FILES:ttf-takao-pmincho = "${datadir}/fonts/truetype/TakaoPMincho.ttf"
FILES:ttf-takao-mincho = "${datadir}/fonts/truetype/TakaoMincho.ttf"

FILES:${PN} += "${datadir}/fonts/*.ttf"
