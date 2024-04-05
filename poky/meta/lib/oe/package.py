#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import errno
import fnmatch
import itertools
import os
import shlex
import re
import glob
import stat
import mmap
import subprocess

import oe.cachedpath

def runstrip(arg):
    # Function to strip a single file, called from split_and_strip_files below
    # A working 'file' (one which works on the target architecture)
    #
    # The elftype is a bit pattern (explained in is_elf below) to tell
    # us what type of file we're processing...
    # 4 - executable
    # 8 - shared library
    # 16 - kernel module

    if len(arg) == 3:
        (file, elftype, strip) = arg
        extra_strip_sections = ''
    else:
        (file, elftype, strip, extra_strip_sections) = arg

    newmode = None
    if not os.access(file, os.W_OK) or os.access(file, os.R_OK):
        origmode = os.stat(file)[stat.ST_MODE]
        newmode = origmode | stat.S_IWRITE | stat.S_IREAD
        os.chmod(file, newmode)

    stripcmd = [strip]
    skip_strip = False
    # kernel module
    if elftype & 16:
        if is_kernel_module_signed(file):
            bb.debug(1, "Skip strip on signed module %s" % file)
            skip_strip = True
        else:
            stripcmd.extend(["--strip-debug", "--remove-section=.comment",
                "--remove-section=.note", "--preserve-dates"])
    # .so and shared library
    elif ".so" in file and elftype & 8:
        stripcmd.extend(["--remove-section=.comment", "--remove-section=.note", "--strip-unneeded"])
    # shared or executable:
    elif elftype & 8 or elftype & 4:
        stripcmd.extend(["--remove-section=.comment", "--remove-section=.note"])
        if extra_strip_sections != '':
            for section in extra_strip_sections.split():
                stripcmd.extend(["--remove-section=" + section])

    stripcmd.append(file)
    bb.debug(1, "runstrip: %s" % stripcmd)

    if not skip_strip:
        output = subprocess.check_output(stripcmd, stderr=subprocess.STDOUT)

    if newmode:
        os.chmod(file, origmode)

# Detect .ko module by searching for "vermagic=" string
def is_kernel_module(path):
    with open(path) as f:
        return mmap.mmap(f.fileno(), 0, prot=mmap.PROT_READ).find(b"vermagic=") >= 0

# Detect if .ko module is signed
def is_kernel_module_signed(path):
    with open(path, "rb") as f:
        f.seek(-28, 2)
        module_tail = f.read()
        return "Module signature appended" in "".join(chr(c) for c in bytearray(module_tail))

# Return type (bits):
# 0 - not elf
# 1 - ELF
# 2 - stripped
# 4 - executable
# 8 - shared library
# 16 - kernel module
def is_elf(path):
    exec_type = 0
    result = subprocess.check_output(["file", "-b", path], stderr=subprocess.STDOUT).decode("utf-8")

    if "ELF" in result:
        exec_type |= 1
        if "not stripped" not in result:
            exec_type |= 2
        if "executable" in result:
            exec_type |= 4
        if "shared" in result:
            exec_type |= 8
        if "relocatable" in result:
            if path.endswith(".ko") and path.find("/lib/modules/") != -1 and is_kernel_module(path):
                exec_type |= 16
    return (path, exec_type)

def is_static_lib(path):
    if path.endswith('.a') and not os.path.islink(path):
        with open(path, 'rb') as fh:
            # The magic must include the first slash to avoid
            # matching golang static libraries
            magic = b'!<arch>\x0a/'
            start = fh.read(len(magic))
            return start == magic
    return False

def strip_execs(pn, dstdir, strip_cmd, libdir, base_libdir, max_process, qa_already_stripped=False):
    """
    Strip executable code (like executables, shared libraries) _in_place_
    - Based on sysroot_strip in staging.bbclass
    :param dstdir: directory in which to strip files
    :param strip_cmd: Strip command (usually ${STRIP})
    :param libdir: ${libdir} - strip .so files in this directory
    :param base_libdir: ${base_libdir} - strip .so files in this directory
    :param max_process: number of stripping processes started in parallel
    :param qa_already_stripped: Set to True if already-stripped' in ${INSANE_SKIP}
    This is for proper logging and messages only.
    """
    import stat, errno, oe.path, oe.utils

    elffiles = {}
    inodes = {}
    libdir = os.path.abspath(dstdir + os.sep + libdir)
    base_libdir = os.path.abspath(dstdir + os.sep + base_libdir)
    exec_mask = stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH
    #
    # First lets figure out all of the files we may have to process
    #
    checkelf = []
    inodecache = {}
    for root, dirs, files in os.walk(dstdir):
        for f in files:
            file = os.path.join(root, f)

            try:
                ltarget = oe.path.realpath(file, dstdir, False)
                s = os.lstat(ltarget)
            except OSError as e:
                (err, strerror) = e.args
                if err != errno.ENOENT:
                    raise
                # Skip broken symlinks
                continue
            if not s:
                continue
            # Check its an excutable
            if s[stat.ST_MODE] & exec_mask \
                    or ((file.startswith(libdir) or file.startswith(base_libdir)) and ".so" in f) \
                    or file.endswith('.ko'):
                # If it's a symlink, and points to an ELF file, we capture the readlink target
                if os.path.islink(file):
                    continue

                # It's a file (or hardlink), not a link
                # ...but is it ELF, and is it already stripped?
                checkelf.append(file)
                inodecache[file] = s.st_ino
    results = oe.utils.multiprocess_launch_mp(is_elf, checkelf, max_process)
    for (file, elf_file) in results:
                #elf_file = is_elf(file)
                if elf_file & 1:
                    if elf_file & 2:
                        if qa_already_stripped:
                            bb.note("Skipping file %s from %s for already-stripped QA test" % (file[len(dstdir):], pn))
                        else:
                            bb.warn("File '%s' from %s was already stripped, this will prevent future debugging!" % (file[len(dstdir):], pn))
                        continue

                    if inodecache[file] in inodes:
                        os.unlink(file)
                        os.link(inodes[inodecache[file]], file)
                    else:
                        # break hardlinks so that we do not strip the original.
                        inodes[inodecache[file]] = file
                        bb.utils.break_hardlinks(file)
                        elffiles[file] = elf_file

    #
    # Now strip them (in parallel)
    #
    sfiles = []
    for file in elffiles:
        elf_file = int(elffiles[file])
        sfiles.append((file, elf_file, strip_cmd))

    oe.utils.multiprocess_launch_mp(runstrip, sfiles, max_process)


def file_translate(file):
    ft = file.replace("@", "@at@")
    ft = ft.replace(" ", "@space@")
    ft = ft.replace("\t", "@tab@")
    ft = ft.replace("[", "@openbrace@")
    ft = ft.replace("]", "@closebrace@")
    ft = ft.replace("_", "@underscore@")
    return ft

def filedeprunner(arg):
    import re, subprocess, shlex

    (pkg, pkgfiles, rpmdeps, pkgdest) = arg
    provides = {}
    requires = {}

    file_re = re.compile(r'\s+\d+\s(.*)')
    dep_re = re.compile(r'\s+(\S)\s+(.*)')
    r = re.compile(r'[<>=]+\s+\S*')

    def process_deps(pipe, pkg, pkgdest, provides, requires):
        file = None
        for line in pipe.split("\n"):

            m = file_re.match(line)
            if m:
                file = m.group(1)
                file = file.replace(pkgdest + "/" + pkg, "")
                file = file_translate(file)
                continue

            m = dep_re.match(line)
            if not m or not file:
                continue

            type, dep = m.groups()

            if type == 'R':
                i = requires
            elif type == 'P':
                i = provides
            else:
               continue

            if dep.startswith("python("):
                continue

            # Ignore all perl(VMS::...) and perl(Mac::...) dependencies. These
            # are typically used conditionally from the Perl code, but are
            # generated as unconditional dependencies.
            if dep.startswith('perl(VMS::') or dep.startswith('perl(Mac::'):
                continue

            # Ignore perl dependencies on .pl files.
            if dep.startswith('perl(') and dep.endswith('.pl)'):
                continue

            # Remove perl versions and perl module versions since they typically
            # do not make sense when used as package versions.
            if dep.startswith('perl') and r.search(dep):
                dep = dep.split()[0]

            # Put parentheses around any version specifications.
            dep = r.sub(r'(\g<0>)',dep)

            if file not in i:
                i[file] = []
            i[file].append(dep)

        return provides, requires

    output = subprocess.check_output(shlex.split(rpmdeps) + pkgfiles, stderr=subprocess.STDOUT).decode("utf-8")
    provides, requires = process_deps(output, pkg, pkgdest, provides, requires)

    return (pkg, provides, requires)


def read_shlib_providers(d):
    import re

    shlib_provider = {}
    shlibs_dirs = d.getVar('SHLIBSDIRS').split()
    list_re = re.compile(r'^(.*)\.list$')
    # Go from least to most specific since the last one found wins
    for dir in reversed(shlibs_dirs):
        bb.debug(2, "Reading shlib providers in %s" % (dir))
        if not os.path.exists(dir):
            continue
        for file in sorted(os.listdir(dir)):
            m = list_re.match(file)
            if m:
                dep_pkg = m.group(1)
                try:
                    fd = open(os.path.join(dir, file))
                except IOError:
                    # During a build unrelated shlib files may be deleted, so
                    # handle files disappearing between the listdirs and open.
                    continue
                lines = fd.readlines()
                fd.close()
                for l in lines:
                    s = l.strip().split(":")
                    if s[0] not in shlib_provider:
                        shlib_provider[s[0]] = {}
                    shlib_provider[s[0]][s[1]] = (dep_pkg, s[2])
    return shlib_provider

