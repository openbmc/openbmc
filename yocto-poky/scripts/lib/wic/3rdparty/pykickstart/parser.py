#
# parser.py:  Kickstart file parser.
#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2005, 2006, 2007, 2008, 2011 Red Hat, Inc.
#
# This copyrighted material is made available to anyone wishing to use, modify,
# copy, or redistribute it subject to the terms and conditions of the GNU
# General Public License v.2.  This program is distributed in the hope that it
# will be useful, but WITHOUT ANY WARRANTY expressed or implied, including the
# implied warranties of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with
# this program; if not, write to the Free Software Foundation, Inc., 51
# Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  Any Red Hat
# trademarks that are incorporated in the source code or documentation are not
# subject to the GNU General Public License and may only be used or replicated
# with the express permission of Red Hat, Inc. 
#
"""
Main kickstart file processing module.

This module exports several important classes:

    Script - Representation of a single %pre, %post, or %traceback script.

    Packages - Representation of the %packages section.

    KickstartParser - The kickstart file parser state machine.
"""

from collections import Iterator
import os
import shlex
import sys
import tempfile
from copy import copy
from optparse import *

import constants
from errors import KickstartError, KickstartParseError, KickstartValueError, formatErrorMsg
from ko import KickstartObject
from sections import *
import version

import gettext
_ = lambda x: gettext.ldgettext("pykickstart", x)

STATE_END = "end"
STATE_COMMANDS = "commands"

ver = version.DEVEL


class PutBackIterator(Iterator):
    def __init__(self, iterable):
        self._iterable = iter(iterable)
        self._buf = None

    def __iter__(self):
        return self

    def put(self, s):
        self._buf = s

    def next(self):
        if self._buf:
            retval = self._buf
            self._buf = None
            return retval
        else:
            return self._iterable.next()

###
### SCRIPT HANDLING
###
class Script(KickstartObject):
    """A class representing a single kickstart script.  If functionality beyond
       just a data representation is needed (for example, a run method in
       anaconda), Script may be subclassed.  Although a run method is not
       provided, most of the attributes of Script have to do with running the
       script.  Instances of Script are held in a list by the Version object.
    """
    def __init__(self, script, *args , **kwargs):
        """Create a new Script instance.  Instance attributes:

           errorOnFail -- If execution of the script fails, should anaconda
                          stop, display an error, and then reboot without
                          running any other scripts?
           inChroot    -- Does the script execute in anaconda's chroot
                          environment or not?
           interp      -- The program that should be used to interpret this
                          script.
           lineno      -- The line number this script starts on.
           logfile     -- Where all messages from the script should be logged.
           script      -- A string containing all the lines of the script.
           type        -- The type of the script, which can be KS_SCRIPT_* from
                          pykickstart.constants.
        """
        KickstartObject.__init__(self, *args, **kwargs)
        self.script = "".join(script)

        self.interp = kwargs.get("interp", "/bin/sh")
        self.inChroot = kwargs.get("inChroot", False)
        self.lineno = kwargs.get("lineno", None)
        self.logfile = kwargs.get("logfile", None)
        self.errorOnFail = kwargs.get("errorOnFail", False)
        self.type = kwargs.get("type", constants.KS_SCRIPT_PRE)

    def __str__(self):
        """Return a string formatted for output to a kickstart file."""
        retval = ""

        if self.type == constants.KS_SCRIPT_PRE:
            retval += '\n%pre'
        elif self.type == constants.KS_SCRIPT_POST:
            retval += '\n%post'
        elif self.type == constants.KS_SCRIPT_TRACEBACK:
            retval += '\n%traceback'

        if self.interp != "/bin/sh" and self.interp != "":
            retval += " --interpreter=%s" % self.interp
        if self.type == constants.KS_SCRIPT_POST and not self.inChroot:
            retval += " --nochroot"
        if self.logfile != None:
            retval += " --logfile %s" % self.logfile
        if self.errorOnFail:
            retval += " --erroronfail"

        if self.script.endswith("\n"):
            if ver >= version.F8:
                return retval + "\n%s%%end\n" % self.script
            else:
                return retval + "\n%s\n" % self.script
        else:
            if ver >= version.F8:
                return retval + "\n%s\n%%end\n" % self.script
            else:
                return retval + "\n%s\n" % self.script


##
## PACKAGE HANDLING
##
class Group:
    """A class representing a single group in the %packages section."""
    def __init__(self, name="", include=constants.GROUP_DEFAULT):
        """Create a new Group instance.  Instance attributes:

           name    -- The group's identifier
           include -- The level of how much of the group should be included.
                      Values can be GROUP_* from pykickstart.constants.
        """
        self.name = name
        self.include = include

    def __str__(self):
        """Return a string formatted for output to a kickstart file."""
        if self.include == constants.GROUP_REQUIRED:
            return "@%s --nodefaults" % self.name
        elif self.include == constants.GROUP_ALL:
            return "@%s --optional" % self.name
        else:
            return "@%s" % self.name

    def __cmp__(self, other):
        if self.name < other.name:
            return -1
        elif self.name > other.name:
            return 1
        return 0

