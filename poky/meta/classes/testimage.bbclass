# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

inherit metadata_scm
# testimage.bbclass enables testing of qemu images using python unittests.
# Most of the tests are commands run on target image over ssh.
# To use it add testimage to global inherit and call your target image with -c testimage
# You can try it out like this:
# - first add IMAGE_CLASSES += "testimage" in local.conf
# - build a qemu core-image-sato
# - then bitbake core-image-sato -c testimage. That will run a standard suite of tests.
#
# The tests can be run automatically each time an image is built if you set
# TESTIMAGE_AUTO = "1"

TESTIMAGE_AUTO ??= "0"

# You can set (or append to) TEST_SUITES in local.conf to select the tests
# which you want to run for your target.
# The test names are the module names in meta/lib/oeqa/runtime/cases.
# Each name in TEST_SUITES represents a required test for the image. (no skipping allowed)
# Appending "auto" means that it will try to run all tests that are suitable for the image (each test decides that on it's own).
# Note that order in TEST_SUITES is relevant: tests are run in an order such that
# tests mentioned in @skipUnlessPassed run before the tests that depend on them,
# but without such dependencies, tests run in the order in which they are listed
# in TEST_SUITES.
#
# A layer can add its own tests in lib/oeqa/runtime, provided it extends BBPATH as normal in its layer.conf.

# TEST_LOG_DIR contains a command ssh log and may contain infromation about what command is running, output and return codes and for qemu a boot log till login.
# Booting is handled by this class, and it's not a test in itself.
# TEST_QEMUBOOT_TIMEOUT can be used to set the maximum time in seconds the launch code will wait for the login prompt.
# TEST_OVERALL_TIMEOUT can be used to set the maximum time in seconds the tests will be allowed to run (defaults to no limit).
# TEST_QEMUPARAMS can be used to pass extra parameters to qemu, e.g. "-m 1024" for setting the amount of ram to 1 GB.
# TEST_RUNQEMUPARAMS can be used to pass extra parameters to runqemu, e.g. "gl" to enable OpenGL acceleration.

# TESTIMAGE_BOOT_PATTERNS can be used to override certain patterns used to communicate with the target when booting,
# if a pattern is not specifically present on this variable a default will be used when booting the target.
# TESTIMAGE_BOOT_PATTERNS[<flag>] overrides the pattern used for that specific flag, where flag comes from a list of accepted flags
# e.g. normally the system boots and waits for a login prompt (login:), after that it sends the command: "root\n" to log as the root user
# if we wanted to log in as the hypothetical "webserver" user for example we could set the following:
# TESTIMAGE_BOOT_PATTERNS = "send_login_user search_login_succeeded"
# TESTIMAGE_BOOT_PATTERNS[send_login_user] = "webserver\n"
# TESTIMAGE_BOOT_PATTERNS[search_login_succeeded] = "webserver@[a-zA-Z0-9\-]+:~#"
# The accepted flags are the following: search_reached_prompt, send_login_user, search_login_succeeded, search_cmd_finished.
# They are prefixed with either search/send, to differentiate if the pattern is meant to be sent or searched to/from the target terminal

TEST_LOG_DIR ?= "${WORKDIR}/testimage"

TEST_EXPORT_DIR ?= "${TMPDIR}/testimage/${PN}"
TEST_INSTALL_TMP_DIR ?= "${WORKDIR}/testimage/install_tmp"
TEST_NEEDED_PACKAGES_DIR ?= "${WORKDIR}/testimage/packages"
TEST_EXTRACTED_DIR ?= "${TEST_NEEDED_PACKAGES_DIR}/extracted"
TEST_PACKAGED_DIR ?= "${TEST_NEEDED_PACKAGES_DIR}/packaged"

BASICTESTSUITE = "\
    ping date df ssh scp python perl gi ptest parselogs \
    logrotate connman systemd oe_syslog pam stap ldd xorg \
    kernelmodule gcc buildcpio buildlzip buildgalculator \
    dnf rpm opkg apt weston"

DEFAULT_TEST_SUITES = "${BASICTESTSUITE}"

# aarch64 has no graphics
DEFAULT_TEST_SUITES_remove_aarch64 = "xorg"
# musl doesn't support systemtap
DEFAULT_TEST_SUITES_remove_libc-musl = "stap"

