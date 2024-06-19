#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import hashlib
import logging
import os
import re
import tempfile
import pickle
import bb.data
import difflib
import simplediff
import json
import types
from contextlib import contextmanager
import bb.compress.zstd
from bb.checksum import FileChecksumCache
from bb import runqueue
import hashserv
import hashserv.client

logger = logging.getLogger('BitBake.SigGen')
hashequiv_logger = logging.getLogger('BitBake.SigGen.HashEquiv')

#find_siginfo and find_siginfo_version are set by the metadata siggen
# The minimum version of the find_siginfo function we need
find_siginfo_minversion = 2

HASHSERV_ENVVARS = [
    "SSL_CERT_DIR",
    "SSL_CERT_FILE",
    "NO_PROXY",
    "HTTPS_PROXY",
    "HTTP_PROXY"
]

def check_siggen_version(siggen):
    if not hasattr(siggen, "find_siginfo_version"):
        bb.fatal("Siggen from metadata (OE-Core?) is too old, please update it (no version found)")
    if siggen.find_siginfo_version < siggen.find_siginfo_minversion:
        bb.fatal("Siggen from metadata (OE-Core?) is too old, please update it (%s vs %s)" % (siggen.find_siginfo_version, siggen.find_siginfo_minversion))

class SetEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, set) or isinstance(obj, frozenset):
            return dict(_set_object=list(sorted(obj)))
        return json.JSONEncoder.default(self, obj)

def SetDecoder(dct):
    if '_set_object' in dct:
        return frozenset(dct['_set_object'])
    return dct

def init(d):
    siggens = [obj for obj in globals().values()
                      if type(obj) is type and issubclass(obj, SignatureGenerator)]

    desired = d.getVar("BB_SIGNATURE_HANDLER") or "noop"
    for sg in siggens:
        if desired == sg.name:
            return sg(d)
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
        self.basehash = {}
        self.taskhash = {}
        self.unihash = {}
        self.runtaskdeps = {}
        self.file_checksum_values = {}
        self.taints = {}
        self.unitaskhashes = {}
        self.tidtopn = {}
        self.setscenetasks = set()

    def finalise(self, fn, d, varient):
        return

    def postparsing_clean_cache(self):
        return

    def setup_datacache(self, datacaches):
        self.datacaches = datacaches

    def setup_datacache_from_datastore(self, mcfn, d):
        # In task context we have no cache so setup internal data structures
        # from the fully parsed data store provided

        mc = d.getVar("__BBMULTICONFIG", False) or ""
        tasks = d.getVar('__BBTASKS', False)

        self.datacaches = {}
        self.datacaches[mc] = types.SimpleNamespace()
        setattr(self.datacaches[mc], "stamp", {})
        self.datacaches[mc].stamp[mcfn] = d.getVar('STAMP')
        setattr(self.datacaches[mc], "stamp_extrainfo", {})
        self.datacaches[mc].stamp_extrainfo[mcfn] = {}
        for t in tasks:
            flag = d.getVarFlag(t, "stamp-extra-info")
            if flag:
                self.datacaches[mc].stamp_extrainfo[mcfn][t] = flag

    def get_cached_unihash(self, tid):
        return None

    def get_unihash(self, tid):
        unihash = self.get_cached_unihash(tid)
        if unihash:
            return unihash
        return self.taskhash[tid]

    def get_unihashes(self, tids):
        return {tid: self.get_unihash(tid) for tid in tids}

    def prep_taskhash(self, tid, deps, dataCaches):
        return

    def get_taskhash(self, tid, deps, dataCaches):
        self.taskhash[tid] = hashlib.sha256(tid.encode("utf-8")).hexdigest()
        return self.taskhash[tid]

    def writeout_file_checksum_cache(self):
        """Write/update the file checksum cache onto disk"""
        return

    def stampfile_base(self, mcfn):
        mc = bb.runqueue.mc_from_tid(mcfn)
        return self.datacaches[mc].stamp[mcfn]

    def stampfile_mcfn(self, taskname, mcfn, extrainfo=True):
        mc = bb.runqueue.mc_from_tid(mcfn)
        stamp = self.datacaches[mc].stamp[mcfn]
        if not stamp:
            return

        stamp_extrainfo = ""
        if extrainfo:
            taskflagname = taskname
            if taskname.endswith("_setscene"):
                taskflagname = taskname.replace("_setscene", "")
            stamp_extrainfo = self.datacaches[mc].stamp_extrainfo[mcfn].get(taskflagname) or ""

        return self.stampfile(stamp, mcfn, taskname, stamp_extrainfo)

    def stampfile(self, stampbase, file_name, taskname, extrainfo):
        return ("%s.%s.%s" % (stampbase, taskname, extrainfo)).rstrip('.')

    def stampcleanmask_mcfn(self, taskname, mcfn):
        mc = bb.runqueue.mc_from_tid(mcfn)
        stamp = self.datacaches[mc].stamp[mcfn]
        if not stamp:
            return []

        taskflagname = taskname
        if taskname.endswith("_setscene"):
            taskflagname = taskname.replace("_setscene", "")
        stamp_extrainfo = self.datacaches[mc].stamp_extrainfo[mcfn].get(taskflagname) or ""

        return self.stampcleanmask(stamp, mcfn, taskname, stamp_extrainfo)

    def stampcleanmask(self, stampbase, file_name, taskname, extrainfo):
        return ("%s.%s.%s" % (stampbase, taskname, extrainfo)).rstrip('.')

    def dump_sigtask(self, mcfn, task, stampbase, runtime):
        return

    def invalidate_task(self, task, mcfn):
        mc = bb.runqueue.mc_from_tid(mcfn)
        stamp = self.datacaches[mc].stamp[mcfn]
        bb.utils.remove(stamp)

    def dump_sigs(self, dataCache, options):
        return

    def get_taskdata(self):
        return (self.runtaskdeps, self.taskhash, self.unihash, self.file_checksum_values, self.taints, self.basehash, self.unitaskhashes, self.tidtopn, self.setscenetasks)

    def set_taskdata(self, data):
        self.runtaskdeps, self.taskhash, self.unihash, self.file_checksum_values, self.taints, self.basehash, self.unitaskhashes, self.tidtopn, self.setscenetasks = data

    def reset(self, data):
        self.__init__(data)

    def get_taskhashes(self):
        return self.taskhash, self.unihash, self.unitaskhashes, self.tidtopn

    def set_taskhashes(self, hashes):
        self.taskhash, self.unihash, self.unitaskhashes, self.tidtopn = hashes

    def save_unitaskhashes(self):
        return

    def set_setscene_tasks(self, setscene_tasks):
        return

    def exit(self):
        return

