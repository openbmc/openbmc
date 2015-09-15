def runstrip(arg):
    # Function to strip a single file, called from split_and_strip_files below
    # A working 'file' (one which works on the target architecture)
    #
    # The elftype is a bit pattern (explained in split_and_strip_files) to tell
    # us what type of file we're processing...
    # 4 - executable
    # 8 - shared library
    # 16 - kernel module

    import commands, stat, subprocess

    (file, elftype, strip) = arg

    newmode = None
    if not os.access(file, os.W_OK) or os.access(file, os.R_OK):
        origmode = os.stat(file)[stat.ST_MODE]
        newmode = origmode | stat.S_IWRITE | stat.S_IREAD
        os.chmod(file, newmode)

    extraflags = ""

    # kernel module    
    if elftype & 16:
        extraflags = "--strip-debug --remove-section=.comment --remove-section=.note --preserve-dates"
    # .so and shared library
    elif ".so" in file and elftype & 8:
        extraflags = "--remove-section=.comment --remove-section=.note --strip-unneeded"
    # shared or executable:
    elif elftype & 8 or elftype & 4:
        extraflags = "--remove-section=.comment --remove-section=.note"

    stripcmd = "'%s' %s '%s'" % (strip, extraflags, file)
    bb.debug(1, "runstrip: %s" % stripcmd)

    try:
        output = subprocess.check_output(stripcmd, stderr=subprocess.STDOUT, shell=True)
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

    r = re.compile(r'[<>=]+ +[^ ]*')

    def process_deps(pipe, pkg, pkgdest, provides, requires):
        for line in pipe:
            f = line.split(" ", 1)[0].strip()
            line = line.split(" ", 1)[1].strip()

            if line.startswith("Requires:"):
                i = requires
            elif line.startswith("Provides:"):
                i = provides
            else:
                continue

            file = f.replace(pkgdest + "/" + pkg, "")
            file = file_translate(file)
            value = line.split(":", 1)[1].strip()
            value = r.sub(r'(\g<0>)', value)

            if value.startswith("rpmlib("):
                continue
            if value == "python":
                continue
            if file not in i:
                i[file] = []
            i[file].append(value)

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
    shlibs_dirs = d.getVar('SHLIBSDIRS', True).split()
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
                fd = open(os.path.join(dir, file))
                lines = fd.readlines()
                fd.close()
                for l in lines:
                    s = l.strip().split(":")
                    if s[0] not in shlib_provider:
                        shlib_provider[s[0]] = {}
                    shlib_provider[s[0]][s[1]] = (dep_pkg, s[2])
    return shlib_provider
