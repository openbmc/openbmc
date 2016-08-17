#
# Sanity check the users setup for common misconfigurations
#

SANITY_REQUIRED_UTILITIES ?= "patch diffstat makeinfo git bzip2 tar \
    gzip gawk chrpath wget cpio perl file"

def bblayers_conf_file(d):
    return os.path.join(d.getVar('TOPDIR', True), 'conf/bblayers.conf')

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
    current_conf  = d.getVar('CONF_VERSION', True)
    conf_version =  d.getVar('LOCALCONF_VERSION', True)

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
    current_sconf = d.getVar('SCONF_VERSION', True)
    sconf_version = d.getVar('SITE_CONF_VERSION', True)

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

    current_lconf = int(d.getVar('LCONF_VERSION', True))
    lconf_version = int(d.getVar('LAYER_CONF_VERSION', True))

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

        if not status.reparse:
            status.addresult()

    elif current_lconf == 6 and lconf_version > 6:
        # Handle rename of meta-yocto -> meta-poky
        # This marks the start of separate version numbers but code is needed in OE-Core
        # for the migration, one last time.
        layers = d.getVar('BBLAYERS', True).split()
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
    if d.getVar("SANITY_USE_EVENTS", True) == "1":
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
    args_set = (data.getVar("TUNE_%s" % which, True) or "").split()
    args_wanted = (data.getVar("TUNEABI_REQUIRED_%s_tune-%s" % (which, tune), True) or "").split()
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
    bb.data.update_data(localdata)
    bb.debug(2, "Sanity-checking tuning '%s' (%s) features:" % (tune, multilib))
    features = (localdata.getVar("TUNE_FEATURES_tune-%s" % tune, True) or "").split()
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
    whitelist = localdata.getVar("TUNEABI_WHITELIST", True)
    if whitelist:
        tuneabi = localdata.getVar("TUNEABI_tune-%s" % tune, True)
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
    deftune = data.getVar("DEFAULTTUNE", True)
    tune_errors = check_toolchain_tune(data, deftune, 'default')
    if tune_errors:
        tune_error_set.append(tune_errors)

    multilibs = (data.getVar("MULTILIB_VARIANTS", True) or "").split()
    global_multilibs = (data.getVar("MULTILIB_GLOBAL_VARIANTS", True) or "").split()

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
            tune = data.getVar("DEFAULTTUNE_virtclass-multilib-%s" % lib, True)
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
    status, result = oe.utils.getstatusoutput("stat -f -c '%s' %s" % ("%t", path))
    if status == 0:
        return result
    else:
        bb.warn("Can't get the filesystem id of: %s" % path)
        return None

# Check that the path isn't located on nfs.
def check_not_nfs(path, name):
    # The nfs' filesystem id is 6969
    if get_filesystem_id(path) == "6969":
        return "The %s: %s can't be located on nfs.\n" % (name, path)
    return ""

# Check that path isn't a broken symlink
def check_symlink(lnk, data):
    if os.path.islink(lnk) and not os.path.exists(lnk):
        raise_sanity_error("%s is a broken symlink." % lnk, data)

def check_connectivity(d):
    # URI's to check can be set in the CONNECTIVITY_CHECK_URIS variable
    # using the same syntax as for SRC_URI. If the variable is not set
    # the check is skipped
    test_uris = (d.getVar('CONNECTIVITY_CHECK_URIS', True) or "").split()
    retval = ""

    # Only check connectivity if network enabled and the
    # CONNECTIVITY_CHECK_URIS are set
    network_enabled = not d.getVar('BB_NO_NETWORK', True)
    check_enabled = len(test_uris)
    # Take a copy of the data store and unset MIRRORS and PREMIRRORS
    data = bb.data.createCopy(d)
    data.delVar('PREMIRRORS')
    data.delVar('MIRRORS')
    if check_enabled and network_enabled:
        try:
            fetcher = bb.fetch2.Fetch(test_uris, data)
            fetcher.checkstatus()
        except Exception as err:
            # Allow the message to be configured so that users can be
            # pointed to a support mechanism.
            msg = data.getVar('CONNECTIVITY_CHECK_MSG', True) or ""
            if len(msg) == 0:
                msg = "%s. Please ensure your network is configured correctly.\n" % err
            retval = msg

    return retval

