def get_package_manager(d, root_path):
    """
    Returns an OE package manager that can install packages in root_path.
    """
    from oe.package_manager import RpmPM, OpkgPM, DpkgPM

    pkg_class = d.getVar("IMAGE_PKGTYPE", True)
    if pkg_class == "rpm":
        pm = RpmPM(d,
                   root_path,
                   d.getVar('TARGET_VENDOR', True))
        pm.create_configs()

    elif pkg_class == "ipk":
        pm = OpkgPM(d,
                    root_path,
                    d.getVar("IPKGCONF_TARGET", True),
                    d.getVar("ALL_MULTILIB_PACKAGE_ARCHS", True))

    elif pkg_class == "deb":
        pm = DpkgPM(d,
                    root_path,
                    d.getVar('PACKAGE_ARCHS', True),
                    d.getVar('DPKG_ARCH', True))

    pm.write_index()
    pm.update()

    return pm
