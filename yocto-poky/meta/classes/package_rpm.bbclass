inherit package

IMAGE_PKGTYPE ?= "rpm"

RPM="rpm"
RPMBUILD="rpmbuild"

PKGWRITEDIRRPM = "${WORKDIR}/deploy-rpms"

# Maintaining the perfile dependencies has singificant overhead when writing the 
# packages. When set, this value merges them for efficiency.
MERGEPERFILEDEPS = "1"

# Construct per file dependencies file
def write_rpm_perfiledata(srcname, d):
    workdir = d.getVar('WORKDIR', True)
    packages = d.getVar('PACKAGES', True)
    pkgd = d.getVar('PKGD', True)

    def dump_filerdeps(varname, outfile, d):
        outfile.write("#!/usr/bin/env python\n\n")
        outfile.write("# Dependency table\n")
        outfile.write('deps = {\n')
        for pkg in packages.split():
            dependsflist_key = 'FILE' + varname + 'FLIST' + "_" + pkg
            dependsflist = (d.getVar(dependsflist_key, True) or "")
            for dfile in dependsflist.split():
                key = "FILE" + varname + "_" + dfile + "_" + pkg
                depends_dict = bb.utils.explode_dep_versions(d.getVar(key, True) or "")
                file = dfile.replace("@underscore@", "_")
                file = file.replace("@closebrace@", "]")
                file = file.replace("@openbrace@", "[")
                file = file.replace("@tab@", "\t")
                file = file.replace("@space@", " ")
                file = file.replace("@at@", "@")
                outfile.write('"' + pkgd + file + '" : "')
                for dep in depends_dict:
                    ver = depends_dict[dep]
                    if dep and ver:
                        ver = ver.replace("(","")
                        ver = ver.replace(")","")
                        outfile.write(dep + " " + ver + " ")
                    else:
                        outfile.write(dep + " ")
                outfile.write('",\n')
        outfile.write('}\n\n')
        outfile.write("import sys\n")
        outfile.write("while 1:\n")
        outfile.write("\tline = sys.stdin.readline().strip()\n")
        outfile.write("\tif not line:\n")
        outfile.write("\t\tsys.exit(0)\n")
        outfile.write("\tif line in deps:\n")
        outfile.write("\t\tprint(deps[line] + '\\n')\n")

    # OE-core dependencies a.k.a. RPM requires
    outdepends = workdir + "/" + srcname + ".requires"

    try:
        dependsfile = open(outdepends, 'w')
    except OSError:
        raise bb.build.FuncFailed("unable to open spec file for writing.")

    dump_filerdeps('RDEPENDS', dependsfile, d)

    dependsfile.close()
    os.chmod(outdepends, 0755)

    # OE-core / RPM Provides
    outprovides = workdir + "/" + srcname + ".provides"

    try:
        providesfile = open(outprovides, 'w')
    except OSError:
        raise bb.build.FuncFailed("unable to open spec file for writing.")

    dump_filerdeps('RPROVIDES', providesfile, d)

    providesfile.close()
    os.chmod(outprovides, 0755)

    return (outdepends, outprovides)


