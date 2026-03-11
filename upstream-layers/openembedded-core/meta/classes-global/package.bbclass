#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# Packaging process
#
# Executive summary: This class iterates over the functions listed in PACKAGEFUNCS
# Taking D and splitting it up into the packages listed in PACKAGES, placing the
# resulting output in PKGDEST.
#
# There are the following default steps but PACKAGEFUNCS can be extended:
#
# a) package_convert_pr_autoinc - convert AUTOINC in PKGV to ${PRSERV_PV_AUTOINC}
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
    return oe.package.legitimize_package_name(s)

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
        on = oe.package.legitimize_package_name(m.group(1))
        pkg = output_pattern % on
        split_packages.add(pkg)
        if not pkg in packages:
            if prepend:
                packages = [pkg] + packages
            else:
                packages.append(pkg)
        oldfiles = d.getVar('FILES:' + pkg)
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
            d.setVar('FILES:' + pkg, " ".join(the_files))
        else:
            d.setVar('FILES:' + pkg, oldfiles + " " + newfile)
        if extra_depends != '':
            d.appendVar('RDEPENDS:' + pkg, ' ' + extra_depends)
        if not d.getVar('DESCRIPTION:' + pkg):
            d.setVar('DESCRIPTION:' + pkg, description % on)
        if not d.getVar('SUMMARY:' + pkg):
            d.setVar('SUMMARY:' + pkg, summary % on)
        if postinst:
            d.setVar('pkg_postinst:' + pkg, postinst)
        if postrm:
            d.setVar('pkg_postrm:' + pkg, postrm)
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
        if bb.utils.contains('DISTRO_FEATURES', 'minidebuginfo', True, False, d):
            deps += ' xz-native:do_populate_sysroot'
        d.appendVarFlag('do_package', 'depends', deps)

        # shlibs requires any DEPENDS to have already packaged for the *.list files
        d.appendVarFlag('do_package', 'deptask', " do_packagedata")
}


PRSERV_ACTIVE = "${@bool(d.getVar("PRSERV_HOST"))}"
PRSERV_ACTIVE[vardepvalue] = "${PRSERV_ACTIVE}"
package_get_auto_pr[vardepsexclude] = "BB_TASKDEPDATA"
package_get_auto_pr[vardeps] += "PRSERV_ACTIVE"
python package_get_auto_pr() {
    import oe.prservice

    def get_do_package_hash(pn):
        if d.getVar("BB_RUNTASK") != "do_package":
            taskdepdata = d.getVar("BB_TASKDEPDATA", False)
            for dep in taskdepdata:
                if taskdepdata[dep][1] == "do_package" and taskdepdata[dep][0] == pn:
                    return taskdepdata[dep][6]
        return None

    # Support per recipe PRSERV_HOST
    pn = d.getVar('PN')
    host = d.getVar("PRSERV_HOST_" + pn)
    if not (host is None):
        d.setVar("PRSERV_HOST", host)

    pkgv = d.getVar("PKGV")

    # PR Server not active, handle AUTOINC
    if not d.getVar('PRSERV_HOST'):
        d.setVar("PRSERV_PV_AUTOINC", "0")
        return

    auto_pr = None
    pv = d.getVar("PV")
    version = d.getVar("PRAUTOINX")
    pkgarch = d.getVar("PACKAGE_ARCH")
    checksum = get_do_package_hash(pn)

    # If do_package isn't in the dependencies, we can't get the checksum...
    if not checksum:
        bb.warn('Task %s requested do_package unihash, but it was not available.' % d.getVar('BB_RUNTASK'))
        #taskdepdata = d.getVar("BB_TASKDEPDATA", False)
        #for dep in taskdepdata:
        #    bb.warn('%s:%s = %s' % (taskdepdata[dep][0], taskdepdata[dep][1], taskdepdata[dep][6]))
        return

    if d.getVar('PRSERV_LOCKDOWN'):
        auto_pr = d.getVar('PRAUTO_' + version + '_' + pkgarch) or d.getVar('PRAUTO_' + version) or None
        if auto_pr is None:
            bb.fatal("Can NOT get PRAUTO from lockdown exported file")
        d.setVar('PRAUTO',str(auto_pr))
        return

    try:
        conn = oe.prservice.prserv_make_conn(d)
        if conn is not None:
            if "AUTOINC" in pkgv:
                srcpv = bb.fetch2.get_srcrev(d)
                base_ver = "AUTOINC-%s" % version[:version.find(srcpv)]
                value = conn.getPR(base_ver, pkgarch, srcpv)
                d.setVar("PRSERV_PV_AUTOINC", str(value))

            auto_pr = conn.getPR(version, pkgarch, checksum)
            conn.close()
    except Exception as e:
        bb.fatal("Can NOT get PRAUTO, exception %s" %  str(e))
    if auto_pr is None:
        bb.fatal("Can NOT get PRAUTO from remote PR service")
    d.setVar('PRAUTO',str(auto_pr))
}

