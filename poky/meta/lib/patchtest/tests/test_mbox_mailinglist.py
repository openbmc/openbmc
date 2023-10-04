# Check if the series was intended for other project (not OE-Core)
#
# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import subprocess
import collections
import base
import re
from data import PatchTestInput

class MailingList(base.Base):

    # base paths of main yocto project sub-projects
    paths = {
        'oe-core': ['meta-selftest', 'meta-skeleton', 'meta', 'scripts'],
        'bitbake': ['bitbake'],
        'documentation': ['documentation'],
        'poky': ['meta-poky','meta-yocto-bsp'],
        'oe': ['meta-gpe', 'meta-gnome', 'meta-efl', 'meta-networking', 'meta-multimedia','meta-initramfs', 'meta-ruby', 'contrib', 'meta-xfce', 'meta-filesystems', 'meta-perl', 'meta-webserver', 'meta-systemd', 'meta-oe', 'meta-python']
        }

    # scripts folder is a mix of oe-core and poky, most is oe-core code except:
    poky_scripts = ['scripts/yocto-bsp', 'scripts/yocto-kernel', 'scripts/yocto-layer', 'scripts/lib/bsp']

    Project = collections.namedtuple('Project', ['name', 'listemail', 'gitrepo', 'paths'])

    bitbake = Project(name='Bitbake', listemail='bitbake-devel@lists.openembedded.org', gitrepo='http://git.openembedded.org/bitbake/', paths=paths['bitbake'])
    doc     = Project(name='Documentantion', listemail='yocto@yoctoproject.org', gitrepo='http://git.yoctoproject.org/cgit/cgit.cgi/yocto-docs/', paths=paths['documentation'])
    poky    = Project(name='Poky', listemail='poky@yoctoproject.org', gitrepo='http://git.yoctoproject.org/cgit/cgit.cgi/poky/', paths=paths['poky'])
    oe      = Project(name='oe', listemail='openembedded-devel@lists.openembedded.org', gitrepo='http://git.openembedded.org/meta-openembedded/', paths=paths['oe'])


    def test_target_mailing_list(self):
        """In case of merge failure, check for other targeted projects"""
        if PatchTestInput.repo.ismerged:
            self.skip('Series merged, no reason to check other mailing lists')

        # a meta project may be indicted in the message subject, if this is the case, just fail
        # TODO: there may be other project with no-meta prefix, we also need to detect these
        project_regex = re.compile("\[(?P<project>meta-.+)\]")
        for commit in MailingList.commits:
            match = project_regex.match(commit.subject)
            if match:
                self.fail('Series sent to the wrong mailing list',
                          'Check the project\'s README (%s) and send the patch to the indicated list' % match.group('project'),
                          commit)

        for patch in self.patchset:
            folders = patch.path.split('/')
            base_path = folders[0]
            for project in [self.bitbake, self.doc, self.oe, self.poky]:
                if base_path in  project.paths:
                    self.fail('Series sent to the wrong mailing list or some patches from the series correspond to different mailing lists', 'Send the series again to the correct mailing list (ML)',
                              data=[('Suggested ML', '%s [%s]' % (project.listemail, project.gitrepo)),
                                    ('Patch\'s path:', patch.path)])

            # check for poky's scripts code
            if base_path.startswith('scripts'):
                for poky_file in self.poky_scripts:
                    if patch.path.startswith(poky_file):
                        self.fail('Series sent to the wrong mailing list or some patches from the series correspond to different mailing lists', 'Send the series again to the correct mailing list (ML)',
                                  data=[('Suggested ML', '%s [%s]' % (self.poky.listemail, self.poky.gitrepo)),('Patch\'s path:', patch.path)])
