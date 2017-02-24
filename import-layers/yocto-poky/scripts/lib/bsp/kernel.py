# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2012, Intel Corporation.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# DESCRIPTION
# This module implements the kernel-related functions used by
# 'yocto-kernel' to manage kernel config items and patches for Yocto
# BSPs.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] intel.com>
#

import sys
import os
import shutil
from .tags import *
import glob
import subprocess
from .engine import create_context

def find_bblayers():
    """
    Find and return a sanitized list of the layers found in BBLAYERS.
    """
    try:
        builddir = os.environ["BUILDDIR"]
    except KeyError:
        print("BUILDDIR not found, exiting. (Did you forget to source oe-init-build-env?)")
        sys.exit(1)
    bblayers_conf = os.path.join(builddir, "conf/bblayers.conf")

    layers = []

    bitbake_env_cmd = "bitbake -e"
    bitbake_env_lines = subprocess.Popen(bitbake_env_cmd, shell=True,
                                         stdout=subprocess.PIPE).stdout.read().decode('utf-8')

    if not bitbake_env_lines:
        print("Couldn't get '%s' output, exiting." % bitbake_env_cmd)
        sys.exit(1)

    for line in bitbake_env_lines.split('\n'):
        bblayers = get_line_val(line, "BBLAYERS")
        if (bblayers):
            break

    if not bblayers:
        print("Couldn't find BBLAYERS in %s output, exiting." % bitbake_env_cmd)
        sys.exit(1)

    raw_layers = bblayers.split()

    for layer in raw_layers:
        if layer == 'BBLAYERS' or '=' in layer:
            continue
        layers.append(layer)

    return layers


def get_line_val(line, key):
    """
    Extract the value from the VAR="val" string
    """
    if line.startswith(key + "="):
        stripped_line = line.split('=')[1]
        stripped_line = stripped_line.replace('\"', '')
        return stripped_line
    return None


def find_meta_layer():
    """
    Find and return the meta layer in BBLAYERS.
    """
    layers = find_bblayers()

    for layer in layers:
        if layer.endswith("meta"):
            return layer

    return None


def find_bsp_layer(machine):
    """
    Find and return a machine's BSP layer in BBLAYERS.
    """
    layers = find_bblayers()

    for layer in layers:
        if layer.endswith(machine):
            return layer

    print("Unable to find the BSP layer for machine %s." % machine)
    print("Please make sure it is listed in bblayers.conf")
    sys.exit(1)


def gen_choices_str(choices):
    """
    Generate a numbered list of choices from a list of choices for
    display to the user.
    """
    choices_str = ""

    for i, choice in enumerate(choices):
        choices_str += "\t" + str(i + 1) + ") " + choice + "\n"

    return choices_str


def open_user_file(scripts_path, machine, userfile, mode):
    """
    Find one of the user files (user-config.cfg, user-patches.scc)
    associated with the machine (could be in files/,
    linux-yocto-custom/, etc).  Returns the open file if found, None
    otherwise.

    The caller is responsible for closing the file returned.
    """
    layer = find_bsp_layer(machine)
    linuxdir = os.path.join(layer, "recipes-kernel/linux")
    linuxdir_list = os.listdir(linuxdir)
    for fileobj in linuxdir_list:
        fileobj_path = os.path.join(linuxdir, fileobj)
        if os.path.isdir(fileobj_path):
            userfile_name = os.path.join(fileobj_path, userfile)
            try:
                f = open(userfile_name, mode)
                return f
            except IOError:
                continue
    return None


def read_config_items(scripts_path, machine):
    """
    Find and return a list of config items (CONFIG_XXX) in a machine's
    user-defined config fragment [${machine}-user-config.cfg].
    """
    config_items = []

    f = open_user_file(scripts_path, machine, machine+"-user-config.cfg", "r")
    lines = f.readlines()
    for line in lines:
        s = line.strip()
        if s and not s.startswith("#"):
            config_items.append(s)
    f.close()

    return config_items


