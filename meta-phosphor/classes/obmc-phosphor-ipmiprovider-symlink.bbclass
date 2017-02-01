# Common code for recipes that create IPMI provider libraries

LIBDIR = "${D}/${libdir}/ipmid-providers/"
HOSTIPMI_LIBDIR = "${D}/${libdir}/host-ipmid/"
NETIPMI_LIBDIR = "${D}/${libdir}/net-ipmid/"

python symlink_create_postinstall() {
    def install_symlink(d, libname, install_dir):
        import glob;

        if not os.path.exists(install_dir):
            os.makedirs(install_dir)

        lib_dir = d.getVar('LIBDIR', True)

        # find the library extension libxxx.so.?
        install_file = lib_dir + libname + ".?"

        filelist = glob.glob(install_file);

        # get the library name
        path, file = os.path.split(filelist[0])
        os.chdir(install_dir)
        source = "../ipmid-providers/" + file

        # create the symlink
        os.symlink(source, file)

    for libname in listvar_to_list(d, 'HOSTIPMI_PROVIDER_LIBRARY'):
        install_dir = d.getVar('HOSTIPMI_LIBDIR', True)
        install_symlink(d, libname, install_dir)

    for libname in listvar_to_list(d, 'NETIPMI_PROVIDER_LIBRARY'):
        install_dir = d.getVar('NETIPMI_LIBDIR', True)
        install_symlink(d, libname, install_dir)
}
do_install[postfuncs] += "symlink_create_postinstall"