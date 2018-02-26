#
# BitBake Curses UI Implementation
#
# Implements an ncurses frontend for the BitBake utility.
#
# Copyright (C) 2006 Michael 'Mickey' Lauer
# Copyright (C) 2006-2007 Richard Purdie
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

"""
    We have the following windows:

        1.) Main Window: Shows what we are ultimately building and how far we are. Includes status bar
        2.) Thread Activity Window: Shows one status line for every concurrent bitbake thread.
        3.) Command Line Window: Contains an interactive command line where you can interact w/ Bitbake.

    Basic window layout is like that:

        |---------------------------------------------------------|
        | <Main Window>               | <Thread Activity Window>  |
        |                             | 0: foo do_compile complete|
        | Building Gtk+-2.6.10        | 1: bar do_patch complete  |
        | Status: 60%                 | ...                       |
        |                             | ...                       |
        |                             | ...                       |
        |---------------------------------------------------------|
        |<Command Line Window>                                    |
        |>>> which virtual/kernel                                 |
        |openzaurus-kernel                                        |
        |>>> _                                                    |
        |---------------------------------------------------------|

"""



import logging
import os, sys, itertools, time, subprocess

try:
    import curses
except ImportError:
    sys.exit("FATAL: The ncurses ui could not load the required curses python module.")

import bb
import xmlrpc.client
from bb import ui
from bb.ui import uihelper

parsespin = itertools.cycle( r'|/-\\' )

X = 0
Y = 1
WIDTH = 2
HEIGHT = 3

MAXSTATUSLENGTH = 32

class NCursesUI:
    """
    NCurses UI Class
    """
    class Window:
        """Base Window Class"""
        def __init__( self, x, y, width, height, fg=curses.COLOR_BLACK, bg=curses.COLOR_WHITE ):
            self.win = curses.newwin( height, width, y, x )
            self.dimensions = ( x, y, width, height )
            """
            if curses.has_colors():
                color = 1
                curses.init_pair( color, fg, bg )
                self.win.bkgdset( ord(' '), curses.color_pair(color) )
            else:
                self.win.bkgdset( ord(' '), curses.A_BOLD )
            """
            self.erase()
            self.setScrolling()
            self.win.noutrefresh()

        def erase( self ):
            self.win.erase()

        def setScrolling( self, b = True ):
            self.win.scrollok( b )
            self.win.idlok( b )

        def setBoxed( self ):
            self.boxed = True
            self.win.box()
            self.win.noutrefresh()

        def setText( self, x, y, text, *args ):
            self.win.addstr( y, x, text, *args )
            self.win.noutrefresh()

        def appendText( self, text, *args ):
            self.win.addstr( text, *args )
            self.win.noutrefresh()

        def drawHline( self, y ):
            self.win.hline( y, 0, curses.ACS_HLINE, self.dimensions[WIDTH] )
            self.win.noutrefresh()

    class DecoratedWindow( Window ):
        """Base class for windows with a box and a title bar"""
        def __init__( self, title, x, y, width, height, fg=curses.COLOR_BLACK, bg=curses.COLOR_WHITE ):
            NCursesUI.Window.__init__( self, x+1, y+3, width-2, height-4, fg, bg )
            self.decoration = NCursesUI.Window( x, y, width, height, fg, bg )
            self.decoration.setBoxed()
            self.decoration.win.hline( 2, 1, curses.ACS_HLINE, width-2 )
            self.setTitle( title )

        def setTitle( self, title ):
            self.decoration.setText( 1, 1, title.center( self.dimensions[WIDTH]-2 ), curses.A_BOLD )

    #-------------------------------------------------------------------------#
#    class TitleWindow( Window ):
    #-------------------------------------------------------------------------#
