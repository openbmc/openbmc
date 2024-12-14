#
# Copyright (C) 2015 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

# Provides functions to help with exporting binaries obtained from built targets

import os, re, glob as g, shutil as sh,sys
from time import sleep
from .commands import runCmd
from difflib import SequenceMatcher as SM

try:
    import bb
except ImportError:
    class my_log():
        def __init__(self):
            pass
        def plain(self, msg):
            if msg:
                print(msg)
        def warn(self, msg):
            if msg:
                print("WARNING: " + msg)
        def fatal(self, msg):
            if msg:
                print("FATAL:" + msg)
                sys.exit(1)
    bb = my_log()    


def determine_if_poky_env():
    """
    used to determine if we are inside the poky env or not. Usefull for remote machine where poky is not present
    """
    check_env = True if ("/scripts" and "/bitbake/bin") in os.getenv("PATH") else False
    return check_env


def get_dest_folder(tune_features, folder_list):
    """
    Function to determine what rpm deploy dir to choose for a given architecture based on TUNE_FEATURES
    """
    features_list = tune_features.split(" ")
    features_list.reverse()
    features_list = "_".join(features_list)
    match_rate = 0
    best_match = None
    for folder in folder_list:
        curr_match_rate = SM(None, folder, features_list).ratio()
        if curr_match_rate > match_rate:
            match_rate = curr_match_rate
            best_match = folder
    return best_match


