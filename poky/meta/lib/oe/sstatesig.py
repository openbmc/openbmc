#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
import bb.siggen
import bb.runqueue
import oe
import netrc

def sstate_rundepfilter(siggen, fn, recipename, task, dep, depname, dataCaches):
    # Return True if we should keep the dependency, False to drop it
    def isNative(x):
        return x.endswith("-native")
    def isCross(x):
        return "-cross-" in x
    def isNativeSDK(x):
        return x.startswith("nativesdk-")
    def isKernel(mc, fn):
        inherits = " ".join(dataCaches[mc].inherits[fn])
        return inherits.find("/module-base.bbclass") != -1 or inherits.find("/linux-kernel-base.bbclass") != -1
    def isPackageGroup(mc, fn):
        inherits = " ".join(dataCaches[mc].inherits[fn])
        return "/packagegroup.bbclass" in inherits
    def isAllArch(mc, fn):
        inherits = " ".join(dataCaches[mc].inherits[fn])
        return "/allarch.bbclass" in inherits
    def isImage(mc, fn):
        return "/image.bbclass" in " ".join(dataCaches[mc].inherits[fn])

    depmc, _, deptaskname, depmcfn = bb.runqueue.split_tid_mcfn(dep)
    mc, _ = bb.runqueue.split_mc(fn)

    # We can skip the rm_work task signature to avoid running the task
    # when we remove some tasks from the dependencie chain
    # i.e INHERIT:remove = "create-spdx" will trigger the do_rm_work
    if task == "do_rm_work":
        return False

    # (Almost) always include our own inter-task dependencies (unless it comes
    # from a mcdepends). The exception is the special
    # do_kernel_configme->do_unpack_and_patch dependency from archiver.bbclass.
    if recipename == depname and depmc == mc:
        if task == "do_kernel_configme" and deptaskname == "do_unpack_and_patch":
            return False
        return True

    # Exclude well defined recipe->dependency
    if "%s->%s" % (recipename, depname) in siggen.saferecipedeps:
        return False

    # Check for special wildcard
    if "*->%s" % depname in siggen.saferecipedeps and recipename != depname:
        return False

    # Don't change native/cross/nativesdk recipe dependencies any further
    if isNative(recipename) or isCross(recipename) or isNativeSDK(recipename):
        return True

    # Only target packages beyond here

    # allarch packagegroups are assumed to have well behaved names which don't change between architecures/tunes
    if isPackageGroup(mc, fn) and isAllArch(mc, fn) and not isNative(depname):
        return False

    # Exclude well defined machine specific configurations which don't change ABI
    if depname in siggen.abisaferecipes and not isImage(mc, fn):
        return False

    # Kernel modules are well namespaced. We don't want to depend on the kernel's checksum
    # if we're just doing an RRECOMMENDS:xxx = "kernel-module-*", not least because the checksum
    # is machine specific.
    # Therefore if we're not a kernel or a module recipe (inheriting the kernel classes)
    # and we reccomend a kernel-module, we exclude the dependency.
    if dataCaches and isKernel(depmc, depmcfn) and not isKernel(mc, fn):
        for pkg in dataCaches[mc].runrecs[fn]:
            if " ".join(dataCaches[mc].runrecs[fn][pkg]).find("kernel-module-") != -1:
                return False

    # Default to keep dependencies
    return True

def sstate_lockedsigs(d):
    sigs = {}
    types = (d.getVar("SIGGEN_LOCKEDSIGS_TYPES") or "").split()
    for t in types:
        siggen_lockedsigs_var = "SIGGEN_LOCKEDSIGS_%s" % t
        lockedsigs = (d.getVar(siggen_lockedsigs_var) or "").split()
        for ls in lockedsigs:
            pn, task, h = ls.split(":", 2)
            if pn not in sigs:
                sigs[pn] = {}
            sigs[pn][task] = [h, siggen_lockedsigs_var]
    return sigs

def lockedsigs_unihashmap(d):
    unihashmap = {}
    data = (d.getVar("SIGGEN_UNIHASHMAP") or "").split()
    for entry in data:
        pn, task, taskhash, unihash = entry.split(":")
        unihashmap[(pn, task)] = (taskhash, unihash)
    return unihashmap