# We generate a master list of directories to process, we start by
# seeding this list with reasonable defaults, then load from
# the fs-perms.txt files
def fixup_perms(d):
    import pwd, grp

    cpath = oe.cachedpath.CachedPath()
    dvar = d.getVar('PKGD')

    # init using a string with the same format as a line as documented in
    # the fs-perms.txt file
    # <path> <mode> <uid> <gid> <walk> <fmode> <fuid> <fgid>
    # <path> link <link target>
    #
    # __str__ can be used to print out an entry in the input format
    #
    # if fs_perms_entry.path is None:
    #    an error occurred
    # if fs_perms_entry.link, you can retrieve:
    #    fs_perms_entry.path = path
    #    fs_perms_entry.link = target of link
    # if not fs_perms_entry.link, you can retrieve:
    #    fs_perms_entry.path = path
    #    fs_perms_entry.mode = expected dir mode or None
    #    fs_perms_entry.uid = expected uid or -1
    #    fs_perms_entry.gid = expected gid or -1
    #    fs_perms_entry.walk = 'true' or something else
    #    fs_perms_entry.fmode = expected file mode or None
    #    fs_perms_entry.fuid = expected file uid or -1
    #    fs_perms_entry_fgid = expected file gid or -1
    class fs_perms_entry():
        def __init__(self, line):
            lsplit = line.split()
            if len(lsplit) == 3 and lsplit[1].lower() == "link":
                self._setlink(lsplit[0], lsplit[2])
            elif len(lsplit) == 8:
                self._setdir(lsplit[0], lsplit[1], lsplit[2], lsplit[3], lsplit[4], lsplit[5], lsplit[6], lsplit[7])
            else:
                msg = "Fixup Perms: invalid config line %s" % line
                oe.qa.handle_error("perm-config", msg, d)
                self.path = None
                self.link = None

        def _setdir(self, path, mode, uid, gid, walk, fmode, fuid, fgid):
            self.path = os.path.normpath(path)
            self.link = None
            self.mode = self._procmode(mode)
            self.uid  = self._procuid(uid)
            self.gid  = self._procgid(gid)
            self.walk = walk.lower()
            self.fmode = self._procmode(fmode)
            self.fuid = self._procuid(fuid)
            self.fgid = self._procgid(fgid)

        def _setlink(self, path, link):
            self.path = os.path.normpath(path)
            self.link = link

        def _procmode(self, mode):
            if not mode or (mode and mode == "-"):
                return None
            else:
                return int(mode,8)

        # Note uid/gid -1 has special significance in os.lchown
        def _procuid(self, uid):
            if uid is None or uid == "-":
                return -1
            elif uid.isdigit():
                return int(uid)
            else:
                return pwd.getpwnam(uid).pw_uid

        def _procgid(self, gid):
            if gid is None or gid == "-":
                return -1
            elif gid.isdigit():
                return int(gid)
            else:
                return grp.getgrnam(gid).gr_gid

        # Use for debugging the entries
        def __str__(self):
            if self.link:
                return "%s link %s" % (self.path, self.link)
            else:
                mode = "-"
                if self.mode:
                    mode = "0%o" % self.mode
                fmode = "-"
                if self.fmode:
                    fmode = "0%o" % self.fmode
                uid = self._mapugid(self.uid)
                gid = self._mapugid(self.gid)
                fuid = self._mapugid(self.fuid)
                fgid = self._mapugid(self.fgid)
                return "%s %s %s %s %s %s %s %s" % (self.path, mode, uid, gid, self.walk, fmode, fuid, fgid)

        def _mapugid(self, id):
            if id is None or id == -1:
                return "-"
            else:
                return "%d" % id

    # Fix the permission, owner and group of path
    def fix_perms(path, mode, uid, gid, dir):
        if mode and not os.path.islink(path):
            #bb.note("Fixup Perms: chmod 0%o %s" % (mode, dir))
            os.chmod(path, mode)
        # -1 is a special value that means don't change the uid/gid
        # if they are BOTH -1, don't bother to lchown
        if not (uid == -1 and gid == -1):
            #bb.note("Fixup Perms: lchown %d:%d %s" % (uid, gid, dir))
            os.lchown(path, uid, gid)

    # Return a list of configuration files based on either the default
    # files/fs-perms.txt or the contents of FILESYSTEM_PERMS_TABLES
    # paths are resolved via BBPATH
    def get_fs_perms_list(d):
        str = ""
        bbpath = d.getVar('BBPATH')
        fs_perms_tables = d.getVar('FILESYSTEM_PERMS_TABLES') or ""
        for conf_file in fs_perms_tables.split():
            confpath = bb.utils.which(bbpath, conf_file)
            if confpath:
                str += " %s" % bb.utils.which(bbpath, conf_file)
            else:
                bb.warn("cannot find %s specified in FILESYSTEM_PERMS_TABLES" % conf_file)
        return str

    fs_perms_table = {}
    fs_link_table = {}

    # By default all of the standard directories specified in
    # bitbake.conf will get 0755 root:root.
    target_path_vars = [    'base_prefix',
                'prefix',
                'exec_prefix',
                'base_bindir',
                'base_sbindir',
                'base_libdir',
                'datadir',
                'sysconfdir',
                'servicedir',
                'sharedstatedir',
                'localstatedir',
                'infodir',
                'mandir',
                'docdir',
                'bindir',
                'sbindir',
                'libexecdir',
                'libdir',
                'includedir' ]

    for path in target_path_vars:
        dir = d.getVar(path) or ""
        if dir == "":
            continue
        fs_perms_table[dir] = fs_perms_entry(d.expand("%s 0755 root root false - - -" % (dir)))

    # Now we actually load from the configuration files
    for conf in get_fs_perms_list(d).split():
        if not os.path.exists(conf):
            continue
        with open(conf) as f:
            for line in f:
                if line.startswith('#'):
                    continue
                lsplit = line.split()
                if len(lsplit) == 0:
                    continue
                if len(lsplit) != 8 and not (len(lsplit) == 3 and lsplit[1].lower() == "link"):
                    msg = "Fixup perms: %s invalid line: %s" % (conf, line)
                    oe.qa.handle_error("perm-line", msg, d)
                    continue
                entry = fs_perms_entry(d.expand(line))
                if entry and entry.path:
                    if entry.link:
                        fs_link_table[entry.path] = entry
                        if entry.path in fs_perms_table:
                            fs_perms_table.pop(entry.path)
                    else:
                        fs_perms_table[entry.path] = entry
                        if entry.path in fs_link_table:
                            fs_link_table.pop(entry.path)

    # Debug -- list out in-memory table
    #for dir in fs_perms_table:
    #    bb.note("Fixup Perms: %s: %s" % (dir, str(fs_perms_table[dir])))
    #for link in fs_link_table:
    #    bb.note("Fixup Perms: %s: %s" % (link, str(fs_link_table[link])))

    # We process links first, so we can go back and fixup directory ownership
    # for any newly created directories
    # Process in sorted order so /run gets created before /run/lock, etc.
    for entry in sorted(fs_link_table.values(), key=lambda x: x.link):
        link = entry.link
        dir = entry.path
        origin = dvar + dir
        if not (cpath.exists(origin) and cpath.isdir(origin) and not cpath.islink(origin)):
            continue

        if link[0] == "/":
            target = dvar + link
            ptarget = link
        else:
            target = os.path.join(os.path.dirname(origin), link)
            ptarget = os.path.join(os.path.dirname(dir), link)
        if os.path.exists(target):
            msg = "Fixup Perms: Unable to correct directory link, target already exists: %s -> %s" % (dir, ptarget)
            oe.qa.handle_error("perm-link", msg, d)
            continue

        # Create path to move directory to, move it, and then setup the symlink
        bb.utils.mkdirhier(os.path.dirname(target))
        #bb.note("Fixup Perms: Rename %s -> %s" % (dir, ptarget))
        bb.utils.rename(origin, target)
        #bb.note("Fixup Perms: Link %s -> %s" % (dir, link))
        os.symlink(link, origin)

    for dir in fs_perms_table:
        origin = dvar + dir
        if not (cpath.exists(origin) and cpath.isdir(origin)):
            continue

        fix_perms(origin, fs_perms_table[dir].mode, fs_perms_table[dir].uid, fs_perms_table[dir].gid, dir)

        if fs_perms_table[dir].walk == 'true':
            for root, dirs, files in os.walk(origin):
                for dr in dirs:
                    each_dir = os.path.join(root, dr)
                    fix_perms(each_dir, fs_perms_table[dir].mode, fs_perms_table[dir].uid, fs_perms_table[dir].gid, dir)
                for f in files:
                    each_file = os.path.join(root, f)
                    fix_perms(each_file, fs_perms_table[dir].fmode, fs_perms_table[dir].fuid, fs_perms_table[dir].fgid, dir)

