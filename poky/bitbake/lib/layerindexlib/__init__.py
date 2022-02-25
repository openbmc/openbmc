# Copyright (C) 2016-2018 Wind River Systems, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import datetime

import logging
import os

from collections import OrderedDict
from layerindexlib.plugin import LayerIndexPluginUrlError

logger = logging.getLogger('BitBake.layerindexlib')

# Exceptions

class LayerIndexException(Exception):
    '''LayerIndex Generic Exception'''
    def __init__(self, message):
         self.msg = message
         Exception.__init__(self, message)

    def __str__(self):
         return self.msg

class LayerIndexUrlError(LayerIndexException):
    '''Exception raised when unable to access a URL for some reason'''
    def __init__(self, url, message=""):
        if message:
            msg = "Unable to access layerindex url %s: %s" % (url, message)
        else:
            msg = "Unable to access layerindex url %s" % url
        self.url = url
        LayerIndexException.__init__(self, msg)

class LayerIndexFetchError(LayerIndexException):
    '''General layerindex fetcher exception when something fails'''
    def __init__(self, url, message=""):
        if message:
            msg = "Unable to fetch layerindex url %s: %s" % (url, message)
        else:
            msg = "Unable to fetch layerindex url %s" % url
        self.url = url
        LayerIndexException.__init__(self, msg)


