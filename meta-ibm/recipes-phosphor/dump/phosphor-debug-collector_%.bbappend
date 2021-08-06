FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append:p10bmc = " host-dump-transport-pldm"
PACKAGECONFIG:append:witherspoon-tacoma = " host-dump-transport-pldm"

PACKAGECONFIG:append:p10bmc = " openpower-dumps-extension"
PACKAGECONFIG:append:witherspoon-tacoma = " openpower-dumps-extension"

SRC_URI += "file://plugins.d/ibm_elogall"
SRC_URI += "file://plugins.d/pels"

install_ibm_plugins() {

    install -m 0755 ${WORKDIR}/plugins.d/ibm_elogall ${D}${dreport_plugin_dir}
    install -m 0755 ${WORKDIR}/plugins.d/pels ${D}${dreport_plugin_dir}

}

#Link in the plugins so dreport run them at the appropriate time
python link_ibm_plugins() {

    workdir = d.getVar('WORKDIR', True)
    script = os.path.join(workdir, 'plugins.d', 'ibm_elogall')
    install_dreport_user_script(script, d)

    script = os.path.join(workdir, 'plugins.d', 'pels')
    install_dreport_user_script(script, d)
}

#Install dump header script from dreport/ibm.d to dreport/include.d
install_dreport_header() {
    install -d ${D}${dreport_include_dir}
    install -m 0755 ${S}/tools/dreport.d/ibm.d/gendumpheader ${D}${dreport_include_dir}/
}

#Install ibm bad vpd script from dreport/ibm.d to dreport/plugins.d
install_ibm_bad_vpd() {
    install -d ${D}${dreport_plugin_dir}
    install -m 0755 ${S}/tools/dreport.d/ibm.d/badvpd ${D}${dreport_plugin_dir}
}

#Link in the plugins so dreport run them at the appropriate time based on the plugin type
python link_ibm_bad_vpd() {
    sourcedir = d.getVar('S', True)
    script = os.path.join(sourcedir, "tools", "dreport.d", "ibm.d", "badvpd")
    install_dreport_user_script(script, d)
}

IBM_INSTALL_POSTFUNCS = "install_ibm_plugins link_ibm_plugins"
IBM_INSTALL_POSTFUNCS:p10bmc += "install_dreport_header install_ibm_bad_vpd link_ibm_bad_vpd"

do_install[postfuncs] += "${IBM_INSTALL_POSTFUNCS}"