class SignatureGeneratorOEBasicHashMixIn(object):
    supports_multiconfig_datacaches = True

    def init_rundepcheck(self, data):
        self.abisaferecipes = (data.getVar("SIGGEN_EXCLUDERECIPES_ABISAFE") or "").split()
        self.saferecipedeps = (data.getVar("SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS") or "").split()
        self.lockedsigs = sstate_lockedsigs(data)
        self.unihashmap = lockedsigs_unihashmap(data)
        self.lockedhashes = {}
        self.lockedpnmap = {}
        self.lockedhashfn = {}
        self.machine = data.getVar("MACHINE")
        self.mismatch_msgs = []
        self.mismatch_number = 0
        self.lockedsigs_msgs = ""
        self.unlockedrecipes = (data.getVar("SIGGEN_UNLOCKED_RECIPES") or
                                "").split()
        self.unlockedrecipes = { k: "" for k in self.unlockedrecipes }
        self._internal = False
        pass

    def tasks_resolved(self, virtmap, virtpnmap, dataCache):
        # Translate virtual/xxx entries to PN values
        newabisafe = []
        for a in self.abisaferecipes:
            if a in virtpnmap:
                newabisafe.append(virtpnmap[a])
            else:
                newabisafe.append(a)
        self.abisaferecipes = newabisafe
        newsafedeps = []
        for a in self.saferecipedeps:
            a1, a2 = a.split("->")
            if a1 in virtpnmap:
                a1 = virtpnmap[a1]
            if a2 in virtpnmap:
                a2 = virtpnmap[a2]
            newsafedeps.append(a1 + "->" + a2)
        self.saferecipedeps = newsafedeps

    def rundep_check(self, fn, recipename, task, dep, depname, dataCaches = None):
        return sstate_rundepfilter(self, fn, recipename, task, dep, depname, dataCaches)

    def get_taskdata(self):
        return (self.lockedpnmap, self.lockedhashfn, self.lockedhashes) + super().get_taskdata()

    def set_taskdata(self, data):
        self.lockedpnmap, self.lockedhashfn, self.lockedhashes = data[:3]
        super().set_taskdata(data[3:])

    def dump_sigs(self, dataCache, options):
        if 'lockedsigs' in options:
            sigfile = os.getcwd() + "/locked-sigs.inc"
            bb.plain("Writing locked sigs to %s" % sigfile)
            self.dump_lockedsigs(sigfile)
        return super(bb.siggen.SignatureGeneratorBasicHash, self).dump_sigs(dataCache, options)


    def get_taskhash(self, tid, deps, dataCaches):
        if tid in self.lockedhashes:
            if self.lockedhashes[tid]:
                return self.lockedhashes[tid]
            else:
                return super().get_taskhash(tid, deps, dataCaches)

        h = super().get_taskhash(tid, deps, dataCaches)

        (mc, _, task, fn) = bb.runqueue.split_tid_mcfn(tid)

        recipename = dataCaches[mc].pkg_fn[fn]
        self.lockedpnmap[fn] = recipename
        self.lockedhashfn[fn] = dataCaches[mc].hashfn[fn]

        unlocked = False
        if recipename in self.unlockedrecipes:
            unlocked = True
        else:
            def recipename_from_dep(dep):
                (depmc, _, _, depfn) = bb.runqueue.split_tid_mcfn(dep)
                return dataCaches[depmc].pkg_fn[depfn]

            # If any unlocked recipe is in the direct dependencies then the
            # current recipe should be unlocked as well.
            depnames = [ recipename_from_dep(x) for x in deps if mc == bb.runqueue.mc_from_tid(x)]
            if any(x in y for y in depnames for x in self.unlockedrecipes):
                self.unlockedrecipes[recipename] = ''
                unlocked = True

        if not unlocked and recipename in self.lockedsigs:
            if task in self.lockedsigs[recipename]:
                h_locked = self.lockedsigs[recipename][task][0]
                var = self.lockedsigs[recipename][task][1]
                self.lockedhashes[tid] = h_locked
                self._internal = True
                unihash = self.get_unihash(tid)
                self._internal = False
                #bb.warn("Using %s %s %s" % (recipename, task, h))

                if h != h_locked and h_locked != unihash:
                    self.mismatch_number += 1
                    self.mismatch_msgs.append('The %s:%s sig is computed to be %s, but the sig is locked to %s in %s'
                                          % (recipename, task, h, h_locked, var))

                return h_locked

        self.lockedhashes[tid] = False
        #bb.warn("%s %s %s" % (recipename, task, h))
        return h

    def get_stampfile_hash(self, tid):
        if tid in self.lockedhashes and self.lockedhashes[tid]:
            return self.lockedhashes[tid]
        return super().get_stampfile_hash(tid)

    def get_cached_unihash(self, tid):
        if tid in self.lockedhashes and self.lockedhashes[tid] and not self._internal:
            return self.lockedhashes[tid]

        (mc, _, task, fn) = bb.runqueue.split_tid_mcfn(tid)
        recipename = self.lockedpnmap[fn]

        if (recipename, task) in self.unihashmap:
            taskhash, unihash = self.unihashmap[(recipename, task)]
            if taskhash == self.taskhash[tid]:
                return unihash

        return super().get_cached_unihash(tid)

    def dump_sigtask(self, fn, task, stampbase, runtime):
        tid = fn + ":" + task
        if tid in self.lockedhashes and self.lockedhashes[tid]:
            return
        super(bb.siggen.SignatureGeneratorBasicHash, self).dump_sigtask(fn, task, stampbase, runtime)

    def dump_lockedsigs(self, sigfile, taskfilter=None):
        types = {}
        unihashmap = {}
        for tid in self.runtaskdeps:
            # Bitbake changed this to a tuple in newer versions
            if isinstance(tid, tuple):
                tid = tid[1]
            if taskfilter:
                if not tid in taskfilter:
                    continue
            (_, _, task, fn) = bb.runqueue.split_tid_mcfn(tid)
            t = self.lockedhashfn[fn].split(" ")[1].split(":")[5]
            t = 't-' + t.replace('_', '-')
            if t not in types:
                types[t] = []
            types[t].append(tid)

            taskhash = self.taskhash[tid]
            unihash = self.get_unihash(tid)
            if taskhash != unihash:
                unihashmap[tid] = "    " + self.lockedpnmap[fn] + ":" + task + ":" + taskhash + ":" + unihash

        with open(sigfile, "w") as f:
            l = sorted(types)
            for t in l:
                f.write('SIGGEN_LOCKEDSIGS_%s = "\\\n' % t)
                types[t].sort()
                sortedtid = sorted(types[t], key=lambda tid: self.lockedpnmap[bb.runqueue.fn_from_tid(tid)])
                for tid in sortedtid:
                    (_, _, task, fn) = bb.runqueue.split_tid_mcfn(tid)
                    if tid not in self.taskhash:
                        continue
                    f.write("    " + self.lockedpnmap[fn] + ":" + task + ":" + self.get_unihash(tid) + " \\\n")
                f.write('    "\n')
            f.write('SIGGEN_LOCKEDSIGS_TYPES:%s = "%s"\n' % (self.machine, " ".join(l)))
            f.write('SIGGEN_UNIHASHMAP += "\\\n')
            sortedtid = sorted(unihashmap, key=lambda tid: self.lockedpnmap[bb.runqueue.fn_from_tid(tid)])
            for tid in sortedtid:
                f.write(unihashmap[tid] + " \\\n")
            f.write('    "\n')

    def dump_siglist(self, sigfile, path_prefix_strip=None):
        def strip_fn(fn):
            nonlocal path_prefix_strip
            if not path_prefix_strip:
                return fn

            fn_exp = fn.split(":")
            if fn_exp[-1].startswith(path_prefix_strip):
                fn_exp[-1] = fn_exp[-1][len(path_prefix_strip):]

            return ":".join(fn_exp)

        with open(sigfile, "w") as f:
            tasks = []
            for taskitem in self.taskhash:
                (fn, task) = taskitem.rsplit(":", 1)
                pn = self.lockedpnmap[fn]
                tasks.append((pn, task, strip_fn(fn), self.taskhash[taskitem]))
            for (pn, task, fn, taskhash) in sorted(tasks):
                f.write('%s:%s %s %s\n' % (pn, task, fn, taskhash))

    def checkhashes(self, sq_data, missed, found, d):
        warn_msgs = []
        error_msgs = []
        sstate_missing_msgs = []
        info_msgs = None

        if self.lockedsigs:
            if len(self.lockedsigs) > 10:
                self.lockedsigs_msgs = "There are %s recipes with locked tasks (%s task(s) have non matching signature)" % (len(self.lockedsigs), self.mismatch_number)
            else:
                self.lockedsigs_msgs = "The following recipes have locked tasks:"
                for pn in self.lockedsigs:
                    self.lockedsigs_msgs += " %s" % (pn)

        for tid in sq_data['hash']:
            if tid not in found:
                for pn in self.lockedsigs:
                    taskname = bb.runqueue.taskname_from_tid(tid)
                    if sq_data['hash'][tid] in iter(self.lockedsigs[pn].values()):
                        if taskname == 'do_shared_workdir':
                            continue
                        sstate_missing_msgs.append("Locked sig is set for %s:%s (%s) yet not in sstate cache?"
                                               % (pn, taskname, sq_data['hash'][tid]))

        checklevel = d.getVar("SIGGEN_LOCKEDSIGS_TASKSIG_CHECK")
        if checklevel == 'info':
            info_msgs = self.lockedsigs_msgs
        if checklevel == 'warn' or checklevel == 'info':
            warn_msgs += self.mismatch_msgs
        elif checklevel == 'error':
            error_msgs += self.mismatch_msgs

        checklevel = d.getVar("SIGGEN_LOCKEDSIGS_SSTATE_EXISTS_CHECK")
        if checklevel == 'warn':
            warn_msgs += sstate_missing_msgs
        elif checklevel == 'error':
            error_msgs += sstate_missing_msgs

        if info_msgs:
            bb.note(info_msgs)
        if warn_msgs:
            bb.warn("\n".join(warn_msgs))
        if error_msgs:
            bb.fatal("\n".join(error_msgs))

