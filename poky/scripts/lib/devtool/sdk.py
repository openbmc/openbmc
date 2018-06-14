# Development tool - sdk-update command plugin
#
# Copyright (C) 2015-2016 Intel Corporation
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
import subprocess
import logging
import glob
import shutil
import errno
import sys
import tempfile
import re
from devtool import exec_build_env_command, setup_tinfoil, parse_recipe, DevtoolError

logger = logging.getLogger('devtool')

def parse_locked_sigs(sigfile_path):
    """Return <pn:task>:<hash> dictionary"""
    sig_dict = {}
    with open(sigfile_path) as f:
        lines = f.readlines()
        for line in lines:
            if ':' in line:
                taskkey, _, hashval = line.rpartition(':')
                sig_dict[taskkey.strip()] = hashval.split()[0]
    return sig_dict

def generate_update_dict(sigfile_new, sigfile_old):
    """Return a dict containing <pn:task>:<hash> which indicates what need to be updated"""
    update_dict = {}
    sigdict_new = parse_locked_sigs(sigfile_new)
    sigdict_old = parse_locked_sigs(sigfile_old)
    for k in sigdict_new:
        if k not in sigdict_old:
            update_dict[k] = sigdict_new[k]
            continue
        if sigdict_new[k] != sigdict_old[k]:
            update_dict[k] = sigdict_new[k]
            continue
    return update_dict

def get_sstate_objects(update_dict, sstate_dir):
    """Return a list containing sstate objects which are to be installed"""
    sstate_objects = []
    for k in update_dict:
        files = set()
        hashval = update_dict[k]
        p = sstate_dir + '/' + hashval[:2] + '/*' + hashval + '*.tgz'
        files |= set(glob.glob(p))
        p = sstate_dir + '/*/' + hashval[:2] + '/*' + hashval + '*.tgz'
        files |= set(glob.glob(p))
        files = list(files)
        if len(files) == 1:
            sstate_objects.extend(files)
        elif len(files) > 1:
            logger.error("More than one matching sstate object found for %s" % hashval)

    return sstate_objects

def mkdir(d):
    try:
        os.makedirs(d)
    except OSError as e:
        if e.errno != errno.EEXIST:
            raise e

def install_sstate_objects(sstate_objects, src_sdk, dest_sdk):
    """Install sstate objects into destination SDK"""
    sstate_dir = os.path.join(dest_sdk, 'sstate-cache')
    if not os.path.exists(sstate_dir):
        logger.error("Missing sstate-cache directory in %s, it might not be an extensible SDK." % dest_sdk)
        raise
    for sb in sstate_objects:
        dst = sb.replace(src_sdk, dest_sdk)
        destdir = os.path.dirname(dst)
        mkdir(destdir)
        logger.debug("Copying %s to %s" % (sb, dst))
        shutil.copy(sb, dst)

def check_manifest(fn, basepath):
    import bb.utils
    changedfiles = []
    with open(fn, 'r') as f:
        for line in f:
            splitline = line.split()
            if len(splitline) > 1:
                chksum = splitline[0]
                fpath = splitline[1]
                curr_chksum = bb.utils.sha256_file(os.path.join(basepath, fpath))
                if chksum != curr_chksum:
                    logger.debug('File %s changed: old csum = %s, new = %s' % (os.path.join(basepath, fpath), curr_chksum, chksum))
                    changedfiles.append(fpath)
    return changedfiles

