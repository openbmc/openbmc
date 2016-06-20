CHRPATH_BIN ?= "chrpath"
PREPROCESS_RELOCATE_DIRS ?= ""

def process_file_linux(cmd, fpath, rootdir, baseprefix, tmpdir, d):
    import subprocess as sub

    p = sub.Popen([cmd, '-l', fpath],stdout=sub.PIPE,stderr=sub.PIPE)
    err, out = p.communicate()
    # If returned successfully, process stderr for results
    if p.returncode != 0:
        return

    # Handle RUNPATH as well as RPATH
    err = err.replace("RUNPATH=","RPATH=")
    # Throw away everything other than the rpath list
    curr_rpath = err.partition("RPATH=")[2]
    #bb.note("Current rpath for %s is %s" % (fpath, curr_rpath.strip()))
    rpaths = curr_rpath.split(":")
    new_rpaths = []
    modified = False
    for rpath in rpaths:
        # If rpath is already dynamic copy it to new_rpath and continue
        if rpath.find("$ORIGIN") != -1:
            new_rpaths.append(rpath.strip())
            continue
        rpath =  os.path.normpath(rpath)
        if baseprefix not in rpath and tmpdir not in rpath:
            new_rpaths.append(rpath.strip())
            continue
        new_rpaths.append("$ORIGIN/" + os.path.relpath(rpath.strip(), os.path.dirname(fpath.replace(rootdir, "/"))))
        modified = True

    # if we have modified some rpaths call chrpath to update the binary
    if modified:
        args = ":".join(new_rpaths)
        #bb.note("Setting rpath for %s to %s" %(fpath, args))
        p = sub.Popen([cmd, '-r', args, fpath],stdout=sub.PIPE,stderr=sub.PIPE)
        out, err = p.communicate()
        if p.returncode != 0:
            bb.error("%s: chrpath command failed with exit code %d:\n%s%s" % (d.getVar('PN', True), p.returncode, out, err))
            raise bb.build.FuncFailed

def process_file_darwin(cmd, fpath, rootdir, baseprefix, tmpdir, d):
    import subprocess as sub

    p = sub.Popen([d.expand("${HOST_PREFIX}otool"), '-L', fpath],stdout=sub.PIPE,stderr=sub.PIPE)
    err, out = p.communicate()
    # If returned successfully, process stderr for results
    if p.returncode != 0:
        return
    for l in err.split("\n"):
        if "(compatibility" not in l:
            continue
        rpath = l.partition("(compatibility")[0].strip()
        if baseprefix not in rpath:
            continue

        newpath = "@loader_path/" + os.path.relpath(rpath, os.path.dirname(fpath.replace(rootdir, "/")))
        p = sub.Popen([d.expand("${HOST_PREFIX}install_name_tool"), '-change', rpath, newpath, fpath],stdout=sub.PIPE,stderr=sub.PIPE)
        err, out = p.communicate()

def process_dir (rootdir, directory, d):
    import stat

    rootdir = os.path.normpath(rootdir)
    cmd = d.expand('${CHRPATH_BIN}')
    tmpdir = os.path.normpath(d.getVar('TMPDIR', False))
    baseprefix = os.path.normpath(d.expand('${base_prefix}'))
    hostos = d.getVar("HOST_OS", True)

    #bb.debug("Checking %s for binaries to process" % directory)
    if not os.path.exists(directory):
        return

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
            process_dir(rootdir, fpath, d)
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
            process_file(cmd, fpath, rootdir, baseprefix, tmpdir, d)
                
            if perms:
                os.chmod(fpath, perms)

def rpath_replace (path, d):
    bindirs = d.expand("${bindir} ${sbindir} ${base_sbindir} ${base_bindir} ${libdir} ${base_libdir} ${libexecdir} ${PREPROCESS_RELOCATE_DIRS}").split()

    for bindir in bindirs:
        #bb.note ("Processing directory " + bindir)
        directory = path + "/" + bindir
        process_dir (path, directory, d)