class SignatureGeneratorOEBasicHash(SignatureGeneratorOEBasicHashMixIn, bb.siggen.SignatureGeneratorBasicHash):
    name = "OEBasicHash"

class SignatureGeneratorOEEquivHash(SignatureGeneratorOEBasicHashMixIn, bb.siggen.SignatureGeneratorUniHashMixIn, bb.siggen.SignatureGeneratorBasicHash):
    name = "OEEquivHash"

    def init_rundepcheck(self, data):
        super().init_rundepcheck(data)
        self.server = data.getVar('BB_HASHSERVE')
        if not self.server:
            bb.fatal("OEEquivHash requires BB_HASHSERVE to be set")
        self.method = data.getVar('SSTATE_HASHEQUIV_METHOD')
        if not self.method:
            bb.fatal("OEEquivHash requires SSTATE_HASHEQUIV_METHOD to be set")
        self.username = data.getVar("BB_HASHSERVE_USERNAME")
        self.password = data.getVar("BB_HASHSERVE_PASSWORD")
        if not self.username or not self.password:
            try:
                n = netrc.netrc()
                auth = n.authenticators(self.server)
                if auth is not None:
                    self.username, _, self.password = auth
            except FileNotFoundError:
                pass
            except netrc.NetrcParseError as e:
                bb.warn("Error parsing %s:%s: %s" % (e.filename, str(e.lineno), e.msg))

