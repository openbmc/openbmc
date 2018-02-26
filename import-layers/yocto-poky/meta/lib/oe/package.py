def runstrip(arg):
    # Function to strip a single file, called from split_and_strip_files below
    # A working 'file' (one which works on the target architecture)
    #
    # The elftype is a bit pattern (explained in split_and_strip_files) to tell
    # us what type of file we're processing...
    # 4 - executable
    # 8 - shared library
    # 16 - kernel module

    import stat, subprocess

    (file, elftype, strip) = arg

    newmode = None
    if not os.access(file, os.W_OK) or os.access(file, os.R_OK):
        origmode = os.stat(file)[stat.ST_MODE]
        newmode = origmode | stat.S_IWRITE | stat.S_IREAD
        os.chmod(file, newmode)

    stripcmd = [strip]

    # kernel module    
    if elftype & 16:
        stripcmd.extend(["--strip-debug", "--remove-section=.comment",
            "--remove-section=.note", "--preserve-dates"])
    # .so and shared library
    elif ".so" in file and elftype & 8:
        stripcmd.extend(["--remove-section=.comment", "--remove-section=.note", "--strip-unneeded"])
    # shared or executable:
    elif elftype & 8 or elftype & 4:
        stripcmd.extend(["--remove-section=.comment", "--remove-section=.note"])

    stripcmd.append(file)
    bb.debug(1, "runstrip: %s" % stripcmd)

    try:
        output = subprocess.check_output(stripcmd, stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError as e:
        bb.error("runstrip: '%s' strip command failed with %s (%s)" % (stripcmd, e.returncode, e.output))

    if newmode:
        os.chmod(file, origmode)

    return


def strip_execs(pn, dstdir, strip_cmd, libdir, base_libdir, qa_already_stripped=False):
    """
    Strip executable code (like executables, shared libraries) _in_place_
    - Based on sysroot_strip in staging.bbclass
    :param dstdir: directory in which to strip files
    :param strip_cmd: Strip command (usually ${STRIP})
    :param libdir: ${libdir} - strip .so files in this directory
    :param base_libdir: ${base_libdir} - strip .so files in this directory
    :param qa_already_stripped: Set to True if already-stripped' in ${INSANE_SKIP}
    This is for proper logging and messages only.
    """
    import stat, errno, oe.path, oe.utils, mmap

    # Detect .ko module by searching for "vermagic=" string
    def is_kernel_module(path):
        with open(path) as f:
            return mmap.mmap(f.fileno(), 0, prot=mmap.PROT_READ).find(b"vermagic=") >= 0

    # Return type (bits):
    # 0 - not elf
    # 1 - ELF
    # 2 - stripped
    # 4 - executable
    # 8 - shared library
    # 16 - kernel module
    def is_elf(path):
        exec_type = 0
        ret, result = oe.utils.getstatusoutput(
            "file \"%s\"" % path.replace("\"", "\\\""))

        if ret:
            bb.error("split_and_strip_files: 'file %s' failed" % path)
            return exec_type

        if "ELF" in result:
            exec_type |= 1
            if "not stripped" not in result:
                exec_type |= 2
            if "executable" in result:
                exec_type |= 4
            if "shared" in result:
                exec_type |= 8
            if "relocatable" in result and is_kernel_module(path):
                exec_type |= 16
        return exec_type

    elffiles = {}
    inodes = {}
    libdir = os.path.abspath(dstdir + os.sep + libdir)
    base_libdir = os.path.abspath(dstdir + os.sep + base_libdir)
    exec_mask = stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH
    #
    # First lets figure out all of the files we may have to process
    #
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
                elf_file = is_elf(file)
                if elf_file & 1:
                    if elf_file & 2:
                        if qa_already_stripped:
                            bb.note("Skipping file %s from %s for already-stripped QA test" % (file[len(dstdir):], pn))
                        else:
                            bb.warn("File '%s' from %s was already stripped, this will prevent future debugging!" % (file[len(dstdir):], pn))
                        continue

                    if s.st_ino in inodes:
                        os.unlink(file)
                        os.link(inodes[s.st_ino], file)
                    else:
                        # break hardlinks so that we do not strip the original.
                        inodes[s.st_ino] = file
                        bb.utils.copyfile(file, file)
                        elffiles[file] = elf_file

    #
    # Now strip them (in parallel)
    #
    sfiles = []
    for file in elffiles:
        elf_file = int(elffiles[file])
        sfiles.append((file, elf_file, strip_cmd))

    oe.utils.multiprocess_exec(sfiles, runstrip)



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
    list_re = re.compile('^(.*)\.list$')
    # Go from least to most specific since the last one found wins
    for dir in reversed(shlibs_dirs):
        bb.debug(2, "Reading shlib providers in %s" % (dir))
        if not os.path.exists(dir):
            continue
        for file in os.listdir(dir):
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


def npm_split_package_dirs(pkgdir):
    """
    Work out the packages fetched and unpacked by BitBake's npm fetcher
    Returns a dict of packagename -> (relpath, package.json) ordered
    such that it is suitable for use in PACKAGES and FILES
    """
    from collections import OrderedDict
    import json
    packages = {}
    for root, dirs, files in os.walk(pkgdir):
        if os.path.basename(root) == 'node_modules':
            for dn in dirs:
                relpth = os.path.relpath(os.path.join(root, dn), pkgdir)
                pkgitems = ['${PN}']
                for pathitem in relpth.split('/'):
                    if pathitem == 'node_modules':
                        continue
                    pkgitems.append(pathitem)
                pkgname = '-'.join(pkgitems).replace('_', '-')
                pkgname = pkgname.replace('@', '')
                pkgfile = os.path.join(root, dn, 'package.json')
                data = None
                if os.path.exists(pkgfile):
                    with open(pkgfile, 'r') as f:
                        data = json.loads(f.read())
                    packages[pkgname] = (relpth, data)
    # We want the main package for a module sorted *after* its subpackages
    # (so that it doesn't otherwise steal the files for the subpackage), so
    # this is a cheap way to do that whilst still having an otherwise
    # alphabetical sort
    return OrderedDict((key, packages[key]) for key in sorted(packages, key=lambda pkg: pkg + '~'))
