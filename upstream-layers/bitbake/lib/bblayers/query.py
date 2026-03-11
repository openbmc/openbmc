#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import collections
import fnmatch
import logging
import sys
import os
import re

import bb.utils

from bblayers.common import LayerPlugin

logger = logging.getLogger('bitbake-layers')


def plugin_init(plugins):
    return QueryPlugin()


class QueryPlugin(LayerPlugin):
    def __init__(self):
        super(QueryPlugin, self).__init__()
        self.collection_res = {}

    def do_show_layers(self, args):
        """show current configured layers."""
        logger.plain("%s  %s  %s" % ("layer".ljust(20), "path".ljust(70), "priority"))
        logger.plain('=' * 104)
        for layer, _, regex, pri in self.tinfoil.cooker.bbfile_config_priorities:
            layerdir = self.bbfile_collections.get(layer, None)
            layername = layer
            logger.plain("%s  %s  %s" % (layername.ljust(20), layerdir.ljust(70), pri))

    def version_str(self, pe, pv, pr = None):
        verstr = "%s" % pv
        if pr:
            verstr = "%s-%s" % (verstr, pr)
        if pe:
            verstr = "%s:%s" % (pe, verstr)
        return verstr

    def do_show_overlayed(self, args):
        """list overlayed recipes (where the same recipe exists in another layer)

Lists the names of overlayed recipes and the available versions in each
layer, with the preferred version first. Note that skipped recipes that
are overlayed will also be listed, with a " (skipped)" suffix.
"""

        items_listed = self.list_recipes('Overlayed recipes', None, True, args.same_version, args.filenames, False, True, None, False, None, args.mc)

        # Check for overlayed .bbclass files
        classes = collections.defaultdict(list)
        for layerdir in self.bblayers:
            for c in ["classes-global", "classes-recipe", "classes"]:
                classdir = os.path.join(layerdir, c)
                if os.path.exists(classdir):
                    for classfile in os.listdir(classdir):
                        if os.path.splitext(classfile)[1] == '.bbclass':
                            classes[classfile].append(classdir)

        # Locating classes and other files is a bit more complicated than recipes -
        # layer priority is not a factor; instead BitBake uses the first matching
        # file in BBPATH, which is manipulated directly by each layer's
        # conf/layer.conf in turn, thus the order of layers in bblayers.conf is a
        # factor - however, each layer.conf is free to either prepend or append to
        # BBPATH (or indeed do crazy stuff with it). Thus the order in BBPATH might
        # not be exactly the order present in bblayers.conf either.
        bbpath = str(self.tinfoil.config_data.getVar('BBPATH'))
        overlayed_class_found = False
        for (classfile, classdirs) in classes.items():
            if len(classdirs) > 1:
                if not overlayed_class_found:
                    logger.plain('=== Overlayed classes ===')
                    overlayed_class_found = True

                mainfile = bb.utils.which(bbpath, os.path.join('classes', classfile))
                if args.filenames:
                    logger.plain('%s' % mainfile)
                else:
                    # We effectively have to guess the layer here
                    logger.plain('%s:' % classfile)
                    mainlayername = '?'
                    for layerdir in self.bblayers:
                        classdir = os.path.join(layerdir, 'classes')
                        if mainfile.startswith(classdir):
                            mainlayername = self.get_layer_name(layerdir)
                    logger.plain('  %s' % mainlayername)
                for classdir in classdirs:
                    fullpath = os.path.join(classdir, classfile)
                    if fullpath != mainfile:
                        if args.filenames:
                            print('  %s' % fullpath)
                        else:
                            print('  %s' % self.get_layer_name(os.path.dirname(classdir)))

        if overlayed_class_found:
            items_listed = True;

        if not items_listed:
            logger.plain('No overlayed files found.')

    def do_show_recipes(self, args):
        """list available recipes, showing the layer they are provided by

Lists the names of recipes and the available versions in each
layer, with the preferred version first. Optionally you may specify
pnspec to match a specified recipe name (supports wildcards). Note that
skipped recipes will also be listed, with a " (skipped)" suffix.
"""

        inheritlist = args.inherits.split(',') if args.inherits else []
        if inheritlist or args.pnspec or args.multiple:
            title = 'Matching recipes:'
        else:
            title = 'Available recipes:'
        self.list_recipes(title, args.pnspec, False, False, args.filenames, args.recipes_only, args.multiple, args.layer, args.bare, inheritlist, args.mc)

    def list_recipes(self, title, pnspec, show_overlayed_only, show_same_ver_only, show_filenames, show_recipes_only, show_multi_provider_only, selected_layer, bare, inherits, mc):
        if inherits:
            bbpath = str(self.tinfoil.config_data.getVar('BBPATH'))
            for classname in inherits:
                found = False
                for c in ["classes-global", "classes-recipe", "classes"]:
                    cfile = c + '/%s.bbclass' % classname
                    if bb.utils.which(bbpath, cfile, history=False):
                        found = True
                        break
                if not found:
                    logger.error('No class named %s found in BBPATH', classname)
                    sys.exit(1)

        pkg_pn = self.tinfoil.cooker.recipecaches[mc].pkg_pn
        (latest_versions, preferred_versions, required_versions) = self.tinfoil.find_providers(mc)
        allproviders = self.tinfoil.get_all_providers(mc)

        # Ensure we list skipped recipes
        # We are largely guessing about PN, PV and the preferred version here,
        # but we have no choice since skipped recipes are not fully parsed
        skiplist = list(self.tinfoil.cooker.skiplist_by_mc[mc].keys())

        if mc:
            skiplist = [s.removeprefix(f'mc:{mc}:') for s in skiplist]

        for fn in skiplist:
            recipe_parts = os.path.splitext(os.path.basename(fn))[0].split('_')
            p = recipe_parts[0]
            if len(recipe_parts) > 1:
                ver = (None, recipe_parts[1], None)
            else:
                ver = (None, 'unknown', None)
            allproviders[p].append((ver, fn))
            if not p in pkg_pn:
                pkg_pn[p] = 'dummy'
                preferred_versions[p] = (ver, fn)

        def print_item(f, pn, ver, layer, ispref):
            if not selected_layer or layer == selected_layer:
                if not bare and f in skiplist:
                    skipped = ' (skipped: %s)' % self.tinfoil.cooker.skiplist_by_mc[mc][f].skipreason
                else:
                    skipped = ''
                if show_filenames:
                    if ispref:
                        logger.plain("%s%s", f, skipped)
                    else:
                        logger.plain("  %s%s", f, skipped)
                elif show_recipes_only:
                    if pn not in show_unique_pn:
                        show_unique_pn.append(pn)
                        logger.plain("%s%s", pn, skipped)
                else:
                    if ispref:
                        logger.plain("%s:", pn)
                    logger.plain("  %s %s%s", layer.ljust(20), ver, skipped)

        global_inherit = (self.tinfoil.config_data.getVar('INHERIT') or "").split()
        cls_re = re.compile('classes.*/')

        preffiles = []
        show_unique_pn = []
        items_listed = False
        for p in sorted(pkg_pn):
            if pnspec:
                found=False
                for pnm in pnspec:
                    if fnmatch.fnmatch(p, pnm):
                        found=True
                        break
                if not found:
                    continue

            if len(allproviders[p]) > 1 or not show_multi_provider_only:
                pref = preferred_versions[p]
                realfn = bb.cache.virtualfn2realfn(pref[1])
                preffile = realfn[0]

                # We only display once per recipe, we should prefer non extended versions of the
                # recipe if present (so e.g. in OpenEmbedded, openssl rather than nativesdk-openssl
                # which would otherwise sort first).
                if realfn[1] and realfn[0] in self.tinfoil.cooker.recipecaches[mc].pkg_fn:
                    continue

                if inherits:
                    matchcount = 0
                    recipe_inherits = self.tinfoil.cooker_data.inherits.get(preffile, [])
                    for cls in recipe_inherits:
                        if cls_re.match(cls):
                            continue
                        classname = os.path.splitext(os.path.basename(cls))[0]
                        if classname in global_inherit:
                            continue
                        elif classname in inherits:
                            matchcount += 1
                    if matchcount != len(inherits):
                        # No match - skip this recipe
                        continue

                if preffile not in preffiles:
                    preflayer = self.get_file_layer(preffile)
                    multilayer = False
                    same_ver = True
                    provs = []
                    for prov in allproviders[p]:
                        provfile = bb.cache.virtualfn2realfn(prov[1])[0]
                        provlayer = self.get_file_layer(provfile)
                        provs.append((provfile, provlayer, prov[0]))
                        if provlayer != preflayer:
                            multilayer = True
                        if prov[0] != pref[0]:
                            same_ver = False
                    if (multilayer or not show_overlayed_only) and (same_ver or not show_same_ver_only):
                        if not items_listed:
                            logger.plain('=== %s ===' % title)
                            items_listed = True
                        print_item(preffile, p, self.version_str(pref[0][0], pref[0][1]), preflayer, True)
                        for (provfile, provlayer, provver) in provs:
                            if provfile != preffile:
                                print_item(provfile, p, self.version_str(provver[0], provver[1]), provlayer, False)
                        # Ensure we don't show two entries for BBCLASSEXTENDed recipes
                        preffiles.append(preffile)

        return items_listed

    def get_file_layer(self, filename):
        layerdir = self.get_file_layerdir(filename)
        if layerdir:
            return self.get_layer_name(layerdir)
        else:
            return '?'

    def get_collection_res(self):
        if not self.collection_res:
            self.collection_res = bb.utils.get_collection_res(self.tinfoil.config_data)
        return self.collection_res

    def get_file_layerdir(self, filename):
        layer = bb.utils.get_file_layer(filename, self.tinfoil.config_data, self.get_collection_res())
        return self.bbfile_collections.get(layer, None)

    def remove_layer_prefix(self, f):
        """Remove the layer_dir prefix, e.g., f = /path/to/layer_dir/foo/blah, the
           return value will be: layer_dir/foo/blah"""
        f_layerdir = self.get_file_layerdir(f)
        if not f_layerdir:
            return f
        prefix = os.path.join(os.path.dirname(f_layerdir), '')
        return f[len(prefix):] if f.startswith(prefix) else f

    def do_show_appends(self, args):
        """list bbappend files and recipe files they apply to

Lists recipes with the bbappends that apply to them as subitems.
"""
        if args.pnspec:
            logger.plain('=== Matched appended recipes ===')
        else:
            logger.plain('=== Appended recipes ===')


        cooker_data = self.tinfoil.cooker.recipecaches[args.mc]

        pnlist = list(cooker_data.pkg_pn.keys())
        pnlist.sort()
        appends = False
        for pn in pnlist:
            if args.pnspec:
                found=False
                for pnm in args.pnspec:
                    if fnmatch.fnmatch(pn, pnm):
                        found=True
                        break
                if not found:
                    continue

            if self.show_appends_for_pn(pn, cooker_data, args.mc):
                appends = True

        if not args.pnspec and self.show_appends_for_skipped(args.mc):
            appends = True

        if not appends:
            logger.plain('No append files found')

    def show_appends_for_pn(self, pn, cooker_data, mc):
        filenames = cooker_data.pkg_pn[pn]
        if mc:
            pn = "mc:%s:%s" % (mc, pn)

        best = self.tinfoil.find_best_provider(pn)
        best_filename = os.path.basename(best[3])

        return self.show_appends_output(filenames, best_filename)

    def show_appends_for_skipped(self, mc):
        filenames = [os.path.basename(f)
                    for f in self.tinfoil.cooker.skiplist_by_mc[mc].keys()]
        return self.show_appends_output(filenames, None, " (skipped)")

    def show_appends_output(self, filenames, best_filename, name_suffix = ''):
        appended, missing = self.get_appends_for_files(filenames)
        if appended:
            for basename, appends in appended:
                logger.plain('%s%s:', basename, name_suffix)
                for append in appends:
                    logger.plain('  %s', append)

            if best_filename:
                if best_filename in missing:
                    logger.warning('%s: missing append for preferred version',
                                   best_filename)
            return True
        else:
            return False

    def get_appends_for_files(self, filenames):
        appended, notappended = [], []
        for filename in filenames:
            _, cls, mc = bb.cache.virtualfn2realfn(filename)
            if cls:
                continue

            basename = os.path.basename(filename)
            appends = self.tinfoil.cooker.collections[mc].get_file_appends(basename)
            if appends:
                appended.append((basename, list(appends)))
            else:
                notappended.append(basename)
        return appended, notappended

    def do_show_cross_depends(self, args):
        """Show dependencies between recipes that cross layer boundaries.

Figure out the dependencies between recipes that cross layer boundaries.

NOTE: .bbappend files can impact the dependencies.
"""
        ignore_layers = (args.ignore or '').split(',')

        pkg_fn = self.tinfoil.cooker_data.pkg_fn
        bbpath = str(self.tinfoil.config_data.getVar('BBPATH'))
        self.require_re = re.compile(r"require\s+(.+)")
        self.include_re = re.compile(r"include\s+(.+)")
        self.inherit_re = re.compile(r"inherit\s+(.+)")

        global_inherit = (self.tinfoil.config_data.getVar('INHERIT') or "").split()

        # The bb's DEPENDS and RDEPENDS
        for f in pkg_fn:
            f = bb.cache.virtualfn2realfn(f)[0]
            # Get the layername that the file is in
            layername = self.get_file_layer(f)

            # The DEPENDS
            deps = self.tinfoil.cooker_data.deps[f]
            for pn in deps:
                if pn in self.tinfoil.cooker_data.pkg_pn:
                    best = self.tinfoil.find_best_provider(pn)
                    self.check_cross_depends("DEPENDS", layername, f, best[3], args.filenames, ignore_layers)

            # The RDPENDS
            all_rdeps = self.tinfoil.cooker_data.rundeps[f].values()
            # Remove the duplicated or null one.
            sorted_rdeps = {}
            # The all_rdeps is the list in list, so we need two for loops
            for k1 in all_rdeps:
                for k2 in k1:
                    sorted_rdeps[k2] = 1
            all_rdeps = sorted_rdeps.keys()
            for rdep in all_rdeps:
                all_p, best = self.tinfoil.get_runtime_providers(rdep)
                if all_p:
                    if f in all_p:
                        # The recipe provides this one itself, ignore
                        continue
                    self.check_cross_depends("RDEPENDS", layername, f, best, args.filenames, ignore_layers)

            # The RRECOMMENDS
            all_rrecs = self.tinfoil.cooker_data.runrecs[f].values()
            # Remove the duplicated or null one.
            sorted_rrecs = {}
            # The all_rrecs is the list in list, so we need two for loops
            for k1 in all_rrecs:
                for k2 in k1:
                    sorted_rrecs[k2] = 1
            all_rrecs = sorted_rrecs.keys()
            for rrec in all_rrecs:
                all_p, best = self.tinfoil.get_runtime_providers(rrec)
                if all_p:
                    if f in all_p:
                        # The recipe provides this one itself, ignore
                        continue
                    self.check_cross_depends("RRECOMMENDS", layername, f, best, args.filenames, ignore_layers)

            # The inherit class
            cls_re = re.compile('classes.*/')
            if f in self.tinfoil.cooker_data.inherits:
                inherits = self.tinfoil.cooker_data.inherits[f]
                for cls in inherits:
                    # The inherits' format is [classes/cls, /path/to/classes/cls]
                    # ignore the classes/cls.
                    if not cls_re.match(cls):
                        classname = os.path.splitext(os.path.basename(cls))[0]
                        if classname in global_inherit:
                            continue
                        inherit_layername = self.get_file_layer(cls)
                        if inherit_layername != layername and not inherit_layername in ignore_layers:
                            if not args.filenames:
                                f_short = self.remove_layer_prefix(f)
                                cls = self.remove_layer_prefix(cls)
                            else:
                                f_short = f
                            logger.plain("%s inherits %s" % (f_short, cls))

            # The 'require/include xxx' in the bb file
            pv_re = re.compile(r"\${PV}")
            with open(f, 'r') as fnfile:
                line = fnfile.readline()
                while line:
                    m, keyword = self.match_require_include(line)
                    # Found the 'require/include xxxx'
                    if m:
                        needed_file = m.group(1)
                        # Replace the ${PV} with the real PV
                        if pv_re.search(needed_file) and f in self.tinfoil.cooker_data.pkg_pepvpr:
                            pv = self.tinfoil.cooker_data.pkg_pepvpr[f][1]
                            needed_file = re.sub(r"\${PV}", pv, needed_file)
                        self.print_cross_files(bbpath, keyword, layername, f, needed_file, args.filenames, ignore_layers)
                    line = fnfile.readline()

        # The "require/include xxx" in conf/machine/*.conf, .inc and .bbclass
        conf_re = re.compile(r".*/conf/machine/[^\/]*\.conf$")
        inc_re = re.compile(r".*\.inc$")
        # The "inherit xxx" in .bbclass
        bbclass_re = re.compile(r".*\.bbclass$")
        for layerdir in self.bblayers:
            layername = self.get_layer_name(layerdir)
            for dirpath, dirnames, filenames in os.walk(layerdir):
                for name in filenames:
                    f = os.path.join(dirpath, name)
                    s = conf_re.match(f) or inc_re.match(f) or bbclass_re.match(f)
                    if s:
                        with open(f, 'r') as ffile:
                            line = ffile.readline()
                            while line:
                                m, keyword = self.match_require_include(line)
                                # Only bbclass has the "inherit xxx" here.
                                bbclass=""
                                if not m and f.endswith(".bbclass"):
                                    m, keyword = self.match_inherit(line)
                                    bbclass=".bbclass"
                                # Find a 'require/include xxxx'
                                if m:
                                    self.print_cross_files(bbpath, keyword, layername, f, m.group(1) + bbclass, args.filenames, ignore_layers)
                                line = ffile.readline()

    def print_cross_files(self, bbpath, keyword, layername, f, needed_filename, show_filenames, ignore_layers):
        """Print the depends that crosses a layer boundary"""
        needed_file = bb.utils.which(bbpath, needed_filename)
        if needed_file:
            # Which layer is this file from
            needed_layername = self.get_file_layer(needed_file)
            if needed_layername != layername and not needed_layername in ignore_layers:
                if not show_filenames:
                    f = self.remove_layer_prefix(f)
                    needed_file = self.remove_layer_prefix(needed_file)
                logger.plain("%s %s %s" %(f, keyword, needed_file))

    def match_inherit(self, line):
        """Match the inherit xxx line"""
        return (self.inherit_re.match(line), "inherits")

    def match_require_include(self, line):
        """Match the require/include xxx line"""
        m = self.require_re.match(line)
        keyword = "requires"
        if not m:
            m = self.include_re.match(line)
            keyword = "includes"
        return (m, keyword)

    def check_cross_depends(self, keyword, layername, f, needed_file, show_filenames, ignore_layers):
        """Print the DEPENDS/RDEPENDS file that crosses a layer boundary"""
        best_realfn = bb.cache.virtualfn2realfn(needed_file)[0]
        needed_layername = self.get_file_layer(best_realfn)
        if needed_layername != layername and not needed_layername in ignore_layers:
            if not show_filenames:
                f = self.remove_layer_prefix(f)
                best_realfn = self.remove_layer_prefix(best_realfn)

            logger.plain("%s %s %s" % (f, keyword, best_realfn))

    def register_commands(self, sp):
        self.add_command(sp, 'show-layers', self.do_show_layers, parserecipes=False)

        parser_show_overlayed = self.add_command(sp, 'show-overlayed', self.do_show_overlayed)
        parser_show_overlayed.add_argument('-f', '--filenames', help='instead of the default formatting, list filenames of higher priority recipes with the ones they overlay indented underneath', action='store_true')
        parser_show_overlayed.add_argument('-s', '--same-version', help='only list overlayed recipes where the version is the same', action='store_true')
        parser_show_overlayed.add_argument('--mc', help='use specified multiconfig', default='')

        parser_show_recipes = self.add_command(sp, 'show-recipes', self.do_show_recipes)
        parser_show_recipes.add_argument('-f', '--filenames', help='instead of the default formatting, list filenames of higher priority recipes with the ones they overlay indented underneath', action='store_true')
        parser_show_recipes.add_argument('-r', '--recipes-only', help='instead of the default formatting, list recipes only', action='store_true')
        parser_show_recipes.add_argument('-m', '--multiple', help='only list where multiple recipes (in the same layer or different layers) exist for the same recipe name', action='store_true')
        parser_show_recipes.add_argument('-i', '--inherits', help='only list recipes that inherit the named class(es) - separate multiple classes using , (without spaces)', metavar='CLASS', default='')
        parser_show_recipes.add_argument('-l', '--layer', help='only list recipes from the selected layer', default='')
        parser_show_recipes.add_argument('-b', '--bare', help='output just names without the "(skipped)" marker', action='store_true')
        parser_show_recipes.add_argument('--mc', help='use specified multiconfig', default='')
        parser_show_recipes.add_argument('pnspec', nargs='*', help='optional recipe name specification (wildcards allowed, enclose in quotes to avoid shell expansion)')

        parser_show_appends = self.add_command(sp, 'show-appends', self.do_show_appends)
        parser_show_appends.add_argument('pnspec', nargs='*', help='optional recipe name specification (wildcards allowed, enclose in quotes to avoid shell expansion)')
        parser_show_appends.add_argument('--mc', help='use specified multiconfig', default='')

        parser_show_cross_depends = self.add_command(sp, 'show-cross-depends', self.do_show_cross_depends)
        parser_show_cross_depends.add_argument('-f', '--filenames', help='show full file path', action='store_true')
        parser_show_cross_depends.add_argument('-i', '--ignore', help='ignore dependencies on items in the specified layer(s) (split multiple layer names with commas, no spaces)', metavar='LAYERNAME')
