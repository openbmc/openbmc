import argparse

already_loaded = False
register_count = 0

def plugin_name(filename):
    return os.path.splitext(os.path.basename(filename))[0]

def plugin_init(plugins):
    global already_loaded
    already_loaded = plugin_name(__file__) in (plugin_name(p.__name__) for p in plugins)

def print_name(opts):
    print (__file__)

def print_bbdir(opts):
    print (__file__.replace('/lib/recipetool/bbpath.py',''))

def print_registered(opts):
    #global kept_context
    #print(kept_context.loaded)
    print ("1")

def multiloaded(opts):
    global already_loaded
    print("yes" if already_loaded else "no")

def register_commands(subparsers):
    global register_count
    register_count += 1

    def addparser(name, helptxt, func):
        parser = subparsers.add_parser(name, help=helptxt,
                                       formatter_class=argparse.ArgumentDefaultsHelpFormatter)
        parser.set_defaults(func=func)
        return parser

    addparser('pluginfile', 'Print the filename of this plugin', print_name)
    addparser('bbdir', 'Print the BBPATH directory of this plugin', print_bbdir)
    addparser('count', 'How many times have this plugin been registered.', print_registered)
    addparser('multiloaded', 'How many times have this plugin been initialized', multiloaded)
