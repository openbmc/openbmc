import bb.siggen

def sstate_rundepfilter(siggen, fn, recipename, task, dep, depname, dataCache):
    # Return True if we should keep the dependency, False to drop it
    def isNative(x):
        return x.endswith("-native")
    def isCross(x):
        return "-cross-" in x
    def isNativeSDK(x):
        return x.startswith("nativesdk-")
    def isKernel(fn):
        inherits = " ".join(dataCache.inherits[fn])
        return inherits.find("/module-base.bbclass") != -1 or inherits.find("/linux-kernel-base.bbclass") != -1
    def isPackageGroup(fn):
        inherits = " ".join(dataCache.inherits[fn])
        return "/packagegroup.bbclass" in inherits
    def isAllArch(fn):
        inherits = " ".join(dataCache.inherits[fn])
        return "/allarch.bbclass" in inherits
    def isImage(fn):
        return "/image.bbclass" in " ".join(dataCache.inherits[fn])

    # Always include our own inter-task dependencies
    if recipename == depname:
        return True

    # Quilt (patch application) changing isn't likely to affect anything
    excludelist = ['quilt-native', 'subversion-native', 'git-native']
    if depname in excludelist and recipename != depname:
        return False

    # Exclude well defined recipe->dependency
    if "%s->%s" % (recipename, depname) in siggen.saferecipedeps:
        return False

    # Don't change native/cross/nativesdk recipe dependencies any further
    if isNative(recipename) or isCross(recipename) or isNativeSDK(recipename):
        return True

    # Only target packages beyond here

    # allarch packagegroups are assumed to have well behaved names which don't change between architecures/tunes
    if isPackageGroup(fn) and isAllArch(fn) and not isNative(depname):
        return False  

    # Exclude well defined machine specific configurations which don't change ABI
    if depname in siggen.abisaferecipes and not isImage(fn):
        return False

    # Kernel modules are well namespaced. We don't want to depend on the kernel's checksum
    # if we're just doing an RRECOMMENDS_xxx = "kernel-module-*", not least because the checksum
    # is machine specific.
    # Therefore if we're not a kernel or a module recipe (inheriting the kernel classes)
    # and we reccomend a kernel-module, we exclude the dependency.
    depfn = dep.rsplit(".", 1)[0]
    if dataCache and isKernel(depfn) and not isKernel(fn):
        for pkg in dataCache.runrecs[fn]:
            if " ".join(dataCache.runrecs[fn][pkg]).find("kernel-module-") != -1:
                return False

    # Default to keep dependencies
    return True

def sstate_lockedsigs(d):
    sigs = {}
    types = (d.getVar("SIGGEN_LOCKEDSIGS_TYPES", True) or "").split()
    for t in types:
        siggen_lockedsigs_var = "SIGGEN_LOCKEDSIGS_%s" % t
        lockedsigs = (d.getVar(siggen_lockedsigs_var, True) or "").split()
        for ls in lockedsigs:
            pn, task, h = ls.split(":", 2)
            if pn not in sigs:
                sigs[pn] = {}
            sigs[pn][task] = [h, siggen_lockedsigs_var]
    return sigs

class SignatureGeneratorOEBasic(bb.siggen.SignatureGeneratorBasic):
    name = "OEBasic"
    def init_rundepcheck(self, data):
        self.abisaferecipes = (data.getVar("SIGGEN_EXCLUDERECIPES_ABISAFE", True) or "").split()
        self.saferecipedeps = (data.getVar("SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS", True) or "").split()
        pass
    def rundep_check(self, fn, recipename, task, dep, depname, dataCache = None):
        return sstate_rundepfilter(self, fn, recipename, task, dep, depname, dataCache)

