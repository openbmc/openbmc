#
# sections.py:  Kickstart file sections.
#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2011 Red Hat, Inc.
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
This module exports the classes that define a section of a kickstart file.  A
section is a chunk of the file starting with a %tag and ending with a %end.
Examples of sections include %packages, %pre, and %post.

You may use this module to define your own custom sections which will be
treated just the same as a predefined one by the kickstart parser.  All that
is necessary is to create a new subclass of Section and call
parser.registerSection with an instance of your new class.
"""
from constants import *
from options import KSOptionParser
from version import *

class Section(object):
    """The base class for defining kickstart sections.  You are free to
       subclass this as appropriate.

       Class attributes:

       allLines    -- Does this section require the parser to call handleLine
                      for every line in the section, even blanks and comments?
       sectionOpen -- The string that denotes the start of this section.  You
                      must start your tag with a percent sign.
       timesSeen   -- This attribute is for informational purposes only.  It is
                      incremented every time handleHeader is called to keep
                      track of the number of times a section of this type is
                      seen.
    """
    allLines = False
    sectionOpen = ""
    timesSeen = 0

    def __init__(self, handler, **kwargs):
        """Create a new Script instance.  At the least, you must pass in an
           instance of a baseHandler subclass.

           Valid kwargs:

           dataObj --
        """
        self.handler = handler

        self.version = self.handler.version

        self.dataObj = kwargs.get("dataObj", None)

    def finalize(self):
        """This method is called when the %end tag for a section is seen.  It
           is not required to be provided.
        """
        pass

    def handleLine(self, line):
        """This method is called for every line of a section.  Take whatever
           action is appropriate.  While this method is not required to be
           provided, not providing it does not make a whole lot of sense.

           Arguments:

           line -- The complete line, with any trailing newline.
        """
        pass

    def handleHeader(self, lineno, args):
        """This method is called when the opening tag for a section is seen.
           Not all sections will need this method, though all provided with
           kickstart include one.

           Arguments:

           args -- A list of all strings passed as arguments to the section
                   opening tag.
        """
        self.timesSeen += 1

class NullSection(Section):
    """This defines a section that pykickstart will recognize but do nothing
       with.  If the parser runs across a %section that has no object registered,
       it will raise an error.  Sometimes, you may want to simply ignore those
       sections instead.  This class is useful for that purpose.
    """
    def __init__(self, *args, **kwargs):
        """Create a new NullSection instance.  You must pass a sectionOpen
           parameter (including a leading '%') for the section you wish to
           ignore.
        """
        Section.__init__(self, *args, **kwargs)
        self.sectionOpen = kwargs.get("sectionOpen")

class ScriptSection(Section):
    allLines = True

    def __init__(self, *args, **kwargs):
        Section.__init__(self, *args, **kwargs)
        self._script = {}
        self._resetScript()

    def _getParser(self):
        op = KSOptionParser(self.version)
        op.add_option("--erroronfail", dest="errorOnFail", action="store_true",
                      default=False)
        op.add_option("--interpreter", dest="interpreter", default="/bin/sh")
        op.add_option("--log", "--logfile", dest="log")
        return op

    def _resetScript(self):
        self._script = {"interp": "/bin/sh", "log": None, "errorOnFail": False,
                        "lineno": None, "chroot": False, "body": []}

    def handleLine(self, line):
        self._script["body"].append(line)

    def finalize(self):
        if " ".join(self._script["body"]).strip() == "":
            return

        kwargs = {"interp": self._script["interp"],
                  "inChroot": self._script["chroot"],
                  "lineno": self._script["lineno"],
                  "logfile": self._script["log"],
                  "errorOnFail": self._script["errorOnFail"],
                  "type": self._script["type"]}

        s = self.dataObj (self._script["body"], **kwargs)
        self._resetScript()

        if self.handler:
            self.handler.scripts.append(s)

    def handleHeader(self, lineno, args):
        """Process the arguments to a %pre/%post/%traceback header for later
           setting on a Script instance once the end of the script is found.
           This method may be overridden in a subclass if necessary.
        """
        Section.handleHeader(self, lineno, args)
        op = self._getParser()

        (opts, extra) = op.parse_args(args=args[1:], lineno=lineno)

        self._script["interp"] = opts.interpreter
        self._script["lineno"] = lineno
        self._script["log"] = opts.log
        self._script["errorOnFail"] = opts.errorOnFail
        if hasattr(opts, "nochroot"):
            self._script["chroot"] = not opts.nochroot

class PreScriptSection(ScriptSection):
    sectionOpen = "%pre"

    def _resetScript(self):
        ScriptSection._resetScript(self)
        self._script["type"] = KS_SCRIPT_PRE

class PostScriptSection(ScriptSection):
    sectionOpen = "%post"

    def _getParser(self):
        op = ScriptSection._getParser(self)
        op.add_option("--nochroot", dest="nochroot", action="store_true",
                      default=False)
        return op

    def _resetScript(self):
        ScriptSection._resetScript(self)
        self._script["chroot"] = True
        self._script["type"] = KS_SCRIPT_POST

class TracebackScriptSection(ScriptSection):
    sectionOpen = "%traceback"

    def _resetScript(self):
        ScriptSection._resetScript(self)
        self._script["type"] = KS_SCRIPT_TRACEBACK

class PackageSection(Section):
    sectionOpen = "%packages"

    def handleLine(self, line):
        if not self.handler:
            return

        (h, s, t) = line.partition('#')
        line = h.rstrip()

        self.handler.packages.add([line])

    def handleHeader(self, lineno, args):
        """Process the arguments to the %packages header and set attributes
           on the Version's Packages instance appropriate.  This method may be
           overridden in a subclass if necessary.
        """
        Section.handleHeader(self, lineno, args)
        op = KSOptionParser(version=self.version)
        op.add_option("--excludedocs", dest="excludedocs", action="store_true",
                      default=False)
        op.add_option("--ignoremissing", dest="ignoremissing",
                      action="store_true", default=False)
        op.add_option("--nobase", dest="nobase", action="store_true",
                      default=False)
        op.add_option("--ignoredeps", dest="resolveDeps", action="store_false",
                      deprecated=FC4, removed=F9)
        op.add_option("--resolvedeps", dest="resolveDeps", action="store_true",
                      deprecated=FC4, removed=F9)
        op.add_option("--default", dest="defaultPackages", action="store_true",
                      default=False, introduced=F7)
        op.add_option("--instLangs", dest="instLangs", type="string",
                      default="", introduced=F9)

        (opts, extra) = op.parse_args(args=args[1:], lineno=lineno)

        self.handler.packages.excludeDocs = opts.excludedocs
        self.handler.packages.addBase = not opts.nobase
        if opts.ignoremissing:
            self.handler.packages.handleMissing = KS_MISSING_IGNORE
        else:
            self.handler.packages.handleMissing = KS_MISSING_PROMPT

        if opts.defaultPackages:
            self.handler.packages.default = True

        if opts.instLangs:
            self.handler.packages.instLangs = opts.instLangs