# Get a list of files from file vars by searching files under current working directory
# The list contains symlinks, directories and normal files.
def files_from_filevars(filevars):
    cpath = oe.cachedpath.CachedPath()
    files = []
    for f in filevars:
        if os.path.isabs(f):
            f = '.' + f
        if not f.startswith("./"):
            f = './' + f
        globbed = glob.glob(f, recursive=True)
        if globbed:
            if [ f ] != globbed:
                files += globbed
                continue
        files.append(f)

    symlink_paths = []
    for ind, f in enumerate(files):
        # Handle directory symlinks. Truncate path to the lowest level symlink
        parent = ''
        for dirname in f.split('/')[:-1]:
            parent = os.path.join(parent, dirname)
            if dirname == '.':
                continue
            if cpath.islink(parent):
                bb.warn("FILES contains file '%s' which resides under a "
                        "directory symlink. Please fix the recipe and use the "
                        "real path for the file." % f[1:])
                symlink_paths.append(f)
                files[ind] = parent
                f = parent
                break

        if not cpath.islink(f):
            if cpath.isdir(f):
                newfiles = [ os.path.join(f,x) for x in os.listdir(f) ]
                if newfiles:
                    files += newfiles

    return files, symlink_paths

# Called in package_<rpm,ipk,deb>.bbclass to get the correct list of configuration files
def get_conffiles(pkg, d):
    pkgdest = d.getVar('PKGDEST')
    root = os.path.join(pkgdest, pkg)
    cwd = os.getcwd()
    os.chdir(root)

    conffiles = d.getVar('CONFFILES:%s' % pkg);
    if conffiles == None:
        conffiles = d.getVar('CONFFILES')
    if conffiles == None:
        conffiles = ""
    conffiles = conffiles.split()
    conf_orig_list = files_from_filevars(conffiles)[0]

    # Remove links and directories from conf_orig_list to get conf_list which only contains normal files
    conf_list = []
    for f in conf_orig_list:
        if os.path.isdir(f):
            continue
        if os.path.islink(f):
            continue
        if not os.path.exists(f):
            continue
        conf_list.append(f)

    # Remove the leading './'
    for i in range(0, len(conf_list)):
        conf_list[i] = conf_list[i][1:]

    os.chdir(cwd)
    return sorted(conf_list)

def legitimize_package_name(s):
    """
    Make sure package names are legitimate strings
    """

    def fixutf(m):
        cp = m.group(1)
        if cp:
            return ('\\u%s' % cp).encode('latin-1').decode('unicode_escape')

    # Handle unicode codepoints encoded as <U0123>, as in glibc locale files.
    s = re.sub(r'<U([0-9A-Fa-f]{1,4})>', fixutf, s)

    # Remaining package name validity fixes
    return s.lower().replace('_', '-').replace('@', '+').replace(',', '+').replace('/', '-')

def split_locales(d):
    cpath = oe.cachedpath.CachedPath()
    if (d.getVar('PACKAGE_NO_LOCALE') == '1'):
        bb.debug(1, "package requested not splitting locales")
        return

    packages = (d.getVar('PACKAGES') or "").split()

    dvar = d.getVar('PKGD')
    pn = d.getVar('LOCALEBASEPN')

    try:
        locale_index = packages.index(pn + '-locale')
        packages.pop(locale_index)
    except ValueError:
        locale_index = len(packages)

    localepaths = []
    locales = set()
    for localepath in (d.getVar('LOCALE_PATHS') or "").split():
        localedir = dvar + localepath
        if not cpath.isdir(localedir):
            bb.debug(1, 'No locale files in %s' % localepath)
            continue

        localepaths.append(localepath)
        with os.scandir(localedir) as it:
            for entry in it:
                if entry.is_dir():
                    locales.add(entry.name)

    if len(locales) == 0:
        bb.debug(1, "No locale files in this package")
        return

    summary = d.getVar('SUMMARY') or pn
    description = d.getVar('DESCRIPTION') or ""
    locale_section = d.getVar('LOCALE_SECTION')
    mlprefix = d.getVar('MLPREFIX') or ""
    for l in sorted(locales):
        ln = legitimize_package_name(l)
        pkg = pn + '-locale-' + ln
        packages.insert(locale_index, pkg)
        locale_index += 1
        files = []
        for localepath in localepaths:
            files.append(os.path.join(localepath, l))
        d.setVar('FILES:' + pkg, " ".join(files))
        d.setVar('RRECOMMENDS:' + pkg, '%svirtual-locale-%s' % (mlprefix, ln))
        d.setVar('RPROVIDES:' + pkg, '%s-locale %s%s-translation' % (pn, mlprefix, ln))
        d.setVar('SUMMARY:' + pkg, '%s - %s translations' % (summary, l))
        d.setVar('DESCRIPTION:' + pkg, '%s  This package contains language translation files for the %s locale.' % (description, l))
        if locale_section:
            d.setVar('SECTION:' + pkg, locale_section)

    d.setVar('PACKAGES', ' '.join(packages))

    # Disabled by RP 18/06/07
    # Wildcards aren't supported in debian
    # They break with ipkg since glibc-locale* will mean that
    # glibc-localedata-translit* won't install as a dependency
    # for some other package which breaks meta-toolchain
    # Probably breaks since virtual-locale- isn't provided anywhere
    #rdep = (d.getVar('RDEPENDS:%s' % pn) or "").split()
    #rdep.append('%s-locale*' % pn)
    #d.setVar('RDEPENDS:%s' % pn, ' '.join(rdep))

def package_debug_vars(d):
    # We default to '.debug' style
    if d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-file-directory':
        # Single debug-file-directory style debug info
        debug_vars = {
            "append": ".debug",
            "staticappend": "",
            "dir": "",
            "staticdir": "",
            "libdir": "/usr/lib/debug",
            "staticlibdir": "/usr/lib/debug-static",
            "srcdir": "/usr/src/debug",
        }
    elif d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-without-src':
        # Original OE-core, a.k.a. ".debug", style debug info, but without sources in /usr/src/debug
        debug_vars = {
            "append": "",
            "staticappend": "",
            "dir": "/.debug",
            "staticdir": "/.debug-static",
            "libdir": "",
            "staticlibdir": "",
            "srcdir": "",
        }
    elif d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-with-srcpkg':
        debug_vars = {
            "append": "",
            "staticappend": "",
            "dir": "/.debug",
            "staticdir": "/.debug-static",
            "libdir": "",
            "staticlibdir": "",
            "srcdir": "/usr/src/debug",
        }
    else:
        # Original OE-core, a.k.a. ".debug", style debug info
        debug_vars = {
            "append": "",
            "staticappend": "",
            "dir": "/.debug",
            "staticdir": "/.debug-static",
            "libdir": "",
            "staticlibdir": "",
            "srcdir": "/usr/src/debug",
        }

    return debug_vars


def parse_debugsources_from_dwarfsrcfiles_output(dwarfsrcfiles_output):
    debugfiles = {}

    for line in dwarfsrcfiles_output.splitlines():
        if line.startswith("\t"):
            debugfiles[os.path.normpath(line.split()[0])] = ""

    return debugfiles.keys()

def source_info(file, d, fatal=True):
    cmd = ["dwarfsrcfiles", file]
    try:
        output = subprocess.check_output(cmd, universal_newlines=True, stderr=subprocess.STDOUT)
        retval = 0
    except subprocess.CalledProcessError as exc:
        output = exc.output
        retval = exc.returncode

    # 255 means a specific file wasn't fully parsed to get the debug file list, which is not a fatal failure
    if retval != 0 and retval != 255:
        msg = "dwarfsrcfiles failed with exit code %s (cmd was %s)%s" % (retval, cmd, ":\n%s" % output if output else "")
        if fatal:
            bb.fatal(msg)
        bb.note(msg)

    debugsources = parse_debugsources_from_dwarfsrcfiles_output(output)

    return list(debugsources)

def splitdebuginfo(file, dvar, dv, d):
    # Function to split a single file into two components, one is the stripped
    # target system binary, the other contains any debugging information. The
    # two files are linked to reference each other.
    #
    # return a mapping of files:debugsources

    src = file[len(dvar):]
    dest = dv["libdir"] + os.path.dirname(src) + dv["dir"] + "/" + os.path.basename(src) + dv["append"]
    debugfile = dvar + dest
    sources = []

    if file.endswith(".ko") and file.find("/lib/modules/") != -1:
        if oe.package.is_kernel_module_signed(file):
            bb.debug(1, "Skip strip on signed module %s" % file)
            return (file, sources)

    # Split the file...
    bb.utils.mkdirhier(os.path.dirname(debugfile))
    #bb.note("Split %s -> %s" % (file, debugfile))
    # Only store off the hard link reference if we successfully split!

    dvar = d.getVar('PKGD')
    objcopy = d.getVar("OBJCOPY")

    newmode = None
    if not os.access(file, os.W_OK) or os.access(file, os.R_OK):
        origmode = os.stat(file)[stat.ST_MODE]
        newmode = origmode | stat.S_IWRITE | stat.S_IREAD
        os.chmod(file, newmode)

    # We need to extract the debug src information here...
    if dv["srcdir"]:
        sources = source_info(file, d)

    bb.utils.mkdirhier(os.path.dirname(debugfile))

    subprocess.check_output([objcopy, '--only-keep-debug', file, debugfile], stderr=subprocess.STDOUT)

    # Set the debuglink to have the view of the file path on the target
    subprocess.check_output([objcopy, '--add-gnu-debuglink', debugfile, file], stderr=subprocess.STDOUT)

    if newmode:
        os.chmod(file, origmode)

    return (file, sources)