def check_supported_distro(sanity_data):
    from fnmatch import fnmatch

    tested_distros = sanity_data.getVar('SANITY_TESTED_DISTROS', True)
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
    if sanity_data.getVar('TUNE_ARCH', True) == 'INVALID':
        messages = messages + 'TUNE_ARCH is unset. Please ensure your MACHINE configuration includes a valid tune configuration file which will set this correctly.\n'

    # Check TARGET_OS is set
    if sanity_data.getVar('TARGET_OS', True) == 'INVALID':
        messages = messages + 'Please set TARGET_OS directly, or choose a MACHINE or DISTRO that does so.\n'

    # Check that we don't have duplicate entries in PACKAGE_ARCHS & that TUNE_PKGARCH is in PACKAGE_ARCHS
    pkgarchs = sanity_data.getVar('PACKAGE_ARCHS', True)
    tunepkg = sanity_data.getVar('TUNE_PKGARCH', True)
    defaulttune = sanity_data.getVar('DEFAULTTUNE', True)
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

# Checks if necessary to add option march to host gcc
def check_gcc_march(sanity_data):
    result = True
    message = ""

    # Check if -march not in BUILD_CFLAGS
    if sanity_data.getVar("BUILD_CFLAGS",True).find("-march") < 0:
        result = False

        # Construct a test file
        f = open("gcc_test.c", "w")
        f.write("int main (){ volatile int atomic = 2; __sync_bool_compare_and_swap (&atomic, 2, 3); return 0; }\n")
        f.close()

        # Check if GCC could work without march
        if not result:
            status,res = oe.utils.getstatusoutput("${BUILD_PREFIX}gcc gcc_test.c -o gcc_test")
            if status == 0:
                result = True;

        if not result:
            status,res = oe.utils.getstatusoutput("${BUILD_PREFIX}gcc -march=native gcc_test.c -o gcc_test")
            if status == 0:
                message = "BUILD_CFLAGS_append = \" -march=native\""
                result = True;

        if not result:
            build_arch = sanity_data.getVar('BUILD_ARCH', True)
            status,res = oe.utils.getstatusoutput("${BUILD_PREFIX}gcc -march=%s gcc_test.c -o gcc_test" % build_arch)
            if status == 0:
                message = "BUILD_CFLAGS_append = \" -march=%s\"" % build_arch
                result = True;

        os.remove("gcc_test.c")
        if os.path.exists("gcc_test"):
            os.remove("gcc_test")

    return (result, message)

# Unpatched versions of make 3.82 are known to be broken.  See GNU Savannah Bug 30612.
# Use a modified reproducer from http://savannah.gnu.org/bugs/?30612 to validate.
def check_make_version(sanity_data):
    from distutils.version import LooseVersion
    status, result = oe.utils.getstatusoutput("make --version")
    if status != 0:
        return "Unable to execute make --version, exit code %s\n" % status
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
        status,result = oe.utils.getstatusoutput("make -f makefile_test")

        os.remove("makefile_test")
        if os.path.exists("makefile_test_a.c"):
            os.remove("makefile_test_a.c")
        if os.path.exists("makefile_test_b.c"):
            os.remove("makefile_test_b.c")
        if os.path.exists("makefile_test.a"):
            os.remove("makefile_test.a")

        if status != 0:
            return "Your version of make 3.82 is broken. Please revert to 3.81 or install a patched version.\n"
    return None


# Tar version 1.24 and onwards handle overwriting symlinks correctly
# but earlier versions do not; this needs to work properly for sstate
def check_tar_version(sanity_data):
    from distutils.version import LooseVersion
    status, result = oe.utils.getstatusoutput("tar --version")
    if status != 0:
        return "Unable to execute tar --version, exit code %s\n" % status
    version = result.split()[3]
    if LooseVersion(version) < LooseVersion("1.24"):
        return "Your version of tar is older than 1.24 and has bugs which will break builds. Please install a newer version of tar.\n"
    return None

# We use git parameters and functionality only found in 1.7.8 or later
# The kernel tools assume git >= 1.8.3.1 (verified needed > 1.7.9.5) see #6162 
# The git fetcher also had workarounds for git < 1.7.9.2 which we've dropped
def check_git_version(sanity_data):
    from distutils.version import LooseVersion
    status, result = oe.utils.getstatusoutput("git --version 2> /dev/null")
    if status != 0:
        return "Unable to execute git --version, exit code %s\n" % status
    version = result.split()[2]
    if LooseVersion(version) < LooseVersion("1.8.3.1"):
        return "Your version of git is older than 1.8.3.1 and has bugs which will break builds. Please install a newer version of git.\n"
    return None

