BB_DEFAULT_TASK ?= "build"
CLASSOVERRIDE ?= "class-target"

inherit patch
inherit staging

inherit mirrors
inherit utils
inherit utility-tasks
inherit metadata_scm
inherit logging

OE_IMPORTS += "os sys time oe.path oe.utils oe.types oe.package oe.packagegroup oe.sstatesig oe.lsb oe.cachedpath oe.license"
OE_IMPORTS[type] = "list"

def oe_import(d):
    import sys

    bbpath = d.getVar("BBPATH").split(":")
    sys.path[0:0] = [os.path.join(dir, "lib") for dir in bbpath]

    def inject(name, value):
        """Make a python object accessible from the metadata"""
        if hasattr(bb.utils, "_context"):
            bb.utils._context[name] = value
        else:
            __builtins__[name] = value

    import oe.data
    for toimport in oe.data.typed_value("OE_IMPORTS", d):
        imported = __import__(toimport)
        inject(toimport.split(".", 1)[0], imported)

    return ""

# We need the oe module name space early (before INHERITs get added)
OE_IMPORTED := "${@oe_import(d)}"

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


def base_dep_prepend(d):
    if d.getVar('INHIBIT_DEFAULT_DEPS', False):
        return ""
    return "${BASE_DEFAULT_DEPS}"

BASE_DEFAULT_DEPS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}compilerlibs virtual/libc"

BASEDEPENDS = ""
BASEDEPENDS_class-target = "${@base_dep_prepend(d)}"
BASEDEPENDS_class-nativesdk = "${@base_dep_prepend(d)}"

DEPENDS_prepend="${BASEDEPENDS} "

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

PATH_prepend = "${@extra_path_elements(d)}"

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
    bb.utils.mkdirhier(dest)
    notfound = []
    for tool in tools:
        desttool = os.path.join(dest, tool)
        if not os.path.exists(desttool):
            srctool = bb.utils.which(path, tool, executable=True)
            if "ccache" in srctool:
                srctool = bb.utils.which(path, tool, executable=True, direction=1)
            if srctool:
                os.symlink(srctool, desttool)
            else:
                notfound.append(tool)
    if notfound and fatal:
        bb.fatal("The following required tools (as specified by HOSTTOOLS) appear to be unavailable in PATH, please install them in order to proceed:\n  %s" % " ".join(notfound))

addtask fetch
do_fetch[dirs] = "${DL_DIR}"
do_fetch[file-checksums] = "${@bb.fetch.get_checksum_file_list(d)}"
do_fetch[file-checksums] += " ${@get_lic_checksum_file_list(d)}"
do_fetch[vardeps] += "SRCREV"
python base_do_fetch() {

    src_uri = (d.getVar('SRC_URI') or "").split()
    if len(src_uri) == 0:
        return

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.download()
    except bb.fetch2.BBFetchException as e:
        bb.fatal(str(e))
}

addtask unpack after do_fetch
do_unpack[dirs] = "${WORKDIR}"

do_unpack[cleandirs] = "${@d.getVar('S') if d.getVar('S') != d.getVar('WORKDIR') else os.path.join('${S}', 'patches')}"

python base_do_unpack() {
    src_uri = (d.getVar('SRC_URI') or "").split()
    if len(src_uri) == 0:
        return

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.unpack(d.getVar('WORKDIR'))
    except bb.fetch2.BBFetchException as e:
        bb.fatal(str(e))
}

def get_layers_branch_rev(d):
    layers = (d.getVar("BBLAYERS") or "").split()
    layers_branch_rev = ["%-20s = \"%s:%s\"" % (os.path.basename(i), \
        base_get_metadata_git_branch(i, None).strip(), \
        base_get_metadata_git_revision(i, None)) \
            for i in layers]
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
base_eventhandler[eventmask] = "bb.event.ConfigParsed bb.event.MultiConfigParsed bb.event.BuildStarted bb.event.RecipePreFinalise bb.runqueue.sceneQueueComplete bb.event.RecipeParsed"
python base_eventhandler() {
    import bb.runqueue

    if isinstance(e, bb.event.ConfigParsed):
        if not d.getVar("NATIVELSBSTRING", False):
            d.setVar("NATIVELSBSTRING", lsb_distro_identifier(d))
        d.setVar('BB_VERSION', bb.__version__)
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
    # target ones and we'd see dulpicate key names overwriting each other
    # for various PREFERRED_PROVIDERS
    if isinstance(e, bb.event.RecipePreFinalise):
        if d.getVar("TARGET_PREFIX") == d.getVar("SDK_PREFIX"):
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils")
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc-initial")
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc")
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}g++")
            d.delVar("PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}compilerlibs")

    if isinstance(e, bb.runqueue.sceneQueueComplete):
        completions = d.expand("${STAGING_DIR}/sstatecompletions")
        if os.path.exists(completions):
            cmds = set()
            with open(completions, "r") as f:
                cmds = set(f)
            d.setVar("completion_function", "\n".join(cmds))
            d.setVarFlag("completion_function", "func", "1")
            bb.debug(1, "Executing SceneQueue Completion commands: %s" % "\n".join(cmds))
            bb.build.exec_func("completion_function", d)
            os.remove(completions)

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
            multiwhitelist = (d.getVar("MULTI_PROVIDER_WHITELIST") or "").split()
            for p in provs:
                if p.startswith("virtual/") and p not in multiwhitelist:
                    profprov = d.getVar("PREFERRED_PROVIDER_" + p)
                    if profprov and pn != profprov:
                        raise bb.parse.SkipRecipe("PREFERRED_PROVIDER_%s set to %s, not %s" % (p, profprov, pn))
}

