# Yocto Project layer check tool
#
# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import re
import subprocess
from enum import Enum

import bb.tinfoil

class LayerType(Enum):
    BSP = 0
    DISTRO = 1
    SOFTWARE = 2
    CORE = 3
    ERROR_NO_LAYER_CONF = 98
    ERROR_BSP_DISTRO = 99

def _get_configurations(path):
    configs = []

    for f in os.listdir(path):
        file_path = os.path.join(path, f)
        if os.path.isfile(file_path) and f.endswith('.conf'):
            configs.append(f[:-5]) # strip .conf
    return configs

def _get_layer_collections(layer_path, lconf=None, data=None):
    import bb.parse
    import bb.data

    if lconf is None:
        lconf = os.path.join(layer_path, 'conf', 'layer.conf')

    if data is None:
        ldata = bb.data.init()
        bb.parse.init_parser(ldata)
    else:
        ldata = data.createCopy()

    ldata.setVar('LAYERDIR', layer_path)
    try:
        ldata = bb.parse.handle(lconf, ldata, include=True, baseconfig=True)
    except:
        raise RuntimeError("Parsing of layer.conf from layer: %s failed" % layer_path)
    ldata.expandVarref('LAYERDIR')

    collections = (ldata.getVar('BBFILE_COLLECTIONS') or '').split()
    if not collections:
        name = os.path.basename(layer_path)
        collections = [name]

    collections = {c: {} for c in collections}
    for name in collections:
        priority = ldata.getVar('BBFILE_PRIORITY_%s' % name)
        pattern = ldata.getVar('BBFILE_PATTERN_%s' % name)
        depends = ldata.getVar('LAYERDEPENDS_%s' % name)
        compat = ldata.getVar('LAYERSERIES_COMPAT_%s' % name)
        try:
            depDict = bb.utils.explode_dep_versions2(depends or "")
        except bb.utils.VersionStringException as vse:
            bb.fatal('Error parsing LAYERDEPENDS_%s: %s' % (name, str(vse)))

        collections[name]['priority'] = priority
        collections[name]['pattern'] = pattern
        collections[name]['depends'] = ' '.join(depDict.keys())
        collections[name]['compat'] = compat

    return collections

def _detect_layer(layer_path):
    """
        Scans layer directory to detect what type of layer
        is BSP, Distro or Software.

        Returns a dictionary with layer name, type and path.
    """

    layer = {}
    layer_name = os.path.basename(layer_path)

    layer['name'] = layer_name
    layer['path'] = layer_path
    layer['conf'] = {}

    if not os.path.isfile(os.path.join(layer_path, 'conf', 'layer.conf')):
        layer['type'] = LayerType.ERROR_NO_LAYER_CONF
        return layer

    machine_conf = os.path.join(layer_path, 'conf', 'machine')
    distro_conf = os.path.join(layer_path, 'conf', 'distro')

    is_bsp = False
    is_distro = False

    if os.path.isdir(machine_conf):
        machines = _get_configurations(machine_conf)
        if machines:
            is_bsp = True

    if os.path.isdir(distro_conf):
        distros = _get_configurations(distro_conf)
        if distros:
            is_distro = True

    layer['collections'] = _get_layer_collections(layer['path'])

    if layer_name == "meta" and "core" in layer['collections']:
        layer['type'] = LayerType.CORE
        layer['conf']['machines'] = machines
        layer['conf']['distros'] = distros
    elif is_bsp and is_distro:
        layer['type'] = LayerType.ERROR_BSP_DISTRO
    elif is_bsp:
        layer['type'] = LayerType.BSP
        layer['conf']['machines'] = machines
    elif is_distro:
        layer['type'] = LayerType.DISTRO
        layer['conf']['distros'] = distros
    else:
        layer['type'] = LayerType.SOFTWARE

    return layer

def detect_layers(layer_directories, no_auto):
    layers = []

    for directory in layer_directories:
        directory = os.path.realpath(directory)
        if directory[-1] == '/':
            directory = directory[0:-1]

        if no_auto:
            conf_dir = os.path.join(directory, 'conf')
            if os.path.isdir(conf_dir):
                layer = _detect_layer(directory)
                if layer:
                    layers.append(layer)
        else:
            for root, dirs, files in os.walk(directory):
                dir_name = os.path.basename(root)
                conf_dir = os.path.join(root, 'conf')
                if os.path.isdir(conf_dir):
                    layer = _detect_layer(root)
                    if layer:
                        layers.append(layer)

    return layers

def _find_layer(depend, layers):
    for layer in layers:
        if 'collections' not in layer:
            continue

        for collection in layer['collections']:
            if depend == collection:
                return layer
    return None

def sanity_check_layers(layers, logger):
    """
    Check that we didn't find duplicate collection names, as the layer that will
    be used is non-deterministic. The precise check is duplicate collections
    with different patterns, as the same pattern being repeated won't cause
    problems.
    """
    import collections

    passed = True
    seen = collections.defaultdict(set)
    for layer in layers:
        for name, data in layer.get("collections", {}).items():
            seen[name].add(data["pattern"])

    for name, patterns in seen.items():
        if len(patterns) > 1:
            passed = False
            logger.error("Collection %s found multiple times: %s" % (name, ", ".join(patterns)))
    return passed

