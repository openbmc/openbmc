inherit package

IMAGE_PKGTYPE ?= "tar"

python do_package_tar () {
    import subprocess
    workdir = d.getVar('WORKDIR', True)
    if not workdir:
        bb.error("WORKDIR not defined, unable to package")
        return

    outdir = d.getVar('DEPLOY_DIR_TAR', True)
    if not outdir:
        bb.error("DEPLOY_DIR_TAR not defined, unable to package")
        return

    dvar = d.getVar('D', True)
    if not dvar:
        bb.error("D not defined, unable to package")
        return

    packages = d.getVar('PACKAGES', True)
    if not packages:
        bb.debug(1, "PACKAGES not defined, nothing to package")
        return

    pkgdest = d.getVar('PKGDEST', True)

    bb.utils.mkdirhier(outdir)
    bb.utils.mkdirhier(dvar)

    for pkg in packages.split():
        localdata = bb.data.createCopy(d)
        root = "%s/%s" % (pkgdest, pkg)

        overrides = localdata.getVar('OVERRIDES', False)
        localdata.setVar('OVERRIDES', '%s:%s' % (overrides, pkg))
        bb.data.update_data(localdata)

        bb.utils.mkdirhier(root)
        basedir = os.path.dirname(root)
        tarfn = localdata.expand("${DEPLOY_DIR_TAR}/${PKG}-${PKGV}-${PKGR}.tar.gz")
        os.chdir(root)
        dlist = os.listdir(root)
        if not dlist:
            bb.note("Not creating empty archive for %s-%s-%s" % (pkg, localdata.getVar('PKGV', True), localdata.getVar('PKGR', True)))
            continue
        args = "tar -cz --exclude=CONTROL --exclude=DEBIAN -f".split()
        ret = subprocess.call(args + [tarfn] + dlist)
        if ret != 0:
            bb.error("Creation of tar %s failed." % tarfn)
}

python () {
    if d.getVar('PACKAGES', True) != '':
        deps = (d.getVarFlag('do_package_write_tar', 'depends', True) or "").split()
        deps.append('tar-native:do_populate_sysroot')
        deps.append('virtual/fakeroot-native:do_populate_sysroot')
        d.setVarFlag('do_package_write_tar', 'depends', " ".join(deps))
        d.setVarFlag('do_package_write_tar', 'fakeroot', "1")
}


python do_package_write_tar () {
    bb.build.exec_func("read_subpackage_metadata", d)
    bb.build.exec_func("do_package_tar", d)
}
do_package_write_tar[dirs] = "${D}"
addtask package_write_tar before do_build after do_packagedata do_package
