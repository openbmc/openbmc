#
# Packaging process
#
# Executive summary: This class iterates over the functions listed in PACKAGEFUNCS
# Taking D and splitting it up into the packages listed in PACKAGES, placing the
# resulting output in PKGDEST.
#
# There are the following default steps but PACKAGEFUNCS can be extended:
#
# a) package_get_auto_pr - get PRAUTO from remote PR service
#
# b) perform_packagecopy - Copy D into PKGD
#
# c) package_do_split_locales - Split out the locale files, updates FILES and PACKAGES
#
# d) split_and_strip_files - split the files into runtime and debug and strip them.
#    Debug files include debug info split, and associated sources that end up in -dbg packages
#
# e) fixup_perms - Fix up permissions in the package before we split it.
#
# f) populate_packages - Split the files in PKGD into separate packages in PKGDEST/<pkgname>
#    Also triggers the binary stripping code to put files in -dbg packages.
#
# g) package_do_filedeps - Collect perfile run-time dependency metadata
#    The data is stores in FILER{PROVIDES,DEPENDS}_file_pkg variables with
#    a list of affected files in FILER{PROVIDES,DEPENDS}FLIST_pkg
#
# h) package_do_shlibs - Look at the shared libraries generated and autotmatically add any
#    dependencies found. Also stores the package name so anyone else using this library
#    knows which package to depend on.
#
# i) package_do_pkgconfig - Keep track of which packages need and provide which .pc files
#
# j) read_shlibdeps - Reads the stored shlibs information into the metadata
#
# k) package_depchains - Adds automatic dependencies to -dbg and -dev packages
#
# l) emit_pkgdata - saves the packaging data into PKGDATA_DIR for use in later
#    packaging steps

inherit packagedata
inherit chrpath
inherit package_pkgdata

# Need the package_qa_handle_error() in insane.bbclass
inherit insane

PKGD    = "${WORKDIR}/package"
PKGDEST = "${WORKDIR}/packages-split"

LOCALE_SECTION ?= ''

ALL_MULTILIB_PACKAGE_ARCHS = "${@all_multilib_tune_values(d, 'PACKAGE_ARCHS')}"

# rpm is used for the per-file dependency identification
# dwarfsrcfiles is used to determine the list of debug source files
PACKAGE_DEPENDS += "rpm-native dwarfsrcfiles-native"


# If your postinstall can execute at rootfs creation time rather than on
# target but depends on a native/cross tool in order to execute, you need to
# list that tool in PACKAGE_WRITE_DEPS. Target package dependencies belong
# in the package dependencies as normal, this is just for native/cross support
# tools at rootfs build time.
PACKAGE_WRITE_DEPS ??= ""

def legitimize_package_name(s):
    """
    Make sure package names are legitimate strings
    """
    import re

    def fixutf(m):
        cp = m.group(1)
        if cp:
            return ('\\u%s' % cp).encode('latin-1').decode('unicode_escape')

    # Handle unicode codepoints encoded as <U0123>, as in glibc locale files.
    s = re.sub(r'<U([0-9A-Fa-f]{1,4})>', fixutf, s)

    # Remaining package name validity fixes
    return s.lower().replace('_', '-').replace('@', '+').replace(',', '+').replace('/', '-')

def do_split_packages(d, root, file_regex, output_pattern, description, postinst=None, recursive=False, hook=None, extra_depends=None, aux_files_pattern=None, postrm=None, allow_dirs=False, prepend=False, match_path=False, aux_files_pattern_verbatim=None, allow_links=False, summary=None):
    """
    Used in .bb files to split up dynamically generated subpackages of a
    given package, usually plugins or modules.

    Arguments:
    root           -- the path in which to search
    file_regex     -- regular expression to match searched files. Use
                      parentheses () to mark the part of this expression
                      that should be used to derive the module name (to be
                      substituted where %s is used in other function
                      arguments as noted below)
    output_pattern -- pattern to use for the package names. Must include %s.
    description    -- description to set for each package. Must include %s.
    postinst       -- postinstall script to use for all packages (as a
                      string)
    recursive      -- True to perform a recursive search - default False
    hook           -- a hook function to be called for every match. The
                      function will be called with the following arguments
                      (in the order listed):
                        f: full path to the file/directory match
                        pkg: the package name
                        file_regex: as above
                        output_pattern: as above
                        modulename: the module name derived using file_regex
    extra_depends  -- extra runtime dependencies (RDEPENDS) to be set for
                      all packages. The default value of None causes a
                      dependency on the main package (${PN}) - if you do
                      not want this, pass '' for this parameter.
    aux_files_pattern -- extra item(s) to be added to FILES for each
                      package. Can be a single string item or a list of
                      strings for multiple items.  Must include %s.
    postrm         -- postrm script to use for all packages (as a string)
    allow_dirs     -- True allow directories to be matched - default False
    prepend        -- if True, prepend created packages to PACKAGES instead
                      of the default False which appends them
    match_path     -- match file_regex on the whole relative path to the
                      root rather than just the file name
    aux_files_pattern_verbatim -- extra item(s) to be added to FILES for
                      each package, using the actual derived module name
                      rather than converting it to something legal for a
                      package name. Can be a single string item or a list
                      of strings for multiple items. Must include %s.
    allow_links    -- True to allow symlinks to be matched - default False
    summary        -- Summary to set for each package. Must include %s;
                      defaults to description if not set.

    """

    dvar = d.getVar('PKGD')
    root = d.expand(root)
    output_pattern = d.expand(output_pattern)
    extra_depends = d.expand(extra_depends)

    # If the root directory doesn't exist, don't error out later but silently do
    # no splitting.
    if not os.path.exists(dvar + root):
        return []

    ml = d.getVar("MLPREFIX")
    if ml:
        if not output_pattern.startswith(ml):
            output_pattern = ml + output_pattern

        newdeps = []
        for dep in (extra_depends or "").split():
            if dep.startswith(ml):
                newdeps.append(dep)
            else:
                newdeps.append(ml + dep)
        if newdeps:
            extra_depends = " ".join(newdeps)


    packages = d.getVar('PACKAGES').split()
    split_packages = set()

    if postinst:
        postinst = '#!/bin/sh\n' + postinst + '\n'
    if postrm:
        postrm = '#!/bin/sh\n' + postrm + '\n'
    if not recursive:
        objs = os.listdir(dvar + root)
    else:
        objs = []
        for walkroot, dirs, files in os.walk(dvar + root):
            for file in files:
                relpath = os.path.join(walkroot, file).replace(dvar + root + '/', '', 1)
                if relpath:
                    objs.append(relpath)

    if extra_depends == None:
        extra_depends = d.getVar("PN")

    if not summary:
        summary = description

    for o in sorted(objs):
        import re, stat
        if match_path:
            m = re.match(file_regex, o)
        else:
            m = re.match(file_regex, os.path.basename(o))

        if not m:
            continue
        f = os.path.join(dvar + root, o)
        mode = os.lstat(f).st_mode
        if not (stat.S_ISREG(mode) or (allow_links and stat.S_ISLNK(mode)) or (allow_dirs and stat.S_ISDIR(mode))):
            continue
        on = legitimize_package_name(m.group(1))
        pkg = output_pattern % on
        split_packages.add(pkg)
        if not pkg in packages:
            if prepend:
                packages = [pkg] + packages
            else:
                packages.append(pkg)
        oldfiles = d.getVar('FILES_' + pkg)
        newfile = os.path.join(root, o)
        # These names will be passed through glob() so if the filename actually
        # contains * or ? (rare, but possible) we need to handle that specially
        newfile = newfile.replace('*', '[*]')
        newfile = newfile.replace('?', '[?]')
        if not oldfiles:
            the_files = [newfile]
            if aux_files_pattern:
                if type(aux_files_pattern) is list:
                    for fp in aux_files_pattern:
                        the_files.append(fp % on)
                else:
                    the_files.append(aux_files_pattern % on)
            if aux_files_pattern_verbatim:
                if type(aux_files_pattern_verbatim) is list:
                    for fp in aux_files_pattern_verbatim:
                        the_files.append(fp % m.group(1))
                else:
                    the_files.append(aux_files_pattern_verbatim % m.group(1))
            d.setVar('FILES_' + pkg, " ".join(the_files))
        else:
            d.setVar('FILES_' + pkg, oldfiles + " " + newfile)
        if extra_depends != '':
            d.appendVar('RDEPENDS_' + pkg, ' ' + extra_depends)
        if not d.getVar('DESCRIPTION_' + pkg):
            d.setVar('DESCRIPTION_' + pkg, description % on)
        if not d.getVar('SUMMARY_' + pkg):
            d.setVar('SUMMARY_' + pkg, summary % on)
        if postinst:
            d.setVar('pkg_postinst_' + pkg, postinst)
        if postrm:
            d.setVar('pkg_postrm_' + pkg, postrm)
        if callable(hook):
            hook(f, pkg, file_regex, output_pattern, m.group(1))

    d.setVar('PACKAGES', ' '.join(packages))
    return list(split_packages)

PACKAGE_DEPENDS += "file-native"

python () {
    if d.getVar('PACKAGES') != '':
        deps = ""
        for dep in (d.getVar('PACKAGE_DEPENDS') or "").split():
            deps += " %s:do_populate_sysroot" % dep
        d.appendVarFlag('do_package', 'depends', deps)

        # shlibs requires any DEPENDS to have already packaged for the *.list files
        d.appendVarFlag('do_package', 'deptask', " do_packagedata")
}

# Get a list of files from file vars by searching files under current working directory
# The list contains symlinks, directories and normal files.
def files_from_filevars(filevars):
    import os,glob
    cpath = oe.cachedpath.CachedPath()
    files = []
    for f in filevars:
        if os.path.isabs(f):
            f = '.' + f
        if not f.startswith("./"):
            f = './' + f
        globbed = glob.glob(f)
        if globbed:
            if [ f ] != globbed:
                files += globbed
                continue
        files.append(f)

    symlink_paths = []
    for ind, f in enumerate(files):
        # Handle directory symlinks. Truncate path to the lowest level symlink
        parent = ''
        for dirname in f.split('/')[:-1]:
            parent = os.path.join(parent, dirname)
            if dirname == '.':
                continue
            if cpath.islink(parent):
                bb.warn("FILES contains file '%s' which resides under a "
                        "directory symlink. Please fix the recipe and use the "
                        "real path for the file." % f[1:])
                symlink_paths.append(f)
                files[ind] = parent
                f = parent
                break

        if not cpath.islink(f):
            if cpath.isdir(f):
                newfiles = [ os.path.join(f,x) for x in os.listdir(f) ]
                if newfiles:
                    files += newfiles

    return files, symlink_paths

# Called in package_<rpm,ipk,deb>.bbclass to get the correct list of configuration files
def get_conffiles(pkg, d):
    pkgdest = d.getVar('PKGDEST')
    root = os.path.join(pkgdest, pkg)
    cwd = os.getcwd()
    os.chdir(root)

    conffiles = d.getVar('CONFFILES_%s' % pkg);
    if conffiles == None:
        conffiles = d.getVar('CONFFILES')
    if conffiles == None:
        conffiles = ""
    conffiles = conffiles.split()
    conf_orig_list = files_from_filevars(conffiles)[0]

    # Remove links and directories from conf_orig_list to get conf_list which only contains normal files
    conf_list = []
    for f in conf_orig_list:
        if os.path.isdir(f):
            continue
        if os.path.islink(f):
            continue
        if not os.path.exists(f):
            continue
        conf_list.append(f)

    # Remove the leading './'
    for i in range(0, len(conf_list)):
        conf_list[i] = conf_list[i][1:]

    os.chdir(cwd)
    return conf_list