class SignatureGeneratorOEBasicHash(bb.siggen.SignatureGeneratorBasicHash):
    name = "OEBasicHash"
    def init_rundepcheck(self, data):
        self.abisaferecipes = (data.getVar("SIGGEN_EXCLUDERECIPES_ABISAFE", True) or "").split()
        self.saferecipedeps = (data.getVar("SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS", True) or "").split()
        self.lockedsigs = sstate_lockedsigs(data)
        self.lockedhashes = {}
        self.lockedpnmap = {}
        self.lockedhashfn = {}
        self.machine = data.getVar("MACHINE", True)
        self.mismatch_msgs = []
        self.unlockedrecipes = (data.getVar("SIGGEN_UNLOCKED_RECIPES", True) or
                                "").split()
        self.unlockedrecipes = { k: "" for k in self.unlockedrecipes }
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

    def rundep_check(self, fn, recipename, task, dep, depname, dataCache = None):
        return sstate_rundepfilter(self, fn, recipename, task, dep, depname, dataCache)

    def get_taskdata(self):
        data = super(bb.siggen.SignatureGeneratorBasicHash, self).get_taskdata()
        return (data, self.lockedpnmap, self.lockedhashfn)

    def set_taskdata(self, data):
        coredata, self.lockedpnmap, self.lockedhashfn = data
        super(bb.siggen.SignatureGeneratorBasicHash, self).set_taskdata(coredata)

    def dump_sigs(self, dataCache, options):
        self.dump_lockedsigs()
        return super(bb.siggen.SignatureGeneratorBasicHash, self).dump_sigs(dataCache, options)

    def get_taskhash(self, fn, task, deps, dataCache):
        h = super(bb.siggen.SignatureGeneratorBasicHash, self).get_taskhash(fn, task, deps, dataCache)

        recipename = dataCache.pkg_fn[fn]
        self.lockedpnmap[fn] = recipename
        self.lockedhashfn[fn] = dataCache.hashfn[fn]

        unlocked = False
        if recipename in self.unlockedrecipes:
            unlocked = True
        else:
            def recipename_from_dep(dep):
                # The dep entry will look something like
                # /path/path/recipename.bb.task, virtual:native:/p/foo.bb.task,
                # ...
                fn = dep.rsplit('.', 1)[0]
                return dataCache.pkg_fn[fn]

            # If any unlocked recipe is in the direct dependencies then the
            # current recipe should be unlocked as well.
            depnames = [ recipename_from_dep(x) for x in deps ]
            if any(x in y for y in depnames for x in self.unlockedrecipes):
                self.unlockedrecipes[recipename] = ''
                unlocked = True

        if not unlocked and recipename in self.lockedsigs:
            if task in self.lockedsigs[recipename]:
                k = fn + "." + task
                h_locked = self.lockedsigs[recipename][task][0]
                var = self.lockedsigs[recipename][task][1]
                self.lockedhashes[k] = h_locked
                self.taskhash[k] = h_locked
                #bb.warn("Using %s %s %s" % (recipename, task, h))

                if h != h_locked:
                    self.mismatch_msgs.append('The %s:%s sig is computed to be %s, but the sig is locked to %s in %s'
                                          % (recipename, task, h, h_locked, var))

                return h_locked
        #bb.warn("%s %s %s" % (recipename, task, h))
        return h

    def dump_sigtask(self, fn, task, stampbase, runtime):
        k = fn + "." + task
        if k in self.lockedhashes:
            return
        super(bb.siggen.SignatureGeneratorBasicHash, self).dump_sigtask(fn, task, stampbase, runtime)

    def dump_lockedsigs(self, sigfile=None, taskfilter=None):
        if not sigfile:
            sigfile = os.getcwd() + "/locked-sigs.inc"

        bb.plain("Writing locked sigs to %s" % sigfile)
        types = {}
        for k in self.runtaskdeps:
            if taskfilter:
                if not k in taskfilter:
                    continue
            fn = k.rsplit(".",1)[0]
            t = self.lockedhashfn[fn].split(" ")[1].split(":")[5]
            t = 't-' + t.replace('_', '-')
            if t not in types:
                types[t] = []
            types[t].append(k)

        with open(sigfile, "w") as f:
            for t in types:
                f.write('SIGGEN_LOCKEDSIGS_%s = "\\\n' % t)
                types[t].sort()
                sortedk = sorted(types[t], key=lambda k: self.lockedpnmap[k.rsplit(".",1)[0]])
                for k in sortedk:
                    fn = k.rsplit(".",1)[0]
                    task = k.rsplit(".",1)[1]
                    if k not in self.taskhash:
                        continue
                    f.write("    " + self.lockedpnmap[fn] + ":" + task + ":" + self.taskhash[k] + " \\\n")
                f.write('    "\n')
            f.write('SIGGEN_LOCKEDSIGS_TYPES_%s = "%s"' % (self.machine, " ".join(types.keys())))

    def checkhashes(self, missed, ret, sq_fn, sq_task, sq_hash, sq_hashfn, d):
        warn_msgs = []
        error_msgs = []
        sstate_missing_msgs = []

        for task in range(len(sq_fn)):
            if task not in ret:
                for pn in self.lockedsigs:
                    if sq_hash[task] in self.lockedsigs[pn].itervalues():
                        if sq_task[task] == 'do_shared_workdir':
                            continue
                        sstate_missing_msgs.append("Locked sig is set for %s:%s (%s) yet not in sstate cache?"
                                               % (pn, sq_task[task], sq_hash[task]))

        checklevel = d.getVar("SIGGEN_LOCKEDSIGS_TASKSIG_CHECK", True)
        if checklevel == 'warn':
            warn_msgs += self.mismatch_msgs
        elif checklevel == 'error':
            error_msgs += self.mismatch_msgs

        checklevel = d.getVar("SIGGEN_LOCKEDSIGS_SSTATE_EXISTS_CHECK", True)
        if checklevel == 'warn':
            warn_msgs += sstate_missing_msgs
        elif checklevel == 'error':
            error_msgs += sstate_missing_msgs

        if warn_msgs:
            bb.warn("\n".join(warn_msgs))
        if error_msgs:
            bb.fatal("\n".join(error_msgs))


