# Recipe creation tool - go support plugin
#
# The code is based on golang internals. See the afftected
# methods for further reference and information.
#
# Copyright (C) 2023 Weidmueller GmbH & Co KG
# Author: Lukas Funke <lukas.funke@weidmueller.com>
#
# SPDX-License-Identifier: GPL-2.0-only
#


from collections import namedtuple
from enum import Enum
from html.parser import HTMLParser
from recipetool.create import RecipeHandler, handle_license_vars
from recipetool.create import find_licenses, tidy_licenses, fixup_license
from recipetool.create import determine_from_url
from urllib.error import URLError, HTTPError

import bb.utils
import json
import logging
import os
import re
import subprocess
import sys
import shutil
import tempfile
import urllib.parse
import urllib.request


GoImport = namedtuple('GoImport', 'root vcs url suffix')
logger = logging.getLogger('recipetool')
CodeRepo = namedtuple(
    'CodeRepo', 'path codeRoot codeDir pathMajor pathPrefix pseudoMajor')

tinfoil = None

# Regular expression to parse pseudo semantic version
# see https://go.dev/ref/mod#pseudo-versions
re_pseudo_semver = re.compile(
    r"^v[0-9]+\.(0\.0-|\d+\.\d+-([^+]*\.)?0\.)(?P<utc>\d{14})-(?P<commithash>[A-Za-z0-9]+)(\+[0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*)?$")
# Regular expression to parse semantic version
re_semver = re.compile(
    r"^v(?P<major>0|[1-9]\d*)\.(?P<minor>0|[1-9]\d*)\.(?P<patch>0|[1-9]\d*)(?:-(?P<prerelease>(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+(?P<buildmetadata>[0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?$")


def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance


class GoRecipeHandler(RecipeHandler):
    """Class to handle the go recipe creation"""

    @staticmethod
    def __ensure_go():
        """Check if the 'go' command is available in the recipes"""
        recipe = "go-native"
        if not tinfoil.recipes_parsed:
            tinfoil.parse_recipes()
        try:
            rd = tinfoil.parse_recipe(recipe)
        except bb.providers.NoProvider:
            bb.error(
                "Nothing provides '%s' which is required for the build" % (recipe))
            bb.note(
                "You will likely need to add a layer that provides '%s'" % (recipe))
            return None

        bindir = rd.getVar('STAGING_BINDIR_NATIVE')
        gopath = os.path.join(bindir, 'go')

        if not os.path.exists(gopath):
            tinfoil.build_targets(recipe, 'addto_recipe_sysroot')

            if not os.path.exists(gopath):
                logger.error(
                    '%s required to process specified source, but %s did not seem to populate it' % 'go', recipe)
                return None

        return bindir

    def __resolve_repository_static(self, modulepath):
        """Resolve the repository in a static manner

            The method is based on the go implementation of
            `repoRootFromVCSPaths` in
            https://github.com/golang/go/blob/master/src/cmd/go/internal/vcs/vcs.go
        """

        url = urllib.parse.urlparse("https://" + modulepath)
        req = urllib.request.Request(url.geturl())

        try:
            resp = urllib.request.urlopen(req)
            # Some modulepath are just redirects to github (or some other vcs
            # hoster). Therefore, we check if this modulepath redirects to
            # somewhere else
            if resp.geturl() != url.geturl():
                bb.debug(1, "%s is redirectred to %s" %
                         (url.geturl(), resp.geturl()))
                url = urllib.parse.urlparse(resp.geturl())
                modulepath = url.netloc + url.path

        except URLError as url_err:
            # This is probably because the module path
            # contains the subdir and major path. Thus,
            # we ignore this error for now
            logger.debug(
                1, "Failed to fetch page from [%s]: %s" % (url, str(url_err)))

        host, _, _ = modulepath.partition('/')

        class vcs(Enum):
            pathprefix = "pathprefix"
            regexp = "regexp"
            type = "type"
            repo = "repo"
            check = "check"
            schemelessRepo = "schemelessRepo"

        # GitHub
        vcsGitHub = {}
        vcsGitHub[vcs.pathprefix] = "github.com"
        vcsGitHub[vcs.regexp] = re.compile(
            r'^(?P<root>github\.com/[A-Za-z0-9_.\-]+/[A-Za-z0-9_.\-]+)(/(?P<suffix>[A-Za-z0-9_.\-]+))*$')
        vcsGitHub[vcs.type] = "git"
        vcsGitHub[vcs.repo] = "https://\\g<root>"

        # Bitbucket
        vcsBitbucket = {}
        vcsBitbucket[vcs.pathprefix] = "bitbucket.org"
        vcsBitbucket[vcs.regexp] = re.compile(
            r'^(?P<root>bitbucket\.org/(?P<bitname>[A-Za-z0-9_.\-]+/[A-Za-z0-9_.\-]+))(/(?P<suffix>[A-Za-z0-9_.\-]+))*$')
        vcsBitbucket[vcs.type] = "git"
        vcsBitbucket[vcs.repo] = "https://\\g<root>"

        # IBM DevOps Services (JazzHub)
        vcsIBMDevOps = {}
        vcsIBMDevOps[vcs.pathprefix] = "hub.jazz.net/git"
        vcsIBMDevOps[vcs.regexp] = re.compile(
            r'^(?P<root>hub\.jazz\.net/git/[a-z0-9]+/[A-Za-z0-9_.\-]+)(/(?P<suffix>[A-Za-z0-9_.\-]+))*$')
        vcsIBMDevOps[vcs.type] = "git"
        vcsIBMDevOps[vcs.repo] = "https://\\g<root>"

        # Git at Apache
        vcsApacheGit = {}
        vcsApacheGit[vcs.pathprefix] = "git.apache.org"
        vcsApacheGit[vcs.regexp] = re.compile(
            r'^(?P<root>git\.apache\.org/[a-z0-9_.\-]+\.git)(/(?P<suffix>[A-Za-z0-9_.\-]+))*$')
        vcsApacheGit[vcs.type] = "git"
        vcsApacheGit[vcs.repo] = "https://\\g<root>"

        # Git at OpenStack
        vcsOpenStackGit = {}
        vcsOpenStackGit[vcs.pathprefix] = "git.openstack.org"
        vcsOpenStackGit[vcs.regexp] = re.compile(
            r'^(?P<root>git\.openstack\.org/[A-Za-z0-9_.\-]+/[A-Za-z0-9_.\-]+)(\.git)?(/(?P<suffix>[A-Za-z0-9_.\-]+))*$')
        vcsOpenStackGit[vcs.type] = "git"
        vcsOpenStackGit[vcs.repo] = "https://\\g<root>"

        # chiselapp.com for fossil
        vcsChiselapp = {}
        vcsChiselapp[vcs.pathprefix] = "chiselapp.com"
        vcsChiselapp[vcs.regexp] = re.compile(
            r'^(?P<root>chiselapp\.com/user/[A-Za-z0-9]+/repository/[A-Za-z0-9_.\-]+)$')
        vcsChiselapp[vcs.type] = "fossil"
        vcsChiselapp[vcs.repo] = "https://\\g<root>"

        # General syntax for any server.
        # Must be last.
        vcsGeneralServer = {}
        vcsGeneralServer[vcs.regexp] = re.compile(
            "(?P<root>(?P<repo>([a-z0-9.\\-]+\\.)+[a-z0-9.\\-]+(:[0-9]+)?(/~?[A-Za-z0-9_.\\-]+)+?)\\.(?P<vcs>bzr|fossil|git|hg|svn))(/~?(?P<suffix>[A-Za-z0-9_.\\-]+))*$")
        vcsGeneralServer[vcs.schemelessRepo] = True

        vcsPaths = [vcsGitHub, vcsBitbucket, vcsIBMDevOps,
                    vcsApacheGit, vcsOpenStackGit, vcsChiselapp,
                    vcsGeneralServer]

        if modulepath.startswith("example.net") or modulepath == "rsc.io":
            logger.warning("Suspicious module path %s" % modulepath)
            return None
        if modulepath.startswith("http:") or modulepath.startswith("https:"):
            logger.warning("Import path should not start with %s %s" %
                           ("http", "https"))
            return None

        rootpath = None
        vcstype = None
        repourl = None
        suffix = None

        for srv in vcsPaths:
            m = srv[vcs.regexp].match(modulepath)
            if vcs.pathprefix in srv:
                if host == srv[vcs.pathprefix]:
                    rootpath = m.group('root')
                    vcstype = srv[vcs.type]
                    repourl = m.expand(srv[vcs.repo])
                    suffix = m.group('suffix')
                    break
            elif m and srv[vcs.schemelessRepo]:
                rootpath = m.group('root')
                vcstype = m[vcs.type]
                repourl = m[vcs.repo]
                suffix = m.group('suffix')
                break

        return GoImport(rootpath, vcstype, repourl, suffix)

    def __resolve_repository_dynamic(self, modulepath):
        """Resolve the repository root in a dynamic manner.

            The method is based on the go implementation of
            `repoRootForImportDynamic` in
            https://github.com/golang/go/blob/master/src/cmd/go/internal/vcs/vcs.go
        """
        url = urllib.parse.urlparse("https://" + modulepath)

        class GoImportHTMLParser(HTMLParser):

            def __init__(self):
                super().__init__()
                self.__srv = {}

            def handle_starttag(self, tag, attrs):
                if tag == 'meta' and list(
                        filter(lambda a: (a[0] == 'name' and a[1] == 'go-import'), attrs)):
                    content = list(
                        filter(lambda a: (a[0] == 'content'), attrs))
                    if content:
                        srv = content[0][1].split()
                        self.__srv[srv[0]] = srv

            def go_import(self, modulepath):
                if modulepath in self.__srv:
                    srv = self.__srv[modulepath]
                    return GoImport(srv[0], srv[1], srv[2], None)
                return None

        url = url.geturl() + "?go-get=1"
        req = urllib.request.Request(url)

        try:
            body = urllib.request.urlopen(req).read()
        except HTTPError as http_err:
            logger.warning(
                "Unclean status when fetching page from [%s]: %s", url, str(http_err))
            body = http_err.fp.read()
        except URLError as url_err:
            logger.warning(
                "Failed to fetch page from [%s]: %s", url, str(url_err))
            return None

        parser = GoImportHTMLParser()
        parser.feed(body.decode('utf-8'))
        parser.close()

        return parser.go_import(modulepath)

    def __resolve_from_golang_proxy(self, modulepath, version):
        """
        Resolves repository data from golang proxy
        """
        url = urllib.parse.urlparse("https://proxy.golang.org/"
                                    + modulepath
                                    + "/@v/"
                                    + version
                                    + ".info")

        # Transform url to lower case, golang proxy doesn't like mixed case
        req = urllib.request.Request(url.geturl().lower())

        try:
            resp = urllib.request.urlopen(req)
        except URLError as url_err:
            logger.warning(
                "Failed to fetch page from [%s]: %s", url, str(url_err))
            return None

        golang_proxy_res = resp.read().decode('utf-8')
        modinfo = json.loads(golang_proxy_res)

        if modinfo and 'Origin' in modinfo:
            origin = modinfo['Origin']
            _root_url = urllib.parse.urlparse(origin['URL'])

            # We normalize the repo URL since we don't want the scheme in it
            _subdir = origin['Subdir'] if 'Subdir' in origin else None
            _root, _, _ = self.__split_path_version(modulepath)
            if _subdir:
                _root = _root[:-len(_subdir)].strip('/')

            _commit = origin['Hash']
            _vcs = origin['VCS']
            return (GoImport(_root, _vcs, _root_url.geturl(), None), _commit)

        return None

    def __resolve_repository(self, modulepath):
        """
        Resolves src uri from go module-path
        """
        repodata = self.__resolve_repository_static(modulepath)
        if not repodata or not repodata.url:
            repodata = self.__resolve_repository_dynamic(modulepath)
            if not repodata or not repodata.url:
                logger.error(
                    "Could not resolve repository for module path '%s'" % modulepath)
                # There is no way to recover from this
                sys.exit(14)
        if repodata:
            logger.debug(1, "Resolved download path for import '%s' => %s" % (
                modulepath, repodata.url))
        return repodata

    def __split_path_version(self, path):
        i = len(path)
        dot = False
        for j in range(i, 0, -1):
            if path[j - 1] < '0' or path[j - 1] > '9':
                break
            if path[j - 1] == '.':
                dot = True
                break
            i = j - 1

        if i <= 1 or i == len(
                path) or path[i - 1] != 'v' or path[i - 2] != '/':
            return path, "", True

        prefix, pathMajor = path[:i - 2], path[i - 2:]
        if dot or len(
                pathMajor) <= 2 or pathMajor[2] == '0' or pathMajor == "/v1":
            return path, "", False

        return prefix, pathMajor, True

    def __get_path_major(self, pathMajor):
        if not pathMajor:
            return ""

        if pathMajor[0] != '/' and pathMajor[0] != '.':
            logger.error(
                "pathMajor suffix %s passed to PathMajorPrefix lacks separator", pathMajor)

        if pathMajor.startswith(".v") and pathMajor.endswith("-unstable"):
            pathMajor = pathMajor[:len("-unstable") - 2]

        return pathMajor[1:]

    def __build_coderepo(self, repo, path):
        codedir = ""
        pathprefix, pathMajor, _ = self.__split_path_version(path)
        if repo.root == path:
            pathprefix = path
        elif path.startswith(repo.root):
            codedir = pathprefix[len(repo.root):].strip('/')

        pseudoMajor = self.__get_path_major(pathMajor)

        logger.debug("root='%s', codedir='%s', prefix='%s', pathMajor='%s', pseudoMajor='%s'",
                     repo.root, codedir, pathprefix, pathMajor, pseudoMajor)

        return CodeRepo(path, repo.root, codedir,
                        pathMajor, pathprefix, pseudoMajor)

    def __resolve_version(self, repo, path, version):
        hash = None
        coderoot = self.__build_coderepo(repo, path)

        def vcs_fetch_all():
            tmpdir = tempfile.mkdtemp()
            clone_cmd = "%s clone --bare %s %s" % ('git', repo.url, tmpdir)
            bb.process.run(clone_cmd)
            log_cmd = "git log --all --pretty='%H %d' --decorate=short"
            output, _ = bb.process.run(
                log_cmd, shell=True, stderr=subprocess.PIPE, cwd=tmpdir)
            bb.utils.prunedir(tmpdir)
            return output.strip().split('\n')

        def vcs_fetch_remote(tag):
            # add * to grab ^{}
            refs = {}
            ls_remote_cmd = "git ls-remote -q --tags {} {}*".format(
                repo.url, tag)
            output, _ = bb.process.run(ls_remote_cmd)
            output = output.strip().split('\n')
            for line in output:
                f = line.split(maxsplit=1)
                if len(f) != 2:
                    continue

                for prefix in ["HEAD", "refs/heads/", "refs/tags/"]:
                    if f[1].startswith(prefix):
                        refs[f[1][len(prefix):]] = f[0]

            for key, hash in refs.items():
                if key.endswith(r"^{}"):
                    refs[key.strip(r"^{}")] = hash

            return refs[tag]

        m_pseudo_semver = re_pseudo_semver.match(version)

        if m_pseudo_semver:
            remote_refs = vcs_fetch_all()
            short_commit = m_pseudo_semver.group('commithash')
            for l in remote_refs:
                r = l.split(maxsplit=1)
                sha1 = r[0] if len(r) else None
                if not sha1:
                    logger.error(
                        "Ups: could not resolve abbref commit for %s" % short_commit)

                elif sha1.startswith(short_commit):
                    hash = sha1
                    break
        else:
            m_semver = re_semver.match(version)
            if m_semver:

                def get_sha1_remote(re):
                    rsha1 = None
                    for line in remote_refs:
                        # Split lines of the following format:
                        # 22e90d9b964610628c10f673ca5f85b8c2a2ca9a  (tag: sometag)
                        lineparts = line.split(maxsplit=1)
                        sha1 = lineparts[0] if len(lineparts) else None
                        refstring = lineparts[1] if len(
                            lineparts) == 2 else None
                        if refstring:
                            # Normalize tag string and split in case of multiple
                            # regs e.g. (tag: speech/v1.10.0, tag: orchestration/v1.5.0 ...)
                            refs = refstring.strip('(), ').split(',')
                            for ref in refs:
                                if re.match(ref.strip()):
                                    rsha1 = sha1
                    return rsha1

                semver = "v" + m_semver.group('major') + "."\
                             + m_semver.group('minor') + "."\
                             + m_semver.group('patch') \
                             + (("-" + m_semver.group('prerelease'))
                                if m_semver.group('prerelease') else "")

                tag = os.path.join(
                    coderoot.codeDir, semver) if coderoot.codeDir else semver

                # probe tag using 'ls-remote', which is faster than fetching
                # complete history
                hash = vcs_fetch_remote(tag)
                if not hash:
                    # backup: fetch complete history
                    remote_refs = vcs_fetch_all()
                    hash = get_sha1_remote(
                        re.compile(fr"(tag:|HEAD ->) ({tag})"))

                logger.debug(
                    "Resolving commit for tag '%s' -> '%s'", tag, hash)
        return hash

    def __generate_srcuri_inline_fcn(self, path, version, replaces=None):
        """Generate SRC_URI functions for go imports"""

        logger.info("Resolving repository for module %s", path)
        # First try to resolve repo and commit from golang proxy
        # Most info is already there and we don't have to go through the
        # repository or even perform the version resolve magic
        golang_proxy_info = self.__resolve_from_golang_proxy(path, version)
        if golang_proxy_info:
            repo = golang_proxy_info[0]
            commit = golang_proxy_info[1]
        else:
            # Fallback
            # Resolve repository by 'hand'
            repo = self.__resolve_repository(path)
            commit = self.__resolve_version(repo, path, version)

        url = urllib.parse.urlparse(repo.url)
        repo_url = url.netloc + url.path

        coderoot = self.__build_coderepo(repo, path)

        inline_fcn = "${@go_src_uri("
        inline_fcn += f"'{repo_url}','{version}'"
        if repo_url != path:
            inline_fcn += f",path='{path}'"
        if coderoot.codeDir:
            inline_fcn += f",subdir='{coderoot.codeDir}'"
        if repo.vcs != 'git':
            inline_fcn += f",vcs='{repo.vcs}'"
        if replaces:
            inline_fcn += f",replaces='{replaces}'"
        if coderoot.pathMajor:
            inline_fcn += f",pathmajor='{coderoot.pathMajor}'"
        inline_fcn += ")}"

        return inline_fcn, commit

    def __go_handle_dependencies(self, go_mod, srctree, localfilesdir, extravalues, d):

        import re
        src_uris = []
        src_revs = []

        def generate_src_rev(path, version, commithash):
            src_rev = f"# {path}@{version} => {commithash}\n"
            # Ups...maybe someone manipulated the source repository and the
            # version or commit could not be resolved. This is a sign of
            # a) the supply chain was manipulated (bad)
            # b) the implementation for the version resolving didn't work
            #    anymore (less bad)
            if not commithash:
                src_rev += f"#!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
                src_rev += f"#!!!   Could not resolve version  !!!\n"
                src_rev += f"#!!! Possible supply chain attack !!!\n"
                src_rev += f"#!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
            src_rev += f"SRCREV_{path.replace('/', '.')} = \"{commithash}\""

            return src_rev

        # we first go over replacement list, because we are essentialy
        # interested only in the replaced path
        if go_mod['Replace']:
            for replacement in go_mod['Replace']:
                oldpath = replacement['Old']['Path']
                path = replacement['New']['Path']
                version = ''
                if 'Version' in replacement['New']:
                    version = replacement['New']['Version']

                if os.path.exists(os.path.join(srctree, path)):
                    # the module refers to the local path, remove it from requirement list
                    # because it's a local module
                    go_mod['Require'][:] = [v for v in go_mod['Require'] if v.get('Path') != oldpath]
                else:
                    # Replace the path and the version, so we don't iterate replacement list anymore
                    for require in go_mod['Require']:
                        if require['Path'] == oldpath:
                            require.update({'Path': path, 'Version': version})
                            break

        for require in go_mod['Require']:
            path = require['Path']
            version = require['Version']

            inline_fcn, commithash = self.__generate_srcuri_inline_fcn(
                path, version)
            src_uris.append(inline_fcn)
            src_revs.append(generate_src_rev(path, version, commithash))

        # strip version part from module URL /vXX
        baseurl = re.sub(r'/v(\d+)$', '', go_mod['Module']['Path'])
        pn, _ = determine_from_url(baseurl)
        go_mods_basename = "%s-modules.inc" % pn

        go_mods_filename = os.path.join(localfilesdir, go_mods_basename)
        with open(go_mods_filename, "w") as f:
            # We introduce this indirection to make the tests a little easier
            f.write("SRC_URI += \"${GO_DEPENDENCIES_SRC_URI}\"\n")
            f.write("GO_DEPENDENCIES_SRC_URI = \"\\\n")
            for uri in src_uris:
                f.write("    " + uri + " \\\n")
            f.write("\"\n\n")
            for rev in src_revs:
                f.write(rev + "\n")

        extravalues['extrafiles'][go_mods_basename] = go_mods_filename

    def __go_run_cmd(self, cmd, cwd, d):
        return bb.process.run(cmd, env=dict(os.environ, PATH=d.getVar('PATH')),
                              shell=True, cwd=cwd)

    def __go_native_version(self, d):
        stdout, _ = self.__go_run_cmd("go version", None, d)
        m = re.match(r".*\sgo((\d+).(\d+).(\d+))\s([\w\/]*)", stdout)
        major = int(m.group(2))
        minor = int(m.group(3))
        patch = int(m.group(4))

        return major, minor, patch

    def __go_mod_patch(self, srctree, localfilesdir, extravalues, d):

        patchfilename = "go.mod.patch"
        go_native_version_major, go_native_version_minor, _ = self.__go_native_version(
            d)
        self.__go_run_cmd("go mod tidy -go=%d.%d" %
                          (go_native_version_major, go_native_version_minor), srctree, d)
        stdout, _ = self.__go_run_cmd("go mod edit -json", srctree, d)

        # Create patch in order to upgrade go version
        self.__go_run_cmd("git diff go.mod > %s" % (patchfilename), srctree, d)
        # Restore original state
        self.__go_run_cmd("git checkout HEAD go.mod go.sum", srctree, d)

        go_mod = json.loads(stdout)
        tmpfile = os.path.join(localfilesdir, patchfilename)
        shutil.move(os.path.join(srctree, patchfilename), tmpfile)

        extravalues['extrafiles'][patchfilename] = tmpfile

        return go_mod, patchfilename

    def __go_mod_vendor(self, go_mod, srctree, localfilesdir, extravalues, d):
        # Perform vendoring to retrieve the correct modules.txt
        tmp_vendor_dir = tempfile.mkdtemp()

        # -v causes to go to print modules.txt to stderr
        _, stderr = self.__go_run_cmd(
            "go mod vendor -v -o %s" % (tmp_vendor_dir), srctree, d)

        modules_txt_basename = "modules.txt"
        modules_txt_filename = os.path.join(localfilesdir, modules_txt_basename)
        with open(modules_txt_filename, "w") as f:
            f.write(stderr)

        extravalues['extrafiles'][modules_txt_basename] = modules_txt_filename

        licenses = []
        lic_files_chksum = []
        licvalues = find_licenses(tmp_vendor_dir, d)
        shutil.rmtree(tmp_vendor_dir)

        if licvalues:
            for licvalue in licvalues:
                license = licvalue[0]
                lics = tidy_licenses(fixup_license(license))
                lics = [lic for lic in lics if lic not in licenses]
                if len(lics):
                    licenses.extend(lics)
                lic_files_chksum.append(
                    'file://src/${GO_IMPORT}/vendor/%s;md5=%s' % (licvalue[1], licvalue[2]))

        # strip version part from module URL /vXX
        baseurl = re.sub(r'/v(\d+)$', '', go_mod['Module']['Path'])
        pn, _ = determine_from_url(baseurl)
        licenses_basename = "%s-licenses.inc" % pn

        licenses_filename = os.path.join(localfilesdir, licenses_basename)
        with open(licenses_filename, "w") as f:
            f.write("GO_MOD_LICENSES = \"%s\"\n\n" %
                    ' & '.join(sorted(licenses, key=str.casefold)))
            # We introduce this indirection to make the tests a little easier
            f.write("LIC_FILES_CHKSUM  += \"${VENDORED_LIC_FILES_CHKSUM}\"\n")
            f.write("VENDORED_LIC_FILES_CHKSUM = \"\\\n")
            for lic in lic_files_chksum:
                f.write("    " + lic + " \\\n")
            f.write("\"\n")

        extravalues['extrafiles'][licenses_basename] = licenses_filename

    def process(self, srctree, classes, lines_before,
                lines_after, handled, extravalues):

        if 'buildsystem' in handled:
            return False

        files = RecipeHandler.checkfiles(srctree, ['go.mod'])
        if not files:
            return False

        d = bb.data.createCopy(tinfoil.config_data)
        go_bindir = self.__ensure_go()
        if not go_bindir:
            sys.exit(14)

        d.prependVar('PATH', '%s:' % go_bindir)
        handled.append('buildsystem')
        classes.append("go-vendor")

        stdout, _ = self.__go_run_cmd("go mod edit -json", srctree, d)

        go_mod = json.loads(stdout)
        go_import = go_mod['Module']['Path']
        go_version_match = re.match("([0-9]+).([0-9]+)", go_mod['Go'])
        go_version_major = int(go_version_match.group(1))
        go_version_minor = int(go_version_match.group(2))
        src_uris = []

        localfilesdir = tempfile.mkdtemp(prefix='recipetool-go-')
        extravalues.setdefault('extrafiles', {})

        # Use an explicit name determined from the module name because it
        # might differ from the actual URL for replaced modules
        # strip version part from module URL /vXX
        baseurl = re.sub(r'/v(\d+)$', '', go_mod['Module']['Path'])
        pn, _ = determine_from_url(baseurl)

        # go.mod files with version < 1.17 may not include all indirect
        # dependencies. Thus, we have to upgrade the go version.
        if go_version_major == 1 and go_version_minor < 17:
            logger.warning(
                "go.mod files generated by Go < 1.17 might have incomplete indirect dependencies.")
            go_mod, patchfilename = self.__go_mod_patch(srctree, localfilesdir,
                                                        extravalues, d)
            src_uris.append(
                "file://%s;patchdir=src/${GO_IMPORT}" % (patchfilename))

        # Check whether the module is vendored. If so, we have nothing to do.
        # Otherwise we gather all dependencies and add them to the recipe
        if not os.path.exists(os.path.join(srctree, "vendor")):

            # Write additional $BPN-modules.inc file
            self.__go_mod_vendor(go_mod, srctree, localfilesdir, extravalues, d)
            lines_before.append("LICENSE += \" & ${GO_MOD_LICENSES}\"")
            lines_before.append("require %s-licenses.inc" % (pn))

            self.__rewrite_src_uri(lines_before, ["file://modules.txt"])

            self.__go_handle_dependencies(go_mod, srctree, localfilesdir, extravalues, d)
            lines_before.append("require %s-modules.inc" % (pn))

        # Do generic license handling
        handle_license_vars(srctree, lines_before, handled, extravalues, d)
        self.__rewrite_lic_uri(lines_before)

        lines_before.append("GO_IMPORT = \"{}\"".format(baseurl))
        lines_before.append("SRCREV_FORMAT = \"${BPN}\"")

    def __update_lines_before(self, updated, newlines, lines_before):
        if updated:
            del lines_before[:]
            for line in newlines:
                # Hack to avoid newlines that edit_metadata inserts
                if line.endswith('\n'):
                    line = line[:-1]
                lines_before.append(line)
        return updated

    def __rewrite_lic_uri(self, lines_before):

        def varfunc(varname, origvalue, op, newlines):
            if varname == 'LIC_FILES_CHKSUM':
                new_licenses = []
                licenses = origvalue.split('\\')
                for license in licenses:
                    if not license:
                        logger.warning("No license file was detected for the main module!")
                        # the license list of the main recipe must be empty
                        # this can happen for example in case of CLOSED license
                        # Fall through to complete recipe generation
                        continue
                    license = license.strip()
                    uri, chksum = license.split(';', 1)
                    url = urllib.parse.urlparse(uri)
                    new_uri = os.path.join(
                        url.scheme + "://", "src", "${GO_IMPORT}", url.netloc + url.path) + ";" + chksum
                    new_licenses.append(new_uri)

                return new_licenses, None, -1, True
            return origvalue, None, 0, True

        updated, newlines = bb.utils.edit_metadata(
            lines_before, ['LIC_FILES_CHKSUM'], varfunc)
        return self.__update_lines_before(updated, newlines, lines_before)

    def __rewrite_src_uri(self, lines_before, additional_uris = []):

        def varfunc(varname, origvalue, op, newlines):
            if varname == 'SRC_URI':
                src_uri = ["git://${GO_IMPORT};destsuffix=git/src/${GO_IMPORT};nobranch=1;name=${BPN};protocol=https"]
                src_uri.extend(additional_uris)
                return src_uri, None, -1, True
            return origvalue, None, 0, True

        updated, newlines = bb.utils.edit_metadata(lines_before, ['SRC_URI'], varfunc)
        return self.__update_lines_before(updated, newlines, lines_before)


def register_recipe_handlers(handlers):
    handlers.append((GoRecipeHandler(), 60))
