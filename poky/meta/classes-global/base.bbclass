#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

BB_DEFAULT_TASK ?= "build"
CLASSOVERRIDE ?= "class-target"

inherit patch
inherit staging

inherit mirrors
inherit utils
inherit utility-tasks
inherit logging

PACKAGECONFIG_CONFARGS ??= ""

inherit metadata_scm

def lsb_distro_identifier(d):
    adjust = d.getVar('LSB_DISTRO_ADJUST')
    adjust_func = None
    if adjust:
        try:
            adjust_func = globals()[adjust]
        except KeyError:
            pass
    return oe.lsb.distro_identifier(adjust_func)

die() {
	bbfatal_log "$*"
}

oe_runmake_call() {
	bbnote ${MAKE} ${EXTRA_OEMAKE} "$@"
	${MAKE} ${EXTRA_OEMAKE} "$@"
}

oe_runmake() {
	oe_runmake_call "$@" || die "oe_runmake failed"
}


def get_base_dep(d):
    if d.getVar('INHIBIT_DEFAULT_DEPS', False):
        return ""
    return "${BASE_DEFAULT_DEPS}"

BASE_DEFAULT_DEPS = "virtual/${HOST_PREFIX}gcc virtual/${HOST_PREFIX}compilerlibs virtual/libc"

BASEDEPENDS = ""
BASEDEPENDS:class-target = "${@get_base_dep(d)}"
BASEDEPENDS:class-nativesdk = "${@get_base_dep(d)}"

DEPENDS:prepend="${BASEDEPENDS} "

FILESPATH = "${@base_set_filespath(["${FILE_DIRNAME}/${BP}", "${FILE_DIRNAME}/${BPN}", "${FILE_DIRNAME}/files"], d)}"
# THISDIR only works properly with imediate expansion as it has to run
# in the context of the location its used (:=)
THISDIR = "${@os.path.dirname(d.getVar('FILE'))}"

def extra_path_elements(d):
    path = ""
    elements = (d.getVar('EXTRANATIVEPATH') or "").split()
    for e in elements:
        path = path + "${STAGING_BINDIR_NATIVE}/" + e + ":"
    return path

PATH:prepend = "${@extra_path_elements(d)}"

def get_lic_checksum_file_list(d):
    filelist = []
    lic_files = d.getVar("LIC_FILES_CHKSUM") or ''
    tmpdir = d.getVar("TMPDIR")
    s = d.getVar("S")
    b = d.getVar("B")
    workdir = d.getVar("WORKDIR")

    urls = lic_files.split()
    for url in urls:
        # We only care about items that are absolute paths since
        # any others should be covered by SRC_URI.
        try:
            (method, host, path, user, pswd, parm) = bb.fetch.decodeurl(url)
            if method != "file" or not path:
                raise bb.fetch.MalformedUrl(url)

            if path[0] == '/':
                if path.startswith((tmpdir, s, b, workdir)):
                    continue
                filelist.append(path + ":" + str(os.path.exists(path)))
        except bb.fetch.MalformedUrl:
            bb.fatal(d.getVar('PN') + ": LIC_FILES_CHKSUM contains an invalid URL: " + url)
    return " ".join(filelist)

def setup_hosttools_dir(dest, toolsvar, d, fatal=True):
    tools = d.getVar(toolsvar).split()
    origbbenv = d.getVar("BB_ORIGENV", False)
    path = origbbenv.getVar("PATH")
    # Need to ignore our own scripts directories to avoid circular links
    for p in path.split(":"):
        if p.endswith("/scripts"):
            path = path.replace(p, "/ignoreme")
    bb.utils.mkdirhier(dest)
    notfound = []
    for tool in tools:
        desttool = os.path.join(dest, tool)
        if not os.path.exists(desttool):
            # clean up dead symlink
            if os.path.islink(desttool):
                os.unlink(desttool)
            srctool = bb.utils.which(path, tool, executable=True)
            # gcc/g++ may link to ccache on some hosts, e.g.,
            # /usr/local/bin/ccache/gcc -> /usr/bin/ccache, then which(gcc)
            # would return /usr/local/bin/ccache/gcc, but what we need is
            # /usr/bin/gcc, this code can check and fix that.
            if os.path.islink(srctool) and os.path.basename(os.readlink(srctool)) == 'ccache':
                srctool = bb.utils.which(path, tool, executable=True, direction=1)
            if srctool:
                os.symlink(srctool, desttool)
            else:
                notfound.append(tool)

    if notfound and fatal:
        bb.fatal("The following required tools (as specified by HOSTTOOLS) appear to be unavailable in PATH, please install them in order to proceed:\n  %s" % " ".join(notfound))

