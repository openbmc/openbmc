# Recipe creation tool - create command plugin
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

import sys
import os
import argparse
import glob
import fnmatch
import re
import json
import logging
import scriptutils
from urllib.parse import urlparse, urldefrag, urlsplit
import hashlib

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

class RecipeHandler(object):
    recipelibmap = {}
    recipeheadermap = {}
    recipecmakefilemap = {}
    recipebinmap = {}

    @staticmethod
    def load_libmap(d):
        '''Load library->recipe mapping'''
        import oe.package

        if RecipeHandler.recipelibmap:
            return
        # First build up library->package mapping
        shlib_providers = oe.package.read_shlib_providers(d)
        libdir = d.getVar('libdir', True)
        base_libdir = d.getVar('base_libdir', True)
        libpaths = list(set([base_libdir, libdir]))
        libname_re = re.compile('^lib(.+)\.so.*$')
        pkglibmap = {}
        for lib, item in shlib_providers.items():
            for path, pkg in item.items():
                if path in libpaths:
                    res = libname_re.match(lib)
                    if res:
                        libname = res.group(1)
                        if not libname in pkglibmap:
                            pkglibmap[libname] = pkg[0]
                    else:
                        logger.debug('unable to extract library name from %s' % lib)

        # Now turn it into a library->recipe mapping
        pkgdata_dir = d.getVar('PKGDATA_DIR', True)
        for libname, pkg in pkglibmap.items():
            try:
                with open(os.path.join(pkgdata_dir, 'runtime', pkg)) as f:
                    for line in f:
                        if line.startswith('PN:'):
                            RecipeHandler.recipelibmap[libname] = line.split(':', 1)[-1].strip()
                            break
            except IOError as ioe:
                if ioe.errno == 2:
                    logger.warn('unable to find a pkgdata file for package %s' % pkg)
                else:
                    raise

        # Some overrides - these should be mapped to the virtual
        RecipeHandler.recipelibmap['GL'] = 'virtual/libgl'
        RecipeHandler.recipelibmap['EGL'] = 'virtual/egl'
        RecipeHandler.recipelibmap['GLESv2'] = 'virtual/libgles2'

    @staticmethod
    def load_devel_filemap(d):
        '''Build up development file->recipe mapping'''
        if RecipeHandler.recipeheadermap:
            return
        pkgdata_dir = d.getVar('PKGDATA_DIR', True)
        includedir = d.getVar('includedir', True)
        cmakedir = os.path.join(d.getVar('libdir', True), 'cmake')
        for pkg in glob.glob(os.path.join(pkgdata_dir, 'runtime', '*-dev')):
            with open(os.path.join(pkgdata_dir, 'runtime', pkg)) as f:
                pn = None
                headers = []
                cmakefiles = []
                for line in f:
                    if line.startswith('PN:'):
                        pn = line.split(':', 1)[-1].strip()
                    elif line.startswith('FILES_INFO:'):
                        val = line.split(':', 1)[1].strip()
                        dictval = json.loads(val)
                        for fullpth in sorted(dictval):
                            if fullpth.startswith(includedir) and fullpth.endswith('.h'):
                                headers.append(os.path.relpath(fullpth, includedir))
                            elif fullpth.startswith(cmakedir) and fullpth.endswith('.cmake'):
                                cmakefiles.append(os.path.relpath(fullpth, cmakedir))
                if pn and headers:
                    for header in headers:
                        RecipeHandler.recipeheadermap[header] = pn
                if pn and cmakefiles:
                    for fn in cmakefiles:
                        RecipeHandler.recipecmakefilemap[fn] = pn

    @staticmethod
    def load_binmap(d):
        '''Build up native binary->recipe mapping'''
        if RecipeHandler.recipebinmap:
            return
        sstate_manifests = d.getVar('SSTATE_MANIFESTS', True)
        staging_bindir_native = d.getVar('STAGING_BINDIR_NATIVE', True)
        build_arch = d.getVar('BUILD_ARCH', True)
        fileprefix = 'manifest-%s-' % build_arch
        for fn in glob.glob(os.path.join(sstate_manifests, '%s*-native.populate_sysroot' % fileprefix)):
            with open(fn, 'r') as f:
                pn = os.path.basename(fn).rsplit('.', 1)[0][len(fileprefix):]
                for line in f:
                    if line.startswith(staging_bindir_native):
                        prog = os.path.basename(line.rstrip())
                        RecipeHandler.recipebinmap[prog] = pn

    @staticmethod
    def checkfiles(path, speclist, recursive=False):
        results = []
        if recursive:
            for root, _, files in os.walk(path):
                for fn in files:
                    for spec in speclist:
                        if fnmatch.fnmatch(fn, spec):
                            results.append(os.path.join(root, fn))
        else:
            for spec in speclist:
                results.extend(glob.glob(os.path.join(path, spec)))
        return results

    @staticmethod
    def handle_depends(libdeps, pcdeps, deps, outlines, values, d):
        if pcdeps:
            recipemap = read_pkgconfig_provides(d)
        if libdeps:
            RecipeHandler.load_libmap(d)

        ignorelibs = ['socket']
        ignoredeps = ['gcc-runtime', 'glibc', 'uclibc', 'musl', 'tar-native', 'binutils-native', 'coreutils-native']

        unmappedpc = []
        pcdeps = list(set(pcdeps))
        for pcdep in pcdeps:
            if isinstance(pcdep, str):
                recipe = recipemap.get(pcdep, None)
                if recipe:
                    deps.append(recipe)
                else:
                    if not pcdep.startswith('$'):
                        unmappedpc.append(pcdep)
            else:
                for item in pcdep:
                    recipe = recipemap.get(pcdep, None)
                    if recipe:
                        deps.append(recipe)
                        break
                else:
                    unmappedpc.append('(%s)' % ' or '.join(pcdep))

        unmappedlibs = []
        for libdep in libdeps:
            if isinstance(libdep, tuple):
                lib, header = libdep
            else:
                lib = libdep
                header = None

            if lib in ignorelibs:
                logger.debug('Ignoring library dependency %s' % lib)
                continue

            recipe = RecipeHandler.recipelibmap.get(lib, None)
            if recipe:
                deps.append(recipe)
            elif recipe is None:
                if header:
                    RecipeHandler.load_devel_filemap(d)
                    recipe = RecipeHandler.recipeheadermap.get(header, None)
                    if recipe:
                        deps.append(recipe)
                    elif recipe is None:
                        unmappedlibs.append(lib)
                else:
                    unmappedlibs.append(lib)

        deps = set(deps).difference(set(ignoredeps))

        if unmappedpc:
            outlines.append('# NOTE: unable to map the following pkg-config dependencies: %s' % ' '.join(unmappedpc))
            outlines.append('#       (this is based on recipes that have previously been built and packaged)')

        if unmappedlibs:
            outlines.append('# NOTE: the following library dependencies are unknown, ignoring: %s' % ' '.join(list(set(unmappedlibs))))
            outlines.append('#       (this is based on recipes that have previously been built and packaged)')

        if deps:
            values['DEPENDS'] = ' '.join(deps)

    def genfunction(self, outlines, funcname, content, python=False, forcespace=False):
        if python:
            prefix = 'python '
        else:
            prefix = ''
        outlines.append('%s%s () {' % (prefix, funcname))
        if python or forcespace:
            indent = '    '
        else:
            indent = '\t'
        addnoop = not python
        for line in content:
            outlines.append('%s%s' % (indent, line))
            if addnoop:
                strippedline = line.lstrip()
                if strippedline and not strippedline.startswith('#'):
                    addnoop = False
        if addnoop:
            # Without this there'll be a syntax error
            outlines.append('%s:' % indent)
        outlines.append('}')
        outlines.append('')

    def process(self, srctree, classes, lines_before, lines_after, handled, extravalues):
        return False


