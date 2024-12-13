#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import pathlib

from bblayers.common import LayerPlugin

logger = logging.getLogger('bitbake-layers')

def plugin_init(plugins):
    return ShowMachinesPlugin()

class ShowMachinesPlugin(LayerPlugin):
    def do_show_machines(self, args):
        """List the machines available in the currently configured layers."""

        for layer_dir in self.bblayers:
            layer_name = self.get_layer_name(layer_dir)

            if args.layer and args.layer != layer_name:
                continue

            for p in sorted(pathlib.Path(layer_dir).glob("conf/machine/*.conf")):
                if args.bare:
                    logger.plain("%s" % (p.stem))
                else:
                    logger.plain("%s (%s)" % (p.stem, layer_name))


    def register_commands(self, sp):
        parser_show_machines = self.add_command(sp, "show-machines", self.do_show_machines)
        parser_show_machines.add_argument('-b', '--bare', help='output just the machine names, not the source layer', action='store_true')
        parser_show_machines.add_argument('-l', '--layer', help='Limit to machines in the specified layer')
