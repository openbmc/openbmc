#
# Sanity check the users setup for common misconfigurations
#

SANITY_REQUIRED_UTILITIES ?= "patch diffstat git bzip2 tar \
    gzip gawk chrpath wget cpio perl file which"

def bblayers_conf_file(d):
    return os.path.join(d.getVar('TOPDIR'), 'conf/bblayers.conf')

def sanity_conf_read(fn):
    with open(fn, 'r') as f:
        lines = f.readlines()
    return lines

def sanity_conf_find_line(pattern, lines):
    import re
    return next(((index, line)
        for index, line in enumerate(lines)
        if re.search(pattern, line)), (None, None))

def sanity_conf_update(fn, lines, version_var_name, new_version):
    index, line = sanity_conf_find_line(r"^%s" % version_var_name, lines)
    lines[index] = '%s = "%d"\n' % (version_var_name, new_version)
    with open(fn, "w") as f:
        f.write(''.join(lines))

# Functions added to this variable MUST throw a NotImplementedError exception unless 
# they successfully changed the config version in the config file. Exceptions
# are used since exec_func doesn't handle return values.
BBLAYERS_CONF_UPDATE_FUNCS += " \
    conf/bblayers.conf:LCONF_VERSION:LAYER_CONF_VERSION:oecore_update_bblayers \
    conf/local.conf:CONF_VERSION:LOCALCONF_VERSION:oecore_update_localconf \
    conf/site.conf:SCONF_VERSION:SITE_CONF_VERSION:oecore_update_siteconf \
"

SANITY_DIFF_TOOL ?= "meld"

SANITY_LOCALCONF_SAMPLE ?= "${COREBASE}/meta*/conf/local.conf.sample"
python oecore_update_localconf() {
    # Check we are using a valid local.conf
    current_conf  = d.getVar('CONF_VERSION')
    conf_version =  d.getVar('LOCALCONF_VERSION')

    failmsg = """Your version of local.conf was generated from an older/newer version of 
local.conf.sample and there have been updates made to this file. Please compare the two 
files and merge any changes before continuing.

Matching the version numbers will remove this message.

\"${SANITY_DIFF_TOOL} conf/local.conf ${SANITY_LOCALCONF_SAMPLE}\" 

is a good way to visualise the changes."""
    failmsg = d.expand(failmsg)

    raise NotImplementedError(failmsg)
}

SANITY_SITECONF_SAMPLE ?= "${COREBASE}/meta*/conf/site.conf.sample"
python oecore_update_siteconf() {
    # If we have a site.conf, check it's valid
    current_sconf = d.getVar('SCONF_VERSION')
    sconf_version = d.getVar('SITE_CONF_VERSION')

    failmsg = """Your version of site.conf was generated from an older version of 
site.conf.sample and there have been updates made to this file. Please compare the two 
files and merge any changes before continuing.

Matching the version numbers will remove this message.

\"${SANITY_DIFF_TOOL} conf/site.conf ${SANITY_SITECONF_SAMPLE}\" 

is a good way to visualise the changes."""
    failmsg = d.expand(failmsg)

    raise NotImplementedError(failmsg)
}

SANITY_BBLAYERCONF_SAMPLE ?= "${COREBASE}/meta*/conf/bblayers.conf.sample"
python oecore_update_bblayers() {
    # bblayers.conf is out of date, so see if we can resolve that

    current_lconf = int(d.getVar('LCONF_VERSION'))
    lconf_version = int(d.getVar('LAYER_CONF_VERSION'))

    failmsg = """Your version of bblayers.conf has the wrong LCONF_VERSION (has ${LCONF_VERSION}, expecting ${LAYER_CONF_VERSION}).
Please compare your file against bblayers.conf.sample and merge any changes before continuing.
"${SANITY_DIFF_TOOL} conf/bblayers.conf ${SANITY_BBLAYERCONF_SAMPLE}" 

is a good way to visualise the changes."""
    failmsg = d.expand(failmsg)

    if not current_lconf:
        raise NotImplementedError(failmsg)

    lines = []

    if current_lconf < 4:
        raise NotImplementedError(failmsg)

    bblayers_fn = bblayers_conf_file(d)
    lines = sanity_conf_read(bblayers_fn)

    if current_lconf == 4 and lconf_version > 4:
        topdir_var = '$' + '{TOPDIR}'
        index, bbpath_line = sanity_conf_find_line('BBPATH', lines)
        if bbpath_line:
            start = bbpath_line.find('"')
            if start != -1 and (len(bbpath_line) != (start + 1)):
                if bbpath_line[start + 1] == '"':
                    lines[index] = (bbpath_line[:start + 1] +
                                    topdir_var + bbpath_line[start + 1:])
                else:
                    if not topdir_var in bbpath_line:
                        lines[index] = (bbpath_line[:start + 1] +
                                    topdir_var + ':' + bbpath_line[start + 1:])
            else:
                raise NotImplementedError(failmsg)
        else:
            index, bbfiles_line = sanity_conf_find_line('BBFILES', lines)
            if bbfiles_line:
                lines.insert(index, 'BBPATH = "' + topdir_var + '"\n')
            else:
                raise NotImplementedError(failmsg)

        current_lconf += 1
        sanity_conf_update(bblayers_fn, lines, 'LCONF_VERSION', current_lconf)
        bb.note("Your conf/bblayers.conf has been automatically updated.")
        return

    elif current_lconf == 5 and lconf_version > 5:
        # Null update, to avoid issues with people switching between poky and other distros
        current_lconf = 6
        sanity_conf_update(bblayers_fn, lines, 'LCONF_VERSION', current_lconf)
        bb.note("Your conf/bblayers.conf has been automatically updated.")
        return

        status.addresult()

    elif current_lconf == 6 and lconf_version > 6:
        # Handle rename of meta-yocto -> meta-poky
        # This marks the start of separate version numbers but code is needed in OE-Core
        # for the migration, one last time.
        layers = d.getVar('BBLAYERS').split()
        layers = [ os.path.basename(path) for path in layers ]
        if 'meta-yocto' in layers:
            found = False
            while True:
                index, meta_yocto_line = sanity_conf_find_line(r'.*meta-yocto[\'"\s\n]', lines)
                if meta_yocto_line:
                    lines[index] = meta_yocto_line.replace('meta-yocto', 'meta-poky')
                    found = True
                else:
                    break
            if not found:
                raise NotImplementedError(failmsg)
            index, meta_yocto_line = sanity_conf_find_line('LCONF_VERSION.*\n', lines)
            if meta_yocto_line:
                lines[index] = 'POKY_BBLAYERS_CONF_VERSION = "1"\n'
            else:
                raise NotImplementedError(failmsg)
            with open(bblayers_fn, "w") as f:
                f.write(''.join(lines))
            bb.note("Your conf/bblayers.conf has been automatically updated.")
            return
        current_lconf += 1
        sanity_conf_update(bblayers_fn, lines, 'LCONF_VERSION', current_lconf)
        bb.note("Your conf/bblayers.conf has been automatically updated.")
        return

    raise NotImplementedError(failmsg)
}