python write_specfile () {
    import oe.packagedata

    # append information for logs and patches to %prep
    def add_prep(d,spec_files_bottom):
        if d.getVarFlag('ARCHIVER_MODE', 'srpm', True) == '1' and bb.data.inherits_class('archiver', d):
            spec_files_bottom.append('%%prep -n %s' % d.getVar('PN', True) )
            spec_files_bottom.append('%s' % "echo \"include logs and patches, Please check them in SOURCES\"")
            spec_files_bottom.append('')

    # append the name of tarball to key word 'SOURCE' in xxx.spec.
    def tail_source(d):
        if d.getVarFlag('ARCHIVER_MODE', 'srpm', True) == '1' and bb.data.inherits_class('archiver', d):
            ar_outdir = d.getVar('ARCHIVER_OUTDIR', True)
            if not os.path.exists(ar_outdir):
                return
            source_list = os.listdir(ar_outdir)
            source_number = 0
            for source in source_list:
                # The rpmbuild doesn't need the root permission, but it needs
                # to know the file's user and group name, the only user and
                # group in fakeroot is "root" when working in fakeroot.
                f = os.path.join(ar_outdir, source)
                os.chown(f, 0, 0)
                spec_preamble_top.append('Source%s: %s' % (source_number, source))
                source_number += 1
    # We need a simple way to remove the MLPREFIX from the package name,
    # and dependency information...
    def strip_multilib(name, d):
        multilibs = d.getVar('MULTILIBS', True) or ""
        for ext in multilibs.split():
            eext = ext.split(':')
            if len(eext) > 1 and eext[0] == 'multilib' and name and name.find(eext[1] + '-') >= 0:
                name = "".join(name.split(eext[1] + '-'))
        return name

    def strip_multilib_deps(deps, d):
        depends = bb.utils.explode_dep_versions2(deps or "")
        newdeps = {}
        for dep in depends:
            newdeps[strip_multilib(dep, d)] = depends[dep]
        return bb.utils.join_deps(newdeps)

#        ml = d.getVar("MLPREFIX", True)
#        if ml and name and len(ml) != 0 and name.find(ml) == 0:
#            return ml.join(name.split(ml, 1)[1:])
#        return name

    # In RPM, dependencies are of the format: pkg <>= Epoch:Version-Release
    # This format is similar to OE, however there are restrictions on the
    # characters that can be in a field.  In the Version field, "-"
    # characters are not allowed.  "-" is allowed in the Release field.
    #
    # We translate the "-" in the version to a "+", by loading the PKGV
    # from the dependent recipe, replacing the - with a +, and then using
    # that value to do a replace inside of this recipe's dependencies.
    # This preserves the "-" separator between the version and release, as
    # well as any "-" characters inside of the release field.
    #
    # All of this has to happen BEFORE the mapping_rename_hook as
    # after renaming we cannot look up the dependencies in the packagedata
    # store.
    def translate_vers(varname, d):
        depends = d.getVar(varname, True)
        if depends:
            depends_dict = bb.utils.explode_dep_versions2(depends)
            newdeps_dict = {}
            for dep in depends_dict:
                verlist = []
                for ver in depends_dict[dep]:
                    if '-' in ver:
                        subd = oe.packagedata.read_subpkgdata_dict(dep, d)
                        if 'PKGV' in subd:
                            pv = subd['PV']
                            pkgv = subd['PKGV']
                            reppv = pkgv.replace('-', '+')
                            ver = ver.replace(pv, reppv).replace(pkgv, reppv)
                        if 'PKGR' in subd:
                            # Make sure PKGR rather than PR in ver
                            pr = '-' + subd['PR']
                            pkgr = '-' + subd['PKGR']
                            if pkgr not in ver:
                                ver = ver.replace(pr, pkgr)
                        verlist.append(ver)
                    else:
                        verlist.append(ver)
                newdeps_dict[dep] = verlist
            depends = bb.utils.join_deps(newdeps_dict)
            d.setVar(varname, depends.strip())

    # We need to change the style the dependency from BB to RPM
    # This needs to happen AFTER the mapping_rename_hook
    def print_deps(variable, tag, array, d):
        depends = variable
        if depends:
            depends_dict = bb.utils.explode_dep_versions2(depends)
            for dep in depends_dict:
                for ver in depends_dict[dep]:
                    ver = ver.replace('(', '')
                    ver = ver.replace(')', '')
                    array.append("%s: %s %s" % (tag, dep, ver))
                if not len(depends_dict[dep]):
                    array.append("%s: %s" % (tag, dep))

    def walk_files(walkpath, target, conffiles, dirfiles):
        # We can race against the ipk/deb backends which create CONTROL or DEBIAN directories
        # when packaging. We just ignore these files which are created in 
        # packages-split/ and not package/
        # We have the odd situation where the CONTROL/DEBIAN directory can be removed in the middle of
        # of the walk, the isdir() test would then fail and the walk code would assume its a file
        # hence we check for the names in files too.
        for rootpath, dirs, files in os.walk(walkpath):
            path = rootpath.replace(walkpath, "")
            if path.endswith("DEBIAN") or path.endswith("CONTROL"):
                continue
            path = path.replace("%", "%%%%%%%%")

            # Treat all symlinks to directories as normal files.
            # os.walk() lists them as directories.
            def move_to_files(dir):
                if os.path.islink(os.path.join(rootpath, dir)):
                    files.append(dir)
                    return True
                else:
                    return False
            dirs[:] = [dir for dir in dirs if not move_to_files(dir)]

            # Directory handling can happen in two ways, either DIRFILES is not set at all
            # in which case we fall back to the older behaviour of packages owning all their
            # directories
            if dirfiles is None:
                for dir in dirs:
                    if dir == "CONTROL" or dir == "DEBIAN":
                        continue
                    dir = dir.replace("%", "%%%%%%%%")
                    # All packages own the directories their files are in...
                    target.append('%dir "' + path + '/' + dir + '"')
            else:
                # packages own only empty directories or explict directory.
                # This will prevent the overlapping of security permission.
                if path and not files and not dirs:
                    target.append('%dir "' + path + '"')
                elif path and path in dirfiles:
                    target.append('%dir "' + path + '"')

            for file in files:
                if file == "CONTROL" or file == "DEBIAN":
                    continue
                file = file.replace("%", "%%%%%%%%")
                if conffiles.count(path + '/' + file):
                    target.append('%config "' + path + '/' + file + '"')
                else:
                    target.append('"' + path + '/' + file + '"')

    # Prevent the prerm/postrm scripts from being run during an upgrade
    def wrap_uninstall(scriptvar):
        scr = scriptvar.strip()
        if scr.startswith("#!"):
            pos = scr.find("\n") + 1
        else:
            pos = 0
        scr = scr[:pos] + 'if [ "$1" = "0" ] ; then\n' + scr[pos:] + '\nfi'
        return scr

    def get_perfile(varname, pkg, d):
        deps = []
        dependsflist_key = 'FILE' + varname + 'FLIST' + "_" + pkg
        dependsflist = (d.getVar(dependsflist_key, True) or "")
        for dfile in dependsflist.split():
            key = "FILE" + varname + "_" + dfile + "_" + pkg
            depends = d.getVar(key, True)
            if depends:
                deps.append(depends)
        return " ".join(deps)

    def append_description(spec_preamble, text):
        """
        Add the description to the spec file.
        """
        import textwrap
        dedent_text = textwrap.dedent(text).strip()
        # Bitbake saves "\n" as "\\n"
        if '\\n' in dedent_text:
            for t in dedent_text.split('\\n'):
                spec_preamble.append(t.strip())
        else:
            spec_preamble.append('%s' % textwrap.fill(dedent_text, width=75))

    packages = d.getVar('PACKAGES', True)
    if not packages or packages == '':
        bb.debug(1, "No packages; nothing to do")
        return

    pkgdest = d.getVar('PKGDEST', True)
    if not pkgdest:
        bb.fatal("No PKGDEST")

    outspecfile = d.getVar('OUTSPECFILE', True)
    if not outspecfile:
        bb.fatal("No OUTSPECFILE")

    # Construct the SPEC file...
    srcname    = strip_multilib(d.getVar('PN', True), d)
    srcsummary = (d.getVar('SUMMARY', True) or d.getVar('DESCRIPTION', True) or ".")
    srcversion = d.getVar('PKGV', True).replace('-', '+')
    srcrelease = d.getVar('PKGR', True)
    srcepoch   = (d.getVar('PKGE', True) or "")
    srclicense = d.getVar('LICENSE', True)
    srcsection = d.getVar('SECTION', True)
    srcmaintainer  = d.getVar('MAINTAINER', True)
    srchomepage    = d.getVar('HOMEPAGE', True)
    srcdescription = d.getVar('DESCRIPTION', True) or "."
    srccustomtagschunk = get_package_additional_metadata("rpm", d)

    srcdepends     = strip_multilib_deps(d.getVar('DEPENDS', True), d)
    srcrdepends    = []
    srcrrecommends = []
    srcrsuggests   = []
    srcrprovides   = []
    srcrreplaces   = []
    srcrconflicts  = []
    srcrobsoletes  = []

    srcrpreinst  = []
    srcrpostinst = []
    srcrprerm    = []
    srcrpostrm   = []

    spec_preamble_top = []
    spec_preamble_bottom = []

    spec_scriptlets_top = []
    spec_scriptlets_bottom = []

    spec_files_top = []
    spec_files_bottom = []

    perfiledeps = (d.getVar("MERGEPERFILEDEPS", True) or "0") == "0"
    extra_pkgdata = (d.getVar("RPM_EXTRA_PKGDATA", True) or "0") == "1"

    for pkg in packages.split():
        localdata = bb.data.createCopy(d)

        root = "%s/%s" % (pkgdest, pkg)

        localdata.setVar('ROOT', '')
        localdata.setVar('ROOT_%s' % pkg, root)
        pkgname = localdata.getVar('PKG_%s' % pkg, True)
        if not pkgname:
            pkgname = pkg
        localdata.setVar('PKG', pkgname)

        localdata.setVar('OVERRIDES', d.getVar("OVERRIDES", False) + ":" + pkg)

        bb.data.update_data(localdata)

        conffiles = get_conffiles(pkg, d)
        dirfiles = localdata.getVar('DIRFILES', True)
        if dirfiles is not None:
            dirfiles = dirfiles.split()

        splitname    = strip_multilib(pkgname, d)

        splitsummary = (localdata.getVar('SUMMARY', True) or localdata.getVar('DESCRIPTION', True) or ".")
        splitversion = (localdata.getVar('PKGV', True) or "").replace('-', '+')
        splitrelease = (localdata.getVar('PKGR', True) or "")
        splitepoch   = (localdata.getVar('PKGE', True) or "")
        splitlicense = (localdata.getVar('LICENSE', True) or "")
        splitsection = (localdata.getVar('SECTION', True) or "")
        splitdescription = (localdata.getVar('DESCRIPTION', True) or ".")
        splitcustomtagschunk = get_package_additional_metadata("rpm", localdata)

        translate_vers('RDEPENDS', localdata)
        translate_vers('RRECOMMENDS', localdata)
        translate_vers('RSUGGESTS', localdata)
        translate_vers('RPROVIDES', localdata)
        translate_vers('RREPLACES', localdata)
        translate_vers('RCONFLICTS', localdata)

        # Map the dependencies into their final form
        mapping_rename_hook(localdata)

        splitrdepends    = strip_multilib_deps(localdata.getVar('RDEPENDS', True), d)
        splitrrecommends = strip_multilib_deps(localdata.getVar('RRECOMMENDS', True), d)
        splitrsuggests   = strip_multilib_deps(localdata.getVar('RSUGGESTS', True), d)
        splitrprovides   = strip_multilib_deps(localdata.getVar('RPROVIDES', True), d)
        splitrreplaces   = strip_multilib_deps(localdata.getVar('RREPLACES', True), d)
        splitrconflicts  = strip_multilib_deps(localdata.getVar('RCONFLICTS', True), d)
        splitrobsoletes  = []

        splitrpreinst  = localdata.getVar('pkg_preinst', True)
        splitrpostinst = localdata.getVar('pkg_postinst', True)
        splitrprerm    = localdata.getVar('pkg_prerm', True)
        splitrpostrm   = localdata.getVar('pkg_postrm', True)


        if not perfiledeps:
            # Add in summary of per file dependencies
            splitrdepends = splitrdepends + " " + get_perfile('RDEPENDS', pkg, d)
            splitrprovides = splitrprovides + " " + get_perfile('RPROVIDES', pkg, d)

        # Gather special src/first package data
        if srcname == splitname:
            srcrdepends    = splitrdepends
            srcrrecommends = splitrrecommends
            srcrsuggests   = splitrsuggests
            srcrprovides   = splitrprovides
            srcrreplaces   = splitrreplaces
            srcrconflicts  = splitrconflicts

            srcrpreinst    = splitrpreinst
            srcrpostinst   = splitrpostinst
            srcrprerm      = splitrprerm
            srcrpostrm     = splitrpostrm

            file_list = []
            walk_files(root, file_list, conffiles, dirfiles)
            if not file_list and localdata.getVar('ALLOW_EMPTY', False) != "1":
                bb.note("Not creating empty RPM package for %s" % splitname)
            else:
                bb.note("Creating RPM package for %s" % splitname)
                spec_files_top.append('%files')
                if extra_pkgdata:
                    package_rpm_extra_pkgdata(splitname, spec_files_top, localdata)
                spec_files_top.append('%defattr(-,-,-,-)')
                if file_list:
                    bb.note("Creating RPM package for %s" % splitname)
                    spec_files_top.extend(file_list)
                else:
                    bb.note("Creating EMPTY RPM Package for %s" % splitname)
                spec_files_top.append('')
            continue

        # Process subpackage data
        spec_preamble_bottom.append('%%package -n %s' % splitname)
        spec_preamble_bottom.append('Summary: %s' % splitsummary)
        if srcversion != splitversion:
            spec_preamble_bottom.append('Version: %s' % splitversion)
        if srcrelease != splitrelease:
            spec_preamble_bottom.append('Release: %s' % splitrelease)
        if srcepoch != splitepoch:
            spec_preamble_bottom.append('Epoch: %s' % splitepoch)
        if srclicense != splitlicense:
            spec_preamble_bottom.append('License: %s' % splitlicense)
        spec_preamble_bottom.append('Group: %s' % splitsection)

        if srccustomtagschunk != splitcustomtagschunk:
            spec_preamble_bottom.append(splitcustomtagschunk)

        # Replaces == Obsoletes && Provides
        robsoletes = bb.utils.explode_dep_versions2(splitrobsoletes or "")
        rprovides = bb.utils.explode_dep_versions2(splitrprovides or "")
        rreplaces = bb.utils.explode_dep_versions2(splitrreplaces or "")
        for dep in rreplaces:
            if not dep in robsoletes:
                robsoletes[dep] = rreplaces[dep]
            if not dep in rprovides:
                rprovides[dep] = rreplaces[dep]
        splitrobsoletes = bb.utils.join_deps(robsoletes, commasep=False)
        splitrprovides = bb.utils.join_deps(rprovides, commasep=False)

        print_deps(splitrdepends, "Requires", spec_preamble_bottom, d)
        if splitrpreinst:
            print_deps(splitrdepends, "Requires(pre)", spec_preamble_bottom, d)
        if splitrpostinst:
            print_deps(splitrdepends, "Requires(post)", spec_preamble_bottom, d)
        if splitrprerm:
            print_deps(splitrdepends, "Requires(preun)", spec_preamble_bottom, d)
        if splitrpostrm:
            print_deps(splitrdepends, "Requires(postun)", spec_preamble_bottom, d)

        # Suggests in RPM are like recommends in OE-core!
        print_deps(splitrrecommends, "Suggests", spec_preamble_bottom, d)
        # While there is no analog for suggests... (So call them recommends for now)
        print_deps(splitrsuggests,  "Recommends", spec_preamble_bottom, d)
        print_deps(splitrprovides,  "Provides", spec_preamble_bottom, d)
        print_deps(splitrobsoletes, "Obsoletes", spec_preamble_bottom, d)

        # conflicts can not be in a provide!  We will need to filter it.
        if splitrconflicts:
            depends_dict = bb.utils.explode_dep_versions2(splitrconflicts)
            newdeps_dict = {}
            for dep in depends_dict:
                if dep not in splitrprovides:
                    newdeps_dict[dep] = depends_dict[dep]
            if newdeps_dict:
                splitrconflicts = bb.utils.join_deps(newdeps_dict)
            else:
                splitrconflicts = ""

        print_deps(splitrconflicts,  "Conflicts", spec_preamble_bottom, d)

        spec_preamble_bottom.append('')

        spec_preamble_bottom.append('%%description -n %s' % splitname)
        append_description(spec_preamble_bottom, splitdescription)

        spec_preamble_bottom.append('')

        # Now process scriptlets
        if splitrpreinst:
            spec_scriptlets_bottom.append('%%pre -n %s' % splitname)
            spec_scriptlets_bottom.append('# %s - preinst' % splitname)
            spec_scriptlets_bottom.append(splitrpreinst)
            spec_scriptlets_bottom.append('')
        if splitrpostinst:
            spec_scriptlets_bottom.append('%%post -n %s' % splitname)
            spec_scriptlets_bottom.append('# %s - postinst' % splitname)
            spec_scriptlets_bottom.append(splitrpostinst)
            spec_scriptlets_bottom.append('')
        if splitrprerm:
            spec_scriptlets_bottom.append('%%preun -n %s' % splitname)
            spec_scriptlets_bottom.append('# %s - prerm' % splitname)
            scriptvar = wrap_uninstall(splitrprerm)
            spec_scriptlets_bottom.append(scriptvar)
            spec_scriptlets_bottom.append('')
        if splitrpostrm:
            spec_scriptlets_bottom.append('%%postun -n %s' % splitname)
            spec_scriptlets_bottom.append('# %s - postrm' % splitname)
            scriptvar = wrap_uninstall(splitrpostrm)
            spec_scriptlets_bottom.append(scriptvar)
            spec_scriptlets_bottom.append('')

        # Now process files
        file_list = []
        walk_files(root, file_list, conffiles, dirfiles)
        if not file_list and localdata.getVar('ALLOW_EMPTY', False) != "1":
            bb.note("Not creating empty RPM package for %s" % splitname)
        else:
            spec_files_bottom.append('%%files -n %s' % splitname)
            if extra_pkgdata:
                package_rpm_extra_pkgdata(splitname, spec_files_bottom, localdata)
            spec_files_bottom.append('%defattr(-,-,-,-)')
            if file_list:
                bb.note("Creating RPM package for %s" % splitname)
                spec_files_bottom.extend(file_list)
            else:
                bb.note("Creating EMPTY RPM Package for %s" % splitname)
            spec_files_bottom.append('')

        del localdata
    
    add_prep(d,spec_files_bottom)
    spec_preamble_top.append('Summary: %s' % srcsummary)
    spec_preamble_top.append('Name: %s' % srcname)
    spec_preamble_top.append('Version: %s' % srcversion)
    spec_preamble_top.append('Release: %s' % srcrelease)
    if srcepoch and srcepoch.strip() != "":
        spec_preamble_top.append('Epoch: %s' % srcepoch)
    spec_preamble_top.append('License: %s' % srclicense)
    spec_preamble_top.append('Group: %s' % srcsection)
    spec_preamble_top.append('Packager: %s' % srcmaintainer)
    if srchomepage:
        spec_preamble_top.append('URL: %s' % srchomepage)
    if srccustomtagschunk:
        spec_preamble_top.append(srccustomtagschunk)
    tail_source(d)

    # Replaces == Obsoletes && Provides
    robsoletes = bb.utils.explode_dep_versions2(srcrobsoletes or "")
    rprovides = bb.utils.explode_dep_versions2(srcrprovides or "")
    rreplaces = bb.utils.explode_dep_versions2(srcrreplaces or "")
    for dep in rreplaces:
        if not dep in robsoletes:
            robsoletes[dep] = rreplaces[dep]
        if not dep in rprovides:
            rprovides[dep] = rreplaces[dep]
    srcrobsoletes = bb.utils.join_deps(robsoletes, commasep=False)
    srcrprovides = bb.utils.join_deps(rprovides, commasep=False)

    print_deps(srcdepends, "BuildRequires", spec_preamble_top, d)
    print_deps(srcrdepends, "Requires", spec_preamble_top, d)
    if srcrpreinst:
        print_deps(srcrdepends, "Requires(pre)", spec_preamble_top, d)
    if srcrpostinst:
        print_deps(srcrdepends, "Requires(post)", spec_preamble_top, d)
    if srcrprerm:
        print_deps(srcrdepends, "Requires(preun)", spec_preamble_top, d)
    if srcrpostrm:
        print_deps(srcrdepends, "Requires(postun)", spec_preamble_top, d)

    # Suggests in RPM are like recommends in OE-core!
    print_deps(srcrrecommends, "Suggests", spec_preamble_top, d)
    # While there is no analog for suggests... (So call them recommends for now)
    print_deps(srcrsuggests, "Recommends", spec_preamble_top, d)
    print_deps(srcrprovides, "Provides", spec_preamble_top, d)
    print_deps(srcrobsoletes, "Obsoletes", spec_preamble_top, d)
    
    # conflicts can not be in a provide!  We will need to filter it.
    if srcrconflicts:
        depends_dict = bb.utils.explode_dep_versions2(srcrconflicts)
        newdeps_dict = {}
        for dep in depends_dict:
            if dep not in srcrprovides:
                newdeps_dict[dep] = depends_dict[dep]
        if newdeps_dict:
            srcrconflicts = bb.utils.join_deps(newdeps_dict)
        else:
            srcrconflicts = ""

    print_deps(srcrconflicts, "Conflicts", spec_preamble_top, d)

    spec_preamble_top.append('')

    spec_preamble_top.append('%description')
    append_description(spec_preamble_top, srcdescription)

    spec_preamble_top.append('')

    if srcrpreinst:
        spec_scriptlets_top.append('%pre')
        spec_scriptlets_top.append('# %s - preinst' % srcname)
        spec_scriptlets_top.append(srcrpreinst)
        spec_scriptlets_top.append('')
    if srcrpostinst:
        spec_scriptlets_top.append('%post')
        spec_scriptlets_top.append('# %s - postinst' % srcname)
        spec_scriptlets_top.append(srcrpostinst)
        spec_scriptlets_top.append('')
    if srcrprerm:
        spec_scriptlets_top.append('%preun')
        spec_scriptlets_top.append('# %s - prerm' % srcname)
        scriptvar = wrap_uninstall(srcrprerm)
        spec_scriptlets_top.append(scriptvar)
        spec_scriptlets_top.append('')
    if srcrpostrm:
        spec_scriptlets_top.append('%postun')
        spec_scriptlets_top.append('# %s - postrm' % srcname)
        scriptvar = wrap_uninstall(srcrpostrm)
        spec_scriptlets_top.append(scriptvar)
        spec_scriptlets_top.append('')

    # Write the SPEC file
    try:
        specfile = open(outspecfile, 'w')
    except OSError:
        raise bb.build.FuncFailed("unable to open spec file for writing.")

    # RPMSPEC_PREAMBLE is a way to add arbitrary text to the top
    # of the generated spec file
    external_preamble = d.getVar("RPMSPEC_PREAMBLE", True)
    if external_preamble:
        specfile.write(external_preamble + "\n")

    for line in spec_preamble_top:
        specfile.write(line + "\n")

    for line in spec_preamble_bottom:
        specfile.write(line + "\n")

    for line in spec_scriptlets_top:
        specfile.write(line + "\n")

    for line in spec_scriptlets_bottom:
        specfile.write(line + "\n")

    for line in spec_files_top:
        specfile.write(line + "\n")

    for line in spec_files_bottom:
        specfile.write(line + "\n")

    specfile.close()
}
# Otherwise allarch packages may change depending on override configuration
write_specfile[vardepsexclude] = "OVERRIDES"

