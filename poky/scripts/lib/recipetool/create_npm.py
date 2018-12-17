# Recipe creation tool - node.js NPM module support plugin
#
# Copyright (C) 2016 Intel Corporation
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
import logging
import subprocess
import tempfile
import shutil
import json
from recipetool.create import RecipeHandler, split_pkg_licenses, handle_license_vars

logger = logging.getLogger('recipetool')


tinfoil = None

def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance


class NpmRecipeHandler(RecipeHandler):
    lockdownpath = None

    def _ensure_npm(self, fixed_setup=False):
        if not tinfoil.recipes_parsed:
            tinfoil.parse_recipes()
        try:
            rd = tinfoil.parse_recipe('nodejs-native')
        except bb.providers.NoProvider:
            if fixed_setup:
                msg = 'nodejs-native is required for npm but is not available within this SDK'
            else:
                msg = 'nodejs-native is required for npm but is not available - you will likely need to add a layer that provides nodejs'
            logger.error(msg)
            return None
        bindir = rd.getVar('STAGING_BINDIR_NATIVE')
        npmpath = os.path.join(bindir, 'npm')
        if not os.path.exists(npmpath):
            tinfoil.build_targets('nodejs-native', 'addto_recipe_sysroot')
            if not os.path.exists(npmpath):
                logger.error('npm required to process specified source, but nodejs-native did not seem to populate it')
                return None
        return bindir

    def _handle_license(self, data):
        '''
        Handle the license value from an npm package.json file
        '''
        license = None
        if 'license' in data:
            license = data['license']
            if isinstance(license, dict):
                license = license.get('type', None)
            if license:
                if 'OR' in license:
                    license = license.replace('OR', '|')
                    license = license.replace('AND', '&')
                    license = license.replace(' ', '_')
                    if not license[0] == '(':
                        license = '(' + license + ')'
                else:
                    license = license.replace('AND', '&')
                    if license[0] == '(':
                        license = license[1:]
                    if license[-1] == ')':
                        license = license[:-1]
                license = license.replace('MIT/X11', 'MIT')
                license = license.replace('Public Domain', 'PD')
                license = license.replace('SEE LICENSE IN EULA',
                                          'SEE-LICENSE-IN-EULA')
        return license

    def _shrinkwrap(self, srctree, localfilesdir, extravalues, lines_before, d):
        try:
            runenv = dict(os.environ, PATH=d.getVar('PATH'))
            bb.process.run('npm shrinkwrap', cwd=srctree, stderr=subprocess.STDOUT, env=runenv, shell=True)
        except bb.process.ExecutionError as e:
            logger.warning('npm shrinkwrap failed:\n%s' % e.stdout)
            return

        tmpfile = os.path.join(localfilesdir, 'npm-shrinkwrap.json')
        shutil.move(os.path.join(srctree, 'npm-shrinkwrap.json'), tmpfile)
        extravalues.setdefault('extrafiles', {})
        extravalues['extrafiles']['npm-shrinkwrap.json'] = tmpfile
        lines_before.append('NPM_SHRINKWRAP := "${THISDIR}/${PN}/npm-shrinkwrap.json"')

    def _lockdown(self, srctree, localfilesdir, extravalues, lines_before, d):
        runenv = dict(os.environ, PATH=d.getVar('PATH'))
        if not NpmRecipeHandler.lockdownpath:
            NpmRecipeHandler.lockdownpath = tempfile.mkdtemp('recipetool-npm-lockdown')
            bb.process.run('npm install lockdown --prefix %s' % NpmRecipeHandler.lockdownpath,
                           cwd=srctree, stderr=subprocess.STDOUT, env=runenv, shell=True)
        relockbin = os.path.join(NpmRecipeHandler.lockdownpath, 'node_modules', 'lockdown', 'relock.js')
        if not os.path.exists(relockbin):
            logger.warning('Could not find relock.js within lockdown directory; skipping lockdown')
            return
        try:
            bb.process.run('node %s' % relockbin, cwd=srctree, stderr=subprocess.STDOUT, env=runenv, shell=True)
        except bb.process.ExecutionError as e:
            logger.warning('lockdown-relock failed:\n%s' % e.stdout)
            return

        tmpfile = os.path.join(localfilesdir, 'lockdown.json')
        shutil.move(os.path.join(srctree, 'lockdown.json'), tmpfile)
        extravalues.setdefault('extrafiles', {})
        extravalues['extrafiles']['lockdown.json'] = tmpfile
        lines_before.append('NPM_LOCKDOWN := "${THISDIR}/${PN}/lockdown.json"')

    def _handle_dependencies(self, d, deps, optdeps, devdeps, lines_before, srctree):
        import scriptutils
        # If this isn't a single module we need to get the dependencies
        # and add them to SRC_URI
        def varfunc(varname, origvalue, op, newlines):
            if varname == 'SRC_URI':
                if not origvalue.startswith('npm://'):
                    src_uri = origvalue.split()
                    deplist = {}
                    for dep, depver in optdeps.items():
                        depdata = self.get_npm_data(dep, depver, d)
                        if self.check_npm_optional_dependency(depdata):
                            deplist[dep] = depdata
                    for dep, depver in devdeps.items():
                        depdata = self.get_npm_data(dep, depver, d)
                        if self.check_npm_optional_dependency(depdata):
                            deplist[dep] = depdata
                    for dep, depver in deps.items():
                        depdata = self.get_npm_data(dep, depver, d)
                        deplist[dep] = depdata

                    extra_urls = []
                    for dep, depdata in deplist.items():
                        version = depdata.get('version', None)
                        if version:
                            url = 'npm://registry.npmjs.org;name=%s;version=%s;subdir=node_modules/%s' % (dep, version, dep)
                            extra_urls.append(url)
                    if extra_urls:
                        scriptutils.fetch_url(tinfoil, ' '.join(extra_urls), None, srctree, logger)
                        src_uri.extend(extra_urls)
                        return src_uri, None, -1, True
            return origvalue, None, 0, True
        updated, newlines = bb.utils.edit_metadata(lines_before, ['SRC_URI'], varfunc)
        if updated:
            del lines_before[:]
            for line in newlines:
                # Hack to avoid newlines that edit_metadata inserts
                if line.endswith('\n'):
                    line = line[:-1]
                lines_before.append(line)
        return updated

    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        import bb.utils
        import oe.package
        from collections import OrderedDict

        if 'buildsystem' in handled:
            return False

        def read_package_json(fn):
            with open(fn, 'r', errors='surrogateescape') as f:
                return json.loads(f.read())

        files = RecipeHandler.checkfiles(srctree, ['package.json'])
        if files:
            d = bb.data.createCopy(tinfoil.config_data)
            npm_bindir = self._ensure_npm()
            if not npm_bindir:
                sys.exit(14)
            d.prependVar('PATH', '%s:' % npm_bindir)

            data = read_package_json(files[0])
            if 'name' in data and 'version' in data:
                extravalues['PN'] = data['name']
                extravalues['PV'] = data['version']
                classes.append('npm')
                handled.append('buildsystem')
                if 'description' in data:
                    extravalues['SUMMARY'] = data['description']
                if 'homepage' in data:
                    extravalues['HOMEPAGE'] = data['homepage']

                fetchdev = extravalues['fetchdev'] or None
                deps, optdeps, devdeps = self.get_npm_package_dependencies(data, fetchdev)
                self._handle_dependencies(d, deps, optdeps, devdeps, lines_before, srctree)

                # Shrinkwrap
                localfilesdir = tempfile.mkdtemp(prefix='recipetool-npm')
                self._shrinkwrap(srctree, localfilesdir, extravalues, lines_before, d)

                # Lockdown
                self._lockdown(srctree, localfilesdir, extravalues, lines_before, d)

                # Split each npm module out to is own package
                npmpackages = oe.package.npm_split_package_dirs(srctree)
                licvalues = None
                for item in handled:
                    if isinstance(item, tuple):
                        if item[0] == 'license':
                            licvalues = item[1]
                            break
                if not licvalues:
                    licvalues = handle_license_vars(srctree, lines_before, handled, extravalues, d)
                if licvalues:
                    # Augment the license list with information we have in the packages
                    licenses = {}
                    license = self._handle_license(data)
                    if license:
                        licenses['${PN}'] = license
                    for pkgname, pkgitem in npmpackages.items():
                        _, pdata = pkgitem
                        license = self._handle_license(pdata)
                        if license:
                            licenses[pkgname] = license
                    # Now write out the package-specific license values
                    # We need to strip out the json data dicts for this since split_pkg_licenses
                    # isn't expecting it
                    packages = OrderedDict((x,y[0]) for x,y in npmpackages.items())
                    packages['${PN}'] = ''
                    pkglicenses = split_pkg_licenses(licvalues, packages, lines_after, licenses)
                    all_licenses = list(set([item.replace('_', ' ') for pkglicense in pkglicenses.values() for item in pkglicense]))
                    if '&' in all_licenses:
                        all_licenses.remove('&')
                    extravalues['LICENSE'] = ' & '.join(all_licenses)

                # Need to move S setting after inherit npm
                for i, line in enumerate(lines_before):
                    if line.startswith('S ='):
                        lines_before.pop(i)
                        lines_after.insert(0, '# Must be set after inherit npm since that itself sets S')
                        lines_after.insert(1, line)
                        break

                return True

        return False

    # FIXME this is duplicated from lib/bb/fetch2/npm.py
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

    # FIXME this is effectively duplicated from lib/bb/fetch2/npm.py
    # (split out from _getdependencies())
    def get_npm_data(self, pkg, version, d):
        import bb.fetch2
        pkgfullname = pkg
        if version != '*' and not '/' in version:
            pkgfullname += "@'%s'" % version
        logger.debug(2, "Calling getdeps on %s" % pkg)
        runenv = dict(os.environ, PATH=d.getVar('PATH'))
        fetchcmd = "npm view %s --json" % pkgfullname
        output, _ = bb.process.run(fetchcmd, stderr=subprocess.STDOUT, env=runenv, shell=True)
        data = self._parse_view(output)
        return data

    # FIXME this is effectively duplicated from lib/bb/fetch2/npm.py
    # (split out from _getdependencies())
    def get_npm_package_dependencies(self, pdata, fetchdev):
        dependencies = pdata.get('dependencies', {})
        optionalDependencies = pdata.get('optionalDependencies', {})
        dependencies.update(optionalDependencies)
        if fetchdev:
            devDependencies = pdata.get('devDependencies', {})
            dependencies.update(devDependencies)
        else:
            devDependencies = {}
        depsfound = {}
        optdepsfound = {}
        devdepsfound = {}
        for dep in dependencies:
            if dep in optionalDependencies:
                optdepsfound[dep] = dependencies[dep]
            elif dep in devDependencies:
                devdepsfound[dep] = dependencies[dep]
            else:
                depsfound[dep] = dependencies[dep]
        return depsfound, optdepsfound, devdepsfound

    # FIXME this is effectively duplicated from lib/bb/fetch2/npm.py
    # (split out from _getdependencies())
    def check_npm_optional_dependency(self, pdata):
        pkg_os = pdata.get('os', None)
        if pkg_os:
            if not isinstance(pkg_os, list):
                pkg_os = [pkg_os]
            blacklist = False
            for item in pkg_os:
                if item.startswith('!'):
                    blacklist = True
                    break
            if (not blacklist and 'linux' not in pkg_os) or '!linux' in pkg_os:
                pkg = pdata.get('name', 'Unnamed package')
                logger.debug(2, "Skipping %s since it's incompatible with Linux" % pkg)
                return False
        return True


def register_recipe_handlers(handlers):
    handlers.append((NpmRecipeHandler(), 60))
