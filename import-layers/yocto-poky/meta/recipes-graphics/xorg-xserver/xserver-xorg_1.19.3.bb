require xserver-xorg.inc

SRC_URI += "file://musl-arm-inb-outb.patch \
            file://0001-configure.ac-Fix-check-for-CLOCK_MONOTONIC.patch \
            file://0002-configure.ac-Fix-wayland-scanner-and-protocols-locat.patch \
            file://0003-modesetting-Fix-16-bit-depth-bpp-mode.patch \
            file://0003-Remove-check-for-useSIGIO-option.patch \
            file://CVE-2017-10971-1.patch \
            file://CVE-2017-10971-2.patch \
            file://CVE-2017-10971-3.patch \
            "
SRC_URI[md5sum] = "015d2fc4b9f2bfe7a626edb63a62c65e"
SRC_URI[sha256sum] = "677a8166e03474719238dfe396ce673c4234735464d6dadf2959b600d20e5a98"

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