def checkbuildpath(file, d):
    tmpdir = d.getVar('TMPDIR')
    with open(file) as f:
        file_content = f.read()
        if tmpdir in file_content:
            return True

    return False

def parse_debugsources_from_dwarfsrcfiles_output(dwarfsrcfiles_output):
    debugfiles = {}

    for line in dwarfsrcfiles_output.splitlines():
        if line.startswith("\t"):
            debugfiles[os.path.normpath(line.split()[0])] = ""

    return debugfiles.keys()

def source_info(file, d, fatal=True):
    import subprocess

    cmd = ["dwarfsrcfiles", file]
    try:
        output = subprocess.check_output(cmd, universal_newlines=True, stderr=subprocess.STDOUT)
        retval = 0
    except subprocess.CalledProcessError as exc:
        output = exc.output
        retval = exc.returncode

    # 255 means a specific file wasn't fully parsed to get the debug file list, which is not a fatal failure
    if retval != 0 and retval != 255:
        msg = "dwarfsrcfiles failed with exit code %s (cmd was %s)%s" % (retval, cmd, ":\n%s" % output if output else "")
        if fatal:
            bb.fatal(msg)
        bb.note(msg)

    debugsources = parse_debugsources_from_dwarfsrcfiles_output(output)

    return list(debugsources)

def splitdebuginfo(file, dvar, debugdir, debuglibdir, debugappend, debugsrcdir, d):
    # Function to split a single file into two components, one is the stripped
    # target system binary, the other contains any debugging information. The
    # two files are linked to reference each other.
    #
    # return a mapping of files:debugsources

    import stat
    import subprocess

    src = file[len(dvar):]
    dest = debuglibdir + os.path.dirname(src) + debugdir + "/" + os.path.basename(src) + debugappend
    debugfile = dvar + dest
    sources = []

    # Split the file...
    bb.utils.mkdirhier(os.path.dirname(debugfile))
    #bb.note("Split %s -> %s" % (file, debugfile))
    # Only store off the hard link reference if we successfully split!

    dvar = d.getVar('PKGD')
    objcopy = d.getVar("OBJCOPY")

    # We ignore kernel modules, we don't generate debug info files.
    if file.find("/lib/modules/") != -1 and file.endswith(".ko"):
        return (file, sources)

    newmode = None
    if not os.access(file, os.W_OK) or os.access(file, os.R_OK):
        origmode = os.stat(file)[stat.ST_MODE]
        newmode = origmode | stat.S_IWRITE | stat.S_IREAD
        os.chmod(file, newmode)

    # We need to extract the debug src information here...
    if debugsrcdir:
        sources = source_info(file, d)

    bb.utils.mkdirhier(os.path.dirname(debugfile))

    subprocess.check_output([objcopy, '--only-keep-debug', file, debugfile], stderr=subprocess.STDOUT)

    # Set the debuglink to have the view of the file path on the target
    subprocess.check_output([objcopy, '--add-gnu-debuglink', debugfile, file], stderr=subprocess.STDOUT)

    if newmode:
        os.chmod(file, origmode)

    return (file, sources)

def splitstaticdebuginfo(file, dvar, debugstaticdir, debugstaticlibdir, debugstaticappend, debugsrcdir, d):
    # Unlike the function above, there is no way to split a static library
    # two components.  So to get similar results we will copy the unmodified
    # static library (containing the debug symbols) into a new directory.
    # We will then strip (preserving symbols) the static library in the
    # typical location.
    #
    # return a mapping of files:debugsources

    import stat
    import shutil

    src = file[len(dvar):]
    dest = debugstaticlibdir + os.path.dirname(src) + debugstaticdir + "/" + os.path.basename(src) + debugstaticappend
    debugfile = dvar + dest
    sources = []

    # Copy the file...
    bb.utils.mkdirhier(os.path.dirname(debugfile))
    #bb.note("Copy %s -> %s" % (file, debugfile))

    dvar = d.getVar('PKGD')

    newmode = None
    if not os.access(file, os.W_OK) or os.access(file, os.R_OK):
        origmode = os.stat(file)[stat.ST_MODE]
        newmode = origmode | stat.S_IWRITE | stat.S_IREAD
        os.chmod(file, newmode)

    # We need to extract the debug src information here...
    if debugsrcdir:
        sources = source_info(file, d)

    bb.utils.mkdirhier(os.path.dirname(debugfile))

    # Copy the unmodified item to the debug directory
    shutil.copy2(file, debugfile)

    if newmode:
        os.chmod(file, origmode)

    return (file, sources)

def copydebugsources(debugsrcdir, sources, d):
    # The debug src information written out to sourcefile is further processed
    # and copied to the destination here.

    import stat
    import subprocess

    if debugsrcdir and sources:
        sourcefile = d.expand("${WORKDIR}/debugsources.list")
        bb.utils.remove(sourcefile)

        # filenames are null-separated - this is an artefact of the previous use
        # of rpm's debugedit, which was writing them out that way, and the code elsewhere
        # is still assuming that.
        debuglistoutput = '\0'.join(sources) + '\0'
        with open(sourcefile, 'a') as sf:
           sf.write(debuglistoutput)

        dvar = d.getVar('PKGD')
        strip = d.getVar("STRIP")
        objcopy = d.getVar("OBJCOPY")
        workdir = d.getVar("WORKDIR")
        workparentdir = os.path.dirname(os.path.dirname(workdir))
        workbasedir = os.path.basename(os.path.dirname(workdir)) + "/" + os.path.basename(workdir)

        # If build path exists in sourcefile, it means toolchain did not use
        # -fdebug-prefix-map to compile
        if checkbuildpath(sourcefile, d):
            localsrc_prefix = workparentdir + "/"
        else:
            localsrc_prefix = "/usr/src/debug/"

        nosuchdir = []
        basepath = dvar
        for p in debugsrcdir.split("/"):
            basepath = basepath + "/" + p
            if not cpath.exists(basepath):
                nosuchdir.append(basepath)
        bb.utils.mkdirhier(basepath)
        cpath.updatecache(basepath)

        # Ignore files from the recipe sysroots (target and native)
        processdebugsrc =  "LC_ALL=C ; sort -z -u '%s' | egrep -v -z '((<internal>|<built-in>)$|/.*recipe-sysroot.*/)' | "
        # We need to ignore files that are not actually ours
        # we do this by only paying attention to items from this package
        processdebugsrc += "fgrep -zw '%s' | "
        # Remove prefix in the source paths
        processdebugsrc += "sed 's#%s##g' | "
        processdebugsrc += "(cd '%s' ; cpio -pd0mlL --no-preserve-owner '%s%s' 2>/dev/null)"

        cmd = processdebugsrc % (sourcefile, workbasedir, localsrc_prefix, workparentdir, dvar, debugsrcdir)
        try:
            subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError:
            # Can "fail" if internal headers/transient sources are attempted
            pass

        # cpio seems to have a bug with -lL together and symbolic links are just copied, not dereferenced.
        # Work around this by manually finding and copying any symbolic links that made it through.
        cmd = "find %s%s -type l -print0 -delete | sed s#%s%s/##g | (cd '%s' ; cpio -pd0mL --no-preserve-owner '%s%s')" % \
                (dvar, debugsrcdir, dvar, debugsrcdir, workparentdir, dvar, debugsrcdir)
        subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

        # The copy by cpio may have resulted in some empty directories!  Remove these
        cmd = "find %s%s -empty -type d -delete" % (dvar, debugsrcdir)
        subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

        # Also remove debugsrcdir if its empty
        for p in nosuchdir[::-1]:
            if os.path.exists(p) and not os.listdir(p):
                os.rmdir(p)

#
# Package data handling routines
#

def get_package_mapping (pkg, basepkg, d):
    import oe.packagedata

    data = oe.packagedata.read_subpkgdata(pkg, d)
    key = "PKG_%s" % pkg

    if key in data:
        # Have to avoid undoing the write_extra_pkgs(global_variants...)
        if bb.data.inherits_class('allarch', d) and not d.getVar('MULTILIB_VARIANTS') \
            and data[key] == basepkg:
            return pkg
        return data[key]

    return pkg

def get_package_additional_metadata (pkg_type, d):
    base_key = "PACKAGE_ADD_METADATA"
    for key in ("%s_%s" % (base_key, pkg_type.upper()), base_key):
        if d.getVar(key, False) is None:
            continue
        d.setVarFlag(key, "type", "list")
        if d.getVarFlag(key, "separator") is None:
            d.setVarFlag(key, "separator", "\\n")
        metadata_fields = [field.strip() for field in oe.data.typed_value(key, d)]
        return "\n".join(metadata_fields).strip()

def runtime_mapping_rename (varname, pkg, d):
    #bb.note("%s before: %s" % (varname, d.getVar(varname)))

    new_depends = {}
    deps = bb.utils.explode_dep_versions2(d.getVar(varname) or "")
    for depend in deps:
        new_depend = get_package_mapping(depend, pkg, d)
        new_depends[new_depend] = deps[depend]

    d.setVar(varname, bb.utils.join_deps(new_depends, commasep=False))

    #bb.note("%s after: %s" % (varname, d.getVar(varname)))

#
# Package functions suitable for inclusion in PACKAGEFUNCS
#

python package_get_auto_pr() {
    import oe.prservice
    import re

    # Support per recipe PRSERV_HOST
    pn = d.getVar('PN')
    host = d.getVar("PRSERV_HOST_" + pn)
    if not (host is None):
        d.setVar("PRSERV_HOST", host)

    pkgv = d.getVar("PKGV")

    # PR Server not active, handle AUTOINC
    if not d.getVar('PRSERV_HOST'):
        if 'AUTOINC' in pkgv:
            d.setVar("PKGV", pkgv.replace("AUTOINC", "0"))
        return

    auto_pr = None
    pv = d.getVar("PV")
    version = d.getVar("PRAUTOINX")
    pkgarch = d.getVar("PACKAGE_ARCH")
    checksum = d.getVar("BB_TASKHASH")

    if d.getVar('PRSERV_LOCKDOWN'):
        auto_pr = d.getVar('PRAUTO_' + version + '_' + pkgarch) or d.getVar('PRAUTO_' + version) or None
        if auto_pr is None:
            bb.fatal("Can NOT get PRAUTO from lockdown exported file")
        d.setVar('PRAUTO',str(auto_pr))
        return

    try:
        conn = d.getVar("__PRSERV_CONN")
        if conn is None:
            conn = oe.prservice.prserv_make_conn(d)
        if conn is not None:
            if "AUTOINC" in pkgv:
                srcpv = bb.fetch2.get_srcrev(d)
                base_ver = "AUTOINC-%s" % version[:version.find(srcpv)]
                value = conn.getPR(base_ver, pkgarch, srcpv)
                d.setVar("PKGV", pkgv.replace("AUTOINC", str(value)))

            auto_pr = conn.getPR(version, pkgarch, checksum)
    except Exception as e:
        bb.fatal("Can NOT get PRAUTO, exception %s" %  str(e))
    if auto_pr is None:
        bb.fatal("Can NOT get PRAUTO from remote PR service")
    d.setVar('PRAUTO',str(auto_pr))
}