#
# Package functions suitable for inclusion in PACKAGEFUNCS
#

python package_setup_pkgv() {
    pkgv = d.getVar("PKGV")
    # Expand SRCPV into PKGV if not present
    srcpv = bb.fetch.get_pkgv_string(d)
    if srcpv and "+" in pkgv:
        d.appendVar("PKGV", srcpv)
        pkgv = d.getVar("PKGV")

    # Adjust pkgv as necessary...
    if 'AUTOINC' in pkgv:
        d.setVar("PKGV", pkgv.replace("AUTOINC", "${PRSERV_PV_AUTOINC}"))
}


python package_convert_pr_autoinc() {
    # Change PRSERV_PV_AUTOINC and EXTENDPRAUTO usage to special values
    d.setVar('PRSERV_PV_AUTOINC', '@PRSERV_PV_AUTOINC@')
    d.setVar('EXTENDPRAUTO', '@EXTENDPRAUTO@')
}

LOCALEBASEPN ??= "${PN}"
LOCALE_PATHS ?= "${datadir}/locale"

python package_do_split_locales() {
    oe.package.split_locales(d)
}

python perform_packagecopy () {
    import subprocess
    import shutil

    dest = d.getVar('D')
    dvar = d.getVar('PKGD')

    # Start by package population by taking a copy of the installed
    # files to operate on
    # Preserve sparse files and hard links
    cmd = 'tar --exclude=./sysroot-only -cf - -C %s -p -S . | tar -xf - -C %s' % (dest, dvar)
    subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

    # replace RPATHs for the nativesdk binaries, to make them relocatable
    if bb.data.inherits_class('nativesdk', d) or bb.data.inherits_class('cross-canadian', d):
        rpath_replace (dvar, d)
}
perform_packagecopy[cleandirs] = "${PKGD}"
perform_packagecopy[dirs] = "${PKGD}"

python populate_packages () {
    oe.package.populate_packages(d)
}
populate_packages[dirs] = "${D}"

