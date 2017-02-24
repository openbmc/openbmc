# Common code for recipes that implement Phosphor IPMI Whitelist
# packages

WHITELIST ?= "${PN}.conf"
INSTALLDIR = "${D}${datadir}/phosphor-ipmi-host/"

python phosphor_ipmi_host_whitelist_postinstall() {
    def install_whitelist(d):
        # Create the install directory if needed
        whitelist_file = d.getVar('WHITELIST', True)
        install_dir = d.getVar('INSTALLDIR', True)
        if not os.path.exists(install_dir):
            os.makedirs(install_dir)
        install_file = os.path.join(install_dir, whitelist_file)

        # Search for conf file in FILESPATH
        searchpaths = d.getVar('FILESPATH', True)
        path = bb.utils.which(searchpaths, whitelist_file)
        if not os.path.isfile(path):
            bb.fatal('Did not find conf file "%s"' % whitelist_file)

        # Copy the conf file into install directory
        bb.utils.copyfile(path, install_file)

    install_whitelist(d)
}
do_install[postfuncs] += "phosphor_ipmi_host_whitelist_postinstall"