def raise_sanity_error(msg, d, network_error=False):
    if d.getVar("SANITY_USE_EVENTS") == "1":
        try:
            bb.event.fire(bb.event.SanityCheckFailed(msg, network_error), d)
        except TypeError:
            bb.event.fire(bb.event.SanityCheckFailed(msg), d)
        return

    bb.fatal(""" OE-core's config sanity checker detected a potential misconfiguration.
    Either fix the cause of this error or at your own risk disable the checker (see sanity.conf).
    Following is the list of potential problems / advisories:
    
    %s""" % msg)

# Check flags associated with a tuning.
def check_toolchain_tune_args(data, tune, multilib, errs):
    found_errors = False
    if check_toolchain_args_present(data, tune, multilib, errs, 'CCARGS'):
        found_errors = True
    if check_toolchain_args_present(data, tune, multilib, errs, 'ASARGS'):
        found_errors = True
    if check_toolchain_args_present(data, tune, multilib, errs, 'LDARGS'):
        found_errors = True

    return found_errors

def check_toolchain_args_present(data, tune, multilib, tune_errors, which):
    args_set = (data.getVar("TUNE_%s" % which) or "").split()
    args_wanted = (data.getVar("TUNEABI_REQUIRED_%s_tune-%s" % (which, tune)) or "").split()
    args_missing = []

    # If no args are listed/required, we are done.
    if not args_wanted:
        return
    for arg in args_wanted:
        if arg not in args_set:
            args_missing.append(arg)

    found_errors = False
    if args_missing:
        found_errors = True
        tune_errors.append("TUNEABI for %s requires '%s' in TUNE_%s (%s)." %
                       (tune, ' '.join(args_missing), which, ' '.join(args_set)))
    return found_errors

# Check a single tune for validity.
def check_toolchain_tune(data, tune, multilib):
    tune_errors = []
    if not tune:
        return "No tuning found for %s multilib." % multilib
    localdata = bb.data.createCopy(data)
    if multilib != "default":
        # Apply the overrides so we can look at the details.
        overrides = localdata.getVar("OVERRIDES", False) + ":virtclass-multilib-" + multilib
        localdata.setVar("OVERRIDES", overrides)
    bb.debug(2, "Sanity-checking tuning '%s' (%s) features:" % (tune, multilib))
    features = (localdata.getVar("TUNE_FEATURES_tune-%s" % tune) or "").split()
    if not features:
        return "Tuning '%s' has no defined features, and cannot be used." % tune
    valid_tunes = localdata.getVarFlags('TUNEVALID') or {}
    conflicts = localdata.getVarFlags('TUNECONFLICTS') or {}
    # [doc] is the documentation for the variable, not a real feature
    if 'doc' in valid_tunes:
        del valid_tunes['doc']
    if 'doc' in conflicts:
        del conflicts['doc']
    for feature in features:
        if feature in conflicts:
            for conflict in conflicts[feature].split():
                if conflict in features:
                    tune_errors.append("Feature '%s' conflicts with '%s'." %
                        (feature, conflict))
        if feature in valid_tunes:
            bb.debug(2, "  %s: %s" % (feature, valid_tunes[feature]))
        else:
            tune_errors.append("Feature '%s' is not defined." % feature)
    whitelist = localdata.getVar("TUNEABI_WHITELIST")
    if whitelist:
        tuneabi = localdata.getVar("TUNEABI_tune-%s" % tune)
        if not tuneabi:
            tuneabi = tune
        if True not in [x in whitelist.split() for x in tuneabi.split()]:
            tune_errors.append("Tuning '%s' (%s) cannot be used with any supported tuning/ABI." %
                (tune, tuneabi))
        else:
            if not check_toolchain_tune_args(localdata, tuneabi, multilib, tune_errors):
                bb.debug(2, "Sanity check: Compiler args OK for %s." % tune)
    if tune_errors:
        return "Tuning '%s' has the following errors:\n" % tune + '\n'.join(tune_errors)

def check_toolchain(data):
    tune_error_set = []
    deftune = data.getVar("DEFAULTTUNE")
    tune_errors = check_toolchain_tune(data, deftune, 'default')
    if tune_errors:
        tune_error_set.append(tune_errors)

    multilibs = (data.getVar("MULTILIB_VARIANTS") or "").split()
    global_multilibs = (data.getVar("MULTILIB_GLOBAL_VARIANTS") or "").split()

    if multilibs:
        seen_libs = []
        seen_tunes = []
        for lib in multilibs:
            if lib in seen_libs:
                tune_error_set.append("The multilib '%s' appears more than once." % lib)
            else:
                seen_libs.append(lib)
            if not lib in global_multilibs:
                tune_error_set.append("Multilib %s is not present in MULTILIB_GLOBAL_VARIANTS" % lib)
            tune = data.getVar("DEFAULTTUNE_virtclass-multilib-%s" % lib)
            if tune in seen_tunes:
                tune_error_set.append("The tuning '%s' appears in more than one multilib." % tune)
            else:
                seen_libs.append(tune)
            if tune == deftune:
                tune_error_set.append("Multilib '%s' (%s) is also the default tuning." % (lib, deftune))
            else:
                tune_errors = check_toolchain_tune(data, tune, lib)
            if tune_errors:
                tune_error_set.append(tune_errors)
    if tune_error_set:
        return "Toolchain tunings invalid:\n" + '\n'.join(tune_error_set) + "\n"

    return ""

