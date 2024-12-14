#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os
import sys
import os.path

import bb.utils

from bblayers.common import LayerPlugin

logger = logging.getLogger('bitbake-config-layers')

sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

def plugin_init(plugins):
    return ConfigFragmentsPlugin()

class ConfigFragmentsPlugin(LayerPlugin):
    def get_fragment_info(self, path, name):
        d = bb.data.init()
        bb.parse.handle(path, d, True)
        summary = d.getVar('BB_CONF_FRAGMENT_SUMMARY')
        description = d.getVar('BB_CONF_FRAGMENT_DESCRIPTION')
        if not summary:
            raise Exception('Please add a one-line summary as BB_CONF_FRAGMENT_SUMMARY = \"...\" variable at the beginning of {}'.format(path))

        if not description:
            raise Exception('Please add a description as BB_CONF_FRAGMENT_DESCRIPTION = \"...\" variable at the beginning of {}'.format(path))

        return summary, description

    def discover_fragments(self):
        fragments_path_prefix = self.tinfoil.config_data.getVar('OE_FRAGMENTS_PREFIX')
        allfragments = {}
        for layername in self.bbfile_collections:
             layerdir = self.bbfile_collections[layername]
             fragments = []
             for topdir, dirs, files in os.walk(os.path.join(layerdir, fragments_path_prefix)):
                 fragmentdir = os.path.relpath(topdir, os.path.join(layerdir, fragments_path_prefix))
                 for fragmentfile in sorted(files):
                     fragmentname = os.path.normpath("/".join((layername, fragmentdir, fragmentfile.split('.')[0])))
                     fragmentpath = os.path.join(topdir, fragmentfile)
                     fragmentsummary, fragmentdesc = self.get_fragment_info(fragmentpath, fragmentname)
                     fragments.append({'path':fragmentpath, 'name':fragmentname, 'summary':fragmentsummary, 'description':fragmentdesc})
             if fragments:
                 allfragments[layername] = {'layerdir':layerdir,'fragments':fragments}
        return allfragments

    def do_list_fragments(self, args):
        """ List available configuration fragments """
        def print_fragment(f, verbose, is_enabled):
            if not verbose:
                print('{}\t{}'.format(f['name'], f['summary']))
            else:
                print('Name: {}\nPath: {}\nEnabled: {}\nSummary: {}\nDescription:\n{}\n'.format(f['name'], f['path'], 'yes' if is_enabled else 'no', f['summary'],''.join(f['description'])))

        all_enabled_fragments = (self.tinfoil.config_data.getVar('OE_FRAGMENTS') or "").split()

        for layername, layerdata in self.discover_fragments().items():
            layerdir = layerdata['layerdir']
            fragments = layerdata['fragments']
            enabled_fragments = [f for f in fragments if f['name'] in all_enabled_fragments]
            disabled_fragments = [f for f in fragments if f['name'] not in all_enabled_fragments]

            print('Available fragments in {} layer located in {}:\n'.format(layername, layerdir))
            if enabled_fragments:
                print('Enabled fragments:')
                for f in enabled_fragments:
                    print_fragment(f, args.verbose, is_enabled=True)
                print('')
            if disabled_fragments:
                print('Unused fragments:')
                for f in disabled_fragments:
                    print_fragment(f, args.verbose, is_enabled=False)
            print('')

    def fragment_exists(self, fragmentname):
        for layername, layerdata in self.discover_fragments().items():
            for f in layerdata['fragments']:
              if f['name'] == fragmentname:
                  return True
        return False

    def create_conf(self, confpath):
        if not os.path.exists(confpath):
            with open(confpath, 'w') as f:
                f.write('')
        with open(confpath, 'r') as f:
            lines = f.read()
        if "OE_FRAGMENTS += " not in lines:
            lines += "\nOE_FRAGMENTS += \"\"\n"
        with open(confpath, 'w') as f:
            f.write(lines)

    def do_enable_fragment(self, args):
        """ Enable a fragment in the local build configuration """
        def enable_helper(varname, origvalue, op, newlines):
            enabled_fragments = origvalue.split()
            if args.fragmentname in enabled_fragments:
                print("Fragment {} already included in {}".format(args.fragmentname, args.confpath))
            else:
                enabled_fragments.append(args.fragmentname)
            return " ".join(enabled_fragments), None, 0, True

        if not self.fragment_exists(args.fragmentname):
            raise Exception("Fragment {} does not exist; use 'list-fragments' to see the full list.".format(args.fragmentname))

        self.create_conf(args.confpath)
        modified = bb.utils.edit_metadata_file(args.confpath, ["OE_FRAGMENTS"], enable_helper)
        if modified:
            print("Fragment {} added to {}.".format(args.fragmentname, args.confpath))

    def do_disable_fragment(self, args):
        """ Disable a fragment in the local build configuration """
        def disable_helper(varname, origvalue, op, newlines):
            enabled_fragments = origvalue.split()
            if args.fragmentname in enabled_fragments:
                enabled_fragments.remove(args.fragmentname)
            else:
                print("Fragment {} not currently enabled in {}".format(args.fragmentname, args.confpath))
            return " ".join(enabled_fragments), None, 0, True

        self.create_conf(args.confpath)
        modified = bb.utils.edit_metadata_file(args.confpath, ["OE_FRAGMENTS"], disable_helper)
        if modified:
            print("Fragment {} removed from {}.".format(args.fragmentname, args.confpath))

    def do_disable_all_fragments(self, args):
        """ Disable all fragments in the local build configuration """
        def disable_all_helper(varname, origvalue, op, newlines):
            return "", None, 0, True

        self.create_conf(args.confpath)
        modified = bb.utils.edit_metadata_file(args.confpath, ["OE_FRAGMENTS"], disable_all_helper)
        if modified:
            print("All fragments removed from {}.".format(args.confpath))

    def register_commands(self, sp):
        default_confpath = os.path.join(os.environ["BBPATH"], "conf/auto.conf")

        parser_list_fragments = self.add_command(sp, 'list-fragments', self.do_list_fragments, parserecipes=False)
        parser_list_fragments.add_argument("--confpath", default=default_confpath, help='Configuration file which contains a list of enabled fragments (default is {}).'.format(default_confpath))
        parser_list_fragments.add_argument('--verbose', '-v', action='store_true', help='Print extended descriptions of the fragments')

        parser_enable_fragment = self.add_command(sp, 'enable-fragment', self.do_enable_fragment, parserecipes=False)
        parser_enable_fragment.add_argument("--confpath", default=default_confpath, help='Configuration file which contains a list of enabled fragments (default is {}).'.format(default_confpath))
        parser_enable_fragment.add_argument('fragmentname', help='The name of the fragment (use list-fragments to see them)')

        parser_disable_fragment = self.add_command(sp, 'disable-fragment', self.do_disable_fragment, parserecipes=False)
        parser_disable_fragment.add_argument("--confpath", default=default_confpath, help='Configuration file which contains a list of enabled fragments (default is {}).'.format(default_confpath))
        parser_disable_fragment.add_argument('fragmentname', help='The name of the fragment')

        parser_disable_all = self.add_command(sp, 'disable-all-fragments', self.do_disable_all_fragments, parserecipes=False)
        parser_disable_all.add_argument("--confpath", default=default_confpath, help='Configuration file which contains a list of enabled fragments (default is {}).'.format(default_confpath))
