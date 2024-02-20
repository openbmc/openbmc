#!/usr/bin/env python3

# Development tool - utility functions for plugins
#
# Copyright (C) 2014 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool plugins module"""

import os
import sys
import subprocess
import logging
import re
import codecs

logger = logging.getLogger('devtool')

class DevtoolError(Exception):
    """Exception for handling devtool errors"""
    def __init__(self, message, exitcode=1):
        super(DevtoolError, self).__init__(message)
        self.exitcode = exitcode


def exec_build_env_command(init_path, builddir, cmd, watch=False, **options):
    """Run a program in bitbake build context"""
    import bb
    if not 'cwd' in options:
        options["cwd"] = builddir
    if init_path:
        # As the OE init script makes use of BASH_SOURCE to determine OEROOT,
        # and can't determine it when running under dash, we need to set
        # the executable to bash to correctly set things up
        if not 'executable' in options:
            options['executable'] = 'bash'
        logger.debug('Executing command: "%s" using init path %s' % (cmd, init_path))
        init_prefix = '. %s %s > /dev/null && ' % (init_path, builddir)
    else:
        logger.debug('Executing command "%s"' % cmd)
        init_prefix = ''
    if watch:
        if sys.stdout.isatty():
            # Fool bitbake into thinking it's outputting to a terminal (because it is, indirectly)
            cmd = 'script -e -q -c "%s" /dev/null' % cmd
        return exec_watch('%s%s' % (init_prefix, cmd), **options)
    else:
        return bb.process.run('%s%s' % (init_prefix, cmd), **options)

def exec_watch(cmd, **options):
    """Run program with stdout shown on sys.stdout"""
    import bb
    if isinstance(cmd, str) and not "shell" in options:
        options["shell"] = True

    process = subprocess.Popen(
        cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, **options
    )

    reader = codecs.getreader('utf-8')(process.stdout)
    buf = ''
    while True:
        out = reader.read(1, 1)
        if out:
            sys.stdout.write(out)
            sys.stdout.flush()
            buf += out
        elif out == '' and process.poll() != None:
            break

    if process.returncode != 0:
        raise bb.process.ExecutionError(cmd, process.returncode, buf, None)

    return buf, None

def exec_fakeroot(d, cmd, **kwargs):
    """Run a command under fakeroot (pseudo, in fact) so that it picks up the appropriate file permissions"""
    # Grab the command and check it actually exists
    fakerootcmd = d.getVar('FAKEROOTCMD')
    fakerootenv = d.getVar('FAKEROOTENV')
    exec_fakeroot_no_d(fakerootcmd, fakerootenv, cmd, kwargs)

def exec_fakeroot_no_d(fakerootcmd, fakerootenv, cmd, **kwargs):
    if not os.path.exists(fakerootcmd):
        logger.error('pseudo executable %s could not be found - have you run a build yet? pseudo-native should install this and if you have run any build then that should have been built')
        return 2
    # Set up the appropriate environment
    newenv = dict(os.environ)
    for varvalue in fakerootenv.split():
        if '=' in varvalue:
            splitval = varvalue.split('=', 1)
            newenv[splitval[0]] = splitval[1]
    return subprocess.call("%s %s" % (fakerootcmd, cmd), env=newenv, **kwargs)

def setup_tinfoil(config_only=False, basepath=None, tracking=False):
    """Initialize tinfoil api from bitbake"""
    import scriptpath
    orig_cwd = os.path.abspath(os.curdir)
    try:
        if basepath:
            os.chdir(basepath)
        bitbakepath = scriptpath.add_bitbake_lib_path()
        if not bitbakepath:
            logger.error("Unable to find bitbake by searching parent directory of this script or PATH")
            sys.exit(1)

        import bb.tinfoil
        tinfoil = bb.tinfoil.Tinfoil(tracking=tracking)
        try:
            tinfoil.logger.setLevel(logger.getEffectiveLevel())
            tinfoil.prepare(config_only)
        except bb.tinfoil.TinfoilUIException:
            tinfoil.shutdown()
            raise DevtoolError('Failed to start bitbake environment')
        except:
            tinfoil.shutdown()
            raise
    finally:
        os.chdir(orig_cwd)
    return tinfoil

def parse_recipe(config, tinfoil, pn, appends, filter_workspace=True):
    """Parse the specified recipe"""
    try:
        recipefile = tinfoil.get_recipe_file(pn)
    except bb.providers.NoProvider as e:
        logger.error(str(e))
        return None
    if appends:
        append_files = tinfoil.get_file_appends(recipefile)
        if filter_workspace:
            # Filter out appends from the workspace
            append_files = [path for path in append_files if
                            not path.startswith(config.workspace_path)]
    else:
        append_files = None
    try:
        rd = tinfoil.parse_recipe_file(recipefile, appends, append_files)
    except Exception as e:
        logger.error(str(e))
        return None
    return rd

