#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

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
#  -Check if the package name is upper case

# These tests are required to be enabled and pass for Yocto Project Compatible Status
# for a layer. To change this list, please contact the Yocto Project TSC.
CHECKLAYER_REQUIRED_TESTS = "\
    configure-gettext configure-unsafe debug-files dep-cmp expanded-d files-invalid \
    host-user-contaminated incompatible-license infodir installed-vs-shipped invalid-chars \
    invalid-packageconfig la \
    license-checksum license-exception license-exists license-file-missing license-format license-no-generic license-syntax \
    mime mime-xdg missing-update-alternatives multilib obsolete-license \
    packages-list patch-fuzz patch-status perllocalpod perm-config perm-line perm-link \
    pkgconfig pkgvarcheck pkgv-undefined pn-overrides shebang-size src-uri-bad symlink-to-sysroot \
    unhandled-features-check unknown-configure-option unlisted-pkg-lics uppercase-pn useless-rpaths \
    var-undefined virtual-slash xorg-driver-abi"

# Elect whether a given type of error is a warning or error, they may
# have been set by other files.
WARN_QA ?= "32bit-time native-last pep517-backend"
ERROR_QA ?= "\
    already-stripped arch buildpaths build-deps debug-deps dev-deps dev-elf dev-so empty-dirs file-rdeps \
    ldflags libdir missing-ptest rpaths staticdev textrel version-going-backwards \
    ${CHECKLAYER_REQUIRED_TESTS}"

# Add usrmerge QA check based on distro feature
ERROR_QA:append = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', ' usrmerge', '', d)}"
WARN_QA:append:layer-core = " missing-metadata missing-maintainer"

FAKEROOT_QA = "host-user-contaminated"
FAKEROOT_QA[doc] = "QA tests which need to run under fakeroot. If any \
enabled tests are listed here, the do_package_qa task will run under fakeroot."

UNKNOWN_CONFIGURE_OPT_IGNORE ?= "--enable-nls --disable-nls --disable-silent-rules --disable-dependency-tracking --disable-static"

# This is a list of directories that are expected to be empty.
QA_EMPTY_DIRS ?= " \
    /dev/pts \
    /media \
    /proc \
    /run \
    /tmp \
    ${localstatedir}/run \
    ${localstatedir}/volatile \
"
# It is possible to specify why a directory is expected to be empty by defining
# QA_EMPTY_DIRS_RECOMMENDATION:<path>, which will then be included in the error
# message if the directory is not empty. If it is not specified for a directory,
# then "but it is expected to be empty" will be used.

def package_qa_clean_path(path, d, pkg=None):
    """
    Remove redundant paths from the path for display.  If pkg isn't set then
    TMPDIR is stripped, otherwise PKGDEST/pkg is stripped.
    """
    if pkg:
        path = path.replace(os.path.join(d.getVar("PKGDEST"), pkg), "/")
    return path.replace(d.getVar("TMPDIR"), "/").replace("//", "/")

QAPATHTEST[shebang-size] = "package_qa_check_shebang_size"
def package_qa_check_shebang_size(path, name, d, elf):
    global cpath

    if elf or cpath.islink(path) or not cpath.isfile(path):
        return

    try:
        with open(path, 'rb') as f:
            stanza = f.readline(130)
    except IOError:
        return

    if stanza.startswith(b'#!'):
        try:
            stanza.decode("utf-8")
        except UnicodeDecodeError:
            #If it is not a text file, it is not a script
            return

        if len(stanza) > 129:
            oe.qa.handle_error("shebang-size", "%s: %s maximum shebang size exceeded, the maximum size is 128." % (name, package_qa_clean_path(path, d, name)), d)
            return

QAPATHTEST[libexec] = "package_qa_check_libexec"
def package_qa_check_libexec(path,name, d, elf):

    # Skip the case where the default is explicitly /usr/libexec
    libexec = d.getVar('libexecdir')
    if libexec == "/usr/libexec":
        return

    if 'libexec' in path.split(os.path.sep):
        oe.qa.handle_error("libexec", "%s: %s is using libexec please relocate to %s" % (name, package_qa_clean_path(path, d, name), libexec), d)

QAPATHTEST[rpaths] = "package_qa_check_rpath"
def package_qa_check_rpath(file, name, d, elf):
    """
    Check for dangerous RPATHs
    """
    if not elf:
        return

    bad_dirs = [d.getVar('BASE_WORKDIR'), d.getVar('STAGING_DIR_TARGET')]

    phdrs = elf.run_objdump("-p", d)

    import re
    rpath_re = re.compile(r"\s+(?:RPATH|RUNPATH)\s+(.*)")
    for line in phdrs.split("\n"):
        m = rpath_re.match(line)
        if m:
            rpath = m.group(1)
            for dir in bad_dirs:
                if dir in rpath:
                    oe.qa.handle_error("rpaths", "%s: %s contains bad RPATH %s" % (name, package_qa_clean_path(file, d, name), rpath), d)

QAPATHTEST[useless-rpaths] = "package_qa_check_useless_rpaths"
def package_qa_check_useless_rpaths(file, name, d, elf):
    """
    Check for RPATHs that are useless but not dangerous
    """
    def rpath_eq(a, b):
        return os.path.normpath(a) == os.path.normpath(b)

    if not elf:
        return

    libdir = d.getVar("libdir")
    base_libdir = d.getVar("base_libdir")

    phdrs = elf.run_objdump("-p", d)

    import re
    rpath_re = re.compile(r"\s+(?:RPATH|RUNPATH)\s+(.*)")
    for line in phdrs.split("\n"):
        m = rpath_re.match(line)
        if m:
            rpath = m.group(1)
            if rpath_eq(rpath, libdir) or rpath_eq(rpath, base_libdir):
                # The dynamic linker searches both these places anyway.  There is no point in
                # looking there again.
                oe.qa.handle_error("useless-rpaths", "%s: %s contains probably-redundant RPATH %s" % (name, package_qa_clean_path(file, d, name), rpath), d)

QAPATHTEST[dev-so] = "package_qa_check_dev"
def package_qa_check_dev(path, name, d, elf):
    """
    Check for ".so" library symlinks in non-dev packages
    """
    global cpath
    if not name.endswith("-dev") and not name.endswith("-dbg") and not name.endswith("-ptest") and not name.startswith("nativesdk-") and path.endswith(".so") and cpath.islink(path):
        oe.qa.handle_error("dev-so", "non -dev/-dbg/nativesdk- package %s contains symlink .so '%s'" % \
                 (name, package_qa_clean_path(path, d, name)), d)

QAPATHTEST[dev-elf] = "package_qa_check_dev_elf"
def package_qa_check_dev_elf(path, name, d, elf):
    """
    Check that -dev doesn't contain real shared libraries.  The test has to
    check that the file is not a link and is an ELF object as some recipes
    install link-time .so files that are linker scripts.
    """
    global cpath
    if name.endswith("-dev") and path.endswith(".so") and not cpath.islink(path) and elf:
        oe.qa.handle_error("dev-elf", "-dev package %s contains non-symlink .so '%s'" % \
                 (name, package_qa_clean_path(path, d, name)), d)

QAPATHTEST[staticdev] = "package_qa_check_staticdev"
def package_qa_check_staticdev(path, name, d, elf):
    """
    Check for ".a" library in non-staticdev packages
    There are a number of exceptions to this rule, -pic packages can contain
    static libraries, the _nonshared.a belong with their -dev packages and
    libgcc.a, libgcov.a will be skipped in their packages
    """

    if not name.endswith("-pic") and not name.endswith("-staticdev") and not name.endswith("-ptest") and path.endswith(".a") and not path.endswith("_nonshared.a") and not '/usr/lib/debug-static/' in path and not '/.debug-static/' in path:
        oe.qa.handle_error("staticdev", "non -staticdev package contains static .a library: %s path '%s'" % \
                 (name, package_qa_clean_path(path, d, name)), d)

QAPATHTEST[mime] = "package_qa_check_mime"
def package_qa_check_mime(path, name, d, elf):
    """
    Check if package installs mime types to /usr/share/mime/packages
    while no inheriting mime.bbclass
    """

    if d.getVar("datadir") + "/mime/packages" in path and path.endswith('.xml') and not bb.data.inherits_class("mime", d):
        oe.qa.handle_error("mime", "package contains mime types but does not inherit mime: %s path '%s'" % \
                 (name, package_qa_clean_path(path, d, name)), d)

