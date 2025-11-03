inherit qemu

TOOLCHAIN_TEST_TARGET ??= "user"
TOOLCHAIN_TEST_HOST ??= "localhost"
TOOLCHAIN_TEST_HOST_USER ??= "root"
TOOLCHAIN_TEST_HOST_PORT ??= "2222"

MAKE_CHECK_BOARDFLAGS ??= ""
MAKE_CHECK_BOARDARGS ??= "--target_board=${TOOLCHAIN_TEST_TARGET}${MAKE_CHECK_BOARDFLAGS}"

python () {
    # Provide the targets compiler args via targets options. This allows dejagnu to
    # correctly mark incompatible tests as UNSUPPORTED (e.g. needs soft-float
    # but running on hard-float target).
    #
    # These options are called "multilib_flags" within the gcc test suite. Most
    # architectures handle these options in a sensible way such that tests that
    # are incompatible with the provided multilib are marked as UNSUPPORTED.
    #
    # Note: multilib flags are added to the compile command after the args
    # provided by any test (through dg-options), CFLAGS_FOR_TARGET is always
    # added to the compile command before any other args but is not interpted
    # as options like multilib flags.
    #
    # i686, x86-64 and aarch64 are special, since most toolchains built for
    # these targets don't do multilib the tests do not get correctly marked as
    # UNSUPPORTED. More importantly the test suite itself does not handle
    # overriding the multilib flags where it could (like other archs do). As
    # such do not pass the target compiler args for these targets.
    args = d.getVar("TUNE_CCARGS").split()
    if d.getVar("TUNE_ARCH") in ["i686", "x86_64", "aarch64"]:
        args = []
    d.setVar("MAKE_CHECK_BOARDFLAGS", ("/" + "/".join(args)) if len(args) != 0 else "")
}

python check_prepare() {
    def generate_qemu_linux_user_config(d):
        content = []
        content.append('load_generic_config "sim"')
        content.append('load_base_board_description "basic-sim"')
        content.append('process_multilib_options ""')

        # qemu args
        qemu_binary = qemu_target_binary(d)
        if not qemu_binary:
            bb.fatal("Missing target qemu linux-user binary")

        args = []
        # QEMU_OPTIONS is not always valid due to -cross recipe
        args += ["-r", d.getVar("OLDEST_KERNEL")]
        # enable all valid instructions, since the test suite itself does not
        # limit itself to the target cpu options.
        #   - valid for x86*, powerpc, arm, arm64
        if qemu_binary.endswith(("x86_64", "i386", "arm", "aarch64")):
            args += ["-cpu", "max"]
        else:
            extra = d.getVar("QEMU_EXTRAOPTIONS_%s" % d.getVar('TUNE_PKGARCH'))
            if extra:
                args += extra.split()
        # For mips64 we could set a maximal CPU (e.g. Loongson-3A4000) however they either have MSA
        # or Loongson-MMI vector extensions, not both and qemu lacks complete support for MMI
        sysroot = d.getVar("RECIPE_SYSROOT")
        args += ["-L", sysroot]
        # lib paths are static here instead of using $libdir since this is used by a -cross recipe
        libpaths = [sysroot + "/usr/lib", sysroot + "/lib"]
        args += ["-E", "LD_LIBRARY_PATH={0}".format(":".join(libpaths))]

        content.append('set_board_info is_simulator 1')
        content.append('set_board_info sim "{0}"'.format(qemu_binary))
        content.append('set_board_info sim,options "{0}"'.format(" ".join(args)))

        # target build/test config
        content.append('set_board_info target_install {%s}' % d.getVar("TARGET_SYS"))
        content.append('set_board_info ldscript ""')
        #content.append('set_board_info needs_status_wrapper 1') # qemu-linux-user return codes work, and abort works fine
        content.append('set_board_info gcc,stack_size 16834')
        content.append('set_board_info gdb,nosignals 1')
        content.append('set_board_info gcc,timeout 60')

        return "\n".join(content)

    def generate_remote_ssh_linux_config(d):
        content = []
        content.append('load_generic_config "unix"')
        content.append('process_multilib_options ""')
        content.append("set_board_info hostname {0}".format(d.getVar("TOOLCHAIN_TEST_HOST")))
        content.append("set_board_info username {0}".format(d.getVar("TOOLCHAIN_TEST_HOST_USER")))

        port = d.getVar("TOOLCHAIN_TEST_HOST_PORT")
        content.append("set_board_info rsh_prog \"/usr/bin/ssh -p {0} -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no\"".format(port))
        content.append("set_board_info rcp_prog \"/usr/bin/scp -P {0} -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no\"".format(port))

        return "\n".join(content)

    dejagnudir = d.expand("${WORKDIR}/dejagnu")
    if not os.path.isdir(dejagnudir):
        os.makedirs(dejagnudir)

    # write out target qemu board config
    with open(os.path.join(dejagnudir, "user.exp"), "w") as f:
        f.write(generate_qemu_linux_user_config(d))

    # write out target ssh board config
    with open(os.path.join(dejagnudir, "ssh.exp"), "w") as f:
        f.write(generate_remote_ssh_linux_config(d))

    # generate site.exp to provide boards
    with open(os.path.join(dejagnudir, "site.exp"), "w") as f:
        f.write("lappend boards_dir {0}\n".format(dejagnudir))
        f.write("set CFLAGS_FOR_TARGET \"{0}\"\n".format(d.getVar("TOOLCHAIN_OPTIONS")))
}