LOCALEBASEPN ??= "${PN}"

python package_do_split_locales() {
    if (d.getVar('PACKAGE_NO_LOCALE') == '1'):
        bb.debug(1, "package requested not splitting locales")
        return

    packages = (d.getVar('PACKAGES') or "").split()

    datadir = d.getVar('datadir')
    if not datadir:
        bb.note("datadir not defined")
        return

    dvar = d.getVar('PKGD')
    pn = d.getVar('LOCALEBASEPN')

    if pn + '-locale' in packages:
        packages.remove(pn + '-locale')

    localedir = os.path.join(dvar + datadir, 'locale')

    if not cpath.isdir(localedir):
        bb.debug(1, "No locale files in this package")
        return

    locales = os.listdir(localedir)

    summary = d.getVar('SUMMARY') or pn
    description = d.getVar('DESCRIPTION') or ""
    locale_section = d.getVar('LOCALE_SECTION')
    mlprefix = d.getVar('MLPREFIX') or ""
    for l in sorted(locales):
        ln = legitimize_package_name(l)
        pkg = pn + '-locale-' + ln
        packages.append(pkg)
        d.setVar('FILES_' + pkg, os.path.join(datadir, 'locale', l))
        d.setVar('RRECOMMENDS_' + pkg, '%svirtual-locale-%s' % (mlprefix, ln))
        d.setVar('RPROVIDES_' + pkg, '%s-locale %s%s-translation' % (pn, mlprefix, ln))
        d.setVar('SUMMARY_' + pkg, '%s - %s translations' % (summary, l))
        d.setVar('DESCRIPTION_' + pkg, '%s  This package contains language translation files for the %s locale.' % (description, l))
        if locale_section:
            d.setVar('SECTION_' + pkg, locale_section)

    d.setVar('PACKAGES', ' '.join(packages))

    # Disabled by RP 18/06/07
    # Wildcards aren't supported in debian
    # They break with ipkg since glibc-locale* will mean that
    # glibc-localedata-translit* won't install as a dependency
    # for some other package which breaks meta-toolchain
    # Probably breaks since virtual-locale- isn't provided anywhere
    #rdep = (d.getVar('RDEPENDS_%s' % pn) or "").split()
    #rdep.append('%s-locale*' % pn)
    #d.setVar('RDEPENDS_%s' % pn, ' '.join(rdep))
}

python perform_packagecopy () {
    import subprocess

    dest = d.getVar('D')
    dvar = d.getVar('PKGD')

    # Start by package population by taking a copy of the installed
    # files to operate on
    # Preserve sparse files and hard links
    cmd = 'tar -cf - -C %s -p -S . | tar -xf - -C %s' % (dest, dvar)
    subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

    # replace RPATHs for the nativesdk binaries, to make them relocatable
    if bb.data.inherits_class('nativesdk', d) or bb.data.inherits_class('cross-canadian', d):
        rpath_replace (dvar, d)
}
perform_packagecopy[cleandirs] = "${PKGD}"
perform_packagecopy[dirs] = "${PKGD}"

# We generate a master list of directories to process, we start by
# seeding this list with reasonable defaults, then load from
# the fs-perms.txt files
python fixup_perms () {
    import pwd, grp

    # init using a string with the same format as a line as documented in
    # the fs-perms.txt file
    # <path> <mode> <uid> <gid> <walk> <fmode> <fuid> <fgid>
    # <path> link <link target>
    #
    # __str__ can be used to print out an entry in the input format
    #
    # if fs_perms_entry.path is None:
    #    an error occurred
    # if fs_perms_entry.link, you can retrieve:
    #    fs_perms_entry.path = path
    #    fs_perms_entry.link = target of link
    # if not fs_perms_entry.link, you can retrieve:
    #    fs_perms_entry.path = path
    #    fs_perms_entry.mode = expected dir mode or None
    #    fs_perms_entry.uid = expected uid or -1
    #    fs_perms_entry.gid = expected gid or -1
    #    fs_perms_entry.walk = 'true' or something else
    #    fs_perms_entry.fmode = expected file mode or None
    #    fs_perms_entry.fuid = expected file uid or -1
    #    fs_perms_entry_fgid = expected file gid or -1
    class fs_perms_entry():
        def __init__(self, line):
            lsplit = line.split()
            if len(lsplit) == 3 and lsplit[1].lower() == "link":
                self._setlink(lsplit[0], lsplit[2])
            elif len(lsplit) == 8:
                self._setdir(lsplit[0], lsplit[1], lsplit[2], lsplit[3], lsplit[4], lsplit[5], lsplit[6], lsplit[7])
            else:
                msg = "Fixup Perms: invalid config line %s" % line
                package_qa_handle_error("perm-config", msg, d)
                self.path = None
                self.link = None

        def _setdir(self, path, mode, uid, gid, walk, fmode, fuid, fgid):
            self.path = os.path.normpath(path)
            self.link = None
            self.mode = self._procmode(mode)
            self.uid  = self._procuid(uid)
            self.gid  = self._procgid(gid)
            self.walk = walk.lower()
            self.fmode = self._procmode(fmode)
            self.fuid = self._procuid(fuid)
            self.fgid = self._procgid(fgid)

        def _setlink(self, path, link):
            self.path = os.path.normpath(path)
            self.link = link

        def _procmode(self, mode):
            if not mode or (mode and mode == "-"):
                return None
            else:
                return int(mode,8)

        # Note uid/gid -1 has special significance in os.lchown
        def _procuid(self, uid):
            if uid is None or uid == "-":
                return -1
            elif uid.isdigit():
                return int(uid)
            else:
                return pwd.getpwnam(uid).pw_uid

        def _procgid(self, gid):
            if gid is None or gid == "-":
                return -1
            elif gid.isdigit():
                return int(gid)
            else:
                return grp.getgrnam(gid).gr_gid

        # Use for debugging the entries
        def __str__(self):
            if self.link:
                return "%s link %s" % (self.path, self.link)
            else:
                mode = "-"
                if self.mode:
                    mode = "0%o" % self.mode
                fmode = "-"
                if self.fmode:
                    fmode = "0%o" % self.fmode
                uid = self._mapugid(self.uid)
                gid = self._mapugid(self.gid)
                fuid = self._mapugid(self.fuid)
                fgid = self._mapugid(self.fgid)
                return "%s %s %s %s %s %s %s %s" % (self.path, mode, uid, gid, self.walk, fmode, fuid, fgid)

        def _mapugid(self, id):
            if id is None or id == -1:
                return "-"
            else:
                return "%d" % id

    # Fix the permission, owner and group of path
    def fix_perms(path, mode, uid, gid, dir):
        if mode and not os.path.islink(path):
            #bb.note("Fixup Perms: chmod 0%o %s" % (mode, dir))
            os.chmod(path, mode)
        # -1 is a special value that means don't change the uid/gid
        # if they are BOTH -1, don't bother to lchown
        if not (uid == -1 and gid == -1):
            #bb.note("Fixup Perms: lchown %d:%d %s" % (uid, gid, dir))
            os.lchown(path, uid, gid)

    # Return a list of configuration files based on either the default
    # files/fs-perms.txt or the contents of FILESYSTEM_PERMS_TABLES
    # paths are resolved via BBPATH
    def get_fs_perms_list(d):
        str = ""
        bbpath = d.getVar('BBPATH')
        fs_perms_tables = d.getVar('FILESYSTEM_PERMS_TABLES') or ""
        for conf_file in fs_perms_tables.split():
            confpath = bb.utils.which(bbpath, conf_file)
            if confpath:
                str += " %s" % bb.utils.which(bbpath, conf_file)
            else:
                bb.warn("cannot find %s specified in FILESYSTEM_PERMS_TABLES" % conf_file)
        return str



    dvar = d.getVar('PKGD')

    fs_perms_table = {}
    fs_link_table = {}

    # By default all of the standard directories specified in
    # bitbake.conf will get 0755 root:root.
    target_path_vars = [    'base_prefix',
                'prefix',
                'exec_prefix',
                'base_bindir',
                'base_sbindir',
                'base_libdir',
                'datadir',
                'sysconfdir',
                'servicedir',
                'sharedstatedir',
                'localstatedir',
                'infodir',
                'mandir',
                'docdir',
                'bindir',
                'sbindir',
                'libexecdir',
                'libdir',
                'includedir',
                'oldincludedir' ]

    for path in target_path_vars:
        dir = d.getVar(path) or ""
        if dir == "":
            continue
        fs_perms_table[dir] = fs_perms_entry(d.expand("%s 0755 root root false - - -" % (dir)))

    # Now we actually load from the configuration files
    for conf in get_fs_perms_list(d).split():
        if not os.path.exists(conf):
            continue
        with open(conf) as f:
            for line in f:
                if line.startswith('#'):
                    continue
                lsplit = line.split()
                if len(lsplit) == 0:
                    continue
                if len(lsplit) != 8 and not (len(lsplit) == 3 and lsplit[1].lower() == "link"):
                    msg = "Fixup perms: %s invalid line: %s" % (conf, line)
                    package_qa_handle_error("perm-line", msg, d)
                    continue
                entry = fs_perms_entry(d.expand(line))
                if entry and entry.path:
                    if entry.link:
                        fs_link_table[entry.path] = entry
                        if entry.path in fs_perms_table:
                            fs_perms_table.pop(entry.path)
                    else:
                        fs_perms_table[entry.path] = entry
                        if entry.path in fs_link_table:
                            fs_link_table.pop(entry.path)

    # Debug -- list out in-memory table
    #for dir in fs_perms_table:
    #    bb.note("Fixup Perms: %s: %s" % (dir, str(fs_perms_table[dir])))
    #for link in fs_link_table:
    #    bb.note("Fixup Perms: %s: %s" % (link, str(fs_link_table[link])))

    # We process links first, so we can go back and fixup directory ownership
    # for any newly created directories
    # Process in sorted order so /run gets created before /run/lock, etc.
    for entry in sorted(fs_link_table.values(), key=lambda x: x.link):
        link = entry.link
        dir = entry.path
        origin = dvar + dir
        if not (cpath.exists(origin) and cpath.isdir(origin) and not cpath.islink(origin)):
            continue

        if link[0] == "/":
            target = dvar + link
            ptarget = link
        else:
            target = os.path.join(os.path.dirname(origin), link)
            ptarget = os.path.join(os.path.dirname(dir), link)
        if os.path.exists(target):
            msg = "Fixup Perms: Unable to correct directory link, target already exists: %s -> %s" % (dir, ptarget)
            package_qa_handle_error("perm-link", msg, d)
            continue

        # Create path to move directory to, move it, and then setup the symlink
        bb.utils.mkdirhier(os.path.dirname(target))
        #bb.note("Fixup Perms: Rename %s -> %s" % (dir, ptarget))
        os.rename(origin, target)
        #bb.note("Fixup Perms: Link %s -> %s" % (dir, link))
        os.symlink(link, origin)

    for dir in fs_perms_table:
        origin = dvar + dir
        if not (cpath.exists(origin) and cpath.isdir(origin)):
            continue

        fix_perms(origin, fs_perms_table[dir].mode, fs_perms_table[dir].uid, fs_perms_table[dir].gid, dir)

        if fs_perms_table[dir].walk == 'true':
            for root, dirs, files in os.walk(origin):
                for dr in dirs:
                    each_dir = os.path.join(root, dr)
                    fix_perms(each_dir, fs_perms_table[dir].mode, fs_perms_table[dir].uid, fs_perms_table[dir].gid, dir)
                for f in files:
                    each_file = os.path.join(root, f)
                    fix_perms(each_file, fs_perms_table[dir].fmode, fs_perms_table[dir].fuid, fs_perms_table[dir].fgid, dir)
}