def check_conf_exists(fn, data):
    bbpath = []
    fn = data.expand(fn)
    vbbpath = data.getVar("BBPATH", False)
    if vbbpath:
        bbpath += vbbpath.split(":")
    for p in bbpath:
        currname = os.path.join(data.expand(p), fn)
        if os.access(currname, os.R_OK):
            return True
    return False

def check_create_long_filename(filepath, pathname):
    import string, random
    testfile = os.path.join(filepath, ''.join(random.choice(string.ascii_letters) for x in range(200)))
    try:
        if not os.path.exists(filepath):
            bb.utils.mkdirhier(filepath)
        f = open(testfile, "w")
        f.close()
        os.remove(testfile)
    except IOError as e:
        import errno
        err, strerror = e.args
        if err == errno.ENAMETOOLONG:
            return "Failed to create a file with a long name in %s. Please use a filesystem that does not unreasonably limit filename length.\n" % pathname
        else:
            return "Failed to create a file in %s: %s.\n" % (pathname, strerror)
    except OSError as e:
        errno, strerror = e.args
        return "Failed to create %s directory in which to run long name sanity check: %s.\n" % (pathname, strerror)
    return ""

def check_path_length(filepath, pathname, limit):
    if len(filepath) > limit:
        return "The length of %s is longer than %s, this would cause unexpected errors, please use a shorter path.\n" % (pathname, limit)
    return ""

def get_filesystem_id(path):
    import subprocess
    try:
        return subprocess.check_output(["stat", "-f", "-c", "%t", path]).decode('utf-8').strip()
    except subprocess.CalledProcessError:
        bb.warn("Can't get filesystem id of: %s" % path)
        return None

# Check that the path isn't located on nfs.
def check_not_nfs(path, name):
    # The nfs' filesystem id is 6969
    if get_filesystem_id(path) == "6969":
        return "The %s: %s can't be located on nfs.\n" % (name, path)
    return ""

# Check that the path is on a case-sensitive file system
def check_case_sensitive(path, name):
    import tempfile
    with tempfile.NamedTemporaryFile(prefix='TmP', dir=path) as tmp_file:
        if os.path.exists(tmp_file.name.lower()):
            return "The %s (%s) can't be on a case-insensitive file system.\n" % (name, path)
        return ""

# Check that path isn't a broken symlink
def check_symlink(lnk, data):
    if os.path.islink(lnk) and not os.path.exists(lnk):
        raise_sanity_error("%s is a broken symlink." % lnk, data)

def check_connectivity(d):
    # URI's to check can be set in the CONNECTIVITY_CHECK_URIS variable
    # using the same syntax as for SRC_URI. If the variable is not set
    # the check is skipped
    test_uris = (d.getVar('CONNECTIVITY_CHECK_URIS') or "").split()
    retval = ""

    bbn = d.getVar('BB_NO_NETWORK')
    if bbn not in (None, '0', '1'):
        return 'BB_NO_NETWORK should be "0" or "1", but it is "%s"' % bbn

    # Only check connectivity if network enabled and the
    # CONNECTIVITY_CHECK_URIS are set
    network_enabled = not (bbn == '1')
    check_enabled = len(test_uris)
    if check_enabled and network_enabled:
        # Take a copy of the data store and unset MIRRORS and PREMIRRORS
        data = bb.data.createCopy(d)
        data.delVar('PREMIRRORS')
        data.delVar('MIRRORS')
        try:
            fetcher = bb.fetch2.Fetch(test_uris, data)
            fetcher.checkstatus()
        except Exception as err:
            # Allow the message to be configured so that users can be
            # pointed to a support mechanism.
            msg = data.getVar('CONNECTIVITY_CHECK_MSG') or ""
            if len(msg) == 0:
                msg = "%s.\n" % err
                msg += "    Please ensure your host's network is configured correctly,\n"
                msg += "    or set BB_NO_NETWORK = \"1\" to disable network access if\n"
                msg += "    all required sources are on local disk.\n"
            retval = msg

    return retval

def check_supported_distro(sanity_data):
    from fnmatch import fnmatch

    tested_distros = sanity_data.getVar('SANITY_TESTED_DISTROS')
    if not tested_distros:
        return

    try:
        distro = oe.lsb.distro_identifier()
    except Exception:
        distro = None

    if not distro:
        bb.warn('Host distribution could not be determined; you may possibly experience unexpected failures. It is recommended that you use a tested distribution.')

    for supported in [x.strip() for x in tested_distros.split('\\n')]:
        if fnmatch(distro, supported):
            return

    bb.warn('Host distribution "%s" has not been validated with this version of the build system; you may possibly experience unexpected failures. It is recommended that you use a tested distribution.' % distro)

