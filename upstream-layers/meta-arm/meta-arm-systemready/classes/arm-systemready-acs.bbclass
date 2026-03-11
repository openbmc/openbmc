# This class contains the common logic to deploy the SystemReady ACS pre-built
# image and set up the testimage environment. It is to be inherited by recipes
# which contains the URI to download the SystemReady ACS image.
# This class also contains a testimage "postfunc" called acs_logs_handle which
# performs the following functions after the tests have completed:
#   * Extract the acs_results directory from the Wic image to
#     ${WORKDIR}/testimage
#   * Create a symlink to the acs_results in ${TMPDIR}/log/oeqa for ease of
#     access
#   * Run the test parser, format results, and check results routines

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_HOST = "aarch64-*"
PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit nopackages deploy rootfs-postcommands ${IMAGE_CLASSES} python3native testimage

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

# Deploy with this suffix so it is picked up in the machine configuration
IMAGE_DEPLOY_SUFFIX ?= ".wic"

# Post-process commands may write to IMGDEPLOYDIR
IMGDEPLOYDIR = "${DEPLOYDIR}"
# Write the test data in IMAGE_POSTPROCESS_COMMAND
IMAGE_POSTPROCESS_COMMAND += "write_image_test_data; "

python do_deploy() {
    deploydir = d.getVar('DEPLOYDIR')
    suffix = d.getVar('IMAGE_DEPLOY_SUFFIX')
    imgfile = os.path.join(d.getVar('UNPACKDIR'), d.getVar('IMAGE_FILENAME'))
    deployfile = os.path.join(deploydir, d.getVar('IMAGE_NAME') + suffix)
    linkfile = os.path.join(deploydir, d.getVar('IMAGE_LINK_NAME') + suffix)

    # Install the image file in the deploy directory
    import shutil
    shutil.copyfile(imgfile, deployfile)
    if os.path.lexists(linkfile):
        os.remove(manifest_link)
    os.symlink(os.path.basename(deployfile), linkfile)

    # Run the image post-process commands
    from oe.utils import execute_pre_post_process
    post_process_cmds = d.getVar("IMAGE_POSTPROCESS_COMMAND")
    execute_pre_post_process(d, post_process_cmds)

    # Copy the report.txt to DEPLOYDIR
    # The machine-specific implementation can optionally put the report file in
    # ${UNPACKDIR}/report.txt. If there is no such file present, use the template.
    unpackdir = d.getVar('UNPACKDIR')
    report_file = os.path.join(unpackdir, "report.txt")
    report_file_dest = os.path.join(deploydir, "report.txt")
    if os.path.exists(report_file):
        report_file_to_copy = report_file
    else:
        report_file_to_copy = os.path.join(unpackdir, "systemready-ir-template",
                                            "report.txt")
    shutil.copyfile(report_file_to_copy, report_file_dest)

    # Ensure an empty rootfs manifest exists (required by testimage)
    fname = os.path.join(deploydir, d.getVar('IMAGE_LINK_NAME') + ".manifest")
    open(fname, 'w').close()
}
addtask deploy after do_install before do_image_complete

do_image_complete() {
    true
}
addtask image_complete after do_deploy before do_build
do_image_complete[depends] += "arm-systemready-firmware:do_image_complete"

ACS_LOG_NAME = "acs_results_${DATETIME}"
ACS_LOG_NAME[vardepsexclude] += "DATETIME"
ACS_LOG_DIR = "${TEST_LOG_DIR}/${ACS_LOG_NAME}"
ACS_LOG_LINK = "${TEST_LOG_DIR}/acs_results"
TEST_LOG_DIR = "${WORKDIR}/testimage"
RM_WORK_EXCLUDE_ITEMS += "${@ os.path.basename(d.getVar('TEST_LOG_DIR')) }"

do_testimage[postfuncs] += "acs_logs_handle"
do_testimage[depends] += "edk2-test-parser-native:do_populate_sysroot \
                          arm-systemready-scripts-native:do_populate_sysroot \
                          mtools-native:do_populate_sysroot \
                          parted-native:do_populate_sysroot"

# Process the logs
python acs_logs_handle() {
    import logging
    from oeqa.utils import make_logger_bitbake_compatible, get_json_result_dir
    import shutil

    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')
    systemready_scripts_dir = os.path.join(d.getVar('STAGING_LIBDIR_NATIVE'),
                                           "systemready_scripts")
    edk2_test_parser_dir = os.path.join(d.getVar('STAGING_LIBDIR_NATIVE'),
                                        "edk2_test_parser")
    deployfile = os.path.join(deploy_dir_image, d.getVar('IMAGE_LINK_NAME')
                              + d.getVar('IMAGE_DEPLOY_SUFFIX'))

    testimage_dir = d.getVar('TEST_LOG_DIR')
    logdir = d.getVar('ACS_LOG_DIR')
    loglink = d.getVar('ACS_LOG_LINK')

    # Copy the report.txt file from DEPLOY_DIR_IMAGE
    report_file = os.path.join(deploy_dir_image, "report.txt")
    report_file_dest = os.path.join(testimage_dir, "report.txt")
    shutil.copyfile(report_file, report_file_dest)

    # Extract the log files from the Wic image to the testimage logs directory
    resultspath = deployfile + ':3/acs_results'
    import subprocess
    subprocess.run(['wic', 'cp', resultspath, logdir], check=True)

    # Create a symlink to the acs_results directory
    if os.path.lexists(loglink):
        os.remove(loglink)
    os.symlink(os.path.basename(logdir), loglink)

    # Create a top-level symlink to the acs_results directory
    top_logdir = os.path.join(get_json_result_dir(d), d.getVar("PN"))
    log_name = d.getVar('ACS_LOG_NAME')
    top_link = os.path.join(top_logdir, log_name)
    log_target = os.path.relpath(logdir, top_logdir)
    os.symlink(log_target, top_link)

    # Parse the logs and generate results file
    logger = make_logger_bitbake_compatible(logging.getLogger("BitBake"))

    sct_log = os.path.join(logdir, 'sct_results', 'Overall', 'Summary.ekl')
    sct_seq = os.path.join(logdir, 'sct_results', 'Sequence', 'EBBR.seq')

    parser_path = os.path.join(edk2_test_parser_dir, "parser.py")
    # format-sr-results.py needs the output file to be called "result.md"
    subprocess.run([parser_path, sct_log, sct_seq, "--md",
                   os.path.join(logdir, "result.md")], check=True)

    scripts_path = os.path.join(systemready_scripts_dir,
                                "format-sr-results.py")
    summary_path = os.path.join(testimage_dir, "summary.md")
    subprocess.run([scripts_path, "--dir", testimage_dir, "--md", summary_path],
                   check=True)

    # Symlink acs-console.log to default_log
    subprocess.run(["ln", "-sf", os.path.join(testimage_dir, "default_log"),
                    os.path.join(testimage_dir, "acs-console.log")], check=True)

    # Run the check-sr-results.py systemready script to check the results
    check_sr_results_log_path = os.path.join(testimage_dir,
                                             "check_sr_results.log")
    with open(check_sr_results_log_path, "w") as f:
        check_sr_results_path = os.path.join(systemready_scripts_dir,
                                            "check-sr-results.py")
        try:
            subprocess.run([check_sr_results_path, "--dir", testimage_dir,
                            "--print-meta", "--debug"],
                           stdout=f, stderr=f, text=True, check=True)
        except subprocess.CalledProcessError:
            logger.error(f"ACS run failed the check SystemReady results. See "
                         f"{summary_path} and {check_sr_results_log_path} for "
                         f"details of the error.")
            raise bb.BBHandledException()
}
