#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2006, 2007, 2008 Red Hat, Inc.
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
Base classes for creating commands and syntax version object.

This module exports several important base classes:

    BaseData - The base abstract class for all data objects.  Data objects
               are contained within a BaseHandler object.

    BaseHandler - The base abstract class from which versioned kickstart
                  handler are derived.  Subclasses of BaseHandler hold
                  BaseData and KickstartCommand objects.

    DeprecatedCommand - An abstract subclass of KickstartCommand that should
                        be further subclassed by users of this module.  When
                        a subclass is used, a warning message will be
                        printed.

    KickstartCommand - The base abstract class for all kickstart commands.
                       Command objects are contained within a BaseHandler
                       object.
"""
import gettext
gettext.textdomain("pykickstart")
_ = lambda x: gettext.ldgettext("pykickstart", x)

import types
import warnings
from pykickstart.errors import *
from pykickstart.ko import *
from pykickstart.parser import Packages
from pykickstart.version import versionToString

###
### COMMANDS
###
class KickstartCommand(KickstartObject):
    """The base class for all kickstart commands.  This is an abstract class."""
    removedKeywords = []
    removedAttrs = []

    def __init__(self, writePriority=0, *args, **kwargs):
        """Create a new KickstartCommand instance.  This method must be
           provided by all subclasses, but subclasses must call
           KickstartCommand.__init__ first.  Instance attributes:

           currentCmd    -- The name of the command in the input file that
                            caused this handler to be run.
           currentLine   -- The current unprocessed line from the input file
                            that caused this handler to be run.
           handler       -- A reference to the BaseHandler subclass this
                            command is contained withing.  This is needed to
                            allow referencing of Data objects.
           lineno        -- The current line number in the input file.
           writePriority -- An integer specifying when this command should be
                            printed when iterating over all commands' __str__
                            methods.  The higher the number, the later this
                            command will be written.  All commands with the
                            same priority will be written alphabetically.
        """

        # We don't want people using this class by itself.
        if self.__class__ is KickstartCommand:
            raise TypeError, "KickstartCommand is an abstract class."

        KickstartObject.__init__(self, *args, **kwargs)

        self.writePriority = writePriority

        # These will be set by the dispatcher.
        self.currentCmd = ""
        self.currentLine = ""
        self.handler = None
        self.lineno = 0

        # If a subclass provides a removedKeywords list, remove all the
        # members from the kwargs list before we start processing it.  This
        # ensures that subclasses don't continue to recognize arguments that
        # were removed.
        for arg in filter(kwargs.has_key, self.removedKeywords):
            kwargs.pop(arg)

    def __call__(self, *args, **kwargs):
        """Set multiple attributes on a subclass of KickstartCommand at once
           via keyword arguments.  Valid attributes are anything specified in
           a subclass, but unknown attributes will be ignored.
        """
        for (key, val) in kwargs.items():
            # Ignore setting attributes that were removed in a subclass, as
            # if they were unknown attributes.
            if key in self.removedAttrs:
                continue

            if hasattr(self, key):
                setattr(self, key, val)

    def __str__(self):
        """Return a string formatted for output to a kickstart file.  This
           method must be provided by all subclasses.
        """
        return KickstartObject.__str__(self)

    def parse(self, args):
        """Parse the list of args and set data on the KickstartCommand object.
           This method must be provided by all subclasses.
        """
        raise TypeError, "parse() not implemented for KickstartCommand"

    def apply(self, instroot="/"):
        """Write out the configuration related to the KickstartCommand object.
           Subclasses which do not provide this method will not have their
           configuration written out.
        """
        return

    def dataList(self):
        """For commands that can occur multiple times in a single kickstart
           file (like network, part, etc.), return the list that we should
           append more data objects to.
        """
        return None

    def deleteRemovedAttrs(self):
        """Remove all attributes from self that are given in the removedAttrs
           list.  This method should be called from __init__ in a subclass,
           but only after the superclass's __init__ method has been called.
        """
        for attr in filter(lambda k: hasattr(self, k), self.removedAttrs):
            delattr(self, attr)

    # Set the contents of the opts object (an instance of optparse.Values
    # returned by parse_args) as attributes on the KickstartCommand object.
    # It's useful to call this from KickstartCommand subclasses after parsing
    # the arguments.
    def _setToSelf(self, optParser, opts):
        self._setToObj(optParser, opts, self)

    # Sets the contents of the opts object (an instance of optparse.Values
    # returned by parse_args) as attributes on the provided object obj.  It's
    # useful to call this from KickstartCommand subclasses that handle lists
    # of objects (like partitions, network devices, etc.) and need to populate
    # a Data object.
    def _setToObj(self, optParser, opts, obj):
        for key in filter (lambda k: getattr(opts, k) != None, optParser.keys()):
            setattr(obj, key, getattr(opts, key))

class DeprecatedCommand(KickstartCommand):
    """Specify that a command is deprecated and no longer has any function.
       Any command that is deprecated should be subclassed from this class,
       only specifying an __init__ method that calls the superclass's __init__.
       This is an abstract class.
    """
    def __init__(self, writePriority=None, *args, **kwargs):
        # We don't want people using this class by itself.
        if self.__class__ is KickstartCommand:
            raise TypeError, "DeprecatedCommand is an abstract class."

        # Create a new DeprecatedCommand instance.
        KickstartCommand.__init__(self, writePriority, *args, **kwargs)

    def __str__(self):
        """Placeholder since DeprecatedCommands don't work anymore."""
        return ""

    def parse(self, args):
        """Print a warning message if the command is seen in the input file."""
        mapping = {"lineno": self.lineno, "cmd": self.currentCmd}
        warnings.warn(_("Ignoring deprecated command on line %(lineno)s:  The %(cmd)s command has been deprecated and no longer has any effect.  It may be removed from future releases, which will result in a fatal error from kickstart.  Please modify your kickstart file to remove this command.") % mapping, DeprecationWarning)