def check_workspace_recipe(workspace, pn, checksrc=True, bbclassextend=False):
    """
    Check that a recipe is in the workspace and (optionally) that source
    is present.
    """

    workspacepn = pn

    for recipe, value in workspace.items():
        if recipe == pn:
            break
        if bbclassextend:
            recipefile = value['recipefile']
            if recipefile:
                targets = get_bbclassextend_targets(recipefile, recipe)
                if pn in targets:
                    workspacepn = recipe
                    break
    else:
        raise DevtoolError("No recipe named '%s' in your workspace" % pn)

    if checksrc:
        srctree = workspace[workspacepn]['srctree']
        if not os.path.exists(srctree):
            raise DevtoolError("Source tree %s for recipe %s does not exist" % (srctree, workspacepn))
        if not os.listdir(srctree):
            raise DevtoolError("Source tree %s for recipe %s is empty" % (srctree, workspacepn))

    return workspacepn

def use_external_build(same_dir, no_same_dir, d):
    """
    Determine if we should use B!=S (separate build and source directories) or not
    """
    b_is_s = True
    if no_same_dir:
        logger.info('Using separate build directory since --no-same-dir specified')
        b_is_s = False
    elif same_dir:
        logger.info('Using source tree as build directory since --same-dir specified')
    elif bb.data.inherits_class('autotools-brokensep', d):
        logger.info('Using source tree as build directory since recipe inherits autotools-brokensep')
    elif os.path.abspath(d.getVar('B')) == os.path.abspath(d.getVar('S')):
        logger.info('Using source tree as build directory since that would be the default for this recipe')
    else:
        b_is_s = False
    return b_is_s

def setup_git_repo(repodir, version, devbranch, basetag='devtool-base', d=None):
    """
    Set up the git repository for the source tree
    """
    import bb.process
    import oe.patch
    if not os.path.exists(os.path.join(repodir, '.git')):
        bb.process.run('git init', cwd=repodir)
        bb.process.run('git config --local gc.autodetach 0', cwd=repodir)
        bb.process.run('git add -f -A .', cwd=repodir)
        commit_cmd = ['git']
        oe.patch.GitApplyTree.gitCommandUserOptions(commit_cmd, d=d)
        commit_cmd += ['commit', '-q']
        stdout, _ = bb.process.run('git status --porcelain', cwd=repodir)
        if not stdout:
            commit_cmd.append('--allow-empty')
            commitmsg = "Initial empty commit with no upstream sources"
        elif version:
            commitmsg = "Initial commit from upstream at version %s" % version
        else:
            commitmsg = "Initial commit from upstream"
        commit_cmd += ['-m', commitmsg]
        bb.process.run(commit_cmd, cwd=repodir)

    # Ensure singletask.lock (as used by externalsrc.bbclass) is ignored by git
    gitinfodir = os.path.join(repodir, '.git', 'info')
    try:
        os.mkdir(gitinfodir)
    except FileExistsError:
        pass
    excludes = []
    excludefile = os.path.join(gitinfodir, 'exclude')
    try:
        with open(excludefile, 'r') as f:
            excludes = f.readlines()
    except FileNotFoundError:
        pass
    if 'singletask.lock\n' not in excludes:
        excludes.append('singletask.lock\n')
    with open(excludefile, 'w') as f:
        for line in excludes:
            f.write(line)

    bb.process.run('git checkout -b %s' % devbranch, cwd=repodir)
    bb.process.run('git tag -f %s' % basetag, cwd=repodir)

    # if recipe unpacks another git repo inside S, we need to declare it as a regular git submodule now,
    # so we will be able to tag branches on it and extract patches when doing finish/update on the recipe
    stdout, _ = bb.process.run("git status --porcelain", cwd=repodir)
    found = False
    for line in stdout.splitlines():
        if line.endswith("/"):
            new_dir = line.split()[1]
            for root, dirs, files in os.walk(os.path.join(repodir, new_dir)):
                if ".git" in dirs + files:
                    (stdout, _) = bb.process.run('git remote', cwd=root)
                    remote = stdout.splitlines()[0]
                    (stdout, _) = bb.process.run('git remote get-url %s' % remote, cwd=root)
                    remote_url = stdout.splitlines()[0]
                    logger.error(os.path.relpath(os.path.join(root, ".."), root))
                    bb.process.run('git submodule add %s %s' % (remote_url, os.path.relpath(root, os.path.join(root, ".."))), cwd=os.path.join(root, ".."))
                    found = True
                if found:
                    oe.patch.GitApplyTree.commitIgnored("Add additional submodule from SRC_URI", dir=os.path.join(root, ".."), d=d)
                    found = False
    if os.path.exists(os.path.join(repodir, '.gitmodules')):
        bb.process.run('git submodule foreach --recursive  "git tag -f %s"' % basetag, cwd=repodir)

def recipe_to_append(recipefile, config, wildcard=False):
    """
    Convert a recipe file to a bbappend file path within the workspace.
    NOTE: if the bbappend already exists, you should be using
    workspace[args.recipename]['bbappend'] instead of calling this
    function.
    """
    appendname = os.path.splitext(os.path.basename(recipefile))[0]
    if wildcard:
        appendname = re.sub(r'_.*', '_%', appendname)
    appendpath = os.path.join(config.workspace_path, 'appends')
    appendfile = os.path.join(appendpath, appendname + '.bbappend')
    return appendfile