python split_and_strip_files () {
    import stat, errno
    import subprocess

    dvar = d.getVar('PKGD')
    pn = d.getVar('PN')
    targetos = d.getVar('TARGET_OS')

    oldcwd = os.getcwd()
    os.chdir(dvar)

    # We default to '.debug' style
    if d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-file-directory':
        # Single debug-file-directory style debug info
        debugappend = ".debug"
        debugstaticappend = ""
        debugdir = ""
        debugstaticdir = ""
        debuglibdir = "/usr/lib/debug"
        debugstaticlibdir = "/usr/lib/debug-static"
        debugsrcdir = "/usr/src/debug"
    elif d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-without-src':
        # Original OE-core, a.k.a. ".debug", style debug info, but without sources in /usr/src/debug
        debugappend = ""
        debugstaticappend = ""
        debugdir = "/.debug"
        debugstaticdir = "/.debug-static"
        debuglibdir = ""
        debugstaticlibdir = ""
        debugsrcdir = ""
    elif d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-with-srcpkg':
        debugappend = ""
        debugstaticappend = ""
        debugdir = "/.debug"
        debugstaticdir = "/.debug-static"
        debuglibdir = ""
        debugstaticlibdir = ""
        debugsrcdir = "/usr/src/debug"
    else:
        # Original OE-core, a.k.a. ".debug", style debug info
        debugappend = ""
        debugstaticappend = ""
        debugdir = "/.debug"
        debugstaticdir = "/.debug-static"
        debuglibdir = ""
        debugstaticlibdir = ""
        debugsrcdir = "/usr/src/debug"

    #
    # First lets figure out all of the files we may have to process ... do this only once!
    #
    elffiles = {}
    symlinks = {}
    kernmods = []
    staticlibs = []
    inodes = {}
    libdir = os.path.abspath(dvar + os.sep + d.getVar("libdir"))
    baselibdir = os.path.abspath(dvar + os.sep + d.getVar("base_libdir"))
    skipfiles = (d.getVar("INHIBIT_PACKAGE_STRIP_FILES") or "").split()
    if (d.getVar('INHIBIT_PACKAGE_STRIP') != '1' or \
            d.getVar('INHIBIT_PACKAGE_DEBUG_SPLIT') != '1'):
        checkelf = {}
        checkelflinks = {}
        for root, dirs, files in cpath.walk(dvar):
            for f in files:
                file = os.path.join(root, f)

                # Skip debug files
                if debugappend and file.endswith(debugappend):
                    continue
                if debugdir and debugdir in os.path.dirname(file[len(dvar):]):
                    continue

                if file in skipfiles:
                    continue

                if file.endswith(".ko") and file.find("/lib/modules/") != -1:
                    kernmods.append(file)
                    continue
                if oe.package.is_static_lib(file):
                    staticlibs.append(file)
                    continue

                try:
                    ltarget = cpath.realpath(file, dvar, False)
                    s = cpath.lstat(ltarget)
                except OSError as e:
                    (err, strerror) = e.args
                    if err != errno.ENOENT:
                        raise
                    # Skip broken symlinks
                    continue
                if not s:
                    continue
                # Check its an executable
                if (s[stat.ST_MODE] & stat.S_IXUSR) or (s[stat.ST_MODE] & stat.S_IXGRP) or (s[stat.ST_MODE] & stat.S_IXOTH) \
                        or ((file.startswith(libdir) or file.startswith(baselibdir)) and (".so" in f or ".node" in f)):

                    if cpath.islink(file):
                        checkelflinks[file] = ltarget
                        continue
                    # Use a reference of device ID and inode number to identify files
                    file_reference = "%d_%d" % (s.st_dev, s.st_ino)
                    checkelf[file] = (file, file_reference)

        results = oe.utils.multiprocess_launch(oe.package.is_elf, checkelflinks.values(), d)
        results_map = {}
        for (ltarget, elf_file) in results:
            results_map[ltarget] = elf_file
        for file in checkelflinks:
            ltarget = checkelflinks[file]
            # If it's a symlink, and points to an ELF file, we capture the readlink target
            if results_map[ltarget]:
                target = os.readlink(file)
                #bb.note("Sym: %s (%d)" % (ltarget, results_map[ltarget]))
                symlinks[file] = target

        results = oe.utils.multiprocess_launch(oe.package.is_elf, checkelf.keys(), d)

        # Sort results by file path. This ensures that the files are always
        # processed in the same order, which is important to make sure builds
        # are reproducible when dealing with hardlinks
        results.sort(key=lambda x: x[0])

        for (file, elf_file) in results:
            # It's a file (or hardlink), not a link
            # ...but is it ELF, and is it already stripped?
            if elf_file & 1:
                if elf_file & 2:
                    if 'already-stripped' in (d.getVar('INSANE_SKIP_' + pn) or "").split():
                        bb.note("Skipping file %s from %s for already-stripped QA test" % (file[len(dvar):], pn))
                    else:
                        msg = "File '%s' from %s was already stripped, this will prevent future debugging!" % (file[len(dvar):], pn)
                        package_qa_handle_error("already-stripped", msg, d)
                    continue

                # At this point we have an unstripped elf file. We need to:
                #  a) Make sure any file we strip is not hardlinked to anything else outside this tree
                #  b) Only strip any hardlinked file once (no races)
                #  c) Track any hardlinks between files so that we can reconstruct matching debug file hardlinks

                # Use a reference of device ID and inode number to identify files
                file_reference = checkelf[file][1]
                if file_reference in inodes:
                    os.unlink(file)
                    os.link(inodes[file_reference][0], file)
                    inodes[file_reference].append(file)
                else:
                    inodes[file_reference] = [file]
                    # break hardlink
                    bb.utils.break_hardlinks(file)
                    elffiles[file] = elf_file
                # Modified the file so clear the cache
                cpath.updatecache(file)

    #
    # First lets process debug splitting
    #
    if (d.getVar('INHIBIT_PACKAGE_DEBUG_SPLIT') != '1'):
        results = oe.utils.multiprocess_launch(splitdebuginfo, list(elffiles), d, extraargs=(dvar, debugdir, debuglibdir, debugappend, debugsrcdir, d))

        if debugsrcdir and not targetos.startswith("mingw"):
            if (d.getVar('PACKAGE_DEBUG_STATIC_SPLIT') == '1'):
                results = oe.utils.multiprocess_launch(splitstaticdebuginfo, staticlibs, d, extraargs=(dvar, debugstaticdir, debugstaticlibdir, debugstaticappend, debugsrcdir, d))
            else:
                for file in staticlibs:
                    results.append( (file,source_info(file, d)) )

        sources = set()
        for r in results:
            sources.update(r[1])

        # Hardlink our debug symbols to the other hardlink copies
        for ref in inodes:
            if len(inodes[ref]) == 1:
                continue

            target = inodes[ref][0][len(dvar):]
            for file in inodes[ref][1:]:
                src = file[len(dvar):]
                dest = debuglibdir + os.path.dirname(src) + debugdir + "/" + os.path.basename(target) + debugappend
                fpath = dvar + dest
                ftarget = dvar + debuglibdir + os.path.dirname(target) + debugdir + "/" + os.path.basename(target) + debugappend
                bb.utils.mkdirhier(os.path.dirname(fpath))
                # Only one hardlink of separated debug info file in each directory
                if not os.access(fpath, os.R_OK):
                    #bb.note("Link %s -> %s" % (fpath, ftarget))
                    os.link(ftarget, fpath)

        # Create symlinks for all cases we were able to split symbols
        for file in symlinks:
            src = file[len(dvar):]
            dest = debuglibdir + os.path.dirname(src) + debugdir + "/" + os.path.basename(src) + debugappend
            fpath = dvar + dest
            # Skip it if the target doesn't exist
            try:
                s = os.stat(fpath)
            except OSError as e:
                (err, strerror) = e.args
                if err != errno.ENOENT:
                    raise
                continue

            ltarget = symlinks[file]
            lpath = os.path.dirname(ltarget)
            lbase = os.path.basename(ltarget)
            ftarget = ""
            if lpath and lpath != ".":
                ftarget += lpath + debugdir + "/"
            ftarget += lbase + debugappend
            if lpath.startswith(".."):
                ftarget = os.path.join("..", ftarget)
            bb.utils.mkdirhier(os.path.dirname(fpath))
            #bb.note("Symlink %s -> %s" % (fpath, ftarget))
            os.symlink(ftarget, fpath)

        # Process the debugsrcdir if requested...
        # This copies and places the referenced sources for later debugging...
        copydebugsources(debugsrcdir, sources, d)
    #
    # End of debug splitting
    #

    #
    # Now lets go back over things and strip them
    #
    if (d.getVar('INHIBIT_PACKAGE_STRIP') != '1'):
        strip = d.getVar("STRIP")
        sfiles = []
        for file in elffiles:
            elf_file = int(elffiles[file])
            #bb.note("Strip %s" % file)
            sfiles.append((file, elf_file, strip))
        for f in kernmods:
            sfiles.append((f, 16, strip))
        if (d.getVar('PACKAGE_STRIP_STATIC') == '1' or d.getVar('PACKAGE_DEBUG_STATIC_SPLIT') == '1'):
            for f in staticlibs:
                sfiles.append((f, 16, strip))

        oe.utils.multiprocess_launch(oe.package.runstrip, sfiles, d)

    #
    # End of strip
    #
    os.chdir(oldcwd)
}

