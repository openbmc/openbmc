inherit package

IMAGE_PKGTYPE ?= "ipk"

IPKGCONF_TARGET = "${WORKDIR}/opkg.conf"
IPKGCONF_SDK =  "${WORKDIR}/opkg-sdk.conf"

PKGWRITEDIRIPK = "${WORKDIR}/deploy-ipks"

# Program to be used to build opkg packages
OPKGBUILDCMD ??= 'opkg-build -Z xz -a "${XZ_DEFAULTS}"'

OPKG_ARGS += "--force_postinstall --prefer-arch-to-version"
OPKG_ARGS += "${@['', '--no-install-recommends'][d.getVar("NO_RECOMMENDATIONS") == "1"]}"
OPKG_ARGS += "${@['', '--add-exclude ' + ' --add-exclude '.join((d.getVar('PACKAGE_EXCLUDE') or "").split())][(d.getVar("PACKAGE_EXCLUDE") or "").strip() != ""]}"

OPKGLIBDIR ??= "${localstatedir}/lib"

python do_package_ipk () {
    workdir = d.getVar('WORKDIR')
    outdir = d.getVar('PKGWRITEDIRIPK')
    tmpdir = d.getVar('TMPDIR')
    pkgdest = d.getVar('PKGDEST')
    if not workdir or not outdir or not tmpdir:
        bb.error("Variables incorrectly set, unable to package")
        return

    packages = d.getVar('PACKAGES')
    if not packages or packages == '':
        bb.debug(1, "No packages; nothing to do")
        return

    # We're about to add new packages so the index needs to be checked
    # so remove the appropriate stamp file.
    if os.access(os.path.join(tmpdir, "stamps", "IPK_PACKAGE_INDEX_CLEAN"), os.R_OK):
        os.unlink(os.path.join(tmpdir, "stamps", "IPK_PACKAGE_INDEX_CLEAN"))

    oe.utils.multiprocess_launch(ipk_write_pkg, packages.split(), d, extraargs=(d,))
}
do_package_ipk[vardeps] += "ipk_write_pkg"
do_package_ipk[vardepsexclude] = "BB_NUMBER_THREADS"