def build_pnid(mc, pn, taskname):
    if mc:
        return "mc:" + mc + ":" + pn + ":" + taskname
    return pn + ":" + taskname

class SignatureGeneratorBasic(SignatureGenerator):
    """
    """
    name = "basic"

    def __init__(self, data):
        self.basehash = {}
        self.taskhash = {}
        self.unihash = {}
        self.runtaskdeps = {}
        self.file_checksum_values = {}
        self.taints = {}
        self.setscenetasks = set()
        self.basehash_ignore_vars = set((data.getVar("BB_BASEHASH_IGNORE_VARS") or "").split())
        self.taskhash_ignore_tasks = None
        self.init_rundepcheck(data)
        checksum_cache_file = data.getVar("BB_HASH_CHECKSUM_CACHE_FILE")
        if checksum_cache_file:
            self.checksum_cache = FileChecksumCache()
            self.checksum_cache.init_cache(data, checksum_cache_file)
        else:
            self.checksum_cache = None

        self.unihash_cache = bb.cache.SimpleCache("3")
        self.unitaskhashes = self.unihash_cache.init_cache(data, "bb_unihashes.dat", {})
        self.localdirsexclude = (data.getVar("BB_SIGNATURE_LOCAL_DIRS_EXCLUDE") or "CVS .bzr .git .hg .osc .p4 .repo .svn").split()
        self.tidtopn = {}

    def init_rundepcheck(self, data):
        self.taskhash_ignore_tasks = data.getVar("BB_TASKHASH_IGNORE_TASKS") or None
        if self.taskhash_ignore_tasks:
            self.twl = re.compile(self.taskhash_ignore_tasks)
        else:
            self.twl = None

    def _build_data(self, mcfn, d):

        ignore_mismatch = ((d.getVar("BB_HASH_IGNORE_MISMATCH") or '') == '1')
        tasklist, gendeps, lookupcache = bb.data.generate_dependencies(d, self.basehash_ignore_vars)

        taskdeps, basehash = bb.data.generate_dependency_hash(tasklist, gendeps, lookupcache, self.basehash_ignore_vars, mcfn)

        for task in tasklist:
            tid = mcfn + ":" + task
            if not ignore_mismatch and tid in self.basehash and self.basehash[tid] != basehash[tid]:
                bb.error("When reparsing %s, the basehash value changed from %s to %s. The metadata is not deterministic and this needs to be fixed." % (tid, self.basehash[tid], basehash[tid]))
                bb.error("The following commands may help:")
                cmd = "$ bitbake %s -c%s" % (d.getVar('PN'), task)
                # Make sure sigdata is dumped before run printdiff
                bb.error("%s -Snone" % cmd)
                bb.error("Then:")
                bb.error("%s -Sprintdiff\n" % cmd)
            self.basehash[tid] = basehash[tid]

        return taskdeps, gendeps, lookupcache

    def set_setscene_tasks(self, setscene_tasks):
        self.setscenetasks = set(setscene_tasks)

    def finalise(self, fn, d, variant):

        mc = d.getVar("__BBMULTICONFIG", False) or ""
        mcfn = fn
        if variant or mc:
            mcfn = bb.cache.realfn2virtual(fn, variant, mc)

        try:
            taskdeps, gendeps, lookupcache = self._build_data(mcfn, d)
        except bb.parse.SkipRecipe:
            raise
        except:
            bb.warn("Error during finalise of %s" % mcfn)
            raise

        basehashes = {}
        for task in taskdeps:
            basehashes[task] = self.basehash[mcfn + ":" + task]

        d.setVar("__siggen_basehashes", basehashes)
        d.setVar("__siggen_gendeps", gendeps)
        d.setVar("__siggen_varvals", lookupcache)
        d.setVar("__siggen_taskdeps", taskdeps)

        #Slow but can be useful for debugging mismatched basehashes
        #self.setup_datacache_from_datastore(mcfn, d)
        #for task in taskdeps:
        #    self.dump_sigtask(mcfn, task, d.getVar("STAMP"), False)

    def setup_datacache_from_datastore(self, mcfn, d):
        super().setup_datacache_from_datastore(mcfn, d)

        mc = bb.runqueue.mc_from_tid(mcfn)
        for attr in ["siggen_varvals", "siggen_taskdeps", "siggen_gendeps"]:
            if not hasattr(self.datacaches[mc], attr):
                setattr(self.datacaches[mc], attr, {})
        self.datacaches[mc].siggen_varvals[mcfn] = d.getVar("__siggen_varvals")
        self.datacaches[mc].siggen_taskdeps[mcfn] = d.getVar("__siggen_taskdeps")
        self.datacaches[mc].siggen_gendeps[mcfn] = d.getVar("__siggen_gendeps")

    def rundep_check(self, fn, recipename, task, dep, depname, dataCaches):
        # Return True if we should keep the dependency, False to drop it
        # We only manipulate the dependencies for packages not in the ignore
        # list
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

    def prep_taskhash(self, tid, deps, dataCaches):

        (mc, _, task, mcfn) = bb.runqueue.split_tid_mcfn(tid)

        self.basehash[tid] = dataCaches[mc].basetaskhash[tid]
        self.runtaskdeps[tid] = []
        self.file_checksum_values[tid] = []
        recipename = dataCaches[mc].pkg_fn[mcfn]

        self.tidtopn[tid] = recipename
        # save hashfn for deps into siginfo?
        for dep in deps:
            (depmc, _, deptask, depmcfn) = bb.runqueue.split_tid_mcfn(dep)
            dep_pn = dataCaches[depmc].pkg_fn[depmcfn]

            if not self.rundep_check(mcfn, recipename, task, dep, dep_pn, dataCaches):
                continue

            if dep not in self.taskhash:
                bb.fatal("%s is not in taskhash, caller isn't calling in dependency order?" % dep)

            dep_pnid = build_pnid(depmc, dep_pn, deptask)
            self.runtaskdeps[tid].append((dep_pnid, dep))

        if task in dataCaches[mc].file_checksums[mcfn]:
            if self.checksum_cache:
                checksums = self.checksum_cache.get_checksums(dataCaches[mc].file_checksums[mcfn][task], recipename, self.localdirsexclude)
            else:
                checksums = bb.fetch2.get_file_checksums(dataCaches[mc].file_checksums[mcfn][task], recipename, self.localdirsexclude)
            for (f,cs) in checksums:
                self.file_checksum_values[tid].append((f,cs))

        taskdep = dataCaches[mc].task_deps[mcfn]
        if 'nostamp' in taskdep and task in taskdep['nostamp']:
            # Nostamp tasks need an implicit taint so that they force any dependent tasks to run
            if tid in self.taints and self.taints[tid].startswith("nostamp:"):
                # Don't reset taint value upon every call
                pass
            else:
                import uuid
                taint = str(uuid.uuid4())
                self.taints[tid] = "nostamp:" + taint

        taint = self.read_taint(mcfn, task, dataCaches[mc].stamp[mcfn])
        if taint:
            self.taints[tid] = taint
            logger.warning("%s is tainted from a forced run" % tid)

        return set(dep for _, dep in self.runtaskdeps[tid])

    def get_taskhash(self, tid, deps, dataCaches):

        data = self.basehash[tid]
        for dep in sorted(self.runtaskdeps[tid]):
            data += self.get_unihash(dep[1])

        for (f, cs) in sorted(self.file_checksum_values[tid], key=clean_checksum_file_path):
            if cs:
                if "/./" in f:
                    data += "./" + f.split("/./")[1]
                data += cs

        if tid in self.taints:
            if self.taints[tid].startswith("nostamp:"):
                data += self.taints[tid][8:]
            else:
                data += self.taints[tid]

        h = hashlib.sha256(data.encode("utf-8")).hexdigest()
        self.taskhash[tid] = h
        #d.setVar("BB_TASKHASH:task-%s" % task, taskhash[task])
        return h

    def writeout_file_checksum_cache(self):
        """Write/update the file checksum cache onto disk"""
        if self.checksum_cache:
            self.checksum_cache.save_extras()
            self.checksum_cache.save_merge()
        else:
            bb.fetch2.fetcher_parse_save()
            bb.fetch2.fetcher_parse_done()

    def save_unitaskhashes(self):
        self.unihash_cache.save(self.unitaskhashes)

    def dump_sigtask(self, mcfn, task, stampbase, runtime):
        tid = mcfn + ":" + task
        mc = bb.runqueue.mc_from_tid(mcfn)
        referencestamp = stampbase
        if isinstance(runtime, str) and runtime.startswith("customfile"):
            sigfile = stampbase
            referencestamp = runtime[11:]
        elif runtime and tid in self.taskhash:
            sigfile = stampbase + "." + task + ".sigdata" + "." + self.get_unihash(tid)
        else:
            sigfile = stampbase + "." + task + ".sigbasedata" + "." + self.basehash[tid]

        with bb.utils.umask(0o002):
            bb.utils.mkdirhier(os.path.dirname(sigfile))

        data = {}
        data['task'] = task
        data['basehash_ignore_vars'] = self.basehash_ignore_vars
        data['taskhash_ignore_tasks'] = self.taskhash_ignore_tasks
        data['taskdeps'] = self.datacaches[mc].siggen_taskdeps[mcfn][task]
        data['basehash'] = self.basehash[tid]
        data['gendeps'] = {}
        data['varvals'] = {}
        data['varvals'][task] = self.datacaches[mc].siggen_varvals[mcfn][task]
        for dep in self.datacaches[mc].siggen_taskdeps[mcfn][task]:
            if dep in self.basehash_ignore_vars:
                continue
            data['gendeps'][dep] = self.datacaches[mc].siggen_gendeps[mcfn][dep]
            data['varvals'][dep] = self.datacaches[mc].siggen_varvals[mcfn][dep]

        if runtime and tid in self.taskhash:
            data['runtaskdeps'] = [dep[0] for dep in sorted(self.runtaskdeps[tid])]
            data['file_checksum_values'] = []
            for f,cs in sorted(self.file_checksum_values[tid], key=clean_checksum_file_path):
                if "/./" in f:
                    data['file_checksum_values'].append(("./" + f.split("/./")[1], cs))
                else:
                    data['file_checksum_values'].append((os.path.basename(f), cs))
            data['runtaskhashes'] = {}
            for dep in self.runtaskdeps[tid]:
                data['runtaskhashes'][dep[0]] = self.get_unihash(dep[1])
            data['taskhash'] = self.taskhash[tid]
            data['unihash'] = self.get_unihash(tid)

        taint = self.read_taint(mcfn, task, referencestamp)
        if taint:
            data['taint'] = taint

        if runtime and tid in self.taints:
            if 'nostamp:' in self.taints[tid]:
                data['taint'] = self.taints[tid]

        computed_basehash = calc_basehash(data)
        if computed_basehash != self.basehash[tid]:
            bb.error("Basehash mismatch %s versus %s for %s" % (computed_basehash, self.basehash[tid], tid))
        if runtime and tid in self.taskhash:
            computed_taskhash = calc_taskhash(data)
            if computed_taskhash != self.taskhash[tid]:
                bb.error("Taskhash mismatch %s versus %s for %s" % (computed_taskhash, self.taskhash[tid], tid))
                sigfile = sigfile.replace(self.taskhash[tid], computed_taskhash)

        fd, tmpfile = bb.utils.mkstemp(dir=os.path.dirname(sigfile), prefix="sigtask.")
        try:
            with bb.compress.zstd.open(fd, "wt", encoding="utf-8", num_threads=1) as f:
                json.dump(data, f, sort_keys=True, separators=(",", ":"), cls=SetEncoder)
                f.flush()
            os.chmod(tmpfile, 0o664)
            bb.utils.rename(tmpfile, sigfile)
        except (OSError, IOError) as err:
            try:
                os.unlink(tmpfile)
            except OSError:
                pass
            raise err

