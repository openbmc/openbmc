#
# BitBake Cache implementation
#
# Caching of bitbake variables before task execution

# Copyright (C) 2006        Richard Purdie
# Copyright (C) 2012        Intel Corporation

# but small sections based on code from bin/bitbake:
# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
# Copyright (C) 2003 - 2005 Michael 'Mickey' Lauer
# Copyright (C) 2005        Holger Hans Peter Freyther
# Copyright (C) 2005        ROAD GmbH
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import logging
import pickle
from collections import defaultdict
from collections.abc import Mapping
import bb.utils
from bb import PrefixLoggerAdapter
import re
import shutil

logger = logging.getLogger("BitBake.Cache")

__cache_version__ = "155"

def getCacheFile(path, filename, mc, data_hash):
    mcspec = ''
    if mc:
        mcspec = ".%s" % mc
    return os.path.join(path, filename + mcspec + "." + data_hash)

# RecipeInfoCommon defines common data retrieving methods
# from meta data for caches. CoreRecipeInfo as well as other
# Extra RecipeInfo needs to inherit this class
class RecipeInfoCommon(object):

    @classmethod
    def listvar(cls, var, metadata):
        return cls.getvar(var, metadata).split()

    @classmethod
    def intvar(cls, var, metadata):
        return int(cls.getvar(var, metadata) or 0)

    @classmethod
    def depvar(cls, var, metadata):
        return bb.utils.explode_deps(cls.getvar(var, metadata))

    @classmethod
    def pkgvar(cls, var, packages, metadata):
        return dict((pkg, cls.depvar("%s:%s" % (var, pkg), metadata))
                    for pkg in packages)

    @classmethod
    def taskvar(cls, var, tasks, metadata):
        return dict((task, cls.getvar("%s:task-%s" % (var, task), metadata))
                    for task in tasks)

    @classmethod
    def flaglist(cls, flag, varlist, metadata, squash=False):
        out_dict = dict((var, metadata.getVarFlag(var, flag))
                    for var in varlist)
        if squash:
            return dict((k,v) for (k,v) in out_dict.items() if v)
        else:
            return out_dict

    @classmethod
    def getvar(cls, var, metadata, expand = True):
        return metadata.getVar(var, expand) or ''


