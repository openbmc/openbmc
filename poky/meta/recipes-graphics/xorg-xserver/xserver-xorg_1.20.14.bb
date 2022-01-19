require xserver-xorg.inc

SRC_URI += "file://0001-xf86pciBus.c-use-Intel-ddx-only-for-pre-gen4-hardwar.patch \
           file://pkgconfig.patch \
           file://0001-test-xtest-Initialize-array-with-braces.patch \
           file://sdksyms-no-build-path.patch \
           file://0001-drmmode_display.c-add-missing-mi.h-include.patch \
           file://0001-Avoid-duplicate-definitions-of-IOPortBase.patch \
           file://0001-Fix-segfault-on-probing-a-non-PCI-platform-device-on.patch \
           "
SRC_URI[sha256sum] = "5cc5b70b9be89443e2594b93656c60bd5e82cd7f01deb4ce4faf81dcf546a16b"

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
