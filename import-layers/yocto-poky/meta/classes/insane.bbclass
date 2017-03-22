# BB Class inspired by ebuild.sh
#
# This class will test files after installation for certain
# security issues and other kind of issues.
#
# Checks we do:
#  -Check the ownership and permissions
#  -Check the RUNTIME path for the $TMPDIR
#  -Check if .la files wrongly point to workdir
#  -Check if .pc files wrongly point to workdir
#  -Check if packages contains .debug directories or .so files
#   where they should be in -dev or -dbg
#  -Check if config.log contains traces to broken autoconf tests
#  -Check invalid characters (non-utf8) on some package metadata
#  -Ensure that binaries in base_[bindir|sbindir|libdir] do not link
#   into exec_prefix
#  -Check that scripts in base_[bindir|sbindir|libdir] do not reference
#   files under exec_prefix


# unsafe-references-in-binaries requires prelink-rtld from
# prelink-native, but we don't want this DEPENDS for -native builds
QADEPENDS = "prelink-native"
QADEPENDS_class-native = ""
QADEPENDS_class-nativesdk = ""
QA_SANE = "True"

# Elect whether a given type of error is a warning or error, they may
# have been set by other files.
WARN_QA ?= "ldflags useless-rpaths rpaths staticdev libdir xorg-driver-abi \
            textrel already-stripped incompatible-license files-invalid \
            installed-vs-shipped compile-host-path install-host-path \
            pn-overrides infodir build-deps file-rdeps \
            unknown-configure-option symlink-to-sysroot multilib \
            invalid-packageconfig host-user-contaminated \
            "
ERROR_QA ?= "dev-so debug-deps dev-deps debug-files arch pkgconfig la \
            perms dep-cmp pkgvarcheck perm-config perm-line perm-link \
            split-strip packages-list pkgv-undefined var-undefined \
            version-going-backwards expanded-d invalid-chars \
            license-checksum dev-elf \
            "
FAKEROOT_QA = "host-user-contaminated"
FAKEROOT_QA[doc] = "QA tests which need to run under fakeroot. If any \
enabled tests are listed here, the do_package_qa task will run under fakeroot."

ALL_QA = "${WARN_QA} ${ERROR_QA}"

UNKNOWN_CONFIGURE_WHITELIST ?= "--enable-nls --disable-nls --disable-silent-rules --disable-dependency-tracking --with-libtool-sysroot --disable-static"

#
# dictionary for elf headers
#
# feel free to add and correct.
#
#           TARGET_OS  TARGET_ARCH   MACHINE, OSABI, ABIVERSION, Little Endian, 32bit?
def package_qa_get_machine_dict(d):
    machdata = {
            "darwin9" : { 
                        "arm" :       (40,     0,    0,          True,          32),
                      },
            "eabi" : {
                        "arm" :       (40,     0,    0,          True,          32),
                      },
            "elf" : {
                        "aarch64" :   (183,    0,    0,          True,          64),
                        "aarch64_be" :(183,    0,    0,          False,         64),
                        "i586" :      (3,      0,    0,          True,          32),
                        "x86_64":     (62,     0,    0,          True,          64),
                        "epiphany":   (4643,   0,    0,          True,          32),
                        "mips":       ( 8,     0,    0,          False,         32),
                        "mipsel":     ( 8,     0,    0,          True,          32),
                      },
            "linux" : { 
                        "aarch64" :   (183,    0,    0,          True,          64),
                        "aarch64_be" :(183,    0,    0,          False,         64),
                        "arm" :       (40,    97,    0,          True,          32),
                        "armeb":      (40,    97,    0,          False,         32),
                        "powerpc":    (20,     0,    0,          False,         32),
                        "powerpc64":  (21,     0,    0,          False,         64),
                        "i386":       ( 3,     0,    0,          True,          32),
                        "i486":       ( 3,     0,    0,          True,          32),
                        "i586":       ( 3,     0,    0,          True,          32),
                        "i686":       ( 3,     0,    0,          True,          32),
                        "x86_64":     (62,     0,    0,          True,          64),
                        "ia64":       (50,     0,    0,          True,          64),
                        "alpha":      (36902,  0,    0,          True,          64),
                        "hppa":       (15,     3,    0,          False,         32),
                        "m68k":       ( 4,     0,    0,          False,         32),
                        "mips":       ( 8,     0,    0,          False,         32),
                        "mipsel":     ( 8,     0,    0,          True,          32),
                        "mips64":     ( 8,     0,    0,          False,         64),
                        "mips64el":   ( 8,     0,    0,          True,          64),
                        "mipsisa32r6":   ( 8,  0,    0,          False,         32),
                        "mipsisa32r6el": ( 8,  0,    0,          True,          32),
                        "mipsisa64r6":   ( 8,  0,    0,          False,         64),
                        "mipsisa64r6el": ( 8,  0,    0,          True,          64),
                        "nios2":      (113,    0,    0,          True,          32),
                        "s390":       (22,     0,    0,          False,         32),
                        "sh4":        (42,     0,    0,          True,          32),
                        "sparc":      ( 2,     0,    0,          False,         32),
                        "microblaze":  (189,   0,    0,          False,         32),
                        "microblazeeb":(189,   0,    0,          False,         32),
                        "microblazeel":(189,   0,    0,          True,          32),
                      },
            "linux-uclibc" : { 
                        "arm" :       (  40,    97,    0,          True,          32),
                        "armeb":      (  40,    97,    0,          False,         32),
                        "powerpc":    (  20,     0,    0,          False,         32),
                        "i386":       (   3,     0,    0,          True,          32),
                        "i486":       (   3,     0,    0,          True,          32),
                        "i586":       (   3,     0,    0,          True,          32),
                        "i686":       (   3,     0,    0,          True,          32),
                        "x86_64":     (  62,     0,    0,          True,          64),
                        "mips":       (   8,     0,    0,          False,         32),
                        "mipsel":     (   8,     0,    0,          True,          32),
                        "mips64":     (   8,     0,    0,          False,         64),
                        "mips64el":   (   8,     0,    0,          True,          64),
                        "avr32":      (6317,     0,    0,          False,         32),
                        "sh4":        (42,       0,    0,          True,          32),

                      },
            "linux-musl" : { 
                        "aarch64" :   (183,    0,    0,            True,          64),
                        "aarch64_be" :(183,    0,    0,            False,         64),
                        "arm" :       (  40,    97,    0,          True,          32),
                        "armeb":      (  40,    97,    0,          False,         32),
                        "powerpc":    (  20,     0,    0,          False,         32),
                        "i386":       (   3,     0,    0,          True,          32),
                        "i486":       (   3,     0,    0,          True,          32),
                        "i586":       (   3,     0,    0,          True,          32),
                        "i686":       (   3,     0,    0,          True,          32),
                        "x86_64":     (  62,     0,    0,          True,          64),
                        "mips":       (   8,     0,    0,          False,         32),
                        "mipsel":     (   8,     0,    0,          True,          32),
                        "mips64":     (   8,     0,    0,          False,         64),
                        "mips64el":   (   8,     0,    0,          True,          64),
                        "microblaze":  (189,     0,    0,          False,         32),
                        "microblazeeb":(189,     0,    0,          False,         32),
                        "microblazeel":(189,     0,    0,          True,          32),
                      },
            "uclinux-uclibc" : {
                        "bfin":       ( 106,     0,    0,          True,         32),
                      }, 
            "linux-gnueabi" : {
                        "arm" :       (40,     0,    0,          True,          32),
                        "armeb" :     (40,     0,    0,          False,         32),
                      },
            "linux-musleabi" : {
                        "arm" :       (40,     0,    0,          True,          32),
                        "armeb" :     (40,     0,    0,          False,         32),
                      },
            "linux-uclibceabi" : {
                        "arm" :       (40,     0,    0,          True,          32),
                        "armeb" :     (40,     0,    0,          False,         32),
                      },
            "linux-gnuspe" : {
                        "powerpc":    (20,     0,    0,          False,         32),
                      },
            "linux-muslspe" : {
                        "powerpc":    (20,     0,    0,          False,         32),
                      },
            "linux-uclibcspe" : {
                        "powerpc":    (20,     0,    0,          False,         32),
                      },
            "linux-gnu" :       {
                        "powerpc":    (20,     0,    0,          False,         32),
                        "sh4":        (42,     0,    0,          True,          32),
                      },
            "linux-gnux32" :       {
                        "x86_64":     (62,     0,    0,          True,          32),
                      },
            "linux-gnun32" :       {
                        "mips64":       ( 8,     0,    0,          False,         32),
                        "mips64el":     ( 8,     0,    0,          True,          32),
                      },
        }

    # Add in any extra user supplied data which may come from a BSP layer, removing the
    # need to always change this class directly
    extra_machdata = (d.getVar("PACKAGEQA_EXTRA_MACHDEFFUNCS", True) or "").split()
    for m in extra_machdata:
        call = m + "(machdata, d)"
        locs = { "machdata" : machdata, "d" : d}
        machdata = bb.utils.better_eval(call, locs)

    return machdata


