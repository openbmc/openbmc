FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG_append_rainier = " host-dump-transport-pldm"
PACKAGECONFIG_append_witherspoon-tacoma = " host-dump-transport-pldm"

PACKAGECONFIG_append_rainier = " openpower-dumps-extension"
PACKAGECONFIG_append_witherspoon-tacoma = " openpower-dumps-extension"

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

IBM_INSTALL_POSTFUNCS = "install_ibm_plugins link_ibm_plugins"
IBM_INSTALL_POSTFUNCS_rainier += "install_dreport_header"

do_install[postfuncs] += "${IBM_INSTALL_POSTFUNCS}"