python populate_packages () {
    import glob, re

    workdir = d.getVar('WORKDIR')
    outdir = d.getVar('DEPLOY_DIR')
    dvar = d.getVar('PKGD')
    packages = d.getVar('PACKAGES').split()
    pn = d.getVar('PN')

    bb.utils.mkdirhier(outdir)
    os.chdir(dvar)
    
    autodebug = not (d.getVar("NOAUTOPACKAGEDEBUG") or False)

    split_source_package = (d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-with-srcpkg')

    # If debug-with-srcpkg mode is enabled then add the source package if it
    # doesn't exist and add the source file contents to the source package.
    if split_source_package:
        src_package_name = ('%s-src' % d.getVar('PN'))
        if not src_package_name in packages:
            packages.append(src_package_name)
        d.setVar('FILES_%s' % src_package_name, '/usr/src/debug')

    # Sanity check PACKAGES for duplicates
    # Sanity should be moved to sanity.bbclass once we have the infrastructure
    package_dict = {}

    for i, pkg in enumerate(packages):
        if pkg in package_dict:
            msg = "%s is listed in PACKAGES multiple times, this leads to packaging errors." % pkg
            package_qa_handle_error("packages-list", msg, d)
        # Ensure the source package gets the chance to pick up the source files
        # before the debug package by ordering it first in PACKAGES. Whether it
        # actually picks up any source files is controlled by
        # PACKAGE_DEBUG_SPLIT_STYLE.
        elif pkg.endswith("-src"):
            package_dict[pkg] = (10, i)
        elif autodebug and pkg.endswith("-dbg"):
            package_dict[pkg] = (30, i)
        else:
            package_dict[pkg] = (50, i)
    packages = sorted(package_dict.keys(), key=package_dict.get)
    d.setVar('PACKAGES', ' '.join(packages))
    pkgdest = d.getVar('PKGDEST')

    seen = []

    # os.mkdir masks the permissions with umask so we have to unset it first
    oldumask = os.umask(0)

    debug = []
    for root, dirs, files in cpath.walk(dvar):
        dir = root[len(dvar):]
        if not dir:
            dir = os.sep
        for f in (files + dirs):
            path = "." + os.path.join(dir, f)
            if "/.debug/" in path or "/.debug-static/" in path or path.endswith("/.debug"):
                debug.append(path)

    for pkg in packages:
        root = os.path.join(pkgdest, pkg)
        bb.utils.mkdirhier(root)

        filesvar = d.getVar('FILES_%s' % pkg) or ""
        if "//" in filesvar:
            msg = "FILES variable for package %s contains '//' which is invalid. Attempting to fix this but you should correct the metadata.\n" % pkg
            package_qa_handle_error("files-invalid", msg, d)
            filesvar.replace("//", "/")

        origfiles = filesvar.split()
        files, symlink_paths = files_from_filevars(origfiles)

        if autodebug and pkg.endswith("-dbg"):
            files.extend(debug)

        for file in files:
            if (not cpath.islink(file)) and (not cpath.exists(file)):
                continue
            if file in seen:
                continue
            seen.append(file)

            def mkdir(src, dest, p):
                src = os.path.join(src, p)
                dest = os.path.join(dest, p)
                fstat = cpath.stat(src)
                os.mkdir(dest)
                os.chmod(dest, fstat.st_mode)
                os.chown(dest, fstat.st_uid, fstat.st_gid)
                if p not in seen:
                    seen.append(p)
                cpath.updatecache(dest)

            def mkdir_recurse(src, dest, paths):
                if cpath.exists(dest + '/' + paths):
                    return
                while paths.startswith("./"):
                    paths = paths[2:]
                p = "."
                for c in paths.split("/"):
                    p = os.path.join(p, c)
                    if not cpath.exists(os.path.join(dest, p)):
                        mkdir(src, dest, p)

            if cpath.isdir(file) and not cpath.islink(file):
                mkdir_recurse(dvar, root, file)
                continue

            mkdir_recurse(dvar, root, os.path.dirname(file))
            fpath = os.path.join(root,file)
            if not cpath.islink(file):
                os.link(file, fpath)
                continue
            ret = bb.utils.copyfile(file, fpath)
            if ret is False or ret == 0:
                bb.fatal("File population failed")

        # Check if symlink paths exist
        for file in symlink_paths:
            if not os.path.exists(os.path.join(root,file)):
                bb.fatal("File '%s' cannot be packaged into '%s' because its "
                         "parent directory structure does not exist. One of "
                         "its parent directories is a symlink whose target "
                         "directory is not included in the package." %
                         (file, pkg))

    os.umask(oldumask)
    os.chdir(workdir)

    # Handle LICENSE_EXCLUSION
    package_list = []
    for pkg in packages:
        licenses = d.getVar('LICENSE_EXCLUSION-' + pkg)
        if licenses:
            msg = "Excluding %s from packaging as it has incompatible license(s): %s" % (pkg, licenses)
            package_qa_handle_error("incompatible-license", msg, d)
        else:
            package_list.append(pkg)
    d.setVar('PACKAGES', ' '.join(package_list))

    unshipped = []
    for root, dirs, files in cpath.walk(dvar):
        dir = root[len(dvar):]
        if not dir:
            dir = os.sep
        for f in (files + dirs):
            path = os.path.join(dir, f)
            if ('.' + path) not in seen:
                unshipped.append(path)

    if unshipped != []:
        msg = pn + ": Files/directories were installed but not shipped in any package:"
        if "installed-vs-shipped" in (d.getVar('INSANE_SKIP_' + pn) or "").split():
            bb.note("Package %s skipping QA tests: installed-vs-shipped" % pn)
        else:
            for f in unshipped:
                msg = msg + "\n  " + f
            msg = msg + "\nPlease set FILES such that these items are packaged. Alternatively if they are unneeded, avoid installing them or delete them within do_install.\n"
            msg = msg + "%s: %d installed and not shipped files." % (pn, len(unshipped))
            package_qa_handle_error("installed-vs-shipped", msg, d)
}
populate_packages[dirs] = "${D}"

python package_fixsymlinks () {
    import errno
    pkgdest = d.getVar('PKGDEST')
    packages = d.getVar("PACKAGES", False).split()

    dangling_links = {}
    pkg_files = {}
    for pkg in packages:
        dangling_links[pkg] = []
        pkg_files[pkg] = []
        inst_root = os.path.join(pkgdest, pkg)
        for path in pkgfiles[pkg]:
                rpath = path[len(inst_root):]
                pkg_files[pkg].append(rpath)
                rtarget = cpath.realpath(path, inst_root, True, assume_dir = True)
                if not cpath.lexists(rtarget):
                    dangling_links[pkg].append(os.path.normpath(rtarget[len(inst_root):]))

    newrdepends = {}
    for pkg in dangling_links:
        for l in dangling_links[pkg]:
            found = False
            bb.debug(1, "%s contains dangling link %s" % (pkg, l))
            for p in packages:
                if l in pkg_files[p]:
                        found = True
                        bb.debug(1, "target found in %s" % p)
                        if p == pkg:
                            break
                        if pkg not in newrdepends:
                            newrdepends[pkg] = []
                        newrdepends[pkg].append(p)
                        break
            if found == False:
                bb.note("%s contains dangling symlink to %s" % (pkg, l))

    for pkg in newrdepends:
        rdepends = bb.utils.explode_dep_versions2(d.getVar('RDEPENDS_' + pkg) or "")
        for p in newrdepends[pkg]:
            if p not in rdepends:
                rdepends[p] = []
        d.setVar('RDEPENDS_' + pkg, bb.utils.join_deps(rdepends, commasep=False))
}


python package_package_name_hook() {
    """
    A package_name_hook function can be used to rewrite the package names by
    changing PKG.  For an example, see debian.bbclass.
    """
    pass
}

EXPORT_FUNCTIONS package_name_hook


PKGDESTWORK = "${WORKDIR}/pkgdata"

PKGDATA_VARS = "PN PE PV PR PKGE PKGV PKGR LICENSE DESCRIPTION SUMMARY RDEPENDS RPROVIDES RRECOMMENDS RSUGGESTS RREPLACES RCONFLICTS SECTION PKG ALLOW_EMPTY FILES CONFFILES FILES_INFO pkg_postinst pkg_postrm pkg_preinst pkg_prerm"

python emit_pkgdata() {
    from glob import glob
    import json

    def process_postinst_on_target(pkg, mlprefix):
        pkgval = d.getVar('PKG_%s' % pkg)
        if pkgval is None:
            pkgval = pkg

        defer_fragment = """
if [ -n "$D" ]; then
    $INTERCEPT_DIR/postinst_intercept delay_to_first_boot %s mlprefix=%s
    exit 0
fi
""" % (pkgval, mlprefix)

        postinst = d.getVar('pkg_postinst_%s' % pkg)
        postinst_ontarget = d.getVar('pkg_postinst_ontarget_%s' % pkg)

        if postinst_ontarget:
            bb.debug(1, 'adding deferred pkg_postinst_ontarget() to pkg_postinst() for %s' % pkg)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += defer_fragment
            postinst += postinst_ontarget
            d.setVar('pkg_postinst_%s' % pkg, postinst)

    def add_set_e_to_scriptlets(pkg):
        for scriptlet_name in ('pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm'):
            scriptlet = d.getVar('%s_%s' % (scriptlet_name, pkg))
            if scriptlet:
                scriptlet_split = scriptlet.split('\n')
                if scriptlet_split[0].startswith("#!"):
                    scriptlet = scriptlet_split[0] + "\nset -e\n" + "\n".join(scriptlet_split[1:])
                else:
                    scriptlet = "set -e\n" + "\n".join(scriptlet_split[0:])
            d.setVar('%s_%s' % (scriptlet_name, pkg), scriptlet)

    def write_if_exists(f, pkg, var):
        def encode(str):
            import codecs
            c = codecs.getencoder("unicode_escape")
            return c(str)[0].decode("latin1")

        val = d.getVar('%s_%s' % (var, pkg))
        if val:
            f.write('%s_%s: %s\n' % (var, pkg, encode(val)))
            return val
        val = d.getVar('%s' % (var))
        if val:
            f.write('%s: %s\n' % (var, encode(val)))
        return val

    def write_extra_pkgs(variants, pn, packages, pkgdatadir):
        for variant in variants:
            with open("%s/%s-%s" % (pkgdatadir, variant, pn), 'w') as fd:
                fd.write("PACKAGES: %s\n" % ' '.join(
                            map(lambda pkg: '%s-%s' % (variant, pkg), packages.split())))

    def write_extra_runtime_pkgs(variants, packages, pkgdatadir):
        for variant in variants:
            for pkg in packages.split():
                ml_pkg = "%s-%s" % (variant, pkg)
                subdata_file = "%s/runtime/%s" % (pkgdatadir, ml_pkg)
                with open(subdata_file, 'w') as fd:
                    fd.write("PKG_%s: %s" % (ml_pkg, pkg))

    packages = d.getVar('PACKAGES')
    pkgdest = d.getVar('PKGDEST')
    pkgdatadir = d.getVar('PKGDESTWORK')

    data_file = pkgdatadir + d.expand("/${PN}")
    with open(data_file, 'w') as fd:
        fd.write("PACKAGES: %s\n" % packages)

    pn = d.getVar('PN')
    global_variants = (d.getVar('MULTILIB_GLOBAL_VARIANTS') or "").split()
    variants = (d.getVar('MULTILIB_VARIANTS') or "").split()

    if bb.data.inherits_class('kernel', d) or bb.data.inherits_class('module-base', d):
        write_extra_pkgs(variants, pn, packages, pkgdatadir)

    if bb.data.inherits_class('allarch', d) and not variants \
        and not bb.data.inherits_class('packagegroup', d):
        write_extra_pkgs(global_variants, pn, packages, pkgdatadir)

    workdir = d.getVar('WORKDIR')

    for pkg in packages.split():
        pkgval = d.getVar('PKG_%s' % pkg)
        if pkgval is None:
            pkgval = pkg
            d.setVar('PKG_%s' % pkg, pkg)

        pkgdestpkg = os.path.join(pkgdest, pkg)
        files = {}
        total_size = 0
        seen = set()
        for f in pkgfiles[pkg]:
            relpth = os.path.relpath(f, pkgdestpkg)
            fstat = os.lstat(f)
            files[os.sep + relpth] = fstat.st_size
            if fstat.st_ino not in seen:
                seen.add(fstat.st_ino)
                total_size += fstat.st_size
        d.setVar('FILES_INFO', json.dumps(files, sort_keys=True))

        process_postinst_on_target(pkg, d.getVar("MLPREFIX"))
        add_set_e_to_scriptlets(pkg)

        subdata_file = pkgdatadir + "/runtime/%s" % pkg
        with open(subdata_file, 'w') as sf:
            for var in (d.getVar('PKGDATA_VARS') or "").split():
                val = write_if_exists(sf, pkg, var)

            write_if_exists(sf, pkg, 'FILERPROVIDESFLIST')
            for dfile in (d.getVar('FILERPROVIDESFLIST_' + pkg) or "").split():
                write_if_exists(sf, pkg, 'FILERPROVIDES_' + dfile)

            write_if_exists(sf, pkg, 'FILERDEPENDSFLIST')
            for dfile in (d.getVar('FILERDEPENDSFLIST_' + pkg) or "").split():
                write_if_exists(sf, pkg, 'FILERDEPENDS_' + dfile)

            sf.write('%s_%s: %d\n' % ('PKGSIZE', pkg, total_size))

        # Symlinks needed for rprovides lookup
        rprov = d.getVar('RPROVIDES_%s' % pkg) or d.getVar('RPROVIDES')
        if rprov:
            for p in rprov.strip().split():
                subdata_sym = pkgdatadir + "/runtime-rprovides/%s/%s" % (p, pkg)
                bb.utils.mkdirhier(os.path.dirname(subdata_sym))
                oe.path.symlink("../../runtime/%s" % pkg, subdata_sym, True)

        allow_empty = d.getVar('ALLOW_EMPTY_%s' % pkg)
        if not allow_empty:
            allow_empty = d.getVar('ALLOW_EMPTY')
        root = "%s/%s" % (pkgdest, pkg)
        os.chdir(root)
        g = glob('*')
        if g or allow_empty == "1":
            # Symlinks needed for reverse lookups (from the final package name)
            subdata_sym = pkgdatadir + "/runtime-reverse/%s" % pkgval
            oe.path.symlink("../runtime/%s" % pkg, subdata_sym, True)

            packagedfile = pkgdatadir + '/runtime/%s.packaged' % pkg
            open(packagedfile, 'w').close()

    if bb.data.inherits_class('kernel', d) or bb.data.inherits_class('module-base', d):
        write_extra_runtime_pkgs(variants, packages, pkgdatadir)

    if bb.data.inherits_class('allarch', d) and not variants \
        and not bb.data.inherits_class('packagegroup', d):
        write_extra_runtime_pkgs(global_variants, packages, pkgdatadir)

}
emit_pkgdata[dirs] = "${PKGDESTWORK}/runtime ${PKGDESTWORK}/runtime-reverse ${PKGDESTWORK}/runtime-rprovides"

ldconfig_postinst_fragment() {
if [ x"$D" = "x" ]; then
	if [ -x /sbin/ldconfig ]; then /sbin/ldconfig ; fi
fi
}

RPMDEPS = "${STAGING_LIBDIR_NATIVE}/rpm/rpmdeps --alldeps"

# Collect perfile run-time dependency metadata
# Output:
#  FILERPROVIDESFLIST_pkg - list of all files w/ deps
#  FILERPROVIDES_filepath_pkg - per file dep
#
#  FILERDEPENDSFLIST_pkg - list of all files w/ deps
#  FILERDEPENDS_filepath_pkg - per file dep

python package_do_filedeps() {
    if d.getVar('SKIP_FILEDEPS') == '1':
        return

    pkgdest = d.getVar('PKGDEST')
    packages = d.getVar('PACKAGES')
    rpmdeps = d.getVar('RPMDEPS')

    def chunks(files, n):
        return [files[i:i+n] for i in range(0, len(files), n)]

    pkglist = []
    for pkg in packages.split():
        if d.getVar('SKIP_FILEDEPS_' + pkg) == '1':
            continue
        if pkg.endswith('-dbg') or pkg.endswith('-doc') or pkg.find('-locale-') != -1 or pkg.find('-localedata-') != -1 or pkg.find('-gconv-') != -1 or pkg.find('-charmap-') != -1 or pkg.startswith('kernel-module-') or pkg.endswith('-src'):
            continue
        for files in chunks(pkgfiles[pkg], 100):
            pkglist.append((pkg, files, rpmdeps, pkgdest))

    processed = oe.utils.multiprocess_launch(oe.package.filedeprunner, pkglist, d)

    provides_files = {}
    requires_files = {}

    for result in processed:
        (pkg, provides, requires) = result

        if pkg not in provides_files:
            provides_files[pkg] = []
        if pkg not in requires_files:
            requires_files[pkg] = []

        for file in sorted(provides):
            provides_files[pkg].append(file)
            key = "FILERPROVIDES_" + file + "_" + pkg
            d.appendVar(key, " " + " ".join(provides[file]))

        for file in sorted(requires):
            requires_files[pkg].append(file)
            key = "FILERDEPENDS_" + file + "_" + pkg
            d.appendVar(key, " " + " ".join(requires[file]))

    for pkg in requires_files:
        d.setVar("FILERDEPENDSFLIST_" + pkg, " ".join(requires_files[pkg]))
    for pkg in provides_files:
        d.setVar("FILERPROVIDESFLIST_" + pkg, " ".join(provides_files[pkg]))
}

SHLIBSDIRS = "${WORKDIR_PKGDATA}/${MLPREFIX}shlibs2"
SHLIBSWORKDIR = "${PKGDESTWORK}/${MLPREFIX}shlibs2"

python package_do_shlibs() {
    import itertools
    import re, pipes
    import subprocess

    exclude_shlibs = d.getVar('EXCLUDE_FROM_SHLIBS', False)
    if exclude_shlibs:
        bb.note("not generating shlibs")
        return

    lib_re = re.compile(r"^.*\.so")
    libdir_re = re.compile(r".*/%s$" % d.getVar('baselib'))

    packages = d.getVar('PACKAGES')

    shlib_pkgs = []
    exclusion_list = d.getVar("EXCLUDE_PACKAGES_FROM_SHLIBS")
    if exclusion_list:
        for pkg in packages.split():
            if pkg not in exclusion_list.split():
                shlib_pkgs.append(pkg)
            else:
                bb.note("not generating shlibs for %s" % pkg)
    else:
        shlib_pkgs = packages.split()

    targetos = d.getVar('TARGET_OS')

    workdir = d.getVar('WORKDIR')

    ver = d.getVar('PKGV')
    if not ver:
        msg = "PKGV not defined"
        package_qa_handle_error("pkgv-undefined", msg, d)
        return

    pkgdest = d.getVar('PKGDEST')

    shlibswork_dir = d.getVar('SHLIBSWORKDIR')

    def linux_so(file, pkg, pkgver, d):
        needs_ldconfig = False
        needed = set()
        sonames = set()
        renames = []
        ldir = os.path.dirname(file).replace(pkgdest + "/" + pkg, '')
        cmd = d.getVar('OBJDUMP') + " -p " + pipes.quote(file) + " 2>/dev/null"
        fd = os.popen(cmd)
        lines = fd.readlines()
        fd.close()
        rpath = tuple()
        for l in lines:
            m = re.match(r"\s+RPATH\s+([^\s]*)", l)
            if m:
                rpaths = m.group(1).replace("$ORIGIN", ldir).split(":")
                rpath = tuple(map(os.path.normpath, rpaths))
        for l in lines:
            m = re.match(r"\s+NEEDED\s+([^\s]*)", l)
            if m:
                dep = m.group(1)
                if dep not in needed:
                    needed.add((dep, file, rpath))
            m = re.match(r"\s+SONAME\s+([^\s]*)", l)
            if m:
                this_soname = m.group(1)
                prov = (this_soname, ldir, pkgver)
                if not prov in sonames:
                    # if library is private (only used by package) then do not build shlib for it
                    import fnmatch
                    if not private_libs or len([i for i in private_libs if fnmatch.fnmatch(this_soname, i)]) == 0:
                        sonames.add(prov)
                if libdir_re.match(os.path.dirname(file)):
                    needs_ldconfig = True
                if snap_symlinks and (os.path.basename(file) != this_soname):
                    renames.append((file, os.path.join(os.path.dirname(file), this_soname)))
        return (needs_ldconfig, needed, sonames, renames)

    def darwin_so(file, needed, sonames, renames, pkgver):
        if not os.path.exists(file):
            return
        ldir = os.path.dirname(file).replace(pkgdest + "/" + pkg, '')

        def get_combinations(base):
            #
            # Given a base library name, find all combinations of this split by "." and "-"
            #
            combos = []
            options = base.split(".")
            for i in range(1, len(options) + 1):
                combos.append(".".join(options[0:i]))
            options = base.split("-")
            for i in range(1, len(options) + 1):
                combos.append("-".join(options[0:i]))
            return combos

        if (file.endswith('.dylib') or file.endswith('.so')) and not pkg.endswith('-dev') and not pkg.endswith('-dbg') and not pkg.endswith('-src'):
            # Drop suffix
            name = os.path.basename(file).rsplit(".",1)[0]
            # Find all combinations
            combos = get_combinations(name)
            for combo in combos:
                if not combo in sonames:
                    prov = (combo, ldir, pkgver)
                    sonames.add(prov)
        if file.endswith('.dylib') or file.endswith('.so'):
            rpath = []
            p = subprocess.Popen([d.expand("${HOST_PREFIX}otool"), '-l', file], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = p.communicate()
            # If returned successfully, process stdout for results
            if p.returncode == 0:
                for l in out.split("\n"):
                    l = l.strip()
                    if l.startswith('path '):
                        rpath.append(l.split()[1])

        p = subprocess.Popen([d.expand("${HOST_PREFIX}otool"), '-L', file], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        # If returned successfully, process stdout for results
        if p.returncode == 0:
            for l in out.split("\n"):
                l = l.strip()
                if not l or l.endswith(":"):
                    continue
                if "is not an object file" in l:
                    continue
                name = os.path.basename(l.split()[0]).rsplit(".", 1)[0]
                if name and name not in needed[pkg]:
                     needed[pkg].add((name, file, tuple()))

    def mingw_dll(file, needed, sonames, renames, pkgver):
        if not os.path.exists(file):
            return

        if file.endswith(".dll"):
            # assume all dlls are shared objects provided by the package
            sonames.add((os.path.basename(file), os.path.dirname(file).replace(pkgdest + "/" + pkg, ''), pkgver))

        if (file.endswith(".dll") or file.endswith(".exe")):
            # use objdump to search for "DLL Name: .*\.dll"
            p = subprocess.Popen([d.expand("${HOST_PREFIX}objdump"), "-p", file], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = p.communicate()
            # process the output, grabbing all .dll names
            if p.returncode == 0:
                for m in re.finditer(r"DLL Name: (.*?\.dll)$", out.decode(), re.MULTILINE | re.IGNORECASE):
                    dllname = m.group(1)
                    if dllname:
                        needed[pkg].add((dllname, file, tuple()))

    if d.getVar('PACKAGE_SNAP_LIB_SYMLINKS') == "1":
        snap_symlinks = True
    else:
        snap_symlinks = False

    needed = {}

    shlib_provider = oe.package.read_shlib_providers(d)

    for pkg in shlib_pkgs:
        private_libs = d.getVar('PRIVATE_LIBS_' + pkg) or d.getVar('PRIVATE_LIBS') or ""
        private_libs = private_libs.split()
        needs_ldconfig = False
        bb.debug(2, "calculating shlib provides for %s" % pkg)

        pkgver = d.getVar('PKGV_' + pkg)
        if not pkgver:
            pkgver = d.getVar('PV_' + pkg)
        if not pkgver:
            pkgver = ver

        needed[pkg] = set()
        sonames = set()
        renames = []
        linuxlist = []
        for file in pkgfiles[pkg]:
                soname = None
                if cpath.islink(file):
                    continue
                if targetos == "darwin" or targetos == "darwin8":
                    darwin_so(file, needed, sonames, renames, pkgver)
                elif targetos.startswith("mingw"):
                    mingw_dll(file, needed, sonames, renames, pkgver)
                elif os.access(file, os.X_OK) or lib_re.match(file):
                    linuxlist.append(file)

        if linuxlist:
            results = oe.utils.multiprocess_launch(linux_so, linuxlist, d, extraargs=(pkg, pkgver, d))
            for r in results:
                ldconfig = r[0]
                needed[pkg] |= r[1]
                sonames |= r[2]
                renames.extend(r[3])
                needs_ldconfig = needs_ldconfig or ldconfig

        for (old, new) in renames:
            bb.note("Renaming %s to %s" % (old, new))
            os.rename(old, new)
            pkgfiles[pkg].remove(old)

        shlibs_file = os.path.join(shlibswork_dir, pkg + ".list")
        if len(sonames):
            with open(shlibs_file, 'w') as fd:
                for s in sonames:
                    if s[0] in shlib_provider and s[1] in shlib_provider[s[0]]:
                        (old_pkg, old_pkgver) = shlib_provider[s[0]][s[1]]
                        if old_pkg != pkg:
                            bb.warn('%s-%s was registered as shlib provider for %s, changing it to %s-%s because it was built later' % (old_pkg, old_pkgver, s[0], pkg, pkgver))
                    bb.debug(1, 'registering %s-%s as shlib provider for %s' % (pkg, pkgver, s[0]))
                    fd.write(s[0] + ':' + s[1] + ':' + s[2] + '\n')
                    if s[0] not in shlib_provider:
                        shlib_provider[s[0]] = {}
                    shlib_provider[s[0]][s[1]] = (pkg, pkgver)
        if needs_ldconfig:
            bb.debug(1, 'adding ldconfig call to postinst for %s' % pkg)
            postinst = d.getVar('pkg_postinst_%s' % pkg)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += d.getVar('ldconfig_postinst_fragment')
            d.setVar('pkg_postinst_%s' % pkg, postinst)
        bb.debug(1, 'LIBNAMES: pkg %s sonames %s' % (pkg, sonames))

    assumed_libs = d.getVar('ASSUME_SHLIBS')
    if assumed_libs:
        libdir = d.getVar("libdir")
        for e in assumed_libs.split():
            l, dep_pkg = e.split(":")
            lib_ver = None
            dep_pkg = dep_pkg.rsplit("_", 1)
            if len(dep_pkg) == 2:
                lib_ver = dep_pkg[1]
            dep_pkg = dep_pkg[0]
            if l not in shlib_provider:
                shlib_provider[l] = {}
            shlib_provider[l][libdir] = (dep_pkg, lib_ver)

    libsearchpath = [d.getVar('libdir'), d.getVar('base_libdir')]

    for pkg in shlib_pkgs:
        bb.debug(2, "calculating shlib requirements for %s" % pkg)

        private_libs = d.getVar('PRIVATE_LIBS_' + pkg) or d.getVar('PRIVATE_LIBS') or ""
        private_libs = private_libs.split()

        deps = list()
        for n in needed[pkg]:
            # if n is in private libraries, don't try to search provider for it
            # this could cause problem in case some abc.bb provides private
            # /opt/abc/lib/libfoo.so.1 and contains /usr/bin/abc depending on system library libfoo.so.1
            # but skipping it is still better alternative than providing own
            # version and then adding runtime dependency for the same system library
            import fnmatch
            if private_libs and len([i for i in private_libs if fnmatch.fnmatch(n[0], i)]) > 0:
                bb.debug(2, '%s: Dependency %s covered by PRIVATE_LIBS' % (pkg, n[0]))
                continue
            if n[0] in shlib_provider.keys():
                shlib_provider_map = shlib_provider[n[0]]
                matches = set()
                for p in itertools.chain(list(n[2]), sorted(shlib_provider_map.keys()), libsearchpath):
                    if p in shlib_provider_map:
                        matches.add(p)
                if len(matches) > 1:
                    matchpkgs = ', '.join([shlib_provider_map[match][0] for match in matches])
                    bb.error("%s: Multiple shlib providers for %s: %s (used by files: %s)" % (pkg, n[0], matchpkgs, n[1]))
                elif len(matches) == 1:
                    (dep_pkg, ver_needed) = shlib_provider_map[matches.pop()]

                    bb.debug(2, '%s: Dependency %s requires package %s (used by files: %s)' % (pkg, n[0], dep_pkg, n[1]))

                    if dep_pkg == pkg:
                        continue

                    if ver_needed:
                        dep = "%s (>= %s)" % (dep_pkg, ver_needed)
                    else:
                        dep = dep_pkg
                    if not dep in deps:
                        deps.append(dep)
                    continue
            bb.note("Couldn't find shared library provider for %s, used by files: %s" % (n[0], n[1]))

        deps_file = os.path.join(pkgdest, pkg + ".shlibdeps")
        if os.path.exists(deps_file):
            os.remove(deps_file)
        if deps:
            with open(deps_file, 'w') as fd:
                for dep in sorted(deps):
                    fd.write(dep + '\n')
}

python package_do_pkgconfig () {
    import re

    packages = d.getVar('PACKAGES')
    workdir = d.getVar('WORKDIR')
    pkgdest = d.getVar('PKGDEST')

    shlibs_dirs = d.getVar('SHLIBSDIRS').split()
    shlibswork_dir = d.getVar('SHLIBSWORKDIR')

    pc_re = re.compile(r'(.*)\.pc$')
    var_re = re.compile(r'(.*)=(.*)')
    field_re = re.compile(r'(.*): (.*)')

    pkgconfig_provided = {}
    pkgconfig_needed = {}
    for pkg in packages.split():
        pkgconfig_provided[pkg] = []
        pkgconfig_needed[pkg] = []
        for file in pkgfiles[pkg]:
                m = pc_re.match(file)
                if m:
                    pd = bb.data.init()
                    name = m.group(1)
                    pkgconfig_provided[pkg].append(name)
                    if not os.access(file, os.R_OK):
                        continue
                    with open(file, 'r') as f:
                        lines = f.readlines()
                    for l in lines:
                        m = var_re.match(l)
                        if m:
                            name = m.group(1)
                            val = m.group(2)
                            pd.setVar(name, pd.expand(val))
                            continue
                        m = field_re.match(l)
                        if m:
                            hdr = m.group(1)
                            exp = pd.expand(m.group(2))
                            if hdr == 'Requires':
                                pkgconfig_needed[pkg] += exp.replace(',', ' ').split()

    for pkg in packages.split():
        pkgs_file = os.path.join(shlibswork_dir, pkg + ".pclist")
        if pkgconfig_provided[pkg] != []:
            with open(pkgs_file, 'w') as f:
                for p in pkgconfig_provided[pkg]:
                    f.write('%s\n' % p)

    # Go from least to most specific since the last one found wins
    for dir in reversed(shlibs_dirs):
        if not os.path.exists(dir):
            continue
        for file in sorted(os.listdir(dir)):
            m = re.match(r'^(.*)\.pclist$', file)
            if m:
                pkg = m.group(1)
                with open(os.path.join(dir, file)) as fd:
                    lines = fd.readlines()
                pkgconfig_provided[pkg] = []
                for l in lines:
                    pkgconfig_provided[pkg].append(l.rstrip())

    for pkg in packages.split():
        deps = []
        for n in pkgconfig_needed[pkg]:
            found = False
            for k in pkgconfig_provided.keys():
                if n in pkgconfig_provided[k]:
                    if k != pkg and not (k in deps):
                        deps.append(k)
                    found = True
            if found == False:
                bb.note("couldn't find pkgconfig module '%s' in any package" % n)
        deps_file = os.path.join(pkgdest, pkg + ".pcdeps")
        if len(deps):
            with open(deps_file, 'w') as fd:
                for dep in deps:
                    fd.write(dep + '\n')
}

def read_libdep_files(d):
    pkglibdeps = {}
    packages = d.getVar('PACKAGES').split()
    for pkg in packages:
        pkglibdeps[pkg] = {}
        for extension in ".shlibdeps", ".pcdeps", ".clilibdeps":
            depsfile = d.expand("${PKGDEST}/" + pkg + extension)
            if os.access(depsfile, os.R_OK):
                with open(depsfile) as fd:
                    lines = fd.readlines()
                for l in lines:
                    l.rstrip()
                    deps = bb.utils.explode_dep_versions2(l)
                    for dep in deps:
                        if not dep in pkglibdeps[pkg]:
                            pkglibdeps[pkg][dep] = deps[dep]
    return pkglibdeps

python read_shlibdeps () {
    pkglibdeps = read_libdep_files(d)

    packages = d.getVar('PACKAGES').split()
    for pkg in packages:
        rdepends = bb.utils.explode_dep_versions2(d.getVar('RDEPENDS_' + pkg) or "")
        for dep in sorted(pkglibdeps[pkg]):
            # Add the dep if it's not already there, or if no comparison is set
            if dep not in rdepends:
                rdepends[dep] = []
            for v in pkglibdeps[pkg][dep]:
                if v not in rdepends[dep]:
                    rdepends[dep].append(v)
        d.setVar('RDEPENDS_' + pkg, bb.utils.join_deps(rdepends, commasep=False))
}

python package_depchains() {
    """
    For a given set of prefix and postfix modifiers, make those packages
    RRECOMMENDS on the corresponding packages for its RDEPENDS.

    Example:  If package A depends upon package B, and A's .bb emits an
    A-dev package, this would make A-dev Recommends: B-dev.

    If only one of a given suffix is specified, it will take the RRECOMMENDS
    based on the RDEPENDS of *all* other packages. If more than one of a given
    suffix is specified, its will only use the RDEPENDS of the single parent
    package.
    """

    packages  = d.getVar('PACKAGES')
    postfixes = (d.getVar('DEPCHAIN_POST') or '').split()
    prefixes  = (d.getVar('DEPCHAIN_PRE') or '').split()

    def pkg_adddeprrecs(pkg, base, suffix, getname, depends, d):

        #bb.note('depends for %s is %s' % (base, depends))
        rreclist = bb.utils.explode_dep_versions2(d.getVar('RRECOMMENDS_' + pkg) or "")

        for depend in sorted(depends):
            if depend.find('-native') != -1 or depend.find('-cross') != -1 or depend.startswith('virtual/'):
                #bb.note("Skipping %s" % depend)
                continue
            if depend.endswith('-dev'):
                depend = depend[:-4]
            if depend.endswith('-dbg'):
                depend = depend[:-4]
            pkgname = getname(depend, suffix)
            #bb.note("Adding %s for %s" % (pkgname, depend))
            if pkgname not in rreclist and pkgname != pkg:
                rreclist[pkgname] = []

        #bb.note('setting: RRECOMMENDS_%s=%s' % (pkg, ' '.join(rreclist)))
        d.setVar('RRECOMMENDS_%s' % pkg, bb.utils.join_deps(rreclist, commasep=False))

    def pkg_addrrecs(pkg, base, suffix, getname, rdepends, d):

        #bb.note('rdepends for %s is %s' % (base, rdepends))
        rreclist = bb.utils.explode_dep_versions2(d.getVar('RRECOMMENDS_' + pkg) or "")

        for depend in sorted(rdepends):
            if depend.find('virtual-locale-') != -1:
                #bb.note("Skipping %s" % depend)
                continue
            if depend.endswith('-dev'):
                depend = depend[:-4]
            if depend.endswith('-dbg'):
                depend = depend[:-4]
            pkgname = getname(depend, suffix)
            #bb.note("Adding %s for %s" % (pkgname, depend))
            if pkgname not in rreclist and pkgname != pkg:
                rreclist[pkgname] = []

        #bb.note('setting: RRECOMMENDS_%s=%s' % (pkg, ' '.join(rreclist)))
        d.setVar('RRECOMMENDS_%s' % pkg, bb.utils.join_deps(rreclist, commasep=False))

    def add_dep(list, dep):
        if dep not in list:
            list.append(dep)

    depends = []
    for dep in bb.utils.explode_deps(d.getVar('DEPENDS') or ""):
        add_dep(depends, dep)

    rdepends = []
    for pkg in packages.split():
        for dep in bb.utils.explode_deps(d.getVar('RDEPENDS_' + pkg) or ""):
            add_dep(rdepends, dep)

    #bb.note('rdepends is %s' % rdepends)

    def post_getname(name, suffix):
        return '%s%s' % (name, suffix)
    def pre_getname(name, suffix):
        return '%s%s' % (suffix, name)

    pkgs = {}
    for pkg in packages.split():
        for postfix in postfixes:
            if pkg.endswith(postfix):
                if not postfix in pkgs:
                    pkgs[postfix] = {}
                pkgs[postfix][pkg] = (pkg[:-len(postfix)], post_getname)

        for prefix in prefixes:
            if pkg.startswith(prefix):
                if not prefix in pkgs:
                    pkgs[prefix] = {}
                pkgs[prefix][pkg] = (pkg[:-len(prefix)], pre_getname)

    if "-dbg" in pkgs:
        pkglibdeps = read_libdep_files(d)
        pkglibdeplist = []
        for pkg in pkglibdeps:
            for k in pkglibdeps[pkg]:
                add_dep(pkglibdeplist, k)
        dbgdefaultdeps = ((d.getVar('DEPCHAIN_DBGDEFAULTDEPS') == '1') or (bb.data.inherits_class('packagegroup', d)))

    for suffix in pkgs:
        for pkg in pkgs[suffix]:
            if d.getVarFlag('RRECOMMENDS_' + pkg, 'nodeprrecs'):
                continue
            (base, func) = pkgs[suffix][pkg]
            if suffix == "-dev":
                pkg_adddeprrecs(pkg, base, suffix, func, depends, d)
            elif suffix == "-dbg":
                if not dbgdefaultdeps:
                    pkg_addrrecs(pkg, base, suffix, func, pkglibdeplist, d)
                    continue
            if len(pkgs[suffix]) == 1:
                pkg_addrrecs(pkg, base, suffix, func, rdepends, d)
            else:
                rdeps = []
                for dep in bb.utils.explode_deps(d.getVar('RDEPENDS_' + base) or ""):
                    add_dep(rdeps, dep)
                pkg_addrrecs(pkg, base, suffix, func, rdeps, d)
}

# Since bitbake can't determine which variables are accessed during package
# iteration, we need to list them here:
PACKAGEVARS = "FILES RDEPENDS RRECOMMENDS SUMMARY DESCRIPTION RSUGGESTS RPROVIDES RCONFLICTS PKG ALLOW_EMPTY pkg_postinst pkg_postrm pkg_postinst_ontarget INITSCRIPT_NAME INITSCRIPT_PARAMS DEBIAN_NOAUTONAME ALTERNATIVE PKGE PKGV PKGR USERADD_PARAM GROUPADD_PARAM CONFFILES SYSTEMD_SERVICE LICENSE SECTION pkg_preinst pkg_prerm RREPLACES GROUPMEMS_PARAM SYSTEMD_AUTO_ENABLE SKIP_FILEDEPS PRIVATE_LIBS"

def gen_packagevar(d, pkgvars="PACKAGEVARS"):
    ret = []
    pkgs = (d.getVar("PACKAGES") or "").split()
    vars = (d.getVar(pkgvars) or "").split()
    for v in vars:
        ret.append(v)
    for p in pkgs:
        for v in vars:
            ret.append(v + "_" + p)

        # Ensure that changes to INCOMPATIBLE_LICENSE re-run do_package for
        # affected recipes.
        ret.append('LICENSE_EXCLUSION-%s' % p)
    return " ".join(ret)

PACKAGE_PREPROCESS_FUNCS ?= ""
# Functions for setting up PKGD
PACKAGEBUILDPKGD ?= " \
                package_prepare_pkgdata \
                perform_packagecopy \
                ${PACKAGE_PREPROCESS_FUNCS} \
                split_and_strip_files \
                fixup_perms \
                "
# Functions which split PKGD up into separate packages
PACKAGESPLITFUNCS ?= " \
                package_do_split_locales \
                populate_packages"
# Functions which process metadata based on split packages
PACKAGEFUNCS += " \
                package_fixsymlinks \
                package_name_hook \
                package_do_filedeps \
                package_do_shlibs \
                package_do_pkgconfig \
                read_shlibdeps \
                package_depchains \
                emit_pkgdata"

python do_package () {
    # Change the following version to cause sstate to invalidate the package
    # cache.  This is useful if an item this class depends on changes in a
    # way that the output of this class changes.  rpmdeps is a good example
    # as any change to rpmdeps requires this to be rerun.
    # PACKAGE_BBCLASS_VERSION = "2"

    # Init cachedpath
    global cpath
    cpath = oe.cachedpath.CachedPath()

    ###########################################################################
    # Sanity test the setup
    ###########################################################################

    packages = (d.getVar('PACKAGES') or "").split()
    if len(packages) < 1:
        bb.debug(1, "No packages to build, skipping do_package")
        return

    workdir = d.getVar('WORKDIR')
    outdir = d.getVar('DEPLOY_DIR')
    dest = d.getVar('D')
    dvar = d.getVar('PKGD')
    pn = d.getVar('PN')

    if not workdir or not outdir or not dest or not dvar or not pn:
        msg = "WORKDIR, DEPLOY_DIR, D, PN and PKGD all must be defined, unable to package"
        package_qa_handle_error("var-undefined", msg, d)
        return

    bb.build.exec_func("package_get_auto_pr", d)

    ###########################################################################
    # Optimisations
    ###########################################################################

    # Continually expanding complex expressions is inefficient, particularly
    # when we write to the datastore and invalidate the expansion cache. This
    # code pre-expands some frequently used variables

    def expandVar(x, d):
        d.setVar(x, d.getVar(x))

    for x in 'PN', 'PV', 'BPN', 'TARGET_SYS', 'EXTENDPRAUTO':
        expandVar(x, d)

    ###########################################################################
    # Setup PKGD (from D)
    ###########################################################################

    for f in (d.getVar('PACKAGEBUILDPKGD') or '').split():
        bb.build.exec_func(f, d)

    ###########################################################################
    # Split up PKGD into PKGDEST
    ###########################################################################

    cpath = oe.cachedpath.CachedPath()

    for f in (d.getVar('PACKAGESPLITFUNCS') or '').split():
        bb.build.exec_func(f, d)

    ###########################################################################
    # Process PKGDEST
    ###########################################################################

    # Build global list of files in each split package
    global pkgfiles
    pkgfiles = {}
    packages = d.getVar('PACKAGES').split()
    pkgdest = d.getVar('PKGDEST')
    for pkg in packages:
        pkgfiles[pkg] = []
        for walkroot, dirs, files in cpath.walk(pkgdest + "/" + pkg):
            for file in files:
                pkgfiles[pkg].append(walkroot + os.sep + file)

    for f in (d.getVar('PACKAGEFUNCS') or '').split():
        bb.build.exec_func(f, d)

    qa_sane = d.getVar("QA_SANE")
    if not qa_sane:
        bb.fatal("Fatal QA errors found, failing task.")
}

do_package[dirs] = "${SHLIBSWORKDIR} ${PKGDESTWORK} ${D}"
do_package[vardeps] += "${PACKAGEBUILDPKGD} ${PACKAGESPLITFUNCS} ${PACKAGEFUNCS} ${@gen_packagevar(d)}"
addtask package after do_install

SSTATETASKS += "do_package"
do_package[cleandirs] = "${PKGDEST} ${PKGDESTWORK}"
do_package[sstate-plaindirs] = "${PKGD} ${PKGDEST} ${PKGDESTWORK}"
do_package_setscene[dirs] = "${STAGING_DIR}"

python do_package_setscene () {
    sstate_setscene(d)
}
addtask do_package_setscene

# Copy from PKGDESTWORK to tempdirectory as tempdirectory can be cleaned at both
# do_package_setscene and do_packagedata_setscene leading to races
python do_packagedata () {
    src = d.expand("${PKGDESTWORK}")
    dest = d.expand("${WORKDIR}/pkgdata-pdata-input")
    oe.path.copyhardlinktree(src, dest)
}

addtask packagedata before do_build after do_package

SSTATETASKS += "do_packagedata"
do_packagedata[sstate-inputdirs] = "${WORKDIR}/pkgdata-pdata-input"
do_packagedata[sstate-outputdirs] = "${PKGDATA_DIR}"
do_packagedata[stamp-extra-info] = "${MACHINE_ARCH}"

python do_packagedata_setscene () {
    sstate_setscene(d)
}
addtask do_packagedata_setscene

#
# Helper functions for the package writing classes
#

def mapping_rename_hook(d):
    """
    Rewrite variables to account for package renaming in things
    like debian.bbclass or manual PKG variable name changes
    """
    pkg = d.getVar("PKG")
    runtime_mapping_rename("RDEPENDS", pkg, d)
    runtime_mapping_rename("RRECOMMENDS", pkg, d)
    runtime_mapping_rename("RSUGGESTS", pkg, d)
