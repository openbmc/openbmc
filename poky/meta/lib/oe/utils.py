#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import subprocess
import multiprocessing
import traceback
import errno

def read_file(filename):
    try:
        f = open( filename, "r" )
    except IOError as reason:
        return "" # WARNING: can't raise an error now because of the new RDEPENDS handling. This is a bit ugly. :M:
    else:
        data = f.read().strip()
        f.close()
        return data
    return None

def ifelse(condition, iftrue = True, iffalse = False):
    if condition:
        return iftrue
    else:
        return iffalse

def conditional(variable, checkvalue, truevalue, falsevalue, d):
    if d.getVar(variable) == checkvalue:
        return truevalue
    else:
        return falsevalue

def vartrue(var, iftrue, iffalse, d):
    import oe.types
    if oe.types.boolean(d.getVar(var)):
        return iftrue
    else:
        return iffalse

def less_or_equal(variable, checkvalue, truevalue, falsevalue, d):
    if float(d.getVar(variable)) <= float(checkvalue):
        return truevalue
    else:
        return falsevalue

def version_less_or_equal(variable, checkvalue, truevalue, falsevalue, d):
    result = bb.utils.vercmp_string(d.getVar(variable), checkvalue)
    if result <= 0:
        return truevalue
    else:
        return falsevalue

def both_contain(variable1, variable2, checkvalue, d):
    val1 = d.getVar(variable1)
    val2 = d.getVar(variable2)
    val1 = set(val1.split())
    val2 = set(val2.split())
    if isinstance(checkvalue, str):
        checkvalue = set(checkvalue.split())
    else:
        checkvalue = set(checkvalue)
    if checkvalue.issubset(val1) and checkvalue.issubset(val2):
        return " ".join(checkvalue)
    else:
        return ""

def set_intersect(variable1, variable2, d):
    """
    Expand both variables, interpret them as lists of strings, and return the
    intersection as a flattened string.

    For example:
    s1 = "a b c"
    s2 = "b c d"
    s3 = set_intersect(s1, s2)
    => s3 = "b c"
    """
    val1 = set(d.getVar(variable1).split())
    val2 = set(d.getVar(variable2).split())
    return " ".join(val1 & val2)

def prune_suffix(var, suffixes, d):
    # See if var ends with any of the suffixes listed and
    # remove it if found
    for suffix in suffixes:
        if suffix and var.endswith(suffix):
            var = var[:-len(suffix)]

    prefix = d.getVar("MLPREFIX")
    if prefix and var.startswith(prefix):
        var = var[len(prefix):]

    return var

def str_filter(f, str, d):
    from re import match
    return " ".join([x for x in str.split() if match(f, x, 0)])

def str_filter_out(f, str, d):
    from re import match
    return " ".join([x for x in str.split() if not match(f, x, 0)])

def build_depends_string(depends, task):
    """Append a taskname to a string of dependencies as used by the [depends] flag"""
    return " ".join(dep + ":" + task for dep in depends.split())

def inherits(d, *classes):
    """Return True if the metadata inherits any of the specified classes"""
    return any(bb.data.inherits_class(cls, d) for cls in classes)

def features_backfill(var,d):
    # This construct allows the addition of new features to variable specified
    # as var
    # Example for var = "DISTRO_FEATURES"
    # This construct allows the addition of new features to DISTRO_FEATURES
    # that if not present would disable existing functionality, without
    # disturbing distributions that have already set DISTRO_FEATURES.
    # Distributions wanting to elide a value in DISTRO_FEATURES_BACKFILL should
    # add the feature to DISTRO_FEATURES_BACKFILL_CONSIDERED
    features = (d.getVar(var) or "").split()
    backfill = (d.getVar(var+"_BACKFILL") or "").split()
    considered = (d.getVar(var+"_BACKFILL_CONSIDERED") or "").split()

    addfeatures = []
    for feature in backfill:
        if feature not in features and feature not in considered:
            addfeatures.append(feature)

    if addfeatures:
        d.appendVar(var, " " + " ".join(addfeatures))

