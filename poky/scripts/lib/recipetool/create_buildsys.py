# Recipe creation tool - create command build system handlers
#
# Copyright (C) 2014-2016 Intel Corporation
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
import glob
from recipetool.create import RecipeHandler, validate_pv

logger = logging.getLogger('recipetool')

tinfoil = None
plugins = None

def plugin_init(pluginlist):
    # Take a reference to the list so we can use it later
    global plugins
    plugins = pluginlist

def tinfoil_init(instance):
    global tinfoil
    tinfoil = instance


class CmakeRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        if 'buildsystem' in handled:
            return False

        if RecipeHandler.checkfiles(srctree, ['CMakeLists.txt']):
            classes.append('cmake')
            values = CmakeRecipeHandler.extract_cmake_deps(lines_before, srctree, extravalues)
            classes.extend(values.pop('inherit', '').split())
            for var, value in values.items():
                lines_before.append('%s = "%s"' % (var, value))
            lines_after.append('# Specify any options you want to pass to cmake using EXTRA_OECMAKE:')
            lines_after.append('EXTRA_OECMAKE = ""')
            lines_after.append('')
            handled.append('buildsystem')
            return True
        return False

    @staticmethod
    def extract_cmake_deps(outlines, srctree, extravalues, cmakelistsfile=None):
        # Find all plugins that want to register handlers
        logger.debug('Loading cmake handlers')
        handlers = []
        for plugin in plugins:
            if hasattr(plugin, 'register_cmake_handlers'):
                plugin.register_cmake_handlers(handlers)

        values = {}
        inherits = []

        if cmakelistsfile:
            srcfiles = [cmakelistsfile]
        else:
            srcfiles = RecipeHandler.checkfiles(srctree, ['CMakeLists.txt'])

        # Note that some of these are non-standard, but probably better to
        # be able to map them anyway if we see them
        cmake_pkgmap = {'alsa': 'alsa-lib',
                        'aspell': 'aspell',
                        'atk': 'atk',
                        'bison': 'bison-native',
                        'boost': 'boost',
                        'bzip2': 'bzip2',
                        'cairo': 'cairo',
                        'cups': 'cups',
                        'curl': 'curl',
                        'curses': 'ncurses',
                        'cvs': 'cvs',
                        'drm': 'libdrm',
                        'dbus': 'dbus',
                        'dbusglib': 'dbus-glib',
                        'egl': 'virtual/egl',
                        'expat': 'expat',
                        'flex': 'flex-native',
                        'fontconfig': 'fontconfig',
                        'freetype': 'freetype',
                        'gettext': '',
                        'git': '',
                        'gio': 'glib-2.0',
                        'giounix': 'glib-2.0',
                        'glew': 'glew',
                        'glib': 'glib-2.0',
                        'glib2': 'glib-2.0',
                        'glu': 'libglu',
                        'glut': 'freeglut',
                        'gobject': 'glib-2.0',
                        'gperf': 'gperf-native',
                        'gnutls': 'gnutls',
                        'gtk2': 'gtk+',
                        'gtk3': 'gtk+3',
                        'gtk': 'gtk+3',
                        'harfbuzz': 'harfbuzz',
                        'icu': 'icu',
                        'intl': 'virtual/libintl',
                        'jpeg': 'jpeg',
                        'libarchive': 'libarchive',
                        'libiconv': 'virtual/libiconv',
                        'liblzma': 'xz',
                        'libxml2': 'libxml2',
                        'libxslt': 'libxslt',
                        'opengl': 'virtual/libgl',
                        'openmp': '',
                        'openssl': 'openssl',
                        'pango': 'pango',
                        'perl': '',
                        'perllibs': '',
                        'pkgconfig': '',
                        'png': 'libpng',
                        'pthread': '',
                        'pythoninterp': '',
                        'pythonlibs': '',
                        'ruby': 'ruby-native',
                        'sdl': 'libsdl',
                        'sdl2': 'libsdl2',
                        'subversion': 'subversion-native',
                        'swig': 'swig-native',
                        'tcl': 'tcl-native',
                        'threads': '',
                        'tiff': 'tiff',
                        'wget': 'wget',
                        'x11': 'libx11',
                        'xcb': 'libxcb',
                        'xext': 'libxext',
                        'xfixes': 'libxfixes',
                        'zlib': 'zlib',
                        }

        pcdeps = []
        libdeps = []
        deps = []
        unmappedpkgs = []

        proj_re = re.compile('project\s*\(([^)]*)\)', re.IGNORECASE)
        pkgcm_re = re.compile('pkg_check_modules\s*\(\s*[a-zA-Z0-9-_]+\s*(REQUIRED)?\s+([^)\s]+)\s*\)', re.IGNORECASE)
        pkgsm_re = re.compile('pkg_search_module\s*\(\s*[a-zA-Z0-9-_]+\s*(REQUIRED)?((\s+[^)\s]+)+)\s*\)', re.IGNORECASE)
        findpackage_re = re.compile('find_package\s*\(\s*([a-zA-Z0-9-_]+)\s*.*', re.IGNORECASE)
        findlibrary_re = re.compile('find_library\s*\(\s*[a-zA-Z0-9-_]+\s*(NAMES\s+)?([a-zA-Z0-9-_ ]+)\s*.*')
        checklib_re = re.compile('check_library_exists\s*\(\s*([^\s)]+)\s*.*', re.IGNORECASE)
        include_re = re.compile('include\s*\(\s*([^)\s]*)\s*\)', re.IGNORECASE)
        subdir_re = re.compile('add_subdirectory\s*\(\s*([^)\s]*)\s*([^)\s]*)\s*\)', re.IGNORECASE)
        dep_re = re.compile('([^ ><=]+)( *[<>=]+ *[^ ><=]+)?')

        def find_cmake_package(pkg):
            RecipeHandler.load_devel_filemap(tinfoil.config_data)
            for fn, pn in RecipeHandler.recipecmakefilemap.items():
                splitname = fn.split('/')
                if len(splitname) > 1:
                    if splitname[0].lower().startswith(pkg.lower()):
                        if splitname[1] == '%s-config.cmake' % pkg.lower() or splitname[1] == '%sConfig.cmake' % pkg or splitname[1] == 'Find%s.cmake' % pkg:
                            return pn
            return None

        def interpret_value(value):
            return value.strip('"')

        def parse_cmake_file(fn, paths=None):
            searchpaths = (paths or []) + [os.path.dirname(fn)]
            logger.debug('Parsing file %s' % fn)
            with open(fn, 'r', errors='surrogateescape') as f:
                for line in f:
                    line = line.strip()
                    for handler in handlers:
                        if handler.process_line(srctree, fn, line, libdeps, pcdeps, deps, outlines, inherits, values):
                            continue
                    res = include_re.match(line)
                    if res:
                        includefn = bb.utils.which(':'.join(searchpaths), res.group(1))
                        if includefn:
                            parse_cmake_file(includefn, searchpaths)
                        else:
                            logger.debug('Unable to recurse into include file %s' % res.group(1))
                        continue
                    res = subdir_re.match(line)
                    if res:
                        subdirfn = os.path.join(os.path.dirname(fn), res.group(1), 'CMakeLists.txt')
                        if os.path.exists(subdirfn):
                            parse_cmake_file(subdirfn, searchpaths)
                        else:
                            logger.debug('Unable to recurse into subdirectory file %s' % subdirfn)
                        continue
                    res = proj_re.match(line)
                    if res:
                        extravalues['PN'] = interpret_value(res.group(1).split()[0])
                        continue
                    res = pkgcm_re.match(line)
                    if res:
                        res = dep_re.findall(res.group(2))
                        if res:
                            pcdeps.extend([interpret_value(x[0]) for x in res])
                        inherits.append('pkgconfig')
                        continue
                    res = pkgsm_re.match(line)
                    if res:
                        res = dep_re.findall(res.group(2))
                        if res:
                            # Note: appending a tuple here!
                            item = tuple((interpret_value(x[0]) for x in res))
                            if len(item) == 1:
                                item = item[0]
                            pcdeps.append(item)
                        inherits.append('pkgconfig')
                        continue
                    res = findpackage_re.match(line)
                    if res:
                        origpkg = res.group(1)
                        pkg = interpret_value(origpkg)
                        found = False
                        for handler in handlers:
                            if handler.process_findpackage(srctree, fn, pkg, deps, outlines, inherits, values):
                                logger.debug('Mapped CMake package %s via handler %s' % (pkg, handler.__class__.__name__))
                                found = True
                                break
                        if found:
                            continue
                        elif pkg == 'Gettext':
                            inherits.append('gettext')
                        elif pkg == 'Perl':
                            inherits.append('perlnative')
                        elif pkg == 'PkgConfig':
                            inherits.append('pkgconfig')
                        elif pkg == 'PythonInterp':
                            inherits.append('pythonnative')
                        elif pkg == 'PythonLibs':
                            inherits.append('python-dir')
                        else:
                            # Try to map via looking at installed CMake packages in pkgdata
                            dep = find_cmake_package(pkg)
                            if dep:
                                logger.debug('Mapped CMake package %s to recipe %s via pkgdata' % (pkg, dep))
                                deps.append(dep)
                            else:
                                dep = cmake_pkgmap.get(pkg.lower(), None)
                                if dep:
                                    logger.debug('Mapped CMake package %s to recipe %s via internal list' % (pkg, dep))
                                    deps.append(dep)
                                elif dep is None:
                                    unmappedpkgs.append(origpkg)
                        continue
                    res = checklib_re.match(line)
                    if res:
                        lib = interpret_value(res.group(1))
                        if not lib.startswith('$'):
                            libdeps.append(lib)
                    res = findlibrary_re.match(line)
                    if res:
                        libs = res.group(2).split()
                        for lib in libs:
                            if lib in ['HINTS', 'PATHS', 'PATH_SUFFIXES', 'DOC', 'NAMES_PER_DIR'] or lib.startswith(('NO_', 'CMAKE_', 'ONLY_CMAKE_')):
                                break
                            lib = interpret_value(lib)
                            if not lib.startswith('$'):
                                libdeps.append(lib)
                    if line.lower().startswith('useswig'):
                        deps.append('swig-native')
                        continue

        parse_cmake_file(srcfiles[0])

        if unmappedpkgs:
            outlines.append('# NOTE: unable to map the following CMake package dependencies: %s' % ' '.join(list(set(unmappedpkgs))))

        RecipeHandler.handle_depends(libdeps, pcdeps, deps, outlines, values, tinfoil.config_data)

        for handler in handlers:
            handler.post_process(srctree, libdeps, pcdeps, deps, outlines, inherits, values)

        if inherits:
            values['inherit'] = ' '.join(list(set(inherits)))

        return values