QAPATHTEST[mime-xdg] = "package_qa_check_mime_xdg"
def package_qa_check_mime_xdg(path, name, d, elf):
    """
    Check if package installs desktop file containing MimeType and requires
    mime-types.bbclass to create /usr/share/applications/mimeinfo.cache
    """

    if d.getVar("datadir") + "/applications" in path and path.endswith('.desktop') and not bb.data.inherits_class("mime-xdg", d):
        mime_type_found = False
        try:
            with open(path, 'r') as f:
                for line in f.read().split('\n'):
                    if 'MimeType' in line:
                        mime_type_found = True
                        break;
        except:
            # At least libreoffice installs symlinks with absolute paths that are dangling here.
            # We could implement some magic but for few (one) recipes it is not worth the effort so just warn:
            wstr = "%s cannot open %s - is it a symlink with absolute path?\n" % (name, package_qa_clean_path(path, d, name))
            wstr += "Please check if (linked) file contains key 'MimeType'.\n"
            pkgname = name
            if name == d.getVar('PN'):
                pkgname = '${PN}'
            wstr += "If yes: add \'inhert mime-xdg\' and \'MIME_XDG_PACKAGES += \"%s\"\' / if no add \'INSANE_SKIP:%s += \"mime-xdg\"\' to recipe." % (pkgname, pkgname)
            oe.qa.handle_error("mime-xdg", wstr, d)
        if mime_type_found:
            oe.qa.handle_error("mime-xdg", "%s: contains desktop file with key 'MimeType' but does not inhert mime-xdg: %s" % \
                    (name, package_qa_clean_path(path, d, name)), d)

def package_qa_check_libdir(d):
    """
    Check for wrong library installation paths. For instance, catch
    recipes installing /lib/bar.so when ${base_libdir}="lib32" or
    installing in /usr/lib64 when ${libdir}="/usr/lib"
    """
    import re

    pkgdest = d.getVar('PKGDEST')
    base_libdir = d.getVar("base_libdir") + os.sep
    libdir = d.getVar("libdir") + os.sep
    libexecdir = d.getVar("libexecdir") + os.sep
    exec_prefix = d.getVar("exec_prefix") + os.sep

    messages = []

    # The re's are purposely fuzzy, as some there are some .so.x.y.z files
    # that don't follow the standard naming convention. It checks later
    # that they are actual ELF files
    lib_re = re.compile(r"^/lib.+\.so(\..+)?$")
    exec_re = re.compile(r"^%s.*/lib.+\.so(\..+)?$" % exec_prefix)

    for root, dirs, files in os.walk(pkgdest):
        if root == pkgdest:
            # Skip subdirectories for any packages with libdir in INSANE_SKIP
            skippackages = []
            for package in dirs:
                if 'libdir' in (d.getVar('INSANE_SKIP:' + package) or "").split():
                    bb.note("Package %s skipping libdir QA test" % (package))
                    skippackages.append(package)
                elif d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-file-directory' and package.endswith("-dbg"):
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
                        except (oe.qa.NotELFFileError, FileNotFoundError):
                            pass
                if exec_re.match(rel_path):
                    if libdir not in rel_path and libexecdir not in rel_path:
                        # make sure it's an actual ELF file
                        elf = oe.qa.ELFFile(full_path)
                        try:
                            elf.open()
                            messages.append("%s: found library in wrong location: %s" % (package, rel_path))
                        except (oe.qa.NotELFFileError, FileNotFoundError):
                            pass

    if messages:
        oe.qa.handle_error("libdir", "\n".join(messages), d)

QAPATHTEST[debug-files] = "package_qa_check_dbg"
def package_qa_check_dbg(path, name, d, elf):
    """
    Check for ".debug" files or directories outside of the dbg package
    """

    if not "-dbg" in name and not "-ptest" in name:
        if '.debug' in path.split(os.path.sep):
            oe.qa.handle_error("debug-files", "%s: non debug package contains .debug directory %s" % \
                     (name, package_qa_clean_path(path, d, name)), d)

QAPATHTEST[arch] = "package_qa_check_arch"
def package_qa_check_arch(path,name,d, elf):
    """
    Check if archs are compatible
    """
    import re, oe.elf

    if not elf:
        return

    host_os   = d.getVar('HOST_OS')
    host_arch = d.getVar('HOST_ARCH')
    provides  = d.getVar('PROVIDES')

    if host_arch == "allarch":
        oe.qa.handle_error("arch", "%s: inherits the allarch class, but has architecture-specific binaries %s" % \
            (name, package_qa_clean_path(path, d, name)), d)
        return

    # If this throws an exception, the machine_dict needs expanding
    (expected_machine, expected_osabi, expected_abiversion, expected_littleendian, expected_bits) \
        = oe.elf.machine_dict(d)[host_os][host_arch]

    actual_machine = elf.machine()
    actual_bits = elf.abiSize()
    actual_littleendian = elf.isLittleEndian()

    # BPF don't match the target
    if oe.qa.elf_machine_to_string(actual_machine) == "BPF":
        return

    # These targets have 32-bit userspace but 64-bit kernel, so fudge the expected values
    if (("virtual/kernel" in provides) or bb.data.inherits_class("module", d)) and (host_os in ("linux-gnux32", "linux-muslx32", "linux-gnu_ilp32") or re.match(r'mips64.*32', d.getVar('DEFAULTTUNE'))):
        expected_bits = 64

    # Check the architecture and endiannes of the binary
    if expected_machine != actual_machine:
        oe.qa.handle_error("arch", "Architecture did not match (%s, expected %s) in %s" % \
                 (oe.qa.elf_machine_to_string(actual_machine), oe.qa.elf_machine_to_string(expected_machine), package_qa_clean_path(path, d, name)), d)

    if expected_bits != actual_bits:
        oe.qa.handle_error("arch", "Bit size did not match (%d, expected %d) in %s" % \
                 (actual_bits, expected_bits, package_qa_clean_path(path, d, name)), d)

    if expected_littleendian != actual_littleendian:
        oe.qa.handle_error("arch", "Endiannes did not match (%d, expected %d) in %s" % \
                 (actual_littleendian, expected_littleendian, package_qa_clean_path(path, d, name)), d)
package_qa_check_arch[vardepsexclude] = "DEFAULTTUNE"

QAPATHTEST[desktop] = "package_qa_check_desktop"
def package_qa_check_desktop(path, name, d, elf):
    """
    Run all desktop files through desktop-file-validate.
    """
    if path.endswith(".desktop"):
        desktop_file_validate = os.path.join(d.getVar('STAGING_BINDIR_NATIVE'),'desktop-file-validate')
        output = os.popen("%s %s" % (desktop_file_validate, path))
        # This only produces output on errors
        for l in output:
            oe.qa.handle_error("desktop", "Desktop file issue: " + l.strip(), d)

QAPATHTEST[textrel] = "package_qa_textrel"
def package_qa_textrel(path, name, d, elf):
    """
    Check if the binary contains relocations in .text
    """

    if not elf:
        return

    phdrs = elf.run_objdump("-p", d)

    import re
    textrel_re = re.compile(r"\s+TEXTREL\s+")
    for line in phdrs.split("\n"):
        if textrel_re.match(line):
            path = package_qa_clean_path(path, d, name)
            oe.qa.handle_error("textrel", "%s: ELF binary %s has relocations in .text" % (name, path), d)
            return

QAPATHTEST[ldflags] = "package_qa_hash_style"
def package_qa_hash_style(path, name, d, elf):
    """
    Check if the binary has the right hash style...
    """

    if not elf:
        return

    gnu_hash = "--hash-style=gnu" in d.getVar('LDFLAGS')
    if not gnu_hash:
        gnu_hash = "--hash-style=both" in d.getVar('LDFLAGS')
    if not gnu_hash:
        return

    sane = False
    has_syms = False

    phdrs = elf.run_objdump("-p", d)

    # If this binary has symbols, we expect it to have GNU_HASH too.
    for line in phdrs.split("\n"):
        if "SYMTAB" in line:
            has_syms = True
        if "GNU_HASH" in line or "MIPS_XHASH" in line:
            sane = True
        if ("[mips32]" in line or "[mips64]" in line) and d.getVar('TCLIBC') == "musl":
            sane = True
    if has_syms and not sane:
        path = package_qa_clean_path(path, d, name)
        oe.qa.handle_error("ldflags", "File %s in package %s doesn't have GNU_HASH (didn't pass LDFLAGS?)" % (path, name), d)
package_qa_hash_style[vardepsexclude] = "TCLIBC"


QAPATHTEST[buildpaths] = "package_qa_check_buildpaths"
def package_qa_check_buildpaths(path, name, d, elf):
    """
    Check for build paths inside target files and error if paths are not
    explicitly ignored.
    """
    import stat
    # Ignore symlinks/devs/fifos
    mode = os.lstat(path).st_mode
    if stat.S_ISLNK(mode) or stat.S_ISBLK(mode) or stat.S_ISFIFO(mode) or stat.S_ISCHR(mode) or stat.S_ISSOCK(mode):
        return

    tmpdir = bytes(d.getVar('TMPDIR'), encoding="utf-8")
    with open(path, 'rb') as f:
        file_content = f.read()
        if tmpdir in file_content:
            path = package_qa_clean_path(path, d, name)
            oe.qa.handle_error("buildpaths", "File %s in package %s contains reference to TMPDIR" % (path, name), d)


