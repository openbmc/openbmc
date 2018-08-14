# Copyright (C) 2013-2016 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# Provides a class for automating build tests for projects

import os
import re
import subprocess
import shutil
import tempfile

from abc import ABCMeta, abstractmethod

class BuildProject(metaclass=ABCMeta):
    def __init__(self, uri, foldername=None, tmpdir=None, dl_dir=None):
        self.uri = uri
        self.archive = os.path.basename(uri)
        if not tmpdir:
            tmpdir = tempfile.mkdtemp(prefix='buildproject')
        self.localarchive = os.path.join(tmpdir, self.archive)
        self.dl_dir = dl_dir
        if foldername:
            self.fname = foldername
        else:
            self.fname = re.sub(r'\.tar\.bz2$|\.tar\.gz$|\.tar\.xz$', '', self.archive)

    # Download self.archive to self.localarchive
    def _download_archive(self):
        if self.dl_dir and os.path.exists(os.path.join(self.dl_dir, self.archive)):
            shutil.copyfile(os.path.join(self.dl_dir, self.archive), self.localarchive)
            return

        cmd = "wget -O %s %s" % (self.localarchive, self.uri)
        subprocess.check_output(cmd, shell=True)

    # This method should provide a way to run a command in the desired environment.
    @abstractmethod
    def _run(self, cmd):
        pass

    # The timeout parameter of target.run is set to 0 to make the ssh command
    # run with no timeout.
    def run_configure(self, configure_args='', extra_cmds=''):
        return self._run('cd %s; gnu-configize; %s ./configure %s' % (self.targetdir, extra_cmds, configure_args))

    def run_make(self, make_args=''):
        return self._run('cd %s; make %s' % (self.targetdir, make_args))

    def run_install(self, install_args=''):
        return self._run('cd %s; make install %s' % (self.targetdir, install_args))

    def clean(self):
        self._run('rm -rf %s' % self.targetdir)
        subprocess.check_call('rm -f %s' % self.localarchive, shell=True)
