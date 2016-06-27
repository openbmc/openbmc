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
    externalsrc = d.getVar('EXTERNALSRC', True)
    if externalsrc:
        d.setVar('S', externalsrc)
        externalsrcbuild = d.getVar('EXTERNALSRC_BUILD', True)
        if externalsrcbuild:
            d.setVar('B', externalsrcbuild)
        else:
            d.setVar('B', '${WORKDIR}/${BPN}-${PV}/')

        local_srcuri = []
        fetch = bb.fetch2.Fetch((d.getVar('SRC_URI', True) or '').split(), d)
        for url in fetch.urls:
            url_data = fetch.ud[url]
            parm = url_data.parm
            if (url_data.type == 'file' or
                    'type' in parm and parm['type'] == 'kmeta'):
                local_srcuri.append(url)

        d.setVar('SRC_URI', ' '.join(local_srcuri))

        if '{SRCPV}' in d.getVar('PV', False):
            # Dummy value because the default function can't be called with blank SRC_URI
            d.setVar('SRCPV', '999')

        tasks = filter(lambda k: d.getVarFlag(k, "task", True), d.keys())

        for task in tasks:
            if task.endswith("_setscene"):
                # sstate is never going to work for external source trees, disable it
                bb.build.deltask(task, d)
            else:
                # Since configure will likely touch ${S}, ensure only we lock so one task has access at a time
                d.appendVarFlag(task, "lockfiles", " ${S}/singletask.lock")

            # We do not want our source to be wiped out, ever (kernel.bbclass does this for do_clean)
            cleandirs = (d.getVarFlag(task, 'cleandirs', False) or '').split()
            setvalue = False
            for cleandir in cleandirs[:]:
                if d.expand(cleandir) == externalsrc:
                    cleandirs.remove(cleandir)
                    setvalue = True
            if setvalue:
                d.setVarFlag(task, 'cleandirs', ' '.join(cleandirs))

        fetch_tasks = ['do_fetch', 'do_unpack']
        # If we deltask do_patch, there's no dependency to ensure do_unpack gets run, so add one
        # Note that we cannot use d.appendVarFlag() here because deps is expected to be a list object, not a string
        d.setVarFlag('do_configure', 'deps', (d.getVarFlag('do_configure', 'deps', False) or []) + ['do_unpack'])

        for task in d.getVar("SRCTREECOVEREDTASKS", True).split():
            if local_srcuri and task in fetch_tasks:
                continue
            bb.build.deltask(task, d)

        d.prependVarFlag('do_compile', 'prefuncs', "externalsrc_compile_prefunc ")
        d.prependVarFlag('do_configure', 'prefuncs', "externalsrc_configure_prefunc ")

        # Force the recipe to be always re-parsed so that the file_checksums
        # function is run every time
        d.setVar('BB_DONT_CACHE', '1')
        d.setVarFlag('do_compile', 'file-checksums', '${@srctree_hash_files(d)}')

        # We don't want the workdir to go away
        d.appendVar('RM_WORK_EXCLUDE', ' ' + d.getVar('PN', True))

        # If B=S the same builddir is used even for different architectures.
        # Thus, use a shared CONFIGURESTAMPFILE and STAMP directory so that
        # change of do_configure task hash is correctly detected and stamps are
        # invalidated if e.g. MACHINE changes.
        if d.getVar('S', True) == d.getVar('B', True):
            configstamp = '${TMPDIR}/work-shared/${PN}/${EXTENDPE}${PV}-${PR}/configure.sstate'
            d.setVar('CONFIGURESTAMPFILE', configstamp)
            d.setVar('STAMP', '${STAMPS_DIR}/work-shared/${PN}/${EXTENDPE}${PV}-${PR}')
}

python externalsrc_configure_prefunc() {
    # Create desired symlinks
    symlinks = (d.getVar('EXTERNALSRC_SYMLINKS', True) or '').split()
    for symlink in symlinks:
        symsplit = symlink.split(':', 1)
        lnkfile = os.path.join(d.getVar('S', True), symsplit[0])
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
}

python externalsrc_compile_prefunc() {
    # Make it obvious that this is happening, since forgetting about it could lead to much confusion
    bb.plain('NOTE: %s: compiling from external source tree %s' % (d.getVar('PN', True), d.getVar('EXTERNALSRC', True)))
}

def srctree_hash_files(d):
    import shutil
    import subprocess
    import tempfile

    s_dir = d.getVar('EXTERNALSRC', True)
    git_dir = os.path.join(s_dir, '.git')
    oe_hash_file = os.path.join(git_dir, 'oe-devtool-tree-sha1')

    ret = " "
    if os.path.exists(git_dir):
        with tempfile.NamedTemporaryFile(dir=git_dir, prefix='oe-devtool-index') as tmp_index:
            # Clone index
            shutil.copy2(os.path.join(git_dir, 'index'), tmp_index.name)
            # Update our custom index
            env = os.environ.copy()
            env['GIT_INDEX_FILE'] = tmp_index.name
            subprocess.check_output(['git', 'add', '.'], cwd=s_dir, env=env)
            sha1 = subprocess.check_output(['git', 'write-tree'], cwd=s_dir, env=env)
        with open(oe_hash_file, 'w') as fobj:
            fobj.write(sha1)
        ret = oe_hash_file + ':True'
    else:
        ret = d.getVar('EXTERNALSRC', True) + '/*:True'
    return ret
