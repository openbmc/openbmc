import argparse

already_loaded = False
kept_context = None

def plugin_name(filename):
    return os.path.splitext(os.path.basename(filename))[0]

def plugin_init(plugins):
    global already_loaded
    already_loaded = plugin_name(__file__) in (plugin_name(p.__name__) for p in plugins)

def print_name(args, config, basepath, workspace):
    print (__file__)

def print_bbdir(args, config, basepath, workspace):
    print (__file__.replace('/lib/devtool/bbpath.py',''))

def print_registered(args, config, basepath, workspace):
    global kept_context
    print(kept_context.loaded)

def multiloaded(args, config, basepath, workspace):
    global already_loaded
    print("yes" if already_loaded else "no")

def register_commands(subparsers, context):
    global kept_context
    kept_context = context
    if 'loaded' in context.__dict__:
        context.loaded += 1
    else:
        context.loaded = 1

    def addparser(name, helptxt, func):
        parser = subparsers.add_parser(name, help=helptxt,
                                       formatter_class=argparse.ArgumentDefaultsHelpFormatter)
        parser.set_defaults(func=func)
        return parser

    addparser('pluginfile', 'Print the filename of this plugin', print_name)
    addparser('bbdir', 'Print the BBPATH directory of this plugin', print_bbdir)
    addparser('count', 'How many times have this plugin been registered.', print_registered)
    addparser('multiloaded', 'How many times have this plugin been initialized', multiloaded)