# qemumips is quite slow and has reached the timeout limit several times on the YP build cluster,
# mitigate this by removing build tests for qemumips machines.
MIPSREMOVE ??= "buildcpio buildlzip buildgalculator"
DEFAULT_TEST_SUITES_remove_qemumips = "${MIPSREMOVE}"
DEFAULT_TEST_SUITES_remove_qemumips64 = "${MIPSREMOVE}"

TEST_SUITES ?= "${DEFAULT_TEST_SUITES}"

TEST_QEMUBOOT_TIMEOUT ?= "1000"
TEST_OVERALL_TIMEOUT ?= ""
TEST_TARGET ?= "qemu"
TEST_QEMUPARAMS ?= ""
TEST_RUNQEMUPARAMS ?= ""

TESTIMAGE_BOOT_PATTERNS ?= ""

TESTIMAGEDEPENDS = ""
TESTIMAGEDEPENDS_append_qemuall = " qemu-native:do_populate_sysroot qemu-helper-native:do_populate_sysroot qemu-helper-native:do_addto_recipe_sysroot"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'cpio-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'dnf-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'createrepo-c-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'ipk', 'opkg-utils-native:do_populate_sysroot package-index:do_package_index', '', d)}"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'deb', 'apt-native:do_populate_sysroot  package-index:do_package_index', '', d)}"

TESTIMAGELOCK = "${TMPDIR}/testimage.lock"
TESTIMAGELOCK_qemuall = ""

TESTIMAGE_DUMP_DIR ?= "${LOG_DIR}/runtime-hostdump/"

TESTIMAGE_UPDATE_VARS ?= "DL_DIR WORKDIR DEPLOY_DIR"

testimage_dump_target () {
    top -bn1
    ps
    free
    df
    # The next command will export the default gateway IP
    export DEFAULT_GATEWAY=$(ip route | awk '/default/ { print $3}')
    ping -c3 $DEFAULT_GATEWAY
    dmesg
    netstat -an
    ip address
    # Next command will dump logs from /var/log/
    find /var/log/ -type f 2>/dev/null -exec echo "====================" \; -exec echo {} \; -exec echo "====================" \; -exec cat {} \; -exec echo "" \;
}

testimage_dump_host () {
    top -bn1
    iostat -x -z -N -d -p ALL 20 2
    ps -ef
    free
    df
    memstat
    dmesg
    ip -s link
    netstat -an
}

python do_testimage() {
    testimage_main(d)
}

addtask testimage
do_testimage[nostamp] = "1"
do_testimage[depends] += "${TESTIMAGEDEPENDS}"
do_testimage[lockfiles] += "${TESTIMAGELOCK}"

def testimage_sanity(d):
    if (d.getVar('TEST_TARGET') == 'simpleremote'
        and (not d.getVar('TEST_TARGET_IP')
             or not d.getVar('TEST_SERVER_IP'))):
        bb.fatal('When TEST_TARGET is set to "simpleremote" '
                 'TEST_TARGET_IP and TEST_SERVER_IP are needed too.')

def get_testimage_configuration(d, test_type, machine):
    import platform
    from oeqa.utils.metadata import get_layers
    configuration = {'TEST_TYPE': test_type,
                    'MACHINE': machine,
                    'DISTRO': d.getVar("DISTRO"),
                    'IMAGE_BASENAME': d.getVar("IMAGE_BASENAME"),
                    'IMAGE_PKGTYPE': d.getVar("IMAGE_PKGTYPE"),
                    'STARTTIME': d.getVar("DATETIME"),
                    'HOST_DISTRO': oe.lsb.distro_identifier().replace(' ', '-'),
                    'LAYERS': get_layers(d.getVar("BBLAYERS"))}
    return configuration
get_testimage_configuration[vardepsexclude] = "DATETIME"

def get_testimage_json_result_dir(d):
    json_result_dir = os.path.join(d.getVar("LOG_DIR"), 'oeqa')
    custom_json_result_dir = d.getVar("OEQA_JSON_RESULT_DIR")
    if custom_json_result_dir:
        json_result_dir = custom_json_result_dir
    return json_result_dir

def get_testimage_result_id(configuration):
    return '%s_%s_%s_%s' % (configuration['TEST_TYPE'], configuration['IMAGE_BASENAME'], configuration['MACHINE'], configuration['STARTTIME'])

