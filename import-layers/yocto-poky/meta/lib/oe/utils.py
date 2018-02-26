import subprocess

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
        if var.endswith(suffix):
            var = var.replace(suffix, "")

    prefix = d.getVar("MLPREFIX")
    if prefix and var.startswith(prefix):
        var = var.replace(prefix, "")

    return var

def str_filter(f, str, d):
    from re import match
    return " ".join([x for x in str.split() if match(f, x, 0)])

def str_filter_out(f, str, d):
    from re import match
    return " ".join([x for x in str.split() if not match(f, x, 0)])

def param_bool(cfg, field, dflt = None):
    """Lookup <field> in <cfg> map and convert it to a boolean; take
    <dflt> when this <field> does not exist"""
    value = cfg.get(field, dflt)
    strvalue = str(value).lower()
    if strvalue in ('yes', 'y', 'true', 't', '1'):
        return True
    elif strvalue in ('no', 'n', 'false', 'f', '0'):
        return False
    raise ValueError("invalid value for boolean parameter '%s': '%s'" % (field, value))

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

def packages_filter_out_system(d):
    """
    Return a list of packages from PACKAGES with the "system" packages such as
    PN-dbg PN-doc PN-locale-eb-gb removed.
    """
    pn = d.getVar('PN')
    blacklist = [pn + suffix for suffix in ('', '-dbg', '-dev', '-doc', '-locale', '-staticdev')]
    localepkg = pn + "-locale-"
    pkgs = []

    for pkg in d.getVar('PACKAGES').split():
        if pkg not in blacklist and localepkg not in pkg:
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

def cpu_count():
    import multiprocessing
    return multiprocessing.cpu_count()

def execute_pre_post_process(d, cmds):
    if cmds is None:
        return

    for cmd in cmds.strip().split(';'):
        cmd = cmd.strip()
        if cmd != '':
            bb.note("Executing %s ..." % cmd)
            bb.build.exec_func(cmd, d)

def multiprocess_exec(commands, function):
    import signal
    import multiprocessing

    if not commands:
        return []

    def init_worker():
        signal.signal(signal.SIGINT, signal.SIG_IGN)

    fails = []

    def failures(res):
        fails.append(res)

    nproc = min(multiprocessing.cpu_count(), len(commands))
    pool = bb.utils.multiprocessingpool(nproc, init_worker)

    try:
        mapresult = pool.map_async(function, commands, error_callback=failures)

        pool.close()
        pool.join()
        results = mapresult.get()
    except KeyboardInterrupt:
        pool.terminate()
        pool.join()
        raise

    if fails:
        raise fails[0]

    return results

def squashspaces(string):
    import re
    return re.sub("\s+", " ", string).strip()

def format_pkg_list(pkg_dict, ret_format=None):
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
        for pkg in sorted(pkg_dict):
            for dep in pkg_dict[pkg]["deps"]:
                output.append("%s|%s" % (pkg, dep))
    else:
        for pkg in sorted(pkg_dict):
            output.append(pkg)

    return '\n'.join(output)

def host_gcc_version(d):
    import re, subprocess

    compiler = d.getVar("BUILD_CC")
    try:
        env = os.environ.copy()
        env["PATH"] = d.getVar("PATH")
        output = subprocess.check_output("%s --version" % compiler, shell=True, env=env).decode("utf-8")
    except subprocess.CalledProcessError as e:
        bb.fatal("Error running %s --version: %s" % (compiler, e.output.decode("utf-8")))

    match = re.match(".* (\d\.\d)\.\d.*", output.split('\n')[0])
    if not match:
        bb.fatal("Can't get compiler version from %s --version output" % compiler)

    version = match.group(1)
    return "-%s" % version if version in ("4.8", "4.9") else ""

#
# Python 2.7 doesn't have threaded pools (just multiprocessing)
# so implement a version here
#

from queue import Queue
from threading import Thread

class ThreadedWorker(Thread):
    """Thread executing tasks from a given tasks queue"""
    def __init__(self, tasks, worker_init, worker_end):
        Thread.__init__(self)
        self.tasks = tasks
        self.daemon = True

        self.worker_init = worker_init
        self.worker_end = worker_end

    def run(self):
        from queue import Empty

        if self.worker_init is not None:
            self.worker_init(self)

        while True:
            try:
                func, args, kargs = self.tasks.get(block=False)
            except Empty:
                if self.worker_end is not None:
                    self.worker_end(self)
                break

            try:
                func(self, *args, **kargs)
            except Exception as e:
                print(e)
            finally:
                self.tasks.task_done()

class ThreadedPool:
    """Pool of threads consuming tasks from a queue"""
    def __init__(self, num_workers, num_tasks, worker_init=None,
            worker_end=None):
        self.tasks = Queue(num_tasks)
        self.workers = []

        for _ in range(num_workers):
            worker = ThreadedWorker(self.tasks, worker_init, worker_end)
            self.workers.append(worker)

    def start(self):
        for worker in self.workers:
            worker.start()

    def add_task(self, func, *args, **kargs):
        """Add a task to the queue"""
        self.tasks.put((func, args, kargs))

    def wait_completion(self):
        """Wait for completion of all the tasks in the queue"""
        self.tasks.join()
        for worker in self.workers:
            worker.join()

def write_ld_so_conf(d):
    # Some utils like prelink may not have the correct target library paths
    # so write an ld.so.conf to help them
    ldsoconf = d.expand("${STAGING_DIR_TARGET}${sysconfdir}/ld.so.conf")
    if os.path.exists(ldsoconf):
        bb.utils.remove(ldsoconf)
    bb.utils.mkdirhier(os.path.dirname(ldsoconf))
    with open(ldsoconf, "w") as f:
        f.write(d.getVar("base_libdir") + '\n')
        f.write(d.getVar("libdir") + '\n')

class ImageQAFailed(bb.build.FuncFailed):
    def __init__(self, description, name=None, logfile=None):
        self.description = description
        self.name = name
        self.logfile=logfile

    def __str__(self):
        msg = 'Function failed: %s' % self.name
        if self.description:
            msg = msg + ' (%s)' % self.description

        return msg