# Interface to the overall layerindex system
# the layer may contain one or more individual indexes
class LayerIndex():
    def __init__(self, d):
        if not d:
            raise LayerIndexException("Must be initialized with bb.data.")

        self.data = d

        # List of LayerIndexObj
        self.indexes = []

        self.plugins = []

        import bb.utils
        bb.utils.load_plugins(logger, self.plugins, os.path.dirname(__file__))
        for plugin in self.plugins:
            if hasattr(plugin, 'init'):
                plugin.init(self)

    def __add__(self, other):
        newIndex = LayerIndex(self.data)

        if self.__class__ != newIndex.__class__ or \
           other.__class__ != newIndex.__class__:
            raise TypeError("Can not add different types.")

        for indexEnt in self.indexes:
            newIndex.indexes.append(indexEnt)

        for indexEnt in other.indexes:
            newIndex.indexes.append(indexEnt)

        return newIndex

    def _parse_params(self, params):
        '''Take a parameter list, return a dictionary of parameters.

           Expected to be called from the data of urllib.parse.urlparse(url).params

           If there are two conflicting parameters, last in wins...
        '''

        param_dict = {}
        for param in params.split(';'):
           if not param:
               continue
           item = param.split('=', 1)
           logger.debug(item)
           param_dict[item[0]] = item[1]

        return param_dict

    def _fetch_url(self, url, username=None, password=None, debuglevel=0):
        '''Fetch data from a specific URL.

           Fetch something from a specific URL.  This is specifically designed to
           fetch data from a layerindex-web instance, but may be useful for other
           raw fetch actions.

           It is not designed to be used to fetch recipe sources or similar.  the
           regular fetcher class should used for that.

           It is the responsibility of the caller to check BB_NO_NETWORK and related
           BB_ALLOWED_NETWORKS.
        '''

        if not url:
            raise LayerIndexUrlError(url, "empty url")

        import urllib
        from urllib.request import urlopen, Request
        from urllib.parse import urlparse

        up = urlparse(url)

        if username:
            logger.debug("Configuring authentication for %s..." % url)
            password_mgr = urllib.request.HTTPPasswordMgrWithDefaultRealm()
            password_mgr.add_password(None, "%s://%s" % (up.scheme, up.netloc), username, password)
            handler = urllib.request.HTTPBasicAuthHandler(password_mgr)
            opener = urllib.request.build_opener(handler, urllib.request.HTTPSHandler(debuglevel=debuglevel))
        else:
            opener = urllib.request.build_opener(urllib.request.HTTPSHandler(debuglevel=debuglevel))

        urllib.request.install_opener(opener)

        logger.debug("Fetching %s (%s)..." % (url, ["without authentication", "with authentication"][bool(username)]))

        try:
            res = urlopen(Request(url, headers={'User-Agent': 'Mozilla/5.0 (bitbake/lib/layerindex)'}, unverifiable=True))
        except urllib.error.HTTPError as e:
            logger.debug("HTTP Error: %s: %s" % (e.code, e.reason))
            logger.debug(" Requested: %s" % (url))
            logger.debug(" Actual:    %s" % (e.geturl()))

            if e.code == 404:
                logger.debug("Request not found.")
                raise LayerIndexFetchError(url, e)
            else:
                logger.debug("Headers:\n%s" % (e.headers))
                raise LayerIndexFetchError(url, e)
        except OSError as e:
            error = 0
            reason = ""

            # Process base OSError first...
            if hasattr(e, 'errno'):
                error = e.errno
                reason = e.strerror

            # Process gaierror (socket error) subclass if available.
            if hasattr(e, 'reason') and hasattr(e.reason, 'errno') and hasattr(e.reason, 'strerror'):
                error = e.reason.errno
                reason = e.reason.strerror
                if error == -2:
                    raise LayerIndexFetchError(url, "%s: %s" % (e, reason))

            if error and error != 0:
                raise LayerIndexFetchError(url, "Unexpected exception: [Error %s] %s" % (error, reason))
            else:
                raise LayerIndexFetchError(url, "Unable to fetch OSError exception: %s" % e)

        finally:
            logger.debug("...fetching %s (%s), done." % (url, ["without authentication", "with authentication"][bool(username)]))

        return res


    def load_layerindex(self, indexURI, load=['layerDependencies', 'recipes', 'machines', 'distros'], reload=False):
        '''Load the layerindex.

           indexURI - An index to load.  (Use multiple calls to load multiple indexes)
           
           reload - If reload is True, then any previously loaded indexes will be forgotten.
           
           load - List of elements to load.  Default loads all items.
                  Note: plugs may ignore this.

The format of the indexURI:

  <url>;branch=<branch>;cache=<cache>;desc=<description>

  Note: the 'branch' parameter if set can select multiple branches by using
  comma, such as 'branch=master,morty,pyro'.  However, many operations only look
  at the -first- branch specified!

  The cache value may be undefined, in this case a network failure will
  result in an error, otherwise the system will look for a file of the cache
  name and load that instead.

  For example:

  https://layers.openembedded.org/layerindex/api/;branch=master;desc=OpenEmbedded%20Layer%20Index
  cooker://
'''
        if reload:
            self.indexes = []

        logger.debug('Loading: %s' % indexURI)

        if not self.plugins:
            raise LayerIndexException("No LayerIndex Plugins available")

        for plugin in self.plugins:
            # Check if the plugin was initialized
            logger.debug('Trying %s' % plugin.__class__)
            if not hasattr(plugin, 'type') or not plugin.type:
                continue
            try:
                # TODO: Implement 'cache', for when the network is not available
                indexEnt = plugin.load_index(indexURI, load)
                break
            except LayerIndexPluginUrlError as e:
                logger.debug("%s doesn't support %s" % (plugin.type, e.url))
            except NotImplementedError:
                pass
        else:
            logger.debug("No plugins support %s" % indexURI)
            raise LayerIndexException("No plugins support %s" % indexURI)

        # Mark CONFIG data as something we've added...
        indexEnt.config['local'] = []
        indexEnt.config['local'].append('config')

        # No longer permit changes..
        indexEnt.lockData()

        self.indexes.append(indexEnt)

    def store_layerindex(self, indexURI, index=None):
        '''Store one layerindex

Typically this will be used to create a local cache file of a remote index.

  file://<path>;branch=<branch>

We can write out in either the restapi or django formats.  The split option
will write out the individual elements split by layer and related components.
'''
        if not index:
            logger.warning('No index to write, nothing to do.')
            return

        if not self.plugins:
            raise LayerIndexException("No LayerIndex Plugins available")

        for plugin in self.plugins:
            # Check if the plugin was initialized
            logger.debug('Trying %s' % plugin.__class__)
            if not hasattr(plugin, 'type') or not plugin.type:
                continue
            try:
                plugin.store_index(indexURI, index)
                break
            except LayerIndexPluginUrlError as e:
                logger.debug("%s doesn't support %s" % (plugin.type, e.url))
            except NotImplementedError:
                logger.debug("Store not implemented in %s" % plugin.type)
                pass
        else:
            logger.debug("No plugins support %s" % indexURI)
            raise LayerIndexException("No plugins support %s" % indexURI)


    def is_empty(self):
        '''Return True or False if the index has any usable data.

We check the indexes entries to see if they have a branch set, as well as
layerBranches set.  If not, they are effectively blank.'''

        found = False
        for index in self.indexes:
            if index.__bool__():
                found = True
                break
        return not found


    def find_vcs_url(self, vcs_url, branch=None):
        '''Return the first layerBranch with the given vcs_url

           If a branch has not been specified, we will iterate over the branches in
           the default configuration until the first vcs_url/branch match.'''

        for index in self.indexes:
            logger.debug(' searching %s' % index.config['DESCRIPTION'])
            layerBranch = index.find_vcs_url(vcs_url, [branch])
            if layerBranch:
                return layerBranch
        return None

    def find_collection(self, collection, version=None, branch=None):
        '''Return the first layerBranch with the given collection name

           If a branch has not been specified, we will iterate over the branches in
           the default configuration until the first collection/branch match.'''

        logger.debug('find_collection: %s (%s) %s' % (collection, version, branch))

        if branch:
            branches = [branch]
        else:
            branches = None

        for index in self.indexes:
            logger.debug(' searching %s' % index.config['DESCRIPTION'])
            layerBranch = index.find_collection(collection, version, branches)
            if layerBranch:
                return layerBranch
        else:
            logger.debug('Collection %s (%s) not found for branch (%s)' % (collection, version, branch))
        return None

    def find_layerbranch(self, name, branch=None):
        '''Return the layerBranch item for a given name and branch

           If a branch has not been specified, we will iterate over the branches in
           the default configuration until the first name/branch match.'''

        if branch:
            branches = [branch]
        else:
            branches = None

        for index in self.indexes:
            layerBranch = index.find_layerbranch(name, branches)
            if layerBranch:
                return layerBranch
        return None

    def find_dependencies(self, names=None, layerbranches=None, ignores=None):
        '''Return a tuple of all dependencies and valid items for the list of (layer) names

        The dependency scanning happens depth-first.  The returned
        dependencies should be in the best order to define bblayers.

          names - list of layer names (searching layerItems)
          branches - when specified (with names) only this list of branches are evaluated

          layerbranches - list of layerbranches to resolve dependencies

          ignores - list of layer names to ignore

        return: (dependencies, invalid)

          dependencies[LayerItem.name] = [ LayerBranch, LayerDependency1, LayerDependency2, ... ]
          invalid = [ LayerItem.name1, LayerItem.name2, ... ]
        '''

        invalid = []

        # Convert name/branch to layerbranches
        if layerbranches is None:
            layerbranches = []

        for name in names:
            if ignores and name in ignores:
                continue

            for index in self.indexes:
                layerbranch = index.find_layerbranch(name)
                if not layerbranch:
                    # Not in this index, hopefully it's in another...
                    continue
                layerbranches.append(layerbranch)
                break
            else:
                invalid.append(name)


        def _resolve_dependencies(layerbranches, ignores, dependencies, invalid, processed=None):
            for layerbranch in layerbranches:
                if ignores and layerbranch.layer.name in ignores:
                    continue

                # Get a list of dependencies and then recursively process them
                for layerdependency in layerbranch.index.layerDependencies_layerBranchId[layerbranch.id]:
                    deplayerbranch = layerdependency.dependency_layerBranch

                    if ignores and deplayerbranch.layer.name in ignores:
                        continue

                    # Since this is depth first, we need to know what we're currently processing
                    # in order to avoid infinite recursion on a loop.
                    if processed and deplayerbranch.layer.name in processed:
                        # We have found a recursion...
                        logger.warning('Circular layer dependency found: %s -> %s' % (processed, deplayerbranch.layer.name))
                        continue

                    # This little block is why we can't re-use the LayerIndexObj version,
                    # we must be able to satisfy each dependencies across layer indexes and
                    # use the layer index order for priority.  (r stands for replacement below)

                    # If this is the primary index, we can fast path and skip this
                    if deplayerbranch.index != self.indexes[0]:
                        # Is there an entry in a prior index for this collection/version?
                        rdeplayerbranch = self.find_collection(
                                              collection=deplayerbranch.collection,
                                              version=deplayerbranch.version
                                          )
                        if rdeplayerbranch != deplayerbranch:
                                logger.debug('Replaced %s:%s:%s with %s:%s:%s' % \
                                      (deplayerbranch.index.config['DESCRIPTION'],
                                       deplayerbranch.branch.name,
                                       deplayerbranch.layer.name,
                                       rdeplayerbranch.index.config['DESCRIPTION'],
                                       rdeplayerbranch.branch.name,
                                       rdeplayerbranch.layer.name))
                                deplayerbranch = rdeplayerbranch

                    # New dependency, we need to resolve it now... depth-first
                    if deplayerbranch.layer.name not in dependencies:
                        # Avoid recursion on this branch.
                        # We copy so we don't end up polluting the depth-first branch with other
                        # branches.  Duplication between individual branches IS expected and
                        # handled by 'dependencies' processing.
                        if not processed:
                            local_processed = []
                        else:
                            local_processed = processed.copy()
                        local_processed.append(deplayerbranch.layer.name)

                        (dependencies, invalid) = _resolve_dependencies([deplayerbranch], ignores, dependencies, invalid, local_processed)

                    if deplayerbranch.layer.name not in dependencies:
                        dependencies[deplayerbranch.layer.name] = [deplayerbranch, layerdependency]
                    else:
                        if layerdependency not in dependencies[deplayerbranch.layer.name]:
                            dependencies[deplayerbranch.layer.name].append(layerdependency)

            return (dependencies, invalid)

        # OK, resolve this one...
        dependencies = OrderedDict()
        (dependencies, invalid) = _resolve_dependencies(layerbranches, ignores, dependencies, invalid)

        for layerbranch in layerbranches:
            if layerbranch.layer.name not in dependencies:
                dependencies[layerbranch.layer.name] = [layerbranch]

        return (dependencies, invalid)


    def list_obj(self, object):
        '''Print via the plain logger object information

This function is used to implement debugging and provide the user info.
'''
        for lix in self.indexes:
            if not hasattr(lix, object):
                continue

            logger.plain ('')
            logger.plain ('Index: %s' % lix.config['DESCRIPTION'])

            output = []

            if object == 'branches':
                logger.plain ('%s %s %s' % ('{:26}'.format('branch'), '{:34}'.format('description'), '{:22}'.format('bitbake branch')))
                logger.plain ('{:-^80}'.format(""))
                for branchid in lix.branches:
                    output.append('%s %s %s' % (
                                  '{:26}'.format(lix.branches[branchid].name),
                                  '{:34}'.format(lix.branches[branchid].short_description),
                                  '{:22}'.format(lix.branches[branchid].bitbake_branch)
                                 ))
                for line in sorted(output):
                    logger.plain (line)

                continue

            if object == 'layerItems':
                logger.plain ('%s %s' % ('{:26}'.format('layer'), '{:34}'.format('description')))
                logger.plain ('{:-^80}'.format(""))
                for layerid in lix.layerItems:
                    output.append('%s %s' % (
                                  '{:26}'.format(lix.layerItems[layerid].name),
                                  '{:34}'.format(lix.layerItems[layerid].summary)
                                 ))
                for line in sorted(output):
                    logger.plain (line)

                continue

            if object == 'layerBranches':
                logger.plain ('%s %s %s' % ('{:26}'.format('layer'), '{:34}'.format('description'), '{:19}'.format('collection:version')))
                logger.plain ('{:-^80}'.format(""))
                for layerbranchid in lix.layerBranches:
                    output.append('%s %s %s' % (
                                  '{:26}'.format(lix.layerBranches[layerbranchid].layer.name),
                                  '{:34}'.format(lix.layerBranches[layerbranchid].layer.summary),
                                  '{:19}'.format("%s:%s" %
                                                          (lix.layerBranches[layerbranchid].collection,
                                                           lix.layerBranches[layerbranchid].version)
                                                )
                                 ))
                for line in sorted(output):
                    logger.plain (line)

                continue

            if object == 'layerDependencies':
                logger.plain ('%s %s %s %s' % ('{:19}'.format('branch'), '{:26}'.format('layer'), '{:11}'.format('dependency'), '{:26}'.format('layer')))
                logger.plain ('{:-^80}'.format(""))
                for layerDependency in lix.layerDependencies:
                    if not lix.layerDependencies[layerDependency].dependency_layerBranch:
                        continue

                    output.append('%s %s %s %s' % (
                                  '{:19}'.format(lix.layerDependencies[layerDependency].layerbranch.branch.name),
                                  '{:26}'.format(lix.layerDependencies[layerDependency].layerbranch.layer.name),
                                  '{:11}'.format('requires' if lix.layerDependencies[layerDependency].required else 'recommends'),
                                  '{:26}'.format(lix.layerDependencies[layerDependency].dependency_layerBranch.layer.name)
                                 ))
                for line in sorted(output):
                    logger.plain (line)

                continue

            if object == 'recipes':
                logger.plain ('%s %s %s' % ('{:20}'.format('recipe'), '{:10}'.format('version'), 'layer'))
                logger.plain ('{:-^80}'.format(""))
                output = []
                for recipe in lix.recipes:
                    output.append('%s %s %s' % (
                                  '{:30}'.format(lix.recipes[recipe].pn),
                                  '{:30}'.format(lix.recipes[recipe].pv),
                                  lix.recipes[recipe].layer.name
                                 ))
                for line in sorted(output):
                    logger.plain (line)

                continue

            if object == 'machines':
                logger.plain ('%s %s %s' % ('{:24}'.format('machine'), '{:34}'.format('description'), '{:19}'.format('layer')))
                logger.plain ('{:-^80}'.format(""))
                for machine in lix.machines:
                    output.append('%s %s %s' % (
                                  '{:24}'.format(lix.machines[machine].name),
                                  '{:34}'.format(lix.machines[machine].description)[:34],
                                  '{:19}'.format(lix.machines[machine].layerbranch.layer.name)
                                 ))
                for line in sorted(output):
                    logger.plain (line)

                continue

            if object == 'distros':
                logger.plain ('%s %s %s' % ('{:24}'.format('distro'), '{:34}'.format('description'), '{:19}'.format('layer')))
                logger.plain ('{:-^80}'.format(""))
                for distro in lix.distros:
                    output.append('%s %s %s' % (
                                  '{:24}'.format(lix.distros[distro].name),
                                  '{:34}'.format(lix.distros[distro].description)[:34],
                                  '{:19}'.format(lix.distros[distro].layerbranch.layer.name)
                                 ))
                for line in sorted(output):
                    logger.plain (line)

                continue

        logger.plain ('')