CONFIGURESTAMPFILE = "${WORKDIR}/configure.sstate"
CLEANBROKEN = "0"

addtask configure after do_patch
do_configure[dirs] = "${B}"
do_prepare_recipe_sysroot[deptask] = "do_populate_sysroot"
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
# Remove and re-create ${D} so that is it guaranteed to be empty
do_install[cleandirs] = "${D}"

base_do_install() {
	:
}

base_do_package() {
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

    # Handle PACKAGECONFIG
    #
    # These take the form:
    #
    # PACKAGECONFIG ??= "<default options>"
    # PACKAGECONFIG[foo] = "--enable-foo,--disable-foo,foo_depends,foo_runtime_depends,foo_runtime_recommends"
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
            if num > 5:
                bb.error("%s: PACKAGECONFIG[%s] Only enable,disable,depend,rdepend,rrecommend can be specified!"
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
        appendVar('DEPENDS', extradeps)
        appendVar('RDEPENDS_${PN}', extrardeps)
        appendVar('RRECOMMENDS_${PN}', extrarrecs)
        appendVar('PACKAGECONFIG_CONFARGS', extraconf)

    pn = d.getVar('PN')
    license = d.getVar('LICENSE')
    if license == "INVALID" and pn != "defaultpkgname":
        bb.fatal('This recipe does not have the LICENSE field set (%s)' % pn)

    if bb.data.inherits_class('license', d):
        check_license_format(d)
        unmatched_license_flag = check_license_flags(d)
        if unmatched_license_flag:
            bb.debug(1, "Skipping %s because it has a restricted license not"
                 " whitelisted in LICENSE_FLAGS_WHITELIST" % pn)
            raise bb.parse.SkipRecipe("because it has a restricted license not"
                 " whitelisted in LICENSE_FLAGS_WHITELIST")

    # If we're building a target package we need to use fakeroot (pseudo)
    # in order to capture permissions, owners, groups and special files
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('cross', d):
        d.setVarFlag('do_unpack', 'umask', '022')
        d.setVarFlag('do_configure', 'umask', '022')
        d.setVarFlag('do_compile', 'umask', '022')
        d.appendVarFlag('do_install', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
        d.setVarFlag('do_install', 'fakeroot', '1')
        d.setVarFlag('do_install', 'umask', '022')
        d.appendVarFlag('do_package', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
        d.setVarFlag('do_package', 'fakeroot', '1')
        d.setVarFlag('do_package', 'umask', '022')
        d.setVarFlag('do_package_setscene', 'fakeroot', '1')
        d.appendVarFlag('do_package_setscene', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
        d.setVarFlag('do_devshell', 'fakeroot', '1')
        d.appendVarFlag('do_devshell', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')

    need_machine = d.getVar('COMPATIBLE_MACHINE')
    if need_machine:
        import re
        compat_machines = (d.getVar('MACHINEOVERRIDES') or "").split(":")
        for m in compat_machines:
            if re.match(need_machine, m):
                break
        else:
            raise bb.parse.SkipRecipe("incompatible with machine %s (not in COMPATIBLE_MACHINE)" % d.getVar('MACHINE'))

    source_mirror_fetch = d.getVar('SOURCE_MIRROR_FETCH', False)
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

            whitelist = []
            incompatwl = []
            for lic in bad_licenses:
                spdx_license = return_spdx(d, lic)
                for w in ["LGPLv2_WHITELIST_", "WHITELIST_"]:
                    whitelist.extend((d.getVar(w + lic) or "").split())
                    if spdx_license:
                        whitelist.extend((d.getVar(w + spdx_license) or "").split())
                    '''
                    We need to track what we are whitelisting and why. If pn is
                    incompatible we need to be able to note that the image that
                    is created may infact contain incompatible licenses despite
                    INCOMPATIBLE_LICENSE being set.
                    '''
                    incompatwl.extend((d.getVar(w + lic) or "").split())
                    if spdx_license:
                        incompatwl.extend((d.getVar(w + spdx_license) or "").split())

            if not pn in whitelist:
                pkgs = d.getVar('PACKAGES').split()
                skipped_pkgs = []
                unskipped_pkgs = []
                for pkg in pkgs:
                    if incompatible_license(d, bad_licenses, pkg):
                        skipped_pkgs.append(pkg)
                    else:
                        unskipped_pkgs.append(pkg)
                all_skipped = skipped_pkgs and not unskipped_pkgs
                if unskipped_pkgs:
                    for pkg in skipped_pkgs:
                        bb.debug(1, "SKIPPING the package " + pkg + " at do_rootfs because it's " + license)
                        mlprefix = d.getVar('MLPREFIX')
                        d.setVar('LICENSE_EXCLUSION-' + mlprefix + pkg, 1)
                    for pkg in unskipped_pkgs:
                        bb.debug(1, "INCLUDING the package " + pkg)
                elif all_skipped or incompatible_license(d, bad_licenses):
                    bb.debug(1, "SKIPPING recipe %s because it's %s" % (pn, license))
                    raise bb.parse.SkipRecipe("it has an incompatible license: %s" % license)
            elif pn in whitelist:
                if pn in incompatwl:
                    bb.note("INCLUDING " + pn + " as buildable despite INCOMPATIBLE_LICENSE because it has been whitelisted")

        # Try to verify per-package (LICENSE_<pkg>) values. LICENSE should be a
        # superset of all per-package licenses. We do not do advanced (pattern)
        # matching of license expressions - just check that all license strings
        # in LICENSE_<pkg> are found in LICENSE.
        license_set = oe.license.list_licenses(license)
        for pkg in d.getVar('PACKAGES').split():
            pkg_license = d.getVar('LICENSE_' + pkg)
            if pkg_license:
                unlisted = oe.license.list_licenses(pkg_license) - license_set
                if unlisted:
                    bb.warn("LICENSE_%s includes licenses (%s) that are not "
                            "listed in LICENSE" % (pkg, ' '.join(unlisted)))

    needsrcrev = False
    srcuri = d.getVar('SRC_URI')
    for uri in srcuri.split():
        (scheme, _ , path) = bb.fetch.decodeurl(uri)[:3]

        # HTTP/FTP use the wget fetcher
        if scheme in ("http", "https", "ftp"):
            d.appendVarFlag('do_fetch', 'depends', ' wget-native:do_populate_sysroot')

        # Svn packages should DEPEND on subversion-native
        if scheme == "svn":
            needsrcrev = True
            d.appendVarFlag('do_fetch', 'depends', ' subversion-native:do_populate_sysroot')

        # Git packages should DEPEND on git-native
        elif scheme in ("git", "gitsm"):
            needsrcrev = True
            d.appendVarFlag('do_fetch', 'depends', ' git-native:do_populate_sysroot')

        # Mercurial packages should DEPEND on mercurial-native
        elif scheme == "hg":
            needsrcrev = True
            d.appendVarFlag('do_fetch', 'depends', ' mercurial-native:do_populate_sysroot')

        # Perforce packages support SRCREV = "${AUTOREV}"
        elif scheme == "p4":
            needsrcrev = True

        # OSC packages should DEPEND on osc-native
        elif scheme == "osc":
            d.appendVarFlag('do_fetch', 'depends', ' osc-native:do_populate_sysroot')

        elif scheme == "npm":
            d.appendVarFlag('do_fetch', 'depends', ' nodejs-native:do_populate_sysroot')

        # *.lz4 should DEPEND on lz4-native for unpacking
        if path.endswith('.lz4'):
            d.appendVarFlag('do_unpack', 'depends', ' lz4-native:do_populate_sysroot')

        # *.lz should DEPEND on lzip-native for unpacking
        elif path.endswith('.lz'):
            d.appendVarFlag('do_unpack', 'depends', ' lzip-native:do_populate_sysroot')

        # *.xz should DEPEND on xz-native for unpacking
        elif path.endswith('.xz') or path.endswith('.txz'):
            d.appendVarFlag('do_unpack', 'depends', ' xz-native:do_populate_sysroot')

        # .zip should DEPEND on unzip-native for unpacking
        elif path.endswith('.zip') or path.endswith('.jar'):
            d.appendVarFlag('do_unpack', 'depends', ' unzip-native:do_populate_sysroot')

        # file is needed by rpm2cpio.sh
        elif path.endswith('.rpm'):
            d.appendVarFlag('do_unpack', 'depends', ' xz-native:do_populate_sysroot')

        # *.deb should DEPEND on xz-native for unpacking
        elif path.endswith('.deb'):
            d.appendVarFlag('do_unpack', 'depends', ' xz-native:do_populate_sysroot')

    if needsrcrev:
        d.setVar("SRCPV", "${@bb.fetch2.get_srcrev(d)}")

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

        if len(paths) != 0:
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
    if len(src_uri) == 0:
        return

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.clean()
    except bb.fetch2.BBFetchException as e:
        bb.fatal(str(e))
}
do_cleanall[nostamp] = "1"


EXPORT_FUNCTIONS do_fetch do_unpack do_configure do_compile do_install do_package
