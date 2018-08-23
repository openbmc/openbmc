require xserver-xorg.inc

SRC_URI += "file://musl-arm-inb-outb.patch \
            file://0001-configure.ac-Fix-check-for-CLOCK_MONOTONIC.patch \
            file://0003-modesetting-Fix-16-bit-depth-bpp-mode.patch \
            file://0003-Remove-check-for-useSIGIO-option.patch \
            file://0001-xf86pciBus.c-use-Intel-ddx-only-for-pre-gen4-hardwar.patch \
            file://0001-config-fix-NULL-value-detection-for-ID_INPUT-being-u.patch \
            "
SRC_URI[md5sum] = "3e47777ff034a331aed2322b078694a8"
SRC_URI[sha256sum] = "a732502f1db000cf36a376cd0c010ffdbf32ecdd7f1fa08ba7f5bdf9601cc197"

# These extensions are now integrated into the server, so declare the migration
# path for in-place upgrades.

RREPLACES_${PN} =  "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
RPROVIDES_${PN} =  "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
RCONFLICTS_${PN} = "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