def validate_pv(pv):
    if not pv or '_version' in pv.lower() or pv[0] not in '0123456789':
        return False
    return True

def determine_from_filename(srcfile):
    """Determine name and version from a filename"""
    if is_package(srcfile):
        # Force getting the value from the package metadata
        return None, None

    if '.tar.' in srcfile:
        namepart = srcfile.split('.tar.')[0]
    else:
        namepart = os.path.splitext(srcfile)[0]
    namepart = namepart.lower().replace('_', '-')
    if namepart.endswith('.src'):
        namepart = namepart[:-4]
    if namepart.endswith('.orig'):
        namepart = namepart[:-5]
    splitval = namepart.split('-')
    logger.debug('determine_from_filename: split name %s into: %s' % (srcfile, splitval))

    ver_re = re.compile('^v?[0-9]')

    pv = None
    pn = None
    if len(splitval) == 1:
        # Try to split the version out if there is no separator (or a .)
        res = re.match('^([^0-9]+)([0-9.]+.*)$', namepart)
        if res:
            if len(res.group(1)) > 1 and len(res.group(2)) > 1:
                pn = res.group(1).rstrip('.')
                pv = res.group(2)
        else:
            pn = namepart
    else:
        if splitval[-1] in ['source', 'src']:
            splitval.pop()
        if len(splitval) > 2 and re.match('^(alpha|beta|stable|release|rc[0-9]|pre[0-9]|p[0-9]|[0-9]{8})', splitval[-1]) and ver_re.match(splitval[-2]):
            pv = '-'.join(splitval[-2:])
            if pv.endswith('-release'):
                pv = pv[:-8]
            splitval = splitval[:-2]
        elif ver_re.match(splitval[-1]):
            pv = splitval.pop()
        pn = '-'.join(splitval)
        if pv and pv.startswith('v'):
            pv = pv[1:]
    logger.debug('determine_from_filename: name = "%s" version = "%s"' % (pn, pv))
    return (pn, pv)

def determine_from_url(srcuri):
    """Determine name and version from a URL"""
    pn = None
    pv = None
    parseres = urlparse(srcuri.lower().split(';', 1)[0])
    if parseres.path:
        if 'github.com' in parseres.netloc:
            res = re.search(r'.*/(.*?)/archive/(.*)-final\.(tar|zip)', parseres.path)
            if res:
                pn = res.group(1).strip().replace('_', '-')
                pv = res.group(2).strip().replace('_', '.')
            else:
                res = re.search(r'.*/(.*?)/archive/v?(.*)\.(tar|zip)', parseres.path)
                if res:
                    pn = res.group(1).strip().replace('_', '-')
                    pv = res.group(2).strip().replace('_', '.')
        elif 'bitbucket.org' in parseres.netloc:
            res = re.search(r'.*/(.*?)/get/[a-zA-Z_-]*([0-9][0-9a-zA-Z_.]*)\.(tar|zip)', parseres.path)
            if res:
                pn = res.group(1).strip().replace('_', '-')
                pv = res.group(2).strip().replace('_', '.')

        if not pn and not pv:
            srcfile = os.path.basename(parseres.path.rstrip('/'))
            pn, pv = determine_from_filename(srcfile)

    logger.debug('Determined from source URL: name = "%s", version = "%s"' % (pn, pv))
    return (pn, pv)

