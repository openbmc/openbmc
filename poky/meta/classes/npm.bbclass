# Copyright (C) 2020 Savoir-Faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only
#
# This bbclass builds and installs an npm package to the target. The package
# sources files should be fetched in the calling recipe by using the SRC_URI
# variable. The ${S} variable should be updated depending of your fetcher.
#
# Usage:
#  SRC_URI = "..."
#  inherit npm
#
# Optional variables:
#  NPM_ARCH:
#       Override the auto generated npm architecture.
#
#  NPM_INSTALL_DEV:
#       Set to 1 to also install devDependencies.

DEPENDS_prepend = "nodejs-native "
RDEPENDS_${PN}_prepend = "nodejs "

NPM_INSTALL_DEV ?= "0"

def npm_target_arch_map(target_arch):
    """Maps arch names to npm arch names"""
    import re
    if re.match("p(pc|owerpc)(|64)", target_arch):
        return "ppc"
    elif re.match("i.86$", target_arch):
        return "ia32"
    elif re.match("x86_64$", target_arch):
        return "x64"
    elif re.match("arm64$", target_arch):
        return "arm"
    return target_arch

NPM_ARCH ?= "${@npm_target_arch_map(d.getVar("TARGET_ARCH"))}"

NPM_PACKAGE = "${WORKDIR}/npm-package"
NPM_CACHE = "${WORKDIR}/npm-cache"
NPM_BUILD = "${WORKDIR}/npm-build"

def npm_global_configs(d):
    """Get the npm global configuration"""
    configs = []
    # Ensure no network access is done
    configs.append(("offline", "true"))
    configs.append(("proxy", "http://invalid"))
    # Configure the cache directory
    configs.append(("cache", d.getVar("NPM_CACHE")))
    return configs

def npm_pack(env, srcdir, workdir):
    """Run 'npm pack' on a specified directory"""
    import shlex
    cmd = "npm pack %s" % shlex.quote(srcdir)
    configs = [("ignore-scripts", "true")]
    tarball = env.run(cmd, configs=configs, workdir=workdir).strip("\n")
    return os.path.join(workdir, tarball)

python npm_do_configure() {
    """
    Step one: configure the npm cache and the main npm package

    Every dependencies have been fetched and patched in the source directory.
    They have to be packed (this remove unneeded files) and added to the npm
    cache to be available for the next step.

    The main package and its associated manifest file and shrinkwrap file have
    to be configured to take into account these cached dependencies.
    """
    import base64
    import copy
    import json
    import re
    import shlex
    import tempfile
    from bb.fetch2.npm import NpmEnvironment
    from bb.fetch2.npm import npm_unpack
    from bb.fetch2.npmsw import foreach_dependencies
    from bb.progress import OutOfProgressHandler

    bb.utils.remove(d.getVar("NPM_CACHE"), recurse=True)
    bb.utils.remove(d.getVar("NPM_PACKAGE"), recurse=True)

    env = NpmEnvironment(d, configs=npm_global_configs(d))

    def _npm_cache_add(tarball):
        """Run 'npm cache add' for a specified tarball"""
        cmd = "npm cache add %s" % shlex.quote(tarball)
        env.run(cmd)

    def _npm_integrity(tarball):
        """Return the npm integrity of a specified tarball"""
        sha512 = bb.utils.sha512_file(tarball)
        return "sha512-" + base64.b64encode(bytes.fromhex(sha512)).decode()

    def _npm_version(tarball):
        """Return the version of a specified tarball"""
        regex = r"-(\d+\.\d+\.\d+(-.*)?(\+.*)?)\.tgz"
        return re.search(regex, tarball).group(1)

    def _npmsw_dependency_dict(orig, deptree):
        """
        Return the sub dictionary in the 'orig' dictionary corresponding to the
        'deptree' dependency tree. This function follows the shrinkwrap file
        format.
        """
        ptr = orig
        for dep in deptree:
            if "dependencies" not in ptr:
                ptr["dependencies"] = {}
            ptr = ptr["dependencies"]
            if dep not in ptr:
                ptr[dep] = {}
            ptr = ptr[dep]
        return ptr

    # Manage the manifest file and shrinkwrap files
    orig_manifest_file = d.expand("${S}/package.json")
    orig_shrinkwrap_file = d.expand("${S}/npm-shrinkwrap.json")
    cached_manifest_file = d.expand("${NPM_PACKAGE}/package.json")
    cached_shrinkwrap_file = d.expand("${NPM_PACKAGE}/npm-shrinkwrap.json")

    with open(orig_manifest_file, "r") as f:
        orig_manifest = json.load(f)

    cached_manifest = copy.deepcopy(orig_manifest)
    cached_manifest.pop("dependencies", None)
    cached_manifest.pop("devDependencies", None)

    with open(orig_shrinkwrap_file, "r") as f:
        orig_shrinkwrap = json.load(f)

    cached_shrinkwrap = copy.deepcopy(orig_shrinkwrap)
    cached_shrinkwrap.pop("dependencies", None)

    # Manage the dependencies
    progress = OutOfProgressHandler(d, r"^(\d+)/(\d+)$")
    progress_total = 1 # also count the main package
    progress_done = 0

    def _count_dependency(name, params, deptree):
        nonlocal progress_total
        progress_total += 1

    def _cache_dependency(name, params, deptree):
        destsubdirs = [os.path.join("node_modules", dep) for dep in deptree]
        destsuffix = os.path.join(*destsubdirs)
        with tempfile.TemporaryDirectory() as tmpdir:
            # Add the dependency to the npm cache
            destdir = os.path.join(d.getVar("S"), destsuffix)
            tarball = npm_pack(env, destdir, tmpdir)
            _npm_cache_add(tarball)
            # Add its signature to the cached shrinkwrap
            dep = _npmsw_dependency_dict(cached_shrinkwrap, deptree)
            dep["version"] = _npm_version(tarball)
            dep["integrity"] = _npm_integrity(tarball)
            if params.get("dev", False):
                dep["dev"] = True
            # Display progress
            nonlocal progress_done
            progress_done += 1
            progress.write("%d/%d" % (progress_done, progress_total))

    dev = bb.utils.to_boolean(d.getVar("NPM_INSTALL_DEV"), False)
    foreach_dependencies(orig_shrinkwrap, _count_dependency, dev)
    foreach_dependencies(orig_shrinkwrap, _cache_dependency, dev)

    # Configure the main package
    with tempfile.TemporaryDirectory() as tmpdir:
        tarball = npm_pack(env, d.getVar("S"), tmpdir)
        npm_unpack(tarball, d.getVar("NPM_PACKAGE"), d)

    # Configure the cached manifest file and cached shrinkwrap file
    def _update_manifest(depkey):
        for name in orig_manifest.get(depkey, {}):
            version = cached_shrinkwrap["dependencies"][name]["version"]
            if depkey not in cached_manifest:
                cached_manifest[depkey] = {}
            cached_manifest[depkey][name] = version

    _update_manifest("dependencies")

    if dev:
        _update_manifest("devDependencies")

    with open(cached_manifest_file, "w") as f:
        json.dump(cached_manifest, f, indent=2)

    with open(cached_shrinkwrap_file, "w") as f:
        json.dump(cached_shrinkwrap, f, indent=2)
}