def package_qa_clean_path(path, d, pkg=None):
    """
    Remove redundant paths from the path for display.  If pkg isn't set then
    TMPDIR is stripped, otherwise PKGDEST/pkg is stripped.
    """
    if pkg:
        path = path.replace(os.path.join(d.getVar("PKGDEST", True), pkg), "/")
    return path.replace(d.getVar("TMPDIR", True), "/").replace("//", "/")

def package_qa_write_error(type, error, d):
    logfile = d.getVar('QA_LOGFILE', True)
    if logfile:
        p = d.getVar('P', True)
        with open(logfile, "a+") as f:
            f.write("%s: %s [%s]\n" % (p, error, type))

def package_qa_handle_error(error_class, error_msg, d):
    package_qa_write_error(error_class, error_msg, d)
    if error_class in (d.getVar("ERROR_QA", True) or "").split():
        bb.error("QA Issue: %s [%s]" % (error_msg, error_class))
        d.setVar("QA_SANE", False)
        return False
    elif error_class in (d.getVar("WARN_QA", True) or "").split():
        bb.warn("QA Issue: %s [%s]" % (error_msg, error_class))
    else:
        bb.note("QA Issue: %s [%s]" % (error_msg, error_class))
    return True

def package_qa_add_message(messages, section, new_msg):
    if section not in messages:
        messages[section] = new_msg
    else:
        messages[section] = messages[section] + "\n" + new_msg

QAPATHTEST[libexec] = "package_qa_check_libexec"
def package_qa_check_libexec(path,name, d, elf, messages):

    # Skip the case where the default is explicitly /usr/libexec
    libexec = d.getVar('libexecdir', True)
    if libexec == "/usr/libexec":
        return True

    if 'libexec' in path.split(os.path.sep):
        package_qa_add_message(messages, "libexec", "%s: %s is using libexec please relocate to %s" % (name, package_qa_clean_path(path, d), libexec))
        return False

    return True

QAPATHTEST[rpaths] = "package_qa_check_rpath"
def package_qa_check_rpath(file,name, d, elf, messages):
    """
    Check for dangerous RPATHs
    """
    if not elf:
        return

    if os.path.islink(file):
        return

    bad_dirs = [d.getVar('BASE_WORKDIR', True), d.getVar('STAGING_DIR_TARGET', True)]

    phdrs = elf.run_objdump("-p", d)

    import re
    rpath_re = re.compile("\s+RPATH\s+(.*)")
    for line in phdrs.split("\n"):
        m = rpath_re.match(line)
        if m:
            rpath = m.group(1)
            for dir in bad_dirs:
                if dir in rpath:
                    package_qa_add_message(messages, "rpaths", "package %s contains bad RPATH %s in file %s" % (name, rpath, file))

QAPATHTEST[useless-rpaths] = "package_qa_check_useless_rpaths"
def package_qa_check_useless_rpaths(file, name, d, elf, messages):
    """
    Check for RPATHs that are useless but not dangerous
    """
    def rpath_eq(a, b):
        return os.path.normpath(a) == os.path.normpath(b)

    if not elf:
        return

    if os.path.islink(file):
        return

    libdir = d.getVar("libdir", True)
    base_libdir = d.getVar("base_libdir", True)

    phdrs = elf.run_objdump("-p", d)

    import re
    rpath_re = re.compile("\s+RPATH\s+(.*)")
    for line in phdrs.split("\n"):
        m = rpath_re.match(line)
        if m:
            rpath = m.group(1)
            if rpath_eq(rpath, libdir) or rpath_eq(rpath, base_libdir):
                # The dynamic linker searches both these places anyway.  There is no point in
                # looking there again.
                package_qa_add_message(messages, "useless-rpaths", "%s: %s contains probably-redundant RPATH %s" % (name, package_qa_clean_path(file, d), rpath))

QAPATHTEST[dev-so] = "package_qa_check_dev"
def package_qa_check_dev(path, name, d, elf, messages):
    """
    Check for ".so" library symlinks in non-dev packages
    """

    if not name.endswith("-dev") and not name.endswith("-dbg") and not name.endswith("-ptest") and not name.startswith("nativesdk-") and path.endswith(".so") and os.path.islink(path):
        package_qa_add_message(messages, "dev-so", "non -dev/-dbg/nativesdk- package contains symlink .so: %s path '%s'" % \
                 (name, package_qa_clean_path(path,d)))

QAPATHTEST[dev-elf] = "package_qa_check_dev_elf"
def package_qa_check_dev_elf(path, name, d, elf, messages):
    """
    Check that -dev doesn't contain real shared libraries.  The test has to
    check that the file is not a link and is an ELF object as some recipes
    install link-time .so files that are linker scripts.
    """
    if name.endswith("-dev") and path.endswith(".so") and not os.path.islink(path) and elf:
        package_qa_add_message(messages, "dev-elf", "-dev package contains non-symlink .so: %s path '%s'" % \
                 (name, package_qa_clean_path(path,d)))

