#
# SPDX-License-Identifier: MIT
#
# Enable other layers to have modules in the same named directory
from pkgutil import extend_path
__path__ = extend_path(__path__, __name__)

# Borrowed from CalledProcessError

class CommandError(Exception):
    def __init__(self, retcode, cmd, output = None):
        self.retcode = retcode
        self.cmd = cmd
        self.output = output
    def __str__(self):
        return "Command '%s' returned non-zero exit status %d with output: %s" % (self.cmd, self.retcode, self.output)

def avoid_paths_in_environ(paths):
    """
        Searches for every path in os.environ['PATH']
        if found remove it.

        Returns new PATH without avoided PATHs.
    """
    import os

    new_path = ''
    for p in os.environ['PATH'].split(':'):
       avoid = False
       for pa in paths:
           if pa in p:
              avoid = True
              break
       if avoid:
           continue

       new_path = new_path + p + ':'

    new_path = new_path[:-1]
    return new_path

def make_logger_bitbake_compatible(logger):
    import logging

    """ 
    We need to raise the log level of the info output so unittest 
    messages are visible on the console.
    """
    def _bitbake_log_info(msg, *args, **kwargs):
        logger.log(logging.INFO + 1, msg, *args, **kwargs)

    logger.info = _bitbake_log_info

    return logger

def load_test_components(logger, executor):
    import sys
    import os
    import importlib

    from oeqa.core.context import OETestContextExecutor

    components = {}

    for path in sys.path:
        base_dir = os.path.join(path, 'oeqa')
        if os.path.exists(base_dir) and os.path.isdir(base_dir):
            for file in os.listdir(base_dir):
                comp_name = file
                comp_context = os.path.join(base_dir, file, 'context.py')
                if os.path.exists(comp_context):
                    comp_plugin = importlib.import_module('oeqa.%s.%s' % \
                            (comp_name, 'context'))
                    try:
                        if not issubclass(comp_plugin._executor_class,
                                OETestContextExecutor):
                            raise TypeError("Component %s in %s, _executor_class "\
                                "isn't derived from OETestContextExecutor."\
                                % (comp_name, comp_context))

                        if comp_plugin._executor_class._script_executor \
                                != executor:
                            continue

                        components[comp_name] = comp_plugin._executor_class()
                    except AttributeError:
                        raise AttributeError("Component %s in %s don't have "\
                                "_executor_class defined." % (comp_name, comp_context))

    return components
