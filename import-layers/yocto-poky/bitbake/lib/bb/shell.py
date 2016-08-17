# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
##########################################################################
#
# Copyright (C) 2005-2006 Michael 'Mickey' Lauer <mickey@Vanille.de>
# Copyright (C) 2005-2006 Vanille Media
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
##########################################################################
#
# Thanks to:
# * Holger Freyther <zecke@handhelds.org>
# * Justin Patrin <papercrane@reversefold.com>
#
##########################################################################

"""
BitBake Shell

IDEAS:
    * list defined tasks per package
    * list classes
    * toggle force
    * command to reparse just one (or more) bbfile(s)
    * automatic check if reparsing is necessary (inotify?)
    * frontend for bb file manipulation
    * more shell-like features:
        - output control, i.e. pipe output into grep, sort, etc.
        - job control, i.e. bring running commands into background and foreground
    * start parsing in background right after startup
    * ncurses interface

PROBLEMS:
    * force doesn't always work
    * readline completion for commands with more than one parameters

"""

##########################################################################
# Import and setup global variables
##########################################################################

from __future__ import print_function
from functools import reduce
try:
    set
except NameError:
    from sets import Set as set
import sys, os, readline, socket, httplib, urllib, commands, popen2, shlex, Queue, fnmatch
from bb import data, parse, build, cache, taskdata, runqueue, providers as Providers

__version__ = "0.5.3.1"
__credits__ = """BitBake Shell Version %s (C) 2005 Michael 'Mickey' Lauer <mickey@Vanille.de>
Type 'help' for more information, press CTRL-D to exit.""" % __version__

cmds = {}
leave_mainloop = False
last_exception = None
cooker = None
parsed = False
debug = os.environ.get( "BBSHELL_DEBUG", "" )

##########################################################################
# Class BitBakeShellCommands
##########################################################################