QAPATHTEST[xorg-driver-abi] = "package_qa_check_xorg_driver_abi"
def package_qa_check_xorg_driver_abi(path, name, d, elf):
    """
    Check that all packages containing Xorg drivers have ABI dependencies
    """

    # Skip dev, dbg or nativesdk packages
    if name.endswith("-dev") or name.endswith("-dbg") or name.startswith("nativesdk-"):
        return

    driverdir = d.expand("${libdir}/xorg/modules/drivers/")
    if driverdir in path and path.endswith(".so"):
        mlprefix = d.getVar('MLPREFIX') or ''
        for rdep in bb.utils.explode_deps(d.getVar('RDEPENDS:' + name) or ""):
            if rdep.startswith("%sxorg-abi-" % mlprefix):
                return
        oe.qa.handle_error("xorg-driver-abi", "Package %s contains Xorg driver (%s) but no xorg-abi- dependencies" % (name, os.path.basename(path)), d)

QAPATHTEST[infodir] = "package_qa_check_infodir"
def package_qa_check_infodir(path, name, d, elf):
    """
    Check that /usr/share/info/dir isn't shipped in a particular package
    """
    infodir = d.expand("${infodir}/dir")

    if infodir in path:
        oe.qa.handle_error("infodir", "The %s file is not meant to be shipped in a particular package." % infodir, d)

QAPATHTEST[symlink-to-sysroot] = "package_qa_check_symlink_to_sysroot"
def package_qa_check_symlink_to_sysroot(path, name, d, elf):
    """
    Check that the package doesn't contain any absolute symlinks to the sysroot.
    """
    global cpath
    if cpath.islink(path):
        target = os.readlink(path)
        if os.path.isabs(target):
            tmpdir = d.getVar('TMPDIR')
            if target.startswith(tmpdir):
                path = package_qa_clean_path(path, d, name)
                oe.qa.handle_error("symlink-to-sysroot", "Symlink %s in %s points to TMPDIR" % (path, name), d)

QAPATHTEST[32bit-time] = "check_32bit_symbols"
def check_32bit_symbols(path, packagename, d, elf):
    """
    Check that ELF files do not use any 32 bit time APIs from glibc.
    """
    thirtytwo_bit_time_archs = {'arm','armeb','mipsarcho32','powerpc','x86'}
    overrides = set(d.getVar('OVERRIDES').split(':'))
    if not (thirtytwo_bit_time_archs & overrides):
        return

    import re
    # This list is manually constructed by searching the image folder of the
    # glibc recipe for __USE_TIME_BITS64.  There is no good way to do this
    # automatically.
    api32 = {
        # /usr/include/time.h
        "clock_getres", "clock_gettime", "clock_nanosleep", "clock_settime",
        "ctime", "ctime_r", "difftime", "gmtime", "gmtime_r", "localtime",
        "localtime_r", "mktime", "nanosleep", "time", "timegm", "timelocal",
        "timer_gettime", "timer_settime", "timespec_get", "timespec_getres",
        # /usr/include/bits/time.h
        "clock_adjtime",
        # /usr/include/signal.h
        "sigtimedwait",
        # /usr/include/sys/time.h
        "adjtime",
        "futimes", "futimesat", "getitimer", "gettimeofday", "lutimes",
        "setitimer", "settimeofday", "utimes",
        # /usr/include/sys/timex.h
        "adjtimex", "ntp_adjtime", "ntp_gettime", "ntp_gettimex",
        # /usr/include/sys/wait.h
        "wait3", "wait4",
        # /usr/include/sys/stat.h
        "fstat", "fstat64", "fstatat", "fstatat64", "futimens", "lstat",
        "lstat64", "stat", "stat64", "utimensat",
        # /usr/include/sys/poll.h
        "ppoll",
        # /usr/include/sys/resource.h
        "getrusage",
        # /usr/include/sys/ioctl.h
        "ioctl",
        # /usr/include/sys/select.h
        "select", "pselect",
        # /usr/include/sys/prctl.h
        "prctl",
        # /usr/include/sys/epoll.h
        "epoll_pwait2",
        # /usr/include/sys/timerfd.h
        "timerfd_gettime", "timerfd_settime",
        # /usr/include/sys/socket.h
        "getsockopt", "recvmmsg", "recvmsg", "sendmmsg", "sendmsg",
        "setsockopt",
        # /usr/include/sys/msg.h
        "msgctl",
        # /usr/include/sys/sem.h
        "semctl", "semtimedop",
        # /usr/include/sys/shm.h
        "shmctl",
        # /usr/include/pthread.h
        "pthread_clockjoin_np", "pthread_cond_clockwait",
        "pthread_cond_timedwait", "pthread_mutex_clocklock",
        "pthread_mutex_timedlock", "pthread_rwlock_clockrdlock",
        "pthread_rwlock_clockwrlock", "pthread_rwlock_timedrdlock",
        "pthread_rwlock_timedwrlock", "pthread_timedjoin_np",
        # /usr/include/semaphore.h
        "sem_clockwait", "sem_timedwait",
        # /usr/include/threads.h
        "cnd_timedwait", "mtx_timedlock", "thrd_sleep",
        # /usr/include/aio.h
        "aio_cancel", "aio_error", "aio_read", "aio_return", "aio_suspend",
        "aio_write", "lio_listio",
        # /usr/include/mqueue.h
        "mq_timedreceive", "mq_timedsend",
        # /usr/include/glob.h
        "glob", "glob64", "globfree", "globfree64",
        # /usr/include/sched.h
        "sched_rr_get_interval",
        # /usr/include/fcntl.h
        "fcntl", "fcntl64",
        # /usr/include/utime.h
        "utime",
        # /usr/include/ftw.h
        "ftw", "ftw64", "nftw", "nftw64",
        # /usr/include/fts.h
        "fts64_children", "fts64_close", "fts64_open", "fts64_read",
        "fts64_set", "fts_children", "fts_close", "fts_open", "fts_read",
        "fts_set",
        # /usr/include/netdb.h
        "gai_suspend",
    }

    ptrn = re.compile(
        r'''
        (?P<value>[\da-fA-F]+) \s+
        (?P<flags>[lgu! ][w ][C ][W ][Ii ][dD ]F) \s+
        (?P<section>\*UND\*) \s+
        (?P<alignment>(?P<size>[\da-fA-F]+)) \s+
        (?P<symbol>
        ''' +
        r'(?P<notag>' + r'|'.join(sorted(api32)) + r')' +
        r'''
        (@+(?P<tag>GLIBC_\d+\.\d+\S*)))
        ''', re.VERBOSE
    )

    # elf is a oe.qa.ELFFile object
    if elf:
        phdrs = elf.run_objdump("-tw", d)
        syms = re.finditer(ptrn, phdrs)
        usedapis = {sym.group('notag') for sym in syms}
        if usedapis:
            elfpath = package_qa_clean_path(path, d, packagename)
            # Remove any .debug dir, heuristic that probably works
            # At this point, any symbol information is stripped into the debug
            # package, so that is the only place we will find them.
            elfpath = elfpath.replace('.debug/', '')
            allowed = "32bit-time" in (d.getVar('INSANE_SKIP') or '').split()
            if not allowed:
                msgformat = elfpath + " uses 32-bit api '%s'"
                for sym in usedapis:
                    oe.qa.handle_error('32bit-time', msgformat % sym, d)
                oe.qa.handle_error('32bit-time', 'Suppress with INSANE_SKIP = "32bit-time"', d)
check_32bit_symbols[vardepsexclude] = "OVERRIDES"