def all_distro_features(d, features, truevalue="1", falsevalue=""):
    """
    Returns truevalue if *all* given features are set in DISTRO_FEATURES,
    else falsevalue. The features can be given as single string or anything
    that can be turned into a set.

    This is a shorter, more flexible version of
    bb.utils.contains("DISTRO_FEATURES", features, truevalue, falsevalue, d).

    Without explicit true/false values it can be used directly where
    Python expects a boolean:
       if oe.utils.all_distro_features(d, "foo bar"):
           bb.fatal("foo and bar are mutually exclusive DISTRO_FEATURES")

    With just a truevalue, it can be used to include files that are meant to be
    used only when requested via DISTRO_FEATURES:
       require ${@ oe.utils.all_distro_features(d, "foo bar", "foo-and-bar.inc")
    """
    return bb.utils.contains("DISTRO_FEATURES", features, truevalue, falsevalue, d)

def any_distro_features(d, features, truevalue="1", falsevalue=""):
    """
    Returns truevalue if at least *one* of the given features is set in DISTRO_FEATURES,
    else falsevalue. The features can be given as single string or anything
    that can be turned into a set.

    This is a shorter, more flexible version of
    bb.utils.contains_any("DISTRO_FEATURES", features, truevalue, falsevalue, d).

    Without explicit true/false values it can be used directly where
    Python expects a boolean:
       if not oe.utils.any_distro_features(d, "foo bar"):
           bb.fatal("foo, bar or both must be set in DISTRO_FEATURES")

    With just a truevalue, it can be used to include files that are meant to be
    used only when requested via DISTRO_FEATURES:
       require ${@ oe.utils.any_distro_features(d, "foo bar", "foo-or-bar.inc")

    """
    return bb.utils.contains_any("DISTRO_FEATURES", features, truevalue, falsevalue, d)

def parallel_make(d, makeinst=False):
    """
    Return the integer value for the number of parallel threads to use when
    building, scraped out of PARALLEL_MAKE. If no parallelization option is
    found, returns None

    e.g. if PARALLEL_MAKE = "-j 10", this will return 10 as an integer.
    """
    if makeinst:
        pm = (d.getVar('PARALLEL_MAKEINST') or '').split()
    else:
        pm = (d.getVar('PARALLEL_MAKE') or '').split()
    # look for '-j' and throw other options (e.g. '-l') away
    while pm:
        opt = pm.pop(0)
        if opt == '-j':
            v = pm.pop(0)
        elif opt.startswith('-j'):
            v = opt[2:].strip()
        else:
            continue

        return int(v)

    return ''

def parallel_make_argument(d, fmt, limit=None, makeinst=False):
    """
    Helper utility to construct a parallel make argument from the number of
    parallel threads specified in PARALLEL_MAKE.

    Returns the input format string `fmt` where a single '%d' will be expanded
    with the number of parallel threads to use. If `limit` is specified, the
    number of parallel threads will be no larger than it. If no parallelization
    option is found in PARALLEL_MAKE, returns an empty string

    e.g. if PARALLEL_MAKE = "-j 10", parallel_make_argument(d, "-n %d") will return
    "-n 10"
    """
    v = parallel_make(d, makeinst)
    if v:
        if limit:
            v = min(limit, v)
        return fmt % v
    return ''

def packages_filter_out_system(d):
    """
    Return a list of packages from PACKAGES with the "system" packages such as
    PN-dbg PN-doc PN-locale-eb-gb removed.
    """
    pn = d.getVar('PN')
    pkgfilter = [pn + suffix for suffix in ('', '-dbg', '-dev', '-doc', '-locale', '-staticdev', '-src')]
    localepkg = pn + "-locale-"
    pkgs = []

    for pkg in d.getVar('PACKAGES').split():
        if pkg not in pkgfilter and localepkg not in pkg:
            pkgs.append(pkg)
    return pkgs

def getstatusoutput(cmd):
    return subprocess.getstatusoutput(cmd)


def trim_version(version, num_parts=2):
    """
    Return just the first <num_parts> of <version>, split by periods.  For
    example, trim_version("1.2.3", 2) will return "1.2".
    """
    if type(version) is not str:
        raise TypeError("Version should be a string")
    if num_parts < 1:
        raise ValueError("Cannot split to parts < 1")

    parts = version.split(".")
    trimmed = ".".join(parts[:num_parts])
    return trimmed

def cpu_count(at_least=1, at_most=64):
    cpus = len(os.sched_getaffinity(0))
    return max(min(cpus, at_most), at_least)

def execute_pre_post_process(d, cmds):
    if cmds is None:
        return

    cmds = cmds.replace(";", " ")

    for cmd in cmds.split():
        bb.note("Executing %s ..." % cmd)
        bb.build.exec_func(cmd, d)

def get_bb_number_threads(d):
    return int(d.getVar("BB_NUMBER_THREADS") or os.cpu_count() or 1)