def supports_srcrev(uri):
    localdata = bb.data.createCopy(tinfoil.config_data)
    # This is a bit sad, but if you don't have this set there can be some
    # odd interactions with the urldata cache which lead to errors
    localdata.setVar('SRCREV', '${AUTOREV}')
    bb.data.update_data(localdata)
    try:
        fetcher = bb.fetch2.Fetch([uri], localdata)
        urldata = fetcher.ud
        for u in urldata:
            if urldata[u].method.supports_srcrev():
                return True
    except bb.fetch2.FetchError as e:
        logger.debug('FetchError in supports_srcrev: %s' % str(e))
        # Fall back to basic check
        if uri.startswith(('git://', 'gitsm://')):
            return True
    return False

def reformat_git_uri(uri):
    '''Convert any http[s]://....git URI into git://...;protocol=http[s]'''
    checkuri = uri.split(';', 1)[0]
    if checkuri.endswith('.git') or '/git/' in checkuri or re.match('https?://github.com/[^/]+/[^/]+/?$', checkuri):
        res = re.match('(http|https|ssh)://([^;]+(\.git)?)(;.*)?$', uri)
        if res:
            # Need to switch the URI around so that the git fetcher is used
            return 'git://%s;protocol=%s%s' % (res.group(2), res.group(1), res.group(4) or '')
        elif '@' in checkuri:
            # Catch e.g. git@git.example.com:repo.git
            return 'git://%s;protocol=ssh' % checkuri.replace(':', '/', 1)
    return uri

def is_package(url):
    '''Check if a URL points to a package'''
    checkurl = url.split(';', 1)[0]
    if checkurl.endswith(('.deb', '.ipk', '.rpm', '.srpm')):
        return True
    return False

