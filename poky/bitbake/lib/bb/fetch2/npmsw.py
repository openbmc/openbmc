# Copyright (C) 2020 Savoir-Faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""
BitBake 'Fetch' npm shrinkwrap implementation

npm fetcher support the SRC_URI with format of:
SRC_URI = "npmsw://some.registry.url;OptionA=xxx;OptionB=xxx;..."

Supported SRC_URI options are:

- dev
   Set to 1 to also install devDependencies.

- destsuffix
    Specifies the directory to use to unpack the dependencies (default: ${S}).
"""

import json
import os
import re
import bb
from bb.fetch2 import Fetch
from bb.fetch2 import FetchMethod
from bb.fetch2 import ParameterError
from bb.fetch2 import URI
from bb.fetch2.npm import npm_integrity
from bb.fetch2.npm import npm_localfile
from bb.fetch2.npm import npm_unpack
from bb.utils import is_semver

def foreach_dependencies(shrinkwrap, callback=None, dev=False):
    """
        Run a callback for each dependencies of a shrinkwrap file.
        The callback is using the format:
            callback(name, params, deptree)
        with:
            name = the package name (string)
            params = the package parameters (dictionary)
            deptree = the package dependency tree (array of strings)
    """
    def _walk_deps(deps, deptree):
        for name in deps:
            subtree = [*deptree, name]
            _walk_deps(deps[name].get("dependencies", {}), subtree)
            if callback is not None:
                if deps[name].get("dev", False) and not dev:
                    continue
                elif deps[name].get("bundled", False):
                    continue
                callback(name, deps[name], subtree)

    _walk_deps(shrinkwrap.get("dependencies", {}), [])

class NpmShrinkWrap(FetchMethod):
    """Class to fetch all package from a shrinkwrap file"""

    def supports(self, ud, d):
        """Check if a given url can be fetched with npmsw"""
        return ud.type in ["npmsw"]

    def urldata_init(self, ud, d):
        """Init npmsw specific variables within url data"""

        # Get the 'shrinkwrap' parameter
        ud.shrinkwrap_file = re.sub(r"^npmsw://", "", ud.url.split(";")[0])

        # Get the 'dev' parameter
        ud.dev = bb.utils.to_boolean(ud.parm.get("dev"), False)

        # Resolve the dependencies
        ud.deps = []

        def _resolve_dependency(name, params, deptree):
            url = None
            localpath = None
            extrapaths = []
            destsubdirs = [os.path.join("node_modules", dep) for dep in deptree]
            destsuffix = os.path.join(*destsubdirs)

            integrity = params.get("integrity", None)
            resolved = params.get("resolved", None)
            version = params.get("version", None)

            # Handle registry sources
            if is_semver(version) and resolved and integrity:
                localfile = npm_localfile(name, version)

                uri = URI(resolved)
                uri.params["downloadfilename"] = localfile

                checksum_name, checksum_expected = npm_integrity(integrity)
                uri.params[checksum_name] = checksum_expected

                url = str(uri)

                localpath = os.path.join(d.getVar("DL_DIR"), localfile)

                # Create a resolve file to mimic the npm fetcher and allow
                # re-usability of the downloaded file.
                resolvefile = localpath + ".resolved"

                bb.utils.mkdirhier(os.path.dirname(resolvefile))
                with open(resolvefile, "w") as f:
                    f.write(url)

                extrapaths.append(resolvefile)

            # Handle http tarball sources
            elif version.startswith("http") and integrity:
                localfile = os.path.join("npm2", os.path.basename(version))

                uri = URI(version)
                uri.params["downloadfilename"] = localfile

                checksum_name, checksum_expected = npm_integrity(integrity)
                uri.params[checksum_name] = checksum_expected

                url = str(uri)

                localpath = os.path.join(d.getVar("DL_DIR"), localfile)

            # Handle git sources
            elif version.startswith("git"):
                regex = re.compile(r"""
                    ^
                    git\+
                    (?P<protocol>[a-z]+)
                    ://
                    (?P<url>[^#]+)
                    \#
                    (?P<rev>[0-9a-f]+)
                    $
                    """, re.VERBOSE)

                match = regex.match(version)

                if not match:
                    raise ParameterError("Invalid git url: %s" % version, ud.url)

                groups = match.groupdict()

                uri = URI("git://" + str(groups["url"]))
                uri.params["protocol"] = str(groups["protocol"])
                uri.params["rev"] = str(groups["rev"])
                uri.params["destsuffix"] = destsuffix

                url = str(uri)

            # local tarball sources and local link sources are unsupported
            else:
                raise ParameterError("Unsupported dependency: %s" % name, ud.url)

            ud.deps.append({
                "url": url,
                "localpath": localpath,
                "extrapaths": extrapaths,
                "destsuffix": destsuffix,
            })

        try:
            with open(ud.shrinkwrap_file, "r") as f:
                shrinkwrap = json.load(f)
        except Exception as e:
            raise ParameterError("Invalid shrinkwrap file: %s" % str(e), ud.url)

        foreach_dependencies(shrinkwrap, _resolve_dependency, ud.dev)

        # Avoid conflicts between the environment data and:
        # - the proxy url revision
        # - the proxy url checksum
        data = bb.data.createCopy(d)
        data.delVar("SRCREV")
        data.delVarFlags("SRC_URI")

        # This fetcher resolves multiple URIs from a shrinkwrap file and then
        # forwards it to a proxy fetcher. The management of the donestamp file,
        # the lockfile and the checksums are forwarded to the proxy fetcher.
        ud.proxy = Fetch([dep["url"] for dep in ud.deps], data)
        ud.needdonestamp = False

    @staticmethod
    def _foreach_proxy_method(ud, handle):
        returns = []
        for proxy_url in ud.proxy.urls:
            proxy_ud = ud.proxy.ud[proxy_url]
            proxy_d = ud.proxy.d
            proxy_ud.setup_localpath(proxy_d)
            returns.append(handle(proxy_ud.method, proxy_ud, proxy_d))
        return returns

    def verify_donestamp(self, ud, d):
        """Verify the donestamp file"""
        def _handle(m, ud, d):
            return m.verify_donestamp(ud, d)
        return all(self._foreach_proxy_method(ud, _handle))

    def update_donestamp(self, ud, d):
        """Update the donestamp file"""
        def _handle(m, ud, d):
            m.update_donestamp(ud, d)
        self._foreach_proxy_method(ud, _handle)

    def need_update(self, ud, d):
        """Force a fetch, even if localpath exists ?"""
        def _handle(m, ud, d):
            return m.need_update(ud, d)
        return all(self._foreach_proxy_method(ud, _handle))

    def try_mirrors(self, fetch, ud, d, mirrors):
        """Try to use a mirror"""
        def _handle(m, ud, d):
            return m.try_mirrors(fetch, ud, d, mirrors)
        return all(self._foreach_proxy_method(ud, _handle))

    def download(self, ud, d):
        """Fetch url"""
        ud.proxy.download()

    def unpack(self, ud, rootdir, d):
        """Unpack the downloaded dependencies"""
        destdir = d.getVar("S")
        destsuffix = ud.parm.get("destsuffix")
        if destsuffix:
            destdir = os.path.join(rootdir, destsuffix)

        bb.utils.mkdirhier(destdir)
        bb.utils.copyfile(ud.shrinkwrap_file,
                          os.path.join(destdir, "npm-shrinkwrap.json"))

        auto = [dep["url"] for dep in ud.deps if not dep["localpath"]]
        manual = [dep for dep in ud.deps if dep["localpath"]]

        if auto:
            ud.proxy.unpack(destdir, auto)

        for dep in manual:
            depdestdir = os.path.join(destdir, dep["destsuffix"])
            npm_unpack(dep["localpath"], depdestdir, d)

    def clean(self, ud, d):
        """Clean any existing full or partial download"""
        ud.proxy.clean()

        # Clean extra files
        for dep in ud.deps:
            for path in dep["extrapaths"]:
                bb.utils.remove(path)

    def done(self, ud, d):
        """Is the download done ?"""
        def _handle(m, ud, d):
            return m.done(ud, d)
        return all(self._foreach_proxy_method(ud, _handle))