def multiprocess_launch(target, items, d, extraargs=None):
    max_process = get_bb_number_threads(d)
    return multiprocess_launch_mp(target, items, max_process, extraargs)

# For each item in items, call the function 'target' with item as the first
# argument, extraargs as the other arguments and handle any exceptions in the
# parent thread
def multiprocess_launch_mp(target, items, max_process, extraargs=None):

    class ProcessLaunch(multiprocessing.Process):
        def __init__(self, *args, **kwargs):
            multiprocessing.Process.__init__(self, *args, **kwargs)
            self._pconn, self._cconn = multiprocessing.Pipe()
            self._exception = None
            self._result = None

        def run(self):
            try:
                ret = self._target(*self._args, **self._kwargs)
                self._cconn.send((None, ret))
            except Exception as e:
                tb = traceback.format_exc()
                self._cconn.send((e, tb))

        def update(self):
            if self._pconn.poll():
                (e, tb) = self._pconn.recv()
                if e is not None:
                    self._exception = (e, tb)
                else:
                    self._result = tb

        @property
        def exception(self):
            self.update()
            return self._exception

        @property
        def result(self):
            self.update()
            return self._result

    launched = []
    errors = []
    results = []
    items = list(items)
    while (items and not errors) or launched:
        if not errors and items and len(launched) < max_process:
            args = (items.pop(),)
            if extraargs is not None:
                args = args + extraargs
            p = ProcessLaunch(target=target, args=args)
            p.start()
            launched.append(p)
        for q in launched:
            # Have to manually call update() to avoid deadlocks. The pipe can be full and
            # transfer stalled until we try and read the results object but the subprocess won't exit
            # as it still has data to write (https://bugs.python.org/issue8426)
            q.update()
            # The finished processes are joined when calling is_alive()
            if not q.is_alive():
                if q.exception:
                    errors.append(q.exception)
                if q.result:
                    results.append(q.result)
                launched.remove(q)
    # Paranoia doesn't hurt
    for p in launched:
        p.join()
    if errors:
        msg = ""
        for (e, tb) in errors:
            if isinstance(e, subprocess.CalledProcessError) and e.output:
                msg = msg + str(e) + "\n"
                msg = msg + "Subprocess output:"
                msg = msg + e.output.decode("utf-8", errors="ignore")
            else:
                msg = msg + str(e) + ": " + str(tb) + "\n"
        bb.fatal("Fatal errors occurred in subprocesses:\n%s" % msg)
    return results

def squashspaces(string):
    import re
    return re.sub(r"\s+", " ", string).strip()

def rprovides_map(pkgdata_dir, pkg_dict):
    # Map file -> pkg provider
    rprov_map = {}

    for pkg in pkg_dict:
        path_to_pkgfile = os.path.join(pkgdata_dir, 'runtime-reverse', pkg)
        if not os.path.isfile(path_to_pkgfile):
            continue
        with open(path_to_pkgfile) as f:
            for line in f:
                if line.startswith('RPROVIDES') or line.startswith('FILERPROVIDES'):
                    # List all components provided by pkg.
                    # Exclude version strings, i.e. those starting with (
                    provides = [x for x in line.split()[1:] if not x.startswith('(')]
                    for prov in provides:
                        if prov in rprov_map:
                            rprov_map[prov].append(pkg)
                        else:
                            rprov_map[prov] = [pkg]

    return rprov_map

def format_pkg_list(pkg_dict, ret_format=None, pkgdata_dir=None):
    output = []

    if ret_format == "arch":
        for pkg in sorted(pkg_dict):
            output.append("%s %s" % (pkg, pkg_dict[pkg]["arch"]))
    elif ret_format == "file":
        for pkg in sorted(pkg_dict):
            output.append("%s %s %s" % (pkg, pkg_dict[pkg]["filename"], pkg_dict[pkg]["arch"]))
    elif ret_format == "ver":
        for pkg in sorted(pkg_dict):
            output.append("%s %s %s" % (pkg, pkg_dict[pkg]["arch"], pkg_dict[pkg]["ver"]))
    elif ret_format == "deps":
        rprov_map = rprovides_map(pkgdata_dir, pkg_dict)
        for pkg in sorted(pkg_dict):
            for dep in pkg_dict[pkg]["deps"]:
                if dep in rprov_map:
                    # There could be multiple providers within the image
                    for pkg_provider in rprov_map[dep]:
                        output.append("%s|%s * %s [RPROVIDES]" % (pkg, pkg_provider, dep))
                else:
                    output.append("%s|%s" % (pkg, dep))
    else:
        for pkg in sorted(pkg_dict):
            output.append(pkg)

    output_str = '\n'.join(output)

    if output_str:
        # make sure last line is newline terminated
        output_str += '\n'

    return output_str