def get_testimage_boot_patterns(d):
    from collections import defaultdict
    boot_patterns = defaultdict(str)
    # Only accept certain values
    accepted_patterns = ['search_reached_prompt', 'send_login_user', 'search_login_succeeded', 'search_cmd_finished']
    # Not all patterns need to be overriden, e.g. perhaps we only want to change the user
    boot_patterns_flags = d.getVarFlags('TESTIMAGE_BOOT_PATTERNS') or {}
    if boot_patterns_flags:
        patterns_set = [p for p in boot_patterns_flags.items() if p[0] in d.getVar('TESTIMAGE_BOOT_PATTERNS').split()]
        for flag, flagval in patterns_set:
                if flag not in accepted_patterns:
                    bb.fatal('Testimage: The only accepted boot patterns are: search_reached_prompt,send_login_user, \
                    search_login_succeeded,search_cmd_finished\n Make sure your TESTIMAGE_BOOT_PATTERNS=%s \
                    contains an accepted flag.' % d.getVar('TESTIMAGE_BOOT_PATTERNS'))
                    return
                # We know boot prompt is searched through in binary format, others might be expressions
                if flag == 'search_reached_prompt':
                    boot_patterns[flag] = flagval.encode()
                else:
                    boot_patterns[flag] = flagval.encode().decode('unicode-escape')
    return boot_patterns


