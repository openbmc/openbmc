# sherrors.py - shell errors and signals
#
# Copyright 2007 Patrick Mezard
#
# This software may be used and distributed according to the terms
# of the GNU General Public License, incorporated herein by reference.

"""Define shell exceptions and error codes.
"""

class ShellError(Exception):
    pass

class ShellSyntaxError(ShellError):
    pass
