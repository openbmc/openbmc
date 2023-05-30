FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# platform configuration files
SRC_URI += "file://hpe-publish-uefi-version.sh"

do_install:append() {
   install -D ${WORKDIR}/hpe-publish-uefi-version ${D}/usr/bin/hpe-publish-uefi-version
}
