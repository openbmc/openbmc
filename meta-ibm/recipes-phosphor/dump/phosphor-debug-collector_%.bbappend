FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://plugins.d/ibm_elogall"

install_ibm_plugins() {

    install -m 0755 ${WORKDIR}/plugins.d/ibm_elogall ${D}${dreport_plugin_dir}

}

#Link in the plugins so dreport run them at the appropriate time
python link_ibm_plugins() {

    workdir = d.getVar('WORKDIR', True)
    script = os.path.join(workdir, 'plugins.d', 'ibm_elogall')
    install_dreport_user_script(script, d)

}

do_install[postfuncs] += "install_ibm_plugins"
do_install[postfuncs] += "link_ibm_plugins"
