FILESEXTRAPATHS:prepend:nuvoton := "${THISDIR}/${PN}:"
SRC_URI:append:nuvoton:df-phosphor-mmc = " file://disable-mmc-mirroruboot.patch"