class BitBakeShellCommands:
    """This class contains the valid commands for the shell"""

    def __init__( self, shell ):
        """Register all the commands"""
        self._shell = shell
        for attr in BitBakeShellCommands.__dict__:
            if not attr.startswith( "_" ):
                if attr.endswith( "_" ):
                    command = attr[:-1].lower()
                else:
                    command = attr[:].lower()
                method = getattr( BitBakeShellCommands, attr )
                debugOut( "registering command '%s'" % command )
                # scan number of arguments
                usage = getattr( method, "usage", "" )
                if usage != "<...>":
                    numArgs = len( usage.split() )
                else:
                    numArgs = -1
                shell.registerCommand( command, method, numArgs, "%s %s" % ( command, usage ), method.__doc__ )

    def _checkParsed( self ):
        if not parsed:
            print("SHELL: This command needs to parse bbfiles...")
            self.parse( None )

    def _findProvider( self, item ):
        self._checkParsed()
        # Need to use taskData for this information
        preferred = data.getVar( "PREFERRED_PROVIDER_%s" % item, cooker.configuration.data, 1 )
        if not preferred: preferred = item
        try:
            lv, lf, pv, pf = Providers.findBestProvider(preferred, cooker.configuration.data, cooker.status)
        except KeyError:
            if item in cooker.status.providers:
                pf = cooker.status.providers[item][0]
            else:
                pf = None
        return pf

    def alias( self, params ):
        """Register a new name for a command"""
        new, old = params
        if not old in cmds:
            print("ERROR: Command '%s' not known" % old)
        else:
            cmds[new] = cmds[old]
            print("OK")
    alias.usage = "<alias> <command>"

    def buffer( self, params ):
        """Dump specified output buffer"""
        index = params[0]
        print(self._shell.myout.buffer( int( index ) ))
    buffer.usage = "<index>"

    def buffers( self, params ):
        """Show the available output buffers"""
        commands = self._shell.myout.bufferedCommands()
        if not commands:
            print("SHELL: No buffered commands available yet. Start doing something.")
        else:
            print("="*35, "Available Output Buffers", "="*27)
            for index, cmd in enumerate( commands ):
                print("| %s %s" % ( str( index ).ljust( 3 ), cmd ))
            print("="*88)

    def build( self, params, cmd = "build" ):
        """Build a providee"""
        global last_exception
        globexpr = params[0]
        self._checkParsed()
        names = globfilter( cooker.status.pkg_pn, globexpr )
        if len( names ) == 0: names = [ globexpr ]
        print("SHELL: Building %s" % ' '.join( names ))

        td = taskdata.TaskData(cooker.configuration.abort)
        localdata = data.createCopy(cooker.configuration.data)
        data.update_data(localdata)
        data.expandKeys(localdata)

        try:
            tasks = []
            for name in names:
                td.add_provider(localdata, cooker.status, name)
                providers = td.get_provider(name)

                if len(providers) == 0:
                    raise Providers.NoProvider

                tasks.append([name, "do_%s" % cmd])

            td.add_unresolved(localdata, cooker.status)

            rq = runqueue.RunQueue(cooker, localdata, cooker.status, td, tasks)
            rq.prepare_runqueue()
            rq.execute_runqueue()

        except Providers.NoProvider:
            print("ERROR: No Provider")
            last_exception = Providers.NoProvider

        except runqueue.TaskFailure as fnids:
            last_exception = runqueue.TaskFailure

        except build.FuncFailed as e:
            print("ERROR: Couldn't build '%s'" % names)
            last_exception = e


    build.usage = "<providee>"

    def clean( self, params ):
        """Clean a providee"""
        self.build( params, "clean" )
    clean.usage = "<providee>"

    def compile( self, params ):
        """Execute 'compile' on a providee"""
        self.build( params, "compile" )
    compile.usage = "<providee>"

    def configure( self, params ):
        """Execute 'configure' on a providee"""
        self.build( params, "configure" )
    configure.usage = "<providee>"

    def install( self, params ):
        """Execute 'install' on a providee"""
        self.build( params, "install" )
    install.usage = "<providee>"

    def edit( self, params ):
        """Call $EDITOR on a providee"""
        name = params[0]
        bbfile = self._findProvider( name )
        if bbfile is not None:
            os.system( "%s %s" % ( os.environ.get( "EDITOR", "vi" ), bbfile ) )
        else:
            print("ERROR: Nothing provides '%s'" % name)
    edit.usage = "<providee>"

    def environment( self, params ):
        """Dump out the outer BitBake environment"""
        cooker.showEnvironment()

    def exit_( self, params ):
        """Leave the BitBake Shell"""
        debugOut( "setting leave_mainloop to true" )
        global leave_mainloop
        leave_mainloop = True

    def fetch( self, params ):
        """Fetch a providee"""
        self.build( params, "fetch" )
    fetch.usage = "<providee>"

    def fileBuild( self, params, cmd = "build" ):
        """Parse and build a .bb file"""
        global last_exception
        name = params[0]
        bf = completeFilePath( name )
        print("SHELL: Calling '%s' on '%s'" % ( cmd, bf ))

        try:
            cooker.buildFile(bf, cmd)
        except parse.ParseError:
            print("ERROR: Unable to open or parse '%s'" % bf)
        except build.FuncFailed as e:
            print("ERROR: Couldn't build '%s'" % name)
            last_exception = e

    fileBuild.usage = "<bbfile>"

    def fileClean( self, params ):
        """Clean a .bb file"""
        self.fileBuild( params, "clean" )
    fileClean.usage = "<bbfile>"

    def fileEdit( self, params ):
        """Call $EDITOR on a .bb file"""
        name = params[0]
        os.system( "%s %s" % ( os.environ.get( "EDITOR", "vi" ), completeFilePath( name ) ) )
    fileEdit.usage = "<bbfile>"

    def fileRebuild( self, params ):
        """Rebuild (clean & build) a .bb file"""
        self.fileBuild( params, "rebuild" )
    fileRebuild.usage = "<bbfile>"

    def fileReparse( self, params ):
        """(re)Parse a bb file"""
        bbfile = params[0]
        print("SHELL: Parsing '%s'" % bbfile)
        parse.update_mtime( bbfile )
        cooker.parser.reparse(bbfile)
        if False: #fromCache:
            print("SHELL: File has not been updated, not reparsing")
        else:
            print("SHELL: Parsed")
    fileReparse.usage = "<bbfile>"

    def abort( self, params ):
        """Toggle abort task execution flag (see bitbake -k)"""
        cooker.configuration.abort = not cooker.configuration.abort
        print("SHELL: Abort Flag is now '%s'" % repr( cooker.configuration.abort ))

    def force( self, params ):
        """Toggle force task execution flag (see bitbake -f)"""
        cooker.configuration.force = not cooker.configuration.force
        print("SHELL: Force Flag is now '%s'" % repr( cooker.configuration.force ))

    def help( self, params ):
        """Show a comprehensive list of commands and their purpose"""
        print("="*30, "Available Commands", "="*30)
        for cmd in sorted(cmds):
            function, numparams, usage, helptext = cmds[cmd]
            print("| %s | %s" % (usage.ljust(30), helptext))
        print("="*78)

    def lastError( self, params ):
        """Show the reason or log that was produced by the last BitBake event exception"""
        if last_exception is None:
            print("SHELL: No Errors yet (Phew)...")
        else:
            reason, event = last_exception.args
            print("SHELL: Reason for the last error: '%s'" % reason)
            if ':' in reason:
                msg, filename = reason.split( ':' )
                filename = filename.strip()
                print("SHELL: Dumping log file for last error:")
                try:
                    print(open( filename ).read())
                except IOError:
                    print("ERROR: Couldn't open '%s'" % filename)

    def match( self, params ):
        """Dump all files or providers matching a glob expression"""
        what, globexpr = params
        if what == "files":
            self._checkParsed()
            for key in globfilter( cooker.status.pkg_fn, globexpr ): print(key)
        elif what == "providers":
            self._checkParsed()
            for key in globfilter( cooker.status.pkg_pn, globexpr ): print(key)
        else:
            print("Usage: match %s" % self.print_.usage)
    match.usage = "<files|providers> <glob>"

    def new( self, params ):
        """Create a new .bb file and open the editor"""
        dirname, filename = params
        packages = '/'.join( data.getVar( "BBFILES", cooker.configuration.data, 1 ).split('/')[:-2] )
        fulldirname = "%s/%s" % ( packages, dirname )

        if not os.path.exists( fulldirname ):
            print("SHELL: Creating '%s'" % fulldirname)
            os.mkdir( fulldirname )
        if os.path.exists( fulldirname ) and os.path.isdir( fulldirname ):
            if os.path.exists( "%s/%s" % ( fulldirname, filename ) ):
                print("SHELL: ERROR: %s/%s already exists" % ( fulldirname, filename ))
                return False
            print("SHELL: Creating '%s/%s'" % ( fulldirname, filename ))
            newpackage = open( "%s/%s" % ( fulldirname, filename ), "w" )
            print("""DESCRIPTION = ""
SECTION = ""
AUTHOR = ""
HOMEPAGE = ""
MAINTAINER = ""
LICENSE = "GPL"
PR = "r0"

SRC_URI = ""

#inherit base

#do_configure() {
#
#}

#do_compile() {
#
#}

#do_stage() {
#
#}

#do_install() {
#
#}
""", file=newpackage)
            newpackage.close()
            os.system( "%s %s/%s" % ( os.environ.get( "EDITOR" ), fulldirname, filename ) )
    new.usage = "<directory> <filename>"

    def package( self, params ):
        """Execute 'package' on a providee"""
        self.build( params, "package" )
    package.usage = "<providee>"

    def pasteBin( self, params ):
        """Send a command + output buffer to the pastebin at http://rafb.net/paste"""
        index = params[0]
        contents = self._shell.myout.buffer( int( index ) )
        sendToPastebin( "output of " + params[0], contents )
    pasteBin.usage = "<index>"

    def pasteLog( self, params ):
        """Send the last event exception error log (if there is one) to http://rafb.net/paste"""
        if last_exception is None:
            print("SHELL: No Errors yet (Phew)...")
        else:
            reason, event = last_exception.args
            print("SHELL: Reason for the last error: '%s'" % reason)
            if ':' in reason:
                msg, filename = reason.split( ':' )
                filename = filename.strip()
                print("SHELL: Pasting log file to pastebin...")

                file = open( filename ).read()
                sendToPastebin( "contents of " + filename, file )

    def patch( self, params ):
        """Execute 'patch' command on a providee"""
        self.build( params, "patch" )
    patch.usage = "<providee>"

    def parse( self, params ):
        """(Re-)parse .bb files and calculate the dependency graph"""
        cooker.status = cache.CacheData(cooker.caches_array)
        ignore = data.getVar("ASSUME_PROVIDED", cooker.configuration.data, 1) or ""
        cooker.status.ignored_dependencies = set( ignore.split() )
        cooker.handleCollections( data.getVar("BBFILE_COLLECTIONS", cooker.configuration.data, 1) )

        (filelist, masked) = cooker.collect_bbfiles()
        cooker.parse_bbfiles(filelist, masked, cooker.myProgressCallback)
        cooker.buildDepgraph()
        global parsed
        parsed = True
        print()

    def reparse( self, params ):
        """(re)Parse a providee's bb file"""
        bbfile = self._findProvider( params[0] )
        if bbfile is not None:
            print("SHELL: Found bbfile '%s' for '%s'" % ( bbfile, params[0] ))
            self.fileReparse( [ bbfile ] )
        else:
            print("ERROR: Nothing provides '%s'" % params[0])
    reparse.usage = "<providee>"

    def getvar( self, params ):
        """Dump the contents of an outer BitBake environment variable"""
        var = params[0]
        value = data.getVar( var, cooker.configuration.data, 1 )
        print(value)
    getvar.usage = "<variable>"

    def peek( self, params ):
        """Dump contents of variable defined in providee's metadata"""
        name, var = params
        bbfile = self._findProvider( name )
        if bbfile is not None:
            the_data = cache.Cache.loadDataFull(bbfile, cooker.configuration.data)
            value = the_data.getVar( var, 1 )
            print(value)
        else:
            print("ERROR: Nothing provides '%s'" % name)
    peek.usage = "<providee> <variable>"

    def poke( self, params ):
        """Set contents of variable defined in providee's metadata"""
        name, var, value = params
        bbfile = self._findProvider( name )
        if bbfile is not None:
            print("ERROR: Sorry, this functionality is currently broken")
            #d = cooker.pkgdata[bbfile]
            #data.setVar( var, value, d )

            # mark the change semi persistant
            #cooker.pkgdata.setDirty(bbfile, d)
            #print "OK"
        else:
            print("ERROR: Nothing provides '%s'" % name)
    poke.usage = "<providee> <variable> <value>"

    def print_( self, params ):
        """Dump all files or providers"""
        what = params[0]
        if what == "files":
            self._checkParsed()
            for key in cooker.status.pkg_fn: print(key)
        elif what == "providers":
            self._checkParsed()
            for key in cooker.status.providers: print(key)
        else:
            print("Usage: print %s" % self.print_.usage)
    print_.usage = "<files|providers>"

    def python( self, params ):
        """Enter the expert mode - an interactive BitBake Python Interpreter"""
        sys.ps1 = "EXPERT BB>>> "
        sys.ps2 = "EXPERT BB... "
        import code
        interpreter = code.InteractiveConsole( dict( globals() ) )
        interpreter.interact( "SHELL: Expert Mode - BitBake Python %s\nType 'help' for more information, press CTRL-D to switch back to BBSHELL." % sys.version )

    def showdata( self, params ):
        """Execute 'showdata' on a providee"""
        cooker.showEnvironment(None, params)
    showdata.usage = "<providee>"

    def setVar( self, params ):
        """Set an outer BitBake environment variable"""
        var, value = params
        data.setVar( var, value, cooker.configuration.data )
        print("OK")
    setVar.usage = "<variable> <value>"

    def rebuild( self, params ):
        """Clean and rebuild a .bb file or a providee"""
        self.build( params, "clean" )
        self.build( params, "build" )
    rebuild.usage = "<providee>"

    def shell( self, params ):
        """Execute a shell command and dump the output"""
        if params != "":
            print(commands.getoutput( " ".join( params ) ))
    shell.usage = "<...>"

    def stage( self, params ):
        """Execute 'stage' on a providee"""
        self.build( params, "populate_staging" )
    stage.usage = "<providee>"

    def status( self, params ):
        """<just for testing>"""
        print("-" * 78)
        print("building list = '%s'" % cooker.building_list)
        print("build path = '%s'" % cooker.build_path)
        print("consider_msgs_cache = '%s'" % cooker.consider_msgs_cache)
        print("build stats = '%s'" % cooker.stats)
        if last_exception is not None: print("last_exception = '%s'" % repr( last_exception.args ))
        print("memory output contents = '%s'" % self._shell.myout._buffer)

    def test( self, params ):
        """<just for testing>"""
        print("testCommand called with '%s'" % params)

    def unpack( self, params ):
        """Execute 'unpack' on a providee"""
        self.build( params, "unpack" )
    unpack.usage = "<providee>"

    def which( self, params ):
        """Computes the providers for a given providee"""
        # Need to use taskData for this information
        item = params[0]

        self._checkParsed()

        preferred = data.getVar( "PREFERRED_PROVIDER_%s" % item, cooker.configuration.data, 1 )
        if not preferred: preferred = item

        try:
            lv, lf, pv, pf = Providers.findBestProvider(preferred, cooker.configuration.data, cooker.status)
        except KeyError:
            lv, lf, pv, pf = (None,)*4

        try:
            providers = cooker.status.providers[item]
        except KeyError:
            print("SHELL: ERROR: Nothing provides", preferred)
        else:
            for provider in providers:
                if provider == pf: provider = " (***) %s" % provider
                else:              provider = "       %s" % provider
                print(provider)
    which.usage = "<providee>"

