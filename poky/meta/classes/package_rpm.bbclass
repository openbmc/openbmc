inherit package

IMAGE_PKGTYPE ?= "rpm"

RPM="rpm"
RPMBUILD="rpmbuild"

PKGWRITEDIRRPM = "${WORKDIR}/deploy-rpms"

# Maintaining the perfile dependencies has singificant overhead when writing the
# packages. When set, this value merges them for efficiency.
MERGEPERFILEDEPS = "1"

# Filter dependencies based on a provided function.
def filter_deps(var, f):
    import collections

    depends_dict = bb.utils.explode_dep_versions2(var)
    newdeps_dict = collections.OrderedDict()
    for dep in depends_dict:
        if f(dep):
            newdeps_dict[dep] = depends_dict[dep]
    return bb.utils.join_deps(newdeps_dict, commasep=False)

# Filter out absolute paths (typically /bin/sh and /usr/bin/env) and any perl
# dependencies for nativesdk packages.
def filter_nativesdk_deps(srcname, var):
    if var and srcname.startswith("nativesdk-"):
        var = filter_deps(var, lambda dep: not dep.startswith('/') and dep != 'perl' and not dep.startswith('perl('))
    return var

# Construct per file dependencies file
def write_rpm_perfiledata(srcname, d):
    workdir = d.getVar('WORKDIR')
    packages = d.getVar('PACKAGES')
    pkgd = d.getVar('PKGD')

    def dump_filerdeps(varname, outfile, d):
        outfile.write("#!/usr/bin/env python3\n\n")
        outfile.write("# Dependency table\n")
        outfile.write('deps = {\n')
        for pkg in packages.split():
            dependsflist_key = 'FILE' + varname + 'FLIST' + "_" + pkg
            dependsflist = (d.getVar(dependsflist_key) or "")
            for dfile in dependsflist.split():
                key = "FILE" + varname + "_" + dfile + "_" + pkg
                deps = filter_nativesdk_deps(srcname, d.getVar(key) or "")
                depends_dict = bb.utils.explode_dep_versions(deps)
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

    dependsfile = open(outdepends, 'w')

    dump_filerdeps('RDEPENDS', dependsfile, d)

    dependsfile.close()
    os.chmod(outdepends, 0o755)

    # OE-core / RPM Provides
    outprovides = workdir + "/" + srcname + ".provides"

    providesfile = open(outprovides, 'w')

    dump_filerdeps('RPROVIDES', providesfile, d)

    providesfile.close()
    os.chmod(outprovides, 0o755)

    return (outdepends, outprovides)