class CoreRecipeInfo(RecipeInfoCommon):
    __slots__ = ()

    cachefile = "bb_cache.dat"

    def __init__(self, filename, metadata):
        self.file_depends = metadata.getVar('__depends', False)
        self.timestamp = bb.parse.cached_mtime(filename)
        self.variants = self.listvar('__VARIANTS', metadata) + ['']
        self.appends = self.listvar('__BBAPPEND', metadata)
        self.nocache = self.getvar('BB_DONT_CACHE', metadata)

        self.provides  = self.depvar('PROVIDES', metadata)
        self.rprovides = self.depvar('RPROVIDES', metadata)
        self.pn = self.getvar('PN', metadata) or bb.parse.vars_from_file(filename,metadata)[0]
        self.packages = self.listvar('PACKAGES', metadata)
        if not self.packages:
            self.packages.append(self.pn)
        self.packages_dynamic = self.listvar('PACKAGES_DYNAMIC', metadata)
        self.rprovides_pkg = self.pkgvar('RPROVIDES', self.packages, metadata)

        self.skipreason = self.getvar('__SKIPPED', metadata)
        if self.skipreason:
            self.skipped = True
            return

        self.tasks = metadata.getVar('__BBTASKS', False)

        self.basetaskhashes = metadata.getVar('__siggen_basehashes', False) or {}
        self.hashfilename = self.getvar('BB_HASHFILENAME', metadata)

        self.task_deps = metadata.getVar('_task_deps', False) or {'tasks': [], 'parents': {}}

        self.skipped = False
        self.pe = self.getvar('PE', metadata)
        self.pv = self.getvar('PV', metadata)
        self.pr = self.getvar('PR', metadata)
        self.defaultpref = self.intvar('DEFAULT_PREFERENCE', metadata)
        self.not_world = self.getvar('EXCLUDE_FROM_WORLD', metadata)
        self.stamp = self.getvar('STAMP', metadata)
        self.stampclean = self.getvar('STAMPCLEAN', metadata)
        self.stamp_extrainfo = self.flaglist('stamp-extra-info', self.tasks, metadata)
        self.file_checksums = self.flaglist('file-checksums', self.tasks, metadata, True)
        self.depends          = self.depvar('DEPENDS', metadata)
        self.rdepends         = self.depvar('RDEPENDS', metadata)
        self.rrecommends      = self.depvar('RRECOMMENDS', metadata)
        self.rdepends_pkg     = self.pkgvar('RDEPENDS', self.packages, metadata)
        self.rrecommends_pkg  = self.pkgvar('RRECOMMENDS', self.packages, metadata)
        self.inherits         = self.getvar('__inherit_cache', metadata, expand=False)
        self.fakerootenv      = self.getvar('FAKEROOTENV', metadata)
        self.fakerootdirs     = self.getvar('FAKEROOTDIRS', metadata)
        self.fakerootlogs     = self.getvar('FAKEROOTLOGS', metadata)
        self.fakerootnoenv    = self.getvar('FAKEROOTNOENV', metadata)
        self.extradepsfunc    = self.getvar('calculate_extra_depends', metadata)

    @classmethod
    def init_cacheData(cls, cachedata):
        # CacheData in Core RecipeInfo Class
        cachedata.task_deps = {}
        cachedata.pkg_fn = {}
        cachedata.pkg_pn = defaultdict(list)
        cachedata.pkg_pepvpr = {}
        cachedata.pkg_dp = {}

        cachedata.stamp = {}
        cachedata.stampclean = {}
        cachedata.stamp_extrainfo = {}
        cachedata.file_checksums = {}
        cachedata.fn_provides = {}
        cachedata.pn_provides = defaultdict(list)
        cachedata.all_depends = []

        cachedata.deps = defaultdict(list)
        cachedata.packages = defaultdict(list)
        cachedata.providers = defaultdict(list)
        cachedata.rproviders = defaultdict(list)
        cachedata.packages_dynamic = defaultdict(list)

        cachedata.rundeps = defaultdict(lambda: defaultdict(list))
        cachedata.runrecs = defaultdict(lambda: defaultdict(list))
        cachedata.possible_world = []
        cachedata.universe_target = []
        cachedata.hashfn = {}

        cachedata.basetaskhash = {}
        cachedata.inherits = {}
        cachedata.fakerootenv = {}
        cachedata.fakerootnoenv = {}
        cachedata.fakerootdirs = {}
        cachedata.fakerootlogs = {}
        cachedata.extradepsfunc = {}

    def add_cacheData(self, cachedata, fn):
        cachedata.task_deps[fn] = self.task_deps
        cachedata.pkg_fn[fn] = self.pn
        cachedata.pkg_pn[self.pn].append(fn)
        cachedata.pkg_pepvpr[fn] = (self.pe, self.pv, self.pr)
        cachedata.pkg_dp[fn] = self.defaultpref
        cachedata.stamp[fn] = self.stamp
        cachedata.stampclean[fn] = self.stampclean
        cachedata.stamp_extrainfo[fn] = self.stamp_extrainfo
        cachedata.file_checksums[fn] = self.file_checksums

        provides = [self.pn]
        for provide in self.provides:
            if provide not in provides:
                provides.append(provide)
        cachedata.fn_provides[fn] = provides

        for provide in provides:
            cachedata.providers[provide].append(fn)
            if provide not in cachedata.pn_provides[self.pn]:
                cachedata.pn_provides[self.pn].append(provide)

        for dep in self.depends:
            if dep not in cachedata.deps[fn]:
                cachedata.deps[fn].append(dep)
            if dep not in cachedata.all_depends:
                cachedata.all_depends.append(dep)

        rprovides = self.rprovides
        for package in self.packages:
            cachedata.packages[package].append(fn)
            rprovides += self.rprovides_pkg[package]

        for rprovide in rprovides:
            if fn not in cachedata.rproviders[rprovide]:
                cachedata.rproviders[rprovide].append(fn)

        for package in self.packages_dynamic:
            cachedata.packages_dynamic[package].append(fn)

        # Build hash of runtime depends and recommends
        for package in self.packages:
            cachedata.rundeps[fn][package] = list(self.rdepends) + self.rdepends_pkg[package]
            cachedata.runrecs[fn][package] = list(self.rrecommends) + self.rrecommends_pkg[package]

        # Collect files we may need for possible world-dep
        # calculations
        if not bb.utils.to_boolean(self.not_world):
            cachedata.possible_world.append(fn)
        #else:
        #    logger.debug2("EXCLUDE FROM WORLD: %s", fn)

        # create a collection of all targets for sanity checking
        # tasks, such as upstream versions, license, and tools for
        # task and image creation.
        cachedata.universe_target.append(self.pn)

        cachedata.hashfn[fn] = self.hashfilename
        for task, taskhash in self.basetaskhashes.items():
            identifier = '%s:%s' % (fn, task)
            cachedata.basetaskhash[identifier] = taskhash

        cachedata.inherits[fn] = self.inherits
        cachedata.fakerootenv[fn] = self.fakerootenv
        cachedata.fakerootnoenv[fn] = self.fakerootnoenv
        cachedata.fakerootdirs[fn] = self.fakerootdirs
        cachedata.fakerootlogs[fn] = self.fakerootlogs
        cachedata.extradepsfunc[fn] = self.extradepsfunc