class CmakeExtensionHandler(object):
    '''Base class for CMake extension handlers'''
    def process_line(self, srctree, fn, line, libdeps, pcdeps, deps, outlines, inherits, values):
        '''
        Handle a line parsed out of an CMake file.
        Return True if you've completely handled the passed in line, otherwise return False.
        '''
        return False

    def process_findpackage(self, srctree, fn, pkg, deps, outlines, inherits, values):
        '''
        Handle a find_package package parsed out of a CMake file.
        Return True if you've completely handled the passed in package, otherwise return False.
        '''
        return False

    def post_process(self, srctree, fn, pkg, deps, outlines, inherits, values):
        '''
        Apply any desired post-processing on the output
        '''
        return



class SconsRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        if 'buildsystem' in handled:
            return False

        if RecipeHandler.checkfiles(srctree, ['SConstruct', 'Sconstruct', 'sconstruct']):
            classes.append('scons')
            lines_after.append('# Specify any options you want to pass to scons using EXTRA_OESCONS:')
            lines_after.append('EXTRA_OESCONS = ""')
            lines_after.append('')
            handled.append('buildsystem')
            return True
        return False


class QmakeRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        if 'buildsystem' in handled:
            return False

        if RecipeHandler.checkfiles(srctree, ['*.pro']):
            classes.append('qmake2')
            handled.append('buildsystem')
            return True
        return False


class AutotoolsRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        if 'buildsystem' in handled:
            return False

        autoconf = False
        if RecipeHandler.checkfiles(srctree, ['configure.ac', 'configure.in']):
            autoconf = True
            values = AutotoolsRecipeHandler.extract_autotools_deps(lines_before, srctree, extravalues)
            classes.extend(values.pop('inherit', '').split())
            for var, value in values.items():
                lines_before.append('%s = "%s"' % (var, value))
        else:
            conffile = RecipeHandler.checkfiles(srctree, ['configure'])
            if conffile:
                # Check if this is just a pre-generated autoconf configure script
                with open(conffile[0], 'r', errors='surrogateescape') as f:
                    for i in range(1, 10):
                        if 'Generated by GNU Autoconf' in f.readline():
                            autoconf = True
                            break

        if autoconf and not ('PV' in extravalues and 'PN' in extravalues):
            # Last resort
            conffile = RecipeHandler.checkfiles(srctree, ['configure'])
            if conffile:
                with open(conffile[0], 'r', errors='surrogateescape') as f:
                    for line in f:
                        line = line.strip()
                        if line.startswith('VERSION=') or line.startswith('PACKAGE_VERSION='):
                            pv = line.split('=')[1].strip('"\'')
                            if pv and not 'PV' in extravalues and validate_pv(pv):
                                extravalues['PV'] = pv
                        elif line.startswith('PACKAGE_NAME=') or line.startswith('PACKAGE='):
                            pn = line.split('=')[1].strip('"\'')
                            if pn and not 'PN' in extravalues:
                                extravalues['PN'] = pn

        if autoconf:
            lines_before.append('')
            lines_before.append('# NOTE: if this software is not capable of being built in a separate build directory')
            lines_before.append('# from the source, you should replace autotools with autotools-brokensep in the')
            lines_before.append('# inherit line')
            classes.append('autotools')
            lines_after.append('# Specify any options you want to pass to the configure script using EXTRA_OECONF:')
            lines_after.append('EXTRA_OECONF = ""')
            lines_after.append('')
            handled.append('buildsystem')
            return True

        return False

    @staticmethod
    def extract_autotools_deps(outlines, srctree, extravalues=None, acfile=None):
        import shlex

        # Find all plugins that want to register handlers
        logger.debug('Loading autotools handlers')
        handlers = []
        for plugin in plugins:
            if hasattr(plugin, 'register_autotools_handlers'):
                plugin.register_autotools_handlers(handlers)

        values = {}
        inherits = []

        # Hardcoded map, we also use a dynamic one based on what's in the sysroot
        progmap = {'flex': 'flex-native',
                'bison': 'bison-native',
                'm4': 'm4-native',
                'tar': 'tar-native',
                'ar': 'binutils-native',
                'ranlib': 'binutils-native',
                'ld': 'binutils-native',
                'strip': 'binutils-native',
                'libtool': '',
                'autoconf': '',
                'autoheader': '',
                'automake': '',
                'uname': '',
                'rm': '',
                'cp': '',
                'mv': '',
                'find': '',
                'awk': '',
                'sed': '',
                }
        progclassmap = {'gconftool-2': 'gconf',
                'pkg-config': 'pkgconfig',
                'python': 'pythonnative',
                'python3': 'python3native',
                'perl': 'perlnative',
                'makeinfo': 'texinfo',
                }

        pkg_re = re.compile('PKG_CHECK_MODULES\(\s*\[?[a-zA-Z0-9_]*\]?,\s*\[?([^,\]]*)\]?[),].*')
        pkgce_re = re.compile('PKG_CHECK_EXISTS\(\s*\[?([^,\]]*)\]?[),].*')
        lib_re = re.compile('AC_CHECK_LIB\(\s*\[?([^,\]]*)\]?,.*')
        libx_re = re.compile('AX_CHECK_LIBRARY\(\s*\[?[^,\]]*\]?,\s*\[?([^,\]]*)\]?,\s*\[?([a-zA-Z0-9-]*)\]?,.*')
        progs_re = re.compile('_PROGS?\(\s*\[?[a-zA-Z0-9_]*\]?,\s*\[?([^,\]]*)\]?[),].*')
        dep_re = re.compile('([^ ><=]+)( [<>=]+ [^ ><=]+)?')
        ac_init_re = re.compile('AC_INIT\(\s*([^,]+),\s*([^,]+)[,)].*')
        am_init_re = re.compile('AM_INIT_AUTOMAKE\(\s*([^,]+),\s*([^,]+)[,)].*')
        define_re = re.compile('\s*(m4_)?define\(\s*([^,]+),\s*([^,]+)\)')
        version_re = re.compile('([0-9.]+)')

        defines = {}
        def subst_defines(value):
            newvalue = value
            for define, defval in defines.items():
                newvalue = newvalue.replace(define, defval)
            if newvalue != value:
                return subst_defines(newvalue)
            return value

        def process_value(value):
            value = value.replace('[', '').replace(']', '')
            if value.startswith('m4_esyscmd(') or value.startswith('m4_esyscmd_s('):
                cmd = subst_defines(value[value.index('(')+1:-1])
                try:
                    if '|' in cmd:
                        cmd = 'set -o pipefail; ' + cmd
                    stdout, _ = bb.process.run(cmd, cwd=srctree, shell=True)
                    ret = stdout.rstrip()
                except bb.process.ExecutionError as e:
                    ret = ''
            elif value.startswith('m4_'):
                return None
            ret = subst_defines(value)
            if ret:
                ret = ret.strip('"\'')
            return ret

        # Since a configure.ac file is essentially a program, this is only ever going to be
        # a hack unfortunately; but it ought to be enough of an approximation
        if acfile:
            srcfiles = [acfile]
        else:
            srcfiles = RecipeHandler.checkfiles(srctree, ['acinclude.m4', 'configure.ac', 'configure.in'])

        pcdeps = []
        libdeps = []
        deps = []
        unmapped = []

        RecipeHandler.load_binmap(tinfoil.config_data)

        def process_macro(keyword, value):
            for handler in handlers:
                if handler.process_macro(srctree, keyword, value, process_value, libdeps, pcdeps, deps, outlines, inherits, values):
                    return
            logger.debug('Found keyword %s with value "%s"' % (keyword, value))
            if keyword == 'PKG_CHECK_MODULES':
                res = pkg_re.search(value)
                if res:
                    res = dep_re.findall(res.group(1))
                    if res:
                        pcdeps.extend([x[0] for x in res])
                inherits.append('pkgconfig')
            elif keyword == 'PKG_CHECK_EXISTS':
                res = pkgce_re.search(value)
                if res:
                    res = dep_re.findall(res.group(1))
                    if res:
                        pcdeps.extend([x[0] for x in res])
                inherits.append('pkgconfig')
            elif keyword in ('AM_GNU_GETTEXT', 'AM_GLIB_GNU_GETTEXT', 'GETTEXT_PACKAGE'):
                inherits.append('gettext')
            elif keyword in ('AC_PROG_INTLTOOL', 'IT_PROG_INTLTOOL'):
                deps.append('intltool-native')
            elif keyword == 'AM_PATH_GLIB_2_0':
                deps.append('glib-2.0')
            elif keyword in ('AC_CHECK_PROG', 'AC_PATH_PROG', 'AX_WITH_PROG'):
                res = progs_re.search(value)
                if res:
                    for prog in shlex.split(res.group(1)):
                        prog = prog.split()[0]
                        for handler in handlers:
                            if handler.process_prog(srctree, keyword, value, prog, deps, outlines, inherits, values):
                                return
                        progclass = progclassmap.get(prog, None)
                        if progclass:
                            inherits.append(progclass)
                        else:
                            progdep = RecipeHandler.recipebinmap.get(prog, None)
                            if not progdep:
                                progdep = progmap.get(prog, None)
                            if progdep:
                                deps.append(progdep)
                            elif progdep is None:
                                if not prog.startswith('$'):
                                    unmapped.append(prog)
            elif keyword == 'AC_CHECK_LIB':
                res = lib_re.search(value)
                if res:
                    lib = res.group(1)
                    if not lib.startswith('$'):
                        libdeps.append(lib)
            elif keyword == 'AX_CHECK_LIBRARY':
                res = libx_re.search(value)
                if res:
                    lib = res.group(2)
                    if not lib.startswith('$'):
                        header = res.group(1)
                        libdeps.append((lib, header))
            elif keyword == 'AC_PATH_X':
                deps.append('libx11')
            elif keyword in ('AX_BOOST', 'BOOST_REQUIRE'):
                deps.append('boost')
            elif keyword in ('AC_PROG_LEX', 'AM_PROG_LEX', 'AX_PROG_FLEX'):
                deps.append('flex-native')
            elif keyword in ('AC_PROG_YACC', 'AX_PROG_BISON'):
                deps.append('bison-native')
            elif keyword == 'AX_CHECK_ZLIB':
                deps.append('zlib')
            elif keyword in ('AX_CHECK_OPENSSL', 'AX_LIB_CRYPTO'):
                deps.append('openssl')
            elif keyword == 'AX_LIB_CURL':
                deps.append('curl')
            elif keyword == 'AX_LIB_BEECRYPT':
                deps.append('beecrypt')
            elif keyword == 'AX_LIB_EXPAT':
                deps.append('expat')
            elif keyword == 'AX_LIB_GCRYPT':
                deps.append('libgcrypt')
            elif keyword == 'AX_LIB_NETTLE':
                deps.append('nettle')
            elif keyword == 'AX_LIB_READLINE':
                deps.append('readline')
            elif keyword == 'AX_LIB_SQLITE3':
                deps.append('sqlite3')
            elif keyword == 'AX_LIB_TAGLIB':
                deps.append('taglib')
            elif keyword in ['AX_PKG_SWIG', 'AC_PROG_SWIG']:
                deps.append('swig-native')
            elif keyword == 'AX_PROG_XSLTPROC':
                deps.append('libxslt-native')
            elif keyword in ['AC_PYTHON_DEVEL', 'AX_PYTHON_DEVEL', 'AM_PATH_PYTHON']:
                pythonclass = 'pythonnative'
                res = version_re.search(value)
                if res:
                    if res.group(1).startswith('3'):
                        pythonclass = 'python3native'
                # Avoid replacing python3native with pythonnative
                if not pythonclass in inherits and not 'python3native' in inherits:
                    if 'pythonnative' in inherits:
                        inherits.remove('pythonnative')
                    inherits.append(pythonclass)
            elif keyword == 'AX_WITH_CURSES':
                deps.append('ncurses')
            elif keyword == 'AX_PATH_BDB':
                deps.append('db')
            elif keyword == 'AX_PATH_LIB_PCRE':
                deps.append('libpcre')
            elif keyword == 'AC_INIT':
                if extravalues is not None:
                    res = ac_init_re.match(value)
                    if res:
                        extravalues['PN'] = process_value(res.group(1))
                        pv = process_value(res.group(2))
                        if validate_pv(pv):
                            extravalues['PV'] = pv
            elif keyword == 'AM_INIT_AUTOMAKE':
                if extravalues is not None:
                    if 'PN' not in extravalues:
                        res = am_init_re.match(value)
                        if res:
                            if res.group(1) != 'AC_PACKAGE_NAME':
                                extravalues['PN'] = process_value(res.group(1))
                            pv = process_value(res.group(2))
                            if validate_pv(pv):
                                extravalues['PV'] = pv
            elif keyword == 'define(':
                res = define_re.match(value)
                if res:
                    key = res.group(2).strip('[]')
                    value = process_value(res.group(3))
                    if value is not None:
                        defines[key] = value

        keywords = ['PKG_CHECK_MODULES',
                    'PKG_CHECK_EXISTS',
                    'AM_GNU_GETTEXT',
                    'AM_GLIB_GNU_GETTEXT',
                    'GETTEXT_PACKAGE',
                    'AC_PROG_INTLTOOL',
                    'IT_PROG_INTLTOOL',
                    'AM_PATH_GLIB_2_0',
                    'AC_CHECK_PROG',
                    'AC_PATH_PROG',
                    'AX_WITH_PROG',
                    'AC_CHECK_LIB',
                    'AX_CHECK_LIBRARY',
                    'AC_PATH_X',
                    'AX_BOOST',
                    'BOOST_REQUIRE',
                    'AC_PROG_LEX',
                    'AM_PROG_LEX',
                    'AX_PROG_FLEX',
                    'AC_PROG_YACC',
                    'AX_PROG_BISON',
                    'AX_CHECK_ZLIB',
                    'AX_CHECK_OPENSSL',
                    'AX_LIB_CRYPTO',
                    'AX_LIB_CURL',
                    'AX_LIB_BEECRYPT',
                    'AX_LIB_EXPAT',
                    'AX_LIB_GCRYPT',
                    'AX_LIB_NETTLE',
                    'AX_LIB_READLINE'
                    'AX_LIB_SQLITE3',
                    'AX_LIB_TAGLIB',
                    'AX_PKG_SWIG',
                    'AC_PROG_SWIG',
                    'AX_PROG_XSLTPROC',
                    'AC_PYTHON_DEVEL',
                    'AX_PYTHON_DEVEL',
                    'AM_PATH_PYTHON',
                    'AX_WITH_CURSES',
                    'AX_PATH_BDB',
                    'AX_PATH_LIB_PCRE',
                    'AC_INIT',
                    'AM_INIT_AUTOMAKE',
                    'define(',
                    ]

        for handler in handlers:
            handler.extend_keywords(keywords)

        for srcfile in srcfiles:
            nesting = 0
            in_keyword = ''
            partial = ''
            with open(srcfile, 'r', errors='surrogateescape') as f:
                for line in f:
                    if in_keyword:
                        partial += ' ' + line.strip()
                        if partial.endswith('\\'):
                            partial = partial[:-1]
                        nesting = nesting + line.count('(') - line.count(')')
                        if nesting == 0:
                            process_macro(in_keyword, partial)
                            partial = ''
                            in_keyword = ''
                    else:
                        for keyword in keywords:
                            if keyword in line:
                                nesting = line.count('(') - line.count(')')
                                if nesting > 0:
                                    partial = line.strip()
                                    if partial.endswith('\\'):
                                        partial = partial[:-1]
                                    in_keyword = keyword
                                else:
                                    process_macro(keyword, line.strip())
                                break

            if in_keyword:
                process_macro(in_keyword, partial)

        if extravalues:
            for k,v in list(extravalues.items()):
                if v:
                    if v.startswith('$') or v.startswith('@') or v.startswith('%'):
                        del extravalues[k]
                    else:
                        extravalues[k] = v.strip('"\'').rstrip('()')

        if unmapped:
            outlines.append('# NOTE: the following prog dependencies are unknown, ignoring: %s' % ' '.join(list(set(unmapped))))

        RecipeHandler.handle_depends(libdeps, pcdeps, deps, outlines, values, tinfoil.config_data)

        for handler in handlers:
            handler.post_process(srctree, libdeps, pcdeps, deps, outlines, inherits, values)

        if inherits:
            values['inherit'] = ' '.join(list(set(inherits)))

        return values


