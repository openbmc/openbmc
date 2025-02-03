"""
BitBake 'Fetch' implementation for Go modules

The gomod/gomodgit fetchers are used to download Go modules to the module cache
from a module proxy or directly from a version control repository.

Example SRC_URI:

SRC_URI += "gomod://golang.org/x/net;version=v0.9.0;sha256sum=..."
SRC_URI += "gomodgit://golang.org/x/net;version=v0.9.0;repo=go.googlesource.com/net;srcrev=..."

Required SRC_URI parameters:

- version
    The version of the module.

Optional SRC_URI parameters:

- mod
    Fetch and unpack the go.mod file only instead of the complete module.
    The go command may need to download go.mod files for many different modules
    when computing the build list, and go.mod files are much smaller than
    module zip files.
    The default is "0", set mod=1 for the go.mod file only.

- sha256sum
    The checksum of the module zip file, or the go.mod file in case of fetching
    only the go.mod file. Alternatively, set the SRC_URI varible flag for
    "module@version.sha256sum".

- protocol
    The method used when fetching directly from a version control repository.
    The default is "https" for git.

- repo
    The URL when fetching directly from a version control repository. Required
    when the URL is different from the module path.

- srcrev
    The revision identifier used when fetching directly from a version control
    repository. Alternatively, set the SRCREV varible for "module@version".

- subdir
    The module subdirectory when fetching directly from a version control
    repository. Required when the module is not located in the root of the
    repository.

Related variables:

- GO_MOD_PROXY
    The module proxy used by the fetcher.

- GO_MOD_CACHE_DIR
    The directory where the module cache is located.
    This must match the exported GOMODCACHE variable for the go command to find
    the downloaded modules.

See the Go modules reference, https://go.dev/ref/mod, for more information
about the module cache, module proxies and version control systems.
"""

import hashlib
import os
import re
import shutil
import subprocess
import zipfile

import bb
from bb.fetch2 import FetchError
from bb.fetch2 import MissingParameterError
from bb.fetch2 import runfetchcmd
from bb.fetch2 import subprocess_setup
from bb.fetch2.git import Git
from bb.fetch2.wget import Wget


def escape(path):
    """Escape capital letters using exclamation points."""
    return re.sub(r'([A-Z])', lambda m: '!' + m.group(1).lower(), path)


class GoMod(Wget):
    """Class to fetch Go modules from a Go module proxy via wget"""

    def supports(self, ud, d):
        """Check to see if a given URL is for this fetcher."""
        return ud.type == 'gomod'

    def urldata_init(self, ud, d):
        """Set up to download the module from the module proxy.

        Set up to download the module zip file to the module cache directory
        and unpack the go.mod file (unless downloading only the go.mod file):

        cache/download/<module>/@v/<version>.zip: The module zip file.
        cache/download/<module>/@v/<version>.mod: The go.mod file.
        """

        proxy = d.getVar('GO_MOD_PROXY') or 'proxy.golang.org'
        moddir = d.getVar('GO_MOD_CACHE_DIR') or 'pkg/mod'

        if 'version' not in ud.parm:
            raise MissingParameterError('version', ud.url)

        module = ud.host
        if ud.path != '/':
            module += ud.path
        ud.parm['module'] = module

        # Set URL and filename for wget download
        path = escape(module + '/@v/' + ud.parm['version'])
        if ud.parm.get('mod', '0') == '1':
            path += '.mod'
        else:
            path += '.zip'
            ud.parm['unpack'] = '0'
        ud.url = bb.fetch2.encodeurl(
            ('https', proxy, '/' + path, None, None, None))
        ud.parm['downloadfilename'] = path

        ud.parm['name'] = f"{module}@{ud.parm['version']}"

        # Set subdir for unpack
        ud.parm['subdir'] = os.path.join(moddir, 'cache/download',
                                         os.path.dirname(path))

        super().urldata_init(ud, d)

    def unpack(self, ud, rootdir, d):
        """Unpack the module in the module cache."""

        # Unpack the module zip file or go.mod file
        super().unpack(ud, rootdir, d)

        if ud.localpath.endswith('.zip'):
            # Unpack the go.mod file from the zip file
            module = ud.parm['module']
            unpackdir = os.path.join(rootdir, ud.parm['subdir'])
            name = os.path.basename(ud.localpath).rsplit('.', 1)[0] + '.mod'
            bb.note(f"Unpacking {name} to {unpackdir}/")
            with zipfile.ZipFile(ud.localpath) as zf:
                with open(os.path.join(unpackdir, name), mode='wb') as mf:
                    try:
                        f = module + '@' + ud.parm['version'] + '/go.mod'
                        shutil.copyfileobj(zf.open(f), mf)
                    except KeyError:
                        # If the module does not have a go.mod file, synthesize
                        # one containing only a module statement.
                        mf.write(f'module {module}\n'.encode())


