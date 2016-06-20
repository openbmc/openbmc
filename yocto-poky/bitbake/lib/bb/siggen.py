import hashlib
import logging
import os
import re
import tempfile
import bb.data
from bb.checksum import FileChecksumCache

logger = logging.getLogger('BitBake.SigGen')

try:
    import cPickle as pickle
except ImportError:
    import pickle
    logger.info('Importing cPickle failed.  Falling back to a very slow implementation.')

def init(d):
    siggens = [obj for obj in globals().itervalues()
                      if type(obj) is type and issubclass(obj, SignatureGenerator)]

    desired = d.getVar("BB_SIGNATURE_HANDLER", True) or "noop"
    for sg in siggens:
        if desired == sg.name:
            return sg(d)
            break
    else:
        logger.error("Invalid signature generator '%s', using default 'noop'\n"
                     "Available generators: %s", desired,
                     ', '.join(obj.name for obj in siggens))
        return SignatureGenerator(d)

class SignatureGenerator(object):
    """
    """
    name = "noop"

    def __init__(self, data):
        self.taskhash = {}
        self.runtaskdeps = {}
        self.file_checksum_values = {}
        self.taints = {}

    def finalise(self, fn, d, varient):
        return

    def get_taskhash(self, fn, task, deps, dataCache):
        return "0"

    def writeout_file_checksum_cache(self):
        """Write/update the file checksum cache onto disk"""
        return

    def stampfile(self, stampbase, file_name, taskname, extrainfo):
        return ("%s.%s.%s" % (stampbase, taskname, extrainfo)).rstrip('.')

    def stampcleanmask(self, stampbase, file_name, taskname, extrainfo):
        return ("%s.%s.%s" % (stampbase, taskname, extrainfo)).rstrip('.')

    def dump_sigtask(self, fn, task, stampbase, runtime):
        return

    def invalidate_task(self, task, d, fn):
        bb.build.del_stamp(task, d, fn)

    def dump_sigs(self, dataCache, options):
        return

    def get_taskdata(self):
       return (self.runtaskdeps, self.taskhash, self.file_checksum_values, self.taints)

    def set_taskdata(self, data):
        self.runtaskdeps, self.taskhash, self.file_checksum_values, self.taints = data