# Checks we should only make if MACHINE is set correctly
def check_sanity_validmachine(sanity_data):
    messages = ""

    # Check TUNE_ARCH is set
    if sanity_data.getVar('TUNE_ARCH') == 'INVALID':
        messages = messages + 'TUNE_ARCH is unset. Please ensure your MACHINE configuration includes a valid tune configuration file which will set this correctly.\n'

    # Check TARGET_OS is set
    if sanity_data.getVar('TARGET_OS') == 'INVALID':
        messages = messages + 'Please set TARGET_OS directly, or choose a MACHINE or DISTRO that does so.\n'

    # Check that we don't have duplicate entries in PACKAGE_ARCHS & that TUNE_PKGARCH is in PACKAGE_ARCHS
    pkgarchs = sanity_data.getVar('PACKAGE_ARCHS')
    tunepkg = sanity_data.getVar('TUNE_PKGARCH')
    defaulttune = sanity_data.getVar('DEFAULTTUNE')
    tunefound = False
    seen = {}
    dups = []

    for pa in pkgarchs.split():
        if seen.get(pa, 0) == 1:
            dups.append(pa)
        else:
            seen[pa] = 1
        if pa == tunepkg:
            tunefound = True

    if len(dups):
        messages = messages + "Error, the PACKAGE_ARCHS variable contains duplicates. The following archs are listed more than once: %s" % " ".join(dups)

    if tunefound == False:
        messages = messages + "Error, the PACKAGE_ARCHS variable (%s) for DEFAULTTUNE (%s) does not contain TUNE_PKGARCH (%s)." % (pkgarchs, defaulttune, tunepkg)

    return messages

# Patch before 2.7 can't handle all the features in git-style diffs.  Some
# patches may incorrectly apply, and others won't apply at all.
def check_patch_version(sanity_data):
    from distutils.version import LooseVersion
    import re, subprocess

    try:
        result = subprocess.check_output(["patch", "--version"], stderr=subprocess.STDOUT).decode('utf-8')
        version = re.search(r"[0-9.]+", result.splitlines()[0]).group()
        if LooseVersion(version) < LooseVersion("2.7"):
            return "Your version of patch is older than 2.7 and has bugs which will break builds. Please install a newer version of patch.\n"
        else:
            return None
    except subprocess.CalledProcessError as e:
        return "Unable to execute patch --version, exit code %d:\n%s\n" % (e.returncode, e.output)

# Unpatched versions of make 3.82 are known to be broken.  See GNU Savannah Bug 30612.
# Use a modified reproducer from http://savannah.gnu.org/bugs/?30612 to validate.
def check_make_version(sanity_data):
    from distutils.version import LooseVersion
    import subprocess

    try:
        result = subprocess.check_output(['make', '--version'], stderr=subprocess.STDOUT).decode('utf-8')
    except subprocess.CalledProcessError as e:
        return "Unable to execute make --version, exit code %d\n%s\n" % (e.returncode, e.output)
    version = result.split()[2]
    if LooseVersion(version) == LooseVersion("3.82"):
        # Construct a test file
        f = open("makefile_test", "w")
        f.write("makefile_test.a: makefile_test_a.c makefile_test_b.c makefile_test.a( makefile_test_a.c makefile_test_b.c)\n")
        f.write("\n")
        f.write("makefile_test_a.c:\n")
        f.write("	touch $@\n")
        f.write("\n")
        f.write("makefile_test_b.c:\n")
        f.write("	touch $@\n")
        f.close()

        # Check if make 3.82 has been patched
        try:
            subprocess.check_call(['make', '-f', 'makefile_test'])
        except subprocess.CalledProcessError as e:
            return "Your version of make 3.82 is broken. Please revert to 3.81 or install a patched version.\n"
        finally:
            os.remove("makefile_test")
            if os.path.exists("makefile_test_a.c"):
                os.remove("makefile_test_a.c")
            if os.path.exists("makefile_test_b.c"):
                os.remove("makefile_test_b.c")
            if os.path.exists("makefile_test.a"):
                os.remove("makefile_test.a")
    return None


# Check if we're running on WSL (Windows Subsystem for Linux).
# WSLv1 is known not to work but WSLv2 should work properly as
# long as the VHDX file is optimized often, let the user know
# upfront.
# More information on installing WSLv2 at:
# https://docs.microsoft.com/en-us/windows/wsl/wsl2-install
def check_wsl(d):
    with open("/proc/version", "r") as f:
        verdata = f.readlines()
    for l in verdata:
        if "Microsoft" in l:
            return "OpenEmbedded doesn't work under WSLv1, please upgrade to WSLv2 if you want to run builds on Windows"
        elif "microsoft" in l:
            bb.warn("You are running bitbake under WSLv2, this works properly but you should optimize your VHDX file eventually to avoid running out of storage space")
    return None

# Require at least gcc version 5.0.
#
# This can be fixed on CentOS-7 with devtoolset-6+
# https://www.softwarecollections.org/en/scls/rhscl/devtoolset-6/
#
# A less invasive fix is with scripts/install-buildtools (or with user
# built buildtools-extended-tarball)
#
def check_gcc_version(sanity_data):
    from distutils.version import LooseVersion
    import subprocess
    
    build_cc, version = oe.utils.get_host_compiler_version(sanity_data)
    if build_cc.strip() == "gcc":
        if LooseVersion(version) < LooseVersion("5.0"):
            return "Your version of gcc is older than 5.0 and will break builds. Please install a newer version of gcc (you could use the project's buildtools-extended-tarball or use scripts/install-buildtools).\n"
    return None

# Tar version 1.24 and onwards handle overwriting symlinks correctly
# but earlier versions do not; this needs to work properly for sstate
# Version 1.28 is needed so opkg-build works correctly when reproducibile builds are enabled
def check_tar_version(sanity_data):
    from distutils.version import LooseVersion
    import subprocess
    try:
        result = subprocess.check_output(["tar", "--version"], stderr=subprocess.STDOUT).decode('utf-8')
    except subprocess.CalledProcessError as e:
        return "Unable to execute tar --version, exit code %d\n%s\n" % (e.returncode, e.output)
    version = result.split()[3]
    if LooseVersion(version) < LooseVersion("1.28"):
        return "Your version of tar is older than 1.28 and does not have the support needed to enable reproducible builds. Please install a newer version of tar (you could use the project's buildtools-tarball from our last release or use scripts/install-buildtools).\n"
    return None