def splitstaticdebuginfo(file, dvar, dv, d):
    # Unlike the function above, there is no way to split a static library
    # two components.  So to get similar results we will copy the unmodified
    # static library (containing the debug symbols) into a new directory.
    # We will then strip (preserving symbols) the static library in the
    # typical location.
    #
    # return a mapping of files:debugsources

    src = file[len(dvar):]
    dest = dv["staticlibdir"] + os.path.dirname(src) + dv["staticdir"] + "/" + os.path.basename(src) + dv["staticappend"]
    debugfile = dvar + dest
    sources = []

    # Copy the file...
    bb.utils.mkdirhier(os.path.dirname(debugfile))
    #bb.note("Copy %s -> %s" % (file, debugfile))

    dvar = d.getVar('PKGD')

    newmode = None
    if not os.access(file, os.W_OK) or os.access(file, os.R_OK):
        origmode = os.stat(file)[stat.ST_MODE]
        newmode = origmode | stat.S_IWRITE | stat.S_IREAD
        os.chmod(file, newmode)

    # We need to extract the debug src information here...
    if dv["srcdir"]:
        sources = source_info(file, d)

    bb.utils.mkdirhier(os.path.dirname(debugfile))

    # Copy the unmodified item to the debug directory
    shutil.copy2(file, debugfile)

    if newmode:
        os.chmod(file, origmode)

    return (file, sources)

def inject_minidebuginfo(file, dvar, dv, d):
    # Extract just the symbols from debuginfo into minidebuginfo,
    # compress it with xz and inject it back into the binary in a .gnu_debugdata section.
    # https://sourceware.org/gdb/onlinedocs/gdb/MiniDebugInfo.html

    readelf = d.getVar('READELF')
    nm = d.getVar('NM')
    objcopy = d.getVar('OBJCOPY')

    minidebuginfodir = d.expand('${WORKDIR}/minidebuginfo')

    src = file[len(dvar):]
    dest = dv["libdir"] + os.path.dirname(src) + dv["dir"] + "/" + os.path.basename(src) + dv["append"]
    debugfile = dvar + dest
    minidebugfile = minidebuginfodir + src + '.minidebug'
    bb.utils.mkdirhier(os.path.dirname(minidebugfile))

    # If we didn't produce debuginfo for any reason, we can't produce minidebuginfo either
    # so skip it.
    if not os.path.exists(debugfile):
        bb.debug(1, 'ELF file {} has no debuginfo, skipping minidebuginfo injection'.format(file))
        return

    # minidebuginfo does not make sense to apply to ELF objects other than
    # executables and shared libraries, skip applying the minidebuginfo
    # generation for objects like kernel modules.
    for line in subprocess.check_output([readelf, '-h', debugfile], universal_newlines=True).splitlines():
        if not line.strip().startswith("Type:"):
            continue
        elftype = line.split(":")[1].strip()
        if not any(elftype.startswith(i) for i in ["EXEC", "DYN"]):
            bb.debug(1, 'ELF file {} is not executable/shared, skipping minidebuginfo injection'.format(file))
            return
        break

    # Find non-allocated PROGBITS, NOTE, and NOBITS sections in the debuginfo.
    # We will exclude all of these from minidebuginfo to save space.
    remove_section_names = []
    for line in subprocess.check_output([readelf, '-W', '-S', debugfile], universal_newlines=True).splitlines():
        # strip the leading "  [ 1]" section index to allow splitting on space
        if ']' not in line:
            continue
        fields = line[line.index(']') + 1:].split()
        if len(fields) < 7:
            continue
        name = fields[0]
        type = fields[1]
        flags = fields[6]
        # .debug_ sections will be removed by objcopy -S so no need to explicitly remove them
        if name.startswith('.debug_'):
            continue
        if 'A' not in flags and type in ['PROGBITS', 'NOTE', 'NOBITS']:
            remove_section_names.append(name)

    # List dynamic symbols in the binary. We can exclude these from minidebuginfo
    # because they are always present in the binary.
    dynsyms = set()
    for line in subprocess.check_output([nm, '-D', file, '--format=posix', '--defined-only'], universal_newlines=True).splitlines():
        dynsyms.add(line.split()[0])

    # Find all function symbols from debuginfo which aren't in the dynamic symbols table.
    # These are the ones we want to keep in minidebuginfo.
    keep_symbols_file = minidebugfile + '.symlist'
    found_any_symbols = False
    with open(keep_symbols_file, 'w') as f:
        for line in subprocess.check_output([nm, debugfile, '--format=sysv', '--defined-only'], universal_newlines=True).splitlines():
            fields = line.split('|')
            if len(fields) < 7:
                continue
            name = fields[0].strip()
            type = fields[3].strip()
            if type == 'FUNC' and name not in dynsyms:
                f.write('{}\n'.format(name))
                found_any_symbols = True

    if not found_any_symbols:
        bb.debug(1, 'ELF file {} contains no symbols, skipping minidebuginfo injection'.format(file))
        return

    bb.utils.remove(minidebugfile)
    bb.utils.remove(minidebugfile + '.xz')

    subprocess.check_call([objcopy, '-S'] +
                          ['--remove-section={}'.format(s) for s in remove_section_names] +
                          ['--keep-symbols={}'.format(keep_symbols_file), debugfile, minidebugfile])

    subprocess.check_call(['xz', '--keep', minidebugfile])

    subprocess.check_call([objcopy, '--add-section', '.gnu_debugdata={}.xz'.format(minidebugfile), file])

def copydebugsources(debugsrcdir, sources, d):
    # The debug src information written out to sourcefile is further processed
    # and copied to the destination here.

    cpath = oe.cachedpath.CachedPath()

    if debugsrcdir and sources:
        sourcefile = d.expand("${WORKDIR}/debugsources.list")
        bb.utils.remove(sourcefile)

        # filenames are null-separated - this is an artefact of the previous use
        # of rpm's debugedit, which was writing them out that way, and the code elsewhere
        # is still assuming that.
        debuglistoutput = '\0'.join(sources) + '\0'
        with open(sourcefile, 'a') as sf:
           sf.write(debuglistoutput)

        dvar = d.getVar('PKGD')
        strip = d.getVar("STRIP")
        objcopy = d.getVar("OBJCOPY")
        workdir = d.getVar("WORKDIR")
        sdir = d.getVar("S")
        cflags = d.expand("${CFLAGS}")

        prefixmap = {}
        for flag in cflags.split():
            if not flag.startswith("-fdebug-prefix-map"):
                continue
            if "recipe-sysroot" in flag:
                continue
            flag = flag.split("=")
            prefixmap[flag[1]] = flag[2]

        nosuchdir = []
        basepath = dvar
        for p in debugsrcdir.split("/"):
            basepath = basepath + "/" + p
            if not cpath.exists(basepath):
                nosuchdir.append(basepath)
        bb.utils.mkdirhier(basepath)
        cpath.updatecache(basepath)

        for pmap in prefixmap:
            # Ignore files from the recipe sysroots (target and native)
            cmd =  "LC_ALL=C ; sort -z -u '%s' | egrep -v -z '((<internal>|<built-in>)$|/.*recipe-sysroot.*/)' | " % sourcefile
            # We need to ignore files that are not actually ours
            # we do this by only paying attention to items from this package
            cmd += "fgrep -zw '%s' | " % prefixmap[pmap]
            # Remove prefix in the source paths
            cmd += "sed 's#%s/##g' | " % (prefixmap[pmap])
            cmd += "(cd '%s' ; cpio -pd0mlL --no-preserve-owner '%s%s' 2>/dev/null)" % (pmap, dvar, prefixmap[pmap])

            try:
                subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)
            except subprocess.CalledProcessError:
                # Can "fail" if internal headers/transient sources are attempted
                pass
            # cpio seems to have a bug with -lL together and symbolic links are just copied, not dereferenced.
            # Work around this by manually finding and copying any symbolic links that made it through.
            cmd = "find %s%s -type l -print0 -delete | sed s#%s%s/##g | (cd '%s' ; cpio -pd0mL --no-preserve-owner '%s%s')" % \
                    (dvar, prefixmap[pmap], dvar, prefixmap[pmap], pmap, dvar, prefixmap[pmap])
            subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

        # debugsources.list may be polluted from the host if we used externalsrc,
        # cpio uses copy-pass and may have just created a directory structure
        # matching the one from the host, if thats the case move those files to
        # debugsrcdir to avoid host contamination.
        # Empty dir structure will be deleted in the next step.

        # Same check as above for externalsrc
        if workdir not in sdir:
            if os.path.exists(dvar + debugsrcdir + sdir):
                cmd = "mv %s%s%s/* %s%s" % (dvar, debugsrcdir, sdir, dvar,debugsrcdir)
                subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

        # The copy by cpio may have resulted in some empty directories!  Remove these
        cmd = "find %s%s -empty -type d -delete" % (dvar, debugsrcdir)
        subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

        # Also remove debugsrcdir if its empty
        for p in nosuchdir[::-1]:
            if os.path.exists(p) and not os.listdir(p):
                os.rmdir(p)