QAPATHTEST[staticdev] = "package_qa_check_staticdev"
def package_qa_check_staticdev(path, name, d, elf, messages):
    """
    Check for ".a" library in non-staticdev packages
    There are a number of exceptions to this rule, -pic packages can contain
    static libraries, the _nonshared.a belong with their -dev packages and
    libgcc.a, libgcov.a will be skipped in their packages
    """

    if not name.endswith("-pic") and not name.endswith("-staticdev") and not name.endswith("-ptest") and path.endswith(".a") and not path.endswith("_nonshared.a"):
        package_qa_add_message(messages, "staticdev", "non -staticdev package contains static .a library: %s path '%s'" % \
                 (name, package_qa_clean_path(path,d)))

def package_qa_check_libdir(d):
    """
    Check for wrong library installation paths. For instance, catch
    recipes installing /lib/bar.so when ${base_libdir}="lib32" or
    installing in /usr/lib64 when ${libdir}="/usr/lib"
    """
    import re

    pkgdest = d.getVar('PKGDEST', True)
    base_libdir = d.getVar("base_libdir",True) + os.sep
    libdir = d.getVar("libdir", True) + os.sep
    libexecdir = d.getVar("libexecdir", True) + os.sep
    exec_prefix = d.getVar("exec_prefix", True) + os.sep

    messages = []

    # The re's are purposely fuzzy, as some there are some .so.x.y.z files
    # that don't follow the standard naming convention. It checks later
    # that they are actual ELF files
    lib_re = re.compile("^/lib.+\.so(\..+)?$")
    exec_re = re.compile("^%s.*/lib.+\.so(\..+)?$" % exec_prefix)

    for root, dirs, files in os.walk(pkgdest):
        if root == pkgdest:
            # Skip subdirectories for any packages with libdir in INSANE_SKIP
            skippackages = []
            for package in dirs:
                if 'libdir' in (d.getVar('INSANE_SKIP_' + package, True) or "").split():
                    bb.note("Package %s skipping libdir QA test" % (package))
                    skippackages.append(package)
                elif d.getVar('PACKAGE_DEBUG_SPLIT_STYLE', True) == 'debug-file-directory' and package.endswith("-dbg"):
                    bb.note("Package %s skipping libdir QA test for PACKAGE_DEBUG_SPLIT_STYLE equals debug-file-directory" % (package))
                    skippackages.append(package)
            for package in skippackages:
                dirs.remove(package)
        for file in files:
            full_path = os.path.join(root, file)
            rel_path = os.path.relpath(full_path, pkgdest)
            if os.sep in rel_path:
                package, rel_path = rel_path.split(os.sep, 1)
                rel_path = os.sep + rel_path
                if lib_re.match(rel_path):
                    if base_libdir not in rel_path:
                        # make sure it's an actual ELF file
                        elf = oe.qa.ELFFile(full_path)
                        try:
                            elf.open()
                            messages.append("%s: found library in wrong location: %s" % (package, rel_path))
                        except (oe.qa.NotELFFileError):
                            pass
                if exec_re.match(rel_path):
                    if libdir not in rel_path and libexecdir not in rel_path:
                        # make sure it's an actual ELF file
                        elf = oe.qa.ELFFile(full_path)
                        try:
                            elf.open()
                            messages.append("%s: found library in wrong location: %s" % (package, rel_path))
                        except (oe.qa.NotELFFileError):
                            pass

    if messages:
        package_qa_handle_error("libdir", "\n".join(messages), d)

QAPATHTEST[debug-files] = "package_qa_check_dbg"
def package_qa_check_dbg(path, name, d, elf, messages):
    """
    Check for ".debug" files or directories outside of the dbg package
    """

    if not "-dbg" in name and not "-ptest" in name:
        if '.debug' in path.split(os.path.sep):
            messages("debug-files", "non debug package contains .debug directory: %s path %s" % \
                     (name, package_qa_clean_path(path,d)))

QAPATHTEST[perms] = "package_qa_check_perm"
def package_qa_check_perm(path,name,d, elf, messages):
    """
    Check the permission of files
    """
    return

QAPATHTEST[unsafe-references-in-binaries] = "package_qa_check_unsafe_references_in_binaries"
def package_qa_check_unsafe_references_in_binaries(path, name, d, elf, messages):
    """
    Ensure binaries in base_[bindir|sbindir|libdir] do not link to files under exec_prefix
    """
    if unsafe_references_skippable(path, name, d):
        return

    if elf:
        import subprocess as sub
        pn = d.getVar('PN', True)

        exec_prefix = d.getVar('exec_prefix', True)
        sysroot_path = d.getVar('STAGING_DIR_TARGET', True)
        sysroot_path_usr = sysroot_path + exec_prefix

        try:
            ldd_output = bb.process.Popen(["prelink-rtld", "--root", sysroot_path, path], stdout=sub.PIPE).stdout.read().decode("utf-8")
        except bb.process.CmdError:
            error_msg = pn + ": prelink-rtld aborted when processing %s" % path
            package_qa_handle_error("unsafe-references-in-binaries", error_msg, d)
            return False

        if sysroot_path_usr in ldd_output:
            ldd_output = ldd_output.replace(sysroot_path, "")

            pkgdest = d.getVar('PKGDEST', True)
            packages = d.getVar('PACKAGES', True)

            for package in packages.split():
                short_path = path.replace('%s/%s' % (pkgdest, package), "", 1)
                if (short_path != path):
                    break

            base_err = pn + ": %s, installed in the base_prefix, requires a shared library under exec_prefix (%s)" % (short_path, exec_prefix)
            for line in ldd_output.split('\n'):
                if exec_prefix in line:
                    error_msg = "%s: %s" % (base_err, line.strip())
                    package_qa_handle_error("unsafe-references-in-binaries", error_msg, d)

            return False

QAPATHTEST[unsafe-references-in-scripts] = "package_qa_check_unsafe_references_in_scripts"
def package_qa_check_unsafe_references_in_scripts(path, name, d, elf, messages):
    """
    Warn if scripts in base_[bindir|sbindir|libdir] reference files under exec_prefix
    """
    if unsafe_references_skippable(path, name, d):
        return

    if not elf:
        import stat
        import subprocess
        pn = d.getVar('PN', True)

        # Ensure we're checking an executable script
        statinfo = os.stat(path)
        if bool(statinfo.st_mode & stat.S_IXUSR):
            # grep shell scripts for possible references to /exec_prefix/
            exec_prefix = d.getVar('exec_prefix', True)
            statement = "grep -e '%s/[^ :]\{1,\}/[^ :]\{1,\}' %s > /dev/null" % (exec_prefix, path)
            if subprocess.call(statement, shell=True) == 0:
                error_msg = pn + ": Found a reference to %s/ in %s" % (exec_prefix, path)
                package_qa_handle_error("unsafe-references-in-scripts", error_msg, d)
                error_msg = "Shell scripts in base_bindir and base_sbindir should not reference anything in exec_prefix"
                package_qa_handle_error("unsafe-references-in-scripts", error_msg, d)