def create_recipe(args):
    import bb.process
    import tempfile
    import shutil
    import oe.recipeutils

    pkgarch = ""
    if args.machine:
        pkgarch = "${MACHINE_ARCH}"

    extravalues = {}
    checksums = (None, None)
    tempsrc = ''
    source = args.source
    srcsubdir = ''
    srcrev = '${AUTOREV}'

    if os.path.isfile(source):
        source = 'file://%s' % os.path.abspath(source)

    if scriptutils.is_src_url(source):
        # Fetch a URL
        fetchuri = reformat_git_uri(urldefrag(source)[0])
        if args.binary:
            # Assume the archive contains the directory structure verbatim
            # so we need to extract to a subdirectory
            fetchuri += ';subdir=${BP}'
        srcuri = fetchuri
        rev_re = re.compile(';rev=([^;]+)')
        res = rev_re.search(srcuri)
        if res:
            srcrev = res.group(1)
            srcuri = rev_re.sub('', srcuri)
        tempsrc = tempfile.mkdtemp(prefix='recipetool-')
        srctree = tempsrc
        if fetchuri.startswith('npm://'):
            # Check if npm is available
            check_npm(tinfoil.config_data)
        logger.info('Fetching %s...' % srcuri)
        try:
            checksums = scriptutils.fetch_uri(tinfoil.config_data, fetchuri, srctree, srcrev)
        except bb.fetch2.BBFetchException as e:
            logger.error(str(e).rstrip())
            sys.exit(1)
        dirlist = os.listdir(srctree)
        if 'git.indirectionsymlink' in dirlist:
            dirlist.remove('git.indirectionsymlink')
        if len(dirlist) == 1:
            singleitem = os.path.join(srctree, dirlist[0])
            if os.path.isdir(singleitem):
                # We unpacked a single directory, so we should use that
                srcsubdir = dirlist[0]
                srctree = os.path.join(srctree, srcsubdir)
            else:
                with open(singleitem, 'r', errors='surrogateescape') as f:
                    if '<html' in f.read(100).lower():
                        logger.error('Fetching "%s" returned a single HTML page - check the URL is correct and functional' % fetchuri)
                        sys.exit(1)
        if os.path.exists(os.path.join(srctree, '.gitmodules')) and srcuri.startswith('git://'):
            srcuri = 'gitsm://' + srcuri[6:]
            logger.info('Fetching submodules...')
            bb.process.run('git submodule update --init --recursive', cwd=srctree)

        if is_package(fetchuri):
            tmpfdir = tempfile.mkdtemp(prefix='recipetool-')
            try:
                pkgfile = None
                try:
                    fileuri = fetchuri + ';unpack=0'
                    scriptutils.fetch_uri(tinfoil.config_data, fileuri, tmpfdir, srcrev)
                    for root, _, files in os.walk(tmpfdir):
                        for f in files:
                            pkgfile = os.path.join(root, f)
                            break
                except bb.fetch2.BBFetchException as e:
                    logger.warn('Second fetch to get metadata failed: %s' % str(e).rstrip())

                if pkgfile:
                    if pkgfile.endswith(('.deb', '.ipk')):
                        stdout, _ = bb.process.run('ar x %s control.tar.gz' % pkgfile, cwd=tmpfdir)
                        stdout, _ = bb.process.run('tar xf control.tar.gz ./control', cwd=tmpfdir)
                        values = convert_debian(tmpfdir)
                        extravalues.update(values)
                    elif pkgfile.endswith(('.rpm', '.srpm')):
                        stdout, _ = bb.process.run('rpm -qp --xml %s > pkginfo.xml' % pkgfile, cwd=tmpfdir)
                        values = convert_rpm_xml(os.path.join(tmpfdir, 'pkginfo.xml'))
                        extravalues.update(values)
            finally:
                shutil.rmtree(tmpfdir)
    else:
        # Assume we're pointing to an existing source tree
        if args.extract_to:
            logger.error('--extract-to cannot be specified if source is a directory')
            sys.exit(1)
        if not os.path.isdir(source):
            logger.error('Invalid source directory %s' % source)
            sys.exit(1)
        srctree = source
        srcuri = ''
        if os.path.exists(os.path.join(srctree, '.git')):
            # Try to get upstream repo location from origin remote
            try:
                stdout, _ = bb.process.run('git remote -v', cwd=srctree, shell=True)
            except bb.process.ExecutionError as e:
                stdout = None
            if stdout:
                for line in stdout.splitlines():
                    splitline = line.split()
                    if len(splitline) > 1:
                        if splitline[0] == 'origin' and scriptutils.is_src_url(splitline[1]):
                            srcuri = reformat_git_uri(splitline[1])
                            srcsubdir = 'git'
                            break

    if args.src_subdir:
        srcsubdir = os.path.join(srcsubdir, args.src_subdir)
        srctree_use = os.path.join(srctree, args.src_subdir)
    else:
        srctree_use = srctree

    if args.outfile and os.path.isdir(args.outfile):
        outfile = None
        outdir = args.outfile
    else:
        outfile = args.outfile
        outdir = None
    if outfile and outfile != '-':
        if os.path.exists(outfile):
            logger.error('Output file %s already exists' % outfile)
            sys.exit(1)

    lines_before = []
    lines_after = []

    lines_before.append('# Recipe created by %s' % os.path.basename(sys.argv[0]))
    lines_before.append('# This is the basis of a recipe and may need further editing in order to be fully functional.')
    lines_before.append('# (Feel free to remove these comments when editing.)')
    # We need a blank line here so that patch_recipe_lines can rewind before the LICENSE comments
    lines_before.append('')

    handled = []
    licvalues = handle_license_vars(srctree_use, lines_before, handled, extravalues, tinfoil.config_data)

    classes = []

    # FIXME This is kind of a hack, we probably ought to be using bitbake to do this
    pn = None
    pv = None
    if outfile:
        recipefn = os.path.splitext(os.path.basename(outfile))[0]
        fnsplit = recipefn.split('_')
        if len(fnsplit) > 1:
            pn = fnsplit[0]
            pv = fnsplit[1]
        else:
            pn = recipefn

    if args.version:
        pv = args.version

    if args.name:
        pn = args.name
        if args.name.endswith('-native'):
            if args.also_native:
                logger.error('--also-native cannot be specified for a recipe named *-native (*-native denotes a recipe that is already only for native) - either remove the -native suffix from the name or drop --also-native')
                sys.exit(1)
            classes.append('native')
        elif args.name.startswith('nativesdk-'):
            if args.also_native:
                logger.error('--also-native cannot be specified for a recipe named nativesdk-* (nativesdk-* denotes a recipe that is already only for nativesdk)')
                sys.exit(1)
            classes.append('nativesdk')

    if pv and pv not in 'git svn hg'.split():
        realpv = pv
    else:
        realpv = None

    if srcuri and not realpv or not pn:
        name_pn, name_pv = determine_from_url(srcuri)
        if name_pn and not pn:
            pn = name_pn
        if name_pv and not realpv:
            realpv = name_pv


    if not srcuri:
        lines_before.append('# No information for SRC_URI yet (only an external source tree was specified)')
    lines_before.append('SRC_URI = "%s"' % srcuri)
    (md5value, sha256value) = checksums
    if md5value:
        lines_before.append('SRC_URI[md5sum] = "%s"' % md5value)
    if sha256value:
        lines_before.append('SRC_URI[sha256sum] = "%s"' % sha256value)
    if srcuri and supports_srcrev(srcuri):
        lines_before.append('')
        lines_before.append('# Modify these as desired')
        lines_before.append('PV = "%s+git${SRCPV}"' % (realpv or '1.0'))
        if not args.autorev and srcrev == '${AUTOREV}':
            if os.path.exists(os.path.join(srctree, '.git')):
                (stdout, _) = bb.process.run('git rev-parse HEAD', cwd=srctree)
            srcrev = stdout.rstrip()
        lines_before.append('SRCREV = "%s"' % srcrev)
    lines_before.append('')

    if srcsubdir and not args.binary:
        # (for binary packages we explicitly specify subdir= when fetching to
        # match the default value of S, so we don't need to set it in that case)
        lines_before.append('S = "${WORKDIR}/%s"' % srcsubdir)
        lines_before.append('')

    if pkgarch:
        lines_after.append('PACKAGE_ARCH = "%s"' % pkgarch)
        lines_after.append('')

    if args.binary:
        lines_after.append('INSANE_SKIP_${PN} += "already-stripped"')
        lines_after.append('')

    # Find all plugins that want to register handlers
    logger.debug('Loading recipe handlers')
    raw_handlers = []
    for plugin in plugins:
        if hasattr(plugin, 'register_recipe_handlers'):
            plugin.register_recipe_handlers(raw_handlers)
    # Sort handlers by priority
    handlers = []
    for i, handler in enumerate(raw_handlers):
        if isinstance(handler, tuple):
            handlers.append((handler[0], handler[1], i))
        else:
            handlers.append((handler, 0, i))
    handlers.sort(key=lambda item: (item[1], -item[2]), reverse=True)
    for handler, priority, _ in handlers:
        logger.debug('Handler: %s (priority %d)' % (handler.__class__.__name__, priority))
    handlers = [item[0] for item in handlers]

    # Apply the handlers
    if args.binary:
        classes.append('bin_package')
        handled.append('buildsystem')

    for handler in handlers:
        handler.process(srctree_use, classes, lines_before, lines_after, handled, extravalues)

    extrafiles = extravalues.pop('extrafiles', {})
    extra_pn = extravalues.pop('PN', None)
    extra_pv = extravalues.pop('PV', None)

    if extra_pv and not realpv:
        realpv = extra_pv
        if not validate_pv(realpv):
            realpv = None
        else:
            realpv = realpv.lower().split()[0]
            if '_' in realpv:
                realpv = realpv.replace('_', '-')
    if extra_pn and not pn:
        pn = extra_pn
        if pn.startswith('GNU '):
            pn = pn[4:]
        if ' ' in pn:
            # Probably a descriptive identifier rather than a proper name
            pn = None
        else:
            pn = pn.lower()
            if '_' in pn:
                pn = pn.replace('_', '-')

    if not outfile:
        if not pn:
            logger.error('Unable to determine short program name from source tree - please specify name with -N/--name or output file name with -o/--outfile')
            # devtool looks for this specific exit code, so don't change it
            sys.exit(15)
        else:
            if srcuri and srcuri.startswith(('gitsm://', 'git://', 'hg://', 'svn://')):
                suffix = srcuri.split(':', 1)[0]
                if suffix == 'gitsm':
                    suffix = 'git'
                outfile = '%s_%s.bb' % (pn, suffix)
            elif realpv:
                outfile = '%s_%s.bb' % (pn, realpv)
            else:
                outfile = '%s.bb' % pn
            if outdir:
                outfile = os.path.join(outdir, outfile)
            # We need to check this again
            if os.path.exists(outfile):
                logger.error('Output file %s already exists' % outfile)
                sys.exit(1)

    # Move any extra files the plugins created to a directory next to the recipe
    if extrafiles:
        if outfile == '-':
            extraoutdir = pn
        else:
            extraoutdir = os.path.join(os.path.dirname(outfile), pn)
        bb.utils.mkdirhier(extraoutdir)
        for destfn, extrafile in extrafiles.items():
            shutil.move(extrafile, os.path.join(extraoutdir, destfn))

    lines = lines_before
    lines_before = []
    skipblank = True
    for line in lines:
        if skipblank:
            skipblank = False
            if not line:
                continue
        if line.startswith('S = '):
            if realpv and pv not in 'git svn hg'.split():
                line = line.replace(realpv, '${PV}')
            if pn:
                line = line.replace(pn, '${BPN}')
            if line == 'S = "${WORKDIR}/${BPN}-${PV}"':
                skipblank = True
                continue
        elif line.startswith('SRC_URI = '):
            if realpv:
                line = line.replace(realpv, '${PV}')
        elif line.startswith('PV = '):
            if realpv:
                line = re.sub('"[^+]*\+', '"%s+' % realpv, line)
        lines_before.append(line)

    if args.also_native:
        lines = lines_after
        lines_after = []
        bbclassextend = None
        for line in lines:
            if line.startswith('BBCLASSEXTEND ='):
                splitval = line.split('"')
                if len(splitval) > 1:
                    bbclassextend = splitval[1].split()
                    if not 'native' in bbclassextend:
                        bbclassextend.insert(0, 'native')
                line = 'BBCLASSEXTEND = "%s"' % ' '.join(bbclassextend)
            lines_after.append(line)
        if not bbclassextend:
            lines_after.append('BBCLASSEXTEND = "native"')

    outlines = []
    outlines.extend(lines_before)
    if classes:
        if outlines[-1] and not outlines[-1].startswith('#'):
            outlines.append('')
        outlines.append('inherit %s' % ' '.join(classes))
        outlines.append('')
    outlines.extend(lines_after)

    if extravalues:
        if 'LICENSE' in extravalues and not licvalues:
            # Don't blow away 'CLOSED' value that comments say we set
            del extravalues['LICENSE']
        _, outlines = oe.recipeutils.patch_recipe_lines(outlines, extravalues, trailing_newline=False)

    if args.extract_to:
        scriptutils.git_convert_standalone_clone(srctree)
        if os.path.isdir(args.extract_to):
            # If the directory exists we'll move the temp dir into it instead of
            # its contents - of course, we could try to always move its contents
            # but that is a pain if there are symlinks; the simplest solution is
            # to just remove it first
            os.rmdir(args.extract_to)
        shutil.move(srctree, args.extract_to)
        if tempsrc == srctree:
            tempsrc = None
        logger.info('Source extracted to %s' % args.extract_to)

    if outfile == '-':
        sys.stdout.write('\n'.join(outlines) + '\n')
    else:
        with open(outfile, 'w') as f:
            lastline = None
            for line in outlines:
                if not lastline and not line:
                    # Skip extra blank lines
                    continue
                f.write('%s\n' % line)
                lastline = line
        logger.info('Recipe %s has been created; further editing may be required to make it fully functional' % outfile)

    if tempsrc:
        if args.keep_temp:
            logger.info('Preserving temporary directory %s' % tempsrc)
        else:
            shutil.rmtree(tempsrc)

    return 0

