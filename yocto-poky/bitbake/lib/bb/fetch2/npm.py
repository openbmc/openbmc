# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' NPM implementation

The NPM fetcher is used to retrieve files from the npmjs repository

Usage in the recipe:

    SRC_URI = "npm://registry.npmjs.org/;name=${PN};version=${PV}"
    Suported SRC_URI options are:

    - name
    - version

    npm://registry.npmjs.org/${PN}/-/${PN}-${PV}.tgz  would become npm://registry.npmjs.org;name=${PN};ver=${PV}
    The fetcher all triggers off the existence of ud.localpath. If that exists and has the ".done" stamp, its assumed the fetch is good/done

"""

import os
import sys
import urllib
import json
import subprocess
import signal
import bb
from   bb import data
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import FetchError
from   bb.fetch2 import ChecksumError
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger
from   bb.fetch2 import UnpackError
from   bb.fetch2 import ParameterError
from   distutils import spawn

def subprocess_setup():
    # Python installs a SIGPIPE handler by default. This is usually not what
    # non-Python subprocesses expect.
    # SIGPIPE errors are known issues with gzip/bash
    signal.signal(signal.SIGPIPE, signal.SIG_DFL)

class Npm(FetchMethod):

    """Class to fetch urls via 'npm'"""
    def init(self, d):
        pass

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with npm
        """
        return ud.type in ['npm']

    def debug(self, msg):
        logger.debug(1, "NpmFetch: %s", msg)

    def clean(self, ud, d):
        logger.debug(2, "Calling cleanup %s" % ud.pkgname)
        bb.utils.remove(ud.localpath, False)
        bb.utils.remove(ud.pkgdatadir, True)
        bb.utils.remove(ud.fullmirror, False)

    def urldata_init(self, ud, d):
        """
        init NPM specific variable within url data
        """
        if 'downloadfilename' in ud.parm:
            ud.basename = ud.parm['downloadfilename']
        else:
            ud.basename = os.path.basename(ud.path)

        # can't call it ud.name otherwise fetcher base class will start doing sha1stuff
        # TODO: find a way to get an sha1/sha256 manifest of pkg & all deps
        ud.pkgname = ud.parm.get("name", None)
        if not ud.pkgname:
            raise ParameterError("NPM fetcher requires a name parameter", ud.url)
        ud.version = ud.parm.get("version", None)
        if not ud.version:
            raise ParameterError("NPM fetcher requires a version parameter", ud.url)
        ud.bbnpmmanifest = "%s-%s.deps.json" % (ud.pkgname, ud.version)
        ud.registry = "http://%s" % (ud.url.replace('npm://', '', 1).split(';'))[0]
        prefixdir = "npm/%s" % ud.pkgname
        ud.pkgdatadir = d.expand("${DL_DIR}/%s" % prefixdir)
        if not os.path.exists(ud.pkgdatadir):
            bb.utils.mkdirhier(ud.pkgdatadir)
        ud.localpath = d.expand("${DL_DIR}/npm/%s" % ud.bbnpmmanifest)

        self.basecmd = d.getVar("FETCHCMD_wget", True) or "/usr/bin/env wget -O -t 2 -T 30 -nv --passive-ftp --no-check-certificate "
        self.basecmd += " --directory-prefix=%s " % prefixdir

        ud.write_tarballs = ((data.getVar("BB_GENERATE_MIRROR_TARBALLS", d, True) or "0") != "0")
        ud.mirrortarball = 'npm_%s-%s.tar.xz' % (ud.pkgname, ud.version)
        ud.fullmirror = os.path.join(d.getVar("DL_DIR", True), ud.mirrortarball)

    def need_update(self, ud, d):
        if os.path.exists(ud.localpath):
            return False
        return True

    def _runwget(self, ud, d, command, quiet):
        logger.debug(2, "Fetching %s using command '%s'" % (ud.url, command))
        bb.fetch2.check_network_access(d, command)
        runfetchcmd(command, d, quiet)

    def _unpackdep(self, ud, pkg, data, destdir, dldir, d):
        file = data[pkg]['tgz']
        logger.debug(2, "file to extract is %s" % file)
        if file.endswith('.tgz') or file.endswith('.tar.gz') or file.endswith('.tar.Z'):
            cmd = 'tar xz --strip 1 --no-same-owner --warning=no-unknown-keyword -f %s/%s' % (dldir, file)
        else:
            bb.fatal("NPM package %s downloaded not a tarball!" % file)

        # Change to subdir before executing command
        save_cwd = os.getcwd()
        if not os.path.exists(destdir):
            os.makedirs(destdir)
        os.chdir(destdir)
        path = d.getVar('PATH', True)
        if path:
            cmd = "PATH=\"%s\" %s" % (path, cmd)
        bb.note("Unpacking %s to %s/" % (file, os.getcwd()))
        ret = subprocess.call(cmd, preexec_fn=subprocess_setup, shell=True)
        os.chdir(save_cwd)

        if ret != 0:
            raise UnpackError("Unpack command %s failed with return value %s" % (cmd, ret), ud.url)

        if 'deps' not in data[pkg]:
            return
        for dep in data[pkg]['deps']:
            self._unpackdep(ud, dep, data[pkg]['deps'], "%s/node_modules/%s" % (destdir, dep), dldir, d)


    def unpack(self, ud, destdir, d):
        dldir = d.getVar("DL_DIR", True)
        depdumpfile = "%s-%s.deps.json" % (ud.pkgname, ud.version)
        with open("%s/npm/%s" % (dldir, depdumpfile)) as datafile:
            workobj = json.load(datafile)
        dldir = "%s/%s" % (os.path.dirname(ud.localpath), ud.pkgname)

        self._unpackdep(ud, ud.pkgname, workobj,  "%s/npmpkg" % destdir, dldir, d)

    def _parse_view(self, output):
        '''
        Parse the output of npm view --json; the last JSON result
        is assumed to be the one that we're interested in.
        '''
        pdata = None
        outdeps = {}
        datalines = []
        bracelevel = 0
        for line in output.splitlines():
            if bracelevel:
                datalines.append(line)
            elif '{' in line:
                datalines = []
                datalines.append(line)
            bracelevel = bracelevel + line.count('{') - line.count('}')
        if datalines:
            pdata = json.loads('\n'.join(datalines))
        return pdata

    def _getdependencies(self, pkg, data, version, d, ud, optional=False):
        pkgfullname = pkg
        if version != '*' and not '/' in version:
            pkgfullname += "@'%s'" % version
        logger.debug(2, "Calling getdeps on %s" % pkg)
        fetchcmd = "npm view %s --json --registry %s" % (pkgfullname, ud.registry)
        output = runfetchcmd(fetchcmd, d, True)
        pdata = self._parse_view(output)
        if not pdata:
            raise FetchError("The command '%s' returned no output" % fetchcmd)
        if optional:
            pkg_os = pdata.get('os', None)
            if pkg_os:
                if not isinstance(pkg_os, list):
                    pkg_os = [pkg_os]
                if 'linux' not in pkg_os or '!linux' in pkg_os:
                    logger.debug(2, "Skipping %s since it's incompatible with Linux" % pkg)
                    return
        #logger.debug(2, "Output URL is %s - %s - %s" % (ud.basepath, ud.basename, ud.localfile))
        outputurl = pdata['dist']['tarball']
        data[pkg] = {}
        data[pkg]['tgz'] = os.path.basename(outputurl)
        self._runwget(ud, d, "%s %s" % (self.basecmd, outputurl), False)

        dependencies = pdata.get('dependencies', {})
        optionalDependencies = pdata.get('optionalDependencies', {})
        depsfound = {}
        optdepsfound = {}
        data[pkg]['deps'] = {}
        for dep in dependencies:
            if dep in optionalDependencies:
                optdepsfound[dep] = dependencies[dep]
            else:
                depsfound[dep] = dependencies[dep]
        for dep, version in optdepsfound.iteritems():
            self._getdependencies(dep, data[pkg]['deps'], version, d, ud, optional=True)
        for dep, version in depsfound.iteritems():
            self._getdependencies(dep, data[pkg]['deps'], version, d, ud)

    def _getshrinkeddependencies(self, pkg, data, version, d, ud, lockdown, manifest):
        logger.debug(2, "NPM shrinkwrap file is %s" % data)
        outputurl = "invalid"
        if ('resolved' not in data) or (not data['resolved'].startswith('http')):
            # will be the case for ${PN}
            fetchcmd = "npm view %s@%s dist.tarball --registry %s" % (pkg, version, ud.registry)
            logger.debug(2, "Found this matching URL: %s" % str(fetchcmd))
            outputurl = runfetchcmd(fetchcmd, d, True)
        else:
            outputurl = data['resolved']
        self._runwget(ud, d, "%s %s" % (self.basecmd, outputurl), False)
        manifest[pkg] = {}
        manifest[pkg]['tgz'] = os.path.basename(outputurl).rstrip()
        manifest[pkg]['deps'] = {}

        if pkg in lockdown:
            sha1_expected = lockdown[pkg][version]
            sha1_data = bb.utils.sha1_file("npm/%s/%s" % (ud.pkgname, manifest[pkg]['tgz']))
            if sha1_expected != sha1_data:
                msg = "\nFile: '%s' has %s checksum %s when %s was expected" % (manifest[pkg]['tgz'], 'sha1', sha1_data, sha1_expected)
                raise ChecksumError('Checksum mismatch!%s' % msg)
        else:
            logger.debug(2, "No lockdown data for %s@%s" % (pkg, version))

        if 'dependencies' in data:
            for obj in data['dependencies']:
                logger.debug(2, "Found dep is %s" % str(obj))
                self._getshrinkeddependencies(obj, data['dependencies'][obj], data['dependencies'][obj]['version'], d, ud, lockdown, manifest[pkg]['deps'])

    def download(self, ud, d):
        """Fetch url"""
        jsondepobj = {}
        shrinkobj = {}
        lockdown = {}

        if not os.listdir(ud.pkgdatadir) and os.path.exists(ud.fullmirror):
            dest = d.getVar("DL_DIR", True)
            bb.utils.mkdirhier(dest)
            save_cwd = os.getcwd()
            os.chdir(dest)
            runfetchcmd("tar -xJf %s" % (ud.fullmirror), d)
            os.chdir(save_cwd)
            return

        shwrf = d.getVar('NPM_SHRINKWRAP', True)
        logger.debug(2, "NPM shrinkwrap file is %s" % shwrf)
        try:
            with open(shwrf) as datafile:
                shrinkobj = json.load(datafile)
        except:
            logger.warn('Missing shrinkwrap file in NPM_SHRINKWRAP for %s, this will lead to unreliable builds!' % ud.pkgname)
        lckdf = d.getVar('NPM_LOCKDOWN', True)
        logger.debug(2, "NPM lockdown file is %s" % lckdf)
        try:
            with open(lckdf) as datafile:
                lockdown = json.load(datafile)
        except:
            logger.warn('Missing lockdown file in NPM_LOCKDOWN for %s, this will lead to unreproducible builds!' % ud.pkgname)

        if ('name' not in shrinkobj):
            self._getdependencies(ud.pkgname, jsondepobj, ud.version, d, ud)
        else:
            self._getshrinkeddependencies(ud.pkgname, shrinkobj, ud.version, d, ud, lockdown, jsondepobj)

        with open(ud.localpath, 'w') as outfile:
            json.dump(jsondepobj, outfile)

    def build_mirror_data(self, ud, d):
        # Generate a mirror tarball if needed
        if ud.write_tarballs and not os.path.exists(ud.fullmirror):
            # it's possible that this symlink points to read-only filesystem with PREMIRROR
            if os.path.islink(ud.fullmirror):
                os.unlink(ud.fullmirror)

            save_cwd = os.getcwd()
            os.chdir(d.getVar("DL_DIR", True))
            logger.info("Creating tarball of npm data")
            runfetchcmd("tar -cJf %s npm/%s npm/%s" % (ud.fullmirror, ud.bbnpmmanifest, ud.pkgname), d)
            runfetchcmd("touch %s.done" % (ud.fullmirror), d)
            os.chdir(save_cwd)

