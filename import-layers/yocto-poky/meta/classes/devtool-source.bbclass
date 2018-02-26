# Development tool - source extraction helper class
#
# NOTE: this class is intended for use by devtool and should not be
# inherited manually.
#
# Copyright (C) 2014-2017 Intel Corporation
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


DEVTOOL_TEMPDIR ?= ""
DEVTOOL_PATCH_SRCDIR = "${DEVTOOL_TEMPDIR}/patchworkdir"


python() {
    tempdir = d.getVar('DEVTOOL_TEMPDIR')

    if not tempdir:
        bb.fatal('devtool-source class is for internal use by devtool only')

    # Make a subdir so we guard against WORKDIR==S
    workdir = os.path.join(tempdir, 'workdir')
    d.setVar('WORKDIR', workdir)
    if not d.getVar('S').startswith(workdir):
        # Usually a shared workdir recipe (kernel, gcc)
        # Try to set a reasonable default
        if bb.data.inherits_class('kernel', d):
            d.setVar('S', '${WORKDIR}/source')
        else:
            d.setVar('S', '${WORKDIR}/%s' % os.path.basename(d.getVar('S')))
    if bb.data.inherits_class('kernel', d):
        # We don't want to move the source to STAGING_KERNEL_DIR here
        d.setVar('STAGING_KERNEL_DIR', '${S}')

    d.setVar('STAMPS_DIR', os.path.join(tempdir, 'stamps'))
    d.setVar('T', os.path.join(tempdir, 'temp'))

    # Hook in pre/postfuncs
    is_kernel_yocto = bb.data.inherits_class('kernel-yocto', d)
    if is_kernel_yocto:
        unpacktask = 'do_kernel_checkout'
        d.appendVarFlag('do_configure', 'postfuncs', ' devtool_post_configure')
    else:
        unpacktask = 'do_unpack'
    d.appendVarFlag(unpacktask, 'postfuncs', ' devtool_post_unpack')
    d.prependVarFlag('do_patch', 'prefuncs', ' devtool_pre_patch')
    d.appendVarFlag('do_patch', 'postfuncs', ' devtool_post_patch')

    # NOTE: in order for the patch stuff to be fully functional,
    # PATCHTOOL and PATCH_COMMIT_FUNCTIONS need to be set; we can't
    # do that here because we can't guarantee the order of the anonymous
    # functions, so it gets done in the bbappend we create.
}


python devtool_post_unpack() {
    import oe.recipeutils
    import shutil
    sys.path.insert(0, os.path.join(d.getVar('COREBASE'), 'scripts', 'lib'))
    import scriptutils
    from devtool import setup_git_repo

    tempdir = d.getVar('DEVTOOL_TEMPDIR')
    workdir = d.getVar('WORKDIR')
    srcsubdir = d.getVar('S')

    def _move_file(src, dst):
        """Move a file. Creates all the directory components of destination path."""
        dst_d = os.path.dirname(dst)
        if dst_d:
            bb.utils.mkdirhier(dst_d)
        shutil.move(src, dst)

    def _ls_tree(directory):
        """Recursive listing of files in a directory"""
        ret = []
        for root, dirs, files in os.walk(directory):
            ret.extend([os.path.relpath(os.path.join(root, fname), directory) for
                        fname in files])
        return ret

    # Move local source files into separate subdir
    recipe_patches = [os.path.basename(patch) for patch in
                        oe.recipeutils.get_recipe_patches(d)]
    local_files = oe.recipeutils.get_recipe_local_files(d)

    # Ignore local files with subdir={BP}
    srcabspath = os.path.abspath(srcsubdir)
    local_files = [fname for fname in local_files if
                    os.path.exists(os.path.join(workdir, fname)) and
                    (srcabspath == workdir or not
                    os.path.join(workdir, fname).startswith(srcabspath +
                        os.sep))]
    if local_files:
        for fname in local_files:
            _move_file(os.path.join(workdir, fname),
                        os.path.join(tempdir, 'oe-local-files', fname))
        with open(os.path.join(tempdir, 'oe-local-files', '.gitignore'),
                    'w') as f:
            f.write('# Ignore local files, by default. Remove this file '
                    'if you want to commit the directory to Git\n*\n')

    if srcsubdir == workdir:
        # Find non-patch non-local sources that were "unpacked" to srctree
        # directory
        src_files = [fname for fname in _ls_tree(workdir) if
                        os.path.basename(fname) not in recipe_patches]
        srcsubdir = d.getVar('DEVTOOL_PATCH_SRCDIR')
        # Move source files to S
        for path in src_files:
            _move_file(os.path.join(workdir, path),
                        os.path.join(srcsubdir, path))
    elif os.path.dirname(srcsubdir) != workdir:
        # Handle if S is set to a subdirectory of the source
        srcsubdir = os.path.join(workdir, os.path.relpath(srcsubdir, workdir).split(os.sep)[0])

    scriptutils.git_convert_standalone_clone(srcsubdir)

    # Make sure that srcsubdir exists
    bb.utils.mkdirhier(srcsubdir)
    if not os.listdir(srcsubdir):
        bb.warn("No source unpacked to S - either the %s recipe "
                "doesn't use any source or the correct source "
                "directory could not be determined" % d.getVar('PN'))

    devbranch = d.getVar('DEVTOOL_DEVBRANCH')
    setup_git_repo(srcsubdir, d.getVar('PV'), devbranch, d=d)

    (stdout, _) = bb.process.run('git rev-parse HEAD', cwd=srcsubdir)
    initial_rev = stdout.rstrip()
    with open(os.path.join(tempdir, 'initial_rev'), 'w') as f:
        f.write(initial_rev)

    with open(os.path.join(tempdir, 'srcsubdir'), 'w') as f:
        f.write(srcsubdir)
}

python devtool_pre_patch() {
    if d.getVar('S') == d.getVar('WORKDIR'):
        d.setVar('S', '${DEVTOOL_PATCH_SRCDIR}')
}

python devtool_post_patch() {
    tempdir = d.getVar('DEVTOOL_TEMPDIR')
    with open(os.path.join(tempdir, 'srcsubdir'), 'r') as f:
        srcsubdir = f.read()
    bb.process.run('git tag -f devtool-patched', cwd=srcsubdir)
}

python devtool_post_configure() {
    import shutil
    tempdir = d.getVar('DEVTOOL_TEMPDIR')
    shutil.copy2(os.path.join(d.getVar('B'), '.config'), tempdir)
}