def write_config_items(scripts_path, machine, config_items):
    """
    Write (replace) the list of config items (CONFIG_XXX) in a
    machine's user-defined config fragment [${machine}=user-config.cfg].
    """
    f = open_user_file(scripts_path, machine, machine+"-user-config.cfg", "w")
    for item in config_items:
        f.write(item + "\n")
    f.close()

    kernel_contents_changed(scripts_path, machine)


def yocto_kernel_config_list(scripts_path, machine):
    """
    Display the list of config items (CONFIG_XXX) in a machine's
    user-defined config fragment [${machine}-user-config.cfg].
    """
    config_items = read_config_items(scripts_path, machine)

    print("The current set of machine-specific kernel config items for %s is:" % machine)
    print(gen_choices_str(config_items))


def yocto_kernel_config_rm(scripts_path, machine):
    """
    Display the list of config items (CONFIG_XXX) in a machine's
    user-defined config fragment [${machine}-user-config.cfg], prompt the user
    for one or more to remove, and remove them.
    """
    config_items = read_config_items(scripts_path, machine)

    print("Specify the kernel config items to remove:")
    inp = input(gen_choices_str(config_items))
    rm_choices = inp.split()
    rm_choices.sort()

    removed = []

    for choice in reversed(rm_choices):
        try:
            idx = int(choice) - 1
        except ValueError:
            print("Invalid choice (%s), exiting" % choice)
            sys.exit(1)
        if idx < 0 or idx >= len(config_items):
            print("Invalid choice (%d), exiting" % (idx + 1))
            sys.exit(1)
        removed.append(config_items.pop(idx))

    write_config_items(scripts_path, machine, config_items)

    print("Removed items:")
    for r in removed:
        print("\t%s" % r)


def yocto_kernel_config_add(scripts_path, machine, config_items):
    """
    Add one or more config items (CONFIG_XXX) to a machine's
    user-defined config fragment [${machine}-user-config.cfg].
    """
    new_items = []
    dup_items = []

    cur_items = read_config_items(scripts_path, machine)

    for item in config_items:
        if not item.startswith("CONFIG") or (not "=y" in item and not "=m" in item):
            print("Invalid config item (%s), exiting" % item)
            sys.exit(1)
        if item not in cur_items and item not in new_items:
            new_items.append(item)
        else:
            dup_items.append(item)

    if len(new_items) > 0:
        cur_items.extend(new_items)
        write_config_items(scripts_path, machine, cur_items)
        print("Added item%s:" % ("" if len(new_items)==1 else "s"))
        for n in new_items:
            print("\t%s" % n)

    if len(dup_items) > 0:
        output="The following item%s already exist%s in the current configuration, ignoring %s:" % \
            (("","s", "it") if len(dup_items)==1 else ("s", "", "them" ))
        print(output)
        for n in dup_items:
            print("\t%s" % n)

def find_current_kernel(bsp_layer, machine):
    """
    Determine the kernel and version currently being used in the BSP.
    """
    machine_conf = os.path.join(bsp_layer, "conf/machine/" + machine + ".conf")

    preferred_kernel = preferred_kernel_version = preferred_version_varname = None

    f = open(machine_conf, "r")
    lines = f.readlines()
    for line in lines:
        if line.strip().startswith("PREFERRED_PROVIDER_virtual/kernel"):
            preferred_kernel = line.split()[-1]
            preferred_kernel = preferred_kernel.replace('\"','')
            preferred_version_varname = "PREFERRED_VERSION_" + preferred_kernel
        if preferred_version_varname and line.strip().startswith(preferred_version_varname):
            preferred_kernel_version = line.split()[-1]
            preferred_kernel_version = preferred_kernel_version.replace('\"','')
            preferred_kernel_version = preferred_kernel_version.replace('%','')

    if preferred_kernel and preferred_kernel_version:
        return preferred_kernel + "_" + preferred_kernel_version
    elif preferred_kernel:
        return preferred_kernel


