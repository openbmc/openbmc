# Copyright (C) 2016 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)
#
#
# testexport.bbclass allows to execute runtime test outside OE environment.
# Most of the tests are commands run on target image over ssh.
# To use it add testexport to global inherit and call your target image with -c testexport
# You can try it out like this:
# - First build an image. i.e. core-image-sato
# - Add INHERIT += "testexport" in local.conf
# - Then bitbake core-image-sato -c testexport. That will generate the directory structure
#   to execute the runtime tests using runexported.py.
#
# For more information on TEST_SUITES check testimage class.

TEST_LOG_DIR ?= "${WORKDIR}/testexport"
TEST_EXPORT_DIR ?= "${TMPDIR}/testexport/${PN}"
TEST_EXPORT_PACKAGED_DIR ?= "packages/packaged"
TEST_EXPORT_EXTRACTED_DIR ?= "packages/extracted"

TEST_TARGET ?= "simpleremote"
TEST_TARGET_IP ?= ""
TEST_SERVER_IP ?= ""

TEST_EXPORT_SDK_PACKAGES ?= ""
TEST_EXPORT_SDK_ENABLED ?= "0"
TEST_EXPORT_SDK_NAME ?= "testexport-tools-nativesdk"
TEST_EXPORT_SDK_DIR ?= "sdk"

TEST_EXPORT_DEPENDS = ""
TEST_EXPORT_DEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'cpio-native:do_populate_sysroot', '', d)}"
TEST_EXPORT_DEPENDS += "${@bb.utils.contains('TEST_EXPORT_SDK_ENABLED', '1', 'testexport-tarball:do_populate_sdk', '', d)}"
TEST_EXPORT_LOCK = "${TMPDIR}/testimage.lock"

python do_testexport() {
    testexport_main(d)
}

addtask testexport
do_testexport[nostamp] = "1"
do_testexport[depends] += "${TEST_EXPORT_DEPENDS} ${TESTIMAGEDEPENDS}"
do_testexport[lockfiles] += "${TEST_EXPORT_LOCK}"