def process_binaries(d, params):
    param_list = params
    export_env = d.getVar("TEST_EXPORT_ONLY")

    def extract_binary(pth_to_pkg, dest_pth=None):
        tar_command = runCmd("which tar")
        rpm2archive_command = runCmd("ls /usr/bin/rpm2archive")
        if (tar_command.status != 0) and (rpm2archive_command.status != 0):
            bb.fatal("Either \"rpm2archive\" or \"tar\" tools are not available on your system."
                    "All binaries extraction processes will not be available, crashing all related tests."
                    "Please install them according to your OS recommendations") # will exit here
        if dest_pth:
            os.chdir(dest_pth)
        else:
            os.chdir("%s" % os.sep)# this is for native package
        extract_bin_command = runCmd("%s -n %s | %s xv" % (rpm2archive_command.output, pth_to_pkg, tar_command.output)) # semi-hardcoded because of a bug on poky's rpm2cpio
        return extract_bin_command

    if determine_if_poky_env(): # machine with poky environment
        exportpath = d.getVar("TEST_EXPORT_DIR") if export_env else d.getVar("DEPLOY_DIR")
        rpm_deploy_dir = d.getVar("DEPLOY_DIR_RPM")
        arch = get_dest_folder(d.getVar("TUNE_FEATURES"), os.listdir(rpm_deploy_dir))
        arch_rpm_dir = os.path.join(rpm_deploy_dir, arch)
        extracted_bin_dir = os.path.join(exportpath,"binaries", arch, "extracted_binaries")
        packaged_bin_dir = os.path.join(exportpath,"binaries", arch, "packaged_binaries")
        # creating necessary directory structure in case testing is done in poky env.
        if export_env == "0":
            if not os.path.exists(extracted_bin_dir): bb.utils.mkdirhier(extracted_bin_dir)
            if not os.path.exists(packaged_bin_dir): bb.utils.mkdirhier(packaged_bin_dir)

        if param_list[3] == "native":
            if export_env == "1": #this is a native package and we only need to copy it. no need for extraction
                native_rpm_dir = os.path.join(rpm_deploy_dir, get_dest_folder("{} nativesdk".format(d.getVar("BUILD_SYS")), os.listdir(rpm_deploy_dir)))
                native_rpm_file_list = [item for item in os.listdir(native_rpm_dir) if re.search("nativesdk-" + param_list[0] + "-([0-9]+\.*)", item)]
                if not native_rpm_file_list:
                    bb.warn("Couldn't find any version of {} native package. Related tests will most probably fail.".format(param_list[0]))
                    return ""
                for item in native_rpm_file_list:# will copy all versions of package. Used version will be selected on remote machine
                    bb.plain("Copying native package file: %s" % item)
                    sh.copy(os.path.join(rpm_deploy_dir, native_rpm_dir, item), os.path.join(d.getVar("TEST_EXPORT_DIR"), "binaries", "native"))
            else: # nothing to do here; running tests under bitbake, so we asume native binaries are in sysroots dir.
                if param_list[1] or param_list[4]:
                    bb.warn("Native binary %s %s%s. Running tests under bitbake environment. Version can't be checked except when the test itself does it"
                            " and binary can't be removed."%(param_list[0],"has assigned ver. " + param_list[1] if param_list[1] else "",
                            ", is marked for removal" if param_list[4] else ""))
        else:# the package is target aka DUT intended and it is either required to be delivered in an extracted form or in a packaged version
            target_rpm_file_list = [item for item in os.listdir(arch_rpm_dir) if re.search(param_list[0] + "-([0-9]+\.*)", item)]
            if not target_rpm_file_list:
                bb.warn("Couldn't find any version of target package %s. Please ensure it was built. "
                        "Related tests will probably fail." % param_list[0])
                return ""
            if param_list[2] == "rpm": # binary should be deployed as rpm; (other, .deb, .ipk? ;  in the near future)
                for item in target_rpm_file_list: # copying all related rpm packages. "Intuition" reasons, someone may need other versions too. Deciding later on version
                    bb.plain("Copying target specific packaged file: %s" % item)
                    sh.copy(os.path.join(arch_rpm_dir, item), packaged_bin_dir)
                    return "copied"
            else: # it is required to extract the binary
                if param_list[1]: # the package is versioned
                    for item in target_rpm_file_list:
                        if re.match(".*-{}-.*\.rpm".format(param_list[1]), item):
                            destination = os.path.join(extracted_bin_dir,param_list[0], param_list[1])
                            bb.utils.mkdirhier(destination)
                            extract_binary(os.path.join(arch_rpm_dir, item), destination)
                            break
                    else:
                        bb.warn("Couldn't find the desired version %s for target binary %s. Related test cases will probably fail." % (param_list[1], param_list[0]))
                        return ""
                    return "extracted"
                else: # no version provided, just extract one binary
                    destination = os.path.join(extracted_bin_dir,param_list[0],
                                               re.search(".*-([0-9]+\.[0-9]+)-.*rpm", target_rpm_file_list[0]).group(1))
                    bb.utils.mkdirhier(destination)
                    extract_binary(os.path.join(arch_rpm_dir, target_rpm_file_list[0]), destination)
                    return "extracted"
    else: # remote machine
        binaries_path = os.getenv("bin_dir")# in order to know where the binaries are, bin_dir is set as env. variable
        if param_list[3] == "native": #need to extract the native pkg here
            native_rpm_dir = os.path.join(binaries_path, "native")
            native_rpm_file_list = os.listdir(native_rpm_dir)
            for item in native_rpm_file_list:
                if param_list[1] and re.match("nativesdk-{}-{}-.*\.rpm".format(param_list[0], param_list[1]), item): # native package has version
                    extract_binary(os.path.join(native_rpm_dir, item))
                    break
                else:# just copy any related native binary
                    found_version = re.match("nativesdk-{}-([0-9]+\.[0-9]+)-".format(param_list[0]), item).group(1)
                    if found_version:
                        extract_binary(os.path.join(native_rpm_dir, item))
            else:
                bb.warn("Couldn't find native package %s%s. Related test cases will be influenced." %
                        (param_list[0], " with version " + param_list[1] if param_list[1] else ""))
                return

        else: # this is for target device
            if param_list[2] == "rpm":
                return "No need to extract, this is an .rpm file"
            arch = get_dest_folder(d.getVar("TUNE_FEATURES"), os.listdir(binaries_path))
            extracted_bin_path = os.path.join(binaries_path, arch, "extracted_binaries")
            extracted_bin_list = [item for item in os.listdir(extracted_bin_path)]
            packaged_bin_path = os.path.join(binaries_path, arch, "packaged_binaries")
            packaged_bin_file_list = os.listdir(packaged_bin_path)
            # see if the package is already in the extracted ones; maybe it was deployed when exported the env.
            if os.path.exists(os.path.join(extracted_bin_path, param_list[0], param_list[1] if param_list[1] else "")):
                return "binary %s is already extracted" % param_list[0]
            else: # we need to search for it in the packaged binaries directory. It may have been shipped after export
                for item in packaged_bin_file_list:
                    if param_list[1]:
                        if re.match("%s-%s.*rpm" % (param_list[0], param_list[1]), item): # package with version
                            if not os.path.exists(os.path.join(extracted_bin_path, param_list[0],param_list[1])):
                                os.makedirs(os.path.join(extracted_bin_path, param_list[0], param_list[1]))
                                extract_binary(os.path.join(packaged_bin_path, item), os.path.join(extracted_bin_path, param_list[0],param_list[1]))
                                bb.plain("Using {} for {}".format(os.path.join(packaged_bin_path, item), param_list[0]))
                            break
                    else:
                        if re.match("%s-.*rpm" % param_list[0], item):
                            found_version = re.match(".*-([0-9]+\.[0-9]+)-", item).group(1)
                            if not os.path.exists(os.path.join(extracted_bin_path, param_list[0], found_version)):
                                os.makedirs(os.path.join(extracted_bin_path, param_list[0], found_version))
                                bb.plain("Used ver. %s for %s" % (found_version, param_list[0]))
                                extract_binary(os.path.join(packaged_bin_path, item), os.path.join(extracted_bin_path, param_list[0], found_version))
                            break
                else:
                    bb.warn("Couldn't find target package %s%s. Please ensure it is available "
                            "in either of these directories: extracted_binaries or packaged_binaries. "
                            "Related tests will probably fail." % (param_list[0], " with version " + param_list[1] if param_list[1] else ""))
                    return
                return "Binary %s extracted successfully." % param_list[0]


