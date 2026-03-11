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


from recipetool.create import RecipeHandler, handle_license_vars

import bb.utils
import json
import logging
import os
import re
import subprocess
import sys
import tempfile


logger = logging.getLogger('recipetool')

tinfoil = None


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

    def process(self, srctree, classes, lines_before,
                lines_after, handled, extravalues):

        if 'buildsystem' in handled:
            return False

        files = RecipeHandler.checkfiles(srctree, ['go.mod'])
        if not files:
            return False

        go_bindir = self.__ensure_go()
        if not go_bindir:
            sys.exit(14)

        handled.append('buildsystem')
        classes.append("go-mod")

        # Use go-mod-update-modules to set the full SRC_URI and LICENSE
        classes.append("go-mod-update-modules")
        extravalues["run_tasks"] = "update_modules"

        with tempfile.TemporaryDirectory(prefix="go-mod-") as tmp_mod_dir:
            env = dict(os.environ)
            env["PATH"] += f":{go_bindir}"
            env['GOMODCACHE'] = tmp_mod_dir

            stdout = subprocess.check_output(["go", "mod", "edit", "-json"], cwd=srctree, env=env, text=True)
            go_mod = json.loads(stdout)
            go_import = re.sub(r'/v([0-9]+)$', '', go_mod['Module']['Path'])

            localfilesdir = tempfile.mkdtemp(prefix='recipetool-go-')
            extravalues.setdefault('extrafiles', {})

            # Write the stub ${BPN}-licenses.inc and ${BPN}-go-mods.inc files
            basename = "{pn}-licenses.inc"
            filename = os.path.join(localfilesdir, basename)
            with open(filename, "w") as f:
                f.write("# FROM RECIPETOOL\n")
            extravalues['extrafiles'][f"../{basename}"] = filename

            basename = "{pn}-go-mods.inc"
            filename = os.path.join(localfilesdir, basename)
            with open(filename, "w") as f:
                f.write("# FROM RECIPETOOL\n")
            extravalues['extrafiles'][f"../{basename}"] = filename

            # Do generic license handling
            d = bb.data.createCopy(tinfoil.config_data)
            handle_license_vars(srctree, lines_before, handled, extravalues, d)
            self.__rewrite_lic_vars(lines_before)

            self.__rewrite_src_uri(lines_before)

            lines_before.append('require ${BPN}-licenses.inc')
            lines_before.append('require ${BPN}-go-mods.inc')
            lines_before.append(f'GO_IMPORT = "{go_import}"')

    def __update_lines_before(self, updated, newlines, lines_before):
        if updated:
            del lines_before[:]
            for line in newlines:
                # Hack to avoid newlines that edit_metadata inserts
                if line.endswith('\n'):
                    line = line[:-1]
                lines_before.append(line)
        return updated

    def __rewrite_lic_vars(self, lines_before):
        def varfunc(varname, origvalue, op, newlines):
            import urllib.parse
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

    def __rewrite_src_uri(self, lines_before):

        def varfunc(varname, origvalue, op, newlines):
            if varname == 'SRC_URI':
                src_uri = ['git://${GO_IMPORT};protocol=https;nobranch=1;destsuffix=${GO_SRCURI_DESTSUFFIX}']
                return src_uri, None, -1, True
            return origvalue, None, 0, True

        updated, newlines = bb.utils.edit_metadata(lines_before, ['SRC_URI'], varfunc)
        return self.__update_lines_before(updated, newlines, lines_before)


def register_recipe_handlers(handlers):
    handlers.append((GoRecipeHandler(), 60))
