FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

install_openpower_plugins() {
    install ${S}/tools/dreport.d/openpower.d/plugins.d/* ${D}${dreport_plugin_dir}
}

#Link the plugins so that dreport can run them based on dump type
python link_openpower_plugins() {
    source = d.getVar('S', True)
    source_path = os.path.join(source, "tools", "dreport.d", "openpower.d", "plugins.d")
    op_plugins = os.listdir(source_path)
    for op_plugin in op_plugins:
        op_plugin_name = os.path.join(source_path, op_plugin)
        install_dreport_user_script(op_plugin_name, d)
}

DEBUG_COLLECTOR_INSTALL_POSTFUNCS ?= ""
DEBUG_COLLECTOR_INSTALL_POSTFUNCS:df-openpower ?= "install_openpower_plugins link_openpower_plugins"

do_install[postfuncs] += "${DEBUG_COLLECTOR_INSTALL_POSTFUNCS}"
