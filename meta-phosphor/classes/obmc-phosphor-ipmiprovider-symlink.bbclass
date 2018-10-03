# Common code for recipes that create IPMI provider libraries

inherit obmc-phosphor-utils

# This LIBDIR is searched for the libraries.
LIBDIR = "${D}/${libdir}/ipmid-providers/"

# The symlinks are installed in the following directories depending on the
# variable.
HOSTIPMI_LIBDIR = "${D}/${libdir}/host-ipmid/"
NETIPMI_LIBDIR = "${D}/${libdir}/net-ipmid/"
BLOBIPMI_LIBDIR = "${D}/${libdir}/blob-ipmid/"

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
        source = "../ipmid-providers/" + file

        # create the symlink
        os.symlink(source, os.path.join(install_dir, file))

    for libname in listvar_to_list(d, 'HOSTIPMI_PROVIDER_LIBRARY'):
        install_dir = d.getVar('HOSTIPMI_LIBDIR', True)
        install_symlink(d, libname, install_dir)

    for libname in listvar_to_list(d, 'NETIPMI_PROVIDER_LIBRARY'):
        install_dir = d.getVar('NETIPMI_LIBDIR', True)
        install_symlink(d, libname, install_dir)

    for libname in listvar_to_list(d, 'BLOBIPMI_PROVIDER_LIBRARY'):
        install_dir = d.getVar('BLOBIPMI_LIBDIR', True)
        install_symlink(d, libname, install_dir)
}
do_install[postfuncs] += "symlink_create_postinstall"