def handle_license_vars(srctree, lines_before, handled, extravalues, d):
    licvalues = guess_license(srctree, d)
    lic_files_chksum = []
    lic_unknown = []
    if licvalues:
        licenses = []
        for licvalue in licvalues:
            if not licvalue[0] in licenses:
                licenses.append(licvalue[0])
            lic_files_chksum.append('file://%s;md5=%s' % (licvalue[1], licvalue[2]))
            if licvalue[0] == 'Unknown':
                lic_unknown.append(licvalue[1])
        lines_before.append('# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is')
        lines_before.append('# your responsibility to verify that the values are complete and correct.')
        if len(licvalues) > 1:
            lines_before.append('#')
            lines_before.append('# NOTE: multiple licenses have been detected; if that is correct you should separate')
            lines_before.append('# these in the LICENSE value using & if the multiple licenses all apply, or | if there')
            lines_before.append('# is a choice between the multiple licenses. If in doubt, check the accompanying')
            lines_before.append('# documentation to determine which situation is applicable.')
        if lic_unknown:
            lines_before.append('#')
            lines_before.append('# The following license files were not able to be identified and are')
            lines_before.append('# represented as "Unknown" below, you will need to check them yourself:')
            for licfile in lic_unknown:
                lines_before.append('#   %s' % licfile)
            lines_before.append('#')
    else:
        lines_before.append('# Unable to find any files that looked like license statements. Check the accompanying')
        lines_before.append('# documentation and source headers and set LICENSE and LIC_FILES_CHKSUM accordingly.')
        lines_before.append('#')
        lines_before.append('# NOTE: LICENSE is being set to "CLOSED" to allow you to at least start building - if')
        lines_before.append('# this is not accurate with respect to the licensing of the software being built (it')
        lines_before.append('# will not be in most cases) you must specify the correct value before using this')
        lines_before.append('# recipe for anything other than initial testing/development!')
        licenses = ['CLOSED']
    pkg_license = extravalues.pop('LICENSE', None)
    if pkg_license:
        if licenses == ['Unknown']:
            lines_before.append('# NOTE: The following LICENSE value was determined from the original package metadata')
            licenses = [pkg_license]
        else:
            lines_before.append('# NOTE: Original package metadata indicates license is: %s' % pkg_license)
    lines_before.append('LICENSE = "%s"' % ' '.join(licenses))
    lines_before.append('LIC_FILES_CHKSUM = "%s"' % ' \\\n                    '.join(lic_files_chksum))
    lines_before.append('')
    handled.append(('license', licvalues))
    return licvalues