class Packages(KickstartObject):
    """A class representing the %packages section of the kickstart file."""
    def __init__(self, *args, **kwargs):
        """Create a new Packages instance.  Instance attributes:

           addBase       -- Should the Base group be installed even if it is
                            not specified?
           default       -- Should the default package set be selected?
           excludedList  -- A list of all the packages marked for exclusion in
                            the %packages section, without the leading minus
                            symbol.
           excludeDocs   -- Should documentation in each package be excluded?
           groupList     -- A list of Group objects representing all the groups
                            specified in the %packages section.  Names will be
                            stripped of the leading @ symbol.
           excludedGroupList -- A list of Group objects representing all the
                                groups specified for removal in the %packages
                                section.  Names will be stripped of the leading
                                -@ symbols.
           handleMissing -- If unknown packages are specified in the %packages
                            section, should it be ignored or not?  Values can
                            be KS_MISSING_* from pykickstart.constants.
           packageList   -- A list of all the packages specified in the
                            %packages section.
           instLangs     -- A list of languages to install.
        """
        KickstartObject.__init__(self, *args, **kwargs)

        self.addBase = True
        self.default = False
        self.excludedList = []
        self.excludedGroupList = []
        self.excludeDocs = False
        self.groupList = []
        self.handleMissing = constants.KS_MISSING_PROMPT
        self.packageList = []
        self.instLangs = None

    def __str__(self):
        """Return a string formatted for output to a kickstart file."""
        pkgs = ""

        if not self.default:
            grps = self.groupList
            grps.sort()
            for grp in grps:
                pkgs += "%s\n" % grp.__str__()

            p = self.packageList
            p.sort()
            for pkg in p:
                pkgs += "%s\n" % pkg

            grps = self.excludedGroupList
            grps.sort()
            for grp in grps:
                pkgs += "-%s\n" % grp.__str__()

            p = self.excludedList
            p.sort()
            for pkg in p:
                pkgs += "-%s\n" % pkg

            if pkgs == "":
                return ""

        retval = "\n%packages"

        if self.default:
            retval += " --default"
        if self.excludeDocs:
            retval += " --excludedocs"
        if not self.addBase:
            retval += " --nobase"
        if self.handleMissing == constants.KS_MISSING_IGNORE:
            retval += " --ignoremissing"
        if self.instLangs:
            retval += " --instLangs=%s" % self.instLangs

        if ver >= version.F8:
            return retval + "\n" + pkgs + "\n%end\n"
        else:
            return retval + "\n" + pkgs + "\n"

    def _processGroup (self, line):
        op = OptionParser()
        op.add_option("--nodefaults", action="store_true", default=False)
        op.add_option("--optional", action="store_true", default=False)

        (opts, extra) = op.parse_args(args=line.split())

        if opts.nodefaults and opts.optional:
            raise KickstartValueError, _("Group cannot specify both --nodefaults and --optional")

        # If the group name has spaces in it, we have to put it back together
        # now.
        grp = " ".join(extra)

        if opts.nodefaults:
            self.groupList.append(Group(name=grp, include=constants.GROUP_REQUIRED))
        elif opts.optional:
            self.groupList.append(Group(name=grp, include=constants.GROUP_ALL))
        else:
            self.groupList.append(Group(name=grp, include=constants.GROUP_DEFAULT))

    def add (self, pkgList):
        """Given a list of lines from the input file, strip off any leading
           symbols and add the result to the appropriate list.
        """
        existingExcludedSet = set(self.excludedList)
        existingPackageSet = set(self.packageList)
        newExcludedSet = set()
        newPackageSet = set()

        excludedGroupList = []

        for pkg in pkgList:
            stripped = pkg.strip()

            if stripped[0] == "@":
                self._processGroup(stripped[1:])
            elif stripped[0] == "-":
                if stripped[1] == "@":
                    excludedGroupList.append(Group(name=stripped[2:]))
                else:
                    newExcludedSet.add(stripped[1:])
            else:
                newPackageSet.add(stripped)

        # Groups have to be excluded in two different ways (note: can't use
        # sets here because we have to store objects):
        excludedGroupNames = map(lambda g: g.name, excludedGroupList)

        # First, an excluded group may be cancelling out a previously given
        # one.  This is often the case when using %include.  So there we should
        # just remove the group from the list.
        self.groupList = filter(lambda g: g.name not in excludedGroupNames, self.groupList)

        # Second, the package list could have included globs which are not
        # processed by pykickstart.  In that case we need to preserve a list of
        # excluded groups so whatever tool doing package/group installation can
        # take appropriate action.
        self.excludedGroupList.extend(excludedGroupList)

        existingPackageSet = (existingPackageSet - newExcludedSet) | newPackageSet
        existingExcludedSet = (existingExcludedSet - existingPackageSet) | newExcludedSet

        self.packageList = list(existingPackageSet)
        self.excludedList = list(existingExcludedSet)