def exportTests(d,tc):
    import json
    import shutil
    import pkgutil
    import re
    import oe.path

    exportpath = d.getVar("TEST_EXPORT_DIR", True)

    savedata = {}
    savedata["d"] = {}
    savedata["target"] = {}
    savedata["target"]["ip"] = tc.target.ip or d.getVar("TEST_TARGET_IP", True)
    savedata["target"]["server_ip"] = tc.target.server_ip or d.getVar("TEST_SERVER_IP", True)

    keys = [ key for key in d.keys() if not key.startswith("_") and not key.startswith("BB") \
            and not key.startswith("B_pn") and not key.startswith("do_") and not d.getVarFlag(key, "func", True)]
    for key in keys:
        try:
            savedata["d"][key] = d.getVar(key, True)
        except bb.data_smart.ExpansionError:
            # we don't care about those anyway
            pass

    json_file = os.path.join(exportpath, "testdata.json")
    with open(json_file, "w") as f:
            json.dump(savedata, f, skipkeys=True, indent=4, sort_keys=True)

    # Replace absolute path with relative in the file
    exclude_path = os.path.join(d.getVar("COREBASE", True),'meta','lib','oeqa')
    f1 = open(json_file,'r').read()
    f2 = open(json_file,'w')
    m = f1.replace(exclude_path,'oeqa')
    f2.write(m)
    f2.close()

    # now start copying files
    # we'll basically copy everything under meta/lib/oeqa, with these exceptions
    #  - oeqa/targetcontrol.py - not needed
    #  - oeqa/selftest - something else
    # That means:
    #   - all tests from oeqa/runtime defined in TEST_SUITES (including from other layers)
    #   - the contents of oeqa/utils and oeqa/runtime/files
    #   - oeqa/oetest.py and oeqa/runexport.py (this will get copied to exportpath not exportpath/oeqa)
    #   - __init__.py files
    bb.utils.mkdirhier(os.path.join(exportpath, "oeqa/runtime/files"))
    bb.utils.mkdirhier(os.path.join(exportpath, "oeqa/utils"))
    # copy test modules, this should cover tests in other layers too
    bbpath = d.getVar("BBPATH", True).split(':')
    for t in tc.testslist:
        isfolder = False
        if re.search("\w+\.\w+\.test_\S+", t):
            t = '.'.join(t.split('.')[:3])
        mod = pkgutil.get_loader(t)
        # More depth than usual?
        if (t.count('.') > 2):
            for p in bbpath:
                foldername = os.path.join(p, 'lib',  os.sep.join(t.split('.')).rsplit(os.sep, 1)[0])
                if os.path.isdir(foldername):
                    isfolder = True
                    target_folder = os.path.join(exportpath, "oeqa", "runtime", os.path.basename(foldername))
                    if not os.path.exists(target_folder):
                        oe.path.copytree(foldername, target_folder)
        if not isfolder:
            shutil.copy2(mod.path, os.path.join(exportpath, "oeqa/runtime"))
            json_file = "%s.json" % mod.path.rsplit(".", 1)[0]
            if os.path.isfile(json_file):
                shutil.copy2(json_file, os.path.join(exportpath, "oeqa/runtime"))
    # Get meta layer
    for layer in d.getVar("BBLAYERS", True).split():
        if os.path.basename(layer) == "meta":
            meta_layer = layer
            break
    # copy oeqa/oetest.py and oeqa/runexported.py
    oeqadir = os.path.join(meta_layer, "lib/oeqa")
    shutil.copy2(os.path.join(oeqadir, "oetest.py"), os.path.join(exportpath, "oeqa"))
    shutil.copy2(os.path.join(oeqadir, "runexported.py"), exportpath)
    # copy oeqa/utils/*.py
    for root, dirs, files in os.walk(os.path.join(oeqadir, "utils")):
        for f in files:
            if f.endswith(".py"):
                shutil.copy2(os.path.join(root, f), os.path.join(exportpath, "oeqa/utils"))
    # copy oeqa/runtime/files/*
    for root, dirs, files in os.walk(os.path.join(oeqadir, "runtime/files")):
        for f in files:
            shutil.copy2(os.path.join(root, f), os.path.join(exportpath, "oeqa/runtime/files"))

    # Create tar file for common parts of testexport
    create_tarball(d, "testexport.tar.gz", d.getVar("TEST_EXPORT_DIR", True))

    # Copy packages needed for runtime testing
    test_pkg_dir = d.getVar("TEST_NEEDED_PACKAGES_DIR", True)
    if os.listdir(test_pkg_dir):
        export_pkg_dir = os.path.join(d.getVar("TEST_EXPORT_DIR", True), "packages")
        oe.path.copytree(test_pkg_dir, export_pkg_dir)
        # Create tar file for packages needed by the DUT
        create_tarball(d, "testexport_packages_%s.tar.gz" % d.getVar("MACHINE", True), export_pkg_dir)

    # Copy SDK
    if d.getVar("TEST_EXPORT_SDK_ENABLED", True) == "1":
        sdk_deploy = d.getVar("SDK_DEPLOY", True)
        tarball_name = "%s.sh" % d.getVar("TEST_EXPORT_SDK_NAME", True)
        tarball_path = os.path.join(sdk_deploy, tarball_name)
        export_sdk_dir = os.path.join(d.getVar("TEST_EXPORT_DIR", True),
                                      d.getVar("TEST_EXPORT_SDK_DIR", True))
        bb.utils.mkdirhier(export_sdk_dir)
        shutil.copy2(tarball_path, export_sdk_dir)

        # Create tar file for the sdk
        create_tarball(d, "testexport_sdk_%s.tar.gz" % d.getVar("SDK_ARCH", True), export_sdk_dir)

    bb.plain("Exported tests to: %s" % exportpath)

def testexport_main(d):
    from oeqa.oetest import ExportTestContext
    from oeqa.targetcontrol import get_target_controller
    from oeqa.utils.dump import get_host_dumper

    test_create_extract_dirs(d)
    export_dir = d.getVar("TEST_EXPORT_DIR", True)
    bb.utils.mkdirhier(d.getVar("TEST_LOG_DIR", True))
    bb.utils.remove(export_dir, recurse=True)
    bb.utils.mkdirhier(export_dir)

    # the robot dance
    target = get_target_controller(d)

    # test context
    tc = ExportTestContext(d, target)

    # this is a dummy load of tests
    # we are doing that to find compile errors in the tests themselves
    # before booting the image
    try:
        tc.loadTests()
    except Exception as e:
        import traceback
        bb.fatal("Loading tests failed:\n%s" % traceback.format_exc())

    tc.extract_packages()
    exportTests(d,tc)

def create_tarball(d, tar_name, src_dir):

    import tarfile

    tar_path = os.path.join(d.getVar("TEST_EXPORT_DIR", True), tar_name)
    current_dir = os.getcwd()
    src_dir = src_dir.rstrip('/')
    dir_name = os.path.dirname(src_dir)
    base_name = os.path.basename(src_dir)

    os.chdir(dir_name)
    tar = tarfile.open(tar_path, "w:gz")
    tar.add(base_name)
    tar.close()
    os.chdir(current_dir)


testexport_main[vardepsexclude] =+ "BB_ORIGENV"

inherit testimage
