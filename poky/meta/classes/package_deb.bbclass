#
# Copyright 2006-2008 OpenedHand Ltd.
#

inherit package

IMAGE_PKGTYPE ?= "deb"

DPKG_BUILDCMD ??= "dpkg-deb"

DPKG_ARCH ?= "${@debian_arch_map(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'))}"
DPKG_ARCH[vardepvalue] = "${DPKG_ARCH}"

PKGWRITEDIRDEB = "${WORKDIR}/deploy-debs"

APTCONF_TARGET = "${WORKDIR}"

APT_ARGS = "${@['', '--no-install-recommends'][d.getVar("NO_RECOMMENDATIONS") == "1"]}"

def debian_arch_map(arch, tune):
    tune_features = tune.split()
    if arch == "allarch":
        return "all"
    if arch in ["i586", "i686"]:
        return "i386"
    if arch == "x86_64":
        if "mx32" in tune_features:
            return "x32"
        return "amd64"
    if arch.startswith("mips"):
        endian = ["el", ""]["bigendian" in tune_features]
        if "n64" in tune_features:
            return "mips64" + endian
        if "n32" in tune_features:
            return "mipsn32" + endian
        return "mips" + endian
    if arch == "powerpc":
        return arch + ["", "spe"]["spe" in tune_features]
    if arch == "aarch64":
        return "arm64"
    if arch == "arm":
        return arch + ["el", "hf"]["callconvention-hard" in tune_features]
    return arch

python do_package_deb () {
    packages = d.getVar('PACKAGES')
    if not packages:
        bb.debug(1, "PACKAGES not defined, nothing to package")
        return

    tmpdir = d.getVar('TMPDIR')
    if os.access(os.path.join(tmpdir, "stamps", "DEB_PACKAGE_INDEX_CLEAN"),os.R_OK):
        os.unlink(os.path.join(tmpdir, "stamps", "DEB_PACKAGE_INDEX_CLEAN"))

    oe.utils.multiprocess_launch(deb_write_pkg, packages.split(), d, extraargs=(d,))
}
do_package_deb[vardeps] += "deb_write_pkg"
do_package_deb[vardepsexclude] = "BB_NUMBER_THREADS"

def deb_write_pkg(pkg, d):
    import re, copy
    import textwrap
    import subprocess
    import collections
    import codecs

    outdir = d.getVar('PKGWRITEDIRDEB')
    pkgdest = d.getVar('PKGDEST')

    def cleanupcontrol(root):
        for p in ['CONTROL', 'DEBIAN']:
            p = os.path.join(root, p)
            if os.path.exists(p):
                bb.utils.prunedir(p)

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

        pkgoutdir = os.path.join(outdir, localdata.getVar('PACKAGE_ARCH'))
        bb.utils.mkdirhier(pkgoutdir)

        os.chdir(root)
        cleanupcontrol(root)
        from glob import glob
        g = glob('*')
        if not g and localdata.getVar('ALLOW_EMPTY', False) != "1":
            bb.note("Not creating empty archive for %s-%s-%s" % (pkg, localdata.getVar('PKGV'), localdata.getVar('PKGR')))
            return

        controldir = os.path.join(root, 'DEBIAN')
        bb.utils.mkdirhier(controldir)
        os.chmod(controldir, 0o755)

        ctrlfile = codecs.open(os.path.join(controldir, 'control'), 'w', 'utf-8')

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
        fields.append(["Architecture: %s\n", ['DPKG_ARCH']])
        fields.append(["OE: %s\n", ['PN']])
        fields.append(["PackageArch: %s\n", ['PACKAGE_ARCH']])
        if d.getVar('HOMEPAGE'):
            fields.append(["Homepage: %s\n", ['HOMEPAGE']])

        # Package, Version, Maintainer, Description - mandatory
        # Section, Priority, Essential, Architecture, Source, Depends, Pre-Depends, Recommends, Suggests, Conflicts, Replaces, Provides - Optional


        def pullData(l, d):
            l2 = []
            for i in l:
                data = d.getVar(i)
                if data is None:
                    raise KeyError(i)
                if i == 'DPKG_ARCH' and d.getVar('PACKAGE_ARCH') == 'all':
                    data = 'all'
                elif i == 'PACKAGE_ARCH' or i == 'DPKG_ARCH':
                   # The params in deb package control don't allow character
                   # `_', so change the arch's `_' to `-'. Such as `x86_64'
                   # -->`x86-64'
                   data = data.replace('_', '-')
                l2.append(data)
            return l2

        ctrlfile.write("Package: %s\n" % pkgname)
        if d.getVar('PACKAGE_ARCH') == "all":
            ctrlfile.write("Multi-Arch: foreign\n")
        # check for required fields
        for (c, fs) in fields:
            # Special behavior for description...
            if 'DESCRIPTION' in fs:
                 summary = localdata.getVar('SUMMARY') or localdata.getVar('DESCRIPTION') or "."
                 ctrlfile.write('Description: %s\n' % summary)
                 description = localdata.getVar('DESCRIPTION') or "."
                 description = textwrap.dedent(description).strip()
                 if '\\n' in description:
                     # Manually indent
                     for t in description.split('\\n'):
                         ctrlfile.write(' %s\n' % (t.strip() or '.'))
                 else:
                     # Auto indent
                     ctrlfile.write('%s\n' % textwrap.fill(description.strip(), width=74, initial_indent=' ', subsequent_indent=' '))

            else:
                 ctrlfile.write(c % tuple(pullData(fs, localdata)))

        # more fields

        custom_fields_chunk = get_package_additional_metadata("deb", localdata)
        if custom_fields_chunk:
            ctrlfile.write(custom_fields_chunk)
            ctrlfile.write("\n")

        mapping_rename_hook(localdata)

        def debian_cmp_remap(var):
            # dpkg does not allow for '(', ')' or ':' in a dependency name
            # Replace any instances of them with '__'
            #
            # In debian '>' and '<' do not mean what it appears they mean
            #   '<' = less or equal
            #   '>' = greater or equal
            # adjust these to the '<<' and '>>' equivalents
            #
            for dep in list(var.keys()):
                if '(' in dep or '/' in dep:
                    newdep = re.sub(r'[(:)/]', '__', dep)
                    if newdep.startswith("__"):
                        newdep = "A" + newdep
                    if newdep != dep:
                        var[newdep] = var[dep]
                        del var[dep]
            for dep in var:
                for i, v in enumerate(var[dep]):
                    if (v or "").startswith("< "):
                        var[dep][i] = var[dep][i].replace("< ", "<< ")
                    elif (v or "").startswith("> "):
                        var[dep][i] = var[dep][i].replace("> ", ">> ")

        rdepends = bb.utils.explode_dep_versions2(localdata.getVar("RDEPENDS") or "")
        debian_cmp_remap(rdepends)
        for dep in list(rdepends.keys()):
                if dep == pkg:
                        del rdepends[dep]
                        continue
                if '*' in dep:
                        del rdepends[dep]
        rrecommends = bb.utils.explode_dep_versions2(localdata.getVar("RRECOMMENDS") or "")
        debian_cmp_remap(rrecommends)
        for dep in list(rrecommends.keys()):
                if '*' in dep:
                        del rrecommends[dep]
        rsuggests = bb.utils.explode_dep_versions2(localdata.getVar("RSUGGESTS") or "")
        debian_cmp_remap(rsuggests)
        # Deliberately drop version information here, not wanted/supported by deb
        rprovides = dict.fromkeys(bb.utils.explode_dep_versions2(localdata.getVar("RPROVIDES") or ""), [])
        # Remove file paths if any from rprovides, debian does not support custom providers
        for key in list(rprovides.keys()):
            if key.startswith('/'):
                del rprovides[key]
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
        ctrlfile.close()

        for script in ["preinst", "postinst", "prerm", "postrm"]:
            scriptvar = localdata.getVar('pkg_%s' % script)
            if not scriptvar:
                continue
            scriptvar = scriptvar.strip()
            scriptfile = open(os.path.join(controldir, script), 'w')

            if scriptvar.startswith("#!"):
                pos = scriptvar.find("\n") + 1
                scriptfile.write(scriptvar[:pos])
            else:
                pos = 0
                scriptfile.write("#!/bin/sh\n")

            # Prevent the prerm/postrm scripts from being run during an upgrade
            if script in ('prerm', 'postrm'):
                scriptfile.write('[ "$1" != "upgrade" ] || exit 0\n')

            scriptfile.write(scriptvar[pos:])
            scriptfile.write('\n')
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
        subprocess.check_output("PATH=\"%s\" %s -b %s %s" % (localdata.getVar("PATH"), localdata.getVar("DPKG_BUILDCMD"),
                                                             root, pkgoutdir),
                                stderr=subprocess.STDOUT,
                                shell=True)

    finally:
        cleanupcontrol(root)
        bb.utils.unlockfile(lf)