#        """Title Window"""
#        def __init__( self, x, y, width, height ):
#            NCursesUI.Window.__init__( self, x, y, width, height )
#            version = bb.__version__
#            title = "BitBake %s" % version
#            credit = "(C) 2003-2007 Team BitBake"
#            #self.win.hline( 2, 1, curses.ACS_HLINE, width-2 )
#            self.win.border()
#            self.setText( 1, 1, title.center( self.dimensions[WIDTH]-2 ), curses.A_BOLD )
#            self.setText( 1, 2, credit.center( self.dimensions[WIDTH]-2 ), curses.A_BOLD )

    #-------------------------------------------------------------------------#
    class ThreadActivityWindow( DecoratedWindow ):
    #-------------------------------------------------------------------------#
        """Thread Activity Window"""
        def __init__( self, x, y, width, height ):
            NCursesUI.DecoratedWindow.__init__( self, "Thread Activity", x, y, width, height )

        def setStatus( self, thread, text ):
            line = "%02d: %s" % ( thread, text )
            width = self.dimensions[WIDTH]
            if ( len(line) > width ):
                line = line[:width-3] + "..."
            else:
                line = line.ljust( width )
            self.setText( 0, thread, line )

    #-------------------------------------------------------------------------#
    class MainWindow( DecoratedWindow ):
    #-------------------------------------------------------------------------#
        """Main Window"""
        def __init__( self, x, y, width, height ):
            self.StatusPosition = width - MAXSTATUSLENGTH
            NCursesUI.DecoratedWindow.__init__( self, None, x, y, width, height )
            curses.nl()

        def setTitle( self, title ):
            title = "BitBake %s" % bb.__version__
            self.decoration.setText( 2, 1, title, curses.A_BOLD )
            self.decoration.setText( self.StatusPosition - 8, 1, "Status:", curses.A_BOLD )

        def setStatus(self, status):
            while len(status) < MAXSTATUSLENGTH:
                status = status + " "
            self.decoration.setText( self.StatusPosition, 1, status, curses.A_BOLD )


    #-------------------------------------------------------------------------#
    class ShellOutputWindow( DecoratedWindow ):
    #-------------------------------------------------------------------------#
        """Interactive Command Line Output"""
        def __init__( self, x, y, width, height ):
            NCursesUI.DecoratedWindow.__init__( self, "Command Line Window", x, y, width, height )

    #-------------------------------------------------------------------------#
    class ShellInputWindow( Window ):
    #-------------------------------------------------------------------------#
        """Interactive Command Line Input"""
        def __init__( self, x, y, width, height ):
            NCursesUI.Window.__init__( self, x, y, width, height )