def get_layer_dependencies(layer, layers, logger):
    def recurse_dependencies(depends, layer, layers, logger, ret = []):
        logger.debug('Processing dependencies %s for layer %s.' % \
                    (depends, layer['name']))

        for depend in depends.split():
            # core (oe-core) is suppose to be provided
            if depend == 'core':
                continue

            layer_depend = _find_layer(depend, layers)
            if not layer_depend:
                logger.error('Layer %s depends on %s and isn\'t found.' % \
                        (layer['name'], depend))
                ret = None
                continue

            # We keep processing, even if ret is None, this allows us to report
            # multiple errors at once
            if ret is not None and layer_depend not in ret:
                ret.append(layer_depend)
            else:
                # we might have processed this dependency already, in which case
                # we should not do it again (avoid recursive loop)
                continue

            # Recursively process...
            if 'collections' not in layer_depend:
                continue

            for collection in layer_depend['collections']:
                collect_deps = layer_depend['collections'][collection]['depends']
                if not collect_deps:
                    continue
                ret = recurse_dependencies(collect_deps, layer_depend, layers, logger, ret)

        return ret

    layer_depends = []
    for collection in layer['collections']:
        depends = layer['collections'][collection]['depends']
        if not depends:
            continue

        layer_depends = recurse_dependencies(depends, layer, layers, logger, layer_depends)

    # Note: [] (empty) is allowed, None is not!
    return layer_depends

def add_layer_dependencies(bblayersconf, layer, layers, logger):

    layer_depends = get_layer_dependencies(layer, layers, logger)
    if layer_depends is None:
        return False
    else:
        add_layers(bblayersconf, layer_depends, logger)

    return True

def add_layers(bblayersconf, layers, logger):
    # Don't add a layer that is already present.
    added = set()
    output = check_command('Getting existing layers failed.', 'bitbake-layers show-layers').decode('utf-8')
    for layer, path, pri in re.findall(r'^(\S+) +([^\n]*?) +(\d+)$', output, re.MULTILINE):
        added.add(path)

    with open(bblayersconf, 'a+') as f:
        for layer in layers:
            logger.info('Adding layer %s' % layer['name'])
            name = layer['name']
            path = layer['path']
            if path in added:
                logger.info('%s is already in %s' % (name, bblayersconf))
            else:
                added.add(path)
                f.write("\nBBLAYERS += \"%s\"\n" % path)
    return True

def check_bblayers(bblayersconf, layer_path, logger):
    '''
    If layer_path found in BBLAYERS return True
    '''
    import bb.parse
    import bb.data

    ldata = bb.parse.handle(bblayersconf, bb.data.init(), include=True)
    for bblayer in (ldata.getVar('BBLAYERS') or '').split():
        if os.path.normpath(bblayer) == os.path.normpath(layer_path):
            return True

    return False

def check_command(error_msg, cmd, cwd=None):
    '''
    Run a command under a shell, capture stdout and stderr in a single stream,
    throw an error when command returns non-zero exit code. Returns the output.
    '''

    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, cwd=cwd)
    output, _ = p.communicate()
    if p.returncode:
        msg = "%s\nCommand: %s\nOutput:\n%s" % (error_msg, cmd, output.decode('utf-8'))
        raise RuntimeError(msg)
    return output

def get_signatures(builddir, failsafe=False, machine=None, extravars=None):
    import re

    # some recipes needs to be excluded like meta-world-pkgdata
    # because a layer can add recipes to a world build so signature
    # will be change
    exclude_recipes = ('meta-world-pkgdata',)

    sigs = {}
    tune2tasks = {}

    cmd = 'BB_ENV_PASSTHROUGH_ADDITIONS="$BB_ENV_PASSTHROUGH_ADDITIONS BB_SIGNATURE_HANDLER" BB_SIGNATURE_HANDLER="OEBasicHash" '
    if extravars:
        cmd += extravars
        cmd += ' '
    if machine:
        cmd += 'MACHINE=%s ' % machine
    cmd += 'bitbake '
    if failsafe:
        cmd += '-k '
    cmd += '-S lockedsigs world'
    sigs_file = os.path.join(builddir, 'locked-sigs.inc')
    if os.path.exists(sigs_file):
        os.unlink(sigs_file)
    try:
        check_command('Generating signatures failed. This might be due to some parse error and/or general layer incompatibilities.',
                      cmd, builddir)
    except RuntimeError as ex:
        if failsafe and os.path.exists(sigs_file):
            # Ignore the error here. Most likely some recipes active
            # in a world build lack some dependencies. There is a
            # separate test_machine_world_build which exposes the
            # failure.
            pass
        else:
            raise

    sig_regex = re.compile(r"^(?P<task>.*:.*):(?P<hash>.*) .$")
    tune_regex = re.compile(r"(^|\s)SIGGEN_LOCKEDSIGS_t-(?P<tune>\S*)\s*=\s*")
    current_tune = None
    with open(sigs_file, 'r') as f:
        for line in f.readlines():
            line = line.strip()
            t = tune_regex.search(line)
            if t:
                current_tune = t.group('tune')
            s = sig_regex.match(line)
            if s:
                exclude = False
                for er in exclude_recipes:
                    (recipe, task) = s.group('task').split(':')
                    if er == recipe:
                        exclude = True
                        break
                if exclude:
                    continue

                sigs[s.group('task')] = s.group('hash')
                tune2tasks.setdefault(current_tune, []).append(s.group('task'))

    if not sigs:
        raise RuntimeError('Can\'t load signatures from %s' % sigs_file)

    return (sigs, tune2tasks)