def unsafe_references_skippable(path, name, d):
    if bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d):
        return True

    if "-dbg" in name or "-dev" in name:
        return True

    # Other package names to skip:
    if name.startswith("kernel-module-"):
        return True

    # Skip symlinks
    if os.path.islink(path):
        return True

    # Skip unusual rootfs layouts which make these tests irrelevant
    exec_prefix = d.getVar('exec_prefix', True)
    if exec_prefix == "":
        return True

    pkgdest = d.getVar('PKGDEST', True)
    pkgdest = pkgdest + "/" + name
    pkgdest = os.path.abspath(pkgdest)
    base_bindir = pkgdest + d.getVar('base_bindir', True)
    base_sbindir = pkgdest + d.getVar('base_sbindir', True)
    base_libdir = pkgdest + d.getVar('base_libdir', True)
    bindir = pkgdest + d.getVar('bindir', True)
    sbindir = pkgdest + d.getVar('sbindir', True)
    libdir = pkgdest + d.getVar('libdir', True)

    if base_bindir == bindir and base_sbindir == sbindir and base_libdir == libdir:
        return True

    # Skip files not in base_[bindir|sbindir|libdir]
    path = os.path.abspath(path)
    if not (base_bindir in path or base_sbindir in path or base_libdir in path):
        return True

    return False

QAPATHTEST[arch] = "package_qa_check_arch"
def package_qa_check_arch(path,name,d, elf, messages):
    """
    Check if archs are compatible
    """
    import re

    if not elf:
        return

    target_os   = d.getVar('TARGET_OS', True)
    target_arch = d.getVar('TARGET_ARCH', True)
    provides = d.getVar('PROVIDES', True)
    bpn = d.getVar('BPN', True)

    if target_arch == "allarch":
        pn = d.getVar('PN', True)
        package_qa_add_message(messages, "arch", pn + ": Recipe inherits the allarch class, but has packaged architecture-specific binaries")
        return

    # FIXME: Cross package confuse this check, so just skip them
    for s in ['cross', 'nativesdk', 'cross-canadian']:
        if bb.data.inherits_class(s, d):
            return

    # avoid following links to /usr/bin (e.g. on udev builds)
    # we will check the files pointed to anyway...
    if os.path.islink(path):
        return

    #if this will throw an exception, then fix the dict above
    (machine, osabi, abiversion, littleendian, bits) \
        = package_qa_get_machine_dict(d)[target_os][target_arch]

    # Check the architecture and endiannes of the binary
    is_32 = (("virtual/kernel" in provides) or bb.data.inherits_class("module", d)) and \
            (target_os == "linux-gnux32" or re.match('mips64.*32', d.getVar('DEFAULTTUNE', True)))
    if not ((machine == elf.machine()) or is_32):
        package_qa_add_message(messages, "arch", "Architecture did not match (%s, expected %s) on %s" % \
                 (oe.qa.elf_machine_to_string(elf.machine()), oe.qa.elf_machine_to_string(machine), package_qa_clean_path(path,d)))
    elif not ((bits == elf.abiSize()) or is_32):
        package_qa_add_message(messages, "arch", "Bit size did not match (%d to %d) %s on %s" % \
                 (bits, elf.abiSize(), bpn, package_qa_clean_path(path,d)))
    elif not littleendian == elf.isLittleEndian():
        package_qa_add_message(messages, "arch", "Endiannes did not match (%d to %d) on %s" % \
                 (littleendian, elf.isLittleEndian(), package_qa_clean_path(path,d)))

QAPATHTEST[desktop] = "package_qa_check_desktop"
def package_qa_check_desktop(path, name, d, elf, messages):
    """
    Run all desktop files through desktop-file-validate.
    """
    if path.endswith(".desktop"):
        desktop_file_validate = os.path.join(d.getVar('STAGING_BINDIR_NATIVE',True),'desktop-file-validate')
        output = os.popen("%s %s" % (desktop_file_validate, path))
        # This only produces output on errors
        for l in output:
            package_qa_add_message(messages, "desktop", "Desktop file issue: " + l.strip())

QAPATHTEST[textrel] = "package_qa_textrel"
def package_qa_textrel(path, name, d, elf, messages):
    """
    Check if the binary contains relocations in .text
    """

    if not elf:
        return

    if os.path.islink(path):
        return

    phdrs = elf.run_objdump("-p", d)
    sane = True

    import re
    textrel_re = re.compile("\s+TEXTREL\s+")
    for line in phdrs.split("\n"):
        if textrel_re.match(line):
            sane = False

    if not sane:
        package_qa_add_message(messages, "textrel", "ELF binary '%s' has relocations in .text" % path)

QAPATHTEST[ldflags] = "package_qa_hash_style"
def package_qa_hash_style(path, name, d, elf, messages):
    """
    Check if the binary has the right hash style...
    """

    if not elf:
        return

    if os.path.islink(path):
        return

    gnu_hash = "--hash-style=gnu" in d.getVar('LDFLAGS', True)
    if not gnu_hash:
        gnu_hash = "--hash-style=both" in d.getVar('LDFLAGS', True)
    if not gnu_hash:
        return

    sane = False
    has_syms = False

    phdrs = elf.run_objdump("-p", d)

    # If this binary has symbols, we expect it to have GNU_HASH too.
    for line in phdrs.split("\n"):
        if "SYMTAB" in line:
            has_syms = True
        if "GNU_HASH" in line:
            sane = True
        if "[mips32]" in line or "[mips64]" in line:
            sane = True

    if has_syms and not sane:
        package_qa_add_message(messages, "ldflags", "No GNU_HASH in the elf binary: '%s'" % path)


QAPATHTEST[buildpaths] = "package_qa_check_buildpaths"
def package_qa_check_buildpaths(path, name, d, elf, messages):
    """
    Check for build paths inside target files and error if not found in the whitelist
    """
    # Ignore .debug files, not interesting
    if path.find(".debug") != -1:
        return

    # Ignore symlinks
    if os.path.islink(path):
        return

    # Ignore ipk and deb's CONTROL dir
    if path.find(name + "/CONTROL/") != -1 or path.find(name + "/DEBIAN/") != -1:
        return

    tmpdir = d.getVar('TMPDIR', True)
    with open(path, 'rb') as f:
        file_content = f.read().decode('utf-8', errors='ignore')
        if tmpdir in file_content:
            package_qa_add_message(messages, "buildpaths", "File %s in package contained reference to tmpdir" % package_qa_clean_path(path,d))


QAPATHTEST[xorg-driver-abi] = "package_qa_check_xorg_driver_abi"
def package_qa_check_xorg_driver_abi(path, name, d, elf, messages):
    """
    Check that all packages containing Xorg drivers have ABI dependencies
    """

    # Skip dev, dbg or nativesdk packages
    if name.endswith("-dev") or name.endswith("-dbg") or name.startswith("nativesdk-"):
        return

    driverdir = d.expand("${libdir}/xorg/modules/drivers/")
    if driverdir in path and path.endswith(".so"):
        mlprefix = d.getVar('MLPREFIX', True) or ''
        for rdep in bb.utils.explode_deps(d.getVar('RDEPENDS_' + name, True) or ""):
            if rdep.startswith("%sxorg-abi-" % mlprefix):
                return
        package_qa_add_message(messages, "xorg-driver-abi", "Package %s contains Xorg driver (%s) but no xorg-abi- dependencies" % (name, os.path.basename(path)))