def testimage_main(d):
    import os
    import json
    import signal
    import logging

    from bb.utils import export_proxies
    from oeqa.core.utils.misc import updateTestData
    from oeqa.runtime.context import OERuntimeTestContext
    from oeqa.runtime.context import OERuntimeTestContextExecutor
    from oeqa.core.target.qemu import supported_fstypes
    from oeqa.core.utils.test import getSuiteCases
    from oeqa.utils import make_logger_bitbake_compatible

    def sigterm_exception(signum, stackframe):
        """
        Catch SIGTERM from worker in order to stop qemu.
        """
        os.kill(os.getpid(), signal.SIGINT)

    def handle_test_timeout(timeout):
        bb.warn("Global test timeout reached (%s seconds), stopping the tests." %(timeout))
        os.kill(os.getpid(), signal.SIGINT)

    testimage_sanity(d)

    if (d.getVar('IMAGE_PKGTYPE') == 'rpm'
       and ('dnf' in d.getVar('TEST_SUITES') or 'auto' in d.getVar('TEST_SUITES'))):
        create_rpm_index(d)

    logger = make_logger_bitbake_compatible(logging.getLogger("BitBake"))
    pn = d.getVar("PN")

    bb.utils.mkdirhier(d.getVar("TEST_LOG_DIR"))

    image_name = ("%s/%s" % (d.getVar('DEPLOY_DIR_IMAGE'),
                             d.getVar('IMAGE_LINK_NAME')))

    tdname = "%s.testdata.json" % image_name
    try:
        td = json.load(open(tdname, "r"))
    except (FileNotFoundError) as err:
         bb.fatal('File %s Not Found. Have you built the image with INHERIT+="testimage" in the conf/local.conf?' % tdname)

    # Some variables need to be updates (mostly paths) with the
    # ones of the current environment because some tests require them.
    updateTestData(d, td, d.getVar('TESTIMAGE_UPDATE_VARS').split())

    image_manifest = "%s.manifest" % image_name
    image_packages = OERuntimeTestContextExecutor.readPackagesManifest(image_manifest)

    extract_dir = d.getVar("TEST_EXTRACTED_DIR")

    # Get machine
    machine = d.getVar("MACHINE")

    # Get rootfs
    fstypes = d.getVar('IMAGE_FSTYPES').split()
    if d.getVar("TEST_TARGET") == "qemu":
        fstypes = [fs for fs in fstypes if fs in supported_fstypes]
        if not fstypes:
            bb.fatal('Unsupported image type built. Add a compatible image to '
                     'IMAGE_FSTYPES. Supported types: %s' %
                     ', '.join(supported_fstypes))
    qfstype = fstypes[0]
    qdeffstype = d.getVar("QB_DEFAULT_FSTYPE")
    if qdeffstype:
        qfstype = qdeffstype
    rootfs = '%s.%s' % (image_name, qfstype)

    # Get tmpdir (not really used, just for compatibility)
    tmpdir = d.getVar("TMPDIR")

    # Get deploy_dir_image (not really used, just for compatibility)
    dir_image = d.getVar("DEPLOY_DIR_IMAGE")

    # Get bootlog
    bootlog = os.path.join(d.getVar("TEST_LOG_DIR"),
                           'qemu_boot_log.%s' % d.getVar('DATETIME'))

    # Get display
    display = d.getVar("BB_ORIGENV").getVar("DISPLAY")

    # Get kernel
    kernel_name = ('%s-%s.bin' % (d.getVar("KERNEL_IMAGETYPE"), machine))
    kernel = os.path.join(d.getVar("DEPLOY_DIR_IMAGE"), kernel_name)

    # Get boottime
    boottime = int(d.getVar("TEST_QEMUBOOT_TIMEOUT"))

    # Get use_kvm
    kvm = oe.types.qemu_use_kvm(d.getVar('QEMU_USE_KVM'), d.getVar('TARGET_ARCH'))

    # Get OVMF
    ovmf = d.getVar("QEMU_USE_OVMF")

    slirp = False
    if d.getVar("QEMU_USE_SLIRP"):
        slirp = True

    # TODO: We use the current implementation of qemu runner because of
    # time constrains, qemu runner really needs a refactor too.
    target_kwargs = { 'machine'     : machine,
                      'rootfs'      : rootfs,
                      'tmpdir'      : tmpdir,
                      'dir_image'   : dir_image,
                      'display'     : display,
                      'kernel'      : kernel,
                      'boottime'    : boottime,
                      'bootlog'     : bootlog,
                      'kvm'         : kvm,
                      'slirp'       : slirp,
                      'dump_dir'    : d.getVar("TESTIMAGE_DUMP_DIR"),
                      'serial_ports': len(d.getVar("SERIAL_CONSOLES").split()),
                      'ovmf'        : ovmf,
                    }

    if d.getVar("TESTIMAGE_BOOT_PATTERNS"):
        target_kwargs['boot_patterns'] = get_testimage_boot_patterns(d)

    # TODO: Currently BBPATH is needed for custom loading of targets.
    # It would be better to find these modules using instrospection.
    target_kwargs['target_modules_path'] = d.getVar('BBPATH')

    # hardware controlled targets might need further access
    target_kwargs['powercontrol_cmd'] = d.getVar("TEST_POWERCONTROL_CMD") or None
    target_kwargs['powercontrol_extra_args'] = d.getVar("TEST_POWERCONTROL_EXTRA_ARGS") or ""
    target_kwargs['serialcontrol_cmd'] = d.getVar("TEST_SERIALCONTROL_CMD") or None
    target_kwargs['serialcontrol_extra_args'] = d.getVar("TEST_SERIALCONTROL_EXTRA_ARGS") or ""

    def export_ssh_agent(d):
        import os

        variables = ['SSH_AGENT_PID', 'SSH_AUTH_SOCK']
        for v in variables:
            if v not in os.environ.keys():
                val = d.getVar(v)
                if val is not None:
                    os.environ[v] = val

    export_ssh_agent(d)

    # runtime use network for download projects for build
    export_proxies(d)

    # we need the host dumper in test context
    host_dumper = OERuntimeTestContextExecutor.getHostDumper(
        d.getVar("testimage_dump_host"),
        d.getVar("TESTIMAGE_DUMP_DIR"))

    # the robot dance
    target = OERuntimeTestContextExecutor.getTarget(
        d.getVar("TEST_TARGET"), logger, d.getVar("TEST_TARGET_IP"),
        d.getVar("TEST_SERVER_IP"), **target_kwargs)

    # test context
    tc = OERuntimeTestContext(td, logger, target, host_dumper,
                              image_packages, extract_dir)

    # Load tests before starting the target
    test_paths = get_runtime_paths(d)
    test_modules = d.getVar('TEST_SUITES').split()
    if not test_modules:
        bb.fatal('Empty test suite, please verify TEST_SUITES variable')

    tc.loadTests(test_paths, modules=test_modules)

    suitecases = getSuiteCases(tc.suites)
    if not suitecases:
        bb.fatal('Empty test suite, please verify TEST_SUITES variable')
    else:
        bb.debug(2, 'test suites:\n\t%s' % '\n\t'.join([str(c) for c in suitecases]))

    package_extraction(d, tc.suites)

    results = None
    orig_sigterm_handler = signal.signal(signal.SIGTERM, sigterm_exception)
    try:
        # We need to check if runqemu ends unexpectedly
        # or if the worker send us a SIGTERM
        tc.target.start(params=d.getVar("TEST_QEMUPARAMS"), runqemuparams=d.getVar("TEST_RUNQEMUPARAMS"))
        import threading
        try:
            threading.Timer(int(d.getVar("TEST_OVERALL_TIMEOUT")), handle_test_timeout, (int(d.getVar("TEST_OVERALL_TIMEOUT")),)).start()
        except ValueError:
            pass
        results = tc.runTests()
    except (KeyboardInterrupt, BlockingIOError) as err:
        if isinstance(err, KeyboardInterrupt):
            bb.error('testimage interrupted, shutting down...')
        else:
            bb.error('runqemu failed, shutting down...')
        if results:
            results.stop()
            results = None
    finally:
        signal.signal(signal.SIGTERM, orig_sigterm_handler)
        tc.target.stop()

    # Show results (if we have them)
    if not results:
        bb.fatal('%s - FAILED - tests were interrupted during execution' % pn, forcelog=True)
    configuration = get_testimage_configuration(d, 'runtime', machine)
    results.logDetails(get_testimage_json_result_dir(d),
                       configuration,
                       get_testimage_result_id(configuration),
                       dump_streams=d.getVar('TESTREPORT_FULLLOGS'))
    results.logSummary(pn)
    if not results.wasSuccessful():
        bb.fatal('%s - FAILED - check the task log and the ssh log' % pn, forcelog=True)

