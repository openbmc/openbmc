# Copyright (C) 2012 Linux Foundation
# Author: Richard Purdie
# Some code and influence taken from srctree.bbclass:
# Copyright (C) 2009 Chris Larson <clarson@kergoth.com>
# Released under the MIT license (see COPYING.MIT for the terms)
#
# externalsrc.bbclass enables use of an existing source tree, usually external to
# the build system to build a piece of software rather than the usual fetch/unpack/patch
# process.
#
# To use, add externalsrc to the global inherit and set EXTERNALSRC to point at the
# directory you want to use containing the sources e.g. from local.conf for a recipe
# called "myrecipe" you would do:
#
# INHERIT += "externalsrc"
# EXTERNALSRC_pn-myrecipe = "/path/to/my/source/tree"
#
# In order to make this class work for both target and native versions (or with
# multilibs/cross or other BBCLASSEXTEND variants), B is set to point to a separate
# directory under the work directory (split source and build directories). This is
# the default, but the build directory can be set to the source directory if
# circumstances dictate by setting EXTERNALSRC_BUILD to the same value, e.g.:
#
# EXTERNALSRC_BUILD_pn-myrecipe = "/path/to/my/source/tree"
#

SRCTREECOVEREDTASKS ?= "do_patch do_unpack do_fetch"
EXTERNALSRC_SYMLINKS ?= "oe-workdir:${WORKDIR} oe-logs:${T}"

