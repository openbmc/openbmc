#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# This bbclass is used for creating archive for:
#  1) original (or unpacked) source: ARCHIVER_MODE[src] = "original"
#  2) patched source: ARCHIVER_MODE[src] = "patched" (default)
#  3) configured source: ARCHIVER_MODE[src] = "configured"
#  4) source mirror: ARCHIVER_MODE[src] = "mirror"
#  5) The patches between do_unpack and do_patch:
#     ARCHIVER_MODE[diff] = "1"
#     And you can set the one that you'd like to exclude from the diff:
#     ARCHIVER_MODE[diff-exclude] ?= ".pc autom4te.cache patches"
#  6) The environment data, similar to 'bitbake -e recipe':
#     ARCHIVER_MODE[dumpdata] = "1"
#  7) The recipe (.bb and .inc): ARCHIVER_MODE[recipe] = "1"
#  8) Whether output the .src.rpm package:
#     ARCHIVER_MODE[srpm] = "1"
#  9) Filter the license, the recipe whose license in
#     COPYLEFT_LICENSE_INCLUDE will be included, and in
#     COPYLEFT_LICENSE_EXCLUDE will be excluded.
#     COPYLEFT_LICENSE_INCLUDE = 'GPL* LGPL*'
#     COPYLEFT_LICENSE_EXCLUDE = 'CLOSED Proprietary'
# 10) The recipe type that will be archived:
#     COPYLEFT_RECIPE_TYPES = 'target'
# 11) The source mirror mode:
#     ARCHIVER_MODE[mirror] = "split" (default): Sources are split into
#     per-recipe directories in a similar way to other archiver modes.
#     Post-processing may be required to produce a single mirror directory.
#     This does however allow inspection of duplicate sources and more
#     intelligent handling.
#     ARCHIVER_MODE[mirror] = "combined": All sources are placed into a single
#     directory suitable for direct use as a mirror. Duplicate sources are
#     ignored.
# 12) Source mirror exclusions:
#     ARCHIVER_MIRROR_EXCLUDE is a list of prefixes to exclude from the mirror.
#     This may be used for sources which you are already publishing yourself
#     (e.g. if the URI starts with 'https://mysite.com/' and your mirror is
#     going to be published to the same site). It may also be used to exclude
#     local files (with the prefix 'file://') if these will be provided as part
#     of an archive of the layers themselves.
#

# Create archive for all the recipe types
COPYLEFT_RECIPE_TYPES ?= 'target native nativesdk cross crosssdk cross-canadian'
inherit copyleft_filter

ARCHIVER_MODE[srpm] ?= "0"
ARCHIVER_MODE[src] ?= "patched"
ARCHIVER_MODE[diff] ?= "0"
ARCHIVER_MODE[diff-exclude] ?= ".pc autom4te.cache patches"
ARCHIVER_MODE[dumpdata] ?= "0"
ARCHIVER_MODE[recipe] ?= "0"
ARCHIVER_MODE[mirror] ?= "split"
ARCHIVER_MODE[compression] ?= "xz"

DEPLOY_DIR_SRC ?= "${DEPLOY_DIR}/sources"
ARCHIVER_TOPDIR ?= "${WORKDIR}/archiver-sources"
ARCHIVER_ARCH = "${TARGET_SYS}"
ARCHIVER_OUTDIR = "${ARCHIVER_TOPDIR}/${ARCHIVER_ARCH}/${PF}/"
ARCHIVER_RPMTOPDIR ?= "${WORKDIR}/deploy-sources-rpm"
ARCHIVER_RPMOUTDIR = "${ARCHIVER_RPMTOPDIR}/${ARCHIVER_ARCH}/${PF}/"
ARCHIVER_WORKDIR = "${WORKDIR}/archiver-work/"

# When producing a combined mirror directory, allow duplicates for the case
# where multiple recipes use the same SRC_URI.
ARCHIVER_COMBINED_MIRRORDIR = "${ARCHIVER_TOPDIR}/mirror"
SSTATE_ALLOW_OVERLAP_FILES += "${DEPLOY_DIR_SRC}/mirror"

