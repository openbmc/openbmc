FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://fw_env.config "

do_install:append() {
   install -d ${D}${sysconfdir}
   install -m 0644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}/
}