def sdk_update(args, config, basepath, workspace):
    """Entry point for devtool sdk-update command"""
    updateserver = args.updateserver
    if not updateserver:
        updateserver = config.get('SDK', 'updateserver', '')
    logger.debug("updateserver: %s" % updateserver)

    # Make sure we are using sdk-update from within SDK
    logger.debug("basepath = %s" % basepath)
    old_locked_sig_file_path = os.path.join(basepath, 'conf/locked-sigs.inc')
    if not os.path.exists(old_locked_sig_file_path):
        logger.error("Not using devtool's sdk-update command from within an extensible SDK. Please specify correct basepath via --basepath option")
        return -1
    else:
        logger.debug("Found conf/locked-sigs.inc in %s" % basepath)

    if not '://' in updateserver:
        logger.error("Update server must be a URL")
        return -1

    layers_dir = os.path.join(basepath, 'layers')
    conf_dir = os.path.join(basepath, 'conf')

    # Grab variable values
    tinfoil = setup_tinfoil(config_only=True, basepath=basepath)
    try:
        stamps_dir = tinfoil.config_data.getVar('STAMPS_DIR')
        sstate_mirrors = tinfoil.config_data.getVar('SSTATE_MIRRORS')
        site_conf_version = tinfoil.config_data.getVar('SITE_CONF_VERSION')
    finally:
        tinfoil.shutdown()

    tmpsdk_dir = tempfile.mkdtemp()
    try:
        os.makedirs(os.path.join(tmpsdk_dir, 'conf'))
        new_locked_sig_file_path = os.path.join(tmpsdk_dir, 'conf', 'locked-sigs.inc')
        # Fetch manifest from server
        tmpmanifest = os.path.join(tmpsdk_dir, 'conf', 'sdk-conf-manifest')
        ret = subprocess.call("wget -q -O %s %s/conf/sdk-conf-manifest" % (tmpmanifest, updateserver), shell=True)
        if ret != 0:
            logger.error("Cannot dowload files from %s" % updateserver)
            return ret
        changedfiles = check_manifest(tmpmanifest, basepath)
        if not changedfiles:
            logger.info("Already up-to-date")
            return 0
        # Update metadata
        logger.debug("Updating metadata via git ...")
        #Check for the status before doing a fetch and reset
        if os.path.exists(os.path.join(basepath, 'layers/.git')):
            out = subprocess.check_output("git status --porcelain", shell=True, cwd=layers_dir)
            if not out:
                ret = subprocess.call("git fetch --all; git reset --hard @{u}", shell=True, cwd=layers_dir)
            else:
                logger.error("Failed to update metadata as there have been changes made to it. Aborting.");
                logger.error("Changed files:\n%s" % out);
                return -1
        else:
            ret = -1
        if ret != 0:
            ret = subprocess.call("git clone %s/layers/.git" % updateserver, shell=True, cwd=tmpsdk_dir)
            if ret != 0:
                logger.error("Updating metadata via git failed")
                return ret
        logger.debug("Updating conf files ...")
        for changedfile in changedfiles:
            ret = subprocess.call("wget -q -O %s %s/%s" % (changedfile, updateserver, changedfile), shell=True, cwd=tmpsdk_dir)
            if ret != 0:
                logger.error("Updating %s failed" % changedfile)
                return ret

        # Check if UNINATIVE_CHECKSUM changed
        uninative = False
        if 'conf/local.conf' in changedfiles:
            def read_uninative_checksums(fn):
                chksumitems = []
                with open(fn, 'r') as f:
                    for line in f:
                        if line.startswith('UNINATIVE_CHECKSUM'):
                            splitline = re.split(r'[\[\]"\']', line)
                            if len(splitline) > 3:
                                chksumitems.append((splitline[1], splitline[3]))
                return chksumitems

            oldsums = read_uninative_checksums(os.path.join(basepath, 'conf/local.conf'))
            newsums = read_uninative_checksums(os.path.join(tmpsdk_dir, 'conf/local.conf'))
            if oldsums != newsums:
                uninative = True
                for buildarch, chksum in newsums:
                    uninative_file = os.path.join('downloads', 'uninative', chksum, '%s-nativesdk-libc.tar.bz2' % buildarch)
                    mkdir(os.path.join(tmpsdk_dir, os.path.dirname(uninative_file)))
                    ret = subprocess.call("wget -q -O %s %s/%s" % (uninative_file, updateserver, uninative_file), shell=True, cwd=tmpsdk_dir)

        # Ok, all is well at this point - move everything over
        tmplayers_dir = os.path.join(tmpsdk_dir, 'layers')
        if os.path.exists(tmplayers_dir):
            shutil.rmtree(layers_dir)
            shutil.move(tmplayers_dir, layers_dir)
        for changedfile in changedfiles:
            destfile = os.path.join(basepath, changedfile)
            os.remove(destfile)
            shutil.move(os.path.join(tmpsdk_dir, changedfile), destfile)
        os.remove(os.path.join(conf_dir, 'sdk-conf-manifest'))
        shutil.move(tmpmanifest, conf_dir)
        if uninative:
            shutil.rmtree(os.path.join(basepath, 'downloads', 'uninative'))
            shutil.move(os.path.join(tmpsdk_dir, 'downloads', 'uninative'), os.path.join(basepath, 'downloads'))

        if not sstate_mirrors:
            with open(os.path.join(conf_dir, 'site.conf'), 'a') as f:
                f.write('SCONF_VERSION = "%s"\n' % site_conf_version)
                f.write('SSTATE_MIRRORS_append = " file://.* %s/sstate-cache/PATH \\n "\n' % updateserver)
    finally:
        shutil.rmtree(tmpsdk_dir)

    if not args.skip_prepare:
        # Find all potentially updateable tasks
        sdk_update_targets = []
        tasks = ['do_populate_sysroot', 'do_packagedata']
        for root, _, files in os.walk(stamps_dir):
            for fn in files:
                if not '.sigdata.' in fn:
                    for task in tasks:
                        if '.%s.' % task in fn or '.%s_setscene.' % task in fn:
                            sdk_update_targets.append('%s:%s' % (os.path.basename(root), task))
        # Run bitbake command for the whole SDK
        logger.info("Preparing build system... (This may take some time.)")
        try:
            exec_build_env_command(config.init_path, basepath, 'bitbake --setscene-only %s' % ' '.join(sdk_update_targets), stderr=subprocess.STDOUT)
            output, _ = exec_build_env_command(config.init_path, basepath, 'bitbake -n %s' % ' '.join(sdk_update_targets), stderr=subprocess.STDOUT)
            runlines = []
            for line in output.splitlines():
                if 'Running task ' in line:
                    runlines.append(line)
            if runlines:
                logger.error('Unexecuted tasks found in preparation log:\n  %s' % '\n  '.join(runlines))
                return -1
        except bb.process.ExecutionError as e:
            logger.error('Preparation failed:\n%s' % e.stdout)
            return -1
    return 0