# put that to the top again from curses.textpad import Textbox
#            self.textbox = Textbox( self.win )
#            t = threading.Thread()
#            t.run = self.textbox.edit
#            t.start()

    #-------------------------------------------------------------------------#
    def main(self, stdscr, server, eventHandler, params):
    #-------------------------------------------------------------------------#
        height, width = stdscr.getmaxyx()

        # for now split it like that:
        # MAIN_y + THREAD_y = 2/3 screen at the top
        # MAIN_x = 2/3 left, THREAD_y = 1/3 right
        # CLI_y = 1/3 of screen at the bottom
        # CLI_x = full

        main_left = 0
        main_top = 0
        main_height = ( height // 3 * 2 )
        main_width = ( width // 3 ) * 2
        clo_left = main_left
        clo_top = main_top + main_height
        clo_height = height - main_height - main_top - 1
        clo_width = width
        cli_left = main_left
        cli_top = clo_top + clo_height
        cli_height = 1
        cli_width = width
        thread_left = main_left + main_width
        thread_top = main_top
        thread_height = main_height
        thread_width = width - main_width

        #tw = self.TitleWindow( 0, 0, width, main_top )
        mw = self.MainWindow( main_left, main_top, main_width, main_height )
        taw = self.ThreadActivityWindow( thread_left, thread_top, thread_width, thread_height )
        clo = self.ShellOutputWindow( clo_left, clo_top, clo_width, clo_height )
        cli = self.ShellInputWindow( cli_left, cli_top, cli_width, cli_height )
        cli.setText( 0, 0, "BB>" )

        mw.setStatus("Idle")

        helper = uihelper.BBUIHelper()
        shutdown = 0

        try:
            params.updateFromServer(server)
            cmdline = params.parseActions()
            if not cmdline:
                print("Nothing to do.  Use 'bitbake world' to build everything, or run 'bitbake --help' for usage information.")
                return 1
            if 'msg' in cmdline and cmdline['msg']:
                logger.error(cmdline['msg'])
                return 1
            cmdline = cmdline['action']
            ret, error = server.runCommand(cmdline)
            if error:
                print("Error running command '%s': %s" % (cmdline, error))
                return
            elif ret != True:
                print("Couldn't get default commandlind! %s" % ret)
                return
        except xmlrpc.client.Fault as x:
            print("XMLRPC Fault getting commandline:\n %s" % x)
            return

        exitflag = False
        while not exitflag:
            try:
                event = eventHandler.waitEvent(0.25)
                if not event:
                    continue

                helper.eventHandler(event)
                if isinstance(event, bb.build.TaskBase):
                    mw.appendText("NOTE: %s\n" % event._message)
                if isinstance(event, logging.LogRecord):
                    mw.appendText(logging.getLevelName(event.levelno) + ': ' + event.getMessage() + '\n')

                if isinstance(event, bb.event.CacheLoadStarted):
                    self.parse_total = event.total
                if isinstance(event, bb.event.CacheLoadProgress):
                    x = event.current
                    y = self.parse_total
                    mw.setStatus("Loading Cache:   %s [%2d %%]" % ( next(parsespin), x*100/y ) )
                if isinstance(event, bb.event.CacheLoadCompleted):
                    mw.setStatus("Idle")
                    mw.appendText("Loaded %d entries from dependency cache.\n"
                                % ( event.num_entries))

                if isinstance(event, bb.event.ParseStarted):
                    self.parse_total = event.total
                if isinstance(event, bb.event.ParseProgress):
                    x = event.current
                    y = self.parse_total
                    mw.setStatus("Parsing Recipes: %s [%2d %%]" % ( next(parsespin), x*100/y ) )
                if isinstance(event, bb.event.ParseCompleted):
                    mw.setStatus("Idle")
                    mw.appendText("Parsing finished. %d cached, %d parsed, %d skipped, %d masked.\n"
                                % ( event.cached, event.parsed, event.skipped, event.masked ))

#                if isinstance(event, bb.build.TaskFailed):
#                    if event.logfile:
#                        if data.getVar("BBINCLUDELOGS", d):
#                            bb.error("log data follows (%s)" % logfile)
#                            number_of_lines = data.getVar("BBINCLUDELOGS_LINES", d)
#                            if number_of_lines:
#                                subprocess.check_call('tail -n%s %s' % (number_of_lines, logfile), shell=True)
#                            else:
#                                f = open(logfile, "r")
#                                while True:
#                                    l = f.readline()
#                                    if l == '':
#                                        break
#                                    l = l.rstrip()
#                                    print '| %s' % l
#                                f.close()
#                        else:
#                            bb.error("see log in %s" % logfile)

                if isinstance(event, bb.command.CommandCompleted):
                    # stop so the user can see the result of the build, but
                    # also allow them to now exit with a single ^C
                    shutdown = 2
                if isinstance(event, bb.command.CommandFailed):
                    mw.appendText(str(event))
                    time.sleep(2)
                    exitflag = True
                if isinstance(event, bb.command.CommandExit):
                    exitflag = True
                if isinstance(event, bb.cooker.CookerExit):
                    exitflag = True

                if isinstance(event, bb.event.LogExecTTY):
                    mw.appendText('WARN: ' + event.msg + '\n')
                if helper.needUpdate:
                    activetasks, failedtasks = helper.getTasks()
                    taw.erase()
                    taw.setText(0, 0, "")
                    if activetasks:
                        taw.appendText("Active Tasks:\n")
                        for task in activetasks.values():
                            taw.appendText(task["title"] + '\n')
                    if failedtasks:
                        taw.appendText("Failed Tasks:\n")
                        for task in failedtasks:
                            taw.appendText(task["title"] + '\n')

                curses.doupdate()
            except EnvironmentError as ioerror:
                # ignore interrupted io
                if ioerror.args[0] == 4:
                    pass

            except KeyboardInterrupt:
                if shutdown == 2:
                    mw.appendText("Third Keyboard Interrupt, exit.\n")
                    exitflag = True
                if shutdown == 1:
                    mw.appendText("Second Keyboard Interrupt, stopping...\n")
                    _, error = server.runCommand(["stateForceShutdown"])
                    if error:
                        print("Unable to cleanly stop: %s" % error)
                if shutdown == 0:
                    mw.appendText("Keyboard Interrupt, closing down...\n")
                    _, error = server.runCommand(["stateShutdown"])
                    if error:
                        print("Unable to cleanly shutdown: %s" % error)
                shutdown = shutdown + 1
                pass

def main(server, eventHandler, params):
    if not os.isatty(sys.stdout.fileno()):
        print("FATAL: Unable to run 'ncurses' UI without a TTY.")
        return
    ui = NCursesUI()
    try:
        curses.wrapper(ui.main, server, eventHandler, params)
    except:
        import traceback
        traceback.print_exc()
