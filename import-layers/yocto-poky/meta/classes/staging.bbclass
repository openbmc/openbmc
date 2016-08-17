
sysroot_stage_dir() {
	src="$1"
	dest="$2"
	# if the src doesn't exist don't do anything
	if [ ! -d "$src" ]; then
		 return
	fi

	mkdir -p "$dest"
	(
		cd $src
		find . -print0 | cpio --null -pdlu $dest
	)
}

sysroot_stage_libdir() {
	src="$1"
	dest="$2"

	sysroot_stage_dir $src $dest
}

sysroot_stage_dirs() {
	from="$1"
	to="$2"

	sysroot_stage_dir $from${includedir} $to${includedir}
	if [ "${BUILD_SYS}" = "${HOST_SYS}" ]; then
		sysroot_stage_dir $from${bindir} $to${bindir}
		sysroot_stage_dir $from${sbindir} $to${sbindir}
		sysroot_stage_dir $from${base_bindir} $to${base_bindir}
		sysroot_stage_dir $from${base_sbindir} $to${base_sbindir}
		sysroot_stage_dir $from${libexecdir} $to${libexecdir}
		sysroot_stage_dir $from${sysconfdir} $to${sysconfdir}
		sysroot_stage_dir $from${localstatedir} $to${localstatedir}
	fi
	if [ -d $from${libdir} ]
	then
		sysroot_stage_libdir $from${libdir} $to${libdir}
	fi
	if [ -d $from${base_libdir} ]
	then
		sysroot_stage_libdir $from${base_libdir} $to${base_libdir}
	fi
	if [ -d $from${nonarch_base_libdir} ]
	then
		sysroot_stage_libdir $from${nonarch_base_libdir} $to${nonarch_base_libdir}
	fi
	sysroot_stage_dir $from${datadir} $to${datadir}
	# We don't care about docs/info/manpages/locales
	rm -rf $to${mandir}/ $to${docdir}/ $to${infodir}/ ${to}${datadir}/locale/
	rm -rf $to${datadir}/applications/ $to${datadir}/fonts/ $to${datadir}/pixmaps/
}

sysroot_stage_all() {
	sysroot_stage_dirs ${D} ${SYSROOT_DESTDIR}
}

python sysroot_strip () {
    import stat, errno

    dvar = d.getVar('SYSROOT_DESTDIR', True)
    pn = d.getVar('PN', True)

    os.chdir(dvar)

    # Return type (bits):
    # 0 - not elf
    # 1 - ELF
    # 2 - stripped
    # 4 - executable
    # 8 - shared library
    # 16 - kernel module
    def isELF(path):
        type = 0
        ret, result = oe.utils.getstatusoutput("file \"%s\"" % path.replace("\"", "\\\""))

        if ret:
            bb.error("split_and_strip_files: 'file %s' failed" % path)
            return type

        # Not stripped
        if "ELF" in result:
            type |= 1
            if "not stripped" not in result:
                type |= 2
            if "executable" in result:
                type |= 4
            if "shared" in result:
                type |= 8
        return type


    elffiles = {}
    inodes = {}
    libdir = os.path.abspath(dvar + os.sep + d.getVar("libdir", True))
    baselibdir = os.path.abspath(dvar + os.sep + d.getVar("base_libdir", True))
    if (d.getVar('INHIBIT_SYSROOT_STRIP', True) != '1'):
        #
        # First lets figure out all of the files we may have to process
        #
        for root, dirs, files in os.walk(dvar):
            for f in files:
                file = os.path.join(root, f)

                try:
                    ltarget = oe.path.realpath(file, dvar, False)
                    s = os.lstat(ltarget)
                except OSError as e:
                    (err, strerror) = e.args
                    if err != errno.ENOENT:
                        raise
                    # Skip broken symlinks
                    continue
                if not s:
                    continue
                # Check its an excutable
                if (s[stat.ST_MODE] & stat.S_IXUSR) or (s[stat.ST_MODE] & stat.S_IXGRP) or (s[stat.ST_MODE] & stat.S_IXOTH) \
                        or ((file.startswith(libdir) or file.startswith(baselibdir)) and ".so" in f):
                    # If it's a symlink, and points to an ELF file, we capture the readlink target
                    if os.path.islink(file):
                        continue

                    # It's a file (or hardlink), not a link
                    # ...but is it ELF, and is it already stripped?
                    elf_file = isELF(file)
                    if elf_file & 1:
                        if elf_file & 2:
                            if 'already-stripped' in (d.getVar('INSANE_SKIP_' + pn, True) or "").split():
                                bb.note("Skipping file %s from %s for already-stripped QA test" % (file[len(dvar):], pn))
                            else:
                                bb.warn("File '%s' from %s was already stripped, this will prevent future debugging!" % (file[len(dvar):], pn))
                            continue

                        if s.st_ino in inodes:
                            os.unlink(file)
                            os.link(inodes[s.st_ino], file)
                        else:
                            inodes[s.st_ino] = file
                            # break hardlink
                            bb.utils.copyfile(file, file)
                            elffiles[file] = elf_file

        #
        # Now strip them (in parallel)
        #
        strip = d.getVar("STRIP", True)
        sfiles = []
        for file in elffiles:
            elf_file = int(elffiles[file])
            #bb.note("Strip %s" % file)
            sfiles.append((file, elf_file, strip))

        oe.utils.multiprocess_exec(sfiles, oe.package.runstrip)
}

