import logging
import os
import sys
import shutil

import bb.utils

from bblayers.common import LayerPlugin

logger = logging.getLogger('bitbake-layers')

def plugin_init(plugins):
    return CreatePlugin()

def read_template(template, template_dir='templates'):
    lines = str()
    with open(os.path.join(os.path.dirname(__file__), template_dir, template)) as fd:
        lines = ''.join(fd.readlines())
    return lines

class CreatePlugin(LayerPlugin):
    def do_create_layer(self, args):
        """Create a basic layer"""
        layerdir = os.path.abspath(args.layerdir)
        if os.path.exists(layerdir):
            sys.stderr.write("Specified layer directory exists\n")
            return 1

        # create dirs
        conf = os.path.join(layerdir, 'conf')
        bb.utils.mkdirhier(conf)

        layername = os.path.basename(os.path.normpath(args.layerdir))

        # Create the README from templates/README
        readme_template =  read_template('README').format(layername=layername)
        readme = os.path.join(layerdir, 'README')
        with open(readme, 'w') as fd:
            fd.write(readme_template)

        # Copy the MIT license from meta
        copying = 'COPYING.MIT'
        dn = os.path.dirname
        license_src = os.path.join(dn(dn(dn(__file__))), copying)
        license_dst = os.path.join(layerdir, copying)
        shutil.copy(license_src, license_dst)

        # Get the compat value for core layer.
        compat = self.tinfoil.config_data.getVar('LAYERSERIES_COMPAT_core') or ""

        # Create the layer.conf from templates/layer.conf
        layerconf_template = read_template('layer.conf').format(
                layername=layername, priority=args.priority, compat=compat)
        layerconf = os.path.join(conf, 'layer.conf')
        with open(layerconf, 'w') as fd:
            fd.write(layerconf_template)

        # Create the example from templates/example.bb
        example_template = read_template('example.bb')
        example = os.path.join(layerdir, 'recipes-' + args.examplerecipe, args.examplerecipe)
        bb.utils.mkdirhier(example)
        with open(os.path.join(example, args.examplerecipe + '_%s.bb') % args.version, 'w') as fd:
            fd.write(example_template)

        logger.plain('Add your new layer with \'bitbake-layers add-layer %s\'' % args.layerdir)

    def register_commands(self, sp):
        parser_create_layer = self.add_command(sp, 'create-layer', self.do_create_layer, parserecipes=False)
        parser_create_layer.add_argument('layerdir', help='Layer directory to create')
        parser_create_layer.add_argument('--priority', '-p', default=6, help='Layer directory to create')
        parser_create_layer.add_argument('--example-recipe-name', '-e', dest='examplerecipe', default='example', help='Filename of the example recipe')
        parser_create_layer.add_argument('--example-recipe-version', '-v', dest='version', default='0.1', help='Version number for the example recipe')