class SignatureGeneratorBasicHash(SignatureGeneratorBasic):
    name = "basichash"

    def get_stampfile_hash(self, tid):
        if tid in self.taskhash:
            return self.taskhash[tid]

        # If task is not in basehash, then error
        return self.basehash[tid]

    def stampfile(self, stampbase, mcfn, taskname, extrainfo, clean=False):
        if taskname.endswith("_setscene"):
            tid = mcfn + ":" + taskname[:-9]
        else:
            tid = mcfn + ":" + taskname
        if clean:
            h = "*"
        else:
            h = self.get_stampfile_hash(tid)

        return ("%s.%s.%s.%s" % (stampbase, taskname, h, extrainfo)).rstrip('.')

    def stampcleanmask(self, stampbase, mcfn, taskname, extrainfo):
        return self.stampfile(stampbase, mcfn, taskname, extrainfo, clean=True)

    def invalidate_task(self, task, mcfn):
        bb.note("Tainting hash to force rebuild of task %s, %s" % (mcfn, task))

        mc = bb.runqueue.mc_from_tid(mcfn)
        stamp = self.datacaches[mc].stamp[mcfn]

        taintfn = stamp + '.' + task + '.taint'

        import uuid
        bb.utils.mkdirhier(os.path.dirname(taintfn))
        # The specific content of the taint file is not really important,
        # we just need it to be random, so a random UUID is used
        with open(taintfn, 'w') as taintf:
            taintf.write(str(uuid.uuid4()))

