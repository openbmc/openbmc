# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#
# testexport.bbclass allows to execute runtime test outside OE environment.
# Most of the tests are commands run on target image over ssh.
# To use it add testexport to global inherit and call your target image with -c testexport
# You can try it out like this:
# - First build an image. i.e. core-image-sato
# - Add IMAGE_CLASSES += "testexport" in local.conf
# - Then bitbake core-image-sato -c testexport. That will generate the directory structure
#   to execute the runtime tests using runexported.py.
#
# For more information on TEST_SUITES check testimage class.

inherit testimage

TEST_LOG_DIR ?= "${WORKDIR}/testexport"
TEST_EXPORT_DIR ?= "${TMPDIR}/testexport/${PN}"
TEST_EXPORT_PACKAGED_DIR ?= "packages/packaged"
TEST_EXPORT_EXTRACTED_DIR ?= "packages/extracted"

TEST_TARGET ?= "simpleremote"
TEST_TARGET_IP ?= ""
TEST_SERVER_IP ?= ""

require conf/testexport.conf

TEST_EXPORT_SDK_ENABLED ?= "0"

TEST_EXPORT_DEPENDS = ""
TEST_EXPORT_DEPENDS += "${@bb.utils.contains('TEST_EXPORT_SDK_ENABLED', '1', 'testexport-tarball:do_populate_sdk', '', d)}"
TEST_EXPORT_LOCK = "${TMPDIR}/testimage.lock"

addtask testexport
do_testexport[nostamp] = "1"
do_testexport[depends] += "${TEST_EXPORT_DEPENDS} ${TESTIMAGEDEPENDS}"
do_testexport[lockfiles] += "${TEST_EXPORT_LOCK}"

python do_testexport() {
    testexport_main(d)
}

def testexport_main(d):
    import json
    import logging

    from oeqa.runtime.context import OERuntimeTestContext
    from oeqa.runtime.context import OERuntimeTestContextExecutor

    image_name = ("%s/%s" % (d.getVar('DEPLOY_DIR_IMAGE'),
                             d.getVar('IMAGE_LINK_NAME') or d.getVar('IMAGE_NAME')))

    tdname = "%s.testdata.json" % image_name
    td = json.load(open(tdname, "r"))

    logger = logging.getLogger("BitBake")

    target_kwargs = { }
    target_kwargs['machine'] = d.getVar("MACHINE") or None
    target_kwargs['serialcontrol_cmd'] = d.getVar("TEST_SERIALCONTROL_CMD") or None
    target_kwargs['serialcontrol_extra_args'] = d.getVar("TEST_SERIALCONTROL_EXTRA_ARGS") or ""
    target_kwargs['serialcontrol_ps1'] = d.getVar("TEST_SERIALCONTROL_PS1") or None
    target_kwargs['serialcontrol_connect_timeout'] = d.getVar("TEST_SERIALCONTROL_CONNECT_TIMEOUT") or None

    target = OERuntimeTestContextExecutor.getTarget(
        d.getVar("TEST_TARGET"), None, d.getVar("TEST_TARGET_IP"),
        d.getVar("TEST_SERVER_IP"), **target_kwargs)

    image_manifest = "%s.manifest" % image_name
    image_packages = OERuntimeTestContextExecutor.readPackagesManifest(image_manifest)

    extract_dir = d.getVar("TEST_EXTRACTED_DIR")

    tc = OERuntimeTestContext(td, logger, target, image_packages, extract_dir)

    copy_needed_files(d, tc)

