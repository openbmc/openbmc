# Compress man pages in ${mandir} and info pages in ${infodir}
#
# 1. The doc will be compressed to gz format by default.
#
# 2. It will automatically correct the compressed doc which is not
# in ${DOC_COMPRESS} but in ${DOC_COMPRESS_LIST} to the format
# of ${DOC_COMPRESS} policy
#
# 3. It is easy to add a new type compression by editing
# local.conf, such as:
# DOC_COMPRESS_LIST:append = ' abc'
# DOC_COMPRESS = 'abc'
# DOC_COMPRESS_CMD[abc] = 'abc compress cmd ***'
# DOC_DECOMPRESS_CMD[abc] = 'abc decompress cmd ***'

# All supported compression policy
DOC_COMPRESS_LIST ?= "gz xz bz2"

# Compression policy, must be one of ${DOC_COMPRESS_LIST}
DOC_COMPRESS ?= "gz"

# Compression shell command
DOC_COMPRESS_CMD[gz] ?= 'gzip -v -9 -n'
DOC_COMPRESS_CMD[bz2] ?= "bzip2 -v -9"
DOC_COMPRESS_CMD[xz] ?= "xz -v"

# Decompression shell command
DOC_DECOMPRESS_CMD[gz] ?= 'gunzip -v'
DOC_DECOMPRESS_CMD[bz2] ?= "bunzip2 -v"
DOC_DECOMPRESS_CMD[xz] ?= "unxz -v"

PACKAGE_PREPROCESS_FUNCS += "package_do_compress_doc compress_doc_updatealternatives"
python package_do_compress_doc() {
    compress_mode = d.getVar('DOC_COMPRESS')
    compress_list = (d.getVar('DOC_COMPRESS_LIST') or '').split()
    if compress_mode not in compress_list:
        bb.fatal('Compression policy %s not supported (not listed in %s)\n' % (compress_mode, compress_list))

    dvar = d.getVar('PKGD')
    compress_cmds = {}
    decompress_cmds = {}
    for mode in compress_list:
        compress_cmds[mode] = d.getVarFlag('DOC_COMPRESS_CMD', mode)
        decompress_cmds[mode] = d.getVarFlag('DOC_DECOMPRESS_CMD', mode)

    mandir = os.path.abspath(dvar + os.sep + d.getVar("mandir"))
    if os.path.exists(mandir):
        # Decompress doc files which format is not compress_mode
        decompress_doc(mandir, compress_mode, decompress_cmds)
        compress_doc(mandir, compress_mode, compress_cmds)

    infodir = os.path.abspath(dvar + os.sep + d.getVar("infodir"))
    if os.path.exists(infodir):
        # Decompress doc files which format is not compress_mode
        decompress_doc(infodir, compress_mode, decompress_cmds)
        compress_doc(infodir, compress_mode, compress_cmds)
}

def _get_compress_format(file, compress_format_list):
    for compress_format in compress_format_list:
        compress_suffix = '.' + compress_format
        if file.endswith(compress_suffix):
            return compress_format

    return ''

# Collect hardlinks to dict, each element in dict lists hardlinks
# which points to the same doc file.
# {hardlink10: [hardlink11, hardlink12],,,}
# The hardlink10, hardlink11 and hardlink12 are the same file.
def _collect_hardlink(hardlink_dict, file):
    for hardlink in hardlink_dict:
        # Add to the existed hardlink
        if os.path.samefile(hardlink, file):
            hardlink_dict[hardlink].append(file)
            return hardlink_dict

    hardlink_dict[file] = []
    return hardlink_dict

def _process_hardlink(hardlink_dict, compress_mode, shell_cmds, decompress=False):
    import subprocess
    for target in hardlink_dict:
        if decompress:
            compress_format = _get_compress_format(target, shell_cmds.keys())
            cmd = "%s -f %s" % (shell_cmds[compress_format], target)
            bb.note('decompress hardlink %s' % target)
        else:
            cmd = "%s -f %s" % (shell_cmds[compress_mode], target)
            bb.note('compress hardlink %s' % target)
        (retval, output) = subprocess.getstatusoutput(cmd)
        if retval:
            bb.warn("de/compress file failed %s (cmd was %s)%s" % (retval, cmd, ":\n%s" % output if output else ""))
            return

        for hardlink_dup in hardlink_dict[target]:
            if decompress:
                # Remove compress suffix
                compress_suffix = '.' + compress_format
                new_hardlink = hardlink_dup[:-len(compress_suffix)]
                new_target = target[:-len(compress_suffix)]
            else:
                # Append compress suffix
                compress_suffix = '.' + compress_mode
                new_hardlink = hardlink_dup + compress_suffix
                new_target = target + compress_suffix

            bb.note('hardlink %s-->%s' % (new_hardlink, new_target))
            if not os.path.exists(new_hardlink):
                os.link(new_target, new_hardlink)
            if os.path.exists(hardlink_dup):
                os.unlink(hardlink_dup)

def _process_symlink(file, compress_format, decompress=False):
    compress_suffix = '.' + compress_format
    if decompress:
        # Remove compress suffix
        new_linkname = file[:-len(compress_suffix)]
        new_source = os.readlink(file)[:-len(compress_suffix)]
    else:
        # Append compress suffix
        new_linkname = file + compress_suffix
        new_source = os.readlink(file) + compress_suffix

    bb.note('symlink %s-->%s' % (new_linkname, new_source))
    if not os.path.exists(new_linkname):
        os.symlink(new_source, new_linkname)

    os.unlink(file)