class SignatureGeneratorBasic(SignatureGenerator):
    """
    """
    name = "basic"

    def __init__(self, data):
        self.basehash = {}
        self.taskhash = {}
        self.taskdeps = {}
        self.runtaskdeps = {}
        self.file_checksum_values = {}
        self.taints = {}
        self.gendeps = {}
        self.lookupcache = {}
        self.pkgnameextract = re.compile("(?P<fn>.*)\..*")
        self.basewhitelist = set((data.getVar("BB_HASHBASE_WHITELIST", True) or "").split())
        self.taskwhitelist = None
        self.init_rundepcheck(data)
        checksum_cache_file = data.getVar("BB_HASH_CHECKSUM_CACHE_FILE", True)
        if checksum_cache_file:
            self.checksum_cache = FileChecksumCache()
            self.checksum_cache.init_cache(data, checksum_cache_file)
        else:
            self.checksum_cache = None

    def init_rundepcheck(self, data):
        self.taskwhitelist = data.getVar("BB_HASHTASK_WHITELIST", True) or None
        if self.taskwhitelist:
            self.twl = re.compile(self.taskwhitelist)
        else:
            self.twl = None

    def _build_data(self, fn, d):

        tasklist, gendeps, lookupcache = bb.data.generate_dependencies(d)

        taskdeps = {}
        basehash = {}

        for task in tasklist:
            data = lookupcache[task]

            if data is None:
                bb.error("Task %s from %s seems to be empty?!" % (task, fn))
                data = ''

            gendeps[task] -= self.basewhitelist
            newdeps = gendeps[task]
            seen = set()
            while newdeps:
                nextdeps = newdeps
                seen |= nextdeps
                newdeps = set()
                for dep in nextdeps:
                    if dep in self.basewhitelist:
                        continue
                    gendeps[dep] -= self.basewhitelist
                    newdeps |= gendeps[dep]
                newdeps -= seen

            alldeps = sorted(seen)
            for dep in alldeps:
                data = data + dep
                var = lookupcache[dep]
                if var is not None:
                    data = data + str(var)
            self.basehash[fn + "." + task] = hashlib.md5(data).hexdigest()
            taskdeps[task] = alldeps

        self.taskdeps[fn] = taskdeps
        self.gendeps[fn] = gendeps
        self.lookupcache[fn] = lookupcache

        return taskdeps

    def finalise(self, fn, d, variant):

        if variant:
            fn = "virtual:" + variant + ":" + fn

        try:
            taskdeps = self._build_data(fn, d)
        except:
            bb.warn("Error during finalise of %s" % fn)
            raise

        #Slow but can be useful for debugging mismatched basehashes
        #for task in self.taskdeps[fn]:
        #    self.dump_sigtask(fn, task, d.getVar("STAMP", True), False)

        for task in taskdeps:
            d.setVar("BB_BASEHASH_task-%s" % task, self.basehash[fn + "." + task])

    def rundep_check(self, fn, recipename, task, dep, depname, dataCache):
        # Return True if we should keep the dependency, False to drop it
        # We only manipulate the dependencies for packages not in the whitelist
        if self.twl and not self.twl.search(recipename):
            # then process the actual dependencies
            if self.twl.search(depname):
                return False
        return True

    def read_taint(self, fn, task, stampbase):
        taint = None
        try:
            with open(stampbase + '.' + task + '.taint', 'r') as taintf:
                taint = taintf.read()
        except IOError:
            pass
        return taint

    def get_taskhash(self, fn, task, deps, dataCache):
        k = fn + "." + task
        data = dataCache.basetaskhash[k]
        self.runtaskdeps[k] = []
        self.file_checksum_values[k] = []
        recipename = dataCache.pkg_fn[fn]

        for dep in sorted(deps, key=clean_basepath):
            depname = dataCache.pkg_fn[self.pkgnameextract.search(dep).group('fn')]
            if not self.rundep_check(fn, recipename, task, dep, depname, dataCache):
                continue
            if dep not in self.taskhash:
                bb.fatal("%s is not in taskhash, caller isn't calling in dependency order?", dep)
            data = data + self.taskhash[dep]
            self.runtaskdeps[k].append(dep)

        if task in dataCache.file_checksums[fn]:
            if self.checksum_cache:
                checksums = self.checksum_cache.get_checksums(dataCache.file_checksums[fn][task], recipename)
            else:
                checksums = bb.fetch2.get_file_checksums(dataCache.file_checksums[fn][task], recipename)
            for (f,cs) in checksums:
                self.file_checksum_values[k].append((f,cs))
                if cs:
                    data = data + cs

        taskdep = dataCache.task_deps[fn]
        if 'nostamp' in taskdep and task in taskdep['nostamp']:
            # Nostamp tasks need an implicit taint so that they force any dependent tasks to run
            import uuid
            taint = str(uuid.uuid4())
            data = data + taint
            self.taints[k] = "nostamp:" + taint

        taint = self.read_taint(fn, task, dataCache.stamp[fn])
        if taint:
            data = data + taint
            self.taints[k] = taint
            logger.warn("%s is tainted from a forced run" % k)

        h = hashlib.md5(data).hexdigest()
        self.taskhash[k] = h
        #d.setVar("BB_TASKHASH_task-%s" % task, taskhash[task])
        return h

    def writeout_file_checksum_cache(self):
        """Write/update the file checksum cache onto disk"""
        if self.checksum_cache:
            self.checksum_cache.save_extras()
            self.checksum_cache.save_merge()
        else:
            bb.fetch2.fetcher_parse_save()
            bb.fetch2.fetcher_parse_done()

    def dump_sigtask(self, fn, task, stampbase, runtime):

        k = fn + "." + task
        referencestamp = stampbase
        if isinstance(runtime, str) and runtime.startswith("customfile"):
            sigfile = stampbase
            referencestamp = runtime[11:]
        elif runtime and k in self.taskhash:
            sigfile = stampbase + "." + task + ".sigdata" + "." + self.taskhash[k]
        else:
            sigfile = stampbase + "." + task + ".sigbasedata" + "." + self.basehash[k]

        bb.utils.mkdirhier(os.path.dirname(sigfile))

        data = {}
        data['task'] = task
        data['basewhitelist'] = self.basewhitelist
        data['taskwhitelist'] = self.taskwhitelist
        data['taskdeps'] = self.taskdeps[fn][task]
        data['basehash'] = self.basehash[k]
        data['gendeps'] = {}
        data['varvals'] = {}
        data['varvals'][task] = self.lookupcache[fn][task]
        for dep in self.taskdeps[fn][task]:
            if dep in self.basewhitelist:
                continue
            data['gendeps'][dep] = self.gendeps[fn][dep]
            data['varvals'][dep] = self.lookupcache[fn][dep]

        if runtime and k in self.taskhash:
            data['runtaskdeps'] = self.runtaskdeps[k]
            data['file_checksum_values'] = [(os.path.basename(f), cs) for f,cs in self.file_checksum_values[k]]
            data['runtaskhashes'] = {}
            for dep in data['runtaskdeps']:
                data['runtaskhashes'][dep] = self.taskhash[dep]
            data['taskhash'] = self.taskhash[k]

        taint = self.read_taint(fn, task, referencestamp)
        if taint:
            data['taint'] = taint

        if runtime and k in self.taints:
            if 'nostamp:' in self.taints[k]:
                data['taint'] = self.taints[k]

        fd, tmpfile = tempfile.mkstemp(dir=os.path.dirname(sigfile), prefix="sigtask.")
        try:
            with os.fdopen(fd, "wb") as stream:
                p = pickle.dump(data, stream, -1)
                stream.flush()
            os.chmod(tmpfile, 0664)
            os.rename(tmpfile, sigfile)
        except (OSError, IOError) as err:
            try:
                os.unlink(tmpfile)
            except OSError:
                pass
            raise err

        computed_basehash = calc_basehash(data)
        if computed_basehash != self.basehash[k]:
            bb.error("Basehash mismatch %s verses %s for %s" % (computed_basehash, self.basehash[k], k))
        if k in self.taskhash:
            computed_taskhash = calc_taskhash(data)
            if computed_taskhash != self.taskhash[k]:
                bb.error("Taskhash mismatch %s verses %s for %s" % (computed_taskhash, self.taskhash[k], k))


    def dump_sigs(self, dataCache, options):
        for fn in self.taskdeps:
            for task in self.taskdeps[fn]:
                k = fn + "." + task
                if k not in self.taskhash:
                    continue
                if dataCache.basetaskhash[k] != self.basehash[k]:
                    bb.error("Bitbake's cached basehash does not match the one we just generated (%s)!" % k)
                    bb.error("The mismatched hashes were %s and %s" % (dataCache.basetaskhash[k], self.basehash[k]))
                self.dump_sigtask(fn, task, dataCache.stamp[fn], True)