# This class holds a single layer index instance
# The LayerIndexObj is made up of dictionary of elements, such as:
#   index['config'] - configuration data for this index
#   index['branches'] - dictionary of Branch objects, by id number
#   index['layerItems'] - dictionary of layerItem objects, by id number
#   ...etc...  (See: https://layers.openembedded.org/layerindex/api/)
#
# The class needs to manage the 'index' entries and allow easily adding
# of new items, as well as simply loading of the items.
class LayerIndexObj():
    def __init__(self):
        super().__setattr__('_index', {})
        super().__setattr__('_lock', False)

    def __bool__(self):
        '''False if the index is effectively empty

           We check the index to see if it has a branch set, as well as
           layerbranches set.  If not, it is effectively blank.'''

        if not bool(self._index):
            return False

        try:
            if self.branches and self.layerBranches:
                return True
        except AttributeError:
            pass

        return False

    def __getattr__(self, name):
        if name.startswith('_'):
            return super().__getattribute__(name)

        if name not in self._index:
            raise AttributeError('%s not in index datastore' % name)

        return self._index[name]

    def __setattr__(self, name, value):
        if self.isLocked():
            raise TypeError("Can not set attribute '%s': index is locked" % name)

        if name.startswith('_'):
            super().__setattr__(name, value)
            return

        self._index[name] = value

    def __delattr__(self, name):
        if self.isLocked():
            raise TypeError("Can not delete attribute '%s': index is locked" % name)

        if name.startswith('_'):
            super().__delattr__(name)

        self._index.pop(name)

    def lockData(self):
        '''Lock data object (make it readonly)'''
        super().__setattr__("_lock", True)

    def unlockData(self):
        '''unlock data object (make it readonly)'''
        super().__setattr__("_lock", False)

        # When the data is unlocked, we have to clear the caches, as
        # modification is allowed!
        del(self._layerBranches_layerId_branchId)
        del(self._layerDependencies_layerBranchId)
        del(self._layerBranches_vcsUrl)

    def isLocked(self):
        '''Is this object locked (readonly)?'''
        return self._lock

    def add_element(self, indexname, objs):
        '''Add a layer index object to index.<indexname>'''
        if indexname not in self._index:
            self._index[indexname] = {}

        for obj in objs:
            if obj.id in self._index[indexname]:
                if self._index[indexname][obj.id] == obj:
                    continue
                raise LayerIndexException('Conflict adding object %s(%s) to index' % (indexname, obj.id))
            self._index[indexname][obj.id] = obj

    def add_raw_element(self, indexname, objtype, rawobjs):
        '''Convert a raw layer index data item to a layer index item object and add to the index'''
        objs = []
        for entry in rawobjs:
            objs.append(objtype(self, entry))
        self.add_element(indexname, objs)

    # Quick lookup table for searching layerId and branchID combos
    @property
    def layerBranches_layerId_branchId(self):
        def createCache(self):
            cache = {}
            for layerbranchid in self.layerBranches:
                layerbranch = self.layerBranches[layerbranchid]
                cache["%s:%s" % (layerbranch.layer_id, layerbranch.branch_id)] = layerbranch
            return cache

        if self.isLocked():
            cache = getattr(self, '_layerBranches_layerId_branchId', None)
        else:
            cache = None

        if not cache:
            cache = createCache(self)

        if self.isLocked():
            super().__setattr__('_layerBranches_layerId_branchId', cache)

        return cache

    # Quick lookup table for finding all dependencies of a layerBranch
    @property
    def layerDependencies_layerBranchId(self):
        def createCache(self):
            cache = {}
            # This ensures empty lists for all branchids
            for layerbranchid in self.layerBranches:
                cache[layerbranchid] = []

            for layerdependencyid in self.layerDependencies:
                layerdependency = self.layerDependencies[layerdependencyid]
                cache[layerdependency.layerbranch_id].append(layerdependency)
            return cache

        if self.isLocked():
            cache = getattr(self, '_layerDependencies_layerBranchId', None)
        else:
            cache = None

        if not cache:
            cache = createCache(self)

        if self.isLocked():
            super().__setattr__('_layerDependencies_layerBranchId', cache)

        return cache

    # Quick lookup table for finding all instances of a vcs_url
    @property
    def layerBranches_vcsUrl(self):
        def createCache(self):
            cache = {}
            for layerbranchid in self.layerBranches:
                layerbranch = self.layerBranches[layerbranchid]
                if layerbranch.layer.vcs_url not in cache:
                   cache[layerbranch.layer.vcs_url] = [layerbranch]
                else:
                   cache[layerbranch.layer.vcs_url].append(layerbranch)
            return cache

        if self.isLocked():
            cache = getattr(self, '_layerBranches_vcsUrl', None)
        else:
            cache = None

        if not cache:
            cache = createCache(self)

        if self.isLocked():
            super().__setattr__('_layerBranches_vcsUrl', cache)

        return cache


    def find_vcs_url(self, vcs_url, branches=None):
        ''''Return the first layerBranch with the given vcs_url

            If a list of branches has not been specified, we will iterate on
            all branches until the first vcs_url is found.'''

        if not self.__bool__():
            return None

        for layerbranch in self.layerBranches_vcsUrl:
            if branches and layerbranch.branch.name not in branches:
                continue

            return layerbranch

        return None


    def find_collection(self, collection, version=None, branches=None):
        '''Return the first layerBranch with the given collection name

           If a list of branches has not been specified, we will iterate on
           all branches until the first collection is found.'''

        if not self.__bool__():
            return None

        for layerbranchid in self.layerBranches:
            layerbranch = self.layerBranches[layerbranchid]
            if branches and layerbranch.branch.name not in branches:
                continue

            if layerbranch.collection == collection and \
                (version is None or version == layerbranch.version):
                return layerbranch

        return None


    def find_layerbranch(self, name, branches=None):
        '''Return the first layerbranch whose layer name matches

           If a list of branches has not been specified, we will iterate on
           all branches until the first layer with that name is found.'''

        if not self.__bool__():
            return None

        for layerbranchid in self.layerBranches:
            layerbranch = self.layerBranches[layerbranchid]
            if branches and layerbranch.branch.name not in branches:
                continue

            if layerbranch.layer.name == name:
                return layerbranch

        return None

    def find_dependencies(self, names=None, branches=None, layerBranches=None, ignores=None):
        '''Return a tuple of all dependencies and valid items for the list of (layer) names

        The dependency scanning happens depth-first.  The returned
        dependencies should be in the best order to define bblayers.

          names - list of layer names (searching layerItems)
          branches - when specified (with names) only this list of branches are evaluated

          layerBranches - list of layerBranches to resolve dependencies

          ignores - list of layer names to ignore

        return: (dependencies, invalid)

          dependencies[LayerItem.name] = [ LayerBranch, LayerDependency1, LayerDependency2, ... ]
          invalid = [ LayerItem.name1, LayerItem.name2, ... ]'''

        invalid = []

        # Convert name/branch to layerBranches
        if layerbranches is None:
            layerbranches = []

        for name in names:
            if ignores and name in ignores:
                continue

            layerbranch = self.find_layerbranch(name, branches)
            if not layerbranch:
                invalid.append(name)
            else:
                layerbranches.append(layerbranch)

        for layerbranch in layerbranches:
            if layerbranch.index != self:
                raise LayerIndexException("Can not resolve dependencies across indexes with this class function!")

        def _resolve_dependencies(layerbranches, ignores, dependencies, invalid):
            for layerbranch in layerbranches:
                if ignores and layerbranch.layer.name in ignores:
                    continue

                for layerdependency in layerbranch.index.layerDependencies_layerBranchId[layerbranch.id]:
                    deplayerbranch = layerdependency.dependency_layerBranch

                    if ignores and deplayerbranch.layer.name in ignores:
                        continue

                    # New dependency, we need to resolve it now... depth-first
                    if deplayerbranch.layer.name not in dependencies:
                        (dependencies, invalid) = _resolve_dependencies([deplayerbranch], ignores, dependencies, invalid)

                    if deplayerbranch.layer.name not in dependencies:
                        dependencies[deplayerbranch.layer.name] = [deplayerbranch, layerdependency]
                    else:
                        if layerdependency not in dependencies[deplayerbranch.layer.name]:
                            dependencies[deplayerbranch.layer.name].append(layerdependency)

                return (dependencies, invalid)

        # OK, resolve this one...
        dependencies = OrderedDict()
        (dependencies, invalid) = _resolve_dependencies(layerbranches, ignores, dependencies, invalid)

        # Is this item already in the list, if not add it
        for layerbranch in layerbranches:
            if layerbranch.layer.name not in dependencies:
                dependencies[layerbranch.layer.name] = [layerbranch]

        return (dependencies, invalid)


