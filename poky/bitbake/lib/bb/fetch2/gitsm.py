# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' git submodules implementation

Inherits from and extends the Git fetcher to retrieve submodules of a git repository
after cloning.

SRC_URI = "gitsm://<see Git fetcher for syntax>"

See the Git fetcher, git://, for usage documentation.

NOTE: Switching a SRC_URI from "git://" to "gitsm://" requires a clean of your recipe.

"""

# Copyright (C) 2013 Richard Purdie
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import os
import bb
import copy
from   bb.fetch2.git import Git
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger
from   bb.fetch2 import Fetch
from   bb.fetch2 import BBFetchException

class GitSM(Git):
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with git.
        """
        return ud.type in ['gitsm']

    @staticmethod
    def parse_gitmodules(gitmodules):
        modules = {}
        module = ""
        for line in gitmodules.splitlines():
            if line.startswith('[submodule'):
                module = line.split('"')[1]
                modules[module] = {}
            elif module and line.strip().startswith('path'):
                path = line.split('=')[1].strip()
                modules[module]['path'] = path
            elif module and line.strip().startswith('url'):
                url = line.split('=')[1].strip()
                modules[module]['url'] = url
        return modules

    def update_submodules(self, ud, d):
        submodules = []
        paths = {}
        uris = {}
        local_paths = {}

        for name in ud.names:
            try:
                gitmodules = runfetchcmd("%s show %s:.gitmodules" % (ud.basecmd, ud.revisions[name]), d, quiet=True, workdir=ud.clonedir)
            except:
                # No submodules to update
                continue

            for m, md in self.parse_gitmodules(gitmodules).items():
                submodules.append(m)
                paths[m] = md['path']
                uris[m] = md['url']
                if uris[m].startswith('..'):
                    newud = copy.copy(ud)
                    newud.path = os.path.realpath(os.path.join(newud.path, md['url']))
                    uris[m] = Git._get_repo_url(self, newud)

        for module in submodules:
            module_hash = runfetchcmd("%s ls-tree -z -d %s %s" % (ud.basecmd, ud.revisions[name], paths[module]), d, quiet=True, workdir=ud.clonedir)
            module_hash = module_hash.split()[2]

            # Build new SRC_URI
            proto = uris[module].split(':', 1)[0]
            url = uris[module].replace('%s:' % proto, 'gitsm:', 1)
            url += ';protocol=%s' % proto
            url += ";name=%s" % module
            url += ";bareclone=1;nocheckout=1;nobranch=1"

            ld = d.createCopy()
            # Not necessary to set SRC_URI, since we're passing the URI to
            # Fetch.
            #ld.setVar('SRC_URI', url)
            ld.setVar('SRCREV_%s' % module, module_hash)

            # Workaround for issues with SRCPV/SRCREV_FORMAT errors
            # error refer to 'multiple' repositories.  Only the repository
            # in the original SRC_URI actually matters...
            ld.setVar('SRCPV', d.getVar('SRCPV'))
            ld.setVar('SRCREV_FORMAT', module)

            newfetch = Fetch([url], ld, cache=False)
            newfetch.download()
            local_paths[module] = newfetch.localpath(url)

            # Correct the submodule references to the local download version...
            runfetchcmd("%(basecmd)s config submodule.%(module)s.url %(url)s" % {'basecmd': ud.basecmd, 'module': module, 'url' : local_paths[module]}, d, workdir=ud.clonedir)

            symlink_path = os.path.join(ud.clonedir, 'modules', paths[module])
            if not os.path.exists(symlink_path):
                try:
                    os.makedirs(os.path.dirname(symlink_path), exist_ok=True)
                except OSError:
                    pass
                os.symlink(local_paths[module], symlink_path)

        return True

    def need_update(self, ud, d):
        main_repo_needs_update = Git.need_update(self, ud, d)

        # First check that the main repository has enough history fetched. If it doesn't, then we don't
        # even have the .gitmodules and gitlinks for the submodules to attempt asking whether the
        # submodules' histories are recent enough.
        if main_repo_needs_update:
            return True

        # Now check that the submodule histories are new enough. The git-submodule command doesn't have
        # any clean interface for doing this aside from just attempting the checkout (with network
        # fetched disabled).
        return not self.update_submodules(ud, d)

    def download(self, ud, d):
        Git.download(self, ud, d)

        if not ud.shallow or ud.localpath != ud.fullshallow:
            self.update_submodules(ud, d)

    def copy_submodules(self, submodules, ud, destdir, d):
        if ud.bareclone:
            repo_conf = destdir
        else:
            repo_conf = os.path.join(destdir, '.git')

        if submodules and not os.path.exists(os.path.join(repo_conf, 'modules')):
            os.mkdir(os.path.join(repo_conf, 'modules'))

        for module, md in submodules.items():
            srcpath = os.path.join(ud.clonedir, 'modules', md['path'])
            modpath = os.path.join(repo_conf, 'modules', md['path'])

            if os.path.exists(srcpath):
                if os.path.exists(os.path.join(srcpath, '.git')):
                    srcpath = os.path.join(srcpath, '.git')

                target = modpath
                if os.path.exists(modpath):
                    target = os.path.dirname(modpath)

                os.makedirs(os.path.dirname(target), exist_ok=True)
                runfetchcmd("cp -fpLR %s %s" % (srcpath, target), d)
            elif os.path.exists(modpath):
                # Module already exists, likely unpacked from a shallow mirror clone
                pass
            else:
                # This is fatal, as we do NOT want git-submodule to hit the network
                raise bb.fetch2.FetchError('Submodule %s does not exist in %s or %s.' % (module, srcpath, modpath))

    def clone_shallow_local(self, ud, dest, d):
        super(GitSM, self).clone_shallow_local(ud, dest, d)

        # Copy over the submodules' fetched histories too.
        repo_conf = os.path.join(dest, '.git')

        submodules = []
        for name in ud.names:
            try:
                gitmodules = runfetchcmd("%s show %s:.gitmodules" % (ud.basecmd, ud.revision), d, quiet=True, workdir=dest)
            except:
                # No submodules to update
                continue

            submodules = self.parse_gitmodules(gitmodules)
            self.copy_submodules(submodules, ud, dest, d)

    def unpack(self, ud, destdir, d):
        Git.unpack(self, ud, destdir, d)

        # Copy over the submodules' fetched histories too.
        if ud.bareclone:
            repo_conf = ud.destdir
        else:
            repo_conf = os.path.join(ud.destdir, '.git')

        update_submodules = False
        paths = {}
        uris = {}
        local_paths = {}
        for name in ud.names:
            try:
                gitmodules = runfetchcmd("%s show HEAD:.gitmodules" % (ud.basecmd), d, quiet=True, workdir=ud.destdir)
            except:
                # No submodules to update
                continue

            submodules = self.parse_gitmodules(gitmodules)
            self.copy_submodules(submodules, ud, ud.destdir, d)

            submodules_queue = [(module, os.path.join(repo_conf, 'modules', md['path'])) for module, md in submodules.items()]
            while len(submodules_queue) != 0:
                module, modpath = submodules_queue.pop()

                # add submodule children recursively
                try:
                    gitmodules = runfetchcmd("%s show HEAD:.gitmodules" % (ud.basecmd), d, quiet=True, workdir=modpath)
                    for m, md in self.parse_gitmodules(gitmodules).items():
                        submodules_queue.append([m, os.path.join(modpath, 'modules', md['path'])])
                except:
                    # no children
                    pass


                # There are submodules to update
                update_submodules = True

                # Determine (from the submodule) the correct url to reference
                try:
                    output = runfetchcmd("%(basecmd)s config remote.origin.url" % {'basecmd': ud.basecmd}, d, workdir=modpath)
                except bb.fetch2.FetchError as e:
                    # No remote url defined in this submodule
                    continue

                local_paths[module] = output

                # Setup the local URL properly (like git submodule init or sync would do...)
                runfetchcmd("%(basecmd)s config submodule.%(module)s.url %(url)s" % {'basecmd': ud.basecmd, 'module': module, 'url' : local_paths[module]}, d, workdir=ud.destdir)

                # Ensure the submodule repository is NOT set to bare, since we're checking it out...
                runfetchcmd("%s config core.bare false" % (ud.basecmd), d, quiet=True, workdir=modpath)

        if update_submodules:
            # Run submodule update, this sets up the directories -- without touching the config
            runfetchcmd("%s submodule update --recursive --no-fetch" % (ud.basecmd), d, quiet=True, workdir=ud.destdir)