# Check the required perl modules which may not be installed by default
def check_perl_modules(sanity_data):
    ret = ""
    modules = ( "Text::ParseWords", "Thread::Queue", "Data::Dumper" )
    errresult = ''
    for m in modules:
        status, result = oe.utils.getstatusoutput("perl -e 'use %s'" % m)
        if status != 0:
            errresult += result
            ret += "%s " % m
    if ret:
        return "Required perl module(s) not found: %s\n\n%s\n" % (ret, errresult)
    return None

def sanity_check_conffiles(status, d):
    funcs = d.getVar('BBLAYERS_CONF_UPDATE_FUNCS', True).split()
    for func in funcs:
        conffile, current_version, required_version, func = func.split(":")
        if check_conf_exists(conffile, d) and d.getVar(current_version, True) is not None and \
                d.getVar(current_version, True) != d.getVar(required_version, True):
            success = True
            try:
                bb.build.exec_func(func, d, pythonexception=True)
            except NotImplementedError as e:
                success = False
                status.addresult(str(e))
            if success:
                status.reparse = True

def sanity_handle_abichanges(status, d):
    #
    # Check the 'ABI' of TMPDIR
    #
    import subprocess

    current_abi = d.getVar('OELAYOUT_ABI', True)
    abifile = d.getVar('SANITY_ABIFILE', True)
    if os.path.exists(abifile):
        with open(abifile, "r") as f:
            abi = f.read().strip()
        if not abi.isdigit():
            with open(abifile, "w") as f:
                f.write(current_abi)
        elif abi == "2" and current_abi == "3":
            bb.note("Converting staging from layout version 2 to layout version 3")
            subprocess.call(d.expand("mv ${TMPDIR}/staging ${TMPDIR}/sysroots"), shell=True)
            subprocess.call(d.expand("ln -s sysroots ${TMPDIR}/staging"), shell=True)
            subprocess.call(d.expand("cd ${TMPDIR}/stamps; for i in */*do_populate_staging; do new=`echo $i | sed -e 's/do_populate_staging/do_populate_sysroot/'`; mv $i $new; done"), shell=True)
            with open(abifile, "w") as f:
                f.write(current_abi)
        elif abi == "3" and current_abi == "4":
            bb.note("Converting staging layout from version 3 to layout version 4")
            if os.path.exists(d.expand("${STAGING_DIR_NATIVE}${bindir_native}/${MULTIMACH_HOST_SYS}")):
                subprocess.call(d.expand("mv ${STAGING_DIR_NATIVE}${bindir_native}/${MULTIMACH_HOST_SYS} ${STAGING_BINDIR_CROSS}"), shell=True)
                subprocess.call(d.expand("ln -s ${STAGING_BINDIR_CROSS} ${STAGING_DIR_NATIVE}${bindir_native}/${MULTIMACH_HOST_SYS}"), shell=True)
            with open(abifile, "w") as f:
                f.write(current_abi)
        elif abi == "4":
            status.addresult("Staging layout has changed. The cross directory has been deprecated and cross packages are now built under the native sysroot.\nThis requires a rebuild.\n")
        elif abi == "5" and current_abi == "6":
            bb.note("Converting staging layout from version 5 to layout version 6")
            subprocess.call(d.expand("mv ${TMPDIR}/pstagelogs ${SSTATE_MANIFESTS}"), shell=True)
            with open(abifile, "w") as f:
                f.write(current_abi)
        elif abi == "7" and current_abi == "8":
            status.addresult("Your configuration is using stamp files including the sstate hash but your build directory was built with stamp files that do not include this.\nTo continue, either rebuild or switch back to the OEBasic signature handler with BB_SIGNATURE_HANDLER = 'OEBasic'.\n")
        elif (abi != current_abi and current_abi == "9"):
            status.addresult("The layout of the TMPDIR STAMPS directory has changed. Please clean out TMPDIR and rebuild (sstate will be still be valid and reused)\n")
        elif (abi != current_abi and current_abi == "10" and (abi == "8" or abi == "9")):
            bb.note("Converting staging layout from version 8/9 to layout version 10")
            cmd = d.expand("grep -r -l sysroot-providers/virtual_kernel ${SSTATE_MANIFESTS}")
            ret, result = oe.utils.getstatusoutput(cmd)
            result = result.split()
            for f in result:
                bb.note("Uninstalling manifest file %s" % f)
                sstate_clean_manifest(f, d)
            with open(abifile, "w") as f:
                f.write(current_abi)
        elif abi == "10" and current_abi == "11":
            bb.note("Converting staging layout from version 10 to layout version 11")
            # Files in xf86-video-modesetting moved to xserver-xorg and bitbake can't currently handle that:
            subprocess.call(d.expand("rm ${TMPDIR}/sysroots/*/usr/lib/xorg/modules/drivers/modesetting_drv.so ${TMPDIR}/sysroots/*/pkgdata/runtime/xf86-video-modesetting* ${TMPDIR}/sysroots/*/pkgdata/runtime-reverse/xf86-video-modesetting* ${TMPDIR}/sysroots/*/pkgdata/shlibs2/xf86-video-modesetting*"), shell=True)
            with open(abifile, "w") as f:
                f.write(current_abi)
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

    # Check the python install is complete. glib-2.0-natives requries
    # xml.parsers.expat
    try:
        import xml.parsers.expat
    except ImportError:
        status.addresult('Your python is not a full install. Please install the module xml.parsers.expat (python-xml on openSUSE and SUSE Linux).\n')
    import stat

    status.addresult(check_make_version(d))
    status.addresult(check_tar_version(d))
    status.addresult(check_git_version(d))
    status.addresult(check_perl_modules(d))

    missing = ""

    if not check_app_exists("${MAKE}", d):
        missing = missing + "GNU make,"

    if not check_app_exists('${BUILD_PREFIX}gcc', d):
        missing = missing + "C Compiler (%sgcc)," % d.getVar("BUILD_PREFIX", True)

    if not check_app_exists('${BUILD_PREFIX}g++', d):
        missing = missing + "C++ Compiler (%sg++)," % d.getVar("BUILD_PREFIX", True)

    required_utilities = d.getVar('SANITY_REQUIRED_UTILITIES', True)

    for util in required_utilities.split():
        if not check_app_exists(util, d):
            missing = missing + "%s," % util

    if missing:
        missing = missing.rstrip(',')
        status.addresult("Please install the following missing utilities: %s\n" % missing)

    assume_provided = d.getVar('ASSUME_PROVIDED', True).split()
    # Check user doesn't have ASSUME_PROVIDED = instead of += in local.conf
    if "diffstat-native" not in assume_provided:
        status.addresult('Please use ASSUME_PROVIDED +=, not ASSUME_PROVIDED = in your local.conf\n')

    if "qemu-native" in assume_provided:
        if not check_app_exists("qemu-arm", d):
            status.addresult("qemu-native was in ASSUME_PROVIDED but the QEMU binaries (qemu-arm) can't be found in PATH")

    if "libsdl-native" in assume_provided:
        if not check_app_exists("sdl-config", d):
            status.addresult("libsdl-native is set to be ASSUME_PROVIDED but sdl-config can't be found in PATH. Please either install it, or configure qemu not to require sdl.")

    (result, message) = check_gcc_march(d)
    if result and message:
        status.addresult("Your gcc version is older than 4.5, please add the following param to local.conf\n \
        %s\n" % message)
    if not result:
        status.addresult("Your gcc version is older than 4.5 or is not working properly.  Please verify you can build")
        status.addresult(" and link something that uses atomic operations, such as: \n")
        status.addresult("        __sync_bool_compare_and_swap (&atomic, 2, 3);\n")

    # Check that TMPDIR isn't on a filesystem with limited filename length (eg. eCryptFS)
    tmpdir = d.getVar('TMPDIR', True)
    status.addresult(check_create_long_filename(tmpdir, "TMPDIR"))
    tmpdirmode = os.stat(tmpdir).st_mode
    if (tmpdirmode & stat.S_ISGID):
        status.addresult("TMPDIR is setgid, please don't build in a setgid directory")
    if (tmpdirmode & stat.S_ISUID):
        status.addresult("TMPDIR is setuid, please don't build in a setuid directory")

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

    nolibs = d.getVar('NO32LIBS', True)
    if not nolibs:
        lib32path = '/lib'
        if os.path.exists('/lib64') and ( os.path.islink('/lib64') or os.path.islink('/lib') ):
           lib32path = '/lib32'

        if os.path.exists('%s/libc.so.6' % lib32path) and not os.path.exists('/usr/include/gnu/stubs-32.h'):
            status.addresult("You have a 32-bit libc, but no 32-bit headers.  You must install the 32-bit libc headers.\n")

    bbpaths = d.getVar('BBPATH', True).split(":")
    if ("." in bbpaths or "./" in bbpaths or "" in bbpaths) and not status.reparse:
        status.addresult("BBPATH references the current directory, either through "    \
                "an empty entry, a './' or a '.'.\n\t This is unsafe and means your "\
                "layer configuration is adding empty elements to BBPATH.\n\t "\
                "Please check your layer.conf files and other BBPATH "        \
                "settings to remove the current working directory "           \
                "references.\n" \
                "Parsed BBPATH is" + str(bbpaths));

    oes_bb_conf = d.getVar( 'OES_BITBAKE_CONF', True)
    if not oes_bb_conf:
        status.addresult('You are not using the OpenEmbedded version of conf/bitbake.conf. This means your environment is misconfigured, in particular check BBPATH.\n')

    # The length of TMPDIR can't be longer than 410
    status.addresult(check_path_length(tmpdir, "TMPDIR", 410))

    # Check that TMPDIR isn't located on nfs
    status.addresult(check_not_nfs(tmpdir, "TMPDIR"))