def find_filesdir(scripts_path, machine):
    """
    Find the name of the 'files' dir associated with the machine
    (could be in files/, linux-yocto-custom/, etc).  Returns the name
    of the files dir if found, None otherwise.
    """
    layer = find_bsp_layer(machine)
    filesdir = None
    linuxdir = os.path.join(layer, "recipes-kernel/linux")
    linuxdir_list = os.listdir(linuxdir)
    for fileobj in linuxdir_list:
        fileobj_path = os.path.join(linuxdir, fileobj)
        if os.path.isdir(fileobj_path):
            # this could be files/ or linux-yocto-custom/, we have no way of distinguishing
            # so we take the first (and normally only) dir we find as the 'filesdir'
            filesdir = fileobj_path

    return filesdir


def read_patch_items(scripts_path, machine):
    """
    Find and return a list of patch items in a machine's user-defined
    patch list [${machine}-user-patches.scc].
    """
    patch_items = []

    f = open_user_file(scripts_path, machine, machine+"-user-patches.scc", "r")
    lines = f.readlines()
    for line in lines:
        s = line.strip()
        if s and not s.startswith("#"):
            fields = s.split()
            if not fields[0] == "patch":
                continue
            patch_items.append(fields[1])
    f.close()

    return patch_items


def write_patch_items(scripts_path, machine, patch_items):
    """
    Write (replace) the list of patches in a machine's user-defined
    patch list [${machine}-user-patches.scc].
    """
    f = open_user_file(scripts_path, machine, machine+"-user-patches.scc", "w")
    for item in patch_items:
        f.write("patch " + item + "\n")
    f.close()

    kernel_contents_changed(scripts_path, machine)


def yocto_kernel_patch_list(scripts_path, machine):
    """
    Display the list of patches in a machine's user-defined patch list
    [${machine}-user-patches.scc].
    """
    patches = read_patch_items(scripts_path, machine)

    print("The current set of machine-specific patches for %s is:" % machine)
    print(gen_choices_str(patches))


def yocto_kernel_patch_rm(scripts_path, machine):
    """
    Remove one or more patches from a machine's user-defined patch
    list [${machine}-user-patches.scc].
    """
    patches = read_patch_items(scripts_path, machine)

    print("Specify the patches to remove:")
    inp = input(gen_choices_str(patches))
    rm_choices = inp.split()
    rm_choices.sort()

    removed = []

    filesdir = find_filesdir(scripts_path, machine)
    if not filesdir:
        print("Couldn't rm patch(es) since we couldn't find a 'files' dir")
        sys.exit(1)

    for choice in reversed(rm_choices):
        try:
            idx = int(choice) - 1
        except ValueError:
            print("Invalid choice (%s), exiting" % choice)
            sys.exit(1)
        if idx < 0 or idx >= len(patches):
            print("Invalid choice (%d), exiting" % (idx + 1))
            sys.exit(1)
        filesdir_patch = os.path.join(filesdir, patches[idx])
        if os.path.isfile(filesdir_patch):
            os.remove(filesdir_patch)
        removed.append(patches[idx])
        patches.pop(idx)

    write_patch_items(scripts_path, machine, patches)

    print("Removed patches:")
    for r in removed:
        print("\t%s" % r)


def yocto_kernel_patch_add(scripts_path, machine, patches):
    """
    Add one or more patches to a machine's user-defined patch list
    [${machine}-user-patches.scc].
    """
    existing_patches = read_patch_items(scripts_path, machine)

    for patch in patches:
        if os.path.basename(patch) in existing_patches:
            print("Couldn't add patch (%s) since it's already been added" % os.path.basename(patch))
            sys.exit(1)

    filesdir = find_filesdir(scripts_path, machine)
    if not filesdir:
        print("Couldn't add patch (%s) since we couldn't find a 'files' dir to add it to" % os.path.basename(patch))
        sys.exit(1)

    new_patches = []

    for patch in patches:
        if not os.path.isfile(patch):
            print("Couldn't find patch (%s), exiting" % patch)
            sys.exit(1)
        basename = os.path.basename(patch)
        filesdir_patch = os.path.join(filesdir, basename)
        shutil.copyfile(patch, filesdir_patch)
        new_patches.append(basename)

    cur_items = read_patch_items(scripts_path, machine)
    cur_items.extend(new_patches)
    write_patch_items(scripts_path, machine, cur_items)

    print("Added patches:")
    for n in new_patches:
        print("\t%s" % n)


