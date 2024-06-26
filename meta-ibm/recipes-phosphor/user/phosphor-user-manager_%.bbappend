FILESEXTRAPATHS:prepend := "${THISDIR}/user:"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"


SRC_URI += " file://update_admin_account.sh"
SYSTEMD_SERVICE:${PN}:append = " update_admin_account.service"

do_install:append() {
  install -m 0755 ${UNPACKDIR}/update_admin_account.sh ${D}${bindir}/update_admin_account.sh
}