# We can't use vardepvalue against do_fetch directly since that would overwrite
# the other task dependencies so we use an indirect function.
python fetcher_hashes_dummyfunc() {
    return
}
fetcher_hashes_dummyfunc[vardepvalue] = "${@bb.fetch.get_hashvalue(d)}"

addtask fetch
do_fetch[dirs] = "${DL_DIR}"
do_fetch[file-checksums] = "${@bb.fetch.get_checksum_file_list(d)}"
do_fetch[file-checksums] += " ${@get_lic_checksum_file_list(d)}"
do_fetch[prefuncs] += "fetcher_hashes_dummyfunc"
do_fetch[network] = "1"
python base_do_fetch() {

    src_uri = (d.getVar('SRC_URI') or "").split()
    if not src_uri:
        return

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.download()
    except bb.fetch2.BBFetchException as e:
        bb.fatal("Bitbake Fetcher Error: " + repr(e))
}

addtask unpack after do_fetch
do_unpack[cleandirs] = "${UNPACKDIR}"

python base_do_unpack() {
    import shutil

    sourcedir = d.getVar('S')
    # Intentionally keep SOURCE_BASEDIR internal to the task just for SDE
    d.setVar("SOURCE_BASEDIR", sourcedir)

    src_uri = (d.getVar('SRC_URI') or "").split()
    if not src_uri:
        return

    basedir = None
    unpackdir = d.getVar('UNPACKDIR')
    workdir = d.getVar('WORKDIR')
    if sourcedir.startswith(workdir) and not sourcedir.startswith(unpackdir):
        basedir = sourcedir.replace(workdir, '').strip("/").split('/')[0]
        if basedir:
            bb.utils.remove(workdir + '/' + basedir, True)
            d.setVar("SOURCE_BASEDIR", workdir + '/' + basedir)

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.unpack(d.getVar('UNPACKDIR'))
    except bb.fetch2.BBFetchException as e:
        bb.fatal("Bitbake Fetcher Error: " + repr(e))

    if basedir and os.path.exists(unpackdir + '/' + basedir):
        # Compatibility magic to ensure ${WORKDIR}/git and ${WORKDIR}/${BP}
        # as often used in S work as expected.
        shutil.move(unpackdir + '/' + basedir, workdir + '/' + basedir)
}

SSTATETASKS += "do_deploy_source_date_epoch"

do_deploy_source_date_epoch () {
    mkdir -p ${SDE_DEPLOYDIR}
    if [ -e ${SDE_FILE} ]; then
        echo "Deploying SDE from ${SDE_FILE} -> ${SDE_DEPLOYDIR}."
        cp -p ${SDE_FILE} ${SDE_DEPLOYDIR}/__source_date_epoch.txt
    else
        echo "${SDE_FILE} not found!"
    fi
}

python do_deploy_source_date_epoch_setscene () {
    sstate_setscene(d)
    bb.utils.mkdirhier(d.getVar('SDE_DIR'))
    sde_file = os.path.join(d.getVar('SDE_DEPLOYDIR'), '__source_date_epoch.txt')
    if os.path.exists(sde_file):
        target = d.getVar('SDE_FILE')
        bb.debug(1, "Moving setscene SDE file %s -> %s" % (sde_file, target))
        bb.utils.rename(sde_file, target)
    else:
        bb.debug(1, "%s not found!" % sde_file)
}

