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
        for line in pipe:
            line = line.decode("utf-8")

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

    try:
        dep_popen = subprocess.Popen(shlex.split(rpmdeps) + pkgfiles, stdout=subprocess.PIPE)
        provides, requires = process_deps(dep_popen.stdout, pkg, pkgdest, provides, requires)
    except OSError as e:
        bb.error("rpmdeps: '%s' command failed, '%s'" % (shlex.split(rpmdeps) + pkgfiles, e))
        raise e

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
