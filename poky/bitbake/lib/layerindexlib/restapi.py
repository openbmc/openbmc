# Copyright (C) 2016-2018 Wind River Systems, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import json
import os

from urllib.parse import unquote
from urllib.parse import urlparse

import bb

import layerindexlib
import layerindexlib.plugin

logger = logging.getLogger('BitBake.layerindexlib.restapi')

def plugin_init(plugins):
    return RestApiPlugin()

class RestApiPlugin(layerindexlib.plugin.IndexPlugin):
    def __init__(self):
        self.type = "restapi"

    def load_index(self, url, load):
        """
            Fetches layer information from a local or remote layer index.

            The return value is a LayerIndexObj.

            url is the url to the rest api of the layer index, such as:
            https://layers.openembedded.org/layerindex/api/

            Or a local file...
        """

        up = urlparse(url)

        if up.scheme == 'file':
            return self.load_index_file(up, url, load)

        if up.scheme == 'http' or up.scheme == 'https':
            return self.load_index_web(up, url, load)

        raise layerindexlib.plugin.LayerIndexPluginUrlError(self.type, url)


    def load_index_file(self, up, url, load):
        """
            Fetches layer information from a local file or directory.

            The return value is a LayerIndexObj.

            ud is the parsed url to the local file or directory.
        """
        if not os.path.exists(up.path):
            raise FileNotFoundError(up.path)

        index = layerindexlib.LayerIndexObj()

        index.config = {}
        index.config['TYPE'] = self.type
        index.config['URL'] = url

        params = self.layerindex._parse_params(up.params)

        if 'desc' in params:
            index.config['DESCRIPTION'] = unquote(params['desc'])
        else:
            index.config['DESCRIPTION'] = up.path

        if 'cache' in params:
            index.config['CACHE'] = params['cache']

        if 'branch' in params:
            branches = params['branch'].split(',')
            index.config['BRANCH'] = branches
        else:
            branches = ['*']


        def load_cache(path, index, branches=[]):
            logger.debug('Loading json file %s' % path)
            with open(path, 'rt', encoding='utf-8') as f:
                pindex = json.load(f)

            # Filter the branches on loaded files...
            newpBranch = []
            for branch in branches:
                if branch != '*':
                    if 'branches' in pindex:
                        for br in pindex['branches']:
                            if br['name'] == branch:
                                newpBranch.append(br)
                else:
                    if 'branches' in pindex:
                        for br in pindex['branches']:
                            newpBranch.append(br)

            if newpBranch:
                index.add_raw_element('branches', layerindexlib.Branch, newpBranch)
            else:
                logger.debug('No matching branches (%s) in index file(s)' % branches)
                # No matching branches.. return nothing...
                return

            for (lName, lType) in [("layerItems", layerindexlib.LayerItem),
                                   ("layerBranches", layerindexlib.LayerBranch),
                                   ("layerDependencies", layerindexlib.LayerDependency),
                                   ("recipes", layerindexlib.Recipe),
                                   ("machines", layerindexlib.Machine),
                                   ("distros", layerindexlib.Distro)]:
                if lName in pindex:
                    index.add_raw_element(lName, lType, pindex[lName])


        if not os.path.isdir(up.path):
            load_cache(up.path, index, branches)
            return index

        logger.debug('Loading from dir %s...' % (up.path))
        for (dirpath, _, filenames) in os.walk(up.path):
            for filename in filenames:
                if not filename.endswith('.json'):
                    continue
                fpath = os.path.join(dirpath, filename)
                load_cache(fpath, index, branches)

        return index


    def load_index_web(self, up, url, load):
        """
            Fetches layer information from a remote layer index.

            The return value is a LayerIndexObj.

            ud is the parsed url to the rest api of the layer index, such as:
            https://layers.openembedded.org/layerindex/api/
        """

        def _get_json_response(apiurl=None, username=None, password=None, retry=True):
            assert apiurl is not None

            logger.debug("fetching %s" % apiurl)

            up = urlparse(apiurl)

            username=up.username
            password=up.password

            # Strip username/password and params
            if up.port:
                up_stripped = up._replace(params="", netloc="%s:%s" % (up.hostname, up.port))
            else:
                up_stripped = up._replace(params="", netloc=up.hostname)

            res = self.layerindex._fetch_url(up_stripped.geturl(), username=username, password=password)

            try:
                parsed = json.loads(res.read().decode('utf-8'))
            except ConnectionResetError:
                if retry:
                    logger.debug("%s: Connection reset by peer.  Retrying..." % url)
                    parsed = _get_json_response(apiurl=up_stripped.geturl(), username=username, password=password, retry=False)
                    logger.debug("%s: retry successful.")
                else:
                    raise layerindexlib.LayerIndexFetchError('%s: Connection reset by peer.  Is there a firewall blocking your connection?' % apiurl)

            return parsed

        index = layerindexlib.LayerIndexObj()

        index.config = {}
        index.config['TYPE'] = self.type
        index.config['URL'] = url

        params = self.layerindex._parse_params(up.params)

        if 'desc' in params:
            index.config['DESCRIPTION'] = unquote(params['desc'])
        else:
            index.config['DESCRIPTION'] = up.hostname

        if 'cache' in params:
            index.config['CACHE'] = params['cache']

        if 'branch' in params:
            branches = params['branch'].split(',')
            index.config['BRANCH'] = branches
        else:
            branches = ['*']

        try:
            index.apilinks = _get_json_response(apiurl=url, username=up.username, password=up.password)
        except Exception as e:
            raise layerindexlib.LayerIndexFetchError(url, e)

        # Local raw index set...
        pindex = {}

        # Load all the requested branches at the same time time,
        # a special branch of '*' means load all branches
        filter = ""
        if "*" not in branches:
            filter = "?filter=name:%s" % "OR".join(branches)

        logger.debug("Loading %s from %s" % (branches, index.apilinks['branches']))

        # The link won't include username/password, so pull it from the original url
        pindex['branches'] = _get_json_response(index.apilinks['branches'] + filter,
                                                    username=up.username, password=up.password)
        if not pindex['branches']:
            logger.debug("No valid branches (%s) found at url %s." % (branch, url))
            return index
        index.add_raw_element("branches", layerindexlib.Branch, pindex['branches'])

        # Load all of the layerItems (these can not be easily filtered)
        logger.debug("Loading %s from %s" % ('layerItems', index.apilinks['layerItems']))


        # The link won't include username/password, so pull it from the original url
        pindex['layerItems'] = _get_json_response(index.apilinks['layerItems'],
                                                  username=up.username, password=up.password)
        if not pindex['layerItems']:
            logger.debug("No layers were found at url %s." % (url))
            return index
        index.add_raw_element("layerItems", layerindexlib.LayerItem, pindex['layerItems'])


	# From this point on load the contents for each branch.  Otherwise we
	# could run into a timeout.
        for branch in index.branches:
            filter = "?filter=branch__name:%s" % index.branches[branch].name

            logger.debug("Loading %s from %s" % ('layerBranches', index.apilinks['layerBranches']))

            # The link won't include username/password, so pull it from the original url
            pindex['layerBranches'] = _get_json_response(index.apilinks['layerBranches'] + filter,
                                                  username=up.username, password=up.password)
            if not pindex['layerBranches']:
                logger.debug("No valid layer branches (%s) found at url %s." % (branches or "*", url))
                return index
            index.add_raw_element("layerBranches", layerindexlib.LayerBranch, pindex['layerBranches'])


            # Load the rest, they all have a similar format
            # Note: the layer index has a few more items, we can add them if necessary
            # in the future.
            filter = "?filter=layerbranch__branch__name:%s" % index.branches[branch].name
            for (lName, lType) in [("layerDependencies", layerindexlib.LayerDependency),
                                   ("recipes", layerindexlib.Recipe),
                                   ("machines", layerindexlib.Machine),
                                   ("distros", layerindexlib.Distro)]:
                if lName not in load:
                    continue
                logger.debug("Loading %s from %s" % (lName, index.apilinks[lName]))

                # The link won't include username/password, so pull it from the original url
                pindex[lName] = _get_json_response(index.apilinks[lName] + filter,
                                            username=up.username, password=up.password)
                index.add_raw_element(lName, lType, pindex[lName])

        return index

    def store_index(self, url, index):
        """
            Store layer information into a local file/dir.

            The return value is a dictionary containing API,
            layer, branch, dependency, recipe, machine, distro, information.

            ud is a parsed url to a directory or file.  If the path is a
            directory, we will split the files into one file per layer.
            If the path is to a file (exists or not) the entire DB will be
            dumped into that one file.
        """

        up = urlparse(url)

        if up.scheme != 'file':
            raise layerindexlib.plugin.LayerIndexPluginUrlError(self.type, url)

        logger.debug("Storing to %s..." % up.path)

        try:
            layerbranches = index.layerBranches
        except KeyError:
            logger.error('No layerBranches to write.')
            return


        def filter_item(layerbranchid, objects):
            filtered = []
            for obj in getattr(index, objects, None):
                try:
                    if getattr(index, objects)[obj].layerbranch_id == layerbranchid:
                       filtered.append(getattr(index, objects)[obj]._data)
                except AttributeError:
                    logger.debug('No obj.layerbranch_id: %s' % objects)
                    # No simple filter method, just include it...
                    try:
                        filtered.append(getattr(index, objects)[obj]._data)
                    except AttributeError:
                        logger.debug('No obj._data: %s %s' % (objects, type(obj)))
                        filtered.append(obj)
            return filtered


        # Write out to a single file.
        # Filter out unnecessary items, then sort as we write for determinism
        if not os.path.isdir(up.path):
            pindex = {}

            pindex['branches'] = []
            pindex['layerItems'] = []
            pindex['layerBranches'] = []

            for layerbranchid in layerbranches:
                if layerbranches[layerbranchid].branch._data not in pindex['branches']:
                    pindex['branches'].append(layerbranches[layerbranchid].branch._data)

                if layerbranches[layerbranchid].layer._data not in pindex['layerItems']:
                    pindex['layerItems'].append(layerbranches[layerbranchid].layer._data)

                if layerbranches[layerbranchid]._data not in pindex['layerBranches']:
                    pindex['layerBranches'].append(layerbranches[layerbranchid]._data)

                for entry in index._index:
                    # Skip local items, apilinks and items already processed
                    if entry in index.config['local'] or \
                       entry == 'apilinks' or \
                       entry == 'branches' or \
                       entry == 'layerBranches' or \
                       entry == 'layerItems':
                        continue
                    if entry not in pindex:
                        pindex[entry] = []
                    pindex[entry].extend(filter_item(layerbranchid, entry))

            bb.debug(1, 'Writing index to %s' % up.path)
            with open(up.path, 'wt') as f:
                json.dump(layerindexlib.sort_entry(pindex), f, indent=4)
            return


        # Write out to a directory one file per layerBranch
        # Prepare all layer related items, to create a minimal file.
        # We have to sort the entries as we write so they are deterministic
        for layerbranchid in layerbranches:
            pindex = {}

            for entry in index._index:
                # Skip local items, apilinks and items already processed
                if entry in index.config['local'] or \
                   entry == 'apilinks' or \
                   entry == 'branches' or \
                   entry == 'layerBranches' or \
                   entry == 'layerItems':
                    continue
                pindex[entry] = filter_item(layerbranchid, entry)

            # Add the layer we're processing as the first one...
            pindex['branches'] = [layerbranches[layerbranchid].branch._data]
            pindex['layerItems'] = [layerbranches[layerbranchid].layer._data]
            pindex['layerBranches'] = [layerbranches[layerbranchid]._data]

            # We also need to include the layerbranch for any dependencies...
            for layerdep in pindex['layerDependencies']:
                layerdependency = layerindexlib.LayerDependency(index, layerdep)

                layeritem = layerdependency.dependency
                layerbranch = layerdependency.dependency_layerBranch

                # We need to avoid duplicates...
                if layeritem._data not in pindex['layerItems']:
                    pindex['layerItems'].append(layeritem._data)

                if layerbranch._data not in pindex['layerBranches']:
                    pindex['layerBranches'].append(layerbranch._data)

            # apply mirroring adjustments here....

            fname = index.config['DESCRIPTION'] + '__' + pindex['branches'][0]['name'] + '__' + pindex['layerItems'][0]['name']
            fname = fname.translate(str.maketrans('/ ', '__'))
            fpath = os.path.join(up.path, fname)

            bb.debug(1, 'Writing index to %s' % fpath + '.json')
            with open(fpath + '.json', 'wt') as f:
                json.dump(layerindexlib.sort_entry(pindex), f, indent=4)