do_deploy_source_date_epoch[dirs] = "${SDE_DEPLOYDIR}"
do_deploy_source_date_epoch[sstate-plaindirs] = "${SDE_DEPLOYDIR}"
addtask do_deploy_source_date_epoch_setscene
addtask do_deploy_source_date_epoch before do_configure after do_patch

python create_source_date_epoch_stamp() {
    # Version: 2
    source_date_epoch = oe.reproducible.get_source_date_epoch(d, d.getVar('SOURCE_BASEDIR') or d.getVar('S'))
    oe.reproducible.epochfile_write(source_date_epoch, d.getVar('SDE_FILE'), d)
}
do_unpack[postfuncs] += "create_source_date_epoch_stamp"

def get_source_date_epoch_value(d):
    return oe.reproducible.epochfile_read(d.getVar('SDE_FILE'), d)

def get_layers_branch_rev(d):
    revisions = oe.buildcfg.get_layer_revisions(d)
    layers_branch_rev = ["%-20s = \"%s:%s\"" % (r[1], r[2], r[3]) for r in revisions]
    i = len(layers_branch_rev)-1
    p1 = layers_branch_rev[i].find("=")
    s1 = layers_branch_rev[i][p1:]
    while i > 0:
        p2 = layers_branch_rev[i-1].find("=")
        s2= layers_branch_rev[i-1][p2:]
        if s1 == s2:
            layers_branch_rev[i-1] = layers_branch_rev[i-1][0:p2]
            i -= 1
        else:
            i -= 1
            p1 = layers_branch_rev[i].find("=")
            s1= layers_branch_rev[i][p1:]
    return layers_branch_rev


BUILDCFG_FUNCS ??= "buildcfg_vars get_layers_branch_rev buildcfg_neededvars"
BUILDCFG_FUNCS[type] = "list"

def buildcfg_vars(d):
    statusvars = oe.data.typed_value('BUILDCFG_VARS', d)
    for var in statusvars:
        value = d.getVar(var)
        if value is not None:
            yield '%-20s = "%s"' % (var, value)

def buildcfg_neededvars(d):
    needed_vars = oe.data.typed_value("BUILDCFG_NEEDEDVARS", d)
    pesteruser = []
    for v in needed_vars:
        val = d.getVar(v)
        if not val or val == 'INVALID':
            pesteruser.append(v)

    if pesteruser:
        bb.fatal('The following variable(s) were not set: %s\nPlease set them directly, or choose a MACHINE or DISTRO that sets them.' % ', '.join(pesteruser))