def get_runtime_paths(d):
    """
    Returns a list of paths where runtime test must reside.

    Runtime tests are expected in <LAYER_DIR>/lib/oeqa/runtime/cases/
    """
    paths = []

    for layer in d.getVar('BBLAYERS').split():
        path = os.path.join(layer, 'lib/oeqa/runtime/cases')
        if os.path.isdir(path):
            paths.append(path)
    return paths

def create_index(arg):
    import subprocess

    index_cmd = arg
    try:
        bb.note("Executing '%s' ..." % index_cmd)
        result = subprocess.check_output(index_cmd,
                                        stderr=subprocess.STDOUT,
                                        shell=True)
        result = result.decode('utf-8')
    except subprocess.CalledProcessError as e:
        return("Index creation command '%s' failed with return code "
               '%d:\n%s' % (e.cmd, e.returncode, e.output.decode("utf-8")))
    if result:
        bb.note(result)
    return None

def create_rpm_index(d):
    import glob
    # Index RPMs
    rpm_createrepo = bb.utils.which(os.getenv('PATH'), "createrepo_c")
    index_cmds = []
    archs = (d.getVar('ALL_MULTILIB_PACKAGE_ARCHS') or '').replace('-', '_')

    for arch in archs.split():
        rpm_dir = os.path.join(d.getVar('DEPLOY_DIR_RPM'), arch)
        idx_path = os.path.join(d.getVar('WORKDIR'), 'oe-testimage-repo', arch)

        if not os.path.isdir(rpm_dir):
            continue

        lockfilename = os.path.join(d.getVar('DEPLOY_DIR_RPM'), 'rpm.lock')
        lf = bb.utils.lockfile(lockfilename, False)
        oe.path.copyhardlinktree(rpm_dir, idx_path)
        # Full indexes overload a 256MB image so reduce the number of rpms
        # in the feed by filtering to specific packages needed by the tests.
        package_list = glob.glob(idx_path + "*/*.rpm")

        for pkg in package_list:
            if not os.path.basename(pkg).startswith(("rpm", "run-postinsts", "busybox", "bash", "update-alternatives", "libc6", "curl", "musl")):
                bb.utils.remove(pkg)

        bb.utils.unlockfile(lf)
        cmd = '%s --update -q %s' % (rpm_createrepo, idx_path)

        # Create repodata
        result = create_index(cmd)
        if result:
            bb.fatal('%s' % ('\n'.join(result)))

def package_extraction(d, test_suites):
    from oeqa.utils.package_manager import find_packages_to_extract
    from oeqa.utils.package_manager import extract_packages

    bb.utils.remove(d.getVar("TEST_NEEDED_PACKAGES_DIR"), recurse=True)
    packages = find_packages_to_extract(test_suites)
    if packages:
        bb.utils.mkdirhier(d.getVar("TEST_INSTALL_TMP_DIR"))
        bb.utils.mkdirhier(d.getVar("TEST_PACKAGED_DIR"))
        bb.utils.mkdirhier(d.getVar("TEST_EXTRACTED_DIR"))
        extract_packages(d, packages)

testimage_main[vardepsexclude] += "BB_ORIGENV DATETIME"

python () {
    if oe.types.boolean(d.getVar("TESTIMAGE_AUTO") or "False"):
        bb.build.addtask("testimage", "do_build", "do_image_complete", d)
}

inherit testsdk
