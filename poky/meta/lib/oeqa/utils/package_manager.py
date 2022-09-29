#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import json
import shutil

from oeqa.core.utils.test import getCaseFile, getCaseMethod

def get_package_manager(d, root_path):
    """
    Returns an OE package manager that can install packages in root_path.
    """
    from oe.package_manager.rpm import RpmPM
    from oe.package_manager.ipk import OpkgPM
    from oe.package_manager.deb import DpkgPM

    pkg_class = d.getVar("IMAGE_PKGTYPE")
    if pkg_class == "rpm":
        pm = RpmPM(d,
                   root_path,
                   d.getVar('TARGET_VENDOR'),
                   filterbydependencies=False)
        pm.create_configs()

    elif pkg_class == "ipk":
        pm = OpkgPM(d,
                    root_path,
                    d.getVar("IPKGCONF_TARGET"),
                    d.getVar("ALL_MULTILIB_PACKAGE_ARCHS"),
                    filterbydependencies=False)

    elif pkg_class == "deb":
        pm = DpkgPM(d,
                    root_path,
                    d.getVar('PACKAGE_ARCHS'),
                    d.getVar('DPKG_ARCH'),
                    filterbydependencies=False)

    pm.write_index()
    pm.update()

    return pm

def find_packages_to_extract(test_suite):
    """
    Returns packages to extract required by runtime tests.
    """
    from oeqa.core.utils.test import getSuiteCasesFiles

    needed_packages = {}
    files = getSuiteCasesFiles(test_suite)

    for f in set(files):
        json_file = _get_json_file(f)
        if json_file:
            needed_packages.update(_get_needed_packages(json_file))

    return needed_packages

def _get_json_file(module_path):
    """
    Returns the path of the JSON file for a module, empty if doesn't exitst.
    """

    json_file = '%s.json' % module_path.rsplit('.', 1)[0]
    if os.path.isfile(module_path) and os.path.isfile(json_file):
        return json_file
    else:
        return ''

def _get_needed_packages(json_file, test=None):
    """
    Returns a dict with needed packages based on a JSON file.

    If a test is specified it will return the dict just for that test.
    """
    needed_packages = {}

    with open(json_file) as f:
        test_packages = json.load(f)
    for key,value in test_packages.items():
        needed_packages[key] = value

    if test:
        if test in needed_packages:
            needed_packages = needed_packages[test]
        else:
            needed_packages = {}

    return needed_packages

def extract_packages(d, needed_packages):
    """
    Extract packages that will be needed during runtime.
    """

    import bb
    import oe.path

    extracted_path = d.getVar('TEST_EXTRACTED_DIR')

    for key,value in needed_packages.items():
        packages = ()
        if isinstance(value, dict):
            packages = (value, )
        elif isinstance(value, list):
            packages = value
        else:
            bb.fatal('Failed to process needed packages for %s; '
                     'Value must be a dict or list' % key)

        for package in packages:
            pkg = package['pkg']
            rm = package.get('rm', False)
            extract = package.get('extract', True)

            if extract:
                #logger.debug('Extracting %s' % pkg)
                dst_dir = os.path.join(extracted_path, pkg)
                # Same package used for more than one test,
                # don't need to extract again.
                if os.path.exists(dst_dir):
                    continue

                # Extract package and copy it to TEST_EXTRACTED_DIR
                pkg_dir = _extract_in_tmpdir(d, pkg)
                oe.path.copytree(pkg_dir, dst_dir)
                shutil.rmtree(pkg_dir)

            else:
                #logger.debug('Copying %s' % pkg)
                _copy_package(d, pkg)

def _extract_in_tmpdir(d, pkg):
    """"
    Returns path to a temp directory where the package was
    extracted without dependencies.
    """

    from oeqa.utils.package_manager import get_package_manager

    pkg_path = os.path.join(d.getVar('TEST_INSTALL_TMP_DIR'), pkg)
    pm = get_package_manager(d, pkg_path)
    extract_dir = pm.extract(pkg)
    shutil.rmtree(pkg_path)

    return extract_dir

def _copy_package(d, pkg):
    """
    Copy the RPM, DEB or IPK package to dst_dir
    """

    from oeqa.utils.package_manager import get_package_manager

    pkg_path = os.path.join(d.getVar('TEST_INSTALL_TMP_DIR'), pkg)
    dst_dir = d.getVar('TEST_PACKAGED_DIR')
    pm = get_package_manager(d, pkg_path)
    pkg_info = pm.package_info(pkg)
    file_path = pkg_info[pkg]['filepath']
    shutil.copy2(file_path, dst_dir)
    shutil.rmtree(pkg_path)

def install_package(test_case):
    """
    Installs package in DUT if required.
    """
    needed_packages = test_needs_package(test_case)
    if needed_packages:
        _install_uninstall_packages(needed_packages, test_case, True)

def uninstall_package(test_case):
    """
    Uninstalls package in DUT if required.
    """
    needed_packages = test_needs_package(test_case)
    if needed_packages:
        _install_uninstall_packages(needed_packages, test_case, False)

def test_needs_package(test_case):
    """
    Checks if a test case requires to install/uninstall packages.
    """
    test_file = getCaseFile(test_case)
    json_file = _get_json_file(test_file)

    if json_file:
        test_method = getCaseMethod(test_case)
        needed_packages = _get_needed_packages(json_file, test_method)
        if needed_packages:
            return needed_packages

    return None

def _install_uninstall_packages(needed_packages, test_case, install=True):
    """
    Install/Uninstall packages in the DUT without using a package manager
    """

    if isinstance(needed_packages, dict):
        packages = [needed_packages]
    elif isinstance(needed_packages, list):
        packages = needed_packages

    for package in packages:
        pkg = package['pkg']
        rm = package.get('rm', False)
        extract = package.get('extract', True)
        src_dir = os.path.join(test_case.tc.extract_dir, pkg)

        # Install package
        if install and extract:
            test_case.tc.target.copyDirTo(src_dir, '/')

        # Uninstall package
        elif not install and rm:
            test_case.tc.target.deleteDirStructure(src_dir, '/')
