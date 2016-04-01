# Development tool - sdk-update command plugin

import os
import subprocess
import logging
import glob
import shutil
import errno
import sys
from devtool import exec_build_env_command, setup_tinfoil, DevtoolError

logger = logging.getLogger('devtool')

def plugin_init(pluginlist):
    """Plugin initialization"""
    pass

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

def sdk_update(args, config, basepath, workspace):
    # Fetch locked-sigs.inc file from remote/local destination
    updateserver = args.updateserver
    if not updateserver:
        updateserver = config.get('SDK', 'updateserver', '')
    if not updateserver:
        raise DevtoolError("Update server not specified in config file, you must specify it on the command line")
    logger.debug("updateserver: %s" % updateserver)

    # Make sure we are using sdk-update from within SDK
    logger.debug("basepath = %s" % basepath)
    old_locked_sig_file_path = os.path.join(basepath, 'conf/locked-sigs.inc')
    if not os.path.exists(old_locked_sig_file_path):
        logger.error("Not using devtool's sdk-update command from within an extensible SDK. Please specify correct basepath via --basepath option")
        return -1
    else:
        logger.debug("Found conf/locked-sigs.inc in %s" % basepath)

    if ':' in updateserver:
        is_remote = True
    else:
        is_remote = False

    if not is_remote:
        # devtool sdk-update /local/path/to/latest/sdk
        new_locked_sig_file_path = os.path.join(updateserver, 'conf/locked-sigs.inc')
        if not os.path.exists(new_locked_sig_file_path):
            logger.error("%s doesn't exist or is not an extensible SDK" % updateserver)
            return -1
        else:
            logger.debug("Found conf/locked-sigs.inc in %s" % updateserver)
        update_dict = generate_update_dict(new_locked_sig_file_path, old_locked_sig_file_path)
        logger.debug("update_dict = %s" % update_dict)
        sstate_dir = os.path.join(newsdk_path, 'sstate-cache')
        if not os.path.exists(sstate_dir):
            logger.error("sstate-cache directory not found under %s" % newsdk_path)
            return 1
        sstate_objects = get_sstate_objects(update_dict, sstate_dir)
        logger.debug("sstate_objects = %s" % sstate_objects)
        if len(sstate_objects) == 0:
            logger.info("No need to update.")
            return 0
        logger.info("Installing sstate objects into %s", basepath)
        install_sstate_objects(sstate_objects, updateserver.rstrip('/'), basepath)
        logger.info("Updating configuration files")
        new_conf_dir = os.path.join(updateserver, 'conf')
        old_conf_dir = os.path.join(basepath, 'conf')
        shutil.rmtree(old_conf_dir)
        shutil.copytree(new_conf_dir, old_conf_dir)
        logger.info("Updating layers")
        new_layers_dir = os.path.join(updateserver, 'layers')
        old_layers_dir = os.path.join(basepath, 'layers')
        shutil.rmtree(old_layers_dir)
        ret = subprocess.call("cp -a %s %s" % (new_layers_dir, old_layers_dir), shell=True)
        if ret != 0:
            logger.error("Copying %s to %s failed" % (new_layers_dir, old_layers_dir))
            return ret
    else:
        # devtool sdk-update http://myhost/sdk
        tmpsdk_dir = '/tmp/sdk-ext'
        if os.path.exists(tmpsdk_dir):
            shutil.rmtree(tmpsdk_dir)
        os.makedirs(tmpsdk_dir)
        os.makedirs(os.path.join(tmpsdk_dir, 'conf'))
        # Fetch locked-sigs.inc from update server
        ret = subprocess.call("wget -q -O - %s/conf/locked-sigs.inc > %s/locked-sigs.inc" % (updateserver, os.path.join(tmpsdk_dir, 'conf')), shell=True)
        if ret != 0:
            logger.error("Fetching conf/locked-sigs.inc from %s to %s/locked-sigs.inc failed" % (updateserver, os.path.join(tmpsdk_dir, 'conf')))
            return ret
        else:
            logger.info("Fetching conf/locked-sigs.inc from %s to %s/locked-sigs.inc succeeded" % (updateserver, os.path.join(tmpsdk_dir, 'conf')))
        new_locked_sig_file_path = os.path.join(tmpsdk_dir, 'conf/locked-sigs.inc')
        update_dict = generate_update_dict(new_locked_sig_file_path, old_locked_sig_file_path)
        logger.debug("update_dict = %s" % update_dict)
        if len(update_dict) == 0:
            logger.info("No need to update.")
            return 0
        # Update metadata
        logger.debug("Updating meta data via git ...")
        # Try using 'git pull', if failed, use 'git clone'
        if os.path.exists(os.path.join(basepath, 'layers/.git')):
            ret = subprocess.call("cd layers && git pull %s/layers/.git" % updateserver, shell=True)
        else:
            ret = -1
        if ret != 0:
            ret = subprocess.call("rm -rf layers && git clone %s/layers/.git" % updateserver, shell=True)
        if ret != 0:
            logger.error("Updating meta data via git failed")
            return ret
        logger.debug("Updating conf files ...")
        conf_files = ['local.conf', 'bblayers.conf', 'devtool.conf', 'locked-sigs.inc']
        for conf in conf_files:
            ret = subprocess.call("wget -q -O - %s/conf/%s > conf/%s" % (updateserver, conf, conf), shell=True)
            if ret != 0:
                logger.error("Update %s failed" % conf)
                return ret
        with open(os.path.join(basepath, 'conf/local.conf'), 'a') as f:
            f.write('SSTATE_MIRRORS_append = " file://.* %s/sstate-cache/PATH \\n "\n' % updateserver)

    # Run bitbake command for the whole SDK
    sdk_targets = config.get('SDK', 'sdk_targets')
    logger.info("Executing 'bitbake %s' ... (This may take some time.)" % sdk_targets)
    try:
        exec_build_env_command(config.init_path, basepath, 'bitbake %s' % sdk_targets)
    except:
        logger.error('bitbake %s failed' % sdk_targets)
        return -1
    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from the sdk plugin"""
    if context.fixed_setup:
        parser_sdk = subparsers.add_parser('sdk-update', help='Update SDK components from a nominated location')
        parser_sdk.add_argument('updateserver', help='The update server to fetch latest SDK components from', nargs='?')
        parser_sdk.set_defaults(func=sdk_update)
