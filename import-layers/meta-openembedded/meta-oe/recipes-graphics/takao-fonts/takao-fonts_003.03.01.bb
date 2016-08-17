SUMMARY = "Takao Fonts are a community developed derivatives of IPA Fonts."
DESCRIPTION = "Takao Fonts are a community developed derivatives of IPA Fonts."

HOMEPAGE = "https://launchpad.net/takao-fonts"
SECTION = "User Interface/X"

LICENSE = "IPA"
LIC_FILES_CHKSUM = "file://IPA_Font_License_Agreement_v1.0.txt;md5=6cd3351ba979cf9db1fad644e8221276"
SRC_URI = "https://launchpad.net/${BPN}/trunk/15.03/+download/TakaoFonts_00303.01.tar.xz"
SRC_URI[md5sum] = "8cd3fe724faa5034a9369e98cf108d2d"
SRC_URI[sha256sum] = "e9871f72ac69acb3e277aebbee7ca01fbebf54800733e29fafdc46133fc3552f"

S = "${WORKDIR}/TakaoFonts_00303.01"
do_install() {
    install -m 0755 -d ${D}/${datadir}/fonts
    install -m 0644 -p ${S}/*.ttf ${D}/${datadir}/fonts/
}

FILES_${PN} += "${datadir}/fonts/*.ttf"