# Define a basic LayerIndexItemObj.  This object forms the basis for all other
# objects.  The raw Layer Index data is stored in the _data element, but we
# do not want users to access data directly.  So wrap this and protect it
# from direct manipulation.
#
# It is up to the insantiators of the objects to fill them out, and once done
# lock the objects to prevent further accidently manipulation.
#
# Using the getattr, setattr and properties we can access and manipulate
# the data within the data element.
class LayerIndexItemObj():
    def __init__(self, index, data=None, lock=False):
        if data is None:
            data = {}

        if type(data) != type(dict()):
            raise TypeError('data (%s) is not a dict' % type(data))

        super().__setattr__('_lock',  lock)
        super().__setattr__('index', index)
        super().__setattr__('_data',  data)

    def __eq__(self, other):
        if self.__class__ != other.__class__:
            return False
        res=(self._data == other._data)
        return res

    def __bool__(self):
        return bool(self._data)

    def __getattr__(self, name):
        # These are internal to THIS class, and not part of data
        if name == "index" or name.startswith('_'):
            return super().__getattribute__(name)

        if name not in self._data:
            raise AttributeError('%s not in datastore' % name)

        return self._data[name]

    def _setattr(self, name, value, prop=True):
        '''__setattr__ like function, but with control over property object behavior'''
        if self.isLocked():
            raise TypeError("Can not set attribute '%s': Object data is locked" % name)

        if name.startswith('_'):
            super().__setattr__(name, value)
            return

        # Since __setattr__ runs before properties, we need to check if
        # there is a setter property and then execute it
        # ... or return self._data[name]
        propertyobj = getattr(self.__class__, name, None)
        if prop and isinstance(propertyobj, property):
            if propertyobj.fset:
                propertyobj.fset(self, value)
            else:
                raise AttributeError('Attribute %s is readonly, and may not be set' % name)
        else:
            self._data[name] = value

    def __setattr__(self, name, value):
        self._setattr(name, value, prop=True)

    def _delattr(self, name, prop=True):
        # Since __delattr__ runs before properties, we need to check if
        # there is a deleter property and then execute it
        # ... or we pop it ourselves..
        propertyobj = getattr(self.__class__, name, None)
        if prop and isinstance(propertyobj, property):
            if propertyobj.fdel:
                propertyobj.fdel(self)
            else:
                raise AttributeError('Attribute %s is readonly, and may not be deleted' % name)
        else:
            self._data.pop(name)

    def __delattr__(self, name):
        self._delattr(name, prop=True)

    def lockData(self):
        '''Lock data object (make it readonly)'''
        super().__setattr__("_lock", True)

    def unlockData(self):
        '''unlock data object (make it readonly)'''
        super().__setattr__("_lock", False)

    def isLocked(self):
        '''Is this object locked (readonly)?'''
        return self._lock

