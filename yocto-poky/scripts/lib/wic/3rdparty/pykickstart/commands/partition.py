#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2005, 2006, 2007, 2008 Red Hat, Inc.
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
from pykickstart.errors import *
from pykickstart.options import *

import gettext
import warnings
_ = lambda x: gettext.ldgettext("pykickstart", x)

class FC3_PartData(BaseData):
    removedKeywords = BaseData.removedKeywords
    removedAttrs = BaseData.removedAttrs

    def __init__(self, *args, **kwargs):
        BaseData.__init__(self, *args, **kwargs)
        self.active = kwargs.get("active", False)
        self.primOnly = kwargs.get("primOnly", False)
        self.end = kwargs.get("end", 0)
        self.fstype = kwargs.get("fstype", "")
        self.grow = kwargs.get("grow", False)
        self.maxSizeMB = kwargs.get("maxSizeMB", 0)
        self.format = kwargs.get("format", True)
        self.onbiosdisk = kwargs.get("onbiosdisk", "")
        self.disk = kwargs.get("disk", "")
        self.onPart = kwargs.get("onPart", "")
        self.recommended = kwargs.get("recommended", False)
        self.size = kwargs.get("size", None)
        self.start = kwargs.get("start", 0)
        self.mountpoint = kwargs.get("mountpoint", "")

    def __eq__(self, y):
        if self.mountpoint:
            return self.mountpoint == y.mountpoint
        else:
            return False

    def _getArgsAsStr(self):
        retval = ""

        if self.active:
            retval += " --active"
        if self.primOnly:
            retval += " --asprimary"
        if hasattr(self, "end") and self.end != 0:
            retval += " --end=%s" % self.end
        if self.fstype != "":
            retval += " --fstype=\"%s\"" % self.fstype
        if self.grow:
            retval += " --grow"
        if self.maxSizeMB > 0:
            retval += " --maxsize=%d" % self.maxSizeMB
        if not self.format:
            retval += " --noformat"
        if self.onbiosdisk != "":
            retval += " --onbiosdisk=%s" % self.onbiosdisk
        if self.disk != "":
            retval += " --ondisk=%s" % self.disk
        if self.onPart != "":
            retval += " --onpart=%s" % self.onPart
        if self.recommended:
            retval += " --recommended"
        if self.size and self.size != 0:
            retval += " --size=%sk" % self.size
        if hasattr(self, "start") and self.start != 0:
            retval += " --start=%s" % self.start

        return retval

    def __str__(self):
        retval = BaseData.__str__(self)
        if self.mountpoint:
            mountpoint_str = "%s" % self.mountpoint
        else:
            mountpoint_str = "(No mount point)"
        retval += "part %s%s\n" % (mountpoint_str, self._getArgsAsStr())
        return retval

class FC4_PartData(FC3_PartData):
    removedKeywords = FC3_PartData.removedKeywords
    removedAttrs = FC3_PartData.removedAttrs

    def __init__(self, *args, **kwargs):
        FC3_PartData.__init__(self, *args, **kwargs)
        self.bytesPerInode = kwargs.get("bytesPerInode", 4096)
        self.fsopts = kwargs.get("fsopts", "")
        self.label = kwargs.get("label", "")

    def _getArgsAsStr(self):
        retval = FC3_PartData._getArgsAsStr(self)

        if hasattr(self, "bytesPerInode") and self.bytesPerInode != 0:
            retval += " --bytes-per-inode=%d" % self.bytesPerInode
        if self.fsopts != "":
            retval += " --fsoptions=\"%s\"" % self.fsopts
        if self.label != "":
            retval += " --label=%s" % self.label

        return retval

class F9_PartData(FC4_PartData):
    removedKeywords = FC4_PartData.removedKeywords + ["bytesPerInode"]
    removedAttrs = FC4_PartData.removedAttrs + ["bytesPerInode"]

    def __init__(self, *args, **kwargs):
        FC4_PartData.__init__(self, *args, **kwargs)
        self.deleteRemovedAttrs()

        self.fsopts = kwargs.get("fsopts", "")
        self.label = kwargs.get("label", "")
        self.fsprofile = kwargs.get("fsprofile", "")
        self.encrypted = kwargs.get("encrypted", False)
        self.passphrase = kwargs.get("passphrase", "")

    def _getArgsAsStr(self):
        retval = FC4_PartData._getArgsAsStr(self)

        if self.fsprofile != "":
            retval += " --fsprofile=\"%s\"" % self.fsprofile
        if self.encrypted:
            retval += " --encrypted"

            if self.passphrase != "":
                retval += " --passphrase=\"%s\"" % self.passphrase

        return retval

class F11_PartData(F9_PartData):
    removedKeywords = F9_PartData.removedKeywords + ["start", "end"]
    removedAttrs = F9_PartData.removedAttrs + ["start", "end"]

class F12_PartData(F11_PartData):
    removedKeywords = F11_PartData.removedKeywords
    removedAttrs = F11_PartData.removedAttrs

    def __init__(self, *args, **kwargs):
        F11_PartData.__init__(self, *args, **kwargs)

        self.escrowcert = kwargs.get("escrowcert", "")
        self.backuppassphrase = kwargs.get("backuppassphrase", False)

    def _getArgsAsStr(self):
        retval = F11_PartData._getArgsAsStr(self)

        if self.encrypted and self.escrowcert != "":
            retval += " --escrowcert=\"%s\"" % self.escrowcert

            if self.backuppassphrase:
                retval += " --backuppassphrase"

        return retval