def check_sanity_everybuild(status, d):
    import os, stat
    # Sanity tests which test the users environment so need to run at each build (or are so cheap
    # it makes sense to always run them.

    if 0 == os.getuid():
        raise_sanity_error("Do not use Bitbake as root.", d)

    # Check the Python version, we now have a minimum of Python 2.7.3
    import sys
    if sys.hexversion < 0x020703F0:
        status.addresult('The system requires at least Python 2.7.3 to run. Please update your Python interpreter.\n')

    # Check the bitbake version meets minimum requirements
    from distutils.version import LooseVersion
    minversion = d.getVar('BB_MIN_VERSION', True)
    if (LooseVersion(bb.__version__) < LooseVersion(minversion)):
        status.addresult('Bitbake version %s is required and version %s was found\n' % (minversion, bb.__version__))

    sanity_check_conffiles(status, d)

    paths = d.getVar('PATH', True).split(":")
    if "." in paths or "./" in paths or "" in paths:
        status.addresult("PATH contains '.', './' or '' (empty element), which will break the build, please remove this.\nParsed PATH is " + str(paths) + "\n")

    # Check that the DISTRO is valid, if set
    # need to take into account DISTRO renaming DISTRO
    distro = d.getVar('DISTRO', True)
    if distro and distro != "nodistro":
        if not ( check_conf_exists("conf/distro/${DISTRO}.conf", d) or check_conf_exists("conf/distro/include/${DISTRO}.inc", d) ):
            status.addresult("DISTRO '%s' not found. Please set a valid DISTRO in your local.conf\n" % d.getVar("DISTRO", True))

    # Check that DL_DIR is set, exists and is writable. In theory, we should never even hit the check if DL_DIR isn't 
    # set, since so much relies on it being set.
    dldir = d.getVar('DL_DIR', True)
    if not dldir:
        status.addresult("DL_DIR is not set. Your environment is misconfigured, check that DL_DIR is set, and if the directory exists, that it is writable. \n")
    if os.path.exists(dldir) and not os.access(dldir, os.W_OK):
        status.addresult("DL_DIR: %s exists but you do not appear to have write access to it. \n" % dldir)
    check_symlink(dldir, d)

    # Check that the MACHINE is valid, if it is set
    machinevalid = True
    if d.getVar('MACHINE', True):
        if not check_conf_exists("conf/machine/${MACHINE}.conf", d):
            status.addresult('Please set a valid MACHINE in your local.conf or environment\n')
            machinevalid = False
        else:
            status.addresult(check_sanity_validmachine(d))
    else:
        status.addresult('Please set a MACHINE in your local.conf or environment\n')
        machinevalid = False
    if machinevalid:
        status.addresult(check_toolchain(d))

    # Check that the SDKMACHINE is valid, if it is set
    if d.getVar('SDKMACHINE', True):
        if not check_conf_exists("conf/machine-sdk/${SDKMACHINE}.conf", d):
            status.addresult('Specified SDKMACHINE value is not valid\n')
        elif d.getVar('SDK_ARCH', False) == "${BUILD_ARCH}":
            status.addresult('SDKMACHINE is set, but SDK_ARCH has not been changed as a result - SDKMACHINE may have been set too late (e.g. in the distro configuration)\n')

    check_supported_distro(d)

    omask = os.umask(022)
    if omask & 0755:
        status.addresult("Please use a umask which allows a+rx and u+rwx\n")
    os.umask(omask)

    if d.getVar('TARGET_ARCH', True) == "arm":
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

    oeroot = d.getVar('COREBASE', True)
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
                 'bzr', 'cvs', 'npm', 'sftp', 'ssh']
    for mirror_var in mirror_vars:
        mirrors = (d.getVar(mirror_var, True) or '').replace('\\n', '\n').split('\n')
        for mirror_entry in mirrors:
            mirror_entry = mirror_entry.strip()
            if not mirror_entry:
                # ignore blank lines
                continue

            try:
                pattern, mirror = mirror_entry.split()
            except ValueError:
                bb.warn('Invalid %s: %s, should be 2 members.' % (mirror_var, mirror_entry.strip()))
                continue

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
                import urlparse
                check_symlink(urlparse.urlparse(mirror).path, d)
                # SSTATE_MIRROR ends with a /PATH string
                if mirror.endswith('/PATH'):
                    # remove /PATH$ from SSTATE_MIRROR to get a working
                    # base directory path
                    mirror_base = urlparse.urlparse(mirror[:-1*len('/PATH')]).path
                    check_symlink(mirror_base, d)

    # Check that TMPDIR hasn't changed location since the last time we were run
    tmpdir = d.getVar('TMPDIR', True)
    checkfile = os.path.join(tmpdir, "saved_tmpdir")
    if os.path.exists(checkfile):
        with open(checkfile, "r") as f:
            saved_tmpdir = f.read().strip()
            if (saved_tmpdir != tmpdir):
                status.addresult("Error, TMPDIR has changed location. You need to either move it back to %s or rebuild\n" % saved_tmpdir)
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

    # Check /bin/sh links to dash or bash
    real_sh = os.path.realpath('/bin/sh')
    if not real_sh.endswith('/dash') and not real_sh.endswith('/bash'):
        status.addresult("Error, /bin/sh links to %s, must be dash or bash\n" % real_sh)