# Insert these classes into siggen's namespace so it can see and select them
bb.siggen.SignatureGeneratorOEBasic = SignatureGeneratorOEBasic
bb.siggen.SignatureGeneratorOEBasicHash = SignatureGeneratorOEBasicHash


def find_siginfo(pn, taskname, taskhashlist, d):
    """ Find signature data files for comparison purposes """

    import fnmatch
    import glob

    if taskhashlist:
        hashfiles = {}

    if not taskname:
        # We have to derive pn and taskname
        key = pn
        splitit = key.split('.bb.')
        taskname = splitit[1]
        pn = os.path.basename(splitit[0]).split('_')[0]
        if key.startswith('virtual:native:'):
            pn = pn + '-native'

    filedates = {}

    # First search in stamps dir
    localdata = d.createCopy()
    localdata.setVar('MULTIMACH_TARGET_SYS', '*')
    localdata.setVar('PN', pn)
    localdata.setVar('PV', '*')
    localdata.setVar('PR', '*')
    localdata.setVar('EXTENDPE', '')
    stamp = localdata.getVar('STAMP', True)
    if pn.startswith("gcc-source"):
        # gcc-source shared workdir is a special case :(
        stamp = localdata.expand("${STAMPS_DIR}/work-shared/gcc-${PV}-${PR}")

    filespec = '%s.%s.sigdata.*' % (stamp, taskname)
    foundall = False
    import glob
    for fullpath in glob.glob(filespec):
        match = False
        if taskhashlist:
            for taskhash in taskhashlist:
                if fullpath.endswith('.%s' % taskhash):
                    hashfiles[taskhash] = fullpath
                    if len(hashfiles) == len(taskhashlist):
                        foundall = True
                        break
        else:
            try:
                filedates[fullpath] = os.stat(fullpath).st_mtime
            except OSError:
                continue

    if not taskhashlist or (len(filedates) < 2 and not foundall):
        # That didn't work, look in sstate-cache
        hashes = taskhashlist or ['*']
        localdata = bb.data.createCopy(d)
        for hashval in hashes:
            localdata.setVar('PACKAGE_ARCH', '*')
            localdata.setVar('TARGET_VENDOR', '*')
            localdata.setVar('TARGET_OS', '*')
            localdata.setVar('PN', pn)
            localdata.setVar('PV', '*')
            localdata.setVar('PR', '*')
            localdata.setVar('BB_TASKHASH', hashval)
            swspec = localdata.getVar('SSTATE_SWSPEC', True)
            if taskname in ['do_fetch', 'do_unpack', 'do_patch', 'do_populate_lic', 'do_preconfigure'] and swspec:
                localdata.setVar('SSTATE_PKGSPEC', '${SSTATE_SWSPEC}')
            elif pn.endswith('-native') or "-cross-" in pn or "-crosssdk-" in pn:
                localdata.setVar('SSTATE_EXTRAPATH', "${NATIVELSBSTRING}/")
            sstatename = taskname[3:]
            filespec = '%s_%s.*.siginfo' % (localdata.getVar('SSTATE_PKG', True), sstatename)

            if hashval != '*':
                sstatedir = "%s/%s" % (d.getVar('SSTATE_DIR', True), hashval[:2])
            else:
                sstatedir = d.getVar('SSTATE_DIR', True)

            for root, dirs, files in os.walk(sstatedir):
                for fn in files:
                    fullpath = os.path.join(root, fn)
                    if fnmatch.fnmatch(fullpath, filespec):
                        if taskhashlist:
                            hashfiles[hashval] = fullpath
                        else:
                            try:
                                filedates[fullpath] = os.stat(fullpath).st_mtime
                            except:
                                continue

    if taskhashlist:
        return hashfiles
    else:
        return filedates

bb.siggen.find_siginfo = find_siginfo


def sstate_get_manifest_filename(task, d):
    """
    Return the sstate manifest file path for a particular task.
    Also returns the datastore that can be used to query related variables.
    """
    d2 = d.createCopy()
    extrainf = d.getVarFlag("do_" + task, 'stamp-extra-info', True)
    if extrainf:
        d2.setVar("SSTATE_MANMACH", extrainf)
    return (d2.expand("${SSTATE_MANFILEPREFIX}.%s" % task), d2)