class SignatureGeneratorBasicHash(SignatureGeneratorBasic):
    name = "basichash"

    def stampfile(self, stampbase, fn, taskname, extrainfo, clean=False):
        if taskname != "do_setscene" and taskname.endswith("_setscene"):
            k = fn + "." + taskname[:-9]
        else:
            k = fn + "." + taskname
        if clean:
            h = "*"
        elif k in self.taskhash:
            h = self.taskhash[k]
        else:
            # If k is not in basehash, then error
            h = self.basehash[k]
        return ("%s.%s.%s.%s" % (stampbase, taskname, h, extrainfo)).rstrip('.')

    def stampcleanmask(self, stampbase, fn, taskname, extrainfo):
        return self.stampfile(stampbase, fn, taskname, extrainfo, clean=True)
        
    def invalidate_task(self, task, d, fn):
        bb.note("Tainting hash to force rebuild of task %s, %s" % (fn, task))
        bb.build.write_taint(task, d, fn)

def dump_this_task(outfile, d):
    import bb.parse
    fn = d.getVar("BB_FILENAME", True)
    task = "do_" + d.getVar("BB_CURRENTTASK", True)
    referencestamp = bb.build.stamp_internal(task, d, None, True)
    bb.parse.siggen.dump_sigtask(fn, task, outfile, "customfile:" + referencestamp)

def clean_basepath(a):
    b = a.rsplit("/", 2)[1] + a.rsplit("/", 2)[2]
    if a.startswith("virtual:"):
        b = b + ":" + a.rsplit(":", 1)[0]
    return b

def clean_basepaths(a):
    b = {}
    for x in a:
        b[clean_basepath(x)] = a[x]
    return b

def clean_basepaths_list(a):
    b = []
    for x in a:
        b.append(clean_basepath(x))
    return b