def _is_info(file):
    flags = '.info .info-'.split()
    for flag in flags:
        if flag in os.path.basename(file):
            return True

    return False

def _is_man(file):
    import re

    # It refers MANSECT-var in man(1.6g)'s man.config
    # ".1:.1p:.8:.2:.3:.3p:.4:.5:.6:.7:.9:.0p:.tcl:.n:.l:.p:.o"
    # Not start with '.', and contain the above colon-seperate element
    p = re.compile(r'[^\.]+\.([1-9lnop]|0p|tcl)')
    if p.search(file):
        return True

    return False

def _is_compress_doc(file, compress_format_list):
    compress_format = _get_compress_format(file, compress_format_list)
    compress_suffix = '.' + compress_format
    if file.endswith(compress_suffix):
        # Remove the compress suffix
        uncompress_file = file[:-len(compress_suffix)]
        if _is_info(uncompress_file) or _is_man(uncompress_file):
            return True, compress_format

    return False, ''

def compress_doc(topdir, compress_mode, compress_cmds):
    import subprocess
    hardlink_dict = {}
    for root, dirs, files in os.walk(topdir):
        for f in files:
            file = os.path.join(root, f)
            if os.path.isdir(file):
                continue

            if _is_info(file) or _is_man(file):
                # Symlink
                if os.path.islink(file):
                    _process_symlink(file, compress_mode)
                # Hardlink
                elif os.lstat(file).st_nlink > 1:
                    _collect_hardlink(hardlink_dict, file)
                # Normal file
                elif os.path.isfile(file):
                    cmd = "%s %s" % (compress_cmds[compress_mode], file)
                    (retval, output) = subprocess.getstatusoutput(cmd)
                    if retval:
                        bb.warn("compress failed %s (cmd was %s)%s" % (retval, cmd, ":\n%s" % output if output else ""))
                        continue
                    bb.note('compress file %s' % file)

    _process_hardlink(hardlink_dict, compress_mode, compress_cmds)

# Decompress doc files which format is not compress_mode
def decompress_doc(topdir, compress_mode, decompress_cmds):
    import subprocess
    hardlink_dict = {}
    decompress = True
    for root, dirs, files in os.walk(topdir):
        for f in files:
            file = os.path.join(root, f)
            if os.path.isdir(file):
                continue

            res, compress_format = _is_compress_doc(file, decompress_cmds.keys())
            # Decompress files which format is not compress_mode
            if res and compress_mode!=compress_format:
                # Symlink
                if os.path.islink(file):
                    _process_symlink(file, compress_format, decompress)
                # Hardlink
                elif os.lstat(file).st_nlink > 1:
                    _collect_hardlink(hardlink_dict, file)
                # Normal file
                elif os.path.isfile(file):
                    cmd = "%s %s" % (decompress_cmds[compress_format], file)
                    (retval, output) = subprocess.getstatusoutput(cmd)
                    if retval:
                        bb.warn("decompress failed %s (cmd was %s)%s" % (retval, cmd, ":\n%s" % output if output else ""))
                        continue
                    bb.note('decompress file %s' % file)

    _process_hardlink(hardlink_dict, compress_mode, decompress_cmds, decompress)

python compress_doc_updatealternatives () {
    if not bb.data.inherits_class('update-alternatives', d):
        return

    mandir = d.getVar("mandir")
    infodir = d.getVar("infodir")
    compress_mode = d.getVar('DOC_COMPRESS')
    for pkg in (d.getVar('PACKAGES') or "").split():
        old_names = (d.getVar('ALTERNATIVE:%s' % pkg) or "").split()
        new_names = []
        for old_name in old_names:
            old_link     = d.getVarFlag('ALTERNATIVE_LINK_NAME', old_name)
            old_target   = d.getVarFlag('ALTERNATIVE_TARGET_%s' % pkg, old_name) or \
                d.getVarFlag('ALTERNATIVE_TARGET', old_name) or \
                d.getVar('ALTERNATIVE_TARGET_%s' % pkg) or \
                d.getVar('ALTERNATIVE_TARGET') or \
                old_link
            # Sometimes old_target is specified as relative to the link name.
            old_target   = os.path.join(os.path.dirname(old_link), old_target)

            # The updatealternatives used for compress doc
            if mandir in old_target or infodir in old_target:
                new_name = old_name + '.' + compress_mode
                new_link = old_link + '.' + compress_mode
                new_target = old_target + '.' + compress_mode
                d.delVarFlag('ALTERNATIVE_LINK_NAME', old_name)
                d.setVarFlag('ALTERNATIVE_LINK_NAME', new_name, new_link)
                if d.getVarFlag('ALTERNATIVE_TARGET_%s' % pkg, old_name):
                    d.delVarFlag('ALTERNATIVE_TARGET_%s' % pkg, old_name)
                    d.setVarFlag('ALTERNATIVE_TARGET_%s' % pkg, new_name, new_target)
                elif d.getVarFlag('ALTERNATIVE_TARGET', old_name):
                    d.delVarFlag('ALTERNATIVE_TARGET', old_name)
                    d.setVarFlag('ALTERNATIVE_TARGET', new_name, new_target)
                elif d.getVar('ALTERNATIVE_TARGET_%s' % pkg):
                    d.setVar('ALTERNATIVE_TARGET_%s' % pkg, new_target)
                elif d.getVar('ALTERNATIVE_TARGET'):
                    d.setVar('ALTERNATIVE_TARGET', new_target)

                new_names.append(new_name)

        if new_names:
            d.setVar('ALTERNATIVE:%s' % pkg, ' '.join(new_names))
}