python npm_do_compile() {
    """
    Step two: install the npm package

    Use the configured main package and the cached dependencies to run the
    installation process. The installation is done in a directory which is
    not the destination directory yet.

    A combination of 'npm pack' and 'npm install' is used to ensure that the
    installed files are actual copies instead of symbolic links (which is the
    default npm behavior).
    """
    import shlex
    import tempfile
    from bb.fetch2.npm import NpmEnvironment

    bb.utils.remove(d.getVar("NPM_BUILD"), recurse=True)

    env = NpmEnvironment(d, configs=npm_global_configs(d))

    dev = bb.utils.to_boolean(d.getVar("NPM_INSTALL_DEV"), False)

    with tempfile.TemporaryDirectory() as tmpdir:
        args = []
        configs = []

        if dev:
            configs.append(("also", "development"))
        else:
            configs.append(("only", "production"))

        # Report as many logs as possible for debugging purpose
        configs.append(("loglevel", "silly"))

        # Configure the installation to be done globally in the build directory
        configs.append(("global", "true"))
        configs.append(("prefix", d.getVar("NPM_BUILD")))

        # Add node-gyp configuration
        configs.append(("arch", d.getVar("NPM_ARCH")))
        configs.append(("release", "true"))
        sysroot = d.getVar("RECIPE_SYSROOT_NATIVE")
        nodedir = os.path.join(sysroot, d.getVar("prefix_native").strip("/"))
        configs.append(("nodedir", nodedir))
        bindir = os.path.join(sysroot, d.getVar("bindir_native").strip("/"))
        pythondir = os.path.join(bindir, "python-native", "python")
        configs.append(("python", pythondir))

        # Add node-pre-gyp configuration
        args.append(("target_arch", d.getVar("NPM_ARCH")))
        args.append(("build-from-source", "true"))

        # Pack and install the main package
        tarball = npm_pack(env, d.getVar("NPM_PACKAGE"), tmpdir)
        env.run("npm install %s" % shlex.quote(tarball), args=args, configs=configs)
}

npm_do_install() {
    # Step three: final install
    #
    # The previous installation have to be filtered to remove some extra files.

    rm -rf ${D}

    # Copy the entire lib and bin directories
    install -d ${D}/${nonarch_libdir}
    cp --no-preserve=ownership --recursive ${NPM_BUILD}/lib/. ${D}/${nonarch_libdir}

    if [ -d "${NPM_BUILD}/bin" ]
    then
        install -d ${D}/${bindir}
        cp --no-preserve=ownership --recursive ${NPM_BUILD}/bin/. ${D}/${bindir}
    fi

    # If the package (or its dependencies) uses node-gyp to build native addons,
    # object files, static libraries or other temporary files can be hidden in
    # the lib directory. To reduce the package size and to avoid QA issues
    # (staticdev with static library files) these files must be removed.
    local GYP_REGEX=".*/build/Release/[^/]*.node"

    # Remove any node-gyp directory in ${D} to remove temporary build files
    for GYP_D_FILE in $(find ${D} -regex "${GYP_REGEX}")
    do
        local GYP_D_DIR=${GYP_D_FILE%/Release/*}

        rm --recursive --force ${GYP_D_DIR}
    done

    # Copy only the node-gyp release files
    for GYP_B_FILE in $(find ${NPM_BUILD} -regex "${GYP_REGEX}")
    do
        local GYP_D_FILE=${D}/${prefix}/${GYP_B_FILE#${NPM_BUILD}}

        install -d ${GYP_D_FILE%/*}
        install -m 755 ${GYP_B_FILE} ${GYP_D_FILE}
    done

    # Remove the shrinkwrap file which does not need to be packed
    rm -f ${D}/${nonarch_libdir}/node_modules/*/npm-shrinkwrap.json
    rm -f ${D}/${nonarch_libdir}/node_modules/@*/*/npm-shrinkwrap.json

    # node(1) is using /usr/lib/node as default include directory and npm(1) is
    # using /usr/lib/node_modules as install directory. Let's make both happy.
    ln -fs node_modules ${D}/${nonarch_libdir}/node
}

FILES_${PN} += " \
    ${bindir} \
    ${nonarch_libdir} \
"

EXPORT_FUNCTIONS do_configure do_compile do_install
