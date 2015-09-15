#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2007 Red Hat, Inc.
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
from pykickstart.base import *
from pykickstart.options import *

class FC3_Bootloader(KickstartCommand):
    removedKeywords = KickstartCommand.removedKeywords
    removedAttrs = KickstartCommand.removedAttrs

    def __init__(self, writePriority=10, *args, **kwargs):
        KickstartCommand.__init__(self, writePriority, *args, **kwargs)
        self.op = self._getParser()

        self.driveorder = kwargs.get("driveorder", [])
        self.appendLine = kwargs.get("appendLine", "")
        self.forceLBA = kwargs.get("forceLBA", False)
        self.linear = kwargs.get("linear", True)
        self.location = kwargs.get("location", "")
        self.md5pass = kwargs.get("md5pass", "")
        self.password = kwargs.get("password", "")
        self.upgrade = kwargs.get("upgrade", False)
        self.useLilo = kwargs.get("useLilo", False)

        self.deleteRemovedAttrs()

    def _getArgsAsStr(self):
        retval = ""

        if self.appendLine != "":
            retval += " --append=\"%s\"" % self.appendLine
        if self.linear:
            retval += " --linear"
        if self.location:
            retval += " --location=%s" % self.location
        if hasattr(self, "forceLBA") and self.forceLBA:
            retval += " --lba32"
        if self.password != "":
            retval += " --password=\"%s\"" % self.password
        if self.md5pass != "":
            retval += " --md5pass=\"%s\"" % self.md5pass
        if self.upgrade:
            retval += " --upgrade"
        if self.useLilo:
            retval += " --useLilo"
        if len(self.driveorder) > 0:
            retval += " --driveorder=\"%s\"" % ",".join(self.driveorder)

        return retval

    def __str__(self):
        retval = KickstartCommand.__str__(self)

        if self.location != "":
            retval += "# System bootloader configuration\nbootloader"
            retval += self._getArgsAsStr() + "\n"

        return retval

    def _getParser(self):
        def driveorder_cb (option, opt_str, value, parser):
            for d in value.split(','):
                parser.values.ensure_value(option.dest, []).append(d)

        op = KSOptionParser()
        op.add_option("--append", dest="appendLine")
        op.add_option("--linear", dest="linear", action="store_true",
                      default=True)
        op.add_option("--nolinear", dest="linear", action="store_false")
        op.add_option("--location", dest="location", type="choice",
                      default="mbr",
                      choices=["mbr", "partition", "none", "boot"])
        op.add_option("--lba32", dest="forceLBA", action="store_true",
                      default=False)
        op.add_option("--password", dest="password", default="")
        op.add_option("--md5pass", dest="md5pass", default="")
        op.add_option("--upgrade", dest="upgrade", action="store_true",
                      default=False)
        op.add_option("--useLilo", dest="useLilo", action="store_true",
                      default=False)
        op.add_option("--driveorder", dest="driveorder", action="callback",
                      callback=driveorder_cb, nargs=1, type="string")
        return op

    def parse(self, args):
        (opts, extra) = self.op.parse_args(args=args, lineno=self.lineno)
        self._setToSelf(self.op, opts)

        if self.currentCmd == "lilo":
            self.useLilo = True

        return self

class FC4_Bootloader(FC3_Bootloader):
    removedKeywords = FC3_Bootloader.removedKeywords + ["linear", "useLilo"]
    removedAttrs = FC3_Bootloader.removedAttrs + ["linear", "useLilo"]

    def __init__(self, writePriority=10, *args, **kwargs):
        FC3_Bootloader.__init__(self, writePriority, *args, **kwargs)

    def _getArgsAsStr(self):
        retval = ""
        if self.appendLine != "":
            retval += " --append=\"%s\"" % self.appendLine
        if self.location:
            retval += " --location=%s" % self.location
        if hasattr(self, "forceLBA") and self.forceLBA:
            retval += " --lba32"
        if self.password != "":
            retval += " --password=\"%s\"" % self.password
        if self.md5pass != "":
            retval += " --md5pass=\"%s\"" % self.md5pass
        if self.upgrade:
            retval += " --upgrade"
        if len(self.driveorder) > 0:
            retval += " --driveorder=\"%s\"" % ",".join(self.driveorder)
        return retval

    def _getParser(self):
        op = FC3_Bootloader._getParser(self)
        op.remove_option("--linear")
        op.remove_option("--nolinear")
        op.remove_option("--useLilo")
        return op

    def parse(self, args):
        (opts, extra) = self.op.parse_args(args=args, lineno=self.lineno)
        self._setToSelf(self.op, opts)
        return self

class F8_Bootloader(FC4_Bootloader):
    removedKeywords = FC4_Bootloader.removedKeywords
    removedAttrs = FC4_Bootloader.removedAttrs

    def __init__(self, writePriority=10, *args, **kwargs):
        FC4_Bootloader.__init__(self, writePriority, *args, **kwargs)

        self.timeout = kwargs.get("timeout", None)
        self.default = kwargs.get("default", "")

    def _getArgsAsStr(self):
        ret = FC4_Bootloader._getArgsAsStr(self)

        if self.timeout is not None:
            ret += " --timeout=%d" %(self.timeout,)
        if self.default:
            ret += " --default=%s" %(self.default,)

        return ret

    def _getParser(self):
        op = FC4_Bootloader._getParser(self)
        op.add_option("--timeout", dest="timeout", type="int")
        op.add_option("--default", dest="default")
        return op

class F12_Bootloader(F8_Bootloader):
    removedKeywords = F8_Bootloader.removedKeywords
    removedAttrs = F8_Bootloader.removedAttrs

    def _getParser(self):
        op = F8_Bootloader._getParser(self)
        op.add_option("--lba32", dest="forceLBA", deprecated=1, action="store_true")
        return op

class F14_Bootloader(F12_Bootloader):
    removedKeywords = F12_Bootloader.removedKeywords + ["forceLBA"]
    removedAttrs = F12_Bootloader.removedKeywords + ["forceLBA"]

    def _getParser(self):
        op = F12_Bootloader._getParser(self)
        op.remove_option("--lba32")
        return op

class F15_Bootloader(F14_Bootloader):
    removedKeywords = F14_Bootloader.removedKeywords
    removedAttrs = F14_Bootloader.removedAttrs

    def __init__(self, writePriority=10, *args, **kwargs):
        F14_Bootloader.__init__(self, writePriority, *args, **kwargs)

        self.isCrypted = kwargs.get("isCrypted", False)

    def _getArgsAsStr(self):
        ret = F14_Bootloader._getArgsAsStr(self)

        if self.isCrypted:
            ret += " --iscrypted"

        return ret

    def _getParser(self):
        def password_cb(option, opt_str, value, parser):
            parser.values.isCrypted = True
            parser.values.password = value

        op = F14_Bootloader._getParser(self)
        op.add_option("--iscrypted", dest="isCrypted", action="store_true", default=False)
        op.add_option("--md5pass", action="callback", callback=password_cb, nargs=1, type="string")
        return op
