# Copyright (C) 2016-2018 Wind River Systems, Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

import logging
import json

from collections import OrderedDict, defaultdict

from urllib.parse import unquote, urlparse

import layerindexlib

import layerindexlib.plugin

logger = logging.getLogger('BitBake.layerindexlib.cooker')

import bb.utils

def plugin_init(plugins):
    return CookerPlugin()

class CookerPlugin(layerindexlib.plugin.IndexPlugin):
    def __init__(self):
        self.type = "cooker"

        self.server_connection = None
        self.ui_module = None
        self.server = None

    def _run_command(self, command, path, default=None):
        try:
            result, _ = bb.process.run(command, cwd=path)
            result = result.strip()
        except bb.process.ExecutionError:
            result = default
        return result

    def _handle_git_remote(self, remote):
        if "://" not in remote:
            if ':' in remote:
                # This is assumed to be ssh
                remote = "ssh://" + remote
            else:
                # This is assumed to be a file path
                remote = "file://" + remote
        return remote

    def _get_bitbake_info(self):
        """Return a tuple of bitbake information"""

        # Our path SHOULD be .../bitbake/lib/layerindex/cooker.py
        bb_path = os.path.dirname(__file__) # .../bitbake/lib/layerindex/cooker.py
        bb_path = os.path.dirname(bb_path)  # .../bitbake/lib/layerindex
        bb_path = os.path.dirname(bb_path)  # .../bitbake/lib
        bb_path = os.path.dirname(bb_path)  # .../bitbake
        bb_path = self._run_command('git rev-parse --show-toplevel', os.path.dirname(__file__), default=bb_path)
        bb_branch = self._run_command('git rev-parse --abbrev-ref HEAD', bb_path, default="<unknown>")
        bb_rev = self._run_command('git rev-parse HEAD', bb_path, default="<unknown>")
        for remotes in self._run_command('git remote -v', bb_path, default="").split("\n"):
            remote = remotes.split("\t")[1].split(" ")[0]
            if "(fetch)" == remotes.split("\t")[1].split(" ")[1]:
                bb_remote = self._handle_git_remote(remote)
                break
        else:
            bb_remote = self._handle_git_remote(bb_path)

        return (bb_remote, bb_branch, bb_rev, bb_path)

    def _load_bblayers(self, branches=None):
        """Load the BBLAYERS and related collection information"""

        d = self.layerindex.data

        if not branches:
            raise LayerIndexFetchError("No branches specified for _load_bblayers!")

        index = layerindexlib.LayerIndexObj()

        branchId = 0
        index.branches = {}

        layerItemId = 0
        index.layerItems = {}

        layerBranchId = 0
        index.layerBranches = {}

        bblayers = d.getVar('BBLAYERS').split()

        if not bblayers:
            # It's blank!  Nothing to process...
            return index

        collections = d.getVar('BBFILE_COLLECTIONS')
        layerconfs = d.varhistory.get_variable_items_files('BBFILE_COLLECTIONS', d)
        bbfile_collections = {layer: os.path.dirname(os.path.dirname(path)) for layer, path in layerconfs.items()}

        (_, bb_branch, _, _) = self._get_bitbake_info()

        for branch in branches:
            branchId += 1
            index.branches[branchId] = layerindexlib.Branch(index, None)
            index.branches[branchId].define_data(branchId, branch, bb_branch)

        for entry in collections.split():
            layerpath = entry
            if entry in bbfile_collections:
                layerpath = bbfile_collections[entry]

            layername = d.getVar('BBLAYERS_LAYERINDEX_NAME_%s' % entry) or os.path.basename(layerpath)
            layerversion = d.getVar('LAYERVERSION_%s' % entry) or ""
            layerurl = self._handle_git_remote(layerpath)

            layersubdir = ""
            layerrev = "<unknown>"
            layerbranch = "<unknown>"

            if os.path.isdir(layerpath):
                layerbasepath = self._run_command('git rev-parse --show-toplevel', layerpath, default=layerpath)
                if os.path.abspath(layerpath) != os.path.abspath(layerbasepath):
                    layersubdir = os.path.abspath(layerpath)[len(layerbasepath) + 1:]

                layerbranch = self._run_command('git rev-parse --abbrev-ref HEAD', layerpath, default="<unknown>")
                layerrev = self._run_command('git rev-parse HEAD', layerpath, default="<unknown>")

                for remotes in self._run_command('git remote -v', layerpath, default="").split("\n"):
                    if not remotes:
                        layerurl = self._handle_git_remote(layerpath)
                    else:
                        remote = remotes.split("\t")[1].split(" ")[0]
                        if "(fetch)" == remotes.split("\t")[1].split(" ")[1]:
                            layerurl = self._handle_git_remote(remote)
                            break

            layerItemId += 1
            index.layerItems[layerItemId] = layerindexlib.LayerItem(index, None)
            index.layerItems[layerItemId].define_data(layerItemId, layername, description=layerpath, vcs_url=layerurl)

            for branchId in index.branches:
                layerBranchId += 1
                index.layerBranches[layerBranchId] = layerindexlib.LayerBranch(index, None)
                index.layerBranches[layerBranchId].define_data(layerBranchId, entry, layerversion, layerItemId, branchId,
                                               vcs_subdir=layersubdir, vcs_last_rev=layerrev, actual_branch=layerbranch)

        return index


    def load_index(self, url, load):
        """
            Fetches layer information from a build configuration.

            The return value is a dictionary containing API,
            layer, branch, dependency, recipe, machine, distro, information.

            url type should be 'cooker'.
            url path is ignored
        """

        up = urlparse(url)

        if up.scheme != 'cooker':
            raise layerindexlib.plugin.LayerIndexPluginUrlError(self.type, url)

        d = self.layerindex.data

        params = self.layerindex._parse_params(up.params)

        # Only reason to pass a branch is to emulate them...
        if 'branch' in params:
            branches = params['branch'].split(',')
        else:
            branches = ['HEAD']

        logger.debug(1, "Loading cooker data branches %s" % branches)

        index = self._load_bblayers(branches=branches)

        index.config = {}
        index.config['TYPE'] = self.type
        index.config['URL'] = url

        if 'desc' in params:
            index.config['DESCRIPTION'] = unquote(params['desc'])
        else:
            index.config['DESCRIPTION'] = 'local'

        if 'cache' in params:
            index.config['CACHE'] = params['cache']

        index.config['BRANCH'] = branches

        # ("layerDependencies", layerindexlib.LayerDependency)
        layerDependencyId = 0
        if "layerDependencies" in load:
            index.layerDependencies = {}
            for layerBranchId in index.layerBranches:
                branchName = index.layerBranches[layerBranchId].branch.name
                collection = index.layerBranches[layerBranchId].collection

                def add_dependency(layerDependencyId, index, deps, required):
                    try:
                        depDict = bb.utils.explode_dep_versions2(deps)
                    except bb.utils.VersionStringException as vse:
                        bb.fatal('Error parsing LAYERDEPENDS_%s: %s' % (c, str(vse)))

                    for dep, oplist in list(depDict.items()):
                        # We need to search ourselves, so use the _ version...
                        depLayerBranch = index.find_collection(dep, branches=[branchName])
                        if not depLayerBranch:
                            # Missing dependency?!
                            logger.error('Missing dependency %s (%s)' % (dep, branchName))
                            continue

                        # We assume that the oplist matches...
                        layerDependencyId += 1
                        layerDependency = layerindexlib.LayerDependency(index, None)
                        layerDependency.define_data(id=layerDependencyId,
                                        required=required, layerbranch=layerBranchId,
                                        dependency=depLayerBranch.layer_id)

                        logger.debug(1, '%s requires %s' % (layerDependency.layer.name, layerDependency.dependency.name))
                        index.add_element("layerDependencies", [layerDependency])

                    return layerDependencyId

                deps = d.getVar("LAYERDEPENDS_%s" % collection)
                if deps:
                    layerDependencyId = add_dependency(layerDependencyId, index, deps, True)

                deps = d.getVar("LAYERRECOMMENDS_%s" % collection)
                if deps:
                    layerDependencyId = add_dependency(layerDependencyId, index, deps, False)

        # Need to load recipes here (requires cooker access)
        recipeId = 0
        ## TODO: NOT IMPLEMENTED
        # The code following this is an example of what needs to be
        # implemented.  However, it does not work as-is.
        if False and 'recipes' in load:
            index.recipes = {}

            ret = self.ui_module.main(self.server_connection.connection, self.server_connection.events, config_params)

            all_versions = self._run_command('allProviders')

            all_versions_list = defaultdict(list, all_versions)
            for pn in all_versions_list:
                for ((pe, pv, pr), fpath) in all_versions_list[pn]:
                    realfn = bb.cache.virtualfn2realfn(fpath)

                    filepath = os.path.dirname(realfn[0])
                    filename = os.path.basename(realfn[0])

                    # This is all HORRIBLY slow, and likely unnecessary
                    #dscon = self._run_command('parseRecipeFile', fpath, False, [])
                    #connector = myDataStoreConnector(self, dscon.dsindex)
                    #recipe_data = bb.data.init()
                    #recipe_data.setVar('_remote_data', connector)

                    #summary = recipe_data.getVar('SUMMARY')
                    #description = recipe_data.getVar('DESCRIPTION')
                    #section = recipe_data.getVar('SECTION')
                    #license = recipe_data.getVar('LICENSE')
                    #homepage = recipe_data.getVar('HOMEPAGE')
                    #bugtracker = recipe_data.getVar('BUGTRACKER')
                    #provides = recipe_data.getVar('PROVIDES')

                    layer = bb.utils.get_file_layer(realfn[0], self.config_data)

                    depBranchId = collection_layerbranch[layer]

                    recipeId += 1
                    recipe = layerindexlib.Recipe(index, None)
                    recipe.define_data(id=recipeId,
                                   filename=filename, filepath=filepath,
                                   pn=pn, pv=pv,
                                   summary=pn, description=pn, section='?',
                                   license='?', homepage='?', bugtracker='?',
                                   provides='?', bbclassextend='?', inherits='?',
                                   blacklisted='?', layerbranch=depBranchId)

                    index = addElement("recipes", [recipe], index)

        # ("machines", layerindexlib.Machine)
        machineId = 0
        if 'machines' in load:
            index.machines = {}

            for layerBranchId in index.layerBranches:
                # load_bblayers uses the description to cache the actual path...
                machine_path = index.layerBranches[layerBranchId].layer.description
                machine_path = os.path.join(machine_path, 'conf/machine')
                if os.path.isdir(machine_path):
                    for (dirpath, _, filenames) in os.walk(machine_path):
                        # Ignore subdirs...
                        if not dirpath.endswith('conf/machine'):
                            continue
                        for fname in filenames:
                            if fname.endswith('.conf'):
                                machineId += 1
                                machine = layerindexlib.Machine(index, None)
                                machine.define_data(id=machineId, name=fname[:-5],
                                                    description=fname[:-5],
                                                    layerbranch=index.layerBranches[layerBranchId])

                                index.add_element("machines", [machine])

        # ("distros", layerindexlib.Distro)
        distroId = 0
        if 'distros' in load:
            index.distros = {}

            for layerBranchId in index.layerBranches:
                # load_bblayers uses the description to cache the actual path...
                distro_path = index.layerBranches[layerBranchId].layer.description
                distro_path = os.path.join(distro_path, 'conf/distro')
                if os.path.isdir(distro_path):
                    for (dirpath, _, filenames) in os.walk(distro_path):
                        # Ignore subdirs...
                        if not dirpath.endswith('conf/distro'):
                            continue
                        for fname in filenames:
                            if fname.endswith('.conf'):
                                distroId += 1
                                distro = layerindexlib.Distro(index, None)
                                distro.define_data(id=distroId, name=fname[:-5],
                                                    description=fname[:-5],
                                                    layerbranch=index.layerBranches[layerBranchId])

                                index.add_element("distros", [distro])

        return index