###
### PARSER
###
class KickstartParser:
    """The kickstart file parser class as represented by a basic state
       machine.  To create a specialized parser, make a subclass and override
       any of the methods you care about.  Methods that don't need to do
       anything may just pass.  However, _stateMachine should never be
       overridden.
    """
    def __init__ (self, handler, followIncludes=True, errorsAreFatal=True,
                  missingIncludeIsFatal=True):
        """Create a new KickstartParser instance.  Instance attributes:

           errorsAreFatal        -- Should errors cause processing to halt, or
                                    just print a message to the screen?  This
                                    is most useful for writing syntax checkers
                                    that may want to continue after an error is
                                    encountered.
           followIncludes        -- If %include is seen, should the included
                                    file be checked as well or skipped?
           handler               -- An instance of a BaseHandler subclass.  If
                                    None, the input file will still be parsed
                                    but no data will be saved and no commands
                                    will be executed.
           missingIncludeIsFatal -- Should missing include files be fatal, even
                                    if errorsAreFatal is False?
        """
        self.errorsAreFatal = errorsAreFatal
        self.followIncludes = followIncludes
        self.handler = handler
        self.currentdir = {}
        self.missingIncludeIsFatal = missingIncludeIsFatal

        self._state = STATE_COMMANDS
        self._includeDepth = 0
        self._line = ""

        self.version = self.handler.version

        global ver
        ver = self.version

        self._sections = {}
        self.setupSections()

    def _reset(self):
        """Reset the internal variables of the state machine for a new kickstart file."""
        self._state = STATE_COMMANDS
        self._includeDepth = 0

    def getSection(self, s):
        """Return a reference to the requested section (s must start with '%'s),
           or raise KeyError if not found.
        """
        return self._sections[s]

    def handleCommand (self, lineno, args):
        """Given the list of command and arguments, call the Version's
           dispatcher method to handle the command.  Returns the command or
           data object returned by the dispatcher.  This method may be
           overridden in a subclass if necessary.
        """
        if self.handler:
            self.handler.currentCmd = args[0]
            self.handler.currentLine = self._line
            retval = self.handler.dispatcher(args, lineno)

            return retval

    def registerSection(self, obj):
        """Given an instance of a Section subclass, register the new section
           with the parser.  Calling this method means the parser will
           recognize your new section and dispatch into the given object to
           handle it.
        """
        if not obj.sectionOpen:
            raise TypeError, "no sectionOpen given for section %s" % obj

        if not obj.sectionOpen.startswith("%"):
            raise TypeError, "section %s tag does not start with a %%" % obj.sectionOpen

        self._sections[obj.sectionOpen] = obj

    def _finalize(self, obj):
        """Called at the close of a kickstart section to take any required
           actions.  Internally, this is used to add scripts once we have the
           whole body read.
        """
        obj.finalize()
        self._state = STATE_COMMANDS

    def _handleSpecialComments(self, line):
        """Kickstart recognizes a couple special comments."""
        if self._state != STATE_COMMANDS:
            return

        # Save the platform for s-c-kickstart.
        if line[:10] == "#platform=":
            self.handler.platform = self._line[11:]

    def _readSection(self, lineIter, lineno):
        obj = self._sections[self._state]

        while True:
            try:
                line = lineIter.next()
                if line == "":
                    # This section ends at the end of the file.
                    if self.version >= version.F8:
                        raise KickstartParseError, formatErrorMsg(lineno, msg=_("Section does not end with %%end."))

                    self._finalize(obj)
            except StopIteration:
                break

            lineno += 1

            # Throw away blank lines and comments, unless the section wants all
            # lines.
            if self._isBlankOrComment(line) and not obj.allLines:
                continue

            if line.startswith("%"):
                args = shlex.split(line)

                if args and args[0] == "%end":
                    # This is a properly terminated section.
                    self._finalize(obj)
                    break
                elif args and args[0] == "%ksappend":
                    continue
                elif args and (self._validState(args[0]) or args[0] in ["%include", "%ksappend"]):
                    # This is an unterminated section.
                    if self.version >= version.F8:
                        raise KickstartParseError, formatErrorMsg(lineno, msg=_("Section does not end with %%end."))

                    # Finish up.  We do not process the header here because
                    # kicking back out to STATE_COMMANDS will ensure that happens.
                    lineIter.put(line)
                    lineno -= 1
                    self._finalize(obj)
                    break
            else:
                # This is just a line within a section.  Pass it off to whatever
                # section handles it.
                obj.handleLine(line)

        return lineno

    def _validState(self, st):
        """Is the given section tag one that has been registered with the parser?"""
        return st in self._sections.keys()

    def _tryFunc(self, fn):
        """Call the provided function (which doesn't take any arguments) and
           do the appropriate error handling.  If errorsAreFatal is False, this
           function will just print the exception and keep going.
        """
        try:
            fn()
        except Exception, msg:
            if self.errorsAreFatal:
                raise
            else:
                print msg

    def _isBlankOrComment(self, line):
        return line.isspace() or line == "" or line.lstrip()[0] == '#'

    def _stateMachine(self, lineIter):
        # For error reporting.
        lineno = 0

        while True:
            # Get the next line out of the file, quitting if this is the last line.
            try:
                self._line = lineIter.next()
                if self._line == "":
                    break
            except StopIteration:
                break

            lineno += 1

            # Eliminate blank lines, whitespace-only lines, and comments.
            if self._isBlankOrComment(self._line):
                self._handleSpecialComments(self._line)
                continue

            # Remove any end-of-line comments.
            sanitized = self._line.split("#")[0]

            # Then split the line.
            args = shlex.split(sanitized.rstrip())

            if args[0] == "%include":
                # This case comes up primarily in ksvalidator.
                if not self.followIncludes:
                    continue

                if len(args) == 1 or not args[1]:
                    raise KickstartParseError, formatErrorMsg(lineno)

                self._includeDepth += 1

                try:
                    self.readKickstart(args[1], reset=False)
                except KickstartError:
                    # Handle the include file being provided over the
                    # network in a %pre script.  This case comes up in the
                    # early parsing in anaconda.
                    if self.missingIncludeIsFatal:
                        raise

                self._includeDepth -= 1
                continue

            # Now on to the main event.
            if self._state == STATE_COMMANDS:
                if args[0] == "%ksappend":
                    # This is handled by the preprocess* functions, so continue.
                    continue
                elif args[0][0] == '%':
                    # This is the beginning of a new section.  Handle its header
                    # here.
                    newSection = args[0]
                    if not self._validState(newSection):
                        raise KickstartParseError, formatErrorMsg(lineno, msg=_("Unknown kickstart section: %s" % newSection))

                    self._state = newSection
                    obj = self._sections[self._state]
                    self._tryFunc(lambda: obj.handleHeader(lineno, args))

                    # This will handle all section processing, kicking us back
                    # out to STATE_COMMANDS at the end with the current line
                    # being the next section header, etc.
                    lineno = self._readSection(lineIter, lineno)
                else:
                    # This is a command in the command section.  Dispatch to it.
                    self._tryFunc(lambda: self.handleCommand(lineno, args))
            elif self._state == STATE_END:
                break

    def readKickstartFromString (self, s, reset=True):
        """Process a kickstart file, provided as the string str."""
        if reset:
            self._reset()

        # Add a "" to the end of the list so the string reader acts like the
        # file reader and we only get StopIteration when we're after the final
        # line of input.
        i = PutBackIterator(s.splitlines(True) + [""])
        self._stateMachine (i)

    def readKickstart(self, f, reset=True):
        """Process a kickstart file, given by the filename f."""
        if reset:
            self._reset()

        # an %include might not specify a full path.  if we don't try to figure
        # out what the path should have been, then we're unable to find it
        # requiring full path specification, though, sucks.  so let's make
        # the reading "smart" by keeping track of what the path is at each
        # include depth.
        if not os.path.exists(f):
            if self.currentdir.has_key(self._includeDepth - 1):
                if os.path.exists(os.path.join(self.currentdir[self._includeDepth - 1], f)):
                    f = os.path.join(self.currentdir[self._includeDepth - 1], f)

        cd = os.path.dirname(f)
        if not cd.startswith("/"):
            cd = os.path.abspath(cd)
        self.currentdir[self._includeDepth] = cd

        try:
            s = file(f).read()
        except IOError, e:
            raise KickstartError, formatErrorMsg(0, msg=_("Unable to open input kickstart file: %s") % e.strerror)

        self.readKickstartFromString(s, reset=False)

    def setupSections(self):
        """Install the sections all kickstart files support.  You may override
           this method in a subclass, but should avoid doing so unless you know
           what you're doing.
        """
        self._sections = {}

        # Install the sections all kickstart files support.
        self.registerSection(PreScriptSection(self.handler, dataObj=Script))
        self.registerSection(PostScriptSection(self.handler, dataObj=Script))
        self.registerSection(TracebackScriptSection(self.handler, dataObj=Script))
        self.registerSection(PackageSection(self.handler))