##########################################################################
# Common helper functions
##########################################################################

def completeFilePath( bbfile ):
    """Get the complete bbfile path"""
    if not cooker.status: return bbfile
    if not cooker.status.pkg_fn: return bbfile
    for key in cooker.status.pkg_fn:
        if key.endswith( bbfile ):
            return key
    return bbfile

def sendToPastebin( desc, content ):
    """Send content to http://oe.pastebin.com"""
    mydata = {}
    mydata["lang"] = "Plain Text"
    mydata["desc"] = desc
    mydata["cvt_tabs"] = "No"
    mydata["nick"] = "%s@%s" % ( os.environ.get( "USER", "unknown" ), socket.gethostname() or "unknown" )
    mydata["text"] = content
    params = urllib.urlencode( mydata )
    headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}

    host = "rafb.net"
    conn = httplib.HTTPConnection( "%s:80" % host )
    conn.request("POST", "/paste/paste.php", params, headers )

    response = conn.getresponse()
    conn.close()

    if response.status == 302:
        location = response.getheader( "location" ) or "unknown"
        print("SHELL: Pasted to http://%s%s" % ( host, location ))
    else:
        print("ERROR: %s %s" % ( response.status, response.reason ))

def completer( text, state ):
    """Return a possible readline completion"""
    debugOut( "completer called with text='%s', state='%d'" % ( text, state ) )

    if state == 0:
        line = readline.get_line_buffer()
        if " " in line:
            line = line.split()
            # we are in second (or more) argument
            if line[0] in cmds and hasattr( cmds[line[0]][0], "usage" ): # known command and usage
                u = getattr( cmds[line[0]][0], "usage" ).split()[0]
                if u == "<variable>":
                    allmatches = cooker.configuration.data.keys()
                elif u == "<bbfile>":
                    if cooker.status.pkg_fn is None: allmatches = [ "(No Matches Available. Parsed yet?)" ]
                    else: allmatches = [ x.split("/")[-1] for x in cooker.status.pkg_fn ]
                elif u == "<providee>":
                    if cooker.status.pkg_fn is None: allmatches = [ "(No Matches Available. Parsed yet?)" ]
                    else: allmatches = cooker.status.providers.iterkeys()
                else: allmatches = [ "(No tab completion available for this command)" ]
            else: allmatches = [ "(No tab completion available for this command)" ]
        else:
            # we are in first argument
            allmatches = cmds.iterkeys()

        completer.matches = [ x for x in allmatches if x[:len(text)] == text ]
        #print "completer.matches = '%s'" % completer.matches
    if len( completer.matches ) > state:
        return completer.matches[state]
    else:
        return None

