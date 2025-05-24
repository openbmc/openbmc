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
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import bb
import copy
import shutil
import tempfile
from   bb.fetch2.git import Git
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger
from   bb.fetch2 import Fetch

class GitSM(Git):
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with git.
        """
        return ud.type in ['gitsm']

    def process_submodules(self, ud, workdir, function, d):
        """
        Iterate over all of the submodules in this repository and execute
        the 'function' for each of them.
        """

        submodules = []
        paths = {}
        revision = {}
        uris = {}
        subrevision = {}

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

        # Collect the defined submodules, and their attributes
        try:
            gitmodules = runfetchcmd("%s show %s:.gitmodules" % (ud.basecmd, ud.revision), d, quiet=True, workdir=workdir)
        except:
            # No submodules to update
            gitmodules = ""

        for m, md in parse_gitmodules(gitmodules).items():
            try:
                module_hash = runfetchcmd("%s ls-tree -z -d %s %s" % (ud.basecmd, ud.revision, md['path']), d, quiet=True, workdir=workdir)
            except:
                # If the command fails, we don't have a valid file to check.  If it doesn't
                # fail -- it still might be a failure, see next check...
                module_hash = ""

            if not module_hash:
                logger.debug("submodule %s is defined, but is not initialized in the repository. Skipping", m)
                continue

            submodules.append(m)
            paths[m] = md['path']
            revision[m] = ud.revision
            uris[m] = md['url']
            subrevision[m] = module_hash.split()[2]

            # Convert relative to absolute uri based on parent uri
            if  uris[m].startswith('..') or uris[m].startswith('./'):
                newud = copy.copy(ud)
                newud.path = os.path.normpath(os.path.join(newud.path, uris[m]))
                uris[m] = Git._get_repo_url(self, newud)

        for module in submodules:
            # Translate the module url into a SRC_URI

            if "://" in uris[module]:
                # Properly formated URL already
                proto = uris[module].split(':', 1)[0]
                url = uris[module].replace('%s:' % proto, 'gitsm:', 1)
            else:
                if ":" in uris[module]:
                    # Most likely an SSH style reference
                    proto = "ssh"
                    if ":/" in uris[module]:
                        # Absolute reference, easy to convert..
                        url = "gitsm://" + uris[module].replace(':/', '/', 1)
                    else:
                        # Relative reference, no way to know if this is right!
                        logger.warning("Submodule included by %s refers to relative ssh reference %s.  References may fail if not absolute." % (ud.url, uris[module]))
                        url = "gitsm://" + uris[module].replace(':', '/', 1)
                else:
                    # This has to be a file reference
                    proto = "file"
                    url = "gitsm://" + uris[module]
            if url.endswith("{}{}".format(ud.host, ud.path)):
                raise bb.fetch2.FetchError("Submodule refers to the parent repository. This will cause deadlock situation in current version of Bitbake." \
                                           "Consider using git fetcher instead.")

            url += ';protocol=%s' % proto
            url += ";name=%s" % module
            url += ";subpath=%s" % module
            url += ";nobranch=1"
            url += ";lfs=%s" % self._need_lfs(ud)
            # Note that adding "user=" here to give credentials to the
            # submodule is not supported. Since using SRC_URI to give git://
            # URL a password is not supported, one have to use one of the
            # recommended way (eg. ~/.netrc or SSH config) which does specify
            # the user (See comment in git.py).
            # So, we will not take patches adding "user=" support here.

            ld = d.createCopy()
            # Not necessary to set SRC_URI, since we're passing the URI to
            # Fetch.
            #ld.setVar('SRC_URI', url)
            ld.setVar('SRCREV_%s' % module, subrevision[module])

            # Workaround for issues with SRCPV/SRCREV_FORMAT errors
            # error refer to 'multiple' repositories.  Only the repository
            # in the original SRC_URI actually matters...
            ld.setVar('SRCPV', d.getVar('SRCPV'))
            ld.setVar('SRCREV_FORMAT', module)

            function(ud, url, module, paths[module], workdir, ld)

        return submodules != []

    def call_process_submodules(self, ud, d, extra_check, subfunc):
        # If we're using a shallow mirror tarball it needs to be
        # unpacked temporarily so that we can examine the .gitmodules file
        # Unpack even when ud.clonedir is not available,
        # which may occur during a fast shallow clone
        unpack = extra_check or not os.path.exists(ud.clonedir)
        if ud.shallow and os.path.exists(ud.fullshallow) and unpack:
            tmpdir = tempfile.mkdtemp(dir=d.getVar("DL_DIR"))
            try:
                runfetchcmd("tar -xzf %s" % ud.fullshallow, d, workdir=tmpdir)
                self.process_submodules(ud, tmpdir, subfunc, d)
            finally:
                shutil.rmtree(tmpdir)
        else:
            self.process_submodules(ud, ud.clonedir, subfunc, d)

    def need_update(self, ud, d):
        if Git.need_update(self, ud, d):
            return True

        need_update_list = []
        def need_update_submodule(ud, url, module, modpath, workdir, d):
            url += ";bareclone=1;nobranch=1"

            try:
                newfetch = Fetch([url], d, cache=False)
                new_ud = newfetch.ud[url]
                if new_ud.method.need_update(new_ud, d):
                    need_update_list.append(modpath)
            except Exception as e:
                logger.error('gitsm: submodule update check failed: %s %s' % (type(e).__name__, str(e)))
                need_update_result = True

        self.call_process_submodules(ud, d, not os.path.exists(ud.clonedir), need_update_submodule)

        if need_update_list:
            logger.debug('gitsm: Submodules requiring update: %s' % (' '.join(need_update_list)))
            return True

        return False

    def download(self, ud, d):
        def download_submodule(ud, url, module, modpath, workdir, d):
            url += ";bareclone=1;nobranch=1"

            # Is the following still needed?
            #url += ";nocheckout=1"

            try:
                newfetch = Fetch([url], d, cache=False)
                newfetch.download()
            except Exception as e:
                logger.error('gitsm: submodule download failed: %s %s' % (type(e).__name__, str(e)))
                raise

        Git.download(self, ud, d)
        self.call_process_submodules(ud, d, self.need_update(ud, d), download_submodule)

    def unpack(self, ud, destdir, d):
        def unpack_submodules(ud, url, module, modpath, workdir, d):
            url += ";bareclone=1;nobranch=1"

            # Figure out where we clone over the bare submodules...
            if ud.bareclone:
                repo_conf = ud.destdir
            else:
                repo_conf = os.path.join(ud.destdir, '.git')

            try:
                newfetch = Fetch([url], d, cache=False)
                # modpath is needed by unpack tracer to calculate submodule
                # checkout dir
                new_ud = newfetch.ud[url]
                new_ud.modpath = modpath
                newfetch.unpack(root=os.path.dirname(os.path.join(repo_conf, 'modules', module)))
            except Exception as e:
                logger.error('gitsm: submodule unpack failed: %s %s' % (type(e).__name__, str(e)))
                raise

            local_path = newfetch.localpath(url)

            # Correct the submodule references to the local download version...
            runfetchcmd("%(basecmd)s config submodule.%(module)s.url %(url)s" % {'basecmd': ud.basecmd, 'module': module, 'url' : local_path}, d, workdir=ud.destdir)

            if ud.shallow:
                runfetchcmd("%(basecmd)s config submodule.%(module)s.shallow true" % {'basecmd': ud.basecmd, 'module': module}, d, workdir=ud.destdir)

            # Ensure the submodule repository is NOT set to bare, since we're checking it out...
            try:
                runfetchcmd("%s config core.bare false" % (ud.basecmd), d, quiet=True, workdir=os.path.join(repo_conf, 'modules', module))
            except:
                logger.error("Unable to set git config core.bare to false for %s" % os.path.join(repo_conf, 'modules', module))
                raise

        Git.unpack(self, ud, destdir, d)

        ret = self.process_submodules(ud, ud.destdir, unpack_submodules, d)

        if not ud.bareclone and ret:
            # All submodules should already be downloaded and configured in the tree.  This simply
            # sets up the configuration and checks out the files.  The main project config should
            # remain unmodified, and no download from the internet should occur. As such, lfs smudge
            # should also be skipped as these files were already smudged in the fetch stage if lfs
            # was enabled.
            runfetchcmd("GIT_LFS_SKIP_SMUDGE=1 %s submodule update --recursive --no-fetch" % (ud.basecmd), d, quiet=True, workdir=ud.destdir)
    def clean(self, ud, d):
        def clean_submodule(ud, url, module, modpath, workdir, d):
            url += ";bareclone=1;nobranch=1"
            try:
                newfetch = Fetch([url], d, cache=False)
                newfetch.clean()
            except Exception as e:
                logger.warning('gitsm: submodule clean failed: %s %s' % (type(e).__name__, str(e)))

        self.call_process_submodules(ud, d, True, clean_submodule)

        # Clean top git dir
        Git.clean(self, ud, d)

    def implicit_urldata(self, ud, d):
        import subprocess

        urldata = []
        def add_submodule(ud, url, module, modpath, workdir, d):
            url += ";bareclone=1;nobranch=1"
            newfetch = Fetch([url], d, cache=False)
            urldata.extend(newfetch.expanded_urldata())

        self.call_process_submodules(ud, d, ud.method.need_update(ud, d), add_submodule)

        return urldata