python () {
    externalsrc = d.getVar('EXTERNALSRC')
    externalsrcbuild = d.getVar('EXTERNALSRC_BUILD')

    if externalsrc and not externalsrc.startswith("/"):
        bb.error("EXTERNALSRC must be an absolute path")
    if externalsrcbuild and not externalsrcbuild.startswith("/"):
        bb.error("EXTERNALSRC_BUILD must be an absolute path")

    # If this is the base recipe and EXTERNALSRC is set for it or any of its
    # derivatives, then enable BB_DONT_CACHE to force the recipe to always be
    # re-parsed so that the file-checksums function for do_compile is run every
    # time.
    bpn = d.getVar('BPN')
    classextend = (d.getVar('BBCLASSEXTEND') or '').split()
    if bpn == d.getVar('PN') or not classextend:
        if (externalsrc or
                ('native' in classextend and
                 d.getVar('EXTERNALSRC_pn-%s-native' % bpn)) or
                ('nativesdk' in classextend and
                 d.getVar('EXTERNALSRC_pn-nativesdk-%s' % bpn)) or
                ('cross' in classextend and
                 d.getVar('EXTERNALSRC_pn-%s-cross' % bpn))):
            d.setVar('BB_DONT_CACHE', '1')

    if externalsrc:
        import oe.recipeutils
        import oe.path

        d.setVar('S', externalsrc)
        if externalsrcbuild:
            d.setVar('B', externalsrcbuild)
        else:
            d.setVar('B', '${WORKDIR}/${BPN}-${PV}/')

        local_srcuri = []
        fetch = bb.fetch2.Fetch((d.getVar('SRC_URI') or '').split(), d)
        for url in fetch.urls:
            url_data = fetch.ud[url]
            parm = url_data.parm
            if (url_data.type == 'file' or
                    url_data.type == 'npmsw' or
                    'type' in parm and parm['type'] == 'kmeta'):
                local_srcuri.append(url)

        d.setVar('SRC_URI', ' '.join(local_srcuri))

        # Dummy value because the default function can't be called with blank SRC_URI
        d.setVar('SRCPV', '999')

        if d.getVar('CONFIGUREOPT_DEPTRACK') == '--disable-dependency-tracking':
            d.setVar('CONFIGUREOPT_DEPTRACK', '')

        tasks = filter(lambda k: d.getVarFlag(k, "task"), d.keys())

        for task in tasks:
            if task.endswith("_setscene"):
                # sstate is never going to work for external source trees, disable it
                bb.build.deltask(task, d)
            elif os.path.realpath(d.getVar('S')) == os.path.realpath(d.getVar('B')):
                # Since configure will likely touch ${S}, ensure only we lock so one task has access at a time
                d.appendVarFlag(task, "lockfiles", " ${S}/singletask.lock")

            # We do not want our source to be wiped out, ever (kernel.bbclass does this for do_clean)
            cleandirs = oe.recipeutils.split_var_value(d.getVarFlag(task, 'cleandirs', False) or '')
            setvalue = False
            for cleandir in cleandirs[:]:
                if oe.path.is_path_parent(externalsrc, d.expand(cleandir)):
                    cleandirs.remove(cleandir)
                    setvalue = True
            if setvalue:
                d.setVarFlag(task, 'cleandirs', ' '.join(cleandirs))

        fetch_tasks = ['do_fetch', 'do_unpack']
        # If we deltask do_patch, there's no dependency to ensure do_unpack gets run, so add one
        # Note that we cannot use d.appendVarFlag() here because deps is expected to be a list object, not a string
        d.setVarFlag('do_configure', 'deps', (d.getVarFlag('do_configure', 'deps', False) or []) + ['do_unpack'])

        for task in d.getVar("SRCTREECOVEREDTASKS").split():
            if local_srcuri and task in fetch_tasks:
                continue
            bb.build.deltask(task, d)

        d.prependVarFlag('do_compile', 'prefuncs', "externalsrc_compile_prefunc ")
        d.prependVarFlag('do_configure', 'prefuncs', "externalsrc_configure_prefunc ")

        d.setVarFlag('do_compile', 'file-checksums', '${@srctree_hash_files(d)}')
        d.setVarFlag('do_configure', 'file-checksums', '${@srctree_configure_hash_files(d)}')

        # We don't want the workdir to go away
        d.appendVar('RM_WORK_EXCLUDE', ' ' + d.getVar('PN'))

        bb.build.addtask('do_buildclean',
                         'do_clean' if d.getVar('S') == d.getVar('B') else None,
                         None, d)

        # If B=S the same builddir is used even for different architectures.
        # Thus, use a shared CONFIGURESTAMPFILE and STAMP directory so that
        # change of do_configure task hash is correctly detected and stamps are
        # invalidated if e.g. MACHINE changes.
        if d.getVar('S') == d.getVar('B'):
            configstamp = '${TMPDIR}/work-shared/${PN}/${EXTENDPE}${PV}-${PR}/configure.sstate'
            d.setVar('CONFIGURESTAMPFILE', configstamp)
            d.setVar('STAMP', '${STAMPS_DIR}/work-shared/${PN}/${EXTENDPE}${PV}-${PR}')
            d.setVar('STAMPCLEAN', '${STAMPS_DIR}/work-shared/${PN}/*-*')
}

python externalsrc_configure_prefunc() {
    s_dir = d.getVar('S')
    # Create desired symlinks
    symlinks = (d.getVar('EXTERNALSRC_SYMLINKS') or '').split()
    newlinks = []
    for symlink in symlinks:
        symsplit = symlink.split(':', 1)
        lnkfile = os.path.join(s_dir, symsplit[0])
        target = d.expand(symsplit[1])
        if len(symsplit) > 1:
            if os.path.islink(lnkfile):
                # Link already exists, leave it if it points to the right location already
                if os.readlink(lnkfile) == target:
                    continue
                os.unlink(lnkfile)
            elif os.path.exists(lnkfile):
                # File/dir exists with same name as link, just leave it alone
                continue
            os.symlink(target, lnkfile)
            newlinks.append(symsplit[0])
    # Hide the symlinks from git
    try:
        git_exclude_file = os.path.join(s_dir, '.git/info/exclude')
        if os.path.exists(git_exclude_file):
            with open(git_exclude_file, 'r+') as efile:
                elines = efile.readlines()
                for link in newlinks:
                    if link in elines or '/'+link in elines:
                        continue
                    efile.write('/' + link + '\n')
    except IOError as ioe:
        bb.note('Failed to hide EXTERNALSRC_SYMLINKS from git')
}

