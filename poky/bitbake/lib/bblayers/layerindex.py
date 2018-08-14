import argparse
import http.client
import json
import logging
import os
import subprocess
import urllib.parse

from bblayers.action import ActionPlugin

logger = logging.getLogger('bitbake-layers')


def plugin_init(plugins):
    return LayerIndexPlugin()


class LayerIndexPlugin(ActionPlugin):
    """Subcommands for interacting with the layer index.

    This class inherits ActionPlugin to get do_add_layer.
    """

    def get_json_data(self, apiurl):
        proxy_settings = os.environ.get("http_proxy", None)
        conn = None
        _parsedurl = urllib.parse.urlparse(apiurl)
        path = _parsedurl.path
        query = _parsedurl.query

        def parse_url(url):
            parsedurl = urllib.parse.urlparse(url)
            if parsedurl.netloc[0] == '[':
                host, port = parsedurl.netloc[1:].split(']', 1)
                if ':' in port:
                    port = port.rsplit(':', 1)[1]
                else:
                    port = None
            else:
                if parsedurl.netloc.count(':') == 1:
                    (host, port) = parsedurl.netloc.split(":")
                else:
                    host = parsedurl.netloc
                    port = None
            return (host, 80 if port is None else int(port))

        if proxy_settings is None:
            host, port = parse_url(apiurl)
            conn = http.client.HTTPConnection(host, port)
            conn.request("GET", path + "?" + query)
        else:
            host, port = parse_url(proxy_settings)
            conn = http.client.HTTPConnection(host, port)
            conn.request("GET", apiurl)

        r = conn.getresponse()
        if r.status != 200:
            raise Exception("Failed to read " + path + ": %d %s" % (r.status, r.reason))
        return json.loads(r.read().decode())

    def get_layer_deps(self, layername, layeritems, layerbranches, layerdependencies, branchnum, selfname=False):
        def layeritems_info_id(items_name, layeritems):
            litems_id = None
            for li in layeritems:
                if li['name'] == items_name:
                    litems_id = li['id']
                    break
            return litems_id

        def layerbranches_info(items_id, layerbranches):
            lbranch = {}
            for lb in layerbranches:
                if lb['layer'] == items_id and lb['branch'] == branchnum:
                    lbranch['id'] = lb['id']
                    lbranch['vcs_subdir'] = lb['vcs_subdir']
                    break
            return lbranch

        def layerdependencies_info(lb_id, layerdependencies):
            ld_deps = []
            for ld in layerdependencies:
                if ld['layerbranch'] == lb_id and not ld['dependency'] in ld_deps:
                    ld_deps.append(ld['dependency'])
            if not ld_deps:
                logger.error("The dependency of layerDependencies is not found.")
            return ld_deps

        def layeritems_info_name_subdir(items_id, layeritems):
            litems = {}
            for li in layeritems:
                if li['id'] == items_id:
                    litems['vcs_url'] = li['vcs_url']
                    litems['name'] = li['name']
                    break
            return litems

        if selfname:
            selfid = layeritems_info_id(layername, layeritems)
            lbinfo = layerbranches_info(selfid, layerbranches)
            if lbinfo:
                selfsubdir = lbinfo['vcs_subdir']
            else:
                logger.error("%s is not found in the specified branch" % layername)
                return
            selfurl = layeritems_info_name_subdir(selfid, layeritems)['vcs_url']
            if selfurl:
                return selfurl, selfsubdir
            else:
                logger.error("Cannot get layer %s git repo and subdir" % layername)
                return
        ldict = {}
        itemsid = layeritems_info_id(layername, layeritems)
        if not itemsid:
            return layername, None
        lbid = layerbranches_info(itemsid, layerbranches)
        if lbid:
            lbid = layerbranches_info(itemsid, layerbranches)['id']
        else:
            logger.error("%s is not found in the specified branch" % layername)
            return None, None
        for dependency in layerdependencies_info(lbid, layerdependencies):
            lname = layeritems_info_name_subdir(dependency, layeritems)['name']
            lurl = layeritems_info_name_subdir(dependency, layeritems)['vcs_url']
            lsubdir = layerbranches_info(dependency, layerbranches)['vcs_subdir']
            ldict[lname] = lurl, lsubdir
        return None, ldict

    def get_fetch_layer(self, fetchdir, url, subdir, fetch_layer):
        layername = self.get_layer_name(url)
        if os.path.splitext(layername)[1] == '.git':
            layername = os.path.splitext(layername)[0]
        repodir = os.path.join(fetchdir, layername)
        layerdir = os.path.join(repodir, subdir)
        if not os.path.exists(repodir):
            if fetch_layer:
                result = subprocess.call('git clone %s %s' % (url, repodir), shell = True)
                if result:
                    logger.error("Failed to download %s" % url)
                    return None, None
                else:
                    return layername, layerdir
            else:
                logger.plain("Repository %s needs to be fetched" % url)
                return layername, layerdir
        elif os.path.exists(layerdir):
            return layername, layerdir
        else:
            logger.error("%s is not in %s" % (url, subdir))
        return None, None

    def do_layerindex_fetch(self, args):
        """Fetches a layer from a layer index along with its dependent layers, and adds them to conf/bblayers.conf.
"""
        apiurl = self.tinfoil.config_data.getVar('BBLAYERS_LAYERINDEX_URL')
        if not apiurl:
            logger.error("Cannot get BBLAYERS_LAYERINDEX_URL")
            return 1
        else:
            if apiurl[-1] != '/':
                apiurl += '/'
            apiurl += "api/"
        apilinks = self.get_json_data(apiurl)
        branches = self.get_json_data(apilinks['branches'])

        branchnum = 0
        for branch in branches:
            if branch['name'] == args.branch:
                branchnum = branch['id']
                break
        if branchnum == 0:
            validbranches = ', '.join([branch['name'] for branch in branches])
            logger.error('Invalid layer branch name "%s". Valid branches: %s' % (args.branch, validbranches))
            return 1

        ignore_layers = []
        for collection in self.tinfoil.config_data.getVar('BBFILE_COLLECTIONS').split():
            lname = self.tinfoil.config_data.getVar('BBLAYERS_LAYERINDEX_NAME_%s' % collection)
            if lname:
                ignore_layers.append(lname)

        if args.ignore:
            ignore_layers.extend(args.ignore.split(','))

        layeritems = self.get_json_data(apilinks['layerItems'])
        layerbranches = self.get_json_data(apilinks['layerBranches'])
        layerdependencies = self.get_json_data(apilinks['layerDependencies'])
        invaluenames = []
        repourls = {}
        printlayers = []

        def query_dependencies(layers, layeritems, layerbranches, layerdependencies, branchnum):
            depslayer = []
            for layername in layers:
                invaluename, layerdict = self.get_layer_deps(layername, layeritems, layerbranches, layerdependencies, branchnum)
                if layerdict:
                    repourls[layername] = self.get_layer_deps(layername, layeritems, layerbranches, layerdependencies, branchnum, selfname=True)
                    for layer in layerdict:
                        if not layer in ignore_layers:
                            depslayer.append(layer)
                        printlayers.append((layername, layer, layerdict[layer][0], layerdict[layer][1]))
                        if not layer in ignore_layers and not layer in repourls:
                            repourls[layer] = (layerdict[layer][0], layerdict[layer][1])
                if invaluename and not invaluename in invaluenames:
                    invaluenames.append(invaluename)
            return depslayer

        depslayers = query_dependencies(args.layername, layeritems, layerbranches, layerdependencies, branchnum)
        while depslayers:
            depslayer = query_dependencies(depslayers, layeritems, layerbranches, layerdependencies, branchnum)
            depslayers = depslayer
        if invaluenames:
            for invaluename in invaluenames:
                logger.error('Layer "%s" not found in layer index' % invaluename)
            return 1
        logger.plain("%s  %s  %s  %s" % ("Layer".ljust(19), "Required by".ljust(19), "Git repository".ljust(54), "Subdirectory"))
        logger.plain('=' * 115)
        for layername in args.layername:
            layerurl = repourls[layername]
            logger.plain("%s %s %s %s" % (layername.ljust(20), '-'.ljust(20), layerurl[0].ljust(55), layerurl[1]))
        printedlayers = []
        for layer, dependency, gitrepo, subdirectory in printlayers:
            if dependency in printedlayers:
                continue
            logger.plain("%s %s %s %s" % (dependency.ljust(20), layer.ljust(20), gitrepo.ljust(55), subdirectory))
            printedlayers.append(dependency)

        if repourls:
            fetchdir = self.tinfoil.config_data.getVar('BBLAYERS_FETCH_DIR')
            if not fetchdir:
                logger.error("Cannot get BBLAYERS_FETCH_DIR")
                return 1
            if not os.path.exists(fetchdir):
                os.makedirs(fetchdir)
            addlayers = []
            for repourl, subdir in repourls.values():
                name, layerdir = self.get_fetch_layer(fetchdir, repourl, subdir, not args.show_only)
                if not name:
                    # Error already shown
                    return 1
                addlayers.append((subdir, name, layerdir))
        if not args.show_only:
            for subdir, name, layerdir in set(addlayers):
                if os.path.exists(layerdir):
                    if subdir:
                        logger.plain("Adding layer \"%s\" to conf/bblayers.conf" % subdir)
                    else:
                        logger.plain("Adding layer \"%s\" to conf/bblayers.conf" % name)
                    localargs = argparse.Namespace()
                    localargs.layerdir = layerdir
                    localargs.force = args.force
                    self.do_add_layer(localargs)
                else:
                    break

    def do_layerindex_show_depends(self, args):
        """Find layer dependencies from layer index.
"""
        args.show_only = True
        args.ignore = []
        self.do_layerindex_fetch(args)

    def register_commands(self, sp):
        parser_layerindex_fetch = self.add_command(sp, 'layerindex-fetch', self.do_layerindex_fetch)
        parser_layerindex_fetch.add_argument('-n', '--show-only', help='show dependencies and do nothing else', action='store_true')
        parser_layerindex_fetch.add_argument('-b', '--branch', help='branch name to fetch (default %(default)s)', default='master')
        parser_layerindex_fetch.add_argument('-i', '--ignore', help='assume the specified layers do not need to be fetched/added (separate multiple layers with commas, no spaces)', metavar='LAYER')
        parser_layerindex_fetch.add_argument('layername', nargs='+', help='layer to fetch')

        parser_layerindex_show_depends = self.add_command(sp, 'layerindex-show-depends', self.do_layerindex_show_depends)
        parser_layerindex_show_depends.add_argument('-b', '--branch', help='branch name to fetch (default %(default)s)', default='master')
        parser_layerindex_show_depends.add_argument('layername', nargs='+', help='layer to query')