addhandler base_eventhandler
base_eventhandler[eventmask] = "bb.event.ConfigParsed bb.event.MultiConfigParsed bb.event.BuildStarted bb.event.RecipePreFinalise bb.event.RecipeParsed"
python base_eventhandler() {
    import bb.runqueue

    if isinstance(e, bb.event.ConfigParsed):
        if not d.getVar("NATIVELSBSTRING", False):
            d.setVar("NATIVELSBSTRING", lsb_distro_identifier(d))
        d.setVar("ORIGNATIVELSBSTRING", d.getVar("NATIVELSBSTRING", False))
        d.setVar('BB_VERSION', bb.__version__)

    # There might be no bb.event.ConfigParsed event if bitbake server is
    # running, so check bb.event.BuildStarted too to make sure ${HOSTTOOLS_DIR}
    # exists.
    if isinstance(e, bb.event.ConfigParsed) or \
            (isinstance(e, bb.event.BuildStarted) and not os.path.exists(d.getVar('HOSTTOOLS_DIR'))):
        # Works with the line in layer.conf which changes PATH to point here
        setup_hosttools_dir(d.getVar('HOSTTOOLS_DIR'), 'HOSTTOOLS', d)
        setup_hosttools_dir(d.getVar('HOSTTOOLS_DIR'), 'HOSTTOOLS_NONFATAL', d, fatal=False)

    if isinstance(e, bb.event.MultiConfigParsed):
        # We need to expand SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS in each of the multiconfig data stores
        # own contexts so the variables get expanded correctly for that arch, then inject back into
        # the main data store.
        deps = []
        for config in e.mcdata:
            deps.append(e.mcdata[config].getVar("SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS"))
        deps = " ".join(deps)
        e.mcdata[''].setVar("SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS", deps)

    if isinstance(e, bb.event.BuildStarted):
        localdata = bb.data.createCopy(d)
        statuslines = []
        for func in oe.data.typed_value('BUILDCFG_FUNCS', localdata):
            g = globals()
            if func not in g:
                bb.warn("Build configuration function '%s' does not exist" % func)
            else:
                flines = g[func](localdata)
                if flines:
                    statuslines.extend(flines)

        statusheader = d.getVar('BUILDCFG_HEADER')
        if statusheader:
            bb.plain('\n%s\n%s\n' % (statusheader, '\n'.join(statuslines)))

    # This code is to silence warnings where the SDK variables overwrite the 
    # target ones and we'd see duplicate key names overwriting each other
    # for various PREFERRED_PROVIDERS
    if isinstance(e, bb.event.RecipePreFinalise):
        if d.getVar("TARGET_PREFIX") == d.getVar("SDK_PREFIX"):
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils")
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc")
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}g++")
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}compilerlibs")

    if isinstance(e, bb.event.RecipeParsed):
        #
        # If we have multiple providers of virtual/X and a PREFERRED_PROVIDER_virtual/X is set
        # skip parsing for all the other providers which will mean they get uninstalled from the
        # sysroot since they're now "unreachable". This makes switching virtual/kernel work in 
        # particular.
        #
        pn = d.getVar('PN')
        source_mirror_fetch = d.getVar('SOURCE_MIRROR_FETCH', False)
        if not source_mirror_fetch:
            provs = (d.getVar("PROVIDES") or "").split()
            multiprovidersallowed = (d.getVar("BB_MULTI_PROVIDER_ALLOWED") or "").split()
            for p in provs:
                if p.startswith("virtual/") and p not in multiprovidersallowed:
                    profprov = d.getVar("PREFERRED_PROVIDER_" + p)
                    if profprov and pn != profprov:
                        raise bb.parse.SkipRecipe("PREFERRED_PROVIDER_%s set to %s, not %s" % (p, profprov, pn))
}

CONFIGURESTAMPFILE = "${WORKDIR}/configure.sstate"
CLEANBROKEN = "0"

addtask configure after do_patch
do_configure[dirs] = "${B}"
base_do_configure() {
	if [ -n "${CONFIGURESTAMPFILE}" -a -e "${CONFIGURESTAMPFILE}" ]; then
		if [ "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" ]; then
			cd ${B}
			if [ "${CLEANBROKEN}" != "1" -a \( -e Makefile -o -e makefile -o -e GNUmakefile \) ]; then
				oe_runmake clean
			fi
			# -ignore_readdir_race does not work correctly with -delete;
			# use xargs to avoid spurious build failures
			find ${B} -ignore_readdir_race -name \*.la -type f -print0 | xargs -0 rm -f
		fi
	fi
	if [ -n "${CONFIGURESTAMPFILE}" ]; then
		mkdir -p `dirname ${CONFIGURESTAMPFILE}`
		echo ${BB_TASKHASH} > ${CONFIGURESTAMPFILE}
	fi
}

addtask compile after do_configure
do_compile[dirs] = "${B}"
base_do_compile() {
	if [ -e Makefile -o -e makefile -o -e GNUmakefile ]; then
		oe_runmake || die "make failed"
	else
		bbnote "nothing to compile"
	fi
}

addtask install after do_compile
do_install[dirs] = "${B}"
# Remove and re-create ${D} so that it is guaranteed to be empty
do_install[cleandirs] = "${D}"

base_do_install() {
	:
}

addtask build after do_populate_sysroot
do_build[noexec] = "1"
do_build[recrdeptask] += "do_deploy"
do_build () {
	:
}