class SiggenRecipeInfo(RecipeInfoCommon):
    __slots__ = ()

    classname = "SiggenRecipeInfo"
    cachefile = "bb_cache_" + classname +".dat"
    # we don't want to show this information in graph files so don't set cachefields
    #cachefields = []

    def __init__(self, filename, metadata):
        self.siggen_gendeps = metadata.getVar("__siggen_gendeps", False)
        self.siggen_varvals = metadata.getVar("__siggen_varvals", False)
        self.siggen_taskdeps = metadata.getVar("__siggen_taskdeps", False)

    @classmethod
    def init_cacheData(cls, cachedata):
        cachedata.siggen_taskdeps = {}
        cachedata.siggen_gendeps = {}
        cachedata.siggen_varvals = {}

    def add_cacheData(self, cachedata, fn):
        cachedata.siggen_gendeps[fn] = self.siggen_gendeps
        cachedata.siggen_varvals[fn] = self.siggen_varvals
        cachedata.siggen_taskdeps[fn] = self.siggen_taskdeps

    # The siggen variable data is large and impacts:
    #  - bitbake's overall memory usage
    #  - the amount of data sent over IPC between parsing processes and the server
    #  - the size of the cache files on disk
    #  - the size of "sigdata" hash information files on disk
    # The data consists of strings (some large) or frozenset lists of variables
    # As such, we a) deplicate the data here and b) pass references to the object at second
    # access (e.g. over IPC or saving into pickle).

    store = {}
    save_map = {}
    save_count = 1
    restore_map = {}
    restore_count = {}

    @classmethod
    def reset(cls):
        # Needs to be called before starting new streamed data in a given process 
        # (e.g. writing out the cache again)
        cls.save_map = {}
        cls.save_count = 1
        cls.restore_map = {}

    @classmethod
    def _save(cls, deps):
        ret = []
        if not deps:
            return deps
        for dep in deps:
            fs = deps[dep]
            if fs is None:
                ret.append((dep, None, None))
            elif fs in cls.save_map:
                ret.append((dep, None, cls.save_map[fs]))
            else:
                cls.save_map[fs] = cls.save_count
                ret.append((dep, fs, cls.save_count))
                cls.save_count = cls.save_count + 1
        return ret

    @classmethod
    def _restore(cls, deps, pid):
        ret = {}
        if not deps:
            return deps
        if pid not in cls.restore_map:
            cls.restore_map[pid] = {}
        map = cls.restore_map[pid]
        for dep, fs, mapnum in deps:
            if fs is None and mapnum is None:
                ret[dep] = None
            elif fs is None:
                ret[dep] = map[mapnum]
            else:
                try:
                    fs = cls.store[fs]
                except KeyError:
                    cls.store[fs] = fs
                map[mapnum] = fs
                ret[dep] = fs
        return ret

    def __getstate__(self):
        ret = {}
        for key in ["siggen_gendeps", "siggen_taskdeps", "siggen_varvals"]:
            ret[key] = self._save(self.__dict__[key])
        ret['pid'] = os.getpid()
        return ret

    def __setstate__(self, state):
        pid = state['pid']
        for key in ["siggen_gendeps", "siggen_taskdeps", "siggen_varvals"]:
            setattr(self, key, self._restore(state[key], pid))