def inc_pr(line):
    """
    Add 1 to the PR value in the given bbappend PR line.  For the PR
    lines in kernel .bbappends after modifications.  Handles PRs of
    the form PR := "${PR}.1" as well as PR = "r0".
    """
    idx = line.find("\"")

    pr_str = line[idx:]
    pr_str = pr_str.replace('\"','')
    fields = pr_str.split('.')
    if len(fields) > 1:
        fields[1] = str(int(fields[1]) + 1)
        pr_str = "\"" + '.'.join(fields) + "\"\n"
    else:
        pr_val = pr_str[1:]
        pr_str = "\"" + "r" + str(int(pr_val) + 1) + "\"\n"
    idx2 = line.find("\"", idx + 1)
    line = line[:idx] + pr_str
    
    return line


def kernel_contents_changed(scripts_path, machine):
    """
    Do what we need to do to notify the system that the kernel
    recipe's contents have changed.
    """
    layer = find_bsp_layer(machine)

    kernel = find_current_kernel(layer, machine)
    if not kernel:
        print("Couldn't determine the kernel for this BSP, exiting.")
        sys.exit(1)

    kernel_bbfile = os.path.join(layer, "recipes-kernel/linux/" + kernel + ".bbappend")
    if not os.path.isfile(kernel_bbfile):
        kernel_bbfile = os.path.join(layer, "recipes-kernel/linux/" + kernel + ".bb")
        if not os.path.isfile(kernel_bbfile):
            return
    kernel_bbfile_prev = kernel_bbfile + ".prev"
    shutil.copyfile(kernel_bbfile, kernel_bbfile_prev)

    ifile = open(kernel_bbfile_prev, "r")
    ofile = open(kernel_bbfile, "w")
    ifile_lines = ifile.readlines()
    for ifile_line in ifile_lines:
        if ifile_line.strip().startswith("PR"):
            ifile_line = inc_pr(ifile_line)
        ofile.write(ifile_line)
    ofile.close()
    ifile.close()


def kernels(context):
    """
    Return the list of available kernels in the BSP i.e. corresponding
    to the kernel .bbappends found in the layer.
    """
    archdir = os.path.join(context["scripts_path"], "lib/bsp/substrate/target/arch/" + context["arch"])
    kerndir = os.path.join(archdir, "recipes-kernel/linux")
    bbglob = os.path.join(kerndir, "*.bbappend")

    bbappends = glob.glob(bbglob)

    kernels = []

    for kernel in bbappends:
        filename = os.path.splitext(os.path.basename(kernel))[0]
        idx = filename.find(CLOSE_TAG)
        if idx != -1:
            filename = filename[idx + len(CLOSE_TAG):].strip()
        kernels.append(filename)

    kernels.append("custom")

    return kernels


def extract_giturl(file):
    """
    Extract the git url of the kernel repo from the kernel recipe's
    SRC_URI.
    """
    url = None
    f = open(file, "r")
    lines = f.readlines()
    for line in lines:
        line = line.strip()
        if line.startswith("SRC_URI"):
            line = line[len("SRC_URI"):].strip()
            if line.startswith("="):
                line = line[1:].strip()
                if line.startswith("\""):
                    line = line[1:].strip()
                    prot = "git"
                    for s in line.split(";"):
                        if s.startswith("git://"):
                            url = s
                        if s.startswith("protocol="):
                            prot = s.split("=")[1]
                    if url:
                        url = prot + url[3:]
    return url