# Insert these classes into siggen's namespace so it can see and select them
bb.siggen.SignatureGeneratorOEBasicHash = SignatureGeneratorOEBasicHash
bb.siggen.SignatureGeneratorOEEquivHash = SignatureGeneratorOEEquivHash


def find_siginfo(pn, taskname, taskhashlist, d):
    """ Find signature data files for comparison purposes """

    import fnmatch
    import glob

    if not taskname:
        # We have to derive pn and taskname
        key = pn
        if key.startswith("mc:"):
           # mc:<mc>:<pn>:<task>
           _, _, pn, taskname = key.split(':', 3)
        else:
           # <pn>:<task>
           pn, taskname = key.split(':', 1)

    hashfiles = {}

    def get_hashval(siginfo):
        if siginfo.endswith('.siginfo'):
            return siginfo.rpartition(':')[2].partition('_')[0]
        else:
            return siginfo.rpartition('.')[2]

    def get_time(fullpath):
        return os.stat(fullpath).st_mtime

    # First search in stamps dir
    localdata = d.createCopy()
    localdata.setVar('MULTIMACH_TARGET_SYS', '*')
    localdata.setVar('PN', pn)
    localdata.setVar('PV', '*')
    localdata.setVar('PR', '*')
    localdata.setVar('EXTENDPE', '')
    stamp = localdata.getVar('STAMP')
    if pn.startswith("gcc-source"):
        # gcc-source shared workdir is a special case :(
        stamp = localdata.expand("${STAMPS_DIR}/work-shared/gcc-${PV}-${PR}")

    filespec = '%s.%s.sigdata.*' % (stamp, taskname)
    foundall = False
    import glob
    bb.debug(1, "Calling glob.glob on {}".format(filespec))
    for fullpath in glob.glob(filespec):
        match = False
        if taskhashlist:
            for taskhash in taskhashlist:
                if fullpath.endswith('.%s' % taskhash):
                    hashfiles[taskhash] = {'path':fullpath, 'sstate':False, 'time':get_time(fullpath)}
                    if len(hashfiles) == len(taskhashlist):
                        foundall = True
                        break
        else:
            hashval = get_hashval(fullpath)
            hashfiles[hashval] = {'path':fullpath, 'sstate':False, 'time':get_time(fullpath)}

    if not taskhashlist or (len(hashfiles) < 2 and not foundall):
        # That didn't work, look in sstate-cache
        hashes = taskhashlist or ['?' * 64]
        localdata = bb.data.createCopy(d)
        for hashval in hashes:
            localdata.setVar('PACKAGE_ARCH', '*')
            localdata.setVar('TARGET_VENDOR', '*')
            localdata.setVar('TARGET_OS', '*')
            localdata.setVar('PN', pn)
            # gcc-source is a special case, same as with local stamps above
            if pn.startswith("gcc-source"):
                localdata.setVar('PN', "gcc")
            localdata.setVar('PV', '*')
            localdata.setVar('PR', '*')
            localdata.setVar('BB_TASKHASH', hashval)
            localdata.setVar('SSTATE_CURRTASK', taskname[3:])
            swspec = localdata.getVar('SSTATE_SWSPEC')
            if taskname in ['do_fetch', 'do_unpack', 'do_patch', 'do_populate_lic', 'do_preconfigure'] and swspec:
                localdata.setVar('SSTATE_PKGSPEC', '${SSTATE_SWSPEC}')
            elif pn.endswith('-native') or "-cross-" in pn or "-crosssdk-" in pn:
                localdata.setVar('SSTATE_EXTRAPATH', "${NATIVELSBSTRING}/")
            filespec = '%s.siginfo' % localdata.getVar('SSTATE_PKG')

            bb.debug(1, "Calling glob.glob on {}".format(filespec))
            matchedfiles = glob.glob(filespec)
            for fullpath in matchedfiles:
                actual_hashval = get_hashval(fullpath)
                if actual_hashval in hashfiles:
                    continue
                hashfiles[actual_hashval] = {'path':fullpath, 'sstate':True, 'time':get_time(fullpath)}

    return hashfiles