def set_packagetriplet(d):
    archs = []
    tos = []
    tvs = []

    archs.append(d.getVar("PACKAGE_ARCHS").split())
    tos.append(d.getVar("TARGET_OS"))
    tvs.append(d.getVar("TARGET_VENDOR"))

    def settriplet(d, varname, archs, tos, tvs):
        triplets = []
        for i in range(len(archs)):
            for arch in archs[i]:
                triplets.append(arch + tvs[i] + "-" + tos[i])
        triplets.reverse()
        d.setVar(varname, " ".join(triplets))

    settriplet(d, "PKGTRIPLETS", archs, tos, tvs)

    variants = d.getVar("MULTILIB_VARIANTS") or ""
    for item in variants.split():
        localdata = bb.data.createCopy(d)
        overrides = localdata.getVar("OVERRIDES", False) + ":virtclass-multilib-" + item
        localdata.setVar("OVERRIDES", overrides)

        archs.append(localdata.getVar("PACKAGE_ARCHS").split())
        tos.append(localdata.getVar("TARGET_OS"))
        tvs.append(localdata.getVar("TARGET_VENDOR"))

    settriplet(d, "PKGMLTRIPLETS", archs, tos, tvs)

python () {
    import string, re

    # Handle backfilling
    oe.utils.features_backfill("DISTRO_FEATURES", d)
    oe.utils.features_backfill("MACHINE_FEATURES", d)

    # To add a recipe to the skip list , set:
    #   SKIP_RECIPE[pn] = "message"
    pn = d.getVar('PN')
    skip_msg = d.getVarFlag('SKIP_RECIPE', pn)
    if skip_msg:
        bb.debug(1, "Skipping %s %s" % (pn, skip_msg))
        raise bb.parse.SkipRecipe("Recipe will be skipped because: %s" % (skip_msg))

    # Handle PACKAGECONFIG
    #
    # These take the form:
    #
    # PACKAGECONFIG ??= "<default options>"
    # PACKAGECONFIG[foo] = "--enable-foo,--disable-foo,foo_depends,foo_runtime_depends,foo_runtime_recommends,foo_conflict_packageconfig"
    pkgconfigflags = d.getVarFlags("PACKAGECONFIG") or {}
    if pkgconfigflags:
        pkgconfig = (d.getVar('PACKAGECONFIG') or "").split()
        pn = d.getVar("PN")

        mlprefix = d.getVar("MLPREFIX")

        def expandFilter(appends, extension, prefix):
            appends = bb.utils.explode_deps(d.expand(" ".join(appends)))
            newappends = []
            for a in appends:
                if a.endswith("-native") or ("-cross-" in a):
                    newappends.append(a)
                elif a.startswith("virtual/"):
                    subs = a.split("/", 1)[1]
                    if subs.startswith(prefix):
                        newappends.append(a + extension)
                    else:
                        newappends.append("virtual/" + prefix + subs + extension)
                else:
                    if a.startswith(prefix):
                        newappends.append(a + extension)
                    else:
                        newappends.append(prefix + a + extension)
            return newappends

        def appendVar(varname, appends):
            if not appends:
                return
            if varname.find("DEPENDS") != -1:
                if bb.data.inherits_class('nativesdk', d) or bb.data.inherits_class('cross-canadian', d) :
                    appends = expandFilter(appends, "", "nativesdk-")
                elif bb.data.inherits_class('native', d):
                    appends = expandFilter(appends, "-native", "")
                elif mlprefix:
                    appends = expandFilter(appends, "", mlprefix)
            varname = d.expand(varname)
            d.appendVar(varname, " " + " ".join(appends))

        extradeps = []
        extrardeps = []
        extrarrecs = []
        extraconf = []
        for flag, flagval in sorted(pkgconfigflags.items()):
            items = flagval.split(",")
            num = len(items)
            if num > 6:
                bb.error("%s: PACKAGECONFIG[%s] Only enable,disable,depend,rdepend,rrecommend,conflict_packageconfig can be specified!"
                    % (d.getVar('PN'), flag))

            if flag in pkgconfig:
                if num >= 3 and items[2]:
                    extradeps.append(items[2])
                if num >= 4 and items[3]:
                    extrardeps.append(items[3])
                if num >= 5 and items[4]:
                    extrarrecs.append(items[4])
                if num >= 1 and items[0]:
                    extraconf.append(items[0])
            elif num >= 2 and items[1]:
                    extraconf.append(items[1])

            if num >= 6 and items[5]:
                conflicts = set(items[5].split())
                invalid = conflicts.difference(set(pkgconfigflags.keys()))
                if invalid:
                    bb.error("%s: PACKAGECONFIG[%s] Invalid conflict package config%s '%s' specified."
                        % (d.getVar('PN'), flag, 's' if len(invalid) > 1 else '', ' '.join(invalid)))

                if flag in pkgconfig:
                    intersec = conflicts.intersection(set(pkgconfig))
                    if intersec:
                        bb.fatal("%s: PACKAGECONFIG[%s] Conflict package config%s '%s' set in PACKAGECONFIG."
                            % (d.getVar('PN'), flag, 's' if len(intersec) > 1 else '', ' '.join(intersec)))

        appendVar('DEPENDS', extradeps)
        appendVar('RDEPENDS:${PN}', extrardeps)
        appendVar('RRECOMMENDS:${PN}', extrarrecs)
        appendVar('PACKAGECONFIG_CONFARGS', extraconf)

    pn = d.getVar('PN')
    license = d.getVar('LICENSE')
    if license == "INVALID" and pn != "defaultpkgname":
        bb.fatal('This recipe does not have the LICENSE field set (%s)' % pn)

    if bb.data.inherits_class('license', d):
        check_license_format(d)
        unmatched_license_flags = check_license_flags(d)
        if unmatched_license_flags:
            for unmatched in unmatched_license_flags:
                message = "Has a restricted license '%s' which is not listed in your LICENSE_FLAGS_ACCEPTED." % unmatched
                details = d.getVarFlag("LICENSE_FLAGS_DETAILS", unmatched)
                if details:
                    message += "\n" + details
            bb.debug(1, "Skipping %s: %s" % (pn, message))
            raise bb.parse.SkipRecipe(message)

    # If we're building a target package we need to use fakeroot (pseudo)
    # in order to capture permissions, owners, groups and special files
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('cross', d):
        d.appendVarFlag('do_prepare_recipe_sysroot', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
        d.appendVarFlag('do_install', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
        d.setVarFlag('do_install', 'fakeroot', '1')
        d.appendVarFlag('do_package', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
        d.setVarFlag('do_package', 'fakeroot', '1')
        d.setVarFlag('do_package_setscene', 'fakeroot', '1')
        d.appendVarFlag('do_package_setscene', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
        d.setVarFlag('do_devshell', 'fakeroot', '1')
        d.appendVarFlag('do_devshell', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')

    need_machine = d.getVar('COMPATIBLE_MACHINE')
    if need_machine and not d.getVar('PARSE_ALL_RECIPES', False):
        import re
        compat_machines = (d.getVar('MACHINEOVERRIDES') or "").split(":")
        for m in compat_machines:
            if re.match(need_machine, m):
                break
        else:
            raise bb.parse.SkipRecipe("incompatible with machine %s (not in COMPATIBLE_MACHINE)" % d.getVar('MACHINE'))

    source_mirror_fetch = d.getVar('SOURCE_MIRROR_FETCH', False) or d.getVar('PARSE_ALL_RECIPES', False)
    if not source_mirror_fetch:
        need_host = d.getVar('COMPATIBLE_HOST')
        if need_host:
            import re
            this_host = d.getVar('HOST_SYS')
            if not re.match(need_host, this_host):
                raise bb.parse.SkipRecipe("incompatible with host %s (not in COMPATIBLE_HOST)" % this_host)

        bad_licenses = (d.getVar('INCOMPATIBLE_LICENSE') or "").split()

        check_license = False if pn.startswith("nativesdk-") else True
        for t in ["-native", "-cross-${TARGET_ARCH}", "-cross-initial-${TARGET_ARCH}",
              "-crosssdk-${SDK_SYS}", "-crosssdk-initial-${SDK_SYS}",
              "-cross-canadian-${TRANSLATED_TARGET_ARCH}"]:
            if pn.endswith(d.expand(t)):
                check_license = False
        if pn.startswith("gcc-source-"):
            check_license = False

        if check_license and bad_licenses:
            bad_licenses = expand_wildcard_licenses(d, bad_licenses)

            exceptions = (d.getVar("INCOMPATIBLE_LICENSE_EXCEPTIONS") or "").split()

            for lic_exception in exceptions:
                if ":" in lic_exception:
                    lic_exception = lic_exception.split(":")[1]
                if lic_exception in oe.license.obsolete_license_list():
                    bb.fatal("Obsolete license %s used in INCOMPATIBLE_LICENSE_EXCEPTIONS" % lic_exception)

            pkgs = d.getVar('PACKAGES').split()
            skipped_pkgs = {}
            unskipped_pkgs = []
            for pkg in pkgs:
                remaining_bad_licenses = oe.license.apply_pkg_license_exception(pkg, bad_licenses, exceptions)

                incompatible_lic = incompatible_license(d, remaining_bad_licenses, pkg)
                if incompatible_lic:
                    skipped_pkgs[pkg] = incompatible_lic
                else:
                    unskipped_pkgs.append(pkg)

            if unskipped_pkgs:
                for pkg in skipped_pkgs:
                    bb.debug(1, "Skipping the package %s at do_rootfs because of incompatible license(s): %s" % (pkg, ' '.join(skipped_pkgs[pkg])))
                    d.setVar('_exclude_incompatible-' + pkg, ' '.join(skipped_pkgs[pkg]))
                for pkg in unskipped_pkgs:
                    bb.debug(1, "Including the package %s" % pkg)
            else:
                incompatible_lic = incompatible_license(d, bad_licenses)
                for pkg in skipped_pkgs:
                    incompatible_lic += skipped_pkgs[pkg]
                incompatible_lic = sorted(list(set(incompatible_lic)))

                if incompatible_lic:
                    bb.debug(1, "Skipping recipe %s because of incompatible license(s): %s" % (pn, ' '.join(incompatible_lic)))
                    raise bb.parse.SkipRecipe("it has incompatible license(s): %s" % ' '.join(incompatible_lic))

    srcuri = d.getVar('SRC_URI')
    for uri_string in srcuri.split():
        uri = bb.fetch.URI(uri_string)
        # Also check downloadfilename as the URL path might not be useful for sniffing
        path = uri.params.get("downloadfilename", uri.path)

        # HTTP/FTP use the wget fetcher
        if uri.scheme in ("http", "https", "ftp"):
            d.appendVarFlag('do_fetch', 'depends', ' wget-native:do_populate_sysroot')

        # Svn packages should DEPEND on subversion-native
        if uri.scheme == "svn":
            d.appendVarFlag('do_fetch', 'depends', ' subversion-native:do_populate_sysroot')

        # Git packages should DEPEND on git-native
        elif uri.scheme in ("git", "gitsm"):
            d.appendVarFlag('do_fetch', 'depends', ' git-native:do_populate_sysroot')

        # Mercurial packages should DEPEND on mercurial-native
        elif uri.scheme == "hg":
            d.appendVar("EXTRANATIVEPATH", ' python3-native ')
            d.appendVarFlag('do_fetch', 'depends', ' mercurial-native:do_populate_sysroot ca-certificates-native:do_populate_sysroot')

        # OSC packages should DEPEND on osc-native
        elif uri.scheme == "osc":
            d.appendVarFlag('do_fetch', 'depends', ' osc-native:do_populate_sysroot')

        elif uri.scheme == "npm":
            d.appendVarFlag('do_fetch', 'depends', ' nodejs-native:do_populate_sysroot')

        elif uri.scheme == "repo":
            d.appendVarFlag('do_fetch', 'depends', ' repo-native:do_populate_sysroot')

        # *.lz4 should DEPEND on lz4-native for unpacking
        if path.endswith('.lz4'):
            d.appendVarFlag('do_unpack', 'depends', ' lz4-native:do_populate_sysroot')

        # *.zst should DEPEND on zstd-native for unpacking
        elif path.endswith('.zst'):
            d.appendVarFlag('do_unpack', 'depends', ' zstd-native:do_populate_sysroot')

        # *.lz should DEPEND on lzip-native for unpacking
        elif path.endswith('.lz'):
            d.appendVarFlag('do_unpack', 'depends', ' lzip-native:do_populate_sysroot')

        # *.xz should DEPEND on xz-native for unpacking
        elif path.endswith('.xz') or path.endswith('.txz'):
            d.appendVarFlag('do_unpack', 'depends', ' xz-native:do_populate_sysroot')

        # .zip should DEPEND on unzip-native for unpacking
        elif path.endswith('.zip') or path.endswith('.jar'):
            d.appendVarFlag('do_unpack', 'depends', ' unzip-native:do_populate_sysroot')

        # Some rpm files may be compressed internally using xz (for example, rpms from Fedora)
        elif path.endswith('.rpm'):
            d.appendVarFlag('do_unpack', 'depends', ' xz-native:do_populate_sysroot')

        # *.deb should DEPEND on xz-native for unpacking
        elif path.endswith('.deb'):
            d.appendVarFlag('do_unpack', 'depends', ' xz-native:do_populate_sysroot')

        # *.7z should DEPEND on p7zip-native for unpacking
        elif path.endswith('.7z'):
            d.appendVarFlag('do_unpack', 'depends', ' p7zip-native:do_populate_sysroot')

    set_packagetriplet(d)

    # 'multimachine' handling
    mach_arch = d.getVar('MACHINE_ARCH')
    pkg_arch = d.getVar('PACKAGE_ARCH')

    if (pkg_arch == mach_arch):
        # Already machine specific - nothing further to do
        return

    #
    # We always try to scan SRC_URI for urls with machine overrides
    # unless the package sets SRC_URI_OVERRIDES_PACKAGE_ARCH=0
    #
    override = d.getVar('SRC_URI_OVERRIDES_PACKAGE_ARCH')
    if override != '0':
        paths = []
        fpaths = (d.getVar('FILESPATH') or '').split(':')
        machine = d.getVar('MACHINE')
        for p in fpaths:
            if os.path.basename(p) == machine and os.path.isdir(p):
                paths.append(p)

        if paths:
            for s in srcuri.split():
                if not s.startswith("file://"):
                    continue
                fetcher = bb.fetch2.Fetch([s], d)
                local = fetcher.localpath(s)
                for mp in paths:
                    if local.startswith(mp):
                        #bb.note("overriding PACKAGE_ARCH from %s to %s for %s" % (pkg_arch, mach_arch, pn))
                        d.setVar('PACKAGE_ARCH', "${MACHINE_ARCH}")
                        return

    packages = d.getVar('PACKAGES').split()
    for pkg in packages:
        pkgarch = d.getVar("PACKAGE_ARCH_%s" % pkg)

        # We could look for != PACKAGE_ARCH here but how to choose
        # if multiple differences are present?
        # Look through PACKAGE_ARCHS for the priority order?
        if pkgarch and pkgarch == mach_arch:
            d.setVar('PACKAGE_ARCH', "${MACHINE_ARCH}")
            bb.warn("Recipe %s is marked as only being architecture specific but seems to have machine specific packages?! The recipe may as well mark itself as machine specific directly." % d.getVar("PN"))
}

addtask cleansstate after do_clean
python do_cleansstate() {
        sstate_clean_cachefiles(d)
}
addtask cleanall after do_cleansstate
do_cleansstate[nostamp] = "1"

python do_cleanall() {
    src_uri = (d.getVar('SRC_URI') or "").split()
    if not src_uri:
        return

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.clean()
    except bb.fetch2.BBFetchException as e:
        bb.fatal(str(e))
}
do_cleanall[nostamp] = "1"


EXPORT_FUNCTIONS do_fetch do_unpack do_configure do_compile do_install
