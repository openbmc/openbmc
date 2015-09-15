#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2005, 2006, 2007 Red Hat, Inc.
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
Specialized option handling.

This module exports two classes:

    KSOptionParser - A specialized subclass of OptionParser to be used
                     in BaseHandler subclasses.

    KSOption - A specialized subclass of Option.
"""
import warnings
from copy import copy
from optparse import *

from constants import *
from errors import *
from version import *

import gettext
_ = lambda x: gettext.ldgettext("pykickstart", x)

class KSOptionParser(OptionParser):
    """A specialized subclass of optparse.OptionParser to handle extra option
       attribute checking, work error reporting into the KickstartParseError
       framework, and to turn off the default help.
    """
    def exit(self, status=0, msg=None):
        pass

    def error(self, msg):
        if self.lineno != None:
            raise KickstartParseError, formatErrorMsg(self.lineno, msg=msg)
        else:
            raise KickstartParseError, msg

    def keys(self):
        retval = []

        for opt in self.option_list:
            if opt not in retval:
                retval.append(opt.dest)

        return retval

    def _init_parsing_state (self):
        OptionParser._init_parsing_state(self)
        self.option_seen = {}

    def check_values (self, values, args):
        def seen(self, option):
            return self.option_seen.has_key(option)

        def usedTooNew(self, option):
            return option.introduced and option.introduced > self.version

        def usedDeprecated(self, option):
            return option.deprecated

        def usedRemoved(self, option):
            return option.removed and option.removed <= self.version

        for option in filter(lambda o: isinstance(o, Option), self.option_list):
            if option.required and not seen(self, option):
                raise KickstartValueError, formatErrorMsg(self.lineno, _("Option %s is required") % option)
            elif seen(self, option) and usedTooNew(self, option):
                mapping = {"option": option, "intro": versionToString(option.introduced),
                           "version": versionToString(self.version)}
                self.error(_("The %(option)s option was introduced in version %(intro)s, but you are using kickstart syntax version %(version)s.") % mapping)
            elif seen(self, option) and usedRemoved(self, option):
                mapping = {"option": option, "removed": versionToString(option.removed),
                           "version": versionToString(self.version)}

                if option.removed == self.version:
                    self.error(_("The %(option)s option is no longer supported.") % mapping)
                else:
                    self.error(_("The %(option)s option was removed in version %(removed)s, but you are using kickstart syntax version %(version)s.") % mapping)
            elif seen(self, option) and usedDeprecated(self, option):
                mapping = {"lineno": self.lineno, "option": option}
                warnings.warn(_("Ignoring deprecated option on line %(lineno)s:  The %(option)s option has been deprecated and no longer has any effect.  It may be removed from future releases, which will result in a fatal error from kickstart.  Please modify your kickstart file to remove this option.") % mapping, DeprecationWarning)

        return (values, args)

    def parse_args(self, *args, **kwargs):
        if kwargs.has_key("lineno"):
            self.lineno = kwargs.pop("lineno")

        return OptionParser.parse_args(self, **kwargs)

    def __init__(self, mapping=None, version=None):
        """Create a new KSOptionParser instance.  Each KickstartCommand
           subclass should create one instance of KSOptionParser, providing
           at least the lineno attribute.  mapping and version are not required.
           Instance attributes:

           mapping -- A mapping from option strings to different values.
           version -- The version of the kickstart syntax we are checking
                      against.
        """
        OptionParser.__init__(self, option_class=KSOption,
                              add_help_option=False,
                              conflict_handler="resolve")
        if mapping is None:
            self.map = {}
        else:
            self.map = mapping

        self.lineno = None
        self.option_seen = {}
        self.version = version

def _check_ksboolean(option, opt, value):
    if value.lower() in ("on", "yes", "true", "1"):
        return True
    elif value.lower() in ("off", "no", "false", "0"):
        return False
    else:
        mapping = {"opt": opt, "value": value}
        raise OptionValueError(_("Option %(opt)s: invalid boolean value: %(value)r") % mapping)

def _check_string(option, opt, value):
    if len(value) > 2 and value.startswith("--"):
        mapping = {"opt": opt, "value": value}
        raise OptionValueError(_("Option %(opt)s: invalid string value: %(value)r") % mapping)
    else:
        return value

def _check_size(option, opt, value):
    # Former default was MB
    if value.isdigit():
        return int(value) * 1024L

    mapping = {"opt": opt, "value": value}
    if not value[:-1].isdigit():
        raise OptionValueError(_("Option %(opt)s: invalid size value: %(value)r") % mapping)

    size = int(value[:-1])
    if value.endswith("k") or value.endswith("K"):
        return size
    if value.endswith("M"):
        return size * 1024L
    if value.endswith("G"):
        return size * 1024L * 1024L
    raise OptionValueError(_("Option %(opt)s: invalid size value: %(value)r") % mapping)

# Creates a new Option class that supports several new attributes:
# - required:  any option with this attribute must be supplied or an exception
#              is thrown
# - introduced:  the kickstart syntax version that this option first appeared
#                in - an exception will be raised if the option is used and
#                the specified syntax version is less than the value of this
#                attribute
# - deprecated:  the kickstart syntax version that this option was deprecated
#                in - a DeprecationWarning will be thrown if the option is
#                used and the specified syntax version is greater than the
#                value of this attribute
# - removed:  the kickstart syntax version that this option was removed in - an
#             exception will be raised if the option is used and the specified
#             syntax version is greated than the value of this attribute
# Also creates a new type:
# - ksboolean:  support various kinds of boolean values on an option
# And two new actions:
# - map :  allows you to define an opt -> val mapping such that dest gets val
#          when opt is seen
# - map_extend:  allows you to define an opt -> [val1, ... valn] mapping such
#                that dest gets a list of vals built up when opt is seen
class KSOption (Option):
    ATTRS = Option.ATTRS + ['introduced', 'deprecated', 'removed', 'required']
    ACTIONS = Option.ACTIONS + ("map", "map_extend",)
    STORE_ACTIONS = Option.STORE_ACTIONS + ("map", "map_extend",)

    TYPES = Option.TYPES + ("ksboolean", "string", "size")
    TYPE_CHECKER = copy(Option.TYPE_CHECKER)
    TYPE_CHECKER["ksboolean"] = _check_ksboolean
    TYPE_CHECKER["string"] = _check_string
    TYPE_CHECKER["size"] = _check_size

    def _check_required(self):
        if self.required and not self.takes_value():
            raise OptionError(_("Required flag set for option that doesn't take a value"), self)

    # Make sure _check_required() is called from the constructor!
    CHECK_METHODS = Option.CHECK_METHODS + [_check_required]

    def process (self, opt, value, values, parser):
        Option.process(self, opt, value, values, parser)
        parser.option_seen[self] = 1

    # Override default take_action method to handle our custom actions.
    def take_action(self, action, dest, opt, value, values, parser):
        if action == "map":
            values.ensure_value(dest, parser.map[opt.lstrip('-')])
        elif action == "map_extend":
            values.ensure_value(dest, []).extend(parser.map[opt.lstrip('-')])
        else:
            Option.take_action(self, action, dest, opt, value, values, parser)

    def takes_value(self):
        # Deprecated options don't take a value.
        return Option.takes_value(self) and not self.deprecated

    def __init__(self, *args, **kwargs):
        self.deprecated = False
        self.required = False
        Option.__init__(self, *args, **kwargs)