def ipk_write_pkg(pkg, d):
    import re, copy
    import subprocess
    import textwrap
    import collections
    import glob

    def cleanupcontrol(root):
        for p in ['CONTROL', 'DEBIAN']:
            p = os.path.join(root, p)
            if os.path.exists(p):
                bb.utils.prunedir(p)

    outdir = d.getVar('PKGWRITEDIRIPK')
    pkgdest = d.getVar('PKGDEST')
    recipesource = os.path.basename(d.getVar('FILE'))

    localdata = bb.data.createCopy(d)
    root = "%s/%s" % (pkgdest, pkg)

    lf = bb.utils.lockfile(root + ".lock")
    try:
        localdata.setVar('ROOT', '')
        localdata.setVar('ROOT_%s' % pkg, root)
        pkgname = localdata.getVar('PKG_%s' % pkg)
        if not pkgname:
            pkgname = pkg
        localdata.setVar('PKG', pkgname)

        localdata.setVar('OVERRIDES', d.getVar("OVERRIDES", False) + ":" + pkg)

        basedir = os.path.join(os.path.dirname(root))
        arch = localdata.getVar('PACKAGE_ARCH')

        if localdata.getVar('IPK_HIERARCHICAL_FEED', False) == "1":
            # Spread packages across subdirectories so each isn't too crowded
            if pkgname.startswith('lib'):
                pkg_prefix = 'lib' + pkgname[3]
            else:
                pkg_prefix = pkgname[0]

            # Keep -dbg, -dev, -doc, -staticdev, -locale and -locale-* packages
            # together. These package suffixes are taken from the definitions of
            # PACKAGES and PACKAGES_DYNAMIC in meta/conf/bitbake.conf
            if pkgname[-4:] in ('-dbg', '-dev', '-doc'):
                pkg_subdir = pkgname[:-4]
            elif pkgname.endswith('-staticdev'):
                pkg_subdir = pkgname[:-10]
            elif pkgname.endswith('-locale'):
                pkg_subdir = pkgname[:-7]
            elif '-locale-' in pkgname:
                pkg_subdir = pkgname[:pkgname.find('-locale-')]
            else:
                pkg_subdir = pkgname

            pkgoutdir = "%s/%s/%s/%s" % (outdir, arch, pkg_prefix, pkg_subdir)
        else:
            pkgoutdir = "%s/%s" % (outdir, arch)

        bb.utils.mkdirhier(pkgoutdir)
        os.chdir(root)
        cleanupcontrol(root)
        g = glob.glob('*')
        if not g and localdata.getVar('ALLOW_EMPTY', False) != "1":
            bb.note("Not creating empty archive for %s-%s-%s" % (pkg, localdata.getVar('PKGV'), localdata.getVar('PKGR')))
            return

        controldir = os.path.join(root, 'CONTROL')
        bb.utils.mkdirhier(controldir)
        ctrlfile = open(os.path.join(controldir, 'control'), 'w')

        fields = []
        pe = d.getVar('PKGE')
        if pe and int(pe) > 0:
            fields.append(["Version: %s:%s-%s\n", ['PKGE', 'PKGV', 'PKGR']])
        else:
            fields.append(["Version: %s-%s\n", ['PKGV', 'PKGR']])
        fields.append(["Description: %s\n", ['DESCRIPTION']])
        fields.append(["Section: %s\n", ['SECTION']])
        fields.append(["Priority: %s\n", ['PRIORITY']])
        fields.append(["Maintainer: %s\n", ['MAINTAINER']])
        fields.append(["License: %s\n", ['LICENSE']])
        fields.append(["Architecture: %s\n", ['PACKAGE_ARCH']])
        fields.append(["OE: %s\n", ['PN']])
        if d.getVar('HOMEPAGE'):
            fields.append(["Homepage: %s\n", ['HOMEPAGE']])

        def pullData(l, d):
            l2 = []
            for i in l:
                l2.append(d.getVar(i))
            return l2

        ctrlfile.write("Package: %s\n" % pkgname)
        # check for required fields
        for (c, fs) in fields:
            for f in fs:
                if localdata.getVar(f, False) is None:
                    raise KeyError(f)
            # Special behavior for description...
            if 'DESCRIPTION' in fs:
                summary = localdata.getVar('SUMMARY') or localdata.getVar('DESCRIPTION') or "."
                ctrlfile.write('Description: %s\n' % summary)
                description = localdata.getVar('DESCRIPTION') or "."
                description = textwrap.dedent(description).strip()
                if '\\n' in description:
                    # Manually indent: multiline description includes a leading space
                    for t in description.split('\\n'):
                        ctrlfile.write(' %s\n' % (t.strip() or ' .'))
                else:
                    # Auto indent
                    ctrlfile.write('%s\n' % textwrap.fill(description, width=74, initial_indent=' ', subsequent_indent=' '))
            else:
                ctrlfile.write(c % tuple(pullData(fs, localdata)))

        custom_fields_chunk = get_package_additional_metadata("ipk", localdata)
        if custom_fields_chunk is not None:
            ctrlfile.write(custom_fields_chunk)
            ctrlfile.write("\n")

        mapping_rename_hook(localdata)

        def debian_cmp_remap(var):
            # In debian '>' and '<' do not mean what it appears they mean
            #   '<' = less or equal
            #   '>' = greater or equal
            # adjust these to the '<<' and '>>' equivalents
            #
            for dep in var:
                for i, v in enumerate(var[dep]):
                    if (v or "").startswith("< "):
                        var[dep][i] = var[dep][i].replace("< ", "<< ")
                    elif (v or "").startswith("> "):
                        var[dep][i] = var[dep][i].replace("> ", ">> ")

        rdepends = bb.utils.explode_dep_versions2(localdata.getVar("RDEPENDS") or "")
        debian_cmp_remap(rdepends)
        rrecommends = bb.utils.explode_dep_versions2(localdata.getVar("RRECOMMENDS") or "")
        debian_cmp_remap(rrecommends)
        rsuggests = bb.utils.explode_dep_versions2(localdata.getVar("RSUGGESTS") or "")
        debian_cmp_remap(rsuggests)
        # Deliberately drop version information here, not wanted/supported by ipk
        rprovides = dict.fromkeys(bb.utils.explode_dep_versions2(localdata.getVar("RPROVIDES") or ""), [])
        rprovides = collections.OrderedDict(sorted(rprovides.items(), key=lambda x: x[0]))
        debian_cmp_remap(rprovides)
        rreplaces = bb.utils.explode_dep_versions2(localdata.getVar("RREPLACES") or "")
        debian_cmp_remap(rreplaces)
        rconflicts = bb.utils.explode_dep_versions2(localdata.getVar("RCONFLICTS") or "")
        debian_cmp_remap(rconflicts)

        if rdepends:
            ctrlfile.write("Depends: %s\n" % bb.utils.join_deps(rdepends))
        if rsuggests:
            ctrlfile.write("Suggests: %s\n" % bb.utils.join_deps(rsuggests))
        if rrecommends:
            ctrlfile.write("Recommends: %s\n" % bb.utils.join_deps(rrecommends))
        if rprovides:
            ctrlfile.write("Provides: %s\n" % bb.utils.join_deps(rprovides))
        if rreplaces:
            ctrlfile.write("Replaces: %s\n" % bb.utils.join_deps(rreplaces))
        if rconflicts:
            ctrlfile.write("Conflicts: %s\n" % bb.utils.join_deps(rconflicts))
        ctrlfile.write("Source: %s\n" % recipesource)
        ctrlfile.close()

        for script in ["preinst", "postinst", "prerm", "postrm"]:
            scriptvar = localdata.getVar('pkg_%s' % script)
            if not scriptvar:
                continue
            scriptfile = open(os.path.join(controldir, script), 'w')
            scriptfile.write(scriptvar)
            scriptfile.close()
            os.chmod(os.path.join(controldir, script), 0o755)

        conffiles_str = ' '.join(get_conffiles(pkg, d))
        if conffiles_str:
            conffiles = open(os.path.join(controldir, 'conffiles'), 'w')
            for f in conffiles_str.split():
                if os.path.exists(oe.path.join(root, f)):
                    conffiles.write('%s\n' % f)
            conffiles.close()

        os.chdir(basedir)
        subprocess.check_output("PATH=\"%s\" %s %s %s" % (localdata.getVar("PATH"),
                                                          d.getVar("OPKGBUILDCMD"), pkg, pkgoutdir),
                                stderr=subprocess.STDOUT,
                                shell=True)

        if d.getVar('IPK_SIGN_PACKAGES') == '1':
            ipkver = "%s-%s" % (d.getVar('PKGV'), d.getVar('PKGR'))
            ipk_to_sign = "%s/%s_%s_%s.ipk" % (pkgoutdir, pkgname, ipkver, d.getVar('PACKAGE_ARCH'))
            sign_ipk(d, ipk_to_sign)

    finally:
        cleanupcontrol(root)
        bb.utils.unlockfile(lf)