def debugOut( text ):
    if debug:
        sys.stderr.write( "( %s )\n" % text )

def columnize( alist, width = 80 ):
    """
    A word-wrap function that preserves existing line breaks
    and most spaces in the text. Expects that existing line
    breaks are posix newlines (\n).
    """
    return reduce(lambda line, word, width=width: '%s%s%s' %
                  (line,
                   ' \n'[(len(line[line.rfind('\n')+1:])
                         + len(word.split('\n', 1)[0]
                              ) >= width)],
                   word),
                  alist
                 )

def globfilter( names, pattern ):
    return fnmatch.filter( names, pattern )

##########################################################################
# Class MemoryOutput
##########################################################################

class MemoryOutput:
    """File-like output class buffering the output of the last 10 commands"""
    def __init__( self, delegate ):
        self.delegate = delegate
        self._buffer = []
        self.text = []
        self._command = None

    def startCommand( self, command ):
        self._command = command
        self.text = []
    def endCommand( self ):
        if self._command is not None:
            if len( self._buffer ) == 10: del self._buffer[0]
            self._buffer.append( ( self._command, self.text ) )
    def removeLast( self ):
        if self._buffer:
            del self._buffer[ len( self._buffer ) - 1 ]
        self.text = []
        self._command = None
    def lastBuffer( self ):
        if self._buffer:
            return self._buffer[ len( self._buffer ) -1 ][1]
    def bufferedCommands( self ):
        return [ cmd for cmd, output in self._buffer ]
    def buffer( self, i ):
        if i < len( self._buffer ):
            return "BB>> %s\n%s" % ( self._buffer[i][0], "".join( self._buffer[i][1] ) )
        else: return "ERROR: Invalid buffer number. Buffer needs to be in (0, %d)" % ( len( self._buffer ) - 1 )
    def write( self, text ):
        if self._command is not None and text != "BB>> ": self.text.append( text )
        if self.delegate is not None: self.delegate.write( text )
    def flush( self ):
        return self.delegate.flush()
    def fileno( self ):
        return self.delegate.fileno()
    def isatty( self ):
        return self.delegate.isatty()