python externalsrc_compile_prefunc() {
    # Make it obvious that this is happening, since forgetting about it could lead to much confusion
    bb.plain('NOTE: %s: compiling from external source tree %s' % (d.getVar('PN'), d.getVar('EXTERNALSRC')))
}

do_buildclean[dirs] = "${S} ${B}"
do_buildclean[nostamp] = "1"
do_buildclean[doc] = "Call 'make clean' or equivalent in ${B}"
externalsrc_do_buildclean() {
	if [ -e Makefile -o -e makefile -o -e GNUmakefile ]; then
		rm -f ${@' '.join([x.split(':')[0] for x in (d.getVar('EXTERNALSRC_SYMLINKS') or '').split()])}
		if [ "${CLEANBROKEN}" != "1" ]; then
			oe_runmake clean || die "make failed"
		fi
	else
		bbnote "nothing to do - no makefile found"
	fi
}

def srctree_hash_files(d, srcdir=None):
    import shutil
    import subprocess
    import tempfile
    import hashlib

    s_dir = srcdir or d.getVar('EXTERNALSRC')
    git_dir = None

    try:
        git_dir = os.path.join(s_dir,
            subprocess.check_output(['git', '-C', s_dir, 'rev-parse', '--git-dir'], stderr=subprocess.DEVNULL).decode("utf-8").rstrip())
        top_git_dir = os.path.join(s_dir, subprocess.check_output(['git', '-C', d.getVar("TOPDIR"), 'rev-parse', '--git-dir'],
            stderr=subprocess.DEVNULL).decode("utf-8").rstrip())
        if git_dir == top_git_dir:
            git_dir = None
    except subprocess.CalledProcessError:
        pass

    ret = " "
    if git_dir is not None:
        oe_hash_file = os.path.join(git_dir, 'oe-devtool-tree-sha1-%s' % d.getVar('PN'))
        with tempfile.NamedTemporaryFile(prefix='oe-devtool-index') as tmp_index:
            # Clone index
            shutil.copyfile(os.path.join(git_dir, 'index'), tmp_index.name)
            # Update our custom index
            env = os.environ.copy()
            env['GIT_INDEX_FILE'] = tmp_index.name
            subprocess.check_output(['git', 'add', '-A', '.'], cwd=s_dir, env=env)
            git_sha1 = subprocess.check_output(['git', 'write-tree'], cwd=s_dir, env=env).decode("utf-8")
            submodule_helper = subprocess.check_output(['git', 'submodule--helper', 'list'], cwd=s_dir, env=env).decode("utf-8")
            for line in submodule_helper.splitlines():
                module_dir = os.path.join(s_dir, line.rsplit(maxsplit=1)[1])
                proc = subprocess.Popen(['git', 'add', '-A', '.'], cwd=module_dir, env=env, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
                proc.communicate()
                proc = subprocess.Popen(['git', 'write-tree'], cwd=module_dir, env=env, stdout=subprocess.PIPE, stderr=subprocess.DEVNULL)
                stdout, _ = proc.communicate()
                git_sha1 += stdout.decode("utf-8")
            sha1 = hashlib.sha1(git_sha1.encode("utf-8")).hexdigest()
        with open(oe_hash_file, 'w') as fobj:
            fobj.write(sha1)
        ret = oe_hash_file + ':True'
    else:
        ret = s_dir + '/*:True'
    return ret

def srctree_configure_hash_files(d):
    """
    Get the list of files that should trigger do_configure to re-execute,
    based on the value of CONFIGURE_FILES
    """
    in_files = (d.getVar('CONFIGURE_FILES') or '').split()
    out_items = []
    search_files = []
    for entry in in_files:
        if entry.startswith('/'):
            out_items.append('%s:%s' % (entry, os.path.exists(entry)))
        else:
            search_files.append(entry)
    if search_files:
        s_dir = d.getVar('EXTERNALSRC')
        for root, _, files in os.walk(s_dir):
            for f in files:
                if f in search_files:
                    out_items.append('%s:True' % os.path.join(root, f))
    return ' '.join(out_items)

EXPORT_FUNCTIONS do_buildclean