# Check license variables
do_populate_lic[postfuncs] += "populate_lic_qa_checksum"
python populate_lic_qa_checksum() {
    """
    Check for changes in the license files.
    """

    lic_files = d.getVar('LIC_FILES_CHKSUM') or ''
    lic = d.getVar('LICENSE')
    pn = d.getVar('PN')

    if lic == "CLOSED":
        return

    if not lic_files and d.getVar('SRC_URI'):
        oe.qa.handle_error("license-checksum", pn + ": Recipe file fetches files and does not have license file information (LIC_FILES_CHKSUM)", d)

    srcdir = d.getVar('S')
    corebase_licensefile = d.getVar('COREBASE') + "/LICENSE"
    for url in lic_files.split():
        try:
            (type, host, path, user, pswd, parm) = bb.fetch.decodeurl(url)
        except bb.fetch.MalformedUrl:
            oe.qa.handle_error("license-checksum", pn + ": LIC_FILES_CHKSUM contains an invalid URL: " + url, d)
            continue
        srclicfile = os.path.join(srcdir, path)
        if not os.path.isfile(srclicfile):
            oe.qa.handle_error("license-checksum", pn + ": LIC_FILES_CHKSUM points to an invalid file: " + srclicfile, d)
            continue

        if (srclicfile == corebase_licensefile):
            bb.warn("${COREBASE}/LICENSE is not a valid license file, please use '${COMMON_LICENSE_DIR}/MIT' for a MIT License file in LIC_FILES_CHKSUM. This will become an error in the future")

        recipemd5 = parm.get('md5', '')
        beginline, endline = 0, 0
        if 'beginline' in parm:
            beginline = int(parm['beginline'])
        if 'endline' in parm:
            endline = int(parm['endline'])

        if (not beginline) and (not endline):
            md5chksum = bb.utils.md5_file(srclicfile)
            with open(srclicfile, 'r', errors='replace') as f:
                license = f.read().splitlines()
        else:
            with open(srclicfile, 'rb') as f:
                import hashlib
                lineno = 0
                license = []
                try:
                    m = hashlib.new('MD5', usedforsecurity=False)
                except TypeError:
                    m = hashlib.new('MD5')
                for line in f:
                    lineno += 1
                    if (lineno >= beginline):
                        if ((lineno <= endline) or not endline):
                            m.update(line)
                            license.append(line.decode('utf-8', errors='replace').rstrip())
                        else:
                            break
                md5chksum = m.hexdigest()
        if recipemd5 == md5chksum:
            bb.note (pn + ": md5 checksum matched for ", url)
        else:
            if recipemd5:
                msg = pn + ": The LIC_FILES_CHKSUM does not match for " + url
                msg = msg + "\n" + pn + ": The new md5 checksum is " + md5chksum
                max_lines = int(d.getVar('QA_MAX_LICENSE_LINES') or 20)
                if not license or license[-1] != '':
                    # Ensure that our license text ends with a line break
                    # (will be added with join() below).
                    license.append('')
                remove = len(license) - max_lines
                if remove > 0:
                    start = max_lines // 2
                    end = start + remove - 1
                    del license[start:end]
                    license.insert(start, '...')
                msg = msg + "\n" + pn + ": Here is the selected license text:" + \
                        "\n" + \
                        "{:v^70}".format(" beginline=%d " % beginline if beginline else "") + \
                        "\n" + "\n".join(license) + \
                        "{:^^70}".format(" endline=%d " % endline if endline else "")
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
            oe.qa.handle_error("license-checksum", msg, d)

    oe.qa.exit_if_errors(d)
}

def qa_check_staged(path,d):
    """
    Check staged la and pc files for common problems like references to the work
    directory.

    As this is run after every stage we should be able to find the one
    responsible for the errors easily even if we look at every .pc and .la file.
    """

    tmpdir = d.getVar('TMPDIR')
    workdir = os.path.join(tmpdir, "work")
    recipesysroot = d.getVar("RECIPE_SYSROOT")

    if bb.data.inherits_class("native", d) or bb.data.inherits_class("cross", d):
        pkgconfigcheck = workdir
    else:
        pkgconfigcheck = tmpdir

    skip = (d.getVar('INSANE_SKIP') or "").split()
    skip_la = False
    if 'la' in skip:
        bb.note("Recipe %s skipping qa checking: la" % d.getVar('PN'))
        skip_la = True

    skip_pkgconfig = False
    if 'pkgconfig' in skip:
        bb.note("Recipe %s skipping qa checking: pkgconfig" % d.getVar('PN'))
        skip_pkgconfig = True

    skip_shebang_size = False
    if 'shebang-size' in skip:
        bb.note("Recipe %s skipping qa checkking: shebang-size" % d.getVar('PN'))
        skip_shebang_size = True

    # find all .la and .pc files
    # read the content
    # and check for stuff that looks wrong
    for root, dirs, files in os.walk(path):
        for file in files:
            path = os.path.join(root,file)
            if file.endswith(".la") and not skip_la:
                with open(path) as f:
                    file_content = f.read()
                    file_content = file_content.replace(recipesysroot, "")
                    if workdir in file_content:
                        error_msg = "%s failed sanity test (workdir) in path %s" % (file,root)
                        oe.qa.handle_error("la", error_msg, d)
            elif file.endswith(".pc") and not skip_pkgconfig:
                with open(path) as f:
                    file_content = f.read()
                    file_content = file_content.replace(recipesysroot, "")
                    if pkgconfigcheck in file_content:
                        error_msg = "%s failed sanity test (tmpdir) in path %s" % (file,root)
                        oe.qa.handle_error("pkgconfig", error_msg, d)

            if not skip_shebang_size:
                global cpath
                cpath = oe.cachedpath.CachedPath()
                package_qa_check_shebang_size(path, "", d, None)
                cpath = None

# Walk over all files in a directory and call func
def package_qa_walk(checkfuncs, package, d):
    global cpath

    elves = {}
    for path in pkgfiles[package]:
            elf = None
            if cpath.isfile(path) and not cpath.islink(path):
                elf = oe.qa.ELFFile(path)
                try:
                    elf.open()
                    elf.close()
                except oe.qa.NotELFFileError:
                    elf = None
            if elf:
                elves[path] = elf

    def prepopulate_objdump_p(elf, d):
        output = elf.run_objdump("-p", d)
        return (elf.name, output)

    results = oe.utils.multiprocess_launch(prepopulate_objdump_p, elves.values(), d, extraargs=(d,))
    for item in results:
        elves[item[0]].set_objdump("-p", item[1])

    for path in pkgfiles[package]:
        elf = elves.get(path)
        if elf:
            elf.open()
        for func in checkfuncs:
            func(path, package, d, elf)
        if elf:
            elf.close()

def package_qa_check_rdepends(pkg, pkgdest, skip, taskdeps, packages, d):
    # Don't do this check for kernel/module recipes, there aren't too many debug/development
    # packages and you can get false positives e.g. on kernel-module-lirc-dev
    if bb.data.inherits_class("kernel", d) or bb.data.inherits_class("module-base", d):
        return

    if not "-dbg" in pkg and not "packagegroup-" in pkg and not "-image" in pkg:
        localdata = bb.data.createCopy(d)
        localdata.setVar('OVERRIDES', localdata.getVar('OVERRIDES') + ':' + pkg)

        # Now check the RDEPENDS
        rdepends = bb.utils.explode_deps(localdata.getVar('RDEPENDS') or "")

        # Now do the sanity check!!!
        if "build-deps" not in skip:
            def check_rdep(rdep_data, possible_pn):
                if rdep_data and "PN" in rdep_data:
                    possible_pn.add(rdep_data["PN"])
                    return rdep_data["PN"] in taskdeps
                return False

            for rdepend in rdepends:
                if "-dbg" in rdepend and "debug-deps" not in skip:
                    error_msg = "%s rdepends on %s" % (pkg,rdepend)
                    oe.qa.handle_error("debug-deps", error_msg, d)
                if (not "-dev" in pkg and not "-staticdev" in pkg) and rdepend.endswith("-dev") and "dev-deps" not in skip:
                    error_msg = "%s rdepends on %s" % (pkg, rdepend)
                    oe.qa.handle_error("dev-deps", error_msg, d)
                if rdepend not in packages:
                    possible_pn = set()
                    rdep_data = oe.packagedata.read_subpkgdata(rdepend, d)
                    if check_rdep(rdep_data, possible_pn):
                        continue

                    if any(check_rdep(rdep_data, possible_pn) for _, rdep_data in  oe.packagedata.foreach_runtime_provider_pkgdata(d, rdepend)):
                        continue

                    if possible_pn:
                        error_msg = "%s rdepends on %s, but it isn't a build dependency, missing one of %s in DEPENDS or PACKAGECONFIG?" % (pkg, rdepend, ", ".join(possible_pn))
                    else:
                        error_msg = "%s rdepends on %s, but it isn't a build dependency?" % (pkg, rdepend)
                    oe.qa.handle_error("build-deps", error_msg, d)

        if "file-rdeps" not in skip:
            ignored_file_rdeps = set(['/bin/sh', '/usr/bin/env', 'rtld(GNU_HASH)'])
            if bb.utils.contains('DISTRO_FEATURES', 'usrmerge', True, False, d):
                ignored_file_rdeps |= set(['/usr/bin/sh'])
            if bb.data.inherits_class('nativesdk', d):
                ignored_file_rdeps |= set(['/bin/bash', '/usr/bin/perl', 'perl'])
                if bb.utils.contains('DISTRO_FEATURES', 'usrmerge', True, False, d):
                    ignored_file_rdeps |= set(['/usr/bin/bash'])
            # For Saving the FILERDEPENDS
            filerdepends = {}
            rdep_data = oe.packagedata.read_subpkgdata(pkg, d)
            for key in rdep_data:
                if key.startswith("FILERDEPENDS:"):
                    for subkey in bb.utils.explode_deps(rdep_data[key]):
                        if subkey not in ignored_file_rdeps and \
                                not subkey.startswith('perl('):
                            # We already know it starts with FILERDEPENDS_
                            filerdepends[subkey] = key[13:]

            if filerdepends:
                done = rdepends[:]
                # Add the rprovides of itself
                if pkg not in done:
                    done.insert(0, pkg)

                # The python is not a package, but python-core provides it, so
                # skip checking /usr/bin/python if python is in the rdeps, in
                # case there is a RDEPENDS:pkg = "python" in the recipe.
                for py in [ d.getVar('MLPREFIX') + "python", "python" ]:
                    if py in done:
                        filerdepends.pop("/usr/bin/python",None)
                        done.remove(py)
                for rdep in done:
                    # The file dependencies may contain package names, e.g.,
                    # perl
                    filerdepends.pop(rdep,None)

                    for _, rdep_data in oe.packagedata.foreach_runtime_provider_pkgdata(d, rdep, True):
                        for key in rdep_data:
                            if key.startswith("FILERPROVIDES:") or key.startswith("RPROVIDES:"):
                                for subkey in bb.utils.explode_deps(rdep_data[key]):
                                    filerdepends.pop(subkey,None)
                            # Add the files list to the rprovides
                            if key.startswith("FILES_INFO:"):
                                # Use eval() to make it as a dict
                                for subkey in eval(rdep_data[key]):
                                    filerdepends.pop(subkey,None)

                    if not filerdepends:
                        # Break if all the file rdepends are met
                        break
            if filerdepends:
                for key in filerdepends:
                    error_msg = "%s contained in package %s requires %s, but no providers found in RDEPENDS:%s?" % \
                            (filerdepends[key].replace(":%s" % pkg, "").replace("@underscore@", "_"), pkg, key, pkg)
                    oe.qa.handle_error("file-rdeps", error_msg, d)