def find_giturl(context):
    """
    Find the git url of the kernel repo from the kernel recipe's
    SRC_URI.
    """
    name = context["name"]
    filebase = context["filename"]
    scripts_path = context["scripts_path"]

    meta_layer = find_meta_layer()

    kerndir = os.path.join(meta_layer, "recipes-kernel/linux")
    bbglob = os.path.join(kerndir, "*.bb")
    bbs = glob.glob(bbglob)
    for kernel in bbs:
        filename = os.path.splitext(os.path.basename(kernel))[0]
        if filename in filebase:
            giturl = extract_giturl(kernel)
            return giturl
    
    return None


def read_features(scripts_path, machine):
    """
    Find and return a list of features in a machine's user-defined
    features fragment [${machine}-user-features.scc].
    """
    features = []

    f = open_user_file(scripts_path, machine, machine+"-user-features.scc", "r")
    lines = f.readlines()
    for line in lines:
        s = line.strip()
        if s and not s.startswith("#"):
            feature_include = s.split()
            features.append(feature_include[1].strip())
    f.close()

    return features


def write_features(scripts_path, machine, features):
    """
    Write (replace) the list of feature items in a
    machine's user-defined features fragment [${machine}=user-features.cfg].
    """
    f = open_user_file(scripts_path, machine, machine+"-user-features.scc", "w")
    for item in features:
        f.write("include " + item + "\n")
    f.close()

    kernel_contents_changed(scripts_path, machine)


def yocto_kernel_feature_list(scripts_path, machine):
    """
    Display the list of features used in a machine's user-defined
    features fragment [${machine}-user-features.scc].
    """
    features = read_features(scripts_path, machine)

    print("The current set of machine-specific features for %s is:" % machine)
    print(gen_choices_str(features))


def yocto_kernel_feature_rm(scripts_path, machine):
    """
    Display the list of features used in a machine's user-defined
    features fragment [${machine}-user-features.scc], prompt the user
    for one or more to remove, and remove them.
    """
    features = read_features(scripts_path, machine)

    print("Specify the features to remove:")
    inp = input(gen_choices_str(features))
    rm_choices = inp.split()
    rm_choices.sort()

    removed = []

    for choice in reversed(rm_choices):
        try:
            idx = int(choice) - 1
        except ValueError:
            print("Invalid choice (%s), exiting" % choice)
            sys.exit(1)
        if idx < 0 or idx >= len(features):
            print("Invalid choice (%d), exiting" % (idx + 1))
            sys.exit(1)
        removed.append(features.pop(idx))

    write_features(scripts_path, machine, features)

    print("Removed features:")
    for r in removed:
        print("\t%s" % r)


def yocto_kernel_feature_add(scripts_path, machine, features):
    """
    Add one or more features a machine's user-defined features
    fragment [${machine}-user-features.scc].
    """
    new_items = []

    for item in features:
        if not item.endswith(".scc"):
            print("Invalid feature (%s), exiting" % item)
            sys.exit(1)
        new_items.append(item)

    cur_items = read_features(scripts_path, machine)
    cur_items.extend(new_items)

    write_features(scripts_path, machine, cur_items)

    print("Added features:")
    for n in new_items:
        print("\t%s" % n)


def find_feature_url(git_url):
    """
    Find the url of the kern-features.rc kernel for the kernel repo
    specified from the BSP's kernel recipe SRC_URI.
    """
    feature_url = ""
    if git_url.startswith("git://"):
        git_url = git_url[len("git://"):].strip()
        s = git_url.split("/")
        if s[1].endswith(".git"):
            s[1] = s[1][:len(s[1]) - len(".git")]
        feature_url = "http://" + s[0] + "/cgit/cgit.cgi/" + s[1] + \
            "/plain/meta/cfg/kern-features.rc?h=meta"

    return feature_url


def find_feature_desc(lines):
    """
    Find the feature description and compatibility in the passed-in
    set of lines.  Returns a string string of the form 'desc
    [compat]'.
    """
    desc = "no description available"
    compat = "unknown"

    for line in lines:
        idx = line.find("KFEATURE_DESCRIPTION")
        if idx != -1:
            desc = line[idx + len("KFEATURE_DESCRIPTION"):].strip()
            if desc.startswith("\""):
                desc = desc[1:]
                if desc.endswith("\""):
                    desc = desc[:-1]
        else:
            idx = line.find("KFEATURE_COMPATIBILITY")
            if idx != -1:
                compat = line[idx + len("KFEATURE_COMPATIBILITY"):].strip()

    return desc + " [" + compat + "]"