def compare_sigfiles(a, b, recursecb = None):
    output = []

    p1 = pickle.Unpickler(open(a, "rb"))
    a_data = p1.load()
    p2 = pickle.Unpickler(open(b, "rb"))
    b_data = p2.load()

    def dict_diff(a, b, whitelist=set()):
        sa = set(a.keys())
        sb = set(b.keys())
        common = sa & sb
        changed = set()
        for i in common:
            if a[i] != b[i] and i not in whitelist:
                changed.add(i)
        added = sb - sa
        removed = sa - sb
        return changed, added, removed

    def file_checksums_diff(a, b):
        from collections import Counter
        # Handle old siginfo format
        if isinstance(a, dict):
            a = [(os.path.basename(f), cs) for f, cs in a.items()]
        if isinstance(b, dict):
            b = [(os.path.basename(f), cs) for f, cs in b.items()]
        # Compare lists, ensuring we can handle duplicate filenames if they exist
        removedcount = Counter(a)
        removedcount.subtract(b)
        addedcount = Counter(b)
        addedcount.subtract(a)
        added = []
        for x in b:
            if addedcount[x] > 0:
                addedcount[x] -= 1
                added.append(x)
        removed = []
        changed = []
        for x in a:
            if removedcount[x] > 0:
                removedcount[x] -= 1
                for y in added:
                    if y[0] == x[0]:
                        changed.append((x[0], x[1], y[1]))
                        added.remove(y)
                        break
                else:
                    removed.append(x)
        added = [x[0] for x in added]
        removed = [x[0] for x in removed]
        return changed, added, removed

    if 'basewhitelist' in a_data and a_data['basewhitelist'] != b_data['basewhitelist']:
        output.append("basewhitelist changed from '%s' to '%s'" % (a_data['basewhitelist'], b_data['basewhitelist']))
        if a_data['basewhitelist'] and b_data['basewhitelist']:
            output.append("changed items: %s" % a_data['basewhitelist'].symmetric_difference(b_data['basewhitelist']))

    if 'taskwhitelist' in a_data and a_data['taskwhitelist'] != b_data['taskwhitelist']:
        output.append("taskwhitelist changed from '%s' to '%s'" % (a_data['taskwhitelist'], b_data['taskwhitelist']))
        if a_data['taskwhitelist'] and b_data['taskwhitelist']:
            output.append("changed items: %s" % a_data['taskwhitelist'].symmetric_difference(b_data['taskwhitelist']))

    if a_data['taskdeps'] != b_data['taskdeps']:
        output.append("Task dependencies changed from:\n%s\nto:\n%s" % (sorted(a_data['taskdeps']), sorted(b_data['taskdeps'])))

    if a_data['basehash'] != b_data['basehash']:
        output.append("basehash changed from %s to %s" % (a_data['basehash'], b_data['basehash']))

    changed, added, removed = dict_diff(a_data['gendeps'], b_data['gendeps'], a_data['basewhitelist'] & b_data['basewhitelist'])
    if changed:
        for dep in changed:
            output.append("List of dependencies for variable %s changed from '%s' to '%s'" % (dep, a_data['gendeps'][dep], b_data['gendeps'][dep]))
            if a_data['gendeps'][dep] and b_data['gendeps'][dep]:
                output.append("changed items: %s" % a_data['gendeps'][dep].symmetric_difference(b_data['gendeps'][dep]))
    if added:
        for dep in added:
            output.append("Dependency on variable %s was added" % (dep))
    if removed:
        for dep in removed:
            output.append("Dependency on Variable %s was removed" % (dep))


    changed, added, removed = dict_diff(a_data['varvals'], b_data['varvals'])
    if changed:
        for dep in changed:
            output.append("Variable %s value changed from '%s' to '%s'" % (dep, a_data['varvals'][dep], b_data['varvals'][dep]))

    changed, added, removed = file_checksums_diff(a_data['file_checksum_values'], b_data['file_checksum_values'])
    if changed:
        for f, old, new in changed:
            output.append("Checksum for file %s changed from %s to %s" % (f, old, new))
    if added:
        for f in added:
            output.append("Dependency on checksum of file %s was added" % (f))
    if removed:
        for f in removed:
            output.append("Dependency on checksum of file %s was removed" % (f))


    if len(a_data['runtaskdeps']) != len(b_data['runtaskdeps']):
        changed = ["Number of task dependencies changed"]
    else:
        changed = []
        for idx, task in enumerate(a_data['runtaskdeps']):
            a = a_data['runtaskdeps'][idx]
            b = b_data['runtaskdeps'][idx]
            if a_data['runtaskhashes'][a] != b_data['runtaskhashes'][b]:
                changed.append("%s with hash %s\n changed to\n%s with hash %s" % (a, a_data['runtaskhashes'][a], b, b_data['runtaskhashes'][b]))

    if changed:
        output.append("runtaskdeps changed from %s to %s" % (clean_basepaths_list(a_data['runtaskdeps']), clean_basepaths_list(b_data['runtaskdeps'])))
        output.append("\n".join(changed))


    if 'runtaskhashes' in a_data and 'runtaskhashes' in b_data:
        a = a_data['runtaskhashes']
        b = b_data['runtaskhashes']
        changed, added, removed = dict_diff(a, b)
        if added:
            for dep in added:
                bdep_found = False
                if removed:
                    for bdep in removed:
                        if b[dep] == a[bdep]:
                            #output.append("Dependency on task %s was replaced by %s with same hash" % (dep, bdep))
                            bdep_found = True
                if not bdep_found:
                    output.append("Dependency on task %s was added with hash %s" % (clean_basepath(dep), b[dep]))
        if removed:
            for dep in removed:
                adep_found = False
                if added:
                    for adep in added:
                        if b[adep] == a[dep]:
                            #output.append("Dependency on task %s was replaced by %s with same hash" % (adep, dep))
                            adep_found = True
                if not adep_found:
                    output.append("Dependency on task %s was removed with hash %s" % (clean_basepath(dep), a[dep]))
        if changed:
            for dep in changed:
                output.append("Hash for dependent task %s changed from %s to %s" % (clean_basepath(dep), a[dep], b[dep]))
                if callable(recursecb):
                    # If a dependent hash changed, might as well print the line above and then defer to the changes in 
                    # that hash since in all likelyhood, they're the same changes this task also saw.
                    recout = recursecb(dep, a[dep], b[dep])
                    if recout:
                        output = [output[-1]] + recout

    a_taint = a_data.get('taint', None)
    b_taint = b_data.get('taint', None)
    if a_taint != b_taint:
        output.append("Taint (by forced/invalidated task) changed from %s to %s" % (a_taint, b_taint))

    return output


