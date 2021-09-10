python read_subpackage_metadata () {
    import oe.packagedata

    vars = {
        "PN" : d.getVar('PN'), 
        "PE" : d.getVar('PE'), 
        "PV" : d.getVar('PV'),
        "PR" : d.getVar('PR'),
    }

    data = oe.packagedata.read_pkgdata(vars["PN"], d)

    for key in data.keys():
        d.setVar(key, data[key])

    for pkg in d.getVar('PACKAGES').split():
        sdata = oe.packagedata.read_subpkgdata(pkg, d)
        for key in sdata.keys():
            if key in vars:
                if sdata[key] != vars[key]:
                    if key == "PN":
                        bb.fatal("Recipe %s is trying to create package %s which was already written by recipe %s. This will cause corruption, please resolve this and only provide the package from one recipe or the other or only build one of the recipes." % (vars[key], pkg, sdata[key]))
                    bb.fatal("Recipe %s is trying to change %s from '%s' to '%s'. This will cause do_package_write_* failures since the incorrect data will be used and they will be unable to find the right workdir." % (vars["PN"], key, vars[key], sdata[key]))
                continue
            #
            # If we set unsuffixed variables here there is a chance they could clobber override versions
            # of that variable, e.g. DESCRIPTION could clobber DESCRIPTION:<pkgname>
            # We therefore don't clobber for the unsuffixed variable versions
            #
            if key.endswith(":" + pkg):
                d.setVar(key, sdata[key])
            else:
                d.setVar(key, sdata[key], parsing=True)
}