class SignatureGeneratorUniHashMixIn(object):
    def __init__(self, data):
        self.extramethod = {}
        # NOTE: The cache only tracks hashes that exist. Hashes that don't
        # exist are always queried from the server since it is possible for
        # hashes to appear over time, but much less likely for them to
        # disappear
        self.unihash_exists_cache = set()
        self.username = None
        self.password = None
        self.env = {}

        origenv = data.getVar("BB_ORIGENV")
        for e in HASHSERV_ENVVARS:
            value = data.getVar(e)
            if not value and origenv:
                value = origenv.getVar(e)
            if value:
                self.env[e] = value
        super().__init__(data)

    def get_taskdata(self):
        return (self.server, self.method, self.extramethod, self.username, self.password, self.env) + super().get_taskdata()

    def set_taskdata(self, data):
        self.server, self.method, self.extramethod, self.username, self.password, self.env = data[:6]
        super().set_taskdata(data[6:])

    def get_hashserv_creds(self):
        if self.username and self.password:
            return {
                "username": self.username,
                "password": self.password,
            }

        return {}

    @contextmanager
    def _client_env(self):
        orig_env = os.environ.copy()
        try:
            for k, v in self.env.items():
                os.environ[k] = v

            yield
        finally:
            for k, v in self.env.items():
                if k in orig_env:
                    os.environ[k] = orig_env[k]
                else:
                    del os.environ[k]

    @contextmanager
    def client(self):
        with self._client_env():
            if getattr(self, '_client', None) is None:
                self._client = hashserv.create_client(self.server, **self.get_hashserv_creds())
            yield self._client

    def reset(self, data):
        self.__close_clients()
        return super().reset(data)

    def exit(self):
        self.__close_clients()
        return super().exit()

    def __close_clients(self):
        with self._client_env():
            if getattr(self, '_client', None) is not None:
                self._client.close()
                self._client = None
            if getattr(self, '_client_pool', None) is not None:
                self._client_pool.close()
                self._client_pool = None

    def get_stampfile_hash(self, tid):
        if tid in self.taskhash:
            # If a unique hash is reported, use it as the stampfile hash. This
            # ensures that if a task won't be re-run if the taskhash changes,
            # but it would result in the same output hash
            unihash = self._get_unihash(tid)
            if unihash is not None:
                return unihash

        return super().get_stampfile_hash(tid)

    def set_unihash(self, tid, unihash):
        (mc, fn, taskname, taskfn) = bb.runqueue.split_tid_mcfn(tid)
        key = mc + ":" + self.tidtopn[tid] + ":" + taskname
        self.unitaskhashes[key] = (self.taskhash[tid], unihash)
        self.unihash[tid] = unihash

    def _get_unihash(self, tid, checkkey=None):
        if tid not in self.tidtopn:
            return None
        (mc, fn, taskname, taskfn) = bb.runqueue.split_tid_mcfn(tid)
        key = mc + ":" + self.tidtopn[tid] + ":" + taskname
        if key not in self.unitaskhashes:
            return None
        if not checkkey:
            checkkey = self.taskhash[tid]
        (key, unihash) = self.unitaskhashes[key]
        if key != checkkey:
            return None
        return unihash

    def get_cached_unihash(self, tid):
        taskhash = self.taskhash[tid]

        # If its not a setscene task we can return
        if self.setscenetasks and tid not in self.setscenetasks:
            self.unihash[tid] = None
            return taskhash

        # TODO: This cache can grow unbounded. It probably only needs to keep
        # for each task
        unihash =  self._get_unihash(tid)
        if unihash is not None:
            self.unihash[tid] = unihash
            return unihash

        return None

    def _get_method(self, tid):
        method = self.method
        if tid in self.extramethod:
            method = method + self.extramethod[tid]

        return method

    def unihashes_exist(self, query):
        if len(query) == 0:
            return {}

        query_keys = []
        result = {}
        for key, unihash in query.items():
            if unihash in self.unihash_exists_cache:
                result[key] = True
            else:
                query_keys.append(key)

        if query_keys:
            with self.client() as client:
                query_result = client.unihash_exists_batch(query[k] for k in query_keys)

        for idx, key in enumerate(query_keys):
            exists = query_result[idx]
            if exists:
                self.unihash_exists_cache.add(query[key])
            result[key] = exists

        return result

    def get_unihash(self, tid):
        return self.get_unihashes([tid])[tid]

    def get_unihashes(self, tids):
        """
        For a iterable of tids, returns a dictionary that maps each tid to a
        unihash
        """
        result = {}
        query_tids = []

        for tid in tids:
            unihash = self.get_cached_unihash(tid)
            if unihash:
                result[tid] = unihash
            else:
                query_tids.append(tid)

        if query_tids:
            unihashes = []
            try:
                with self.client() as client:
                    unihashes = client.get_unihash_batch((self._get_method(tid), self.taskhash[tid]) for tid in query_tids)
            except (ConnectionError, FileNotFoundError) as e:
                bb.warn('Error contacting Hash Equivalence Server %s: %s' % (self.server, str(e)))

        for idx, tid in enumerate(query_tids):
            # In the absence of being able to discover a unique hash from the
            # server, make it be equivalent to the taskhash. The unique "hash" only
            # really needs to be a unique string (not even necessarily a hash), but
            # making it match the taskhash has a few advantages:
            #
            # 1) All of the sstate code that assumes hashes can be the same
            # 2) It provides maximal compatibility with builders that don't use
            #    an equivalency server
            # 3) The value is easy for multiple independent builders to derive the
            #    same unique hash from the same input. This means that if the
            #    independent builders find the same taskhash, but it isn't reported
            #    to the server, there is a better chance that they will agree on
            #    the unique hash.
            taskhash = self.taskhash[tid]

            if unihashes and unihashes[idx]:
                unihash = unihashes[idx]
                # A unique hash equal to the taskhash is not very interesting,
                # so it is reported it at debug level 2. If they differ, that
                # is much more interesting, so it is reported at debug level 1
                hashequiv_logger.bbdebug((1, 2)[unihash == taskhash], 'Found unihash %s in place of %s for %s from %s' % (unihash, taskhash, tid, self.server))
            else:
                hashequiv_logger.debug2('No reported unihash for %s:%s from %s' % (tid, taskhash, self.server))
                unihash = taskhash

            self.set_unihash(tid, unihash)
            self.unihash[tid] = unihash
            result[tid] = unihash

        return result

    def report_unihash(self, path, task, d):
        import importlib

        taskhash = d.getVar('BB_TASKHASH')
        unihash = d.getVar('BB_UNIHASH')
        report_taskdata = d.getVar('SSTATE_HASHEQUIV_REPORT_TASKDATA') == '1'
        tempdir = d.getVar('T')
        mcfn = d.getVar('BB_FILENAME')
        tid = mcfn + ':do_' + task
        key = tid + ':' + taskhash

        if self.setscenetasks and tid not in self.setscenetasks:
            return

        # This can happen if locked sigs are in action. Detect and just exit
        if taskhash != self.taskhash[tid]:
            return

        # Sanity checks
        cache_unihash = self._get_unihash(tid, checkkey=taskhash)
        if cache_unihash is None:
            bb.fatal('%s not in unihash cache. Please report this error' % key)

        if cache_unihash != unihash:
            bb.fatal("Cache unihash %s doesn't match BB_UNIHASH %s" % (cache_unihash, unihash))

        sigfile = None
        sigfile_name = "depsig.do_%s.%d" % (task, os.getpid())
        sigfile_link = "depsig.do_%s" % task

        try:
            sigfile = open(os.path.join(tempdir, sigfile_name), 'w+b')

            locs = {'path': path, 'sigfile': sigfile, 'task': task, 'd': d}

            if "." in self.method:
                (module, method) = self.method.rsplit('.', 1)
                locs['method'] = getattr(importlib.import_module(module), method)
                outhash = bb.utils.better_eval('method(path, sigfile, task, d)', locs)
            else:
                outhash = bb.utils.better_eval(self.method + '(path, sigfile, task, d)', locs)

            try:
                extra_data = {}

                owner = d.getVar('SSTATE_HASHEQUIV_OWNER')
                if owner:
                    extra_data['owner'] = owner

                if report_taskdata:
                    sigfile.seek(0)

                    extra_data['PN'] = d.getVar('PN')
                    extra_data['PV'] = d.getVar('PV')
                    extra_data['PR'] = d.getVar('PR')
                    extra_data['task'] = task
                    extra_data['outhash_siginfo'] = sigfile.read().decode('utf-8')

                method = self.method
                if tid in self.extramethod:
                    method = method + self.extramethod[tid]

                with self.client() as client:
                    data = client.report_unihash(taskhash, method, outhash, unihash, extra_data)

                new_unihash = data['unihash']

                if new_unihash != unihash:
                    hashequiv_logger.debug('Task %s unihash changed %s -> %s by server %s' % (taskhash, unihash, new_unihash, self.server))
                    bb.event.fire(bb.runqueue.taskUniHashUpdate(mcfn + ':do_' + task, new_unihash), d)
                    self.set_unihash(tid, new_unihash)
                    d.setVar('BB_UNIHASH', new_unihash)
                else:
                    hashequiv_logger.debug('Reported task %s as unihash %s to %s' % (taskhash, unihash, self.server))
            except (ConnectionError, FileNotFoundError) as e:
                bb.warn('Error contacting Hash Equivalence Server %s: %s' % (self.server, str(e)))
        finally:
            if sigfile:
                sigfile.close()

                sigfile_link_path = os.path.join(tempdir, sigfile_link)
                bb.utils.remove(sigfile_link_path)

                try:
                    os.symlink(sigfile_name, sigfile_link_path)
                except OSError:
                    pass

    def report_unihash_equiv(self, tid, taskhash, wanted_unihash, current_unihash, datacaches):
        try:
            extra_data = {}
            method = self.method
            if tid in self.extramethod:
                method = method + self.extramethod[tid]

            with self.client() as client:
                data = client.report_unihash_equiv(taskhash, method, wanted_unihash, extra_data)

            hashequiv_logger.verbose('Reported task %s as unihash %s to %s (%s)' % (tid, wanted_unihash, self.server, str(data)))

            if data is None:
                bb.warn("Server unable to handle unihash report")
                return False

            finalunihash = data['unihash']

            if finalunihash == current_unihash:
                hashequiv_logger.verbose('Task %s unihash %s unchanged by server' % (tid, finalunihash))
            elif finalunihash == wanted_unihash:
                hashequiv_logger.verbose('Task %s unihash changed %s -> %s as wanted' % (tid, current_unihash, finalunihash))
                self.set_unihash(tid, finalunihash)
                return True
            else:
                # TODO: What to do here?
                hashequiv_logger.verbose('Task %s unihash reported as unwanted hash %s' % (tid, finalunihash))

        except (ConnectionError, FileNotFoundError) as e:
            bb.warn('Error contacting Hash Equivalence Server %s: %s' % (self.server, str(e)))

        return False