# Branch object
class Branch(LayerIndexItemObj):
    def define_data(self, id, name, bitbake_branch,
                 short_description=None, sort_priority=1,
                 updates_enabled=True, updated=None,
                 update_environment=None):
        self.id = id
        self.name = name
        self.bitbake_branch = bitbake_branch
        self.short_description = short_description or name
        self.sort_priority = sort_priority
        self.updates_enabled = updates_enabled
        self.updated = updated or datetime.datetime.today().isoformat()
        self.update_environment = update_environment

    @property
    def name(self):
        return self.__getattr__('name')

    @name.setter
    def name(self, value):
        self._data['name'] = value

        if self.bitbake_branch == value:
            self.bitbake_branch = ""

    @name.deleter
    def name(self):
        self._delattr('name', prop=False)

    @property
    def bitbake_branch(self):
        try:
            return self.__getattr__('bitbake_branch')
        except AttributeError:
            return self.name

    @bitbake_branch.setter
    def bitbake_branch(self, value):
        if self.name == value:
            self._data['bitbake_branch'] = ""
        else:
            self._data['bitbake_branch'] = value

    @bitbake_branch.deleter
    def bitbake_branch(self):
        self._delattr('bitbake_branch', prop=False)


class LayerItem(LayerIndexItemObj):
    def define_data(self, id, name, status='P',
                 layer_type='A', summary=None,
                 description=None,
                 vcs_url=None, vcs_web_url=None,
                 vcs_web_tree_base_url=None,
                 vcs_web_file_base_url=None,
                 usage_url=None,
                 mailing_list_url=None,
                 index_preference=1,
                 classic=False,
                 updated=None):
        self.id = id
        self.name = name
        self.status = status
        self.layer_type = layer_type
        self.summary = summary or name
        self.description = description or summary or name
        self.vcs_url = vcs_url
        self.vcs_web_url = vcs_web_url
        self.vcs_web_tree_base_url = vcs_web_tree_base_url
        self.vcs_web_file_base_url = vcs_web_file_base_url
        self.index_preference = index_preference
        self.classic = classic
        self.updated = updated or datetime.datetime.today().isoformat()


