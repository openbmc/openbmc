#
# SPDX-License-Identifier: GPL-2.0-only
#

import layerindexlib

import argparse
import logging
import os
import subprocess

from bblayers.action import ActionPlugin

logger = logging.getLogger('bitbake-layers')


def plugin_init(plugins):
    return LayerIndexPlugin()


class LayerIndexPlugin(ActionPlugin):
    """Subcommands for interacting with the layer index.

    This class inherits ActionPlugin to get do_add_layer.
    """

    def get_fetch_layer(self, fetchdir, url, subdir, fetch_layer):
        layername = self.get_layer_name(url)
        if os.path.splitext(layername)[1] == '.git':
            layername = os.path.splitext(layername)[0]
        repodir = os.path.join(fetchdir, layername)
        layerdir = os.path.join(repodir, subdir)
        if not os.path.exists(repodir):
            if fetch_layer:
                result = subprocess.call(['git', 'clone', url, repodir])
                if result:
                    logger.error("Failed to download %s" % url)
                    return None, None, None
                else:
                    return subdir, layername, layerdir
            else:
                logger.plain("Repository %s needs to be fetched" % url)
                return subdir, layername, layerdir
        elif os.path.exists(layerdir):
            return subdir, layername, layerdir
        else:
            logger.error("%s is not in %s" % (url, subdir))
        return None, None, None

    def do_layerindex_fetch(self, args):
        """Fetches a layer from a layer index along with its dependent layers, and adds them to conf/bblayers.conf.
"""

        def _construct_url(baseurls, branches):
            urls = []
            for baseurl in baseurls:
                if baseurl[-1] != '/':
                    baseurl += '/'

                if not baseurl.startswith('cooker'):
                    baseurl += "api/"

                if branches:
                    baseurl += ";branch=%s" % ','.join(branches)

                urls.append(baseurl)

            return urls


        # Set the default...
        if args.branch:
            branches = [args.branch]
        else:
            branches = (self.tinfoil.config_data.getVar('LAYERSERIES_CORENAMES') or 'master').split()
        logger.debug(1, 'Trying branches: %s' % branches)

        ignore_layers = []
        if args.ignore:
            ignore_layers.extend(args.ignore.split(','))

        # Load the cooker DB
        cookerIndex = layerindexlib.LayerIndex(self.tinfoil.config_data)
        cookerIndex.load_layerindex('cooker://', load='layerDependencies')

        # Fast path, check if we already have what has been requested!
        (dependencies, invalidnames) = cookerIndex.find_dependencies(names=args.layername, ignores=ignore_layers)
        if not args.show_only and not invalidnames:
            logger.plain("You already have the requested layer(s): %s" % args.layername)
            return 0

        # The information to show is already in the cookerIndex
        if invalidnames:
            # General URL to use to access the layer index
            # While there is ONE right now, we're expect users could enter several
            apiurl = self.tinfoil.config_data.getVar('BBLAYERS_LAYERINDEX_URL').split()
            if not apiurl:
                logger.error("Cannot get BBLAYERS_LAYERINDEX_URL")
                return 1

            remoteIndex = layerindexlib.LayerIndex(self.tinfoil.config_data)

            for remoteurl in _construct_url(apiurl, branches):
                logger.plain("Loading %s..." % remoteurl)
                remoteIndex.load_layerindex(remoteurl)

            if remoteIndex.is_empty():
                logger.error("Remote layer index %s is empty for branches %s" % (apiurl, branches))
                return 1

            lIndex = cookerIndex + remoteIndex

            (dependencies, invalidnames) = lIndex.find_dependencies(names=args.layername, ignores=ignore_layers)

            if invalidnames:
                for invaluename in invalidnames:
                    logger.error('Layer "%s" not found in layer index' % invaluename)
                return 1

        logger.plain("%s  %s  %s" % ("Layer".ljust(49), "Git repository (branch)".ljust(54), "Subdirectory"))
        logger.plain('=' * 125)

        for deplayerbranch in dependencies:
            layerBranch = dependencies[deplayerbranch][0]

            # TODO: Determine display behavior
            # This is the local content, uncomment to hide local
            # layers from the display.
            #if layerBranch.index.config['TYPE'] == 'cooker':
            #    continue

            layerDeps = dependencies[deplayerbranch][1:]

            requiredby = []
            recommendedby = []
            for dep in layerDeps:
                if dep.required:
                    requiredby.append(dep.layer.name)
                else:
                    recommendedby.append(dep.layer.name)

            logger.plain('%s %s %s' % (("%s:%s:%s" %
                                  (layerBranch.index.config['DESCRIPTION'],
                                  layerBranch.branch.name,
                                  layerBranch.layer.name)).ljust(50),
                                  ("%s (%s)" % (layerBranch.layer.vcs_url,
                                  layerBranch.actual_branch)).ljust(55),
                                  layerBranch.vcs_subdir
                                               ))
            if requiredby:
                logger.plain('  required by: %s' % ' '.join(requiredby))
            if recommendedby:
                logger.plain('  recommended by: %s' % ' '.join(recommendedby))

        if dependencies:
            fetchdir = self.tinfoil.config_data.getVar('BBLAYERS_FETCH_DIR')
            if not fetchdir:
                logger.error("Cannot get BBLAYERS_FETCH_DIR")
                return 1
            if not os.path.exists(fetchdir):
                os.makedirs(fetchdir)
            addlayers = []

            for deplayerbranch in dependencies:
                layerBranch = dependencies[deplayerbranch][0]

                if layerBranch.index.config['TYPE'] == 'cooker':
                    # Anything loaded via cooker is already local, skip it
                    continue

                subdir, name, layerdir = self.get_fetch_layer(fetchdir,
                                                      layerBranch.layer.vcs_url,
                                                      layerBranch.vcs_subdir,
                                                      not args.show_only)
                if not name:
                    # Error already shown
                    return 1
                addlayers.append((subdir, name, layerdir))
        if not args.show_only:
            localargs = argparse.Namespace()
            localargs.layerdir = []
            localargs.force = args.force
            for subdir, name, layerdir in addlayers:
                if os.path.exists(layerdir):
                    if subdir:
                        logger.plain("Adding layer \"%s\" (%s) to conf/bblayers.conf" % (subdir, layerdir))
                    else:
                        logger.plain("Adding layer \"%s\" (%s) to conf/bblayers.conf" % (name, layerdir))
                    localargs.layerdir.append(layerdir)
                else:
                    break

            if localargs.layerdir:
                self.do_add_layer(localargs)

    def do_layerindex_show_depends(self, args):
        """Find layer dependencies from layer index.
"""
        args.show_only = True
        args.ignore = []
        self.do_layerindex_fetch(args)

    def register_commands(self, sp):
        parser_layerindex_fetch = self.add_command(sp, 'layerindex-fetch', self.do_layerindex_fetch, parserecipes=False)
        parser_layerindex_fetch.add_argument('-n', '--show-only', help='show dependencies and do nothing else', action='store_true')
        parser_layerindex_fetch.add_argument('-b', '--branch', help='branch name to fetch')
        parser_layerindex_fetch.add_argument('-i', '--ignore', help='assume the specified layers do not need to be fetched/added (separate multiple layers with commas, no spaces)', metavar='LAYER')
        parser_layerindex_fetch.add_argument('layername', nargs='+', help='layer to fetch')

        parser_layerindex_show_depends = self.add_command(sp, 'layerindex-show-depends', self.do_layerindex_show_depends, parserecipes=False)
        parser_layerindex_show_depends.add_argument('-b', '--branch', help='branch name to fetch')
        parser_layerindex_show_depends.add_argument('layername', nargs='+', help='layer to query')
