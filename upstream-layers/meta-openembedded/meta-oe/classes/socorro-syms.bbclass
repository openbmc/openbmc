# Inherit this class when you want to allow Mozilla Socorro to link Breakpad's
# stack trace information to the correct source code revision.
# This class creates a new version of the symbol file (.sym) created by
# Breakpad. The absolute file paths in the symbol file will be replaced by VCS,
# branch, file and revision of the source file. That information facilitates the
# lookup of a particular source code line in the stack trace.
#
# Use example:
#
# BREAKPAD_BIN = "YourBinary"
# inherit socorro-syms
#

# We depend on Breakpad creating the original symbol file.
inherit breakpad

PACKAGE_PREPROCESS_FUNCS += "symbol_file_preprocess"
PACKAGES =+ "${PN}-socorro-syms"
FILES:${PN}-socorro-syms = "/usr/share/socorro-syms"


python symbol_file_preprocess() {

    package_dir = d.getVar("PKGD")
    breakpad_bin = d.getVar("BREAKPAD_BIN")
    if not breakpad_bin:
        package_name = d.getVar("PN")
        bb.error("Package %s depends on Breakpad via socorro-syms. See "
            "breakpad.bbclass for instructions on setting up the Breakpad "
            "configuration." % package_name)
        raise ValueError("BREAKPAD_BIN not defined in %s." % package_name)

    sym_file_name = breakpad_bin + ".sym"

    breakpad_syms_dir = os.path.join(
        package_dir, "usr", "share", "breakpad-syms")
    socorro_syms_dir = os.path.join(
        package_dir, "usr", "share", "socorro-syms")
    if not os.path.exists(socorro_syms_dir):
        os.makedirs(socorro_syms_dir)

    breakpad_sym_file_path = os.path.join(breakpad_syms_dir, sym_file_name)
    socorro_sym_file_path = os.path.join(socorro_syms_dir, sym_file_name)

    create_socorro_sym_file(d, breakpad_sym_file_path, socorro_sym_file_path)

    arrange_socorro_sym_file(socorro_sym_file_path, socorro_syms_dir)

    return
}


def run_command(command, directory):

    (output, error) = bb.process.run(command, cwd=directory)
    if error:
        raise bb.process.ExecutionError(command, error)

    return output.rstrip()


def create_socorro_sym_file(d, breakpad_sym_file_path, socorro_sym_file_path):

    # In the symbol file, all source files are referenced like the following.
    # FILE 123 /path/to/some/File.cpp
    # Go through all references and replace the file paths with repository
    # paths.
    with open(breakpad_sym_file_path, 'r') as breakpad_sym_file, \
            open(socorro_sym_file_path, 'w') as socorro_sym_file:

        for line in breakpad_sym_file:
            if line.startswith("FILE "):
                socorro_sym_file.write(socorro_file_reference(d, line))
            else:
                socorro_sym_file.write(line)

    return


def socorro_file_reference(d, line):

    # The 3rd position is the file path. See example above.
    source_file_path = line.split()[2]
    source_file_repo_path = repository_path(
        d, os.path.normpath(source_file_path))

    # If the file could be found in any repository then replace it with the
    # repository's path.
    if source_file_repo_path:
        return line.replace(source_file_path, source_file_repo_path)

    return line


def repository_path(d, source_file_path):

    if not os.path.isfile(source_file_path):
        return None

    # Check which VCS is used and use that to extract repository information.
    (output, error) = bb.process.run("git status",
        cwd=os.path.dirname(source_file_path))
    if not error:
        # Make sure the git repository we just found wasn't the yocto repository
        # itself, i.e. the root of the repository we're looking for must be a
        # child of the build directory TOPDIR.
        git_root_dir = run_command(
            "git rev-parse --show-toplevel", os.path.dirname(source_file_path))
        if not git_root_dir.startswith(d.getVar("TOPDIR")):
            return None

        return git_repository_path(source_file_path)

    # Here we can add support for other VCSs like hg, svn, cvs, etc.

    # The source file isn't under any VCS so we leave it be.
    return None


def is_local_url(url):

    return \
        url.startswith("file:") or url.startswith("/") or url.startswith("./")