# We use git parameters and functionality only found in 1.7.8 or later
# The kernel tools assume git >= 1.8.3.1 (verified needed > 1.7.9.5) see #6162 
# The git fetcher also had workarounds for git < 1.7.9.2 which we've dropped
def check_git_version(sanity_data):
    from distutils.version import LooseVersion
    import subprocess
    try:
        result = subprocess.check_output(["git", "--version"], stderr=subprocess.DEVNULL).decode('utf-8')
    except subprocess.CalledProcessError as e:
        return "Unable to execute git --version, exit code %d\n%s\n" % (e.returncode, e.output)
    version = result.split()[2]
    if LooseVersion(version) < LooseVersion("1.8.3.1"):
        return "Your version of git is older than 1.8.3.1 and has bugs which will break builds. Please install a newer version of git.\n"
    return None

# Check the required perl modules which may not be installed by default
def check_perl_modules(sanity_data):
    import subprocess
    ret = ""
    modules = ( "Text::ParseWords", "Thread::Queue", "Data::Dumper" )
    errresult = ''
    for m in modules:
        try:
            subprocess.check_output(["perl", "-e", "use %s" % m])
        except subprocess.CalledProcessError as e:
            errresult += bytes.decode(e.output)
            ret += "%s " % m
    if ret:
        return "Required perl module(s) not found: %s\n\n%s\n" % (ret, errresult)
    return None

def sanity_check_conffiles(d):
    funcs = d.getVar('BBLAYERS_CONF_UPDATE_FUNCS').split()
    for func in funcs:
        conffile, current_version, required_version, func = func.split(":")
        if check_conf_exists(conffile, d) and d.getVar(current_version) is not None and \
                d.getVar(current_version) != d.getVar(required_version):
            try:
                bb.build.exec_func(func, d)
            except NotImplementedError as e:
                bb.fatal(str(e))
            d.setVar("BB_INVALIDCONF", True)

def sanity_handle_abichanges(status, d):
    #
    # Check the 'ABI' of TMPDIR
    #
    import subprocess

    current_abi = d.getVar('OELAYOUT_ABI')
    abifile = d.getVar('SANITY_ABIFILE')
    if os.path.exists(abifile):
        with open(abifile, "r") as f:
            abi = f.read().strip()
        if not abi.isdigit():
            with open(abifile, "w") as f:
                f.write(current_abi)
        elif int(abi) <= 11 and current_abi == "12":
            status.addresult("The layout of TMPDIR changed for Recipe Specific Sysroots.\nConversion doesn't make sense and this change will rebuild everything so please delete TMPDIR (%s).\n" % d.getVar("TMPDIR"))
        elif int(abi) <= 13 and current_abi == "14":
            status.addresult("TMPDIR changed to include path filtering from the pseudo database.\nIt is recommended to use a clean TMPDIR with the new pseudo path filtering so TMPDIR (%s) would need to be removed to continue.\n" % d.getVar("TMPDIR"))

        elif (abi != current_abi):
            # Code to convert from one ABI to another could go here if possible.
            status.addresult("Error, TMPDIR has changed its layout version number (%s to %s) and you need to either rebuild, revert or adjust it at your own risk.\n" % (abi, current_abi))
    else:
        with open(abifile, "w") as f:
            f.write(current_abi)

def check_sanity_sstate_dir_change(sstate_dir, data):
    # Sanity checks to be done when the value of SSTATE_DIR changes

    # Check that SSTATE_DIR isn't on a filesystem with limited filename length (eg. eCryptFS)
    testmsg = ""
    if sstate_dir != "":
        testmsg = check_create_long_filename(sstate_dir, "SSTATE_DIR")
        # If we don't have permissions to SSTATE_DIR, suggest the user set it as an SSTATE_MIRRORS
        try:
            err = testmsg.split(': ')[1].strip()
            if err == "Permission denied.":
                testmsg = testmsg + "You could try using %s in SSTATE_MIRRORS rather than as an SSTATE_CACHE.\n" % (sstate_dir)
        except IndexError:
            pass
    return testmsg

