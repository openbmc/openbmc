# Recipe creation tool - kernel support plugin
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

import re
import logging
from recipetool.create import RecipeHandler, read_pkgconfig_provides, validate_pv

logger = logging.getLogger('recipetool')

tinfoil = None

def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance


class KernelRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        import bb.process
        if 'buildsystem' in handled:
            return False

        for tell in ['arch', 'firmware', 'Kbuild', 'Kconfig']:
            if not os.path.exists(os.path.join(srctree, tell)):
                return False

        handled.append('buildsystem')
        del lines_after[:]
        del classes[:]
        template = os.path.join(tinfoil.config_data.getVar('COREBASE', True), 'meta-skeleton', 'recipes-kernel', 'linux', 'linux-yocto-custom.bb')
        def handle_var(varname, origvalue, op, newlines):
            if varname in ['SRCREV', 'SRCREV_machine']:
                while newlines[-1].startswith('#'):
                    del newlines[-1]
                try:
                    stdout, _ = bb.process.run('git rev-parse HEAD', cwd=srctree, shell=True)
                except bb.process.ExecutionError as e:
                    stdout = None
                if stdout:
                    return stdout.strip(), op, 0, True
            elif varname == 'LINUX_VERSION':
                makefile = os.path.join(srctree, 'Makefile')
                if os.path.exists(makefile):
                    kversion = -1
                    kpatchlevel = -1
                    ksublevel = -1
                    kextraversion = ''
                    with open(makefile, 'r') as f:
                        for i, line in enumerate(f):
                            if i > 10:
                                break
                            if line.startswith('VERSION ='):
                                kversion = int(line.split('=')[1].strip())
                            elif line.startswith('PATCHLEVEL ='):
                                kpatchlevel = int(line.split('=')[1].strip())
                            elif line.startswith('SUBLEVEL ='):
                                ksublevel = int(line.split('=')[1].strip())
                            elif line.startswith('EXTRAVERSION ='):
                                kextraversion = line.split('=')[1].strip()
                    version = ''
                    if kversion > -1 and kpatchlevel > -1:
                        version = '%d.%d' % (kversion, kpatchlevel)
                        if ksublevel > -1:
                            version += '.%d' % ksublevel
                        version += kextraversion
                    if version:
                        return version, op, 0, True
            elif varname == 'SRC_URI':
                while newlines[-1].startswith('#'):
                    del newlines[-1]
            elif varname == 'COMPATIBLE_MACHINE':
                while newlines[-1].startswith('#'):
                    del newlines[-1]
                machine = tinfoil.config_data.getVar('MACHINE', True)
                return machine, op, 0, True
            return origvalue, op, 0, True
        with open(template, 'r') as f:
            varlist = ['SRCREV', 'SRCREV_machine', 'SRC_URI', 'LINUX_VERSION', 'COMPATIBLE_MACHINE']
            (_, newlines) = bb.utils.edit_metadata(f, varlist, handle_var)
        lines_before[:] = [line.rstrip('\n') for line in newlines]

        return True

def register_recipe_handlers(handlers):
    handlers.append((KernelRecipeHandler(), 100))
