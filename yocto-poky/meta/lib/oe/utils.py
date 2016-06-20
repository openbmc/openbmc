try:
    # Python 2
    import commands as cmdstatus
except ImportError:
    # Python 3
    import subprocess as cmdstatus

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
    if d.getVar(variable,1) == checkvalue:
        return truevalue
    else:
        return falsevalue

def less_or_equal(variable, checkvalue, truevalue, falsevalue, d):
    if float(d.getVar(variable,1)) <= float(checkvalue):
        return truevalue
    else:
        return falsevalue

def version_less_or_equal(variable, checkvalue, truevalue, falsevalue, d):
    result = bb.utils.vercmp_string(d.getVar(variable,True), checkvalue)
    if result <= 0:
        return truevalue
    else:
        return falsevalue

def both_contain(variable1, variable2, checkvalue, d):
    val1 = d.getVar(variable1, True)
    val2 = d.getVar(variable2, True)
    val1 = set(val1.split())
    val2 = set(val2.split())
    if isinstance(checkvalue, basestring):
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
    val1 = set(d.getVar(variable1, True).split())
    val2 = set(d.getVar(variable2, True).split())
    return " ".join(val1 & val2)

def prune_suffix(var, suffixes, d):
    # See if var ends with any of the suffixes listed and
    # remove it if found
    for suffix in suffixes:
        if var.endswith(suffix):
            var = var.replace(suffix, "")

    prefix = d.getVar("MLPREFIX", True)
    if prefix and var.startswith(prefix):
        var = var.replace(prefix, "")

    return var

def str_filter(f, str, d):
    from re import match
    return " ".join(filter(lambda x: match(f, x, 0), str.split()))

def str_filter_out(f, str, d):
    from re import match
    return " ".join(filter(lambda x: not match(f, x, 0), str.split()))

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
    features = (d.getVar(var, True) or "").split()
    backfill = (d.getVar(var+"_BACKFILL", True) or "").split()
    considered = (d.getVar(var+"_BACKFILL_CONSIDERED", True) or "").split()

    addfeatures = []
    for feature in backfill:
        if feature not in features and feature not in considered:
            addfeatures.append(feature)

    if addfeatures:
        d.appendVar(var, " " + " ".join(addfeatures))


def packages_filter_out_system(d):
    """
    Return a list of packages from PACKAGES with the "system" packages such as
    PN-dbg PN-doc PN-locale-eb-gb removed.
    """
    pn = d.getVar('PN', True)
    blacklist = map(lambda suffix: pn + suffix, ('', '-dbg', '-dev', '-doc', '-locale', '-staticdev'))
    localepkg = pn + "-locale-"
    pkgs = []

    for pkg in d.getVar('PACKAGES', True).split():
        if pkg not in blacklist and localepkg not in pkg:
            pkgs.append(pkg)
    return pkgs

def getstatusoutput(cmd):
    return cmdstatus.getstatusoutput(cmd)


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

    nproc = min(multiprocessing.cpu_count(), len(commands))
    pool = bb.utils.multiprocessingpool(nproc, init_worker)
    imap = pool.imap(function, commands)

    try:
        res = list(imap)
        pool.close()
        pool.join()
        results = []
        for result in res:
            if result is not None:
                results.append(result)
        return results

    except KeyboardInterrupt:
        pool.terminate()
        pool.join()
        raise

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

#
# Python 2.7 doesn't have threaded pools (just multiprocessing)
# so implement a version here
#

from Queue import Queue
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
        from Queue import Empty

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
            except Exception, e:
                print e
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
        f.write(d.getVar("base_libdir", True) + '\n')
        f.write(d.getVar("libdir", True) + '\n')