do_dumpdata[dirs] = "${ARCHIVER_OUTDIR}"
do_ar_recipe[dirs] = "${ARCHIVER_OUTDIR}"
do_ar_original[dirs] = "${ARCHIVER_OUTDIR} ${ARCHIVER_WORKDIR}"

# This is a convenience for the shell script to use it

def include_package(d, pn):

    included, reason = copyleft_should_include(d)
    if not included:
        bb.debug(1, 'archiver: %s is excluded: %s' % (pn, reason))
        return False

    else:
        bb.debug(1, 'archiver: %s is included: %s' % (pn, reason))

    # glibc-locale: do_fetch, do_unpack and do_patch tasks have been deleted,
    # so avoid archiving source here.
    if pn.startswith('glibc-locale'):
        return False

    # We just archive gcc-source for all the gcc related recipes
    if d.getVar('BPN') in ['gcc', 'libgcc'] \
            and not pn.startswith('gcc-source'):
        bb.debug(1, 'archiver: %s is excluded, covered by gcc-source' % pn)
        return False

    return True

python () {
    pn = d.getVar('PN')
    assume_provided = (d.getVar("ASSUME_PROVIDED") or "").split()
    if pn in assume_provided:
        for p in d.getVar("PROVIDES").split():
            if p != pn:
                pn = p
                break

    if not include_package(d, pn):
        return

    # TARGET_SYS in ARCHIVER_ARCH will break the stamp for gcc-source in multiconfig
    if pn.startswith('gcc-source'):
        d.setVar('ARCHIVER_ARCH', "allarch")

    def hasTask(task):
        return bool(d.getVarFlag(task, "task", False)) and not bool(d.getVarFlag(task, "noexec", False))

    ar_src = d.getVarFlag('ARCHIVER_MODE', 'src')
    ar_dumpdata = d.getVarFlag('ARCHIVER_MODE', 'dumpdata')
    ar_recipe = d.getVarFlag('ARCHIVER_MODE', 'recipe')

    if ar_src == "original":
        d.appendVarFlag('do_deploy_archives', 'depends', ' %s:do_ar_original' % pn)
        # 'patched' and 'configured' invoke do_unpack_and_patch because
        # do_ar_patched resp. do_ar_configured depend on it, but for 'original'
        # we have to add it explicitly.
        if d.getVarFlag('ARCHIVER_MODE', 'diff') == '1':
            d.appendVarFlag('do_deploy_archives', 'depends', ' %s:do_unpack_and_patch' % pn)
    elif ar_src == "patched":
        d.appendVarFlag('do_deploy_archives', 'depends', ' %s:do_ar_patched' % pn)
    elif ar_src == "configured":
        # We can't use "addtask do_ar_configured after do_configure" since it
        # will cause the deptask of do_populate_sysroot to run no matter what
        # archives we need, so we add the depends here.

        # There is a corner case with "gcc-source-${PV}" recipes, they don't have
        # the "do_configure" task, so we need to use "do_preconfigure"
        if hasTask("do_preconfigure"):
            d.appendVarFlag('do_ar_configured', 'depends', ' %s:do_preconfigure' % pn)
        elif hasTask("do_configure"):
            d.appendVarFlag('do_ar_configured', 'depends', ' %s:do_configure' % pn)
        d.appendVarFlag('do_deploy_archives', 'depends', ' %s:do_ar_configured' % pn)
    elif ar_src == "mirror":
        d.appendVarFlag('do_deploy_archives', 'depends', '%s:do_ar_mirror' % pn)

    elif ar_src:
        bb.fatal("Invalid ARCHIVER_MODE[src]: %s" % ar_src)

    if ar_dumpdata == "1":
        d.appendVarFlag('do_deploy_archives', 'depends', ' %s:do_dumpdata' % pn)

    if ar_recipe == "1":
        d.appendVarFlag('do_deploy_archives', 'depends', ' %s:do_ar_recipe' % pn)

    # Output the SRPM package
    if d.getVarFlag('ARCHIVER_MODE', 'srpm') == "1" and d.getVar('PACKAGES'):
        if "package_rpm" not in d.getVar('PACKAGE_CLASSES'):
            bb.fatal("ARCHIVER_MODE[srpm] needs package_rpm in PACKAGE_CLASSES")

        # Some recipes do not have any packaging tasks
        if hasTask("do_package_write_rpm"):
            d.appendVarFlag('do_deploy_archives', 'depends', ' %s:do_package_write_rpm' % pn)
            d.appendVarFlag('do_package_write_rpm', 'dirs', ' ${ARCHIVER_RPMTOPDIR}')
            d.appendVarFlag('do_package_write_rpm', 'sstate-inputdirs', ' ${ARCHIVER_RPMTOPDIR}')
            d.appendVarFlag('do_package_write_rpm', 'sstate-outputdirs', ' ${DEPLOY_DIR_SRC}')
            if ar_dumpdata == "1":
                d.appendVarFlag('do_package_write_rpm', 'depends', ' %s:do_dumpdata' % pn)
            if ar_recipe == "1":
                d.appendVarFlag('do_package_write_rpm', 'depends', ' %s:do_ar_recipe' % pn)
            if ar_src == "original":
                d.appendVarFlag('do_package_write_rpm', 'depends', ' %s:do_ar_original' % pn)
            elif ar_src == "patched":
                d.appendVarFlag('do_package_write_rpm', 'depends', ' %s:do_ar_patched' % pn)
            elif ar_src == "configured":
                d.appendVarFlag('do_package_write_rpm', 'depends', ' %s:do_ar_configured' % pn)
}

