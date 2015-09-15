#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2009 Red Hat, Inc.
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
Base classes for internal pykickstart use.

The module exports the following important classes:

    KickstartObject - The base class for all classes in pykickstart
"""

class KickstartObject(object):
    """The base class for all other classes in pykickstart."""
    def __init__(self, *args, **kwargs):
        """Create a new KickstartObject instance.  All other classes in
           pykickstart should be derived from this one.  Instance attributes:
        """
        pass

    def __str__(self):
        return ""