#
# Dummy class used for bitbake-selftest
#
class SignatureGeneratorTestEquivHash(SignatureGeneratorUniHashMixIn, SignatureGeneratorBasicHash):
    name = "TestEquivHash"
    def init_rundepcheck(self, data):
        super().init_rundepcheck(data)
        self.server = data.getVar('BB_HASHSERVE')
        self.method = "sstate_output_hash"

def clean_checksum_file_path(file_checksum_tuple):
    f, cs = file_checksum_tuple
    if "/./" in f:
        return "./" + f.split("/./")[1]
    return f

def dump_this_task(outfile, d):
    import bb.parse
    mcfn = d.getVar("BB_FILENAME")
    task = "do_" + d.getVar("BB_CURRENTTASK")
    referencestamp = bb.parse.siggen.stampfile_base(mcfn)
    bb.parse.siggen.dump_sigtask(mcfn, task, outfile, "customfile:" + referencestamp)

def init_colors(enable_color):
    """Initialise colour dict for passing to compare_sigfiles()"""
    # First set up the colours
    colors = {'color_title':   '\033[1m',
              'color_default': '\033[0m',
              'color_add':     '\033[0;32m',
              'color_remove':  '\033[0;31m',
             }
    # Leave all keys present but clear the values
    if not enable_color:
        for k in colors.keys():
            colors[k] = ''
    return colors

