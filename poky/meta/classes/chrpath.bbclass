#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

CHRPATH_BIN ?= "chrpath"
PREPROCESS_RELOCATE_DIRS ?= ""

def process_file_linux(cmd, fpath, rootdir, baseprefix, tmpdir, d, break_hardlinks = False):
    import subprocess, oe.qa

    with oe.qa.ELFFile(fpath) as elf:
        try:
            elf.open()
        except oe.qa.NotELFFileError:
            return

    try:
        out = subprocess.check_output([cmd, "-l", fpath], universal_newlines=True)
    except subprocess.CalledProcessError:
        return

    # Handle RUNPATH as well as RPATH
    out = out.replace("RUNPATH=","RPATH=")
    # Throw away everything other than the rpath list
    curr_rpath = out.partition("RPATH=")[2]
    #bb.note("Current rpath for %s is %s" % (fpath, curr_rpath.strip()))
    rpaths = curr_rpath.strip().split(":")
    new_rpaths = []
    modified = False
    for rpath in rpaths:
        # If rpath is already dynamic copy it to new_rpath and continue
        if rpath.find("$ORIGIN") != -1:
            new_rpaths.append(rpath)
            continue
        rpath =  os.path.normpath(rpath)
        if baseprefix not in rpath and tmpdir not in rpath:
            # Skip standard search paths
            if rpath in ['/lib', '/usr/lib', '/lib64/', '/usr/lib64']:
                bb.warn("Skipping RPATH %s as is a standard search path for %s" % (rpath, fpath))
                modified = True
                continue
            new_rpaths.append(rpath)
            continue
        new_rpaths.append("$ORIGIN/" + os.path.relpath(rpath, os.path.dirname(fpath.replace(rootdir, "/"))))
        modified = True

    # if we have modified some rpaths call chrpath to update the binary
    if modified:
        if break_hardlinks:
            bb.utils.break_hardlinks(fpath)

        args = ":".join(new_rpaths)
        #bb.note("Setting rpath for %s to %s" %(fpath, args))
        try:
            subprocess.check_output([cmd, "-r", args, fpath],
            stderr=subprocess.PIPE, universal_newlines=True)
        except subprocess.CalledProcessError as e:
            bb.fatal("chrpath command failed with exit code %d:\n%s\n%s" % (e.returncode, e.stdout, e.stderr))

def process_file_darwin(cmd, fpath, rootdir, baseprefix, tmpdir, d, break_hardlinks = False):
    import subprocess as sub

    p = sub.Popen([d.expand("${HOST_PREFIX}otool"), '-L', fpath],stdout=sub.PIPE,stderr=sub.PIPE, text=True)
    out, err = p.communicate()
    # If returned successfully, process stdout for results
    if p.returncode != 0:
        return
    for l in out.split("\n"):
        if "(compatibility" not in l:
            continue
        rpath = l.partition("(compatibility")[0].strip()
        if baseprefix not in rpath:
            continue

        if break_hardlinks:
            bb.utils.break_hardlinks(fpath)

        newpath = "@loader_path/" + os.path.relpath(rpath, os.path.dirname(fpath.replace(rootdir, "/")))
        p = sub.Popen([d.expand("${HOST_PREFIX}install_name_tool"), '-change', rpath, newpath, fpath],stdout=sub.PIPE,stderr=sub.PIPE)
        out, err = p.communicate()

def process_dir(rootdir, directory, d, break_hardlinks = False):
    bb.debug(2, "Checking %s for binaries to process" % directory)
    if not os.path.exists(directory):
        return

    import stat

    rootdir = os.path.normpath(rootdir)
    cmd = d.expand('${CHRPATH_BIN}')
    tmpdir = os.path.normpath(d.getVar('TMPDIR', False))
    baseprefix = os.path.normpath(d.expand('${base_prefix}'))
    hostos = d.getVar("HOST_OS")

    if "linux" in hostos:
        process_file = process_file_linux
    elif "darwin" in hostos:
        process_file = process_file_darwin
    else:
        # Relocations not supported
        return

    dirs = os.listdir(directory)
    for file in dirs:
        fpath = directory + "/" + file
        fpath = os.path.normpath(fpath)
        if os.path.islink(fpath):
            # Skip symlinks
            continue

        if os.path.isdir(fpath):
            process_dir(rootdir, fpath, d, break_hardlinks = break_hardlinks)
        else:
            #bb.note("Testing %s for relocatability" % fpath)

            # We need read and write permissions for chrpath, if we don't have
            # them then set them temporarily. Take a copy of the files
            # permissions so that we can restore them afterwards.
            perms = os.stat(fpath)[stat.ST_MODE]
            if os.access(fpath, os.W_OK|os.R_OK):
                perms = None
            else:
                # Temporarily make the file writeable so we can chrpath it
                os.chmod(fpath, perms|stat.S_IRWXU)

            process_file(cmd, fpath, rootdir, baseprefix, tmpdir, d, break_hardlinks = break_hardlinks)

            if perms:
                os.chmod(fpath, perms)

def rpath_replace (path, d):
    bindirs = d.expand("${bindir} ${sbindir} ${base_sbindir} ${base_bindir} ${libdir} ${base_libdir} ${libexecdir} ${PREPROCESS_RELOCATE_DIRS}").split()

    for bindir in bindirs:
        #bb.note ("Processing directory " + bindir)
        directory = path + "/" + bindir
        process_dir (path, directory, d)