def check_sanity_version_change(status, d):
    # Sanity checks to be done when SANITY_VERSION or NATIVELSBSTRING changes
    # In other words, these tests run once in a given build directory and then 
    # never again until the sanity version or host distrubution id/version changes.

    # Check the python install is complete. Examples that are often removed in
    # minimal installations: glib-2.0-natives requries # xml.parsers.expat and icu
    # requires distutils.sysconfig.
    try:
        import xml.parsers.expat
        import distutils.sysconfig
    except ImportError as e:
        status.addresult('Your Python 3 is not a full install. Please install the module %s (see the Getting Started guide for further information).\n' % e.name)

    status.addresult(check_gcc_version(d))
    status.addresult(check_make_version(d))
    status.addresult(check_patch_version(d))
    status.addresult(check_tar_version(d))
    status.addresult(check_git_version(d))
    status.addresult(check_perl_modules(d))
    status.addresult(check_wsl(d))

    missing = ""

    if not check_app_exists("${MAKE}", d):
        missing = missing + "GNU make,"

    if not check_app_exists('${BUILD_CC}', d):
        missing = missing + "C Compiler (%s)," % d.getVar("BUILD_CC")

    if not check_app_exists('${BUILD_CXX}', d):
        missing = missing + "C++ Compiler (%s)," % d.getVar("BUILD_CXX")

    required_utilities = d.getVar('SANITY_REQUIRED_UTILITIES')

    for util in required_utilities.split():
        if not check_app_exists(util, d):
            missing = missing + "%s," % util

    if missing:
        missing = missing.rstrip(',')
        status.addresult("Please install the following missing utilities: %s\n" % missing)

    assume_provided = d.getVar('ASSUME_PROVIDED').split()
    # Check user doesn't have ASSUME_PROVIDED = instead of += in local.conf
    if "diffstat-native" not in assume_provided:
        status.addresult('Please use ASSUME_PROVIDED +=, not ASSUME_PROVIDED = in your local.conf\n')

    # Check that TMPDIR isn't on a filesystem with limited filename length (eg. eCryptFS)
    import stat
    tmpdir = d.getVar('TMPDIR')
    status.addresult(check_create_long_filename(tmpdir, "TMPDIR"))
    tmpdirmode = os.stat(tmpdir).st_mode
    if (tmpdirmode & stat.S_ISGID):
        status.addresult("TMPDIR is setgid, please don't build in a setgid directory")
    if (tmpdirmode & stat.S_ISUID):
        status.addresult("TMPDIR is setuid, please don't build in a setuid directory")

    # Check that a user isn't building in a path in PSEUDO_IGNORE_PATHS
    pseudoignorepaths = d.getVar('PSEUDO_IGNORE_PATHS', expand=True).split(",")
    workdir = d.getVar('WORKDIR', expand=True)
    for i in pseudoignorepaths:
        if i and workdir.startswith(i):
            status.addresult("You are building in a path included in PSEUDO_IGNORE_PATHS " + str(i) + " please locate the build outside this path.\n")

    # Check if PSEUDO_IGNORE_PATHS and and paths under pseudo control overlap
    pseudoignorepaths = d.getVar('PSEUDO_IGNORE_PATHS', expand=True).split(",")
    pseudo_control_dir = "${D},${PKGD},${PKGDEST},${IMAGEROOTFS},${SDK_OUTPUT}"
    pseudocontroldir = d.expand(pseudo_control_dir).split(",")
    for i in pseudoignorepaths:
        for j in pseudocontroldir:
            if i and j:
                if j.startswith(i):
                    status.addresult("A path included in PSEUDO_IGNORE_PATHS " + str(i) + " and the path " + str(j) + " overlap and this will break pseudo permission and ownership tracking. Please set the path " + str(j) + " to a different directory which does not overlap with pseudo controlled directories. \n")

    # Some third-party software apparently relies on chmod etc. being suid root (!!)
    import stat
    suid_check_bins = "chown chmod mknod".split()
    for bin_cmd in suid_check_bins:
        bin_path = bb.utils.which(os.environ["PATH"], bin_cmd)
        if bin_path:
            bin_stat = os.stat(bin_path)
            if bin_stat.st_uid == 0 and bin_stat.st_mode & stat.S_ISUID:
                status.addresult('%s has the setuid bit set. This interferes with pseudo and may cause other issues that break the build process.\n' % bin_path)

    # Check that we can fetch from various network transports
    netcheck = check_connectivity(d)
    status.addresult(netcheck)
    if netcheck:
        status.network_error = True

    nolibs = d.getVar('NO32LIBS')
    if not nolibs:
        lib32path = '/lib'
        if os.path.exists('/lib64') and ( os.path.islink('/lib64') or os.path.islink('/lib') ):
           lib32path = '/lib32'

        if os.path.exists('%s/libc.so.6' % lib32path) and not os.path.exists('/usr/include/gnu/stubs-32.h'):
            status.addresult("You have a 32-bit libc, but no 32-bit headers.  You must install the 32-bit libc headers.\n")

    bbpaths = d.getVar('BBPATH').split(":")
    if ("." in bbpaths or "./" in bbpaths or "" in bbpaths):
        status.addresult("BBPATH references the current directory, either through "    \
                "an empty entry, a './' or a '.'.\n\t This is unsafe and means your "\
                "layer configuration is adding empty elements to BBPATH.\n\t "\
                "Please check your layer.conf files and other BBPATH "        \
                "settings to remove the current working directory "           \
                "references.\n" \
                "Parsed BBPATH is" + str(bbpaths));

    oes_bb_conf = d.getVar( 'OES_BITBAKE_CONF')
    if not oes_bb_conf:
        status.addresult('You are not using the OpenEmbedded version of conf/bitbake.conf. This means your environment is misconfigured, in particular check BBPATH.\n')

    # The length of TMPDIR can't be longer than 410
    status.addresult(check_path_length(tmpdir, "TMPDIR", 410))

    # Check that TMPDIR isn't located on nfs
    status.addresult(check_not_nfs(tmpdir, "TMPDIR"))

    # Check for case-insensitive file systems (such as Linux in Docker on
    # macOS with default HFS+ file system)
    status.addresult(check_case_sensitive(tmpdir, "TMPDIR"))

def sanity_check_locale(d):
    """
    Currently bitbake switches locale to en_US.UTF-8 so check that this locale actually exists.
    """
    import locale
    try:
        locale.setlocale(locale.LC_ALL, "en_US.UTF-8")
    except locale.Error:
        raise_sanity_error("Your system needs to support the en_US.UTF-8 locale.", d)