def print_feature_descs(layer, feature_dir):
    """
    Print the feature descriptions for the features in feature_dir.
    """
    kernel_files_features = os.path.join(layer, "recipes-kernel/linux/files/" +
                                         feature_dir)
    for root, dirs, files in os.walk(kernel_files_features):
        for file in files:
            if file.endswith("~") or file.endswith("#"):
                continue
            if file.endswith(".scc"):
                fullpath = os.path.join(layer, "recipes-kernel/linux/files/" +
                                        feature_dir + "/" + file)
                f = open(fullpath)
                feature_desc = find_feature_desc(f.readlines())
                print(feature_dir + "/" + file + ": " + feature_desc)


def yocto_kernel_available_features_list(scripts_path, machine):
    """
    Display the list of all the kernel features available for use in
    BSPs, as gathered from the set of feature sources.
    """
    layer = find_bsp_layer(machine)
    kernel = find_current_kernel(layer, machine)
    if not kernel:
        print("Couldn't determine the kernel for this BSP, exiting.")
        sys.exit(1)

    context = create_context(machine, "arch", scripts_path)
    context["name"] = "name"
    context["filename"] = kernel
    giturl = find_giturl(context)
    feature_url = find_feature_url(giturl)

    feature_cmd = "wget -q -O - " + feature_url
    tmp = subprocess.Popen(feature_cmd, shell=True, stdout=subprocess.PIPE).stdout.read().decode('utf-8')

    print("The current set of kernel features available to %s is:\n" % machine)

    if tmp:
        tmpline = tmp.split("\n")
        in_kernel_options = False
        for line in tmpline:
            if not "=" in line:
                if in_kernel_options:
                    break
                if "kernel-options" in line:
                    in_kernel_options = True
                continue
            if in_kernel_options:
                feature_def = line.split("=")
                feature_type = feature_def[0].strip()
                feature = feature_def[1].strip()
                desc = get_feature_desc(giturl, feature)
                print("%s: %s" % (feature, desc))

    print("[local]")

    print_feature_descs(layer, "cfg")
    print_feature_descs(layer, "features")


def find_feature_desc_url(git_url, feature):
    """
    Find the url of the kernel feature in the kernel repo specified
    from the BSP's kernel recipe SRC_URI.
    """
    feature_desc_url = ""
    if git_url.startswith("git://"):
        git_url = git_url[len("git://"):].strip()
        s = git_url.split("/")
        if s[1].endswith(".git"):
            s[1] = s[1][:len(s[1]) - len(".git")]
        feature_desc_url = "http://" + s[0] + "/cgit/cgit.cgi/" + s[1] + \
            "/plain/meta/cfg/kernel-cache/" + feature + "?h=meta"

    return feature_desc_url


def get_feature_desc(git_url, feature):
    """
    Return a feature description of the form 'description [compatibility]
    BSPs, as gathered from the set of feature sources.
    """
    feature_desc_url = find_feature_desc_url(git_url, feature)
    feature_desc_cmd = "wget -q -O - " + feature_desc_url
    tmp = subprocess.Popen(feature_desc_cmd, shell=True, stdout=subprocess.PIPE).stdout.read().decode('utf-8')

    return find_feature_desc(tmp.split("\n"))


def yocto_kernel_feature_describe(scripts_path, machine, feature):
    """
    Display the description of a specific kernel feature available for
    use in a BSP.
    """
    layer = find_bsp_layer(machine)

    kernel = find_current_kernel(layer, machine)
    if not kernel:
        print("Couldn't determine the kernel for this BSP, exiting.")
        sys.exit(1)

    context = create_context(machine, "arch", scripts_path)
    context["name"] = "name"
    context["filename"] = kernel
    giturl = find_giturl(context)

    desc = get_feature_desc(giturl, feature)

    print(desc)