QAPATHTEST[infodir] = "package_qa_check_infodir"
def package_qa_check_infodir(path, name, d, elf, messages):
    """
    Check that /usr/share/info/dir isn't shipped in a particular package
    """
    infodir = d.expand("${infodir}/dir")

    if infodir in path:
        package_qa_add_message(messages, "infodir", "The /usr/share/info/dir file is not meant to be shipped in a particular package.")

QAPATHTEST[symlink-to-sysroot] = "package_qa_check_symlink_to_sysroot"
def package_qa_check_symlink_to_sysroot(path, name, d, elf, messages):
    """
    Check that the package doesn't contain any absolute symlinks to the sysroot.
    """
    if os.path.islink(path):
        target = os.readlink(path)
        if os.path.isabs(target):
            tmpdir = d.getVar('TMPDIR', True)
            if target.startswith(tmpdir):
                trimmed = path.replace(os.path.join (d.getVar("PKGDEST", True), name), "")
                package_qa_add_message(messages, "symlink-to-sysroot", "Symlink %s in %s points to TMPDIR" % (trimmed, name))

# Check license variables
do_populate_lic[postfuncs] += "populate_lic_qa_checksum"
python populate_lic_qa_checksum() {
    """
    Check for changes in the license files.
    """
    import tempfile
    sane = True

    lic_files = d.getVar('LIC_FILES_CHKSUM', True) or ''
    lic = d.getVar('LICENSE', True)
    pn = d.getVar('PN', True)

    if lic == "CLOSED":
        return

    if not lic_files and d.getVar('SRC_URI', True):
        sane = package_qa_handle_error("license-checksum", pn + ": Recipe file fetches files and does not have license file information (LIC_FILES_CHKSUM)", d)

    srcdir = d.getVar('S', True)

    for url in lic_files.split():
        try:
            (type, host, path, user, pswd, parm) = bb.fetch.decodeurl(url)
        except bb.fetch.MalformedUrl:
            sane = package_qa_handle_error("license-checksum", pn + ": LIC_FILES_CHKSUM contains an invalid URL: " + url, d)
            continue
        srclicfile = os.path.join(srcdir, path)
        if not os.path.isfile(srclicfile):
            package_qa_handle_error("license-checksum", pn + ": LIC_FILES_CHKSUM points to an invalid file: " + srclicfile, d)
            continue

        recipemd5 = parm.get('md5', '')
        beginline, endline = 0, 0
        if 'beginline' in parm:
            beginline = int(parm['beginline'])
        if 'endline' in parm:
            endline = int(parm['endline'])

        if (not beginline) and (not endline):
            md5chksum = bb.utils.md5_file(srclicfile)
        else:
            fi = open(srclicfile, 'rb')
            fo = tempfile.NamedTemporaryFile(mode='wb', prefix='poky.', suffix='.tmp', delete=False)
            tmplicfile = fo.name;
            lineno = 0
            linesout = 0
            for line in fi:
                lineno += 1
                if (lineno >= beginline):
                    if ((lineno <= endline) or not endline):
                        fo.write(line)
                        linesout += 1
                    else:
                        break
            fo.flush()
            fo.close()
            fi.close()
            md5chksum = bb.utils.md5_file(tmplicfile)
            os.unlink(tmplicfile)

        if recipemd5 == md5chksum:
            bb.note (pn + ": md5 checksum matched for ", url)
        else:
            if recipemd5:
                msg = pn + ": The LIC_FILES_CHKSUM does not match for " + url
                msg = msg + "\n" + pn + ": The new md5 checksum is " + md5chksum
                if beginline:
                    if endline:
                        srcfiledesc = "%s (lines %d through to %d)" % (srclicfile, beginline, endline)
                    else:
                        srcfiledesc = "%s (beginning on line %d)" % (srclicfile, beginline)
                elif endline:
                    srcfiledesc = "%s (ending on line %d)" % (srclicfile, endline)
                else:
                    srcfiledesc = srclicfile
                msg = msg + "\n" + pn + ": Check if the license information has changed in %s to verify that the LICENSE value \"%s\" remains valid" % (srcfiledesc, lic)

            else:
                msg = pn + ": LIC_FILES_CHKSUM is not specified for " +  url
                msg = msg + "\n" + pn + ": The md5 checksum is " + md5chksum
            sane = package_qa_handle_error("license-checksum", msg, d)

    if not sane:
        bb.fatal("Fatal QA errors found, failing task.")
}

def package_qa_check_staged(path,d):
    """
    Check staged la and pc files for common problems like references to the work
    directory.

    As this is run after every stage we should be able to find the one
    responsible for the errors easily even if we look at every .pc and .la file.
    """

    sane = True
    tmpdir = d.getVar('TMPDIR', True)
    workdir = os.path.join(tmpdir, "work")

    if bb.data.inherits_class("native", d) or bb.data.inherits_class("cross", d):
        pkgconfigcheck = workdir
    else:
        pkgconfigcheck = tmpdir

    # find all .la and .pc files
    # read the content
    # and check for stuff that looks wrong
    for root, dirs, files in os.walk(path):
        for file in files:
            path = os.path.join(root,file)
            if file.endswith(".la"):
                with open(path) as f:
                    file_content = f.read()
                    if workdir in file_content:
                        error_msg = "%s failed sanity test (workdir) in path %s" % (file,root)
                        sane = package_qa_handle_error("la", error_msg, d)
            elif file.endswith(".pc"):
                with open(path) as f:
                    file_content = f.read()
                    if pkgconfigcheck in file_content:
                        error_msg = "%s failed sanity test (tmpdir) in path %s" % (file,root)
                        sane = package_qa_handle_error("pkgconfig", error_msg, d)

    return sane

# Run all package-wide warnfuncs and errorfuncs
def package_qa_package(warnfuncs, errorfuncs, skip, package, d):
    warnings = {}
    errors = {}

    for func in warnfuncs:
        func(package, d, warnings)
    for func in errorfuncs:
        func(package, d, errors)

    for w in warnings:
        package_qa_handle_error(w, warnings[w], d)
    for e in errors:
        package_qa_handle_error(e, errors[e], d)

    return len(errors) == 0

# Walk over all files in a directory and call func
def package_qa_walk(warnfuncs, errorfuncs, skip, package, d):
    import oe.qa

    #if this will throw an exception, then fix the dict above
    target_os   = d.getVar('TARGET_OS', True)
    target_arch = d.getVar('TARGET_ARCH', True)

    warnings = {}
    errors = {}
    for path in pkgfiles[package]:
            elf = oe.qa.ELFFile(path)
            try:
                elf.open()
            except (IOError, oe.qa.NotELFFileError):
                # IOError can happen if the packaging control files disappear,
                elf = None
            for func in warnfuncs:
                func(path, package, d, elf, warnings)
            for func in errorfuncs:
                func(path, package, d, elf, errors)

    for w in warnings:
        package_qa_handle_error(w, warnings[w], d)
    for e in errors:
        package_qa_handle_error(e, errors[e], d)