# Helper function to get the host compiler version
# Do not assume the compiler is gcc
def get_host_compiler_version(d, taskcontextonly=False):
    import re, subprocess

    if taskcontextonly and d.getVar('BB_WORKERCONTEXT') != '1':
        return

    compiler = d.getVar("BUILD_CC")
    # Get rid of ccache since it is not present when parsing.
    if compiler.startswith('ccache '):
        compiler = compiler[7:]
    try:
        env = os.environ.copy()
        # datastore PATH does not contain session PATH as set by environment-setup-...
        # this breaks the install-buildtools use-case
        # env["PATH"] = d.getVar("PATH")
        output = subprocess.check_output("%s --version" % compiler, \
                    shell=True, env=env, stderr=subprocess.STDOUT).decode("utf-8")
    except subprocess.CalledProcessError as e:
        bb.fatal("Error running %s --version: %s" % (compiler, e.output.decode("utf-8")))

    match = re.match(r".* (\d+\.\d+)\.\d+.*", output.split('\n')[0])
    if not match:
        bb.fatal("Can't get compiler version from %s --version output" % compiler)

    version = match.group(1)
    return compiler, version


def host_gcc_version(d, taskcontextonly=False):
    import re, subprocess

    if taskcontextonly and d.getVar('BB_WORKERCONTEXT') != '1':
        return

    compiler = d.getVar("BUILD_CC")
    # Get rid of ccache since it is not present when parsing.
    if compiler.startswith('ccache '):
        compiler = compiler[7:]
    try:
        env = os.environ.copy()
        env["PATH"] = d.getVar("PATH")
        output = subprocess.check_output("%s --version" % compiler, \
                    shell=True, env=env, stderr=subprocess.STDOUT).decode("utf-8")
    except subprocess.CalledProcessError as e:
        bb.fatal("Error running %s --version: %s" % (compiler, e.output.decode("utf-8")))

    match = re.match(r".* (\d+\.\d+)\.\d+.*", output.split('\n')[0])
    if not match:
        bb.fatal("Can't get compiler version from %s --version output" % compiler)

    version = match.group(1)
    return "-%s" % version if version in ("4.8", "4.9") else ""


def get_multilib_datastore(variant, d):
    localdata = bb.data.createCopy(d)
    if variant:
        overrides = localdata.getVar("OVERRIDES", False) + ":virtclass-multilib-" + variant
        localdata.setVar("OVERRIDES", overrides)
        localdata.setVar("MLPREFIX", variant + "-")
    else:
        origdefault = localdata.getVar("DEFAULTTUNE_MULTILIB_ORIGINAL")
        if origdefault:
            localdata.setVar("DEFAULTTUNE", origdefault)
        overrides = localdata.getVar("OVERRIDES", False).split(":")
        overrides = ":".join([x for x in overrides if not x.startswith("virtclass-multilib-")])
        localdata.setVar("OVERRIDES", overrides)
        localdata.setVar("MLPREFIX", "")
    return localdata

def sh_quote(string):
    import shlex
    return shlex.quote(string)

def directory_size(root, blocksize=4096):
    """
    Calculate the size of the directory, taking into account hard links,
    rounding up every size to multiples of the blocksize.
    """
    def roundup(size):
        """
        Round the size up to the nearest multiple of the block size.
        """
        import math
        return math.ceil(size / blocksize) * blocksize

    def getsize(filename):
        """
        Get the size of the filename, not following symlinks, taking into
        account hard links.
        """
        stat = os.lstat(filename)
        if stat.st_ino not in inodes:
            inodes.add(stat.st_ino)
            return stat.st_size
        else:
            return 0

    inodes = set()
    total = 0
    for root, dirs, files in os.walk(root):
        total += sum(roundup(getsize(os.path.join(root, name))) for name in files)
        total += roundup(getsize(root))
    return total

# Update the mtime of a file, skip if permission/read-only issues
def touch(filename):
    try:
        os.utime(filename, None)
    except PermissionError:
        pass
    except OSError as e:
        # Handle read-only file systems gracefully
        if e.errno != errno.EROFS:
            raise e