def virtualfn2realfn(virtualfn):
    """
    Convert a virtual file name to a real one + the associated subclass keyword
    """
    mc = ""
    if virtualfn.startswith('mc:') and virtualfn.count(':') >= 2:
        (_, mc, virtualfn) = virtualfn.split(':', 2)

    fn = virtualfn
    cls = ""
    if virtualfn.startswith('virtual:'):
        elems = virtualfn.split(':')
        cls = ":".join(elems[1:-1])
        fn = elems[-1]

    return (fn, cls, mc)

def realfn2virtual(realfn, cls, mc):
    """
    Convert a real filename + the associated subclass keyword to a virtual filename
    """
    if cls:
        realfn = "virtual:" + cls + ":" + realfn
    if mc:
        realfn = "mc:" + mc + ":" + realfn
    return realfn

def variant2virtual(realfn, variant):
    """
    Convert a real filename + a variant to a virtual filename
    """
    if variant == "":
        return realfn
    if variant.startswith("mc:") and variant.count(':') >= 2:
        elems = variant.split(":")
        if elems[2]:
            return "mc:" + elems[1] + ":virtual:" + ":".join(elems[2:]) + ":" + realfn
        return "mc:" + elems[1] + ":" + realfn
    return "virtual:" + variant + ":" + realfn

