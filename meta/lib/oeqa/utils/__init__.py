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
