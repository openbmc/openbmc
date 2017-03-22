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
from recipetool.create import RecipeHandler, split_pkg_licenses, handle_license_vars, check_npm

logger = logging.getLogger('recipetool')


tinfoil = None

def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance


class NpmRecipeHandler(RecipeHandler):
    lockdownpath = None

    def _handle_license(self, data):
        '''
        Handle the license value from an npm package.json file
        '''
        license = None
        if 'license' in data:
            license = data['license']
            if isinstance(license, dict):
                license = license.get('type', None)
        return license

    def _shrinkwrap(self, srctree, localfilesdir, extravalues, lines_before):
        try:
            runenv = dict(os.environ, PATH=tinfoil.config_data.getVar('PATH', True))
            bb.process.run('npm shrinkwrap', cwd=srctree, stderr=subprocess.STDOUT, env=runenv, shell=True)
        except bb.process.ExecutionError as e:
            logger.warn('npm shrinkwrap failed:\n%s' % e.stdout)
            return

        tmpfile = os.path.join(localfilesdir, 'npm-shrinkwrap.json')
        shutil.move(os.path.join(srctree, 'npm-shrinkwrap.json'), tmpfile)
        extravalues.setdefault('extrafiles', {})
        extravalues['extrafiles']['npm-shrinkwrap.json'] = tmpfile
        lines_before.append('NPM_SHRINKWRAP := "${THISDIR}/${PN}/npm-shrinkwrap.json"')

    def _lockdown(self, srctree, localfilesdir, extravalues, lines_before):
        runenv = dict(os.environ, PATH=tinfoil.config_data.getVar('PATH', True))
        if not NpmRecipeHandler.lockdownpath:
            NpmRecipeHandler.lockdownpath = tempfile.mkdtemp('recipetool-npm-lockdown')
            bb.process.run('npm install lockdown --prefix %s' % NpmRecipeHandler.lockdownpath,
                           cwd=srctree, stderr=subprocess.STDOUT, env=runenv, shell=True)
        relockbin = os.path.join(NpmRecipeHandler.lockdownpath, 'node_modules', 'lockdown', 'relock.js')
        if not os.path.exists(relockbin):
            logger.warn('Could not find relock.js within lockdown directory; skipping lockdown')
            return
        try:
            bb.process.run('node %s' % relockbin, cwd=srctree, stderr=subprocess.STDOUT, env=runenv, shell=True)
        except bb.process.ExecutionError as e:
            logger.warn('lockdown-relock failed:\n%s' % e.stdout)
            return

        tmpfile = os.path.join(localfilesdir, 'lockdown.json')
        shutil.move(os.path.join(srctree, 'lockdown.json'), tmpfile)
        extravalues.setdefault('extrafiles', {})
        extravalues['extrafiles']['lockdown.json'] = tmpfile
        lines_before.append('NPM_LOCKDOWN := "${THISDIR}/${PN}/lockdown.json"')

    def _handle_dependencies(self, d, deps, lines_before, srctree):
        import scriptutils
        # If this isn't a single module we need to get the dependencies
        # and add them to SRC_URI
        def varfunc(varname, origvalue, op, newlines):
            if varname == 'SRC_URI':
                if not origvalue.startswith('npm://'):
                    src_uri = origvalue.split()
                    changed = False
                    for dep, depdata in deps.items():
                        version = self.get_node_version(dep, depdata, d)
                        if version:
                            url = 'npm://registry.npmjs.org;name=%s;version=%s;subdir=node_modules/%s' % (dep, version, dep)
                            scriptutils.fetch_uri(d, url, srctree)
                            src_uri.append(url)
                            changed = True
                    if changed:
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

    def _replace_license_vars(self, srctree, lines_before, handled, extravalues, d):
        for item in handled:
            if isinstance(item, tuple):
                if item[0] == 'license':
                    del item
                    break

        calledvars = []
        def varfunc(varname, origvalue, op, newlines):
            if varname in ['LICENSE', 'LIC_FILES_CHKSUM']:
                for i, e in enumerate(reversed(newlines)):
                    if not e.startswith('#'):
                        stop = i
                        while stop > 0:
                            newlines.pop()
                            stop -= 1
                        break
                calledvars.append(varname)
                if len(calledvars) > 1:
                    # The second time around, put the new license text in
                    insertpos = len(newlines)
                    handle_license_vars(srctree, newlines, handled, extravalues, d)
                return None, None, 0, True
            return origvalue, None, 0, True
        updated, newlines = bb.utils.edit_metadata(lines_before, ['LICENSE', 'LIC_FILES_CHKSUM'], varfunc)
        if updated:
            del lines_before[:]
            lines_before.extend(newlines)
        else:
            raise Exception('Did not find license variables')

    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        import bb.utils
        import oe
        from collections import OrderedDict

        if 'buildsystem' in handled:
            return False

        def read_package_json(fn):
            with open(fn, 'r', errors='surrogateescape') as f:
                return json.loads(f.read())

        files = RecipeHandler.checkfiles(srctree, ['package.json'])
        if files:
            check_npm(tinfoil.config_data)

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

                deps = data.get('dependencies', {})
                updated = self._handle_dependencies(tinfoil.config_data, deps, lines_before, srctree)
                if updated:
                    # We need to redo the license stuff
                    self._replace_license_vars(srctree, lines_before, handled, extravalues, tinfoil.config_data)

                # Shrinkwrap
                localfilesdir = tempfile.mkdtemp(prefix='recipetool-npm')
                self._shrinkwrap(srctree, localfilesdir, extravalues, lines_before)

                # Lockdown
                self._lockdown(srctree, localfilesdir, extravalues, lines_before)

                # Split each npm module out to is own package
                npmpackages = oe.package.npm_split_package_dirs(srctree)
                for item in handled:
                    if isinstance(item, tuple):
                        if item[0] == 'license':
                            licvalues = item[1]
                            break
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
                    all_licenses = list(set([item for pkglicense in pkglicenses.values() for item in pkglicense]))
                    # Go back and update the LICENSE value since we have a bit more
                    # information than when that was written out (and we know all apply
                    # vs. there being a choice, so we can join them with &)
                    for i, line in enumerate(lines_before):
                        if line.startswith('LICENSE = '):
                            lines_before[i] = 'LICENSE = "%s"' % ' & '.join(all_licenses)
                            break

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
    def get_node_version(self, pkg, version, d):
        import bb.fetch2
        pkgfullname = pkg
        if version != '*' and not '/' in version:
            pkgfullname += "@'%s'" % version
        logger.debug(2, "Calling getdeps on %s" % pkg)
        runenv = dict(os.environ, PATH=d.getVar('PATH', True))
        fetchcmd = "npm view %s --json" % pkgfullname
        output, _ = bb.process.run(fetchcmd, stderr=subprocess.STDOUT, env=runenv, shell=True)
        data = self._parse_view(output)
        return data.get('version', None)

def register_recipe_handlers(handlers):
    handlers.append((NpmRecipeHandler(), 60))