def get_license_md5sums(d, static_only=False):
    import bb.utils
    md5sums = {}
    if not static_only:
        # Gather md5sums of license files in common license dir
        commonlicdir = d.getVar('COMMON_LICENSE_DIR', True)
        for fn in os.listdir(commonlicdir):
            md5value = bb.utils.md5_file(os.path.join(commonlicdir, fn))
            md5sums[md5value] = fn
    # The following were extracted from common values in various recipes
    # (double checking the license against the license file itself, not just
    # the LICENSE value in the recipe)
    md5sums['94d55d512a9ba36caa9b7df079bae19f'] = 'GPLv2'
    md5sums['b234ee4d69f5fce4486a80fdaf4a4263'] = 'GPLv2'
    md5sums['59530bdf33659b29e73d4adb9f9f6552'] = 'GPLv2'
    md5sums['0636e73ff0215e8d672dc4c32c317bb3'] = 'GPLv2'
    md5sums['eb723b61539feef013de476e68b5c50a'] = 'GPLv2'
    md5sums['751419260aa954499f7abaabaa882bbe'] = 'GPLv2'
    md5sums['393a5ca445f6965873eca0259a17f833'] = 'GPLv2'
    md5sums['12f884d2ae1ff87c09e5b7ccc2c4ca7e'] = 'GPLv2'
    md5sums['8ca43cbc842c2336e835926c2166c28b'] = 'GPLv2'
    md5sums['ebb5c50ab7cab4baeffba14977030c07'] = 'GPLv2'
    md5sums['c93c0550bd3173f4504b2cbd8991e50b'] = 'GPLv2'
    md5sums['9ac2e7cff1ddaf48b6eab6028f23ef88'] = 'GPLv2'
    md5sums['4325afd396febcb659c36b49533135d4'] = 'GPLv2'
    md5sums['18810669f13b87348459e611d31ab760'] = 'GPLv2'
    md5sums['d7810fab7487fb0aad327b76f1be7cd7'] = 'GPLv2' # the Linux kernel's COPYING file
    md5sums['bbb461211a33b134d42ed5ee802b37ff'] = 'LGPLv2.1'
    md5sums['7fbc338309ac38fefcd64b04bb903e34'] = 'LGPLv2.1'
    md5sums['4fbd65380cdd255951079008b364516c'] = 'LGPLv2.1'
    md5sums['2d5025d4aa3495befef8f17206a5b0a1'] = 'LGPLv2.1'
    md5sums['fbc093901857fcd118f065f900982c24'] = 'LGPLv2.1'
    md5sums['a6f89e2100d9b6cdffcea4f398e37343'] = 'LGPLv2.1'
    md5sums['d8045f3b8f929c1cb29a1e3fd737b499'] = 'LGPLv2.1'
    md5sums['fad9b3332be894bab9bc501572864b29'] = 'LGPLv2.1'
    md5sums['3bf50002aefd002f49e7bb854063f7e7'] = 'LGPLv2'
    md5sums['9f604d8a4f8e74f4f5140845a21b6674'] = 'LGPLv2'
    md5sums['5f30f0716dfdd0d91eb439ebec522ec2'] = 'LGPLv2'
    md5sums['55ca817ccb7d5b5b66355690e9abc605'] = 'LGPLv2'
    md5sums['252890d9eee26aab7b432e8b8a616475'] = 'LGPLv2'
    md5sums['3214f080875748938ba060314b4f727d'] = 'LGPLv2'
    md5sums['db979804f025cf55aabec7129cb671ed'] = 'LGPLv2'
    md5sums['d32239bcb673463ab874e80d47fae504'] = 'GPLv3'
    md5sums['f27defe1e96c2e1ecd4e0c9be8967949'] = 'GPLv3'
    md5sums['6a6a8e020838b23406c81b19c1d46df6'] = 'LGPLv3'
    md5sums['3b83ef96387f14655fc854ddc3c6bd57'] = 'Apache-2.0'
    md5sums['385c55653886acac3821999a3ccd17b3'] = 'Artistic-1.0 | GPL-2.0' # some perl modules
    md5sums['54c7042be62e169199200bc6477f04d1'] = 'BSD-3-Clause'
    return md5sums

