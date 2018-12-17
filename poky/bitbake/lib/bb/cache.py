# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
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
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import os
import sys
import logging
import pickle
from collections import defaultdict
import bb.utils

logger = logging.getLogger("BitBake.Cache")

__cache_version__ = "152"

def getCacheFile(path, filename, data_hash):
    return os.path.join(path, filename + "." + data_hash)

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
        return dict((pkg, cls.depvar("%s_%s" % (var, pkg), metadata))
                    for pkg in packages)

    @classmethod
    def taskvar(cls, var, tasks, metadata):
        return dict((task, cls.getvar("%s_task-%s" % (var, task), metadata))
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

        self.skipreason = self.getvar('__SKIPPED', metadata)
        if self.skipreason:
            self.pn = self.getvar('PN', metadata) or bb.parse.BBHandler.vars_from_file(filename,metadata)[0]
            self.skipped = True
            self.provides  = self.depvar('PROVIDES', metadata)
            self.rprovides = self.depvar('RPROVIDES', metadata)
            return

        self.tasks = metadata.getVar('__BBTASKS', False)

        self.pn = self.getvar('PN', metadata)
        self.packages = self.listvar('PACKAGES', metadata)
        if not self.packages:
            self.packages.append(self.pn)

        self.basetaskhashes = self.taskvar('BB_BASEHASH', self.tasks, metadata)
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
        self.packages_dynamic = self.listvar('PACKAGES_DYNAMIC', metadata)
        self.depends          = self.depvar('DEPENDS', metadata)
        self.provides         = self.depvar('PROVIDES', metadata)
        self.rdepends         = self.depvar('RDEPENDS', metadata)
        self.rprovides        = self.depvar('RPROVIDES', metadata)
        self.rrecommends      = self.depvar('RRECOMMENDS', metadata)
        self.rprovides_pkg    = self.pkgvar('RPROVIDES', self.packages, metadata)
        self.rdepends_pkg     = self.pkgvar('RDEPENDS', self.packages, metadata)
        self.rrecommends_pkg  = self.pkgvar('RRECOMMENDS', self.packages, metadata)
        self.inherits         = self.getvar('__inherit_cache', metadata, expand=False)
        self.fakerootenv      = self.getvar('FAKEROOTENV', metadata)
        self.fakerootdirs     = self.getvar('FAKEROOTDIRS', metadata)
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
        if self.not_world:
            logger.debug(1, "EXCLUDE FROM WORLD: %s", fn)
        else:
            cachedata.possible_world.append(fn)

        # create a collection of all targets for sanity checking
        # tasks, such as upstream versions, license, and tools for
        # task and image creation.
        cachedata.universe_target.append(self.pn)

        cachedata.hashfn[fn] = self.hashfilename
        for task, taskhash in self.basetaskhashes.items():
            identifier = '%s.%s' % (fn, task)
            cachedata.basetaskhash[identifier] = taskhash

        cachedata.inherits[fn] = self.inherits
        cachedata.fakerootenv[fn] = self.fakerootenv
        cachedata.fakerootnoenv[fn] = self.fakerootnoenv
        cachedata.fakerootdirs[fn] = self.fakerootdirs
        cachedata.extradepsfunc[fn] = self.extradepsfunc

def virtualfn2realfn(virtualfn):
    """
    Convert a virtual file name to a real one + the associated subclass keyword
    """
    mc = ""
    if virtualfn.startswith('multiconfig:'):
        elems = virtualfn.split(':')
        mc = elems[1]
        virtualfn = ":".join(elems[2:])

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
        realfn = "multiconfig:" + mc + ":" + realfn
    return realfn

def variant2virtual(realfn, variant):
    """
    Convert a real filename + the associated subclass keyword to a virtual filename
    """
    if variant == "":
        return realfn
    if variant.startswith("multiconfig:"):
        elems = variant.split(":")
        if elems[2]:
            return "multiconfig:" + elems[1] + ":virtual:" + ":".join(elems[2:]) + ":" + realfn
        return "multiconfig:" + elems[1] + ":" + realfn
    return "virtual:" + variant + ":" + realfn

def parse_recipe(bb_data, bbfile, appends, mc=''):
    """
    Parse a recipe
    """

    chdir_back = False

    bb_data.setVar("__BBMULTICONFIG", mc)

    # expand tmpdir to include this topdir
    bb_data.setVar('TMPDIR', bb_data.getVar('TMPDIR') or "")
    bbfile_loc = os.path.abspath(os.path.dirname(bbfile))
    oldpath = os.path.abspath(os.getcwd())
    bb.parse.cached_mtime_noerror(bbfile_loc)

    # The ConfHandler first looks if there is a TOPDIR and if not
    # then it would call getcwd().
    # Previously, we chdir()ed to bbfile_loc, called the handler
    # and finally chdir()ed back, a couple of thousand times. We now
    # just fill in TOPDIR to point to bbfile_loc if there is no TOPDIR yet.
    if not bb_data.getVar('TOPDIR', False):
        chdir_back = True
        bb_data.setVar('TOPDIR', bbfile_loc)
    try:
        if appends:
            bb_data.setVar('__BBAPPEND', " ".join(appends))
        bb_data = bb.parse.handle(bbfile, bb_data)
        if chdir_back:
            os.chdir(oldpath)
        return bb_data
    except:
        if chdir_back:
            os.chdir(oldpath)
        raise



class NoCache(object):

    def __init__(self, databuilder):
        self.databuilder = databuilder
        self.data = databuilder.data

    def loadDataFull(self, virtualfn, appends):
        """
        Return a complete set of data for fn.
        To do this, we need to parse the file.
        """
        logger.debug(1, "Parsing %s (full)" % virtualfn)
        (fn, virtual, mc) = virtualfn2realfn(virtualfn)
        bb_data = self.load_bbfile(virtualfn, appends, virtonly=True)
        return bb_data[virtual]

    def load_bbfile(self, bbfile, appends, virtonly = False):
        """
        Load and parse one .bb build file
        Return the data and whether parsing resulted in the file being skipped
        """

        if virtonly:
            (bbfile, virtual, mc) = virtualfn2realfn(bbfile)
            bb_data = self.databuilder.mcdata[mc].createCopy()
            bb_data.setVar("__ONLYFINALISE", virtual or "default")
            datastores = parse_recipe(bb_data, bbfile, appends, mc)
            return datastores

        bb_data = self.data.createCopy()
        datastores = parse_recipe(bb_data, bbfile, appends)

        for mc in self.databuilder.mcdata:
            if not mc:
                continue
            bb_data = self.databuilder.mcdata[mc].createCopy()
            newstores = parse_recipe(bb_data, bbfile, appends, mc)
            for ns in newstores:
                datastores["multiconfig:%s:%s" % (mc, ns)] = newstores[ns]

        return datastores

class Cache(NoCache):
    """
    BitBake Cache implementation
    """

    def __init__(self, databuilder, data_hash, caches_array):
        super().__init__(databuilder)
        data = databuilder.data

        # Pass caches_array information into Cache Constructor
        # It will be used later for deciding whether we
        # need extra cache file dump/load support
        self.caches_array = caches_array
        self.cachedir = data.getVar("CACHE")
        self.clean = set()
        self.checked = set()
        self.depends_cache = {}
        self.data_fn = None
        self.cacheclean = True
        self.data_hash = data_hash

        if self.cachedir in [None, '']:
            self.has_cache = False
            logger.info("Not using a cache. "
                        "Set CACHE = <directory> to enable.")
            return

        self.has_cache = True
        self.cachefile = getCacheFile(self.cachedir, "bb_cache.dat", self.data_hash)

        logger.debug(1, "Cache dir: %s", self.cachedir)
        bb.utils.mkdirhier(self.cachedir)

        cache_ok = True
        if self.caches_array:
            for cache_class in self.caches_array:
                cachefile = getCacheFile(self.cachedir, cache_class.cachefile, self.data_hash)
                cache_ok = cache_ok and os.path.exists(cachefile)
                cache_class.init_cacheData(self)
        if cache_ok:
            self.load_cachefile()
        elif os.path.isfile(self.cachefile):
            logger.info("Out of date cache found, rebuilding...")
        else:
            logger.debug(1, "Cache file %s not found, building..." % self.cachefile)

    def load_cachefile(self):
        cachesize = 0
        previous_progress = 0
        previous_percent = 0

        # Calculate the correct cachesize of all those cache files
        for cache_class in self.caches_array:
            cachefile = getCacheFile(self.cachedir, cache_class.cachefile, self.data_hash)
            with open(cachefile, "rb") as cachefile:
                cachesize += os.fstat(cachefile.fileno()).st_size

        bb.event.fire(bb.event.CacheLoadStarted(cachesize), self.data)

        for cache_class in self.caches_array:
            cachefile = getCacheFile(self.cachedir, cache_class.cachefile, self.data_hash)
            logger.debug(1, 'Loading cache file: %s' % cachefile)
            with open(cachefile, "rb") as cachefile:
                pickled = pickle.Unpickler(cachefile)
                # Check cache version information
                try:
                    cache_ver = pickled.load()
                    bitbake_ver = pickled.load()
                except Exception:
                    logger.info('Invalid cache, rebuilding...')
                    return

                if cache_ver != __cache_version__:
                    logger.info('Cache version mismatch, rebuilding...')
                    return
                elif bitbake_ver != bb.__version__:
                    logger.info('Bitbake version mismatch, rebuilding...')
                    return

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
                    if current_progress > cachesize:
                        # we might have calculated incorrect total size because a file
                        # might've been written out just after we checked its size
                        cachesize = current_progress
                    current_percent = 100 * current_progress / cachesize
                    if current_percent > previous_percent:
                        previous_percent = current_percent
                        bb.event.fire(bb.event.CacheLoadProgress(current_progress, cachesize),
                                      self.data)

                previous_progress += current_progress

        # Note: depends cache number is corresponding to the parsing file numbers.
        # The same file has several caches, still regarded as one item in the cache
        bb.event.fire(bb.event.CacheLoadCompleted(cachesize,
                                                  len(self.depends_cache)),
                      self.data)

    def parse(self, filename, appends):
        """Parse the specified filename, returning the recipe information"""
        logger.debug(1, "Parsing %s", filename)
        infos = []
        datastores = self.load_bbfile(filename, appends)
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

    def load(self, filename, appends):
        """Obtain the recipe information for the specified filename,
        using cached values if available, otherwise parsing.

        Note that if it does parse to obtain the info, it will not
        automatically add the information to the cache or to your
        CacheData.  Use the add or add_info method to do so after
        running this, or use loadData instead."""
        cached = self.cacheValid(filename, appends)
        if cached:
            infos = []
            # info_array item is a list of [CoreRecipeInfo, XXXRecipeInfo]
            info_array = self.depends_cache[filename]
            for variant in info_array[0].variants:
                virtualfn = variant2virtual(filename, variant)
                infos.append((virtualfn, self.depends_cache[virtualfn]))
        else:
            return self.parse(filename, appends, configdata, self.caches_array)

        return cached, infos

    def loadData(self, fn, appends, cacheData):
        """Load the recipe info for the specified filename,
        parsing and adding to the cache if necessary, and adding
        the recipe information to the supplied CacheData instance."""
        skipped, virtuals = 0, 0

        cached, infos = self.load(fn, appends)
        for virtualfn, info_array in infos:
            if info_array[0].skipped:
                logger.debug(1, "Skipping %s: %s", virtualfn, info_array[0].skipreason)
                skipped += 1
            else:
                self.add_info(virtualfn, info_array, cacheData, not cached)
                virtuals += 1

        return cached, skipped, virtuals

    def cacheValid(self, fn, appends):
        """
        Is the cache valid for fn?
        Fast version, no timestamps checked.
        """
        if fn not in self.checked:
            self.cacheValidUpdate(fn, appends)

        # Is cache enabled?
        if not self.has_cache:
            return False
        if fn in self.clean:
            return True
        return False

    def cacheValidUpdate(self, fn, appends):
        """
        Is the cache valid for fn?
        Make thorough (slower) checks including timestamps.
        """
        # Is cache enabled?
        if not self.has_cache:
            return False

        self.checked.add(fn)

        # File isn't in depends_cache
        if not fn in self.depends_cache:
            logger.debug(2, "Cache: %s is not cached", fn)
            return False

        mtime = bb.parse.cached_mtime_noerror(fn)

        # Check file still exists
        if mtime == 0:
            logger.debug(2, "Cache: %s no longer exists", fn)
            self.remove(fn)
            return False

        info_array = self.depends_cache[fn]
        # Check the file's timestamp
        if mtime != info_array[0].timestamp:
            logger.debug(2, "Cache: %s changed", fn)
            self.remove(fn)
            return False

        # Check dependencies are still valid
        depends = info_array[0].file_depends
        if depends:
            for f, old_mtime in depends:
                fmtime = bb.parse.cached_mtime_noerror(f)
                # Check if file still exists
                if old_mtime != 0 and fmtime == 0:
                    logger.debug(2, "Cache: %s's dependency %s was removed",
                                    fn, f)
                    self.remove(fn)
                    return False

                if (fmtime != old_mtime):
                    logger.debug(2, "Cache: %s's dependency %s changed",
                                    fn, f)
                    self.remove(fn)
                    return False

        if hasattr(info_array[0], 'file_checksums'):
            for _, fl in info_array[0].file_checksums.items():
                fl = fl.strip()
                while fl:
                    # A .split() would be simpler but means spaces or colons in filenames would break
                    a = fl.find(":True")
                    b = fl.find(":False")
                    if ((a < 0) and b) or ((b > 0) and (b < a)):
                        f = fl[:b+6]
                        fl = fl[b+7:]
                    elif ((b < 0) and a) or ((a > 0) and (a < b)):
                        f = fl[:a+5]
                        fl = fl[a+6:]
                    else:
                        break
                    fl = fl.strip()
                    if "*" in f:
                        continue
                    f, exist = f.split(":")
                    if (exist == "True" and not os.path.exists(f)) or (exist == "False" and os.path.exists(f)):
                        logger.debug(2, "Cache: %s's file checksum list file %s changed",
                                        fn, f)
                        self.remove(fn)
                        return False

        if appends != info_array[0].appends:
            logger.debug(2, "Cache: appends for %s changed", fn)
            logger.debug(2, "%s to %s" % (str(appends), str(info_array[0].appends)))
            self.remove(fn)
            return False

        invalid = False
        for cls in info_array[0].variants:
            virtualfn = variant2virtual(fn, cls)
            self.clean.add(virtualfn)
            if virtualfn not in self.depends_cache:
                logger.debug(2, "Cache: %s is not cached", virtualfn)
                invalid = True
            elif len(self.depends_cache[virtualfn]) != len(self.caches_array):
                logger.debug(2, "Cache: Extra caches missing for %s?" % virtualfn)
                invalid = True

        # If any one of the variants is not present, mark as invalid for all
        if invalid:
            for cls in info_array[0].variants:
                virtualfn = variant2virtual(fn, cls)
                if virtualfn in self.clean:
                    logger.debug(2, "Cache: Removing %s from cache", virtualfn)
                    self.clean.remove(virtualfn)
            if fn in self.clean:
                logger.debug(2, "Cache: Marking %s as not clean", fn)
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
            logger.debug(1, "Removing %s from cache", fn)
            del self.depends_cache[fn]
        if fn in self.clean:
            logger.debug(1, "Marking %s as unclean", fn)
            self.clean.remove(fn)

    def sync(self):
        """
        Save the cache
        Called from the parser when complete (or exiting)
        """

        if not self.has_cache:
            return

        if self.cacheclean:
            logger.debug(2, "Cache is clean, not saving.")
            return

        for cache_class in self.caches_array:
            cache_class_name = cache_class.__name__
            cachefile = getCacheFile(self.cachedir, cache_class.cachefile, self.data_hash)
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

    @staticmethod
    def mtime(cachefile):
        return bb.parse.cached_mtime_noerror(cachefile)

    def add_info(self, filename, info_array, cacheData, parsed=None, watcher=None):
        if isinstance(info_array[0], CoreRecipeInfo) and (not info_array[0].skipped):
            cacheData.add_from_recipeinfo(filename, info_array)

            if watcher:
                watcher(info_array[0].file_depends)

        if not self.has_cache:
            return

        if (info_array[0].skipped or 'SRCREVINACTION' not in info_array[0].pv) and not info_array[0].nocache:
            if parsed:
                self.cacheclean = False
            self.depends_cache[filename] = info_array

    def add(self, file_name, data, cacheData, parsed=None):
        """
        Save data we need into the cache
        """

        realfn = virtualfn2realfn(file_name)[0]

        info_array = []
        for cache_class in self.caches_array:
            info_array.append(cache_class(realfn, data))
        self.add_info(file_name, info_array, cacheData, parsed)


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

    def init_cache(self, d, cache_file_name=None):
        cachedir = (d.getVar("PERSISTENT_DIR") or
                    d.getVar("CACHE"))
        if cachedir in [None, '']:
            return
        bb.utils.mkdirhier(cachedir)
        self.cachefile = os.path.join(cachedir,
                                      cache_file_name or self.__class__.cache_file_name)
        logger.debug(1, "Using cache in '%s'", self.cachefile)

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

            self.merge_data(extradata, data)
            os.unlink(f)

        with open(self.cachefile, "wb") as f:
            p = pickle.Pickler(f, -1)
            p.dump([data, self.__class__.CACHE_VERSION])

        bb.utils.unlockfile(glf)