bb.siggen.find_siginfo = find_siginfo
bb.siggen.find_siginfo_version = 2


def sstate_get_manifest_filename(task, d):
    """
    Return the sstate manifest file path for a particular task.
    Also returns the datastore that can be used to query related variables.
    """
    d2 = d.createCopy()
    extrainf = d.getVarFlag("do_" + task, 'stamp-extra-info')
    if extrainf:
        d2.setVar("SSTATE_MANMACH", extrainf)
    return (d2.expand("${SSTATE_MANFILEPREFIX}.%s" % task), d2)

def find_sstate_manifest(taskdata, taskdata2, taskname, d, multilibcache):
    d2 = d
    variant = ''
    curr_variant = ''
    if d.getVar("BBEXTENDCURR") == "multilib":
        curr_variant = d.getVar("BBEXTENDVARIANT")
        if "virtclass-multilib" not in d.getVar("OVERRIDES"):
            curr_variant = "invalid"
    if taskdata2.startswith("virtual:multilib"):
        variant = taskdata2.split(":")[2]
    if curr_variant != variant:
        if variant not in multilibcache:
            multilibcache[variant] = oe.utils.get_multilib_datastore(variant, d)
        d2 = multilibcache[variant]

    if taskdata.endswith("-native"):
        pkgarchs = ["${BUILD_ARCH}", "${BUILD_ARCH}_${ORIGNATIVELSBSTRING}"]
    elif taskdata.startswith("nativesdk-"):
        pkgarchs = ["${SDK_ARCH}_${SDK_OS}", "allarch"]
    elif "-cross-canadian" in taskdata:
        pkgarchs = ["${SDK_ARCH}_${SDK_ARCH}-${SDKPKGSUFFIX}"]
    elif "-cross-" in taskdata:
        pkgarchs = ["${BUILD_ARCH}"]
    elif "-crosssdk" in taskdata:
        pkgarchs = ["${BUILD_ARCH}_${SDK_ARCH}_${SDK_OS}"]
    else:
        pkgarchs = ['${MACHINE_ARCH}']
        pkgarchs = pkgarchs + list(reversed(d2.getVar("PACKAGE_EXTRA_ARCHS").split()))
        pkgarchs.append('allarch')
        pkgarchs.append('${SDK_ARCH}_${SDK_ARCH}-${SDKPKGSUFFIX}')

    searched_manifests = []

    for pkgarch in pkgarchs:
        manifest = d2.expand("${SSTATE_MANIFESTS}/manifest-%s-%s.%s" % (pkgarch, taskdata, taskname))
        if os.path.exists(manifest):
            return manifest, d2
        searched_manifests.append(manifest)
    bb.fatal("The sstate manifest for task '%s:%s' (multilib variant '%s') could not be found.\nThe pkgarchs considered were: %s.\nBut none of these manifests exists:\n    %s"
            % (taskdata, taskname, variant, d2.expand(", ".join(pkgarchs)),"\n    ".join(searched_manifests)))
    return None, d2

