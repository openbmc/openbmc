#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os
import stat
import sys
import shutil

import bb.utils
import bb.process

from bblayers.common import LayerPlugin

logger = logging.getLogger('bitbake-layers')

sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

import oe.buildcfg

def plugin_init(plugins):
    return MakeSetupPlugin()

class MakeSetupPlugin(LayerPlugin):

    def _get_repo_path(self, layer_path):
        repo_path, _ = bb.process.run('git rev-parse --show-toplevel', cwd=layer_path)
        return repo_path.strip()

    def _get_remotes(self, repo_path):
        remotes = {}
        remotes_list,_ = bb.process.run('git remote', cwd=repo_path)
        for r in remotes_list.split():
            uri,_ = bb.process.run('git remote get-url {r}'.format(r=r), cwd=repo_path)
            remotes[r] = {'uri':uri.strip()}
        return remotes

    def _get_describe(self, repo_path):
        try:
            describe,_ = bb.process.run('git describe --tags', cwd=repo_path)
        except bb.process.ExecutionError:
            return ""
        return describe.strip()

    def _is_submodule(self, repo_path):
        # This is slightly brittle: git does not offer a way to tell whether
        # a given repo dir is a submodule checkout, so we need to rely on .git
        # being a file (rather than a dir like it is in standalone checkouts).
        # The file typically contains a gitdir pointer to elsewhere.
        return os.path.isfile(os.path.join(repo_path,".git"))

    def make_repo_config(self, destdir):
        """ This is a helper function for the writer plugins that discovers currently configured layers.
        The writers do not have to use it, but it can save a bit of work and avoid duplicated code, hence it is
        available here. """
        repos = {}
        layers = oe.buildcfg.get_layer_revisions(self.tinfoil.config_data)
        try:
            destdir_repo = self._get_repo_path(destdir)
        except bb.process.ExecutionError:
            destdir_repo = None

        for (l_path, l_name, l_branch, l_rev, l_ismodified) in layers:
            if l_name == 'workspace':
                continue
            if l_ismodified:
                logger.error("Layer {name} in {path} has uncommitted modifications or is not in a git repository.".format(name=l_name,path=l_path))
                return
            repo_path = self._get_repo_path(l_path)

            if self._is_submodule(repo_path):
                continue
            if repo_path not in repos.keys():
                repos[repo_path] = {'path':os.path.basename(repo_path),'git-remote':{'rev':l_rev, 'branch':l_branch, 'remotes':self._get_remotes(repo_path), 'describe':self._get_describe(repo_path)}}
                if repo_path == destdir_repo:
                    repos[repo_path]['contains_this_file'] = True
                if not repos[repo_path]['git-remote']['remotes'] and not repos[repo_path]['contains_this_file']:
                    logger.error("Layer repository in {path} does not have any remotes configured. Please add at least one with 'git remote add'.".format(path=repo_path))
                    return

        top_path = os.path.commonpath([os.path.dirname(r) for r in repos.keys()])

        repos_nopaths = {}
        for r in repos.keys():
            r_nopath = os.path.basename(r)
            repos_nopaths[r_nopath] = repos[r]
            r_relpath = os.path.relpath(r, top_path)
            repos_nopaths[r_nopath]['path'] = r_relpath
        return repos_nopaths

    def do_make_setup(self, args):
        """ Writes out a configuration file and/or a script that replicate the directory structure and revisions of the layers in a current build. """
        for p in self.plugins:
            if str(p) == args.writer:
                p.do_write(self, args)

    def register_commands(self, sp):
        parser_setup_layers = self.add_command(sp, 'create-layers-setup', self.do_make_setup, parserecipes=False)
        parser_setup_layers.add_argument('destdir',
            help='Directory where to write the output\n(if it is inside one of the layers, the layer becomes a bootstrap repository and thus will be excluded from fetching).')
        parser_setup_layers.add_argument('--output-prefix', '-o',
            help='File name prefix for the output files, if the default (setup-layers) is undesirable.')

        self.plugins = []

        for path in (self.tinfoil.config_data.getVar('BBPATH').split(':')):
            pluginpath = os.path.join(path, 'lib', 'bblayers', 'setupwriters')
            bb.utils.load_plugins(logger, self.plugins, pluginpath)

        parser_setup_layers.add_argument('--writer', '-w', choices=[str(p) for p in self.plugins], help="Choose the output format (defaults to oe-setup-layers).\n\nCurrently supported options are:\noe-setup-layers - a self-contained python script and a json config for it.\n\n", default="oe-setup-layers")

        for plugin in self.plugins:
            if hasattr(plugin, 'register_arguments'):
                plugin.register_arguments(parser_setup_layers)