# Have to list any variables referenced as X_<pkg> that aren't in pkgdata here
IPKEXTRAVARS = "PRIORITY MAINTAINER PACKAGE_ARCH HOMEPAGE"
ipk_write_pkg[vardeps] += "${@gen_packagevar(d, 'IPKEXTRAVARS')}"

# Otherwise allarch packages may change depending on override configuration
ipk_write_pkg[vardepsexclude] = "OVERRIDES"


SSTATETASKS += "do_package_write_ipk"
do_package_write_ipk[sstate-inputdirs] = "${PKGWRITEDIRIPK}"
do_package_write_ipk[sstate-outputdirs] = "${DEPLOY_DIR_IPK}"

python do_package_write_ipk_setscene () {
    tmpdir = d.getVar('TMPDIR')

    if os.access(os.path.join(tmpdir, "stamps", "IPK_PACKAGE_INDEX_CLEAN"), os.R_OK):
        os.unlink(os.path.join(tmpdir, "stamps", "IPK_PACKAGE_INDEX_CLEAN"))

    sstate_setscene(d)
}
addtask do_package_write_ipk_setscene

python () {
    if d.getVar('PACKAGES') != '':
        deps = ' opkg-utils-native:do_populate_sysroot virtual/fakeroot-native:do_populate_sysroot xz-native:do_populate_sysroot'
        d.appendVarFlag('do_package_write_ipk', 'depends', deps)
        d.setVarFlag('do_package_write_ipk', 'fakeroot', "1")
}

python do_package_write_ipk () {
    bb.build.exec_func("read_subpackage_metadata", d)
    bb.build.exec_func("do_package_ipk", d)
}
do_package_write_ipk[dirs] = "${PKGWRITEDIRIPK}"
do_package_write_ipk[cleandirs] = "${PKGWRITEDIRIPK}"
do_package_write_ipk[umask] = "022"
do_package_write_ipk[depends] += "${@oe.utils.build_depends_string(d.getVar('PACKAGE_WRITE_DEPS'), 'do_populate_sysroot')}"
EPOCHTASK ??= ""
addtask package_write_ipk after do_packagedata do_package ${EPOCHTASK}

PACKAGEINDEXDEPS += "opkg-utils-native:do_populate_sysroot"
PACKAGEINDEXDEPS += "opkg-native:do_populate_sysroot"

do_build[recrdeptask] += "do_package_write_ipk"