def calc_basehash(sigdata):
    task = sigdata['task']
    basedata = sigdata['varvals'][task]

    if basedata is None:
        basedata = ''

    alldeps = sigdata['taskdeps']
    for dep in alldeps:
        basedata = basedata + dep
        val = sigdata['varvals'][dep]
        if val is not None:
            basedata = basedata + str(val)

    return hashlib.md5(basedata).hexdigest()

def calc_taskhash(sigdata):
    data = sigdata['basehash']

    for dep in sigdata['runtaskdeps']:
        data = data + sigdata['runtaskhashes'][dep]

    for c in sigdata['file_checksum_values']:
        data = data + c[1]

    if 'taint' in sigdata:
        if 'nostamp:' in sigdata['taint']:
            data = data + sigdata['taint'][8:]
        else:
            data = data + sigdata['taint']

    return hashlib.md5(data).hexdigest()


def dump_sigfile(a):
    output = []

    p1 = pickle.Unpickler(open(a, "rb"))
    a_data = p1.load()

    output.append("basewhitelist: %s" % (a_data['basewhitelist']))

    output.append("taskwhitelist: %s" % (a_data['taskwhitelist']))

    output.append("Task dependencies: %s" % (sorted(a_data['taskdeps'])))

    output.append("basehash: %s" % (a_data['basehash']))

    for dep in a_data['gendeps']:
        output.append("List of dependencies for variable %s is %s" % (dep, a_data['gendeps'][dep]))

    for dep in a_data['varvals']:
        output.append("Variable %s value is %s" % (dep, a_data['varvals'][dep]))

    if 'runtaskdeps' in a_data:
        output.append("Tasks this task depends on: %s" % (a_data['runtaskdeps']))

    if 'file_checksum_values' in a_data:
        output.append("This task depends on the checksums of files: %s" % (a_data['file_checksum_values']))

    if 'runtaskhashes' in a_data:
        for dep in a_data['runtaskhashes']:
            output.append("Hash for dependent task %s is %s" % (dep, a_data['runtaskhashes'][dep]))

    if 'taint' in a_data:
        output.append("Tainted (by forced/invalidated task): %s" % a_data['taint'])

    if 'task' in a_data:
        computed_basehash = calc_basehash(a_data)
        output.append("Computed base hash is %s and from file %s" % (computed_basehash, a_data['basehash']))
    else:
        output.append("Unable to compute base hash")

    computed_taskhash = calc_taskhash(a_data)
    output.append("Computed task hash is %s" % computed_taskhash)

    return output