package_qa_check_rdepends[vardepsexclude] = "OVERRIDES"

def package_qa_check_deps(pkg, pkgdest, d):

    localdata = bb.data.createCopy(d)
    localdata.setVar('OVERRIDES', pkg)

    def check_valid_deps(var):
        try:
            rvar = bb.utils.explode_dep_versions2(localdata.getVar(var) or "")
        except ValueError as e:
            bb.fatal("%s:%s: %s" % (var, pkg, e))
        for dep in rvar:
            for v in rvar[dep]:
                if v and not v.startswith(('< ', '= ', '> ', '<= ', '>=')):
                    error_msg = "%s:%s is invalid: %s (%s)   only comparisons <, =, >, <=, and >= are allowed" % (var, pkg, dep, v)
                    oe.qa.handle_error("dep-cmp", error_msg, d)

    check_valid_deps('RDEPENDS')
    check_valid_deps('RRECOMMENDS')
    check_valid_deps('RSUGGESTS')
    check_valid_deps('RPROVIDES')
    check_valid_deps('RREPLACES')
    check_valid_deps('RCONFLICTS')

QAPKGTEST[usrmerge] = "package_qa_check_usrmerge"
def package_qa_check_usrmerge(pkg, d):
    global cpath
    pkgdest = d.getVar('PKGDEST')
    pkg_dir = pkgdest + os.sep + pkg + os.sep
    merged_dirs = ['bin', 'sbin', 'lib'] + d.getVar('MULTILIB_VARIANTS').split()
    for f in merged_dirs:
        if cpath.exists(pkg_dir + f) and not cpath.islink(pkg_dir + f):
            msg = "%s package is not obeying usrmerge distro feature. /%s should be relocated to /usr." % (pkg, f)
            oe.qa.handle_error("usrmerge", msg, d)
            return

QAPKGTEST[perllocalpod] = "package_qa_check_perllocalpod"
def package_qa_check_perllocalpod(pkg, d):
    """
    Check that the recipe didn't ship a perlocal.pod file, which shouldn't be
    installed in a distribution package.  cpan.bbclass sets NO_PERLLOCAL=1 to
    handle this for most recipes.
    """
    import glob
    pkgd = oe.path.join(d.getVar('PKGDEST'), pkg)
    podpath = oe.path.join(pkgd, d.getVar("libdir"), "perl*", "*", "*", "perllocal.pod")

    matches = glob.glob(podpath)
    if matches:
        matches = [package_qa_clean_path(path, d, pkg) for path in matches]
        msg = "%s contains perllocal.pod (%s), should not be installed" % (pkg, " ".join(matches))
        oe.qa.handle_error("perllocalpod", msg, d)

QAPKGTEST[expanded-d] = "package_qa_check_expanded_d"
def package_qa_check_expanded_d(package, d):
    """
    Check for the expanded D (${D}) value in pkg_* and FILES
    variables, warn the user to use it correctly.
    """
    expanded_d = d.getVar('D')

    for var in 'FILES','pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm':
        bbvar = d.getVar(var + ":" + package) or ""
        if expanded_d in bbvar:
            if var == 'FILES':
                oe.qa.handle_error("expanded-d", "FILES in %s recipe should not contain the ${D} variable as it references the local build directory not the target filesystem, best solution is to remove the ${D} reference" % package, d)
            else:
                oe.qa.handle_error("expanded-d", "%s in %s recipe contains ${D}, it should be replaced by $D instead" % (var, package), d)

QAPKGTEST[unlisted-pkg-lics] = "package_qa_check_unlisted_pkg_lics"
def package_qa_check_unlisted_pkg_lics(package, d):
    """
    Check that all licenses for a package are among the licenses for the recipe.
    """
    pkg_lics = d.getVar('LICENSE:' + package)
    if not pkg_lics:
        return

    recipe_lics_set = oe.license.list_licenses(d.getVar('LICENSE'))
    package_lics = oe.license.list_licenses(pkg_lics)
    unlisted = package_lics - recipe_lics_set
    if unlisted:
        oe.qa.handle_error("unlisted-pkg-lics",
                               "LICENSE:%s includes licenses (%s) that are not "
                               "listed in LICENSE" % (package, ' '.join(unlisted)), d)
    obsolete = set(oe.license.obsolete_license_list()) & package_lics - recipe_lics_set
    if obsolete:
        oe.qa.handle_error("obsolete-license",
                               "LICENSE:%s includes obsolete licenses %s" % (package, ' '.join(obsolete)), d)

QAPKGTEST[empty-dirs] = "package_qa_check_empty_dirs"
def package_qa_check_empty_dirs(pkg, d):
    """
    Check for the existence of files in directories that are expected to be
    empty.
    """

    global cpath
    pkgd = oe.path.join(d.getVar('PKGDEST'), pkg)
    for dir in (d.getVar('QA_EMPTY_DIRS') or "").split():
        empty_dir = oe.path.join(pkgd, dir)
        if cpath.exists(empty_dir) and os.listdir(empty_dir):
            recommendation = (d.getVar('QA_EMPTY_DIRS_RECOMMENDATION:' + dir) or
                              "but it is expected to be empty")
            msg = "%s installs files in %s, %s" % (pkg, dir, recommendation)
            oe.qa.handle_error("empty-dirs", msg, d)

def package_qa_check_encoding(keys, encode, d):
    def check_encoding(key, enc):
        sane = True
        value = d.getVar(key)
        if value:
            try:
                s = value.encode(enc)
            except UnicodeDecodeError as e:
                error_msg = "%s has non %s characters" % (key,enc)
                sane = False
                oe.qa.handle_error("invalid-chars", error_msg, d)
        return sane

    for key in keys:
        sane = check_encoding(key, encode)
        if not sane:
            break

HOST_USER_UID := "${@os.getuid()}"
HOST_USER_GID := "${@os.getgid()}"