python do_package_rpm () {
    # We need a simple way to remove the MLPREFIX from the package name,
    # and dependency information...
    def strip_multilib(name, d):
        ml = d.getVar("MLPREFIX", True)
        if ml and name and len(ml) != 0 and name.find(ml) >= 0:
            return "".join(name.split(ml))
        return name

    workdir = d.getVar('WORKDIR', True)
    tmpdir = d.getVar('TMPDIR', True)
    pkgd = d.getVar('PKGD', True)
    pkgdest = d.getVar('PKGDEST', True)
    if not workdir or not pkgd or not tmpdir:
        bb.error("Variables incorrectly set, unable to package")
        return

    packages = d.getVar('PACKAGES', True)
    if not packages or packages == '':
        bb.debug(1, "No packages; nothing to do")
        return

    # Construct the spec file...
    # If the spec file already exist, and has not been stored into 
    # pseudo's files.db, it maybe cause rpmbuild src.rpm fail,
    # so remove it before doing rpmbuild src.rpm.
    srcname    = strip_multilib(d.getVar('PN', True), d)
    outspecfile = workdir + "/" + srcname + ".spec"
    if os.path.isfile(outspecfile):
        os.remove(outspecfile)
    d.setVar('OUTSPECFILE', outspecfile)
    bb.build.exec_func('write_specfile', d)

    perfiledeps = (d.getVar("MERGEPERFILEDEPS", True) or "0") == "0"
    if perfiledeps:
        outdepends, outprovides = write_rpm_perfiledata(srcname, d)

    # Setup the rpmbuild arguments...
    rpmbuild = d.getVar('RPMBUILD', True)
    targetsys = d.getVar('TARGET_SYS', True)
    targetvendor = d.getVar('HOST_VENDOR', True)
    package_arch = (d.getVar('PACKAGE_ARCH', True) or "").replace("-", "_")
    sdkpkgsuffix = (d.getVar('SDKPKGSUFFIX', True) or "nativesdk").replace("-", "_")
    if package_arch not in "all any noarch".split() and not package_arch.endswith(sdkpkgsuffix):
        ml_prefix = (d.getVar('MLPREFIX', True) or "").replace("-", "_")
        d.setVar('PACKAGE_ARCH_EXTEND', ml_prefix + package_arch)
    else:
        d.setVar('PACKAGE_ARCH_EXTEND', package_arch)
    pkgwritedir = d.expand('${PKGWRITEDIRRPM}/${PACKAGE_ARCH_EXTEND}')
    d.setVar('RPM_PKGWRITEDIR', pkgwritedir)
    bb.debug(1, 'PKGWRITEDIR: %s' % d.getVar('RPM_PKGWRITEDIR', True))
    pkgarch = d.expand('${PACKAGE_ARCH_EXTEND}${HOST_VENDOR}-${HOST_OS}')
    magicfile = d.expand('${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc')
    bb.utils.mkdirhier(pkgwritedir)
    os.chmod(pkgwritedir, 0755)

    cmd = rpmbuild
    cmd = cmd + " --nodeps --short-circuit --target " + pkgarch + " --buildroot " + pkgd
    cmd = cmd + " --define '_topdir " + workdir + "' --define '_rpmdir " + pkgwritedir + "'"
    cmd = cmd + " --define '_builddir " + d.getVar('S', True) + "'"
    cmd = cmd + " --define '_build_name_fmt %%{NAME}-%%{VERSION}-%%{RELEASE}.%%{ARCH}.rpm'"
    cmd = cmd + " --define '_use_internal_dependency_generator 0'"
    if perfiledeps:
        cmd = cmd + " --define '__find_requires " + outdepends + "'"
        cmd = cmd + " --define '__find_provides " + outprovides + "'"
    else:
        cmd = cmd + " --define '__find_requires %{nil}'"
        cmd = cmd + " --define '__find_provides %{nil}'"
    cmd = cmd + " --define '_unpackaged_files_terminate_build 0'"
    cmd = cmd + " --define 'debug_package %{nil}'"
    cmd = cmd + " --define '_rpmfc_magic_path " + magicfile + "'"
    cmd = cmd + " --define '_tmppath " + workdir + "'"
    if d.getVarFlag('ARCHIVER_MODE', 'srpm', True) == '1' and bb.data.inherits_class('archiver', d):
        cmd = cmd + " --define '_sourcedir " + d.getVar('ARCHIVER_OUTDIR', True) + "'"
        cmdsrpm = cmd + " --define '_srcrpmdir " + d.getVar('ARCHIVER_OUTDIR', True) + "'"
        cmdsrpm = cmdsrpm + " -bs " + outspecfile
        # Build the .src.rpm
        d.setVar('SBUILDSPEC', cmdsrpm + "\n")
        d.setVarFlag('SBUILDSPEC', 'func', '1')
        bb.build.exec_func('SBUILDSPEC', d)
    cmd = cmd + " -bb " + outspecfile

    # Build the rpm package!
    d.setVar('BUILDSPEC', cmd + "\n")
    d.setVarFlag('BUILDSPEC', 'func', '1')
    bb.build.exec_func('BUILDSPEC', d)

    if d.getVar('RPM_SIGN_PACKAGES', True) == '1':
        bb.build.exec_func("sign_rpm", d)
}