def get_depgraph(targets=['world'], failsafe=False):
    '''
    Returns the dependency graph for the given target(s).
    The dependency graph is taken directly from DepTreeEvent.
    '''
    depgraph = None
    with bb.tinfoil.Tinfoil() as tinfoil:
        tinfoil.prepare(config_only=False)
        tinfoil.set_event_mask(['bb.event.NoProvider', 'bb.event.DepTreeGenerated', 'bb.command.CommandCompleted'])
        if not tinfoil.run_command('generateDepTreeEvent', targets, 'do_build'):
            raise RuntimeError('starting generateDepTreeEvent failed')
        while True:
            event = tinfoil.wait_event(timeout=1000)
            if event:
                if isinstance(event, bb.command.CommandFailed):
                    raise RuntimeError('Generating dependency information failed: %s' % event.error)
                elif isinstance(event, bb.command.CommandCompleted):
                    break
                elif isinstance(event, bb.event.NoProvider):
                    if failsafe:
                        # The event is informational, we will get information about the
                        # remaining dependencies eventually and thus can ignore this
                        # here like we do in get_signatures(), if desired.
                        continue
                    if event._reasons:
                        raise RuntimeError('Nothing provides %s: %s' % (event._item, event._reasons))
                    else:
                        raise RuntimeError('Nothing provides %s.' % (event._item))
                elif isinstance(event, bb.event.DepTreeGenerated):
                    depgraph = event._depgraph

    if depgraph is None:
        raise RuntimeError('Could not retrieve the depgraph.')
    return depgraph

def compare_signatures(old_sigs, curr_sigs):
    '''
    Compares the result of two get_signatures() calls. Returns None if no
    problems found, otherwise a string that can be used as additional
    explanation in self.fail().
    '''
    # task -> (old signature, new signature)
    sig_diff = {}
    for task in old_sigs:
        if task in curr_sigs and \
           old_sigs[task] != curr_sigs[task]:
            sig_diff[task] = (old_sigs[task], curr_sigs[task])

    if not sig_diff:
        return None

    # Beware, depgraph uses task=<pn>.<taskname> whereas get_signatures()
    # uses <pn>:<taskname>. Need to convert sometimes. The output follows
    # the convention from get_signatures() because that seems closer to
    # normal bitbake output.
    def sig2graph(task):
        pn, taskname = task.rsplit(':', 1)
        return pn + '.' + taskname
    def graph2sig(task):
        pn, taskname = task.rsplit('.', 1)
        return pn + ':' + taskname
    depgraph = get_depgraph(failsafe=True)
    depends = depgraph['tdepends']

    # If a task A has a changed signature, but none of its
    # dependencies, then we need to report it because it is
    # the one which introduces a change. Any task depending on
    # A (directly or indirectly) will also have a changed
    # signature, but we don't need to report it. It might have
    # its own changes, which will become apparent once the
    # issues that we do report are fixed and the test gets run
    # again.
    sig_diff_filtered = []
    for task, (old_sig, new_sig) in sig_diff.items():
        deps_tainted = False
        for dep in depends.get(sig2graph(task), ()):
            if graph2sig(dep) in sig_diff:
                deps_tainted = True
                break
        if not deps_tainted:
            sig_diff_filtered.append((task, old_sig, new_sig))

    msg = []
    msg.append('%d signatures changed, initial differences (first hash before, second after):' %
               len(sig_diff))
    for diff in sorted(sig_diff_filtered):
        recipe, taskname = diff[0].rsplit(':', 1)
        cmd = 'bitbake-diffsigs --task %s %s --signature %s %s' % \
              (recipe, taskname, diff[1], diff[2])
        msg.append('   %s: %s -> %s' % diff)
        msg.append('      %s' % cmd)
        try:
            output = check_command('Determining signature difference failed.',
                                   cmd).decode('utf-8')
        except RuntimeError as error:
            output = str(error)
        if output:
            msg.extend(['      ' + line for line in output.splitlines()])
            msg.append('')
    return '\n'.join(msg)


def get_git_toplevel(directory):
    """
    Try and find the top of the git repository that directory might be in.
    Returns the top-level directory, or None.
    """
    cmd = ["git", "-C", directory, "rev-parse", "--show-toplevel"]
    try:
        return subprocess.check_output(cmd, text=True).strip()
    except:
        return None