def crunch_license(licfile):
    '''
    Remove non-material text from a license file and then check
    its md5sum against a known list. This works well for licenses
    which contain a copyright statement, but is also a useful way
    to handle people's insistence upon reformatting the license text
    slightly (with no material difference to the text of the
    license).
    '''

    import oe.utils

    # Note: these are carefully constructed!
    license_title_re = re.compile('^\(?(#+ *)?(The )?.{1,10} [Ll]icen[sc]e( \(.{1,10}\))?\)?:?$')
    license_statement_re = re.compile('^(This (project|software) is( free software)? (released|licen[sc]ed)|(Released|Licen[cs]ed)) under the .{1,10} [Ll]icen[sc]e:?$')
    copyright_re = re.compile('^(#+)? *Copyright .*$')

    crunched_md5sums = {}
    # The following two were gleaned from the "forever" npm package
    crunched_md5sums['0a97f8e4cbaf889d6fa51f84b89a79f6'] = 'ISC'
    crunched_md5sums['eecf6429523cbc9693547cf2db790b5c'] = 'MIT'
    # https://github.com/vasi/pixz/blob/master/LICENSE
    crunched_md5sums['2f03392b40bbe663597b5bd3cc5ebdb9'] = 'BSD-2-Clause'
    # https://github.com/waffle-gl/waffle/blob/master/LICENSE.txt
    crunched_md5sums['e72e5dfef0b1a4ca8a3d26a60587db66'] = 'BSD-2-Clause'
    # https://github.com/spigwitmer/fakeds1963s/blob/master/LICENSE
    crunched_md5sums['8be76ac6d191671f347ee4916baa637e'] = 'GPLv2'
    # https://github.com/datto/dattobd/blob/master/COPYING
    # http://git.savannah.gnu.org/cgit/freetype/freetype2.git/tree/docs/GPLv2.TXT
    crunched_md5sums['1d65c5ad4bf6489f85f4812bf08ae73d'] = 'GPLv2'
    # http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
    # http://git.neil.brown.name/?p=mdadm.git;a=blob;f=COPYING;h=d159169d1050894d3ea3b98e1c965c4058208fe1;hb=HEAD
    crunched_md5sums['fb530f66a7a89ce920f0e912b5b66d4b'] = 'GPLv2'
    # https://github.com/gkos/nrf24/blob/master/COPYING
    crunched_md5sums['7b6aaa4daeafdfa6ed5443fd2684581b'] = 'GPLv2'
    # https://github.com/josch09/resetusb/blob/master/COPYING
    crunched_md5sums['8b8ac1d631a4d220342e83bcf1a1fbc3'] = 'GPLv3'
    # https://github.com/FFmpeg/FFmpeg/blob/master/COPYING.LGPLv2.1
    crunched_md5sums['2ea316ed973ae176e502e2297b574bb3'] = 'LGPLv2.1'
    # unixODBC-2.3.4 COPYING
    crunched_md5sums['1daebd9491d1e8426900b4fa5a422814'] = 'LGPLv2.1'
    # https://github.com/FFmpeg/FFmpeg/blob/master/COPYING.LGPLv3
    crunched_md5sums['2ebfb3bb49b9a48a075cc1425e7f4129'] = 'LGPLv3'
    lictext = []
    with open(licfile, 'r', errors='surrogateescape') as f:
        for line in f:
            # Drop opening statements
            if copyright_re.match(line):
                continue
            elif license_title_re.match(line):
                continue
            elif license_statement_re.match(line):
                continue
            # Squash spaces, and replace smart quotes, double quotes
            # and backticks with single quotes
            line = oe.utils.squashspaces(line.strip())
            line = line.replace(u"\u2018", "'").replace(u"\u2019", "'").replace(u"\u201c","'").replace(u"\u201d", "'").replace('"', '\'').replace('`', '\'')
            if line:
                lictext.append(line)

    m = hashlib.md5()
    try:
        m.update(' '.join(lictext).encode('utf-8'))
        md5val = m.hexdigest()
    except UnicodeEncodeError:
        md5val = None
        lictext = ''
    license = crunched_md5sums.get(md5val, None)
    return license, md5val, lictext

def guess_license(srctree, d):
    import bb
    md5sums = get_license_md5sums(d)

    licenses = []
    licspecs = ['*LICEN[CS]E*', 'COPYING*', '*[Ll]icense*', 'LEGAL*', '[Ll]egal*', '*GPL*', 'README.lic*', 'COPYRIGHT*', '[Cc]opyright*']
    licfiles = []
    for root, dirs, files in os.walk(srctree):
        for fn in files:
            for spec in licspecs:
                if fnmatch.fnmatch(fn, spec):
                    fullpath = os.path.join(root, fn)
                    if not fullpath in licfiles:
                        licfiles.append(fullpath)
    for licfile in licfiles:
        md5value = bb.utils.md5_file(licfile)
        license = md5sums.get(md5value, None)
        if not license:
            license, crunched_md5, lictext = crunch_license(licfile)
            if not license:
                license = 'Unknown'
        licenses.append((license, os.path.relpath(licfile, srctree), md5value))

    # FIXME should we grab at least one source file with a license header and add that too?

    return licenses