# Take all the sources for a recipe and put them in WORKDIR/archiver-work/.
# Files in SRC_URI are copied directly, anything that's a directory
# (e.g. git repositories) is "unpacked" and then put into a tarball.
python do_ar_original() {

    import shutil, tempfile

    if d.getVarFlag('ARCHIVER_MODE', 'src') != "original":
        return

    ar_outdir = d.getVar('ARCHIVER_OUTDIR')
    bb.note('Archiving the original source...')
    urls = d.getVar("SRC_URI").split()
    # destsuffix (git fetcher) and subdir (everything else) are allowed to be
    # absolute paths (for example, destsuffix=${S}/foobar).
    # That messes with unpacking inside our tmpdir below, because the fetchers
    # will then unpack in that directory and completely ignore the tmpdir.
    # That breaks parallel tasks relying on ${S}, like do_compile.
    #
    # To solve this, we remove these parameters from all URLs.
    # We do this even for relative paths because it makes the content of the
    # archives more useful (no extra paths that are only used during
    # compilation).
    for i, url in enumerate(urls):
        decoded = bb.fetch2.decodeurl(url)
        for param in ('destsuffix', 'subdir'):
            if param in decoded[5]:
                del decoded[5][param]
        encoded = bb.fetch2.encodeurl(decoded)
        urls[i] = encoded

    # Cleanup SRC_URI before call bb.fetch2.Fetch() since now SRC_URI is in the
    # variable "urls", otherwise there might be errors like:
    # The SRCREV_FORMAT variable must be set when multiple SCMs are used
    ld = bb.data.createCopy(d)
    ld.setVar('SRC_URI', '')
    fetch = bb.fetch2.Fetch(urls, ld)
    tarball_suffix = {}
    for url in fetch.urls:
        local = fetch.localpath(url).rstrip("/");
        if os.path.isfile(local):
            shutil.copy(local, ar_outdir)
        elif os.path.isdir(local):
            tmpdir = tempfile.mkdtemp(dir=d.getVar('ARCHIVER_WORKDIR'))
            fetch.unpack(tmpdir, (url,))
            # To handle recipes with more than one source, we add the "name"
            # URL parameter as suffix. We treat it as an error when
            # there's more than one URL without a name, or a name gets reused.
            # This is an additional safety net, in practice the name has
            # to be set when using the git fetcher, otherwise SRCREV cannot
            # be set separately for each URL.
            params = bb.fetch2.decodeurl(url)[5]
            type = bb.fetch2.decodeurl(url)[0]
            location = bb.fetch2.decodeurl(url)[2]
            name = params.get('name', '')
            if type.lower() == 'file':
                name_tmp = location.rstrip("*").rstrip("/")
                name = os.path.basename(name_tmp)
            else:
                if name in tarball_suffix:
                    if not name:
                        bb.fatal("Cannot determine archive names for original source because 'name' URL parameter is unset in more than one URL. Add it to at least one of these: %s %s" % (tarball_suffix[name], url))
                    else:
                        bb.fatal("Cannot determine archive names for original source because 'name=' URL parameter '%s' is used twice. Make it unique in: %s %s" % (tarball_suffix[name], url))
            tarball_suffix[name] = url
            create_tarball(d, tmpdir + '/.', name, ar_outdir)

    # Emit patch series files for 'original'
    bb.note('Writing patch series files...')
    for patch in src_patches(d):
        _, _, local, _, _, parm = bb.fetch.decodeurl(patch)
        patchdir = parm.get('patchdir')
        if patchdir:
            series = os.path.join(ar_outdir, 'series.subdir.%s' % patchdir.replace('/', '_'))
        else:
            series = os.path.join(ar_outdir, 'series')

        with open(series, 'a') as s:
            s.write('%s -p%s\n' % (os.path.basename(local), parm['striplevel']))
}