def worddiff_str(oldstr, newstr, colors=None):
    if not colors:
        colors = init_colors(False)
    diff = simplediff.diff(oldstr.split(' '), newstr.split(' '))
    ret = []
    for change, value in diff:
        value = ' '.join(value)
        if change == '=':
            ret.append(value)
        elif change == '+':
            item = '{color_add}{{+{value}+}}{color_default}'.format(value=value, **colors)
            ret.append(item)
        elif change == '-':
            item = '{color_remove}[-{value}-]{color_default}'.format(value=value, **colors)
            ret.append(item)
    whitespace_note = ''
    if oldstr != newstr and ' '.join(oldstr.split()) == ' '.join(newstr.split()):
        whitespace_note = ' (whitespace changed)'
    return '"%s"%s' % (' '.join(ret), whitespace_note)

def list_inline_diff(oldlist, newlist, colors=None):
    if not colors:
        colors = init_colors(False)
    diff = simplediff.diff(oldlist, newlist)
    ret = []
    for change, value in diff:
        value = ' '.join(value)
        if change == '=':
            ret.append("'%s'" % value)
        elif change == '+':
            item = '{color_add}+{value}{color_default}'.format(value=value, **colors)
            ret.append(item)
        elif change == '-':
            item = '{color_remove}-{value}{color_default}'.format(value=value, **colors)
            ret.append(item)
    return '[%s]' % (', '.join(ret))

# Handled renamed fields
def handle_renames(data):
    if 'basewhitelist' in data:
        data['basehash_ignore_vars'] = data['basewhitelist']
        del data['basewhitelist']
    if 'taskwhitelist' in data:
        data['taskhash_ignore_tasks'] = data['taskwhitelist']
        del data['taskwhitelist']