def package_qa_check_rdepends(pkg, pkgdest, skip, taskdeps, packages, d):
    # Don't do this check for kernel/module recipes, there aren't too many debug/development
    # packages and you can get false positives e.g. on kernel-module-lirc-dev
    if bb.data.inherits_class("kernel", d) or bb.data.inherits_class("module-base", d):
        return

    if not "-dbg" in pkg and not "packagegroup-" in pkg and not "-image" in pkg:
        localdata = bb.data.createCopy(d)
        localdata.setVar('OVERRIDES', pkg)
        bb.data.update_data(localdata)

        # Now check the RDEPENDS
        rdepends = bb.utils.explode_deps(localdata.getVar('RDEPENDS', True) or "")

        # Now do the sanity check!!!
        if "build-deps" not in skip:
            for rdepend in rdepends:
                if "-dbg" in rdepend and "debug-deps" not in skip:
                    error_msg = "%s rdepends on %s" % (pkg,rdepend)
                    package_qa_handle_error("debug-deps", error_msg, d)
                if (not "-dev" in pkg and not "-staticdev" in pkg) and rdepend.endswith("-dev") and "dev-deps" not in skip:
                    error_msg = "%s rdepends on %s" % (pkg, rdepend)
                    package_qa_handle_error("dev-deps", error_msg, d)
                if rdepend not in packages:
                    rdep_data = oe.packagedata.read_subpkgdata(rdepend, d)
                    if rdep_data and 'PN' in rdep_data and rdep_data['PN'] in taskdeps:
                        continue
                    if not rdep_data or not 'PN' in rdep_data:
                        pkgdata_dir = d.getVar("PKGDATA_DIR", True)
                        try:
                            possibles = os.listdir("%s/runtime-rprovides/%s/" % (pkgdata_dir, rdepend))
                        except OSError:
                            possibles = []
                        for p in possibles:
                            rdep_data = oe.packagedata.read_subpkgdata(p, d)
                            if rdep_data and 'PN' in rdep_data and rdep_data['PN'] in taskdeps:
                                break
                    if rdep_data and 'PN' in rdep_data and rdep_data['PN'] in taskdeps:
                        continue
                    if rdep_data and 'PN' in rdep_data:
                        error_msg = "%s rdepends on %s, but it isn't a build dependency, missing %s in DEPENDS or PACKAGECONFIG?" % (pkg, rdepend, rdep_data['PN'])
                    else:
                        error_msg = "%s rdepends on %s, but it isn't a build dependency?" % (pkg, rdepend)
                    package_qa_handle_error("build-deps", error_msg, d)

        if "file-rdeps" not in skip:
            ignored_file_rdeps = set(['/bin/sh', '/usr/bin/env', 'rtld(GNU_HASH)'])
            if bb.data.inherits_class('nativesdk', d):
                ignored_file_rdeps |= set(['/bin/bash', '/usr/bin/perl'])
            # For Saving the FILERDEPENDS
            filerdepends = {}
            rdep_data = oe.packagedata.read_subpkgdata(pkg, d)
            for key in rdep_data:
                if key.startswith("FILERDEPENDS_"):
                    for subkey in rdep_data[key].split():
                        if subkey not in ignored_file_rdeps:
                            # We already know it starts with FILERDEPENDS_
                            filerdepends[subkey] = key[13:]

            if filerdepends:
                next = rdepends
                done = rdepends[:]
                # Find all the rdepends on the dependency chain
                while next:
                    new = []
                    for rdep in next:
                        rdep_data = oe.packagedata.read_subpkgdata(rdep, d)
                        sub_rdeps = rdep_data.get("RDEPENDS_" + rdep)
                        if not sub_rdeps:
                            continue
                        for sub_rdep in sub_rdeps.split():
                            if sub_rdep in done:
                                continue
                            if not sub_rdep.startswith('(') and \
                                    oe.packagedata.has_subpkgdata(sub_rdep, d):
                                # It's a new rdep
                                done.append(sub_rdep)
                                new.append(sub_rdep)
                    next = new

                # Add the rprovides of itself
                if pkg not in done:
                    done.insert(0, pkg)

                # The python is not a package, but python-core provides it, so
                # skip checking /usr/bin/python if python is in the rdeps, in
                # case there is a RDEPENDS_pkg = "python" in the recipe.
                for py in [ d.getVar('MLPREFIX', True) + "python", "python" ]:
                    if py in done:
                        filerdepends.pop("/usr/bin/python",None)
                        done.remove(py)
                for rdep in done:
                    # For Saving the FILERPROVIDES, RPROVIDES and FILES_INFO
                    rdep_data = oe.packagedata.read_subpkgdata(rdep, d)
                    for key in rdep_data:
                        if key.startswith("FILERPROVIDES_") or key.startswith("RPROVIDES_"):
                            for subkey in rdep_data[key].split():
                                filerdepends.pop(subkey,None)
                        # Add the files list to the rprovides
                        if key == "FILES_INFO":
                            # Use eval() to make it as a dict
                            for subkey in eval(rdep_data[key]):
                                filerdepends.pop(subkey,None)
                    if not filerdepends:
                        # Break if all the file rdepends are met
                        break
            if filerdepends:
                for key in filerdepends:
                    error_msg = "%s contained in package %s requires %s, but no providers found in RDEPENDS_%s?" % \
                            (filerdepends[key].replace("_%s" % pkg, "").replace("@underscore@", "_"), pkg, key, pkg)
                package_qa_handle_error("file-rdeps", error_msg, d)

def package_qa_check_deps(pkg, pkgdest, skip, d):

    localdata = bb.data.createCopy(d)
    localdata.setVar('OVERRIDES', pkg)
    bb.data.update_data(localdata)

    def check_valid_deps(var):
        try:
            rvar = bb.utils.explode_dep_versions2(localdata.getVar(var, True) or "")
        except ValueError as e:
            bb.fatal("%s_%s: %s" % (var, pkg, e))
        for dep in rvar:
            for v in rvar[dep]:
                if v and not v.startswith(('< ', '= ', '> ', '<= ', '>=')):
                    error_msg = "%s_%s is invalid: %s (%s)   only comparisons <, =, >, <=, and >= are allowed" % (var, pkg, dep, v)
                    package_qa_handle_error("dep-cmp", error_msg, d)

    check_valid_deps('RDEPENDS')
    check_valid_deps('RRECOMMENDS')
    check_valid_deps('RSUGGESTS')
    check_valid_deps('RPROVIDES')
    check_valid_deps('RREPLACES')
    check_valid_deps('RCONFLICTS')

QAPKGTEST[expanded-d] = "package_qa_check_expanded_d"
def package_qa_check_expanded_d(package, d, messages):
    """
    Check for the expanded D (${D}) value in pkg_* and FILES
    variables, warn the user to use it correctly.
    """
    sane = True
    expanded_d = d.getVar('D', True)

    for var in 'FILES','pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm':
        bbvar = d.getVar(var + "_" + package, True) or ""
        if expanded_d in bbvar:
            if var == 'FILES':
                package_qa_add_message(messages, "expanded-d", "FILES in %s recipe should not contain the ${D} variable as it references the local build directory not the target filesystem, best solution is to remove the ${D} reference" % package)
                sane = False
            else:
                package_qa_add_message(messages, "expanded-d", "%s in %s recipe contains ${D}, it should be replaced by $D instead" % (var, package))
                sane = False
    return sane

