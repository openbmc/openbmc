import argparse
import logging
import os

logger = logging.getLogger('bitbake-layers')


class LayerPlugin():
    def __init__(self):
        self.tinfoil = None
        self.bblayers = []

    def tinfoil_init(self, tinfoil):
        self.tinfoil = tinfoil
        self.bblayers = (self.tinfoil.config_data.getVar('BBLAYERS') or "").split()
        layerconfs = self.tinfoil.config_data.varhistory.get_variable_items_files('BBFILE_COLLECTIONS', self.tinfoil.config_data)
        self.bbfile_collections = {layer: os.path.dirname(os.path.dirname(path)) for layer, path in layerconfs.items()}

    @staticmethod
    def add_command(subparsers, cmdname, function, parserecipes=True, *args, **kwargs):
        """Convert docstring for function to help."""
        docsplit = function.__doc__.splitlines()
        help = docsplit[0]
        if len(docsplit) > 1:
            desc = '\n'.join(docsplit[1:])
        else:
            desc = help
        subparser = subparsers.add_parser(cmdname, *args, help=help, description=desc, formatter_class=argparse.RawTextHelpFormatter, **kwargs)
        subparser.set_defaults(func=function, parserecipes=parserecipes)
        return subparser

    def get_layer_name(self, layerdir):
        return os.path.basename(layerdir.rstrip(os.sep))
