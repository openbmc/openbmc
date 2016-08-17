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
from recipetool.create import RecipeHandler, split_pkg_licenses

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
        return None

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

    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        import bb.utils
        import oe
        from collections import OrderedDict

        if 'buildsystem' in handled:
            return False

        def read_package_json(fn):
            with open(fn, 'r') as f:
                return json.loads(f.read())

        files = RecipeHandler.checkfiles(srctree, ['package.json'])
        if files:
            data = read_package_json(files[0])
            if 'name' in data and 'version' in data:
                extravalues['PN'] = data['name']
                extravalues['PV'] = data['version']
                classes.append('npm')
                handled.append('buildsystem')
                if 'description' in data:
                    lines_before.append('SUMMARY = "%s"' % data['description'])
                if 'homepage' in data:
                    lines_before.append('HOMEPAGE = "%s"' % data['homepage'])

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
                    for pkgname, pkgitem in npmpackages.iteritems():
                        _, pdata = pkgitem
                        license = self._handle_license(pdata)
                        if license:
                            licenses[pkgname] = license
                    # Now write out the package-specific license values
                    # We need to strip out the json data dicts for this since split_pkg_licenses
                    # isn't expecting it
                    packages = OrderedDict((x,y[0]) for x,y in npmpackages.iteritems())
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

                return True

        return False

def register_recipe_handlers(handlers):
    handlers.append((NpmRecipeHandler(), 60))