python do_ar_patched() {

    if d.getVarFlag('ARCHIVER_MODE', 'src') != 'patched':
        return

    # Get the ARCHIVER_OUTDIR before we reset the WORKDIR
    ar_outdir = d.getVar('ARCHIVER_OUTDIR')
    if not is_work_shared(d):
        ar_workdir = d.getVar('ARCHIVER_WORKDIR')
        d.setVar('WORKDIR', ar_workdir)
    bb.note('Archiving the patched source...')
    create_tarball(d, d.getVar('S'), 'patched', ar_outdir)
}

python do_ar_configured() {
    import shutil

    # Forcibly expand the sysroot paths as we're about to change WORKDIR
    d.setVar('STAGING_DIR_HOST', d.getVar('STAGING_DIR_HOST'))
    d.setVar('STAGING_DIR_TARGET', d.getVar('STAGING_DIR_TARGET'))
    d.setVar('RECIPE_SYSROOT', d.getVar('RECIPE_SYSROOT'))
    d.setVar('RECIPE_SYSROOT_NATIVE', d.getVar('RECIPE_SYSROOT_NATIVE'))

    ar_outdir = d.getVar('ARCHIVER_OUTDIR')
    if d.getVarFlag('ARCHIVER_MODE', 'src') == 'configured':
        bb.note('Archiving the configured source...')
        pn = d.getVar('PN')
        # "gcc-source-${PV}" recipes don't have "do_configure"
        # task, so we need to run "do_preconfigure" instead
        if pn.startswith("gcc-source-"):
            d.setVar('WORKDIR', d.getVar('ARCHIVER_WORKDIR'))
            bb.build.exec_func('do_preconfigure', d)

        # The libtool-native's do_configure will remove the
        # ${STAGING_DATADIR}/aclocal/libtool.m4, so we can't re-run the
        # do_configure, we archive the already configured ${S} to
        # instead of.
        # The kernel class functions require it to be on work-shared, we
        # don't unpack, patch, configure again, just archive the already
        # configured ${S}
        elif not (pn == 'libtool-native' or is_work_shared(d)):
            def runTask(task):
                prefuncs = d.getVarFlag(task, 'prefuncs') or ''
                for func in prefuncs.split():
                    if func != "sysroot_cleansstate":
                        bb.build.exec_func(func, d)
                bb.build.exec_func(task, d)
                postfuncs = d.getVarFlag(task, 'postfuncs') or ''
                for func in postfuncs.split():
                    if func != 'do_qa_configure':
                        bb.build.exec_func(func, d)

            # Change the WORKDIR to make do_configure run in another dir.
            d.setVar('WORKDIR', d.getVar('ARCHIVER_WORKDIR'))

            preceeds = bb.build.preceedtask('do_configure', False, d)
            for task in preceeds:
                if task != 'do_patch' and task != 'do_prepare_recipe_sysroot':
                    runTask(task)
            runTask('do_configure')

        srcdir = d.getVar('S')
        builddir = d.getVar('B')
        if srcdir != builddir:
            if os.path.exists(builddir):
                oe.path.copytree(builddir, os.path.join(srcdir, \
                    'build.%s.ar_configured' % d.getVar('PF')))
        create_tarball(d, srcdir, 'configured', ar_outdir)
}