def OEOuthashBasic(path, sigfile, task, d):
    """
    Basic output hash function

    Calculates the output hash of a task by hashing all output file metadata,
    and file contents.
    """
    import hashlib
    import stat
    import pwd
    import grp
    import re
    import fnmatch

    def update_hash(s):
        s = s.encode('utf-8')
        h.update(s)
        if sigfile:
            sigfile.write(s)

    h = hashlib.sha256()
    prev_dir = os.getcwd()
    corebase = d.getVar("COREBASE")
    tmpdir = d.getVar("TMPDIR")
    include_owners = os.environ.get('PSEUDO_DISABLED') == '0'
    if "package_write_" in task or task == "package_qa":
        include_owners = False
    include_timestamps = False
    include_root = True
    if task == "package":
        include_timestamps = True
        include_root = False
    hash_version = d.getVar('HASHEQUIV_HASH_VERSION')
    extra_sigdata = d.getVar("HASHEQUIV_EXTRA_SIGDATA")

    filemaps = {}
    for m in (d.getVar('SSTATE_HASHEQUIV_FILEMAP') or '').split():
        entry = m.split(":")
        if len(entry) != 3 or entry[0] != task:
            continue
        filemaps.setdefault(entry[1], [])
        filemaps[entry[1]].append(entry[2])

    try:
        os.chdir(path)
        basepath = os.path.normpath(path)

        update_hash("OEOuthashBasic\n")
        if hash_version:
            update_hash(hash_version + "\n")

        if extra_sigdata:
            update_hash(extra_sigdata + "\n")

        # It is only currently useful to get equivalent hashes for things that
        # can be restored from sstate. Since the sstate object is named using
        # SSTATE_PKGSPEC and the task name, those should be included in the
        # output hash calculation.
        update_hash("SSTATE_PKGSPEC=%s\n" % d.getVar('SSTATE_PKGSPEC'))
        update_hash("task=%s\n" % task)

        for root, dirs, files in os.walk('.', topdown=True):
            # Sort directories to ensure consistent ordering when recursing
            dirs.sort()
            files.sort()

            def process(path):
                s = os.lstat(path)

                if stat.S_ISDIR(s.st_mode):
                    update_hash('d')
                elif stat.S_ISCHR(s.st_mode):
                    update_hash('c')
                elif stat.S_ISBLK(s.st_mode):
                    update_hash('b')
                elif stat.S_ISSOCK(s.st_mode):
                    update_hash('s')
                elif stat.S_ISLNK(s.st_mode):
                    update_hash('l')
                elif stat.S_ISFIFO(s.st_mode):
                    update_hash('p')
                else:
                    update_hash('-')

                def add_perm(mask, on, off='-'):
                    if mask & s.st_mode:
                        update_hash(on)
                    else:
                        update_hash(off)

                add_perm(stat.S_IRUSR, 'r')
                add_perm(stat.S_IWUSR, 'w')
                if stat.S_ISUID & s.st_mode:
                    add_perm(stat.S_IXUSR, 's', 'S')
                else:
                    add_perm(stat.S_IXUSR, 'x')

                if include_owners:
                    # Group/other permissions are only relevant in pseudo context
                    add_perm(stat.S_IRGRP, 'r')
                    add_perm(stat.S_IWGRP, 'w')
                    if stat.S_ISGID & s.st_mode:
                        add_perm(stat.S_IXGRP, 's', 'S')
                    else:
                        add_perm(stat.S_IXGRP, 'x')

                    add_perm(stat.S_IROTH, 'r')
                    add_perm(stat.S_IWOTH, 'w')
                    if stat.S_ISVTX & s.st_mode:
                        update_hash('t')
                    else:
                        add_perm(stat.S_IXOTH, 'x')

                    try:
                        update_hash(" %10s" % pwd.getpwuid(s.st_uid).pw_name)
                        update_hash(" %10s" % grp.getgrgid(s.st_gid).gr_name)
                    except KeyError as e:
                        msg = ("KeyError: %s\nPath %s is owned by uid %d, gid %d, which doesn't match "
                            "any user/group on target. This may be due to host contamination." %
                            (e, os.path.abspath(path), s.st_uid, s.st_gid))
                        raise Exception(msg).with_traceback(e.__traceback__)

                if include_timestamps:
                    update_hash(" %10d" % s.st_mtime)

                update_hash(" ")
                if stat.S_ISBLK(s.st_mode) or stat.S_ISCHR(s.st_mode):
                    update_hash("%9s" % ("%d.%d" % (os.major(s.st_rdev), os.minor(s.st_rdev))))
                else:
                    update_hash(" " * 9)

                filterfile = False
                for entry in filemaps:
                    if fnmatch.fnmatch(path, entry):
                        filterfile = True

                update_hash(" ")
                if stat.S_ISREG(s.st_mode) and not filterfile:
                    update_hash("%10d" % s.st_size)
                else:
                    update_hash(" " * 10)

                update_hash(" ")
                fh = hashlib.sha256()
                if stat.S_ISREG(s.st_mode):
                    # Hash file contents
                    if filterfile:
                        # Need to ignore paths in crossscripts and postinst-useradd files.
                        with open(path, 'rb') as d:
                            chunk = d.read()
                            chunk = chunk.replace(bytes(basepath, encoding='utf8'), b'')
                            for entry in filemaps:
                                if not fnmatch.fnmatch(path, entry):
                                    continue
                                for r in filemaps[entry]:
                                    if r.startswith("regex-"):
                                        chunk = re.sub(bytes(r[6:], encoding='utf8'), b'', chunk)
                                    else:
                                        chunk = chunk.replace(bytes(r, encoding='utf8'), b'')
                            fh.update(chunk)
                    else:
                        with open(path, 'rb') as d:
                            for chunk in iter(lambda: d.read(4096), b""):
                                fh.update(chunk)
                    update_hash(fh.hexdigest())
                else:
                    update_hash(" " * len(fh.hexdigest()))

                update_hash(" %s" % path)

                if stat.S_ISLNK(s.st_mode):
                    update_hash(" -> %s" % os.readlink(path))

                update_hash("\n")

            # Process this directory and all its child files
            if include_root or root != ".":
                process(root)
            for f in files:
                if f == 'fixmepath':
                    continue
                process(os.path.join(root, f))

            for dir in dirs:
                if os.path.islink(os.path.join(root, dir)):
                    process(os.path.join(root, dir))
    finally:
        os.chdir(prev_dir)

    return h.hexdigest()


