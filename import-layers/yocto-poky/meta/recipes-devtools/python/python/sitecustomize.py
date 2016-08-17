# OpenEmbedded sitecustomize.py (C) 2002-2008 Michael 'Mickey' Lauer <mlauer@vanille-media.de>
# GPLv2 or later
# Version: 20081123
# Features:
# * set proper default encoding
# * enable readline completion in the interactive interpreter
# * load command line history on startup
# * save command line history on exit 

import os

def __exithandler():
    try:
        readline.write_history_file( "%s/.python-history" % os.getenv( "HOME", "/tmp" ) )
    except IOError:
        pass

def __registerExitHandler():
    import atexit
    atexit.register( __exithandler )

def __enableReadlineSupport():
    readline.set_history_length( 1000 )
    readline.parse_and_bind( "tab: complete" )
    try:
        readline.read_history_file( "%s/.python-history" % os.getenv( "HOME", "/tmp" ) )
    except IOError:
        pass

def __enableDefaultEncoding():
    import sys
    try:
        sys.setdefaultencoding( "utf8" )
    except LookupError:
        pass

import sys
try:
    import rlcompleter, readline
except ImportError:
    pass
else:
    __enableDefaultEncoding()
    __registerExitHandler()
    __enableReadlineSupport()
