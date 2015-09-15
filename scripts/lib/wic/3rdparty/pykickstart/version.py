#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2006, 2007, 2008, 2009, 2010 Red Hat, Inc.
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
Methods for working with kickstart versions.

This module defines several symbolic constants that specify kickstart syntax
versions.  Each version corresponds roughly to one release of Red Hat Linux,
Red Hat Enterprise Linux, or Fedora Core as these are where most syntax
changes take place.

This module also exports several functions:

    makeVersion - Given a version number, return an instance of the
                  matching handler class.

    returnClassForVersion - Given a version number, return the matching
                            handler class.  This does not return an
                            instance of that class, however.

    stringToVersion - Convert a string representation of a version number
                      into the symbolic constant.

    versionToString - Perform the reverse mapping.

    versionFromFile - Read a kickstart file and determine the version of
                      syntax it uses.  This requires the kickstart file to
                      have a version= comment in it.
"""
import imputil, re, sys

import gettext
_ = lambda x: gettext.ldgettext("pykickstart", x)

from pykickstart.errors import KickstartVersionError

# Symbolic names for internal version numbers.
RHEL3 = 900
FC3 = 1000
RHEL4 = 1100
FC4 = 2000
FC5 = 3000
FC6 = 4000
RHEL5 = 4100
F7  = 5000
F8 = 6000
F9 = 7000
F10 = 8000
F11 = 9000
F12 = 10000
F13 = 11000
RHEL6 = 11100
F14 = 12000
F15 = 13000
F16 = 14000

# This always points at the latest version and is the default.
DEVEL = F16

# A one-to-one mapping from string representations to version numbers.
versionMap = {
        "DEVEL": DEVEL,
        "FC3": FC3, "FC4": FC4, "FC5": FC5, "FC6": FC6, "F7": F7, "F8": F8,
        "F9": F9, "F10": F10, "F11": F11, "F12": F12, "F13": F13,
        "F14": F14, "F15": F15, "F16": F16,
        "RHEL3": RHEL3, "RHEL4": RHEL4, "RHEL5": RHEL5, "RHEL6": RHEL6
}

def stringToVersion(s):
    """Convert string into one of the provided version constants.  Raises
       KickstartVersionError if string does not match anything.
    """
    # First try these short forms.
    try:
        return versionMap[s.upper()]
    except KeyError:
        pass

    # Now try the Fedora versions.
    m = re.match("^fedora.* (\d+)$", s, re.I)

    if m and m.group(1):
        if versionMap.has_key("FC" + m.group(1)):
            return versionMap["FC" + m.group(1)]
        elif versionMap.has_key("F" + m.group(1)):
            return versionMap["F" + m.group(1)]
        else:
            raise KickstartVersionError(_("Unsupported version specified: %s") % s)

    # Now try the RHEL versions.
    m = re.match("^red hat enterprise linux.* (\d+)([\.\d]*)$", s, re.I)

    if m and m.group(1):
        if versionMap.has_key("RHEL" + m.group(1)):
            return versionMap["RHEL" + m.group(1)]
        else:
            raise KickstartVersionError(_("Unsupported version specified: %s") % s)

    # If nothing else worked, we're out of options.
    raise KickstartVersionError(_("Unsupported version specified: %s") % s)

def versionToString(version, skipDevel=False):
    """Convert version into a string representation of the version number.
       This is the reverse operation of stringToVersion.  Raises
       KickstartVersionError if version does not match anything.
    """
    if not skipDevel and version == versionMap["DEVEL"]:
        return "DEVEL"

    for (key, val) in versionMap.iteritems():
        if key == "DEVEL":
            continue
        elif val == version:
            return key

    raise KickstartVersionError(_("Unsupported version specified: %s") % version)

def returnClassForVersion(version=DEVEL):
    """Return the class of the syntax handler for version.  version can be
       either a string or the matching constant.  Raises KickstartValueError
       if version does not match anything.
    """
    try:
        version = int(version)
        module = "%s" % versionToString(version, skipDevel=True)
    except ValueError:
        module = "%s" % version
        version = stringToVersion(version)

    module = module.lower()

    try:
        import pykickstart.handlers
        sys.path.extend(pykickstart.handlers.__path__)
        found = imputil.imp.find_module(module)
        loaded = imputil.imp.load_module(module, found[0], found[1], found[2])

        for (k, v) in loaded.__dict__.iteritems():
            if k.lower().endswith("%shandler" % module):
                return v
    except:
        raise KickstartVersionError(_("Unsupported version specified: %s") % version)

def makeVersion(version=DEVEL):
    """Return a new instance of the syntax handler for version.  version can be
       either a string or the matching constant.  This function is useful for
       standalone programs which just need to handle a specific version of
       kickstart syntax (as provided by a command line argument, for example)
       and need to instantiate the correct object.
    """
    cl = returnClassForVersion(version)
    return cl()
