DESCRIPTION = "Different utilities from Android - corresponding configuration files"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://android-gadget-setup"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/android-gadget-setup ${D}${bindir}
}

python () {
    pn = d.getVar('PN')
    profprov = d.getVar("PREFERRED_PROVIDER_" + pn)
    if profprov and pn != profprov:
        raise bb.parse.SkipRecipe("PREFERRED_PROVIDER_%s set to %s, not %s" % (pn, profprov, pn))
}