class LayerBranch(LayerIndexItemObj):
    def define_data(self, id, collection, version, layer, branch,
                 vcs_subdir="", vcs_last_fetch=None,
                 vcs_last_rev=None, vcs_last_commit=None,
                 actual_branch="",
                 updated=None):
        self.id = id
        self.collection = collection
        self.version = version
        if isinstance(layer, LayerItem):
            self.layer = layer
        else:
            self.layer_id = layer

        if isinstance(branch, Branch):
            self.branch = branch
        else:
            self.branch_id = branch

        self.vcs_subdir = vcs_subdir
        self.vcs_last_fetch = vcs_last_fetch
        self.vcs_last_rev = vcs_last_rev
        self.vcs_last_commit = vcs_last_commit
        self.actual_branch = actual_branch
        self.updated = updated or datetime.datetime.today().isoformat()

    # This is a little odd, the _data attribute is 'layer', but it's really
    # referring to the layer id.. so lets adjust this to make it useful
    @property
    def layer_id(self):
        return self.__getattr__('layer')

    @layer_id.setter
    def layer_id(self, value):
        self._setattr('layer', value, prop=False)

    @layer_id.deleter
    def layer_id(self):
        self._delattr('layer', prop=False)

    @property
    def layer(self):
        try:
            return self.index.layerItems[self.layer_id]
        except KeyError:
            raise AttributeError('Unable to find layerItems in index to map layer_id %s' % self.layer_id)
        except IndexError:
            raise AttributeError('Unable to find layer_id %s in index layerItems' % self.layer_id)

    @layer.setter
    def layer(self, value):
        if not isinstance(value, LayerItem):
            raise TypeError('value is not a LayerItem')
        if self.index != value.index:
            raise AttributeError('Object and value do not share the same index and thus key set.')
        self.layer_id = value.id

    @layer.deleter
    def layer(self):
        del self.layer_id

    @property
    def branch_id(self):
        return self.__getattr__('branch')

    @branch_id.setter
    def branch_id(self, value):
        self._setattr('branch', value, prop=False)

    @branch_id.deleter
    def branch_id(self):
        self._delattr('branch', prop=False)

    @property
    def branch(self):
        try:
            logger.debug("Get branch object from branches[%s]" % (self.branch_id))
            return self.index.branches[self.branch_id]
        except KeyError:
            raise AttributeError('Unable to find branches in index to map branch_id %s' % self.branch_id)
        except IndexError:
            raise AttributeError('Unable to find branch_id %s in index branches' % self.branch_id)

    @branch.setter
    def branch(self, value):
        if not isinstance(value, LayerItem):
            raise TypeError('value is not a LayerItem')
        if self.index != value.index:
            raise AttributeError('Object and value do not share the same index and thus key set.')
        self.branch_id = value.id

    @branch.deleter
    def branch(self):
        del self.branch_id

    @property
    def actual_branch(self):
        if self.__getattr__('actual_branch'):
            return self.__getattr__('actual_branch')
        else:
            return self.branch.name

    @actual_branch.setter
    def actual_branch(self, value):
        logger.debug("Set actual_branch to %s .. name is %s" % (value, self.branch.name))
        if value != self.branch.name:
            self._setattr('actual_branch', value, prop=False)
        else:
            self._setattr('actual_branch', '', prop=False)

    @actual_branch.deleter
    def actual_branch(self):
        self._delattr('actual_branch', prop=False)