python () {
    if d.getVar('PACKAGES', True) != '':
        deps = ' rpm-native:do_populate_sysroot virtual/fakeroot-native:do_populate_sysroot'
        d.appendVarFlag('do_package_write_rpm', 'depends', deps)
        d.setVarFlag('do_package_write_rpm', 'fakeroot', '1')
}

SSTATETASKS += "do_package_write_rpm"
do_package_write_rpm[sstate-inputdirs] = "${PKGWRITEDIRRPM}"
do_package_write_rpm[sstate-outputdirs] = "${DEPLOY_DIR_RPM}"
# Take a shared lock, we can write multiple packages at the same time...
# but we need to stop the rootfs/solver from running while we do...
do_package_write_rpm[sstate-lockfile-shared] += "${DEPLOY_DIR_RPM}/rpm.lock"

python do_package_write_rpm_setscene () {
    sstate_setscene(d)
}
addtask do_package_write_rpm_setscene

python do_package_write_rpm () {
    bb.build.exec_func("read_subpackage_metadata", d)
    bb.build.exec_func("do_package_rpm", d)
}

do_package_write_rpm[dirs] = "${PKGWRITEDIRRPM}"
do_package_write_rpm[cleandirs] = "${PKGWRITEDIRRPM}"
do_package_write_rpm[umask] = "022"
addtask package_write_rpm after do_packagedata do_package

PACKAGEINDEXDEPS += "rpm-native:do_populate_sysroot"
PACKAGEINDEXDEPS += "createrepo-native:do_populate_sysroot"

do_build[recrdeptask] += "do_package_write_rpm"