###
### HANDLERS
###
class BaseHandler(KickstartObject):
    """Each version of kickstart syntax is provided by a subclass of this
       class.  These subclasses are what users will interact with for parsing,
       extracting data, and writing out kickstart files.  This is an abstract
       class.

       version -- The version this syntax handler supports.  This is set by
                  a class attribute of a BaseHandler subclass and is used to
                  set up the command dict.  It is for read-only use.
    """
    version = None

    def __init__(self, mapping=None, dataMapping=None, commandUpdates=None,
                 dataUpdates=None, *args, **kwargs):
        """Create a new BaseHandler instance.  This method must be provided by
           all subclasses, but subclasses must call BaseHandler.__init__ first.

           mapping          -- A custom map from command strings to classes,
                               useful when creating your own handler with
                               special command objects.  It is otherwise unused
                               and rarely needed.  If you give this argument,
                               the mapping takes the place of the default one
                               and so must include all commands you want
                               recognized.
           dataMapping      -- This is the same as mapping, but for data
                               objects.  All the same comments apply.
           commandUpdates   -- This is similar to mapping, but does not take
                               the place of the defaults entirely.  Instead,
                               this mapping is applied after the defaults and
                               updates it with just the commands you want to
                               modify.
           dataUpdates      -- This is the same as commandUpdates, but for
                               data objects.


           Instance attributes:

           commands -- A mapping from a string command to a KickstartCommand
                       subclass object that handles it.  Multiple strings can
                       map to the same object, but only one instance of the
                       command object should ever exist.  Most users should
                       never have to deal with this directly, as it is
                       manipulated internally and called through dispatcher.
           currentLine -- The current unprocessed line from the input file
                          that caused this handler to be run.
           packages -- An instance of pykickstart.parser.Packages which
                       describes the packages section of the input file.
           platform -- A string describing the hardware platform, which is
                       needed only by system-config-kickstart.
           scripts  -- A list of pykickstart.parser.Script instances, which is
                       populated by KickstartParser.addScript and describes the
                       %pre/%post/%traceback script section of the input file.
        """

        # We don't want people using this class by itself.
        if self.__class__ is BaseHandler:
            raise TypeError, "BaseHandler is an abstract class."

        KickstartObject.__init__(self, *args, **kwargs)

        # This isn't really a good place for these, but it's better than
        # everything else I can think of.
        self.scripts = []
        self.packages = Packages()
        self.platform = ""

        # These will be set by the dispatcher.
        self.commands = {}
        self.currentLine = 0

        # A dict keyed by an integer priority number, with each value being a
        # list of KickstartCommand subclasses.  This dict is maintained by
        # registerCommand and used in __str__.  No one else should be touching
        # it.
        self._writeOrder = {}

        self._registerCommands(mapping, dataMapping, commandUpdates, dataUpdates)

    def __str__(self):
        """Return a string formatted for output to a kickstart file."""
        retval = ""

        if self.platform != "":
            retval += "#platform=%s\n" % self.platform

        retval += "#version=%s\n" % versionToString(self.version)

        lst = self._writeOrder.keys()
        lst.sort()

        for prio in lst:
            for obj in self._writeOrder[prio]:
                retval += obj.__str__()

        for script in self.scripts:
            retval += script.__str__()

        retval += self.packages.__str__()

        return retval

    def _insertSorted(self, lst, obj):
        length = len(lst)
        i = 0

        while i < length:
            # If the two classes have the same name, it's because we are
            # overriding an existing class with one from a later kickstart
            # version, so remove the old one in favor of the new one.
            if obj.__class__.__name__ > lst[i].__class__.__name__:
                i += 1
            elif obj.__class__.__name__ == lst[i].__class__.__name__:
                lst[i] = obj
                return
            elif obj.__class__.__name__ < lst[i].__class__.__name__:
                break

        if i >= length:
            lst.append(obj)
        else:
            lst.insert(i, obj)

    def _setCommand(self, cmdObj):
        # Add an attribute on this version object.  We need this to provide a
        # way for clients to access the command objects.  We also need to strip
        # off the version part from the front of the name.
        if cmdObj.__class__.__name__.find("_") != -1:
            name = unicode(cmdObj.__class__.__name__.split("_", 1)[1])
        else:
            name = unicode(cmdObj.__class__.__name__).lower()

        setattr(self, name.lower(), cmdObj)

        # Also, add the object into the _writeOrder dict in the right place.
        if cmdObj.writePriority is not None:
            if self._writeOrder.has_key(cmdObj.writePriority):
                self._insertSorted(self._writeOrder[cmdObj.writePriority], cmdObj)
            else:
                self._writeOrder[cmdObj.writePriority] = [cmdObj]

    def _registerCommands(self, mapping=None, dataMapping=None, commandUpdates=None,
                          dataUpdates=None):
        if mapping == {} or mapping == None:
            from pykickstart.handlers.control import commandMap
            cMap = commandMap[self.version]
        else:
            cMap = mapping

        if dataMapping == {} or dataMapping == None:
            from pykickstart.handlers.control import dataMap
            dMap = dataMap[self.version]
        else:
            dMap = dataMapping

        if type(commandUpdates) == types.DictType:
            cMap.update(commandUpdates)

        if type(dataUpdates) == types.DictType:
            dMap.update(dataUpdates)

        for (cmdName, cmdClass) in cMap.iteritems():
            # First make sure we haven't instantiated this command handler
            # already.  If we have, we just need to make another mapping to
            # it in self.commands.
            cmdObj = None

            for (key, val) in self.commands.iteritems():
                if val.__class__.__name__ == cmdClass.__name__:
                    cmdObj = val
                    break

            # If we didn't find an instance in self.commands, create one now.
            if cmdObj == None:
                cmdObj = cmdClass()
                self._setCommand(cmdObj)

            # Finally, add the mapping to the commands dict.
            self.commands[cmdName] = cmdObj
            self.commands[cmdName].handler = self

        # We also need to create attributes for the various data objects.
        # No checks here because dMap is a bijection.  At least, that's what
        # the comment says.  Hope no one screws that up.
        for (dataName, dataClass) in dMap.iteritems():
            setattr(self, dataName, dataClass)

    def dispatcher(self, args, lineno):
        """Call the appropriate KickstartCommand handler for the current line
           in the kickstart file.  A handler for the current command should
           be registered, though a handler of None is not an error.  Returns
           the data object returned by KickstartCommand.parse.

           args    -- A list of arguments to the current command
           lineno  -- The line number in the file, for error reporting
        """
        cmd = args[0]

        if not self.commands.has_key(cmd):
            raise KickstartParseError, formatErrorMsg(lineno, msg=_("Unknown command: %s" % cmd))
        elif self.commands[cmd] != None:
            self.commands[cmd].currentCmd = cmd
            self.commands[cmd].currentLine = self.currentLine
            self.commands[cmd].lineno = lineno

            # The parser returns the data object that was modified.  This could
            # be a BaseData subclass that should be put into a list, or it
            # could be the command handler object itself.
            obj = self.commands[cmd].parse(args[1:])
            lst = self.commands[cmd].dataList()
            if lst is not None:
                lst.append(obj)

            return obj

    def maskAllExcept(self, lst):
        """Set all entries in the commands dict to None, except the ones in
           the lst.  All other commands will not be processed.
        """
        self._writeOrder = {}

        for (key, val) in self.commands.iteritems():
            if not key in lst:
                self.commands[key] = None

    def hasCommand(self, cmd):
        """Return true if there is a handler for the string cmd."""
        return hasattr(self, cmd)