class AutotoolsExtensionHandler(object):
    '''Base class for Autotools extension handlers'''
    def process_macro(self, srctree, keyword, value, process_value, libdeps, pcdeps, deps, outlines, inherits, values):
        '''
        Handle a macro parsed out of an autotools file. Note that if you want this to be called
        for any macro other than the ones AutotoolsRecipeHandler already looks for, you'll need
        to add it to the keywords list in extend_keywords().
        Return True if you've completely handled the passed in macro, otherwise return False.
        '''
        return False

    def extend_keywords(self, keywords):
        '''Adds keywords to be recognised by the parser (so that you get a call to process_macro)'''
        return

    def process_prog(self, srctree, keyword, value, prog, deps, outlines, inherits, values):
        '''
        Handle an AC_PATH_PROG, AC_CHECK_PROG etc. line
        Return True if you've completely handled the passed in macro, otherwise return False.
        '''
        return False

    def post_process(self, srctree, fn, pkg, deps, outlines, inherits, values):
        '''
        Apply any desired post-processing on the output
        '''
        return


class MakefileRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        if 'buildsystem' in handled:
            return False

        makefile = RecipeHandler.checkfiles(srctree, ['Makefile', 'makefile', 'GNUmakefile'])
        if makefile:
            lines_after.append('# NOTE: this is a Makefile-only piece of software, so we cannot generate much of the')
            lines_after.append('# recipe automatically - you will need to examine the Makefile yourself and ensure')
            lines_after.append('# that the appropriate arguments are passed in.')
            lines_after.append('')

            scanfile = os.path.join(srctree, 'configure.scan')
            skipscan = False
            try:
                stdout, stderr = bb.process.run('autoscan', cwd=srctree, shell=True)
            except bb.process.ExecutionError as e:
                skipscan = True
            if scanfile and os.path.exists(scanfile):
                values = AutotoolsRecipeHandler.extract_autotools_deps(lines_before, srctree, acfile=scanfile)
                classes.extend(values.pop('inherit', '').split())
                for var, value in values.items():
                    if var == 'DEPENDS':
                        lines_before.append('# NOTE: some of these dependencies may be optional, check the Makefile and/or upstream documentation')
                    lines_before.append('%s = "%s"' % (var, value))
                lines_before.append('')
                for f in ['configure.scan', 'autoscan.log']:
                    fp = os.path.join(srctree, f)
                    if os.path.exists(fp):
                        os.remove(fp)

            self.genfunction(lines_after, 'do_configure', ['# Specify any needed configure commands here'])

            func = []
            func.append('# You will almost certainly need to add additional arguments here')
            func.append('oe_runmake')
            self.genfunction(lines_after, 'do_compile', func)

            installtarget = True
            try:
                stdout, stderr = bb.process.run('make -n install', cwd=srctree, shell=True)
            except bb.process.ExecutionError as e:
                if e.exitcode != 1:
                    installtarget = False
            func = []
            if installtarget:
                func.append('# This is a guess; additional arguments may be required')
                makeargs = ''
                with open(makefile[0], 'r', errors='surrogateescape') as f:
                    for i in range(1, 100):
                        if 'DESTDIR' in f.readline():
                            makeargs += " 'DESTDIR=${D}'"
                            break
                func.append('oe_runmake install%s' % makeargs)
            else:
                func.append('# NOTE: unable to determine what to put here - there is a Makefile but no')
                func.append('# target named "install", so you will need to define this yourself')
            self.genfunction(lines_after, 'do_install', func)

            handled.append('buildsystem')
        else:
            lines_after.append('# NOTE: no Makefile found, unable to determine what needs to be done')
            lines_after.append('')
            self.genfunction(lines_after, 'do_configure', ['# Specify any needed configure commands here'])
            self.genfunction(lines_after, 'do_compile', ['# Specify compilation commands here'])
            self.genfunction(lines_after, 'do_install', ['# Specify install commands here'])


class VersionFileRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        if 'PV' not in extravalues:
            # Look for a VERSION or version file containing a single line consisting
            # only of a version number
            filelist = RecipeHandler.checkfiles(srctree, ['VERSION', 'version'])
            version = None
            for fileitem in filelist:
                linecount = 0
                with open(fileitem, 'r', errors='surrogateescape') as f:
                    for line in f:
                        line = line.rstrip().strip('"\'')
                        linecount += 1
                        if line:
                            if linecount > 1:
                                version = None
                                break
                            else:
                                if validate_pv(line):
                                    version = line
                if version:
                    extravalues['PV'] = version
                    break


class SpecFileRecipeHandler(RecipeHandler):
    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        if 'PV' in extravalues and 'PN' in extravalues:
            return
        filelist = RecipeHandler.checkfiles(srctree, ['*.spec'], recursive=True)
        valuemap = {'Name': 'PN',
                    'Version': 'PV',
                    'Summary': 'SUMMARY',
                    'Url': 'HOMEPAGE',
                    'License': 'LICENSE'}
        foundvalues = {}
        for fileitem in filelist:
            linecount = 0
            with open(fileitem, 'r', errors='surrogateescape') as f:
                for line in f:
                    for value, varname in valuemap.items():
                        if line.startswith(value + ':') and not varname in foundvalues:
                            foundvalues[varname] = line.split(':', 1)[1].strip()
                            break
                    if len(foundvalues) == len(valuemap):
                        break
        # Drop values containing unexpanded RPM macros
        for k in list(foundvalues.keys()):
            if '%' in foundvalues[k]:
                del foundvalues[k]
        if 'PV' in foundvalues:
            if not validate_pv(foundvalues['PV']):
                del foundvalues['PV']
        license = foundvalues.pop('LICENSE', None)
        if license:
            liccomment = '# NOTE: spec file indicates the license may be "%s"' % license
            for i, line in enumerate(lines_before):
                if line.startswith('LICENSE ='):
                    lines_before.insert(i, liccomment)
                    break
            else:
                lines_before.append(liccomment)
        extravalues.update(foundvalues)

def register_recipe_handlers(handlers):
    # Set priorities with some gaps so that other plugins can insert
    # their own handlers (so avoid changing these numbers)
    handlers.append((CmakeRecipeHandler(), 50))
    handlers.append((AutotoolsRecipeHandler(), 40))
    handlers.append((SconsRecipeHandler(), 30))
    handlers.append((QmakeRecipeHandler(), 20))
    handlers.append((MakefileRecipeHandler(), 10))
    handlers.append((VersionFileRecipeHandler(), -1))
    handlers.append((SpecFileRecipeHandler(), -1))