#
# Cooker calls cacheValid on its recipe list, then either calls loadCached
# from it's main thread or parse from separate processes to generate an up to
# date cache
#
class Cache(object):
    """
    BitBake Cache implementation
    """
    def __init__(self, databuilder, mc, data_hash, caches_array):
        self.databuilder = databuilder
        self.data = databuilder.data

        # Pass caches_array information into Cache Constructor
        # It will be used later for deciding whether we
        # need extra cache file dump/load support
        self.mc = mc
        self.logger = PrefixLoggerAdapter("Cache: %s: " % (mc if mc else "default"), logger)
        self.caches_array = caches_array
        self.cachedir = self.data.getVar("CACHE")
        self.clean = set()
        self.checked = set()
        self.depends_cache = {}
        self.data_fn = None
        self.cacheclean = True
        self.data_hash = data_hash
        self.filelist_regex = re.compile(r'(?:(?<=:True)|(?<=:False))\s+')

        if self.cachedir in [None, '']:
            bb.fatal("Please ensure CACHE is set to the cache directory for BitBake to use")

    def getCacheFile(self, cachefile):
        return getCacheFile(self.cachedir, cachefile, self.mc, self.data_hash)

    def prepare_cache(self, progress):
        loaded = 0

        self.cachefile = self.getCacheFile("bb_cache.dat")

        self.logger.debug("Cache dir: %s", self.cachedir)
        bb.utils.mkdirhier(self.cachedir)

        cache_ok = True
        if self.caches_array:
            for cache_class in self.caches_array:
                cachefile = self.getCacheFile(cache_class.cachefile)
                cache_exists = os.path.exists(cachefile)
                self.logger.debug2("Checking if %s exists: %r", cachefile, cache_exists)
                cache_ok = cache_ok and cache_exists
                cache_class.init_cacheData(self)
        if cache_ok:
            loaded = self.load_cachefile(progress)
        elif os.path.isfile(self.cachefile):
            self.logger.info("Out of date cache found, rebuilding...")
        else:
            self.logger.debug("Cache file %s not found, building..." % self.cachefile)

        # We don't use the symlink, its just for debugging convinience
        if self.mc:
            symlink = os.path.join(self.cachedir, "bb_cache.dat.%s" % self.mc)
        else:
            symlink = os.path.join(self.cachedir, "bb_cache.dat")

        if os.path.exists(symlink):
            bb.utils.remove(symlink)
        try:
            os.symlink(os.path.basename(self.cachefile), symlink)
        except OSError:
            pass

        return loaded

    def cachesize(self):
        cachesize = 0
        for cache_class in self.caches_array:
            cachefile = self.getCacheFile(cache_class.cachefile)
            try:
                with open(cachefile, "rb") as cachefile:
                    cachesize += os.fstat(cachefile.fileno()).st_size
            except FileNotFoundError:
                pass

        return cachesize

    def load_cachefile(self, progress):
        previous_progress = 0

        for cache_class in self.caches_array:
            cachefile = self.getCacheFile(cache_class.cachefile)
            self.logger.debug('Loading cache file: %s' % cachefile)
            with open(cachefile, "rb") as cachefile:
                pickled = pickle.Unpickler(cachefile)
                # Check cache version information
                try:
                    cache_ver = pickled.load()
                    bitbake_ver = pickled.load()
                except Exception:
                    self.logger.info('Invalid cache, rebuilding...')
                    return 0

                if cache_ver != __cache_version__:
                    self.logger.info('Cache version mismatch, rebuilding...')
                    return 0
                elif bitbake_ver != bb.__version__:
                    self.logger.info('Bitbake version mismatch, rebuilding...')
                    return 0

                # Load the rest of the cache file
                current_progress = 0
                while cachefile:
                    try:
                        key = pickled.load()
                        value = pickled.load()
                    except Exception:
                        break
                    if not isinstance(key, str):
                        bb.warn("%s from extras cache is not a string?" % key)
                        break
                    if not isinstance(value, RecipeInfoCommon):
                        bb.warn("%s from extras cache is not a RecipeInfoCommon class?" % value)
                        break

                    if key in self.depends_cache:
                        self.depends_cache[key].append(value)
                    else:
                        self.depends_cache[key] = [value]
                    # only fire events on even percentage boundaries
                    current_progress = cachefile.tell() + previous_progress
                    progress(cachefile.tell() + previous_progress)

                previous_progress += current_progress

        return len(self.depends_cache)

    def parse(self, filename, appends, layername):
        """Parse the specified filename, returning the recipe information"""
        self.logger.debug("Parsing %s", filename)
        infos = []
        datastores = self.databuilder.parseRecipeVariants(filename, appends, mc=self.mc, layername=layername)
        depends = []
        variants = []
        # Process the "real" fn last so we can store variants list
        for variant, data in sorted(datastores.items(),
                                    key=lambda i: i[0],
                                    reverse=True):
            virtualfn = variant2virtual(filename, variant)
            variants.append(variant)
            depends = depends + (data.getVar("__depends", False) or [])
            if depends and not variant:
                data.setVar("__depends", depends)
            if virtualfn == filename:
                data.setVar("__VARIANTS", " ".join(variants))
            info_array = []
            for cache_class in self.caches_array:
                info = cache_class(filename, data)
                info_array.append(info)
            infos.append((virtualfn, info_array))

        return infos

    def loadCached(self, filename, appends):
        """Obtain the recipe information for the specified filename,
        using cached values.
        """

        infos = []
        # info_array item is a list of [CoreRecipeInfo, XXXRecipeInfo]
        info_array = self.depends_cache[filename]
        for variant in info_array[0].variants:
            virtualfn = variant2virtual(filename, variant)
            infos.append((virtualfn, self.depends_cache[virtualfn]))

        return infos

    def cacheValid(self, fn, appends):
        """
        Is the cache valid for fn?
        Fast version, no timestamps checked.
        """
        if fn not in self.checked:
            self.cacheValidUpdate(fn, appends)
        if fn in self.clean:
            return True
        return False

    def cacheValidUpdate(self, fn, appends):
        """
        Is the cache valid for fn?
        Make thorough (slower) checks including timestamps.
        """
        self.checked.add(fn)

        # File isn't in depends_cache
        if not fn in self.depends_cache:
            self.logger.debug2("%s is not cached", fn)
            return False

        mtime = bb.parse.cached_mtime_noerror(fn)

        # Check file still exists
        if mtime == 0:
            self.logger.debug2("%s no longer exists", fn)
            self.remove(fn)
            return False

        info_array = self.depends_cache[fn]
        # Check the file's timestamp
        if mtime != info_array[0].timestamp:
            self.logger.debug2("%s changed", fn)
            self.remove(fn)
            return False

        # Check dependencies are still valid
        depends = info_array[0].file_depends
        if depends:
            for f, old_mtime in depends:
                fmtime = bb.parse.cached_mtime_noerror(f)
                # Check if file still exists
                if old_mtime != 0 and fmtime == 0:
                    self.logger.debug2("%s's dependency %s was removed",
                                         fn, f)
                    self.remove(fn)
                    return False

                if (fmtime != old_mtime):
                    self.logger.debug2("%s's dependency %s changed",
                                         fn, f)
                    self.remove(fn)
                    return False

        if hasattr(info_array[0], 'file_checksums'):
            for _, fl in info_array[0].file_checksums.items():
                fl = fl.strip()
                if not fl:
                    continue
                # Have to be careful about spaces and colons in filenames
                flist = self.filelist_regex.split(fl)
                for f in flist:
                    if not f:
                        continue
                    f, exist = f.rsplit(":", 1)
                    if (exist == "True" and not os.path.exists(f)) or (exist == "False" and os.path.exists(f)):
                        self.logger.debug2("%s's file checksum list file %s changed",
                                             fn, f)
                        self.remove(fn)
                        return False

        if tuple(appends) != tuple(info_array[0].appends):
            self.logger.debug2("appends for %s changed", fn)
            self.logger.debug2("%s to %s" % (str(appends), str(info_array[0].appends)))
            self.remove(fn)
            return False

        invalid = False
        for cls in info_array[0].variants:
            virtualfn = variant2virtual(fn, cls)
            self.clean.add(virtualfn)
            if virtualfn not in self.depends_cache:
                self.logger.debug2("%s is not cached", virtualfn)
                invalid = True
            elif len(self.depends_cache[virtualfn]) != len(self.caches_array):
                self.logger.debug2("Extra caches missing for %s?" % virtualfn)
                invalid = True

        # If any one of the variants is not present, mark as invalid for all
        if invalid:
            for cls in info_array[0].variants:
                virtualfn = variant2virtual(fn, cls)
                if virtualfn in self.clean:
                    self.logger.debug2("Removing %s from cache", virtualfn)
                    self.clean.remove(virtualfn)
            if fn in self.clean:
                self.logger.debug2("Marking %s as not clean", fn)
                self.clean.remove(fn)
            return False

        self.clean.add(fn)
        return True

    def remove(self, fn):
        """
        Remove a fn from the cache
        Called from the parser in error cases
        """
        if fn in self.depends_cache:
            self.logger.debug("Removing %s from cache", fn)
            del self.depends_cache[fn]
        if fn in self.clean:
            self.logger.debug("Marking %s as unclean", fn)
            self.clean.remove(fn)

    def sync(self):
        """
        Save the cache
        Called from the parser when complete (or exiting)
        """
        if self.cacheclean:
            self.logger.debug2("Cache is clean, not saving.")
            return

        for cache_class in self.caches_array:
            cache_class_name = cache_class.__name__
            cachefile = self.getCacheFile(cache_class.cachefile)
            self.logger.debug2("Writing %s", cachefile)
            with open(cachefile, "wb") as f:
                p = pickle.Pickler(f, pickle.HIGHEST_PROTOCOL)
                p.dump(__cache_version__)
                p.dump(bb.__version__)

                for key, info_array in self.depends_cache.items():
                    for info in info_array:
                        if isinstance(info, RecipeInfoCommon) and info.__class__.__name__ == cache_class_name:
                            p.dump(key)
                            p.dump(info)

        del self.depends_cache
        SiggenRecipeInfo.reset()

    @staticmethod
    def mtime(cachefile):
        return bb.parse.cached_mtime_noerror(cachefile)

    def add_info(self, filename, info_array, cacheData, parsed=None, watcher=None):
        if self.mc is not None:
            (fn, cls, mc) = virtualfn2realfn(filename)
            if mc:
                self.logger.error("Unexpected multiconfig %s", filename)
                return

            vfn = realfn2virtual(fn, cls, self.mc)
        else:
            vfn = filename

        if isinstance(info_array[0], CoreRecipeInfo) and (not info_array[0].skipped):
            cacheData.add_from_recipeinfo(vfn, info_array)

            if watcher:
                watcher(info_array[0].file_depends)

        if (info_array[0].skipped or 'SRCREVINACTION' not in info_array[0].pv) and not info_array[0].nocache:
            if parsed:
                self.cacheclean = False
            self.depends_cache[filename] = info_array