###
### DATA
###
class BaseData(KickstartObject):
    """The base class for all data objects.  This is an abstract class."""
    removedKeywords = []
    removedAttrs = []

    def __init__(self, *args, **kwargs):
        """Create a new BaseData instance.
        
           lineno -- Line number in the ks-file where this object was defined
        """

        # We don't want people using this class by itself.
        if self.__class__ is BaseData:
            raise TypeError, "BaseData is an abstract class."

        KickstartObject.__init__(self, *args, **kwargs)
        self.lineno = 0

    def __str__(self):
        """Return a string formatted for output to a kickstart file."""
        return ""

    def __call__(self, *args, **kwargs):
        """Set multiple attributes on a subclass of BaseData at once via
           keyword arguments.  Valid attributes are anything specified in a
           subclass, but unknown attributes will be ignored.
        """
        for (key, val) in kwargs.items():
            # Ignore setting attributes that were removed in a subclass, as
            # if they were unknown attributes.
            if key in self.removedAttrs:
                continue

            if hasattr(self, key):
                setattr(self, key, val)

    def deleteRemovedAttrs(self):
        """Remove all attributes from self that are given in the removedAttrs
           list.  This method should be called from __init__ in a subclass,
           but only after the superclass's __init__ method has been called.
        """
        for attr in filter(lambda k: hasattr(self, k), self.removedAttrs):
            delattr(self, attr)