python do_ar_mirror() {
    import subprocess

    src_uri = (d.getVar('SRC_URI') or '').split()
    if len(src_uri) == 0:
        return

    dl_dir = d.getVar('DL_DIR')
    mirror_exclusions = (d.getVar('ARCHIVER_MIRROR_EXCLUDE') or '').split()
    mirror_mode = d.getVarFlag('ARCHIVER_MODE', 'mirror')
    have_mirror_tarballs = d.getVar('BB_GENERATE_MIRROR_TARBALLS')

    if mirror_mode == 'combined':
        destdir = d.getVar('ARCHIVER_COMBINED_MIRRORDIR')
    elif mirror_mode == 'split':
        destdir = d.getVar('ARCHIVER_OUTDIR')
    else:
        bb.fatal('Invalid ARCHIVER_MODE[mirror]: %s' % (mirror_mode))

    if not have_mirror_tarballs:
        bb.fatal('Using `ARCHIVER_MODE[src] = "mirror"` depends on setting `BB_GENERATE_MIRROR_TARBALLS = "1"`')

    def is_excluded(url):
        for prefix in mirror_exclusions:
            if url.startswith(prefix):
                return True
        return False

    bb.note('Archiving the source as a mirror...')

    bb.utils.mkdirhier(destdir)

    fetcher = bb.fetch2.Fetch(src_uri, d)

    for ud in fetcher.expanded_urldata():
        if is_excluded(ud.url):
            bb.note('Skipping excluded url: %s' % (ud.url))
            continue

        bb.note('Archiving url: %s' % (ud.url))
        ud.setup_localpath(d)
        localpath = None

        # Check for mirror tarballs first. We will archive the first mirror
        # tarball that we find as it's assumed that we just need one.
        for mirror_fname in ud.mirrortarballs:
            mirror_path = os.path.join(dl_dir, mirror_fname)
            if os.path.exists(mirror_path):
                bb.note('Found mirror tarball: %s' % (mirror_path))
                localpath = mirror_path
                break

        if len(ud.mirrortarballs) and not localpath:
            bb.warn('Mirror tarballs are listed for a source but none are present. ' \
                    'Falling back to original download.\n' \
                    'SRC_URI = %s' % (ud.url))

        # Check original download
        if not localpath:
            bb.note('Using original download: %s' % (ud.localpath))
            localpath = ud.localpath

        if not localpath or not os.path.exists(localpath):
            bb.fatal('Original download is missing for a source.\n' \
                        'SRC_URI = %s' % (ud.url))

        # We now have an appropriate localpath
        bb.note('Copying source mirror')
        cmd = 'cp -fpPRH %s %s' % (localpath, destdir)
        subprocess.check_call(cmd, shell=True)
}

def create_tarball(d, srcdir, suffix, ar_outdir):
    """
    create the tarball from srcdir
    """
    import subprocess

    # Make sure we are only creating a single tarball for gcc sources
    if (d.getVar('SRC_URI') == ""):
        return

    # For the kernel archive, srcdir may just be a link to the
    # work-shared location. Use os.path.realpath to make sure
    # that we archive the actual directory and not just the link.
    srcdir = os.path.realpath(srcdir)

    compression_method = d.getVarFlag('ARCHIVER_MODE', 'compression')
    if compression_method == "xz":
        compression_cmd = "xz %s" % d.getVar('XZ_DEFAULTS')
    # To keep compatibility with ARCHIVER_MODE[compression]
    elif compression_method == "gz":
        compression_cmd = "gzip"
    elif compression_method == "bz2":
        compression_cmd = "bzip2"
    else:
        bb.fatal("Unsupported compression_method: %s" % compression_method)

    bb.utils.mkdirhier(ar_outdir)
    if suffix:
        filename = '%s-%s.tar.%s' % (d.getVar('PF'), suffix, compression_method)
    else:
        filename = '%s.tar.%s' % (d.getVar('PF'), compression_method)
    tarname = os.path.join(ar_outdir, filename)

    bb.note('Creating %s' % tarname)
    dirname = os.path.dirname(srcdir)
    basename = os.path.basename(srcdir)
    exclude = "--exclude=temp --exclude=patches --exclude='.pc'"
    tar_cmd = "tar %s -cf - %s | %s > %s" % (exclude, basename, compression_cmd, tarname)
    subprocess.check_call(tar_cmd, cwd=dirname, shell=True)