def package_qa_check_encoding(keys, encode, d):
    def check_encoding(key, enc):
        sane = True
        value = d.getVar(key, True)
        if value:
            try:
                s = value.encode(enc)
            except UnicodeDecodeError as e:
                error_msg = "%s has non %s characters" % (key,enc)
                sane = False
                package_qa_handle_error("invalid-chars", error_msg, d)
        return sane

    for key in keys:
        sane = check_encoding(key, encode)
        if not sane:
            break

HOST_USER_UID := "${@os.getuid()}"
HOST_USER_GID := "${@os.getgid()}"

QAPATHTEST[host-user-contaminated] = "package_qa_check_host_user"
def package_qa_check_host_user(path, name, d, elf, messages):
    """Check for paths outside of /home which are owned by the user running bitbake."""

    if not os.path.lexists(path):
        return

    dest = d.getVar('PKGDEST', True)
    pn = d.getVar('PN', True)
    home = os.path.join(dest, 'home')
    if path == home or path.startswith(home + os.sep):
        return

    try:
        stat = os.lstat(path)
    except OSError as exc:
        import errno
        if exc.errno != errno.ENOENT:
            raise
    else:
        rootfs_path = path[len(dest):]
        check_uid = int(d.getVar('HOST_USER_UID', True))
        if stat.st_uid == check_uid:
            package_qa_add_message(messages, "host-user-contaminated", "%s: %s is owned by uid %d, which is the same as the user running bitbake. This may be due to host contamination" % (pn, rootfs_path, check_uid))
            return False

        check_gid = int(d.getVar('HOST_USER_GID', True))
        if stat.st_gid == check_gid:
            package_qa_add_message(messages, "host-user-contaminated", "%s: %s is owned by gid %d, which is the same as the user running bitbake. This may be due to host contamination" % (pn, rootfs_path, check_gid))
            return False
    return True

# The PACKAGE FUNC to scan each package
python do_package_qa () {
    import subprocess
    import oe.packagedata

    bb.note("DO PACKAGE QA")

    bb.build.exec_func("read_subpackage_metadata", d)

    # Check non UTF-8 characters on recipe's metadata
    package_qa_check_encoding(['DESCRIPTION', 'SUMMARY', 'LICENSE', 'SECTION'], 'utf-8', d)

    logdir = d.getVar('T', True)
    pkg = d.getVar('PN', True)

    # Check the compile log for host contamination
    compilelog = os.path.join(logdir,"log.do_compile")

    if os.path.exists(compilelog):
        statement = "grep -e 'CROSS COMPILE Badness:' -e 'is unsafe for cross-compilation' %s > /dev/null" % compilelog
        if subprocess.call(statement, shell=True) == 0:
            msg = "%s: The compile log indicates that host include and/or library paths were used.\n \
        Please check the log '%s' for more information." % (pkg, compilelog)
            package_qa_handle_error("compile-host-path", msg, d)

    # Check the install log for host contamination
    installlog = os.path.join(logdir,"log.do_install")

    if os.path.exists(installlog):
        statement = "grep -e 'CROSS COMPILE Badness:' -e 'is unsafe for cross-compilation' %s > /dev/null" % installlog
        if subprocess.call(statement, shell=True) == 0:
            msg = "%s: The install log indicates that host include and/or library paths were used.\n \
        Please check the log '%s' for more information." % (pkg, installlog)
            package_qa_handle_error("install-host-path", msg, d)

    # Scan the packages...
    pkgdest = d.getVar('PKGDEST', True)
    packages = set((d.getVar('PACKAGES', True) or '').split())

    cpath = oe.cachedpath.CachedPath()
    global pkgfiles
    pkgfiles = {}
    for pkg in packages:
        pkgfiles[pkg] = []
        for walkroot, dirs, files in cpath.walk(pkgdest + "/" + pkg):
            for file in files:
                pkgfiles[pkg].append(walkroot + os.sep + file)

    # no packages should be scanned
    if not packages:
        return

    import re
    # The package name matches the [a-z0-9.+-]+ regular expression
    pkgname_pattern = re.compile("^[a-z0-9.+-]+$")

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    taskdeps = set()
    for dep in taskdepdata:
        taskdeps.add(taskdepdata[dep][0])

    for package in packages:
        def parse_test_matrix(matrix_name):
            testmatrix = d.getVarFlags(matrix_name) or {}
            g = globals()
            warnchecks = []
            for w in (d.getVar("WARN_QA", True) or "").split():
                if w in skip:
                   continue
                if w in testmatrix and testmatrix[w] in g:
                    warnchecks.append(g[testmatrix[w]])
                if w == 'unsafe-references-in-binaries':
                    oe.utils.write_ld_so_conf(d)

            errorchecks = []
            for e in (d.getVar("ERROR_QA", True) or "").split():
                if e in skip:
                   continue
                if e in testmatrix and testmatrix[e] in g:
                    errorchecks.append(g[testmatrix[e]])
                if e == 'unsafe-references-in-binaries':
                    oe.utils.write_ld_so_conf(d)
            return warnchecks, errorchecks

        skip = (d.getVar('INSANE_SKIP_' + package, True) or "").split()
        if skip:
            bb.note("Package %s skipping QA tests: %s" % (package, str(skip)))


        bb.note("Checking Package: %s" % package)
        # Check package name
        if not pkgname_pattern.match(package):
            package_qa_handle_error("pkgname",
                    "%s doesn't match the [a-z0-9.+-]+ regex" % package, d)

        warn_checks, error_checks = parse_test_matrix("QAPATHTEST")
        package_qa_walk(warn_checks, error_checks, skip, package, d)

        warn_checks, error_checks = parse_test_matrix("QAPKGTEST")
        package_qa_package(warn_checks, error_checks, skip, package, d)

        package_qa_check_rdepends(package, pkgdest, skip, taskdeps, packages, d)
        package_qa_check_deps(package, pkgdest, skip, d)

    if 'libdir' in d.getVar("ALL_QA", True).split():
        package_qa_check_libdir(d)

    qa_sane = d.getVar("QA_SANE", True)
    if not qa_sane:
        bb.fatal("QA run found fatal errors. Please consider fixing them.")
    bb.note("DONE with PACKAGE QA")
}

do_package_qa[vardepsexclude] = "BB_TASKDEPDATA"
do_package_qa[rdeptask] = "do_packagedata"
addtask do_package_qa after do_packagedata do_package before do_build

SSTATETASKS += "do_package_qa"
do_package_qa[sstate-inputdirs] = ""
do_package_qa[sstate-outputdirs] = ""
python do_package_qa_setscene () {
    sstate_setscene(d)
}
addtask do_package_qa_setscene

python do_qa_staging() {
    bb.note("QA checking staging")

    if not package_qa_check_staged(d.expand('${SYSROOT_DESTDIR}${libdir}'), d):
        bb.fatal("QA staging was broken by the package built above")
}