def copy_needed_files(d, tc):
    import shutil
    import oe.path

    from oeqa.utils.package_manager import _get_json_file
    from oeqa.core.utils.test import getSuiteCasesFiles

    export_path = d.getVar('TEST_EXPORT_DIR')
    corebase_path = d.getVar('COREBASE')

    # Clean everything before starting
    oe.path.remove(export_path)
    bb.utils.mkdirhier(os.path.join(export_path, 'lib', 'oeqa'))

    # The source of files to copy are relative to 'COREBASE' directory
    # The destination is relative to 'TEST_EXPORT_DIR'
    # Because we are squashing the libraries, we need to remove
    # the layer/script directory
    files_to_copy = [ os.path.join('meta', 'lib', 'oeqa', 'core'),
                      os.path.join('meta', 'lib', 'oeqa', 'runtime'),
                      os.path.join('meta', 'lib', 'oeqa', 'files'),
                      os.path.join('meta', 'lib', 'oeqa', 'utils'),
                      os.path.join('scripts', 'oe-test'),
                      os.path.join('scripts', 'lib', 'argparse_oe.py'),
                      os.path.join('scripts', 'lib', 'scriptutils.py'), ]

    for f in files_to_copy:
        src = os.path.join(corebase_path, f)
        dst = os.path.join(export_path, f.split('/', 1)[-1])
        if os.path.isdir(src):
            oe.path.copytree(src, dst)
        else:
            shutil.copy2(src, dst)

    # Remove cases and just copy the ones specified
    cases_path = os.path.join(export_path, 'lib', 'oeqa', 'runtime', 'cases')
    oe.path.remove(cases_path)
    bb.utils.mkdirhier(cases_path)
    test_paths = get_runtime_paths(d)
    test_modules = d.getVar('TEST_SUITES').split()
    tc.loadTests(test_paths, modules=test_modules)
    for f in getSuiteCasesFiles(tc.suites):
        shutil.copy2(f, cases_path)
        json_file = _get_json_file(f)
        if json_file:
            shutil.copy2(json_file, cases_path)

    # Copy test data
    image_name = ("%s/%s" % (d.getVar('DEPLOY_DIR_IMAGE'),
                            d.getVar('IMAGE_LINK_NAME')))
    image_manifest = "%s.manifest" % image_name
    tdname = "%s.testdata.json" % image_name
    test_data_path = os.path.join(export_path, 'data')
    bb.utils.mkdirhier(test_data_path)
    shutil.copy2(image_manifest, os.path.join(test_data_path, 'manifest'))
    shutil.copy2(tdname, os.path.join(test_data_path, 'testdata.json'))

    for subdir, dirs, files in os.walk(export_path):
        for dir in dirs:
            if dir == '__pycache__':
                shutil.rmtree(os.path.join(subdir, dir))

    # Create tar file for common parts of testexport
    testexport_create_tarball(d, "testexport.tar.gz", d.getVar("TEST_EXPORT_DIR"))

    # Copy packages needed for runtime testing
    package_extraction(d, tc.suites)
    test_pkg_dir = d.getVar("TEST_NEEDED_PACKAGES_DIR")
    if os.path.isdir(test_pkg_dir) and os.listdir(test_pkg_dir):
        export_pkg_dir = os.path.join(d.getVar("TEST_EXPORT_DIR"), "packages")
        oe.path.copytree(test_pkg_dir, export_pkg_dir)
        # Create tar file for packages needed by the DUT
        testexport_create_tarball(d, "testexport_packages_%s.tar.gz" % d.getVar("MACHINE"), export_pkg_dir)

    # Copy SDK
    if d.getVar("TEST_EXPORT_SDK_ENABLED") == "1":
        sdk_deploy = d.getVar("SDK_DEPLOY")
        tarball_name = "%s.sh" % d.getVar("TEST_EXPORT_SDK_NAME")
        tarball_path = os.path.join(sdk_deploy, tarball_name)
        export_sdk_dir = os.path.join(d.getVar("TEST_EXPORT_DIR"),
                                      d.getVar("TEST_EXPORT_SDK_DIR"))
        bb.utils.mkdirhier(export_sdk_dir)
        shutil.copy2(tarball_path, export_sdk_dir)

        # Create tar file for the sdk
        testexport_create_tarball(d, "testexport_sdk_%s.tar.gz" % d.getVar("SDK_ARCH"), export_sdk_dir)

    bb.plain("Exported tests to: %s" % export_path)

def testexport_create_tarball(d, tar_name, src_dir):

    import tarfile

    tar_path = os.path.join(d.getVar("TEST_EXPORT_DIR"), tar_name)
    current_dir = os.getcwd()
    src_dir = src_dir.rstrip('/')
    dir_name = os.path.dirname(src_dir)
    base_name = os.path.basename(src_dir)

    os.chdir(dir_name)
    tar = tarfile.open(tar_path, "w:gz")
    tar.add(base_name)
    tar.close()
    os.chdir(current_dir)