# creating .diff.gz between source.orig and source
def create_diff_gz(d, src_orig, src, ar_outdir):

    import subprocess

    if not os.path.isdir(src) or not os.path.isdir(src_orig):
        return

    # The diff --exclude can't exclude the file with path, so we copy
    # the patched source, and remove the files that we'd like to
    # exclude.
    src_patched = src + '.patched'
    oe.path.copyhardlinktree(src, src_patched)
    for i in d.getVarFlag('ARCHIVER_MODE', 'diff-exclude').split():
        bb.utils.remove(os.path.join(src_orig, i), recurse=True)
        bb.utils.remove(os.path.join(src_patched, i), recurse=True)

    dirname = os.path.dirname(src)
    basename = os.path.basename(src)
    bb.utils.mkdirhier(ar_outdir)
    cwd = os.getcwd()
    try:
        os.chdir(dirname)
        out_file = os.path.join(ar_outdir, '%s-diff.gz' % d.getVar('PF'))
        diff_cmd = 'diff -Naur %s.orig %s.patched | gzip -c > %s' % (basename, basename, out_file)
        subprocess.check_call(diff_cmd, shell=True)
        bb.utils.remove(src_patched, recurse=True)
    finally:
        os.chdir(cwd)

def is_work_shared(d):
    sharedworkdir = os.path.join(d.getVar('TMPDIR'), 'work-shared')
    sourcedir = os.path.realpath(d.getVar('S'))
    return sourcedir.startswith(sharedworkdir)

# Run do_unpack and do_patch
python do_unpack_and_patch() {
    if d.getVarFlag('ARCHIVER_MODE', 'src') not in \
            [ 'patched', 'configured'] and \
            d.getVarFlag('ARCHIVER_MODE', 'diff') != '1':
        return
    ar_outdir = d.getVar('ARCHIVER_OUTDIR')
    ar_workdir = d.getVar('ARCHIVER_WORKDIR')
    ar_sysroot_native = d.getVar('STAGING_DIR_NATIVE')
    pn = d.getVar('PN')

    # The kernel class functions require it to be on work-shared, so we don't change WORKDIR
    if not is_work_shared(d):
        # Change the WORKDIR to make do_unpack do_patch run in another dir.
        d.setVar('WORKDIR', ar_workdir)
        # Restore the original path to recipe's native sysroot (it's relative to WORKDIR).
        d.setVar('STAGING_DIR_NATIVE', ar_sysroot_native)

        # The changed 'WORKDIR' also caused 'B' changed, create dir 'B' for the
        # possibly requiring of the following tasks (such as some recipes's
        # do_patch required 'B' existed).
        bb.utils.mkdirhier(d.getVar('B'))

        bb.build.exec_func('do_unpack', d)

    # Save the original source for creating the patches
    if d.getVarFlag('ARCHIVER_MODE', 'diff') == '1':
        src = d.getVar('S').rstrip('/')
        src_orig = '%s.orig' % src
        oe.path.copytree(src, src_orig)

    if bb.data.inherits_class('dos2unix', d):
        bb.build.exec_func('do_convert_crlf_to_lf', d)

    # Make sure gcc and kernel sources are patched only once
    if not (d.getVar('SRC_URI') == "" or is_work_shared(d)):
        bb.build.exec_func('do_patch', d)

    # Create the patches
    if d.getVarFlag('ARCHIVER_MODE', 'diff') == '1':
        bb.note('Creating diff gz...')
        create_diff_gz(d, src_orig, src, ar_outdir)
        bb.utils.remove(src_orig, recurse=True)
}

