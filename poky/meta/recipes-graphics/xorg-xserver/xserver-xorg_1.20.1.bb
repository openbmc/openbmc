require xserver-xorg.inc

SRC_URI += "file://musl-arm-inb-outb.patch \
            file://0001-xf86pciBus.c-use-Intel-ddx-only-for-pre-gen4-hardwar.patch \
            file://pkgconfig.patch \
            file://CVE-2018-14665.patch \
            "
SRC_URI[md5sum] = "e525846d1d0af5732ba835f2e2ec066d"
SRC_URI[sha256sum] = "59c99fe86fe75b8164c6567bfc6e982aecc2e4a51e6fbac1b842d5d00549e918"

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