python write_specfile () {
    import oe.packagedata

    # append information for logs and patches to %prep
    def add_prep(d,spec_files_bottom):
        if d.getVarFlag('ARCHIVER_MODE', 'srpm') == '1' and bb.data.inherits_class('archiver', d):
            spec_files_bottom.append('%%prep -n %s' % d.getVar('PN') )
            spec_files_bottom.append('%s' % "echo \"include logs and patches, Please check them in SOURCES\"")
            spec_files_bottom.append('')

    # append the name of tarball to key word 'SOURCE' in xxx.spec.
    def tail_source(d):
        if d.getVarFlag('ARCHIVER_MODE', 'srpm') == '1' and bb.data.inherits_class('archiver', d):
            ar_outdir = d.getVar('ARCHIVER_OUTDIR')
            if not os.path.exists(ar_outdir):
                return
            source_list = os.listdir(ar_outdir)
            source_number = 0
            for source in source_list:
                # do_deploy_archives may have already run (from sstate) meaning a .src.rpm may already 
                # exist in ARCHIVER_OUTDIR so skip if present.
                if source.endswith(".src.rpm"):
                    continue
                # The rpmbuild doesn't need the root permission, but it needs
                # to know the file's user and group name, the only user and
                # group in fakeroot is "root" when working in fakeroot.
                f = os.path.join(ar_outdir, source)
                os.chown(f, 0, 0)
                spec_preamble_top.append('Source%s: %s' % (source_number, source))
                source_number += 1

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
        depends = d.getVar(varname)
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
            path = path.replace("[", "?")
            path = path.replace("]", "?")

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
                    dir = dir.replace("[", "?")
                    dir = dir.replace("]", "?")
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
                file = file.replace("[", "?")
                file = file.replace("]", "?")
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
        dependsflist = (d.getVar(dependsflist_key) or "")
        for dfile in dependsflist.split():
            key = "FILE" + varname + "_" + dfile + "_" + pkg
            depends = d.getVar(key)
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

    packages = d.getVar('PACKAGES')
    if not packages or packages == '':
        bb.debug(1, "No packages; nothing to do")
        return

    pkgdest = d.getVar('PKGDEST')
    if not pkgdest:
        bb.fatal("No PKGDEST")

    outspecfile = d.getVar('OUTSPECFILE')
    if not outspecfile:
        bb.fatal("No OUTSPECFILE")

    # Construct the SPEC file...
    srcname    = d.getVar('PN')
    srcsummary = (d.getVar('SUMMARY') or d.getVar('DESCRIPTION') or ".")
    srcversion = d.getVar('PKGV').replace('-', '+')
    srcrelease = d.getVar('PKGR')
    srcepoch   = (d.getVar('PKGE') or "")
    srclicense = d.getVar('LICENSE')
    srcsection = d.getVar('SECTION')
    srcmaintainer  = d.getVar('MAINTAINER')
    srchomepage    = d.getVar('HOMEPAGE')
    srcdescription = d.getVar('DESCRIPTION') or "."
    srccustomtagschunk = get_package_additional_metadata("rpm", d)

    srcdepends     = d.getVar('DEPENDS')
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

    perfiledeps = (d.getVar("MERGEPERFILEDEPS") or "0") == "0"
    extra_pkgdata = (d.getVar("RPM_EXTRA_PKGDATA") or "0") == "1"

    for pkg in packages.split():
        localdata = bb.data.createCopy(d)

        root = "%s/%s" % (pkgdest, pkg)

        localdata.setVar('ROOT', '')
        localdata.setVar('ROOT_%s' % pkg, root)
        pkgname = localdata.getVar('PKG_%s' % pkg)
        if not pkgname:
            pkgname = pkg
        localdata.setVar('PKG', pkgname)

        localdata.setVar('OVERRIDES', d.getVar("OVERRIDES", False) + ":" + pkg)

        conffiles = get_conffiles(pkg, d)
        dirfiles = localdata.getVar('DIRFILES')
        if dirfiles is not None:
            dirfiles = dirfiles.split()

        splitname    = pkgname

        splitsummary = (localdata.getVar('SUMMARY') or localdata.getVar('DESCRIPTION') or ".")
        splitversion = (localdata.getVar('PKGV') or "").replace('-', '+')
        splitrelease = (localdata.getVar('PKGR') or "")
        splitepoch   = (localdata.getVar('PKGE') or "")
        splitlicense = (localdata.getVar('LICENSE') or "")
        splitsection = (localdata.getVar('SECTION') or "")
        splitdescription = (localdata.getVar('DESCRIPTION') or ".")
        splitcustomtagschunk = get_package_additional_metadata("rpm", localdata)

        translate_vers('RDEPENDS', localdata)
        translate_vers('RRECOMMENDS', localdata)
        translate_vers('RSUGGESTS', localdata)
        translate_vers('RPROVIDES', localdata)
        translate_vers('RREPLACES', localdata)
        translate_vers('RCONFLICTS', localdata)

        # Map the dependencies into their final form
        mapping_rename_hook(localdata)

        splitrdepends    = localdata.getVar('RDEPENDS')
        splitrrecommends = localdata.getVar('RRECOMMENDS')
        splitrsuggests   = localdata.getVar('RSUGGESTS')
        splitrprovides   = localdata.getVar('RPROVIDES')
        splitrreplaces   = localdata.getVar('RREPLACES')
        splitrconflicts  = localdata.getVar('RCONFLICTS')
        splitrobsoletes  = []

        splitrpreinst  = localdata.getVar('pkg_preinst')
        splitrpostinst = localdata.getVar('pkg_postinst')
        splitrprerm    = localdata.getVar('pkg_prerm')
        splitrpostrm   = localdata.getVar('pkg_postrm')


        if not perfiledeps:
            # Add in summary of per file dependencies
            splitrdepends = splitrdepends + " " + get_perfile('RDEPENDS', pkg, d)
            splitrprovides = splitrprovides + " " + get_perfile('RPROVIDES', pkg, d)

        splitrdepends = filter_nativesdk_deps(srcname, splitrdepends)

        # Gather special src/first package data
        if srcname == splitname:
            archiving = d.getVarFlag('ARCHIVER_MODE', 'srpm') == '1' and \
                        bb.data.inherits_class('archiver', d)
            if archiving and srclicense != splitlicense:
                bb.warn("The SRPM produced may not have the correct overall source license in the License tag. This is due to the LICENSE for the primary package and SRPM conflicting.")

            srclicense     = splitlicense
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
                spec_files_top.append('%files')
                if extra_pkgdata:
                    package_rpm_extra_pkgdata(splitname, spec_files_top, localdata)
                spec_files_top.append('%defattr(-,-,-,-)')
                if file_list:
                    bb.note("Creating RPM package for %s" % splitname)
                    spec_files_top.extend(file_list)
                else:
                    bb.note("Creating empty RPM package for %s" % splitname)
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

        print_deps(splitrrecommends, "Recommends", spec_preamble_bottom, d)
        print_deps(splitrsuggests,  "Suggests", spec_preamble_bottom, d)
        print_deps(splitrprovides,  "Provides", spec_preamble_bottom, d)
        print_deps(splitrobsoletes, "Obsoletes", spec_preamble_bottom, d)
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
                bb.note("Creating empty RPM package for %s" % splitname)
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

    print_deps(srcrrecommends, "Recommends", spec_preamble_top, d)
    print_deps(srcrsuggests, "Suggests", spec_preamble_top, d)
    print_deps(srcrprovides + (" /bin/sh" if srcname.startswith("nativesdk-") else ""), "Provides", spec_preamble_top, d)
    print_deps(srcrobsoletes, "Obsoletes", spec_preamble_top, d)
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
    specfile = open(outspecfile, 'w')

    # RPMSPEC_PREAMBLE is a way to add arbitrary text to the top
    # of the generated spec file
    external_preamble = d.getVar("RPMSPEC_PREAMBLE")
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
    workdir = d.getVar('WORKDIR')
    tmpdir = d.getVar('TMPDIR')
    pkgd = d.getVar('PKGD')
    pkgdest = d.getVar('PKGDEST')
    if not workdir or not pkgd or not tmpdir:
        bb.error("Variables incorrectly set, unable to package")
        return

    packages = d.getVar('PACKAGES')
    if not packages or packages == '':
        bb.debug(1, "No packages; nothing to do")
        return

    # Construct the spec file...
    # If the spec file already exist, and has not been stored into 
    # pseudo's files.db, it maybe cause rpmbuild src.rpm fail,
    # so remove it before doing rpmbuild src.rpm.
    srcname    = d.getVar('PN')
    outspecfile = workdir + "/" + srcname + ".spec"
    if os.path.isfile(outspecfile):
        os.remove(outspecfile)
    d.setVar('OUTSPECFILE', outspecfile)
    bb.build.exec_func('write_specfile', d)

    perfiledeps = (d.getVar("MERGEPERFILEDEPS") or "0") == "0"
    if perfiledeps:
        outdepends, outprovides = write_rpm_perfiledata(srcname, d)

    # Setup the rpmbuild arguments...
    rpmbuild = d.getVar('RPMBUILD')
    targetsys = d.getVar('TARGET_SYS')
    targetvendor = d.getVar('HOST_VENDOR')

    # Too many places in dnf stack assume that arch-independent packages are "noarch".
    # Let's not fight against this.
    package_arch = (d.getVar('PACKAGE_ARCH') or "").replace("-", "_")
    if package_arch == "all":
        package_arch = "noarch"

    sdkpkgsuffix = (d.getVar('SDKPKGSUFFIX') or "nativesdk").replace("-", "_")
    d.setVar('PACKAGE_ARCH_EXTEND', package_arch)
    pkgwritedir = d.expand('${PKGWRITEDIRRPM}/${PACKAGE_ARCH_EXTEND}')
    d.setVar('RPM_PKGWRITEDIR', pkgwritedir)
    bb.debug(1, 'PKGWRITEDIR: %s' % d.getVar('RPM_PKGWRITEDIR'))
    pkgarch = d.expand('${PACKAGE_ARCH_EXTEND}${HOST_VENDOR}-linux')
    bb.utils.mkdirhier(pkgwritedir)
    os.chmod(pkgwritedir, 0o755)

    cmd = rpmbuild
    cmd = cmd + " --noclean --nodeps --short-circuit --target " + pkgarch + " --buildroot " + pkgd
    cmd = cmd + " --define '_topdir " + workdir + "' --define '_rpmdir " + pkgwritedir + "'"
    cmd = cmd + " --define '_builddir " + d.getVar('B') + "'"
    cmd = cmd + " --define '_build_name_fmt %%{NAME}-%%{VERSION}-%%{RELEASE}.%%{ARCH}.rpm'"
    cmd = cmd + " --define '_use_internal_dependency_generator 0'"
    cmd = cmd + " --define '_binaries_in_noarch_packages_terminate_build 0'"
    cmd = cmd + " --define '_build_id_links none'"
    cmd = cmd + " --define '_binary_payload w6T.xzdio'"
    cmd = cmd + " --define '_source_payload w6T.xzdio'"
    cmd = cmd + " --define 'clamp_mtime_to_source_date_epoch 1'"
    cmd = cmd + " --define '_buildhost reproducible'"
    if perfiledeps:
        cmd = cmd + " --define '__find_requires " + outdepends + "'"
        cmd = cmd + " --define '__find_provides " + outprovides + "'"
    else:
        cmd = cmd + " --define '__find_requires %{nil}'"
        cmd = cmd + " --define '__find_provides %{nil}'"
    cmd = cmd + " --define '_unpackaged_files_terminate_build 0'"
    cmd = cmd + " --define 'debug_package %{nil}'"
    cmd = cmd + " --define '_tmppath " + workdir + "'"
    if d.getVarFlag('ARCHIVER_MODE', 'srpm') == '1' and bb.data.inherits_class('archiver', d):
        cmd = cmd + " --define '_sourcedir " + d.getVar('ARCHIVER_OUTDIR') + "'"
        cmdsrpm = cmd + " --define '_srcrpmdir " + d.getVar('ARCHIVER_RPMOUTDIR') + "'"
        cmdsrpm = cmdsrpm + " -bs " + outspecfile
        # Build the .src.rpm
        d.setVar('SBUILDSPEC', cmdsrpm + "\n")
        d.setVarFlag('SBUILDSPEC', 'func', '1')
        bb.build.exec_func('SBUILDSPEC', d)
    cmd = cmd + " -bb " + outspecfile

    # rpm 4 creates various empty directories in _topdir, let's clean them up
    cleanupcmd = "rm -rf %s/BUILDROOT %s/SOURCES %s/SPECS %s/SRPMS" % (workdir, workdir, workdir, workdir)

    # Build the rpm package!
    d.setVar('BUILDSPEC', cmd + "\n" + cleanupcmd + "\n")
    d.setVarFlag('BUILDSPEC', 'func', '1')
    bb.build.exec_func('BUILDSPEC', d)

    if d.getVar('RPM_SIGN_PACKAGES') == '1':
        bb.build.exec_func("sign_rpm", d)
}

python () {
    if d.getVar('PACKAGES') != '':
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
do_package_write_rpm[depends] += "${@oe.utils.build_depends_string(d.getVar('PACKAGE_WRITE_DEPS'), 'do_populate_sysroot')}"
addtask package_write_rpm after do_packagedata do_package

PACKAGEINDEXDEPS += "rpm-native:do_populate_sysroot"
PACKAGEINDEXDEPS += "createrepo-c-native:do_populate_sysroot"

do_build[recrdeptask] += "do_package_write_rpm"