def get_bbclassextend_targets(recipefile, pn):
    """
    Cheap function to get BBCLASSEXTEND and then convert that to the
    list of targets that would result.
    """
    import bb.utils

    values = {}
    def get_bbclassextend_varfunc(varname, origvalue, op, newlines):
        values[varname] = origvalue
        return origvalue, None, 0, True
    with open(recipefile, 'r') as f:
        bb.utils.edit_metadata(f, ['BBCLASSEXTEND'], get_bbclassextend_varfunc)

    targets = []
    bbclassextend = values.get('BBCLASSEXTEND', '').split()
    if bbclassextend:
        for variant in bbclassextend:
            if variant == 'nativesdk':
                targets.append('%s-%s' % (variant, pn))
            elif variant in ['native', 'cross', 'crosssdk']:
                targets.append('%s-%s' % (pn, variant))
    return targets

def replace_from_file(path, old, new):
    """Replace strings on a file"""

    def read_file(path):
        data = None
        with open(path) as f:
            data = f.read()
        return data

    def write_file(path, data):
        if data is None:
            return
        wdata = data.rstrip() + "\n"
        with open(path, "w") as f:
            f.write(wdata)

    # In case old is None, return immediately
    if old is None:
        return
    try:
        rdata = read_file(path)
    except IOError as e:
        # if file does not exit, just quit, otherwise raise an exception
        if e.errno == errno.ENOENT:
            return
        else:
            raise

    old_contents = rdata.splitlines()
    new_contents = []
    for old_content in old_contents:
        try:
            new_contents.append(old_content.replace(old, new))
        except ValueError:
            pass
    write_file(path, "\n".join(new_contents))


def update_unlockedsigs(basepath, workspace, fixed_setup, extra=None):
    """ This function will make unlocked-sigs.inc match the recipes in the
    workspace plus any extras we want unlocked. """

    if not fixed_setup:
        # Only need to write this out within the eSDK
        return

    if not extra:
        extra = []

    confdir = os.path.join(basepath, 'conf')
    unlockedsigs = os.path.join(confdir, 'unlocked-sigs.inc')

    # Get current unlocked list if any
    values = {}
    def get_unlockedsigs_varfunc(varname, origvalue, op, newlines):
        values[varname] = origvalue
        return origvalue, None, 0, True
    if os.path.exists(unlockedsigs):
        with open(unlockedsigs, 'r') as f:
            bb.utils.edit_metadata(f, ['SIGGEN_UNLOCKED_RECIPES'], get_unlockedsigs_varfunc)
    unlocked = sorted(values.get('SIGGEN_UNLOCKED_RECIPES', []))

    # If the new list is different to the current list, write it out
    newunlocked = sorted(list(workspace.keys()) + extra)
    if unlocked != newunlocked:
        bb.utils.mkdirhier(confdir)
        with open(unlockedsigs, 'w') as f:
            f.write("# DO NOT MODIFY! YOUR CHANGES WILL BE LOST.\n" +
                    "# This layer was created by the OpenEmbedded devtool" +
                    " utility in order to\n" +
                    "# contain recipes that are unlocked.\n")

            f.write('SIGGEN_UNLOCKED_RECIPES += "\\\n')
            for pn in newunlocked:
                f.write('    ' + pn)
            f.write('"')

def check_prerelease_version(ver, operation):
    if 'pre' in ver or 'rc' in ver:
        logger.warning('Version "%s" looks like a pre-release version. '
                       'If that is the case, in order to ensure that the '
                       'version doesn\'t appear to go backwards when you '
                       'later upgrade to the final release version, it is '
                       'recommmended that instead you use '
                       '<current version>+<pre-release version> e.g. if '
                       'upgrading from 1.9 to 2.0-rc2 use "1.9+2.0-rc2". '
                       'If you prefer not to reset and re-try, you can change '
                       'the version after %s succeeds using "devtool rename" '
                       'with -V/--version.' % (ver, operation))

def check_git_repo_dirty(repodir):
    """Check if a git repository is clean or not"""
    stdout, _ = bb.process.run('git status --porcelain', cwd=repodir)
    return stdout

def check_git_repo_op(srctree, ignoredirs=None):
    """Check if a git repository is in the middle of a rebase"""
    stdout, _ = bb.process.run('git rev-parse --show-toplevel', cwd=srctree)
    topleveldir = stdout.strip()
    if ignoredirs and topleveldir in ignoredirs:
        return
    gitdir = os.path.join(topleveldir, '.git')
    if os.path.exists(os.path.join(gitdir, 'rebase-merge')):
        raise DevtoolError("Source tree %s appears to be in the middle of a rebase - please resolve this first" % srctree)
    if os.path.exists(os.path.join(gitdir, 'rebase-apply')):
        raise DevtoolError("Source tree %s appears to be in the middle of 'git am' or 'git apply' - please resolve this first" % srctree)