def check_sanity_everybuild(status, d):
    import os, stat
    # Sanity tests which test the users environment so need to run at each build (or are so cheap
    # it makes sense to always run them.

    if 0 == os.getuid():
        raise_sanity_error("Do not use Bitbake as root.", d)

    # Check the Python version, we now have a minimum of Python 3.4
    import sys
    if sys.hexversion < 0x030500F0:
        status.addresult('The system requires at least Python 3.5 to run. Please update your Python interpreter.\n')

    # Check the bitbake version meets minimum requirements
    from distutils.version import LooseVersion
    minversion = d.getVar('BB_MIN_VERSION')
    if (LooseVersion(bb.__version__) < LooseVersion(minversion)):
        status.addresult('Bitbake version %s is required and version %s was found\n' % (minversion, bb.__version__))

    sanity_check_locale(d)

    paths = d.getVar('PATH').split(":")
    if "." in paths or "./" in paths or "" in paths:
        status.addresult("PATH contains '.', './' or '' (empty element), which will break the build, please remove this.\nParsed PATH is " + str(paths) + "\n")

    #Check if bitbake is present in PATH environment variable
    bb_check = bb.utils.which(d.getVar('PATH'), 'bitbake')
    if not bb_check:
        bb.warn("bitbake binary is not found in PATH, did you source the script?")

    # Check whether 'inherit' directive is found (used for a class to inherit)
    # in conf file it's supposed to be uppercase INHERIT
    inherit = d.getVar('inherit')
    if inherit:
        status.addresult("Please don't use inherit directive in your local.conf. The directive is supposed to be used in classes and recipes only to inherit of bbclasses. Here INHERIT should be used.\n")

    # Check that the DISTRO is valid, if set
    # need to take into account DISTRO renaming DISTRO
    distro = d.getVar('DISTRO')
    if distro and distro != "nodistro":
        if not ( check_conf_exists("conf/distro/${DISTRO}.conf", d) or check_conf_exists("conf/distro/include/${DISTRO}.inc", d) ):
            status.addresult("DISTRO '%s' not found. Please set a valid DISTRO in your local.conf\n" % d.getVar("DISTRO"))

    # Check that these variables don't use tilde-expansion as we don't do that
    for v in ("TMPDIR", "DL_DIR", "SSTATE_DIR"):
        if d.getVar(v).startswith("~"):
            status.addresult("%s uses ~ but Bitbake will not expand this, use an absolute path or variables." % v)

    # Check that DL_DIR is set, exists and is writable. In theory, we should never even hit the check if DL_DIR isn't 
    # set, since so much relies on it being set.
    dldir = d.getVar('DL_DIR')
    if not dldir:
        status.addresult("DL_DIR is not set. Your environment is misconfigured, check that DL_DIR is set, and if the directory exists, that it is writable. \n")
    if os.path.exists(dldir) and not os.access(dldir, os.W_OK):
        status.addresult("DL_DIR: %s exists but you do not appear to have write access to it. \n" % dldir)
    check_symlink(dldir, d)

    # Check that the MACHINE is valid, if it is set
    machinevalid = True
    if d.getVar('MACHINE'):
        if not check_conf_exists("conf/machine/${MACHINE}.conf", d):
            status.addresult('MACHINE=%s is invalid. Please set a valid MACHINE in your local.conf, environment or other configuration file.\n' % (d.getVar('MACHINE')))
            machinevalid = False
        else:
            status.addresult(check_sanity_validmachine(d))
    else:
        status.addresult('Please set a MACHINE in your local.conf or environment\n')
        machinevalid = False
    if machinevalid:
        status.addresult(check_toolchain(d))

    # Check that the SDKMACHINE is valid, if it is set
    if d.getVar('SDKMACHINE'):
        if not check_conf_exists("conf/machine-sdk/${SDKMACHINE}.conf", d):
            status.addresult('Specified SDKMACHINE value is not valid\n')
        elif d.getVar('SDK_ARCH', False) == "${BUILD_ARCH}":
            status.addresult('SDKMACHINE is set, but SDK_ARCH has not been changed as a result - SDKMACHINE may have been set too late (e.g. in the distro configuration)\n')

    # If SDK_VENDOR looks like "-my-sdk" then the triples are badly formed so fail early
    sdkvendor = d.getVar("SDK_VENDOR")
    if not (sdkvendor.startswith("-") and sdkvendor.count("-") == 1):
        status.addresult("SDK_VENDOR should be of the form '-foosdk' with a single dash; found '%s'\n" % sdkvendor)

    check_supported_distro(d)

    omask = os.umask(0o022)
    if omask & 0o755:
        status.addresult("Please use a umask which allows a+rx and u+rwx\n")
    os.umask(omask)

    if d.getVar('TARGET_ARCH') == "arm":
        # This path is no longer user-readable in modern (very recent) Linux
        try:
            if os.path.exists("/proc/sys/vm/mmap_min_addr"):
                f = open("/proc/sys/vm/mmap_min_addr", "r")
                try:
                    if (int(f.read().strip()) > 65536):
                        status.addresult("/proc/sys/vm/mmap_min_addr is not <= 65536. This will cause problems with qemu so please fix the value (as root).\n\nTo fix this in later reboots, set vm.mmap_min_addr = 65536 in /etc/sysctl.conf.\n")
                finally:
                    f.close()
        except:
            pass

    oeroot = d.getVar('COREBASE')
    if oeroot.find('+') != -1:
        status.addresult("Error, you have an invalid character (+) in your COREBASE directory path. Please move the installation to a directory which doesn't include any + characters.")
    if oeroot.find('@') != -1:
        status.addresult("Error, you have an invalid character (@) in your COREBASE directory path. Please move the installation to a directory which doesn't include any @ characters.")
    if oeroot.find(' ') != -1:
        status.addresult("Error, you have a space in your COREBASE directory path. Please move the installation to a directory which doesn't include a space since autotools doesn't support this.")

    # Check the format of MIRRORS, PREMIRRORS and SSTATE_MIRRORS
    import re
    mirror_vars = ['MIRRORS', 'PREMIRRORS', 'SSTATE_MIRRORS']
    protocols = ['http', 'ftp', 'file', 'https', \
                 'git', 'gitsm', 'hg', 'osc', 'p4', 'svn', \
                 'bzr', 'cvs', 'npm', 'sftp', 'ssh', 's3' ]
    for mirror_var in mirror_vars:
        mirrors = (d.getVar(mirror_var) or '').replace('\\n', ' ').split()

        # Split into pairs
        if len(mirrors) % 2 != 0:
            bb.warn('Invalid mirror variable value for %s: %s, should contain paired members.' % (mirror_var, str(mirrors)))
            continue
        mirrors = list(zip(*[iter(mirrors)]*2))

        for mirror_entry in mirrors:
            pattern, mirror = mirror_entry

            decoded = bb.fetch2.decodeurl(pattern)
            try:
                pattern_scheme = re.compile(decoded[0])
            except re.error as exc:
                bb.warn('Invalid scheme regex (%s) in %s; %s' % (pattern, mirror_var, mirror_entry))
                continue

            if not any(pattern_scheme.match(protocol) for protocol in protocols):
                bb.warn('Invalid protocol (%s) in %s: %s' % (decoded[0], mirror_var, mirror_entry))
                continue

            if not any(mirror.startswith(protocol + '://') for protocol in protocols):
                bb.warn('Invalid protocol in %s: %s' % (mirror_var, mirror_entry))
                continue

            if mirror.startswith('file://'):
                import urllib
                check_symlink(urllib.parse.urlparse(mirror).path, d)
                # SSTATE_MIRROR ends with a /PATH string
                if mirror.endswith('/PATH'):
                    # remove /PATH$ from SSTATE_MIRROR to get a working
                    # base directory path
                    mirror_base = urllib.parse.urlparse(mirror[:-1*len('/PATH')]).path
                    check_symlink(mirror_base, d)

    # Check that TMPDIR hasn't changed location since the last time we were run
    tmpdir = d.getVar('TMPDIR')
    checkfile = os.path.join(tmpdir, "saved_tmpdir")
    if os.path.exists(checkfile):
        with open(checkfile, "r") as f:
            saved_tmpdir = f.read().strip()
            if (saved_tmpdir != tmpdir):
                status.addresult("Error, TMPDIR has changed location. You need to either move it back to %s or delete it and rebuild\n" % saved_tmpdir)
    else:
        bb.utils.mkdirhier(tmpdir)
        # Remove setuid, setgid and sticky bits from TMPDIR
        try:
            os.chmod(tmpdir, os.stat(tmpdir).st_mode & ~ stat.S_ISUID)
            os.chmod(tmpdir, os.stat(tmpdir).st_mode & ~ stat.S_ISGID)
            os.chmod(tmpdir, os.stat(tmpdir).st_mode & ~ stat.S_ISVTX)
        except OSError as exc:
            bb.warn("Unable to chmod TMPDIR: %s" % exc)
        with open(checkfile, "w") as f:
            f.write(tmpdir)

    # If /bin/sh is a symlink, check that it points to dash or bash
    if os.path.islink('/bin/sh'):
        real_sh = os.path.realpath('/bin/sh')
        # Due to update-alternatives, the shell name may take various
        # forms, such as /bin/dash, bin/bash, /bin/bash.bash ...
        if '/dash' not in real_sh and '/bash' not in real_sh:
            status.addresult("Error, /bin/sh links to %s, must be dash or bash\n" % real_sh)