def check_feature_name(feature_name):
    """
    Sanity-check the feature name for create/destroy.  Return False if not OK.
    """
    if not feature_name.endswith(".scc"):
        print("Invalid feature name (must end with .scc) [%s], exiting" % feature_name)
        return False

    if "/" in feature_name:
        print("Invalid feature name (don't specify directory) [%s], exiting" % feature_name)
        return False

    return True


def check_create_input(feature_items):
    """
    Sanity-check the create input.  Return False if not OK.
    """
    if not check_feature_name(feature_items[0]):
        return False

    if feature_items[1].endswith(".patch") or feature_items[1].startswith("CONFIG_"):
        print("Missing description and/or compatibilty [%s], exiting" % feature_items[1])
        return False

    if feature_items[2].endswith(".patch") or feature_items[2].startswith("CONFIG_"):
        print("Missing description and/or compatibility [%s], exiting" % feature_items[1])
        return False

    return True


def yocto_kernel_feature_create(scripts_path, machine, feature_items):
    """
    Create a recipe-space kernel feature in a BSP.
    """
    if not check_create_input(feature_items):
        sys.exit(1)

    feature = feature_items[0]
    feature_basename = feature.split(".")[0]
    feature_description = feature_items[1]
    feature_compat = feature_items[2]

    patches = []
    cfg_items = []

    for item in feature_items[3:]:
        if item.endswith(".patch"):
            patches.append(item)
        elif item.startswith("CONFIG"):
            if ("=y" in item or "=m" in item):
                cfg_items.append(item)
        else:
            print("Invalid feature item (must be .patch or CONFIG_*) [%s], exiting" % item)
            sys.exit(1)

    feature_dirname = "cfg"
    if patches:
        feature_dirname = "features"

    filesdir = find_filesdir(scripts_path, machine)
    if not filesdir:
        print("Couldn't add feature (%s), no 'files' dir found" % feature)
        sys.exit(1)

    featdir = os.path.join(filesdir, feature_dirname)
    if not os.path.exists(featdir):
        os.mkdir(featdir)

    for patch in patches:
        if not os.path.isfile(patch):
            print("Couldn't find patch (%s), exiting" % patch)
            sys.exit(1)
        basename = os.path.basename(patch)
        featdir_patch = os.path.join(featdir, basename)
        shutil.copyfile(patch, featdir_patch)

    new_cfg_filename = os.path.join(featdir, feature_basename + ".cfg")
    new_cfg_file = open(new_cfg_filename, "w")
    for cfg_item in cfg_items:
        new_cfg_file.write(cfg_item + "\n")
    new_cfg_file.close()

    new_feature_filename = os.path.join(featdir, feature_basename + ".scc")
    new_feature_file = open(new_feature_filename, "w")
    new_feature_file.write("define KFEATURE_DESCRIPTION \"" + feature_description + "\"\n")
    new_feature_file.write("define KFEATURE_COMPATIBILITY " + feature_compat + "\n\n")

    for patch in patches:
        patch_dir, patch_file = os.path.split(patch)
        new_feature_file.write("patch " + patch_file + "\n")

    new_feature_file.write("kconf non-hardware " + feature_basename + ".cfg\n")
    new_feature_file.close()

    print("Added feature:")
    print("\t%s" % feature_dirname + "/" + feature)


def feature_in_use(scripts_path, machine, feature):
    """
    Determine whether the specified feature is in use by the BSP.
    Return True if so, False otherwise.
    """
    features = read_features(scripts_path, machine)
    for f in features:
        if f == feature:
            return True
    return False


def feature_remove(scripts_path, machine, feature):
    """
    Remove the specified feature from the available recipe-space
    features defined for the BSP.
    """
    features = read_features(scripts_path, machine)
    new_features = []
    for f in features:
        if f == feature:
            continue
        new_features.append(f)
    write_features(scripts_path, machine, new_features)