def git_repository_path(source_file_path):

    import re

    # We need to extract the following.
    # (1): VCS URL, (2): branch, (3): repo root directory name, (4): repo file,
    # (5): revision.

    source_file_dir = os.path.dirname(source_file_path)

    # (1) Get the VCS URL and extract the server part, i.e. change the URL from
    # gitolite@git.someserver.com:SomeRepo.git to just git.someserver.com.
    source_long_url = run_command(
        "git config --get remote.origin.url", source_file_dir)

    # The URL could be a local download directory. If so, get the URL again
    # using the local directory's config file.
    if is_local_url(source_long_url):
        git_config_file = os.path.join(source_long_url, "config")
        source_long_url = run_command(
            "git config --file %s --get remote.origin.url" % git_config_file,
            source_file_dir)

        # If also the download directory redirects to a local git directory,
        # then we're probably using source code from a local debug branch which
        # won't be accessible by Socorro.
        if is_local_url(source_long_url):
            return None

    # The URL can have several formats. A full list can be found using
    # git help clone. Extract the server part with a regex.
    url_match = re.search(".*(://|@)([^:/]*).*", source_long_url)
    source_server = url_match.group(2)

    # (2) Get the branch for this file.
    source_branch_list = run_command("git show-branch --list", source_file_dir)
    source_branch_match = re.search(".*?\[(.*?)\].*", source_branch_list)
    source_branch = source_branch_match.group(1)

    # (3) Since the repo root directory name can be changed without affecting
    # git, we need to extract the name from something more reliable.
    # The git URL has a repo name that we could use. We just need to strip off
    # everything around it - from gitolite@git.someserver.com:SomeRepo.git/ to
    # SomeRepo.
    source_repo_dir = re.sub("/$", "", source_long_url)
    source_repo_dir = re.sub("\.git$", "", source_repo_dir)
    source_repo_dir = re.sub(".*[:/]", "", source_repo_dir)

    # (4) We know the file but want to remove all of the build system dependent
    # path up to and including the repository's root directory, e.g. remove
    # /home/someuser/dev/repo/projectx/
    source_toplevel = run_command(
        "git rev-parse --show-toplevel", source_file_dir)
    source_toplevel = source_toplevel + os.path.sep
    source_file = source_file_path.replace(source_toplevel, "")

    # (5) Get the source revision this file is part of.
    source_revision = run_command("git rev-parse HEAD", source_file_dir)

    # Assemble the repository path according to the Socorro format.
    socorro_reference = "git:%s/%s:%s/%s:%s" % \
        (source_server, source_branch,
        source_repo_dir, source_file,
        source_revision)

    return socorro_reference


def arrange_socorro_sym_file(socorro_sym_file_path, socorro_syms_dir):

    import re

    # Breakpad's minidump_stackwalk needs a certain directory structure in order
    # to find correct symbols when extracting a stack trace out of a minidump.
    # The directory structure must look like the following.
    # YourBinary/<hash>/YourBinary.sym
    # YourLibrary.so/<hash>/YourLibrary.so.sym
    # To be able to create such structure we need to extract the hash value that
    # is found in each symbol file. The header of the symbol file looks
    # something like this:
    # MODULE Linux x86 A079E473106CE51C74C1C25AF536CCD30 YourBinary
    # See
    # http://code.google.com/p/google-breakpad/wiki/LinuxStarterGuide

    # Create the directory with the same name as the binary.
    binary_dir = re.sub("\.sym$", "", socorro_sym_file_path)
    if not os.path.exists(binary_dir):
        os.makedirs(binary_dir)

    # Get the hash from the header of the symbol file.
    with open(socorro_sym_file_path, 'r') as socorro_sym_file:

        # The hash is the 4th argument of the first line.
        sym_file_hash = socorro_sym_file.readline().split()[3]

    # Create the hash directory.
    hash_dir = os.path.join(binary_dir, sym_file_hash)
    if not os.path.exists(hash_dir):
        os.makedirs(hash_dir)

    # Move the symbol file to the hash directory.
    sym_file_name = os.path.basename(socorro_sym_file_path)
    os.rename(socorro_sym_file_path, os.path.join(hash_dir, sym_file_name))

    return

