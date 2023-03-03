#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os
import json
import stat

logger = logging.getLogger('bitbake-layers')

def plugin_init(plugins):
    return OeSetupLayersWriter()

class OeSetupLayersWriter():

    def __str__(self):
        return "oe-setup-layers"

    def _write_python(self, input, output):
        with open(input) as f:
            script = f.read()
        with open(output, 'w') as f:
            f.write(script)
        st = os.stat(output)
        os.chmod(output, st.st_mode | stat.S_IEXEC | stat.S_IXGRP | stat.S_IXOTH)

    def _write_json(self, repos, output):
        with open(output, 'w') as f:
            json.dump(repos, f, sort_keys=True, indent=4)

    def do_write(self, parent, args):
        """ Writes out a python script and a json config that replicate the directory structure and revisions of the layers in a current build. """
        if not os.path.exists(args.destdir):
            os.makedirs(args.destdir)
        repos = parent.make_repo_config(args.destdir)
        json = {"version":"1.0","sources":repos}
        if not repos:
            raise Exception("Could not determine layer sources")
        output = args.output_prefix or "setup-layers"
        output = os.path.join(os.path.abspath(args.destdir),output)
        self._write_json(json, output + ".json")
        logger.info('Created {}.json'.format(output))
        if not args.json_only:
            self._write_python(os.path.join(os.path.dirname(__file__),'../../../../scripts/oe-setup-layers'), output)
        logger.info('Created {}'.format(output))

    def register_arguments(self, parser):
        parser.add_argument('--json-only', action='store_true',
            help='When using the oe-setup-layers writer, write only the layer configuruation in json format. Otherwise, also a copy of scripts/oe-setup-layers (from oe-core or poky) is provided, which is a self contained python script that fetches all the needed layers and sets them to correct revisions using the data from the json.')
