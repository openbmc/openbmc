FILESEXTRAPATHS_prepend_zynqmp := "${THISDIR}/files:"

SRC_URI_append_zynqmp = " file://weston.ini"

do_install_append_zynqmp() {
    install -Dm 0700 ${WORKDIR}/weston.ini ${D}/${sysconfdir}/xdg/weston/weston.ini
}