do_populate_sysroot[dirs] = "${SYSROOT_DESTDIR}"
do_populate_sysroot[umask] = "022"

addtask populate_sysroot after do_install

SYSROOT_PREPROCESS_FUNCS ?= ""
SYSROOT_DESTDIR = "${WORKDIR}/sysroot-destdir"
SYSROOT_LOCK = "${STAGING_DIR}/staging.lock"

# We clean out any existing sstate from the sysroot if we rerun configure
python sysroot_cleansstate () {
    ss = sstate_state_fromvars(d, "populate_sysroot")
    sstate_clean(ss, d)
}
do_configure[prefuncs] += "sysroot_cleansstate"


BB_SETSCENE_VERIFY_FUNCTION = "sysroot_checkhashes"

def sysroot_checkhashes(covered, tasknames, fnids, fns, d, invalidtasks = None):
    problems = set()
    configurefnids = set()
    if not invalidtasks:
        invalidtasks = xrange(len(tasknames))
    for task in invalidtasks:
        if tasknames[task] == "do_configure" and task not in covered:
            configurefnids.add(fnids[task])
    for task in covered:
        if tasknames[task] == "do_populate_sysroot" and fnids[task] in configurefnids:
            problems.add(task)
    return problems

python do_populate_sysroot () {
    bb.build.exec_func("sysroot_stage_all", d)
    bb.build.exec_func("sysroot_strip", d)
    for f in (d.getVar('SYSROOT_PREPROCESS_FUNCS', True) or '').split():
        bb.build.exec_func(f, d)
    pn = d.getVar("PN", True)
    multiprov = d.getVar("MULTI_PROVIDER_WHITELIST", True).split()
    provdir = d.expand("${SYSROOT_DESTDIR}${base_prefix}/sysroot-providers/")
    bb.utils.mkdirhier(provdir)
    for p in d.getVar("PROVIDES", True).split():
        if p in multiprov:
            continue
        p = p.replace("/", "_")
        with open(provdir + p, "w") as f:
            f.write(pn)
}

do_populate_sysroot[vardeps] += "${SYSROOT_PREPROCESS_FUNCS}"
do_populate_sysroot[vardepsexclude] += "MULTI_PROVIDER_WHITELIST"

SSTATETASKS += "do_populate_sysroot"
do_populate_sysroot[cleandirs] = "${SYSROOT_DESTDIR}"
do_populate_sysroot[sstate-inputdirs] = "${SYSROOT_DESTDIR}"
do_populate_sysroot[sstate-outputdirs] = "${STAGING_DIR_HOST}/"
do_populate_sysroot[stamp-extra-info] = "${MACHINE}"

python do_populate_sysroot_setscene () {
    sstate_setscene(d)
}
addtask do_populate_sysroot_setscene