def compare_sigfiles(a, b, recursecb=None, color=False, collapsed=False):
    output = []

    colors = init_colors(color)
    def color_format(formatstr, **values):
        """
        Return colour formatted string.
        NOTE: call with the format string, not an already formatted string
        containing values (otherwise you could have trouble with { and }
        characters)
        """
        if not formatstr.endswith('{color_default}'):
            formatstr += '{color_default}'
        # In newer python 3 versions you can pass both of these directly,
        # but we only require 3.4 at the moment
        formatparams = {}
        formatparams.update(colors)
        formatparams.update(values)
        return formatstr.format(**formatparams)

    try:
        with bb.compress.zstd.open(a, "rt", encoding="utf-8", num_threads=1) as f:
            a_data = json.load(f, object_hook=SetDecoder)
    except (TypeError, OSError) as err:
        bb.error("Failed to open sigdata file '%s': %s" % (a, str(err)))
        raise err
    try:
        with bb.compress.zstd.open(b, "rt", encoding="utf-8", num_threads=1) as f:
            b_data = json.load(f, object_hook=SetDecoder)
    except (TypeError, OSError) as err:
        bb.error("Failed to open sigdata file '%s': %s" % (b, str(err)))
        raise err

    for data in [a_data, b_data]:
        handle_renames(data)

    def dict_diff(a, b, ignored_vars=set()):
        sa = set(a.keys())
        sb = set(b.keys())
        common = sa & sb
        changed = set()
        for i in common:
            if a[i] != b[i] and i not in ignored_vars:
                changed.add(i)
        added = sb - sa
        removed = sa - sb
        return changed, added, removed

    def file_checksums_diff(a, b):
        from collections import Counter

        # Convert lists back to tuples
        a = [(f[0], f[1]) for f in a]
        b = [(f[0], f[1]) for f in b]

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

    if 'basehash_ignore_vars' in a_data and a_data['basehash_ignore_vars'] != b_data['basehash_ignore_vars']:
        output.append(color_format("{color_title}basehash_ignore_vars changed{color_default} from '%s' to '%s'") % (a_data['basehash_ignore_vars'], b_data['basehash_ignore_vars']))
        if a_data['basehash_ignore_vars'] and b_data['basehash_ignore_vars']:
            output.append("changed items: %s" % a_data['basehash_ignore_vars'].symmetric_difference(b_data['basehash_ignore_vars']))

    if 'taskhash_ignore_tasks' in a_data and a_data['taskhash_ignore_tasks'] != b_data['taskhash_ignore_tasks']:
        output.append(color_format("{color_title}taskhash_ignore_tasks changed{color_default} from '%s' to '%s'") % (a_data['taskhash_ignore_tasks'], b_data['taskhash_ignore_tasks']))
        if a_data['taskhash_ignore_tasks'] and b_data['taskhash_ignore_tasks']:
            output.append("changed items: %s" % a_data['taskhash_ignore_tasks'].symmetric_difference(b_data['taskhash_ignore_tasks']))

    if a_data['taskdeps'] != b_data['taskdeps']:
        output.append(color_format("{color_title}Task dependencies changed{color_default} from:\n%s\nto:\n%s") % (sorted(a_data['taskdeps']), sorted(b_data['taskdeps'])))

    if a_data['basehash'] != b_data['basehash'] and not collapsed:
        output.append(color_format("{color_title}basehash changed{color_default} from %s to %s") % (a_data['basehash'], b_data['basehash']))

    changed, added, removed = dict_diff(a_data['gendeps'], b_data['gendeps'], a_data['basehash_ignore_vars'] & b_data['basehash_ignore_vars'])
    if changed:
        for dep in sorted(changed):
            output.append(color_format("{color_title}List of dependencies for variable %s changed from '{color_default}%s{color_title}' to '{color_default}%s{color_title}'") % (dep, a_data['gendeps'][dep], b_data['gendeps'][dep]))
            if a_data['gendeps'][dep] and b_data['gendeps'][dep]:
                output.append("changed items: %s" % a_data['gendeps'][dep].symmetric_difference(b_data['gendeps'][dep]))
    if added:
        for dep in sorted(added):
            output.append(color_format("{color_title}Dependency on variable %s was added") % (dep))
    if removed:
        for dep in sorted(removed):
            output.append(color_format("{color_title}Dependency on Variable %s was removed") % (dep))


    changed, added, removed = dict_diff(a_data['varvals'], b_data['varvals'])
    if changed:
        for dep in sorted(changed):
            oldval = a_data['varvals'][dep]
            newval = b_data['varvals'][dep]
            if newval and oldval and ('\n' in oldval or '\n' in newval):
                diff = difflib.unified_diff(oldval.splitlines(), newval.splitlines(), lineterm='')
                # Cut off the first two lines, since we aren't interested in
                # the old/new filename (they are blank anyway in this case)
                difflines = list(diff)[2:]
                if color:
                    # Add colour to diff output
                    for i, line in enumerate(difflines):
                        if line.startswith('+'):
                            line = color_format('{color_add}{line}', line=line)
                            difflines[i] = line
                        elif line.startswith('-'):
                            line = color_format('{color_remove}{line}', line=line)
                            difflines[i] = line
                output.append(color_format("{color_title}Variable {var} value changed:{color_default}\n{diff}", var=dep, diff='\n'.join(difflines)))
            elif newval and oldval and (' ' in oldval or ' ' in newval):
                output.append(color_format("{color_title}Variable {var} value changed:{color_default}\n{diff}", var=dep, diff=worddiff_str(oldval, newval, colors)))
            else:
                output.append(color_format("{color_title}Variable {var} value changed from '{color_default}{oldval}{color_title}' to '{color_default}{newval}{color_title}'{color_default}", var=dep, oldval=oldval, newval=newval))

    if not 'file_checksum_values' in a_data:
         a_data['file_checksum_values'] = []
    if not 'file_checksum_values' in b_data:
         b_data['file_checksum_values'] = []

    changed, added, removed = file_checksums_diff(a_data['file_checksum_values'], b_data['file_checksum_values'])
    if changed:
        for f, old, new in changed:
            output.append(color_format("{color_title}Checksum for file %s changed{color_default} from %s to %s") % (f, old, new))
    if added:
        for f in added:
            output.append(color_format("{color_title}Dependency on checksum of file %s was added") % (f))
    if removed:
        for f in removed:
            output.append(color_format("{color_title}Dependency on checksum of file %s was removed") % (f))

    if not 'runtaskdeps' in a_data:
         a_data['runtaskdeps'] = {}
    if not 'runtaskdeps' in b_data:
         b_data['runtaskdeps'] = {}

    if not collapsed:
        if len(a_data['runtaskdeps']) != len(b_data['runtaskdeps']):
            changed = ["Number of task dependencies changed"]
        else:
            changed = []
            for idx, task in enumerate(a_data['runtaskdeps']):
                a = a_data['runtaskdeps'][idx]
                b = b_data['runtaskdeps'][idx]
                if a_data['runtaskhashes'][a] != b_data['runtaskhashes'][b] and not collapsed:
                    changed.append("%s with hash %s\n changed to\n%s with hash %s" % (a, a_data['runtaskhashes'][a], b, b_data['runtaskhashes'][b]))

        if changed:
            clean_a = a_data['runtaskdeps']
            clean_b = b_data['runtaskdeps']
            if clean_a != clean_b:
                output.append(color_format("{color_title}runtaskdeps changed:{color_default}\n%s") % list_inline_diff(clean_a, clean_b, colors))
            else:
                output.append(color_format("{color_title}runtaskdeps changed:"))
            output.append("\n".join(changed))


    if 'runtaskhashes' in a_data and 'runtaskhashes' in b_data:
        a = a_data['runtaskhashes']
        b = b_data['runtaskhashes']
        changed, added, removed = dict_diff(a, b)
        if added:
            for dep in sorted(added):
                bdep_found = False
                if removed:
                    for bdep in removed:
                        if b[dep] == a[bdep]:
                            #output.append("Dependency on task %s was replaced by %s with same hash" % (dep, bdep))
                            bdep_found = True
                if not bdep_found:
                    output.append(color_format("{color_title}Dependency on task %s was added{color_default} with hash %s") % (dep, b[dep]))
        if removed:
            for dep in sorted(removed):
                adep_found = False
                if added:
                    for adep in added:
                        if b[adep] == a[dep]:
                            #output.append("Dependency on task %s was replaced by %s with same hash" % (adep, dep))
                            adep_found = True
                if not adep_found:
                    output.append(color_format("{color_title}Dependency on task %s was removed{color_default} with hash %s") % (dep, a[dep]))
        if changed:
            for dep in sorted(changed):
                if not collapsed:
                    output.append(color_format("{color_title}Hash for task dependency %s changed{color_default} from %s to %s") % (dep, a[dep], b[dep]))
                if callable(recursecb):
                    recout = recursecb(dep, a[dep], b[dep])
                    if recout:
                        if collapsed:
                            output.extend(recout)
                        else:
                            # If a dependent hash changed, might as well print the line above and then defer to the changes in
                            # that hash since in all likelyhood, they're the same changes this task also saw.
                            output = [output[-1]] + recout
                            break

    a_taint = a_data.get('taint', None)
    b_taint = b_data.get('taint', None)
    if a_taint != b_taint:
        if a_taint and a_taint.startswith('nostamp:'):
            a_taint = a_taint.replace('nostamp:', 'nostamp(uuid4):')
        if b_taint and b_taint.startswith('nostamp:'):
            b_taint = b_taint.replace('nostamp:', 'nostamp(uuid4):')
        output.append(color_format("{color_title}Taint (by forced/invalidated task) changed{color_default} from %s to %s") % (a_taint, b_taint))

    return output