QAPATHTEST[host-user-contaminated] = "package_qa_check_host_user"
def package_qa_check_host_user(path, name, d, elf):
    """Check for paths outside of /home which are owned by the user running bitbake."""
    global cpath

    if not cpath.lexists(path):
        return

    dest = d.getVar('PKGDEST')
    pn = d.getVar('PN')
    home = os.path.join(dest, name, 'home')
    if path == home or path.startswith(home + os.sep):
        return

    try:
        stat = os.lstat(path)
    except OSError as exc:
        import errno
        if exc.errno != errno.ENOENT:
            raise
    else:
        check_uid = int(d.getVar('HOST_USER_UID'))
        if stat.st_uid == check_uid:
            oe.qa.handle_error("host-user-contaminated", "%s: %s is owned by uid %d, which is the same as the user running bitbake. This may be due to host contamination" % (pn, package_qa_clean_path(path, d, name), check_uid), d)

        check_gid = int(d.getVar('HOST_USER_GID'))
        if stat.st_gid == check_gid:
            oe.qa.handle_error("host-user-contaminated", "%s: %s is owned by gid %d, which is the same as the user running bitbake. This may be due to host contamination" % (pn, package_qa_clean_path(path, d, name), check_gid), d)

QARECIPETEST[unhandled-features-check] = "package_qa_check_unhandled_features_check"
def package_qa_check_unhandled_features_check(pn, d):
    if not bb.data.inherits_class('features_check', d):
        var_set = False
        for kind in ['DISTRO', 'MACHINE', 'COMBINED']:
            for var in ['ANY_OF_' + kind + '_FEATURES', 'REQUIRED_' + kind + '_FEATURES', 'CONFLICT_' + kind + '_FEATURES']:
                if d.getVar(var) is not None or d.hasOverrides(var):
                    var_set = True
        if var_set:
            oe.qa.handle_error("unhandled-features-check", "%s: recipe doesn't inherit features_check" % pn, d)

QARECIPETEST[missing-update-alternatives] = "package_qa_check_missing_update_alternatives"
def package_qa_check_missing_update_alternatives(pn, d):
    # Look at all packages and find out if any of those sets ALTERNATIVE variable
    # without inheriting update-alternatives class
    for pkg in (d.getVar('PACKAGES') or '').split():
        if d.getVar('ALTERNATIVE:%s' % pkg) and not bb.data.inherits_class('update-alternatives', d):
            oe.qa.handle_error("missing-update-alternatives", "%s: recipe defines ALTERNATIVE:%s but doesn't inherit update-alternatives. This might fail during do_rootfs later!" % (pn, pkg), d)

def parse_test_matrix(matrix_name, skip, d):
        testmatrix = d.getVarFlags(matrix_name) or {}
        g = globals()
        checks = []
        for w in (d.getVar("WARN_QA") or "").split():
            if w in skip:
               continue
            if w in testmatrix and testmatrix[w] in g:
                checks.append(g[testmatrix[w]])

        for e in (d.getVar("ERROR_QA") or "").split():
            if e in skip:
               continue
            if e in testmatrix and testmatrix[e] in g:
                checks.append(g[testmatrix[e]])
        return checks
parse_test_matrix[vardepsexclude] = "ERROR_QA WARN_QA"


# The PACKAGE FUNC to scan each package
python do_package_qa () {
    import oe.packagedata

    # Check for obsolete license references in main LICENSE (packages are checked below for any changes)
    main_licenses = oe.license.list_licenses(d.getVar('LICENSE'))
    obsolete = set(oe.license.obsolete_license_list()) & main_licenses
    if obsolete:
        oe.qa.handle_error("obsolete-license", "Recipe LICENSE includes obsolete licenses %s" % ' '.join(obsolete), d)

    bb.build.exec_func("read_subpackage_metadata", d)

    # Check non UTF-8 characters on recipe's metadata
    package_qa_check_encoding(['DESCRIPTION', 'SUMMARY', 'LICENSE', 'SECTION'], 'utf-8', d)

    logdir = d.getVar('T')
    pn = d.getVar('PN')

    # Scan the packages...
    packages = set((d.getVar('PACKAGES') or '').split())
    # no packages should be scanned
    if not packages:
        return

    global pkgfiles, cpath
    pkgfiles = {}
    cpath = oe.cachedpath.CachedPath()
    pkgdest = d.getVar('PKGDEST')
    for pkg in packages:
        pkgdir = os.path.join(pkgdest, pkg)
        pkgfiles[pkg] = []
        for walkroot, dirs, files in os.walk(pkgdir):
            # Don't walk into top-level CONTROL or DEBIAN directories as these
            # are temporary directories created by do_package.
            if walkroot == pkgdir:
                for removedir in ("CONTROL", "DEBIAN"):
                    try:
                        dirs.remove(removedir)
                    except ValueError:
                        pass
            pkgfiles[pkg].extend((os.path.join(walkroot, f) for f in files))

    import re
    # The package name matches the [a-z0-9.+-]+ regular expression
    pkgname_pattern = re.compile(r"^[a-z0-9.+-]+$")

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    taskdeps = set()
    for dep in taskdepdata:
        taskdeps.add(taskdepdata[dep][0])

    for package in packages:
        skip = set((d.getVar('INSANE_SKIP') or "").split() +
                   (d.getVar('INSANE_SKIP:' + package) or "").split())
        if skip:
            bb.note("Package %s skipping QA tests: %s" % (package, str(skip)))

        bb.note("Checking Package: %s" % package)
        # Check package name
        if not pkgname_pattern.match(package):
            oe.qa.handle_error("pkgname",
                    "%s doesn't match the [a-z0-9.+-]+ regex" % package, d)

        checks = parse_test_matrix("QAPATHTEST", skip, d)
        package_qa_walk(checks, package, d)

        checks = parse_test_matrix("QAPKGTEST", skip, d)
        for func in checks:
            func(package, d)

        package_qa_check_rdepends(package, pkgdest, skip, taskdeps, packages, d)
        package_qa_check_deps(package, pkgdest, d)

    checks = parse_test_matrix("QARECIPETEST", skip, d)
    for func in checks:
        func(pn, d)

    package_qa_check_libdir(d)

    cpath = None
    oe.qa.exit_if_errors(d)
}

# binutils is used for most checks, so need to set as dependency
# POPULATESYSROOTDEPS is defined in staging class.
do_package_qa[depends] += "${POPULATESYSROOTDEPS}"
do_package_qa[vardeps] = "${@bb.utils.contains('ERROR_QA', 'empty-dirs', 'QA_EMPTY_DIRS', '', d)}"
do_package_qa[vardepsexclude] = "BB_TASKDEPDATA"
do_package_qa[rdeptask] = "do_packagedata"
addtask do_package_qa after do_packagedata do_package before do_build

do_build[rdeptask] += "do_package_qa"

# Add the package specific INSANE_SKIPs to the sstate dependencies
python() {
    pkgs = (d.getVar('PACKAGES') or '').split()
    for pkg in pkgs:
        d.appendVarFlag("do_package_qa", "vardeps", " INSANE_SKIP:{}".format(pkg))
    funcs = d.getVarFlags("QAPATHTEST")
    funcs.update(d.getVarFlags("QAPKGTEST"))
    funcs.update(d.getVarFlags("QARECIPETEST"))
    d.appendVarFlag("do_package_qa", "vardeps", " ".join(funcs.values()))
}

SSTATETASKS += "do_package_qa"
do_package_qa[sstate-inputdirs] = ""
do_package_qa[sstate-outputdirs] = ""
python do_package_qa_setscene () {
    sstate_setscene(d)
}
addtask do_package_qa_setscene

python do_qa_sysroot() {
    bb.note("QA checking do_populate_sysroot")
    sysroot_destdir = d.expand('${SYSROOT_DESTDIR}')
    for sysroot_dir in d.expand('${SYSROOT_DIRS}').split():
        qa_check_staged(sysroot_destdir + sysroot_dir, d)
    oe.qa.exit_with_message_if_errors("do_populate_sysroot for this recipe installed files with QA issues", d)
}
do_populate_sysroot[postfuncs] += "do_qa_sysroot"