# Extend LayerIndexItemObj with common LayerBranch manipulations
# All of the remaining LayerIndex objects refer to layerbranch, and it is
# up to the user to follow that back through the LayerBranch object into
# the layer object to get various attributes.  So add an intermediate set
# of attributes that can easily get us the layerbranch as well as layer.

class LayerIndexItemObj_LayerBranch(LayerIndexItemObj):
    @property
    def layerbranch_id(self):
        return self.__getattr__('layerbranch')

    @layerbranch_id.setter
    def layerbranch_id(self, value):
        self._setattr('layerbranch', value, prop=False)

    @layerbranch_id.deleter
    def layerbranch_id(self):
        self._delattr('layerbranch', prop=False)

    @property
    def layerbranch(self):
        try:
            return self.index.layerBranches[self.layerbranch_id]
        except KeyError:
            raise AttributeError('Unable to find layerBranches in index to map layerbranch_id %s' % self.layerbranch_id)
        except IndexError:
            raise AttributeError('Unable to find layerbranch_id %s in index branches' % self.layerbranch_id)

    @layerbranch.setter
    def layerbranch(self, value):
        if not isinstance(value, LayerBranch):
            raise TypeError('value (%s) is not a layerBranch' % type(value))
        if self.index != value.index:
            raise AttributeError('Object and value do not share the same index and thus key set.')
        self.layerbranch_id = value.id

    @layerbranch.deleter
    def layerbranch(self):
        del self.layerbranch_id

    @property
    def layer_id(self):
        return self.layerbranch.layer_id

    # Doesn't make sense to set or delete layer_id

    @property
    def layer(self):
        return self.layerbranch.layer

    # Doesn't make sense to set or delete layer