def yocto_kernel_feature_destroy(scripts_path, machine, feature):
    """
    Remove a recipe-space kernel feature from a BSP.
    """
    if not check_feature_name(feature):
        sys.exit(1)

    if feature_in_use(scripts_path, machine, "features/" + feature) or \
            feature_in_use(scripts_path, machine, "cfg/" + feature):
        print("Feature %s is in use (use 'feature rm' to un-use it first), exiting" % feature)
        sys.exit(1)

    filesdir = find_filesdir(scripts_path, machine)
    if not filesdir:
        print("Couldn't destroy feature (%s), no 'files' dir found" % feature)
        sys.exit(1)

    feature_dirname = "features"
    featdir = os.path.join(filesdir, feature_dirname)
    if not os.path.exists(featdir):
        print("Couldn't find feature directory (%s)" % feature_dirname)
        sys.exit(1)

    feature_fqn = os.path.join(featdir, feature)
    if not os.path.exists(feature_fqn):
        feature_dirname = "cfg"
        featdir = os.path.join(filesdir, feature_dirname)
        if not os.path.exists(featdir):
            print("Couldn't find feature directory (%s)" % feature_dirname)
            sys.exit(1)
        feature_fqn = os.path.join(featdir, feature_filename)
        if not os.path.exists(feature_fqn):
            print("Couldn't find feature (%s)" % feature)
            sys.exit(1)

    f = open(feature_fqn, "r")
    lines = f.readlines()
    for line in lines:
        s = line.strip()
        if s.startswith("patch ") or s.startswith("kconf "):
            split_line = s.split()
            filename = os.path.join(featdir, split_line[-1])
            if os.path.exists(filename):
                os.remove(filename)
    f.close()
    os.remove(feature_fqn)

    feature_remove(scripts_path, machine, feature)

    print("Removed feature:")
    print("\t%s" % feature_dirname + "/" + feature)


def base_branches(context):
    """
    Return a list of the base branches found in the kernel git repo.
    """
    giturl = find_giturl(context)

    print("Getting branches from remote repo %s..." % giturl)

    gitcmd = "git ls-remote %s *heads* 2>&1" % (giturl)
    tmp = subprocess.Popen(gitcmd, shell=True, stdout=subprocess.PIPE).stdout.read().decode('utf-8')

    branches = []

    if tmp:
        tmpline = tmp.split("\n")
        for line in tmpline:
            if len(line)==0:
                break;
            if not line.endswith("base"):
                continue;
            idx = line.find("refs/heads/")
            kbranch = line[idx + len("refs/heads/"):]
            if kbranch.find("/") == -1 and kbranch.find("base") == -1:
                continue
            idx = kbranch.find("base")
            branches.append(kbranch[:idx - 1])

    return branches


def all_branches(context):
    """
    Return a list of all the branches found in the kernel git repo.
    """
    giturl = find_giturl(context)

    print("Getting branches from remote repo %s..." % giturl)

    gitcmd = "git ls-remote %s *heads* 2>&1" % (giturl)
    tmp = subprocess.Popen(gitcmd, shell=True, stdout=subprocess.PIPE).stdout.read().decode('utf-8')

    branches = []

    base_prefixes = None

    try:
        branches_base = context["branches_base"]
        if branches_base:
            base_prefixes = branches_base.split(":")
    except KeyError:
        pass

    arch = context["arch"]

    if tmp:
        tmpline = tmp.split("\n")
        for line in tmpline:
            if len(line)==0:
                break;
            idx = line.find("refs/heads/")
            kbranch = line[idx + len("refs/heads/"):]
            kbranch_prefix = kbranch.rsplit("/", 1)[0]

            if base_prefixes:
                for base_prefix in base_prefixes:
                    if kbranch_prefix == base_prefix:
                        branches.append(kbranch)
                continue

            if (kbranch.find("/") != -1 and
                (kbranch.find("standard") != -1 or kbranch.find("base") != -1) or
                kbranch == "base"):
                branches.append(kbranch)
                continue

    return branches