python do_qa_patch() {
    import subprocess

    ###########################################################################
    # Check patch.log for fuzz warnings
    #
    # Further information on why we check for patch fuzz warnings:
    # http://lists.openembedded.org/pipermail/openembedded-core/2018-March/148675.html
    # https://bugzilla.yoctoproject.org/show_bug.cgi?id=10450
    ###########################################################################

    logdir = d.getVar('T')
    patchlog = os.path.join(logdir,"log.do_patch")

    if os.path.exists(patchlog):
        fuzzheader = '--- Patch fuzz start ---'
        fuzzfooter = '--- Patch fuzz end ---'
        statement = "grep -e '%s' %s > /dev/null" % (fuzzheader, patchlog)
        if subprocess.call(statement, shell=True) == 0:
            msg = "Fuzz detected:\n\n"
            fuzzmsg = ""
            inFuzzInfo = False
            f = open(patchlog, "r")
            for line in f:
                if fuzzheader in line:
                    inFuzzInfo = True
                    fuzzmsg = ""
                elif fuzzfooter in line:
                    fuzzmsg = fuzzmsg.replace('\n\n', '\n')
                    msg += fuzzmsg
                    msg += "\n"
                    inFuzzInfo = False
                elif inFuzzInfo and not 'Now at patch' in line:
                    fuzzmsg += line
            f.close()
            msg += "The context lines in the patches can be updated with devtool:\n"
            msg += "\n"
            msg += "    devtool modify %s\n" % d.getVar('PN')
            msg += "    devtool finish --force-patch-refresh %s <layer_path>\n\n" % d.getVar('PN')
            msg += "Don't forget to review changes done by devtool!\n"
            msg += "\nPatch log indicates that patches do not apply cleanly."
            oe.qa.handle_error("patch-fuzz", msg, d)

    # Check if the patch contains a correctly formatted and spelled Upstream-Status
    import re
    from oe import patch

    for url in patch.src_patches(d):
        (_, _, fullpath, _, _, _) = bb.fetch.decodeurl(url)

        msg = oe.qa.check_upstream_status(fullpath)
        if msg:
            oe.qa.handle_error("patch-status", msg, d)

    ###########################################################################
    # Check for missing ptests
    ###########################################################################
    def match_line_in_files(toplevel, filename_glob, line_regex):
        import pathlib
        try:
            toppath = pathlib.Path(toplevel)
            for entry in toppath.glob(filename_glob):
                try:
                    with open(entry, 'r', encoding='utf-8', errors='ignore') as f:
                        for line in f.readlines():
                            if re.match(line_regex, line):
                                return True
                except FileNotFoundError:
                    # Broken symlink in source
                    pass
        except FileNotFoundError:
            # pathlib.Path.glob() might throw this when file/directory
            # disappear while scanning.
            bb.note("unimplemented-ptest: FileNotFoundError exception while scanning (disappearing file while scanning?). Check was ignored." % d.getVar('PN'))
            pass
        return False

    srcdir = d.getVar('S')
    if not bb.utils.contains('DISTRO_FEATURES', 'ptest', True, False, d):
        pass
    elif not (bb.utils.contains('ERROR_QA', 'unimplemented-ptest', True, False, d) or bb.utils.contains('WARN_QA', 'unimplemented-ptest', True, False, d)):
        pass
    elif bb.data.inherits_class('ptest', d):
        bb.note("Package %s QA: skipping unimplemented-ptest: ptest implementation detected" % d.getVar('PN'))

    # Detect perl Test:: based tests
    elif os.path.exists(os.path.join(srcdir, "t")) and any(filename.endswith('.t') for filename in os.listdir(os.path.join(srcdir, 't'))):
        oe.qa.handle_error("unimplemented-ptest", "%s: perl Test:: based tests detected" % d.getVar('PN'), d)

    # Detect pytest-based tests
    elif match_line_in_files(srcdir, "**/*.py", r'\s*(?:import\s*pytest|from\s*pytest)'):
        oe.qa.handle_error("unimplemented-ptest", "%s: pytest-based tests detected" % d.getVar('PN'), d)

    # Detect meson-based tests
    elif os.path.exists(os.path.join(srcdir, "meson.build")) and match_line_in_files(srcdir, "**/meson.build", r'\s*test\s*\('):
        oe.qa.handle_error("unimplemented-ptest", "%s: meson-based tests detected" % d.getVar('PN'), d)

    # Detect cmake-based tests
    elif os.path.exists(os.path.join(srcdir, "CMakeLists.txt")) and match_line_in_files(srcdir, "**/CMakeLists.txt", r'\s*(?:add_test|enable_testing)\s*\('):
        oe.qa.handle_error("unimplemented-ptest", "%s: cmake-based tests detected" % d.getVar('PN'), d)

    # Detect autotools-based·tests
    elif os.path.exists(os.path.join(srcdir, "Makefile.in")) and (match_line_in_files(srcdir, "**/Makefile.in", r'\s*TESTS\s*\+?=') or match_line_in_files(srcdir,"**/*.at",r'.*AT_INIT')):
        oe.qa.handle_error("unimplemented-ptest", "%s: autotools-based tests detected" % d.getVar('PN'), d)

    # Last resort, detect a test directory in sources
    elif os.path.exists(srcdir) and any(filename.lower() in ["test", "tests"] for filename in os.listdir(srcdir)):
        oe.qa.handle_error("unimplemented-ptest", "%s: test subdirectory detected" % d.getVar('PN'), d)

    oe.qa.exit_if_errors(d)
}

python do_qa_configure() {
    import subprocess

    ###########################################################################
    # Check config.log for cross compile issues
    ###########################################################################

    configs = []
    workdir = d.getVar('WORKDIR')

    skip = (d.getVar('INSANE_SKIP') or "").split()
    skip_configure_unsafe = False
    if 'configure-unsafe' in skip:
        bb.note("Recipe %s skipping qa checking: configure-unsafe" % d.getVar('PN'))
        skip_configure_unsafe = True

    if bb.data.inherits_class('autotools', d) and not skip_configure_unsafe:
        bb.note("Checking autotools environment for common misconfiguration")
        for root, dirs, files in os.walk(workdir):
            statement = "grep -q -F -e 'is unsafe for cross-compilation' %s" % \
                        os.path.join(root,"config.log")
            if "config.log" in files:
                if subprocess.call(statement, shell=True) == 0:
                    error_msg = """This autoconf log indicates errors, it looked at host include and/or library paths while determining system capabilities.
Rerun configure task after fixing this."""
                    oe.qa.handle_error("configure-unsafe", error_msg, d)

            if "configure.ac" in files:
                configs.append(os.path.join(root,"configure.ac"))
            if "configure.in" in files:
                configs.append(os.path.join(root, "configure.in"))

    ###########################################################################
    # Check gettext configuration and dependencies are correct
    ###########################################################################

    skip_configure_gettext = False
    if 'configure-gettext' in skip:
        bb.note("Recipe %s skipping qa checking: configure-gettext" % d.getVar('PN'))
        skip_configure_gettext = True

    cnf = d.getVar('EXTRA_OECONF') or ""
    if not ("gettext" in d.getVar('P') or "gcc-runtime" in d.getVar('P') or \
            "--disable-nls" in cnf or skip_configure_gettext):
        ml = d.getVar("MLPREFIX") or ""
        if bb.data.inherits_class('cross-canadian', d):
            gt = "nativesdk-gettext"
        else:
            gt = "gettext-native"
        deps = bb.utils.explode_deps(d.getVar('DEPENDS') or "")
        if gt not in deps:
            for config in configs:
                gnu = "grep \"^[[:space:]]*AM_GNU_GETTEXT\" %s >/dev/null" % config
                if subprocess.call(gnu, shell=True) == 0:
                    error_msg = "AM_GNU_GETTEXT used but no inherit gettext"
                    oe.qa.handle_error("configure-gettext", error_msg, d)

    ###########################################################################
    # Check unrecognised configure options (with a white list)
    ###########################################################################
    if bb.data.inherits_class("autotools", d):
        bb.note("Checking configure output for unrecognised options")
        try:
            if bb.data.inherits_class("autotools", d):
                flag = "WARNING: unrecognized options:"
                log = os.path.join(d.getVar('B'), 'config.log')
            output = subprocess.check_output(['grep', '-F', flag, log]).decode("utf-8").replace(', ', ' ').replace('"', '')
            options = set()
            for line in output.splitlines():
                options |= set(line.partition(flag)[2].split())
            ignore_opts = set(d.getVar("UNKNOWN_CONFIGURE_OPT_IGNORE").split())
            options -= ignore_opts
            if options:
                pn = d.getVar('PN')
                error_msg = pn + ": configure was passed unrecognised options: " + " ".join(options)
                oe.qa.handle_error("unknown-configure-option", error_msg, d)
        except subprocess.CalledProcessError:
            pass

    # Check invalid PACKAGECONFIG
    pkgconfig = (d.getVar("PACKAGECONFIG") or "").split()
    if pkgconfig:
        pkgconfigflags = d.getVarFlags("PACKAGECONFIG") or {}
        for pconfig in pkgconfig:
            if pconfig not in pkgconfigflags:
                pn = d.getVar('PN')
                error_msg = "%s: invalid PACKAGECONFIG: %s" % (pn, pconfig)
                oe.qa.handle_error("invalid-packageconfig", error_msg, d)

    oe.qa.exit_if_errors(d)
}

python do_qa_unpack() {
    src_uri = d.getVar('SRC_URI')
    s_dir = d.getVar('S')
    if src_uri and not os.path.exists(s_dir):
        bb.warn('%s: the directory %s (%s) pointed to by the S variable doesn\'t exist - please set S within the recipe to point to where the source has been unpacked to' % (d.getVar('PN'), d.getVar('S', False), s_dir))
}