# BBINCLUDED is special (excluded from basehash signature
# calculation). Using it in a task signature can cause "basehash
# changed" errors.
#
# Depending on BBINCLUDED also causes do_ar_recipe to run again
# for unrelated changes, like adding or removing buildhistory.bbclass.
#
# For these reasons we ignore the dependency completely. The versioning
# of the output file ensures that we create it each time the recipe
# gets rebuilt, at least as long as a PR server is used. We also rely
# on that mechanism to catch changes in the file content, because the
# file content is not part of the task signature either.
do_ar_recipe[vardepsexclude] += "BBINCLUDED"
python do_ar_recipe () {
    """
    archive the recipe, including .bb and .inc.
    """
    import re
    import shutil

    require_re = re.compile( r"require\s+(.+)" )
    include_re = re.compile( r"include\s+(.+)" )
    bbfile = d.getVar('FILE')
    outdir = os.path.join(d.getVar('WORKDIR'), \
            '%s-recipe' % d.getVar('PF'))
    bb.utils.mkdirhier(outdir)
    shutil.copy(bbfile, outdir)

    pn = d.getVar('PN')
    bbappend_files = d.getVar('BBINCLUDED').split()
    # If recipe name is aa, we need to match files like aa.bbappend and aa_1.1.bbappend
    # Files like aa1.bbappend or aa1_1.1.bbappend must be excluded.
    bbappend_re = re.compile( r".*/%s_[^/]*\.bbappend$" % re.escape(pn))
    bbappend_re1 = re.compile( r".*/%s\.bbappend$" % re.escape(pn))
    for file in bbappend_files:
        if bbappend_re.match(file) or bbappend_re1.match(file):
            shutil.copy(file, outdir)

    dirname = os.path.dirname(bbfile)
    bbpath = '%s:%s' % (dirname, d.getVar('BBPATH'))
    f = open(bbfile, 'r')
    for line in f.readlines():
        incfile = None
        if require_re.match(line):
            incfile = require_re.match(line).group(1)
        elif include_re.match(line):
            incfile = include_re.match(line).group(1)
        if incfile:
            incfile = d.expand(incfile)
        if incfile:
            incfile = bb.utils.which(bbpath, incfile)
        if incfile:
            shutil.copy(incfile, outdir)

    create_tarball(d, outdir, 'recipe', d.getVar('ARCHIVER_OUTDIR'))
    bb.utils.remove(outdir, recurse=True)
}

python do_dumpdata () {
    """
    dump environment data to ${PF}-showdata.dump
    """

    dumpfile = os.path.join(d.getVar('ARCHIVER_OUTDIR'), \
        '%s-showdata.dump' % d.getVar('PF'))
    bb.note('Dumping metadata into %s' % dumpfile)
    with open(dumpfile, "w") as f:
        # emit variables and shell functions
        bb.data.emit_env(f, d, True)
        # emit the metadata which isn't valid shell
        for e in d.keys():
            if d.getVarFlag(e, "python", False):
                f.write("\npython %s () {\n%s}\n" % (e, d.getVar(e, False)))
}

SSTATETASKS += "do_deploy_archives"
do_deploy_archives () {
    bbnote "Deploying source archive files from ${ARCHIVER_TOPDIR} to ${DEPLOY_DIR_SRC}."
}
python do_deploy_archives_setscene () {
    sstate_setscene(d)
}
do_deploy_archives[dirs] = "${ARCHIVER_TOPDIR}"
do_deploy_archives[sstate-inputdirs] = "${ARCHIVER_TOPDIR}"
do_deploy_archives[sstate-outputdirs] = "${DEPLOY_DIR_SRC}"
addtask do_deploy_archives_setscene

addtask do_ar_original after do_unpack
addtask do_unpack_and_patch after do_patch do_preconfigure
addtask do_ar_patched after do_unpack_and_patch
addtask do_ar_configured after do_unpack_and_patch
addtask do_ar_mirror after do_fetch
addtask do_dumpdata
addtask do_ar_recipe
addtask do_deploy_archives
do_build[recrdeptask] += "do_deploy_archives"
do_rootfs[recrdeptask] += "do_deploy_archives"
do_populate_sdk[recrdeptask] += "do_deploy_archives"

python () {
    # Add tasks in the correct order, specifically for linux-yocto to avoid race condition.
    # sstatesig.py:sstate_rundepfilter has special support that excludes this dependency
    # so that do_kernel_configme does not need to run again when do_unpack_and_patch
    # gets added or removed (by adding or removing archiver.bbclass).
    if bb.data.inherits_class('kernel-yocto', d):
        bb.build.addtask('do_kernel_configme', 'do_configure', 'do_unpack_and_patch', d)
}
