# Copyright (C) 2020 Savoir-Faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""
BitBake 'Fetch' npm implementation

npm fetcher support the SRC_URI with format of:
SRC_URI = "npm://some.registry.url;OptionA=xxx;OptionB=xxx;..."

Supported SRC_URI options are:

- package
   The npm package name. This is a mandatory parameter.

- version
    The npm package version. This is a mandatory parameter.

- downloadfilename
    Specifies the filename used when storing the downloaded file.

- destsuffix
    Specifies the directory to use to unpack the package (default: npm).
"""

import base64
import json
import os
import re
import shlex
import tempfile
import bb
from bb.fetch2 import Fetch
from bb.fetch2 import FetchError
from bb.fetch2 import FetchMethod
from bb.fetch2 import MissingParameterError
from bb.fetch2 import ParameterError
from bb.fetch2 import URI
from bb.fetch2 import check_network_access
from bb.fetch2 import runfetchcmd
from bb.utils import is_semver

def npm_package(package):
    """Convert the npm package name to remove unsupported character"""
    # For scoped package names ('@user/package') the '/' is replaced by a '-'.
    # This is similar to what 'npm pack' does, but 'npm pack' also strips the
    # leading '@', which can lead to ambiguous package names.
    name = re.sub("/", "-", package)
    name = name.lower()
    name = re.sub(r"[^\-a-z0-9@]", "", name)
    name = name.strip("-")
    return name


def npm_filename(package, version):
    """Get the filename of a npm package"""
    return npm_package(package) + "-" + version + ".tgz"

def npm_localfile(package, version=None):
    """Get the local filename of a npm package"""
    if version is not None:
        filename = npm_filename(package, version)
    else:
        filename = package
    return os.path.join("npm2", filename)

def npm_integrity(integrity):
    """
    Get the checksum name and expected value from the subresource integrity
        https://www.w3.org/TR/SRI/
    """
    algo, value = integrity.split("-", maxsplit=1)
    return "%ssum" % algo, base64.b64decode(value).hex()

def npm_unpack(tarball, destdir, d):
    """Unpack a npm tarball"""
    bb.utils.mkdirhier(destdir)
    cmd = "tar --extract --gzip --file=%s" % shlex.quote(tarball)
    cmd += " --no-same-owner"
    cmd += " --delay-directory-restore"
    cmd += " --strip-components=1"
    runfetchcmd(cmd, d, workdir=destdir)
    runfetchcmd("chmod -R +X '%s'" % (destdir), d, quiet=True, workdir=destdir)

class NpmEnvironment(object):
    """
    Using a npm config file seems more reliable than using cli arguments.
    This class allows to create a controlled environment for npm commands.
    """
    def __init__(self, d, configs=[], npmrc=None):
        self.d = d

        self.user_config = tempfile.NamedTemporaryFile(mode="w", buffering=1)
        for key, value in configs:
            self.user_config.write("%s=%s\n" % (key, value))

        if npmrc:
            self.global_config_name = npmrc
        else:
            self.global_config_name = "/dev/null"

    def __del__(self):
        if self.user_config:
            self.user_config.close()

    def run(self, cmd, args=None, configs=None, workdir=None):
        """Run npm command in a controlled environment"""
        with tempfile.TemporaryDirectory() as tmpdir:
            d = bb.data.createCopy(self.d)
            d.setVar("PATH", d.getVar("PATH"))  # PATH might contain $HOME - evaluate it before patching
            d.setVar("HOME", tmpdir)

            if not workdir:
                workdir = tmpdir

            def _run(cmd):
                cmd = "NPM_CONFIG_USERCONFIG=%s " % (self.user_config.name) + cmd
                cmd = "NPM_CONFIG_GLOBALCONFIG=%s " % (self.global_config_name) + cmd
                return runfetchcmd(cmd, d, workdir=workdir)

            if configs:
                bb.warn("Use of configs argument of NpmEnvironment.run() function"
                    " is deprecated. Please use args argument instead.")
                for key, value in configs:
                    cmd += " --%s=%s" % (key, shlex.quote(value))

            if args:
                for key, value in args:
                    cmd += " --%s=%s" % (key, shlex.quote(value))

            return _run(cmd)

class Npm(FetchMethod):
    """Class to fetch a package from a npm registry"""

    def supports(self, ud, d):
        """Check if a given url can be fetched with npm"""
        return ud.type in ["npm"]

    def urldata_init(self, ud, d):
        """Init npm specific variables within url data"""
        ud.package = None
        ud.version = None
        ud.registry = None

        # Get the 'package' parameter
        if "package" in ud.parm:
            ud.package = ud.parm.get("package")

        if not ud.package:
            raise MissingParameterError("Parameter 'package' required", ud.url)

        # Get the 'version' parameter
        if "version" in ud.parm:
            ud.version = ud.parm.get("version")

        if not ud.version:
            raise MissingParameterError("Parameter 'version' required", ud.url)

        if not is_semver(ud.version) and not ud.version == "latest":
            raise ParameterError("Invalid 'version' parameter", ud.url)

        # Extract the 'registry' part of the url
        ud.registry = re.sub(r"^npm://", "https://", ud.url.split(";")[0])

        # Using the 'downloadfilename' parameter as local filename
        # or the npm package name.
        if "downloadfilename" in ud.parm:
            ud.localfile = npm_localfile(d.expand(ud.parm["downloadfilename"]))
        else:
            ud.localfile = npm_localfile(ud.package, ud.version)

        # Get the base 'npm' command
        ud.basecmd = d.getVar("FETCHCMD_npm") or "npm"

        # This fetcher resolves a URI from a npm package name and version and
        # then forwards it to a proxy fetcher. A resolve file containing the
        # resolved URI is created to avoid unwanted network access (if the file
        # already exists). The management of the donestamp file, the lockfile
        # and the checksums are forwarded to the proxy fetcher.
        ud.proxy = None
        ud.needdonestamp = False
        ud.resolvefile = self.localpath(ud, d) + ".resolved"

    def _resolve_proxy_url(self, ud, d):
        def _npm_view():
            args = []
            args.append(("json", "true"))
            args.append(("registry", ud.registry))
            pkgver = shlex.quote(ud.package + "@" + ud.version)
            cmd = ud.basecmd + " view %s" % pkgver
            env = NpmEnvironment(d)
            check_network_access(d, cmd, ud.registry)
            view_string = env.run(cmd, args=args)

            if not view_string:
                raise FetchError("Unavailable package %s" % pkgver, ud.url)

            try:
                view = json.loads(view_string)

                error = view.get("error")
                if error is not None:
                    raise FetchError(error.get("summary"), ud.url)

                if ud.version == "latest":
                    bb.warn("The npm package %s is using the latest " \
                            "version available. This could lead to " \
                            "non-reproducible builds." % pkgver)
                elif ud.version != view.get("version"):
                    raise ParameterError("Invalid 'version' parameter", ud.url)

                return view

            except Exception as e:
                raise FetchError("Invalid view from npm: %s" % str(e), ud.url)

        def _get_url(view):
            tarball_url = view.get("dist", {}).get("tarball")

            if tarball_url is None:
                raise FetchError("Invalid 'dist.tarball' in view", ud.url)

            uri = URI(tarball_url)
            uri.params["downloadfilename"] = ud.localfile

            integrity = view.get("dist", {}).get("integrity")
            shasum = view.get("dist", {}).get("shasum")

            if integrity is not None:
                checksum_name, checksum_expected = npm_integrity(integrity)
                uri.params[checksum_name] = checksum_expected
            elif shasum is not None:
                uri.params["sha1sum"] = shasum
            else:
                raise FetchError("Invalid 'dist.integrity' in view", ud.url)

            return str(uri)

        url = _get_url(_npm_view())

        bb.utils.mkdirhier(os.path.dirname(ud.resolvefile))
        with open(ud.resolvefile, "w") as f:
            f.write(url)

    def _setup_proxy(self, ud, d):
        if ud.proxy is None:
            if not os.path.exists(ud.resolvefile):
                self._resolve_proxy_url(ud, d)

            with open(ud.resolvefile, "r") as f:
                url = f.read()

            # Avoid conflicts between the environment data and:
            # - the proxy url checksum
            data = bb.data.createCopy(d)
            data.delVarFlags("SRC_URI")
            ud.proxy = Fetch([url], data)

    def _get_proxy_method(self, ud, d):
        self._setup_proxy(ud, d)
        proxy_url = ud.proxy.urls[0]
        proxy_ud = ud.proxy.ud[proxy_url]
        proxy_d = ud.proxy.d
        proxy_ud.setup_localpath(proxy_d)
        return proxy_ud.method, proxy_ud, proxy_d

    def verify_donestamp(self, ud, d):
        """Verify the donestamp file"""
        proxy_m, proxy_ud, proxy_d = self._get_proxy_method(ud, d)
        return proxy_m.verify_donestamp(proxy_ud, proxy_d)

    def update_donestamp(self, ud, d):
        """Update the donestamp file"""
        proxy_m, proxy_ud, proxy_d = self._get_proxy_method(ud, d)
        proxy_m.update_donestamp(proxy_ud, proxy_d)

    def need_update(self, ud, d):
        """Force a fetch, even if localpath exists ?"""
        if not os.path.exists(ud.resolvefile):
            return True
        if ud.version == "latest":
            return True
        proxy_m, proxy_ud, proxy_d = self._get_proxy_method(ud, d)
        return proxy_m.need_update(proxy_ud, proxy_d)

    def try_mirrors(self, fetch, ud, d, mirrors):
        """Try to use a mirror"""
        proxy_m, proxy_ud, proxy_d = self._get_proxy_method(ud, d)
        return proxy_m.try_mirrors(fetch, proxy_ud, proxy_d, mirrors)

    def download(self, ud, d):
        """Fetch url"""
        self._setup_proxy(ud, d)
        ud.proxy.download()

    def unpack(self, ud, rootdir, d):
        """Unpack the downloaded archive"""
        destsuffix = ud.parm.get("destsuffix", "npm")
        destdir = os.path.join(rootdir, destsuffix)
        npm_unpack(ud.localpath, destdir, d)
        ud.unpack_tracer.unpack("npm", destdir)

    def clean(self, ud, d):
        """Clean any existing full or partial download"""
        if os.path.exists(ud.resolvefile):
            self._setup_proxy(ud, d)
            ud.proxy.clean()
            bb.utils.remove(ud.resolvefile)

    def done(self, ud, d):
        """Is the download done ?"""
        if not os.path.exists(ud.resolvefile):
            return False
        proxy_m, proxy_ud, proxy_d = self._get_proxy_method(ud, d)
        return proxy_m.done(proxy_ud, proxy_d)