def process_split_and_strip_files(d):
    cpath = oe.cachedpath.CachedPath()

    dvar = d.getVar('PKGD')
    pn = d.getVar('PN')
    hostos = d.getVar('HOST_OS')

    oldcwd = os.getcwd()
    os.chdir(dvar)

    dv = package_debug_vars(d)

    #
    # First lets figure out all of the files we may have to process ... do this only once!
    #
    elffiles = {}
    symlinks = {}
    staticlibs = []
    inodes = {}
    libdir = os.path.abspath(dvar + os.sep + d.getVar("libdir"))
    baselibdir = os.path.abspath(dvar + os.sep + d.getVar("base_libdir"))
    skipfiles = (d.getVar("INHIBIT_PACKAGE_STRIP_FILES") or "").split()
    if (d.getVar('INHIBIT_PACKAGE_STRIP') != '1' or \
            d.getVar('INHIBIT_PACKAGE_DEBUG_SPLIT') != '1'):
        checkelf = {}
        checkelflinks = {}
        for root, dirs, files in cpath.walk(dvar):
            for f in files:
                file = os.path.join(root, f)

                # Skip debug files
                if dv["append"] and file.endswith(dv["append"]):
                    continue
                if dv["dir"] and dv["dir"] in os.path.dirname(file[len(dvar):]):
                    continue

                if file in skipfiles:
                    continue

                if oe.package.is_static_lib(file):
                    staticlibs.append(file)
                    continue

                try:
                    ltarget = cpath.realpath(file, dvar, False)
                    s = cpath.lstat(ltarget)
                except OSError as e:
                    (err, strerror) = e.args
                    if err != errno.ENOENT:
                        raise
                    # Skip broken symlinks
                    continue
                if not s:
                    continue
                # Check its an executable
                if (s[stat.ST_MODE] & stat.S_IXUSR) or (s[stat.ST_MODE] & stat.S_IXGRP) \
                        or (s[stat.ST_MODE] & stat.S_IXOTH) \
                        or ((file.startswith(libdir) or file.startswith(baselibdir)) \
                        and (".so" in f or ".node" in f)) \
                        or (f.startswith('vmlinux') or ".ko" in f):

                    if cpath.islink(file):
                        checkelflinks[file] = ltarget
                        continue
                    # Use a reference of device ID and inode number to identify files
                    file_reference = "%d_%d" % (s.st_dev, s.st_ino)
                    checkelf[file] = (file, file_reference)

        results = oe.utils.multiprocess_launch(oe.package.is_elf, checkelflinks.values(), d)
        results_map = {}
        for (ltarget, elf_file) in results:
            results_map[ltarget] = elf_file
        for file in checkelflinks:
            ltarget = checkelflinks[file]
            # If it's a symlink, and points to an ELF file, we capture the readlink target
            if results_map[ltarget]:
                target = os.readlink(file)
                #bb.note("Sym: %s (%d)" % (ltarget, results_map[ltarget]))
                symlinks[file] = target

        results = oe.utils.multiprocess_launch(oe.package.is_elf, checkelf.keys(), d)

        # Sort results by file path. This ensures that the files are always
        # processed in the same order, which is important to make sure builds
        # are reproducible when dealing with hardlinks
        results.sort(key=lambda x: x[0])

        for (file, elf_file) in results:
            # It's a file (or hardlink), not a link
            # ...but is it ELF, and is it already stripped?
            if elf_file & 1:
                if elf_file & 2:
                    if 'already-stripped' in (d.getVar('INSANE_SKIP:' + pn) or "").split():
                        bb.note("Skipping file %s from %s for already-stripped QA test" % (file[len(dvar):], pn))
                    else:
                        msg = "File '%s' from %s was already stripped, this will prevent future debugging!" % (file[len(dvar):], pn)
                        oe.qa.handle_error("already-stripped", msg, d)
                    continue

                # At this point we have an unstripped elf file. We need to:
                #  a) Make sure any file we strip is not hardlinked to anything else outside this tree
                #  b) Only strip any hardlinked file once (no races)
                #  c) Track any hardlinks between files so that we can reconstruct matching debug file hardlinks

                # Use a reference of device ID and inode number to identify files
                file_reference = checkelf[file][1]
                if file_reference in inodes:
                    os.unlink(file)
                    os.link(inodes[file_reference][0], file)
                    inodes[file_reference].append(file)
                else:
                    inodes[file_reference] = [file]
                    # break hardlink
                    bb.utils.break_hardlinks(file)
                    elffiles[file] = elf_file
                # Modified the file so clear the cache
                cpath.updatecache(file)

    def strip_pkgd_prefix(f):
        nonlocal dvar

        if f.startswith(dvar):
            return f[len(dvar):]

        return f

    #
    # First lets process debug splitting
    #
    if (d.getVar('INHIBIT_PACKAGE_DEBUG_SPLIT') != '1'):
        results = oe.utils.multiprocess_launch(splitdebuginfo, list(elffiles), d, extraargs=(dvar, dv, d))

        if dv["srcdir"] and not hostos.startswith("mingw"):
            if (d.getVar('PACKAGE_DEBUG_STATIC_SPLIT') == '1'):
                results = oe.utils.multiprocess_launch(splitstaticdebuginfo, staticlibs, d, extraargs=(dvar, dv, d))
            else:
                for file in staticlibs:
                    results.append( (file,source_info(file, d)) )

        d.setVar("PKGDEBUGSOURCES", {strip_pkgd_prefix(f): sorted(s) for f, s in results})

        sources = set()
        for r in results:
            sources.update(r[1])

        # Hardlink our debug symbols to the other hardlink copies
        for ref in inodes:
            if len(inodes[ref]) == 1:
                continue

            target = inodes[ref][0][len(dvar):]
            for file in inodes[ref][1:]:
                src = file[len(dvar):]
                dest = dv["libdir"] + os.path.dirname(src) + dv["dir"] + "/" + os.path.basename(target) + dv["append"]
                fpath = dvar + dest
                ftarget = dvar + dv["libdir"] + os.path.dirname(target) + dv["dir"] + "/" + os.path.basename(target) + dv["append"]
                bb.utils.mkdirhier(os.path.dirname(fpath))
                # Only one hardlink of separated debug info file in each directory
                if not os.access(fpath, os.R_OK):
                    #bb.note("Link %s -> %s" % (fpath, ftarget))
                    os.link(ftarget, fpath)

        # Create symlinks for all cases we were able to split symbols
        for file in symlinks:
            src = file[len(dvar):]
            dest = dv["libdir"] + os.path.dirname(src) + dv["dir"] + "/" + os.path.basename(src) + dv["append"]
            fpath = dvar + dest
            # Skip it if the target doesn't exist
            try:
                s = os.stat(fpath)
            except OSError as e:
                (err, strerror) = e.args
                if err != errno.ENOENT:
                    raise
                continue

            ltarget = symlinks[file]
            lpath = os.path.dirname(ltarget)
            lbase = os.path.basename(ltarget)
            ftarget = ""
            if lpath and lpath != ".":
                ftarget += lpath + dv["dir"] + "/"
            ftarget += lbase + dv["append"]
            if lpath.startswith(".."):
                ftarget = os.path.join("..", ftarget)
            bb.utils.mkdirhier(os.path.dirname(fpath))
            #bb.note("Symlink %s -> %s" % (fpath, ftarget))
            os.symlink(ftarget, fpath)

        # Process the dv["srcdir"] if requested...
        # This copies and places the referenced sources for later debugging...
        copydebugsources(dv["srcdir"], sources, d)
    #
    # End of debug splitting
    #

    #
    # Now lets go back over things and strip them
    #
    if (d.getVar('INHIBIT_PACKAGE_STRIP') != '1'):
        strip = d.getVar("STRIP")
        sfiles = []
        for file in elffiles:
            elf_file = int(elffiles[file])
            #bb.note("Strip %s" % file)
            sfiles.append((file, elf_file, strip))
        if (d.getVar('PACKAGE_STRIP_STATIC') == '1' or d.getVar('PACKAGE_DEBUG_STATIC_SPLIT') == '1'):
            for f in staticlibs:
                sfiles.append((f, 16, strip))

        oe.utils.multiprocess_launch(oe.package.runstrip, sfiles, d)

    # Build "minidebuginfo" and reinject it back into the stripped binaries
    if bb.utils.contains('DISTRO_FEATURES', 'minidebuginfo', True, False, d):
        oe.utils.multiprocess_launch(inject_minidebuginfo, list(elffiles), d,
                                     extraargs=(dvar, dv, d))

    #
    # End of strip
    #
    os.chdir(oldcwd)