def check_sanity(sanity_data):
    class SanityStatus(object):
        def __init__(self):
            self.messages = ""
            self.network_error = False

        def addresult(self, message):
            if message:
                self.messages = self.messages + message

    status = SanityStatus()

    tmpdir = sanity_data.getVar('TMPDIR')
    sstate_dir = sanity_data.getVar('SSTATE_DIR')

    check_symlink(sstate_dir, sanity_data)

    # Check saved sanity info
    last_sanity_version = 0
    last_tmpdir = ""
    last_sstate_dir = ""
    last_nativelsbstr = ""
    sanityverfile = sanity_data.expand("${TOPDIR}/cache/sanity_info")
    if os.path.exists(sanityverfile):
        with open(sanityverfile, 'r') as f:
            for line in f:
                if line.startswith('SANITY_VERSION'):
                    last_sanity_version = int(line.split()[1])
                if line.startswith('TMPDIR'):
                    last_tmpdir = line.split()[1]
                if line.startswith('SSTATE_DIR'):
                    last_sstate_dir = line.split()[1]
                if line.startswith('NATIVELSBSTRING'):
                    last_nativelsbstr = line.split()[1]

    check_sanity_everybuild(status, sanity_data)
    
    sanity_version = int(sanity_data.getVar('SANITY_VERSION') or 1)
    network_error = False
    # NATIVELSBSTRING var may have been overridden with "universal", so
    # get actual host distribution id and version
    nativelsbstr = lsb_distro_identifier(sanity_data)
    if last_sanity_version < sanity_version or last_nativelsbstr != nativelsbstr: 
        check_sanity_version_change(status, sanity_data)
        status.addresult(check_sanity_sstate_dir_change(sstate_dir, sanity_data))
    else: 
        if last_sstate_dir != sstate_dir:
            status.addresult(check_sanity_sstate_dir_change(sstate_dir, sanity_data))

    if os.path.exists(os.path.dirname(sanityverfile)) and not status.messages:
        with open(sanityverfile, 'w') as f:
            f.write("SANITY_VERSION %s\n" % sanity_version) 
            f.write("TMPDIR %s\n" % tmpdir) 
            f.write("SSTATE_DIR %s\n" % sstate_dir) 
            f.write("NATIVELSBSTRING %s\n" % nativelsbstr) 

    sanity_handle_abichanges(status, sanity_data)

    if status.messages != "":
        raise_sanity_error(sanity_data.expand(status.messages), sanity_data, status.network_error)

# Create a copy of the datastore and finalise it to ensure appends and 
# overrides are set - the datastore has yet to be finalised at ConfigParsed
def copy_data(e):
    sanity_data = bb.data.createCopy(e.data)
    sanity_data.finalize()
    return sanity_data

addhandler config_reparse_eventhandler
config_reparse_eventhandler[eventmask] = "bb.event.ConfigParsed"
python config_reparse_eventhandler() {
    sanity_check_conffiles(e.data)
}

addhandler check_sanity_eventhandler
check_sanity_eventhandler[eventmask] = "bb.event.SanityCheck bb.event.NetworkTest"
python check_sanity_eventhandler() {
    if bb.event.getName(e) == "SanityCheck":
        sanity_data = copy_data(e)
        check_sanity(sanity_data)
        if e.generateevents:
            sanity_data.setVar("SANITY_USE_EVENTS", "1")
        bb.event.fire(bb.event.SanityCheckPassed(), e.data)
    elif bb.event.getName(e) == "NetworkTest":
        sanity_data = copy_data(e)
        if e.generateevents:
            sanity_data.setVar("SANITY_USE_EVENTS", "1")
        bb.event.fire(bb.event.NetworkTestFailed() if check_connectivity(sanity_data) else bb.event.NetworkTestPassed(), e.data)

    return
}