def calc_basehash(sigdata):
    task = sigdata['task']
    basedata = sigdata['varvals'][task]

    if basedata is None:
        basedata = ''

    alldeps = sigdata['taskdeps']
    for dep in sorted(alldeps):
        basedata = basedata + dep
        val = sigdata['varvals'][dep]
        if val is not None:
            basedata = basedata + str(val)

    return hashlib.sha256(basedata.encode("utf-8")).hexdigest()

def calc_taskhash(sigdata):
    data = sigdata['basehash']

    for dep in sigdata['runtaskdeps']:
        data = data + sigdata['runtaskhashes'][dep]

    for c in sigdata['file_checksum_values']:
        if c[1]:
            if "./" in c[0]:
                data = data + c[0]
            data = data + c[1]

    if 'taint' in sigdata:
        if 'nostamp:' in sigdata['taint']:
            data = data + sigdata['taint'][8:]
        else:
            data = data + sigdata['taint']

    return hashlib.sha256(data.encode("utf-8")).hexdigest()


def dump_sigfile(a):
    output = []

    try:
        with bb.compress.zstd.open(a, "rt", encoding="utf-8", num_threads=1) as f:
            a_data = json.load(f, object_hook=SetDecoder)
    except (TypeError, OSError) as err:
        bb.error("Failed to open sigdata file '%s': %s" % (a, str(err)))
        raise err

    handle_renames(a_data)

    output.append("basehash_ignore_vars: %s" % (sorted(a_data['basehash_ignore_vars'])))

    output.append("taskhash_ignore_tasks: %s" % (sorted(a_data['taskhash_ignore_tasks'] or [])))

    output.append("Task dependencies: %s" % (sorted(a_data['taskdeps'])))

    output.append("basehash: %s" % (a_data['basehash']))

    for dep in sorted(a_data['gendeps']):
        output.append("List of dependencies for variable %s is %s" % (dep, sorted(a_data['gendeps'][dep])))

    for dep in sorted(a_data['varvals']):
        output.append("Variable %s value is %s" % (dep, a_data['varvals'][dep]))

    if 'runtaskdeps' in a_data:
        output.append("Tasks this task depends on: %s" % (sorted(a_data['runtaskdeps'])))

    if 'file_checksum_values' in a_data:
        output.append("This task depends on the checksums of files: %s" % (sorted(a_data['file_checksum_values'])))

    if 'runtaskhashes' in a_data:
        for dep in sorted(a_data['runtaskhashes']):
            output.append("Hash for dependent task %s is %s" % (dep, a_data['runtaskhashes'][dep]))

    if 'taint' in a_data:
        if a_data['taint'].startswith('nostamp:'):
            msg = a_data['taint'].replace('nostamp:', 'nostamp(uuid4):')
        else:
            msg = a_data['taint']
        output.append("Tainted (by forced/invalidated task): %s" % msg)

    if 'task' in a_data:
        computed_basehash = calc_basehash(a_data)
        output.append("Computed base hash is %s and from file %s" % (computed_basehash, a_data['basehash']))
    else:
        output.append("Unable to compute base hash")

    computed_taskhash = calc_taskhash(a_data)
    output.append("Computed task hash is %s" % computed_taskhash)

    return output