class MulticonfigCache(Mapping):
    def __init__(self, databuilder, data_hash, caches_array):
        def progress(p):
            nonlocal current_progress
            nonlocal previous_progress
            nonlocal previous_percent
            nonlocal cachesize

            current_progress = previous_progress + p

            if current_progress > cachesize:
                # we might have calculated incorrect total size because a file
                # might've been written out just after we checked its size
                cachesize = current_progress
            current_percent = 100 * current_progress / cachesize
            if current_percent > previous_percent:
                previous_percent = current_percent
                bb.event.fire(bb.event.CacheLoadProgress(current_progress, cachesize),
                                databuilder.data)


        cachesize = 0
        current_progress = 0
        previous_progress = 0
        previous_percent = 0
        self.__caches = {}

        for mc, mcdata in databuilder.mcdata.items():
            self.__caches[mc] = Cache(databuilder, mc, data_hash, caches_array)

            cachesize += self.__caches[mc].cachesize()

        bb.event.fire(bb.event.CacheLoadStarted(cachesize), databuilder.data)
        loaded = 0

        for c in self.__caches.values():
            SiggenRecipeInfo.reset()
            loaded += c.prepare_cache(progress)
            previous_progress = current_progress

        # Note: depends cache number is corresponding to the parsing file numbers.
        # The same file has several caches, still regarded as one item in the cache
        bb.event.fire(bb.event.CacheLoadCompleted(cachesize, loaded), databuilder.data)

    def __len__(self):
        return len(self.__caches)

    def __getitem__(self, key):
        return self.__caches[key]

    def __contains__(self, key):
        return key in self.__caches

    def __iter__(self):
        for k in self.__caches:
            yield k