python do_recipe_qa() {
    import re

    def test_missing_metadata(pn, d):
        fn = d.getVar("FILE")
        srcfile = d.getVar('SRC_URI').split()
        # Check that SUMMARY is not the same as the default from bitbake.conf
        if d.getVar('SUMMARY') == d.expand("${PN} version ${PV}-${PR}"):
            oe.qa.handle_error("missing-metadata", "Recipe {} in {} does not contain a SUMMARY. Please add an entry.".format(pn, fn), d)
        if not d.getVar('HOMEPAGE'):
            if srcfile and srcfile[0].startswith('file') or not d.getVar('SRC_URI'):
                # We are only interested in recipes SRC_URI fetched from external sources
                pass
            else:
                oe.qa.handle_error("missing-metadata", "Recipe {} in {} does not contain a HOMEPAGE. Please add an entry.".format(pn, fn), d)

    def test_missing_maintainer(pn, d):
        fn = d.getVar("FILE")
        if pn.endswith("-native") or pn.startswith("nativesdk-") or "packagegroup-" in pn or "core-image-ptest-" in pn:
            return
        if not d.getVar('RECIPE_MAINTAINER'):
            oe.qa.handle_error("missing-maintainer", "Recipe {} in {} does not have an assigned maintainer. Please add an entry into meta/conf/distro/include/maintainers.inc.".format(pn, fn), d)

    def test_srcuri(pn, d):
        skip = (d.getVar('INSANE_SKIP') or "").split()
        if 'src-uri-bad' in skip:
            bb.note("Recipe %s skipping qa checking: src-uri-bad" % pn)
            return

        if "${PN}" in d.getVar("SRC_URI", False):
            oe.qa.handle_error("src-uri-bad", "%s: SRC_URI uses PN not BPN" % pn, d)

        for url in d.getVar("SRC_URI").split():
            # Search for github and gitlab URLs that pull unstable archives (comment for future greppers)
            if re.search(r"git(hu|la)b\.com/.+/.+/archive/.+", url) or "//codeload.github.com/" in url:
                oe.qa.handle_error("src-uri-bad", "%s: SRC_URI uses unstable GitHub/GitLab archives, convert recipe to use git protocol" % pn, d)

    pn = d.getVar('PN')
    test_missing_metadata(pn, d)
    test_missing_maintainer(pn, d)
    test_srcuri(pn, d)
    oe.qa.exit_if_errors(d)
}

addtask do_recipe_qa before do_fetch do_package_qa do_build

SSTATETASKS += "do_recipe_qa"
do_recipe_qa[sstate-inputdirs] = ""
do_recipe_qa[sstate-outputdirs] = ""
python do_recipe_qa_setscene () {
    sstate_setscene(d)
}
addtask do_recipe_qa_setscene

# Check for patch fuzz
do_patch[postfuncs] += "do_qa_patch "

# Check broken config.log files, for packages requiring Gettext which
# don't have it in DEPENDS.
#addtask qa_configure after do_configure before do_compile
do_configure[postfuncs] += "do_qa_configure "

# Check does S exist.
do_unpack[postfuncs] += "do_qa_unpack"

python () {
    import re

    if bb.utils.contains('ERROR_QA', 'desktop', True, False, d) or bb.utils.contains('WARN_QA', 'desktop', True, False, d):
        d.appendVar("PACKAGE_DEPENDS", " desktop-file-utils-native")

    ###########################################################################
    # Check various variables
    ###########################################################################

    # Checking ${FILESEXTRAPATHS}
    extrapaths = (d.getVar("FILESEXTRAPATHS") or "")
    if '__default' not in extrapaths.split(":"):
        msg = "FILESEXTRAPATHS-variable, must always use :prepend (or :append)\n"
        msg += "type of assignment, and don't forget the colon.\n"
        msg += "Please assign it with the format of:\n"
        msg += "  FILESEXTRAPATHS:append := \":${THISDIR}/Your_Files_Path\" or\n"
        msg += "  FILESEXTRAPATHS:prepend := \"${THISDIR}/Your_Files_Path:\"\n"
        msg += "in your bbappend file\n\n"
        msg += "Your incorrect assignment is:\n"
        msg += "%s\n" % extrapaths
        bb.warn(msg)

    overrides = d.getVar('OVERRIDES').split(':')
    pn = d.getVar('PN')
    if pn in overrides:
        msg = 'Recipe %s has PN of "%s" which is in OVERRIDES, this can result in unexpected behaviour.' % (d.getVar("FILE"), pn)
        oe.qa.handle_error("pn-overrides", msg, d)
    prog = re.compile(r'[A-Z]')
    if prog.search(pn):
        oe.qa.handle_error("uppercase-pn", 'PN: %s is upper case, this can result in unexpected behavior.' % pn, d)

    sourcedir = d.getVar("S")
    builddir = d.getVar("B")
    workdir = d.getVar("WORKDIR")
    unpackdir = d.getVar("UNPACKDIR")
    if sourcedir == workdir:
        bb.fatal("Using S = ${WORKDIR} is no longer supported")
    if builddir == workdir:
        bb.fatal("Using B = ${WORKDIR} is no longer supported")
    if unpackdir == workdir:
        bb.fatal("Using UNPACKDIR = ${WORKDIR} is not supported")
    if sourcedir[-1] == '/':
        bb.warn("Recipe %s sets S variable with trailing slash '%s', remove it" % (d.getVar("PN"), d.getVar("S")))
    if builddir[-1] == '/':
        bb.warn("Recipe %s sets B variable with trailing slash '%s', remove it" % (d.getVar("PN"), d.getVar("B")))

    # Some people mistakenly use DEPENDS:${PN} instead of DEPENDS and wonder
    # why it doesn't work.
    if (d.getVar(d.expand('DEPENDS:${PN}'))):
        oe.qa.handle_error("pkgvarcheck", "recipe uses DEPENDS:${PN}, should use DEPENDS", d)

    # virtual/ is meaningless for these variables
    for k in ['RDEPENDS', 'RPROVIDES']:
        for var in bb.utils.explode_deps(d.getVar(k + ':' + pn) or ""):
            if var.startswith("virtual/"):
                oe.qa.handle_error("virtual-slash", "%s is set to %s but the substring 'virtual/' holds no meaning in this context. It only works for build time dependencies, not runtime ones. It is suggested to use 'VIRTUAL-RUNTIME_' variables instead." % (k, var), d)

    issues = []
    if (d.getVar('PACKAGES') or "").split():
        for dep in (d.getVar('QADEPENDS') or "").split():
            d.appendVarFlag('do_package_qa', 'depends', " %s:do_populate_sysroot" % dep)
        for var in 'RDEPENDS', 'RRECOMMENDS', 'RSUGGESTS', 'RCONFLICTS', 'RPROVIDES', 'RREPLACES', 'FILES', 'pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm', 'ALLOW_EMPTY':
            if d.getVar(var, False):
                issues.append(var)

        if bb.utils.contains('ERROR_QA', 'host-user-contaminated', True, False, d) or bb.utils.contains('WARN_QA', 'host-user-contaminated', True, False, d):
            d.setVarFlag('do_package_qa', 'fakeroot', '1')
            d.appendVarFlag('do_package_qa', 'depends', ' virtual/fakeroot-native:do_populate_sysroot')
    else:
        d.setVarFlag('do_package_qa', 'rdeptask', '')
    for i in issues:
        oe.qa.handle_error("pkgvarcheck", "%s: Variable %s is set as not being package specific, please fix this." % (d.getVar("FILE"), i), d)

    if 'native-last' not in (d.getVar('INSANE_SKIP') or "").split():
        for native_class in ['native', 'nativesdk']:
            if bb.data.inherits_class(native_class, d):

                inherited_classes = d.getVar('__inherit_cache', False) or []
                needle = "/" + native_class

                bbclassextend = (d.getVar('BBCLASSEXTEND') or '').split()
                # BBCLASSEXTEND items are always added in the end
                skip_classes = bbclassextend
                if bb.data.inherits_class('native', d) or 'native' in bbclassextend:
                    # native also inherits nopackages and relocatable bbclasses
                    skip_classes.extend(['nopackages', 'relocatable'])

                broken_order = []
                for class_item in reversed(inherited_classes):
                    if needle not in class_item:
                        for extend_item in skip_classes:
                            if '/%s.bbclass' % extend_item in class_item:
                                break
                        else:
                            pn = d.getVar('PN')
                            broken_order.append(os.path.basename(class_item))
                    else:
                        break
                if broken_order:
                    oe.qa.handle_error("native-last", "%s: native/nativesdk class is not inherited last, this can result in unexpected behaviour. "
                                             "Classes inherited after native/nativesdk: %s" % (pn, " ".join(broken_order)), d)

    oe.qa.exit_if_errors(d)
}
