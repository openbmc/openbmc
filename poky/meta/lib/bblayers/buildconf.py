#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os
import sys

from bblayers.common import LayerPlugin

logger = logging.getLogger('bitbake-layers')

sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

import oe.buildcfg

def plugin_init(plugins):
    return BuildConfPlugin()

class BuildConfPlugin(LayerPlugin):
    notes_fixme = """FIXME: Please place here the detailed instructions for using this build configuration.
They will be shown to the users when they set up their builds via TEMPLATECONF.
"""
    summary_fixme = """FIXME: Please place here the short summary of what this build configuration is for.
It will be shown to the users when they set up their builds via TEMPLATECONF.
"""

    def _save_conf(self, templatename, templatepath, oecorepath, relpaths_to_oecore):
        confdir = os.path.join(os.environ["BBPATH"], "conf")
        destdir = os.path.join(templatepath, "conf", "templates", templatename)
        os.makedirs(destdir, exist_ok=True)

        with open(os.path.join(confdir, "local.conf")) as src:
            with open(os.path.join(destdir, "local.conf.sample"), 'w') as dest:
                dest.write(src.read())

        with open(os.path.join(confdir, "bblayers.conf")) as src:
            with open(os.path.join(destdir, "bblayers.conf.sample"), 'w') as dest:
                bblayers_data = src.read()

                for (abspath, relpath) in relpaths_to_oecore:
                    bblayers_data = bblayers_data.replace(abspath, "##OEROOT##/" + relpath)
                dest.write(bblayers_data)

        with open(os.path.join(destdir, "conf-summary.txt"), 'w') as dest:
            dest.write(self.summary_fixme)
        with open(os.path.join(destdir, "conf-notes.txt"), 'w') as dest:
            dest.write(self.notes_fixme)

        logger.info("""Configuration template placed into {}
Please review the files in there, and particularly provide a configuration summary in {}
and notes in {}
You can try out the configuration with
TEMPLATECONF={} . {}/oe-init-build-env build-try-{}"""
.format(destdir, os.path.join(destdir, "conf-summary.txt"), os.path.join(destdir, "conf-notes.txt"), destdir, oecorepath, templatename))

    def do_save_build_conf(self, args):
        """ Save the currently active build configuration (conf/local.conf, conf/bblayers.conf) as a template into a layer.\n This template can later be used for setting up builds via TEMPLATECONF. """
        layers = oe.buildcfg.get_layer_revisions(self.tinfoil.config_data)
        targetlayer = None
        oecore = None

        for l in layers:
            if os.path.abspath(l[0]) == os.path.abspath(args.layerpath):
                targetlayer = l[0]
            if l[1] == 'meta':
                oecore = os.path.dirname(l[0])

        if not targetlayer:
            logger.error("Layer {} not in one of the currently enabled layers:\n{}".format(args.layerpath, "\n".join([l[0] for l in layers])))
        elif not oecore:
            logger.error("Openembedded-core not in one of the currently enabled layers:\n{}".format("\n".join([l[0] for l in layers])))
        else:
            relpaths_to_oecore = [(l[0], os.path.relpath(l[0], start=oecore)) for l in layers]
            self._save_conf(args.templatename, targetlayer, oecore, relpaths_to_oecore)

    def register_commands(self, sp):
        parser_build_conf = self.add_command(sp, 'save-build-conf', self.do_save_build_conf, parserecipes=False)
        parser_build_conf.add_argument('layerpath',
            help='The path to the layer where the configuration template should be saved.')
        parser_build_conf.add_argument('templatename',
            help='The name of the configuration template.')