def init(cooker):
    """
    The Objective: Cache the minimum amount of data possible yet get to the
    stage of building packages (i.e. tryBuild) without reparsing any .bb files.

    To do this, we intercept getVar calls and only cache the variables we see
    being accessed. We rely on the cache getVar calls being made for all
    variables bitbake might need to use to reach this stage. For each cached
    file we need to track:

    * Its mtime
    * The mtimes of all its dependencies
    * Whether it caused a parse.SkipRecipe exception

    Files causing parsing errors are evicted from the cache.

    """
    return Cache(cooker.configuration.data, cooker.configuration.data_hash)


class CacheData(object):
    """
    The data structures we compile from the cached data
    """

    def __init__(self, caches_array):
        self.caches_array = caches_array
        for cache_class in self.caches_array:
            if not issubclass(cache_class, RecipeInfoCommon):
                bb.error("Extra cache data class %s should subclass RecipeInfoCommon class" % cache_class)
            cache_class.init_cacheData(self)

        # Direct cache variables
        self.task_queues = {}
        self.preferred = {}
        self.tasks = {}
        # Indirect Cache variables (set elsewhere)
        self.ignored_dependencies = []
        self.world_target = set()
        self.bbfile_priority = {}

    def add_from_recipeinfo(self, fn, info_array):
        for info in info_array:
            info.add_cacheData(self, fn)