# Otherwise allarch packages may change depending on override configuration
deb_write_pkg[vardepsexclude] = "OVERRIDES"

# Have to list any variables referenced as X_<pkg> that aren't in pkgdata here
DEBEXTRAVARS = "PKGV PKGR PKGV DESCRIPTION SECTION PRIORITY MAINTAINER DPKG_ARCH PN HOMEPAGE PACKAGE_ADD_METADATA_DEB"
do_package_write_deb[vardeps] += "${@gen_packagevar(d, 'DEBEXTRAVARS')}"

SSTATETASKS += "do_package_write_deb"
do_package_write_deb[sstate-inputdirs] = "${PKGWRITEDIRDEB}"
do_package_write_deb[sstate-outputdirs] = "${DEPLOY_DIR_DEB}"

python do_package_write_deb_setscene () {
    tmpdir = d.getVar('TMPDIR')

    if os.access(os.path.join(tmpdir, "stamps", "DEB_PACKAGE_INDEX_CLEAN"),os.R_OK):
        os.unlink(os.path.join(tmpdir, "stamps", "DEB_PACKAGE_INDEX_CLEAN"))

    sstate_setscene(d)
}
addtask do_package_write_deb_setscene

python () {
    if d.getVar('PACKAGES') != '':
        deps = ' dpkg-native:do_populate_sysroot virtual/fakeroot-native:do_populate_sysroot'
        d.appendVarFlag('do_package_write_deb', 'depends', deps)
        d.setVarFlag('do_package_write_deb', 'fakeroot', "1")
}

python do_package_write_deb () {
    bb.build.exec_func("read_subpackage_metadata", d)
    bb.build.exec_func("do_package_deb", d)
}
do_package_write_deb[dirs] = "${PKGWRITEDIRDEB}"
do_package_write_deb[cleandirs] = "${PKGWRITEDIRDEB}"
do_package_write_deb[depends] += "${@oe.utils.build_depends_string(d.getVar('PACKAGE_WRITE_DEPS'), 'do_populate_sysroot')}"
addtask package_write_deb after do_packagedata do_package


PACKAGEINDEXDEPS += "dpkg-native:do_populate_sysroot"
PACKAGEINDEXDEPS += "apt-native:do_populate_sysroot"

do_build[recrdeptask] += "do_package_write_deb"
