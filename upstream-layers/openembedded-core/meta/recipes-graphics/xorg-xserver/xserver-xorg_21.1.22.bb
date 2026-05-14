require xserver-xorg.inc

SRC_URI += " file://0001-xf86pciBus.c-use-Intel-ddx-only-for-pre-gen4-hardwar.patch"
SRC_URI[sha256sum] = "1a242c8917c49ba29ccc1f6021613d8a2b9805dd0d271a66ae9d09f4b0bb06b3"

# These extensions are now integrated into the server, so declare the migration
# path for in-place upgrades.

RREPLACES:${PN} =  "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
RPROVIDES:${PN} =  "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
RCONFLICTS:${PN} = "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "

RDEPENDS:${PN} += "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "", "x11-volatiles", d)}"