python package_fixsymlinks () {
    oe.package.process_fixsymlinks(pkgfiles, d)
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

PKGDATA_VARS = "PN PE PV PR PKGE PKGV PKGR LICENSE DESCRIPTION SUMMARY RDEPENDS RPROVIDES RRECOMMENDS RSUGGESTS RREPLACES RCONFLICTS SECTION PKG ALLOW_EMPTY FILES CONFFILES FILES_INFO PACKAGE_ADD_METADATA pkg_postinst pkg_postrm pkg_preinst pkg_prerm"

python emit_pkgdata() {
    import oe.packagedata
    oe.packagedata.emit_pkgdata(pkgfiles, d)
}
emit_pkgdata[dirs] = "${PKGDESTWORK}/runtime ${PKGDESTWORK}/runtime-reverse ${PKGDESTWORK}/runtime-rprovides ${PKGDESTWORK}/extended"

ldconfig_postinst_fragment() {
if [ x"$D" = "x" ]; then
	if [ -x /sbin/ldconfig ]; then /sbin/ldconfig ; fi
fi
}

RPMDEPS = "${STAGING_LIBDIR_NATIVE}/rpm/rpmdeps --alldeps --define '__font_provides %{nil}'"

python package_do_filedeps() {
    oe.package.process_filedeps(pkgfiles, d)
}

SHLIBSDIRS = "${WORKDIR_PKGDATA}/${MLPREFIX}shlibs2"
SHLIBSWORKDIR = "${PKGDESTWORK}/${MLPREFIX}shlibs2"

python package_do_shlibs() {
    oe.package.process_shlibs(pkgfiles, d)
}

python package_do_pkgconfig () {
    oe.package.process_pkgconfig(pkgfiles, d)
}

python read_shlibdeps () {
    pkglibdeps = oe.package.read_libdep_files(d)

    packages = d.getVar('PACKAGES').split()
    for pkg in packages:
        rdepends = bb.utils.explode_dep_versions2(d.getVar('RDEPENDS:' + pkg) or "")
        for dep in sorted(pkglibdeps[pkg]):
            # Add the dep if it's not already there, or if no comparison is set
            if dep not in rdepends:
                rdepends[dep] = []
            for v in pkglibdeps[pkg][dep]:
                if v not in rdepends[dep]:
                    rdepends[dep].append(v)
        d.setVar('RDEPENDS:' + pkg, bb.utils.join_deps(rdepends, commasep=False))
}

python package_depchains() {
    oe.package.process_depchains(pkgfiles, d)
}

# Since bitbake can't determine which variables are accessed during package
# iteration, we need to list them here:
PACKAGEVARS = "FILES RDEPENDS RRECOMMENDS SUMMARY DESCRIPTION RSUGGESTS RPROVIDES RCONFLICTS PKG ALLOW_EMPTY pkg_postinst pkg_postrm pkg_postinst_ontarget INITSCRIPT_NAME INITSCRIPT_PARAMS DEBIAN_NOAUTONAME ALTERNATIVE PKGE PKGV PKGR USERADD_PARAM GROUPADD_PARAM CONFFILES SYSTEMD_SERVICE LICENSE SECTION pkg_preinst pkg_prerm RREPLACES GROUPMEMS_PARAM SYSTEMD_AUTO_ENABLE SKIP_FILEDEPS PRIVATE_LIBS PACKAGE_ADD_METADATA"

def gen_packagevar(d, pkgvars="PACKAGEVARS"):
    ret = []
    pkgs = (d.getVar("PACKAGES") or "").split()
    vars = (d.getVar(pkgvars) or "").split()
    for v in vars:
        ret.append(v)
    for p in pkgs:
        for v in vars:
            ret.append(v + ":" + p)
    return " ".join(ret)


# Functions for setting up PKGD
PACKAGE_PREPROCESS_FUNCS ?= ""
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
    # PACKAGE_BBCLASS_VERSION = "6"

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
        oe.qa.handle_error("var-undefined", msg, d)
        return

    bb.build.exec_func("package_setup_pkgv", d)
    bb.build.exec_func("package_convert_pr_autoinc", d)

    # Check for conflict between renamed packages and existing ones
    # for each package in PACKAGES, check if it will be renamed to an existing one
    for p in packages:
        rename = d.getVar('PKG:%s' % p)
        if rename and rename in packages:
            bb.fatal('package "%s" is renamed to "%s" using PKG:%s, but package name already exists' % (p, rename, p))

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

    bb.build.exec_func("package_prepare_pkgdata", d)
    bb.build.exec_func("perform_packagecopy", d)
    for f in (d.getVar('PACKAGE_PREPROCESS_FUNCS') or '').split():
        bb.build.exec_func(f, d)
    oe.package.process_split_and_strip_files(d)
    oe.package.fixup_perms(d)

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

    oe.qa.exit_if_errors(d)
}

do_package[dirs] = "${SHLIBSWORKDIR} ${D}"
do_package[vardeps] += "${PACKAGE_PREPROCESS_FUNCS} ${PACKAGESPLITFUNCS} ${PACKAGEFUNCS} ${@gen_packagevar(d)}"
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
    bb.build.exec_func("package_setup_pkgv", d)
    bb.build.exec_func("package_get_auto_pr", d)

    src = d.expand("${PKGDESTWORK}")
    dest = d.expand("${WORKDIR}/pkgdata-pdata-input")
    oe.path.copyhardlinktree(src, dest)

    bb.build.exec_func("packagedata_translate_pr_autoinc", d)
}
do_packagedata[cleandirs] += "${WORKDIR}/pkgdata-pdata-input"

# Translate the EXTENDPRAUTO and AUTOINC to the final values
packagedata_translate_pr_autoinc() {
    find ${WORKDIR}/pkgdata-pdata-input -type f | xargs --no-run-if-empty \
        sed -e 's,@PRSERV_PV_AUTOINC@,${PRSERV_PV_AUTOINC},g' \
            -e 's,@EXTENDPRAUTO@,${EXTENDPRAUTO},g' -i
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