def split_pkg_licenses(licvalues, packages, outlines, fallback_licenses=None, pn='${PN}'):
    """
    Given a list of (license, path, md5sum) as returned by guess_license(),
    a dict of package name to path mappings, write out a set of
    package-specific LICENSE values.
    """
    pkglicenses = {pn: []}
    for license, licpath, _ in licvalues:
        for pkgname, pkgpath in packages.items():
            if licpath.startswith(pkgpath + '/'):
                if pkgname in pkglicenses:
                    pkglicenses[pkgname].append(license)
                else:
                    pkglicenses[pkgname] = [license]
                break
        else:
            # Accumulate on the main package
            pkglicenses[pn].append(license)
    outlicenses = {}
    for pkgname in packages:
        license = ' '.join(list(set(pkglicenses.get(pkgname, ['Unknown'])))) or 'Unknown'
        if license == 'Unknown' and pkgname in fallback_licenses:
            license = fallback_licenses[pkgname]
        outlines.append('LICENSE_%s = "%s"' % (pkgname, license))
        outlicenses[pkgname] = license.split()
    return outlicenses

def read_pkgconfig_provides(d):
    pkgdatadir = d.getVar('PKGDATA_DIR', True)
    pkgmap = {}
    for fn in glob.glob(os.path.join(pkgdatadir, 'shlibs2', '*.pclist')):
        with open(fn, 'r') as f:
            for line in f:
                pkgmap[os.path.basename(line.rstrip())] = os.path.splitext(os.path.basename(fn))[0]
    recipemap = {}
    for pc, pkg in pkgmap.items():
        pkgdatafile = os.path.join(pkgdatadir, 'runtime', pkg)
        if os.path.exists(pkgdatafile):
            with open(pkgdatafile, 'r') as f:
                for line in f:
                    if line.startswith('PN: '):
                        recipemap[pc] = line.split(':', 1)[1].strip()
    return recipemap

def convert_debian(debpath):
    value_map = {'Package': 'PN',
                 'Version': 'PV',
                 'Section': 'SECTION',
                 'License': 'LICENSE',
                 'Homepage': 'HOMEPAGE'}

    # FIXME extend this mapping - perhaps use distro_alias.inc?
    depmap = {'libz-dev': 'zlib'}

    values = {}
    depends = []
    with open(os.path.join(debpath, 'control'), 'r', errors='surrogateescape') as f:
        indesc = False
        for line in f:
            if indesc:
                if line.startswith(' '):
                    if line.startswith(' This package contains'):
                        indesc = False
                    else:
                        if 'DESCRIPTION' in values:
                            values['DESCRIPTION'] += ' ' + line.strip()
                        else:
                            values['DESCRIPTION'] = line.strip()
                else:
                    indesc = False
            if not indesc:
                splitline = line.split(':', 1)
                if len(splitline) < 2:
                    continue
                key = splitline[0]
                value = splitline[1].strip()
                if key == 'Build-Depends':
                    for dep in value.split(','):
                        dep = dep.split()[0]
                        mapped = depmap.get(dep, '')
                        if mapped:
                            depends.append(mapped)
                elif key == 'Description':
                    values['SUMMARY'] = value
                    indesc = True
                else:
                    varname = value_map.get(key, None)
                    if varname:
                        values[varname] = value

    #if depends:
    #    values['DEPENDS'] = ' '.join(depends)

    return values

def convert_rpm_xml(xmlfile):
    '''Converts the output from rpm -qp --xml to a set of variable values'''
    import xml.etree.ElementTree as ElementTree
    rpmtag_map = {'Name': 'PN',
                  'Version': 'PV',
                  'Summary': 'SUMMARY',
                  'Description': 'DESCRIPTION',
                  'License': 'LICENSE',
                  'Url': 'HOMEPAGE'}

    values = {}
    tree = ElementTree.parse(xmlfile)
    root = tree.getroot()
    for child in root:
        if child.tag == 'rpmTag':
            name = child.attrib.get('name', None)
            if name:
                varname = rpmtag_map.get(name, None)
                if varname:
                    values[varname] = child[0].text
    return values


def check_npm(d):
    if not os.path.exists(os.path.join(d.getVar('STAGING_BINDIR_NATIVE', True), 'npm')):
        logger.error('npm required to process specified source, but npm is not available - you need to build nodejs-native first')
        sys.exit(14)

def register_commands(subparsers):
    parser_create = subparsers.add_parser('create',
                                          help='Create a new recipe',
                                          description='Creates a new recipe from a source tree')
    parser_create.add_argument('source', help='Path or URL to source')
    parser_create.add_argument('-o', '--outfile', help='Specify filename for recipe to create')
    parser_create.add_argument('-m', '--machine', help='Make recipe machine-specific as opposed to architecture-specific', action='store_true')
    parser_create.add_argument('-x', '--extract-to', metavar='EXTRACTPATH', help='Assuming source is a URL, fetch it and extract it to the directory specified as %(metavar)s')
    parser_create.add_argument('-N', '--name', help='Name to use within recipe (PN)')
    parser_create.add_argument('-V', '--version', help='Version to use within recipe (PV)')
    parser_create.add_argument('-b', '--binary', help='Treat the source tree as something that should be installed verbatim (no compilation, same directory structure)', action='store_true')
    parser_create.add_argument('--also-native', help='Also add native variant (i.e. support building recipe for the build host as well as the target machine)', action='store_true')
    parser_create.add_argument('--src-subdir', help='Specify subdirectory within source tree to use', metavar='SUBDIR')
    parser_create.add_argument('-a', '--autorev', help='When fetching from a git repository, set SRCREV in the recipe to a floating revision instead of fixed', action="store_true")
    parser_create.add_argument('--keep-temp', action="store_true", help='Keep temporary directory (for debugging)')
    parser_create.set_defaults(func=create_recipe)