class GoModGit(Git):
    """Class to fetch Go modules directly from a git repository"""

    def supports(self, ud, d):
        """Check to see if a given URL is for this fetcher."""
        return ud.type == 'gomodgit'

    def urldata_init(self, ud, d):
        """Set up to download the module from the git repository.

        Set up to download the git repository to the module cache directory and
        unpack the module zip file and the go.mod file:

        cache/vcs/<hash>:                         The bare git repository.
        cache/download/<module>/@v/<version>.zip: The module zip file.
        cache/download/<module>/@v/<version>.mod: The go.mod file.
        """

        moddir = d.getVar('GO_MOD_CACHE_DIR') or 'pkg/mod'

        if 'version' not in ud.parm:
            raise MissingParameterError('version', ud.url)

        module = ud.host
        if ud.path != '/':
            module += ud.path
        ud.parm['module'] = module

        # Set host, path and srcrev for git download
        if 'repo' in ud.parm:
            repo = ud.parm['repo']
            idx = repo.find('/')
            if idx != -1:
                ud.host = repo[:idx]
                ud.path = repo[idx:]
            else:
                ud.host = repo
                ud.path = ''
        if 'protocol' not in ud.parm:
            ud.parm['protocol'] = 'https'
        name = f"{module}@{ud.parm['version']}"
        ud.names = [name]
        srcrev = d.getVar('SRCREV_' + name)
        if srcrev:
            if 'srcrev' not in ud.parm:
                ud.parm['srcrev'] = srcrev
        else:
            if 'srcrev' in ud.parm:
                d.setVar('SRCREV_' + name, ud.parm['srcrev'])
        if 'branch' not in ud.parm:
            ud.parm['nobranch'] = '1'

        # Set subpath, subdir and bareclone for git unpack
        if 'subdir' in ud.parm:
            ud.parm['subpath'] = ud.parm['subdir']
        key = f"git3:{ud.parm['protocol']}://{ud.host}{ud.path}".encode()
        ud.parm['key'] = key
        ud.parm['subdir'] = os.path.join(moddir, 'cache/vcs',
                                         hashlib.sha256(key).hexdigest())
        ud.parm['bareclone'] = '1'

        super().urldata_init(ud, d)

    def unpack(self, ud, rootdir, d):
        """Unpack the module in the module cache."""

        # Unpack the bare git repository
        super().unpack(ud, rootdir, d)

        moddir = d.getVar('GO_MOD_CACHE_DIR') or 'pkg/mod'

        # Create the info file
        module = ud.parm['module']
        repodir = os.path.join(rootdir, ud.parm['subdir'])
        with open(repodir + '.info', 'wb') as f:
            f.write(ud.parm['key'])

        # Unpack the go.mod file from the repository
        unpackdir = os.path.join(rootdir, moddir, 'cache/download',
                                 escape(module), '@v')
        bb.utils.mkdirhier(unpackdir)
        srcrev = ud.parm['srcrev']
        version = ud.parm['version']
        escaped_version = escape(version)
        cmd = f"git ls-tree -r --name-only '{srcrev}'"
        if 'subpath' in ud.parm:
            cmd += f" '{ud.parm['subpath']}'"
        files = runfetchcmd(cmd, d, workdir=repodir).split()
        name = escaped_version + '.mod'
        bb.note(f"Unpacking {name} to {unpackdir}/")
        with open(os.path.join(unpackdir, name), mode='wb') as mf:
            f = 'go.mod'
            if 'subpath' in ud.parm:
                f = os.path.join(ud.parm['subpath'], f)
            if f in files:
                cmd = ['git', 'cat-file', 'blob', srcrev + ':' + f]
                subprocess.check_call(cmd, stdout=mf, cwd=repodir,
                                      preexec_fn=subprocess_setup)
            else:
                # If the module does not have a go.mod file, synthesize one
                # containing only a module statement.
                mf.write(f'module {module}\n'.encode())

        # Synthesize the module zip file from the repository
        name = escaped_version + '.zip'
        bb.note(f"Unpacking {name} to {unpackdir}/")
        with zipfile.ZipFile(os.path.join(unpackdir, name), mode='w') as zf:
            prefix = module + '@' + version + '/'
            for f in files:
                cmd = ['git', 'cat-file', 'blob', srcrev + ':' + f]
                data = subprocess.check_output(cmd, cwd=repodir,
                                               preexec_fn=subprocess_setup)
                zf.writestr(prefix + f, data)
