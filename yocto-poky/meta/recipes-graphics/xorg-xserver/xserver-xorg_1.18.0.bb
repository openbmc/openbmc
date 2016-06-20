require xserver-xorg.inc

SRC_URI += "file://configure.ac-Use-libsystemd-in-REQUIRED_LIBS-check.patch \
            file://musl-arm-inb-outb.patch \
            file://modesetting_libdrm_requirements.patch \
           "
SRC_URI[md5sum] = "3c1c1057d3ad27380d8dd87ffcc182cd"
SRC_URI[sha256sum] = "195670819695d9cedd8dde95fbe069be0d0f488a77797a2d409f9f702daf312e"

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
