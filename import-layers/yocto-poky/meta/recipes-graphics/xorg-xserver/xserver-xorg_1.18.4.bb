require xserver-xorg.inc

SRC_URI += "file://musl-arm-inb-outb.patch"
SRC_URI[md5sum] = "d4842dfe3bd9a9d062f2fa1df9104a46"
SRC_URI[sha256sum] = "278459b2c31d61a15655d95a72fb79930c480a6bb8cf9226e48a07df8b1d31c8"

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