def populate_packages(d):
    cpath = oe.cachedpath.CachedPath()

    workdir = d.getVar('WORKDIR')
    outdir = d.getVar('DEPLOY_DIR')
    dvar = d.getVar('PKGD')
    packages = d.getVar('PACKAGES').split()
    pn = d.getVar('PN')

    bb.utils.mkdirhier(outdir)
    os.chdir(dvar)

    autodebug = not (d.getVar("NOAUTOPACKAGEDEBUG") or False)

    split_source_package = (d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-with-srcpkg')

    # If debug-with-srcpkg mode is enabled then add the source package if it
    # doesn't exist and add the source file contents to the source package.
    if split_source_package:
        src_package_name = ('%s-src' % d.getVar('PN'))
        if not src_package_name in packages:
            packages.append(src_package_name)
        d.setVar('FILES:%s' % src_package_name, '/usr/src/debug')

    # Sanity check PACKAGES for duplicates
    # Sanity should be moved to sanity.bbclass once we have the infrastructure
    package_dict = {}

    for i, pkg in enumerate(packages):
        if pkg in package_dict:
            msg = "%s is listed in PACKAGES multiple times, this leads to packaging errors." % pkg
            oe.qa.handle_error("packages-list", msg, d)
        # Ensure the source package gets the chance to pick up the source files
        # before the debug package by ordering it first in PACKAGES. Whether it
        # actually picks up any source files is controlled by
        # PACKAGE_DEBUG_SPLIT_STYLE.
        elif pkg.endswith("-src"):
            package_dict[pkg] = (10, i)
        elif autodebug and pkg.endswith("-dbg"):
            package_dict[pkg] = (30, i)
        else:
            package_dict[pkg] = (50, i)
    packages = sorted(package_dict.keys(), key=package_dict.get)
    d.setVar('PACKAGES', ' '.join(packages))
    pkgdest = d.getVar('PKGDEST')

    seen = []

    # os.mkdir masks the permissions with umask so we have to unset it first
    oldumask = os.umask(0)

    debug = []
    for root, dirs, files in cpath.walk(dvar):
        dir = root[len(dvar):]
        if not dir:
            dir = os.sep
        for f in (files + dirs):
            path = "." + os.path.join(dir, f)
            if "/.debug/" in path or "/.debug-static/" in path or path.endswith("/.debug"):
                debug.append(path)

    for pkg in packages:
        root = os.path.join(pkgdest, pkg)
        bb.utils.mkdirhier(root)

        filesvar = d.getVar('FILES:%s' % pkg) or ""
        if "//" in filesvar:
            msg = "FILES variable for package %s contains '//' which is invalid. Attempting to fix this but you should correct the metadata.\n" % pkg
            oe.qa.handle_error("files-invalid", msg, d)
            filesvar.replace("//", "/")

        origfiles = filesvar.split()
        files, symlink_paths = oe.package.files_from_filevars(origfiles)

        if autodebug and pkg.endswith("-dbg"):
            files.extend(debug)

        for file in files:
            if (not cpath.islink(file)) and (not cpath.exists(file)):
                continue
            if file in seen:
                continue
            seen.append(file)

            def mkdir(src, dest, p):
                src = os.path.join(src, p)
                dest = os.path.join(dest, p)
                fstat = cpath.stat(src)
                os.mkdir(dest)
                os.chmod(dest, fstat.st_mode)
                os.chown(dest, fstat.st_uid, fstat.st_gid)
                if p not in seen:
                    seen.append(p)
                cpath.updatecache(dest)

            def mkdir_recurse(src, dest, paths):
                if cpath.exists(dest + '/' + paths):
                    return
                while paths.startswith("./"):
                    paths = paths[2:]
                p = "."
                for c in paths.split("/"):
                    p = os.path.join(p, c)
                    if not cpath.exists(os.path.join(dest, p)):
                        mkdir(src, dest, p)

            if cpath.isdir(file) and not cpath.islink(file):
                mkdir_recurse(dvar, root, file)
                continue

            mkdir_recurse(dvar, root, os.path.dirname(file))
            fpath = os.path.join(root,file)
            if not cpath.islink(file):
                os.link(file, fpath)
                continue
            ret = bb.utils.copyfile(file, fpath)
            if ret is False or ret == 0:
                bb.fatal("File population failed")

        # Check if symlink paths exist
        for file in symlink_paths:
            if not os.path.exists(os.path.join(root,file)):
                bb.fatal("File '%s' cannot be packaged into '%s' because its "
                         "parent directory structure does not exist. One of "
                         "its parent directories is a symlink whose target "
                         "directory is not included in the package." %
                         (file, pkg))

    os.umask(oldumask)
    os.chdir(workdir)

    # Handle excluding packages with incompatible licenses
    package_list = []
    for pkg in packages:
        licenses = d.getVar('_exclude_incompatible-' + pkg)
        if licenses:
            msg = "Excluding %s from packaging as it has incompatible license(s): %s" % (pkg, licenses)
            oe.qa.handle_error("incompatible-license", msg, d)
        else:
            package_list.append(pkg)
    d.setVar('PACKAGES', ' '.join(package_list))

    unshipped = []
    for root, dirs, files in cpath.walk(dvar):
        dir = root[len(dvar):]
        if not dir:
            dir = os.sep
        for f in (files + dirs):
            path = os.path.join(dir, f)
            if ('.' + path) not in seen:
                unshipped.append(path)

    if unshipped != []:
        msg = pn + ": Files/directories were installed but not shipped in any package:"
        if "installed-vs-shipped" in (d.getVar('INSANE_SKIP:' + pn) or "").split():
            bb.note("Package %s skipping QA tests: installed-vs-shipped" % pn)
        else:
            for f in unshipped:
                msg = msg + "\n  " + f
            msg = msg + "\nPlease set FILES such that these items are packaged. Alternatively if they are unneeded, avoid installing them or delete them within do_install.\n"
            msg = msg + "%s: %d installed and not shipped files." % (pn, len(unshipped))
            oe.qa.handle_error("installed-vs-shipped", msg, d)

def process_fixsymlinks(pkgfiles, d):
    cpath = oe.cachedpath.CachedPath()
    pkgdest = d.getVar('PKGDEST')
    packages = d.getVar("PACKAGES", False).split()

    dangling_links = {}
    pkg_files = {}
    for pkg in packages:
        dangling_links[pkg] = []
        pkg_files[pkg] = []
        inst_root = os.path.join(pkgdest, pkg)
        for path in pkgfiles[pkg]:
                rpath = path[len(inst_root):]
                pkg_files[pkg].append(rpath)
                rtarget = cpath.realpath(path, inst_root, True, assume_dir = True)
                if not cpath.lexists(rtarget):
                    dangling_links[pkg].append(os.path.normpath(rtarget[len(inst_root):]))

    newrdepends = {}
    for pkg in dangling_links:
        for l in dangling_links[pkg]:
            found = False
            bb.debug(1, "%s contains dangling link %s" % (pkg, l))
            for p in packages:
                if l in pkg_files[p]:
                        found = True
                        bb.debug(1, "target found in %s" % p)
                        if p == pkg:
                            break
                        if pkg not in newrdepends:
                            newrdepends[pkg] = []
                        newrdepends[pkg].append(p)
                        break
            if found == False:
                bb.note("%s contains dangling symlink to %s" % (pkg, l))

    for pkg in newrdepends:
        rdepends = bb.utils.explode_dep_versions2(d.getVar('RDEPENDS:' + pkg) or "")
        for p in newrdepends[pkg]:
            if p not in rdepends:
                rdepends[p] = []
        d.setVar('RDEPENDS:' + pkg, bb.utils.join_deps(rdepends, commasep=False))

def process_filedeps(pkgfiles, d):
    """
    Collect perfile run-time dependency metadata
    Output:
     FILERPROVIDESFLIST:pkg - list of all files w/ deps
     FILERPROVIDES:filepath:pkg - per file dep

      FILERDEPENDSFLIST:pkg - list of all files w/ deps
      FILERDEPENDS:filepath:pkg - per file dep
    """
    if d.getVar('SKIP_FILEDEPS') == '1':
        return

    pkgdest = d.getVar('PKGDEST')
    packages = d.getVar('PACKAGES')
    rpmdeps = d.getVar('RPMDEPS')

    def chunks(files, n):
        return [files[i:i+n] for i in range(0, len(files), n)]

    pkglist = []
    for pkg in packages.split():
        if d.getVar('SKIP_FILEDEPS:' + pkg) == '1':
            continue
        if pkg.endswith('-dbg') or pkg.endswith('-doc') or pkg.find('-locale-') != -1 or pkg.find('-localedata-') != -1 or pkg.find('-gconv-') != -1 or pkg.find('-charmap-') != -1 or pkg.startswith('kernel-module-') or pkg.endswith('-src'):
            continue
        for files in chunks(pkgfiles[pkg], 100):
            pkglist.append((pkg, files, rpmdeps, pkgdest))

    processed = oe.utils.multiprocess_launch(oe.package.filedeprunner, pkglist, d)

    provides_files = {}
    requires_files = {}

    for result in processed:
        (pkg, provides, requires) = result

        if pkg not in provides_files:
            provides_files[pkg] = []
        if pkg not in requires_files:
            requires_files[pkg] = []

        for file in sorted(provides):
            provides_files[pkg].append(file)
            key = "FILERPROVIDES:" + file + ":" + pkg
            d.appendVar(key, " " + " ".join(provides[file]))

        for file in sorted(requires):
            requires_files[pkg].append(file)
            key = "FILERDEPENDS:" + file + ":" + pkg
            d.appendVar(key, " " + " ".join(requires[file]))

    for pkg in requires_files:
        d.setVar("FILERDEPENDSFLIST:" + pkg, " ".join(sorted(requires_files[pkg])))
    for pkg in provides_files:
        d.setVar("FILERPROVIDESFLIST:" + pkg, " ".join(sorted(provides_files[pkg])))

def process_shlibs(pkgfiles, d):
    cpath = oe.cachedpath.CachedPath()

    exclude_shlibs = d.getVar('EXCLUDE_FROM_SHLIBS', False)
    if exclude_shlibs:
        bb.note("not generating shlibs")
        return

    lib_re = re.compile(r"^.*\.so")
    libdir_re = re.compile(r".*/%s$" % d.getVar('baselib'))

    packages = d.getVar('PACKAGES')

    shlib_pkgs = []
    exclusion_list = d.getVar("EXCLUDE_PACKAGES_FROM_SHLIBS")
    if exclusion_list:
        for pkg in packages.split():
            if pkg not in exclusion_list.split():
                shlib_pkgs.append(pkg)
            else:
                bb.note("not generating shlibs for %s" % pkg)
    else:
        shlib_pkgs = packages.split()

    hostos = d.getVar('HOST_OS')

    workdir = d.getVar('WORKDIR')

    ver = d.getVar('PKGV')
    if not ver:
        msg = "PKGV not defined"
        oe.qa.handle_error("pkgv-undefined", msg, d)
        return

    pkgdest = d.getVar('PKGDEST')

    shlibswork_dir = d.getVar('SHLIBSWORKDIR')

    def linux_so(file, pkg, pkgver, d):
        needs_ldconfig = False
        needed = set()
        sonames = set()
        renames = []
        ldir = os.path.dirname(file).replace(pkgdest + "/" + pkg, '')
        cmd = d.getVar('OBJDUMP') + " -p " + shlex.quote(file) + " 2>/dev/null"
        fd = os.popen(cmd)
        lines = fd.readlines()
        fd.close()
        rpath = tuple()
        for l in lines:
            m = re.match(r"\s+RPATH\s+([^\s]*)", l)
            if m:
                rpaths = m.group(1).replace("$ORIGIN", ldir).split(":")
                rpath = tuple(map(os.path.normpath, rpaths))
        for l in lines:
            m = re.match(r"\s+NEEDED\s+([^\s]*)", l)
            if m:
                dep = m.group(1)
                if dep not in needed:
                    needed.add((dep, file, rpath))
            m = re.match(r"\s+SONAME\s+([^\s]*)", l)
            if m:
                this_soname = m.group(1)
                prov = (this_soname, ldir, pkgver)
                if not prov in sonames:
                    # if library is private (only used by package) then do not build shlib for it
                    if not private_libs or len([i for i in private_libs if fnmatch.fnmatch(this_soname, i)]) == 0:
                        sonames.add(prov)
                if libdir_re.match(os.path.dirname(file)):
                    needs_ldconfig = True
                if needs_ldconfig and snap_symlinks and (os.path.basename(file) != this_soname):
                    renames.append((file, os.path.join(os.path.dirname(file), this_soname)))
        return (needs_ldconfig, needed, sonames, renames)

    def darwin_so(file, needed, sonames, renames, pkgver):
        if not os.path.exists(file):
            return
        ldir = os.path.dirname(file).replace(pkgdest + "/" + pkg, '')

        def get_combinations(base):
            #
            # Given a base library name, find all combinations of this split by "." and "-"
            #
            combos = []
            options = base.split(".")
            for i in range(1, len(options) + 1):
                combos.append(".".join(options[0:i]))
            options = base.split("-")
            for i in range(1, len(options) + 1):
                combos.append("-".join(options[0:i]))
            return combos

        if (file.endswith('.dylib') or file.endswith('.so')) and not pkg.endswith('-dev') and not pkg.endswith('-dbg') and not pkg.endswith('-src'):
            # Drop suffix
            name = os.path.basename(file).rsplit(".",1)[0]
            # Find all combinations
            combos = get_combinations(name)
            for combo in combos:
                if not combo in sonames:
                    prov = (combo, ldir, pkgver)
                    sonames.add(prov)
        if file.endswith('.dylib') or file.endswith('.so'):
            rpath = []
            p = subprocess.Popen([d.expand("${HOST_PREFIX}otool"), '-l', file], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            out, err = p.communicate()
            # If returned successfully, process stdout for results
            if p.returncode == 0:
                for l in out.split("\n"):
                    l = l.strip()
                    if l.startswith('path '):
                        rpath.append(l.split()[1])

        p = subprocess.Popen([d.expand("${HOST_PREFIX}otool"), '-L', file], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        out, err = p.communicate()
        # If returned successfully, process stdout for results
        if p.returncode == 0:
            for l in out.split("\n"):
                l = l.strip()
                if not l or l.endswith(":"):
                    continue
                if "is not an object file" in l:
                    continue
                name = os.path.basename(l.split()[0]).rsplit(".", 1)[0]
                if name and name not in needed[pkg]:
                     needed[pkg].add((name, file, tuple()))

    def mingw_dll(file, needed, sonames, renames, pkgver):
        if not os.path.exists(file):
            return

        if file.endswith(".dll"):
            # assume all dlls are shared objects provided by the package
            sonames.add((os.path.basename(file), os.path.dirname(file).replace(pkgdest + "/" + pkg, ''), pkgver))

        if (file.endswith(".dll") or file.endswith(".exe")):
            # use objdump to search for "DLL Name: .*\.dll"
            p = subprocess.Popen([d.expand("${OBJDUMP}"), "-p", file], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = p.communicate()
            # process the output, grabbing all .dll names
            if p.returncode == 0:
                for m in re.finditer(r"DLL Name: (.*?\.dll)$", out.decode(), re.MULTILINE | re.IGNORECASE):
                    dllname = m.group(1)
                    if dllname:
                        needed[pkg].add((dllname, file, tuple()))

    if d.getVar('PACKAGE_SNAP_LIB_SYMLINKS') == "1":
        snap_symlinks = True
    else:
        snap_symlinks = False

    needed = {}

    shlib_provider = oe.package.read_shlib_providers(d)

    for pkg in shlib_pkgs:
        private_libs = d.getVar('PRIVATE_LIBS:' + pkg) or d.getVar('PRIVATE_LIBS') or ""
        private_libs = private_libs.split()
        needs_ldconfig = False
        bb.debug(2, "calculating shlib provides for %s" % pkg)

        pkgver = d.getVar('PKGV:' + pkg)
        if not pkgver:
            pkgver = d.getVar('PV_' + pkg)
        if not pkgver:
            pkgver = ver

        needed[pkg] = set()
        sonames = set()
        renames = []
        linuxlist = []
        for file in pkgfiles[pkg]:
                soname = None
                if cpath.islink(file):
                    continue
                if hostos.startswith("darwin"):
                    darwin_so(file, needed, sonames, renames, pkgver)
                elif hostos.startswith("mingw"):
                    mingw_dll(file, needed, sonames, renames, pkgver)
                elif os.access(file, os.X_OK) or lib_re.match(file):
                    linuxlist.append(file)

        if linuxlist:
            results = oe.utils.multiprocess_launch(linux_so, linuxlist, d, extraargs=(pkg, pkgver, d))
            for r in results:
                ldconfig = r[0]
                needed[pkg] |= r[1]
                sonames |= r[2]
                renames.extend(r[3])
                needs_ldconfig = needs_ldconfig or ldconfig

        for (old, new) in renames:
            bb.note("Renaming %s to %s" % (old, new))
            bb.utils.rename(old, new)
            pkgfiles[pkg].remove(old)

        shlibs_file = os.path.join(shlibswork_dir, pkg + ".list")
        if len(sonames):
            with open(shlibs_file, 'w') as fd:
                for s in sorted(sonames):
                    if s[0] in shlib_provider and s[1] in shlib_provider[s[0]]:
                        (old_pkg, old_pkgver) = shlib_provider[s[0]][s[1]]
                        if old_pkg != pkg:
                            bb.warn('%s-%s was registered as shlib provider for %s, changing it to %s-%s because it was built later' % (old_pkg, old_pkgver, s[0], pkg, pkgver))
                    bb.debug(1, 'registering %s-%s as shlib provider for %s' % (pkg, pkgver, s[0]))
                    fd.write(s[0] + ':' + s[1] + ':' + s[2] + '\n')
                    if s[0] not in shlib_provider:
                        shlib_provider[s[0]] = {}
                    shlib_provider[s[0]][s[1]] = (pkg, pkgver)
        if needs_ldconfig:
            bb.debug(1, 'adding ldconfig call to postinst for %s' % pkg)
            postinst = d.getVar('pkg_postinst:%s' % pkg)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += d.getVar('ldconfig_postinst_fragment')
            d.setVar('pkg_postinst:%s' % pkg, postinst)
        bb.debug(1, 'LIBNAMES: pkg %s sonames %s' % (pkg, sonames))

    assumed_libs = d.getVar('ASSUME_SHLIBS')
    if assumed_libs:
        libdir = d.getVar("libdir")
        for e in assumed_libs.split():
            l, dep_pkg = e.split(":")
            lib_ver = None
            dep_pkg = dep_pkg.rsplit("_", 1)
            if len(dep_pkg) == 2:
                lib_ver = dep_pkg[1]
            dep_pkg = dep_pkg[0]
            if l not in shlib_provider:
                shlib_provider[l] = {}
            shlib_provider[l][libdir] = (dep_pkg, lib_ver)

    libsearchpath = [d.getVar('libdir'), d.getVar('base_libdir')]

    for pkg in shlib_pkgs:
        bb.debug(2, "calculating shlib requirements for %s" % pkg)

        private_libs = d.getVar('PRIVATE_LIBS:' + pkg) or d.getVar('PRIVATE_LIBS') or ""
        private_libs = private_libs.split()

        deps = list()
        for n in needed[pkg]:
            # if n is in private libraries, don't try to search provider for it
            # this could cause problem in case some abc.bb provides private
            # /opt/abc/lib/libfoo.so.1 and contains /usr/bin/abc depending on system library libfoo.so.1
            # but skipping it is still better alternative than providing own
            # version and then adding runtime dependency for the same system library
            if private_libs and len([i for i in private_libs if fnmatch.fnmatch(n[0], i)]) > 0:
                bb.debug(2, '%s: Dependency %s covered by PRIVATE_LIBS' % (pkg, n[0]))
                continue
            if n[0] in shlib_provider.keys():
                shlib_provider_map = shlib_provider[n[0]]
                matches = set()
                for p in itertools.chain(list(n[2]), sorted(shlib_provider_map.keys()), libsearchpath):
                    if p in shlib_provider_map:
                        matches.add(p)
                if len(matches) > 1:
                    matchpkgs = ', '.join([shlib_provider_map[match][0] for match in matches])
                    bb.error("%s: Multiple shlib providers for %s: %s (used by files: %s)" % (pkg, n[0], matchpkgs, n[1]))
                elif len(matches) == 1:
                    (dep_pkg, ver_needed) = shlib_provider_map[matches.pop()]

                    bb.debug(2, '%s: Dependency %s requires package %s (used by files: %s)' % (pkg, n[0], dep_pkg, n[1]))

                    if dep_pkg == pkg:
                        continue

                    if ver_needed:
                        dep = "%s (>= %s)" % (dep_pkg, ver_needed)
                    else:
                        dep = dep_pkg
                    if not dep in deps:
                        deps.append(dep)
                    continue
            bb.note("Couldn't find shared library provider for %s, used by files: %s" % (n[0], n[1]))

        deps_file = os.path.join(pkgdest, pkg + ".shlibdeps")
        if os.path.exists(deps_file):
            os.remove(deps_file)
        if deps:
            with open(deps_file, 'w') as fd:
                for dep in sorted(deps):
                    fd.write(dep + '\n')

def process_pkgconfig(pkgfiles, d):
    packages = d.getVar('PACKAGES')
    workdir = d.getVar('WORKDIR')
    pkgdest = d.getVar('PKGDEST')

    shlibs_dirs = d.getVar('SHLIBSDIRS').split()
    shlibswork_dir = d.getVar('SHLIBSWORKDIR')

    pc_re = re.compile(r'(.*)\.pc$')
    var_re = re.compile(r'(.*)=(.*)')
    field_re = re.compile(r'(.*): (.*)')

    pkgconfig_provided = {}
    pkgconfig_needed = {}
    for pkg in packages.split():
        pkgconfig_provided[pkg] = []
        pkgconfig_needed[pkg] = []
        for file in sorted(pkgfiles[pkg]):
                m = pc_re.match(file)
                if m:
                    pd = bb.data.init()
                    name = m.group(1)
                    pkgconfig_provided[pkg].append(os.path.basename(name))
                    if not os.access(file, os.R_OK):
                        continue
                    with open(file, 'r') as f:
                        lines = f.readlines()
                    for l in lines:
                        m = field_re.match(l)
                        if m:
                            hdr = m.group(1)
                            exp = pd.expand(m.group(2))
                            if hdr == 'Requires':
                                pkgconfig_needed[pkg] += exp.replace(',', ' ').split()
                                continue
                        m = var_re.match(l)
                        if m:
                            name = m.group(1)
                            val = m.group(2)
                            pd.setVar(name, pd.expand(val))

    for pkg in packages.split():
        pkgs_file = os.path.join(shlibswork_dir, pkg + ".pclist")
        if pkgconfig_provided[pkg] != []:
            with open(pkgs_file, 'w') as f:
                for p in sorted(pkgconfig_provided[pkg]):
                    f.write('%s\n' % p)

    # Go from least to most specific since the last one found wins
    for dir in reversed(shlibs_dirs):
        if not os.path.exists(dir):
            continue
        for file in sorted(os.listdir(dir)):
            m = re.match(r'^(.*)\.pclist$', file)
            if m:
                pkg = m.group(1)
                with open(os.path.join(dir, file)) as fd:
                    lines = fd.readlines()
                pkgconfig_provided[pkg] = []
                for l in lines:
                    pkgconfig_provided[pkg].append(l.rstrip())

    for pkg in packages.split():
        deps = []
        for n in pkgconfig_needed[pkg]:
            found = False
            for k in pkgconfig_provided.keys():
                if n in pkgconfig_provided[k]:
                    if k != pkg and not (k in deps):
                        deps.append(k)
                    found = True
            if found == False:
                bb.note("couldn't find pkgconfig module '%s' in any package" % n)
        deps_file = os.path.join(pkgdest, pkg + ".pcdeps")
        if len(deps):
            with open(deps_file, 'w') as fd:
                for dep in deps:
                    fd.write(dep + '\n')

def read_libdep_files(d):
    pkglibdeps = {}
    packages = d.getVar('PACKAGES').split()
    for pkg in packages:
        pkglibdeps[pkg] = {}
        for extension in ".shlibdeps", ".pcdeps", ".clilibdeps":
            depsfile = d.expand("${PKGDEST}/" + pkg + extension)
            if os.access(depsfile, os.R_OK):
                with open(depsfile) as fd:
                    lines = fd.readlines()
                for l in lines:
                    l.rstrip()
                    deps = bb.utils.explode_dep_versions2(l)
                    for dep in deps:
                        if not dep in pkglibdeps[pkg]:
                            pkglibdeps[pkg][dep] = deps[dep]
    return pkglibdeps

def process_depchains(pkgfiles, d):
    """
    For a given set of prefix and postfix modifiers, make those packages
    RRECOMMENDS on the corresponding packages for its RDEPENDS.

    Example:  If package A depends upon package B, and A's .bb emits an
    A-dev package, this would make A-dev Recommends: B-dev.

    If only one of a given suffix is specified, it will take the RRECOMMENDS
    based on the RDEPENDS of *all* other packages. If more than one of a given
    suffix is specified, its will only use the RDEPENDS of the single parent
    package.
    """

    packages  = d.getVar('PACKAGES')
    postfixes = (d.getVar('DEPCHAIN_POST') or '').split()
    prefixes  = (d.getVar('DEPCHAIN_PRE') or '').split()

    def pkg_adddeprrecs(pkg, base, suffix, getname, depends, d):

        #bb.note('depends for %s is %s' % (base, depends))
        rreclist = bb.utils.explode_dep_versions2(d.getVar('RRECOMMENDS:' + pkg) or "")

        for depend in sorted(depends):
            if depend.find('-native') != -1 or depend.find('-cross') != -1 or depend.startswith('virtual/'):
                #bb.note("Skipping %s" % depend)
                continue
            if depend.endswith('-dev'):
                depend = depend[:-4]
            if depend.endswith('-dbg'):
                depend = depend[:-4]
            pkgname = getname(depend, suffix)
            #bb.note("Adding %s for %s" % (pkgname, depend))
            if pkgname not in rreclist and pkgname != pkg:
                rreclist[pkgname] = []

        #bb.note('setting: RRECOMMENDS:%s=%s' % (pkg, ' '.join(rreclist)))
        d.setVar('RRECOMMENDS:%s' % pkg, bb.utils.join_deps(rreclist, commasep=False))

    def pkg_addrrecs(pkg, base, suffix, getname, rdepends, d):

        #bb.note('rdepends for %s is %s' % (base, rdepends))
        rreclist = bb.utils.explode_dep_versions2(d.getVar('RRECOMMENDS:' + pkg) or "")

        for depend in sorted(rdepends):
            if depend.find('virtual-locale-') != -1:
                #bb.note("Skipping %s" % depend)
                continue
            if depend.endswith('-dev'):
                depend = depend[:-4]
            if depend.endswith('-dbg'):
                depend = depend[:-4]
            pkgname = getname(depend, suffix)
            #bb.note("Adding %s for %s" % (pkgname, depend))
            if pkgname not in rreclist and pkgname != pkg:
                rreclist[pkgname] = []

        #bb.note('setting: RRECOMMENDS:%s=%s' % (pkg, ' '.join(rreclist)))
        d.setVar('RRECOMMENDS:%s' % pkg, bb.utils.join_deps(rreclist, commasep=False))

    def add_dep(list, dep):
        if dep not in list:
            list.append(dep)

    depends = []
    for dep in bb.utils.explode_deps(d.getVar('DEPENDS') or ""):
        add_dep(depends, dep)

    rdepends = []
    for pkg in packages.split():
        for dep in bb.utils.explode_deps(d.getVar('RDEPENDS:' + pkg) or ""):
            add_dep(rdepends, dep)

    #bb.note('rdepends is %s' % rdepends)

    def post_getname(name, suffix):
        return '%s%s' % (name, suffix)
    def pre_getname(name, suffix):
        return '%s%s' % (suffix, name)

    pkgs = {}
    for pkg in packages.split():
        for postfix in postfixes:
            if pkg.endswith(postfix):
                if not postfix in pkgs:
                    pkgs[postfix] = {}
                pkgs[postfix][pkg] = (pkg[:-len(postfix)], post_getname)

        for prefix in prefixes:
            if pkg.startswith(prefix):
                if not prefix in pkgs:
                    pkgs[prefix] = {}
                pkgs[prefix][pkg] = (pkg[:-len(prefix)], pre_getname)

    if "-dbg" in pkgs:
        pkglibdeps = read_libdep_files(d)
        pkglibdeplist = []
        for pkg in pkglibdeps:
            for k in pkglibdeps[pkg]:
                add_dep(pkglibdeplist, k)
        dbgdefaultdeps = ((d.getVar('DEPCHAIN_DBGDEFAULTDEPS') == '1') or (bb.data.inherits_class('packagegroup', d)))

    for suffix in pkgs:
        for pkg in pkgs[suffix]:
            if d.getVarFlag('RRECOMMENDS:' + pkg, 'nodeprrecs'):
                continue
            (base, func) = pkgs[suffix][pkg]
            if suffix == "-dev":
                pkg_adddeprrecs(pkg, base, suffix, func, depends, d)
            elif suffix == "-dbg":
                if not dbgdefaultdeps:
                    pkg_addrrecs(pkg, base, suffix, func, pkglibdeplist, d)
                    continue
            if len(pkgs[suffix]) == 1:
                pkg_addrrecs(pkg, base, suffix, func, rdepends, d)
            else:
                rdeps = []
                for dep in bb.utils.explode_deps(d.getVar('RDEPENDS:' + base) or ""):
                    add_dep(rdeps, dep)
                pkg_addrrecs(pkg, base, suffix, func, rdeps, d)
