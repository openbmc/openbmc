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

    def _read_repo_config(self, json_path):
        with open(json_path) as f:
            json_config = json.load(f)

        supported_versions = ["1.0"]
        if json_config["version"] not in supported_versions:
            err = "File {} has version {}, which is not in supported versions: {}".format(json_path, json_config["version"], supported_versions)
            logger.error(err)
            raise Exception(err)

        return json_config

    def _modify_repo_config(self, json_config, args):
        sources = json_config['sources']
        for pair in args.custom_references:
            try:
                repo, rev = pair.split(':', maxsplit=1)
            except ValueError:
                err = "Invalid custom reference specified: '{}'. Provide one using 'REPOSITORY:REFERENCE'.".format(pair)
                logger.error(err)
                raise Exception(err)
            if not repo in sources.keys():
                err = "Repository {} does not exist in setup-layers config".format(repo)
                logger.error(err)
                raise Exception(err)

            layer_remote = json_config['sources'][repo]['git-remote']
            layer_remote['rev'] = rev
            # Clear describe
            layer_remote['describe'] = ''

    def do_write(self, parent, args):
        """ Writes out a python script and a json config that replicate the directory structure and revisions of the layers in a current build. """
        output = args.output_prefix or "setup-layers"
        output = os.path.join(os.path.abspath(args.destdir), output)

        if args.update:
            # Modify existing layers setup
            if args.custom_references is None:
                err = "No custom reference specified. Please provide one using '--use-custom-reference REPOSITORY:REFERENCE'."
                logger.error(err)
                raise Exception(err)

            json = self._read_repo_config(output + ".json")
            if not 'sources' in json.keys():
                err = "File {}.json does not contain valid layer sources.".format(output)
                logger.error(err)
                raise Exception(err)

        else:
            # Create new layers setup
            if not os.path.exists(args.destdir):
                os.makedirs(args.destdir)
            repos = parent.make_repo_config(args.destdir)
            json = {"version":"1.0","sources":repos}
            if not repos:
                err = "Could not determine layer sources"
                logger.error(err)
                raise Exception(err)

        if args.custom_references is not None:
            self._modify_repo_config(json, args)

        self._write_json(json, output + ".json")
        logger.info('Created {}.json'.format(output))
        if not args.json_only:
            self._write_python(os.path.join(os.path.dirname(__file__),'../../../../scripts/oe-setup-layers'), output)
            logger.info('Created {}'.format(output))

    def register_arguments(self, parser):
        parser.add_argument('--json-only', action='store_true',
            help='When using the oe-setup-layers writer, write only the layer configuruation in json format. Otherwise, also a copy of scripts/oe-setup-layers (from oe-core or poky) is provided, which is a self contained python script that fetches all the needed layers and sets them to correct revisions using the data from the json.')

        parser.add_argument('--update', '-u',
            action='store_true',
            help=("Instead of writing a new json file, update an existing layer setup json file with custom references provided via the '--use-custom-reference' option."
                  "\nThis will only update repositories for which a custom reference is specified, all other repositores will be left unchanged."))
        parser.add_argument('--use-custom-reference', '-r',
            action='append',
            dest='custom_references',
            metavar='REPOSITORY:REFERENCE',
            help=("A pair consisting of a repository and a custom reference to use for it (by default the currently checked out commit id would be written out)."
                  "\nThis value can be any reference that 'git checkout' would accept, and is not checked for validity."
                  "\nThis option can be used multiple times."))