def sdk_install(args, config, basepath, workspace):
    """Entry point for the devtool sdk-install command"""

    import oe.recipeutils
    import bb.process

    for recipe in args.recipename:
        if recipe in workspace:
            raise DevtoolError('recipe %s is a recipe in your workspace' % recipe)

    tasks = ['do_populate_sysroot', 'do_packagedata']
    stampprefixes = {}
    def checkstamp(recipe):
        stampprefix = stampprefixes[recipe]
        stamps = glob.glob(stampprefix + '*')
        for stamp in stamps:
            if '.sigdata.' not in stamp and stamp.startswith((stampprefix + '.', stampprefix + '_setscene.')):
                return True
        else:
            return False

    install_recipes = []
    tinfoil = setup_tinfoil(config_only=False, basepath=basepath)
    try:
        for recipe in args.recipename:
            rd = parse_recipe(config, tinfoil, recipe, True)
            if not rd:
                return 1
            stampprefixes[recipe] = '%s.%s' % (rd.getVar('STAMP'), tasks[0])
            if checkstamp(recipe):
                logger.info('%s is already installed' % recipe)
            else:
                install_recipes.append(recipe)
    finally:
        tinfoil.shutdown()

    if install_recipes:
        logger.info('Installing %s...' % ', '.join(install_recipes))
        install_tasks = []
        for recipe in install_recipes:
            for task in tasks:
                if recipe.endswith('-native') and 'package' in task:
                    continue
                install_tasks.append('%s:%s' % (recipe, task))
        options = ''
        if not args.allow_build:
            options += ' --setscene-only'
        try:
            exec_build_env_command(config.init_path, basepath, 'bitbake %s %s' % (options, ' '.join(install_tasks)), watch=True)
        except bb.process.ExecutionError as e:
            raise DevtoolError('Failed to install %s:\n%s' % (recipe, str(e)))
        failed = False
        for recipe in install_recipes:
            if checkstamp(recipe):
                logger.info('Successfully installed %s' % recipe)
            else:
                raise DevtoolError('Failed to install %s - unavailable' % recipe)
                failed = True
        if failed:
            return 2

        try:
            exec_build_env_command(config.init_path, basepath, 'bitbake build-sysroots', watch=True)
        except bb.process.ExecutionError as e:
            raise DevtoolError('Failed to bitbake build-sysroots:\n%s' % (str(e)))


def register_commands(subparsers, context):
    """Register devtool subcommands from the sdk plugin"""
    if context.fixed_setup:
        parser_sdk = subparsers.add_parser('sdk-update',
                                           help='Update SDK components',
                                           description='Updates installed SDK components from a remote server',
                                           group='sdk')
        updateserver = context.config.get('SDK', 'updateserver', '')
        if updateserver:
            parser_sdk.add_argument('updateserver', help='The update server to fetch latest SDK components from (default %s)' % updateserver, nargs='?')
        else:
            parser_sdk.add_argument('updateserver', help='The update server to fetch latest SDK components from')
        parser_sdk.add_argument('--skip-prepare', action="store_true", help='Skip re-preparing the build system after updating (for debugging only)')
        parser_sdk.set_defaults(func=sdk_update)

        parser_sdk_install = subparsers.add_parser('sdk-install',
                                                   help='Install additional SDK components',
                                                   description='Installs additional recipe development files into the SDK. (You can use "devtool search" to find available recipes.)',
                                                   group='sdk')
        parser_sdk_install.add_argument('recipename', help='Name of the recipe to install the development artifacts for', nargs='+')
        parser_sdk_install.add_argument('-s', '--allow-build', help='Allow building requested item(s) from source', action='store_true')
        parser_sdk_install.set_defaults(func=sdk_install)