def files_to_copy(base_dir):
    """
    Produces a list of files relative to the base dir path sent as param
    :return: the list of relative path files
    """
    files_list = []
    dir_list = [base_dir]
    count = 1
    dir_count = 1
    while (dir_count == 1 or dir_count != count):
        count = dir_count
        for dir in dir_list:
            for item in os.listdir(dir):
                if os.path.isdir(os.path.join(dir, item)) and os.path.join(dir, item) not in dir_list:
                   dir_list.append(os.path.join(dir, item))
                   dir_count = len(dir_list)
                elif os.path.join(dir, item) not in files_list and os.path.isfile(os.path.join(dir, item)):
                   files_list.append(os.path.join(dir, item))
    return files_list


def send_bin_to_DUT(d,params):
    from oeqa.oetest import oeRuntimeTest
    param_list = params
    cleanup_list = list()
    bins_dir = os.path.join(d.getVar("TEST_EXPORT_DIR"), "binaries") if determine_if_poky_env() \
                    else os.getenv("bin_dir")
    arch = get_dest_folder(d.getVar("TUNE_FEATURES"), os.listdir(bins_dir))
    arch_rpms_dir = os.path.join(bins_dir, arch, "packaged_binaries")
    extracted_bin_dir = os.path.join(bins_dir, arch, "extracted_binaries", param_list[0])

    def send_extracted_binary():
        bin_local_dir = os.path.join(extracted_bin_dir, param_list[1] if param_list[1] else os.listdir(extracted_bin_dir)[0])
        for item in files_to_copy(bin_local_dir):
            split_path = item.split(bin_local_dir)[1]
            path_on_DUT = split_path if split_path[0] is "/" else "/" + split_path # create the path as on DUT; eg. /usr/bin/bin_file
            (status, output) = oeRuntimeTest.tc.target.copy_to(item, path_on_DUT)
            if status != 0:
                bb.warn("Failed to copy %s binary file %s on the remote target: %s" %
                        (param_list[0], "ver. " + param_list[1] if param_list[1] else "", d.getVar("MACHINE")))
                return
            if param_list[4] == "rm":
                cleanup_list.append(path_on_DUT)
        return cleanup_list

    def send_rpm(remote_path): # if it is not required to have an extracted binary, but to send an .rpm file
        rpm_to_send = ""
        for item in os.listdir(arch_rpms_dir):
            if param_list[1] and re.match("%s-%s-.*rpm"%(param_list[0], param_list[1]), item):
                rpm_to_send = item
                break
            elif re.match("%s-[0-9]+\.[0-9]+-.*rpm" % param_list[0], item):
                rpm_to_send = item
                break
        else:
            bb.warn("No rpm package found for %s %s in .rpm files dir %s. Skipping deployment." %
                    (param_list[0], "ver. " + param_list[1] if param_list[1] else "", rpms_file_dir) )
            return
        (status, output) = oeRuntimeTest.tc.target.copy_to(os.path.join(arch_rpms_dir, rpm_to_send), remote_path)
        if status != 0:
                bb.warn("Failed to copy %s on the remote target: %s" %(param_list[0], d.getVar("MACHINE")))
                return
        if param_list[4] == "rm":
            cleanup_list.append(os.path.join(remote_path, rpm_to_send))
            return cleanup_list

    if param_list[2] == "rpm": # send an .rpm file
        return send_rpm("/home/root") # rpms will be sent on home dir of remote machine
    else:
        return send_extracted_binary()


def rm_bin(removal_list): # need to know both if the binary is sent archived and the path where it is sent if archived
    from oeqa.oetest import oeRuntimeTest
    for item in removal_list:
        (status,output) = oeRuntimeTest.tc.target.run("rm " + item)
        if status != 0:
            bb.warn("Failed to remove: %s. Please ensure connection with the target device is up and running and "
                     "you have the needed rights." % item)