class LayerDependency(LayerIndexItemObj_LayerBranch):
    def define_data(self, id, layerbranch, dependency, required=True):
        self.id = id
        if isinstance(layerbranch, LayerBranch):
            self.layerbranch = layerbranch
        else:
            self.layerbranch_id = layerbranch
        if isinstance(dependency, LayerDependency):
            self.dependency = dependency
        else:
            self.dependency_id = dependency
        self.required = required

    @property
    def dependency_id(self):
        return self.__getattr__('dependency')

    @dependency_id.setter
    def dependency_id(self, value):
        self._setattr('dependency', value, prop=False)

    @dependency_id.deleter
    def dependency_id(self):
        self._delattr('dependency', prop=False)

    @property
    def dependency(self):
        try:
            return self.index.layerItems[self.dependency_id]
        except KeyError:
            raise AttributeError('Unable to find layerItems in index to map layerbranch_id %s' % self.dependency_id)
        except IndexError:
            raise AttributeError('Unable to find dependency_id %s in index layerItems' % self.dependency_id)

    @dependency.setter
    def dependency(self, value):
        if not isinstance(value, LayerDependency):
            raise TypeError('value (%s) is not a dependency' % type(value))
        if self.index != value.index:
            raise AttributeError('Object and value do not share the same index and thus key set.')
        self.dependency_id = value.id

    @dependency.deleter
    def dependency(self):
        self._delattr('dependency', prop=False)

    @property
    def dependency_layerBranch(self):
        layerid = self.dependency_id
        branchid = self.layerbranch.branch_id

        try:
            return self.index.layerBranches_layerId_branchId["%s:%s" % (layerid, branchid)]
        except IndexError:
            # layerBranches_layerId_branchId -- but not layerId:branchId
            raise AttributeError('Unable to find layerId:branchId %s:%s in index layerBranches_layerId_branchId' % (layerid, branchid))
        except KeyError:
            raise AttributeError('Unable to find layerId:branchId %s:%s in layerItems and layerBranches' % (layerid, branchid))

    # dependency_layerBranch doesn't make sense to set or del


class Recipe(LayerIndexItemObj_LayerBranch):
    def define_data(self, id,
                    filename, filepath, pn, pv, layerbranch,
                    summary="", description="", section="", license="",
                    homepage="", bugtracker="", provides="", bbclassextend="",
                    inherits="", disallowed="", updated=None):
        self.id = id
        self.filename = filename
        self.filepath = filepath
        self.pn = pn
        self.pv = pv
        self.summary = summary
        self.description = description
        self.section = section
        self.license = license
        self.homepage = homepage
        self.bugtracker = bugtracker
        self.provides = provides
        self.bbclassextend = bbclassextend
        self.inherits = inherits
        self.updated = updated or datetime.datetime.today().isoformat()
        self.disallowed = disallowed
        if isinstance(layerbranch, LayerBranch):
            self.layerbranch = layerbranch
        else:
            self.layerbranch_id = layerbranch

    @property
    def fullpath(self):
        return os.path.join(self.filepath, self.filename)

    # Set would need to understand how to split it
    # del would we del both parts?

    @property
    def inherits(self):
        if 'inherits' not in self._data:
            # Older indexes may not have this, so emulate it
            if '-image-' in self.pn:
                return 'image'
        return self.__getattr__('inherits')

    @inherits.setter
    def inherits(self, value):
        return self._setattr('inherits', value, prop=False)

    @inherits.deleter
    def inherits(self):
        return self._delattr('inherits', prop=False)


class Machine(LayerIndexItemObj_LayerBranch):
    def define_data(self, id,
                    name, description, layerbranch,
                    updated=None):
        self.id = id
        self.name = name
        self.description = description
        if isinstance(layerbranch, LayerBranch):
            self.layerbranch = layerbranch
        else:
            self.layerbranch_id = layerbranch
        self.updated = updated or datetime.datetime.today().isoformat()

class Distro(LayerIndexItemObj_LayerBranch):
    def define_data(self, id,
                    name, description, layerbranch,
                    updated=None):
        self.id = id
        self.name = name
        self.description = description
        if isinstance(layerbranch, LayerBranch):
            self.layerbranch = layerbranch
        else:
            self.layerbranch_id = layerbranch
        self.updated = updated or datetime.datetime.today().isoformat()

# When performing certain actions, we may need to sort the data.
# This will allow us to keep it consistent from run to run.
def sort_entry(item):
    newitem = item
    try:
        if type(newitem) == type(dict()):
            newitem = OrderedDict(sorted(newitem.items(), key=lambda t: t[0]))
            for index in newitem:
                newitem[index] = sort_entry(newitem[index])
        elif type(newitem) == type(list()):
            newitem.sort(key=lambda obj: obj['id'])
            for index, _ in enumerate(newitem):
                newitem[index] = sort_entry(newitem[index])
    except:
        logger.error('Sort failed for item %s' % type(item))
        pass

    return newitem