class MultiProcessCache(object):
    """
    BitBake multi-process cache implementation

    Used by the codeparser & file checksum caches
    """

    def __init__(self):
        self.cachefile = None
        self.cachedata = self.create_cachedata()
        self.cachedata_extras = self.create_cachedata()

    def init_cache(self, cachedir, cache_file_name=None):
        if not cachedir:
            return

        bb.utils.mkdirhier(cachedir)
        self.cachefile = os.path.join(cachedir,
                                      cache_file_name or self.__class__.cache_file_name)
        logger.debug("Using cache in '%s'", self.cachefile)

        glf = bb.utils.lockfile(self.cachefile + ".lock")

        try:
            with open(self.cachefile, "rb") as f:
                p = pickle.Unpickler(f)
                data, version = p.load()
        except:
            bb.utils.unlockfile(glf)
            return

        bb.utils.unlockfile(glf)

        if version != self.__class__.CACHE_VERSION:
            return

        self.cachedata = data

    def create_cachedata(self):
        data = [{}]
        return data

    def save_extras(self):
        if not self.cachefile:
            return

        have_data = any(self.cachedata_extras)
        if not have_data:
            return

        glf = bb.utils.lockfile(self.cachefile + ".lock", shared=True)

        i = os.getpid()
        lf = None
        while not lf:
            lf = bb.utils.lockfile(self.cachefile + ".lock." + str(i), retry=False)
            if not lf or os.path.exists(self.cachefile + "-" + str(i)):
                if lf:
                    bb.utils.unlockfile(lf)
                    lf = None
                i = i + 1
                continue

            with open(self.cachefile + "-" + str(i), "wb") as f:
                p = pickle.Pickler(f, -1)
                p.dump([self.cachedata_extras, self.__class__.CACHE_VERSION])

        bb.utils.unlockfile(lf)
        bb.utils.unlockfile(glf)

    def merge_data(self, source, dest):
        for j in range(0,len(dest)):
            for h in source[j]:
                if h not in dest[j]:
                    dest[j][h] = source[j][h]

    def save_merge(self):
        if not self.cachefile:
            return

        glf = bb.utils.lockfile(self.cachefile + ".lock")

        data = self.cachedata

        have_data = False

        for f in [y for y in os.listdir(os.path.dirname(self.cachefile)) if y.startswith(os.path.basename(self.cachefile) + '-')]:
            f = os.path.join(os.path.dirname(self.cachefile), f)
            try:
                with open(f, "rb") as fd:
                    p = pickle.Unpickler(fd)
                    extradata, version = p.load()
            except (IOError, EOFError):
                os.unlink(f)
                continue

            if version != self.__class__.CACHE_VERSION:
                os.unlink(f)
                continue

            have_data = True
            self.merge_data(extradata, data)
            os.unlink(f)

        if have_data:
            with open(self.cachefile, "wb") as f:
                p = pickle.Pickler(f, -1)
                p.dump([data, self.__class__.CACHE_VERSION])

        bb.utils.unlockfile(glf)


class SimpleCache(object):
    """
    BitBake multi-process cache implementation

    Used by the codeparser & file checksum caches
    """

    def __init__(self, version):
        self.cachefile = None
        self.cachedata = None
        self.cacheversion = version

    def init_cache(self, d, cache_file_name=None, defaultdata=None):
        cachedir = (d.getVar("PERSISTENT_DIR") or
                    d.getVar("CACHE"))
        if not cachedir:
            return defaultdata

        bb.utils.mkdirhier(cachedir)
        self.cachefile = os.path.join(cachedir,
                                      cache_file_name or self.__class__.cache_file_name)
        logger.debug("Using cache in '%s'", self.cachefile)

        glf = bb.utils.lockfile(self.cachefile + ".lock")

        try:
            with open(self.cachefile, "rb") as f:
                p = pickle.Unpickler(f)
                data, version = p.load()
        except:
            bb.utils.unlockfile(glf)
            return defaultdata

        bb.utils.unlockfile(glf)

        if version != self.cacheversion:
            return defaultdata

        return data

    def save(self, data):
        if not self.cachefile:
            return

        glf = bb.utils.lockfile(self.cachefile + ".lock")

        with open(self.cachefile, "wb") as f:
            p = pickle.Pickler(f, -1)
            p.dump([data, self.cacheversion])

        bb.utils.unlockfile(glf)

    def copyfile(self, target):
        if not self.cachefile:
            return

        glf = bb.utils.lockfile(self.cachefile + ".lock")
        shutil.copy(self.cachefile, target)
        bb.utils.unlockfile(glf)
