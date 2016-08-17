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
    
class UtilityError(ShellError):
    """Raised upon utility syntax error (option or operand error)."""
    pass
   
class ExpansionError(ShellError):
    pass
     
class CommandNotFound(ShellError):
    """Specified command was not found."""
    pass
    
class RedirectionError(ShellError):
    pass
    
class VarAssignmentError(ShellError):
    """Variable assignment error."""
    pass
    
class ExitSignal(ShellError):
    """Exit signal."""
    pass
    
class ReturnSignal(ShellError):
    """Exit signal."""
    pass