F14_PartData = F12_PartData

class FC3_Partition(KickstartCommand):
    removedKeywords = KickstartCommand.removedKeywords
    removedAttrs = KickstartCommand.removedAttrs

    def __init__(self, writePriority=130, *args, **kwargs):
        KickstartCommand.__init__(self, writePriority, *args, **kwargs)
        self.op = self._getParser()

        self.partitions = kwargs.get("partitions", [])

    def __str__(self):
        retval = ""

        for part in self.partitions:
            retval += part.__str__()

        if retval != "":
            return "# Disk partitioning information\n" + retval
        else:
            return ""

    def _getParser(self):
        def part_cb (option, opt_str, value, parser):
            if value.startswith("/dev/"):
                parser.values.ensure_value(option.dest, value[5:])
            else:
                parser.values.ensure_value(option.dest, value)

        op = KSOptionParser()
        op.add_option("--active", dest="active", action="store_true",
                      default=False)
        op.add_option("--asprimary", dest="primOnly", action="store_true",
                      default=False)
        op.add_option("--end", dest="end", action="store", type="int",
                      nargs=1)
        op.add_option("--fstype", "--type", dest="fstype")
        op.add_option("--grow", dest="grow", action="store_true", default=False)
        op.add_option("--maxsize", dest="maxSizeMB", action="store", type="int",
                      nargs=1)
        op.add_option("--noformat", dest="format", action="store_false",
                      default=True)
        op.add_option("--onbiosdisk", dest="onbiosdisk")
        op.add_option("--ondisk", "--ondrive", dest="disk")
        op.add_option("--onpart", "--usepart", dest="onPart", action="callback",
                      callback=part_cb, nargs=1, type="string")
        op.add_option("--recommended", dest="recommended", action="store_true",
                      default=False)
        op.add_option("--size", dest="size", action="store", type="size",
                      nargs=1)
        op.add_option("--start", dest="start", action="store", type="int",
                      nargs=1)
        return op

    def parse(self, args):
        (opts, extra) = self.op.parse_args(args=args, lineno=self.lineno)

        pd = self.handler.PartData()
        self._setToObj(self.op, opts, pd)
        pd.lineno = self.lineno
        if extra:
            pd.mountpoint = extra[0]
            if pd in self.dataList():
                warnings.warn(_("A partition with the mountpoint %s has already been defined.") % pd.mountpoint)
        else:
            pd.mountpoint = None

        return pd

    def dataList(self):
        return self.partitions

class FC4_Partition(FC3_Partition):
    removedKeywords = FC3_Partition.removedKeywords
    removedAttrs = FC3_Partition.removedAttrs

    def __init__(self, writePriority=130, *args, **kwargs):
        FC3_Partition.__init__(self, writePriority, *args, **kwargs)

        def part_cb (option, opt_str, value, parser):
            if value.startswith("/dev/"):
                parser.values.ensure_value(option.dest, value[5:])
            else:
                parser.values.ensure_value(option.dest, value)

    def _getParser(self):
        op = FC3_Partition._getParser(self)
        op.add_option("--bytes-per-inode", dest="bytesPerInode", action="store",
                      type="int", nargs=1)
        op.add_option("--fsoptions", dest="fsopts")
        op.add_option("--label", dest="label")
        return op

class F9_Partition(FC4_Partition):
    removedKeywords = FC4_Partition.removedKeywords
    removedAttrs = FC4_Partition.removedAttrs

    def __init__(self, writePriority=130, *args, **kwargs):
        FC4_Partition.__init__(self, writePriority, *args, **kwargs)

        def part_cb (option, opt_str, value, parser):
            if value.startswith("/dev/"):
                parser.values.ensure_value(option.dest, value[5:])
            else:
                parser.values.ensure_value(option.dest, value)

    def _getParser(self):
        op = FC4_Partition._getParser(self)
        op.add_option("--bytes-per-inode", deprecated=1)
        op.add_option("--fsprofile")
        op.add_option("--encrypted", action="store_true", default=False)
        op.add_option("--passphrase")
        return op

class F11_Partition(F9_Partition):
    removedKeywords = F9_Partition.removedKeywords
    removedAttrs = F9_Partition.removedAttrs

    def _getParser(self):
        op = F9_Partition._getParser(self)
        op.add_option("--start", deprecated=1)
        op.add_option("--end", deprecated=1)
        return op

class F12_Partition(F11_Partition):
    removedKeywords = F11_Partition.removedKeywords
    removedAttrs = F11_Partition.removedAttrs

    def _getParser(self):
        op = F11_Partition._getParser(self)
        op.add_option("--escrowcert")
        op.add_option("--backuppassphrase", action="store_true", default=False)
        return op

class F14_Partition(F12_Partition):
    removedKeywords = F12_Partition.removedKeywords
    removedAttrs = F12_Partition.removedAttrs

    def _getParser(self):
        op = F12_Partition._getParser(self)
        op.remove_option("--bytes-per-inode")
        op.remove_option("--start")
        op.remove_option("--end")
        return op