python do_qa_configure() {
    import subprocess

    ###########################################################################
    # Check config.log for cross compile issues
    ###########################################################################

    configs = []
    workdir = d.getVar('WORKDIR', True)

    if bb.data.inherits_class('autotools', d):
        bb.note("Checking autotools environment for common misconfiguration")
        for root, dirs, files in os.walk(workdir):
            statement = "grep -q -F -e 'CROSS COMPILE Badness:' -e 'is unsafe for cross-compilation' %s" % \
                        os.path.join(root,"config.log")
            if "config.log" in files:
                if subprocess.call(statement, shell=True) == 0:
                    bb.fatal("""This autoconf log indicates errors, it looked at host include and/or library paths while determining system capabilities.
Rerun configure task after fixing this.""")

            if "configure.ac" in files:
                configs.append(os.path.join(root,"configure.ac"))
            if "configure.in" in files:
                configs.append(os.path.join(root, "configure.in"))

    ###########################################################################
    # Check gettext configuration and dependencies are correct
    ###########################################################################

    cnf = d.getVar('EXTRA_OECONF', True) or ""
    if "gettext" not in d.getVar('P', True) and "gcc-runtime" not in d.getVar('P', True) and "--disable-nls" not in cnf:
        ml = d.getVar("MLPREFIX", True) or ""
        if bb.data.inherits_class('native', d) or bb.data.inherits_class('cross', d) or bb.data.inherits_class('crosssdk', d) or bb.data.inherits_class('nativesdk', d):
            gt = "gettext-native"
        elif bb.data.inherits_class('cross-canadian', d):
            gt = "nativesdk-gettext"
        else:
            gt = "virtual/" + ml + "gettext"
        deps = bb.utils.explode_deps(d.getVar('DEPENDS', True) or "")
        if gt not in deps:
            for config in configs:
                gnu = "grep \"^[[:space:]]*AM_GNU_GETTEXT\" %s >/dev/null" % config
                if subprocess.call(gnu, shell=True) == 0:
                    bb.fatal("""%s required but not in DEPENDS for file %s.
Missing inherit gettext?""" % (gt, config))

    ###########################################################################
    # Check unrecognised configure options (with a white list)
    ###########################################################################
    if bb.data.inherits_class("autotools", d):
        bb.note("Checking configure output for unrecognised options")
        try:
            flag = "WARNING: unrecognized options:"
            log = os.path.join(d.getVar('B', True), 'config.log')
            output = subprocess.check_output(['grep', '-F', flag, log]).decode("utf-8").replace(', ', ' ')
            options = set()
            for line in output.splitlines():
                options |= set(line.partition(flag)[2].split())
            whitelist = set(d.getVar("UNKNOWN_CONFIGURE_WHITELIST", True).split())
            options -= whitelist
            if options:
                pn = d.getVar('PN', True)
                error_msg = pn + ": configure was passed unrecognised options: " + " ".join(options)
                package_qa_handle_error("unknown-configure-option", error_msg, d)
        except subprocess.CalledProcessError:
            pass

    # Check invalid PACKAGECONFIG
    pkgconfig = (d.getVar("PACKAGECONFIG", True) or "").split()
    if pkgconfig:
        pkgconfigflags = d.getVarFlags("PACKAGECONFIG") or {}
        for pconfig in pkgconfig:
            if pconfig not in pkgconfigflags:
                pn = d.getVar('PN', True)
                error_msg = "%s: invalid PACKAGECONFIG: %s" % (pn, pconfig)
                package_qa_handle_error("invalid-packageconfig", error_msg, d)

    qa_sane = d.getVar("QA_SANE", True)
    if not qa_sane:
        bb.fatal("Fatal QA errors found, failing task.")
}

python do_qa_unpack() {
    src_uri = d.getVar('SRC_URI', True)
    s_dir = d.getVar('S', True)
    if src_uri and not os.path.exists(s_dir):
        bb.warn('%s: the directory %s (%s) pointed to by the S variable doesn\'t exist - please set S within the recipe to point to where the source has been unpacked to' % (d.getVar('PN', True), d.getVar('S', False), s_dir))
}

# The Staging Func, to check all staging
#addtask qa_staging after do_populate_sysroot before do_build
do_populate_sysroot[postfuncs] += "do_qa_staging "

# Check broken config.log files, for packages requiring Gettext which
# don't have it in DEPENDS.
#addtask qa_configure after do_configure before do_compile
do_configure[postfuncs] += "do_qa_configure "

# Check does S exist.
do_unpack[postfuncs] += "do_qa_unpack"

python () {
    tests = d.getVar('ALL_QA', True).split()
    if "desktop" in tests:
        d.appendVar("PACKAGE_DEPENDS", " desktop-file-utils-native")

    ###########################################################################
    # Check various variables
    ###########################################################################

    # Checking ${FILESEXTRAPATHS}
    extrapaths = (d.getVar("FILESEXTRAPATHS", True) or "")
    if '__default' not in extrapaths.split(":"):
        msg = "FILESEXTRAPATHS-variable, must always use _prepend (or _append)\n"
        msg += "type of assignment, and don't forget the colon.\n"
        msg += "Please assign it with the format of:\n"
        msg += "  FILESEXTRAPATHS_append := \":${THISDIR}/Your_Files_Path\" or\n"
        msg += "  FILESEXTRAPATHS_prepend := \"${THISDIR}/Your_Files_Path:\"\n"
        msg += "in your bbappend file\n\n"
        msg += "Your incorrect assignment is:\n"
        msg += "%s\n" % extrapaths
        bb.warn(msg)

    overrides = d.getVar('OVERRIDES', True).split(':')
    pn = d.getVar('PN', True)
    if pn in overrides:
        msg = 'Recipe %s has PN of "%s" which is in OVERRIDES, this can result in unexpected behaviour.' % (d.getVar("FILE", True), pn)
        package_qa_handle_error("pn-overrides", msg, d)

    issues = []
    if (d.getVar('PACKAGES', True) or "").split():
        for dep in (d.getVar('QADEPENDS', True) or "").split():
            d.appendVarFlag('do_package_qa', 'depends', " %s:do_populate_sysroot" % dep)
        for var in 'RDEPENDS', 'RRECOMMENDS', 'RSUGGESTS', 'RCONFLICTS', 'RPROVIDES', 'RREPLACES', 'FILES', 'pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm', 'ALLOW_EMPTY':
            if d.getVar(var, False):
                issues.append(var)

        fakeroot_tests = d.getVar('FAKEROOT_QA', True).split()
        if set(tests) & set(fakeroot_tests):
            d.setVarFlag('do_package_qa', 'fakeroot', '1')
            d.appendVarFlag('do_package_qa', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
    else:
        d.setVarFlag('do_package_qa', 'rdeptask', '')
    for i in issues:
        package_qa_handle_error("pkgvarcheck", "%s: Variable %s is set as not being package specific, please fix this." % (d.getVar("FILE", True), i), d)
    qa_sane = d.getVar("QA_SANE", True)
    if not qa_sane:
        bb.fatal("Fatal QA errors found, failing task.")
}
