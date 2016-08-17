#!/usr/bin/env python -tt
#
# Copyright (c) 2007 Red Hat  Inc.
# Copyright (c) 2009, 2010, 2011 Intel, Inc.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; version 2 of the License
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.

from __future__ import with_statement
import os
import tempfile
import shutil

from wic import msger
from wic.utils.errors import CreatorError
from wic.utils import runner

class BaseImageCreator(object):
    """Base class for image creation.

    BaseImageCreator is the simplest creator class available; it will
    create a system image according to the supplied kickstart file.

    e.g.

      import wic.imgcreate as imgcreate
      ks = imgcreate.read_kickstart("foo.ks")
      imgcreate.ImageCreator(ks, "foo").create()
    """

    def __del__(self):
        self.cleanup()

    def __init__(self, createopts=None):
        """Initialize an ImageCreator instance.

        ks -- a pykickstart.KickstartParser instance; this instance will be
              used to drive the install by e.g. providing the list of packages
              to be installed, the system configuration and %post scripts

        name -- a name for the image; used for e.g. image filenames or
                filesystem labels
        """

        self.__builddir = None

        self.ks = None
        self.name = "target"
        self.tmpdir = "/var/tmp/wic"
        self.workdir = "/var/tmp/wic/build"

        # setup tmpfs tmpdir when enabletmpfs is True
        self.enabletmpfs = False

        if createopts:
            # Mapping table for variables that have different names.
            optmap = {"outdir" : "destdir",
                     }

            # update setting from createopts
            for key in createopts.keys():
                if key in optmap:
                    option = optmap[key]
                else:
                    option = key
                setattr(self, option, createopts[key])

            self.destdir = os.path.abspath(os.path.expanduser(self.destdir))

        self._dep_checks = ["ls", "bash", "cp", "echo"]

        # Output image file names
        self.outimage = []

        # No ks provided when called by convertor, so skip the dependency check
        if self.ks:
            # If we have btrfs partition we need to check necessary tools
            for part in self.ks.partitions:
                if part.fstype and part.fstype == "btrfs":
                    self._dep_checks.append("mkfs.btrfs")
                    break

        # make sure the specified tmpdir and cachedir exist
        if not os.path.exists(self.tmpdir):
            os.makedirs(self.tmpdir)


    #
    # Hooks for subclasses
    #
    def _create(self):
        """Create partitions for the disk image(s)

        This is the hook where subclasses may create the partitions
        that will be assembled into disk image(s).

        There is no default implementation.
        """
        pass

    def _cleanup(self):
        """Undo anything performed in _create().

        This is the hook where subclasses must undo anything which was
        done in _create().

        There is no default implementation.

        """
        pass

    #
    # Actual implementation
    #
    def __ensure_builddir(self):
        if not self.__builddir is None:
            return

        try:
            self.workdir = os.path.join(self.tmpdir, "build")
            if not os.path.exists(self.workdir):
                os.makedirs(self.workdir)
            self.__builddir = tempfile.mkdtemp(dir=self.workdir,
                                               prefix="imgcreate-")
        except OSError as err:
            raise CreatorError("Failed create build directory in %s: %s" %
                               (self.tmpdir, err))

    def __setup_tmpdir(self):
        if not self.enabletmpfs:
            return

        runner.show('mount -t tmpfs -o size=4G tmpfs %s' % self.workdir)

    def __clean_tmpdir(self):
        if not self.enabletmpfs:
            return

        runner.show('umount -l %s' % self.workdir)

    def create(self):
        """Create partitions for the disk image(s)

        Create the partitions that will be assembled into disk
        image(s).
        """
        self.__setup_tmpdir()
        self.__ensure_builddir()

        self._create()

    def cleanup(self):
        """Undo anything performed in create().

        Note, make sure to call this method once finished with the creator
        instance in order to ensure no stale files are left on the host e.g.:

          creator = ImageCreator(ks, name)
          try:
              creator.create()
          finally:
              creator.cleanup()

        """
        if not self.__builddir:
            return

        self._cleanup()

        shutil.rmtree(self.__builddir, ignore_errors=True)
        self.__builddir = None

        self.__clean_tmpdir()


    def print_outimage_info(self):
        msg = "The new image can be found here:\n"
        self.outimage.sort()
        for path in self.outimage:
            msg += '  %s\n' % os.path.abspath(path)

        msger.info(msg)