##########################################################################
# Class BitBakeShell
##########################################################################

class BitBakeShell:

    def __init__( self ):
        """Register commands and set up readline"""
        self.commandQ = Queue.Queue()
        self.commands = BitBakeShellCommands( self )
        self.myout = MemoryOutput( sys.stdout )
        self.historyfilename = os.path.expanduser( "~/.bbsh_history" )
        self.startupfilename = os.path.expanduser( "~/.bbsh_startup" )

        readline.set_completer( completer )
        readline.set_completer_delims( " " )
        readline.parse_and_bind("tab: complete")

        try:
            readline.read_history_file( self.historyfilename )
        except IOError:
            pass  # It doesn't exist yet.

        print(__credits__)

    def cleanup( self ):
        """Write readline history and clean up resources"""
        debugOut( "writing command history" )
        try:
            readline.write_history_file( self.historyfilename )
        except:
            print("SHELL: Unable to save command history")

    def registerCommand( self, command, function, numparams = 0, usage = "", helptext = "" ):
        """Register a command"""
        if usage == "": usage = command
        if helptext == "": helptext = function.__doc__ or "<not yet documented>"
        cmds[command] = ( function, numparams, usage, helptext )

    def processCommand( self, command, params ):
        """Process a command. Check number of params and print a usage string, if appropriate"""
        debugOut( "processing command '%s'..." % command )
        try:
            function, numparams, usage, helptext = cmds[command]
        except KeyError:
            print("SHELL: ERROR: '%s' command is not a valid command." % command)
            self.myout.removeLast()
        else:
            if (numparams != -1) and (not len( params ) == numparams):
                print("Usage: '%s'" % usage)
                return

            result = function( self.commands, params )
            debugOut( "result was '%s'" % result )

    def processStartupFile( self ):
        """Read and execute all commands found in $HOME/.bbsh_startup"""
        if os.path.exists( self.startupfilename ):
            startupfile = open( self.startupfilename, "r" )
            for cmdline in startupfile:
                debugOut( "processing startup line '%s'" % cmdline )
                if not cmdline:
                    continue
                if "|" in cmdline:
                    print("ERROR: '|' in startup file is not allowed. Ignoring line")
                    continue
                self.commandQ.put( cmdline.strip() )

    def main( self ):
        """The main command loop"""
        while not leave_mainloop:
            try:
                if self.commandQ.empty():
                    sys.stdout = self.myout.delegate
                    cmdline = raw_input( "BB>> " )
                    sys.stdout = self.myout
                else:
                    cmdline = self.commandQ.get()
                if cmdline:
                    allCommands = cmdline.split( ';' )
                    for command in allCommands:
                        pipecmd = None
                        #
                        # special case for expert mode
                        if command == 'python':
                            sys.stdout = self.myout.delegate
                            self.processCommand( command, "" )
                            sys.stdout = self.myout
                        else:
                            self.myout.startCommand( command )
                            if '|' in command: # disable output
                                command, pipecmd = command.split( '|' )
                                delegate = self.myout.delegate
                                self.myout.delegate = None
                            tokens = shlex.split( command, True )
                            self.processCommand( tokens[0], tokens[1:] or "" )
                            self.myout.endCommand()
                            if pipecmd is not None: # restore output
                                self.myout.delegate = delegate

                                pipe = popen2.Popen4( pipecmd )
                                pipe.tochild.write( "\n".join( self.myout.lastBuffer() ) )
                                pipe.tochild.close()
                                sys.stdout.write( pipe.fromchild.read() )
                        #
            except EOFError:
                print()
                return
            except KeyboardInterrupt:
                print()

##########################################################################
# Start function - called from the BitBake command line utility
##########################################################################

def start( aCooker ):
    global cooker
    cooker = aCooker
    bbshell = BitBakeShell()
    bbshell.processStartupFile()
    bbshell.main()
    bbshell.cleanup()

if __name__ == "__main__":
    print("SHELL: Sorry, this program should only be called by BitBake.")