def check_sanity(sanity_data):
    class SanityStatus(object):
        def __init__(self):
            self.messages = ""
            self.network_error = False
            self.reparse = False

        def addresult(self, message):
            if message:
                self.messages = self.messages + message

    status = SanityStatus()

    tmpdir = sanity_data.getVar('TMPDIR', True)
    sstate_dir = sanity_data.getVar('SSTATE_DIR', True)

    check_symlink(sstate_dir, sanity_data)

    # Check saved sanity info
    last_sanity_version = 0
    last_tmpdir = ""
    last_sstate_dir = ""
    last_nativelsbstr = ""
    sanityverfile = sanity_data.expand("${TOPDIR}/conf/sanity_info")
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
    
    sanity_version = int(sanity_data.getVar('SANITY_VERSION', True) or 1)
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
    return status.reparse

# Create a copy of the datastore and finalise it to ensure appends and 
# overrides are set - the datastore has yet to be finalised at ConfigParsed
def copy_data(e):
    sanity_data = bb.data.createCopy(e.data)
    sanity_data.finalize()
    return sanity_data

addhandler check_sanity_eventhandler
check_sanity_eventhandler[eventmask] = "bb.event.SanityCheck bb.event.NetworkTest"
python check_sanity_eventhandler() {
    if bb.event.getName(e) == "SanityCheck":
        sanity_data = copy_data(e)
        if e.generateevents:
            sanity_data.setVar("SANITY_USE_EVENTS", "1")
        reparse = check_sanity(sanity_data)
        e.data.setVar("BB_INVALIDCONF", reparse)
        bb.event.fire(bb.event.SanityCheckPassed(), e.data)
    elif bb.event.getName(e) == "NetworkTest":
        sanity_data = copy_data(e)
        if e.generateevents:
            sanity_data.setVar("SANITY_USE_EVENTS", "1")
        bb.event.fire(bb.event.NetworkTestFailed() if check_connectivity(sanity_data) else bb.event.NetworkTestPassed(), e.data)

    return
}
